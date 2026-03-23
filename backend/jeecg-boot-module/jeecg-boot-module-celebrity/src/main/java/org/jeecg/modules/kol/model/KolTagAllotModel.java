package org.jeecg.modules.kol.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigInteger;
import java.util.Date;

/**
 * @Description: 网红标签分配明细视图模型
 * @Author: xyc
 * @Date: 2024-12-24 09:08:42
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class KolTagAllotModel extends UserTagAllotModel {

    /**
     * 分配时间
     */
    @Schema(title =  "分配时间")
    private Date allotTime;

    /**
     * 分配日志明细ID
     */
    @Schema(title =  "分配日志明细ID")
    private String allotLogDetailId;

    /**
     * 个人签名
     */
    @Schema(title =  "个人签名")
    private String signature;

    /**
     * 网红粉丝数
     */
    @Schema(title =  "网红粉丝数")
    private BigInteger authorFollowerCount;

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
     * 电子邮箱
     */
    @Schema(title =  "电子邮箱")
    private String email;

    /**
     * 头像链接
     */
    @Schema(title =  "头像链接")
    private String authorAvatarUrl;
    /**
     * 平台类型 0=IG 1=YT 2=TK
     */
    @Schema(title =  "平台类型")
    private Integer platformType;
    /**
     * 标签id
     */
    @Schema(title =  "标签id")
    private String tagsId;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 指定日期时间格式
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date contractTime;

    private String productName;
}
