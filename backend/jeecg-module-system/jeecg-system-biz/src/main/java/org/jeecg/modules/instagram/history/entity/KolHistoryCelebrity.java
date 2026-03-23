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
 * @Description: 历史提报网红主表
 * @Author: jeecg-boot
 * @Date: 2025-11-26
 * @Version: V1.0
 */
@Data
@TableName("kol_history_celebrity")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class KolHistoryCelebrity {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
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
     * 性别 0=男 1=女 2=未知
     */
    @Excel(name = "性别 0=男 1=女 2=未知", width = 15)
    @Schema(title =  "性别 0=男 1=女 2=未知")
    private String gender;
    /**
     * 国籍
     */
    @Excel(name = "国籍", width = 15)
    @Schema(title =  "国籍")
    private String countryName;
    /**
     * 粉丝数
     */
    @Excel(name = "粉丝数", width = 15)
    @Schema(title =  "粉丝数")
    private Integer followersNum;
    /**
     * 平均播放量（均播）
     */
    @Excel(name = "平均播放量（均播）", width = 15)
    @Schema(title =  "平均播放量（均播）")
    private Integer playAvgNum;
    /**
     * 主页链接
     */
    @Excel(name = "主页链接", width = 15)
    @Schema(title =  "主页链接")
    private String webUrl;
    /**
     * 达人备注
     */
    @Excel(name = "达人备注", width = 15)
    @Schema(title =  "达人备注")
    private Object celebrityRemark;
    /**
     * 网红顾问名称
     */
    @Excel(name = "网红顾问名称", width = 15)
    @Schema(title =  "网红顾问名称")
    private String celebrityCounselorName;
    /**
     * 最近提报品牌
     */
    @Excel(name = "最近提报品牌", width = 15)
    @Schema(title =  "最近提报品牌")
    private String brandName;
    /**
     * 最近提报产品
     */
    @Excel(name = "最近提报产品", width = 15)
    @Schema(title =  "最近提报产品")
    private String productName;
    /**
     * 最近提报日期
     */
    @Excel(name = "最近提报日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(title =  "最近提报日期")
    private Date lastSubmitDate;
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
     * 删除标志 0=正常 1=已删除
     */
    @Excel(name = "删除标志 0=正常 1=已删除", width = 15)
    @Schema(title =  "删除标志 0=正常 1=已删除")
    private Integer isDelete;

    @TableField(exist = false)
    private String kolTypes;

    @TableField(exist = false)
    private Integer sendCount;

    @TableField(exist = false)
    private Integer openCount;

    @TableField(exist = false)
    private Integer replyCount;
}
