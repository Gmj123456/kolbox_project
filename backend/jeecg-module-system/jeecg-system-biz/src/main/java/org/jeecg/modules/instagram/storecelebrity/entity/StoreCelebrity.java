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
 * @Description: 网红档案
 * @Author: nqr
 * @Date: 2020-04-20
 * @Version: V1.0
 */
@Data
@TableName("store_celebrity")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreCelebrity {

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
     * 用户id
     */
    @Excel(name = "用户名", width = 15)
    @Schema(title =  "用户名")
    private String userId;
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
    @Schema(title =  "个人简介")
    private String description;
    /**
     * 关注数
     */
    @Schema(title =  "关注数")
    private Long followingNum;
    /**
     * 粉丝数
     */
    @Schema(title =  "粉丝数 ")
    private Long followersNum;
    /**
     * 帖子数
     */
    @Schema(title =  "帖子数")
    private Integer postsNum;
    /**
     * 点赞平均数
     */
    @Schema(title =  "点赞平均数")
    private Integer likeAvgNum;
    /**
     * 评论平均数
     */
    @Schema(title =  "评论平均数")
    private Integer commentAvgNum;
    /**
     * 播放平均数
     */
    @Schema(title =  "播放平均数")
    private Long playAvgNum;
    /**
     * 标签
     */
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
    @Excel(name = "平台类型", width = 15)
    @Schema(title =  "平台类型 0=instagram;1=youtube")
    private Integer platformType;
    /**
     * 查询关键词
     */
    @Schema(title =  "查询关键词")
    private String keyword;
    /**
     * 带货能力标签
     */
    private String newTag;
    /**
     * 是否人像
     */
    @Schema(title =  "是否人像")
    private Integer faceStatus;

    /**
     * 删除状态
     */
    private Integer delFlag;


    /**
     * 网红最低报价
     */
    @Schema(title =  "网红最低报价")
    private String kolPriceMin;
    /**
     * 网红最高报价
     */
    @Schema(title =  "网红最高报价")
    private String kolPriceMax;
    /**
     * 讨厌的标签
     */
    @Schema(title =  "讨厌的标签")
    private String dislikeTags;
    /**
     * 喜欢的标签
     */
    @Schema(title =  "喜欢的标签")
    private String likeTags;
    /**
     * 备注
     */
    @Schema(title =  "备注")
    private String remark;
    /**
     * 创建人
     */
    @Schema(title =  "创建人")
    private String createBy;
    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "创建时间")
    private Date createTime;
    /**
     * 修改人
     */
    @Schema(title =  "修改人")
    private String updateBy;
    /**
     * 修改时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "修改时间")
    private Date updateTime;


    /**
     * 标签是否确认（0：未确认；1：已确认）
     */
    @Schema(title =  "标签是否确认（0：未确认；1：已确认）")
    private Integer isTagConfirm;

    /**
     * 网红星级
     */
    @Schema(title =  "网红星级")
    private Integer star;

    /**
     * 性別（0：男；1：女）
     */
    @Schema(title =  "性別")
    private Integer gender;

    /**
     * 年齡
     */
    @Schema(title =  "年齡")
    private Integer age;
    /**
     * 验证结果
     */
    @Schema(title =  "验证结果（0：待确认；1：已确认；-1：链接无效）")
    private Integer verifyResult;
    /**
     * 验证人id
     */
    @Schema(title =  "验证人id")
    private String verifyUserId;
    /**
     * 验证人名称
     */
    @Schema(title =  "验证人名称")
    private String verifyUserName;
    /**
     * 验证时间
     */
    @Schema(title =  "验证时间")
    private Date verifyDate;
    /**
     * 审核通过时间
     */
    @Schema(title =  "审核通过时间")
    private Date approvedDate;
    /**
     * 审核人员
     */
    @Schema(title =  "审核人员")
    private String approvedUserId;
    /**
     * 审核状态
     */
    @Schema(title =  "审核状态（0：待审核；1：审核通过；-1：未通过）")
    private Integer approvedStatus;





    /**
     * 是否验证国家（0：否；1：验证成功；-1：验证失败）
     */
    @Schema(title =  "是否验证国家（0：否；1：验证成功；-1：验证失败）")
    private Integer isVerifyCountry;
    /**
     * 是否用户拒绝(0:未拒绝；1:拒绝)
     */
    @Schema(title =  "是否用户拒绝(0:未拒绝；1:拒绝)")
    private Integer isUserRefuse;
    /**
     * 是否有效(0：默认；1:open；2：click)
     */
    @Schema(title =  "是否有效(0：默认；1:open；2：click)")
    private Integer isConfirm;
    /**
     * 发送邮件状态（0：默认；1：送达4：无效:5：软退信，18：请求中）
     */
    @Schema(title =  "发送邮件状态（0：默认；1：送达4：无效:5：软退信，18：请求中）")
    private Integer isSendStatus;
    /**
     * 发送失败原因
     */
    @Schema(title =  "发送失败原因")
    private String sendFailReason;

    /**
     * 当日发送数量
     */
    @Schema(title =  "当日发送数量")
    private Integer emailsDay;
    /**
     * 总发送数量
     */
    @Schema(title =  "总发送数量")
    private Integer emailsTotal;
    /**
     * 发送时间
     */
    @Schema(title =  "发送时间")
    private Date emailsSendDate;
    /**
     * 任务主键
     */
    @Schema(title =  "任务主键")
    private String taskId;
    /**
     * 是否更新ins官方头像（0：未更新；1：已更新；-1：更新失败）
     */
    @Schema(title =  "是否更新ins官方头像（0：未更新；1：已更新；-1：更新失败）")
    private Integer isUpdataAvatar;
    /**
     * 是否desc分析tag （0：未分析；1：已分析成功；-1：分析失败）
     */
    @Schema(title =  "是否desc分析tag （0：未分析；1：已分析成功；-1：分析失败）")
    private Integer isDescTags;



    /**
     * 手机号
     */
    @Schema(title =  "手机号")
    private String phone;
    /**
     * 个人站点
     */
    @Schema(title =  "个人站点")
    private String website;
    /**
     * 网红分类
     */
    @Schema(title =  "网红分类")
    private String category;
    /**
     * 是否导入数据 0:否；1：是
     */
    @Schema(title =  "是否导入数据 0:否；1：是")
    private Integer isImport;

}
