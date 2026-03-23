package org.jeecg.modules.email.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description: 邮件回复请求模型
 * @Author:
 * @Date: 2025-11-07
 * @Version: V1.0
 */
@Data
public class EmailReplyRequestModel {

    /**
     * 邮件回复ID
     */
    @Schema(title =  "邮件回复ID", required = true)
    private String id;

    /**
     * 邮件内容
     */
    @Schema(title =  "邮件内容", required = true)
    private String emailContent;

    /**
     * 抄送邮件
     */
    private String ccEmails;
}