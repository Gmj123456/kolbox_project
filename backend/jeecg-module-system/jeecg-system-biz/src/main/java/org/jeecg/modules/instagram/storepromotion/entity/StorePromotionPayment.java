package org.jeecg.modules.instagram.storepromotion.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 商家推广支付记录
 * @Author: dong
 * @Date: 2021-03-09
 * @Version: V1.0
 */
@Data
@TableName("store_promotion_payment")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StorePromotionPayment {


    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;

    /**
     * 商家ID
     */
    @Schema(title =  "商家ID")
    private String sellerId;

    /**
     * 商家昵称
     */
    @Excel(name = "商家账号", width = 15,orderNum = "8")
    @Schema(title =  "商家账号")
    private String sellerName;

    /**
     * 推广ID
     */
    @Schema(title =  "推广ID")
    private String promId;

    /**
     * 推广单号
     */
    @Excel(name = "推广单号", width = 15,orderNum="2")
    @Schema(title =  "推广单号")
    private String extensionCode;


    /**
     * 支付金额
     */
    @Schema(title =  "支付金额")
    private java.math.BigDecimal payAmount;

    /**
     * 支付金额
     */
    @Excel(name = "支付金额(¥)", width = 15,orderNum = "14")
    @Schema(title =  "支付金额(rmb)")
    private java.math.BigDecimal payRmbAmount;

    /**
     * 货币符号
     */
    @Schema(title =  "货币符号")
    private String currencySymbol;

    /**
     * 支付时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yy-MM-dd HH:mm:ss")
    @Schema(title =  "支付时间")
    @Excel(name = "支付时间", width = 20, format = "yy-MM-dd HH:mm:ss",orderNum = "16")
    private Date payTime;


    /**
     * 备注
     */
    @Excel(name = "备注", width = 50,orderNum = "18")
    @Schema(title =  "备注")
    private String remark;

    /**
     * 创建人
     */
    @Schema(title =  "创建人")
    private String createBy;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "创建时间")
    private Date createTime;
    /**
     * 修改人
     */
    @Schema(title =  "修改人")
    private String updateBy;

    /**
     * 修改时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "修改时间")
    private Date updateTime;

    /**
     * 支付类型
     */
    @Dict(dicCode = "pay_type")
    @Schema(title =  "支付类型")
    @Excel(name = "支付类型",width = 10,orderNum = "13",dicCode = "pay_type")
    private Integer payType;

    /**
     * 支付方式
     */
    @Dict(dicCode = "pay_way")
    @Excel(name = "支付方式",width = 10,orderNum = "15",replace = {"支付宝_0","微信_1","余额_2"})
    @Schema(title =  "支付方式")
    private Integer payWay;


    /**
     * 商家支付时间
     */
    @Schema(title =  "商家支付时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date consumeDate;

    /**
     * 支付交易流水号
     */
    @Schema(title =  "支付交易流水号")
    private String payTradeCode;

    /**
     * 是否线下支付（0：否（默认）；1：是）
     */
    @Schema(title =  "是否线下支付")
    @Excel(name = "线上支付", width = 10,orderNum = "9",replace = {"是_0","否_1"})
    private Integer isOffline;


}
