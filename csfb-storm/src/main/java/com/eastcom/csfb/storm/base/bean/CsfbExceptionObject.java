package com.eastcom.csfb.storm.base.bean;

/**
 * Created by linghang.kong on 2016/6/7.
 */
public class CsfbExceptionObject {
    // 默认异常码为0
    private final int defaultCode = 0;
    private int exceptionCode;
    private int causeCode;

    public CsfbExceptionObject() {
        this.exceptionCode = defaultCode;
        this.causeCode = defaultCode;
    }

    public int getExceptionCode() {
        return exceptionCode;
    }

    public void setExceptionCode(int exceptionCode) {
        this.exceptionCode = exceptionCode;
    }

    public int getCauseCode() {
        return causeCode;
    }

    public void setCauseCode(int causeCode) {
        this.causeCode = causeCode;
    }

    public void clean() {
        setExceptionCode(defaultCode);
        setCauseCode(defaultCode);
    }
}
