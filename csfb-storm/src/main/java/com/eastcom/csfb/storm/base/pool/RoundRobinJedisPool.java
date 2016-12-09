package com.eastcom.csfb.storm.base.pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vibur.objectpool.ConcurrentLinkedPool;
import org.vibur.objectpool.PoolObjectFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.util.Pool;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinJedisPool extends Pool<Jedis> implements
        PoolObjectFactory<Jedis> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private List<String> addresses;

    private String password;

    private AtomicInteger jedisIndex = new AtomicInteger(-1);

    private ConcurrentLinkedPool<Jedis> internalPool;

    public RoundRobinJedisPool(List<String> addresses, String password,
                               int initialSize, int maxSize) {
        super();
        // 各进程起始索引随机,避免链接集中在某个实例.
        Random random = new Random(System.currentTimeMillis());
        int offset = random.nextInt(addresses.size() + 1);
        jedisIndex.addAndGet(offset);

        this.addresses = addresses;
        this.password = password;
        initialSize = (initialSize / addresses.size() + 1) * addresses.size();
        maxSize = (maxSize / addresses.size() + 1) * addresses.size();
        internalPool = new ConcurrentLinkedPool<Jedis>(this, initialSize,
                maxSize, false);
    }

    @Override
    public Jedis getResource() {
        return internalPool.take();
    }

    @Override
    public void returnResource(Jedis resource) {
        internalPool.restore(resource, true);
    }

    @Override
    public void returnBrokenResource(Jedis resource) {
        internalPool.restore(resource, false);
        logger.info("return broken resource.");
    }

    @Override
    public void returnResourceObject(Jedis resource) {
        internalPool.restore(resource, true);
    }

    @Override
    protected void returnBrokenResourceObject(Jedis resource) {
        internalPool.restore(resource, false);
    }

    @Override
    public Jedis create() {
        for (int i = 0; i < addresses.size(); i++) {
            int k = Math.abs(jedisIndex.incrementAndGet() % addresses.size());
            Jedis jedis = create(k);
            if (jedis != null) {
                return jedis;
            }
        }
        throw new RuntimeException("无可用redis连接.");
    }

    private Jedis create(int k) {
        String address = addresses.get(k);
        int index = address.indexOf(':');
        String host = address.substring(0, index);
        int port = Integer.parseInt(address.substring(index + 1));
        try {
            Jedis jedis = new Jedis(host, port, 10000);
            jedis.setDataSource(this);
            jedis.connect();
            jedis.auth(password);
            return jedis;
        } catch (JedisConnectionException e) {
            logger.error("连接Redis异常:" + address, e);
            return null;
        }
    }

    @Override
    public boolean readyToTake(Jedis obj) {
        return true;
    }

    @Override
    public boolean readyToRestore(Jedis obj) {
        return true;
    }

    @Override
    public void destroy(Jedis jedis) {
        if (jedis != null) {
            if (jedis.isConnected()) {
                try {
                    try {
                        jedis.quit();
                    } catch (Exception e) {
                    }
                    jedis.disconnect();
                } catch (Exception e) {

                }
            }
        }

    }

}
