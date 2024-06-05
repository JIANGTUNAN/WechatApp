package top.tolan.common.handler;

import cn.hutool.core.collection.CollUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.scheduling.TaskScheduler;
import top.tolan.common.annotation.MessageHandler;
import top.tolan.common.annotation.MessageListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;

/**
 * Stream方式 实现消息队列
 *
 * @author 散装java
 * @date 2023-02-06
 */
@Slf4j
@MessageHandler(value = MessageListener.Mode.STREAM)
public class StreamMessageHandler extends AbstractMessageHandler {

    @Resource
    StreamMessageListenerContainer<String, ObjectRecord<String, String>> redisListenerContainer;
    @Resource
    RedisTemplate<Object, Object> redisTemplate;
    @Resource
    TaskScheduler taskScheduler;

    public StreamMessageHandler(RedisTemplate<Object, Object> redisTemplate) {
        super(redisTemplate);
    }

    @Override
    public synchronized void invokeMessage(Method method) {
        // 获取注解中的相关信息
        MessageListener annotation = method.getAnnotation(MessageListener.class);
        String streamKey = annotation.streamKey();
        String consumerGroup = annotation.consumerGroup();
        String consumerName = annotation.consumerName();
        boolean pending = annotation.pending();

        // 检测组是否存在
        this.checkAndCreatGroup(streamKey, consumerGroup);

        // fix https://gitee.com/bulkall/bulk-demo/issues/I86EES
        // 如果是pending消息 ，则需要单独处理。
        // fixme 这里是单机应用直接使用taskScheduler调度，注意分布式情况下，的幂等处理，或者使用分布式任务调度框架来解决
        if (pending) {
            taskScheduler.scheduleAtFixedRate(() -> pendingHandler(method, streamKey, consumerGroup), 5000);
            return;
        }

        // 获取 StreamOffset
        StreamOffset<String> offset = this.getOffset(streamKey, pending);

        // 创建消费者
        Consumer consumer = Consumer.from(consumerGroup, consumerName);

        StreamMessageListenerContainer.StreamReadRequest<String> streamReadRequest =
                StreamMessageListenerContainer
                        .StreamReadRequest.builder(offset)
                        .errorHandler((error) -> log.error("redis stream 异常 ：{}", error.getMessage()))
                        .cancelOnError(e -> false)
                        .consumer(consumer)
                        //关闭自动ack确认
                        .autoAcknowledge(false)
                        .build();

        // 指定消费者对象注册到容器中去
        redisListenerContainer.register(streamReadRequest, (message) -> {
            Class<?> declaringClass = method.getDeclaringClass();
            Object bean = applicationContext.getBean(declaringClass);
            try {
                method.invoke(bean, message);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 拉取 pending 消息， 只处理超过一定时间的pending消息，防止消费者还没有消费完就被处理的情况。
     *
     * @param method        回调的方法
     * @param streamKey     key
     * @param consumerGroup 组
     */
    private void pendingHandler(Method method, String streamKey, String consumerGroup) {
        StreamOperations<Object, Object, Object> streamOperations = redisTemplate.opsForStream();
        PendingMessages messages = streamOperations.pending(streamKey, consumerGroup, Range.unbounded(), Long.MAX_VALUE);
        List<PendingMessage> msgList = messages.stream().toList();
        // 获取超过一分钟已读但是未ack的消息。这时间，依据自己的业务场景设置
        long time = 1000 * 60;
        List<RecordId> needList = msgList.stream()
                .filter(item -> {
                    Long t = item.getId().getTimestamp();
                    if (t != null) {
                        return System.currentTimeMillis() - item.getId().getTimestamp() > time;
                    }
                    return true;
                })
                .map(PendingMessage::getId)
                .toList();

        if (CollUtil.isEmpty(needList)) {
            return;
        }
        // 消息id排序，缩小取数范围。id 大概长这样 "1700537625199-0"
        List<String> sortedMessageIds = needList.stream().map(RecordId::getValue)
                .sorted(Comparator.comparingLong(messageId -> Long.parseLong(messageId.split("-")[0])))
                .sorted(Comparator.comparingInt(messageId -> Integer.parseInt(messageId.split("-")[1])))
                .toList();

        // 消息范围 闭区间
        Range<String> range = Range.closed(sortedMessageIds.get(0), sortedMessageIds.get(sortedMessageIds.size() - 1));

        List<ObjectRecord<Object, Object>> pendingRecords = streamOperations.range(Object.class, streamKey, range).stream()
                // 只取 pending 消息
                .filter(e -> needList.contains(e.getId()))
                .toList();
        if (CollUtil.isNotEmpty(pendingRecords)) {
            for (ObjectRecord<Object, Object> record : pendingRecords) {
                Class<?> declaringClass = method.getDeclaringClass();
                Object bean = applicationContext.getBean(declaringClass);
                try {
                    method.invoke(bean, record);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private StreamOffset<String> getOffset(String streamKey, boolean pending) {
        if (pending) {
            // 获取尚未 ack 的消息
            return StreamOffset.create(streamKey, ReadOffset.from("0"));
        }
        // 指定消费最新的消息
        return StreamOffset.create(streamKey, ReadOffset.lastConsumed());
    }

    /**
     * 校验消费组是否存在，不存在则创建，否则会出现异常
     * Error in execution; nested exception is io.lettuce.core.RedisCommandExecutionException: NOGROUP No such key 'streamKey' or consumer group 'consumerGroup' in XREADGROUP with GROUP option
     *
     * @param streamKey     streamKey
     * @param consumerGroup consumerGroup
     */
    private void checkAndCreatGroup(String streamKey, String consumerGroup) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(streamKey))) {
            StreamOperations<Object, Object, Object> streamOperations = redisTemplate.opsForStream();
            StreamInfo.XInfoGroups groups = streamOperations.groups(streamKey);
            if (groups.isEmpty() || groups.stream().noneMatch(data -> consumerGroup.equals(data.groupName()))) {
                creatGroup(streamKey, consumerGroup);
            } else {
                groups.stream().forEach(g -> {
                    log.info("XInfoGroups:{}", g);
                    StreamInfo.XInfoConsumers consumers = streamOperations.consumers(streamKey, g.groupName());
                    log.info("XInfoConsumers:{}", consumers);
                });
            }
        } else {
            creatGroup(streamKey, consumerGroup);
        }
    }

    private void creatGroup(String key, String group) {
        StreamOperations<Object, Object, Object> streamOperations = redisTemplate.opsForStream();
        String groupName = streamOperations.createGroup(key, group);
        log.info("创建组成功:{}", groupName);
    }
}
