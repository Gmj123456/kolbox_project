package org.jeecg.modules.instagram.storepromotion.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.modules.instagram.storepromotion.entity.StoreSellerPromotion;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionGoods;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class StoreSellerPromotionModel extends StoreSellerPromotion {

    private String userId;
    private String companyName;
    private String linkMan;
    private String linkTel;
    private String invoiceCode;
    private String goodsTitle;
    private String kolAccount;
    private String sid;
    private String qrCode;
    private String nickname;
    private String account;


    private String celebrityCounselorId;
    /**
     * 产品
     */
    private List<StorePromotionGoods> goodsList;
    /**
     * 开始时间
     */
    private String startTime;
    /**
     * 结束时间
     */
    private String endTime;
    /**
     * 审核人名称
     */
    private String username;

    /**
     * 支付金额
     */
    private java.math.BigDecimal payAmount;
    /**
     * 网红进度
     */
    private Integer celebrityPromStatus;

    /**
     * 可开发票
     */
    private java.math.BigDecimal allowInvoiceAmount;
    /**
     * 开始日期
     */
    private String startDate;
    /**
     * 结束日期
     */
    private String endDate;
    /**
     * 查询条件
     */
    private Integer params;

    /**
     * 支付开始时间
     */
    private String startPayDate;

    /**
     * 支付结束时间
     */
    private String endPayDate;

    /**
     * 产品id
     */

    private String kolProductId;
    /**
     * 2025年8月18日17:43:11
     * 1:只查询2025年之后的数据
     */
    private Integer isNewData;
}
