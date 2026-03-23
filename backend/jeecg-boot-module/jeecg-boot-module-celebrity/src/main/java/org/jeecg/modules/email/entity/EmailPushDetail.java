package org.jeecg.modules.email.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 邮件消息表
 * @Author: jeecgboot
 * @Date: 2025-11-07
 * @Version: V1.0
 */
@Data
@TableName("email_push_detail")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class EmailPushDetail {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title = "主键")
    private String id;
    /**
     * 发送邮件主表ID
     */
    @Excel(name = "发送邮件主表ID", width = 15)
    @Schema(title = "发送邮件主表ID")
    private String mainId;
    /**
     * 网红表ID（TK、YT、IG网红表ID）
     */
    @Excel(name = "网红表ID（TK、YT、IG网红表ID）", width = 15)
    @Schema(title = "网红表ID（TK、YT、IG网红表ID）")
    private String celebrityId;
    /**
     * 用户唯一ID TK：author_id;YT:account;IG:account
     */
    @Excel(name = "用户唯一ID TK：author_id;YT:account;IG:account", width = 15)
    @Schema(title = "用户唯一ID TK：author_id;YT:account;IG:account")
    private String account;
    /**
     * 网红名称 TK：unique_id；YT：username；IG：ig_username
     */
    @Excel(name = "网红账号", width = 15)
    @Schema(title = "网红名称")
    private String username;
    /**
     * 昵称
     */
    @Excel(name = "昵称", width = 15)
    @Schema(title = "昵称")
    private String nickname;
    /**
     * 网红邮箱
     */
    @Excel(name = "邮箱", width = 15)
    @Schema(title = "网红邮箱")
    private String email;
    /**
     * 邮件标题
     */
    @Excel(name = "邮件标题", width = 15)
    @Schema(title = "邮件标题")
    private String emailTitle;
    /**
     * 邮件内容
     */
    @Excel(name = "邮件内容", width = 15)
    @Schema(title = "邮件内容")
    private String emailContent;
    /**
     * 发送邮箱
     */
    @Excel(name = "发送邮箱", width = 15)
    @Schema(title = "发送邮箱")
    private String sendEmail;
    /**
     * 邮件预计发送时间
     */
    @Excel(name = "邮件预计发送时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title = "邮件预计发送时间")
    private Date planSendTime;
    /**
     * 发送时间
     */
    @Excel(name = "发送时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title = "发送时间")
    private Date sendTime;
    /**
     * 是否发送（0：未发送；1：已发送；-1：发送失败 -2:取消发送 10：正在发送）
     */
    @Excel(name = "是否发送（0：未发送；1：已发送；-1：发送失败 -2:取消发送  10：正在发送）", width = 15)
    @Schema(title = "是否发送（0：未发送；1：已发送；-1：发送失败 -2:取消发送  10：正在发送）")
    private Integer isSend;
    /**
     * gmail消息ID
     */
    @Excel(name = "gmail消息ID", width = 15)
    @Schema(title = "gmail消息ID")
    private String messageId;
    /**
     * gmail线程ID
     */
    @Excel(name = "gmail线程ID", width = 15)
    @Schema(title = "gmail线程ID")
    private String threadId;
    /**
     * 是否回复0 未回复 1已回复
     */
    @Excel(name = "是否回复0 未回复  1已回复", width = 15)
    @Schema(title = "是否回复0 未回复  1已回复")
    private Integer isReply;
    /**
     * 是否有效(0：默认；1:有效；-1：邮箱不存在)
     */
    @Excel(name = "是否有效(0：默认；1:有效；-1：邮箱不存在)", width = 15)
    @Schema(title = "是否有效(0：默认；1:有效；-1：邮箱不存在)")
    private Integer isConfirm;
    /**
     * ip
     */
    @Excel(name = "ip", width = 15)
    @Schema(title = "ip")
    private String ip;
    /**
     * ip所在地
     */
    @Excel(name = "ip所在地", width = 15)
    @Schema(title = "ip所在地")
    private String ipAddress;
    /**
     * 错误信息
     */
    @Excel(name = "错误信息", width = 15)
    @Schema(title = "错误信息")
    private Object errorMessage;
    /**
     * 个性签名ID
     */
    @Excel(name = "个性签名ID", width = 15)
    @Schema(title = "个性签名ID")
    private String signatureId;
    /**
     * 签名标题
     */
    @Excel(name = "签名标题", width = 15)
    @Schema(title = "签名标题")
    private String signatureTitle;
    /**
     * 签名内容
     */
    @Excel(name = "签名内容", width = 15)
    @Schema(title = "签名内容")
    private Object signatureContent;
    /**
     * 邮件模版ID
     */
    @Excel(name = "邮件模版ID", width = 15)
    @Schema(title = "邮件模版ID")
    private String templateId;
    /**
     * 建联邮箱ID
     */
    @Excel(name = "建联邮箱ID", width = 15)
    @Schema(title = "建联邮箱ID")
    private String contactEmailId;
    /**
     * 网红顾问_ID
     */
    @Excel(name = "网红顾问_ID", width = 15)
    @Schema(title = "网红顾问_ID")
    private String counselorId;
    /**
     * 网红顾问名称
     */
    @Excel(name = "网红顾问名称", width = 15)
    @Schema(title = "网红顾问名称")
    private String counselorName;
    /**
     * 抄送邮箱，多个用英文逗号或分号分隔
     */
    @Excel(name = "抄送邮箱，多个用英文逗号或分号分隔", width = 15)
    @Schema(title = "抄送邮箱，多个用英文逗号或分号分隔")
    private String ccEmails;
    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @Schema(title = "备注")
    private String remark;
    /**
     * 创建人
     */
    @Excel(name = "创建人", width = 15)
    @Schema(title = "创建人")
    private String createBy;
    /**
     * 创建时间
     */
    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title = "创建时间")
    private Date createTime;
    /**
     * 修改人
     */
    @Excel(name = "修改人", width = 15)
    @Schema(title = "修改人")
    private String updateBy;
    /**
     * 修改时间
     */
    @Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title = "修改时间")
    private Date updateTime;

    /** ==================== 邮件打开追踪字段 开始 ==================== */

    /**
     * 是否已打开（0：未打开；1：已打开）
     */
    @Excel(name = "是否已打开", width = 15, dicCode = "open_status")
    @Schema(title = "是否已打开（0：未打开；1：已打开）")
    private Integer isOpened;

    /**
     * 邮件打开次数
     */
    @Excel(name = "打开次数", width = 15)
    @Schema(title = "邮件打开次数")
    private Integer openCount;

    /**
     * 首次打开时间
     */
    @Excel(name = "首次打开时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title = "首次打开时间")
    private Date firstOpenTime;

    /**
     * 最后打开时间
     */
    @Excel(name = "最后打开时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title = "最后打开时间")
    private Date lastOpenTime;

    /** ==================== 邮件打开追踪字段 结束 ==================== */

    /**
     * 是否能够继续回复 1不能回复 0可回复
     */
    @TableField(exist = false)
    private int notReply;

    /**
     * 平台类型（0:IG;1:YT;2:TK）
     */
    @Schema(title = "平台类型")
    private Integer platformType;

    /**
     * 品牌名称（仅用于Excel导入，不存储到数据库）
     */
    @Excel(name = "品牌名称", width = 15)
    @Schema(title = "品牌名称")
    private String brandName;

    /**
     * 品牌类目（仅用于Excel导入，不存储到数据库）
     */
    @Excel(name = "品牌类目", width = 15)
    @Schema(title = "品牌类目")
    private String brandCategory;

    /**
     * 品牌类目ID（仅用于Excel导入，不存储到数据库）
     */
    @Excel(name = "品牌类目ID", width = 15)
    @Schema(title = "品牌类目ID")
    private String brandCategoryId;

    /**
     * 邮箱密码
     */
    private String emailPassword;

    /**
     * 平台类型（TK、YT、IG）
     */
    @TableField(exist = false)
    @Excel(name = "平台", width = 15)
    private String platformTypeStr;
    @TableField(exist = false)
    private Integer isPrivate;
    @TableField(exist = false)
    private String avatarUrl;
}
