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
 * @Description: 消息发送模板
 * @Author: jeecg-boot
 * @Date: 2020-05-11
 * @Version: V1.0
 */
@Data
@TableName("store_message_template")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MessageTemplate {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;

    /**
     * 商家ID
     */
    @Excel(name = "商家ID", width = 15)
    @Schema(title =  "商家ID")
    private String sellerId;
    /**
     * 商家昵称
     */
    @Excel(name = "商家昵称", width = 15)
    @Schema(title =  "商家昵称")
    private String sellerName;

    /**
     * 模板类型
     */
    @Excel(name = "模板类型", width = 15)
    @Schema(title =  "模板类型")
    private String templateType;
    /**
     * 模板类型名称
     */
    @Excel(name = "模板类型名称", width = 15)
    @Schema(title =  "模板类型名称")
    private String templateTypeName;
    /**
     * 模板标题
     */
    @Excel(name = "模板标题", width = 15)
    @Schema(title =  "模板标题")
    private String templateTitle;
    /**
     * 模板内容
     */
    @Excel(name = "模板内容", width = 15)
    @Schema(title =  "模板内容")
    private String templateContent;
    /**
     * 模板富文本内容
     */
    @Excel(name = "模板富文本内容", width = 15)
    @Schema(title =  "模板富文本内容")
    private String templateRichContent;
    /**
     * 有效标志
     */
    @Excel(name = "有效标志", width = 15)
    @Schema(title =  "有效标志")
    private Integer isEnabled;
    /**
     * 删除标记
     */
    @Excel(name = "删除标记", width = 15)
    @Schema(title =  "删除标记")
    private Integer isDelete;
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
     * 模板名称
     */
    private String templateName;

    /**
     * 是否公有（0：否；1：是）
     */
    @Excel(name = "是否公有（0：否；1：是）", width = 15)
    @Schema(title =  "是否公有（0：否；1：是）")
    private Integer isPublic;



    /**
     * 申请通过时间
     */
    @Excel(name = "申请通过时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "申请通过时间")
    private Date approvedDate;
    /**
     * 审核人员
     */
    @Excel(name = "审核人员", width = 15)
    @Schema(title =  "审核人员")
    private String approvedUserId;
    /**
     * 审核状态（0：待审核；1：审核通过；-1：未通过）
     */
    @Excel(name = "审核状态（0：待审核；1：审核通过；-1：未通过）", width = 15)
    @Schema(title =  "审核状态（0：待审核；1：审核通过；-1：未通过）")
    private Integer approvedStatus;

    /**
     * 驳回原因
     */
    @Excel(name = "驳回原因", width = 15)
    @Schema(title =  "驳回原因")
    private String approvedFailReason;
    /**
     * 测试发送次数
     */
    private Integer testSendNum;

    /**
     * 模板分值
     */
    @Excel(name = "模板分值", width = 15)
    @Schema(title =  "模板分值")
    private java.math.BigDecimal templateScore;


    /**
     * 敏感词扣分列表
     */
    @Excel(name = "敏感词扣分列表", width = 15)
    @Schema(title =  "敏感词扣分列表")
    private String sensitiveJson;



}
