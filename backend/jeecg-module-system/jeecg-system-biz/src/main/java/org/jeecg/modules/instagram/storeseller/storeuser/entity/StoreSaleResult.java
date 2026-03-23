package org.jeecg.modules.instagram.storeseller.storeuser.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: storeSaleResult
 * @Author: jeecg-boot
 * @Date:   2020-10-16
 * @Version: V1.0
 */
@Data
@TableName("store_sale_result")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class StoreSaleResult implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
	/**网红ID*/
	@Excel(name = "网红ID", width = 15)
    @Schema(title =  "网红ID")
    private String celebrityId;
	/**类目ID*/
	@Excel(name = "类目ID", width = 15)
    @Schema(title =  "类目ID")
    private String categoryId;
	/**类目名称*/
	@Excel(name = "类目名称", width = 15)
    @Schema(title =  "类目名称")
    private String categoryName;
	/** 带货能力分值*/
	@Excel(name = " 带货能力分值", width = 15)
    @Schema(title =  " 带货能力分值")
    private Integer saleScore;
	/**本次销售单数*/
	@Excel(name = "本次销售单数", width = 15)
    @Schema(title =  "本次销售单数")
    private Integer saleNum;
	/**当前粉丝量*/
	@Excel(name = "当前粉丝量", width = 15)
    @Schema(title =  "当前粉丝量")
    private Integer followersNum;
	/**商品标题 */
	@Excel(name = "商品标题 ", width = 15)
    @Schema(title =  "商品标题 ")
    private String productTitle;
	/**商品图片链接*/
	@Excel(name = "商品图片链接", width = 15)
    @Schema(title =  "商品图片链接")
    private String productImageUrl;
	/**销售日期*/
	@Excel(name = "销售日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(title =  "销售日期")
    private Date saleDate;
	/**销售结果截图*/
	@Excel(name = "销售结果截图", width = 15)
    @Schema(title =  "销售结果截图")
    private String saleImageUrl;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @Schema(title =  "备注")
    private String remark;
	/**创建人*/
	@Excel(name = "创建人", width = 15)
    @Schema(title =  "创建人")
    private String createBy;
	/**创建时间*/
	@Excel(name = "创建时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(title =  "创建时间")
    private Date createTime;
	/**修改人*/
	@Excel(name = "修改人", width = 15)
    @Schema(title =  "修改人")
    private String updateBy;
	/**修改时间*/
	@Excel(name = "修改时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(title =  "修改时间")
    private Date updateTime;

    /**一级类目ID*/
    @Excel(name = "一级类目ID", width = 15)
    @Schema(title =  "类目ID")
    private String categoryParentId;
    /**类目名称*/
    @Excel(name = "一级类目名称", width = 15)
    @Schema(title =  "类目名称")
    private String categoryParentName;
}
