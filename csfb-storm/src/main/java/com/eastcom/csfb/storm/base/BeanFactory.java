package com.eastcom.csfb.storm.base;

import com.eastcom.csfb.storm.base.pool.RoundRobinJedisPool;
import com.eastcom.csfb.storm.base.pool.RoundRobinJodisPool;
import com.eastcom.csfb.storm.kafka.ConfigKey;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class BeanFactory {

    public static final String KAFKA_PREFIX = "kafka.";
    public static final String JEDIS_PREFIX = "jedis.";
    public static final String PUBSUB_JEDIS_PREFIX = "pubsub.jedis.";
    private static DataCache dataCache;
    private static Pool<Jedis> jedisPool;
    private static TopicCSVParsers topicCsvParser;
    private static Object LOCKER = "LOCKER";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Map<String, Object> stormConfig;

    public BeanFactory(Map<String, Object> config) {
        super();
        this.stormConfig = config;
    }

    /**
     * 把Map<key,value>stormConfig中key带前缀prefix的的取出赋值给Properties pop对象
     *
     * @param prefix
     * @return
     */
    private Properties propsPrefix(final String prefix) {
        final Properties prop = new Properties();
        for (Map.Entry<String, Object> entry : stormConfig.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                prop.setProperty(entry.getKey().substring(prefix.length()), String.valueOf(entry.getValue()));
            }
        }
        return prop;
    }

    /**
     * 把Map<key,value>stormConfig中key带前缀prefix的的取出赋值给Map<String, Object> prop对象
     *
     * @param prefix
     * @return
     */
    private Map<String, Object> mapPrefix(final String prefix) {
        final Map<String, Object> prop = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : stormConfig.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                prop.put(entry.getKey().substring(prefix.length()), entry.getValue());
            }
        }
        return prop;
    }

    /**
     * 取出kafa配置信息
     *
     * @return
     */
    public Properties getKafkaConsumerConfig() {
        return propsPrefix(KAFKA_PREFIX);
    }

    /**
     * 取出PUBSUB_JEDIS_PREFIX配置
     *
     * @return
     */
    public Properties getPubSubJedisConfig() {
        return propsPrefix(PUBSUB_JEDIS_PREFIX);
    }

    /**
     * 根据配置信息，同步建立Redis连接池
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public Pool<Jedis> getJedisPool() {
        synchronized (LOCKER) {
            if (jedisPool != null) {
                return jedisPool;
            }
            Map<String, Object> props = mapPrefix(JEDIS_PREFIX);
            boolean poolStatus = (props.get("pool.zookeeper.address") != null);
            logger.warn("codis pool status: {}.", poolStatus);
            if (props.get("pool.zookeeper.address") == null) {
                Number minIdle = (Number) props.get("minIdle");
                Number maxTotal = (Number) props.get("maxTotal");
                List<String> addresses = (List<String>) props.get("addresses");
                String password = (String) props.get("password");
                try {
                    jedisPool = new RoundRobinJedisPool(addresses, password, minIdle.intValue(), maxTotal.intValue());
                } catch (Exception e) {
                    logger.error("Failed to create redis pool, exception: {}.", e.getMessage());
                }
            } else if (props.get("pool.zookeeper.address") != null) {
                String zkConnect = MapUtils.getString(props, "pool.zookeeper.address");
                int zkTimeout = MapUtils.getIntValue(props, "pool.zookeeper.timeout", 30_000);
                String clusterName = MapUtils.getString(props, "pool.cluster");
                String password = MapUtils.getString(props, "pool.password");
                int maxTotal = MapUtils.getInteger(props, "pool.maxtotal", 100);
                try {
                    jedisPool = new RoundRobinJodisPool(zkConnect, zkTimeout, clusterName, password, maxTotal);
                } catch (Exception e) {
                    logger.error("Failed to create redis pool, exception: {}.", e.getMessage());
                }
            }
            return jedisPool;
        }
    }

    public DataCache getDataCache() {
        synchronized (LOCKER) {
            if (dataCache != null) {
                return dataCache;
            }
            dataCache = new DataCache(getJedisPool());
            return dataCache;
        }
    }

    public TopicCSVParsers getTopicCsvParser() {
        synchronized (BeanFactory.class) {
            try {
                if (topicCsvParser != null) {
                    return topicCsvParser;
                }
                String clazzName = MapUtils.getString(stormConfig, ConfigKey.CSVPARSER_SELECTOR_CLASS,
                        "com.eastcom.csfb.storm.base.TopicCSVParsers");
                Class<?> clazz = Class.forName(clazzName);
                topicCsvParser = (TopicCSVParsers) clazz.newInstance();
                return topicCsvParser;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
