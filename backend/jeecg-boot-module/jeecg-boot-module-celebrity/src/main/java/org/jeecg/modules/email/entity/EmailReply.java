package org.jeecg.modules.email.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 邮件回复表
 * @Author: jeecgboot
 * @Date: 2025-11-07
 * @Version: V1.0
 */
@Data
@TableName("email_reply")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class EmailReply {

    /**
     * 唯一标识符
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "唯一标识符")
    private String id;
    /**
     * 邮件明细ID
     */
    @Excel(name = "邮件明细ID", width = 15)
    @Schema(title =  "邮件明细ID")
    private String pushDetailId;
    /**
     * 邮件消息ID
     */
    @Excel(name = "邮件消息ID", width = 15)
    @Schema(title =  "邮件消息ID")
    private String messageId;
    /**
     * 邮件线程ID
     */
    @Excel(name = "邮件线程ID", width = 15)
    @Schema(title =  "邮件线程ID")
    private String threadId;
    /**
     * 网红邮箱
     */
    @Excel(name = "网红邮箱", width = 15)
    @Schema(title =  "网红邮箱")
    private String email;
    /**
     * 接收者邮箱/发送邮箱
     */
    @Excel(name = "接收者邮箱/发送邮箱", width = 15)
    @Schema(title =  "接收者邮箱/发送邮箱")
    private String sendEmail;
    /**
     * 邮件标题
     */
    @Excel(name = "邮件标题", width = 15)
    @Schema(title =  "邮件标题")
    private String emailTitle;
    /**
     * 邮件内容
     */
    @Excel(name = "邮件内容", width = 15)
    @Schema(title =  "邮件内容")
    private String emailContent;
    /**
     * 接收时间/发送时间
     */
    @Excel(name = "接收时间/发送时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "接收时间/发送时间")
    private Date receivedTime;
    /**
     * 回复邮件类型 0：网红回复邮件（默认）；1：网红顾问回复邮件
     */
    @Excel(name = "回复邮件类型 0：网红回复邮件（默认）；1：网红顾问回复邮件", width = 15)
    @Schema(title =  "回复邮件类型 0：网红回复邮件（默认）；1：网红顾问回复邮件")
    private Integer replyType;
    /**
     * 邮件模版ID
     */
    @Excel(name = "邮件模版ID", width = 15)
    @Schema(title =  "邮件模版ID")
    private String templateId;
    /**
     * 建联邮箱ID
     */
    @Excel(name = "建联邮箱ID", width = 15)
    @Schema(title =  "建联邮箱ID")
    private String contactEmailId;
    /**
     * 网红顾问_ID
     */
    @Excel(name = "网红顾问_ID", width = 15)
    @Schema(title =  "网红顾问_ID")
    private String counselorId;
    /**
     * 网红顾问名称
     */
    @Excel(name = "网红顾问名称", width = 15)
    @Schema(title =  "网红顾问名称")
    private String counselorName;
    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @Schema(title =  "备注")
    private String remark;
    /**
     * 创建人
     */
    @Excel(name = "创建人", width = 15)
    @Schema(title =  "创建人")
    private String createBy;
    /**
     * 创建时间
     */
    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "创建时间")
    private Date createTime;
    /**
     * 更新人
     */
    @Excel(name = "更新人", width = 15)
    @Schema(title =  "更新人")
    private String updateBy;
    /**
     * 更新时间
     */
    @Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "更新时间")
    private Date updateTime;

    /**
     * 抄送邮箱，多个用英文逗号或分号分隔
     */
    @Excel(name = "抄送邮箱，多个用英文逗号或分号分隔", width = 15)
    @Schema(title =  "抄送邮箱，多个用英文逗号或分号分隔")
    private String ccEmails;

    /** ==================== 邮件打开追踪字段 开始 ==================== */

    /**
     * 是否已打开（0：未打开；1：已打开）
     */
    @Excel(name = "是否已打开", width = 15, dicCode = "open_status")
    @Schema(title =  "是否已打开（0：未打开；1：已打开）")
    private Integer isOpened;

    /**
     * 邮件打开次数
     */
    @Excel(name = "打开次数", width = 15)
    @Schema(title =  "邮件打开次数")
    private Integer openCount;

    /**
     * 首次打开时间
     */
    @Excel(name = "首次打开时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "首次打开时间")
    private Date firstOpenTime;

    /**
     * 最后打开时间
     */
    @Excel(name = "最后打开时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "最后打开时间")
    private Date lastOpenTime;
    /**
     * ip
     */
    @Excel(name = "ip", width = 15)
    @Schema(title =  "ip")
    private String ip;
    /**
     * ip所在地
     */
    @Excel(name = "ip所在地", width = 15)
    @Schema(title =  "ip所在地")
    private String ipAddress;
    /** ==================== 邮件打开追踪字段 结束 ==================== */
}
