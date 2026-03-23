package org.jeecg.common.constant.enums;

public enum PayWay {
    ZFB(0, "支付宝"),
    WX(1, "微信"),
    OVER(2, "余额"),
    H5(3, "H5"),
    APPLETS(4, "小程序");

    private final int code;
    private final String info;

    PayWay(int code, String info) {
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
