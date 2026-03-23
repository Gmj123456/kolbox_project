package org.jeecg.modules.instagram.storepromotion.model;


import lombok.Data;

import java.math.BigDecimal;

/**
 * 商家带货统计类
 *
 */
@Data
public class SellerCommerceModel {

    /**
     * 商家ID
     */
    private String sellerId;
    /**
     * 商家账号
     */
    private String sellerName;
    /**
     * 年份
     */
    private Integer year;
    /**
     * 月份
     */
    private Integer month;
    /**
     * 带货数量
     */
    private Integer commerceCount;
    /**
     * 已结算数量
     */
    private Integer settlementCount;
    /**
     * 未结算数量
     */
    private Integer unSettlementCount;
    /**
     * 已结算金额
     */
    private BigDecimal settlementAmount;
    /**
     * 商家顾问
     */
    private String sellerCounselorName;
    /**
     * 结算状态
     */
    private Integer settlementStatus;

    /**
     * 开始年份
     */
    private Integer startYear;
    /**
     * 结束年份
     */
    private Integer endYear;
    /**
     * 开始月份
     */
    private Integer startMonth;
    /**
     * 结束月份
     */
    private Integer endMonth;

}
