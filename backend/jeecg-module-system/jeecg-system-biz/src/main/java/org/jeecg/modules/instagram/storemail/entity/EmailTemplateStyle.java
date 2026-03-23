package org.jeecg.modules.instagram.storemail.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * @Description: 邮件模板样式
 * @Author: jeecg-boot
 * @Date: 2020-12-15
 * @Version: V1.0
 */
@Data
@TableName("email_template_style")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class EmailTemplateStyle {

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "id")
    private String id;
    /**
     * 模板标题
     */
    @Excel(name = "模板标题", width = 15)
    @Schema(title =  "模板标题")
    private String templateTitle;
    /**
     * 模板图片
     */
    @Excel(name = "模板图片", width = 15)
    @Schema(title =  "模板图片")
    private String templateImg;
    /**
     * 模板内容
     */
    @Excel(name = "模板内容", width = 15)
    @Schema(title =  "模板内容")
    private Object templateContent;
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
