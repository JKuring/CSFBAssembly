package com.eastcom.csfb.storm.base.common;

import com.eastcom.csfb.data.UserCommon;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Set;


/**
 * Created by linghang.kong on 2016/5/25.
 */
public class LinkedHashMapSerializer extends Serializer<LinkedHashMap<String, UserCommon>> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void write(Kryo kryo, Output output, LinkedHashMap<String, UserCommon> o) {
        //MapSerializer mapSerializer = new MapSerializer();
        //mapSerializer.setKeyClass(String.class);

        Set<String> set;
        kryo.register(Object[].class);
        set = o.keySet();
        kryo.writeClassAndObject(output, set.toArray());
        for (String key : set
                ) {
            kryo.writeClassAndObject(output, o.get(key));
        }
    }

    @Override
    public LinkedHashMap<String, UserCommon> read(Kryo kryo, Input input, Class<LinkedHashMap<String, UserCommon>> type) {
        LinkedHashMap<String, UserCommon> objects = null;
        kryo.register(Object[].class);
        Object[] set = (Object[]) kryo.readClassAndObject(input);
        for (int i = 0; i < set.length; i++) {
            objects.put(set[i].toString(), (UserCommon) kryo.readClassAndObject(input));
        }
        return objects;
    }

}
