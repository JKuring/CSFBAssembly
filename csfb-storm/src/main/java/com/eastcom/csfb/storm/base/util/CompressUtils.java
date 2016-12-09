package com.eastcom.csfb.storm.base.util;

import com.google.common.base.Charsets;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.io.IOUtils;
import redis.clients.jedis.Jedis;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class CompressUtils {
    /**
     * 解压缩
     *
     * @param bs
     * @return
     * @throws CompressorException
     * @throws IOException
     */
    public static byte[] decompress(byte[] bs) throws CompressorException, IOException {
        CompressorInputStream xzInputStream = new CompressorStreamFactory()
                .createCompressorInputStream(CompressorStreamFactory.XZ, new ByteArrayInputStream(bs));
        byte[] data = IOUtils.toByteArray(xzInputStream);
        xzInputStream.close();
        return data;
    }

    /**
     * 解压缩为字符串，jedis中key对应的值进行解压缩转换为字符串(byte[] bytesData->string)
     *
     * @param jedis
     * @param key
     * @return
     * @throws CompressorException
     * @throws IOException
     */
    public static String decompressAsString(Jedis jedis, String key) throws CompressorException, IOException {
        byte[] bytesData = jedis.get(key.getBytes(Charsets.UTF_8));
        if (bytesData == null) {
            return null;
        }
        CompressorInputStream xzInputStream = new CompressorStreamFactory()
                .createCompressorInputStream(CompressorStreamFactory.XZ, new ByteArrayInputStream(bytesData));
        return IOUtils.toString(xzInputStream, Charsets.UTF_8);
    }

}
