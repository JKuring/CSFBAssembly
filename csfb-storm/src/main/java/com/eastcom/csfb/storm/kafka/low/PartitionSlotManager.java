package com.eastcom.csfb.storm.kafka.low;

import com.eastcom.csfb.storm.kafka.ConfigKey;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.EnsurePath;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.collections.MapUtils.getIntValue;
import static org.apache.commons.collections.MapUtils.getString;
import static org.apache.commons.lang3.StringUtils.join;

@SuppressWarnings("deprecation")
public abstract class PartitionSlotManager {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private CuratorFramework client;
    private NodeCache nodeCache;
    private String basePath;

    private int spoutParallelism;
    private String zkConnectionString;
    private int timeout;

    private List<Thread> threads = new ArrayList<>();

    @SuppressWarnings({"rawtypes", "unchecked",})
    public PartitionSlotManager(Map conf) {
        spoutParallelism = getIntValue(conf, ConfigKey.PROJECT_SPOUT_PARALLELISM, 128);
        zkConnectionString = getString(conf, ConfigKey.PROJECT_SPOUT_ZOOKEEPER_CONNECT);
        String groupId = MapUtils.getString(conf, ConfigKey.KAFKA_GROUP_ID);
        timeout = getIntValue(conf, ConfigKey.PROJECT_SPOUT_ZOOKEEPER_CONNECT_TIMEOUT, 10000);
        basePath = StringUtils.join("/", groupId, "/");
    }

    public void start() {
        init();
        rebalance();
    }

    private void init() {
        try {
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 1000);
            client = CuratorFrameworkFactory.builder().connectString(zkConnectionString).namespace("slot")
                    .retryPolicy(retryPolicy).sessionTimeoutMs(timeout).build();
            client.start();
            EnsurePath ensurePath = new EnsurePath("/");
            ensurePath.ensure(client.getZookeeperClient());
        } catch (Exception e) {
            logger.error("", e);
            throw new RuntimeException(e);
        }
    }

    private void rebalance() {
        logger.warn("Lookup available slot ....");
        int slot = findAvailSlot();
        logger.info("Available slot: {}.", slot);
        for (Thread thread : threads) {
            logger.warn("Stoping cousumers {} ....", thread.getName());
            try {
                thread.interrupt();
            } catch (Exception e) {
                logger.error("", e);
            }
        }
        threads.clear();
        this.threads = startWorkers(slot);
        logger.warn("Cousumers started.");
    }

    public abstract List<Thread> startWorkers(int slot);

    private int findAvailSlot() {
        while (true) {
            try {
                for (int slot = 0; slot < this.spoutParallelism; slot++) {
                    String path = join(basePath + slot);
                    try {
                        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
                        registerListener(path);
                        return slot;
                    } catch (Exception e) {
                        logger.warn("Slot not available: {}.", path);
                    }
                }
            } catch (Exception e) {
                logger.error("", e);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
        }
    }

    private void registerListener(String path) throws Exception {
        if (nodeCache != null) {
            IOUtils.closeQuietly(nodeCache);
        }
        nodeCache = new NodeCache(client, path);
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                long clientSid = client.getZookeeperClient().getZooKeeper().getSessionId();
                long nodeSid = -1;
                if (nodeCache.getCurrentData() != null && nodeCache.getCurrentData().getStat() != null) {
                    nodeSid = nodeCache.getCurrentData().getStat().getEphemeralOwner();
                }
                if (clientSid != nodeSid) {// 同zk通信异常(如网络异常、严重gc等),导致瞬时节点owner变更
                    logger.warn("Node status changed, {} --> {}", clientSid, nodeSid);
                    rebalance();
                }
            }
        });
        nodeCache.start();
    }

    public void close() {
        for (Thread thread : threads) {
            thread.interrupt();
        }
        client.close();
    }

}
