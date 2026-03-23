package org.jeecg.modules.kol.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Description: 网红查询条件基类
 * @Author: xyc
 * @Date: 2024年12月3日 10:58:42
 * @Version: V1.1
 * @History: 增加tag类型 2024-12-21 09:37:52
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class KolBaseSearchModel implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    /**
     * tag类型：0=hashtag 1=keyword 2=rootVideo 3=rootAccount
     */
    private Integer tagType;

    /**
     * 查询标签名称列表
     */
    private List<String> tagNameList;
    private List<String> uniqueIdList;

    /**
     * 符合要求的样本视频最小个数
     */
    private Integer minVideoStandardCount;

    /**
     * 符合要求的样本视频最大个数
     */
    private Integer maxVideoStandardCount;

    /**
     * 国家简码
     */
    private String countryCode;

    /**
     * 是否分配 0:否 1:是
     */
    private Integer isAllot;

    /**
     * 是否有邮箱地址 0:否 1:是
     */
    private Integer isHasEmail;

    /**
     * 是否已经建联 0:否 1:是
     */
    private Integer isContact;

    /**
     * 邮箱地址关键字
     */
    private String emailKeyword;

    /**
     * 是否为私有网红 0:否 1:是
     */
    private Integer isPrivateKol;

    /**
     * 最小粉丝数
     */
    private Integer minFollowers;

    /**
     * 最大粉丝数
     */
    private Integer maxFollowers;

    /**
     * 网红顾问名称
     */
    private String counselorName;

    /**
     * 标签网红顾问id
     */
    private String tagCounselorId;

    /**
     * 个人简介关键字
     */
    private String signatureKeyword;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 分配天数
     */
    private Integer allotDays;
    private Date allotDaysDate;

    /**
     * 最小播放量中位数
     */
    private Integer minMedianViews;

    /**
     * 最大播放量中位数
     */
    private Integer maxMedianViews;


    /**
     * 最小均播数
     */
    private Integer minPlayAvgNum;

    /**
     * 最大均播数
     */
    private Integer maxPlayAvgNum;
    /**
     * 是否置顶
     */
    private Integer isNonPinned;

    /**
     * 分配起始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd") // 指定日期时间格式
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date allotStartTime;

    /**
     * 分配结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd") // 指定日期时间格式
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date allotEndTime;
    /**
     * 标签id
     */
    private String tagsId;
    /**
     * 签名外链
     **/

    private String bioLink;
    private String linktree;
}
