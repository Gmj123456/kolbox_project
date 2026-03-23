package org.jeecg.common.constant.enums;

public enum UserType {
    PLATFORM(0, "平台"),
    SELLER(1, "商家"),
    BUYERS(2, "网红"),
    BUSINESS_COUNSELOR(3, "商家顾问"),
    CELEBRITY_COUNSELOR(4, "网红顾问"),
    BUSINESS_PERSONNEL(10, "商务人员");


    private final int code;
    private final String info;

    UserType(int code, String info) {
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
