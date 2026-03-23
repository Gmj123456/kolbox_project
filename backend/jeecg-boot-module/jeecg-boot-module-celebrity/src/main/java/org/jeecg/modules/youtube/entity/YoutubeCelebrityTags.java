package org.jeecg.modules.youtube.entity;

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

/**
 * @Description: YT网红标签
 * @Author: nqr
 * @Date: 2023-11-22
 * @Version: V1.0
 */
@Data
@TableName("youtube_celebrity_tags")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class YoutubeCelebrityTags {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
    /**
     * YouTube网红唯一ID
     */
    @Excel(name = "YouTube网红唯一ID", width = 15)
    @Schema(title =  "YouTube网红唯一ID")
    private String account;

    /**
     * 网红账号名称
     */
    @Excel(name = "网红账号名称", width = 15)
    @Schema(title =  "网红账号名称")
    private String username;
    /**
     * tag名称
     */
    @Excel(name = "tag名称", width = 15)
    @Schema(title =  "tag名称")
    private String tagName;

    /**
     * tag类型（tag类型：0=hashtag 1=keyword 2=rootVideo 3=rootAccount）
     */
    @Excel(name = "tag类型：0=hashtag 1=keyword 2=rootVideo 3=rootAccount", width = 15)
    @Schema(title =  "tag类型：0=hashtag 1=keyword 2=rootVideo 3=rootAccount")
    private Integer tagType;

    /**
     * 是否分配 (0=未分配,1=已分配)
     */
    @Excel(name = "是否分配 (0=未分配,1=已分配)", width = 15)
    @Schema(title =  "是否分配 (0=未分配,1=已分配)")
    private Integer isAllot;
    /**
     * 网红顾问_ID
     */
    @Excel(name = "网红顾问_ID", width = 15)
    @Schema(title =  "网红顾问_ID")
    private String counselorId;
    /**
     * 网红顾问名称
     */
    @Excel(name = "网红顾问名称", width = 15)
    @Schema(title =  "网红顾问名称")
    private String counselorName;
    /**
     * 分配时间
     */
    @Excel(name = "分配时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "分配时间")
    private java.util.Date allotTime;
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
     * 分配日志ID
     */
    @Excel(name = "分配日志ID", width = 15)
    @Schema(title =  "分配日志ID")
    private String allotLogId;
    /**
     * 分配日志明细ID
     */
    @Excel(name = "分配日志明细ID", width = 15)
    @Schema(title =  "分配日志明细ID")
    private String allotLogDetailId;
    /**
     * 任务主键
     */
    @Excel(name = "任务主键", width = 15)
    @Schema(title =  "任务主键")
    private String taskId;

    /**
     * 网红国家
     */
    @Excel(name = "网红国家", width = 15)
    @Schema(title =  "网红国家")
    private String country;

    /**
     * 网红粉丝数
     */
    @Excel(name = "网红粉丝数", width = 15)
    @Schema(title =  "网红粉丝数")
    private Integer followersNum;
}
