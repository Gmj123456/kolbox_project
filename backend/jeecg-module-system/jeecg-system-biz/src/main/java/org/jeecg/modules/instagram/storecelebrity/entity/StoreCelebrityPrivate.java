package org.jeecg.modules.instagram.storecelebrity.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * @Description: 私有网红表
 * @Author: jeecg-boot
 * @Date: 2020-11-11
 * @Version: V1.0
 */
@Data
@TableName("store_celebrity_private")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreCelebrityPrivate {

    /**
     * 账号
     */
    @Excel(name = "账号", width = 20)
    @Schema(title =  "账号")
    private String account;
    /**
     * 头像
     */
    @Excel(name = "头像", width = 20)
    @Schema(title =  "头像")
    private String avatarUrl;
    /**
     * 性別（0：男；1：女）
     */
    @Excel(name = "性別 (男,女)", dicCode = "sex", width = 15)
    @Schema(title =  "性別（0：男；1：女）")
    private Integer gender;
    /**
     * 平台类型 0=instagram;1=youtube
     */
    @Excel(name = "平台类型 (IG,YT,TK)", dicCode = "platform_type", width = 15)
    @Schema(title =  "平台类型 0=instagram;1=youtube")
    private Integer platformType;
    /**
     * 电子邮箱
     */
    @Excel(name = "电子邮箱", width = 20)
    @Schema(title =  "电子邮箱")
    private String email;
    /**
     * 粉丝数
     */
    @Excel(name = "粉丝数", width = 15)
    @Schema(title =  "粉丝数 ")
    private Integer followersNum;
    /**
     * 签约状态（0：推进中；1：已签约；2：可出战）
     */
    @Excel(name = "签约状态(推进中,已签约,可出战)", dicCode = "contract_status", width = 15)
    @Schema(title =  "签约状态")
    private Integer contractStatus;
    /**
     * 签约费用
     */
    @Excel(name = "签约费用", width = 15)
    @Schema(title =  "签约费用")
    private BigDecimal contractAmount;
    /**
     * 类目
     */
    @Excel(name = "类目", width = 15)
    @Schema(title =  "类目")
    private String likeTags;


    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
    /**
     * 网红ID
     */
    @Schema(title =  "网红ID")
    private String celebrityId;
    /**
     * 昵称
     */
    @Schema(title =  "昵称")
    private String nickName;


    /**
     * 合作时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "合作时间")
    private Date cooperationTime;
    /**
     * 签约时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "签约时间")
    private Date contractTime;

    /**
     * 签约产品数量
     */
    @Schema(title =  "签约产品数量")
    private Integer contractGoodsNum;
    /**
     * 签约产品单价
     */
    @Schema(title =  "签约产品单价")
    private BigDecimal contractGoodsPrice;
    /**
     * 擅长类型
     */
    @Schema(title =  "擅长类型")
    private String promLikeTags;

    /**
     * 推广费用
     */
    @Schema(title =  "推广费用")
    private BigDecimal promPrice;

    /**
     * 网红顾问_ID
     */
    @Schema(title =  " 网红顾问_ID")
    private String celebrityCounselorId;
    /**
     * 网红顾问名称
     */
    @Schema(title =  " 网红顾问名称")
    private String celebrityCounselorName;
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
    /**
     * 收货地址
     */
    @Schema(title =  "收货地址")
    private String fullAddress;
    /**
     * 点赞平均数
     */
    @Schema(title =  "点赞平均数")
    private Integer likeAvgNum;

    /**
     * 视频标签
     */
    @Schema(title =  "视频标签")
    private String videoTags;
    /**
     * 平均互动率
     */
    @Schema(title =  "平均互动率")
    private BigDecimal interactAvgNum;
    /**
     * 平均观看量
     */
    @Schema(title =  "平均观看量")
    private BigInteger playAvgNum;
    /**
     * 总视频数
     */
    @Schema(title =  "总视频数")
    private Integer videoCount;
    /**
     * 总获赞数
     */
    @Schema(title =  "总获赞数")
    private Integer likeCount;

    /**
     * 国家名称
     */
    @Schema(title =  "国家名称")
    private String countryName;
    /**
     * 国家id
     */
    @Schema(title =  "国家id")
    private String countryId;

    /**
     * 网红电话
     */
    @Schema(title =  "网红电话")
    private String celebrityTel;
    /**
     * 是否头牌
     */
    @Schema(title =  "是否头牌")
    @Dict(dicCode = "celebrity_segment")
    private Integer isTopStar;
    /**
     * 个性标签
     */
    private String personalTags;

    /**
     * 是否MCN
     */
    private Integer isMcn;
    private String mcnId;
    private String mcnName;
    /**
     * 链接
     */
    private String webUrl;
    /**
     * 初始报价
     */
    private String initialAmt;

    private Integer isDown;

    /**
     * 网红建联邮箱
     */
    private String celebrityContactEmail;


    /**
     * 账号异常标记 0=正常账号 1=异常账号
     */
    @Excel(name = "账号异常标记", width = 15)
    @Schema(title =  "账号异常标记 0=正常账号 1=异常账号")
    private Integer isAbnormalAccount;


    /**
     * 视频数据json
     */
    @Excel(name = "视频数据json", width = 15)
    @Schema(title =  "视频数据json")
    private String videoData;


    /**
     * 同步日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "同步日期")
    private Date lastCrawledTime;


    /**
     * 异常代码 -400=非法账号 -409=私密账号 -405=无内容 -414=无账号 -419=需登录
     */
    @Excel(name = "异常代码", width = 15)
    @Schema(title =  "异常代码 -400=非法账号 -409=私密账号 -405=无内容 -414=无账号 -419=需登录")
    private Integer abnormalCode;

    /**
     * 是否人工确认社媒属性 0=未确认 1=已确认
     */
    @Schema(title =  "是否人工确认社媒属性 0=待生成 1=待确认 2=已确认")
    private Integer isAttributeConfirmed;
    private Integer getAttributeStatus;
    @TableField(exist = false)
    private String contractInfo;
    /**
     * 视频 GMV （JSON 格式）
     */
    private String videoGmv;
    /**
     * 视频内容标签 （JSON 格式）
     */
    private String contentLabels;
    /**
     * 行业标签 （JSON 格式）
     */
    private String industryLabels;
    /**
     * 价格 （JSON 格式）
     */
    private String esData;
    /**
     * 中位 GMV 收入范围，GMV 收入的范围区间（JSON 格式）
     */
    private String medGmvRevenueRange;
    /**
     * 视频GMV值
     */
    private BigDecimal videoGmvValue;
    /**
     * GMV最新爬取时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "GMV最新爬取时间")
    private Date lastGmvCrawledTime;

    @TableField(exist = false)
    private Integer rowNum;

    /**
     * 是否人工确认个性化标签 0=未确认 1=已确认
     */
    private Integer isPersonalTagsConfirmed;
    private String personalTagsConfirmedUserId;
    private String personalTagsConfirmedUsername;
    private String attributeConfirmedUserId;
    private String attributeConfirmedUsername;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "社媒属性确认时间")
    private Date attributeConfirmedConfirmedTime;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "个性化标签确认时间")
    private Date personalTagsConfirmedTime;

}
