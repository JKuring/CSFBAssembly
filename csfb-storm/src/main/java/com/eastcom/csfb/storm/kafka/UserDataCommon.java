package com.eastcom.csfb.storm.kafka;

import com.eastcom.csfb.data.UserCommon;

/**
 * 用户使用某类APP所用上行流量和下行流量。(大类->小类、上行流量、下行流量)
 */
@Deprecated
public interface UserDataCommon extends UserCommon, UserFlowCommon {

    /**
     * App Type 集团规定的18种大类
     *
     * @return
     */
    public int getAppMajorClass();

    public void setAppMajorClass(int appMajorClass);

    /**
     * App Sub-type 根据集团定义的识别规则识别出来的小类
     *
     * @return
     */
    public int getAppMinorClass();

    public void setAppMinorClass(int appMinorClass);

}
