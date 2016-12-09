package com.eastcom.csfb.storm.arithmetic;

import com.eastcom.csfb.data.UserCommon;
import com.eastcom.csfb.data.ltesignal.LteS1Mme;
import com.eastcom.csfb.data.ltesignal.LteSGs;
import com.eastcom.csfb.data.mc.McCallEvent;
import com.eastcom.csfb.storm.base.bean.CsfbExceptionCode;
import com.eastcom.csfb.storm.base.bean.CsfbExceptionObject;

/**
 * Created by linghang.kong on 2016/6/8.
 */
public class InterruptXdrCode {

    private CsfbExceptionCode csfbExceptionCode = new CsfbExceptionCode();
    private CsfbExceptionObject csfbExceptionObject = new CsfbExceptionObject();


    public CsfbExceptionObject getInterruptXdrCode(UserCommon signal) {
        csfbExceptionObject.clean();
        if (signal instanceof LteS1Mme) {
            getS1MmeInterruptXdrCode(signal);
        } else if (signal instanceof LteSGs) {
            getSGsInterruptXdrCode(signal);
        } else if (signal instanceof McCallEvent) {
            getMcCallEventInterruptXdrCode(signal);
        }
        return csfbExceptionObject;
    }

    public void getS1MmeInterruptXdrCode(UserCommon signal) {
        LteS1Mme lteS1Mme = (LteS1Mme) signal;
        switch (lteS1Mme.getProcedureType()) {
            // 4：Paging
            case 4:
                csfbExceptionObject.setExceptionCode(csfbExceptionCode.getS1AP_Paging());
                break;
            //  3：Extended Service Request
            case 3:
                csfbExceptionObject.setExceptionCode(csfbExceptionCode.getExtended_Service_Request());
                break;
            // 18：Initial context setup
            case 18:
                csfbExceptionObject.setExceptionCode(csfbExceptionCode.getS1AP_Initial_Context_Setup());
                break;
            // 19：UE context modification
            case 19:
                csfbExceptionObject.setExceptionCode(csfbExceptionCode.getS1AP_UE_Context_Modification());
                break;
            // 20：UE context release
            case 20:
                csfbExceptionObject.setExceptionCode(csfbExceptionCode.getS1AP_UE_Context_Release());
                break;
            // 1：Attach
            case 1:
                csfbExceptionObject.setExceptionCode(csfbExceptionCode.getAttach_Event());
                break;
            // 5：TAU
            case 5:
                csfbExceptionObject.setExceptionCode(csfbExceptionCode.getTAU_Event());
                break;
        }

    }

    public void getSGsInterruptXdrCode(UserCommon signal) {
        LteSGs lteSGs = (LteSGs) signal;
        switch (lteSGs.getProcedureType()) {
            // 1：SGsAP-PAGING
            case 1:
                csfbExceptionObject.setExceptionCode(csfbExceptionCode.getSGs_Paging_Request());
                break;
            // 2：SGsAP-SERVICE-REQUEST
            case 2:
                csfbExceptionObject.setExceptionCode(csfbExceptionCode.getSGs_Service_Request());
                break;
            // 16：SGsAP-UE-UNREACHABLE
            case 16:
                csfbExceptionObject.setExceptionCode(csfbExceptionCode.getSGs_UE_Unreachable());
                break;
        }
    }

    private void getMcCallEventInterruptXdrCode(UserCommon signal) {
        McCallEvent mcCallEvent = (McCallEvent) signal;
        switch (mcCallEvent.getEventId()) {
            case 1:
                csfbExceptionObject.setExceptionCode(csfbExceptionCode.getMc_Paging_Response());
            case 3:
                csfbExceptionObject.setExceptionCode(csfbExceptionCode.getMc_CM_Service_Request());
        }
    }
}
