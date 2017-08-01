package com.eastcom.csfb.storm.kafka;

public class ConfigKey {

    // public
    // spout
    public static final String PROJECT_SPOUT_TYPE="project.spout.type";
    public static final String PROJECT_SPOUT_PARALLELISM="project.spout.parallelism";
    public static final String PROJECT_SPOUT_REDIS_BUFFER_MS="project.spout.redis.buffer.ms";
    public static final String PROJECT_SPOUT_REDIS_READER_THREADS="project.spout.redis.reader.threads";


    public static final String PROJECT_REDIS_PARTITION_SIZE="project.redis.partition.size";
    public static final String PROJECT_SPOUT_ZK_CONNECT="project.spout.zk.connect";


    // bolt
    public static final String PROJECT_BOLT_PARALLELISM="project.bolt.parallelism";



    // ftp add mq
    public static final String PROJECT_SPOUT_FTP_READER_THREADS="project.spout.ftp.reader.threads";
    public static final String PROJECT_SPOUT_FILE_TOPICS="project.spout.topics";
    public static final String PROJECT_MQ_BROKER_URL="project.mq.broker.url";
    public static final String PROJECT_MQ_QUEUE_NAME="project.mq.queue.name";

    // kafka
    public static final String PROJECT_SPOUT_KAFKA_READER_THREADS="project.spout.kafka.reader.threads";
    public static final String KAFKA_GROUP_ID = "kafka.group.id";
    public static final String KAFKA_TOPIC_NAMES = "kafka.topic.names";
    public static final String KAFKA_BOOTSTRAP_SERVERS = "kafka.bootstrap.servers";
    public static final String KAFKA_KEY_DESERIALIZER = "kafka.key.deserializer";
    public static final String KAFKA_VALUE_DESERIALIZER = "kafka.value.deserializer";
    public static final String KAFKA_AUTO_OFFSET_RESET= "kafka.auto.offset.reset";


    public static final String FILE_QUEUE_NAME = "file.queue.name";
    public static final String FILE_TOPIC_NAMES = "file.topic.names";
    public static final String FILE_READ_THREAD = "file.read.thread";

    public static final String FTP_POOL_ENABLE = "ftp.pool.enable";
    public static final String FTP_POOL_MAX_TOTAL = "ftp.pool.maxTotal";
    public static final String FTP_POOL_MAX_IDLE = "ftp.pool.maxIdle";

    public static final String FTP_READ_RETRY = "ftp.read.retry";

    // the class parse csv object.
    public static final String CSVPARSER_SELECTOR_CLASS = "csvparser.selector.class";

}
