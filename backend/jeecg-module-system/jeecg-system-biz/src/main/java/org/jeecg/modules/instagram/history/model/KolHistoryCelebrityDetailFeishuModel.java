package org.jeecg.modules.instagram.history.model;

import org.jeecgframework.poi.excel.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description: 飞书历史提报网红明细表模型
 * @Author:
 * @Date: 2025-11-26
 * @Version: V1.0
 */
@Data
public class KolHistoryCelebrityDetailFeishuModel {

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
    @Schema(title =  "提报日期")
    private String submitDateText;

    /**
     * 网红名称（唯一标识字段之一）
     */
    @Excel(name = "账号ID", width = 15)
    @Schema(title =  "网红名称（唯一标识字段之一）")
    private String celebrityName;

    /**
     * 平台类型 0=instagram;1=youtube;2=tiktok (飞书表格中为字符串)
     */
    @Excel(name = "社交平台", width = 15)
    @Schema(title =  "平台类型 (飞书表格中为字符串)")
    private String platformTypeText;

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
    private String referencePriceText;

    /**
     * 性别 (飞书表格中为字符串)
     */
    @Excel(name = "性别", width = 15)
    @Schema(title =  "性别 (飞书表格中为字符串)")
    private String genderText;

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
    private String followersNumText;

    /**
     * 平均播放量（均播）
     */
    @Excel(name = "均播", width = 15)
    @Schema(title =  "平均播放量（均播）")
    private String playAvgNumText;

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
    private String isSelectedText;

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
    private String costText;

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
    private String cooperationPriceText;

    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @Schema(title =  "备注")
    private String remark;

    /**
     * 失败原因
     */
    @Excel(name = "失败原因", width = 15)
    @Schema(title =  "失败原因")
    private String failReason;

    /**
     * 是否同步
     */
    @Excel(name = "是否同步", width = 15)
    @Schema(title =  "是否同步")
    private String isUpdate;

    private Integer rowNum;
}