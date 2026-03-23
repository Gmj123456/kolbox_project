package org.jeecg.common.constant.enums;

public enum GoodsCategoryType {
    ISROOT(0, "一级类目"),
    ISCHILD(0, "子类目");


    private final int code;
    private final String info;

    GoodsCategoryType(int code, String info) {
        this.code = code;
        this.info = info;
    }

    public int getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }

    public String getCodeString() {return Integer.toString(code);}
}
