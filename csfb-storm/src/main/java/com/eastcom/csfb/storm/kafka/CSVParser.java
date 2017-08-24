package com.eastcom.csfb.storm.kafka;

import com.eastcom.csfb.data.DbKeys;
import com.eastcom.csfb.data.UserCommon;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 主要是把csv格式数据解析为T对象。
 * <p>
 * 抽象方法，交由继承的子类实现
 *
 * @param <T>
 */
@Deprecated
public abstract class CSVParser<T> {

    public static final char separator = '|';
    public final int ratVlt = 601; // volte
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    final Splitter splitter = Splitter.on(separator).trimResults();

    /**
     * 抽取lac
     *
     * @param eci
     * @return
     */
    public static int extractLac(int eci) {
        return eci / 256;
    }

    /**
     * 抽取eci
     *
     * @param eci
     * @return
     */
    public static int extractCellId(int eci) {
        return eci % 256;
    }

    /**
     * 抽取imei tac(终端型号)
     *
     * @param imei
     * @return
     */
    public static String extractImeiTac(String imei) {
        if (imei == null || imei.length() < 8) {
            return DbKeys.unknown;
        }
        return imei.substring(0, 8);
    }

    /**
     * 抽取IMSI
     *
     * @param imsi
     * @return
     */
    public static String extractImsi(String imsi) {
        if (StringUtils.isEmpty(imsi)) {
            return "0";
        }
        return imsi;
    }

    public static int extractImsOperationType(String operation) {
        if (StringUtils.isEmpty(operation)) {
            return 0;
        }
        int startIndex = 0;
        int endIndex = 0;
        while (endIndex < operation.length()) {
            endIndex = operation.indexOf(";", startIndex);
            if (endIndex == 0) {
                startIndex = endIndex + 1;
                continue;
            }
            if (endIndex < 0) {
                endIndex = operation.length();
            }
            int index = operation.indexOf("/0", startIndex);
            if (index < 0 || index > endIndex) {
                int colse = operation.indexOf("/", startIndex);
                if (colse > 0) {
                    return NumberUtils.toInt(operation.substring(startIndex, colse), 0);
                } else {
                    return 0;
                }
            }
            startIndex = endIndex + 1;
        }
        return 0;
    }

    /**
     * 把byte[] data-->String csvData-->T对象
     *
     * @param data
     * @return
     */
    public T parse(byte[] data) {
        return parse(data, null);
    }

    /**
     * 把byte[] data-->String csvData-->T对象
     *
     * @param data
     * @param uri
     * @return
     */
    public T parse(byte[] data, String uri) {
        return parse(new String(data, Charsets.UTF_8), uri);
    }

    /**
     * String csvData-->T对象
     *
     * @param data
     * @return
     */
    public T parse(String data) {
        return parse(data, null);
    }

    /**
     * 把hdfs中一行记录（string csv）data数据解析为T对象。
     *
     * @param data hdfs中一行记录
     * @param uri  hdfs文件地址
     * @return
     */
    public T parse(String data, String uri) {
        List<String> csvArr = splitter.splitToList(data); // 把csv格式字符串解析为list数组。
        // 字段长度非法
        // if (csvArr.size() != getTotalFields()) {
        // logger.warn("illegal filed count, require {}, actually {}, data [{}],
        // file [{}].", getColumns(),
        // csvArr.size(), data, uri);
        // return null;
        // }
        // 如果解析出来的List<String> csvArr 少于所需要的字段数，则返回空。
        if (csvArr.size() < getRequiredFields()) {
            logger.error("illegal filed num, require {}, actually {}, data [{}],file [{}].", getRequiredFields(),
                    csvArr.size(), data, uri);
            return null;
        }
        T t = doParse(csvArr);// 把csv格式数据解析为T对象。抽象方法，交由继承的子类实现。
        if (validate(t, uri)) {
            return t; // 返回T对象
        } else {
            return null;
        }
    }

    public abstract int getRequiredFields();

    public abstract int getTotalFields();

    public abstract T doParse(List<String> csvArr);

    /**
     * 验证是否为有效数据
     * <p>
     * t对象中属性值是否为有效数据
     *
     * @param t   一行记录解析出来的数据对象
     * @param uri
     * @return
     */
    public boolean validate(T t, String uri) {
        if (t instanceof UserCommon) {
            UserCommon d = (UserCommon) t;
            // if (d.getStartTime() <= 0 || System.currentTimeMillis() -
            // d.getStartTime() > 43200_000) {// 12小时
            // // logger.error("illegal startTime [{}],file [{}].", t, uri);
            // return false;
            // }
            // 超过12小时为无效数据
            if (d.getEndTime() <= 0 || System.currentTimeMillis() - d.getEndTime() > 43200_000) {// 12小时
                // logger.error("illegal endTime [{}],file [{}].", t, uri);
                return false;
            }
        }
        if (t instanceof UserDataCommon) {
            UserDataCommon d = (UserDataCommon) t;
            if (d.getEndTime() - d.getStartTime() == 0) {
                if (d.getDlBytes() > 100_000_000 || d.getUlBytes() > 100_000_000) {
                    logger.warn("illegal bytes rate [{}], file [{}].", t, uri);
                    return false;
                } else {
                    return true;
                }
            }
            if (d.getDlBytes() / (d.getEndTime() - d.getStartTime()) > 10_00000
                    || d.getUlBytes() / (d.getEndTime() - d.getStartTime()) > 10_00000) {
                logger.warn("illegal bytes rate [{}], file [{}].", t, uri);// TD-LTE理论下行速度为100Mbps
                return false;
            }
        }
        return true;
    }

    /**
     * 把空字符串返回为unknown，非空字符串按原样返回
     *
     * @param s
     * @return
     */
    public String extractString(String s) {
        if (StringUtils.isEmpty(s)) {
            return DbKeys.unknown;
        }
        return s;
    }

    public String extractLowerString(String s) {
        if (StringUtils.isEmpty(s)) {
            return DbKeys.unknown;
        }
        return s.toLowerCase();
    }

    /**
     * 抽取时间
     *
     * @param str 时间格式字符串
     * @return date类型时间
     */
    public long extractTime(String str) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date date = null;
        try {
            date = sdf.parse(str);
        } catch (ParseException e) {
            return 0;
        }
        return date.getTime();
    }

    public long extractMroTime(String str) {
        try {
            return DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS").parseDateTime(str).getMillis();
        } catch (Exception e) {
            logger.error("", e);
            return 0;
        }
    }

}
