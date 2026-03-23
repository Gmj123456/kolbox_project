package org.jeecg.modules.instagram.storecelebrity.model;

import lombok.Data;

@Data
public class PushUser {

    /**
     * 网红id
     */
    private String kolId;
    /**
     * 网红昵称
     */
    private String kolName;
    /**
     * 网红邮箱
     */
    private String kolEmail;

    /**
     * 发送人员id
     */
    private String sellerId;

    /**
     * 发送人员昵称
     */
    private String sellerName;

    /**
     * 发送人员昵称
     */
    private String reamrk;

}
