package com.eastcom.csfb.storm.kafka.low;

import com.eastcom.csfb.storm.kafka.ConfigKey;
import com.google.common.net.HostAndPort;
import kafka.api.PartitionOffsetRequestInfo;
import kafka.cluster.BrokerEndPoint;
import kafka.common.TopicAndPartition;
import kafka.javaapi.OffsetRequest;
import kafka.javaapi.*;
import kafka.javaapi.consumer.SimpleConsumer;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.mutable.MutableLong;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.EnsurePath;
import org.apache.kafka.common.TopicPartition;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.util.SafeEncoder;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static kafka.api.OffsetRequest.*;
import static org.apache.commons.collections.MapUtils.getIntValue;
import static org.apache.commons.collections4.MapUtils.getLongValue;
import static org.apache.commons.collections4.MapUtils.getString;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.join;

@SuppressWarnings("deprecation")
public class PartitionStateManager extends TimerTask {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private CuratorFramework client;

    private Timer timer = new Timer(true);

    private int sotimeout;
    private int bufferSize;
    private String groupId;
    private int autoOffsetReset;
    private List<HostAndPort> bootstrapServers;

    private Map<TopicPartition, SimpleConsumer> partitionConsumers = new ConcurrentHashMap<>();
    private Map<TopicPartition, MutableLong> partitionOffsets = new ConcurrentHashMap<>();
    private Map<TopicPartition, String> partitionZkPath = new ConcurrentHashMap<>();

    @SuppressWarnings({"rawtypes", "unchecked"})
    public PartitionStateManager(Map conf) {
        this.sotimeout = MapUtils.getIntValue(conf, ConfigKey.KAFKA_OLD_LOW_SOTIMEOUT, 100000);
        this.bufferSize = MapUtils.getIntValue(conf, ConfigKey.KAFKA_OLD_LOW_BUFFER_SIZE, 64 * 1024);
        this.groupId = MapUtils.getString(conf, ConfigKey.KAFKA_GROUP_ID);
        String autoOffsetReset = MapUtils.getString(conf, ConfigKey.KAFKA_AUTO_OFFSET_RESET, "latest");
        this.autoOffsetReset = equalsIgnoreCase(autoOffsetReset, "earliest")
                || equalsIgnoreCase(autoOffsetReset, "smallest") ? -1 : 1;

        {
            String kafkaBrokers = MapUtils.getString(conf, ConfigKey.KAFKA_BOOTSTRAP_SERVERS);
            String[] strings = kafkaBrokers.split(",");
            List<HostAndPort> bootstrapServers = new ArrayList<>();
            for (String string : strings) {
                HostAndPort hp = HostAndPort.fromString(string).withDefaultPort(9092);
                bootstrapServers.add(hp);
            }
            this.bootstrapServers = bootstrapServers;
        }

        String zkConnectionString = getString(conf, ConfigKey.PROJECT_SPOUT_ZOOKEEPER_CONNECT);
        int timeout = getIntValue(conf, ConfigKey.PROJECT_SPOUT_ZOOKEEPER_CONNECT_TIMEOUT, 10000);
        long interval = getLongValue(conf, ConfigKey.KAFKA_AUTO_COMMIT_INTERVAL_MS, 5_000);

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 1000);
        client = CuratorFrameworkFactory.builder().connectString(zkConnectionString).retryPolicy(retryPolicy)
                .namespace("consumers").connectionTimeoutMs(timeout).build();
        client.start();
        try {
            EnsurePath ensurePath = new EnsurePath("/");
            ensurePath.ensure(client.getZookeeperClient());
        } catch (Exception e) {
            logger.error("", e);
            throw new RuntimeException(e);
        }

        timer.schedule(this, interval, interval);
    }

    @SuppressWarnings("unchecked")
    public void register(TopicPartition topicPartition) {
        try {
            String topic = topicPartition.topic();
            int partition = topicPartition.partition();

            String offsetZkPath = StringUtils.join("/", groupId, "/offsets/", topic, "/", partition);
            partitionZkPath.put(topicPartition, offsetZkPath);

            SimpleConsumer consumer = resetConsumer(topicPartition);

            String ownerZkPath = StringUtils.join("/", groupId, "/owners/", topic, "/", partition);
            Stat stat = client.checkExists().forPath(ownerZkPath);
            if (stat != null) {
                try {
                    client.delete().forPath(ownerZkPath);
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(ownerZkPath);
            client.setData().forPath(ownerZkPath, SafeEncoder.encode(consumer.clientId()));
        } catch (Exception e) {
            logger.error("", e);
            throw new RuntimeException(e);
        }
    }

    public SimpleConsumer resetConsumer(TopicPartition topicPartition) throws Exception {
        SimpleConsumer consumer = partitionConsumers.get(topicPartition);
        if (consumer != null) {
            try {
                consumer.close();
            } catch (Exception e) {
                logger.error("", e);
            }
        }
        HostAndPort leadBroker = findLeader(topicPartition);
        String clientId = join(new Object[]{groupId, topicPartition.topic(), topicPartition.partition()}, '-');
        consumer = new SimpleConsumer(leadBroker.getHostText(), leadBroker.getPort(), sotimeout, bufferSize, clientId);
        partitionConsumers.put(topicPartition, consumer);

        MutableLong poffset = new MutableLong(-1);
        poffset.setValue(getOffset(topicPartition.topic(), topicPartition.partition(), consumer,
                partitionZkPath.get(topicPartition)));
        this.partitionOffsets.put(topicPartition, poffset);
        return consumer;
    }

    public SimpleConsumer getConsumer(TopicPartition topicPartition) {
        return partitionConsumers.get(topicPartition);
    }

    public MutableLong getOffset(TopicPartition topicPartition) {
        return partitionOffsets.get(topicPartition);
    }

    public void destory() {
        try {
            // save offset first
            run();
            for (TopicPartition topicPartition : partitionConsumers.keySet()) {
                partitionConsumers.get(topicPartition).close();
            }
            this.client.close();
            this.timer.cancel();
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    private long getOffset(String topic, int partition, SimpleConsumer consumer, String offsetZkPath) throws Exception {
        long latestOffset = findOffset(consumer, topic, partition, LatestTime());
        long earliestOffset = findOffset(consumer, topic, partition, EarliestTime());

        Stat stat = client.checkExists().forPath(offsetZkPath);
        if (stat == null) {
            client.create().creatingParentsIfNeeded().forPath(offsetZkPath);
            return autoOffsetReset == 1 ? latestOffset : earliestOffset;
        } else {
            byte[] bs = client.getData().forPath(offsetZkPath);
            long oldOffset = NumberUtils.toLong(SafeEncoder.encode(bs));
            if (oldOffset >= earliestOffset && oldOffset <= latestOffset) {
                logger.info("Offset for group {} partition {}-{} is {}.", groupId, topic, partition, oldOffset);
                return oldOffset;
            } else {
                long resetOffset = autoOffsetReset == 1 ? latestOffset : earliestOffset;
                logger.info("Offset for group {} partition {}-{} is {}. Offset range is {}-{}, reset to {}.", groupId,
                        topic, partition, oldOffset, earliestOffset, latestOffset, resetOffset);
                return resetOffset;
            }
        }
    }

    private long findOffset(SimpleConsumer consumer, String topic, int partition, long whichTime) {
        Map<TopicAndPartition, PartitionOffsetRequestInfo> reqInfo = new HashMap<TopicAndPartition, PartitionOffsetRequestInfo>();
        reqInfo.put(new TopicAndPartition(topic, partition), new PartitionOffsetRequestInfo(whichTime, 1));
        OffsetRequest request = new OffsetRequest(reqInfo, CurrentVersion(), consumer.clientId());
        OffsetResponse response = consumer.getOffsetsBefore(request);
        if (response.hasError()) {
            logger.warn("Error fetching data Offset Data the Broker. Reason: {}", response.errorCode(topic, partition));
            return -1;
        }
        long[] offsets = response.offsets(topic, partition);
        return offsets[0];
    }

    private HostAndPort findLeader(TopicPartition topicPartition) throws InterruptedException {
        while (true) {
            PartitionMetadata metadata = findLeaderOnce(topicPartition);
            if (metadata == null || metadata.leader() == null) {
                Thread.sleep(1000);
                logger.warn("Unable to find new leader after Broker failure. Sleep and retry.");
            } else {
                BrokerEndPoint broker = metadata.leader();
                logger.warn("Find leader {}:{} for partition {}.", broker.host(), broker.port(), topicPartition);
                return HostAndPort.fromParts(broker.host(), broker.port());
            }
        }
    }

    private PartitionMetadata findLeaderOnce(TopicPartition topicPartition) {
        PartitionMetadata returnMetaData = null;
        loop:
        for (HostAndPort broker : bootstrapServers) {
            SimpleConsumer consumer = null;
            try {
                consumer = new SimpleConsumer(broker.getHostText(), broker.getPort(), sotimeout, bufferSize,
                        "leaderLookup");
                TopicMetadataRequest req = new TopicMetadataRequest(Arrays.asList(topicPartition.topic()));
                TopicMetadataResponse resp = consumer.send(req);

                List<TopicMetadata> metaData = resp.topicsMetadata();
                for (TopicMetadata item : metaData) {
                    for (PartitionMetadata part : item.partitionsMetadata()) {
                        if (part.partitionId() == topicPartition.partition()) {
                            returnMetaData = part;
                            break loop;
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Error communicating with Broker {} to find Leader for {}.", broker, topicPartition, e);
            } finally {
                if (consumer != null)
                    consumer.close();
            }
        }
        return returnMetaData;
    }

    @Override
    public void run() {
        try {
            for (TopicPartition topicPartition : partitionOffsets.keySet()) {
                String path = partitionZkPath.get(topicPartition);
                MutableLong partitionOffset = partitionOffsets.get(topicPartition);
                if (partitionOffset.longValue() != -1) {
                    String offset = String.valueOf(partitionOffset.longValue());
                    byte[] bs = SafeEncoder.encode(offset);
                    client.setData().forPath(path, bs);
                    logger.info("Sync partition {}-{} offset to {}", topicPartition.topic(), topicPartition.partition(),
                            offset);
                }
            }
            logger.info("Sync partition offset");
        } catch (Exception e) {
            logger.error("", e);
        }
    }

}
