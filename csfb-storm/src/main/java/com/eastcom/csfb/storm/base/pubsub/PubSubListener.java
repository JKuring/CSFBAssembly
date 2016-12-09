package com.eastcom.csfb.storm.base.pubsub;

import com.eastcom.csfb.storm.base.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import static com.eastcom.csfb.storm.base.pubsub.Channels.ALL_STORM_CHANNEL;

public class PubSubListener extends JedisPubSub implements Runnable {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private String redisHost;

    private int redisPort;

    private String redisPassword;

    private Set<PubSubSubscriber> pubSubSubscribers = new HashSet<>();

    /**
     * 构造方法初始化redis登录参数
     *
     * @param properties
     */
    private PubSubListener(Properties properties) {
        String address = properties.getProperty("address");
        String password = properties.getProperty("password");
        init(address, password);
    }

    /**
     * 构造方法初始化redis登录参数
     *
     * @param address
     * @param password
     */
    private PubSubListener(String address, String password) {
        init(address, password);
    }

    public static PubSubListener register(Properties properties, PubSubSubscriber... pubSubSubscribers) {
        PubSubListener pubSubListener = new PubSubListener(properties);
        pubSubListener.pubSubSubscribers.addAll(Arrays.asList(pubSubSubscribers));
        new Thread(pubSubListener, "pubsub-listener").start();
        return pubSubListener;
    }

    public static PubSubListener register(String address, String password, PubSubSubscriber... pubSubSubscribers) {
        PubSubListener pubSubListener = new PubSubListener(address, password);
        pubSubListener.pubSubSubscribers.addAll(Arrays.asList(pubSubSubscribers));
        new Thread(pubSubListener, "pubsub-listener").start();
        return pubSubListener;
    }

    /**
     * 增加订阅者
     *
     * @param pubSubSubscriber
     */
    public void addSubscriber(PubSubSubscriber pubSubSubscriber) {
        this.pubSubSubscribers.add(pubSubSubscriber);
    }

    @Override
    public void run() {
        while (true) {
            try {
                psubscribe();
            } catch (Exception ex) {
                logger.error("{}", ex.getMessage());
                DateUtils.sleep(1000);
            }
        }
    }

    @Override
    public void onMessage(String channel, String message) {
        logger.info("Receive PubSub message, channel:{}, message: {}.", channel, message);
        dispatcher(channel, message);
    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        logger.info("Receive PubSub message, pattern: {}, channel:{}, message: {}.", pattern, channel, message);
        dispatcher(channel, message);
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        logger.info("PubSub: onSubscribe, channel: {}, subscribedChannels: {}.", channel, subscribedChannels);
    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        logger.info("PubSub: onUnsubscribe, channel: {}, subscribedChannels: {}.", channel, subscribedChannels);
    }

    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {
        logger.info("PubSub: onPUnsubscribe, pattern: {}, subscribedChannels: {}.", pattern, subscribedChannels);
    }

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
        logger.info("PubSub: onPSubscribe, pattern: {}, subscribedChannels: {}.", pattern, subscribedChannels);
    }

    /**
     * 所有订阅者订阅频道channel的消息message
     *
     * @param channel
     * @param message
     */
    private void dispatcher(String channel, String message) {
        if (pubSubSubscribers != null) {
            for (PubSubSubscriber sub : pubSubSubscribers) {
                sub.onPubSubMessage(channel, message);
            }
        } else {
            logger.info("无消息订阅者.");
        }
    }

    /**
     * 初始化redis登录参数
     *
     * @param address
     * @param password
     */
    private void init(String address, String password) {
        int index = address.indexOf(':');
        String host = address.substring(0, index);
        int port = Integer.parseInt(address.substring(index + 1));
        this.redisHost = host;
        this.redisPort = port;
        this.redisPassword = password;
    }

    /**
     * 订阅ALL_STORM_CHANNEL匹配频道的消息
     */
    private void psubscribe() {
        Jedis jedis = new Jedis(redisHost, redisPort, 120000);
        jedis.auth(redisPassword);
        jedis.psubscribe(this, ALL_STORM_CHANNEL);
        jedis.close();
    }

}
