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
 * @Description: 用户监控提醒
 * @Author: jeecg-boot
 * @Date: 2021-04-29
 * @Version: V1.0
 */
@Data
@TableName("store_user_remind")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreUserRemind {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
    /**
     * 商家ID
     */
    @Excel(name = "商家ID", width = 15)
    @Schema(title =  "商家ID")
    private String userId;
    /**
     * 商家昵称
     */
    @Excel(name = "商家昵称", width = 15)
    @Schema(title =  "商家昵称")
    private String userName;
    /**
     * 任务ID
     */
    @Excel(name = "任务ID", width = 15)
    @Schema(title =  "任务ID")
    private String taskId;
    /**
     * 提醒类型 0:价格监控；1：跟卖监控
     */
    @Excel(name = "提醒类型 0:价格监控；1：跟卖监控", width = 15)
    @Schema(title =  "提醒类型 0:价格监控；1：跟卖监控")
    private Integer remindType;
    /**
     * 微信提醒结果 -1：失败；1：成功 ；0：未绑定
     */
    @Excel(name = "微信提醒结果 -1：失败；1：成功 ；0：未绑定", width = 15)
    @Schema(title =  "微信提醒结果 -1：失败；1：成功 ；0：未绑定")
    private Integer remindWxResult;
    /**
     * 邮箱提醒结果 -1：失败；1：成功 ；0：未绑定
     */
    @Excel(name = "邮箱提醒结果 -1：失败；1：成功 ；0：未绑定", width = 15)
    @Schema(title =  "邮箱提醒结果 -1：失败；1：成功 ；0：未绑定")
    private Integer remindEmailResult;
    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @Schema(title =  "备注")
    private String remark;
    /**
     * 创建人
     */
    @Excel(name = "创建人", width = 15)
    @Schema(title =  "创建人")
    private String createBy;
    /**
     * 创建时间
     */
    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "创建时间")
    private Date createTime;
    /**
     * 修改人
     */
    @Excel(name = "修改人", width = 15)
    @Schema(title =  "修改人")
    private String updateBy;
    /**
     * 修改时间
     */
    @Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "修改时间")
    private Date updateTime;

    /**
     * 是否微信提醒 0：否；1：是
     */
    @Excel(name = "是否微信提醒", width = 15)
    @Schema(title =  "是否微信提醒")
    private Integer isWxRemind;

    /**
     * 是否Email提醒 0：否；1：是
     */
    @Excel(name = "是否Email提醒", width = 15)
    @Schema(title =  "是否Email提醒")
    private Integer isEmailRemind;

    /**
     * 微信公众号openid
     */
    @Excel(name = "微信公众号openid", width = 15)
    @Schema(title =  "微信公众号openid")
    private String mpOpenId;
    /**
     * 邮箱
     */
    @Excel(name = "邮箱", width = 15)
    @Schema(title =  "邮箱")
    private String email;
    /**
     * 微信发送内容
     */
    @Excel(name = "微信发送内容", width = 15)
    @Schema(title =  "微信发送内容")
    private String pushWxContent;
    /**
     * 邮件发送内容
     */
    @Excel(name = "邮件发送内容", width = 15)
    @Schema(title =  "邮件发送内容")
    private String pushEmailContent;
    /**
     * 发送邮箱
     */
    @Excel(name = "发送邮箱", width = 15)
    @Schema(title =  "发送邮箱")
    private String sendEmail;

}
