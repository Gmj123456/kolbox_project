package org.jeecg.common.constant.enums;

/**
 * @author Administrator
 */

public enum ProcessingStatus {
    DONE(0, "DONE"),
    CANCEL(1, "CANCEL"),
    FATAL(2, "FATAL");

    private final int code;
    private final String info;

    ProcessingStatus(int code, String info) {
        this.code = code;
        this.info = info;
    }

    public int getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }
}
