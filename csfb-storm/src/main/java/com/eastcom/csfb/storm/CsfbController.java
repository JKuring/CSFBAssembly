package com.eastcom.csfb.storm;


import com.eastcom.csfb.storm.topo.CsfbTopology;
import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;


/**
 * Created by linghang.kong on 2016/5/16.
 */
public class CsfbController {


    public static void main(String[] args) throws InvalidTopologyException, AuthorizationException, AlreadyAliveException {
        /**
         * 获取配置，建立集群实例，提交拓扑,关闭拓扑，关闭集群实例
         */
        Config config = new Config();
        CsfbTopology csfbTopology = new CsfbTopology();

        // 本地模式用于开发、测试，模拟一个完整的集群模式
        /*
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("CSFB", config, csfbTopology.getCsfbTopology(config));
        Utils.sleep(100);
        cluster.killTopology("CSFB");
        cluster.shutdown();
        */
        // 提交到集群
        StormSubmitter.submitTopology("CSFB", config, csfbTopology.getCsfbTopology(config));
    }
}
