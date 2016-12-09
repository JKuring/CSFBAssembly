package com.eastcom.csfb.storm.base.util;

import com.google.common.collect.HashBasedTable;
import org.apache.commons.lang3.mutable.MutableLong;

import java.util.List;
import java.util.Map;

public class CollectionUtils {
    /**
     * 把value与table中的值（指标值）相加，用于次数的累加
     *
     * @param map       <String(表示指标对象的key), String(指标), MutableLong(指标值)> map
     * @param rowKey    redis中的key
     * @param columnKey redis中的指标值key (请求次数key or 成功次数key)
     * @param value
     */
    public static void incrTableValue(HashBasedTable<String, String, MutableLong> map, String rowKey, String columnKey,
                                      long value) {
        MutableLong v = map.get(rowKey, columnKey);
        if (v == null) {
            map.put(rowKey, columnKey, new MutableLong(value));
        } else {
            v.add(value);
        }
    }

    /**
     * 获得list中的第一个元素
     *
     * @param list
     * @return
     */
    public static <T> T first(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @SuppressWarnings("unchecked")
    public static <TK, TV, TVS> TV getOrCreate(Map<TK, TV> map, TK key, Class<TVS> type) {
        try {
            TV val = map.get(key);
            if (val == null) {
                val = (TV) type.newInstance();
                map.put(key, val);
            }
            return val;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
