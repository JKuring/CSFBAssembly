package com.eastcom.csfb.storm.base.msg;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.util.Pool;

public class RedisReceiver implements MsgReceiver {

    private Pool<Jedis> jedisPool;

    private String queueName;

    public RedisReceiver(String host, int port, String password, String queueName) {
        GenericObjectPoolConfig gopc = new GenericObjectPoolConfig();
        gopc.setMaxTotal(10);
        gopc.setMaxIdle(2);
        jedisPool = new JedisPool(gopc, host, port, 10000, password);
        this.queueName = queueName;
    }

    @Override
    public String receive() throws Exception {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.rpop(queueName);
        }
    }

}
