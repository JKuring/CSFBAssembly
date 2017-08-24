package com.eastcom.csfb.data.mc.parser;

import com.eastcom.csfb.data.CSVParser;
import com.eastcom.csfb.data.mc.McCallEvent;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.trim;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

public class McCallParser extends CSVParser<McCallEvent> {

    @Override
    public int getRequiredFields() {
        return 81;
    }

    @Override
    public int getTotalFields() {
        return 81;
    }

    @Override
    public McCallEvent doParse(List<String> csvArr) {
        McCallEvent mcCallEvent = new McCallEvent();
        mcCallEvent.setStartTime(extractTime(csvArr.get(0)));
        mcCallEvent.setEndTime(extractTime(csvArr.get(1)));
        mcCallEvent.setEventId(toInt(csvArr.get(3)));
        mcCallEvent.setLac(toInt(csvArr.get(9)));
        mcCallEvent.setCi(toInt(csvArr.get(10)));
        mcCallEvent.setResult(toInt(csvArr.get(17)));
        mcCallEvent.setCallingNum(trim(csvArr.get(21)));
        mcCallEvent.setCalledNum(trim(csvArr.get(22)));
        mcCallEvent.setCallingimsi(trim(csvArr.get(24)));
        mcCallEvent.setCalledimsi(trim(csvArr.get(25)));
        mcCallEvent.setCallingTmsi(trim(csvArr.get(29)));
        mcCallEvent.setCalledTmsi(trim(csvArr.get(30)));
        mcCallEvent.setEventCause(toInt(csvArr.get(37)));
        mcCallEvent.setEventResult(toInt(csvArr.get(39)));
        mcCallEvent.setAssgCause(toInt(csvArr.get(41)));
        mcCallEvent.setDisconCause(toInt(csvArr.get(42)));
        mcCallEvent.setClearCause(toInt(csvArr.get(43)));
        mcCallEvent.setRelCauseValue(toInt(csvArr.get(44)));
        mcCallEvent.setSetupResult(toInt(csvArr.get(46)));
        mcCallEvent.setCmreqoffset(toInt(csvArr.get(51)));
        mcCallEvent.setCmrspoffset(toInt(csvArr.get(52)));
        mcCallEvent.setAuthreqOffset(toInt(csvArr.get(53)));
        mcCallEvent.setAssreqOffset(toInt(csvArr.get(59)));
        mcCallEvent.setSetupOffset(toInt(csvArr.get(61)));
        mcCallEvent.setCpgoffset(toInt(csvArr.get(62)));
        mcCallEvent.setCpgrspoffset(toInt(csvArr.get(63)));
        mcCallEvent.setConnOffset(toInt(csvArr.get(64)));
        mcCallEvent.setConnackoffset(toInt(csvArr.get(65)));
        mcCallEvent.setDisconnOffset(toInt(csvArr.get(67)));
        mcCallEvent.setAlertOffset(toInt(csvArr.get(70)));
        mcCallEvent.setRelOffset(toInt(csvArr.get(71)));
        mcCallEvent.setClearOffset(toInt(csvArr.get(74)));
        mcCallEvent.setPagingResptype(toInt(csvArr.get(78)));
        mcCallEvent.setRspdur(toInt(csvArr.get(80)));

        return mcCallEvent;

    }

}
