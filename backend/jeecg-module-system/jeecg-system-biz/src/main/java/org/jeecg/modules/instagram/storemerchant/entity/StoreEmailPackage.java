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
 * @Description: 流量包表
 * @Author: jeecg-boot
 * @Date: 2020-10-01
 * @Version: V1.0
 */
@Data
@TableName("store_email_package")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreEmailPackage {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
    /**
     * 流量包名称
     */
    @Excel(name = "流量包名称", width = 15)
    @Schema(title =  "流量包名称")
    private String packageName;
    /**
     * 流量包流量
     */
    @Excel(name = "流量包流量", width = 15)
    @Schema(title =  "流量包流量")
    private Integer packageNum;
    /**
     * 流量包价格
     */
    @Excel(name = "流量包价格", width = 15)
    @Schema(title =  "流量包价格")
    private java.math.BigDecimal packagePrice;
    /**
     * 流量包类型
     */
    @Excel(name = "流量包类型", width = 15)
    @Schema(title =  "流量包类型")
    private Integer packageType;
    /**
     * 流量包描述
     */
    @Excel(name = "流量包描述", width = 15)
    @Schema(title =  "流量包描述")
    private String packageDesc;
    /**
     * 流量包有效天数
     */
    @Excel(name = " 流量包有效天数", width = 15)
    @Schema(title =  " 流量包有效天数")
    private Integer expirationDay;
    /**
     * 流量包属性
     */
    @Excel(name = "流量包属性", width = 15)
    @Schema(title =  "流量包属性")
    private Integer packageProperty;
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
