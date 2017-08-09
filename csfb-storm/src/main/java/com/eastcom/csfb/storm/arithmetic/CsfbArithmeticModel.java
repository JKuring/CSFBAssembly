package com.eastcom.csfb.storm.arithmetic;

import com.eastcom.csfb.data.UserCommon;
import com.eastcom.csfb.data.ltesignal.LteS1Mme;
import com.eastcom.csfb.data.ltesignal.LteSGs;
import com.eastcom.csfb.data.mc.McCallEvent;
import com.eastcom.csfb.data.mc.McLocationUpdate;
import com.eastcom.csfb.data.mc.McPaging;
import com.eastcom.csfb.storm.base.bean.Csfb;
import com.eastcom.csfb.storm.base.bean.CsfbDataModel;
import com.eastcom.csfb.storm.base.bean.CsfbExceptionCode;
import com.eastcom.csfb.storm.base.bean.CsfbExceptionObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * Created by linghang.kong on 2016/5/17.
 */
public class CsfbArithmeticModel implements CsfbDataModel, Serializable {
    public static final long serialVersionUID = 1L;
    private static final int FALSE_CODE = -1;
    private static final Logger logger = LoggerFactory.getLogger(CsfbArithmeticModel.class);
    private Csfb csfb;
    // Csfb Signal Map
    private LinkedHashMap<String, UserCommon> csfbSignal;
    // Exception Code
    private CsfbExceptionObject csfbExceptionObject = new CsfbExceptionObject();
    private InterruptXdrCode interruptXdrCode = new InterruptXdrCode();
    private ExceptionCode exceptionCode = new ExceptionCode();
    private CsfbExceptionCode csfbExceptionCode = new CsfbExceptionCode();

    private static void throwException() throws Exception {
        logger.debug("Invalid object in Csfb signal");
        throw new Exception("Invalid object");
    }

    private static void logProcessing(String log) {
        logger.debug(log);
    }

    public CsfbArithmeticModel createModel(LinkedHashMap<String, UserCommon> csfbSignal) {
        this.csfbSignal = csfbSignal;
        this.csfb = new Csfb();
        return this;
    }


    public Csfb getCsfbCallTicket() {
        if (filter()) {
            return null;
        }
        this.csfb.setBeginTime(this.getBeginTime());
        this.csfb.setEndTime(this.getEndTime());
        this.csfb.setCallType(this.getCallType());
        this.csfb.setAlertingFlag(this.getAlertingFlag());
        this.csfb.setTAC(this.getTAC());
        this.csfb.setECI(this.getECI());
        this.csfb.setLAC(this.getLAC());
        this.csfb.setCI(this.getCI());
        this.csfb.setIMSI(this.getIMSI());
        this.csfb.setIMEI(this.getIMEI());
        this.csfb.setIMEI_TAC(this.getIMEI_TAC());
        this.csfb.setMSC_ID(this.getMSC_ID());
        this.csfb.setMME_ID(this.getMME_ID());
        this.csfb.setENB_ID(this.getENB_ID());
        this.csfb.setCMServiceRequestTime(this.getCMServiceRequestTime());
        this.csfb.setPagingRequestTime(this.getPagingRequestTime());
        this.csfb.setUEContextReleaseTime(this.getUEContextReleaseTime());
        this.csfb.setAlertingTime(this.getAlertingTime());
        this.csfb.setMcCallEndTime(this.getMcCallEndTime());
        this.csfb.setBackTime(this.getBackTime());
        this.csfb.setPagingLong(this.getPagingLong());
        this.csfb.setFallbackLong(this.getFallbackLong());
        this.csfb.setBackLong(this.getBackLong());
        this.csfb.setE2ELong(this.getE2ELong());
        this.csfb.setMTLong(this.getMTLong());
        this.csfb.setMOLong(this.getMOLong());
        this.csfb.setMONum(this.getMONum());
        this.csfb.setMTNum(this.getMTNum());
        this.csfb.setMOSuccNum(this.getMOSuccNum());
        this.csfb.setMTSuccNum(this.getMTSuccNum());
        this.csfb.setPagingNum(this.getPagingNum());
        this.csfb.setPagingSuccNum(this.getPagingSuccNum());
        this.csfb.setCallServiceNum(this.getCallServiceNum());
        this.csfb.setCallServiceSuccNum(this.getCallServiceSuccNum());
        this.csfb.setFallbackNum(this.getFallbackNum());
        this.csfb.setFallbackSuccNum(this.getFallbackSuccNum());
        this.csfb.setCallSetupNum(this.getCallSetupNum());
        this.csfb.setCallSetupSuccNum(this.getCallSetupSuccNum());
        this.csfb.setCallAlertingNum(this.getCallAlertingNum());
        this.csfb.setCallAlertingSuccNum(this.getCallAlertingSuccNum());
        this.csfb.setRetInFiveSec(this.getRetInFiveSec());
        this.csfb.setRetInTwoMin(this.getRetInTwoMin());
        this.csfb.setCallConnectNum(this.getCallConnectNum());
        this.csfb.setCallDropNum(this.getCallDropNum());
        this.csfb.setCallConnectSuccNum(this.getCallConnectSuccNum());
        this.csfb.setLuFlag(this.getLuFlag());
        this.csfb.setOlac(this.getOlac());
        this.csfb.setOci(this.getOci());
        this.csfb.setDlac(this.getDlac());
        this.csfb.setDci(this.getDci());
        this.csfb.setLuNum(this.getLuNum());
        this.csfb.setLuSuccNum(this.getLuSuccNum());
        this.csfb.setExtendServiceRequestTime(this.getExtendServiceRequestTime());
        this.csfb.setMcPagingResponseTime(this.getMcPagingResponseTime());
        this.csfb.setMcConnectTime(this.getMcConnectTime());
        this.csfb.setReleaseTime(this.getReleaseTime());
        this.csfb.setClearTime(this.getClearTime());
        this.csfb.setTAUTime(this.getTAUTime());
        this.csfb.setCallingNum(this.getCallingNum());
        this.csfb.setCalledNum(this.getCalledNum());
        this.csfb.setReturnSuccNum(this.getReturnSuccNum());
        this.csfb.setSGsPagingNum(this.getSGsPagingNum());
        this.csfb.setSGsPagingSuccNum(this.getSGsPagingSuccNum());
        this.csfb.setSGsServiceNum(this.getSGsServiceNum());
        this.csfb.setSGsServiceSuccNum(this.getSGsServiceSuccNum());
        this.csfb.setS1PagingNum(this.getS1PagingNum());
        this.csfb.setS1PagingSuccNum(this.getS1PagingSuccNum());
        this.csfb.setS1ServiceNum(this.getS1ServiceNum());
        this.csfb.setS1ServiceSuccNum(this.getS1ServiceSuccNum());
        this.csfb.setS1ModifyNum(this.getS1ModifyNum());
        this.csfb.setS1ModifySuccNum(this.getS1ModifySuccNum());
        this.csfb.setS1ReleaseNum(this.getS1ReleaseNum());
        this.csfb.setS1ReleaseSuccNum(this.getS1ReleaseSuccNum());
        this.csfb.setAssignTime(this.getAssignTime());
        this.csfb.setAssignReqNum(this.getAssignReqNum());
        this.csfb.setAssignSuccNum(this.getAssignSuccNum());
        this.csfb.setAuthTime(this.getAuthTime());
        this.csfb.setAuthReqNum(this.getAuthReqNum());
        this.csfb.setAuthSuccNum(this.getAuthSuccNum());
        this.csfb.setCallServiceLong(this.getCallServiceLong());
        this.csfb.setCallSetupLong(this.getCallSetupLong());
        this.csfb.setCallAlertingLong(this.getCallAlertingLong());
        this.csfb.setFirstErrorXDR(this.getFirstErrorXDR());
        this.csfb.setCauseCode(this.getCauseCode());
        this.csfb.setState(this.getState());
        this.csfb.setInterruptXdr(this.getInterruptXdr());

        return this.csfb;

    }

    /**
     * 信令过滤规则
     *
     * @return True is filter the signal
     */
    private boolean filter() {
        boolean result = false;
        result = isCSFB() && isInsertedExceptionSignal();
        return result;
    }

    private boolean isCSFB() {
        boolean result = false;
        try {
            UserCommon mcCallEvent = this.csfbSignal.get("McCallEvent");
            if (mcCallEvent instanceof McCallEvent) {
                int eventId = ((McCallEvent) mcCallEvent).getEventId();
                if (eventId == 1 && !(csfbSignal.get("LteS1Mme-3") instanceof LteS1Mme) && !(csfbSignal.get("LteS1Mme-18") instanceof LteS1Mme)) {
                    result = true;
                } else if (eventId == 3 && !(this.csfbSignal.get("LteSGs-1") instanceof LteSGs) && !(csfbSignal.get("LteS1Mme-4") instanceof LteS1Mme)) {
                    result = true;
                }
            }
        } catch (Exception e) {
            logProcessing("isCSFB: " + e.getMessage());
        }
        return result;
    }

    /**
     * @param eventId
     * @return
     */
    private boolean switchSignal(int eventId) {
        boolean result = false;
        if (eventId == 1) {
            csfbSignal.put("LteS1Mme-3", csfbSignal.get("LteS1Mme-18"));
            result = true;
        } else if (eventId == 3) {
            csfbSignal.put("LteSGs-1", csfbSignal.get("LteS1Mme-4"));
            result = true;
        }
        return result;
    }

    private boolean isInsertedExceptionSignal() {
        boolean result = false;
        try {
            // 过滤信令时序异常
            // 3：Extended Service Request
            UserCommon lteS1Mme = csfbSignal.get("LteS1Mme-3");
            // 1：SGsAP-PAGING
            UserCommon lteSGs = this.csfbSignal.get("LteSGs-1");
            // Mc voice call
            UserCommon mcCallEvent = this.csfbSignal.get("McCallEvent");
            if (mcCallEvent instanceof McCallEvent) {
                McCallEvent mot = (McCallEvent) mcCallEvent;
                if (mot.getEventId() == 1 && lteS1Mme instanceof LteS1Mme && (mcCallEvent.getStartTime() < lteS1Mme.getStartTime())) {
                    result = true;
                } else if (mot.getEventId() == 3 && lteSGs instanceof LteSGs && (mcCallEvent.getStartTime() < lteSGs.getStartTime())) {
                    result = true;
                }
            }
        } catch (Exception e) {
            logProcessing("isInsertedExceptionSignal: " + e.getMessage());
        }
        return result;
    }


    @Override
    public long getBeginTime() {
        try {
            UserCommon mcCE = this.csfbSignal.get("McCallEvent");
            if (mcCE instanceof McCallEvent) {
                int eventId = ((McCallEvent) mcCE).getEventId();
                if (eventId == 1) {
                    // 3：Extended Service Request
                    UserCommon data = this.csfbSignal.get("LteS1Mme-3");
                    LteS1Mme lteS1Mme;
                    if (data instanceof LteS1Mme) {
                        return ((LteS1Mme) data).getStartTime();
                    }
                } else if (eventId == 3) {
                    // 1：SGsAP-PAGING
                    UserCommon data = this.csfbSignal.get("LteSGs-1");
                    LteSGs lteSGs;
                    if (data instanceof LteSGs) {
                        return ((LteSGs) data).getStartTime();
                    }
                }
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getBeginTime() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public long getEndTime() {
        try {
            // 5：TAU
            UserCommon data = this.csfbSignal.get("LteS1Mme-5");
            LteS1Mme lteS1Mme;
            if (data instanceof LteS1Mme) {
                return ((LteS1Mme) data).getStartTime();
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getEndTime() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public int getCallType() {
        try {
            // 主被叫判断
            UserCommon mcCE = this.csfbSignal.get("McCallEvent");
            int eventId;
            if (mcCE instanceof McCallEvent) {
                eventId = ((McCallEvent) mcCE).getEventId();
                if (eventId == 1) {
                    return 1;
                } else if (eventId == 3) {
                    return 2;
                }
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getCallType() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public int getAlertingFlag() {
        try {
            UserCommon mcCE = this.csfbSignal.get("McCallEvent");
            int alertoffset;
            if (mcCE instanceof McCallEvent) {
                alertoffset = ((McCallEvent) mcCE).getAlertOffset();
                if (alertoffset > 0) {
                    return 1;
                } else
                    return 0;
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getAlertingFlag() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public int getTAC() {
        try {
            UserCommon data = this.csfbSignal.get("LteS1Mme-3");
            if (data instanceof LteS1Mme) {
                return ((LteS1Mme) data).getTac();
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getTAC() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public int getECI() {
        try {
            UserCommon data = this.csfbSignal.get("LteS1Mme-3");
            LteS1Mme lteS1Mme;
            if (data instanceof LteS1Mme) {
                return ((LteS1Mme) data).getCellId();
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getECI() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public int getLAC() {
        try {
            UserCommon mcCE = this.csfbSignal.get("McCallEvent");
            if (mcCE instanceof McCallEvent) {
                return ((McCallEvent) mcCE).getLac();
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getLAC() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public int getCI() {
        try {
            UserCommon mcCE = this.csfbSignal.get("McCallEvent");
            if (mcCE instanceof McCallEvent) {
                return ((McCallEvent) mcCE).getCi();
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getCI() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public String getIMSI() {
        try {
            UserCommon mcCE = this.csfbSignal.get("McCallEvent");
            if (mcCE instanceof McCallEvent) {
                McCallEvent mcCallEvent = (McCallEvent) mcCE;
                if (mcCallEvent.getEventId() == 1) {
                    return mcCallEvent.getCallingimsi();
                } else if (mcCallEvent.getEventId() == 3) {
                    return mcCallEvent.getCalledimsi();
                }
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getIMSI() " + e.getMessage());
        }
        return null;
    }

    @Override
    public String getIMEI() {
        try {
            UserCommon mcCE = this.csfbSignal.get("McCallEvent");
            int eventId;
            if (mcCE instanceof McCallEvent) {
                eventId = ((McCallEvent) mcCE).getEventId();
                if (eventId == 1) {
                    UserCommon data = this.csfbSignal.get("LteS1Mme-3");
                    LteS1Mme lteS1Mme;
                    if (data instanceof LteS1Mme) {
                        return ((LteS1Mme) data).getImei();
                    } else
                        throwException();
                } else if (eventId == 3) {
                    // 1：SGsAP-PAGING
                    UserCommon data = this.csfbSignal.get("LteSGs-1");
                    LteSGs lteSGs;
                    if (data instanceof LteSGs) {
                        return ((LteSGs) data).getImei();
                    } else
                        throwException();
                }
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getIMEI() " + e.getMessage());
        }
        return null;
    }

    @Override
    public String getIMEI_TAC() {
        try {
            UserCommon mcCE = this.csfbSignal.get("McCallEvent");
            int eventId;
            if (mcCE instanceof McCallEvent) {
                eventId = ((McCallEvent) mcCE).getEventId();
                if (eventId == 1) {
                    UserCommon data = this.csfbSignal.get("LteS1Mme-3");
                    LteS1Mme lteS1Mme;
                    if (data instanceof LteS1Mme) {
                        String imei_tac = ((LteS1Mme) data).getImei();
                        if (imei_tac.length() > 0)
                            return imei_tac.substring(0, 8);
                    }
                } else if (eventId == 3) {
                    // 1：SGsAP-PAGING
                    UserCommon data = this.csfbSignal.get("LteSGs-1");
                    LteSGs lteSGs;
                    if (data instanceof LteSGs) {
                        String imei_tac = ((LteSGs) data).getImei();
                        if (imei_tac.length() > 0)
                            return imei_tac.substring(0, 8);
                    }
                }
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getIMEI_TAC() " + e.getMessage());
        }
        return null;
    }

    @Override
    public String getMSC_ID() {
        try {
            // 主被叫判断
            UserCommon mcCE = this.csfbSignal.get("McCallEvent");
            if (mcCE instanceof McCallEvent) {
                int eventId = ((McCallEvent) mcCE).getEventId();
                if (eventId == 3) {
                    UserCommon data = this.csfbSignal.get("LteSGs-1");
                    LteSGs lteSGs;
                    if (data instanceof LteSGs) {
                        return ((LteSGs) data).getMscServerIpAdd();
                    } else
                        throwException();
                }
            }
        } catch (Exception e) {
            logProcessing("getMSC_ID() " + e.getMessage());
        }
        return null;
    }

    @Override
    public String getMME_ID() {
        try {
            // 主被叫判断
            UserCommon mcCE = this.csfbSignal.get("McCallEvent");
            if (mcCE instanceof McCallEvent) {
                int eventId = ((McCallEvent) mcCE).getEventId();
                if (eventId == 1) {
                    UserCommon data = this.csfbSignal.get("LteS1Mme-3");
                    LteS1Mme lteS1Mme;
                    if (data instanceof LteS1Mme) {
                        return ((LteS1Mme) data).getMmeIpAdd();
                    }
                } else if (eventId == 3) {
                    // 1：SGsAP-PAGING
                    UserCommon data = this.csfbSignal.get("LteSGs-1");
                    LteSGs lteSGs;
                    if (data instanceof LteSGs) {
                        return ((LteSGs) data).getMmeIpAdd();
                    }
                }
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getMME_ID() " + e.getMessage());
        }
        return null;
    }

    @Override
    public String getENB_ID() {
        try {
            // 主被叫判断
            UserCommon mcCE = this.csfbSignal.get("McCallEvent");
            if (mcCE instanceof McCallEvent) {
                int eventId = ((McCallEvent) mcCE).getEventId();
                if (eventId == 1) {
                    UserCommon data = this.csfbSignal.get("LteS1Mme-3");
                    LteS1Mme lteS1Mme;
                    if (data instanceof LteS1Mme) {
                        return ((LteS1Mme) data).getEnbIpAdd();
                    }
                } else if (eventId == 3) {
                    // 4：    Paging
                    UserCommon data = this.csfbSignal.get("LteS1Mme-4");
                    LteS1Mme lteS1Mme;
                    if (data instanceof LteS1Mme) {
                        return ((LteS1Mme) data).getEnbIpAdd();
                    }
                }
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getENB_ID() " + e.getMessage());
        }
        return null;
    }

    @Override
    public long getCMServiceRequestTime() {
        try {
            UserCommon mcCE = this.csfbSignal.get("McCallEvent");
            if (mcCE instanceof McCallEvent) {
                McCallEvent mcCallEvent = (McCallEvent) mcCE;
                return mcCallEvent.getStartTime() + mcCallEvent.getCmreqoffset();
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getCMServiceRequestTime() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public long getPagingRequestTime() {
        try {
            // 1：SGsAP-PAGING
            UserCommon data = this.csfbSignal.get("LteSGs-1");
            LteSGs lteSGs;
            if (data instanceof LteSGs) {
                return ((LteSGs) data).getStartTime();
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getPagingRequestTime() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public long getUEContextReleaseTime() {
        try {
            // 20：UE context release
            UserCommon data = this.csfbSignal.get("LteS1Mme-20");
            LteS1Mme lteS1Mme;
            if (data instanceof LteS1Mme) {
                return ((LteS1Mme) data).getEndTime();
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getUEContextReleaseTime()" + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public long getAlertingTime() {
        try {
            UserCommon mcCE = this.csfbSignal.get("McCallEvent");
            if (mcCE instanceof McCallEvent) {
                McCallEvent mcCallEvent = (McCallEvent) mcCE;
                return mcCallEvent.getStartTime() + mcCallEvent.getAlertOffset();
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getAlertingTime() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public long getMcCallEndTime() {
        try {
            UserCommon mcCE = this.csfbSignal.get("McCallEvent");
            if (mcCE instanceof McCallEvent) {
                McCallEvent mcCallEvent = (McCallEvent) mcCE;
                return mcCallEvent.getStartTime() + mcCallEvent.getRelOffset();
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getMcCallEndTime() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public long getBackTime() {
        try {
            // 5：    TAU
            UserCommon data = this.csfbSignal.get("LteS1Mme-20");
            LteS1Mme lteS1Mme;
            if (data instanceof LteS1Mme) {
                return ((LteS1Mme) data).getStartTime();
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getBackTime() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public long getPagingLong() {
        try {
            // 主被叫判断
            UserCommon mot = this.csfbSignal.get("McCallEvent");
            if (mot instanceof McCallEvent) {
                int eventId = ((McCallEvent) mot).getEventId();
                if (eventId == 3) {
                    // 1：SGsAP-PAGING
                    // 2：SGsAP-SERVICE-REQUEST
                    UserCommon data1 = this.csfbSignal.get("LteSGs-1");
                    UserCommon data2 = this.csfbSignal.get("LteSGs-2");
                    LteSGs lteSGs;
                    if (data1 instanceof LteSGs && data2 instanceof LteSGs) {
                        long data1StartTime = ((LteSGs) data1).getStartTime();
                        long data2StartTime = ((LteSGs) data2).getStartTime();
                        if (data1StartTime > 0 && data2StartTime > 0) {
                            return data2StartTime - data1StartTime;
                        } else throwException();
                    } else
                        throwException();
                }
            }
        } catch (Exception e) {
            logProcessing("getPagingLong() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public long getFallbackLong() {
        try {
            UserCommon mcCE = this.csfbSignal.get("McCallEvent");
            UserCommon mcLU = this.csfbSignal.get("McLocationUpdate");
            // 主被叫判断
            if (mcCE instanceof McCallEvent) {
                int eventId = ((McCallEvent) mcCE).getEventId();
                if (eventId == 1) {
                    // 3：Extended Service Request
                    UserCommon data = this.csfbSignal.get("LteS1Mme-3");
                    if (mcLU instanceof McLocationUpdate && data instanceof LteS1Mme) {
                        long mcCEStartTime = ((McCallEvent) mcCE).getStartTime();
                        long mcLUStartTime = ((McLocationUpdate) mcLU).getStartTime();
                        long lteS1MmeESRStartTime = ((LteS1Mme) data).getStartTime();
                        if ((mcCEStartTime > 0 || mcLUStartTime > 0) && lteS1MmeESRStartTime > 0) {
                            return (mcCEStartTime > mcLUStartTime ? mcCEStartTime : mcLUStartTime) - lteS1MmeESRStartTime;
                        } else throwException();
                    } else
                        throwException();
                } else if (eventId == 3) {
                    UserCommon data = this.csfbSignal.get("LteSGs-1");
                    if (mcLU instanceof McLocationUpdate && data instanceof LteSGs) {
                        long mcCEStartTime = ((McCallEvent) mcCE).getStartTime();
                        long mcLUStartTime = ((McLocationUpdate) mcLU).getStartTime();
                        long lteSGsPagingStartTime = ((LteSGs) data).getStartTime();
                        if ((mcCEStartTime > 0 || mcLUStartTime > 0) && lteSGsPagingStartTime > 0) {
                            return (mcCEStartTime > mcLUStartTime ? mcCEStartTime : mcLUStartTime) - lteSGsPagingStartTime;
                        } else throwException();
                    } else
                        throwException();
                }
            }
        } catch (Exception e) {
            logProcessing("getFallbackLong() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public long getBackLong() {
        try {
            // 5：TAU
            UserCommon data = this.csfbSignal.get("LteS1Mme-5");
            UserCommon mcCE = this.csfbSignal.get("McCallEvent");
            LteS1Mme lteS1Mme;
            if (data instanceof LteS1Mme && mcCE instanceof McCallEvent) {
                long lteS1MmeStartTime = ((LteS1Mme) data).getStartTime();
                McCallEvent mc = (McCallEvent) mcCE;
                long backLong = lteS1MmeStartTime - (mc.getStartTime() + mc.getRelOffset());
                return backLong > 0 ? backLong : 0;
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getBackLong() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public long getE2ELong() {
        try {
            // 3：    Extended Service Request
            UserCommon data = this.csfbSignal.get("LteS1Mme-3");
            UserCommon mcCE = this.csfbSignal.get("McCallEvent");
            if (data instanceof LteS1Mme && mcCE instanceof McCallEvent) {
                McCallEvent mc = (McCallEvent) mcCE;
                if (mc.getEventId() == 1) {
                    long mcCEAlert = mc.getStartTime() + mc.getAlertOffset();
                    return mcCEAlert - ((LteS1Mme) data).getStartTime();
                }
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getE2ELong() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public long getMTLong() {
        try {
            // 主被叫判断
            UserCommon mot = this.csfbSignal.get("McCallEvent");
            if (mot instanceof McCallEvent) {
                int eventId = ((McCallEvent) mot).getEventId();
                if (eventId == 1) {
                    return 0;
                } else if (eventId == 3) {
                    // 1：SGsAP-PAGING
                    UserCommon data = this.csfbSignal.get("LteSGs-1");
                    UserCommon mcCE = this.csfbSignal.get("McCallEvent");
                    if (data instanceof LteSGs && mcCE instanceof McCallEvent) {
                        McCallEvent mc = (McCallEvent) mcCE;
                        if (mc.getAlertOffset() > 0) {
                            return mc.getStartTime() + mc.getAlertOffset() - ((LteSGs) data).getStartTime();
                        }
                    }
                }
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getMTLong() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public long getMOLong() {
        try {
            // 主被叫判断
            UserCommon mot = this.csfbSignal.get("McCallEvent");
            if (mot instanceof McCallEvent) {
                int eventId = ((McCallEvent) mot).getEventId();
                if (eventId == 1) {
                    // 3：    Extended Service Request
                    UserCommon data = this.csfbSignal.get("LteS1Mme-3");
                    UserCommon mcCE = this.csfbSignal.get("McCallEvent");
                    if (data instanceof LteS1Mme && mcCE instanceof McCallEvent) {
                        McCallEvent mc = (McCallEvent) mcCE;
                        if (mc.getAlertOffset() >= 0) {
                            return mc.getStartTime() + mc.getAlertOffset() - ((LteS1Mme) data).getStartTime();
                        } else {
                            return mc.getStartTime() + mc.getSetupOffset() - ((LteS1Mme) data).getStartTime();
                        }
                    }
                } else if (eventId == 3) {
                    return 0;
                }
            } else
                throwException();
        } catch (Exception e) {
            //logProcessing("getMOLong() " + e.getMessage());

        }
        return FALSE_CODE;
    }

    @Override
    public int getMONum() {
        try {
            // 3：    Extended Service Request
            UserCommon data = this.csfbSignal.get("LteS1Mme-3");
            // 主被叫判断
            UserCommon mot = this.csfbSignal.get("McCallEvent");
            if (mot instanceof McCallEvent) {
                McCallEvent mcCallEvent = (McCallEvent) mot;
                int eventId = mcCallEvent.getEventId();
                if (eventId == 1 && data != null) {
                    return 1;
                } else if (eventId == 3) {
                    return 0;
                }
                return 0;
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getMONum() " + e.getMessage());

        }
        return FALSE_CODE;
    }

    @Override
    public int getMTNum() {
        try {
            // 1：SGsAP-PAGING
            UserCommon data = this.csfbSignal.get("LteSGs-1");
            UserCommon mot = this.csfbSignal.get("McCallEvent");
            if (data instanceof LteSGs && mot instanceof McCallEvent) {
                McCallEvent mcCallEvent = (McCallEvent) mot;
                int eventId = mcCallEvent.getEventId();
                if (eventId == 1) {
                    return 0;
                } else if (eventId == 3 && ((LteSGs) data).getServiceIndicator() == 1) {
                    return 1;
                } else
                    return 0;
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getMTNum() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public int getMOSuccNum() {
        try {
            // 主被叫判断
            UserCommon mot = this.csfbSignal.get("McCallEvent");
            if (mot instanceof McCallEvent) {
                McCallEvent mcCallEvent = (McCallEvent) mot;
                int eventId = mcCallEvent.getEventId();
                if (eventId == 1 && mcCallEvent.getAlertOffset() > 0) {
                    return 1;
                } else if (eventId == 3) {
                    return 0;
                } else
                    return 0;
            } else
                throwException();
        } catch (Exception e) {
            logProcessing(" getMOSuccNum() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public int getMTSuccNum() {
        try {
            // 主被叫判断
            UserCommon mot = this.csfbSignal.get("McCallEvent");
            if (mot instanceof McCallEvent) {
                McCallEvent mcCallEvent = (McCallEvent) mot;
                int eventId = mcCallEvent.getEventId();
                if (eventId == 1) {
                    return 0;
                } else if (eventId == 3 && mcCallEvent.getAlertOffset() > 0) {
                    return 1;
                } else
                    return 0;
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getMTSuccNum() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public int getPagingNum() {
        try {
            // 1：SGsAP-PAGING
            UserCommon data = this.csfbSignal.get("LteSGs-1");
            LteSGs lteSGs;
            if (data instanceof LteSGs) {
                if (((LteSGs) data).getServiceIndicator() == 1)
                    return 1;
                else
                    return 0;
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getPagingNum() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public int getPagingSuccNum() {
        try {
            // 1：SGsAP-PAGING
            UserCommon data = this.csfbSignal.get("LteSGs-1");
            LteSGs lteSGs;
            if (data instanceof LteSGs) {
                lteSGs = (LteSGs) data;
                int procedureStatus = lteSGs.getProcedureStatus();
                if (lteSGs.getServiceIndicator() == 1 && (procedureStatus == 0 || procedureStatus == 255))
                    return 1;
                else
                    return 0;
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getPagingSuccNum() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public int getCallServiceNum() {
        try {
            // 主被叫判断
            UserCommon mot = this.csfbSignal.get("McCallEvent");
            if (mot instanceof McCallEvent) {
                return 1;
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getCallServiceNum() " + e.getMessage());
        }
        return 0;
    }

    @Override
    public int getCallServiceSuccNum() {
        try {
            // 主被叫判断
            UserCommon mot = this.csfbSignal.get("McCallEvent");
            if (mot instanceof McCallEvent) {
                McCallEvent mcCallEvent = (McCallEvent) mot;
                int eventId = mcCallEvent.getEventId();
                if (eventId == 1 && mcCallEvent.getCmrspoffset() > 0) {
                    return 1;
                } else if (eventId == 3 && mcCallEvent.getPagingResptype() > 0) {
                    return 1;
                }
                return 0;
            } else
                throwException();
        } catch (Exception e) {
            //logProcessing("getCallServiceSuccNum() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public int getFallbackNum() {
        try {
            // 主被叫判断
            UserCommon mot = this.csfbSignal.get("McCallEvent");
            if (mot instanceof McCallEvent) {
                McCallEvent mcCallEvent = (McCallEvent) mot;
                int eventId = mcCallEvent.getEventId();
                // 3：Extended Service Request
                if (eventId == 1 && this.csfbSignal.get("LteS1Mme-3") != null) {
                    return 1;
                }
                // 1：SGsAP-PAGING
                else if (eventId == 3 && this.csfbSignal.get("LteSGs-1") != null) {
                    return 1;
                }
                return 0;
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getFallbackNum() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public int getFallbackSuccNum() {
        try {
            // 主被叫判断
            UserCommon mot = this.csfbSignal.get("McCallEvent");
            if (mot instanceof McCallEvent) {
                McCallEvent mcCallEvent = (McCallEvent) mot;
                long motStartTime = mcCallEvent.getStartTime();
                int eventId = mcCallEvent.getEventId();
                if (eventId == 1) {
                    // 3：Extended Service Request
                    UserCommon data = this.csfbSignal.get("LteS1Mme-3");
                    if (data instanceof LteS1Mme) {
                        long dataStartTime = ((LteS1Mme) data).getStartTime();
                        if (motStartTime > 0 && dataStartTime > 0 && (motStartTime - dataStartTime <= 30000))
                            return 1;
                    } else
                        throwException();
                } else if (eventId == 3) {
                    // 1：SGsAP-PAGING
                    UserCommon data = this.csfbSignal.get("LteSGs-1");
                    if (data instanceof LteSGs) {
                        long dataStartTime = ((LteSGs) data).getStartTime();
                        if (motStartTime > 0 && dataStartTime > 0 && (motStartTime - dataStartTime <= 30000))
                            return 1;
                    } else
                        throwException();
                }
                return 0;
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getFallbackSuccNum() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public int getCallSetupNum() {
        try {
            // 主被叫判断
            UserCommon mot = this.csfbSignal.get("McCallEvent");
            if (mot instanceof McCallEvent) {
                McCallEvent mcCallEvent = (McCallEvent) mot;
                int eventId = mcCallEvent.getEventId();
                // 3：Extended Service Request
                if (eventId == 1 && mcCallEvent.getCmrspoffset() > 0) {
                    return 1;
                }
                // 1：SGsAP-PAGING
                else if (eventId == 3 && mcCallEvent.getPagingResptype() > 0) {
                    return 1;
                } else
                    return 0;
            } else
                throwException();
        } catch (Exception e) {
            //logProcessing("getCallSetupNum() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public int getCallSetupSuccNum() {
        try {
            UserCommon mot = this.csfbSignal.get("McCallEvent");
            if (mot instanceof McCallEvent) {
                McCallEvent mcCallEvent = (McCallEvent) mot;
                if (mcCallEvent.getSetupOffset() > 0) {
                    return 1;
                } else
                    return 0;
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getCallSetupSuccNum() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public int getCallAlertingNum() {
        try {
            UserCommon mot = this.csfbSignal.get("McCallEvent");
            if (mot instanceof McCallEvent) {
                McCallEvent mcCallEvent = (McCallEvent) mot;
                if (mcCallEvent.getSetupOffset() > 0) {
                    return 1;
                } else
                    return 0;
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getCallAlertingNum() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public int getCallAlertingSuccNum() {
        try {
            UserCommon mot = this.csfbSignal.get("McCallEvent");
            if (mot instanceof McCallEvent) {
                McCallEvent mcCallEvent = (McCallEvent) mot;
                if (mcCallEvent.getAlertOffset() > 0) {
                    return 1;
                } else
                    return 0;
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getCallAlertingSuccNum() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public int getRetInFiveSec() {
        if (csfb.getBackLong() < 5000) {
            return 1;
        } else return 0;
    }

    @Override
    public int getRetInTwoMin() {
        if (csfb.getBackLong() < 120000) {
            return 1;
        } else return 0;
    }

    @Override
    public int getCallConnectNum() {
        try {
            UserCommon mot = this.csfbSignal.get("McCallEvent");
            if (mot instanceof McCallEvent) {
                McCallEvent mcCallEvent = (McCallEvent) mot;
                if (mcCallEvent.getConnOffset() > 0) {
                    return 1;
                } else
                    return 0;
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getCallConnectNum() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public int getCallDropNum() {
        try {
            UserCommon mot = this.csfbSignal.get("McCallEvent");
            if (mot instanceof McCallEvent) {
                McCallEvent mcCallEvent = (McCallEvent) mot;
                if (mcCallEvent.getDisconnOffset() <= 0 && mcCallEvent.getClearOffset() > 0) {
                    return 1;
                } else
                    return 0;
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getCallDropNum() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public int getCallConnectSuccNum() {
        try {
            UserCommon mot = this.csfbSignal.get("McCallEvent");
            if (mot instanceof McCallEvent) {
                McCallEvent mcCallEvent = (McCallEvent) mot;
                if (mcCallEvent.getConnackoffset() > 0) {
                    return 1;
                } else
                    return 0;
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getCallConnectNum() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public int getLuFlag() {
        try {
            UserCommon mcLocationUpdate = this.csfbSignal.get("McLocationUpdate");
            if (mcLocationUpdate instanceof McLocationUpdate) {
                return 1;
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getLuFlag() " + e.getMessage());
        }
        return 0;
    }

    @Override
    public int getOlac() {
        try {
            UserCommon mcLocationUpdate = this.csfbSignal.get("McLocationUpdate");
            if (mcLocationUpdate instanceof McLocationUpdate) {
                return ((McLocationUpdate) mcLocationUpdate).getOlac();
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getOlac() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public int getOci() {
        try {
            UserCommon mcLocationUpdate = this.csfbSignal.get("McLocationUpdate");
            if (mcLocationUpdate instanceof McLocationUpdate) {
                return ((McLocationUpdate) mcLocationUpdate).getOci();
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getOci() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public int getDlac() {
        try {
            UserCommon mcLocationUpdate = this.csfbSignal.get("McLocationUpdate");
            if (mcLocationUpdate instanceof McLocationUpdate) {
                return ((McLocationUpdate) mcLocationUpdate).getDlac();
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getDlac() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public int getDci() {
        try {
            UserCommon mcLocationUpdate = this.csfbSignal.get("McLocationUpdate");
            if (mcLocationUpdate instanceof McLocationUpdate) {
                return ((McLocationUpdate) mcLocationUpdate).getDci();
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getDci() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public int getLuNum() {
        try {
            UserCommon mcLocationUpdate = this.csfbSignal.get("McLocationUpdate");
            if (mcLocationUpdate instanceof McLocationUpdate) {
                return 1;
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getLuNum() " + e.getMessage());
        }
        return 0;
    }

    @Override
    public int getLuSuccNum() {
        try {
            UserCommon mcLocationUpdate = this.csfbSignal.get("McLocationUpdate");
            if (mcLocationUpdate instanceof McLocationUpdate) {
                if (((McLocationUpdate) mcLocationUpdate).getResult() == 1)
                    return 1;
                else
                    return 0;
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getLuSuccNum() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public long getExtendServiceRequestTime() {
        try {
            // 3：Extended Service Request
            UserCommon data = this.csfbSignal.get("LteS1Mme-3");
            if (data instanceof LteS1Mme) {
                return ((LteS1Mme) data).getStartTime();
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getExtendServiceRequestTime() " + e.getMessage());
        }
        return FALSE_CODE;
    }


    @Override
    public long getMcPagingResponseTime() {
        try {
            UserCommon mot = this.csfbSignal.get("McPaging");
            if (mot instanceof McPaging) {
                return ((McPaging) mot).getRspdur();
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getMcPagingResponseTime() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public long getMcConnectTime() {
        try {
            UserCommon mot = this.csfbSignal.get("McCallEvent");
            if (mot instanceof McCallEvent) {
                McCallEvent mcCallEvent = (McCallEvent) mot;
                return mcCallEvent.getStartTime() + mcCallEvent.getConnOffset();
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getMcConnectTime() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public long getReleaseTime() {
        try {
            UserCommon mot = this.csfbSignal.get("McCallEvent");
            if (mot instanceof McCallEvent) {
                McCallEvent mcCallEvent = (McCallEvent) mot;
                return mcCallEvent.getStartTime() + mcCallEvent.getRelOffset();
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getReleaseTime() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public long getClearTime() {
        try {
            UserCommon mot = this.csfbSignal.get("McCallEvent");
            if (mot instanceof McCallEvent) {
                McCallEvent mcCallEvent = (McCallEvent) mot;
                return mcCallEvent.getStartTime() + mcCallEvent.getClearOffset();
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getClearTime() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public long getTAUTime() {
        try {
            // 5：    TAU
            UserCommon data = this.csfbSignal.get("LteS1Mme-5");
            if (data instanceof LteS1Mme) {
                return ((LteS1Mme) data).getStartTime();
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getExtendServiceRequestTime() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public String getCallingNum() {
        try {
            UserCommon mot = this.csfbSignal.get("McCallEvent");
            if (mot instanceof McCallEvent) {
                return ((McCallEvent) mot).getCallingNum();
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getCallingNum() " + e.getMessage());
        }
        return null;
    }

    @Override
    public String getCalledNum() {
        try {
            UserCommon mot = this.csfbSignal.get("McCallEvent");
            if (mot instanceof McCallEvent) {
                return ((McCallEvent) mot).getCalledNum();
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getCalledNum() " + e.getMessage());
        }
        return null;
    }

    @Override
    public int getReturnSuccNum() {
        try {
            // 主被叫判断
            UserCommon mot = this.csfbSignal.get("McCallEvent");
            McPaging mcPaging = (McPaging) this.csfbSignal.get("McPaging");
            if (mot instanceof McCallEvent && mcPaging instanceof McPaging) {
                McCallEvent mcCallEvent = (McCallEvent) mot;
                int eventId = mcCallEvent.getEventId();
                // 3：Extended Service Request
                if (eventId == 1 && mcCallEvent.getCmrspoffset() > 0) {
                    return 1;
                }
                // 1：SGsAP-PAGING
                else if (eventId == 3 && mcPaging.getPagingResptype() > 0) {
                    return 1;
                } else
                    return 0;
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getReturnSuccNum() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public int getSGsPagingNum() {
        try {
            // 主被叫判断
            UserCommon mcCE = this.csfbSignal.get("McCallEvent");
            int eventId;
            if (mcCE instanceof McCallEvent) {
                eventId = ((McCallEvent) mcCE).getEventId();
                if (eventId == 3) {
                    // 1：SGsAP-PAGING
                    UserCommon data = this.csfbSignal.get("LteSGs-1");
                    if (data instanceof LteSGs) {
                        if (((LteSGs) data).getServiceIndicator() == 1)
                            return 1;
                        else
                            return 0;
                    } else
                        throwException();
                }
            } else
                throwException();

        } catch (Exception e) {
            logProcessing("getSGsPagingNum() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public int getSGsPagingSuccNum() {
        try {
            // 主被叫判断
            UserCommon mcCE = this.csfbSignal.get("McCallEvent");
            int eventId;
            if (mcCE instanceof McCallEvent) {
                eventId = ((McCallEvent) mcCE).getEventId();
                if (eventId == 3) {
                    // 1：SGsAP-PAGING
                    UserCommon data = this.csfbSignal.get("LteSGs-1");
                    if (data instanceof LteSGs) {
                        LteSGs lteSGs = ((LteSGs) data);
                        int status = lteSGs.getProcedureStatus();
                        if (lteSGs.getServiceIndicator() == 1 && (status == 0 || status == 255))
                            return 1;
                        else
                            return 0;
                    } else
                        throwException();
                }
            }
        } catch (Exception e) {
            logProcessing("getSGsPagingSuccNum() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public int getSGsServiceNum() {
        try {
            // 主被叫判断
            UserCommon mcCE = this.csfbSignal.get("McCallEvent");
            int eventId;
            if (mcCE instanceof McCallEvent) {
                eventId = ((McCallEvent) mcCE).getEventId();
                if (eventId == 3) {
                    // 2：SGsAP-SERVICE-REQUEST
                    UserCommon data = this.csfbSignal.get("LteSGs-2");
                    if (data instanceof LteSGs) {
                        if (((LteSGs) data).getServiceIndicator() == 1)
                            return 1;
                        else
                            return 0;
                    } else
                        throwException();
                }
            }
        } catch (Exception e) {
            logProcessing("getSGsServiceNum() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public int getSGsServiceSuccNum() {
        try {
            // 主被叫判断
            UserCommon mcCE = this.csfbSignal.get("McCallEvent");
            int eventId;
            if (mcCE instanceof McCallEvent) {
                eventId = ((McCallEvent) mcCE).getEventId();
                if (eventId == 3) {
                    // 2：SGsAP-SERVICE-REQUEST
                    UserCommon data = this.csfbSignal.get("LteSGs-2");
                    if (data instanceof LteSGs) {
                        LteSGs lteSGs = ((LteSGs) data);
                        int status = lteSGs.getProcedureStatus();
                        if (lteSGs.getServiceIndicator() == 1 && (status == 0 || status == 255))
                            return 1;
                        else
                            return 0;
                    } else
                        throwException();
                }
            }
        } catch (Exception e) {
            logProcessing("getSGsServiceSuccNum() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public int getS1PagingNum() {
        try {
            // 4：    Paging
            UserCommon data = this.csfbSignal.get("LteS1Mme-4");
            if (data instanceof LteS1Mme) {
                return 1;
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getS1PagingNum() " + e.getMessage());
        }
        return 0;
    }

    @Override
    public int getS1PagingSuccNum() {
        try {
            // 4：    Paging
            UserCommon data = this.csfbSignal.get("LteS1Mme-4");
            if (data instanceof LteS1Mme) {
                int status = ((LteS1Mme) data).getProcedureStatus();
                if (status == 0 || status == 255)
                    return 1;
                else
                    return 0;
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getS1PagingSuccNum() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public int getS1ServiceNum() {
        try {
            // 3：    Extended Service Request
            UserCommon data = this.csfbSignal.get("LteS1Mme-3");
            if (data instanceof LteS1Mme) {
                return 1;
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getS1ServiceNum() " + e.getMessage());
        }
        return 0;
    }

    @Override
    public int getS1ServiceSuccNum() {
        try {
            // 3：    Extended Service Request
            UserCommon data = this.csfbSignal.get("LteS1Mme-3");
            if (data instanceof LteS1Mme) {
                int status = ((LteS1Mme) data).getProcedureStatus();
                if (status == 0 || status == 255)
                    return 1;
                else
                    return 0;
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getS1ServiceSuccNum() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public int getS1ModifyNum() {
        try {
            // 19：UE context modification
            UserCommon data = this.csfbSignal.get("LteS1Mme-19");
            if (data instanceof LteS1Mme) {
                return 1;
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getS1ModifyNum() " + e.getMessage());
        }
        return 0;
    }

    @Override
    public int getS1ModifySuccNum() {
        try {
            // 19：UE context modification
            UserCommon data = this.csfbSignal.get("LteS1Mme-19");
            if (data instanceof LteS1Mme) {
                int status = ((LteS1Mme) data).getProcedureStatus();
                if (status == 0 || status == 255)
                    return 1;
                else
                    return 0;
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getS1ModifySuccNum() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public int getS1ReleaseNum() {
        try {
            // 20：UE context release
            UserCommon data = this.csfbSignal.get("LteS1Mme-20");
            if (data instanceof LteS1Mme) {
                return 1;
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getS1ReleaseNum() " + e.getMessage());
        }
        return 0;
    }

    @Override
    public int getS1ReleaseSuccNum() {
        try {
            // 20：UE context release
            UserCommon data = this.csfbSignal.get("LteS1Mme-20");
            if (data instanceof LteS1Mme) {
                int status = ((LteS1Mme) data).getProcedureStatus();
                if (status == 0 || status == 255)
                    return 1;
                else
                    return 0;
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getS1ReleaseSuccNum() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public long getAssignTime() {
        try {
            UserCommon mot = this.csfbSignal.get("McCallEvent");
            if (mot instanceof McCallEvent) {
                McCallEvent mcCallEvent = ((McCallEvent) mot);
                return mcCallEvent.getStartTime() + mcCallEvent.getAssreqOffset();
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getAssignTime() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public int getAssignReqNum() {
        try {
            UserCommon mot = this.csfbSignal.get("McCallEvent");
            if (mot instanceof McCallEvent) {
                if (((McCallEvent) mot).getAssreqOffset() > 0)
                    return 1;
                else
                    return 0;
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getAssignReqNum() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public int getAssignSuccNum() {
        try {
            UserCommon mot = this.csfbSignal.get("McCallEvent");
            if (mot instanceof McCallEvent) {
                McCallEvent mcCallEvent = ((McCallEvent) mot);
                if (mcCallEvent.getAssreqOffset() > 0 && mcCallEvent.getEventResult() == 0)
                    return 1;
                else
                    return 0;
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getAssignSuccNum() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public long getAuthTime() {
        try {
            UserCommon mot = this.csfbSignal.get("McCallEvent");
            if (mot instanceof McCallEvent) {
                McCallEvent mcCallEvent = ((McCallEvent) mot);
                return mcCallEvent.getStartTime() + mcCallEvent.getAuthreqOffset();
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getAuthTime() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public int getAuthReqNum() {
        try {
            UserCommon mot = this.csfbSignal.get("McCallEvent");
            if (mot instanceof McCallEvent) {
                if (((McCallEvent) mot).getAuthreqOffset() > 0)
                    return 1;
                else
                    return 0;
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getAuthReqNum() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public int getAuthSuccNum() {
        try {
            UserCommon mot = this.csfbSignal.get("McCallEvent");
            if (mot instanceof McCallEvent) {
                McCallEvent mcCallEvent = ((McCallEvent) mot);
                if (mcCallEvent.getAuthreqOffset() > 0 && mcCallEvent.getEventResult() == 0)
                    return 1;
                else
                    return 0;
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getAuthSuccNum() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public long getCallServiceLong() {
        try {
            // 20：UE context release
            // 3：    Extended Service Request
            UserCommon data1 = this.csfbSignal.get("LteS1Mme-20");
            UserCommon data2 = this.csfbSignal.get("LteS1Mme-3");
            if (data1 instanceof LteS1Mme && data2 instanceof LteS1Mme) {
                return ((LteS1Mme) data1).getStartTime() - ((LteS1Mme) data2).getStartTime();
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getCallServiceLong() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public long getCallSetupLong() {
        try {
            UserCommon mot = this.csfbSignal.get("McCallEvent");
            if (mot instanceof McCallEvent) {
                McCallEvent mcCallEvent = ((McCallEvent) mot);
                return mcCallEvent.getStartTime() + mcCallEvent.getSetupOffset();
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getCallSetupLong() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public long getCallAlertingLong() {
        try {
            UserCommon mot = this.csfbSignal.get("McCallEvent");
            if (mot instanceof McCallEvent) {
                McCallEvent mcCallEvent = ((McCallEvent) mot);
                int alertOffset = mcCallEvent.getAlertOffset();
                int setupOffset = mcCallEvent.getSetupOffset();
//                if (alertOffset > 0 && setupOffset > 0) {
                return mcCallEvent.getAlertOffset() - mcCallEvent.getSetupOffset();
//                } else throwException();
            } else
                throwException();
        } catch (Exception e) {
            logProcessing("getCallAlertingLong() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public int getFirstErrorXDR() {
        for (String signalName : csfbSignal.keySet()
                ) {
            UserCommon signal = csfbSignal.get(signalName);
            CsfbExceptionObject cfbExceptionObject = exceptionCode.getExceptionObject(signal);
            int exceptionCode = cfbExceptionObject.getExceptionCode();
            if (exceptionCode != 0) {
                this.csfbExceptionObject = cfbExceptionObject;
                return exceptionCode;
            }
        }
        return FALSE_CODE;
    }

    @Override
    public int getCauseCode() {
        int causeCode = csfbExceptionObject.getCauseCode();
        if (causeCode != 0)
            return csfbExceptionObject.getCauseCode();
        return FALSE_CODE;
    }

    @Override
    public int getState() {
        return 0;
    }

    @Override
    public int getInterruptXdr() {
        try {
            CsfbExceptionObject interruptXdrObject = interruptXdrCode
                    .getInterruptXdrCode(csfbSignal.get(csfbSignal.keySet().toArray()[csfbSignal.size() - 1]));
            int exceptionCode = interruptXdrObject.getExceptionCode();
            if (exceptionCode == csfbExceptionCode.getTAU_Event())
                return FALSE_CODE;
            return interruptXdrObject.getExceptionCode();
        } catch (Exception e) {
            logProcessing("getInterruptXdr() " + e.getMessage());
        }
        return FALSE_CODE;
    }

    @Override
    public String getXdrRowKeyList() {
        return null;
    }
}
