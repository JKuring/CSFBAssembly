//package com.eastcom.csfb.storm.topo;
//
//import com.eastcom.csfb.storm.kafka.ConfigKey;
//import org.apache.storm.Config;
//import org.apache.storm.generated.StormTopology;
//import org.apache.storm.topology.TopologyBuilder;
//import org.apache.storm.tuple.Fields;
//
//import java.util.List;
//
//import static org.apache.commons.collections4.MapUtils.getIntValue;
//
//public class CsfbTopology {
//    /**
//     * create a topology
//     *
//     * @param conf Storm configuration
//     * @return A topology object
//     */
//    @SuppressWarnings("unchecked")
//    public StormTopology getCsfbTopology(Config conf) {
//
////        List<String> topicNames = (List<String>) conf.get(ConfigKey.PROJECT_SPOUT_FILE_TOPICS);
//        int spoutParallelism = getIntValue(conf, ConfigKey.PROJECT_SPOUT_PARALLELISM);
//        int boltParallelism = Math.abs(getIntValue(conf, ConfigKey.PROJECT_BOLT_PARALLELISM));
//
//        String spoutName = "csfbSpout";
//        String extractSignalBoltName = "extractSignalBolt";
//        String outputDataBoltName = "outputDataBolt";
//
//        CsfbSpout csfbSpout = new CsfbSpout();
//        ExtractSignalBolt extractSignalBolt = new ExtractSignalBolt();
//        OutputDataBolt outputDataBolt = new OutputDataBolt();
//
//        TopologyBuilder builder = new TopologyBuilder();
//        builder.setSpout(spoutName, csfbSpout, spoutParallelism);
//        builder.setBolt(extractSignalBoltName, extractSignalBolt, boltParallelism).fieldsGrouping(spoutName,
//                new Fields("partition"));
//        builder.setBolt(outputDataBoltName, outputDataBolt, boltParallelism).shuffleGrouping(extractSignalBoltName);
//
//        return builder.createTopology();
//    }
//
//}
