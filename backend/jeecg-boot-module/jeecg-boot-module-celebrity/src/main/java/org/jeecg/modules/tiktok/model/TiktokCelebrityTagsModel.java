package org.jeecg.modules.tiktok.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.modules.tiktok.entity.TiktokCelebrityTags;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 网红tag表
 * @Author: dongruyang
 * @Date: 2023-10-10
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TiktokCelebrityTagsModel extends TiktokCelebrityTags {
    // tag状态
    private Integer tagStatus = 99;

    // 头像
    private String authorAvatarUrl;

    // 样本视频总个数
    private Integer videoSampleCount;

    // 符合要求的视频播放量的个数
    private Integer videoStandardCount;
    /**
     * 粉丝数
     */
//    private Integer authorFollowerCount;
    private String celebrityPrivateId;

    /**
     * 播放开始数量
     */
    private Integer playAvgStartNum;
    /**
     * 播放结束数量
     */
    private Integer playAvgEndNum;

    // 开始时间
    @DateTimeFormat(pattern = "yyyy-MM-dd") // 指定日期时间格式
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
//    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date startTime;
    private String startTimeStr;
    // 结束时间
    @DateTimeFormat(pattern = "yyyy-MM-dd") // 指定日期时间格式
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
//    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date endTime;
    private String endTimeStr;

    /**
     * 列表国家名字
     */
    private String countryName;


    /**
     * 列表查询:国家名字
     */
    private String shortCode;

    /**
     * 列表查询:是否包含邮箱 0:否 1:是
     */
    private Integer emailFlag;

    /**
     * 列表查询: 是否分配 0:否 1:是
     */
    private Integer isAllot;


    /**
     * 历史分配列表查询: 是否分配 0:否 1:是
     */
    private Integer historyIsAllot = 1;


    // 邮箱
    private String email;

    // 简介
    private Object signature;

    // 标签
    private String tagNames;

    private String tagName;

    /**
     * 粉丝数
     */
    private Integer startFollowers;
    private Integer endFollowers;
    private Integer days;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 指定日期时间格式
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date contractTime;

    private String productId;
    private String attributeId;
    private String attributeName;
}
