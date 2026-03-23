package org.jeecg.common.constant.enums;

public enum PromotionGoodsType {

    DNS(0, "未开始"),
    UNDERWAY(10, "进行中"),
    FINISHEDNORMAL (99,"已结束（正常）"),
    FINISHEDABNORMAL(90,"已结束（异常）");

    private final int code;
    private final String info;

    PromotionGoodsType(int code, String info) {
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
