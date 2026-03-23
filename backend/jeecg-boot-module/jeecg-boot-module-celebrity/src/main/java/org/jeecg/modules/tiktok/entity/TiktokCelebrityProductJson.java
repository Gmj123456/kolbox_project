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
 * @Description: TikTok网红产品JSON原始数据表
 * @Author: nqr
 * @Date:   2025-10-08
 * @Version: V1.0
 */
@Data
@TableName("tiktok_celebrity_product_json")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description="TikTok网红产品JSON原始数据表")
public class TiktokCelebrityProductJson {
    
	/**主键ID*/
	@TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键ID")
	private String id;
	/**产品ID*/
	@Excel(name = "产品ID", width = 15)
    @Schema(title =  "产品ID")
	private String productId;
	/**作者ID*/
	@Excel(name = "作者ID", width = 15)
    @Schema(title =  "作者ID")
	private String authorId;
	/**安全用户ID*/
	@Excel(name = "安全用户ID", width = 15)
    @Schema(title =  "安全用户ID")
	private String secUid;
	/**产品JSON原始数据*/
	@Excel(name = "产品JSON原始数据", width = 15)
    @Schema(title =  "产品JSON原始数据")
	private Object jsonData;
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
