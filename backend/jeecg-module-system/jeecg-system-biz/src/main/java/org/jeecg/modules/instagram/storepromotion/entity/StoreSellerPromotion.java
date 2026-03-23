package org.jeecg.modules.instagram.storepromotion.entity;

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

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: 商家推广管理
 * @Author: jeecg-boot
 * @Date: 2021-02-07
 * @Version: V1.0
 */
@Data
@TableName("store_seller_promotion")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreSellerPromotion {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title = "主键")
    private String id;
    /**
     * 商家ID
     */
    @Schema(title = "商家ID")
    private String sellerId;
    /**
     * 商家昵称
     */
    @Excel(name = "商家账号", width = 15, orderNum = "2")
    @Schema(title = "商家昵称")
    private String sellerName;
    /**
     * 推广标题
     */
    @Schema(title = "推广标题")
    private String promTitle;
    /**
     * 推广单号
     */
    @Excel(name = "推广单号", width = 15, orderNum = "1")
    @Schema(title = "推广单号")
    private String extensionCode;
    /**
     * 推广开始时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(title = "推广开始时间")
    private Date promStartTime;
    /**
     * 推广结束时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title = "推广结束时间")
    private Date promEndTime;
    /**
     * 推广最小预算（$）
     */
    @Schema(title = "推广最小预算（$）")
    private BigDecimal promMinBudget;
    /**
     * 推广最大预算（$）
     */
    @Schema(title = "推广最大预算（$）")
    private BigDecimal promMaxBudget;
    /**
     * 推广国家
     */
    @Schema(title = "推广国家")
    private String promCountry;
    /**
     * 推广平台 0=instagram;1=youtube
     */
    @Schema(title = "推广平台")
    private String promPlatform;
    /**
     * 网红要求备注
     */
    @Schema(title = "网红要求备注")
    private String promRequirement;
    /**
     * 网红要求（类目）
     */
    @Schema(title = "网红要求（类目）")
    private String promTags;
    /**
     * 网红要求（性别）0：男 1：女 -1：不限
     */
    @Schema(title = "网红要求（性别）")
    private Integer promGender;
    /**
     * 网红要求（最小粉丝数）
     */
    @Schema(title = "网红要求（最小粉丝数）")
    private Integer promFollowersStartnum;
    /**
     * 网红要求（最大粉丝数）
     */
    @Schema(title = "网红要求（最大粉丝数）")
    private Integer promFollowersEndnum;
    /**
     * 参考案例链接
     */
    @Schema(title = "参考案例链接")
    private String promExample;
    /**
     * 推广状态 0：待寄样 1：待上线 2：已上线 3：订单完成 -1：商家取消 -2：推广终结
     */
    @Excel(name = "推广状态", width = 20, orderNum = "4", replace = {"未开始_0", "进行中_10", "已结束（正常）_99", "已结束（异常）_90"})
    @Schema(title = "推广状态")
    private Integer promStatus;
    /**
     * 审核状态 0=待审核 -1=未通过 1=审核通过
     */
    @Excel(name = "审核状态", width = 15, orderNum = "5", replace = {"待审核_0", "未通过_-1", "审核通过_1"})
    @Schema(title = "审核状态")
    private Integer approvedStatus;
    /**
     * 申请通过时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title = "申请通过时间")
    private Date approvedDate;
    /**
     * 审核人员
     */
    @Schema(title = "审核人员")
    private String approvedUserId;
    /**
     * 驳回原因
     */
    @Schema(title = "驳回原因")
    private String approvedFailReason;
    /**
     * 支付状态 -1=无需支付 0=待支付 10=已付订金 20=已付全款
     */
    @Excel(name = "支付状态", width = 15, orderNum = "19", replace = {"无需支付_-1", "待支付_0", "待支付_0", "已付意向金_11", "已付全款_20"})
    @Schema(title = "支付状态")
    private Integer payStatus;
    /**
     * 订单总金额
     */
    @Schema(title = "订单总金额")
    private BigDecimal totalAmount;
    /**
     * 优惠金额
     */
    @Schema(title = "优惠金额")
    private BigDecimal discountAmount;
    /**
     * 应付金额
     */
    @Excel(name = "待付金额", width = 15, orderNum = "16")
    @Schema(title = "应付金额")
    private BigDecimal payableAmount;
    /**
     * 实付总金额
     */
    @Excel(name = "已付金额", width = 15, orderNum = "15")
    @Schema(title = "实付总金额")
    private BigDecimal paymentAmount;
    /**
     * 退款金额
     */
    @Excel(name = "退款金额", width = 15, orderNum = "17")
    @Schema(title = "退款金额")
    private BigDecimal refundAmount;
    /**
     * 订单追加金额
     */
    @Schema(title = "订单追加金额")
    private BigDecimal appendAmount;

    /**
     * 当前汇率
     */
    @Schema(title = "当前汇率")
    private BigDecimal exchangeRate;

    /**
     * 备注
     */
    @Excel(name = "备注", width = 50, orderNum = "20")
    @Schema(title = "备注")
    private String remark;
    /**
     * 创建人
     */
    @Schema(title = "创建人")
    private String createBy;
    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title = "创建时间")
    private Date createTime;
    /**
     * 修改人
     */
    @Schema(title = "修改人")
    private String updateBy;
    /**
     * 修改时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title = "修改时间")
    private Date updateTime;


    /**
     * 推广费
     */
    @Schema(title = "推广费")
    private BigDecimal promAmount;


    /**
     * paypal费用
     */
    @Schema(title = "paypal费用")
    private BigDecimal paypalFeesAmount;


    /**
     * 总费用（RMB）
     */
    @Schema(title = "总费用（RMB）")
    private BigDecimal totalRmbAmount;

    /**
     * 货币符号
     */
    @Schema(title = "货币符号")
    private String currencySymbol;

    /**
     * 已开票金额
     */
    @Schema(title = "已开票金额")
    private BigDecimal invoiceAmount;

    /**
     * 已交税款
     */
    @Schema(title = "已交税款")
    private BigDecimal taxFeeAmount;

    /**
     * 意向金
     */
    @Schema(title = "意向金")
    private BigDecimal intentionAmount;

    /**
     * 产品品牌
     */
    @Excel(name = "产品品牌", width = 15, orderNum = "4")
    @Schema(title = "产品品牌")
    private String productBrand;
    private String productBrandId;

    /**
     * 删除标识  0正常 1删除
     */
    @Schema(title = "删除标识  0正常 1删除")
    private Integer isDel;

    /**
     * 方案负责人ID
     */
    @Schema(title = "方案负责人ID")
    private String celebrityUserId;

    /**
     * 方案负责人名称
     */
    @Schema(title = "方案负责人名称")
    private String celebrityUserName;

    /**
     * 网红已付总金额
     */
    @Schema(title = "网红已付总金额")
    private BigDecimal celebrityPayTotalAmount;

    /**
     * 网红费用总金额
     */
    @Schema(title = "网红费用总金额")
    private BigDecimal celebrityTotalAmount;

    /**
     * 推广类型 0(默认，旧数据) 1 新增加
     */
    private Integer promotionType;
    /**
     * 是否包含PP费 0：否；1：是（默认）
     */
    private Integer isIncludePaypalFees;
    /**
     * 推广总税费
     */
    private BigDecimal totalTaxAmount;
    /**
     * 税率%
     */
    private BigDecimal taxRate;
    /**
     * 商务人员
     */
    private String counselorUserId;
    private String counselorUserName;
    /**
     * 商家顾问
     */
    private String sellerCounselorId;
    private String sellerCounselorName;
    /**
     * 推广类型 0：商务顾问添加；1：商家添加
     */
    private Integer promType;
    /**
     * 产品链接（商家添加）
     */
    private String productUrl;
    /**
     * 推广预算（商家添加）
     */
    private String promBudget;
    /**
     * 联系方式 商家填写或系统自动填写
     */
    private String contactInfo;


}
