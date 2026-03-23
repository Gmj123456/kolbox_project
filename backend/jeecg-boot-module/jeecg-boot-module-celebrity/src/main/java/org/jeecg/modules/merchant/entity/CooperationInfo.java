package org.jeecg.modules.merchant.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
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
 * @Description: 商家合作信息表
 * @Author: dongruyang
 * @Date: 2025-03-12
 * @Version: V1.0
 */
@Data
@TableName("cooperation_info")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CooperationInfo {

  /**
   * 主键ID
   */
  @TableId(type = IdType.ASSIGN_UUID)
  @Schema(title =  "主键ID")
  private String id;

  @Excel(name = "合作日期(格式为：2025-01-01)", width = 15)
  @TableField(exist = false)
  private String cooperationDateStr;
  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  @Schema(title =  "合作日期")
@TableField(update = "")
  private Date cooperationDate;

  /**
   * 品牌ID
   */
  @Schema(title =  "品牌ID")
  private String brandId;
  @Excel(name = "品牌", width = 15)
  @TableField(exist = false)
  private String brandName;
  /**
   * 产品计划
   */
  @Excel(name = "产品计划", width = 15)
  @Schema(title =  "产品计划")
  private String productPlan;
  /**
   * 商务ID
   */
  @Schema(title =  "商务ID")
  private String businessId;
  @Excel(name = "商务", width = 15)
  @TableField(exist = false)
  private String businessName;
  /**
   * 合作金额
   */
  @Schema(title =  "合作金额")
  private java.math.BigDecimal cooperationAmount;
  @Excel(name = "合作金额", width = 15)
  @TableField(exist = false)
  private String cooperationAmountStr;
  /**
   * 获客方式
   */
  @Excel(name = "获客方式", width = 15)
  @Schema(title =  "获客方式")
  private String customerAcquisitionMethod;
  /**
   * TT数量
   */
  @Schema(title =  "TT数量")
  private Integer ttQuantity;
  @Excel(name = "TT数量", width = 15)
  @TableField(exist = false)
  private String ttQuantityStr;
  /**
   * IG数量
   */
  @Schema(title =  "IG数量")
  private Integer igQuantity;
  @Excel(name = "IG数量", width = 15)
  @TableField(exist = false)
  private String igQuantityStr;

  /**
   * YT数量
   */
  @Schema(title =  "YT数量")
  private Integer ytQuantity;
  @Excel(name = "YT数量", width = 15)
  @TableField(exist = false)
  private String ytQuantityStr;
  /**
   * 最新合作动态
   */
  @Excel(name = "最新合作动态", width = 15)
  @Schema(title =  "最新合作动态")
  private String latestCooperationDynamic;
  /**
   * 项目状态
   */
  @Excel(name = "项目状态", width = 15)
  @Schema(title =  "项目状态")
  private String projectStatus;
  /**
   * 备注
   */
  @Excel(name = "备注", width = 15)
  @Schema(title =  "备注")
  private Object remarks;
  /**
   * 创建人
   */
  @Excel(name = "创建人", width = 15)
  @Schema(title =  "创建人")
  private String createBy;
  /**
   * 创建日期
   */
  @Excel(name = "创建日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Schema(title =  "创建日期")
  private Date createTime;
  /**
   * 更新人
   */
  @Excel(name = "更新人", width = 15)
  @Schema(title =  "更新人")
  private String updateBy;
  /**
   * 更新日期
   */
  @Excel(name = "更新日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Schema(title =  "更新日期")
  private Date updateTime;
}
