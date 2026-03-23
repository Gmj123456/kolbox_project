package org.jeecg.modules.feishu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * @Description: 上线视频指标表
 * @Author: nqr
 * @Date: 2025-10-20
 * @Version: V1.0
 */
@Data
@TableName("kol_video_metrics")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class KolVideoMetrics {

    /**
     * 主键
     */
    @Excel(name = "系统ID", width = 15)
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
    private String videoId;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date onlineTime;
    /**
     * 视频发布时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date videoCreateTime;

    @Excel(name = "日期", width = 15)
    @Schema(title =  "日期")
    @TableField(exist = false)
    private String onlineTimeStr;
    /**
     * 推广单号
     */
    @Excel(name = "推广单号", width = 15)
    @Schema(title =  "推广单号")
    private String extensionCode;
    /**
     * 商务人员ID
     */
    @Excel(name = "商务人员ID", width = 15)
    @Schema(title =  "商务人员ID")
    private String businessUserId;
    /**
     * 商务人员名称
     */
    @Excel(name = "商务顾问", width = 15)
    @Schema(title =  "商务顾问")
    private String businessUserName;
    /**
     * 网红顾问_ID
     */
    @Excel(name = "网红顾问_ID", width = 15)
    @Schema(title =  "网红顾问_ID")
    private String celebrityCounselorId;
    /**
     * 网红顾问名称
     */
    @Excel(name = "网红顾问", width = 15)
    @Schema(title =  "网红顾问")
    private String celebrityCounselorName;
    /**
     * 品牌名称
     */
    @Excel(name = "品牌名", width = 15)
    @Schema(title =  "品牌名")
    private String brandName;
    /**
     * 产品名称
     */
    @Excel(name = "产品名称中英文", width = 15)
    @Schema(title =  "产品名称中英文")
    private String productName;
    /**
     * 网红名称
     */
    @Excel(name = "选中网红", width = 15)
    @Schema(title =  "选中网红")
    private String kolAccount;
    /**
     * 网红平台类型（0:IG;1:YT;2:TK）
     */
    @Excel(name = "平台", width = 15)
    @Schema(title =  "平台")
    @TableField(exist = false)
    private String platformTypeStr;
    private Integer platformType;
    /**
     * 类目名称
     */
    @Excel(name = "类目", width = 15)
    @Schema(title =  "类目")
    private String categoryName;
    /**
     * 网红报价
     */
    @Excel(name = "网红报价", width = 15)
    @Schema(title =  "网红报价")
    private java.math.BigDecimal contractAmount;
    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @Schema(title =  "备注")
    private String remark;
    /**
     * 视频链接
     */
    @Excel(name = "上线链接", width = 15)
    @Schema(title =  "上线链接")
    private String videoUrl;
    /**
     * 播放数
     */
    @Excel(name = "播放数", width = 15)
    @Schema(title =  "播放数")
    private Integer playCount;
    /**
     * TK:点赞数（YT/IG:喜欢视频用户数）
     */
    @Excel(name = "点赞数", width = 15)
    @Schema(title =  "点赞数")
    private Integer diggCount;
    /**
     * 收藏数
     */
    @Excel(name = "收藏数", width = 15)
    @Schema(title =  "收藏数")
    private Integer collectCount;
    /**
     * 转发数
     */
    @Excel(name = "转发数", width = 15)
    @Schema(title =  "转发数")
    private Integer shareCount;
    /**
     * 评论数
     */
    @Excel(name = "评论数", width = 15)
    @Schema(title =  "评论数")
    private Integer commentCount;
    /**
     * 最后执行时间
     */
    @Excel(name = "最后执行时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "最后执行时间")
    private Date lastExecutionTime;
    /**
     * 失败原因
     */
    @Excel(name = "异常原因", width = 15)
    private String errorMsg;
    private Integer errorCode;
    /**
     * 是否完成
     * (0=未开始,
     * -1=执行失败
     * 1=执行成功 )
     */
    @Excel(name = "是否完成(0=未开始,  -1=执行失败  1=执行成功)", width = 15)
    @Schema(title =  "是否完成 (0=未开始,  -1=执行失败  1=执行成功)")
    private Integer isCompleted;
    /**
     * @description: 飞书同步状态（0=待同步,1=已读取,2=已同步,3=同步异常）
     * @author: nqr
     * @date: 2025/10/20 9:45
     **/
    private Integer syncStatus;
    /**
     * 删除状态（0=未删除,1=已删除）
     */
    @Excel(name = "删除状态（0=未删除,1=已删除）", width = 15)
    @Schema(title =  "删除状态（0=未删除,1=已删除）")
    private Integer isDelete;
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
     * 同步状态 已读取、已同步、同步异常
     */
    @Excel(name = "同步状态", width = 15)
    @TableField(exist = false)
    private String isUpdate;

    @Excel(name = "行号", width = 15)
    @TableField(exist = false)
    private Integer rowNum;
}
