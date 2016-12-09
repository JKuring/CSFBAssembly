package com.eastcom.csfb.storm.base.reader;

import com.google.common.base.Charsets;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.provider.ftp.FtpFileSystemConfigBuilder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * ftp 长连接
 *
 * @author louyj
 */
public class VfsFtpReader implements Readable {

    private FileSystemOptions opts;

    public VfsFtpReader() {
        try {
            opts = new FileSystemOptions();

            FtpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, false);
            FtpFileSystemConfigBuilder.getInstance().setDataTimeout(opts, 10000);
            FtpFileSystemConfigBuilder.getInstance().setSoTimeout(opts, 10000);
            FtpFileSystemConfigBuilder.getInstance().setPassiveMode(opts, true);

            // SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts,
            // "no");
            // SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts,
            // false);// 是否使用用户目录作为跟路径
            // SftpFileSystemConfigBuilder.getInstance().setTimeout(opts,
            // 10000);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 远程文件输入流
     */
    @Override
    public BufferedReader read(String fileUri) throws Exception {
        FileObject remoteFile = VFS.getManager().resolveFile(fileUri, opts);
        InputStream input = remoteFile.getContent().getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input, Charsets.UTF_8));
        return reader;
    }

}
