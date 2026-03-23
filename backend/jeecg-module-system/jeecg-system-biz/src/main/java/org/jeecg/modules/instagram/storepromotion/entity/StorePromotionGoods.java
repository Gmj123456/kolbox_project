package org.jeecg.modules.instagram.storepromotion.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 商家推广产品表
 * @Author: jeecg-boot
 * @Date: 2021-02-07
 * @Version: V1.0
 */
@Data
@TableName("store_promotion_goods")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StorePromotionGoods {

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

    private String kolProductId;
    /**
     * 产品名称中英文
     */
    @Excel(name = "产品名称中英文", width = 15)
    @Schema(title =  "产品名称中英文")
    private String goodsTitle;
    /**
     * 产品链接
     */
    @Excel(name = "产品链接", width = 15)
    @Schema(title =  "产品链接")
    private String goodsUrl;
    /**
     * 产品图片链接
     */
    @Excel(name = "产品图片链接", width = 15)
    @Schema(title =  "产品图片链接")
    private String goodsPicUrl;
    /**
     * 产品备注
     */
    @Excel(name = "产品备注", width = 15)
    @Schema(title =  "产品备注")
    private String goodsRemark;
    /**
     * 产品状态 0：待匹配（默认） 1：待寄样 2：待上线 3：已上线
     */
    @Excel(name = "产品状态 0：未开始 10：进行中 99：已结束（正常） 90：已结束（异常）", width = 15)
    @Schema(title =  "产品状态 0：未开始 10：进行中 99：已结束（正常） 90：已结束（异常）")
    private Integer goodsStatus;
    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @Schema(title =  "备注")
    private String remark;
    /**
     * 产品属性JSON（型号、尺寸、颜色等）
     */
    @Excel(name = "型号", width = 15)
    @Schema(title =  "产品属性JSON（型号、尺寸、颜色等）")
    private String productAttributes;
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
     * 带货变体
     */
    @Excel(name = "带货变体", width = 15)
    @Schema(title =  "带货变体")
    private String attribute;
}
