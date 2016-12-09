package com.eastcom.csfb.storm.base.util;

import com.eastcom.csfb.data.DbKeys;
import com.google.common.base.Splitter;

import java.util.List;

public class StringUtils {

    public static final String Separator = ":";

    private static Splitter colonSplitter = Splitter.on(':');

    /**
     * 拼成字符串 aa:bb:cc
     */
    public static String toKey(Object... parts) {
        StringBuilder sb = new StringBuilder(75);
        sb.append(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            sb.append(Separator).append(parts[i]);
        }
        return sb.toString();
    }

    /**
     * 把aa:bb:cc 字符串 拆分成 list<String> {aa,bb,cc}
     *
     * @param key
     * @return
     */
    public static List<String> splitKey(String key) {
        return colonSplitter.splitToList(key);
    }

    /**
     * 拼成key:md5字符串返回
     *
     * @param key
     * @return
     */
    public static String md5Key(String key) {
        return toKey(key, DbKeys.md5);
    }

    /**
     * 拼成key:uptime字符串
     *
     * @param key
     * @return
     */
    public static String uptimeKey(String key) {
        return toKey(key, DbKeys.uptime);
    }

    /**
     * 比较字符串str1与str2是否相等
     *
     * @param str1
     * @param str2
     * @return
     */
    public static boolean strEquals(String str1, String str2) {
        return org.apache.commons.lang3.StringUtils.equals(str1, str2);
    }

}
