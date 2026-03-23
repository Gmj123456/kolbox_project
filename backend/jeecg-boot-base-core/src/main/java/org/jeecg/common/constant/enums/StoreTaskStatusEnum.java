package org.jeecg.common.constant.enums;

public enum StoreTaskStatusEnum {
    NOT_START(0, "未开始"),
    RUNNING(1, "执行中"),
    STOP_SUCCESS(90, "中断完成"),
    SUCCESSFULLY(99, "成功完成");

    private final int code;
    private final String info;

    StoreTaskStatusEnum(int code, String info) {
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
