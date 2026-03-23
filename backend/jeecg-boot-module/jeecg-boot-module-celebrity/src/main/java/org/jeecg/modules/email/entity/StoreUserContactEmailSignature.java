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
 * @Description: 建联邮箱签名表
 * @Author: jeecgboot
 * @Date: 2025-11-07
 * @Version: V1.0
 */
@Data
@TableName("store_user_contact_email_signature")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreUserContactEmailSignature {

    /**
     * 主键id
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键id")
    private String id;
    /**
     * 网红顾问建联邮箱表ID
     */
    @Excel(name = "网红顾问建联邮箱表ID", width = 15)
    @Schema(title =  "网红顾问建联邮箱表ID")
    private String contactEmailId;
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
     * 邮箱显示名称
     */
    @Excel(name = "邮箱显示名称", width = 15)
    @Schema(title =  "邮箱显示名称")
    private String emailDisplayName;
    /**
     * 个性化签名标题
     */
    @Excel(name = "个性化签名标题", width = 15)
    @Schema(title =  "个性化签名标题")
    private String signatureTitle;
    /**
     * 个性化签名内容
     */
    @Excel(name = "个性化签名内容", width = 15)
    @Schema(title =  "个性化签名内容")
    private String signatureContent;
    /**
     * 个性化签名原始内容
     */
    @Excel(name = "个性化签名原始内容", width = 15)
    @Schema(title =  "个性化签名原始内容")
    private String signatureContentOriginal;
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
}
