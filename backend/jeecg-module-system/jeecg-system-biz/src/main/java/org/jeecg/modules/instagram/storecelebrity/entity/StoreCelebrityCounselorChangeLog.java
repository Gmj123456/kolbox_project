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
 * @Description: 网红顾问变更日志
 * @Author: jeecg-boot
 * @Date:   2025-10-30
 * @Version: V1.0
 */
@Data
@TableName("store_celebrity_counselor_change_log")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreCelebrityCounselorChangeLog {
    
	/**变更ID*/
	@TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "变更ID")
	private String id;
	/**操作人ID*/
	@Excel(name = "操作人ID", width = 15)
    @Schema(title =  "操作人ID")
	private String operatorId;
	/**操作人名称*/
	@Excel(name = "操作人名称", width = 15)
    @Schema(title =  "操作人名称")
	private String operatorName;
	/**被交接顾问ID*/
	@Excel(name = "被交接顾问ID", width = 15)
    @Schema(title =  "被交接顾问ID")
	private String fromCounselorId;
	/**被交接顾问名称*/
	@Excel(name = "被交接顾问名称", width = 15)
    @Schema(title =  "被交接顾问名称")
	private String fromCounselorName;
	/**被交接人建联邮箱*/
	@Excel(name = "被交接人建联邮箱", width = 15)
    @Schema(title =  "被交接人建联邮箱")
	private String fromContactEmail;
	/**交接顾问ID*/
	@Excel(name = "交接顾问ID", width = 15)
    @Schema(title =  "交接顾问ID")
	private String toCounselorId;
	/**交接顾问名称*/
	@Excel(name = "交接顾问名称", width = 15)
    @Schema(title =  "交接顾问名称")
	private String toCounselorName;
	/**交接人建联邮箱*/
	@Excel(name = "交接人建联邮箱", width = 15)
    @Schema(title =  "交接人建联邮箱")
	private String toContactEmail;
	/**是否已合作：0-仅建联，1-合作中/已合作*/
	@Excel(name = "是否已合作：0-仅建联，1-合作中/已合作", width = 15)
    @Schema(title =  "是否已合作：0-仅建联，1-合作中/已合作")
	private Integer isCooperated;
	/**变更状态：1-成功，-1-异常*/
	@Excel(name = "变更状态：1-成功，-1-异常", width = 15)
    @Schema(title =  "变更状态：1-成功，-1-异常，-2-取消")
	private Integer changeStatus;
	/**异常备注*/
	@Excel(name = "异常备注", width = 15)
    @Schema(title =  "异常备注")
	private String remark;
	/**创建人*/
	@Excel(name = "创建人", width = 15)
    @Schema(title =  "创建人")
	private String createBy;
	/**变更时间*/
	@Excel(name = "变更时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "变更时间")
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
