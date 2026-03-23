package org.jeecg.modules.instagram.storecelebrity.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrity;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.math.BigDecimal;

/**
 * @Description: 网红档案
 * @Author: nqr
 * @Date: 2020-04-20
 * @Version: V1.0
 */
@Data
public class StoreCelebrityPriceModel extends StoreCelebrity {

    /**
     * story价格
     */
    @Excel(name = "story价格", width = 15)
    @Schema(title =  "story价格")
    private BigDecimal igStoryPrice;
    /**
     * post价格
     */
    @Excel(name = "post价格", width = 15)
    @Schema(title =  "post价格")
    private BigDecimal igPostPrice;
    /**
     * story加post价格
     */
    @Excel(name = "story加post价格", width = 15)
    @Schema(title =  "story加post价格")
    private BigDecimal igStoryPostPrice;
    /**
     * BlogPost价格
     */
    @Excel(name = "BlogPost价格", width = 15)
    @Schema(title =  "BlogPost价格")
    private BigDecimal blogPostPrice;
    /**
     * YoutubeVideo价格
     */
    @Excel(name = "YoutubeVideo价格", width = 15)
    @Schema(title =  "YoutubeVideo价格")
    private BigDecimal youtubeVideoPrice;
    /**
     * 价格区间
     */
    @Excel(name = "价格区间", width = 15)
    @Schema(title =  "价格区间")
    private String priceRange;
    /**
     * 发送次数
     */
    private Integer sendEmailCount;


    /**
     * 国家ID
     */
    @Excel(name = " 国家ID", width = 15)
    @Schema(title =  " 国家ID")
    private String countryId;
    /**
     * 国家名称
     */
    @Excel(name = " 国家名称", width = 15)
    @Schema(title =  " 国家名称")
    private String countryName;
    /**
     * 收件人姓名
     */
    @Excel(name = " 收件人姓名", width = 15)
    @Schema(title =  " 收件人姓名")
    private String fullname;
    /**
     * 联系电话
     */
    @Excel(name = " 联系电话", width = 15)
    @Schema(title =  " 联系电话")
    private String mobile;
    /**
     * 街道门牌号
     */
    @Excel(name = " 街道门牌号", width = 15)
    @Schema(title =  " 街道门牌号")
    private String address;
    /**
     * 街道门牌号
     */
    @Excel(name = "街道门牌号", width = 15)
    @Schema(title =  "街道门牌号")
    private String address2;
    /**
     * 市
     */
    @Excel(name = " 市", width = 15)
    @Schema(title =  " 市")
    private String city;
    /**
     * 省
     */
    @Excel(name = " 省", width = 15)
    @Schema(title =  " 省")
    private String province;
    /**
     * 邮政编码
     */
    @Excel(name = "邮政编码", width = 15)
    @Schema(title =  "邮政编码")
    private String zipCode;
    /**
     * 收件备注
     */
    @Excel(name = "收件备注", width = 15)
    @Schema(title =  "收件备注")
    private String consigneeRemark;

    private Integer editType;

    /*网红Id*/
    @Schema(title =  "网红Id")
    private String celebrityId;

    private Integer userStatus;

    /*
    商家收藏ID
     */
    private String collectionId;
}
