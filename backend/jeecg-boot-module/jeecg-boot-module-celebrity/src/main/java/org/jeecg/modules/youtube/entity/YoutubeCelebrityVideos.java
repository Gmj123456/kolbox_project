package org.jeecg.modules.youtube.entity;

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
 * @Description: YT网红视频
 * @Author: nqr
 * @Date: 2023-11-22
 * @Version: V1.0
 */
@Data
@TableName("youtube_celebrity_videos")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class YoutubeCelebrityVideos {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
    /**
     * 视频ID
     */
    @Excel(name = "视频ID", width = 15)
    @Schema(title =  "视频ID")
    private String videoId;
    /**
     * YouTube网红唯一ID
     */
    @Excel(name = "YouTube网红唯一ID", width = 15)
    @Schema(title =  "YouTube网红唯一ID")
    private String account;
    /**
     * 发布时间
     */
    @Excel(name = "发布时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "发布时间")
    private Date publishedAt;
    /**
     * 视频状态（是否私有）
     */
    @Excel(name = "视频状态（是否私有）", width = 15)
    @Schema(title =  "视频状态（是否私有）")
    private String videoStatus;
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
    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
    @Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "修改时间")
    private Date updateTime;
    /**
     * 视频标题
     */
    @Excel(name = "视频标题", width = 15)
    @Schema(title =  "视频标题")
    private String title;
    /**
     * 视频描述
     */
    @Excel(name = "视频描述", width = 15)
    @Schema(title =  "视频描述")
    private Object description;
    /**
     * 视频缩略图URL
     */
    @Excel(name = "视频缩略图URL", width = 15)
    @Schema(title =  "视频缩略图URL")
    private String thumbnailsUrl;
    /**
     * 视频缩略图宽度
     */
    @Excel(name = "视频缩略图宽度", width = 15)
    @Schema(title =  "视频缩略图宽度")
    private Integer thumbnailsWidth;
    /**
     * 视频缩略图高度
     */
    @Excel(name = "视频缩略图高度", width = 15)
    @Schema(title =  "视频缩略图高度")
    private Integer thumbnailsHeight;
    /**
     * 视频Tags
     */
    @Excel(name = "视频Tags", width = 15)
    @Schema(title =  "视频Tags")
    private Object tags;
    /**
     * 视频类别
     */
    @Excel(name = "视频类别", width = 15)
    @Schema(title =  "视频类别")
    private String categoryid;
    /**
     * 视频观看次数
     */
    @Excel(name = "视频观看次数", width = 15)
    @Schema(title =  "视频观看次数")
    private Integer viewcount;
    /**
     * 喜欢该视频的用户数量
     */
    @Excel(name = "喜欢该视频的用户数量", width = 15)
    @Schema(title =  "喜欢该视频的用户数量")
    private Integer likecount;
    /**
     * 不喜欢视频的用户数量
     */
    @Excel(name = "不喜欢视频的用户数量", width = 15)
    @Schema(title =  "不喜欢视频的用户数量")
    private Integer dislikecount;
    /**
     * 视频的评论数
     */
    @Excel(name = "视频的评论数", width = 15)
    @Schema(title =  "视频的评论数")
    private Integer commentcount;
    /**
     * 任务主键
     */
    @Excel(name = "任务主键", width = 15)
    @Schema(title =  "任务主键")
    private String taskId;
}
