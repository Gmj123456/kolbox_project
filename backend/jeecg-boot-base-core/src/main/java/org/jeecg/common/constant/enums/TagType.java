package org.jeecg.common.constant.enums;

/**
 * @author nqr
 * @description
 * @date 2025/01/06:14:04
 */
public enum TagType {
    HASHTAG(0, "hashtag"),
    KEYWORD(1, "keyword"),
    ROOT_VIDEO(2, "rootVideo"),
    ROOT_ACCOUNT(3, "rootAccount"),
    WATCHLIST(4, "watchlist");

    private final int code;
    private final String info;

    TagType(int code, String info) {
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
