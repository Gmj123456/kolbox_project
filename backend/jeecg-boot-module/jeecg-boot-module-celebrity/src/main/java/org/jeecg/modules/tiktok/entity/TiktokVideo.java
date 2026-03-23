package org.jeecg.modules.tiktok.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: Tiktok视频获取
 * @Author: dongruyang
 * @Date:   2024-05-30
 * @Version: V1.0
 */
@Data
@TableName("tiktok_video")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description="Tiktok视频获取")
public class TiktokVideo {
    
	/**主键*/
	@TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
	private String id;
	/**网红唯一id*/
	@Excel(name = "网红账号", width = 15)
	@Schema(title =  "网红账号")
	private String uniqueId;

	/**视频链接*/
	@Excel(name = "视频链接", width = 50)
    @Schema(title =  "视频链接")
	private String videoUrl;

	/**作者id*/
    @Schema(title =  "作者id")
	private String authorId;
	/**secUid*/
    @Schema(title =  "secUid")
	private String secUid;
	/**作者昵称*/
    @Schema(title =  "作者昵称")
	private String nickName;
	/**视频id*/
    @Schema(title =  "视频id")
	private String videoId;
	/**视频创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "创建时间")
	@TableField(update = "")
	private Date videoCreateTime;
	/**视频描述*/
	@Excel(name = "视频标题", width = 50)
    @Schema(title =  "视频描述")
	private Object videoDesc;
	/**视频收藏数*/
	@Excel(name = "收藏数", width = 15)
    @Schema(title =  "视频收藏数")
	private Integer videoCollectCount;
	/**视频评论数*/
	@Excel(name = "评论数", width = 15)
    @Schema(title =  "视频评论数")
	private Integer videoCommentCount;
	/**视频点赞数*/
	@Excel(name = "点赞数", width = 15)
    @Schema(title =  "视频点赞数")
	private Integer videoDiggCount;
	/**视频播放数*/
	@Excel(name = "播放数", width = 15)
    @Schema(title =  "视频播放数")
	private Integer videoPlayCount;
	/**视频分享数*/
	@Excel(name = "分享数", width = 15)
    @Schema(title =  "视频分享数")
	private Integer videoShareCount;
	/**备注*/
    @Schema(title =  "备注")
	private String remark;
	/**创建人*/
    @Schema(title =  "创建人")
	private String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "创建时间")
	private Date createTime;
	/**修改人*/
    @Schema(title =  "修改人")
	private String updateBy;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "修改时间")
	private Date updateTime;
	/**用户ID*/
    @Schema(title =  "用户ID")
	private String userId;
	/**用户名称*/
    @Schema(title =  "用户名称")
	private String userName;
	/**导入批次号*/
    @Schema(title =  "导入批次号")
	private String batchNo;
}
