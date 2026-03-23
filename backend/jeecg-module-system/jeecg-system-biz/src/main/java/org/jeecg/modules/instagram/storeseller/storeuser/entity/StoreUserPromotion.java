package org.jeecg.modules.instagram.storeseller.storeuser.entity;

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
 * @Description: 商家产品促销
 * @Author: jeecg-boot
 * @Date: 2021-05-21
 * @Version: V1.0
 */
@Data
@TableName("store_user_promotion")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreUserPromotion {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
    /**
     * 联系人
     */
    @Excel(name = "联系人", width = 15)
    @Schema(title =  "联系人")
    private String userName;
    /**
     * 产品名称
     */
    @Excel(name = "产品名称", width = 15)
    @Schema(title =  "产品名称")
    private String productName;
    /**
     * 产品品牌
     */
    @Excel(name = "产品品牌", width = 15)
    @Schema(title =  "产品品牌")
    private String productBrand;
    /**
     * 产品链接
     */
    @Excel(name = "产品链接", width = 15)
    @Schema(title =  "产品链接")
    private String pruductUrl;
    /**
     * 推广最小预算（$）
     */
    @Excel(name = "推广最小预算（$）", width = 15)
    @Schema(title =  "推广最小预算（$）")
    private java.math.BigDecimal promMinBudget;
    /**
     * 推广最大预算（$）
     */
    @Excel(name = "推广最大预算（$）", width = 15)
    @Schema(title =  "推广最大预算（$）")
    private java.math.BigDecimal promMaxBudget;
    /**
     * 推广国家
     */
    @Excel(name = "推广国家", width = 15)
    @Schema(title =  "推广国家")
    private String promCounrty;
    /**
     * 网红要求备注
     */
    @Excel(name = "网红要求备注", width = 15)
    @Schema(title =  "网红要求备注")
    private String promRequirement;
    /**
     * 参考案例链接
     */
    @Excel(name = "参考案例链接", width = 15)
    @Schema(title =  "参考案例链接")
    private String promExample;
    /**
     * 匹配日期
     */
    @Excel(name = "匹配日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "匹配日期")
    private Date promTime;
    /**
     * 订单总金额
     */
    @Excel(name = "订单总金额", width = 15)
    @Schema(title =  "订单总金额")
    private java.math.BigDecimal totalAmount;
    /**
     * 定金支付时间
     */
    @Excel(name = "定金支付时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "定金支付时间")
    private Date downPaymentDate;
    /**
     * 货币符号
     */
    @Excel(name = "货币符号", width = 15)
    @Schema(title =  "货币符号")
    private String currencySymbol;
    /**
     * 销量
     */
    @Excel(name = "销量", width = 15)
    @Schema(title =  "销量")
    private Integer sales;
    /**
     * 上线日期
     */
    @Excel(name = "上线日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "上线日期")
    private Date onlineDate;
    /**
     * 推广状态
     */
    @Excel(name = "推广状态", width = 15)
    @Schema(title =  "推广状态")
    private Integer promStatus;
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
    /**
     * 手机号
     */
    @Excel(name = "手机号", width = 15)
    @Schema(title =  "手机号")
    private String phone;
    /**
     * 二维码
     */
    @Excel(name = "二维码", width = 15)
    @Schema(title =  "二维码")
    private String qrCode;
    /**
     * 推广需求id
     */
    @Excel(name = "推广需求id", width = 15)
    @Schema(title =  "推广需求id")
    private String sellerPromId;
    /**
     * 推广单号
     */
    @Excel(name = "推广单号", width = 15)
    @Schema(title =  "推广单号")
    private String sellerPromCode;
    /**
     * 推广需求标题
     */
    @Excel(name = "推广需求标题", width = 15)
    @Schema(title =  "推广需求标题")
    private String sellerPromTitle;


}
