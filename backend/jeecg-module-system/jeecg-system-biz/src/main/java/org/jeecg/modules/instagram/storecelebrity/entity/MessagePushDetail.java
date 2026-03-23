package org.jeecg.modules.instagram.storecelebrity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 邮件推送日志表
 * @Author: jeecg-boot
 * @Date: 2020-05-11
 * @Version: V1.0
 */
@Data
@TableName("store_message_push_detail")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MessagePushDetail {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
    /**
     * 商家id
     */
    @Excel(name = "商家id", width = 15)
    @Schema(title =  "商家id")
    private String sellerId;
    /**
     * 商家昵称
     */
    @Excel(name = "商家昵称", width = 15)
    @Schema(title =  "商家昵称")
    private String sellerName;
    /**
     * 邮件推送时间
     */
    @Schema(title =  "邮件推送时间")
    private String emailPushDate;
    /**
     * 邮件标题
     */
    @Excel(name = "消息标题", width = 15)
    @Schema(title =  "消息标题")
    private String emailTitle;
    /**
     * 发送邮箱
     */
    @Excel(name = "发送邮箱", width = 15)
    @Schema(title =  "发送邮箱")
    private String sendEmail;
    /**
     * 发送邮箱密码
     */
    @Excel(name = "发送邮箱密码", width = 15)
    @Schema(title =  "发送邮箱密码")
    private String sendEmailPassword;

    /**
     * 邮件内容
     */
    @Excel(name = "邮件内容", width = 15)
    @Schema(title =  "邮件内容")
    private String emailContent;

    /**
     * 模板类型
     */
    @Excel(name = "模板类型", width = 15)
    @Schema(title =  "模板类型")
    private String messageTelType;
    /**
     * 是否发送（0：未发送；1：已发送；-1：发送失败）
     */
    @Excel(name = "是否发送（0：未发送；1：已发送；-1：发送失败）", width = 15)
    @Schema(title =  "是否发送（0：未发送；1：已发送；-1：发送失败）")
    private Integer isSend;

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
     * 修改人
     */
    @Excel(name = "修改人", width = 15)
    @Schema(title =  "修改人")
    private String updateBy;
    /**
     * 修改时间
     */
    @Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "修改时间")
    private Date updateTime;

    /**
     * 接收邮箱
     */
    @Excel(name = "接收邮箱", width = 15)
    @Schema(title =  "接收邮箱")
    private String kolEmail;
    /**
     * 网红id
     */
    @Excel(name = "网红id", width = 15)
    @Schema(title =  "网红id")
    private String kolId;
    /**
     * 网红名称
     */
    @Excel(name = "网红名称", width = 15)
    @Schema(title =  "网红名称")
    private String kolName;
    /**
     * 流量包订购id
     */
    @Excel(name = "流量包订购id", width = 15)
    @Schema(title =  "流量包订购id")
    private String packagePurchaseId;
    /**
     * 流量包id
     */
    @Excel(name = "流量包id", width = 15)
    @Schema(title =  "流量包id")
    private String packageId;
    /**
     * 流量包名称
     */
    @Excel(name = "流量包名称", width = 15)
    @Schema(title =  "流量包名称")
    private String packageName;

    /**
     * 是否回复
     */
    @Excel(name = "是否回复", width = 15)
    @Schema(title =  "是否回复")
    private Integer memberReply;

    /**
     * 是否有效(0：默认；1:open；2：click)
     */
    @Excel(name = "是否有效", width = 15)
    @Schema(title =  "是否有效")
    private Integer isConfirm;

    /**
     * ip所在地
     */
    @Excel(name = "ip所在地", width = 15)
    @Schema(title =  "ip所在地")
    private String ipAddress;

    /**
     * ip
     */
    @Excel(name = "ip", width = 15)
    @Schema(title =  "ip")
    private String ip;

    /**
     * 发送域名
     */
    @Excel(name = "发送域名", width = 15)
    @Schema(title =  "发送域名")
    private String sendDomain;
}
