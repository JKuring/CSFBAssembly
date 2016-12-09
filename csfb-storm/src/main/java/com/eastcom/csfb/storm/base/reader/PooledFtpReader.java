package com.eastcom.csfb.storm.base.reader;

import com.google.common.base.Charsets;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.KeyedObjectPool;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;

public class PooledFtpReader implements Readable, KeyedPooledObjectFactory<String, FTPClient>, Closeable {

    private static Logger logger = LoggerFactory.getLogger(PooledFtpReader.class);

    private GenericKeyedObjectPool<String, FTPClient> ftpClients;

    public PooledFtpReader(int maxTotal, int maxIdle) {
        GenericKeyedObjectPoolConfig config = new GenericKeyedObjectPoolConfig();
        config.setMaxTotalPerKey(maxTotal);
        config.setMaxIdlePerKey(maxIdle);
        config.setMaxWaitMillis(-1);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(false);
        config.setTestWhileIdle(false);
        config.setBlockWhenExhausted(true);
        ftpClients = new GenericKeyedObjectPool<>(this, config);
    }

    private static void close(FTPClient ftpClient) throws IOException {
        ftpClient.logout();
        ftpClient.disconnect();
    }

    public BufferedReader read(String fileUri) throws Exception {
        FTPClient ftpClient = null;
        String key = null;
        try {
            URI uri = URI.create(fileUri);
            key = uri.getAuthority();
            ftpClient = ftpClients.borrowObject(key);
            InputStream input = ftpClient.retrieveFileStream(uri.getPath());
            PooledFtpBufferedReader reader = new PooledFtpBufferedReader(new InputStreamReader(input, Charsets.UTF_8),
                    key, ftpClient, ftpClients);
            return reader;
        } catch (Exception ex) {
            if (ftpClient != null) {
                try {
                    ftpClient.disconnect();
                } finally {
                    ftpClients.returnObject(key, ftpClient);
                }
            }
            throw ex;
        }
    }

    public void close() {
        this.ftpClients.close();
    }

    /**
     * 创建连接
     */
    @Override
    public PooledObject<FTPClient> makeObject(String authority) throws Exception {
        logger.info("Create connection on server {}.", authority);
        Authority auth = Authority.create(authority);
        FTPClient ftpClient = FtpReader.connect(auth.getHost(), auth.getPort(), auth.getUsername(), auth.getPassword());
        PooledObject<FTPClient> po = new DefaultPooledObject<FTPClient>(ftpClient);
        return po;
    }

    /**
     * 关闭ftpClient
     */
    @Override
    public void destroyObject(String authority, PooledObject<FTPClient> p) throws Exception {
        logger.info("Close connection on server {}.", authority);
        FTPClient ftpClient = p.getObject();
        close(ftpClient);
    }

    @Override
    public boolean validateObject(String authority, PooledObject<FTPClient> p) {
        FTPClient ftpClient = p.getObject();
        boolean availed = ftpClient != null && ftpClient.isAvailable();
        if (availed == false) {
            logger.warn("Invalid connection on server {}", authority);
        }
        return availed;
    }

    @Override
    public void activateObject(String authority, PooledObject<FTPClient> p) throws Exception {
    }

    @Override
    public void passivateObject(String authority, PooledObject<FTPClient> p) throws Exception {
    }

    public static class PooledFtpBufferedReader extends BufferedReader {

        private KeyedObjectPool<String, FTPClient> ftpClients;

        private String host;

        private FTPClient ftpClient;

        public PooledFtpBufferedReader(Reader in, String host, FTPClient ftpClient,
                                       KeyedObjectPool<String, FTPClient> ftpClients) {
            super(in);
            this.ftpClient = ftpClient;
            this.host = host;
            this.ftpClients = ftpClients;
        }

        @Override
        public void close() throws IOException {
            super.close();
            if (ftpClient == null) {
                return;
            }
            try {
                ftpClient.completePendingCommand();
            } catch (Exception ex) {
                ftpClient.disconnect();
                logger.error("", ex);
            } finally {
                try {
                    ftpClients.returnObject(host, ftpClient);
                } catch (Exception e) {
                }
            }
        }
    }

    static class Authority {

        private String authority;

        private String username;

        private String password;

        private String host;

        private int port;

        public static Authority create(String authority) {
            Authority auth = new Authority();
            auth.authority = authority;
            int index = authority.indexOf('@');
            if (index != -1) {
                String up = authority.substring(0, index);
                authority = authority.substring(index + 1);
                index = up.indexOf(':');
                if (index != -1) {
                    auth.username = up.substring(0, index);
                    auth.password = up.substring(index + 1);
                } else {
                    auth.username = up;
                }
            }
            index = authority.indexOf(':');
            if (index != -1) {
                auth.host = authority.substring(0, index);
                auth.port = NumberUtils.toInt(authority.substring(index + 1));
            } else {
                auth.host = authority;
                auth.port = FTP.DEFAULT_PORT;
            }
            return auth;
        }

        public String getAuthority() {
            return authority;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

    }

}
