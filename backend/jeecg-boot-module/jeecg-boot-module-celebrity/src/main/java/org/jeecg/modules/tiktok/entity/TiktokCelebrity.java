package org.jeecg.modules.tiktok.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.math.BigInteger;

/**
 * @Description: tiktok网红表
 * @Author: dongruyang
 * @Date: 2023-10-10
 * @Version: V1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tiktok_celebrity")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TiktokCelebrity {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
    /**
     * 唯一id
     */
    @Excel(name = "唯一id", width = 15)
    @Schema(title =  "唯一id")
    private String uniqueId;
    /**
     * 昵称
     */
    @Excel(name = "昵称", width = 15)
    @Schema(title =  "昵称")
    private String nickname;
    /**
     * 国家
     */
    @Excel(name = "国家", width = 15)
    @Schema(title =  "国家")
    private String country;
    /**
     * 签名
     */
    @Excel(name = "签名", width = 15)
    @Schema(title =  "签名")
    private Object signature;
    /**
     * 用户头像地址
     */
    @Excel(name = "用户头像地址", width = 15)
    @Schema(title =  "用户头像地址")
    private String authorAvatarUrl;
    /**
     * 用户头像大图
     */
    @Excel(name = "用户头像大图", width = 15)
    @Schema(title =  "用户头像大图")
    private String authorAvatarLarger;
    /**
     * 用户头像中图
     */
    @Excel(name = "用户头像中图", width = 15)
    @Schema(title =  "用户头像中图")
    private String authorAvatarMedium;
    /**
     * 用户头像缩略图
     */
    @Excel(name = "用户头像缩略图", width = 15)
    @Schema(title =  "用户头像缩略图")
    private String authorAvatarThumb;
    /**
     * 用户粉丝数
     */
    @Excel(name = "用户粉丝数", width = 15)
    @Schema(title =  "用户粉丝数")
    private Integer authorFollowerCount;
    /**
     * 用户关注数
     */
    @Excel(name = "用户关注数", width = 15)
    @Schema(title =  "用户关注数")
    private Integer authorFollowingCount;
    /**
     * 用户收藏数
     */
    @Excel(name = "用户收藏数", width = 15)
    @Schema(title =  "用户收藏数")
    private Integer authorHeartCount;
    /**
     * 用户视频数
     */
    @Excel(name = "用户视频数", width = 15)
    @Schema(title =  "用户视频数")
    private Integer authorVideoCount;
    /**
     * 用户点赞数
     */
    @Excel(name = "用户点赞数", width = 15)
    @Schema(title =  "用户点赞数")
    private Integer authorDiggCount;
    /**
     * 样本视频总个数
     */
    @Excel(name = "样本视频总个数", width = 15)
    @Schema(title =  "样本视频总个数")
    private Integer videoSampleCount;
    /**
     * 符合要求的视频播放量的个数
     */
    @Excel(name = "符合要求的视频播放量的个数", width = 15)
    @Schema(title =  "符合要求的视频播放量的个数")
    private Integer videoStandardCount;
    /**
     * 样本视频总播放量
     */
    @Excel(name = "样本视频总播放量", width = 15)
    @Schema(title =  "样本视频总播放量")
    private BigInteger videoSamplePlaySum;
    /**
     * 最新视频创建时间
     */
    @Excel(name = "最新视频创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "最新视频创建时间")
    private java.util.Date videoCreateTimeLast;
    /**
     * 邮箱
     */
    @Excel(name = "邮箱", width = 15)
    @Schema(title =  "邮箱")
    private String email;
    /**
     * 签名链接
     */
    @Excel(name = "签名链接", width = 15)
    @Schema(title =  "签名链接")
    private String bioLink;
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
    private java.util.Date createTime;
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
    private java.util.Date updateTime;
    /**
     * 删除状态（0=未删除,1=已删除）
     */
    @Excel(name = "删除状态（0=未删除,1=已删除）", width = 15)
    @Schema(title =  "删除状态（0=未删除,1=已删除）")
    private Integer isDelete;
    /**
     * 用户id修改状态（0=未修改,1=已修改）
     */
    @Excel(name = "用户id修改状态（0=未修改,1=已修改）", width = 15)
    @Schema(title =  "用户id修改状态（0=未修改,1=已修改）")
    private Integer isChangeUniqueId;
    /**
     * linktree地址
     */
    @Excel(name = "linktree地址", width = 15)
    @Schema(title =  "linktree地址")
    private String linktree;
    /**
     * instagram账号
     */
    @Excel(name = "instagram账号", width = 15)
    @Schema(title =  "instagram账号")
    private String instagramAccount;
    /**
     * youtube账号
     */
    @Excel(name = "youtube账号", width = 15)
    @Schema(title =  "youtube账号")
    private String youtubeAccount;
    /**
     * 手机号
     */
    @Excel(name = "手机号", width = 15)
    @Schema(title =  "手机号")
    private String phone;
    /**
     * 性別（0=男；1=女）
     */
    @Excel(name = "性別（0=男；1=女）", width = 15)
    @Schema(title =  "性別（0=男；1=女）")
    private Integer gender;
    /**
     * 网红账号的唯一标识
     */
    private String secUid;
    private String authorId;

    private Integer playAvgNum;

    /**
     * 播放量中位数
     */
    @Excel(name = "播放量中位数", width = 15)
    @Schema(title =  "播放量中位数")
    private Integer medianViews;
}
