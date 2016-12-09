package com.eastcom.csfb.storm.base.bean;

import java.io.Serializable;

/**
 * Created by linghang.kong on 2initTime16/5/17.
 * create a csfb object.
 */
public class Csfb implements CsfbDataModel, Serializable {

    public static final long serialVersionUID = 1L;

    public static final int initValue = -1;
    public static final int initTime = 0;

    private long BeginTime = initTime;
    private long EndTime = initTime;
    private int CallType = initValue;
    private int AlertingFlag = initValue;
    private int TAC = initValue;
    private int ECI = initValue;
    private int LAC = initValue;
    private int CI = initValue;
    private String IMSI;
    private String IMEI;
    private String IMEI_TAC;
    private String MSC_ID;
    private String MME_ID;
    private String ENB_ID;
    private long CMServiceRequestTime = initTime;
    private long PagingRequestTime = initTime;
    private long UEContextReleaseTime = initTime;
    private long AlertingTime = initTime;
    private long McCallEndTime = initTime;
    private long BackTime = initTime;
    private long PagingLong = initValue;
    private long FallbackLong = initValue;
    private long BackLong = initValue;
    private long E2ELong = initValue;
    private long MTLong = initValue;
    private long MOLong = initValue;
    private int MONum = initValue;
    private int MTNum = initValue;
    private int MOSuccNum = initValue;
    private int MTSuccNum = initValue;
    private int PagingNum = initValue;
    private int PagingSuccNum = initValue;
    private int CallServiceNum = initValue;
    private int CallServiceSuccNum = initValue;
    private int FallbackNum = initValue;
    private int FallbackSuccNum = initValue;
    private int CallSetupNum = initValue;
    private int CallSetupSuccNum = initValue;
    private int CallAlertingNum = initValue;
    private int CallAlertingSuccNum = initValue;
    private int RetInFiveSec = initValue;
    private int RetInTwoMin = initValue;
    private int CallConnectNum = initValue;
    private int CallDropNum = initValue;
    private int CallConnectSuccNum = initValue;
    private int LuFlag = initValue;
    private int olac = initValue;
    private int oci = initValue;
    private int dlac = initValue;
    private int dci = initValue;
    private int LuNum = initValue;
    private int LuSuccNum = initValue;
    private long ExtendServiceRequestTime = initTime;
    private long McPagingResponseTime = initTime;
    private long McConnectTime = initTime;
    private long ReleaseTime = initTime;
    private long ClearTime = initTime;
    private long TAUTime = initTime;
    private String CallingNum;
    private String CalledNum;
    private int ReturnSuccNum = initValue;
    private int SGsPagingNum = initValue;
    private int SGsPagingSuccNum = initValue;
    private int SGsServiceNum = initValue;
    private int SGsServiceSuccNum = initValue;
    private int S1PagingNum = initValue;
    private int S1PagingSuccNum = initValue;
    private int S1ServiceNum = initValue;
    private int S1ServiceSuccNum = initValue;
    private int S1ModifyNum = initValue;
    private int S1ModifySuccNum = initValue;
    private int S1ReleaseNum = initValue;
    private int S1ReleaseSuccNum = initValue;
    private long AssignTime = initValue;
    private int AssignReqNum = initValue;
    private int AssignSuccNum = initValue;
    private long AuthTime = initValue;
    private int AuthReqNum = initValue;
    private int AuthSuccNum = initValue;
    private long CallServiceLong = initValue;
    private long CallSetupLong = initValue;
    private long CallAlertingLong = initValue;
    private int FirstErrorXDR = initValue;
    private int CauseCode = initValue;
    private int State = initValue;
    private int InterruptXdr = initValue;
    private String XdrRowKeyList;

    public long getUEContextReleaseTime() {
        return UEContextReleaseTime;
    }

    public void setUEContextReleaseTime(long UEContextReleaseTime) {
        this.UEContextReleaseTime = UEContextReleaseTime;
    }

    public long getBeginTime() {
        return BeginTime;
    }

    public void setBeginTime(long beginTime) {
        BeginTime = beginTime;
    }

    public long getEndTime() {
        return EndTime;
    }

    public void setEndTime(long endTime) {
        EndTime = endTime;
    }

    public int getCallType() {
        return CallType;
    }

    public void setCallType(int callType) {
        CallType = callType;
    }

    public int getAlertingFlag() {
        return AlertingFlag;
    }

    public void setAlertingFlag(int alertingFlag) {
        AlertingFlag = alertingFlag;
    }

    public int getTAC() {
        return TAC;
    }

    public void setTAC(int TAC) {
        this.TAC = TAC;
    }

    public int getECI() {
        return ECI;
    }

    public void setECI(int ECI) {
        this.ECI = ECI;
    }

    public int getLAC() {
        return LAC;
    }

    public void setLAC(int LAC) {
        this.LAC = LAC;
    }

    public int getCI() {
        return CI;
    }

    public void setCI(int CI) {
        this.CI = CI;
    }

    public String getIMSI() {
        return IMSI;
    }

    public void setIMSI(String IMSI) {
        this.IMSI = IMSI;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public String getIMEI_TAC() {
        return IMEI_TAC;
    }

    public void setIMEI_TAC(String IMEI_TAC) {
        this.IMEI_TAC = IMEI_TAC;
    }

    public String getMSC_ID() {
        return MSC_ID;
    }

    public void setMSC_ID(String MSC_ID) {
        this.MSC_ID = MSC_ID;
    }

    public String getMME_ID() {
        return MME_ID;
    }

    public void setMME_ID(String MME_ID) {
        this.MME_ID = MME_ID;
    }

    public String getENB_ID() {
        return ENB_ID;
    }

    public void setENB_ID(String ENB_ID) {
        this.ENB_ID = ENB_ID;
    }

    public long getCMServiceRequestTime() {
        return CMServiceRequestTime;
    }

    public void setCMServiceRequestTime(long CMServiceRequestTime) {
        this.CMServiceRequestTime = CMServiceRequestTime;
    }

    public long getPagingRequestTime() {
        return PagingRequestTime;
    }

    public void setPagingRequestTime(long pagingRequestTime) {
        PagingRequestTime = pagingRequestTime;
    }

    public long getAlertingTime() {
        return AlertingTime;
    }

    public void setAlertingTime(long alertingTime) {
        AlertingTime = alertingTime;
    }

    public long getMcCallEndTime() {
        return McCallEndTime;
    }

    public void setMcCallEndTime(long mcCallEndTime) {
        McCallEndTime = mcCallEndTime;
    }

    public long getBackTime() {
        return BackTime;
    }

    public void setBackTime(long backTime) {
        BackTime = backTime;
    }

    public long getPagingLong() {
        return PagingLong;
    }

    public void setPagingLong(long pagingLong) {
        PagingLong = pagingLong;
    }

    public long getFallbackLong() {
        return FallbackLong;
    }

    public void setFallbackLong(long fallbackLong) {
        FallbackLong = fallbackLong;
    }

    public long getBackLong() {
        return BackLong;
    }

    public void setBackLong(long backLong) {
        BackLong = backLong;
    }

    public long getE2ELong() {
        return E2ELong;
    }

    public void setE2ELong(long e2ELong) {
        E2ELong = e2ELong;
    }

    public long getMTLong() {
        return MTLong;
    }

    public void setMTLong(long MTLong) {
        this.MTLong = MTLong;
    }

    public long getMOLong() {
        return MOLong;
    }

    public void setMOLong(long MOLong) {
        this.MOLong = MOLong;
    }

    public int getMONum() {
        return MONum;
    }

    public void setMONum(int MONum) {
        this.MONum = MONum;
    }

    public int getMTNum() {
        return MTNum;
    }

    public void setMTNum(int MTNum) {
        this.MTNum = MTNum;
    }

    public int getMOSuccNum() {
        return MOSuccNum;
    }

    public void setMOSuccNum(int MOSuccNum) {
        this.MOSuccNum = MOSuccNum;
    }

    public int getMTSuccNum() {
        return MTSuccNum;
    }

    public void setMTSuccNum(int MTSuccNum) {
        this.MTSuccNum = MTSuccNum;
    }

    public int getPagingNum() {
        return PagingNum;
    }

    public void setPagingNum(int pagingNum) {
        PagingNum = pagingNum;
    }

    public int getPagingSuccNum() {
        return PagingSuccNum;
    }

    public void setPagingSuccNum(int pagingSuccNum) {
        PagingSuccNum = pagingSuccNum;
    }

    public int getCallServiceNum() {
        return CallServiceNum;
    }

    public void setCallServiceNum(int callServiceNum) {
        CallServiceNum = callServiceNum;
    }

    public int getCallServiceSuccNum() {
        return CallServiceSuccNum;
    }

    public void setCallServiceSuccNum(int callServiceSuccNum) {
        CallServiceSuccNum = callServiceSuccNum;
    }

    public int getFallbackNum() {
        return FallbackNum;
    }

    public void setFallbackNum(int fallbackNum) {
        FallbackNum = fallbackNum;
    }

    public int getFallbackSuccNum() {
        return FallbackSuccNum;
    }

    public void setFallbackSuccNum(int fallbackSuccNum) {
        FallbackSuccNum = fallbackSuccNum;
    }

    public int getCallSetupNum() {
        return CallSetupNum;
    }

    public void setCallSetupNum(int callSetupNum) {
        CallSetupNum = callSetupNum;
    }

    public int getCallSetupSuccNum() {
        return CallSetupSuccNum;
    }

    public void setCallSetupSuccNum(int callSetupSuccNum) {
        CallSetupSuccNum = callSetupSuccNum;
    }

    public int getCallAlertingNum() {
        return CallAlertingNum;
    }

    public void setCallAlertingNum(int callAlertingNum) {
        CallAlertingNum = callAlertingNum;
    }

    public int getCallAlertingSuccNum() {
        return CallAlertingSuccNum;
    }

    public void setCallAlertingSuccNum(int callAlertingSuccNum) {
        CallAlertingSuccNum = callAlertingSuccNum;
    }

    public int getRetInFiveSec() {
        return RetInFiveSec;
    }

    public void setRetInFiveSec(int retInFiveSec) {
        RetInFiveSec = retInFiveSec;
    }

    public int getRetInTwoMin() {
        return RetInTwoMin;
    }

    public void setRetInTwoMin(int retInTwoMin) {
        RetInTwoMin = retInTwoMin;
    }

    public int getCallConnectNum() {
        return CallConnectNum;
    }

    public void setCallConnectNum(int callConnectNum) {
        CallConnectNum = callConnectNum;
    }

    public int getCallDropNum() {
        return CallDropNum;
    }

    public void setCallDropNum(int callDropNum) {
        CallDropNum = callDropNum;
    }

    public int getCallConnectSuccNum() {
        return CallConnectSuccNum;
    }

    public void setCallConnectSuccNum(int callConnectSuccNum) {
        CallConnectSuccNum = callConnectSuccNum;
    }

    public int getLuFlag() {
        return LuFlag;
    }

    public void setLuFlag(int luFlag) {
        LuFlag = luFlag;
    }

    public int getOlac() {
        return olac;
    }

    public void setOlac(int olac) {
        this.olac = olac;
    }

    public int getOci() {
        return oci;
    }

    public void setOci(int oci) {
        this.oci = oci;
    }

    public int getDlac() {
        return dlac;
    }

    public void setDlac(int dlac) {
        this.dlac = dlac;
    }

    public int getDci() {
        return dci;
    }

    public void setDci(int dci) {
        this.dci = dci;
    }

    public int getLuNum() {
        return LuNum;
    }

    public void setLuNum(int luNum) {
        LuNum = luNum;
    }

    public int getLuSuccNum() {
        return LuSuccNum;
    }

    public void setLuSuccNum(int luSuccNum) {
        LuSuccNum = luSuccNum;
    }

    public long getExtendServiceRequestTime() {
        return ExtendServiceRequestTime;
    }

    public void setExtendServiceRequestTime(long extendServiceRequestTime) {
        ExtendServiceRequestTime = extendServiceRequestTime;
    }

    public long getMcPagingResponseTime() {
        return McPagingResponseTime;
    }

    public void setMcPagingResponseTime(long mcPagingResponseTime) {
        McPagingResponseTime = mcPagingResponseTime;
    }

    public long getMcConnectTime() {
        return McConnectTime;
    }

    public void setMcConnectTime(long mcConnectTime) {
        McConnectTime = mcConnectTime;
    }

    public long getReleaseTime() {
        return ReleaseTime;
    }

    public void setReleaseTime(long releaseTime) {
        ReleaseTime = releaseTime;
    }

    public long getClearTime() {
        return ClearTime;
    }

    public void setClearTime(long clearTime) {
        ClearTime = clearTime;
    }

    public long getTAUTime() {
        return TAUTime;
    }

    public void setTAUTime(long TAUTime) {
        this.TAUTime = TAUTime;
    }

    public String getCallingNum() {
        return CallingNum;
    }

    public void setCallingNum(String callingNum) {
        CallingNum = callingNum;
    }

    public String getCalledNum() {
        return CalledNum;
    }

    public void setCalledNum(String calledNum) {
        CalledNum = calledNum;
    }

    public int getReturnSuccNum() {
        return ReturnSuccNum;
    }

    public void setReturnSuccNum(int returnSuccNum) {
        ReturnSuccNum = returnSuccNum;
    }

    public int getSGsPagingNum() {
        return SGsPagingNum;
    }

    public void setSGsPagingNum(int SGsPagingNum) {
        this.SGsPagingNum = SGsPagingNum;
    }

    public int getSGsPagingSuccNum() {
        return SGsPagingSuccNum;
    }

    public void setSGsPagingSuccNum(int SGsPagingSuccNum) {
        this.SGsPagingSuccNum = SGsPagingSuccNum;
    }

    public int getSGsServiceNum() {
        return SGsServiceNum;
    }

    public void setSGsServiceNum(int SGsServiceNum) {
        this.SGsServiceNum = SGsServiceNum;
    }

    public int getSGsServiceSuccNum() {
        return SGsServiceSuccNum;
    }

    public void setSGsServiceSuccNum(int SGsServiceSuccNum) {
        this.SGsServiceSuccNum = SGsServiceSuccNum;
    }

    public int getS1PagingNum() {
        return S1PagingNum;
    }

    public void setS1PagingNum(int s1PagingNum) {
        S1PagingNum = s1PagingNum;
    }

    public int getS1PagingSuccNum() {
        return S1PagingSuccNum;
    }

    public void setS1PagingSuccNum(int s1PagingSuccNum) {
        S1PagingSuccNum = s1PagingSuccNum;
    }

    public int getS1ServiceNum() {
        return S1ServiceNum;
    }

    public void setS1ServiceNum(int s1ServiceNum) {
        S1ServiceNum = s1ServiceNum;
    }

    public int getS1ServiceSuccNum() {
        return S1ServiceSuccNum;
    }

    public void setS1ServiceSuccNum(int s1ServiceSuccNum) {
        S1ServiceSuccNum = s1ServiceSuccNum;
    }

    public int getS1ModifyNum() {
        return S1ModifyNum;
    }

    public void setS1ModifyNum(int s1ModifyNum) {
        S1ModifyNum = s1ModifyNum;
    }

    public int getS1ModifySuccNum() {
        return S1ModifySuccNum;
    }

    public void setS1ModifySuccNum(int s1ModifySuccNum) {
        S1ModifySuccNum = s1ModifySuccNum;
    }

    public int getS1ReleaseNum() {
        return S1ReleaseNum;
    }

    public void setS1ReleaseNum(int s1ReleaseNum) {
        S1ReleaseNum = s1ReleaseNum;
    }

    public int getS1ReleaseSuccNum() {
        return S1ReleaseSuccNum;
    }

    public void setS1ReleaseSuccNum(int s1ReleaseSuccNum) {
        S1ReleaseSuccNum = s1ReleaseSuccNum;
    }

    public long getAssignTime() {
        return AssignTime;
    }

    public void setAssignTime(long assignTime) {
        AssignTime = assignTime;
    }

    public int getAssignReqNum() {
        return AssignReqNum;
    }

    public void setAssignReqNum(int assignReqNum) {
        AssignReqNum = assignReqNum;
    }

    public int getAssignSuccNum() {
        return AssignSuccNum;
    }

    public void setAssignSuccNum(int assignSuccNum) {
        AssignSuccNum = assignSuccNum;
    }

    public long getAuthTime() {
        return AuthTime;
    }

    public void setAuthTime(long authTime) {
        AuthTime = authTime;
    }

    public int getAuthReqNum() {
        return AuthReqNum;
    }

    public void setAuthReqNum(int authReqNum) {
        AuthReqNum = authReqNum;
    }

    public int getAuthSuccNum() {
        return AuthSuccNum;
    }

    public void setAuthSuccNum(int authSuccNum) {
        AuthSuccNum = authSuccNum;
    }

    public long getCallServiceLong() {
        return CallServiceLong;
    }

    public void setCallServiceLong(long callServiceLong) {
        CallServiceLong = callServiceLong;
    }

    public long getCallSetupLong() {
        return CallSetupLong;
    }

    public void setCallSetupLong(long callSetupLong) {
        CallSetupLong = callSetupLong;
    }

    public long getCallAlertingLong() {
        return CallAlertingLong;
    }

    public void setCallAlertingLong(long callAlertingLong) {
        CallAlertingLong = callAlertingLong;
    }

    public int getFirstErrorXDR() {
        return FirstErrorXDR;
    }

    public void setFirstErrorXDR(int firstErrorXDR) {
        FirstErrorXDR = firstErrorXDR;
    }

    public int getCauseCode() {
        return CauseCode;
    }

    public void setCauseCode(int causeCode) {
        CauseCode = causeCode;
    }

    public int getState() {
        return State;
    }

    public void setState(int state) {
        State = state;
    }

    public int getInterruptXdr() {
        return InterruptXdr;
    }

    public void setInterruptXdr(int interruptXdr) {
        InterruptXdr = interruptXdr;
    }

    public String getXdrRowKeyList() {
        return XdrRowKeyList;
    }

    public void setXdrRowKeyList(String xdrRowKeyList) {
        XdrRowKeyList = xdrRowKeyList;
    }


    public String filter(long data) {
        if (data == -1) {
            return "";
        }
        return String.valueOf(data);
    }


    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(filter(BeginTime));
        stringBuffer.append("|").append(filter(EndTime));
        stringBuffer.append("|").append(filter(CallType));
        stringBuffer.append("|").append(filter(AlertingFlag));
        stringBuffer.append("|").append(filter(TAC));
        stringBuffer.append("|").append(filter(ECI));
        stringBuffer.append("|").append(filter(LAC));
        stringBuffer.append("|").append(filter(CI));
        stringBuffer.append("|").append(IMSI);
        stringBuffer.append("|").append(IMEI);
        stringBuffer.append("|").append(IMEI_TAC);
        stringBuffer.append("|").append(MSC_ID);
        stringBuffer.append("|").append(MME_ID);
        stringBuffer.append("|").append(ENB_ID);
        stringBuffer.append("|").append(filter(CMServiceRequestTime));
        stringBuffer.append("|").append(filter(PagingRequestTime));
        stringBuffer.append("|").append(filter(UEContextReleaseTime));
        stringBuffer.append("|").append(filter(AlertingTime));
        stringBuffer.append("|").append(filter(McCallEndTime));
        stringBuffer.append("|").append(filter(BackTime));
        stringBuffer.append("|").append(filter(PagingLong));
        stringBuffer.append("|").append(filter(FallbackLong));
        stringBuffer.append("|").append(filter(BackLong));
        stringBuffer.append("|").append(filter(E2ELong));
        stringBuffer.append("|").append(filter(MTLong));
        stringBuffer.append("|").append(filter(MOLong));
        stringBuffer.append("|").append(filter(MONum));
        stringBuffer.append("|").append(filter(MTNum));
        stringBuffer.append("|").append(filter(MOSuccNum));
        stringBuffer.append("|").append(filter(MTSuccNum));
        stringBuffer.append("|").append(filter(PagingNum));
        stringBuffer.append("|").append(filter(PagingSuccNum));
        stringBuffer.append("|").append(filter(CallServiceNum));
        stringBuffer.append("|").append(filter(CallServiceSuccNum));
        stringBuffer.append("|").append(filter(FallbackNum));
        stringBuffer.append("|").append(filter(FallbackSuccNum));
        stringBuffer.append("|").append(filter(CallSetupNum));
        stringBuffer.append("|").append(filter(CallSetupSuccNum));
        stringBuffer.append("|").append(filter(CallAlertingNum));
        stringBuffer.append("|").append(filter(CallAlertingSuccNum));
        stringBuffer.append("|").append(filter(RetInFiveSec));
        stringBuffer.append("|").append(filter(RetInTwoMin));
        stringBuffer.append("|").append(filter(CallConnectNum));
        stringBuffer.append("|").append(filter(CallDropNum));
        stringBuffer.append("|").append(filter(CallConnectSuccNum));
        stringBuffer.append("|").append(filter(LuFlag));
        stringBuffer.append("|").append(filter(olac));
        stringBuffer.append("|").append(filter(oci));
        stringBuffer.append("|").append(filter(dlac));
        stringBuffer.append("|").append(filter(dci));
        stringBuffer.append("|").append(filter(LuNum));
        stringBuffer.append("|").append(filter(LuSuccNum));
        stringBuffer.append("|").append(filter(ExtendServiceRequestTime));
        stringBuffer.append("|").append(filter(McPagingResponseTime));
        stringBuffer.append("|").append(filter(McConnectTime));
        stringBuffer.append("|").append(filter(ReleaseTime));
        stringBuffer.append("|").append(filter(ClearTime));
        stringBuffer.append("|").append(filter(TAUTime));
        stringBuffer.append("|").append(CallingNum);
        stringBuffer.append("|").append(CalledNum);
        stringBuffer.append("|").append(filter(ReturnSuccNum));
        stringBuffer.append("|").append(filter(SGsPagingNum));
        stringBuffer.append("|").append(filter(SGsPagingSuccNum));
        stringBuffer.append("|").append(filter(SGsServiceNum));
        stringBuffer.append("|").append(filter(SGsServiceSuccNum));
        stringBuffer.append("|").append(filter(S1PagingNum));
        stringBuffer.append("|").append(filter(S1PagingSuccNum));
        stringBuffer.append("|").append(filter(S1ServiceNum));
        stringBuffer.append("|").append(filter(S1ServiceSuccNum));
        stringBuffer.append("|").append(filter(S1ModifyNum));
        stringBuffer.append("|").append(filter(S1ModifySuccNum));
        stringBuffer.append("|").append(filter(S1ReleaseNum));
        stringBuffer.append("|").append(filter(S1ReleaseSuccNum));
        stringBuffer.append("|").append(filter(AssignTime));
        stringBuffer.append("|").append(filter(AssignReqNum));
        stringBuffer.append("|").append(filter(AssignSuccNum));
        stringBuffer.append("|").append(filter(AuthTime));
        stringBuffer.append("|").append(filter(AuthReqNum));
        stringBuffer.append("|").append(filter(AuthSuccNum));
        stringBuffer.append("|").append(filter(CallServiceLong));
        stringBuffer.append("|").append(filter(CallSetupLong));
        stringBuffer.append("|").append(filter(CallAlertingLong));
        stringBuffer.append("|").append(filter(FirstErrorXDR));
        stringBuffer.append("|").append(filter(CauseCode));
        stringBuffer.append("|").append(filter(State));
        stringBuffer.append("|").append(filter(InterruptXdr));
        stringBuffer.append("|").append(XdrRowKeyList);
        return stringBuffer.toString();
    }
}
