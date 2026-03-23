package org.jeecg.modules.tiktok.entity;

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
 * @Description: 分配日志
 * @Author: nqr
 * @Date:   2023-11-03
 * @Version: V1.0
 */
@Data
@TableName("tiktok_allot_log")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema( description="分配日志")
public class TiktokAllotLog {
    
	/**主键*/
	@TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
	private String id;
	/**分配内容*/
	@Excel(name = "分配内容", width = 15)
    @Schema(title =  "分配内容")
	private String allotContent;
	/**分配时间*/
	@Excel(name = "分配时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "分配时间")
	private Date allotTime;
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
	/**删除状态（0=未删除,1=已删除）*/
	@Excel(name = "删除状态（0=未删除,1=已删除）", width = 15)
    @Schema(title =  "删除状态（0=未删除,1=已删除）")
	private Integer isDelete;
	/**
	 * 平台类型
	 */
	private Integer platformType;
}
