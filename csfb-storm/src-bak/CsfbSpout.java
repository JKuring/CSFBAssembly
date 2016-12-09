package com.eastcom.csfb.storm.topo;

import com.eastcom.csfb.data.CSVParser;
import com.eastcom.csfb.data.UserCommon;
import com.eastcom.csfb.storm.base.BeanFactory;
import com.eastcom.csfb.storm.base.RedisBatchExector;
import com.eastcom.csfb.storm.base.TopicCSVParsers;
import com.eastcom.csfb.storm.base.msg.MsgReceiver;
import com.eastcom.csfb.storm.base.msg.RedisReceiver;
import com.eastcom.csfb.storm.base.reader.Readable;
import com.eastcom.csfb.storm.base.reader.XdrReader;
import com.eastcom.csfb.storm.base.util.DateUtils;
import com.eastcom.csfb.storm.base.util.KryoUtils;
import com.esotericsoftware.kryo.Kryo;
import com.google.common.base.Charsets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;

import java.io.BufferedReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static com.eastcom.csfb.storm.base.util.StringUtils.toKey;
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
 * @author louyj
 */
public class CsfbSpout extends BaseRichSpout {

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

    private List<String> topicNames;
    private int fileReadThreads = 5, redisReadThreads = 5; // 线程数
    private int partitionSize = 1000;// 分区总数
    private int partitionBegin, partitionEnd;// 该spout负责读取的分区范围

    @SuppressWarnings("rawtypes")
    private Map conf;

    private MsgReceiver msgReceiver;

    /**
     * Initialize the CSFB Spout.
     *
     * @param topicNames
     * @param fileReadThreads
     * @param redisReadThreads
     * @param partitionSize
     * @param partitionBegin
     * @param partitionEnd
     */
    public CsfbSpout(List<String> topicNames, int fileReadThreads, int redisReadThreads, int partitionSize,
                     int partitionBegin, int partitionEnd) {
        super();
        this.topicNames = topicNames;
        this.fileReadThreads = fileReadThreads;
        this.redisReadThreads = redisReadThreads;
        this.partitionSize = partitionSize;
        this.partitionBegin = partitionBegin;
        this.partitionEnd = partitionEnd;
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
        this.beanFactory = new BeanFactory(conf);
        this.jedisPool = beanFactory.getJedisPool();
        this.collector = collector;
        this.xdrReader = new XdrReader(conf);
        this.bufferQueue = new ArrayBlockingQueue<Values>(bufferQueueSize);
        this.hashFunction = Hashing.murmur3_128();
        this.conf = conf;
        // start threads

        logger.error("partitionSize:{}", partitionSize);

        String brokerURL = MapUtils.getString(conf, "csfb.activemq.broker.url");
        String queueName = MapUtils.getString(conf, "csfb.activemq.queue.name");
        this.msgReceiver = new RedisReceiver("10.221.247.5", 6498, "stream!23$", "file_uri_queue3");

        for (int i = 0; i < fileReadThreads; i++) {
            new FileReader("file-reader-" + i).start();
        }

        // read the batch data form redis to bufferQueue (concurrent ArrayList)
        int partitionSize = this.partitionEnd - this.partitionBegin + 1;
        int partitionPerThread = (int) Math.ceil(partitionSize * 1f / this.redisReadThreads);
        int threadIndex = 0;
        for (int i = this.partitionBegin; i < this.partitionEnd; i += partitionPerThread) {
            int from = i;
            int to = from + partitionPerThread - 1;
            if (to > this.partitionEnd) {
                to = this.partitionEnd;
            }
            String threadName = String.format("redis-reader-%d(%d-%d)", threadIndex, from, to);
            new RedisReader(threadName, from, to).start();
            logger.info(threadName);
            threadIndex++;
        }
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
                DateUtils.sleep(1);
                break;
            }
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("csvData", "partition"));
    }

    /**
     *
     */
    private class FileReader extends Thread {

        private RedisBatchExector rbe;

        private Kryo kryo;

        public FileReader(String name) {
            super(name);
            kryo = KryoUtils.create(conf);
        }

        @Override
        public void run() {
            // according to JedisPool to create Jedis Pipeline.
            rbe = new RedisBatchExector(jedisPool);
            rbe.open();
            while (this.isInterrupted() == false) {
                try {
                    doRead();
                } catch (Exception e) {
                    logger.error("", e);
                    rbe.broken();
                    DateUtils.sleep(100);
                    rbe.open();
                }
            }
            rbe.close();
        }

        protected void doRead() throws Exception {
            String fileUri = popFileUri();
            //
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
            CSVParser<? extends UserCommon> csvParser = TopicCSVParsers.getCSVParser(topicName);
            if (csvParser == null) {
                logger.error("No parser found for topic {}.", topicName);
                return;
            }
            parseFileLines(topicName, csvParser, fileUri);
        }

        /**
         * add line data to zset as the sequence set
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
                    if (csvObject != null) {
                        String imsi = csvObject.getImsi();
                        int imsiHash = hashFunction.newHasher().putString(imsi, Charsets.UTF_8).hash().asInt();
                        // 根据imsi产生的hash，获取数据分区
                        int partition = Math.abs(imsiHash % partitionSize);
                        String key = toKey(redisKey, "{" + partition + "}");
                        byte[] bs = KryoUtils.serialize(kryo, csvObject);
                        long time = csvObject.getStartTime();
                        rbe.zadd(encode(key), time, bs);
                    }
                    line = reader.readLine();
                    lines++;
                }
                long useTime = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - ss);
                logger.info("文件{}处理完毕. 发送{}条消息. 用时{}秒.", fileUri, lines, useTime);
            }
        }

        // TODO: 对数据做过滤, 只保留csfb需要的数据
        private UserCommon filter(UserCommon csvObject) {
            return csvObject;
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
            UserCommon data = null;
            if (csvParser != null) {
                data = csvParser.parse(message, uri);
            } else {
                data = TopicCSVParsers.parse(topic, message, uri);
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
            kryo = KryoUtils.create(conf);
            logger.warn("thread:{},begin:{},end:{}.", name, partitionBegin, partitionEnd);
        }

        @Override
        public void run() {
            while (this.isInterrupted() == false) {
                try {
                    doRun();
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        }

        /**
         * 批处理数据，并更新数据 默认Redis时间窗口为 30分钟
         *
         * @throws InterruptedException
         */
        private void doRun() throws InterruptedException {
            long timeEnd = System.currentTimeMillis() - 1 * 60_000;
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

}
