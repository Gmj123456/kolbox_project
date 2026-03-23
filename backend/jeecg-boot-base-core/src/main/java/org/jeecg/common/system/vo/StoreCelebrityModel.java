package org.jeecg.common.system.vo;

import lombok.Data;

@Data
public class StoreCelebrityModel {
    String id;
    /**
     * 账号
     */
    private String account;
    /**
     * 网红顾问_ID
     */
    private String celebrityCounselorId;
    /**
     * 网红顾问名称
     */
    private String celebrityCounselorName;
}
