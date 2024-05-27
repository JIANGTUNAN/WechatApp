package top.tolan.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import top.tolan.common.config.compression.SnappyCompressorAdapter;
import top.tolan.common.config.serialization.FastJson2JsonRedisSerializer;
import top.tolan.common.config.serialization.KryoSerializerAdapter;

import java.time.Duration;

/**
 * redis配置
 */
@Configuration
public class RedisConfig {

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
        return "local key = KEYS[1]\n" + "local count = tonumber(ARGV[1])\n" + "local time = tonumber(ARGV[2])\n"
                + "local current = redis.call('get', key);\n" + "if current and tonumber(current) > count then\n"
                + "    return tonumber(current);\n" + "end\n" + "current = redis.call('incr', key)\n"
                + "if tonumber(current) == 1 then\n" + "    redis.call('expire', key, time)\n" + "end\n"
                + "return tonumber(current);";
    }
}
