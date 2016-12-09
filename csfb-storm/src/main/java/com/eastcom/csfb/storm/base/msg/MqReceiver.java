package com.eastcom.csfb.storm.base.msg;

import com.eastcom.csfb.storm.base.util.DateUtils;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.io.Closeable;
import java.io.IOException;

public class MqReceiver implements Closeable, MsgReceiver {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ConnectionFactory connectionFactory;
    private Connection connection;
    private Session session;
    private Destination destination;
    private MessageConsumer consumer;

    private String queueName;

    public MqReceiver(String brokerURL, String queueName) {
        this.queueName = queueName;
        connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER,
                ActiveMQConnection.DEFAULT_PASSWORD, brokerURL);
        initConsumer();
    }

    @Override
    public synchronized String receive() throws Exception {
        try {
            ObjectMessage message = (ObjectMessage) consumer.receive();
            return (String) message.getObject();
        } catch (Exception e) {
            logger.error("", e);
            destoryConsumer();
            DateUtils.sleep(100);
            initConsumer();
            return null;
        }
    }

    public synchronized String receive(int timeout) throws Exception {
        try {
            ObjectMessage message = (ObjectMessage) consumer.receive(timeout);
            if (message == null) {
                return null;
            }
            return (String) message.getObject();
        } catch (Exception e) {
            logger.error("", e);
            destoryConsumer();
            DateUtils.sleep(100);
            initConsumer();
            return null;
        }
    }

    @Override
    public void close() throws IOException {
        destoryConsumer();
    }

    private void destoryConsumer() {
        try {
            logger.info("starting destory consumer...");
            consumer.close();
            session.close();
            connection.close();
            logger.info("finished destory consumer.");
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    private void initConsumer() {
        while (true) {
            try {
                logger.info("starting init consumer...");
                connection = connectionFactory.createConnection();
                connection.start();
                session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
                destination = session.createQueue(queueName);
                consumer = session.createConsumer(destination);
                logger.info("finished init consumer.");
                return;
            } catch (Exception e) {
                logger.error("", e);
                DateUtils.sleep(100);
            }
        }
    }

}
