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
 * @Description: 私有网红网红顾问签约表
 * @Author: nqr
 * @Date: 2023-09-05
 * @Version: V1.0
 */
@Data
@TableName("store_celebrity_private_counselor")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreCelebrityPrivateCounselor {

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
     * 网红顾问_ID
     */
    @Excel(name = " 网红顾问_ID", width = 15)
    @Schema(title =  " 网红顾问_ID")
    private String celebrityCounselorId;
    /**
     * 网红顾问名称
     */
    @Excel(name = " 网红顾问名称", width = 15)
    @Schema(title =  " 网红顾问名称")
    private String celebrityCounselorName;

    private String email;
    /**
     * 签约费用
     */
    @Excel(name = "签约费用", width = 15)
    @Schema(title =  "签约费用")
    private java.math.BigDecimal contractAmount;
    /**
     * 排序
     */
    @Excel(name = "排序", width = 15)
    @Schema(title =  "排序")
    private Integer sort;
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
     * 网红建联邮箱
     */
    private String celebrityContactEmail;
    /**
     * 签约时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "签约时间")
    private Date contractTime;

    /**
     * 网红内容签约费用
     */
    @Schema(title =  "网红内容签约费用")
    private String contractInfo;
    /**
     * 是否调整网红顾问 0：否（默认）；1：是
     */
    private Integer isAdjust;
    /**
     * 调整网红顾问_ID
     */
    private String adjustCelebrityCounselorId;
    /**
     * 调整网红顾问名称
     */
    private String adjustCelebrityCounselorName;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "调整网红顾问时间")
    private Date adjustTime;

}
