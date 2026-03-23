package org.jeecg.modules.instagram.storeseller.category.entity;

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
 * @Description: 商家商品类目表
 * @Author: jeecg-boot
 * @Date:   2020-10-12
 * @Version: V1.0
 */
@Data
@TableName("store_goods_category")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreGoodsCategory {
    
	/**主键*/
	@TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
	private String id;
	/**父类目ID（根目录0）*/
	@Excel(name = "父类目ID（根目录0）", width = 15)
    @Schema(title =  "父类目ID（根目录0）")
	private String parentId;
	/** 类目名称*/
	@Excel(name = " 类目名称", width = 15)
    @Schema(title =  " 类目名称")
	private String categoryName;
	/** 类目编码*/
	@Excel(name = " 类目编码", width = 15)
    @Schema(title =  " 类目编码")
	private String categoryCode;
	/** 类目类型（0：一级类目:1：子类目）*/
	@Excel(name = " 类目类型（0：一级类目:1：子类目）", width = 15)
    @Schema(title =  " 类目类型（0：一级类目:1：子类目）")
	private Integer categoryType;
	/**排序码*/
	@Excel(name = "排序码", width = 15)
    @Schema(title =  "排序码")
	private Integer sortCode;
	/**状态 0：正常 ；-1：异常*/
	@Excel(name = "状态 0：正常 ；-1：异常", width = 15)
    @Schema(title =  "状态 0：正常 ；-1：异常")
	private Integer status;
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

	/**是否叶子节点:    1:是   0:不是*/
	@Excel(name = "是否叶子节点:    1:是   0:不是", width = 15)
	@Schema(title =  "是否叶子节点:    1:是   0:不是")
	private boolean isLeaf;

	/**是否叶子节点:    1:是   0:不是*/
	@Excel(name = "删除状态(0:正常,1:已删除)", width = 15)
	@Schema(title =  "删除状态(0:正常,1:已删除)")
	private boolean delFlag;
}
