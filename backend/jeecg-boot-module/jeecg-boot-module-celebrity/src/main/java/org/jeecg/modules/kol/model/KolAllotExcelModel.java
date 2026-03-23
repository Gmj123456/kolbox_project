package org.jeecg.modules.kol.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * @Author: nqr
 * @Date: 2025/5/6 10:32
 * @Description:
 **/
@Data
public class KolAllotExcelModel {
    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;

    /**
     * 网红账号的唯一标识
     */
    @Excel(name = "网红账号", width = 25)
    @Schema(title =  "唯一id")
    private String account;
    private String authorId;
    /**
     * 昵称
     */
    @Schema(title =  "昵称")
    private String nickname;
    /**
     * 国家
     */
    @Schema(title =  "国家")
    private String country;
    /**
     * 用户粉丝数
     */
    @Excel(name = "粉丝数", width = 15)
    private String authorFollowerCountStr;

    @Schema(title =  "用户粉丝数")
    private BigInteger authorFollowerCount;
    /**
     * 有效视频
     */
    @Schema(title =  "有效视频")
    private String videoCountStr;
    /**
     * 样本视频总个数
     */
    @Schema(title =  "样本视频总个数")
    private Integer videoSampleCount;
    /**
     * 符合要求的视频播放量的个数
     */
    @Schema(title =  "符合要求的视频播放量的个数")
    private Integer videoStandardCount;
    /**
     * 邮箱
     */
    @Excel(name = "邮箱", width = 50)
    @Schema(title =  "邮箱")
    private String email;
    /**
     * 简介
     */
    @Schema(title =  "简介")
    private String signature;
    /**
     * 签名外链
     */
    @Excel(name = "达人主页链接", width = 80)
    @Schema(title =  "签名外链")
    private String bioLink;
    /**
     * 用户名
     */
    @Schema(title =  "用户名")
    private String uniqueId;
    /**
     * 用户头像地址
     */
    @Schema(title =  "用户头像地址")
    private String authorAvatarUrl;
    /**
     * 最新视频创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "最新视频创建时间")
    private Date videoCreateTimeLast;
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
     * 手机号
     */
    @Schema(title =  "手机号")
    private String phone;
    /**
     * 私有网红Id
     */
    @Schema(title =  "私有网红Id")
    private String celebrityPrivateId;
    /**
     * 网红标签关联视图列表
     */
    @Schema(title =  "标签列表")
    private List<KolAllotTagModel> tagList;

    /**
     * 开发历史
     */
    @Excel(name = "开发历史", width = 100)
    @Schema(title =  "开发历史")
    private String contactHistory;

    /**
     * 平台类型 0=IG 1=YT 2=TK
     */
    @Schema(title =  "平台类型")
    private Integer platformType;
    /**
     * 分配日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(title =  "分配日期")
    private Date allotTime;

    private Integer contactCount;
}
