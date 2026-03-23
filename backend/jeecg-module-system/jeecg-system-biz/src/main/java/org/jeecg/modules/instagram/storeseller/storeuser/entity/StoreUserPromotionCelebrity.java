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
 * @Description: 商家产品促销网红
 * @Author: jeecg-boot
 * @Date: 2021-05-21
 * @Version: V1.0
 */
@Data
@TableName("store_user_promotion_celebrity")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreUserPromotionCelebrity {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
    /**
     * 推广ID
     */
    @Excel(name = "推广ID", width = 15)
    @Schema(title =  "推广ID")
    private String promId;
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
     * 喜欢的标签
     */
    @Excel(name = "喜欢的标签", width = 15)
    @Schema(title =  "喜欢的标签")
    private String likeTags;
    /**
     * 视频标签
     */
    @Excel(name = "视频标签", width = 15)
    @Schema(title =  "视频标签")
    private String videoTags;
    /**
     * 平台 0=instagram;1=youtube
     */
    @Excel(name = "平台 0=instagram;1=youtube;2=tiktok", width = 15)
    @Schema(title =  "平台 0=instagram;1=youtube;2=tiktok")
    private String platform;
    /**
     * 粉丝数
     */
    @Excel(name = "粉丝数 ", width = 15)
    @Schema(title =  "粉丝数 ")
    private String followersNum;
    /**
     * 点赞平均数
     */
    @Excel(name = "点赞平均数", width = 15)
    @Schema(title =  "点赞平均数")
    private String likeAvgNum;
    /**
     * 评论平均数
     */
    @Excel(name = "评论平均数", width = 15)
    @Schema(title =  "评论平均数")
    private String commentAvgNum;
    /**
     * 平均互动率
     */
    @Excel(name = "平均互动率", width = 15)
    @Schema(title =  "平均互动率")
    private String interactAvgNum;
    /**
     * 平均观看量
     */
    @Excel(name = "平均观看量", width = 15)
    @Schema(title =  "平均观看量")
    private String playAvgNum;
    /**
     * 总视频数
     */
    @Excel(name = "总视频数", width = 15)
    @Schema(title =  "总视频数")
    private String videoCount;
    /**
     * 总获赞数
     */
    @Excel(name = "总获赞数", width = 15)
    @Schema(title =  "总获赞数")
    private String likeCount;
    /**
     * 推广费用
     */
    @Excel(name = "推广费用", width = 15)
    @Schema(title =  "推广费用")
    private String promPrice;
    /**
     * 网红状态 0：未选中；1：已选中
     */
    @Excel(name = "网红状态 0：未选中；1：已选中", width = 15)
    @Schema(title =  "网红状态 0：未选中；1：已选中")
    private Integer status;
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
}
