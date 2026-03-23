package org.jeecg.modules.instagram.storecelebrity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Description: 私有网红社媒属性关联表
 * @Author: jeecg-boot
 * @Date: 2025-07-24
 * @Version: V1.0
 */
@Data
@TableName("store_celebrity_private_attribute_relation")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreCelebrityPrivateAttributeRelation {

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
     * 网红ID
     */
    @Excel(name = "网红ID", width = 15)
    @Schema(title =  "网红ID")
    private String celebrityId;
    /**
     * 账号
     */
    @Excel(name = "账号", width = 15)
    @Schema(title =  "账号")
    private String account;
    /**
     * 属性ID，关联 kol_attribute
     */
    @Excel(name = "属性ID，关联 kol_attribute", width = 15)
    @Schema(title =  "属性ID，关联 kol_attribute")
    private String attributeId;
    /**
     * 属性编码
     */
    @Excel(name = "属性编码", width = 15)
    @Schema(title =  "属性编码")
    private String attributeCode;
    /**
     * 属性名称
     */
    @Excel(name = "属性名称", width = 15)
    @Schema(title =  "属性名称")
    private String attributeName;
    /**
     * 属性类型ID
     */
    @Excel(name = "属性类型ID", width = 15)
    @Schema(title =  "属性类型ID")
    private String attributeTypeId;
    /**
     * 属性类型名称
     */
    @Excel(name = "属性类型名称", width = 15)
    @Schema(title =  "属性类型名称")
    private String attributeTypeName;
    /**
     * 重复次数
     */
    @Excel(name = "重复次数", width = 15)
    @Schema(title =  "重复次数")
    private Integer repeatQty;
    /**
     * 总数量
     */
    @Excel(name = "总数量", width = 15)
    @Schema(title =  "总数量")
    private Integer totalQty;
    /**
     * 占比(%),由 repeat_qty/total_qty*100计算
     */
    @Excel(name = "占比(%),由 repeat_qty/total_qty*100计算", width = 15)
    @Schema(title =  "占比(%),由 repeat_qty/total_qty*100计算")
    private java.math.BigDecimal percentage;
    /**
     * 排名
     */
    @Excel(name = "排名", width = 15)
    @Schema(title =  "排名")
    @TableField("`rank`")
    private Integer rank;
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
    private java.util.Date createTime;
    /**
     * 更新人
     */
    @Excel(name = "更新人", width = 15)
    @Schema(title =  "更新人")
    private String updateBy;
    /**
     * 更新时间
     */
    @Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "更新时间")
    private java.util.Date updateTime;
}
