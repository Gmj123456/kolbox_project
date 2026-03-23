package org.jeecg.common.constant.enums;

/**
 * @author lf
 * @version 1.0.0
 * @ClassName AttributeConfirmationStatus.java
 * @Description TODO
 * @createTime 2025年08月19日 16:04:00
 */
public enum AttributeConfirmationStatus {
    PENDING_GENERATION(0, "待生成"),
    PENDING_CONFIRMATION(1, "待确认"),
    CONFIRMED(2, "已确认");

    private final int code;
    private final String info;

    AttributeConfirmationStatus(int code, String info) {
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
