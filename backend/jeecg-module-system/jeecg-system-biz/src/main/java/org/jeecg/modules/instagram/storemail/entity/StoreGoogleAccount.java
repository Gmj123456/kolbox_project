package org.jeecg.modules.instagram.storemail.entity;

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
 * @Description: 谷歌账号
 * @Author: jeecg-boot
 * @Date:   2020-04-23
 * @Version: V1.0
 */
@Data
@TableName("store_google_account")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreGoogleAccount {
    
	/**主键*/
	@TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
	private String id;
	/**账号名称*/
	@Excel(name = "账号名称", width = 15)
    @Schema(title =  "账号名称")
	private String accountName;
	/**账号密码*/
	@Excel(name = "账号密码", width = 15)
    @Schema(title =  "账号密码")
	private String accountPassword;
	/**账号key*/
	@Excel(name = "账号key", width = 15)
    @Schema(title =  "账号key")
	private String accountKey;
	/**使用日期*/
	@Excel(name = "使用日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "使用日期")
	private Date useDate;
	/**使用状态0：未使用;1：使用中;-1：配额已满*/
	@Excel(name = "使用状态0：未使用;1：使用中;-1：配额已满", width = 15)
    @Schema(title =  "使用状态0：未使用;1：使用中;-1：配额已满")
	private Integer useStatus;
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

	/**类型 0：google；1：ins*/
	@Excel(name = "类型 0：google；1：ins", width = 15)
	@Schema(title =  "类型 0：google；1：ins")
	private Integer type;

	/**失败次数*/
	@Excel(name = "失败次数", width = 15)
	@Schema(title =  "失败次数")
	private Integer errorCount;

	/**代理IP*/
	@Excel(name = "代理IP", width = 15)
	@Schema(title =  "代理IP")
	private String ip;
}
