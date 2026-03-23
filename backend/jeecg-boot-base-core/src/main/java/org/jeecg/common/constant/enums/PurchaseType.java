package org.jeecg.common.constant.enums;

public enum PurchaseType {
    INVITE(0, "邀请"),
    BUY(1, "购买"),
    COUPON(2, "优惠券");


    private final int code;
    private final String info;

    PurchaseType(int code, String info) {
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
