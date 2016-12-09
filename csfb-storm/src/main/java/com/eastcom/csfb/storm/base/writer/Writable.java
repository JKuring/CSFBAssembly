package com.eastcom.csfb.storm.base.writer;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by linghang.kong on 2016/5/18.
 */
public interface Writable<T> {

    public void write(T data) throws IOException, URISyntaxException;

    public void close() throws IOException;
}
