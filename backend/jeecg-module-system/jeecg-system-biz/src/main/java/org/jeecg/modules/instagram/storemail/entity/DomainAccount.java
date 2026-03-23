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
 * @Description: 域名账号管理
 * @Author: jeecg-boot
 * @Date: 2020-11-27
 * @Version: V1.0
 */
@Data
@TableName("domain_account")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DomainAccount {

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "id")
    private String id;
    /**
     * access
     */
    @Excel(name = "access", width = 15)
    @Schema(title =  "access")
    private String access;
    /**
     * secret
     */
    @Excel(name = "secret", width = 15)
    @Schema(title =  "secret")
    private String secret;
    /**
     * 发送域名
     */
    @Excel(name = "发送域名", width = 15)
    @Schema(title =  "发送域名")
    private String sendDomain;
    /**
     * smtp密码
     */
    @Excel(name = "smtp密码", width = 15)
    @Schema(title =  "smtp密码")
    private String smtpPwd;
    /**
     * 所属账号
     */
    @Excel(name = "所属账号", width = 15)
    @Schema(title =  "所属账号")
    private String account;

    /**
     * 状态 0正常 1禁用
     */
    @Excel(name = "状态", width = 15)
    @Schema(title =  "状态")
    private String status;

    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "创建时间")
    private Date createTime;

    /**
     * 使用日期
     */
    @Excel(name = "使用日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "创建时间")
    private String useDate;
    /**
     * 使用次数
     */
    @Excel(name = "使用次数", width = 15)
    @Schema(title =  "使用次数")
    private Integer useCount;
    /**
     * 权重
     */
    @Excel(name = "权重", width = 15)
    @Schema(title =  "权重")
    private Integer weight;
    /**
     * 账户类型
     */
    @Excel(name = "账户类型", width = 15)
    @Schema(title =  "账户类型")
    private Integer type;

}
