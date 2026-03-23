package org.jeecg.common.constant.enums;

/**
 * 份数,在此用作list所分成的份数
 * @Author: zhoushen
 * */
public enum NumberOfCopies {
    /**
     * 份数,默认5
     * */
    DEFAULT_NUMBER_OF_COPIES(5, "默认份数");

    private final int code;
    private final String info;

    NumberOfCopies(int code, String info) {
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
