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
 * @Description: 邮箱信纸表
 * @Author: jeecg-boot
 * @Date: 2026-02-11
 * @Version: V1.0
 */
@Data
@TableName("email_stationery")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "邮箱信纸表")
public class EmailStationery {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private java.lang.String id;
    /**
     * 信纸名称（如：标准中文签名、英文专业版、节日限定）
     */
    @Excel(name = "信纸名称（如：标准中文签名、英文专业版、节日限定）", width = 15)
    @Schema(description = "信纸名称（如：标准中文签名、英文专业版、节日限定）")
    private java.lang.String name;
    /**
     * 唯一编码（程序调用用，可为空）
     */
    @Excel(name = "唯一编码（程序调用用，可为空）", width = 15)
    @Schema(description = "唯一编码（程序调用用，可为空）")
    private java.lang.String code;
    /**
     * 图片完整访问地址（CDN/OSS路径）
     */
    @Excel(name = "图片完整访问地址（CDN/OSS路径）", width = 15)
    @Schema(description = "图片完整访问地址（CDN/OSS路径）")
    private java.lang.String imageUrl;
    /**
     * 图片背景
     */
    @Excel(name = "图片背景", width = 15)
    @Schema(description = "图片背景")
    private java.lang.String imageBg;
    /**
     * 是否默认信纸：1=是，0=否
     */
    @Excel(name = "是否默认信纸：1=是，0=否", width = 15)
    @Schema(description = "是否默认信纸：1=是，0=否")
    private java.lang.Integer isDefault;
    /**
     * 排序值（越大越靠前）
     */
    @Excel(name = "排序值（越大越靠前）", width = 15)
    @Schema(description = "排序值（越大越靠前）")
    private java.lang.Integer sortOrder;
    /**
     * 状态：1=启用，0=禁用，-1=已删除
     */
    @Excel(name = "状态：1=启用，0=禁用，-1=已删除", width = 15)
    @Schema(description = "状态：1=启用，0=禁用，-1=已删除")
    private java.lang.Integer status;
    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private java.lang.String remark;
    /**
     * 创建人
     */
    @Excel(name = "创建人", width = 15)
    @Schema(description = "创建人")
    private java.lang.String createBy;
    /**
     * 创建时间
     */
    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private java.util.Date createTime;
    /**
     * 更新人
     */
    @Excel(name = "更新人", width = 15)
    @Schema(description = "更新人")
    private java.lang.String updateBy;
    /**
     * 更新时间
     */
    @Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private java.util.Date updateTime;
    /**
     * 租户/组织编码（多租户用）
     */
    @Excel(name = "租户/组织编码（多租户用）", width = 15)
    @Schema(description = "租户/组织编码（多租户用）")
    private java.lang.String sysOrgCode;
}
