package com.eastcom.csfb.storm.base.bean;

/**
 * Created by linghang.kong on 2016/5/17.
 */
public interface CsfbDataModel {
    /**
     * 01 BeginTime
     */
    public long getBeginTime();

    /**
     * 02 EndTime
     */
    public long getEndTime();

    /**
     * 03 CallType
     */
    public int getCallType();

    /**
     * 04 AlertingFlag
     */
    public int getAlertingFlag();

    /**
     * 05 TAC
     */
    public int getTAC();

    /**
     * 06 ECI
     */
    public int getECI();

    /**
     * 07 LAC
     */
    public int getLAC();

    /**
     * 08 CI
     */
    public int getCI();

    /**
     * 09 IMSI
     */
    public String getIMSI();

    /**
     * 10 IMEI
     */
    public String getIMEI();

    /**
     * 11 IMEI_TAC
     */
    public String getIMEI_TAC();

    /**
     * 12 MSC_ID
     */
    public String getMSC_ID();

    /**
     * 13 MME_ID
     */
    public String getMME_ID();

    /**
     * 14 ENB_ID
     */
    public String getENB_ID();

    /**
     * 15 CMServiceRequestTime
     */
    public long getCMServiceRequestTime();

    /**
     * 16 PagingRequestTime
     */
    public long getPagingRequestTime();

    /**
     * 17 UEContextReleaseTime
     */
    public long getUEContextReleaseTime();

    /**
     * 18 AlertingTime
     */
    public long getAlertingTime();

    /**
     * 19 McCallEndTime
     */
    public long getMcCallEndTime();

    /**
     * 20 BackTime
     */
    public long getBackTime();

    /**
     * 21 PagingLong
     */
    public long getPagingLong();

    /**
     * 22 FallbackLong
     */
    public long getFallbackLong();

    /**
     * 23 BackLong
     */
    public long getBackLong();

    /**
     * 24 E2ELong
     */
    public long getE2ELong();

    public long getMTLong();

    public long getMOLong();

    public int getMONum();

    public int getMTNum();

    public int getMOSuccNum();

    public int getMTSuccNum();

    public int getPagingNum();

    public int getPagingSuccNum();

    public int getCallServiceNum();

    public int getCallServiceSuccNum();

    public int getFallbackNum();

    public int getFallbackSuccNum();

    public int getCallSetupNum();

    public int getCallSetupSuccNum();

    public int getCallAlertingNum();

    public int getCallAlertingSuccNum();

    public int getRetInFiveSec();

    public int getRetInTwoMin();

    public int getCallConnectNum();

    public int getCallDropNum();

    public int getCallConnectSuccNum();

    public int getLuFlag();

    public int getOlac();

    public int getOci();

    public int getDlac();

    public int getDci();

    public int getLuNum();

    public int getLuSuccNum();

    public long getExtendServiceRequestTime();

    public long getMcPagingResponseTime();

    public long getMcConnectTime();

    public long getReleaseTime();

    public long getClearTime();

    public long getTAUTime();

    public String getCallingNum();

    public String getCalledNum();

    public int getReturnSuccNum();

    public int getSGsPagingNum();

    public int getSGsPagingSuccNum();

    public int getSGsServiceNum();

    public int getSGsServiceSuccNum();

    public int getS1PagingNum();

    public int getS1PagingSuccNum();

    public int getS1ServiceNum();

    public int getS1ServiceSuccNum();

    public int getS1ModifyNum();

    public int getS1ModifySuccNum();

    public int getS1ReleaseNum();

    public int getS1ReleaseSuccNum();

    public long getAssignTime();

    public int getAssignReqNum();

    public int getAssignSuccNum();

    public long getAuthTime();

    public int getAuthReqNum();

    public int getAuthSuccNum();

    public long getCallServiceLong();

    public long getCallSetupLong();

    public long getCallAlertingLong();

    public int getFirstErrorXDR();

    public int getCauseCode();

    public int getState();

    public int getInterruptXdr();

    public String getXdrRowKeyList();


}
