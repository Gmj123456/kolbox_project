package org.jeecg.modules.instagram.storepromotion.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionPayment;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class StorePromotionPaymentModel extends StorePromotionPayment {

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 支付开始时间
     */
    private String startPayDate;

    /**
     * 支付结束时间
     */
    private String endPayDate;

    /**
     * 推广标题
     */
    @Excel(name = "推广标题", width = 15, orderNum = "3")
    private String promTitle;

    /**
     * 实付
     */
    @Excel(name = "已付金额(¥)", width = 15, orderNum = "11")
    private BigDecimal paymentAmount;

    /**
     * 总金额（rmb）
     */
    private BigDecimal totalAmount;


    /**
     * 退款金额（rmb）
     */
    @Excel(name = "已退金额(¥)", width = 15, orderNum = "12")
    private BigDecimal refundAmount;


    /**
     * 总金额（rmb）
     */
    @Excel(name = "订单金额(¥)", width = 15, orderNum = "10")
    private BigDecimal totalRmbAmount;

    /**
     * 推广开始时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date promStartTime;

    /**
     * 推广结束时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date promEndTime;

    /**
     * 推广汇率
     */
    private BigDecimal exchangeRate;

    private BigDecimal intentionAmount;


    @Excel(name = "商家顾问", width = 15, orderNum = "7")
    private String nickname;

    /**
     * 商家顾问id
     */
    private String sid;

    /**
     * 支付状态
     */
    @Excel(name = "支付状态", width = 10, orderNum = "5", replace = {"无需支付_-1", "待支付_0", "已付定金_10", "已付意向金_11", "已付全款_20"})
    private Integer payStatus;

    /**
     * 推广状态
     */
    @Excel(name = "推广状态", width = 20, orderNum = "4", replace = {"未开始_0", "进行中_10", "已结束（正常）_99", "已结束（异常）_90"})
    private Integer promStatus;

    /**
     * 推广创建时间
     */
    @Excel(name = "推广时间", width = 15, format = "yyyy-MM-dd", orderNum = "1")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date promCreateTime;


    // 统计收入
    private BigDecimal statisticsIncome;

    // 统计支出
    private BigDecimal statisticsExpend;

    // 统计实际收入
    private BigDecimal statisticsRealIncome;

    /**
     * 商务人员id
     */
    private String counselorUserId;

    /**
     * 商务人员
     */
    @Excel(name = "商务人员", width = 15, orderNum = "7")
    private String counselorUserName;


    /**
     * 网红负责人ID
     */
    private String celebrityUserId;

    /**
     * 网红负责人名称
     */
    @Excel(name = "网红负责人", width = 15, orderNum = "6")
    @Schema(title =  "网红负责人")
    private String celebrityUserName;

    private String symbol;


    // 仅意向金
    private Long intentionCount;
    // 意向金加全款
    private Long intentionAndFullCount;

    // 全款
    private Long paidInFullCount;
    // 统计
    private Long totalCount;
    // 退款
    private Long refundCount;
    // 超期
    private Long exceedTimeCount;
    // 网红id
    private String celebrityPrivateId;
    //网红账号
    private String celebrityAccount;
}
