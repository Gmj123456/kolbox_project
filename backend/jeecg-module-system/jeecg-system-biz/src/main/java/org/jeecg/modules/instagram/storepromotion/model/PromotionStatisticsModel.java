package org.jeecg.modules.instagram.storepromotion.model;

import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

@Data
public class PromotionStatisticsModel {
    /**
     * 姓名
     */
    @Excel(name = "姓名", width = 15)
    private String username;
    /**
     * 推广单数
     */
    @Excel(name = "推广单数", width = 15)
    private String promCount;
    /**
     * 未开始单数
     */
    @Excel(name = "未开始单数", width = 15)
    private String unstartCount;
    /**
     * 进行中单数
     */
    @Excel(name = "进行中单数", width = 15)
    private String underwayCount;
    /**
     * 已结束（异常）单数
     */
    @Excel(name = "异常结束单数", width = 15)
    private String abnormalFinishCount;
    /**
     * 已结束（正常）单数
     */
    @Excel(name = "正常结束单数", width = 15)
    private String normalFinishCount;
    /**
     * 仅收取意向金单数
     */
    @Excel(name = "仅收取意向金单数", width = 16)
    private String onlyIntentionCount;
    /**
     * 仅收取意向金总额
     */
    @Excel(name = "仅收取意向金总额", width = 16)
    private String onlyIntentionAmountSum;
    /**
     * 已付意向金单数
     */
    @Excel(name = "已付意向金单数", width = 15)
    private String intentionCount;
    /**
     * 已付意向金总额
     */
    @Excel(name = "已付意向金总额", width = 15)
    private String intentionAmountSum;
    /**
     * 已付订金单数
     */
    @Excel(name = "已付订金单数", width = 15)
    private String paymentAmountCount;
    /**
     * 已付订金总额
     */
    @Excel(name = "已付订金总额", width = 15)
    private String paymentAmountSum;
    /**
     * 已付全款单数
     */
    @Excel(name = "已付全款单数", width = 15)
    private String fullPaymentAmountCount;
    /**
     * 已付全款总额
     */
    @Excel(name = "已付全款总额", width = 15)
    private String fullPaymentAmountSum;
    /**
     * 含退款订单单数
     */
    @Excel(name = "含退款订单单数", width = 15)
    private String refundAmountCount;
    /**
     * 退款总额
     */
    @Excel(name = "退款总额", width = 15)
    private String refundAmountSum;
    /**
     * 总收入金额
     */
    @Excel(name = "总收入金额", width = 15)
    private String totalAmountSum;
    /**
     * 开始日期
     */
    private String startDate;
    /**
     * 结束日期
     */
    private String endDate;
    /**
     * 商务人员id
     */
    private String counselorUserId;
    /**
     * 商家顾问id
     */
    private String sid;
    /**
     * 网红负责人id
     */
    private String celebrityUserId;
    /**
     * 查询类型 dataType 1 商务人员  网红负责人
     */
    private Integer dataType;

}
