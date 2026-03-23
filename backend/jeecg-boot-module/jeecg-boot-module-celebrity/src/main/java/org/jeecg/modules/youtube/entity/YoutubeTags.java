package org.jeecg.modules.youtube.entity;

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
 * @Description: YT标签
 * @Author: nqr
 * @Date: 2023-11-22
 * @Version: V1.0
 */
@Data
@TableName("youtube_tags")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class YoutubeTags {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
    /**
     * tag名称
     */
    @Excel(name = "tag名称", width = 15)
    @Schema(title =  "tag名称")
    private String tagName;
    /**
     * tag状态
     * (0=未开始,
     * 10=执行中,
     * 90=中断完成,
     * 99=执行成功 )
     */
    @Excel(name = "tag状态  (0=未开始,10=执行中,90=中断完成,99=执行成功 )", width = 15)
    @Schema(title =  "tag状态 (0=未开始,10=执行中,90=中断完成,99=执行成功 )")
    private Integer tagStatus;
    /**
     * 最后执行时间
     */
    @Excel(name = "最后执行时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "最后执行时间")
    private Date lastExecutionTime;
    /**
     * 排序
     */
    @Excel(name = "排序", width = 15)
    @Schema(title =  "排序")
    private Integer sortCode;
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
     * 平台类型（0:TK;1:YT;2:IG）
     */
    @Excel(name = "平台类型（0:TK;1:YT;2:IG）", width = 15)
    @Schema(title =  "平台类型（0:TK;1:YT;2:IG）")
    private Integer platformType;
}
