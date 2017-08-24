package com.eastcom.csfb.storm.kafka;

public class RecordProcessor {

    public static <QV> void processRecord(String topicName, String line, ITopicCsvParser topicCsvParser,
                                          ReadHook<QV> readHook) throws Exception {
        if (topicName.startsWith("r_")) {
            topicName = topicName.substring(2);
        }
        Object csvObject = topicCsvParser.parse(topicName, line, null);
        if (csvObject != null) {
            readHook.afterParse(csvObject);
            readHook.putValues(readHook.getBufferQueue(), csvObject, topicName);
            readHook.afterEmit(topicName, csvObject, line);
        }
    }

}
