package org.jeecg.modules.instagram.storepromotion.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class StorePromotionGoodsModel {
    private String id;
    private String promId;
    @Excel(name = "产品名称", width = 20, orderNum = "1")
    private String goodsTitle;

    @Excel(name = "产品状态", width = 15, dicCode = "goods_status", orderNum = "2")
    private Integer goodsStatus;

    @Excel(name = "网红顾问", width = 15, orderNum = "3")
    private String celebrityCounselorName;

    @Excel(name = "网红进度", width = 15, dicCode = "celebrity_progress", orderNum = "4")
    private String celebrityStatus;

    @Excel(name = "网红账号", width = 15, orderNum = "5")
    private String account;

    @Excel(name = "平台", width = 15, dicCode = "platform_type", orderNum = "6")
    private Integer platformType;

    @Excel(name = "国家", width = 15, orderNum = "7")
    private String countryName;

    @Excel(name = "视频类型", width = 15, orderNum = "8")
    private String videoTags;

    @Excel(name = "粉丝数", width = 15, orderNum = "9")
    private String followersNum;

    @Excel(name = "均播", width = 15, orderNum = "10")
    private String playAvgNum;

    @Excel(name = "网红费用", width = 15, orderNum = "11")
    private BigDecimal celebrityPrice;

    @Excel(name = "推广费用", width = 15, orderNum = "12")
    private String promPrice;
    /**
     * 网红税费
     */
    @Excel(name = "网红税费", width = 15, orderNum = "13")
    private BigDecimal taxAmount;

    @Excel(name = "上线时间", width = 15, orderNum = "14")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date onlineTime;

    private String goodsPicUrl;
    private String goodsUrl;
    private String goodsRemark;
    private String kolId;
    private String likeTags;
    private String fullAddress;
    private String correId;
    private String goodsWaybill;
    private String goodsCarrier;
    private Integer celebrityPromStatus;
    private Integer num;
    private BigDecimal paymentAmount;
    private String mediaUploads;
    private String mediaImgJson;
    private String avatarUrl;
    private String likeAvgNum;
    private String interactAvgNum;
    private String videoCount;
    private String likeCount;
    private String remark;
    private String promLikeTags;
    private String discountCode;
    private String discountOff;
    private Date discountStartDate;
    private Date discountEndDate;
    private String mediaUploadsDraft;
    private String mediaImgJsonDraft;
    private String promTitle;
    private String celebrityCounselorId;

    private Integer promStatus;

    private Date createTime;
    private BigDecimal celebrityPayAmount;
    private BigDecimal celebrityPaypalFees;
    private String goodsAttribute;
    private Integer promotionStatus;
    private String promoGoodsId;

    /**
     * 是否含税 0：否（默认）1：是
     */
    private Integer isIncludingTax;

    private String kolProductId;
}
