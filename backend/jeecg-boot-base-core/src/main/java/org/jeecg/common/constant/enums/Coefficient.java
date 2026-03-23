package org.jeecg.common.constant.enums;

/**
 * 系数,0~1小数类型
 * @Author: zhoushen
 * */
public enum Coefficient {
    /**
     * 匹配系数,默认0.5
     * */
    DEFAULT_COEFFICIENT(0.5, "默认系数");

    private final double code;
    private final String info;

    Coefficient(double code, String info) {
        this.code = code;
        this.info = info;
    }

    public double getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }

}
