package org.jeecg.modules.instagram.storeseller.storeuser.entity;

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
 * @Description: 商家公司信息表
 * @Author: jeecg-boot
 * @Date:   2021-02-06
 * @Version: V1.0
 */
@Data
@TableName("store_user_company")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreUserCompany {
    
	/**主键id*/
	@TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键id")
	private String id;
	/**用户id*/
	@Excel(name = "用户id", width = 15)
    @Schema(title =  "用户id")
	private String userId;
	/**公司名称*/
	@Excel(name = "公司名称", width = 15)
    @Schema(title =  "公司名称")
	private String companyName;
	/**联系人*/
	@Excel(name = "联系人", width = 15)
    @Schema(title =  "联系人")
	private String linkMan;
	/**联系电话*/
	@Excel(name = "联系电话", width = 15)
    @Schema(title =  "联系电话")
	private String linkTel;
	/**公司税号*/
	@Excel(name = "公司税号", width = 15)
    @Schema(title =  "公司税号")
	private String invoiceCode;
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
