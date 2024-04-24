package top.tolan.common.config.serialization;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.Pool;

import java.io.ByteArrayOutputStream;

public class KryoSerializer {

    // Kryo对象池
    static Pool<Kryo> pool = initializePool();

    // 初始化Kryo对象池
    private static Pool<Kryo> initializePool() {
        return new Pool<>(true, false, 8) {
            // 创建新的Kryo对象
            protected Kryo create() {
                Kryo kryo = new Kryo();
                // 同样的代码，同样的Class在不同的机器上注册编号任然不能保证一致
                // 所以多机器部署时候反序列化可能会出现问题。
                // 关闭注册功能
                kryo.setRegistrationRequired(false);
                // 循环引用检测
                kryo.setReferences(true);
                return kryo;
            }
        };
    }

    // 将对象序列化为字节数组
    public static byte[] serialize(final Object object) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Output output = new Output(stream);
        Kryo kryoPoolElement = pool.obtain();
        // 将对象写入输出流
        kryoPoolElement.writeClassAndObject(output, object);
        output.close();
        pool.free(kryoPoolElement); // 释放Kryo对象到对象池
        return stream.toByteArray(); // 返回序列化后的字节数组
    }

    // 将字节数组反序列化为对象
    @SuppressWarnings("unchecked")
    public static <O> O deserialize(final byte[] dataStream) {
        Kryo kryoPoolElement = pool.obtain();
        O deserializedObject = (O) kryoPoolElement.readClassAndObject(new Input(dataStream)); // 从输入流读取对象
        pool.free(kryoPoolElement); // 释放Kryo对象到对象池
        return deserializedObject; // 返回反序列化后的对象
    }

}
