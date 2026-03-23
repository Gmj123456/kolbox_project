package org.jeecg.modules.instagram.storecelebrity.entity;

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
 * @Description: youtube无邮箱数据
 * @Author: jeecg-boot
 * @Date: 2021-03-13
 * @Version: V1.0
 */
@Data
@TableName("store_celebrity_noemail")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreCelebrityNoemail {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
    /**
     * 账号
     */
    @Excel(name = "账号", width = 15)
    @Schema(title =  "账号")
    private String account;
    /**
     * 头像
     */
    @Excel(name = "头像", width = 15)
    @Schema(title =  "头像")
    private String avatarUrl;
    /**
     * 用户名
     */
    @Excel(name = "用户名", width = 15)
    @Schema(title =  "用户名")
    private String username;
    /**
     * 昵称
     */
    @Excel(name = "昵称", width = 15)
    @Schema(title =  "昵称")
    private String nickName;
    /**
     * 电子邮箱
     */
    @Excel(name = "电子邮箱", width = 15)
    @Schema(title =  "电子邮箱")
    private String email;
    /**
     * 个人简介
     */
    @Excel(name = "个人简介", width = 15)
    @Schema(title =  "个人简介")
    private String description;
    /**
     * 关注数
     */
    @Excel(name = "关注数", width = 15)
    @Schema(title =  "关注数")
    private Float followingNum;
    /**
     * 粉丝数
     */
    @Excel(name = "粉丝数 ", width = 15)
    @Schema(title =  "粉丝数 ")
    private Float followersNum;
    /**
     * 帖子数
     */
    @Excel(name = "帖子数", width = 15)
    @Schema(title =  "帖子数")
    private Integer postsNum;
    /**
     * 点赞平均数
     */
    @Excel(name = "点赞平均数", width = 15)
    @Schema(title =  "点赞平均数")
    private Integer likeAvgNum;
    /**
     * 评论平均数
     */
    @Excel(name = "评论平均数", width = 15)
    @Schema(title =  "评论平均数")
    private Integer commentAvgNum;
    /**
     * 播放平均数
     */
    @Excel(name = "播放平均数", width = 15)
    @Schema(title =  "播放平均数")
    private Float playAvgNum;
    /**
     * 标签
     */
    @Excel(name = "标签", width = 15)
    @Schema(title =  "标签")
    private String tag;
    /**
     * 所在国家
     */
    @Excel(name = "所在国家", width = 15)
    @Schema(title =  "所在国家")
    private String country;
    /**
     * 平台类型 0=instagram;1=youtube
     */
    @Excel(name = "平台类型 0=instagram;1=youtube", width = 15)
    @Schema(title =  "平台类型 0=instagram;1=youtube")
    private Integer platformType;
    /**
     * 查询关键词
     */
    @Excel(name = "查询关键词", width = 15)
    @Schema(title =  "查询关键词")
    private String keyword;
    /**
     * 带货能力标签
     */
    @Excel(name = "带货能力标签", width = 15)
    @Schema(title =  "带货能力标签")
    private String newTag;
    /**
     * 是否人像（0 待检测 1 是 -1 不是 -2识别失败）
     */
    @Excel(name = "是否人像（0 待检测 1 是 -1 不是 -2识别失败）", width = 15)
    @Schema(title =  "是否人像（0 待检测 1 是 -1 不是 -2识别失败）")
    private Integer faceStatus;
    /**
     * 删除状态(0-正常,1-已删除)
     */
    @Excel(name = "删除状态(0-正常,1-已删除)", width = 15)
    @Schema(title =  "删除状态(0-正常,1-已删除)")
    private Integer delFlag;
    /**
     * 网红最低报价
     */
    @Excel(name = "网红最低报价", width = 15)
    @Schema(title =  "网红最低报价")
    private java.math.BigDecimal kolPriceMin;
    /**
     * 网红最高报价
     */
    @Excel(name = "网红最高报价", width = 15)
    @Schema(title =  "网红最高报价")
    private java.math.BigDecimal kolPriceMax;
    /**
     * 讨厌的标签
     */
    @Excel(name = "讨厌的标签", width = 15)
    @Schema(title =  "讨厌的标签")
    private String dislikeTags;
    /**
     * 喜欢的标签
     */
    @Excel(name = "喜欢的标签", width = 15)
    @Schema(title =  "喜欢的标签")
    private String likeTags;
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
     * 标签是否确认（0：未确认；1：已确认）
     */
    @Excel(name = "标签是否确认（0：未确认；1：已确认）", width = 15)
    @Schema(title =  "标签是否确认（0：未确认；1：已确认）")
    private Integer isTagConfirm;
    /**
     * 网红星级
     */
    @Excel(name = "网红星级", width = 15)
    @Schema(title =  "网红星级")
    private Integer star;
    /**
     * 性別（0：男；1：女）
     */
    @Excel(name = "性別（0：男；1：女）", width = 15)
    @Schema(title =  "性別（0：男；1：女）")
    private Integer gender;
    /**
     * 年齡
     */
    @Excel(name = "年齡", width = 15)
    @Schema(title =  "年齡")
    private Integer age;
    /**
     * 是否验证邮箱（0：否；1：是）
     */
    @Excel(name = "是否验证邮箱（0：否；1：是）", width = 15)
    @Schema(title =  "是否验证邮箱（0：否；1：是）")
    private Integer isVerifyEmail;
    /**
     * 验证结果（0：待确认；1：已确认；-1：链接无效）
     */
    @Excel(name = "验证结果（0：待确认；1：已确认；-1：链接无效）", width = 15)
    @Schema(title =  "验证结果（0：待确认；1：已确认；-1：链接无效）")
    private Integer verifyResult;
    /**
     * 验证人ID
     */
    @Excel(name = "验证人ID", width = 15)
    @Schema(title =  "验证人ID")
    private String verifyUserId;
    /**
     * 验证人名称
     */
    @Excel(name = "验证人名称", width = 15)
    @Schema(title =  "验证人名称")
    private String verifyUserName;
    /**
     * 验证时间
     */
    @Excel(name = "验证时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "验证时间")
    private Date verifyDate;
    /**
     * 审核通过时间
     */
    @Excel(name = "审核通过时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "审核通过时间")
    private Date approvedDate;
    /**
     * 审核人员
     */
    @Excel(name = "审核人员", width = 15)
    @Schema(title =  "审核人员")
    private String approvedUserId;
    /**
     * 审核状态（0：待审核；1：审核通过；-1：未通过）
     */
    @Excel(name = "审核状态（0：待审核；1：审核通过；-1：未通过）", width = 15)
    @Schema(title =  "审核状态（0：待审核；1：审核通过；-1：未通过）")
    private Integer approvedStatus;
    /**
     * 是否验证国家（0：否；1：验证成功；-1：验证失败）
     */
    @Excel(name = "是否验证国家（0：否；1：验证成功；-1：验证失败）", width = 15)
    @Schema(title =  "是否验证国家（0：否；1：验证成功；-1：验证失败）")
    private Integer isVerifyCountry;
    /**
     * 是否用户拒绝(0:未拒绝；1:拒绝)
     */
    @Excel(name = "是否用户拒绝(0:未拒绝；1:拒绝)", width = 15)
    @Schema(title =  "是否用户拒绝(0:未拒绝；1:拒绝)")
    private Integer isUserRefuse;
    /**
     * 是否有效(0：默认；1:open；2：click)
     */
    @Excel(name = "是否有效(0：默认；1:open；2：click)", width = 15)
    @Schema(title =  "是否有效(0：默认；1:open；2：click)")
    private Integer isConfirm;
    /**
     * 发送邮件状态（0：默认；1：送达4：无效:5：软退信，18：请求中）
     */
    @Excel(name = "发送邮件状态（0：默认；1：送达4：无效:5：软退信，18：请求中）", width = 15)
    @Schema(title =  "发送邮件状态（0：默认；1：送达4：无效:5：软退信，18：请求中）")
    private Integer isSendStatus;
    /**
     * 发送失败原因
     */
    @Excel(name = "发送失败原因", width = 15)
    @Schema(title =  "发送失败原因")
    private String sendFailReason;
    /**
     * 当日发送数量
     */
    @Excel(name = "当日发送数量", width = 15)
    @Schema(title =  "当日发送数量")
    private Integer emailsDay;
    /**
     * 总发送数量
     */
    @Excel(name = "总发送数量", width = 15)
    @Schema(title =  "总发送数量")
    private Integer emailsTotal;
    /**
     * 发送时间
     */
    @Excel(name = "发送时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "发送时间")
    private Date emailsSendDate;
    /**
     * 任务主键
     */
    @Excel(name = "任务主键", width = 15)
    @Schema(title =  "任务主键")
    private String taskId;
    /**
     * 是否更新ins官方头像（0：未更新；1：已更新；-1：更新失败）
     */
    @Excel(name = "是否更新ins官方头像（0：未更新；1：已更新；-1：更新失败）", width = 15)
    @Schema(title =  "是否更新ins官方头像（0：未更新；1：已更新；-1：更新失败）")
    private Integer isUpdataAvatar;
    /**
     * 是否desc分析tag （0：未分析；1：已分析成功；-1：分析失败）
     */
    @Excel(name = "是否desc分析tag （0：未分析；1：已分析成功；-1：分析失败）", width = 15)
    @Schema(title =  "是否desc分析tag （0：未分析；1：已分析成功；-1：分析失败）")
    private Integer isDescTags;
    /**
     * 手机号
     */
    @Excel(name = "手机号", width = 15)
    @Schema(title =  "手机号")
    private String phone;
    /**
     * 个人站点
     */
    @Excel(name = "个人站点", width = 15)
    @Schema(title =  "个人站点")
    private String website;
    /**
     * 网红分类
     */
    @Excel(name = "网红分类", width = 15)
    @Schema(title =  "网红分类")
    private String category;
    /**
     * 是否导入数据 0:否；1：是
     */
    @Excel(name = "是否导入数据 0:否；1：是", width = 15)
    @Schema(title =  "是否导入数据 0:否；1：是")
    private Integer isImport;
}
