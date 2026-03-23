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

import java.util.Date;

/**
 * @Description: 邮箱账号
 * @Author: jeecg-boot
 * @Date: 2020-05-11
 * @Version: V1.0
 */
@Data
@TableName("store_email_account")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Emailaccount {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;

    /**商家ID*/
    @Excel(name = "商家ID", width = 15)
    @Schema(title =  "商家ID")
    private String sellerId;
    /**商家昵称*/
    @Excel(name = "商家昵称", width = 15)
    @Schema(title =  "商家昵称")
    private String sellerName;
    /**
     * 账号名称
     */
    @Excel(name = "账号名称", width = 15)
    @Schema(title =  "账号名称")
    private String accountName;
    /**
     * 账号密码
     */
    @Excel(name = "账号密码", width = 15)
    @Schema(title =  "账号密码")
    private String accountAppPassword;
    /**
     * 使用次数
     */
    @Excel(name = "使用次数", width = 15)
    @Schema(title =  "使用次数")
    private Integer useCount;
    /**
     * 使用日期
     */
    @Excel(name = "使用日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "使用日期")
    private Date useDate;
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
     * accessToken
     */
    @Excel(name = "accessToken", width = 15)
    @Schema(title =  "accessToken")
    private String accessToken;

    /**
     * refreshToken
     */
    @Excel(name = "refreshToken", width = 15)
    @Schema(title =  "refreshToken")
    private String refreshToken;


    /**
     * accessToken过期时间
     */
    @Excel(name = "accessToken过期时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "accessToken过期时间")
    private Date expiresTime;


    /**
     * 邮箱账号用户名称
     */
    @Excel(name = "邮箱账号用户名称", width = 15)
    @Schema(title =  "邮箱账号用户名称")
    private String accountUserName;


    /**
     * 用户图片
     */
    @Excel(name = "用户图片", width = 15)
    @Schema(title =  "用户图片")
    private String accountPictureUrl;

    /**
     * 用户认证ID
     */
    @Excel(name = "用户认证ID", width = 15)
    @Schema(title =  "用户认证ID")
    private String authId;

    /**
     * 账号类型，区分sendCloud发送账号   0 默认 1sendCloud
     */
    @Excel(name = "账号类型", width = 15)
    @Schema(title =  "账号类型")
    private Integer accountType;
}
