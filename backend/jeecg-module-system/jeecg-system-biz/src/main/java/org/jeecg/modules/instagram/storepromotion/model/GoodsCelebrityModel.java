package org.jeecg.modules.instagram.storepromotion.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class GoodsCelebrityModel {
    /**
     * 推广单号
     */
    @Excel(name = "推广单号", width = 15)
    private String extensionCode;
    /**
     * 网红账号
     */
    @Excel(name = "网红账号", width = 15)
    private String celebrityAccount;
    /**
     * 网红平台
     */
    @Excel(name = "网红平台", width = 15, dicCode = "platform_type")
    private String platformType;
    /**
     * 网红负责人
     */
    @Excel(name = "网红负责人", width = 20)
    private String celebrityCounselorName;
    /**
     * 网红费用
     */
    @Excel(name = "网红费用（$）", width = 20)
    private BigDecimal celebrityPrice;
    /**
     * 网红已付金额
     */
    @Excel(name = "网红已付金额（$）", width = 20)
    private BigDecimal celebrityPayAmount;
    /**
     * 网红上线时间
     */
    @Excel(name = "网红上线时间", width = 20)
    private String onlineTime;
    /**
     * 商家账号
     */
    @Excel(name = "商家账号", width = 15)
    private String sellerName;
    /**
     * 商家顾问
     */
    @Excel(name = "商家顾问", width = 15)
    private String nickname;
    /**
     * 商务人员
     */
    @Excel(name = "商务人员", width = 15)
    private String counselorUserName;
    /**
     * 推广标题
     */
    @Excel(name = "推广标题", width = 15)
    private String promTitle;
    /**
     * 产品标题
     */
    @Excel(name = "产品标题", width = 15)
    private String goodsTitle;
    /**
     * 推广费
     */
    @Excel(name = "推广费用", width = 15)
    private BigDecimal totalAmount;
    /**
     * 推广费
     */
    @Excel(name = "推广费", width = 20)
    private BigDecimal promPrice;
    /**
     * 商务人员id
     */
    private String counselorUserId;
    /**
     * 商家顾问Id
     */
    private String sid;
    /**
     * 产品图片
     */
    private String goodsPicUrl;
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
     * 推广时间
     */
    @Excel(name = "推广时间", width = 30)
    private String promTime;
    /**
     * 上线时间
     */
    private String startOnlineTime;
    /**
     * 上线时间
     */
    private String endOnlineTime;
    /**
     * 网红负责人id
     */
    private String celebrityCounselorId;
    /**
     * 推广状态
     */
    private String promStatus;
}
