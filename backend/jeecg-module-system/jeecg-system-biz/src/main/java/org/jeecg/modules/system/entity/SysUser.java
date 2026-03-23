package org.jeecg.modules.system.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 登录账号
     */
    @Excel(name = "登录账号", width = 15)
    private String username;

    /**
     * 真实姓名
     */
    @Excel(name = "真实姓名", width = 15)
    private String realname;

    /**
     * 密码
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /**
     * md5密码盐
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String salt;

    /**
     * 头像
     */
    @Excel(name = "头像", width = 15,type = 2)
    private String avatar;

    /**
     * 生日
     */
    @Excel(name = "生日", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    /**
     * 性别（1：男 2：女）
     */
    @Excel(name = "性别", width = 15,dicCode="sex1")
    @Dict(dicCode = "sex1")
    private Integer sex;

    /**
     * 电子邮件
     */
    @Excel(name = "电子邮件", width = 15)
    private String email;

    /**
     * 电话
     */
    @Excel(name = "电话", width = 15)
    private String phone;

    /**
     * 登录选择部门编码
     */
    private String orgCode;
    /**
     * 登录选择租户ID
     */
    private Integer loginTenantId;

    /**部门名称*/
    private transient String orgCodeTxt;

    /**
     * 状态(1：正常  2：冻结 ）
     */
    @Excel(name = "状态", width = 15,dicCode="user_status")
    @Dict(dicCode = "user_status")
    private Integer status;

    /**
     * 删除状态（0，正常，1已删除）
     */
    @Excel(name = "删除状态", width = 15,dicCode="del_flag")
    @TableLogic
    private Integer delFlag;

    /**
     * 工号，唯一键
     */
    @Excel(name = "工号", width = 15)
    private String workNo;

    /**
     * 职务，关联职务表
     */
    @Excel(name = "职务", width = 15)
    @Dict(dictTable ="sys_position",dicText = "name",dicCode = "id")
    @TableField(exist = false)
    private String post;

    /**
     * 座机号
     */
    @Excel(name = "座机号", width = 15)
    private String telephone;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 同步工作流引擎1同步0不同步
     */
    private Integer activitiSync;

    /**
     * 身份（0 普通成员 1 上级）
     */
    @Excel(name="（1普通成员 2上级）",width = 15)
    private Integer userIdentity;

    /**
     * 负责部门
     */
    @Excel(name="负责部门",width = 15,dictTable ="sys_depart",dicText = "depart_name",dicCode = "id")
    @Dict(dictTable ="sys_depart",dicText = "depart_name",dicCode = "id")
    private String departIds;

    /**
     * 多租户ids临时用，不持久化数据库(数据库字段不存在)
     */
    @TableField(exist = false)
    private String relTenantIds;

    /**设备id uniapp推送用*/
    private String clientId;

    /**
     * 登录首页地址
     */
    @TableField(exist = false)
    private String homePath;

    /**
     * 职位名称
     */
    @TableField(exist = false)
    private String postText;

    /**
     * 流程状态
     */
    private String bpmStatus;

    /**
     * 是否已经绑定第三方
     */
    @TableField(exist = false)
    private boolean izBindThird;

    /**
     * 个性签名
     */
    private String sign;

    /**
     * 是否开启个性签名
     */
    private Integer signEnable;

    /**
     * 主岗位
     */
    @Excel(name="主岗位",width = 15,dictTable ="sys_depart",dicText = "depart_name",dicCode = "id")
    @Dict(dictTable ="sys_depart",dicText = "depart_name",dicCode = "id")
    private String mainDepPostId;

    /**
     * 兼职岗位
     */
    @Excel(name="兼职岗位",width = 15,dictTable ="sys_depart",dicText = "depart_name",dicCode = "id")
    @Dict(dictTable ="sys_depart",dicText = "depart_name",dicCode = "id")
    @TableField(exist = false)
    private String otherDepPostId;

    /**
     * 职务(字典)
     */
    @Excel(name = "职务", width = 15, dicCode = "position_type")
    @Dict(dicCode = "position_type")
    private String positionType;

    /**
     * 账户余额
     */
    private BigDecimal currentAmount;

    /**
     * 上级邀请码
     */
    @Excel(name = "上级邀请码", width = 15)
    private String parentInvitationCode;
    /**
     * 邀请码
     */
    @Excel(name = "邀请码", width = 15)
    private String invitationCode;

    /**
     * 等级id
     */
    private String gradeId;

    /**
     * 等级名称
     */
    private String gradeName;


    /**
     * 邮件发送额度
     */
    private Integer emailPushCount;

    /**
     * 邮箱账号额度
     */
    private Integer emailAccountCount;

    /**
     * 用户类型
     */
    private Integer userType;

    /**
     * 账号有效期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expirationDate;

    /**
     * 备注
     */
    private String remark;


    /**
     * 申请通过时间
     */
    @Excel(name = "申请通过时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date approvedDate;
    /**
     * 审核人员
     */
    @Excel(name = "审核人员", width = 15)
    private String approvedUserId;
    /**
     * 审核状态（0：待审核；1：审核通过；-1：未通过）
     */
    @Excel(name = "审核状态（0：待审核；1：审核通过；-1：未通过）", width = 15)
    private Integer approvedStatus;

    /**
     * 当日邮件个数
     */
    private Integer emailsDay;
    /**
     * 驳回原因
     */
    private String approvedFailReason;

    /**
     * 商家顾问ID
     */
    private String sellerCounselorId;
    /**
     * 商家顾问名称
     */
    private String sellerCounselorName;

    /**
     * 二维码
     */
    private String qrCode;

    /**
     * 商家顾问二维码
     */
    private String sellerCounselorQr;

    /**
     * 商家顾问自定义昵称
     */
    private String nickname;

    /**
     * 提现绑定账户类型 0=支付宝，1=微信，2=Paypal
     */
    private Integer cashAccountType;

    /**
     * 提现绑定账户
     */
    private String cashAccount;

    /**
     * 提现收款人姓名
     */
    private String cashPersonName;

    /**
     * 审核人员
     */
    @Excel(name = "审核人员名称", width = 15)
    private String approvedUserName;
    /**
     * 是否开启群发邮件 0：否；1：是（默认1）
     */
    @Excel(name = "是否开启群发邮件", width = 15)
    private String isEnableEmail;
    /**
     * 微信openID
     */
    @Excel(name = "微信openID", width = 15)
    private String openId;
    /**
     * 微信平台唯一id
     */
    @Excel(name = "微信平台唯一id", width = 15)
    private String unionId;
    /**
     * 微信公众号openid
     */
    @Excel(name = "微信公众号openid", width = 15)
    private String mpOpenId;
    /**
     * 微信公众号扫码URL
     */
    @Excel(name = "微信公众号扫码URL", width = 15)
    private String wxQrUrl;

    /**
     * 是否邮件推送监控提醒 0：否 ；1：是（默认）
     */
    @Excel(name = "是否邮件推送监控提醒 0：否 ；1：是（默认）", width = 15)
    private Integer isEmailPush;

    /**
     * 是否微信推送监控提醒 0：否 ；1：是（默认）
     */
    @Excel(name = "是否微信推送监控提醒 0：否 ；1：是（默认）", width = 15)
    private Integer isWxPush;

    /**
     * 最后一次登录时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastLoginDate;
    /**
     * 商务人员ID
     */
    @Excel(name = "商务人员ID", width = 15)
    private String counselorUserId;
    /**
     * 商务人员名称
     */
    @Excel(name = "商务人员名称", width = 15)
    private String counselorUserName;

    /**
     * 是否自己员工 0：否 ，1：是
     */
    @Excel(name = "是否邮件推送监控提醒 0：否 ；1：是（默认）", width = 15)
    private Integer isEmployee;

    /**
     * 父用户id
     */
    private String parentUserId;
    private String wxAccount;
    /**
     * 是否离职 是否离职 0：否；1：是
     */
    private Integer isLeave;

    /**
     * 短名称
     */
    private String shortName;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 飞书账号
     */
    private String feishuAccount;
    /**
     * 获客方式
     */
    private String acquisitionChannel;
    private String feishuOpenId;

}
