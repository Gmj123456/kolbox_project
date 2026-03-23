package org.jeecg.common.constant.enums;

/**
 * 消息类型
 *
 * @AUTHOR
 */
public enum MessageType {

    BUSINESS_CONSULTING(1, "商务咨询");

    /**
     * code
     */
    private final int code;
    /**
     * 注释
     */
    private final String info;

    /**
     * 构造方法
     *
     * @param code
     * @param info
     */
    MessageType(int code, String info) {
        this.code = code;
        this.info = info;
    }

    /**
     * 获取code
     *
     * @return
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取注释
     *
     * @return
     */
    public String getInfo() {
        return info;
    }
}
