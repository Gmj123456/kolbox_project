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
 * @Description: 商务Email模板
 * @Author: jeecgboot
 * @Date: 2025-11-07
 * @Version: V1.0
 */
@Data
@TableName("email_template_business")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class EmailTemplateBusiness {

    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title = "id")
    private String id;

    @Excel(name = "模版邮件标题", width = 15)
    @Schema(title = "模版邮件标题")
    private String templateTitle;

    @Excel(name = "模版邮件内容", width = 15)
    @Schema(title = "模版邮件内容")
    private String templateContent;

    @Excel(name = "模版邮件原始内容", width = 15)
    @Schema(title = "模版邮件原始内容")
    private String templateContentOriginal;

    @Excel(name = "商务顾问_ID", width = 15)
    @Schema(title = "商务顾问_ID")
    private String counselorId;

    @Excel(name = "商务顾问名称", width = 15)
    @Schema(title = "商务顾问名称")
    private String counselorName;

    @Excel(name = "品牌类目", width = 15)
    @Schema(title = "品牌类目")
    private String brandCategory;

    @Excel(name = "品牌类目ID", width = 15)
    @Schema(title = "品牌类目ID")
    private String brandCategoryId;

    @Excel(name = "排序", width = 15)
    @Schema(title = "排序")
    private Integer sortCode;

    @Excel(name = "备注", width = 15)
    @Schema(title = "备注")
    private String remark;

    @Excel(name = "创建人", width = 15)
    @Schema(title = "创建人")
    private String createBy;

    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title = "创建时间")
    private Date createTime;

    @Excel(name = "修改人", width = 15)
    @Schema(title = "修改人")
    private String updateBy;

    @Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title = "修改时间")
    private Date updateTime;
}
