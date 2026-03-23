package org.jeecg.modules.tiktok.entity;

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
 * @Description: TikTok网红产品与视频关联表
 * @Author: nqr
 * @Date:   2025-10-08
 * @Version: V1.0
 */
@Data
@TableName("tiktok_celebrity_product_video")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description="TikTok网红产品与视频关联表")
public class TiktokCelebrityProductVideo {
    
	/**主键ID*/
	@TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键ID")
	private String id;
	/**产品ID*/
	@Excel(name = "产品ID", width = 15)
    @Schema(title =  "产品ID")
	private String productId;
	/**视频ID*/
	@Excel(name = "视频ID", width = 15)
    @Schema(title =  "视频ID")
	private String videoId;
	/**用户唯一ID*/
	@Excel(name = "用户唯一ID", width = 15)
    @Schema(title =  "用户唯一ID")
	private String uniqueId;
	/**作者ID*/
	@Excel(name = "作者ID", width = 15)
    @Schema(title =  "作者ID")
	private String authorId;
	/**安全用户ID*/
	@Excel(name = "安全用户ID", width = 15)
    @Schema(title =  "安全用户ID")
	private String secUid;
	/**视频封面URL*/
	@Excel(name = "视频封面URL", width = 15)
    @Schema(title =  "视频封面URL")
	private String cover;
	/**视频描述*/
	@Excel(name = "视频描述", width = 15)
    @Schema(title =  "视频描述")
	private Object videoDesc;
	/**视频创建时间戳*/
	@Excel(name = "视频创建时间戳", width = 15)
    @Schema(title =  "视频创建时间戳")
	private Integer videoCreateTime;
	/**视频时长(秒)*/
	@Excel(name = "视频时长(秒)", width = 15)
    @Schema(title =  "视频时长(秒)")
	private Integer duration;
	/**播放数*/
	@Excel(name = "播放数", width = 15)
    @Schema(title =  "播放数")
	private Integer playCount;
	/**点赞数*/
	@Excel(name = "点赞数", width = 15)
    @Schema(title =  "点赞数")
	private Integer diggCount;
	/**评论数*/
	@Excel(name = "评论数", width = 15)
    @Schema(title =  "评论数")
	private Integer commentCount;
	/**视频URL*/
	@Excel(name = "视频URL", width = 15)
    @Schema(title =  "视频URL")
	private String videoUrl;
	/**互动率(百分比)*/
	@Excel(name = "互动率(百分比)", width = 15)
    @Schema(title =  "互动率(百分比)")
	private java.math.BigDecimal engagementRate;
	/**创建人*/
	@Excel(name = "创建人", width = 15)
    @Schema(title =  "创建人")
	private String createBy;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "创建时间")
	private Date createTime;
	/**更新人*/
	@Excel(name = "更新人", width = 15)
    @Schema(title =  "更新人")
	private String updateBy;
	/**更新时间*/
	@Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "更新时间")
	private Date updateTime;
	/**逻辑删除标志(0:未删除，1:删除)*/
	@Excel(name = "逻辑删除标志(0:未删除，1:删除)", width = 15)
    @Schema(title =  "逻辑删除标志(0:未删除，1:删除)")
	private Integer delFlag;
}
