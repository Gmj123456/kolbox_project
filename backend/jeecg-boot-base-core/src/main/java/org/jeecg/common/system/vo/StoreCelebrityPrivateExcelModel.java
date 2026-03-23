package org.jeecg.common.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

/**
 * @Description: 私有网红表
 * @Author: jeecg-boot
 * @Date: 2020-11-11
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreCelebrityPrivateExcelModel {

    /**
     * 账号
     */
    @Excel(name = "账号", width = 20)
    @Schema(title = "账号")
    private String account;
    /**
     * 头像
     */
    @Excel(name = "头像", width = 20)
    @Schema(title = "头像")
    private String avatarUrl;
    /**
     * 性別（0：男；1：女）
     */
    @Excel(name = "性别", width = 15)
    @Schema(title = "性别（0：男；1：女）")
    private String genderText;
    private Integer gender;
    /**
     * 平台类型 0=instagram;1=youtube
     */
    @Excel(name = "平台", width = 15)
    @Schema(title = "平台 0=instagram;1=youtube")
    private String platformTypeText;
    private Integer platformType;
    /**
     * 电子邮箱
     */
    @Excel(name = "网红邮箱", width = 20)
    @Schema(title = "网红邮箱")
    private String email;
    /**
     * 均播
     */
    @Excel(name = "均播(K)", width = 15)
    @Schema(title = "均播")
    private String playAvgNumStr;
    /**
     * 国家
     */
    @Excel(name = "国家", width = 15)
    @Schema(title = "国家")
    private String countryName;
    /**
     * 段位
     */
    @Excel(name = "段位", dicCode = "celebrity_segment", width = 15)
    @Schema(title = "段位")
    private Integer isTopStar;
    /**
     * 内容
     */
    @Excel(name = "内容1", width = 15)
    @Schema(title = "内容1")
    private String videoTags;
    /**
     * 签约费用
     */
    @Excel(name = "签约费用1", width = 15)
    @Schema(title = "签约费用1")
    private String contractAmountStr;
    private BigDecimal contractAmount;
    /**
     * 内容
     */
    @Excel(name = "内容2", width = 15)
    @Schema(title = "内容2")
    private String videoTags2;
    /**
     * 签约费用
     */
    @Excel(name = "签约费用2", width = 15)
    @Schema(title = "签约费用2")
    private String contractAmountStr2;
    private BigDecimal contractAmount2;
    /**
     * 内容
     */
    @Excel(name = "内容3", width = 15)
    @Schema(title = "内容3")
    private String videoTags3;
    /**
     * 签约费用
     */
    @Excel(name = "签约费用3", width = 15)
    @Schema(title = "签约费用3")
    private String contractAmountStr3;
    private BigDecimal contractAmount3;
    /**
     * 电话
     */
    @Excel(name = "电话", width = 15)
    @Schema(title = "电话")
    private String celebrityTel;
    /**
     * 标签
     */
    @Excel(name = "个性化标签", width = 15)
    @Schema(title = "标签")
    private String personalTags;
    /**
     * 地址
     */
    @Excel(name = "地址", width = 15)
    @Schema(title = "地址")
    private String fullAddress;
    /**
     * 粉丝数
     */
    @Excel(name = "粉丝数(K)", width = 15)
    @Schema(title = "粉丝数 ")
    private String followersNumStr;
    private Integer followersNum;
    /**
     * 签约状态（0：推进中；1：已签约；2：可出战）
     */
    @Excel(name = "签约状态", dicCode = "contract_status", width = 15)
    @Schema(title = "签约状态")
    private Integer contractStatus;
    /**
     * 类目
     */
    @Excel(name = "达人类型1", width = 15)
    @Schema(title = "达人类型1")
    private String influencerType1;
    @Excel(name = "达人类型2", width = 15)
    @Schema(title = "达人类型2")
    private String influencerType2;
    @Excel(name = "达人类型3", width = 15)
    @Schema(title = "达人类型3")
    private String influencerType3;

    private String influencerTypes;

    @Excel(name = "物理空间1", width = 15)
    @Schema(title = "物理空间1")
    private String scene1;
    @Excel(name = "物理空间2", width = 15)
    @Schema(title = "物理空间2")
    private String scene2;
    @Excel(name = "物理空间3", width = 15)
    @Schema(title = "物理空间3")
    private String scene3;

    private String scenes;

    @Excel(name = "人群类型1", width = 15)
    @Schema(title = "人群类型1")
    private String audience1;
    @Excel(name = "人群类型2", width = 15)
    @Schema(title = "人群类型2")
    private String audience2;
    @Excel(name = "人群类型3", width = 15)
    @Schema(title = "人群类型3")
    private String audience3;

    private String audiences;
    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @Schema(title = "备注")
    private String remark;
    /**
     * 链接
     */
    @Excel(name = "链接", width = 15)
    @Schema(title = "链接")
    private String webUrl;
    /**
     * 初始报价
     */
    @Excel(name = "初始报价", width = 15)
    @Schema(title = "初始报价")
    private String initialAmt;
    /**
     * MCN
     */
    @Excel(name = "MCN", width = 15)
    @Schema(title = "MCN")
    private String mcnName;

    /**
     * MCN
     */
    @Excel(name = "建联邮箱", width = 15)
    @Schema(title = "建联邮箱")
    private String celebrityContactEmail;
    private String userId;
    private String userName;
    /**
     * 失败原因
     */
    private String failReason;

    /**
     * 是否同步
     */
    @Excel(name = "是否同步", width = 15)
    private String isUpdate;
    /**
     * 视频内容费用
     */
    private List<PrivateContractInfo> privateContractInfoList;
    private BigInteger playAvgNum;

    private String celebrityId;
    @Data
    public static class PrivateContractInfo {
        /**
         * 视频内容
         */
        private String videoTag;
        /**
         * 视频内容费用
         */
        private BigDecimal contractAmount;
    }

    private String recordId;
    @Excel(name = "行号", width = 15)
    private Integer rowNum;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoreCelebrityPrivateExcelModel that = (StoreCelebrityPrivateExcelModel) o;
        return Objects.equals(account, that.account) && Objects.equals(failReason, that.failReason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(account, failReason);
    }
}
