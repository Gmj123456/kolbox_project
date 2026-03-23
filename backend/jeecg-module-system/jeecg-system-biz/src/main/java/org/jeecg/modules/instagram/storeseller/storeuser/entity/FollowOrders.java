package org.jeecg.modules.instagram.storeseller.storeuser.entity;

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
 * @Description: 反馈订单
 * @Author: jeecg-boot
 * @Date: 2020-05-28
 * @Version: V1.0
 */
@Data
@TableName("store_follow_orders")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class FollowOrders {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
    /**
     * 网红ID
     */
    @Excel(name = "网红ID", width = 15)
    @Schema(title =  "网红ID")
    private String celebrityId;
    /**
     * 跟进表ID
     */
    @Excel(name = "跟进表ID", width = 15)
    @Schema(title =  "跟进表ID")
    private String followupId;
    /**
     * 订单号
     */
    @Excel(name = "订单号", width = 15)
    @Schema(title =  "订单号")
    private String orderId;
    /**
     * 运单号
     */
    @Excel(name = "运单号", width = 15)
    @Schema(title =  "运单号")
    private String waybill;
    /**
     * 发货地址
     */
    @Excel(name = "发货地址", width = 15)
    @Schema(title =  "发货地址")
    private String shipmentAddress;
    /**
     * 发货时间
     */
    @Excel(name = "发货时间", width = 20, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(title =  "发货时间")
    private Date shipmentDate;
    /**
     * 发货方式（0:FBA;1:FBM）
     */
    @Excel(name = "发货方式（0:FBA;1:FBM）", width = 15)
    @Schema(title =  "发货方式（0:FBA;1:FBM）")
    private Integer shipmentType;
    /**
     * 运费
     */
    @Excel(name = "运费", width = 15)
    @Schema(title =  "运费")
    private java.math.BigDecimal freightPrice;
    /**
     * 是否发货（-1：无需发货0：未发货1：已发货）
     */
    @Excel(name = "是否发货（-1：无需发货0：未发货1：已发货）", width = 15)
    @Schema(title =  "是否发货（-1：无需发货0：未发货1：已发货）")
    private Integer isShipment;
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
}
