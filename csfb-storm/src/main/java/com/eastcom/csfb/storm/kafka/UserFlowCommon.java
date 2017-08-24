package com.eastcom.csfb.storm.kafka;

/**
 * 流量公共字段
 *
 * @author louyj
 */
@Deprecated
public interface UserFlowCommon {

    /**
     * UL Data 上行流量
     *
     * @return
     */
    public long getUlBytes();

    /**
     * DL Data 下行流量
     *
     * @return
     */
    public long getDlBytes();

}
