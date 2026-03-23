package org.jeecg.modules.instagram.storecelebrity.entity;

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
 * @Description: 网红销售明细
 * @Author: jeecg-boot
 * @Date: 2020-11-12
 * @Version: V1.0
 */
@Data
@TableName("store_sell_detail")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreSellDetail {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
    /**
     * 私有网红ID
     */
    @Excel(name = "私有网红ID", width = 15)
    @Schema(title =  "私有网红ID")
    private String celebrityPrivateId;
    /**
     * 私有网红结算表ID
     */
    @Excel(name = "私有网红结算表ID", width = 15)
    @Schema(title =  "私有网红结算表ID")
    private String settlementId;
    /**
     * 商品ID
     */
    @Excel(name = "商品ID", width = 15)
    @Schema(title =  "商品ID")
    private String goodsId;
    /**
     * 年份
     */
    @Excel(name = "年份", width = 15)
    @Schema(title =  "年份")
    private Integer year;
    /**
     * 月份
     */
    @Excel(name = "月份", width = 15)
    @Schema(title =  "月份")
    private Integer month;
    /**
     * 商品code码
     */
    @Excel(name = "商品code码", width = 15)
    @Schema(title =  "商品code码")
    private String goodsCode;
    /**
     * code码开始日期
     */
    @Excel(name = "code码开始日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "code码开始日期")
    private Date codeStartTime;
    /**
     * code码结束日期
     */
    @Excel(name = "code码结束日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "code码结束日期")
    private Date codeEndTime;
    /**
     * code码折扣
     */
    @Excel(name = "code码折扣", width = 15)
    @Schema(title =  "code码折扣")
    private java.math.BigDecimal codeDiscountRate;
    /**
     * 变体属性
     */
    @Excel(name = "变体属性", width = 15)
    @Schema(title =  "变体属性")
    private String goodsProperty;
    /**
     * 推广时间
     */
    @Excel(name = "推广时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "推广时间")
    private Date shareTime;
    /**
     * 结算状态
     */
    @Excel(name = " 结算状态", width = 15)
    @Schema(title =  " 结算状态")
    private Integer settlementStatus;
    /**
     * 结算佣金
     */
    @Excel(name = " 结算佣金", width = 15)
    @Schema(title =  " 结算佣金")
    private java.math.BigDecimal settlementAmount;
    /**
     * 结算日期
     */
    @Excel(name = "结算日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "结算日期")
    private Date settlementTime;

    /**
     * 商家顾问ID
     */
    @Excel(name = " 商家顾问ID", width = 15)
    @Schema(title =  " 商家顾问ID")
    private String sellerCounselorId;
    /**
     * 商家顾问名称
     */
    @Excel(name = " 商家顾问名称", width = 15)
    @Schema(title =  " 商家顾问名称")
    private String sellerCounselorName;

    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @Schema(title =  "备注")
    private String remark;
    /**
     * 创建人
     */
    @Excel(name = "创建人", width = 15)
    @Schema(title =  "创建人")
    private String createBy;
    /**
     * 创建时间
     */
    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "创建时间")
    private Date createTime;
    /**
     * 修改人
     */
    @Excel(name = "修改人", width = 15)
    @Schema(title =  "修改人")
    private String updateBy;
    /**
     * 修改时间
     */
    @Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "修改时间")
    private Date updateTime;
}
