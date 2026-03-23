package org.jeecg.modules.merchant.entity;

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
 * @Description: 商家信息表
 * @Author: dongruyang
 * @Date: 2025-03-12
 * @Version: V1.0
 */
@Data
@TableName("merchant_info")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MerchantInfo {

  /**
   * 主键ID
   */
  @TableId(type = IdType.ASSIGN_UUID)
  @Schema(title =  "主键ID")
  private String id;
  /**
   * 商务ID
   */
  @Schema(title =  "商务ID")
  private String businessId;
  @Excel(name = "商务", width = 15)
  @TableField(exist = false)
  private String businessName;
  /**
   * 品牌ID
   */
  @Schema(title =  "品牌ID")
  private String brandId;
  @Excel(name = "品牌", width = 15)
  @TableField(exist = false)
  private String brandName;
  /**
   * 主推产品
   */
  @Excel(name = "主推产品", width = 15)
  @Schema(title =  "主推产品")
  private String mainProducts;
  /**
   * 获客方式
   */
  @Excel(name = "获客方式", width = 15)
  @Schema(title =  "获客方式")
  private String customerAcquisitionMethod;
  /**
   * 销售渠道
   */
  @Excel(name = "销售渠道", width = 15)
  @Schema(title =  "销售渠道")
  private String salesChannel;
  /**
   * 客户量级
   */
  @Excel(name = "客户量级", width = 15)
  @Schema(title =  "客户量级")
  private String customerLevel;
  /**
   * 公司名称
   */
  @Excel(name = "公司名称", width = 15)
  @Schema(title =  "公司名称")
  private String companyName;
  /**
   * 地址
   */
  @Excel(name = "地址", width = 15)
  @Schema(title =  "地址")
  private String address;
  /**
   * 品牌关键对接人
   */
  @Excel(name = "品牌关键对接人", width = 15)
  @Schema(title =  "品牌关键对接人")
  private String brandContactPerson;
  /**
   * 电话
   */
  @Excel(name = "电话", width = 15)
  @Schema(title =  "电话")
  private String phone;
  /**
   * 微信号
   */
  @Excel(name = "微信号", width = 15)
  @Schema(title =  "微信号")
  private String wechatId;
  /**
   * 飞书号
   */
  @Excel(name = "飞书号", width = 15)
  @Schema(title =  "飞书号")
  private String feishuId;
  /**
   * 邮箱
   */
  @Excel(name = "邮箱", width = 15)
  @Schema(title =  "邮箱")
  private String email;
  /**
   * 合作状态
   */
  @Excel(name = "合作状态", width = 15)
  @Schema(title =  "合作状态")
  private String cooperationStatus;
  /**
   * 商务对接微信号
   */
  @Excel(name = "商务对接微信号", width = 15)
  @Schema(title =  "商务对接微信号")
  private String businessWechat;
  /**
   * 商务对接飞书号
   */
  @Excel(name = "商务对接飞书号", width = 15)
  @Schema(title =  "商务对接飞书号")
  private String businessFeishu;
  /**
   * 商务电话
   */
  @Excel(name = "商务电话", width = 15)
  @Schema(title =  "商务电话")
  private String businessPhone;
  /**
   * 备注
   */
  @Excel(name = "备注", width = 15)
  @Schema(title =  "备注")
  private Object remark;
  /**
   * 创建人
   */
  @Schema(title =  "创建人")
  private String createBy;
  /**
   * 创建日期
   */
  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Schema(title =  "创建日期")
  private Date createTime;
  /**
   * 更新人
   */
  @Schema(title =  "更新人")
  private String updateBy;
  /**
   * 更新日期
   */
  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Schema(title =  "更新日期")
  private Date updateTime;
  @TableField(exist = false)
  private String keyword;
}
