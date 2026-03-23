package org.jeecg.common.constant.enums;

public enum PlatformType {
    INSTAGRAM(0, "instagram"),
    YOUTUBE(1, "youtube"),
    TIKTOK(2, "tiktok");

    private final int code;
    private final String info;

    PlatformType(int code, String info) {
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
