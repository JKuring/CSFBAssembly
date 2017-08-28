package com.eastcom.csfb.storm.kafka.low;

import com.eastcom.csfb.storm.kafka.ConfigKey;
import com.eastcom.csfb.storm.kafka.ITopicCsvParser;
import com.eastcom.csfb.storm.kafka.ReadHook;
import com.google.common.base.Charsets;
import kafka.api.FetchRequest;
import kafka.api.FetchRequestBuilder;
import kafka.javaapi.FetchResponse;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.message.MessageAndOffset;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.mutable.MutableLong;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import static com.eastcom.csfb.storm.kafka.RecordProcessor.processRecord;


public class OldTopicConsumer<QV> extends Thread {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private List<TopicPartition> topicPartitions;
    private ITopicCsvParser topicCsvParser;
    private ReadHook<QV> readHook;

    private PartitionStateManager stateManager;
    private int fetchSize;

    public OldTopicConsumer(Map<String, Object> conf, String threadName, List<TopicPartition> topicPartitions,
                            ReadHook<QV> readHook, ITopicCsvParser topicCsvParser) {
        super(threadName);
        this.topicPartitions = topicPartitions;
        this.readHook = readHook;
        this.topicCsvParser = topicCsvParser;
        this.fetchSize = MapUtils.getIntValue(conf, ConfigKey.KAFKA_OLD_LOW_FETCH_SIZE, 10_0000);

        this.stateManager = new PartitionStateManager(conf);
        for (TopicPartition topicPartition : topicPartitions) {
            this.stateManager.register(topicPartition);
        }
    }

    @Override
    public void run() {
        logger.info("Thread {} started.", this.getName());
        int[] partitionWaitTimes = new int[topicPartitions.size()];
        while (this.isInterrupted() == false) {
            try {
                long totalRead = 0;
                for (int i = 0; i < topicPartitions.size(); i++) {
                    if (partitionWaitTimes[i] > 0) {
                        partitionWaitTimes[i]--;
                        continue;
                    }
                    TopicPartition topicPartition = topicPartitions.get(i);
                    long readNum = doRun(topicPartition);
                    if (readNum <= 0) {
                        partitionWaitTimes[i] = 10;
                    }
                    totalRead += readNum;
                }
                if (totalRead == 0) {
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                this.interrupt();
            } catch (Throwable e) {
                logger.error("", e);
            }
        }
        stateManager.destory();
        logger.warn("Thread {} exit.", this.getName());
    }

    private long doRun(TopicPartition topicPartition) throws Exception {
        SimpleConsumer consumer = stateManager.getConsumer(topicPartition);
        MutableLong offset = stateManager.getOffset(topicPartition);

        long numRead = fetchData(consumer, topicPartition, offset);
        if (numRead < 0) {
            stateManager.resetConsumer(topicPartition);
        }
        return numRead;
    }

    private long fetchData(SimpleConsumer consumer, TopicPartition topicPartition, MutableLong offset)
            throws Exception {
        FetchRequest req = new FetchRequestBuilder().clientId(consumer.clientId())
                .addFetch(topicPartition.topic(), topicPartition.partition(), offset.longValue(), fetchSize).build();
        FetchResponse fetchResponse = consumer.fetch(req);
        if (fetchResponse.hasError()) {
            short code = fetchResponse.errorCode(topicPartition.topic(), topicPartition.partition());
            logger.error("Error fetching data from the partition: {}:{} Reason: {}", topicPartition.topic(),
                    topicPartition.partition(), code);
            return -1;
        }

        long numRead = 0;
        for (MessageAndOffset messageAndOffset : fetchResponse.messageSet(topicPartition.topic(),
                topicPartition.partition())) {
            long currentOffset = messageAndOffset.offset();
            if (currentOffset < offset.longValue()) {
                logger.warn("Found an old offset: {} Expecting: {}", currentOffset, offset);
                continue;
            }
            offset.setValue(messageAndOffset.nextOffset());
            ByteBuffer payload = messageAndOffset.message().payload();
            byte[] bytes = new byte[payload.limit()];
            payload.get(bytes);
            String record = new String(bytes, Charsets.UTF_8);
            processRecord(topicPartition.topic(), record, topicCsvParser, readHook);
            numRead++;
        }
        return numRead;
    }

}
