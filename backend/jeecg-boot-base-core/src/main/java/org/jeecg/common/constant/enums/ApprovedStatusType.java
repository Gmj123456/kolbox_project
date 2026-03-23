package org.jeecg.common.constant.enums;

/**
 * @author Administrator
 */

public enum ApprovedStatusType {
    DID_NOT_PASS(-1, "未通过"),
    PENDING_REVIEW(0, "待审核"),
    EXAMINATION_PASSED(1, "审核通过"),
    PROVIDE_PLAN(2, "已提供方案"),
    MATCH_SUCCESSFULLY(3, "匹配成功");

    private final int code;
    private final String info;

    ApprovedStatusType(int code, String info) {
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
