package com.eastcom.csfb.storm.utils;

import org.apache.storm.Config;

import java.net.URL;

/**
 * Created by linghang.kong on 2017/8/4.
 *
 * @author linghang.kong
 */
public class ConfigCreater {

    /**
     * @param isJar if param is true, the configuration get form Jar root.
     * @return {@link Config}
     */
    public static Config getConfig(boolean isJar) {
        Config config = null;
        if (isJar) {
            config = new Config();
        } else {
            config = getConfigFromJar();
        }
        return config;
    }

    private static Config getConfigFromJar() {
        URL topologyConf = ConfigCreater.class.getResource("/csfb-topology.yaml");
        URL hdfsConf = ConfigCreater.class.getResource("/hdfs-conf.yaml");
        URL redisConf = ConfigCreater.class.getResource("/redisPool.yaml");
        URL serializerConf = ConfigCreater.class.getResource("/serializer.yaml");


        Config config = new Config();


        return null;
    }
}
