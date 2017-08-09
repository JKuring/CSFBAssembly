package com.eastcom.csfb.storm.kafka;

import com.eastcom.csfb.data.CSVParser;
import com.eastcom.csfb.storm.base.util.DateUtils;
import com.google.common.base.Charsets;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopicConsumer<QV> extends Thread {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Consumer<byte[], byte[]> consumer;

    private ReadHook<QV> readHook;
    private ITopicCsvParser topicCsvParser;

    public TopicConsumer(String name, Consumer<byte[], byte[]> consumer, ReadHook<QV> readHook,
                         ITopicCsvParser topicCsvParser) {
        super(name);
        this.consumer = consumer;
        this.readHook = readHook;
        this.topicCsvParser = topicCsvParser;
    }

    @Override
    public void run() {
        logger.warn("Thread {} started.", this.getName());
        while (this.isInterrupted() == false) {
            try {
                fillBuffer();
            } catch (InterruptedException e) {
                this.interrupt();
            } catch (Throwable e) {
                logger.error("", e);
            }
            DateUtils.sleep(5);
        }
        logger.warn("线程{}中断退出。", this.getName());
    }

    private void fillBuffer() throws Exception {
        while (true) {
            ConsumerRecords<byte[], byte[]> records = consumer.poll(100);
            if (records == null) {
                continue;
            }
            for (ConsumerRecord<byte[], byte[]> record : records) {
                String topicName = record.topic();
                if (topicName.startsWith("r_")) {
                    topicName = topicName.substring(2);
                }
                String line = new String(record.value(), Charsets.UTF_8);
                CSVParser<?> csvParser = getCSVParser(topicName);
                Object csvObject = toObject(topicName, csvParser, line, null);
                if (csvObject != null) {
                    readHook.afterParse(csvObject);
                    readHook.putValues(readHook.getBufferQueue(), csvObject, topicName);
                    readHook.afterEmit(topicName, csvObject, line);
                }
            }
        }
    }

    protected CSVParser<?> getCSVParser(String topicName) {
        return topicCsvParser.getCSVParser(topicName);
    }

    protected Object toObject(String topic, CSVParser<?> csvParser, String line, String uri) {
        Object data = null;
        if (csvParser != null) {
            data = csvParser.parse(line, uri);
        } else {
            data = topicCsvParser.parse(topic, line, uri);
        }
        return data;
    }

}
