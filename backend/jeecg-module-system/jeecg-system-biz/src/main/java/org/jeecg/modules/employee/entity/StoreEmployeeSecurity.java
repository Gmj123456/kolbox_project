package org.jeecg.modules.employee.entity;

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
 * @Description: 员工防伪
 * @Author: nqr
 * @Date:   2023-05-29
 * @Version: V1.0
 */
@Data
@TableName("store_employee_security")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreEmployeeSecurity {
    
	/**id*/
	@TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "id")
	private String id;
	/**姓名*/
	@Excel(name = "姓名", width = 15)
    @Schema(title =  "姓名")
	private String name;
	/**英文名*/
	@Excel(name = "英文名", width = 15)
    @Schema(title =  "英文名")
	private String nameEn;
	/**微信名*/
	@Excel(name = "微信名", width = 15)
    @Schema(title =  "微信名")
	private String nameWechat;
	/**手机号*/
	@Excel(name = "手机号", width = 15)
    @Schema(title =  "手机号")
	private String phone;
	/**邮箱*/
	@Excel(name = "邮箱", width = 15)
    @Schema(title =  "邮箱")
	private String email;
	/**微信号*/
	@Excel(name = "微信号", width = 15)
    @Schema(title =  "微信号")
	private String wechatNum;
	/**职务*/
	@Excel(name = "职务", width = 15)
    @Schema(title =  "职务")
	private String job;
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
	/**更新人*/
	@Excel(name = "更新人", width = 15)
    @Schema(title =  "更新人")
	private String updateBy;
	/**更新时间*/
	@Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "更新时间")
	private Date updateTime;
}
