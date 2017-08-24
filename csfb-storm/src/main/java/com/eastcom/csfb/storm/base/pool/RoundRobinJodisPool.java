package com.eastcom.csfb.storm.base.pool;

import com.google.gson.Gson;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.RetryNTimes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vibur.objectpool.ConcurrentLinkedPool;
import org.vibur.objectpool.PoolObjectFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.util.Pool;
import redis.clients.util.SafeEncoder;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode.BUILD_INITIAL_CACHE;

public class RoundRobinJodisPool extends Pool<Jedis> implements PoolObjectFactory<Jedis> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String password;

    private AtomicInteger jedisIndex = new AtomicInteger(-1);

    private ConcurrentLinkedPool<Jedis> internalPool;

    private PathChildrenCache watcher;

    public RoundRobinJodisPool(String zkAddr, int zkSessionTimeoutMs, String clusterName, String password, int maxSize)
            throws Exception {
        super();

        CuratorFramework curatorClient = CuratorFrameworkFactory.builder().connectString(zkAddr)
                .sessionTimeoutMs(zkSessionTimeoutMs).retryPolicy(new RetryNTimes(Integer.MAX_VALUE, 1_000)).build();
        curatorClient.start();
        watcher = new PathChildrenCache(curatorClient, "/jodis/" + clusterName, true);
        watcher.start(BUILD_INITIAL_CACHE);

        // 各进程起始索引随机,避免链接集中在某个实例.
        Random random = new Random(System.currentTimeMillis());
        int offset = random.nextInt(watcher.getCurrentData().size() + 1);
        jedisIndex.addAndGet(offset);

        this.password = password;
        internalPool = new ConcurrentLinkedPool<Jedis>(this, 0, maxSize, false);
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
        int proxySize = watcher.getCurrentData().size();
        for (int i = 0; i < proxySize; i++) {
            int k = Math.abs(jedisIndex.incrementAndGet() % proxySize);
            Jedis jedis = create(k);
            if (jedis != null) {
                return jedis;
            }
        }
        throw new RuntimeException("No avaliable redis proxy.");
    }

    private Jedis create(int k) {
        if (watcher.getCurrentData().size() <= k) {
            logger.warn("Proxy state changed.");
            return null;
        }
        byte[] data = watcher.getCurrentData().get(k).getData();
        String json = SafeEncoder.encode(data);
        CodisProxyInfo proxyInfo = new Gson().fromJson(json, CodisProxyInfo.class);
        if (!"online".equals(proxyInfo.getState())) {
            logger.warn("Proxy {} is offline.", proxyInfo.getAddr());
            return null;
        }
        String address = proxyInfo.getAddr();
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
            logger.error("Error when create jedis: " + address, e);
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

    public class CodisProxyInfo {

        private String addr;

        private String state;

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }
    }

}
