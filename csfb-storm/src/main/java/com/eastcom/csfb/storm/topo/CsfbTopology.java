package com.eastcom.csfb.storm.topo;

import com.eastcom.csfb.storm.kafka.ConfigKey;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.hdfs.bolt.format.DefaultFileNameFormat;
import org.apache.storm.hdfs.bolt.format.DelimitedRecordFormat;
import org.apache.storm.hdfs.bolt.format.FileNameFormat;
import org.apache.storm.hdfs.bolt.format.RecordFormat;
import org.apache.storm.hdfs.bolt.rotation.FileRotationPolicy;
import org.apache.storm.hdfs.bolt.rotation.TimedRotationPolicy;
import org.apache.storm.hdfs.bolt.sync.CountSyncPolicy;
import org.apache.storm.hdfs.bolt.sync.SyncPolicy;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

import java.util.List;
import java.util.Map;

import static org.apache.commons.collections4.MapUtils.getIntValue;
import static org.apache.commons.collections4.MapUtils.getString;

public class CsfbTopology {

    // Hdfs Service
    private static String HDFS_PATH;
    private static String HDFS_URL;
    private static final float CREATE_HDFS_FILE_INTERVAL = 5.0f;
    private static final int SYNC_COUNT = 10000;

    /**
     * create a topology
     *
     * @param conf Storm configuration
     * @return A topology object
     */
    @SuppressWarnings("unchecked")
    public StormTopology getCsfbTopology(Map<String, Object> conf) {

        List<String> topicNames = (List<String>) conf.get(ConfigKey.PROJECT_SPOUT_FILE_TOPICS);
        int spoutParallelism = getIntValue(conf, ConfigKey.PROJECT_SPOUT_PARALLELISM);
        int boltParallelism = Math.abs(getIntValue(conf, ConfigKey.PROJECT_BOLT_PARALLELISM));

        HDFS_URL = getString(conf,"hdfs.url");
        HDFS_PATH = getString(conf,"hdfs.path");

        String spoutName = "csfbSpout";
        String extractSignalBoltName = "extractSignalBolt";
        String outputDataBoltName = "outputDataBolt";

        CsfbSpout csfbSpout = new CsfbSpout(topicNames);
        ExtractSignalBolt extractSignalBolt = new ExtractSignalBolt();
        OutputDataBolt outputDataBolt = new OutputDataBolt();

        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout(spoutName, csfbSpout, spoutParallelism);
        builder.setBolt(extractSignalBoltName, extractSignalBolt, boltParallelism).fieldsGrouping(spoutName,
                new Fields("partition"));

        // use "|" instead of "," for field delimiter
        RecordFormat format = new DelimitedRecordFormat().withFieldDelimiter("|");
        // sync the filesystem after every 1k tuples
        SyncPolicy syncPolicy = new CountSyncPolicy(SYNC_COUNT);
        // rotate files， 5 minutes
        FileRotationPolicy rotationPolicy = new TimedRotationPolicy(CREATE_HDFS_FILE_INTERVAL, TimedRotationPolicy.TimeUnit.MINUTES);
        FileNameFormat fileNameFormat = new DefaultFileNameFormat().withPath(HDFS_PATH).withPrefix("csfb_")
                .withExtension(".csv");
        outputDataBolt.withFsUrl(HDFS_URL).withFileNameFormat(fileNameFormat).withRecordFormat(format)
                .withRotationPolicy(rotationPolicy).withSyncPolicy(syncPolicy);

        builder.setBolt(outputDataBoltName, outputDataBolt, boltParallelism).shuffleGrouping(extractSignalBoltName);

        return builder.createTopology();
    }

}
