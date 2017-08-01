package com.eastcom.csfb.storm.base;

import com.eastcom.csfb.storm.base.pool.RoundRobinJedisPool;
import com.eastcom.csfb.storm.kafka.ConfigKey;
import com.eastcom.csfb.storm.kafka.ITopicCsvParser;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class BeanFactory {

    public static final String KAFKA_PREFIX = "kafka.";

    public static final String JEDIS_PREFIX = "jedis.";

    public static final String PUBSUB_JEDIS_PREFIX = "pubsub.jedis.";
    private static DataCache dataCache;
    private static RoundRobinJedisPool jedisPool;
    private static TopicCSVParsers topicCsvParser;
    private static Object LOCKER = "LOCKER";
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
    public RoundRobinJedisPool getJedisPool() {
        synchronized (LOCKER) {
            if (jedisPool != null) {
                return jedisPool;
            }
            Map<String, Object> props = mapPrefix(JEDIS_PREFIX);
            Number minIdle = (Number) props.get("minIdle");
            Number maxTotal = (Number) props.get("maxTotal");
            List<String> addresses = (List<String>) props.get("addresses");
            String password = (String) props.get("password");
            jedisPool = new RoundRobinJedisPool(addresses, password, minIdle.intValue(), maxTotal.intValue());
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
