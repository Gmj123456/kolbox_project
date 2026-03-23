package org.jeecg.modules.instagram.storecelebrity.entity;

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

import java.util.Date;

/**
 * @Description: 网红报价明细
 * @Author: jeecg-boot
 * @Date:   2020-09-29
 * @Version: V1.0
 */
@Data
@TableName("store_celebrity_price")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreCelebrityPrice {
    
	/**主键*/
	@TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
	private String id;
	/**网红ID*/
	@Excel(name = "网红ID", width = 15)
    @Schema(title =  "网红ID")
	private String celebrityId;
	/**story价格*/
	@Excel(name = "story价格", width = 15)
    @Schema(title =  "story价格")
	private java.math.BigDecimal igStoryPrice;
	/**post价格*/
	@Excel(name = "post价格", width = 15)
    @Schema(title =  "post价格")
	private java.math.BigDecimal igPostPrice;
	/**story加post价格*/
	@Excel(name = "story加post价格", width = 15)
    @Schema(title =  "story加post价格")
	private java.math.BigDecimal igStoryPostPrice;
	/**BlogPost价格*/
	@Excel(name = "BlogPost价格", width = 15)
    @Schema(title =  "BlogPost价格")
	private java.math.BigDecimal blogPostPrice;
	/**YoutubeVideo价格*/
	@Excel(name = "YoutubeVideo价格", width = 15)
    @Schema(title =  "YoutubeVideo价格")
	private java.math.BigDecimal youtubeVideoPrice;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @Schema(title =  "备注")
	private String remark;
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
	/**修改人*/
	@Excel(name = "修改人", width = 15)
    @Schema(title =  "修改人")
	private String updateBy;
	/**修改时间*/
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "修改时间")
	private Date updateTime;
}
