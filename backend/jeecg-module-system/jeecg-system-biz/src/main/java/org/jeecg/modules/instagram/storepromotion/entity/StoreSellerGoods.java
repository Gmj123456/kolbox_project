package org.jeecg.modules.instagram.storepromotion.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 商家商品库表
 * @Author: jeecg-boot
 * @Date: 2020-10-02
 * @Version: V1.0
 */
@Data
@TableName("store_seller_goods")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreSellerGoods {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
    /**
     * 商家ID
     */
    @Excel(name = "商家ID", width = 15)
    @Schema(title =  "商家ID")
    private String sellerId;
    /**
     * 商家昵称
     */
    @Excel(name = "商家昵称", width = 15)
    @Schema(title =  "商家昵称")
    private String sellerName;
    /**
     * 商品标题
     */
    @Excel(name = "商品标题", width = 15)
    @Schema(title =  "商品标题")
    private String goodsTitle;
    /**
     * 商品图片
     */
    @Excel(name = "商品图片", width = 15)
    @Schema(title =  "商品图片")
    private String goodsPicUrl;
    /**
     * 商品链接
     */
    @Excel(name = "商品链接", width = 15)
    @Schema(title =  "商品链接")
    private String goodsUrl;
    /**
     * 商品价格
     */
    @Excel(name = "商品价格", width = 15)
    @Schema(title =  "商品价格")
    private java.math.BigDecimal goodsPrice;
    /**
     * 商品佣金
     */
    @Excel(name = "商品佣金", width = 15)
    @Schema(title =  "商品佣金")
    private java.math.BigDecimal goodsServiceAmount;
    /**
     * 商品售价价格
     */
    @Excel(name = "商品售价价格", width = 15)
    @Schema(title =  "商品售价价格")
    private java.math.BigDecimal goodsSellerPrice;
    /**
     * 商品编号
     */
    @Excel(name = "商品编号", width = 15)
    @Schema(title =  "商品编号")
    private String goodsCode;
    /**
     * 商品卖点一
     */
    @Excel(name = "商品卖点一", width = 15)
    @Schema(title =  "商品卖点一")
    private String goodsDescOne;
    /**
     * 商品卖点二
     */
    @Excel(name = "商品卖点二", width = 15)
    @Schema(title =  "商品卖点二")
    private String goodsDescTwo;
    /**
     * 商品卖点三
     */
    @Excel(name = "商品卖点三", width = 15)
    @Schema(title =  "商品卖点三")
    private String goodsDescThree;
    /**
     * 商品品牌
     */
    @Excel(name = "商品品牌", width = 15)
    @Schema(title =  "商品品牌")
    private String goodsBrand;
    /**
     * 商品有效期
     */
    @Excel(name = "商品有效期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "商品有效期")
    private Date goodsExpirationDate;
    /**
     * 申请通过时间
     */
    @Excel(name = "申请通过时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "申请通过时间")
    private Date approvedDate;
    /**
     * 审核人员
     */
    @Excel(name = "审核人员", width = 15)
    @Schema(title =  "审核人员")
    private String approvedUserId;
    /**
     * 审核状态（0：待审核；1：审核通过；-1：未通过）
     */
    @Excel(name = "审核状态（0：待审核；1：审核通过；-1：未通过）", width = 15)
    @Schema(title =  "审核状态（0：待审核；1：审核通过；-1：未通过）")
    private Integer approvedStatus;
    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @Schema(title =  "备注")
    private String remark;
    /**
     * 创建人
     */
    @Excel(name = "创建人", width = 15)
    @Schema(title =  "创建人")
    private String createBy;
    /**
     * 创建时间
     */
    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "创建时间")
    private Date createTime;
    /**
     * 修改人
     */
    @Excel(name = "修改人", width = 15)
    @Schema(title =  "修改人")
    private String updateBy;
    /**
     * 修改时间
     */
    @Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "修改时间")
    private Date updateTime;
    /**
     * 货币符号
     */
    @Excel(name = "货币符号", width = 15)
    @Schema(title =  "货币符号")
    private String currencyType;
    /**
     * 货币名称
     */
    @Excel(name = "货币名称", width = 15)
    @Schema(title =  "货币名称")
    private String currencyName;

    /**
     * 货币ID
     */
    @Excel(name = "货币ID", width = 15)
    @Schema(title =  "货币ID")
    private String currencyTypeId;

    /**
     * 商品类目
     */
    @Excel(name = "商品类目", width = 15)
    @Schema(title =  "商品类目")
    private String goodsCategory;


    /**
     * 折扣比例
     */
    @Excel(name = "折扣比例", width = 15)
    @Schema(title =  "折扣比例")
    private java.math.BigDecimal discountRate;


    /**
     * 上架状态（0：下架；1：上架）
     */
    @Excel(name = "上架状态（0：下架；1：上架）", width = 15)
    @Schema(title =  "上架状态（0：下架；1：上架）")
    private Integer putawayStatus;


    /**
     * 驳回原因
     */
    @Excel(name = "驳回原因", width = 15)
    @Schema(title =  "驳回原因")
    private String approvedFailReason;
    /**
     * 销售渠道
     */
    @Excel(name = "销售渠道", width = 15)
    @Schema(title =  "销售渠道")
    private String goodsChannel;
    /**
     * Review数量
     */
    @Excel(name = "Review数量", width = 15)
    @Schema(title =  "Review数量")
    private Integer reviewNum;
    /**
     * 是否推荐 0：未推荐；1：推荐
     */
    @Excel(name = "是否推荐", width = 15)
    @Schema(title =  "是否推荐")
    private Integer isRecommend;

}
