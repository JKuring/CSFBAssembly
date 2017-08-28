package com.eastcom.csfb.storm.kafka.low;

import com.eastcom.csfb.storm.kafka.ConfigKey;
import com.eastcom.csfb.storm.kafka.ITopicCsvParser;
import com.eastcom.csfb.storm.kafka.ReadHook;
import org.apache.commons.collections4.MapUtils;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.eastcom.csfb.storm.kafka.low.KafkaConfigHelper.*;
import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Louyj
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class KafkaOldLowReader<QV> {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private PartitionSlotManager partitionManager;

    public KafkaOldLowReader(final Map conf, final ReadHook<QV> readHook, final ITopicCsvParser topicCsvParser) {
        checkArgument(conf.get(ConfigKey.KAFKA_GROUP_ID) != null);
        checkArgument(conf.get(ConfigKey.KAFKA_BOOTSTRAP_SERVERS) != null);
        checkArgument(conf.get(ConfigKey.KAFKA_TOPIC_NAMES) != null);

        final int threadNum = MapUtils.getInteger(conf, ConfigKey.PROJECT_SPOUT_KAFKA_READER_THREADS, 3);
        final Properties configProps = getKafkaConsumerConfig(conf);

        this.partitionManager = new PartitionSlotManager(conf) {

            @Override
            public List<Thread> startWorkers(int slot) {
                int spoutParallelism = MapUtils.getIntValue(conf, ConfigKey.PROJECT_SPOUT_PARALLELISM, 128);
                List<String> topics = (List<String>) conf.get(ConfigKey.KAFKA_TOPIC_NAMES);

                List<TopicPartition> topicPartitions = getSlotPartitions(configProps, topics, spoutParallelism, slot);
                int partitionPerThread = (int) Math.ceil(topicPartitions.size() * 1f / threadNum);
                List<List<TopicPartition>> list = fixedSize(topicPartitions, partitionPerThread);

                List<Thread> threads = new ArrayList<>();
                int threadIndex = 0;
                for (List<TopicPartition> tps : list) {
                    String threadName = "kafka-reader-" + threadIndex + "[" + tps + "]";
                    OldTopicConsumer<QV> tc = new OldTopicConsumer<>(conf, threadName, tps, readHook, topicCsvParser);
                    tc.start();
                    logger.info("Thread {} for {} started.", threadName, tps);
                    threads.add(tc);
                }
                return threads;
            }
        };
        this.partitionManager.start();

    }

}
