package org.jeecg.modules.kol.entity;

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

/**
 * @Description: 标签产品管理表
 * @Author: nqr
 * @Date:   2025-06-28
 * @Version: V1.0
 */
@Data
@TableName("kol_tag_product")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description="标签产品管理表")
public class KolTagProduct {
    
	/**主键*/
	@TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
	private String id;
	/**标签ID*/
	@Excel(name = "标签ID", width = 15)
    @Schema(title =  "标签ID")
	private String tagId;
	/**tag名称*/
	@Excel(name = "tag名称", width = 15)
    @Schema(title =  "tag名称")
	private String tagName;
	/**品牌ID*/
	@Excel(name = "品牌ID", width = 15)
    @Schema(title =  "品牌ID")
	private String brandId;
	/**品牌名称*/
	@Excel(name = "品牌名称", width = 15)
    @Schema(title =  "品牌名称")
	private String brandName;
	/**产品备ID*/
	@Excel(name = "产品备ID", width = 15)
    @Schema(title =  "产品备ID")
	private String productId;
	/**产品名称*/
	@Excel(name = "产品名称", width = 15)
    @Schema(title =  "产品名称")
	private String productName;
	/**平台类型（2:TK;1:YT;0:IG）*/
	@Excel(name = "平台类型（2:TK;1:YT;0:IG）", width = 15)
    @Schema(title =  "平台类型（2:TK;1:YT;0:IG）")
	private Integer platformType;
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
	private java.util.Date createTime;
	/**修改人*/
	@Excel(name = "修改人", width = 15)
    @Schema(title =  "修改人")
	private String updateBy;
	/**修改时间*/
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "修改时间")
	private java.util.Date updateTime;
	/**删除状态（0=未删除,1=已删除）*/
	@Excel(name = "删除状态（0=未删除,1=已删除）", width = 15)
    @Schema(title =  "删除状态（0=未删除,1=已删除）")
	private Integer isDelete;
}
