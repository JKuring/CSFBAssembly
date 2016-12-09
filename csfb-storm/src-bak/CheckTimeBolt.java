package com.eastcom.csfb.storm.topo;

import com.eastcom.csfb.data.UserCommon;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CheckTimeBolt extends BaseRichBolt {

    private static final long serialVersionUID = 1L;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Map<String, Long> imsiTimes;

    private int counter = 0;

    @SuppressWarnings("rawtypes")
    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.imsiTimes = new HashMap<>();
    }

    @Override
    public void execute(Tuple input) {
        UserCommon uc = (UserCommon) input.getValue(0);
        String imsi = uc.getImsi();
        long startTime = uc.getStartTime();
        Long lastTime = imsiTimes.get(imsi);
        if (lastTime == null) {
            lastTime = 0L;
            logger.info(imsi);
        }
        imsiTimes.put(imsi, startTime);
        if (startTime < lastTime) {
            logger.info("lastTime: {}, startTime: {}.", lastTime, startTime);
        }
        if (++counter % 1000000 == 0) {
            logger.info("receive data: {}.", counter);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }

}
