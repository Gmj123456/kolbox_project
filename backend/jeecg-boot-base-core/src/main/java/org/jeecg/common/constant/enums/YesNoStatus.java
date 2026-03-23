package org.jeecg.common.constant.enums;

public enum YesNoStatus {
    YES(1, "是"),
    NO(0, "否"),
    Exception(-1, "异常"),
    CANCEL(-2, "取消"),
    CANCELSEND(2, "取消发送"),
    FINISH(3, "完成");


    private final int code;
    private final String info;

    YesNoStatus(int code, String info) {
        this.code = code;
        this.info = info;
    }

    public int getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }

    public String getCodeString() {
        return Integer.toString(code);
    }
}
