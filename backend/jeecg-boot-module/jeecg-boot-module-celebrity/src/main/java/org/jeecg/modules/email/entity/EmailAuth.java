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
 * @Description: 邮箱授权表
 * @Author: dongruyang
 * @Date:   2025-11-07
 * @Version: V1.0
 */
@Data
@TableName("email_auth")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description="email_auth对象")
public class EmailAuth {
    
	/**ID*/
	@TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "ID")
	private String id;
	/**发送邮箱*/
	@Excel(name = "发送邮箱", width = 15)
    @Schema(title =  "发送邮箱")
	private String email;
	/**token*/
	@Excel(name = "token", width = 15)
    @Schema(title =  "token")
	private String token;
	/**刷新token*/
	@Excel(name = "刷新token", width = 15)
    @Schema(title =  "刷新token")
	private String refreshToken;
	/**token授权地址*/
	@Excel(name = "token授权地址", width = 15)
    @Schema(title =  "token授权地址")
	private String tokenUri;
	/**应用id*/
	@Excel(name = "应用id", width = 15)
    @Schema(title =  "应用id")
	private String clientId;
	/**应用密钥*/
	@Excel(name = "应用密钥", width = 15)
    @Schema(title =  "应用密钥")
	private String clientSecret;
	/**权限列表*/
	@Excel(name = "权限列表", width = 15)
    @Schema(title =  "权限列表")
	private String scopes;
	/**universeDomain*/
	@Excel(name = "universeDomain", width = 15)
    @Schema(title =  "universeDomain")
	private String universeDomain;
	/**account*/
	@Excel(name = "account", width = 15)
    @Schema(title =  "account")
	private String account;
	/**过期时间*/
	@Excel(name = "过期时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "过期时间")
	private Date expiry;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @Schema(title =  "备注")
	private String remark;
	/**创建人*/
	@Excel(name = "创建人", width = 15)
    @Schema(title =  "创建人")
	private String createBy;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "创建时间")
	private Date createTime;
	/**修改人*/
	@Excel(name = "修改人", width = 15)
    @Schema(title =  "修改人")
	private String updateBy;
	/**修改时间*/
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "修改时间")
	private Date updateTime;
}
