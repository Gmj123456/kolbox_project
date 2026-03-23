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
 * @Description: 商家消费失败退还记录表
 * @Author: jeecg-boot
 * @Date: 2020-11-03
 * @Version: V1.0
 */
@Data
@TableName("store_push_refund")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StorePushRefund {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
    /**
     * 推送记录ID
     */
    @Excel(name = "推送记录ID", width = 15)
    @Schema(title =  "推送记录ID")
    private String messagePushDetailId;
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
     * 网红id
     */
    @Excel(name = "网红id", width = 15)
    @Schema(title =  "网红id")
    private String kolId;
    /**
     * 网红昵称
     */
    @Excel(name = "网红昵称", width = 15)
    @Schema(title =  "网红昵称")
    private String kolName;
    /**
     * 接收邮箱
     */
    @Excel(name = "接收邮箱", width = 15)
    @Schema(title =  "接收邮箱")
    private String kolEmail;
    /**
     * 流量包订购ID
     */
    @Excel(name = "流量包订购ID", width = 15)
    @Schema(title =  "流量包订购ID")
    private String packagePurchaseId;
    /**
     * 流量包ID
     */
    @Excel(name = "流量包ID", width = 15)
    @Schema(title =  "流量包ID")
    private String packageId;
    /**
     * 流量包名称
     */
    @Excel(name = "流量包名称", width = 15)
    @Schema(title =  "流量包名称")
    private String packageName;
    /**
     * 退还时间
     */
    @Excel(name = "退还时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "退还时间")
    private Date refundDate;
    /**
     * 退还状态（0：未退 ；1：已退）
     */
    @Excel(name = "退还状态（0：未退 ；1：已退）", width = 15)
    @Schema(title =  "退还状态（0：未退 ；1：已退）")
    private Integer refundStatus;
    /**
     * 退还类型（0：套餐 1：邮件包）
     */
    @Excel(name = "退还类型（0：套餐 1：邮件包 2:取消发送）", width = 15)
    @Schema(title =  "退还类型（0：套餐 1：邮件包 2:取消发送）")
    private Integer refundType;
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
