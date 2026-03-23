package org.jeecg.modules.instagram.storepromotion.model;

import lombok.Data;
import org.jeecg.modules.instagram.storepromotion.entity.StoreSellerGoods;

import java.util.Date;

@Data
public class StoreSellerGoodsModel extends StoreSellerGoods {
    private Integer priceStartNum;
    private Integer priceEndNum;
    /**
     * 排序字段
     */
    private String orderWord;
    /**
     * 升序降序
     */
    private String order;

    //卖家商户顾问id
    private String sellerCounselorId;
    //卖家商户顾问名称
    private String sellerCounselorName;

    //商品细节表字段
    private String storeSellDetailId;
    private String celebrityPrivateId;
    private String goodsId;
    private String storeSellDetailGoodsCode;
    private Date codeStartTime;
    private Date codeEndTime;
    private java.math.BigDecimal codeDiscountRate;
    private String goodsProperty;
    private Integer sellCount;
    private java.math.BigDecimal realityServiceAmount;
    private Integer promotionStatus;
    private String logisticsNum;
    private Date receiveTime;
    private Date shareTime;
    private Date serviceEndTime;
    private String storeSellDetailRemark;

    //私有网红表字段
    private String storeCeleBrityPrivateId;
    private String celebrityId;
    private String account;
    private String username;
    private String email;
    private String nickName;
    private String likeTags;
    private String address;
    private String bankAccount;
    private Integer gender;
    private Integer platformType;
    private Integer followersNum;
    private Integer postsNum;
    private Date cooperationTime;
    private Integer contractStatus;
    private Date contractTime;
    private java.math.BigDecimal contractAmount;
    private Integer serviceAmountPeriod;
    private Integer flatFeePeriod;
    private String celebrityCounselorId;
    private String celebrityCounselorName;
    private String sellVideoUrl;
    private String storeCeleBrityPrivateRemar;

}
