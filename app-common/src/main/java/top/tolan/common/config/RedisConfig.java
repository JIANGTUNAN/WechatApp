package top.tolan.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import top.tolan.common.config.compression.SnappyCompressorAdapter;
import top.tolan.common.config.serialization.FastJson2JsonRedisSerializer;
import top.tolan.common.config.serialization.KryoSerializerAdapter;

import java.time.Duration;

/**
 * redis配置
 */
@Configuration
public class RedisConfig {

    /**
     * 配置RedisTemplate
     *
     * @param connectionFactory factory
     * @return RedisTemplate
     */
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        // 设置Redis连接工厂
        template.setConnectionFactory(connectionFactory);
        // 设置键的序列化器为StringRedisSerializer
        template.setKeySerializer(new StringRedisSerializer());
        // 设置值的序列化器为FastJson2JsonRedisSerializer
        template.setValueSerializer(this.fastJson2JsonRedisSerializer());
        // 设置哈希键的序列化器为StringRedisSerializer
        template.setHashKeySerializer(new StringRedisSerializer());
        // 设置哈希值的序列化器为FastJson2JsonRedisSerializer
        template.setHashValueSerializer(this.fastJson2JsonRedisSerializer());
        template.afterPropertiesSet(); // 初始化模板
        return template;
    }

    /**
     * 获取一个 StreamMessageListenerContainer
     *
     * @param executor      执行线程池
     * @param redisTemplate template
     * @return redisListenerContainer
     */
    @Bean
    public StreamMessageListenerContainer<String, ObjectRecord<String, String>> redisListenerContainer(
            ThreadPoolTaskExecutor executor,
            RedisTemplate<Object, Object> redisTemplate
    ) {
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String, String>> options =
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                        .builder()
                        // 拉取消息超时时间
                        .pollTimeout(Duration.ofSeconds(5))
                        // 批量抓取消息
                        .batchSize(1)
                        // 传递的数据类型
                        .targetType(String.class)
                        .executor(executor)
                        .build();
        RedisConnectionFactory connectionFactory = redisTemplate.getConnectionFactory();
        assert connectionFactory != null;
        StreamMessageListenerContainer<String, ObjectRecord<String, String>> container = StreamMessageListenerContainer
                .create(connectionFactory, options);
        container.start();
        return container;
    }

    @Bean
    public RedisCacheManager redisCacheManager(LettuceConnectionFactory lettuceConnectionFactory) {
        // 定义 Redis 缓存配置
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                // 禁用键前缀
                .disableKeyPrefix()
                // 禁用缓存 null 值
                .disableCachingNullValues()
                // 设置条目的过期时间为 24 小时
                .entryTtl(Duration.ofHours(24))
                // 使用 Snappy 压缩器进行值的序列化
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(snappyCompressorAdapter()));

        // 使用 Redis 连接工厂构建 Redis 缓存管理器
        return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(lettuceConnectionFactory)
                // 设置缓存默认配置
                .cacheDefaults(redisCacheConfiguration).build();
    }

    @Bean
    public FastJson2JsonRedisSerializer<Object> fastJson2JsonRedisSerializer() {
        // 返回FastJson2JsonRedisSerializer对象
        return new FastJson2JsonRedisSerializer<>(Object.class);
    }

    @Bean
    public SnappyCompressorAdapter<Object> snappyCompressorAdapter() {
        // 返回SnappyCompressorAdapter对象，使用KryoSerializerAdapter作为内部序列化器
        return new SnappyCompressorAdapter<>(new KryoSerializerAdapter<>());
    }

    @Bean
    public KryoSerializerAdapter<Object> kryoSerializerAdapter() {
        return new KryoSerializerAdapter<>();
    }


    @Bean
    public DefaultRedisScript<Long> limitScript() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(limitScriptText());
        redisScript.setResultType(Long.class);
        return redisScript;
    }

    /**
     * 限流脚本
     */
    private String limitScriptText() {
        return """
                local key = KEYS[1]
                local count = tonumber(ARGV[1])
                local time = tonumber(ARGV[2])
                local current = redis.call('get', key);
                if current and tonumber(current) > count then
                    return tonumber(current);
                end
                current = redis.call('incr', key)
                if tonumber(current) == 1 then
                    redis.call('expire', key, time)
                end
                return tonumber(current);
                """;
    }
}
