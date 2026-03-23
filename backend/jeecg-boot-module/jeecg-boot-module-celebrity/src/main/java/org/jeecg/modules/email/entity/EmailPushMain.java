package org.jeecg.modules.email.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 邮件消息主表
 * @Author: jeecgboot
 * @Date: 2025-11-07
 * @Version: V1.0
 */
@Data
@TableName("email_push_main")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class EmailPushMain {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title = "主键")
    private String id;

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
    private String signatureContent;
    /**
     * 建联邮箱ID
     */
    @Excel(name = "建联邮箱ID", width = 15)
    @Schema(title = "建联邮箱ID")
    private String contactEmailId;
    /**
     * 发送邮箱
     */
    @Excel(name = "发送邮箱", width = 15)
    @Schema(title = "发送邮箱")
    private String sendEmail;
    /**
     * 邮箱密码
     */
    private String emailPassword;
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
     * 邮件发送时间
     */
    @Excel(name = "邮件发送时间", width = 15)
    @Schema(title = "邮件发送时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date emailPushDate;
    /**
     * 发送状态（
     * 0：未发送；
     * 99：已发送；
     * -1：发送失败；
     * -2:取消发送；
     * 10：部分发送
     * ）
     */
    @Excel(name = "发送状态（0：未发送；99：已发送；-1：发送失败； -2:取消发送； 10：部分发送）", width = 15)
    private Integer isSend;
    /**
     * 发送时间
     */
    @Excel(name = "发送时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title = "发送时间")
    private Date sendTime;
    /**
     * 排序
     */
    @Excel(name = "排序", width = 15)
    @Schema(title = "排序")
    private Integer sortCode;
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

    /**
     * 开始时间
     */
    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date emailPushDateStart;
    /**
     * 结束时间
     */
    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date emailPushDateEnd;

    /**
     * 发送网红列表
     */
    @TableField(exist = false)
    private String username;
    @TableField(exist = false)
    private Integer isReply;

    /**
     * 发送间隔最小值
     */
    private Integer sendIntervalMin;
    /**
     * 发送间隔最大值
     */
    private Integer sendIntervalMax;

    /**
     * 品牌ID
     */
    @Excel(name = "品牌ID", width = 15)
    @Schema(title = "品牌ID")
    private String brandId;
    /**
     * 品牌名称
     */
    @Excel(name = "品牌名称", width = 15)
    @Schema(title = "品牌")
    private String brandName;
    /**
     * 产品ID
     */
    @Excel(name = "产品ID", width = 15)
    @Schema(title = "品牌ID")
    private String productId;
    /**
     * 产品名称
     */
    @Excel(name = "产品名称", width = 15)
    @Schema(title = "产品名称")
    private String productName;
    /**
     * 发送类型 0：我的网红；1：Excel上传
     */
    private Integer sendType;

    private Integer platformType;
    /**
     * 业务类型 0：网红顾问；1：商务
     */
    @Excel(name = "业务类型", width = 15)
    @Schema(title = "业务类型 0：网红顾问；1：商务")
    private Integer businessType;
    /**
     * 网红邮箱
     */
    @TableField(exist = false)
    private String email;
    /**
     * 品牌分类ID
     */
    @TableField(exist = false)
    private String brandCategoryId;
    /**
     * 发送总数量
     */
    @TableField(exist = false)
    private Integer totalSentCount;
    /**
     * 发送成功数量
     */
    @TableField(exist = false)
    private Integer actualSentCount;
    /**
     * 发送失败数量
     */
    @TableField(exist = false)
    private Integer bounceCount;
    /**
     * 打开数量
     */
    @TableField(exist = false)
    private Integer openCount;
    /**
     * 回复数量
     */
    @TableField(exist = false)
    private Integer replyCount;
}
