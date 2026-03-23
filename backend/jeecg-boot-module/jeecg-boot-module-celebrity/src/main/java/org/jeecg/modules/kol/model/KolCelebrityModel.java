package org.jeecg.modules.kol.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * @Author: nqr
 * @Date: 2025/5/6 10:32
 * @Description:
 **/
@Data
public class KolCelebrityModel {
    private String id;
    private String account;
    private String authorId;
    private String username;
    private String avatarUrl;
    private String fullName;
    private String email;
    private String description;
    private BigInteger followingCount;
    private Integer followerCount;
    private Integer postCount;
    private Double avgPlayCount;
    private Double avgCommentCount;
    private Double avgLikeCount;
    private String country;
    private String gender;
    private String website;
    private String linktree;
    private Integer videoSampleCount;
    private Integer videoStandardCount;
    /**
     * 最新视频创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "最新视频创建时间")
    private Date lastVideoCreateTime;
    private String videoData;
    private Boolean isHighFollower;
    private Boolean isDeleted;
    private String remark;
    private String createdBy;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "创建时间")
    private Date createTime;
    private String updatedBy;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "更新时间")
    private Date updateTime;
    /**
     * 私有网红Id
     */
    @Schema(title =  "私有网红Id")
    private String celebrityPrivateId;
    /**
     * 网红标签关联视图列表
     */
    @Schema(title =  "标签列表")
    private List<KolAllotTagModel> tagList;

    /**
     * 开发历史
     */
    @Schema(title =  "开发历史")
    private String contactHistory;
    /**
     * 平台类型 0=IG 1=YT 2=TK
     */
    @Schema(title =  "平台类型")
    private Integer platformType;
    /**
     * 分配日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(title =  "分配日期")
    private Date allotTime;

    List<TypeData> dataList;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 指定日期时间格式
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date contractTime;

    private Integer playAvgNum;
    private Integer contactCount;
    /**
     * 播放量中位数
     */
    private Integer medianViews;
}
