package org.jeecg.modules.youtube.entity;

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

import java.math.BigInteger;

/**
 * @Description: YT网红
 * @Author: nqr
 * @Date: 2023-11-22
 * @Version: V1.0
 */
@Data
@TableName("youtube_celebrity")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class YoutubeCelebrity {

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
    private String nickname;
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
    private Long followingNum;
    /**
     * 粉丝数
     */
    @Excel(name = "粉丝数 ", width = 15)
    @Schema(title =  "粉丝数 ")
    private Long followersNum;
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
    private BigInteger playAvgNum;
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
     * 喜欢的标签
     */
    @Excel(name = "喜欢的标签", width = 15)
    @Schema(title =  "喜欢的标签")
    private String likeTags;
    /**
     * 删除状态（0=未删除,1=已删除）
     */
    @Excel(name = "删除状态（0=未删除,1=已删除）", width = 15)
    @Schema(title =  "删除状态（0=未删除,1=已删除）")
    private Integer isDelete;
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
    private java.util.Date createTime;
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
    private java.util.Date updateTime;
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
     * 样本视频总个数
     */
    @Excel(name = "样本视频总个数", width = 15)
    @Schema(title =  "样本视频总个数")
    private Integer videoSampleCount;
    /**
     * 符合要求的视频播放量的个数
     */
    @Excel(name = "符合要求的视频播放量的个数", width = 15)
    @Schema(title =  "符合要求的视频播放量的个数")
    private Integer videoStandardCount;

}
