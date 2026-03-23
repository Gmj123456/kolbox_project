package org.jeecg.common.constant.enums;
/**
 * 审核状态
 * @Author: zhoushen
 * */
public enum ApproveStatus {
    UNAPPROVED(0, "未审核"),
    APPROVED(1, "已审核"),
    REJECTED(-1, "驳回"),
    CANCEL(-2, "取消");


    private final int code;
    private final String info;

    ApproveStatus(int code, String info) {
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
