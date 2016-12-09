package com.eastcom.csfb.storm.base.common;

import com.eastcom.csfb.storm.base.bean.Csfb;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Created by linghang.kong on 2016/5/26.
 */
public class CsfbSerializer extends Serializer<Csfb> {

    @Override
    public void write(Kryo kryo, Output output, Csfb object) {
        /*
        output.writeLong(object.getBeginTime(),true);
        output.writeLong(object.getEndTime(),true);
        output.writeInt(object.getCallType(),true);
        output.writeInt(object.getAlertingFlag(),true);
        output.writeInt(object.getTAC(),true);
        output.writeInt(object.getECI(),true);
        output.writeInt(object.getLAC(),true);
        output.writeInt(object.getCI(),true);
        output.writeString(object.getIMSI());
        output.writeString(object.getIMEI());
        output.writeString(object.getIMEI_TAC());
        output.writeString(object.getMSC_ID());
        output.writeString(object.getMME_ID());
        output.writeString(object.getENB_ID());
        output.writeLong(object.getCMServiceRequestTime(),true);
        output.writeLong(object.getPagingRequestTime(),true);
        output.writeLong(object.getUEContextReleaseTime(),true);
        output.writeLong(object.getAlertingTime(),true);
        output.writeLong(object.getMcCallEndTime(),true);
        output.writeLong(object.getBackTime(),true);
        output.writeLong(object.getPagingLong(),true);
        output.writeLong(object.getFallbackLong(),true);
        output.writeLong(object.getBackLong(),true);
        output.writeLong(object.getE2ELong(),true);
        output.writeLong(object.getMTLong(),true);
        output.writeLong(object.getMOLong(),true);
        output.writeInt(object.getMONum();
        output.writeInt(object.getMTNum();
        output.writeInt(object.getMOSuccNum();
        output.writeInt(object.getMTSuccNum();
        output.writeInt(object.getPagingNum();
        output.writeInt(object.getPagingSuccNum();
        output.writeInt(object.getCallServiceNum();
        output.writeInt(object.getCallServiceSuccNum();
        output.writeInt(object.getFallbackNum();
        output.writeInt(object.getFallbackSuccNum();
        output.writeInt(object.getCallSetupNum();
        output.writeInt(object.getCallSetupSuccNum();
        output.writeInt(object.getCallAlertingNum();
        output.writeInt(object.getCallAlertingSuccNum();
        output.writeInt(object.getRetInFiveSec();
        output.writeInt(object.getRetInTwoMin();
        output.writeInt(object.getCallConnectNum();
        output.writeInt(object.getCallDropNum();
        output.writeInt(object.getCallConnectSuccNum();
        output.writeInt(object.getLuFlag();
        output.writeInt(object.getOlac();
        output.writeInt(object.getOci();
        output.writeInt(object.getDlac();
        output.writeInt(object.getDci();
        output.writeInt(object.getLuNum();
        output.writeInt(object.getLuSuccNum();
        object.getExtendServiceRequestTime();
        object.getMcPagingResponseTime();
        object.getMcConnectTime();
        object.getReleaseTime();
        object.getClearTime();
        object.getTAUTime();
        object.getCallingNum();
        object.getCalledNum();
        object.getReturnSuccNum();
        object.getSGsPagingNum();
        object.getSGsPagingSuccNum();
        object.getSGsServiceNum();
        object.getSGsServiceSuccNum();
        object.getS1PagingNum();
        object.getS1PagingSuccNum();
        object.getS1ServiceNum();
        object.getS1ServiceSuccNum();
        object.getS1ModifyNum();
        object.getS1ModifySuccNum();
        object.getS1ReleaseNum();
        object.getS1ReleaseSuccNum();
        object.getAssignTime();
        object.getAssignReqNum();
        object.getAssignSuccNum();
        object.getAuthTime();
        object.getAuthReqNum();
        object.getAuthSuccNum();
        object.getCallServiceLong();
        object.getCallSetupLong();
        object.getCallAlertingLong();
        object.getFirstErrorXDR();
        object.getCauseCode();
        object.getState();
        object.getInterruptXdr();
        object.getXdrRowKeyList();
        */
    }

    @Override
    public Csfb read(Kryo kryo, Input input, Class type) {
        return null;
    }
}
