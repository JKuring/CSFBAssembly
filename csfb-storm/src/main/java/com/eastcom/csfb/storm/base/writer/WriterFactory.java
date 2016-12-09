package com.eastcom.csfb.storm.base.writer;

import java.io.IOException;

/**
 * Created by linghang.kong on 2016/5/18.
 */
public class WriterFactory {
    private static final String Local = "local";
    private static final String HDFS = "hdfs";
    private static final String REDIS = "redis";

    public static Writable selectTarget(String targetNmae) throws IOException {
        Writable writer = null;
        if (targetNmae == Local) {
            writer = new LocalFileWriter();
        } else if (targetNmae == HDFS) {
            writer = new HdfsWriter();
        } else if (targetNmae == REDIS) {
            writer = new ReidsWriter();
        }
        return writer;
    }
}
