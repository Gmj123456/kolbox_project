package org.jeecg.modules.kol.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 用户飞书在线表格关联表
 * @Author: dongruyang
 * @Date:   2025-09-15
 * @Version: V1.0
 */
@Data
@TableName("kol_sys_user_feishu_sheet")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description="用户飞书在线表格关联表")
public class KolSysUserFeishuSheet {
    
	/**主键*/
	@TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
	private String id;
	/**用户ID*/
	@Excel(name = "用户ID", width = 15)
    @Schema(title =  "用户ID")
	private String sysUserId;
	/**登录账号*/
	@Excel(name = "登录账号", width = 15)
    @Schema(title =  "登录账号")
	private String username;
	/**真实姓名*/
	@Excel(name = "真实姓名", width = 15)
    @Schema(title =  "真实姓名")
	private String realname;
	/**飞书在线表格ID*/
	@Excel(name = "飞书在线表格ID", width = 15)
    @Schema(title =  "飞书在线表格ID")
	private String spreadSheetId;
	/**飞书在线表格URL*/
	@Excel(name = "飞书在线表格URL", width = 15)
    @Schema(title =  "飞书在线表格URL")
	private String spreadSheetUrl;
	/**飞书在线表格sheetID*/
	@Excel(name = "飞书在线表格sheetID", width = 15)
    @Schema(title =  "飞书在线表格sheetID")
	private String sheetId;
	/**飞书在线表格类型（PrivateCelebrity、PersonalTags、Country、ContactEmail、Product）*/
	@Excel(name = "飞书在线表格类型（PrivateCelebrity、PersonalTags、Country、ContactEmail、Product）", width = 15)
    @Schema(title =  "飞书在线表格类型（PrivateCelebrity、PersonalTags、Country、ContactEmail、Product）")
	private String spreadSheetType;
	/**开始列*/
	@Excel(name = "开始列", width = 15)
    @Schema(title =  "开始列")
	private String startColumn;
	/**结束列*/
	@Excel(name = "结束列", width = 15)
    @Schema(title =  "结束列")
	private String endColumn;
	/**标题行*/
	@Excel(name = "标题行", width = 15)
    @Schema(title =  "标题行")
	private Integer headerRow;
	/**是否同步列*/
	@Excel(name = "是否同步列", width = 15)
    @Schema(title =  "是否同步列")
	private String isSynchronizeColumn;
	/**错误原因列*/
	@Excel(name = "错误原因列", width = 15)
    @Schema(title =  "错误原因列")
	private String errorReasonColumn;
	/**创建人*/
	@Excel(name = "创建人", width = 15)
    @Schema(title =  "创建人")
	private String createBy;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "创建时间")
	private java.util.Date createTime;
	/**更新人*/
	@Excel(name = "更新人", width = 15)
    @Schema(title =  "更新人")
	private String updateBy;
	/**更新时间*/
	@Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "更新时间")
	private java.util.Date updateTime;
	/**逻辑删除标志（0:未删除，1:已删除）*/
	@Excel(name = "逻辑删除标志（0:未删除，1:已删除）", width = 15)
    @Schema(title =  "逻辑删除标志（0:未删除，1:已删除）")
	private Integer delFlag;
}
