package com.eastcom.csfb.storm.base.reader;

import com.google.common.base.Charsets;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

/**
 * 支持hdfs
 * <p>
 * ftp短连接
 *
 * @author louyj
 */
public class HdfsReader implements Readable {

    protected Configuration hdfsConf;

    public HdfsReader() {
        this.hdfsConf = new Configuration();
        hdfsConf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        hdfsConf.set("fs.ftp.impl", org.apache.hadoop.fs.ftp.FTPFileSystem.class.getName());
        hdfsConf.set("fs.ftp.impl.disable.cache", "true");
    }

    /**
     *
     */
    @Override
    public BufferedReader read(String fileUri) throws Exception {
        // hdfs 文件系统
        FileSystem fs = FileSystem.get(URI.create(fileUri), hdfsConf);
        // 根据fileUri获取文件输入流
        InputStream input = fs.open(new Path(fileUri));
        BufferedReader reader = new BufferedReader(new InputStreamReader(input, Charsets.UTF_8));
        return reader; // 文件缓存输入流
    }

}
