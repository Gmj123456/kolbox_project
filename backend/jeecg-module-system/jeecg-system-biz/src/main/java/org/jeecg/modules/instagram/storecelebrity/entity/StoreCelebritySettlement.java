package org.jeecg.modules.instagram.storecelebrity.entity;

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
 * @Description: 私有网红带货结算表
 * @Author: jeecg-boot
 * @Date: 2021-01-03
 * @Version: V1.0
 */
@Data
@TableName("store_celebrity_settlement")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreCelebritySettlement {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
    /**
     * 私有网红ID
     */
    @Excel(name = "私有网红ID", width = 15)
    @Schema(title =  "私有网红ID")
    private String celebrityPrivateId;
    /**
     * 私有网红账号
     */
    @Schema(title =  "私有网红账号")
    private String privateAccount;
    /**
     * 年份
     */
    @Excel(name = "年份", width = 15)
    @Schema(title =  "年份")
    private Integer year;
    /**
     * 月份
     */
    @Excel(name = "月份", width = 15)
    @Schema(title =  "月份")
    private Integer month;
    /**
     * 带货数量
     */
    @Excel(name = "带货数量", width = 15)
    @Schema(title =  "带货数量")
    private Integer goodsNum;
    /**
     * 产品单价
     */
    @Excel(name = "产品单价", width = 15)
    @Schema(title =  "产品单价")
    private java.math.BigDecimal goodsPrice;
    /**
     * 结算金额
     */
    @Excel(name = "结算金额", width = 15)
    @Schema(title =  "结算金额")
    private java.math.BigDecimal settlementPrice;
    /**
     * 实际结算金额
     */
    @Excel(name = "实际结算金额", width = 15)
    @Schema(title =  "实际结算金额")
    private java.math.BigDecimal realitySettlementPrice;
    /**
     * 结算状态（0：未结算；1：已结算）
     */
    @Excel(name = "结算状态（0：未结算；1：已结算）", width = 15)
    @Schema(title =  "结算状态（0：未结算；1：已结算）")
    private Integer settlementStatus;
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
