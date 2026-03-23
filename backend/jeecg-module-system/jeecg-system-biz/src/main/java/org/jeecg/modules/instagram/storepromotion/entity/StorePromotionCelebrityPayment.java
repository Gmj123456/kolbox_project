package org.jeecg.modules.instagram.storepromotion.entity;

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
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 网红支付列表
 * @Author: jeecg-boot
 * @Date: 2021-09-27
 * @Version: V1.0
 */
@Data
@TableName("store_promotion_celebrity_payment")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StorePromotionCelebrityPayment {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
    /**
     * 商家ID
     */
    @Excel(name = "商家ID", width = 15)
    @Schema(title =  "商家ID")
    private String sellerId;
    /**
     * 商家昵称
     */
    @Excel(name = "商家昵称", width = 15)
    @Schema(title =  "商家昵称")
    private String sellerName;
    /**
     * 推广单号
     */
    @Excel(name = "推广单号", width = 15)
    @Schema(title =  "推广单号")
    private String extensionCode;
    /**
     * 推广ID
     */
    @Excel(name = "推广ID", width = 15)
    @Schema(title =  "推广ID")
    private String promId;
    /**
     * 产品ID
     */
    @Excel(name = "产品ID", width = 15)
    @Schema(title =  "产品ID")
    private String goodsId;
    /**
     * 私有网红ID
     */
    @Excel(name = "私有网红ID", width = 15)
    @Schema(title =  "私有网红ID")
    private String celebrityPrivateId;
    /**
     * 账号
     */
    @Excel(name = "账号", width = 15)
    @Schema(title =  "账号")
    private String celebrityAccount;
    /**
     * 支付金额
     */
    @Excel(name = "支付金额", width = 15)
    @Schema(title =  "支付金额")
    private java.math.BigDecimal payAmount;
    /**
     * 货币符号
     */
    @Excel(name = "货币符号", width = 15)
    @Schema(title =  "货币符号")
    private String currencySymbol;
    /**
     * 当前汇率
     */
    @Excel(name = "当前汇率", width = 15)
    @Schema(title =  "当前汇率")
    private java.math.BigDecimal exchangeRate;
    /**
     * 支付金额（RMB）
     */
    @Excel(name = "支付金额（RMB）", width = 15)
    @Schema(title =  "支付金额（RMB）")
    private java.math.BigDecimal payRmbAmount;
    /**
     * 付款类型:0:定金；4：全款
     */
    @Excel(name = "付款类型:0:定金；4：全款", width = 15)
    @Schema(title =  "付款类型:0:定金；4：全款")
    private Integer payType;
    /**
     * 支付方式0=支付宝,1=微信 2=余额
     */
    @Excel(name = "支付方式0=支付宝,1=微信 2=余额", width = 15)
    @Schema(title =  "支付方式0=支付宝,1=微信 2=余额")
    @Dict(dicCode = "pay_way")
    private Integer payWay;
    /**
     * 是否线下支付（0：否（默认）；1：是）
     */
    @Excel(name = "是否线下支付（0：否（默认）；1：是）", width = 15)
    @Schema(title =  "是否线下支付（0：否（默认）；1：是）")
    private Integer isOffline;
    /**
     * 支付交易流水号
     */
    @Excel(name = "支付交易流水号", width = 15)
    @Schema(title =  "支付交易流水号")
    private String payTradeCode;
    /**
     * 支付时间
     */
    @Excel(name = "支付时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "支付时间")
    private Date payTime;
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
