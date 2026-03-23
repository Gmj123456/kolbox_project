package org.jeecg.modules.kol.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.modules.kol.entity.KolShields;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * @Description: 网红基础数据模型
 * @Author: xyc
 * @Date: 2024-12-30 17:10:42
 * @Version: V1.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class KolBaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;

    /**
     * 网红账号的唯一标识
     */
    @Schema(title =  "唯一id")
    private String account;
    /**
     * 用户名
     */
    @Schema(title =  "用户名")
    private String uniqueId;

    /**
     * 昵称
     */
    @Excel(name = "昵称", width = 15)
    @Schema(title =  "昵称")
    private String nickname;
    /**
     * 国家
     */
    @Excel(name = "国家", width = 15)
    @Schema(title =  "国家")
    private String country;
    /**
     * 签名
     */
    @Excel(name = "签名", width = 15)
    @Schema(title =  "签名")
    private String signature;
    /**
     * 用户头像地址
     */
    @Excel(name = "用户头像地址", width = 15)
    @Schema(title =  "用户头像地址")
    private String authorAvatarUrl;
    /**
     * 用户粉丝数
     */
    @Excel(name = "用户粉丝数", width = 15)
    @Schema(title =  "用户粉丝数")
    private BigInteger authorFollowerCount;
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
    /**
     * 最新视频创建时间
     */
    @Excel(name = "最新视频创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "最新视频创建时间")
    private Date videoCreateTimeLast;
    /**
     * 邮箱
     */
    @Excel(name = "邮箱", width = 15)
    @Schema(title =  "邮箱")
    private String email;
    /**
     * 签名链接
     */
    @Excel(name = "签名链接", width = 15)
    @Schema(title =  "签名链接")
    private String bioLink;
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
//    /**
//     * 删除状态（0=未删除,1=已删除）
//     */
//    @Excel(name = "删除状态（0=未删除,1=已删除）", width = 15)
//    @Schema(title =  "删除状态（0=未删除,1=已删除）")
//    private Integer isDelete;

    /**
     * 手机号
     */
    @Excel(name = "手机号", width = 15)
    @Schema(title =  "手机号")
    private String phone;
//    /**
//     * 性別（0=男；1=女）
//     */
//    @Excel(name = "性別（0=男；1=女）", width = 15)
//    @Schema(title =  "性別（0=男；1=女）")
//    private Integer gender;

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
    @Excel(name = "分配日期", width = 20, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(title =  "分配日期")
    private Date allotTime;

    private String authorId;

    private List<TypeData> dataList;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 指定日期时间格式
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date contractTime;

    private Integer contactCount;

    private String linktree;
    private String videoData;
    private String productId;
    private String productName;
    private String brandId;
    private String brandName;
    /**
     * 网红屏蔽产品列表
     */
    @Schema(title =  "网红屏蔽产品列表")
    private List<KolShields> kolShields;

    /**
     * 播放量中位数
     */
    @Schema(title =  "播放量中位数")
    private Integer medianViews;
}
