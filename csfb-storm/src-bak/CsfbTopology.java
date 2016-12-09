package com.eastcom.csfb.storm.topo;

import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.BoltDeclarer;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static org.apache.commons.collections4.MapUtils.getIntValue;

public class CsfbTopology {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @SuppressWarnings("unchecked")
    public StormTopology getCsfbTopology(Map<String, Object> conf) {

        List<String> topicNames = (List<String>) conf.get("csfb.spout.topics");
        int fileReadThreads = getIntValue(conf, "csfb.spout.threads.file");
        int redisReadThreads = getIntValue(conf, "csfb.spout.threads.redis");
        int partitionSize = getIntValue(conf, "csfb.partition.size");
        int spoutParallelism = getIntValue(conf, "csfb.spout.parallelism");
        int boltParallelism = Math.abs(getIntValue(conf, "csfb.bolt.parallelism"));
        logger.error(String.valueOf(fileReadThreads));
        logger.error(String.valueOf(boltParallelism));

        TopologyBuilder builder = new TopologyBuilder();

        // TODO: define BoltDeclarer
        // 按照分区范围分割定义多个spout

        CheckTimeBolt checkTimeBolt = new CheckTimeBolt();
        BoltDeclarer boltDeclarer = builder.setBolt("checkTime", checkTimeBolt, boltParallelism);

        int partitionPerSpout = (int) Math.ceil(partitionSize * 1f / spoutParallelism);
        for (int i = 0; i < partitionSize; i += partitionPerSpout) {
            int from = i;
            int to = from + partitionPerSpout - 1;
            if (to >= partitionSize) {
                to = partitionSize - 1;
            }
            CsfbSpout csfbSpout = new CsfbSpout(topicNames, fileReadThreads, redisReadThreads, partitionSize, from, to);
            String spoutName = "spout-" + from + "-" + to;
            builder.setSpout(spoutName, csfbSpout, 1);
            boltDeclarer.fieldsGrouping(spoutName, new Fields("partition"));

        }

        return builder.createTopology();
    }

}
