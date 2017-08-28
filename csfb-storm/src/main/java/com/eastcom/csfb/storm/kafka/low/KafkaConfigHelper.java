package com.eastcom.csfb.storm.kafka.low;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class KafkaConfigHelper {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConfigHelper.class);

    public static Properties getKafkaConsumerConfig(Map<String, Object> stormConfig) {
        String prefix = "kafka.";
        final Properties prop = new Properties();
        for (Map.Entry<String, Object> entry : stormConfig.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                prop.setProperty(entry.getKey().substring(prefix.length()), String.valueOf(entry.getValue()));
            }
        }
        return prop;
    }

    public static List<TopicPartition> getSlotPartitions(Properties configProps, List<String> topics,
                                                         int spoutParallelism, int slot) {
        List<TopicPartition> kafkaPartitions = getTopicPartitions(topics, configProps);
        List<TopicPartition> tps = new ArrayList<>();
        int index = slot;
        while (index < kafkaPartitions.size()) {
            tps.add(kafkaPartitions.get(index));
            index += spoutParallelism;
        }
        logger.warn("Slot {}, responsible for partition {}.", slot, tps);
        return tps;
    }

    private static List<TopicPartition> getTopicPartitions(List<String> topics, Properties configProps) {
        Consumer<byte[], byte[]> consumer = new KafkaConsumer<>(configProps);
        Map<String, List<PartitionInfo>> listTopics = consumer.listTopics();// partitionsFor
        List<TopicPartition> topicPartitions = new ArrayList<>();
        for (String topic : topics) {
            List<PartitionInfo> list = listTopics.get(topic);
            int psize = list.size();
            logger.info("Topic {} partition {}.", topic, psize);
            for (int i = 0; i < psize; i++) {
                topicPartitions.add(new TopicPartition(topic, i));
            }
        }
        consumer.close();
        return topicPartitions;
    }

    public static <T> List<List<T>> fixedSize(List<T> list, int size) {
        List<List<T>> result = new ArrayList<List<T>>();
        int index = 0;
        while (index + size <= list.size()) {
            result.add(list.subList(index, index + size));
            index += size;
        }
        if (index < list.size()) {
            result.add(list.subList(index, list.size()));
        }
        return result;
    }

}
