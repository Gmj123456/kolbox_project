package org.jeecg.common.system.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 网红顾问建联邮箱表
 * @Author: dongruyang
 * @Date: 2024-01-15
 * @Version: V1.0
 */
@Data
public class StoreUserContactEmailVo {

    /**
     * 主键id
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键id")
    private String id;
    /**
     * 网红顾问人员ID
     */
    @Excel(name = "网红顾问人员ID", width = 15)
    @Schema(title =  "网红顾问人员ID")
    private String sysUserId;
    /**
     * 网红顾问人员名称
     */
    @Excel(name = "网红顾问人员名称", width = 15)
    @Schema(title =  "网红顾问人员名称")
    private String sysUserName;
    /**
     * 建联邮箱
     */
    @Excel(name = "建联邮箱", width = 15)
    @Schema(title =  "建联邮箱")
    private String contactEmail;
    /**
     * 邮箱密码
     */
    @Excel(name = "邮箱密码", width = 15)
    @Schema(title =  "邮箱密码")
    private String emailPassword;
    /**
     * 邮箱显示名称
     */
    @Excel(name = "邮箱显示名称", width = 15)
    @Schema(title =  "邮箱显示名称")
    private String emailDisplayName;
    /**
     * 邮箱状态 0：不可用；1：可用（默认）
     */
    @Excel(name = "邮箱状态 0：不可用；1：可用（默认）", width = 15)
    @Schema(title =  "邮箱状态 0：不可用；1：可用（默认）")
    private Integer emailStatus;
    /**
     * 0:未授权   1:已授权
     */
    private Integer isAuthorized;
    /**
     * 排序码
     */
    @Excel(name = "排序码", width = 15)
    @Schema(title =  "排序码")
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
     * 邮箱类型（0：Gmail；1:企业邮箱）
     */
    private Integer emailType;
    /**
     * 邮箱类型 0建联邮箱 1企业邮箱
     */
    @Excel(name = "邮箱类型", width = 15)
    @Schema(title = "邮箱类型")
    private Integer type;
    /**
     * 邮箱信纸ID
     */
    private String stationeryId;
    /**
     * 邮箱信纸图片URL
     */
    private String stationeryImageUrl;
    /**
     * 邮箱信纸背景
     */
    private String stationeryImageBg;
    /**
     * 邮箱信纸背景code
     */
    private String stationeryImageCode;
}
