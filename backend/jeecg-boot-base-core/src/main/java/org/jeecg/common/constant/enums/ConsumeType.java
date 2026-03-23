package org.jeecg.common.constant.enums;

public enum ConsumeType {
    GRADE(0, "等级"),
    EMAILACCOUNTCOUNT(1, "邮箱个数"),
    EMAILCOUNT(2, "邮件个数"),
    UPGRADE(3, "升级"),
    RENEW(4, "续费"),
    COUPON(5, "优惠券");


    private final int code;
    private final String info;

    ConsumeType(int code, String info) {
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
