package org.jeecg.modules.tiktok.entity;


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
 * @Description: 国家表
 * @Author: dongruyang
 * @Date:   2023-10-11
 * @Version: V1.0
 */
@Data
@TableName("tiktok_country")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description="国家表")
public class TiktokCountry {
    
	/**主键*/
	@TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
	private String id;
	/**国家名称*/
	@Excel(name = "国家名称", width = 15)
    @Schema(title =  "国家名称")
	private String country;
	/**站点URL*/
	@Excel(name = "站点URL", width = 15)
    @Schema(title =  "站点URL")
	private String siteUrl;
	/**货币类型主键*/
	@Excel(name = "货币类型主键", width = 15)
    @Schema(title =  "货币类型主键")
	private String currencyTypeId;
	/**国际化语言*/
	@Excel(name = "国际化语言", width = 15)
    @Schema(title =  "国际化语言")
	private String languageName;
	/**排序码*/
	@Excel(name = "排序码", width = 15)
    @Schema(title =  "排序码")
	private Integer sortCode;
	/**删除标记0=正常,1=已删除*/
	@Excel(name = "删除标记0=正常,1=已删除", width = 15)
    @Schema(title =  "删除标记0=正常,1=已删除")
	private Integer isDelete;
	/**有效标志0:否；1：是*/
	@Excel(name = "有效标志0:否；1：是", width = 15)
    @Schema(title =  "有效标志0:否；1：是")
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
	/**货币名称*/
	@Excel(name = "货币名称", width = 15)
    @Schema(title =  "货币名称")
	private String currencyTypeName;
	/**paypal费率*/
	@Excel(name = "paypal费率", width = 15)
    @Schema(title =  "paypal费率")
	private java.math.BigDecimal paypalRate;
	/**paypal每笔转账固定费用*/
	@Excel(name = "paypal每笔转账固定费用", width = 15)
    @Schema(title =  "paypal每笔转账固定费用")
	private java.math.BigDecimal paypalBaseFees;
	/**名称简称*/
	@Excel(name = "名称简称", width = 15)
    @Schema(title =  "名称简称")
	private String shortName;
	/**英文简称*/
	@Excel(name = "英文简称", width = 15)
    @Schema(title =  "英文简称")
	private String shortCode;
	/**是否推广展示（0：否（默认）；1：是）*/
	@Excel(name = "是否推广展示（0：否（默认）；1：是）", width = 15)
    @Schema(title =  "是否推广展示（0：否（默认）；1：是）")
	private Integer isPromotion;
}
