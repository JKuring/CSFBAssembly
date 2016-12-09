package com.eastcom.csfb.storm.base.reader;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.net.URI;
import java.util.Map;

public class XdrReader implements Readable {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Readable hdfsXdrReader = new HdfsReader();

    private Readable ftpXdrReader;

    @SuppressWarnings("rawtypes")
    public XdrReader(Map conf) {
        boolean enablePool = MapUtils.getBoolean(conf, "ftp.pool.enable", false);
        if (enablePool) {
            // 从map conf中取出key="ftp.pool.maxTotal"的值，如果值为null，则返回默认值100
            Integer maxTotal = MapUtils.getInteger(conf, "ftp.pool.maxTotal", 100);
            Integer maxIdle = MapUtils.getInteger(conf, "ftp.pool.maxIdle", 10);
            ftpXdrReader = new PooledFtpReader(maxTotal, maxIdle);
        } else {
            ftpXdrReader = new FtpReader();
        }
    }

    /**
     * 根据fileuri的scheme来读不通类型文件
     */
    @Override
    public BufferedReader read(String fileUri) throws Exception {
        URI uri = URI.create(fileUri);
        String scheme = uri.getScheme();
        if (StringUtils.equalsIgnoreCase(scheme, "ftp")) {
            return ftpXdrReader.read(fileUri);
        } else if (StringUtils.equalsIgnoreCase(scheme, "hdfs")) {
            return hdfsXdrReader.read(fileUri);
        } else {
            logger.error("Unknow scheme {} for {}.", scheme, fileUri);
            return hdfsXdrReader.read(fileUri);
        }
    }

}
