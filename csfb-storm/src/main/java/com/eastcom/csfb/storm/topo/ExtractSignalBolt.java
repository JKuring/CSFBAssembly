package com.eastcom.csfb.storm.topo;

import com.eastcom.csfb.data.UserCommon;
import com.eastcom.csfb.data.ltesignal.LteS1Mme;
import com.eastcom.csfb.data.ltesignal.LteSGs;
import com.eastcom.csfb.data.mc.McCallEvent;
import com.eastcom.csfb.data.mc.McLocationUpdate;
import com.eastcom.csfb.data.mc.McPaging;
import com.eastcom.csfb.storm.arithmetic.CsfbArithmeticModel;
import com.eastcom.csfb.storm.base.bean.Csfb;
import com.google.common.cache.*;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.google.common.cache.RemovalCause.COLLECTED;


/**
 * Created by linghang.kong on 2016/5/17.
 */
public class ExtractSignalBolt extends BaseRichBolt {

    private static final int MAXIMUM_CACHE_SIZE = 1000000;
    private static final int MAXIMUM_CACHE_TIME = 60;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private OutputCollector collector;
    private LoadingCache<String, LinkedHashMap> csfbSignalCache;
    private ExecutorService executor;
    private List<Object> csfbSignalList;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;

        this.executor = Executors.newCachedThreadPool();
        this.csfbSignalCache = this.getLoadingCache(this.collector);

        this.csfbSignalList = new ArrayList<>();
    }

    @Override
    public void execute(Tuple input) {
        try {
            //存放时，若为TUA则存放完成后，算法计算，然后发送，删除。
            final ExtractSignalBolt extractSignalBolt = this;
            String csfbImsi = this.putCsfbSignalCache(input.getValueByField("csvData"));
            if (csfbImsi != null) {
                final LinkedHashMap<String, UserCommon> csfbSignal = getCsfbSignalMap(csfbImsi);
                this.executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        // 正常的信令合成过程
                        try {
                            // 过滤主被叫以外的信令（单个TAU信令、不包含MC口的csfb信令、非主被叫信令）
                            if (extractSignalBolt.isMOT(csfbSignal.get("McCallEvent"))) {
                                Csfb csfb = new CsfbArithmeticModel().createModel(csfbSignal).getCsfbCallTicket();
                                if (csfb != null) {
                                    csfb.setState(2);
                                    String csfbData = csfb.toString();
                                    extractSignalBolt.collector.emit(new Values(csfbData));
                                }
                            }
                        } catch (Exception e) {
                            logger.error("Thread error! " + e.getMessage());
                        }
                    }
                });
            }
        } catch (Exception e) {
            logger.error("execute() " + e.getMessage());
        }
    }

    private synchronized LinkedHashMap<String, UserCommon> getCsfbSignalMap(String csfbImsi) throws ExecutionException {
        LinkedHashMap csfbSignal = this.csfbSignalCache.get(csfbImsi);
        // delete data ,when make sure emit data
        this.csfbSignalCache.invalidate(csfbImsi);
        return csfbSignal;
    }

    /**
     * Create a LoadingCache object and register a removal listener that emitting timeout data to next bolt.
     *
     * @param collector OutputCollector object is emit tuple to next bolt
     * @return LoadingCache
     */
    private LoadingCache getLoadingCache(final OutputCollector collector) {
        ListeningExecutorService executorService = MoreExecutors.listeningDecorator(executor);
        RemovalListener myListener = new RemovalListener() {
            @Override
            public void onRemoval(RemovalNotification removalNotification) {
                if (removalNotification.getCause() == COLLECTED) {
                    Csfb csfb = new CsfbArithmeticModel().createModel((LinkedHashMap) removalNotification.getValue()).getCsfbCallTicket();
                    if (csfb != null) {
                        csfb.setState(3);
                        csfb.setInterruptXdr(1);
                        String csfbData = csfb.toString();
                        collector.emit(new Values(csfbData));
                    }
                }
            }
        };
        RemovalListener removalListener = RemovalListeners.asynchronous(myListener, executorService);

        LoadingCache<String, LinkedHashMap> loadingCache = CacheBuilder.newBuilder()
                //.maximumSize(MAXIMUM_CACHE_SIZE)
                .expireAfterWrite(MAXIMUM_CACHE_TIME, TimeUnit.MINUTES)
                .removalListener(removalListener)
                .build(
                        new CacheLoader<String, LinkedHashMap>() {
                            public LinkedHashMap load(String key) throws Exception {
                                return new LinkedHashMap<String, UserCommon>();
                            }
                        }
                );
        return loadingCache;
    }

    /**
     * @param data a signal data from tuple
     * @return imsi as Csfb signal mark.
     * @throws Exception
     */
    private String putCsfbSignalCache(Object data) throws Exception {
        try {
            List<Object> csfbSignalList = this.getCSVData(data);
            if (csfbSignalList != null) {
                String signalName = (String) csfbSignalList.get(0);
                UserCommon signal = (UserCommon) csfbSignalList.get(1);
                String imsi = signal.getImsi();
                LinkedHashMap<String, UserCommon> csfbDataMap;
                csfbDataMap = this.csfbSignalCache.get(imsi);
                csfbDataMap.put(signalName, signal);
                if (this.isTAUSignal(signal)) {
                    return imsi;
                }
            }
        } catch (Exception e) {
            logger.error("putCsfbSignalCache() " + e.getMessage());
        }
        return null;
    }

    private boolean isTAUSignal(UserCommon data) throws Exception {
        try {
            LteS1Mme lteS1Mme;
            if (data instanceof LteS1Mme) {
                lteS1Mme = (LteS1Mme) data;
                return lteS1Mme.getProcedureType() == 5;
            }
        } catch (Exception e) {
            logger.error("isTAUSignal() " + e.getMessage());
        }
        return false;
    }

    private boolean isMOT(UserCommon data) {
        try {
            if (data instanceof McCallEvent) {
                McCallEvent mcCallEvent = (McCallEvent) data;
                if (mcCallEvent.getEventId() == 1 || mcCallEvent.getEventId() == 3) {
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("isMOT() " + e.getMessage());
        }
        return false;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("csfbData"));
    }

    /**
     * Judge an object belongs to which kind of signal type.
     *
     * @param csvData A signal data from tuple
     * @return a list object
     */
    private List<Object> getCSVData(Object csvData) {
        try {
            csvData = filter((UserCommon) csvData);
            if (csvData != null) {
                csfbSignalList.clear();
                String key;
                UserCommon value;
                if (csvData instanceof LteS1Mme) {
                    key = "LteS1Mme-" + ((LteS1Mme) csvData).getProcedureType();
                    value = (LteS1Mme) csvData;
                    csfbSignalList.add(key);
                    csfbSignalList.add(value);
                    return csfbSignalList;
                } else if (csvData instanceof LteSGs) {
                    key = "LteSGs-" + ((LteSGs) csvData).getProcedureType();
                    value = (LteSGs) csvData;
                    csfbSignalList.add(key);
                    csfbSignalList.add(value);
                    return csfbSignalList;
                } else if (csvData instanceof McCallEvent) {
                    key = "McCallEvent";
                    value = (McCallEvent) csvData;
                    csfbSignalList.add(key);
                    csfbSignalList.add(value);
                    return csfbSignalList;
                } else if (csvData instanceof McLocationUpdate) {
                    key = "McLocationUpdate";
                    value = (McLocationUpdate) csvData;
                    csfbSignalList.add(key);
                    csfbSignalList.add(value);
                    return csfbSignalList;
                } else if (csvData instanceof McPaging) {
                    key = "McPaging";
                    value = (McPaging) csvData;
                    csfbSignalList.add(key);
                    csfbSignalList.add(value);
                    return csfbSignalList;
                }
            }
        } catch (Exception e) {
            logger.error("getCSVData() " + e.getMessage());
        }
        return null;
    }

    // 过滤掉六分之五的无用数据
    private UserCommon filter(UserCommon signal) {
        if (signal instanceof LteS1Mme) {
            switch (((LteS1Mme) signal).getProcedureType()) {
                case 3:
                    return signal;
                case 4:
                    return signal;
                case 5:
                    return signal;
                case 19:
                    return signal;
                case 20:
                    return signal;
                default:
                    return null;
            }
        } else if (signal instanceof LteSGs) {
            switch (((LteSGs) signal).getProcedureType()) {
                case 1:
                    return signal;
                case 2:
                    return signal;
                default:
                    return null;
            }
        }
        return signal;
    }
}
