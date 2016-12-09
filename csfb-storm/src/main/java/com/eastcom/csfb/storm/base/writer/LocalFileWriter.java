package com.eastcom.csfb.storm.base.writer;

import com.eastcom.csfb.storm.base.bean.Csfb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created by linghang.kong on 2016/5/18.
 */
public class LocalFileWriter implements Writable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private File file = new File("/tmp/csfb.txt");
    private OutputStream outputStream;

    public LocalFileWriter() throws IOException {
        this.outputStream = new FileOutputStream(this.file);
        try {
            if (this.file.exists()) {
                this.file.delete();
            }
            this.file.createNewFile();
            this.outputStream = new FileOutputStream(this.file);
        } catch (FileNotFoundException e) {
            logger.error(e.getStackTrace().toString());
            throw e;
        } catch (IOException e) {
            logger.error(e.getStackTrace().toString());
            throw e;
        }
    }


    @Override
    public void write(Object data) throws IOException {
        this.outputStream.write(((Csfb) data).toString().getBytes());
        this.outputStream.write("\n".getBytes());
    }

    @Override
    public void close() throws IOException {
        this.outputStream.flush();
        this.outputStream.close();
    }
}
