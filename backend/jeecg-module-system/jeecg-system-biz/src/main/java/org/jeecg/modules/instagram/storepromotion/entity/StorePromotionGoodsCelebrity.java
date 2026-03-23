package org.jeecg.modules.instagram.storepromotion.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 商家推广产品网红关联表
 * @Author: jeecg-boot
 * @Date: 2021-02-07
 * @Version: V1.0
 */
@Data
@TableName("store_promotion_goods_celebrity")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StorePromotionGoodsCelebrity {

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
     * 推广单号
     */
    @Excel(name = "推广单号", width = 15)
    @Schema(title =  "推广单号")
    private String extensionCode;
    /**
     * 推广ID
     */
    @Excel(name = "推广ID", width = 15)
    @Schema(title =  "推广ID")
    private String promId;
    /**
     * 产品ID
     */
    @Excel(name = "产品ID", width = 15)
    @Schema(title =  "产品ID")
    private String goodsId;
    /**
     * 私有网红ID
     */
    @Excel(name = "私有网红ID", width = 15)
    @Schema(title =  "私有网红ID")
    private String celebrityPrivateId;
    /**
     * 物流单号
     */
    @Excel(name = "物流单号", width = 15)
    @Schema(title =  "物流单号")
    private String goodsWaybill;
    /**
     * 运营商
     */
    @Excel(name = "运输商", width = 15)
    @Schema(title =  "运输商")
    private String goodsCarrier;
    /**
     * 商品追加金额
     */
    @Excel(name = "商品追加金额", width = 15)
    @Schema(title =  "商品追加金额")
    private BigDecimal appendGoodsAmount;
    /**
     * 网红推广状态 0：待寄样 1：待上线 2：已上线 3：已完成 -1：已终止
     */
    @Excel(name = "网红推广状态 0：待寄样 1：待上线 2：已上线 3：已完成 -1：已终止", width = 15)
    @Schema(title =  "网红推广状态 0：待寄样 1：待上线 2：已上线 3：已完成 -1：已终止")
    private Integer celebrityPromStatus;

    @Excel(name = "收货地址", width = 15)
    @Schema(title =  "收货地址")
    private String fullAddress;

    @Excel(name = "推广视频图片列表（JSON格式存取）", width = 15)
    @Schema(title =  "推广视频图片列表（JSON格式存取）")
    private String mediaImgJson;

    @Excel(name = "推广视频链接列表", width = 15)
    @Schema(title =  "推广视频链接列表")
    private String mediaUploads;

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
     * 推广费
     */
    @Excel(name = "推广费", width = 20)
    @Schema(title =  "推广费")
    private BigDecimal promPrice;
    /**
     * 网红状态 0：未选中；1：已选中
     */
    @Excel(name = "网红状态 0：未选中；1：已选中", width = 20)
    @Schema(title =  "网红状态 0：未选中；1：已选中")
    private Integer celebrityStatus;

    @Schema(title =  "折扣码")
    private String discountCode;

    /**
     * 折扣力度
     */
    @Excel(name = "折扣力度", width = 20)
    @Schema(title =  "折扣力度")
    private Integer discountOff;

    /**
     * 折扣码有效开始时间
     */
    @Excel(name = "折扣码有效开始时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "折扣码有效开始时间")
    private Date discountStartDate;
    /**
     * 折扣码有效结束时间
     */
    @Excel(name = "折扣码有效结束时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "折扣码有效结束时间")
    private Date discountEndDate;

    @Schema(title =  "视频草稿")
    private String mediaUploadsDraft;

    @Schema(title =  "视频图片草稿")
    private String mediaImgJsonDraft;

    @Schema(title =  "视频标签")
    private String videoTags;

    @Schema(title =  "擅长标签")
    private String promLikeTags;

    @Schema(title =  "删除标识 0 正常 1删除")
    private Integer isDel;

    @Schema(title =  "网红负责人id")
    private String celebrityCounselorId;

    @Schema(title =  "网红负责人名称")
    private String celebrityCounselorName;
    /**
     * 网红费用
     */
    @Schema(title =  "网红费用")
    private BigDecimal celebrityPrice;
    /**
     * 网红paypal费用
     */
    @Schema(title =  "网红paypal费用")
    private BigDecimal celebrityPaypalFees;
    /**
     * 网红上线时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(title =  "网红上线时间")
    private Date onlineTime;
    /**
     * 网红已付金额
     */
    @Schema(title =  "网红已付金额")
    private BigDecimal celebrityPayAmount;

    @Schema(title =  "带货变体")
    private String goodsAttribute;
    /**
     * 网红税费
     */
    private BigDecimal taxAmount;
    /**
     * 是否含税 0：否（默认）1：是
     */
    private Integer isIncludingTax;

}
