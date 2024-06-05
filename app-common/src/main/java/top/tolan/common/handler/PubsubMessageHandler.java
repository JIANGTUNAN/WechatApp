package top.tolan.common.handler;

import cn.hutool.core.util.StrUtil;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import top.tolan.common.annotation.MessageHandler;
import top.tolan.common.annotation.MessageListener;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * Pub/sub 处理方式 实现 广播消息
 *
 * @author 散装java
 * @date 2023-02-03
 */
@MessageHandler(value = MessageListener.Mode.PUBSUB)
public class PubsubMessageHandler extends AbstractMessageHandler {

    public PubsubMessageHandler(RedisTemplate<Object, Object> redisTemplate) {
        super(redisTemplate);
    }

    /**
     * 调用带有MessageListener注解的方法。
     * 当收到Redis频道的消息时，此方法将动态调用指定方法处理消息。
     *
     * @param method 要调用的方法，该方法必须带有MessageListener注解。
     */
    @Override
    public void invokeMessage(Method method) {
        // 用于存储订阅的消费者标识
        Set<String> consumers = new HashSet<>();
        // 从方法上获取MessageListener注解
        MessageListener listener = method.getAnnotation(MessageListener.class);
        // 从注解中获取订阅的频道
        String channel = getChannel(listener);
        // 获取Redis连接
        RedisConnection connection = getConnection();
        // 订阅指定频道，接收消息时动态调用方法处理消息
        connection.subscribe((message, pattern) -> {
            // 获取方法所属的类
            Class<?> declaringClass = method.getDeclaringClass();
            // 从应用上下文中获取类的实例
            Object bean = applicationContext.getBean(declaringClass);
            // 获取消息体
            byte[] body = message.getBody();
            // 调用方法处理消息
            consumer(method, consumers, bean, body);
        }, channel.getBytes());
    }


    /**
     * 从MessageListener注解中获取频道信息。
     * <p>
     * 此方法旨在通过检查注解的value()和channel()方法的返回值，
     * 来确定应使用的频道名称。如果channel()方法返回的字符串为空或仅包含空格，
     * 则使用value()方法的返回值作为频道名称。
     * 这种设计允许注解的使用者以更灵活的方式指定频道，
     * 既可以直接使用value()方法，也可以通过channel()方法。
     *
     * @param annotation MessageListener注解实例，用于获取注解的value和channel值。
     * @return 返回从注解中提取的频道名称，优先使用channel()方法的返回值，
     * 如果该值为空或仅包含空格，则使用value()方法的返回值。
     */
    private String getChannel(MessageListener annotation) {
        String value = annotation.value();
        String channel = annotation.channel();
        // 使用StrUtil.isBlank判断channel是否为空或仅包含空格，如果是，则返回value作为频道名称，否则返回channel。
        return StrUtil.isBlank(channel) ? value : channel;
    }
}
