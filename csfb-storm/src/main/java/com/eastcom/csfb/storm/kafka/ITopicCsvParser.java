package com.eastcom.csfb.storm.kafka;


import com.eastcom.csfb.data.CSVParser;


public interface ITopicCsvParser {

    CSVParser<?> getCSVParser(String topic);

    Object parse(String topic, byte[] csvData, String uri);

    Object parse(String topic, String csvData, String uri);

}
