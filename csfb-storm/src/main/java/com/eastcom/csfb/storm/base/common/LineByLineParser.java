package com.eastcom.csfb.storm.base.common;

import org.apache.commons.lang3.StringUtils;

import java.io.*;

import static com.google.common.base.Charsets.UTF_8;

public class LineByLineParser {
    /**
     * 字符串str转换为缓冲字节流
     *
     * @param str
     * @return
     */
    public static BufferedReader toReader(String str) {
        BufferedReader br = new BufferedReader(new StringReader(str));
        return br;
    }

    public static void parseConfigFile(String path, LineParser parser) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), UTF_8));
        reader.readLine(); // 跳过redisKey行 #! redisKey
        parse(reader, parser);
    }

    public static void splitParseConfigFile(String path, String separator, int minFiled, LineSplitParser parser)
            throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), UTF_8));
        reader.readLine(); // 跳过redisKey行 #! redisKey
        splitParse(reader, separator, minFiled, parser);
    }

    public static void splitParseConfigFile(String path, int minFiled, LineSplitParser parser) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), UTF_8));
        reader.readLine(); // 跳过redisKey行 #! redisKey
        splitParse(reader, minFiled, parser);
    }

    public static void parse(BufferedReader reader, LineParser parser) throws IOException {
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            if (StringUtils.isBlank(line)) {
                continue;
            }
            parser.parse(line.trim());
        }
        reader.close();
    }

    public static void splitParse(BufferedReader reader, String separator, int minFiled, LineSplitParser parser)
            throws IOException {
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            if (StringUtils.isBlank(line)) {
                continue;
            }
            String[] arr = line.split(separator);
            if (arr.length < minFiled) {
                continue;
            }
            parser.parse(arr);
        }
        reader.close();
    }

    public static void splitParse(BufferedReader reader, int minFiled, LineSplitParser parser) throws IOException {
        splitParse(reader, "\\s+", minFiled, parser);
    }

    public static interface LineParser {

        public void parse(String line);

    }

    public static interface LineSplitParser {

        public void parse(String[] fields);

    }

}
