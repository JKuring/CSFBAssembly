package com.eastcom.csfb.storm.base;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.util.Pool;

public class JedisPipeline implements Cloneable {

    protected Pool<Jedis> jedisPool;

    private Jedis jedis;

    private Pipeline pipeline;

    public JedisPipeline(Pool<Jedis> jedisPool) {
        this.jedisPool = jedisPool;
    }

    public void open() {
        jedis = jedisPool.getResource();
        if (jedis == null) {
            throw new NullPointerException("the number of redis object is null in the pool.");
        }
        pipeline = jedis.pipelined();
    }

    public void syncAndChange() {
        pipeline.sync();
        jedisPool.returnResource(jedis);
        jedis = jedisPool.getResource();
        pipeline = jedis.pipelined();
    }

    public void close() {
        pipeline.sync();
        jedisPool.returnResource(jedis);
    }

    public void close(int size) {
        if (size > 0) {
            pipeline.sync();
        }
        jedisPool.returnResource(jedis);
    }

    public void broken() {
        jedisPool.returnBrokenResource(jedis);
    }

    public Jedis getJedis() {
        return jedis;
    }

    public Pipeline getPipeline() {
        return pipeline;
    }

}
