package org.jeecg.modules.instagram.storecelebrity.entity;


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

import java.util.Date;

/**
 * @Description: 网红顾问变更日志明细
 * @Author: jeecg-boot
 * @Date: 2025-10-30
 * @Version: V1.0
 */
@Data
@TableName("store_celebrity_counselor_change_detail")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreCelebrityCounselorChangeDetail {

    /**
     * 明细ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "明细ID")
    private String id;
    /**
     * 关联变更主日志ID
     */
    @Excel(name = "关联变更主日志ID", width = 15)
    @Schema(title =  "关联变更主日志ID")
    private String changeLogId;
    /**
     * 私有网红ID
     */
    @Excel(name = "私有网红ID", width = 15)
    @Schema(title =  "私有网红ID")
    private String celebrityPrivateId;
    /**
     * 原建联记录ID（store_celebrity_private_counselor.id）
     */
    @Excel(name = "原建联记录ID（store_celebrity_private_counselor.id）", width = 15)
    @Schema(title =  "原建联记录ID（store_celebrity_private_counselor.id）")
    private String sourceRecordId;
    /**
     * 操作类型：0-DELETE-删除，1-INSERT-新增
     */
    @Excel(name = "操作类型：0-DELETE-删除，1-INSERT-新增", width = 15)
    @Schema(title =  "操作类型：0-DELETE-删除，1-INSERT-新增")
    private Integer operationType;
    /**
     * 顾问ID（快照）
     */
    @Excel(name = "顾问ID（快照）", width = 15)
    @Schema(title =  "顾问ID（快照）")
    private String celebrityCounselorId;
    /**
     * 顾问名称（快照）
     */
    @Excel(name = "顾问名称（快照）", width = 15)
    @Schema(title =  "顾问名称（快照）")
    private String celebrityCounselorName;
    /**
     * 网红邮箱（快照）
     */
    @Excel(name = "网红邮箱（快照）", width = 15)
    @Schema(title =  "网红邮箱（快照）")
    private String email;
    /**
     * 建联邮箱（快照）
     */
    @Excel(name = "建联邮箱（快照）", width = 15)
    @Schema(title =  "建联邮箱（快照）")
    private String celebrityContactEmail;
    /**
     * 签约费用（快照）
     */
    @Excel(name = "签约费用（快照）", width = 15)
    @Schema(title =  "签约费用（快照）")
    private java.math.BigDecimal contractAmount;
    /**
     * 签约时间（快照）
     */
    @Excel(name = "签约时间（快照）", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "签约时间（快照）")
    private Date contractTime;
    /**
     * 签约内容JSON快照
     */
    @Excel(name = "签约内容JSON快照", width = 15)
    @Schema(title =  "签约内容JSON快照")
    private String contractInfo;
    /**
     * 排序（快照）
     */
    @Excel(name = "排序（快照）", width = 15)
    @Schema(title =  "排序（快照）")
    private Integer sort;
    /**
     * 备注（快照）
     */
    @Excel(name = "备注（快照）", width = 15)
    @Schema(title =  "备注（快照）")
    private String remark;
    /**
     * 创建人
     */
    @Excel(name = "创建人", width = 15)
    @Schema(title =  "创建人")
    private String createBy;
    /**
     * 快照时间
     */
    @Excel(name = "快照时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "快照时间")
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
