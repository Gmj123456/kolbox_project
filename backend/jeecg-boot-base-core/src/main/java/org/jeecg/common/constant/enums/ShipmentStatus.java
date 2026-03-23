package org.jeecg.common.constant.enums;

public enum ShipmentStatus {
    NOT_SHIPMENT(-1, "无需发货"),
    UN_SHIPMENT(0, "待发货"),
    SUCCESS_SHIPMENT(1, "已发货"),
    UN_ORDER(-2, "无订单");

    private final int code;
    private final String info;

    ShipmentStatus(int code, String info) {
        this.code = code;
        this.info = info;
    }

    public int getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }

    public String getCodeString() {
        return Integer.toString(code);
    }
}
