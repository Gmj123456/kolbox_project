package org.jeecg.modules.instagram.storepromotion.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: 消费记录表
 * @Author: jeecg-boot
 * @Date: 2020-09-30
 * @Version: V1.0
 */
@Data
@TableName("store_seller_consume")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreSellerConsume {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
    /**
     * 流水号
     */
    @Excel(name = "流水号", width = 15)
    @Schema(title =  "流水号")
    private String serialCode;
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
     * 项目id
     */
    @Excel(name = "项目id", width = 15)
    @Schema(title =  "项目id")
    private String itemId;
    /**
     * 项目名称
     */
    @Excel(name = "项目名称", width = 15)
    @Schema(title =  "项目名称")
    private String itemName;
    /**
     * 项目金额
     */
    @Excel(name = "项目金额", width = 15)
    @Schema(title =  "项目金额")
    private BigDecimal itemAmount;
    /**
     * 当前余额
     */
    @Excel(name = "当前余额", width = 15)
    @Schema(title =  "当前余额")
    private BigDecimal currentAmount;
    /**
     * 消费时间
     */
    @Excel(name = "消费时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "消费时间")
    private Date consumeDate;
    /**
     * 状态0=正常, -1=异常
     */
    @Excel(name = "状态0=正常, -1=异常", width = 15)
    @Schema(title =  "状态0=正常, -1=异常")
    private Integer status;
    /**
     * 消费类型（0：会员等级；1：邮箱个数）
     */
    @Excel(name = "消费类型（0：会员等级；1：邮箱个数 2：邮件个数 10:推广）", width = 15)
    @Schema(title =  "消费类型（0：会员等级；1：邮箱个数 2：邮件个数 10:推广）")
    private Integer consumeType;
    /**
     * 退款标识0=否,1=是
     */
    @Excel(name = "退款标识0=否,1=是", width = 15)
    @Schema(title =  "退款标识0=否,1=是")
    private Integer isRefund;
    /**
     * 退款时间
     */
    @Excel(name = "退款时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "退款时间")
    private Date refundDate;
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
     * 支付方式0=支付宝,1=微信 2=余额
     */
    @Dict(dicCode = "pay_way")
    @Excel(name = "支付方式", width = 15)
    @Schema(title =  "支付方式")
    private Integer payWay;

    /**
     * 支付交易流水号
     */
    @Excel(name = "支付交易流水号", width = 15)
    @Schema(title =  "支付交易流水号")
    private String payTradeCode;
}
