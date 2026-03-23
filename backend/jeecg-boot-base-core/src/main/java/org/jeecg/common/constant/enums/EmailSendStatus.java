package org.jeecg.common.constant.enums;
/**
 * 邮件发送状态
 * @Author: zhoushen
 * */
public enum EmailSendStatus {
    DEFAULT(0, "默认"),
    SERVICE(1, "送达"),
    INVALID(4, "无效"),
    SOFTBOUNCE(5, "软退信"),
    ASKING(18, "请求中");


    private final int code;
    private final String info;

    EmailSendStatus(int code, String info) {
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
