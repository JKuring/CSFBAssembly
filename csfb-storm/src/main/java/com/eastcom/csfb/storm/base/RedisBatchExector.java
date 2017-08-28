package com.eastcom.csfb.storm.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.util.Pool;

import java.io.Closeable;

/**
 * 操作redis的基本方法
 */
public class RedisBatchExector implements Closeable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private JedisPipeline jp;

    private int pSize = 0;

    private int batchSize = 10000;

    private Pool<Jedis> jedisPool;

    public RedisBatchExector(Pool<Jedis> jedisPool) {
        this.jedisPool = jedisPool;
        init();
    }

    public RedisBatchExector(Pool<Jedis> jedisPool, int batchSize) {
        this.jedisPool = jedisPool;
        this.batchSize = batchSize;
        init();
    }


    private void init() {
        this.jp = new JedisPipeline(this.jedisPool);
        this.jp.open();
    }


    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    /*
     * keys
     */
    public synchronized void expire(String key, int seconds) {
        jp.getPipeline().expire(key, seconds);
        incr();
    }

    public synchronized void del(String key) {
        jp.getPipeline().del(key);
        incr();
    }

    /*
     * hashs
     */
    public synchronized void hdel(String key, String... field) {
        jp.getPipeline().hdel(key, field);
        incr();
    }

    public synchronized void hset(byte[] key, byte[] field, byte[] value) {
        jp.getPipeline().hset(key, field, value);
        incr();
    }

    public synchronized void hset(String key, String field, String value) {
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
    public synchronized void hincrBy(String key, String field, long value) {
        jp.getPipeline().hincrBy(key, field, value);
        incr();
    }

    /*
     * zset
     */
    public synchronized void zincrBy(String key, double score, String member) {
        jp.getPipeline().zincrby(key, score, member);
        incr();
    }

    /**
     * 添加异常处理，对于一个pipeline，出现异常需要重新从池中获取。
     *
     * @param key
     * @param score
     * @param member
     */
    public synchronized void zadd(byte[] key, double score, byte[] member) {
        try {
            jp.getPipeline().zadd(key, score, member);
            incr();
        } catch (JedisConnectionException exception) {
            logger.warn("change redis handle.");
            jp.broken();
            jp.open();
            zadd(key, score, member);
        }
    }

    public synchronized void zadd(String key, double score, String member) {
        jp.getPipeline().zadd(key, score, member);
        incr();
    }

	/*
     * list
	 */

    public synchronized void lpush(String key, String... values) {
        jp.getPipeline().lpush(key, values);
        incr();
    }

    /**
     * 在set集合中往key中添加member成员 set
     */
    public synchronized void sadd(String key, String... member) {
        jp.getPipeline().sadd(key, member);
        incr();
    }

    public synchronized void close() {
        jp.close(pSize);
    }

    public synchronized void broken() {
        jp.broken();
    }

    private void incr() {
        pSize++;
        if (pSize >= batchSize) {
            jp.syncAndChange();
            pSize = 0;
            logger.warn("flush pipeline at {} batch.", batchSize);
        }
    }

}
