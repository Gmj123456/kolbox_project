package org.jeecg.modules.instagram.storepromotion.entity;

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
 * @Description: 产品网红推广历史匹配详情
 * @Author: nqr
 * @Date: 2023-09-02
 * @Version: V1.0
 */
@Data
@TableName("store_promotion_project_detail")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StorePromotionProjectDetail {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
    /**
     * 商家ID
     */
    @Schema(title =  "商家ID")
    private String sellerId;
    /**
     * 商家昵称
     */
    @Schema(title =  "商家昵称")
    private String sellerName;
    /**
     * 推广单号
     */
    @Schema(title =  "推广单号")
    private String extensionCode;
    /**
     * 推广ID
     */
    @Schema(title =  "推广ID")
    private String promId;
    /**
     * 方案ID
     */
    @Schema(title =  "方案ID")
    private String projectId;
    /**
     * 方案名称
     */
    @Schema(title =  "方案名称")
    private String projectName;
    /**
     * 账号
     */
    @Excel(name = "网红账号", width = 15)
    @Schema(title =  "网红账号")
    private String account;
    /**
     * 平台类型 0=instagram;1=youtube
     */
    @Excel(name = "平台类型(IG,YT,TK)", dicCode = "platform_type", width = 22)
    @Schema(title =  "平台类型 0=instagram;1=youtube")
    private Integer platformType;
    /**
     * 报价
     */
    @Excel(name = "报价($)", width = 15)
    @Schema(title =  "报价")
    private String offerAmt;
    /**
     * 所在国家
     */
    @Excel(name = "国家", width = 15)
    @Schema(title =  "国家")
    private String countryName;
    /**
     * 粉丝数
     */
    @Excel(name = "粉丝量", width = 15)
    @Schema(title =  "粉丝量")
    private String followersNum;
    /**
     * 平均观看量
     */
    @Excel(name = "平均观看量", width = 15)
    @Schema(title =  "平均观看量")
    private String playAvgNum;
    /**
     * 粉丝互动率
     */
    @Excel(name = "粉丝互动率", width = 15)
    @Schema(title =  "粉丝互动率")
    private String interactAvgNum;
    /**
     * 个人站点
     */
    @Schema(title =  "个人站点")
    private String website;
    /**
     * 男女比例
     */
    @Excel(name = "男女比例", width = 15)
    @Schema(title =  "男女比例")
    private String maleFemaleRatio;
    /**
     * 国家占比
     */
    @Excel(name = "主要国家占比", width = 15)
    @Schema(title =  "国家占比")
    private String countryRatio;
    /**
     * 粉丝年龄分布
     */
    @Excel(name = "粉丝年龄分布", width = 15)
    @Schema(title =  "粉丝年龄分布")
    private String fansAgeDistribution;
    /**
     * 视频风格
     */
    @Excel(name = "视频风格", width = 15)
    @Schema(title =  "视频风格")
    private String videoStyle;
    /**
     * 网红主页
     */
    @Excel(name = "主页链接", width = 15)
    @Schema(title =  "主页链接")
    private String celebrityUrl;
    /**
     * 推荐理由
     */
    @Excel(name = "推荐理由", width = 15)
    @Schema(title =  "推荐理由")
    private String reason;
    /**
     * 备注
     */
    @Schema(title =  "备注")
    private String remark;
    /**
     * 创建人
     */
    @Schema(title =  "创建人")
    private String createBy;
    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "创建时间")
    private Date createTime;
    /**
     * 修改人
     */
    @Schema(title =  "修改人")
    private String updateBy;
    /**
     * 修改时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "修改时间")
    private Date updateTime;
}
