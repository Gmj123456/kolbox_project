package org.jeecg.modules.kol.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 分配日志表
 * @Author: dongruyang
 * @Date: 2025-09-17
 * @Version: V1.0
 */
@Data
@TableName("kol_allot_log")
public class KolAllotLog {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
    /**
     * 平台类型（0:IG;1:YT;2:TK）
     */
    @Excel(name = "平台类型（0:IG;1:YT;2:TK）", width = 15)
    @Schema(title =  "平台类型（0:IG;1:YT;2:TK）")
    private Integer platformType;
    /**
     * 分配内容
     */
    @Excel(name = "分配内容", width = 15)
    @Schema(title =  "分配内容")
    private String allotContent;
    /**
     * 分配顾问列表
     */
    @Excel(name = "分配顾问列表", width = 15)
    @Schema(title =  "分配顾问列表")
    private String allotCounselorNames;
    /**
     * 分配开始时间
     */
    @Excel(name = "分配开始时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "分配开始时间")
    private Date allotStartTime;
    /**
     * 分配结束时间
     */
    @Excel(name = "分配结束时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "分配结束时间")
    private Date allotEndTime;
    /**
     * 分配类型 1：标签2：产品；3：达人类型
     */
    @Excel(name = "分配类型 1：标签2：产品；3：达人类型", width = 15)
    @Schema(title =  "分配类型 1：标签 2：产品；3：达人类型")
    private Integer allotType;
    /**
     * 分配状态 1：分配中；9：分配完成；-1：分配异常
     */
    @Excel(name = "分配状态 1：分配中；9：分配完成；-1：分配异常", width = 15)
    @Schema(title =  "分配状态 1：分配中；9：分配完成；-1：分配异常")
    private Integer allotStatus;
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
     * 分配结果
     */
    @Excel(name = "分配结果", width = 15)
    @Schema(title =  "分配结果")
    private String allotResult;
}
