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
 * @Description: store_celebrity 网红档案(试用)
 * @Author: zhoushen
 * @Date:   2020-11-06
 * @Version: V1.0
 */
@Data
@TableName("store_celebrity_trial")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreCelebrityTrial {
    
	/**主键*/
	@TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
	private String id;
	/**账号*/
	@Excel(name = "账号", width = 15)
    @Schema(title =  "账号")
	private String account;
	/**头像*/
	@Excel(name = "头像", width = 15)
    @Schema(title =  "头像")
	private String avatarUrl;
	/**用户名*/
	@Excel(name = "用户名", width = 15)
    @Schema(title =  "用户名")
	private String username;
	/**昵称*/
	@Excel(name = "昵称", width = 15)
    @Schema(title =  "昵称")
	private String nickName;
	/**电子邮箱*/
	@Excel(name = "电子邮箱", width = 15)
    @Schema(title =  "电子邮箱")
	private String email;
	/**个人简介*/
	@Excel(name = "个人简介", width = 15)
    @Schema(title =  "个人简介")
	private String description;
	/**关注数*/
	@Excel(name = "关注数", width = 15)
    @Schema(title =  "关注数")
	private Integer followingNum;
	/**粉丝数 */
	@Excel(name = "粉丝数 ", width = 15)
    @Schema(title =  "粉丝数 ")
	private Integer followersNum;
	/**帖子数*/
	@Excel(name = "帖子数", width = 15)
    @Schema(title =  "帖子数")
	private Integer postsNum;
	/**点赞平均数*/
	@Excel(name = "点赞平均数", width = 15)
    @Schema(title =  "点赞平均数")
	private Integer likeAvgNum;
	/**评论平均数*/
	@Excel(name = "评论平均数", width = 15)
    @Schema(title =  "评论平均数")
	private Integer commentAvgNum;
	/**播放平均数*/
	@Excel(name = "播放平均数", width = 15)
    @Schema(title =  "播放平均数")
	private Integer playAvgNum;
	/**标签*/
	@Excel(name = "标签", width = 15)
    @Schema(title =  "标签")
	private String tag;
	/**所在国家*/
	@Excel(name = "所在国家", width = 15)
    @Schema(title =  "所在国家")
	private String country;
	/**平台类型 0=instagram;1=youtube*/
	@Excel(name = "平台类型 0=instagram;1=youtube", width = 15)
    @Schema(title =  "平台类型 0=instagram;1=youtube")
	private Integer platformType;
	/**查询关键词*/
	@Excel(name = "查询关键词", width = 15)
    @Schema(title =  "查询关键词")
	private String keyword;
	/**带货能力标签*/
	@Excel(name = "带货能力标签", width = 15)
    @Schema(title =  "带货能力标签")
	private String newTag;
	/**是否人像（0 待检测 1 是 -1 不是 -2识别失败）*/
	@Excel(name = "是否人像（0 待检测 1 是 -1 不是 -2识别失败）", width = 15)
    @Schema(title =  "是否人像（0 待检测 1 是 -1 不是 -2识别失败）")
	private Integer faceStatus;
	/**删除状态(0-正常,1-已删除)*/
	@Excel(name = "删除状态(0-正常,1-已删除)", width = 15)
    @Schema(title =  "删除状态(0-正常,1-已删除)")
	private Integer delFlag;
	/**网红最低报价*/
	@Excel(name = "网红最低报价", width = 15)
    @Schema(title =  "网红最低报价")
	private java.math.BigDecimal kolPriceMin;
	/**网红最高报价*/
	@Excel(name = "网红最高报价", width = 15)
    @Schema(title =  "网红最高报价")
	private java.math.BigDecimal kolPriceMax;
	/**讨厌的标签*/
	@Excel(name = "讨厌的标签", width = 15)
    @Schema(title =  "讨厌的标签")
	private String dislikeTags;
	/**喜欢的标签*/
	@Excel(name = "喜欢的标签", width = 15)
    @Schema(title =  "喜欢的标签")
	private String likeTags;
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
	/**标签是否确认（0：未确认；1：已确认）*/
	@Excel(name = "标签是否确认（0：未确认；1：已确认）", width = 15)
    @Schema(title =  "标签是否确认（0：未确认；1：已确认）")
	private Integer isTagConfirm;
	/**网红星级*/
	@Excel(name = "网红星级", width = 15)
    @Schema(title =  "网红星级")
	private Integer star;
	/**性別（0：男；1：女）*/
	@Excel(name = "性別（0：男；1：女）", width = 15)
    @Schema(title =  "性別（0：男；1：女）")
	private Integer gender;
	/**年齡*/
	@Excel(name = "年齡", width = 15)
    @Schema(title =  "年齡")
	private Integer age;
	/**是否验证邮箱（0：否；1：是）*/
	@Excel(name = "是否验证邮箱（0：否；1：是）", width = 15)
    @Schema(title =  "是否验证邮箱（0：否；1：是）")
	private Integer isVerifyEmail;
}
