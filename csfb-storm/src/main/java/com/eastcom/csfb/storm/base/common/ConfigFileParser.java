package com.eastcom.csfb.storm.base.common;

import com.eastcom.csfb.storm.base.common.LineByLineParser.LineParser;
import com.eastcom.csfb.storm.base.common.LineByLineParser.LineSplitParser;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static com.eastcom.csfb.storm.base.common.LineByLineParser.toReader;

public class ConfigFileParser {

    private static Logger logger = LoggerFactory.getLogger(ConfigFileParser.class);

    public static Map<Integer, Table<Integer, Integer, Boolean>> parseIdrsLevel(String str) throws IOException {

        final Map<Integer, Table<Integer, Integer, Boolean>> levelMapper = new HashMap<Integer, Table<Integer, Integer, Boolean>>();
        LineByLineParser.splitParse(toReader(str), 2, new LineSplitParser() {
            @Override
            public void parse(String[] arr) {
                String[] ratIdr = arr[1].split(":");
                if (ratIdr.length != 2) {
                    logger.info("字段长度不一致.");
                    return;
                }
                int level = NumberUtils.toInt(arr[0]);
                int rat = NumberUtils.toInt(ratIdr[0]);
                int idr = NumberUtils.toInt(ratIdr[1]);
                Table<Integer, Integer, Boolean> table = levelMapper.get(level);
                if (table == null) {
                    table = HashBasedTable.create();
                    levelMapper.put(level, table);
                }
                table.put(rat, idr, true);
            }
        });
        return levelMapper;
    }

    /**
     * 从json中解析获取IP地址集合
     *
     * @param json
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Set<String> parseIpMapper(String json) {
        Map<String, Object> jsonMap = new Gson().fromJson(json, Map.class);
        Map<String, List<String>> mmeNameIpsMapper = (Map<String, List<String>>) jsonMap.get("nameIps");
        Set<String> mmeIps = new HashSet<>();
        for (String key : mmeNameIpsMapper.keySet()) {
            List<String> ips = mmeNameIpsMapper.get(key);
            mmeIps.addAll(ips);
        }
        return mmeIps;
    }

    public static Set<String> parseAppServerIps(String str) throws IOException {
        return simpleLines(str);
    }

    public static Set<String> parseVipImsis(String str) throws IOException {
        return simpleLines(str);
    }

    public static Set<String> parseHosts(String str) throws IOException {
        return simpleLines(str);
    }

    public static Set<String> parseVipRecordXdrs(String str) throws IOException {
        return simpleLines(str);
    }

    public static Set<String> parseSgwIps(String str) throws IOException {
        return simpleLines(str);
    }

    public static Map<String, String> parsePtnIpNames(String str) throws IOException {
        final Map<String, String> result = new HashMap<>();
        LineByLineParser.splitParse(toReader(str), "\\s*,\\s*", 2, new LineSplitParser() {

            @Override
            public void parse(String[] fields) {
                String ip = fields[0];
                String name = fields[1];
                result.put(ip, name);
            }
        });
        return result;
    }

    public static Set<String> parseImeiTacs(String str) throws IOException {
        return simpleLines(str);
    }

    public static Table<Integer, Integer, Set<String>> parseAppIps(String str) throws IOException {
        final Table<Integer, Integer, Set<String>> appIps = HashBasedTable.create();
        if (StringUtils.isBlank(str)) {
            return appIps;
        }
        LineByLineParser.splitParse(toReader(str), 2, new LineSplitParser() {

            @Override
            public void parse(String[] fields) {
                String app = fields[0];
                String[] arr = app.split(":");
                if (arr.length < 2) {
                    logger.error("应用小类格式错误:{}.", app);
                    return;
                }
                int majorId = NumberUtils.toInt(arr[0]);
                int minorId = NumberUtils.toInt(arr[1]);
                String ip = fields[1];
                Set<String> ips = appIps.get(majorId, minorId);
                if (ips == null) {
                    ips = new HashSet<>();
                    appIps.put(majorId, minorId, ips);
                }
                ips.add(ip);
            }
        });
        return appIps;
    }

    /**
     * 从str中解析出ips返回
     *
     * @param str
     * @return
     * @throws IOException
     */
    public static Set<String> simpleLines(String str) throws IOException {
        final Set<String> ips = new HashSet<>();
        LineByLineParser.parse(toReader(str), new LineParser() {
            @Override
            public void parse(String line) {
                ips.add(line);
            }
        });
        return ips;
    }

}
