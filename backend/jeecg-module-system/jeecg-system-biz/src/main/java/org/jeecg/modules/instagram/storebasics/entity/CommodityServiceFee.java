package org.jeecg.modules.instagram.storebasics.entity;

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
 * @Description: 商品服务费
 * @Author: jeecg-boot
 * @Date: 2021-04-13
 * @Version: V1.0
 */
@Data
@TableName("commodity_service_fee")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CommodityServiceFee {

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "id")
    private String id;
    /**
     * 类型
     */
    @Excel(name = "类型", width = 15)
    @Schema(title =  "类型")
    private String category;
    /**
     * 服务费
     */
    @Excel(name = "服务费", width = 15)
    @Schema(title =  "服务费")
    private java.math.BigDecimal rate;
    /**
     * 固定费用
     */
    @Excel(name = "固定费用", width = 15)
    @Schema(title =  "固定费用")
    private java.math.BigDecimal fixedCharges;
    /**
     * 国家
     */
    @Excel(name = "国家", width = 15)
    @Schema(title =  "国家")
    private String country;
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
    private java.util.Date createTime;
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
    private java.util.Date updateTime;

    /**汇率*/
    @Excel(name = "汇率", width = 15)
    @Schema(title =  "汇率")
    private java.math.BigDecimal exchangeRate;


    /**
     * 货币类型
     */
    @Excel(name = "货币类型", width = 15)
    @Schema(title =  "货币类型")
    private String currencyType;

    /**
     * 货币符号
     */
    @Excel(name = "货币符号", width = 15)
    @Schema(title =  "货币符号")
    private String symbol;
}
