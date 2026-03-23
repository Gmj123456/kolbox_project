package org.jeecg.modules.email.entity;

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
 * @Description: 邮件模板
 * @Author: jeecgboot
 * @Date: 2025-11-07
 * @Version: V1.0
 */
@Data
@TableName("email_template")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class EmailTemplate {

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "id")
    private String id;
    /**
     * 模板标题
     */
    @Excel(name = "模版邮件标题", width = 15)
    @Schema(title =  "模版邮件标题")
    private String templateTitle;
    /**
     * 模板内容
     */
    @Excel(name = "模版邮件内容", width = 15)
    @Schema(title =  "模版邮件内容")
    private String templateContent;
    /**
     * 模板内容原始内容
     */
    @Excel(name = "模版邮件原始内容", width = 15)
    @Schema(title =  "模版邮件原始内容")
    private String templateContentOriginal;
    /**
     * 网红顾问_ID
     */
    @Excel(name = "网红顾问_ID", width = 15)
    @Schema(title =  "网红顾问_ID")
    private String counselorId;
    /**
     * 网红顾问名称
     */
    @Excel(name = "网红顾问名称", width = 15)
    @Schema(title =  "网红顾问名称")
    private String counselorName;
    /**
     * 排序
     */
    @Excel(name = "排序", width = 15)
    @Schema(title =  "排序")
    private Integer sortCode;
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
     * 品牌ID
     */
    @Excel(name = "品牌ID", width = 15)
    @Schema(title =  "品牌ID")
    private String brandId;
    /**
     * 品牌名称
     */
    @Excel(name = "品牌", width = 15)
    @Schema(title =  "品牌")
    private String brandName;
    /**
     * 产品ID
     */
    @Excel(name = "产品ID", width = 15)
    @Schema(title =  "品牌ID")
    private String productId;
    /**
     * 产品名称
     */
    @Excel(name = "产品名称", width = 15)
    @Schema(title =  "产品名称")
    private String productName;

    /**
     * 平台类型（0:IG;1:YT;2:TK）
     */
    @Schema(title =  "平台类型")
    private Integer platformType;
}
