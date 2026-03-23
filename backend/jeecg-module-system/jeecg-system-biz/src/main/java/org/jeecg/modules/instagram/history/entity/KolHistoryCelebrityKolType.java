package org.jeecg.modules.instagram.history.entity;

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
 * @Description: 历史提报网红达人类型表
 * @Author: jeecg-boot
 * @Date: 2025-11-26
 * @Version: V1.0
 */
@Data
@TableName("kol_history_celebrity_kol_type")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class KolHistoryCelebrityKolType {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
    /**
     * 历史提报网红主表ID
     */
    @Excel(name = "历史提报网红主表ID", width = 15)
    @Schema(title =  "历史提报网红主表ID")
    private String celebrityId;
    /**
     * 网红名称（唯一标识字段之一）
     */
    @Excel(name = "网红名称（唯一标识字段之一）", width = 15)
    @Schema(title =  "网红名称（唯一标识字段之一）")
    private String celebrityName;
    /**
     * 平台类型 0=instagram;1=youtube;2:tiktok
     */
    @Excel(name = "平台类型 0=instagram;1=youtube;2:tiktok", width = 15)
    @Schema(title =  "平台类型 0=instagram;1=youtube;2:tiktok")
    private Integer platformType;
    /**
     * 达人类型单值（去重后）
     */
    @Excel(name = "达人类型单值（去重后）", width = 15)
    @Schema(title =  "达人类型单值（去重后）")
    private String kolType;
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
     * 删除标志 0=正常 1=已删除
     */
    @Excel(name = "删除标志 0=正常 1=已删除", width = 15)
    @Schema(title =  "删除标志 0=正常 1=已删除")
    private Integer isDelete;
}
