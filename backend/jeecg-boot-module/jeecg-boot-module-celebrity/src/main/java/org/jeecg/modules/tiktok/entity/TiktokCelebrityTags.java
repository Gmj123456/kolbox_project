package org.jeecg.modules.tiktok.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigInteger;
import java.util.Date;

/**
 * @Description: 网红tag表
 * @Author: xyc
 * @Date: 2023-10-10
 * @Version: V1.0
 */
@Data
@TableName("tiktok_celebrity_tags")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TiktokCelebrityTags {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
    /**
     * 网红唯一id
     */
    @Excel(name = "网红唯一id", width = 15)
    @Schema(title =  "网红唯一id")
    private String uniqueId;
    private String authorId;
    /**
     * tag名称
     */
    @Excel(name = "tag名称", width = 15)
    @Schema(title =  "tag名称")
    private String tagName;

    /**
     * tag类型（0=hashtag 1=keyword 2=rootVideo 3=rootAccount）
     */
    @Excel(name = "tag类型：0=hashtag 1=keyword 2=rootVideo 3=rootAccount", width = 15)
    @Schema(title =  "tag类型：0=hashtag 1=keyword 2=rootVideo 3=rootAccount")
    private Integer tagType;

    /**
     * 是否分配 (0=未分配,1=已分配)
     */
    @Excel(name = "是否分配 (0=未分配,1=已分配)", width = 15)
    @Schema(title =  "是否分配 (0=未分配,1=已分配)")
    private Integer isAllot;
    /**
     * 网红顾问_ID
     */
    @Excel(name = "网红顾问_ID", width = 15)
    @Schema(title =  "网红顾问_ID")
    private String celebrityCounselorId;
    /**
     * 网红顾问名称
     */
    @Excel(name = "网红顾问名称", width = 15)
    @Schema(title =  "网红顾问名称")
    private String celebrityCounselorName;
    /**
     * 分配时间
     */
    @Excel(name = "分配时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "分配时间")
    private Date allotTime;
    /**
     * 视频id
     */
    @Excel(name = "视频id", width = 15)
    @Schema(title =  "视频id")
    private String videoId;
    /**
     * 视频创建时间
     */
    @Excel(name = "视频创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "视频创建时间")
    private Date videoCreateTime;
    /**
     * 视频描述
     */
    @Excel(name = "视频描述", width = 15)
    @Schema(title =  "视频描述")
    private String videoDesc;
    /**
     * 视频封面
     */
    @Excel(name = "视频封面", width = 15)
    @Schema(title =  "视频封面")
    private Object videoCover;
    /**
     * 视频收藏数
     */
    @Excel(name = "视频收藏数", width = 15)
    @Schema(title =  "视频收藏数")
    private Integer videoCollectCount;
    /**
     * 视频评论数
     */
    @Excel(name = "视频评论数", width = 15)
    @Schema(title =  "视频评论数")
    private Integer videoCommentCount;
    /**
     * 视频点赞数
     */
    @Excel(name = "视频点赞数", width = 15)
    @Schema(title =  "视频点赞数")
    private Integer videoDiggCount;
    /**
     * 视频播放数
     */
    @Excel(name = "视频播放数", width = 15)
    @Schema(title =  "视频播放数")
    private Integer videoPlayCount;
    /**
     * 视频分享数
     */
    @Excel(name = "视频分享数", width = 15)
    @Schema(title =  "视频分享数")
    private Integer videoShareCount;
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
     * 删除状态（0=未删除,1=已删除）
     */
    @Excel(name = "删除状态（0=未删除,1=已删除）", width = 15)
    @Schema(title =  "删除状态（0=未删除,1=已删除）")
    @TableLogic
    private Integer isDelete;

    /**
     * 分配日志ID
     */
    private String allotLogId;

    /**
     * 分配日志明细ID
     */
    private String allotLogDetailId;
    /**
     * 平台类型 0:TK;1:YT;2:IG
     */
    private Integer platformType;

    /**
     * 网红国家(编码code)
     */
    @Excel(name = "网红国家", width = 15)
    @Schema(title =  "网红国家")
    private String country;
    /**
     * 网红粉丝数
     */
    @Excel(name = "网红粉丝数", width = 15)
    @Schema(title =  "网红粉丝数")

    private BigInteger authorFollowerCount;
}
