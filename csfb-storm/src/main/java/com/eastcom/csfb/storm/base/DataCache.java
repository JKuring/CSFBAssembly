package com.eastcom.csfb.storm.base;

import com.eastcom.csfb.data.DbKeys;
import com.eastcom.csfb.storm.base.bean.SiteInfo;
import com.eastcom.csfb.storm.base.pubsub.PubSubSubscriber;
import com.eastcom.csfb.storm.base.util.DateUtils;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import com.google.gson.Gson;
import org.apache.commons.compress.compressors.CompressorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.eastcom.csfb.data.DbKeys.SITE_INFO;
import static com.eastcom.csfb.storm.base.pubsub.Channels.DEFAULT_CHANNEL;
import static com.eastcom.csfb.storm.base.pubsub.MessageTypes.UPDATE_CACHE;
import static com.eastcom.csfb.storm.base.util.CompressUtils.decompressAsString;
import static com.eastcom.csfb.storm.base.util.StringUtils.md5Key;
import static com.eastcom.csfb.storm.base.util.StringUtils.strEquals;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class DataCache extends Thread implements PubSubSubscriber {

    private final Map<String, String> updateStatus = new HashMap<>();
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Pool<Jedis> jedisPool;
    private volatile Table<Integer, Integer, SiteInfo> siteInfos;

    public DataCache(Pool<Jedis> jedisPool) {
        this.jedisPool = jedisPool;
        this.update();
    }

    /**
     * 从siteInfos中通过（lac，cellId）获取SiteInfo信息
     *
     * @param lac
     * @param cellId
     * @return
     */
    public SiteInfo getSiteInfo(int lac, int cellId) {
        SiteInfo o = siteInfos.get(lac, cellId);
        if (o != null) {
            return o;
        } else {
            return SiteInfo.emptySiteInfo;
        }
    }

    /**
     * 格式化网络类型，如果是2g,3g,4g 则返回其具体的编号，如果不是则返回-1
     *
     * @param rat 网络类型编号ID
     * @return
     */
    public int formatRat(int rat) {
        if (rat == 1 || rat == 2 || rat == 6) {
            return rat;
        }
        return -1;
    }

    /**
     * 判断网络类型是否存在
     *
     * @param rat 网络类型编号ID
     * @return
     */
    public boolean ratExists(int rat) {
        if (rat == 1 || rat == 2 || rat == 6) {
            return true;
        }
        return false;
    }

    private void update() {
        while (true) {
            logger.info("更新缓存数据开始.");
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();

                doUpdate(jedis);

                jedisPool.returnResource(jedis);
                logger.info("更新缓存数据结束.");
                break;
            } catch (Exception e) {
                jedisPool.returnBrokenResource(jedis);
                logger.error("更新缓存数据异常.", e);
                DateUtils.sleep(100);
            }
        }
    }

    private void doUpdate(Jedis jedis) throws Exception {
        updateSiteInfos(jedis);
    }

    private void updateSiteInfos(Jedis jedis) throws CompressorException, IOException {
        String md5 = jedis.get(md5Key(SITE_INFO));
        String json = getCompressDataAsString(jedis, SITE_INFO, md5);
        if (isNotEmpty(json)) {
            SiteInfo[] siteInfoList = new Gson().fromJson(json, SiteInfo[].class);
            HashBasedTable<Integer, Integer, SiteInfo> tmpSiteInfos = HashBasedTable.create();
            for (SiteInfo siteInfo : siteInfoList) {
                if (isEmpty(siteInfo.getSd())) {
                    siteInfo.setSd(DbKeys.unknown);
                }
                if (siteInfo.getHotspots() != null && siteInfo.getHotspots().isEmpty()) {
                    siteInfo.setHotspots(null);
                }
                tmpSiteInfos.put(siteInfo.getLac(), siteInfo.getCellId(), siteInfo);
            }
            this.siteInfos = ImmutableTable.copyOf(tmpSiteInfos);
            setUptime(SITE_INFO, md5);
            logger.info("siteInfo已更新,共加载: {}.", tmpSiteInfos.size());
        }
    }

    private boolean isChanged(String key, String md5) {
        if (strEquals(updateStatus.get(key), md5)) {
            logger.info("{}数据未变化.", key);
            return false;
        }
        return true;
    }

    /**
     * key->md5 存入map<key,md5> updateStatus 中
     *
     * @param key
     * @param md5
     */
    private void setUptime(String key, String md5) {
        updateStatus.put(key, md5);
    }

    /**
     * 判断str数据是否为空
     *
     * @param key
     * @param str
     * @return
     */
    private boolean isDataEmpty(String key, String str) {
        if (isEmpty(str)) {
            logger.info("{}数据为空.", key);
            return true;
        }
        return false;
    }

    /**
     * 从redis中通过key获取数据转换为string
     *
     * @param jedis
     * @param key
     * @param uptime
     * @return
     * @throws CompressorException
     * @throws IOException
     */
    private String getCompressDataAsString(Jedis jedis, String key, String uptime)
            throws CompressorException, IOException {
        if (!isChanged(key, uptime)) {
            return null;
        }
        String data = decompressAsString(jedis, key);
        if (isDataEmpty(key, data)) {
            return null;
        }
        return data;
    }

    @Override
    public void onPubSubMessage(String channel, String message) {
        if (DEFAULT_CHANNEL.equals(channel) && UPDATE_CACHE.equals(message)) {
            logger.info("收到缓存更新请求,开始更新缓存...");
            update();
            logger.info("完成缓存更新.");
        }
    }

}
