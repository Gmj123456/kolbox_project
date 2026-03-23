package org.jeecg.modules.instagram.storebasics.entity;

import java.util.Date;
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
 * @Description: 客户信息表
 * @Author: jeecg-boot
 * @Date:   2020-12-19
 * @Version: V1.0
 */
@Data
@TableName("store_customer_message")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreCustomerMessage {
    
	/**主键*/
	@TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
	private String id;
	/**全名*/
	@Excel(name = "全名", width = 15)
    @Schema(title =  "全名")
	private String fullName;
	/**邮箱*/
	@Excel(name = "邮箱", width = 15)
    @Schema(title =  "邮箱")
	private String email;
	/**邮箱*/
	@Excel(name = "电话", width = 15)
	@Schema(title =  "电话")
	private String telephone;
	/**邮箱*/
	@Excel(name = "社交账号", width = 15)
	@Schema(title =  "社交账号")
	private String socialAccount;
	/**公司*/
	@Excel(name = "公司", width = 15)
    @Schema(title =  "公司")
	private String company;
	/**留言信息*/
	@Excel(name = "留言信息", width = 15)
    @Schema(title =  "留言信息")
	private String message;
	/**是否已读 0：未读；1：已读*/
	@Excel(name = "是否已读 0：未读；1：已读", width = 15)
    @Schema(title =  "是否已读 0：未读；1：已读")
	private Integer isRead;
	/**已读用户ID*/
	@Excel(name = "已读用户ID", width = 15)
    @Schema(title =  "已读用户ID")
	private String readUserId;
	/**已读用户名称*/
	@Excel(name = "已读用户名称", width = 15)
    @Schema(title =  "已读用户名称")
	private String readUserName;
	/**是否删除 0：未删除；1：已删除*/
	@Excel(name = "是否删除 0：未删除；1：已删除", width = 15)
    @Schema(title =  "是否删除 0：未删除；1：已删除")
	private Integer isDel;
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
