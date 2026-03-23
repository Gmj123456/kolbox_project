package org.jeecg.modules.instagram.storepromotion.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.modules.instagram.storepromotion.entity.StoreSellerPromotion;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class StoreSellerPromotionPayModel extends StoreSellerPromotion {
    /**
     * 支付金额
     */
    private java.math.BigDecimal payAmount;

    /**
     * 支付币种
     */
    private String payCurency;

    /**
     *
     *  支付备注
     *
     */
    private String payRemark;

    /**
     *
     *  货币符号
     *
     */
    private String symbol;

    /**
     * 付款类型
     */
    private Integer payType;

    /**
     * 支付方式
     */
    private Integer payWay;


    /**
     * 商家支付时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date consumeDate;

    /**
     * 支付交易流水号
     */
    private String payTradeCode;

}
