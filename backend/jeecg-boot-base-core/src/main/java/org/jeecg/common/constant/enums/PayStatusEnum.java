package org.jeecg.common.constant.enums;

public enum PayStatusEnum {
    DOWITHOUTPAY(-1, "无需支付"),
    TOBEPAID(0, "待支付"),
    DOWNPAYMENT(10, "部分支付"),
    INTENTIONMENT(11, "已付意向金"),
    FINALPAYMENT(12, "已付尾款"),
    PAIDINFULL(20, "已付全款");

    private final int code;
    private final String info;

    PayStatusEnum(int code, String info) {
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
