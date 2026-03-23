package org.jeecg.modules.instagram.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: ig_celebrity
 * @Author: jeecg-boot
 * @Date: 2024-12-02
 * @Version: V1.0
 */
@Data
@TableName("ig_celebrity")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class IgCelebrity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
    /**
     * 账号
     */
    @Excel(name = "账号", width = 15)
    @Schema(title =  "账号")
    private String igUsername;
    /**
     * 头像
     */
    @Excel(name = "头像", width = 15)
    @Schema(title =  "头像")
    private String profilePicUrl;
    /**
     * 昵称
     */
    @Excel(name = "昵称", width = 15)
    @Schema(title =  "昵称")
    private String fullName;
    /**
     * 发帖数
     */
    @Excel(name = "发帖数", width = 15)
    @Schema(title =  "发帖数")
    private Integer mediaCount;
    /**
     * 粉丝数
     */
    @Excel(name = "粉丝数 ", width = 15)
    @Schema(title =  "粉丝数 ")
    private Integer followerCount;
    /**
     * 个人简介
     */
    @Excel(name = "个人简介", width = 15)
    @Schema(title =  "个人简介")
    private String biography;
    /**
     * 电子邮箱
     */
    @Excel(name = "电子邮箱", width = 15)
    @Schema(title =  "电子邮箱")
    private String email;
    /**
     * 手机号
     */
    @Excel(name = "手机号", width = 15)
    @Schema(title =  "手机号")
    private String phone;
    /**
     * 所在国家简码
     */
    @Excel(name = "所在国家简码", width = 15)
    @Schema(title =  "所在国家简码")
    private String country;
    /**
     * 视频平均播放量
     */
    @Excel(name = "视频平均播放量", width = 15)
    @Schema(title =  "视频平均播放量")
    private Integer avgPlayCount;
    /**
     * 视频平均评论数
     */
    @Excel(name = "视频平均评论数", width = 15)
    @Schema(title =  "视频平均评论数")
    private Integer avgCommentCount;
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
     * 性別（0：男；1：女）
     */
    @Excel(name = "性別（0：男；1：女）", width = 15)
    @Schema(title =  "性別（0：男；1：女）")
    private Integer gender;
    /**
     * 个人站点
     */
    @Excel(name = "个人站点", width = 15)
    @Schema(title =  "个人站点")
    private String website;
    /**
     * instagram账号
     */
    @Excel(name = "instagram账号", width = 15)
    @Schema(title =  "instagram账号")
    private String tiktokAccount;
    /**
     * youtube账号
     */
    @Excel(name = "youtube账号", width = 15)
    @Schema(title =  "youtube账号")
    private String youtubeAccount;
    /**
     * linktree地址
     */
    @Excel(name = "linktree地址", width = 15)
    @Schema(title =  "linktree地址")
    private String linktree;
    /**
     * 粉丝数大于10000=1
     */
    @Excel(name = "粉丝数大于10000=1", width = 15)
    @Schema(title =  "粉丝数大于10000=1")
    private Integer isHighFollower;
    /**
     * 删除状态（0=未删除,1=已删除）
     */
    @Excel(name = "删除状态（0=未删除,1=已删除）", width = 15)
    @Schema(title =  "删除状态（0=未删除,1=已删除）")
    private Integer isDelete;
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
    @Excel(name = "创建时间", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
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
    @Excel(name = "修改时间", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(title =  "修改时间")
    private Date updateTime;

    private String account;
}
