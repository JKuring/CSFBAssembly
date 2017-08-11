package com.eastcom.csfb.storm.topo;

import com.eastcom.csfb.data.CSVParser;
import com.eastcom.csfb.data.UserCommon;
import com.eastcom.csfb.storm.base.BeanFactory;
import com.eastcom.csfb.storm.base.RedisBatchExector;
import com.eastcom.csfb.storm.base.TopicCSVParsers;
import com.eastcom.csfb.storm.base.msg.MqReceiver;
import com.eastcom.csfb.storm.base.msg.MsgReceiver;
import com.eastcom.csfb.storm.base.reader.Readable;
import com.eastcom.csfb.storm.base.reader.XdrReader;
import com.eastcom.csfb.storm.base.util.DateUtils;
import com.eastcom.csfb.storm.base.util.KryoUtils;
import com.eastcom.csfb.storm.kafka.ConfigKey;
import com.eastcom.csfb.storm.kafka.KafkaReader;
import com.eastcom.csfb.storm.kafka.ReadHook;
import com.esotericsoftware.kryo.Kryo;
import com.google.common.base.Charsets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.EnsurePath;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;

import java.io.BufferedReader;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static com.eastcom.csfb.storm.base.util.StringUtils.toKey;
import static org.apache.commons.collections4.MapUtils.getIntValue;
import static org.apache.commons.lang3.StringUtils.join;
import static redis.clients.util.SafeEncoder.encode;

/**
 * 对数据做预排序
 * <p>
 * FileReader线程: 对将读取到的数据按imsi号做hash, 将数据分片到 {partitionSize} 个 sort set做预排序.
 * <p>
 * RedisReader线程: 从redis分片的sort set中读取数据, 为保证数据的有序性 单个 sort set 只能被一个线程消费.
 * <p>
 * spout 数据分发: 对RedisReader读取到的数据, 按imsi hash(分区索引) 分发到下游 bolt中.
 * 从而保证数据对同一imsi号在时间上是有序的. (spout发出的数据整体不是时间有序的)
 *
 * @author linghang.kong
 */
public class CsfbSpout extends BaseRichSpout implements ReadHook {

    private static final long serialVersionUID = 1L;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String redisKey = "data:sort";
    private final int bufferQueueSize = 2500;
    private SpoutOutputCollector collector;
    private BeanFactory beanFactory;
    private Pool<Jedis> jedisPool;
    private Readable xdrReader;
    private BlockingQueue<Values> bufferQueue;
    private HashFunction hashFunction;

    private String spoutType;
    private TopicCSVParsers topicCSVParsers;

    private RedisBatchExector redisBatchExector;

    private Kryo kryo;

    private List<String> topicNames;
    private int partitionSize;
    private int redisBufferMs;

    private MsgReceiver msgReceiver;

    private PartitionManager partitionManager;


    /**
     * Initialize the CSFB Spout.
     */
    public CsfbSpout() {
        super();
    }

    /**
     * get parameters from the conf object.
     *
     * @param conf
     * @param context
     * @param collector
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.topicNames = (List<String>) conf.get(ConfigKey.PROJECT_SPOUT_FILE_TOPICS);

        this.kryo = KryoUtils.create();


        this.beanFactory = new BeanFactory(conf);
        this.jedisPool = beanFactory.getJedisPool();
        this.collector = collector;

        this.redisBatchExector = new RedisBatchExector(jedisPool);
        this.redisBatchExector.open();

        this.bufferQueue = new ArrayBlockingQueue<Values>(bufferQueueSize);
        this.partitionSize = MapUtils.getIntValue(conf, ConfigKey.PROJECT_REDIS_PARTITION_SIZE, 1000);
        this.redisBufferMs = MapUtils.getIntValue(conf, ConfigKey.PROJECT_SPOUT_REDIS_BUFFER_MS, 30 * 60_000);
        this.hashFunction = Hashing.murmur3_128();
        this.spoutType = MapUtils.getString(conf, ConfigKey.PROJECT_SPOUT_TYPE);


        if (Objects.equals(this.spoutType, "ftp")) {
            // 获取Ftp配置 使用commons
            this.xdrReader = new XdrReader(conf);
            // start threads
            String brokerURL = MapUtils.getString(conf, ConfigKey.PROJECT_MQ_BROKER_URL);
            String queueName = MapUtils.getString(conf, ConfigKey.PROJECT_MQ_QUEUE_NAME);
            int fileReadThreads = MapUtils.getIntValue(conf, ConfigKey.PROJECT_SPOUT_FTP_READER_THREADS, 5);
            this.msgReceiver = new MqReceiver(brokerURL, queueName);
            for (int i = 0; i < fileReadThreads; i++) {
                new FileReader("file-reader-" + i).start();
            }
        } else if (Objects.equals(this.spoutType, "kafka")) {
            this.topicCSVParsers = this.beanFactory.getTopicCsvParser();
            new KafkaReader<>(conf, this, this.topicCSVParsers);
        }

        partitionManager = new PartitionManager(conf);
        partitionManager.start();
    }

    /**
     * 把队列中的元素发送出去
     */
    @Override
    public void nextTuple() {
        for (int i = 0; i < bufferQueueSize; i++) {
            Values v = bufferQueue.poll();// 返回队列首元素，同时移除队列头元素。
            if (v != null) {
                collector.emit(v);
            } else {
                DateUtils.sleep(10);
                break;
            }
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("csvData", "partition"));
    }

    // TODO: 对数据做过滤, 只保留csfb需要的数据
    private UserCommon filter(UserCommon csvObject) {
        if (csvObject == null)
            return null;
        String imsi = csvObject.getImsi();
        if (StringUtils.isEmpty(imsi) || "0".equals(imsi)) {
            return null;
        }
        return csvObject;
    }

    private void toRedisPartition(UserCommon csvObject) {
        if (csvObject != null) {
            String imsi = csvObject.getImsi();
            int imsiHash = hashFunction.newHasher().putString(imsi, Charsets.UTF_8).hash().asInt();
            // 根据imsi产生的hash，创建redis数据分区
            int partition = Math.abs(imsiHash % partitionSize);
            String key = toKey(redisKey, "{" + partition + "}");
            byte[] bs = KryoUtils.serialize(kryo, csvObject);
            long time = csvObject.getStartTime();
            // 添加数据到zset中 jp.getPipeline().zadd(key, score, member);
            redisBatchExector.zadd(encode(key), time, bs);
        }
    }

    @Override
    public void afterEmitFile(String topic, int lines, long useTime) {

    }

    @Override
    public void afterEmit(String topic, Object data, String message) {

    }

    @Override
    public void putValues(BlockingQueue bufferQueue, Object data, String topicName) throws Exception {
        try {
            // 获取并初始化对象
            UserCommon csvObject = (UserCommon) data;
            csvObject = filter(csvObject);
            toRedisPartition(csvObject);
        } catch (Exception e) {
            redisBatchExector.broken();
            DateUtils.sleep(100);
            redisBatchExector.open();
            throw e;
        }
    }

    @Override
    public BlockingQueue getBufferQueue() {
        return bufferQueue;
    }

    @Override
    public void afterParse(Object data) {

    }

    /**
     *
     */
    private class FileReader extends Thread {

//        private RedisBatchExector rbe;

//        private Kryo kryo;

        public FileReader(String name) {
            super(name);
//            kryo = KryoUtils.create();
        }

        @Override
        public void run() {
            // according to JedisPool to create Jedis Pipeline.
//            rbe = new RedisBatchExector(jedisPool);
//            rbe.open();
            while (!this.isInterrupted()) {
                try {
                    doRead();
                } catch (Exception e) {
                    logger.error("", e);
                    redisBatchExector.broken();
                    DateUtils.sleep(100);
                    redisBatchExector.open();
                }
            }
            redisBatchExector.close();
        }

        protected void doRead() throws Exception {
            // 接收MQ对象消息 msgReceiver.receive();
            String fileUri = popFileUri();
            // 接收不到MQ则等待1000 毫秒
            if (StringUtils.isEmpty(fileUri)) {
                DateUtils.sleep(1000);
                return;
            }
            // 根据不同的Ftp文件uri区分所读文件的类型，来建立相应的对象
            String topicName = parseTopicName(fileUri);
            if (StringUtils.isEmpty(topicName)) {
                return;
            }
            // TopicCSVParsers is a factory class of CSV parser. According to
            // 'topicName' ,it choose a CSV parser object.
            // eg, lte S1-AP, SGs, etc.
            CSVParser<? extends UserCommon> csvParser = topicCSVParsers.getCSVParser(topicName);
            if (csvParser == null) {
                logger.error("No parser found for topic {}.", topicName);
                return;
            }
            parseFileLines(topicName, csvParser, fileUri);
        }

        /**
         * add line data to zset as the sequence set
         * 以imsi作为分区，以start time作为排序
         *
         * @param topicName
         * @param csvParser
         * @param fileUri
         * @throws Exception
         */
        protected void parseFileLines(String topicName, CSVParser<? extends UserCommon> csvParser, String fileUri)
                throws Exception {
            long ss = System.nanoTime();
            int lines = 0;

            try (BufferedReader reader = xdrReader.read(fileUri)) {
                String line = reader.readLine();
                while (line != null) {
                    // 获取并初始化对象
                    UserCommon csvObject = toObject(topicName, csvParser, line, fileUri);
                    csvObject = filter(csvObject);
                    toRedisPartition(csvObject);
                    line = reader.readLine();
                    lines++;
                }
                long useTime = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - ss);
                logger.debug("文件{}处理完毕. 发送{}条消息. 用时{}秒.", fileUri, lines, useTime);
            }
        }


        private String parseTopicName(String fileUri) {
            for (String topicName : topicNames) {
                if (fileUri.contains(topicName)) {
                    return topicName;
                }
            }
            return null;
        }

        protected String popFileUri() throws Exception {
            return msgReceiver.receive();
        }

        protected UserCommon toObject(String topic, CSVParser<? extends UserCommon> csvParser, String message,
                                      String uri) {
            UserCommon data;
            if (csvParser != null) {
                data = csvParser.parse(message, uri);
            } else {
                data = topicCSVParsers.parse(topic, message, uri);
            }
            return data;
        }

    }

    private class RedisReader extends Thread {

        private int partitionBegin;
        private int partitionEnd;

        private long lastTime = 0;
        private Kryo kryo;

        public RedisReader(String name, int partitionBegin, int partitionEnd) {
            super(name);
            this.partitionBegin = partitionBegin;
            this.partitionEnd = partitionEnd;
            kryo = KryoUtils.create();
        }

        @Override
        public void run() {
            while (!this.isInterrupted()) {
                try {
                    doRun();
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        }

        /**
         * 批处理数据，并更新数据 默认Redis时间窗口为 60分钟
         * 每隔1秒读取一次一个小时前数据，窗口为一秒
         * 以start time作为时间的排序和获取，而不是进入zset的时间
         * 默认为1秒获取一次并发送一次
         *
         * @throws InterruptedException
         */
        private void doRun() throws InterruptedException {
            long timeEnd = System.currentTimeMillis() - redisBufferMs;
            for (int index = partitionBegin; index <= partitionEnd; index++) {
                String key = toKey(redisKey, "{" + index + "}");
                Set<byte[]> datas = fetchDatas(key, timeEnd);
                for (byte[] data : datas) {
                    UserCommon csvData = KryoUtils.deserialize(kryo, data);
                    bufferQueue.put(new Values(csvData, index));
                }
                deleteDatas(key, timeEnd);
            }
            lastTime = timeEnd;
            DateUtils.sleep(1000);
        }

        private Set<byte[]> fetchDatas(String key, long timeEnd) {
            try (Jedis jedis = jedisPool.getResource()) {
                Set<byte[]> linkedSet = jedis.zrangeByScore(encode(key), lastTime, timeEnd);
                return linkedSet;
            } catch (Exception ex) {
                logger.error("fetch {} -- {}:{}:{}", ex.getMessage(), key, lastTime, timeEnd);
                return Collections.emptySet();
            }
        }

        /**
         * 按照截至时间戳，删除指定的key数据
         *
         * @param key
         * @param timeEnd
         */
        private void deleteDatas(String key, long timeEnd) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.zremrangeByScore(key, 0, timeEnd);
            } catch (Exception ex) {
                logger.error("delete {} -- {}:{}:{}", ex.getMessage(), key, 0, timeEnd);
            }
        }

    }

    class PartitionManager {

        private Logger logger = LoggerFactory.getLogger(getClass());

        private CuratorFramework client;
        private NodeCache nodeCache;
        private String basePath = "/slot/";

        private int partitionSize;
        private int spoutParallelism;
        private int redisReadThreads;
        private String zkConnectionString;

        private List<Thread> threads = new ArrayList<>();

        @SuppressWarnings({"rawtypes", "unchecked"})
        public PartitionManager(Map conf) {
            partitionSize = getIntValue(conf, ConfigKey.PROJECT_REDIS_PARTITION_SIZE, 1000);
            spoutParallelism = getIntValue(conf, ConfigKey.PROJECT_SPOUT_PARALLELISM, 10);
            redisReadThreads = getIntValue(conf, ConfigKey.PROJECT_SPOUT_REDIS_READER_THREADS, 5);
            zkConnectionString = MapUtils.getString(conf, ConfigKey.PROJECT_SPOUT_ZK_CONNECT);
        }

        public void start() {
            init();
            rebalance();
        }

        private void rebalance() {
            logger.warn("Stoping cousumers ....");
            for (Thread thread : threads) {
                thread.interrupt();
            }
            threads.clear();
            logger.warn("Lookup available slot ....");
            int slot = findAvailSlot();
            logger.info("Available slot: {}.", slot);
            startWorker(slot);
            logger.warn("Cousumers started.");
        }

        private void startWorker(int slot) {
            int partitionPerSpout = (int) Math.ceil(partitionSize * 1f / spoutParallelism);
            int partitionBegin = partitionPerSpout * slot;
            int partitionEnd = partitionBegin + partitionPerSpout - 1;
            if (partitionEnd >= partitionSize) {
                partitionEnd = partitionSize - 1;
            }
            logger.warn("Slot {}, responsible for partition {} -> {}", slot, partitionBegin, partitionEnd);
            int partitionSize = partitionEnd - partitionBegin + 1;
            int partitionPerThread = (int) Math.ceil(partitionSize * 1f / this.redisReadThreads);
            int threadIndex = 0;
            for (int i = partitionBegin; i < partitionEnd; i += partitionPerThread) {
                int from = i;
                int to = from + partitionPerThread - 1;
                if (to > partitionEnd) {
                    to = partitionEnd;
                }
                String threadName = String.format("redis-reader-%d(%d-%d)", threadIndex, from, to);
                RedisReader redisReader = new RedisReader(threadName, from, to);
                redisReader.start();
                logger.warn("Start worker {}, responsible for partition {} -> {}.", threadName, from, to);
                threadIndex++;
                threads.add(redisReader);
            }
        }

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
                DateUtils.sleep(1000);
            }
        }

        private void registerListener(String path) throws Exception {
            if (nodeCache != null) {
                nodeCache.close();
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
                    logger.warn("Node status changed, {} --> {}", clientSid, nodeSid);
                    if (clientSid != nodeSid) {
                        rebalance();
                    }
                }
            });
            nodeCache.start();
        }

        private void init() {
            try {
                RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
                client = CuratorFrameworkFactory.builder().connectString(zkConnectionString).retryPolicy(retryPolicy)
                        .sessionTimeoutMs(30000).build();
                client.start();
                EnsurePath ensurePath = new EnsurePath("/");
                ensurePath.ensure(client.getZookeeperClient());
            } catch (Exception e) {
                logger.error("", e);
                throw new RuntimeException(e);
            }
        }

        public void close() {
            for (Thread thread : threads) {
                thread.interrupt();
            }
            client.close();
        }

    }
}
