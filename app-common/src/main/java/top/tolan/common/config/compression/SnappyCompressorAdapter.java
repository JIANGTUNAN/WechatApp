package top.tolan.common.config.compression;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.xerial.snappy.Snappy;

import java.util.Objects;

public class SnappyCompressorAdapter<T> implements RedisSerializer<T> {

    // 内部Redis序列化器
    private final RedisSerializer<T> innerSerializer;

    // 构造方法，接受内部Redis序列化器作为参数
    public SnappyCompressorAdapter(RedisSerializer<T> innerSerializer) {
        this.innerSerializer = innerSerializer;
    }

    // 序列化对象
    @Override
    public byte[] serialize(T t) {
        try {
            byte[] bytes = innerSerializer.serialize(t); // 使用内部序列化器序列化对象
            return Snappy.compress(Objects.requireNonNull(bytes)); // 使用Snappy压缩字节数组
        } catch (Exception e) {
            throw new SerializationException(e.getMessage(), e); // 抛出序列化异常
        }
    }

    // 反序列化字节数组为对象
    @Override
    public T deserialize(byte[] bytes) {
        try {
            byte[] uncompressedData = Snappy.uncompress(bytes); // 使用Snappy解压缩字节数组
            return innerSerializer.deserialize(uncompressedData); // 使用内部序列化器反序列化数据
        } catch (Exception e) {
            throw new SerializationException(e.getMessage(), e); // 抛出反序列化异常
        }
    }

}
