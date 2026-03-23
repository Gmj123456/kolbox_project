package org.jeecg.modules.instagram.history.entity;

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
 * @Description: 历史提报网红明细表
 * @Author: jeecg-boot
 * @Date: 2025-11-26
 * @Version: V1.0
 */
@Data
@TableName("kol_history_celebrity_detail")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class KolHistoryCelebrityDetail {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
    /**
     * 关联主表ID
     */
    @Schema(title =  "关联主表ID")
    private String celebrityId;
    /**
     * 品牌
     */
    @Excel(name = "品牌", width = 15)
    @Schema(title =  "品牌")
    private String brandName;
    /**
     * 产品
     */
    @Excel(name = "产品", width = 15)
    @Schema(title =  "产品")
    private String productName;
    /**
     * 提报日期
     */
    @Excel(name = "提报日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(title =  "提报日期")
    private Date submitDate;
    /**
     * 网红名称（唯一标识字段之一）
     */
    @Excel(name = "账号ID", width = 15)
    @Schema(title =  "网红名称（唯一标识字段之一）")
    private String celebrityName;
    /**
     * 平台类型 0=instagram;1=youtube;2:tiktok
     */
    @Excel(name = "社交平台", width = 15)
    @Schema(title =  "平台类型 0=instagram;1=youtube;2:tiktok")
    private Integer platformType;
    /**
     * 交付内容
     */
    @Excel(name = "交付内容", width = 15)
    @Schema(title =  "交付内容")
    private String deliveryContent;
    /**
     * 参考报价（敏感金额，仅后台存储）
     */
    @Excel(name = "参考报价", width = 15)
    @Schema(title =  "参考报价（敏感金额，仅后台存储）")
    @JsonIgnore
    private String referencePrice;
    /**
     * 性别 0=男 1=女 2=未知
     */
    @Excel(name = "性别", width = 15)
    @Schema(title =  "性别 0=男 1=女 2=未知")
    private String gender;
    /**
     * 国籍
     */
    @Excel(name = "国籍", width = 15)
    @Schema(title =  "国籍")
    private String countryName;
    /**
     * 粉丝数
     */
    @Excel(name = "粉丝量", width = 15)
    @Schema(title =  "粉丝数")
    private Integer followersNum;
    /**
     * 平均播放量（均播）
     */
    @Excel(name = "均播", width = 15)
    @Schema(title =  "平均播放量（均播）")
    private Integer playAvgNum;
    /**
     * 达人类型（原始，可能含多个）
     */
    @Excel(name = "达人类型", width = 15)
    @Schema(title =  "达人类型（原始，可能含多个）")
    private String kolType;
    /**
     * 主页链接
     */
    @Excel(name = "主页链接", width = 15)
    @Schema(title =  "主页链接")
    private String webUrl;
    /**
     * 达人备注
     */
    @Excel(name = "达人备注", width = 15)
    @Schema(title =  "达人备注")
    private String celebrityRemark;
    /**
     * 是否选中 0=否 1=是 2=未确定
     */
    @Excel(name = "是否选中", width = 15)
    @Schema(title =  "是否选中 0=否 1=是 2=未确定")
    private Integer isSelected;
    /**
     * 甲方反馈
     */
    @Excel(name = "甲方反馈", width = 15)
    @Schema(title =  "甲方反馈")
    private String partyAFeedback;
    /**
     * 网红顾问名称
     */
    @Excel(name = "BD", width = 15)
    @Schema(title =  "网红顾问名称")
    private String celebrityCounselorName;
    /**
     * 成本（敏感金额，仅后台存储）
     */
    @Excel(name = "成本", width = 15)
    @Schema(title =  "成本（敏感金额，仅后台存储）")
    @JsonIgnore
    private String cost;
    /**
     * 合作结果
     */
    @Excel(name = "合作结果", width = 15)
    @Schema(title =  "合作结果")
    private String cooperationResult;
    /**
     * 合作价格（敏感金额，仅后台存储）
     */
    @Excel(name = "合作价格", width = 15)
    @Schema(title =  "合作价格（敏感金额，仅后台存储）")
    @JsonIgnore
    private String cooperationPrice;
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
     * 删除标志 0=正常 1=已删除
     */
    @Excel(name = "删除标志 0=正常 1=已删除", width = 15)
    @Schema(title =  "删除标志 0=正常 1=已删除")
    private Integer isDelete;

    /**
     * 失败原因
     */
    @TableField(exist = false)
    @Excel(name = "失败原因", width = 15)
    private String failReason;

    /**
     * 是否同步
     */
    @Excel(name = "是否同步", width = 15)
    @TableField(exist = false)
    private String isUpdate;

    @TableField(exist = false)
    private Integer rowNum;
}
