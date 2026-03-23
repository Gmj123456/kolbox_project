package org.jeecg.modules.instagram.storecelebrity.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.math.BigDecimal;

/**
 * @Description: 私有网红表
 * @Author: jeecg-boot
 * @Date: 2020-11-11
 * @Version: V1.0
 */
@Data
public class CelebrityPrivateExportModel {
    private String id;

    /**
     * 账号
     */
    @Excel(name = "网红ID", width = 20)
    @Schema(title = "账号")
    private String account;
    /**
     * 平台类型 0=instagram;1=youtube
     */
    @Excel(name = "平台", width = 15)
    @Schema(title = "平台")
    private String platformTypeText;
    private Integer platformType;
    /**
     * 性別（0：男；1：女）
     */
    @Excel(name = "性别", width = 15)
    @Schema(title = "性别（0：男；1：女）")
    private String genderText;
    private Integer gender;
    /**
     * 国家
     */
    @Excel(name = "国家", width = 15)
    @Schema(title = "国家")
    private String countryName;
    /**
     * 粉丝数
     */
    @Excel(name = "粉丝数", width = 15)
    @Schema(title = "粉丝数")
    private String followersNumStr;
    private Integer followersNum;

    /**
     * 均播
     */
    @Excel(name = "均播", width = 15)
    @Schema(title = "均播")
    private String avgNum;
    /**
     * 达人类型
     */
    @Excel(name = "达人类型", width = 30)
    @Schema(title = "达人类型")
    private String attributeNames;
    /**
     * 红人链接
     */
    @Excel(name = "红人链接", width = 50)
    @Schema(title = "红人链接 ")
    private String webUrl;

    /**
     * 历史合作
     */
    @Excel(name = "历史合作", width = 30)
    @Schema(title = "历史合作")
    private String productCooperateInfo;
    /**
     * 网红顾问名称
     */
    @Excel(name = "网红顾问", width = 15)
    @Schema(title = "网红顾问")
    private String counselorName;
    private String counselorId;
    /**
     * 合同信息详情
     */
    @Excel(name = "签约详情", width = 30)
    @Schema(title = "签约详情")
    private String contractInfoDetails;
    /**
     * 视频GMV值
     */
    @Excel(name = "GMV", width = 20)
    @Schema(title = "视频GMV值")
    private String videoGmvValue;
    /**
     * 个性化标签
     */
    @Excel(name = "个性化标签", width = 30)
    @Schema(title = "个性化标签")
    private String personalTagsList;
    /**
     * 签约时间
     */
    @Excel(name = "签约时间", width = 20)
    @Schema(title = "签约时间")
    private String contractTime;

    /**
     * 段位
     */
//    @Excel(name = "段位", dicCode = "celebrity_segment", width = 15)
    @Schema(title = "段位")
    private Integer isTopStar;
    /**
     * 类目
     */
    @Schema(title = "类目")
    private String likeTags;
    /**
     * 视频 GMV
     */
    @Schema(title = "视频GMV")
    private String videoGmv;
    /**
     * 中位GMV
     */
    @Schema(title = "中位GMV")
    private String medGmvRevenueRange;

}