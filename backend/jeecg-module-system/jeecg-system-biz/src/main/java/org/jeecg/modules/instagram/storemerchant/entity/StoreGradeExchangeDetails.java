package org.jeecg.modules.instagram.storemerchant.entity;

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
 * @Description: 商家兑换等级明细表
 * @Author: jeecg-boot
 * @Date: 2021-02-03
 * @Version: V1.0
 */
@Data
@TableName("store_grade_exchange_details")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreGradeExchangeDetails {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
    /**
     * 系统用户主键
     */
    @Excel(name = "系统用户主键", width = 15)
    @Schema(title =  "系统用户主键")
    private String userId;
    /**
     * 兑换等级ID
     */
    @Excel(name = "兑换等级ID", width = 15)
    @Schema(title =  "兑换等级ID")
    private String gradeId;
    /**
     * 兑换码等级名称
     */
    @Excel(name = "兑换码等级名称", width = 15)
    @Schema(title =  "兑换码等级名称")
    private String gradeName;

    /**
     * 兑换码周期
     */
    @Excel(name = "兑换码周期", width = 15)
    @Schema(title =  "兑换码周期")
    private Integer gradeCycle;
    /**
     * 兑换码
     */
    @Excel(name = "兑换码", width = 15)
    @Schema(title =  "兑换码")
    private String couponCode;
    /**
     * 兑换码有效期
     */
    @Excel(name = "兑换码有效期", width = 20, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(title =  "兑换码有效期")
    private Date expirationDate;
    /**
     * 兑换码状态
     */
    @Excel(name = "兑换码状态", width = 15)
    @Schema(title =  "兑换码状态")
    private Integer couponStatus;
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
     * 兑换日期
     */
    @Excel(name = "兑换日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "兑换日期")
    private Date exchangeDate;
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
     * 兑换备注
     */
    @Excel(name = "兑换备注", width = 15)
    @Schema(title =  "兑换备注")
    private String exchangeRemark;
}
