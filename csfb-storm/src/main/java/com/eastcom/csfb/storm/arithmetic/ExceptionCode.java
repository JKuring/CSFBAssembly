package com.eastcom.csfb.storm.arithmetic;

import com.eastcom.csfb.data.UserCommon;
import com.eastcom.csfb.data.ltesignal.LteS1Mme;
import com.eastcom.csfb.data.ltesignal.LteSGs;
import com.eastcom.csfb.data.mc.McCallEvent;
import com.eastcom.csfb.storm.base.bean.CsfbExceptionCode;
import com.eastcom.csfb.storm.base.bean.CsfbExceptionObject;

/**
 * Created by linghang.kong on 2016/6/7.
 */
public class ExceptionCode {

    private CsfbExceptionCode csfbExceptionCode = new CsfbExceptionCode();
    private CsfbExceptionObject csfbExceptionObject = new CsfbExceptionObject();


    /**
     * Get exception object form the UserCommon object.
     *
     * @param signal
     * @return CsfbExceptionObject
     */
    public CsfbExceptionObject getExceptionObject(UserCommon signal) {
        csfbExceptionObject.clean();
        if (signal instanceof LteS1Mme) {
            getS1MmeSignalExceptionCode(signal);
        } else if (signal instanceof LteSGs) {
            getSGsSignalExceptionCode(signal);
        } else if (signal instanceof McCallEvent) {
            getMcCallEventSignalExceptionCode(signal);
        }
        return csfbExceptionObject;
    }

    private void getS1MmeSignalExceptionCode(UserCommon signal) {
        LteS1Mme lteS1Mme = (LteS1Mme) signal;
        if (lteS1Mme.getProcedureStatus() > 0) {
            switch (lteS1Mme.getProcedureType()) {
                // 4：Paging
                case 4:
                    csfbExceptionObject.setExceptionCode(csfbExceptionCode.getS1AP_Paging());
                    csfbExceptionObject.setCauseCode(lteS1Mme.getFailCauseCode());
                    break;
                //  3：Extended Service Request
                case 3:
                    csfbExceptionObject.setExceptionCode(csfbExceptionCode.getExtended_Service_Request());
                    csfbExceptionObject.setCauseCode(lteS1Mme.getFailCauseCode());
                    break;
                // 18：Initial context setup
                case 18:
                    csfbExceptionObject.setExceptionCode(csfbExceptionCode.getS1AP_Initial_Context_Setup());
                    csfbExceptionObject.setCauseCode(lteS1Mme.getFailCauseCode());
                    break;
                // 19：UE context modification
                case 19:
                    csfbExceptionObject.setExceptionCode(csfbExceptionCode.getS1AP_UE_Context_Modification());
                    csfbExceptionObject.setCauseCode(lteS1Mme.getFailCauseCode());
                    break;
                // 20：UE context release
                case 20:
                    csfbExceptionObject.setExceptionCode(csfbExceptionCode.getS1AP_UE_Context_Release());
                    csfbExceptionObject.setCauseCode(lteS1Mme.getFailCauseCode());
                    break;
                // 1：Attach
                case 1:
                    csfbExceptionObject.setExceptionCode(csfbExceptionCode.getAttach_Event());
                    csfbExceptionObject.setCauseCode(lteS1Mme.getFailCauseCode());
                    break;
                // 5：TAU
                case 5:
                    csfbExceptionObject.setExceptionCode(csfbExceptionCode.getTAU_Event());
                    csfbExceptionObject.setCauseCode(lteS1Mme.getFailCauseCode());
                    break;
            }
        }

    }

    private void getSGsSignalExceptionCode(UserCommon signal) {
        LteSGs lteSGs = (LteSGs) signal;
        if (lteSGs.getProcedureStatus() > 0) {
            switch (lteSGs.getProcedureType()) {
                // 1：SGsAP-PAGING
                case 1:
                    csfbExceptionObject.setExceptionCode(csfbExceptionCode.getSGs_Paging_Request());
                    csfbExceptionObject.setCauseCode(lteSGs.getSgsCause());
                    break;
                // 2：SGsAP-SERVICE-REQUEST
                case 2:
                    csfbExceptionObject.setExceptionCode(csfbExceptionCode.getSGs_Service_Request());
                    csfbExceptionObject.setCauseCode(lteSGs.getSgsCause());
                    break;
                // 16：SGsAP-UE-UNREACHABLE
                case 16:
                    csfbExceptionObject.setExceptionCode(csfbExceptionCode.getSGs_UE_Unreachable());
                    csfbExceptionObject.setCauseCode(lteSGs.getSgsCause());
                    break;
            }
        }
    }

    private void getMcCallEventSignalExceptionCode(UserCommon signal) {
        McCallEvent mcCallEvent = (McCallEvent) signal;
        if (mcCallEvent.getEventResult() > 0 && mcCallEvent.getEventCause() > 0) {
            int eventId = mcCallEvent.getEventId();
            switch (mcCallEvent.getEventResult()) {
                case 1:
                    if ((eventId == 3 || eventId == 5) && mcCallEvent.getEventId() == 1) {
                        csfbExceptionObject.setExceptionCode(csfbExceptionCode.getMc_Paging_Response());
                        csfbExceptionObject.setCauseCode(mcCallEvent.getEventCause());
                        break;
                    } else break;
                case 2:
                    if ((eventId == 1 || eventId == 2 || eventId == 4) && mcCallEvent.getEventId() == 1) {
                        csfbExceptionObject.setExceptionCode(csfbExceptionCode.getMc_CM_Service_Request());
                        csfbExceptionObject.setCauseCode(mcCallEvent.getEventCause());
                        break;
                    } else break;
                case 3:
                    csfbExceptionObject.setExceptionCode(csfbExceptionCode.getMc_Authentication_Request());
                    csfbExceptionObject.setCauseCode(mcCallEvent.getEventCause());
                    break;
                case 4:
                    csfbExceptionObject.setExceptionCode(csfbExceptionCode.getMc_Cipher_Mode_Command());
                    csfbExceptionObject.setCauseCode(mcCallEvent.getEventCause());
                    break;
                case 6:
                    csfbExceptionObject.setExceptionCode(csfbExceptionCode.getMc_Assignment_Request());
                    csfbExceptionObject.setCauseCode(mcCallEvent.getEventCause());
                    break;
                case 8:
                    csfbExceptionObject.setExceptionCode(csfbExceptionCode.getMc_Disconnect());
                    csfbExceptionObject.setCauseCode(mcCallEvent.getEventCause());
                    break;
                case 9:
                    csfbExceptionObject.setExceptionCode(csfbExceptionCode.getMc_Release());
                    csfbExceptionObject.setCauseCode(mcCallEvent.getEventCause());
                    break;
                case 10:
                    csfbExceptionObject.setExceptionCode(csfbExceptionCode.getMc_Clear_Request());
                    csfbExceptionObject.setCauseCode(mcCallEvent.getEventCause());
                    break;
                default:
                    if ((eventId == 3 || eventId == 5) && mcCallEvent.getRspdur() == 0 && mcCallEvent.getEventId() == 3) {
                        csfbExceptionObject.setExceptionCode(csfbExceptionCode.getMc_Paging_Response());
                        csfbExceptionObject.setCauseCode(mcCallEvent.getEventCause());
                        break;
                    }
                    if (mcCallEvent.getSetupOffset() == 0) {
                        csfbExceptionObject.setExceptionCode(csfbExceptionCode.getMc_CC_Setup());
                        csfbExceptionObject.setCauseCode(mcCallEvent.getEventCause());
                        break;
                    }
                    if (mcCallEvent.getCpgoffset() == 0) {
                        csfbExceptionObject.setExceptionCode(csfbExceptionCode.getMc_CC_Call_Proceeding());
                        csfbExceptionObject.setCauseCode(mcCallEvent.getEventCause());
                        break;
                    }
                    if (mcCallEvent.getCpgrspoffset() == 0) {
                        csfbExceptionObject.setExceptionCode(csfbExceptionCode.getMc_CC_Call_Confirmed());
                        csfbExceptionObject.setCauseCode(mcCallEvent.getEventCause());
                        break;
                    }
                    if (mcCallEvent.getAlertOffset() == 0) {
                        csfbExceptionObject.setExceptionCode(csfbExceptionCode.getMc_Alerting());
                        csfbExceptionObject.setCauseCode(mcCallEvent.getEventCause());
                        break;
                    }
                    if (mcCallEvent.getConnOffset() == 0 || mcCallEvent.getConnackoffset() == 0) {
                        csfbExceptionObject.setExceptionCode(csfbExceptionCode.getMc_Connect());
                        csfbExceptionObject.setCauseCode(mcCallEvent.getEventCause());
                        break;
                    }
            }
        }
    }
}
