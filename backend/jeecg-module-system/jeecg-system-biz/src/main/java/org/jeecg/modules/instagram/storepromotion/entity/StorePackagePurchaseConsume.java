package org.jeecg.modules.instagram.storepromotion.entity;

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
 * @Description: 商家消费流量包表
 * @Author: jeecg-boot
 * @Date:   2020-10-01
 * @Version: V1.0
 */
@Data
@TableName("store_package_purchase_consume")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StorePackagePurchaseConsume {
    
	/**主键*/
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
	/**流量包ID*/
	@Excel(name = "流量包ID", width = 15)
    @Schema(title =  "流量包ID")
	private String emailPackageId;
	/**邮箱推送日志ID*/
	@Excel(name = "邮箱推送日志ID", width = 15)
    @Schema(title =  "邮箱推送日志ID")
	private String pushDetailId;
	/**使用时间*/
	@Excel(name = "使用时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "使用时间")
	private Date useTime;
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
