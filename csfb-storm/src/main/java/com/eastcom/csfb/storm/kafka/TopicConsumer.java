package com.eastcom.csfb.storm.kafka;

import com.eastcom.csfb.data.CSVParser;
import com.eastcom.csfb.storm.base.util.DateUtils;
import com.google.common.base.Charsets;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TopicConsumer<QV> extends Thread {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Consumer<byte[], byte[]> consumer;

    private ReadHook<QV> readHook;
    private ITopicCsvParser topicCsvParser;

    public TopicConsumer(String name, ReadHook<QV> readHook, ITopicCsvParser topicCsvParser) {
        super(name);
        this.readHook = readHook;
        this.topicCsvParser = topicCsvParser;
        this.resetConsumer();
    }

    private void resetConsumer() {
        logger.warn("Reset kafka consumer ...");
        if (this.consumer != null) {
            try {
                this.consumer.close();
            } catch (Exception e) {
                logger.error("", e);
            }
        }
        this.consumer = createConsumer();
    }

    protected abstract Consumer<byte[], byte[]> createConsumer();

    @Override
    public void run() {
        logger.warn("Thread {} started.", this.getName());
        while (this.isInterrupted() == false) {
            try {
                fillBuffer();
            } catch (InterruptedException e) {
                this.interrupt();
            } catch (Throwable e) {
                resetConsumer();
            }
            DateUtils.sleep(5);
        }
        this.consumer.close();
        logger.warn("Thread {} interrupted .", this.getName());
    }

    private void fillBuffer() throws Exception {
        while (true) {
            ConsumerRecords<byte[], byte[]> records = consumer.poll(100);
            if (records == null) {
                continue;
            }
            for (ConsumerRecord<byte[], byte[]> record : records) {
                String topicName = record.topic();
                String line = new String(record.value(), Charsets.UTF_8);
                RecordProcessor.processRecord(topicName, line, topicCsvParser, readHook);
            }
        }
    }

}
