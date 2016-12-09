package com.eastcom.csfb.storm.base;

import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;

import java.io.Closeable;

/**
 * 操作redis的基本方法
 */
public class RedisBatchExector implements Closeable {

    private JedisPipeline jp;

    private int pSize = 0;

    private int batchSize = 1000;

    public RedisBatchExector(Pool<Jedis> jedisPool) {
        jp = new JedisPipeline(jedisPool);
    }

    public RedisBatchExector(Pool<Jedis> jedisPool, int batchSize) {
        this.batchSize = batchSize;
        jp = new JedisPipeline(jedisPool);
    }

    public void open() {
        jp.open();
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    /*
     * keys
     */
    public void expire(String key, int seconds) {
        jp.getPipeline().expire(key, seconds);
        incr();
    }

    public void del(String key) {
        jp.getPipeline().del(key);
        incr();
    }

    /*
     * hashs
     */
    public void hdel(String key, String... field) {
        jp.getPipeline().hdel(key, field);
        incr();
    }

    public void hset(byte[] key, byte[] field, byte[] value) {
        jp.getPipeline().hset(key, field, value);
        incr();
    }

    public void hset(String key, String field, String value) {
        jp.getPipeline().hset(key, field, value);
        incr();
    }

    /**
     * redis中字段field的值添加value
     *
     * @param key
     * @param field
     * @param value
     */
    public void hincrBy(String key, String field, long value) {
        jp.getPipeline().hincrBy(key, field, value);
        incr();
    }

    /*
     * zset
     */
    public void zincrBy(String key, double score, String member) {
        jp.getPipeline().zincrby(key, score, member);
        incr();
    }

    public void zadd(byte[] key, double score, byte[] member) {
        jp.getPipeline().zadd(key, score, member);
        incr();
    }

    public void zadd(String key, double score, String member) {
        jp.getPipeline().zadd(key, score, member);
        incr();
    }

	/*
     * list
	 */

    public void lpush(String key, String... values) {
        jp.getPipeline().lpush(key, values);
        incr();
    }

    /**
     * 在set集合中往key中添加member成员 set
     */
    public void sadd(String key, String... member) {
        jp.getPipeline().sadd(key, member);
        incr();
    }

    @Override
    public void close() {
        jp.close(pSize);
    }

    public void broken() {
        jp.broken();
    }

    private void incr() {
        pSize++;
        if (pSize >= batchSize) {
            jp.syncAndChange();
            pSize = 0;
        }
    }

}
