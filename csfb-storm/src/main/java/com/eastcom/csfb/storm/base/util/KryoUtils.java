package com.eastcom.csfb.storm.base.util;

import com.eastcom.csfb.data.ltesignal.LteS1Mme;
import com.eastcom.csfb.data.ltesignal.LteSGs;
import com.eastcom.csfb.data.ltesignal.serializer.LteS1MmeSerializer;
import com.eastcom.csfb.data.ltesignal.serializer.LteSgsSerializer;
import com.eastcom.csfb.data.mc.McCallEvent;
import com.eastcom.csfb.data.mc.McLocationUpdate;
import com.eastcom.csfb.data.mc.McPaging;
import com.eastcom.csfb.data.mc.serializer.McCallSerializer;
import com.eastcom.csfb.data.mc.serializer.McLocationUpdateSerializer;
import com.eastcom.csfb.data.mc.serializer.McPagingSerializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KryoUtils {

    protected static final Logger logger = LoggerFactory.getLogger(KryoUtils.class);

    /**
     * 根据storm配置创建kryo
     * <p>
     * 注册序列化类 ($--内部类)
     *
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     */
    public static Kryo create() {
        try {
            Kryo kryo = new Kryo();
            kryo.register(LteS1Mme.class, new LteS1MmeSerializer(), 1);
            kryo.register(LteSGs.class, new LteSgsSerializer(), 2);
            kryo.register(McCallEvent.class, new McCallSerializer(), 3);
            kryo.register(McLocationUpdate.class, new McLocationUpdateSerializer(), 4);
            kryo.register(McPaging.class, new McPagingSerializer(), 5);
//            kryo.setReferences(false);
            return kryo;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 序列化输出格式
     *
     * @param kryo   Kryo handle
     * @param object Object
     * @return byte array
     */
    public static byte[] serialize(Kryo kryo, Object object) {
        Output output = new Output(1000, Integer.MAX_VALUE);
        kryo.writeClassAndObject(output, object);
        output.close();
        return output.toBytes();
    }

    /**
     * 反序列化输入格式
     *
     * @param kryo Kryo handle
     * @param data data
     * @param <T>  Object type
     * @return Object
     */
    public static <T> T deserialize(Kryo kryo, byte[] data) {
        return (T) kryo.readClassAndObject(new Input(data));
    }

}
