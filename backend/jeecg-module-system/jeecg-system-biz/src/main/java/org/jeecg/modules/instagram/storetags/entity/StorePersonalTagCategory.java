package org.jeecg.modules.instagram.storetags.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Description: 个性化标签类目关联表
 * @Author: jeecg-boot
 * @Date:   2025-04-27
 * @Version: V1.0
 */
@Data
@TableName("store_personal_tag_category")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StorePersonalTagCategory {
    
	/**主键*/
	@TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
	private String id;
	/**个性标签ID*/
	@Excel(name = "个性标签ID", width = 15)
    @Schema(title =  "个性标签ID")
	private String tagId;
	/**标签分类ID*/
	@Excel(name = "标签分类ID", width = 15)
    @Schema(title =  "标签分类ID")
	private String categoryId;
	/**创建人*/
	@Excel(name = "创建人", width = 15)
    @Schema(title =  "创建人")
	private String createBy;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "创建时间")
	private java.util.Date createTime;
	/**更新人*/
	@Excel(name = "更新人", width = 15)
    @Schema(title =  "更新人")
	private String updateBy;
	/**更新时间*/
	@Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "更新时间")
	private java.util.Date updateTime;
	/**删除标志 0=正常,1=已删除*/
	@Excel(name = "删除标志 0=正常,1=已删除", width = 15)
    @Schema(title =  "删除标志 0=正常,1=已删除")
	private Integer delFlag;
	@TableField(exist = false)
	private String tagName;
}
