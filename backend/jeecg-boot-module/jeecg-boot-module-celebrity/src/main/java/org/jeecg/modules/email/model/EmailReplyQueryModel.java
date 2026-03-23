package org.jeecg.modules.email.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @Description: 邮件回复查询模型
 * @Author:
 * @Date: 2025-11-07
 * @Version: V1.0
 */
@Data
public class EmailReplyQueryModel {

    /**
     * 邮件明细ID
     */
    @Schema(title =  "邮件明细ID")
    private String pushDetailId;

    /**
     * 邮件消息ID
     */
    @Schema(title =  "邮件消息ID")
    private String messageId;

    /**
     * 邮件线程ID
     */
    @Schema(title =  "邮件线程ID")
    private String threadId;

    /**
     * 网红邮箱
     */
    @Schema(title =  "网红邮箱")
    private String email;

    /**
     * 接收者邮箱/发送邮箱
     */
    @Schema(title =  "接收者邮箱/发送邮箱")
    private String sendEmail;

    /**
     * 邮件标题
     */
    @Schema(title =  "邮件标题")
    private String emailTitle;

    /**
     * 回复邮件类型 0：网红回复邮件（默认）；1：网红顾问回复邮件
     */
    @Schema(title =  "回复邮件类型 0：网红回复邮件（默认）；1：网红顾问回复邮件")
    private Integer replyType;

    /**
     * 开始时间
     */
    @Schema(title =  "开始时间")
    private Date startTime;

    /**
     * 结束时间
     */
    @Schema(title =  "结束时间")
    private Date endTime;
}