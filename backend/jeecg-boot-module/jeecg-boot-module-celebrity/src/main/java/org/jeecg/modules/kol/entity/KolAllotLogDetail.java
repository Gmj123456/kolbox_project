package org.jeecg.modules.kol.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: kol_allot_log_detail
 * @Author: jeecg-boot
 * @Date:   2024-12-11
 * @Version: V1.0
 */
@Data
@TableName("kol_allot_log_detail")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema( description="kol_allot_log_detail")
public class KolAllotLogDetail implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
	/**分配ID*/
	@Excel(name = "分配ID", width = 15)
    @Schema(title =  "分配ID")
    private String allotId;
	/**网红顾问_ID*/
	@Excel(name = "网红顾问_ID", width = 15)
    @Schema(title =  "网红顾问_ID")
    private String counselorId;
	/**网红顾问名称*/
	@Excel(name = "网红顾问名称", width = 15)
    @Schema(title =  "网红顾问名称")
    private String counselorName;
	/**平台类型（0:IG;1:YT;2:TK）*/
	@Excel(name = "平台类型（0:IG;1:YT;2:TK）", width = 15)
    @Schema(title =  "平台类型（0:IG;1:YT;2:TK）")
    private Integer platformType;
	/**分配内容*/
	@Excel(name = "分配内容", width = 15)
    @Schema(title =  "分配内容")
    private String allotContent;
	/**分配时间*/
	@Excel(name = "分配时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(title =  "分配时间")
    private Date allotTime;
	/**删除状态（0=未删除,1=已删除）*/
	@Excel(name = "删除状态（0=未删除,1=已删除）", width = 15)
    @Schema(title =  "删除状态（0=未删除,1=已删除）")
    private Integer isDelete;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @Schema(title =  "备注")
    private String remark;
	/**创建人*/
	@Excel(name = "创建人", width = 15)
    @Schema(title =  "创建人")
    private String createBy;
	/**创建时间*/
	@Excel(name = "创建时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(title =  "创建时间")
    private Date createTime;
	/**修改人*/
	@Excel(name = "修改人", width = 15)
    @Schema(title =  "修改人")
    private String updateBy;
	/**修改时间*/
	@Excel(name = "修改时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(title =  "修改时间")
    private Date updateTime;
}
