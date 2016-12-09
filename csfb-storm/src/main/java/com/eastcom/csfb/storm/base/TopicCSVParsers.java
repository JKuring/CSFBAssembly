package com.eastcom.csfb.storm.base;

import com.eastcom.csfb.data.CSVParser;
import com.eastcom.csfb.data.UserCommon;
import com.eastcom.csfb.data.ltesignal.LteS1Mme;
import com.eastcom.csfb.data.ltesignal.LteSGs;
import com.eastcom.csfb.data.ltesignal.parser.LteS1MmeCSVParser;
import com.eastcom.csfb.data.ltesignal.parser.LteSgsCSVParser;
import com.eastcom.csfb.data.mc.McCallEvent;
import com.eastcom.csfb.data.mc.McLocationUpdate;
import com.eastcom.csfb.data.mc.McPaging;
import com.eastcom.csfb.data.mc.parser.McCallParser;
import com.eastcom.csfb.data.mc.parser.McLocationUpdateParser;
import com.eastcom.csfb.data.mc.parser.McPagingParser;

/**
 * 根据不同topic 解析创建不同对象
 */
public class TopicCSVParsers {

    public final static CSVParser<LteS1Mme> lte_s1_mme = new LteS1MmeCSVParser();
    public final static CSVParser<LteSGs> lte_sgs = new LteSgsCSVParser();

    public final static CSVParser<McCallEvent> mc_call = new McCallParser();
    public final static CSVParser<McPaging> mc_paging = new McPagingParser();
    public final static CSVParser<McLocationUpdate> mc_location = new McLocationUpdateParser();

    /**
     * 根据topic产生不同的csv解析器CSVParser<？>。其中有doParse方法
     *
     * @param topic
     * @return
     */
    public static CSVParser<? extends UserCommon> getCSVParser(String topic) {

        if ("lte_s1_mme".equals(topic)) {
            return lte_s1_mme;
        }
        if ("lte_sgs".equals(topic)) {
            return lte_sgs;
        }
        if ("mc_call".equals(topic)) {
            return mc_call;
        }
        if ("mc_paging".equals(topic)) {
            return mc_paging;
        }
        if ("mc_location".equals(topic)) {
            return mc_location;
        }
        throw new IllegalArgumentException(topic);

    }

    /**
     * 把字节流数据解析为对象
     * <p>
     * 数据转换变化流程 byte[] csvData->String csvData->List<String> csvArr-> T
     * <p>
     * 根据不同的topic创建不同的对象，然后解析byte[] csvData格式数据 赋值给T对象返回
     *
     * @param topic
     * @param csvData
     * @param uri
     * @return
     */
    public static UserCommon parse(String topic, byte[] csvData, String uri) {
        return getCSVParser(topic).parse(csvData, uri);
    }

    /**
     * 把csv字符串数据解析为对象
     * <p>
     * 数据转换变化流程 String csvData->List<String> csvArr-> T
     * <p>
     * 根据不同的topic创建不同的对象，然后解析String csvData格式数据 赋值给T对象返回
     *
     * @param topic
     * @param csvData
     * @param uri
     * @return
     */
    public static UserCommon parse(String topic, String csvData, String uri) {
        return getCSVParser(topic).parse(csvData, uri);
    }

}
