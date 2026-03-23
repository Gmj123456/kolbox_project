package org.jeecg.modules.instagram.storebasics.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 货币表
 * @Author: jeecg-boot
 * @Date:   2020-10-06
 * @Version: V1.0
 */
@Data
@TableName("store_currency_type")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreCurrencyType {

    /**主键*/
	@TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
	private String id;
	/**类型名称*/
	@Excel(name = "类型名称", width = 15)
    @Schema(title =  "类型名称")
	private String typeName;
	/**货币符号*/
	@Excel(name = "货币符号", width = 15)
    @Schema(title =  "货币符号")
	private String symbol;
	/**汇率*/
	@Excel(name = "汇率", width = 15)
    @Schema(title =  "汇率")
	private java.math.BigDecimal exchangeRate;
	/**排序码*/
	@Excel(name = "排序码", width = 15)
    @Schema(title =  "排序码")
	private Integer sortCode;
	/**删除标记0=正常,1=已删除*/
	@Excel(name = "删除标记0=正常,1=已删除", width = 15)
    @Schema(title =  "删除标记0=正常,1=已删除")
	private Integer isDelete;
	/**有效标志0=是,1=否*/
	@Excel(name = "有效标志0=是,1=否", width = 15)
    @Schema(title =  "有效标志0=是,1=否")
	private Integer isEnabled;
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
