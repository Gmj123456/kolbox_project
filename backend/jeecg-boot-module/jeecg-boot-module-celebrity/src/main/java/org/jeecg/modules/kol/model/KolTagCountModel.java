package org.jeecg.modules.kol.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 网红标签统计模型
 * @Author: xyc
 * @Date: 2024-12-17 16:20:36
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class KolTagCountModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * tag名称（标签/种子账号）
     */
    @Excel(name = "tag名称（标签/种子账号）", width = 15)
    @Schema(title =  "tag名称（标签/种子账号）")
    private String tagName;

    /**
     * 标签数量
     */
    @Excel(name = "标签数量", width = 15)
    @Schema(title =  "标签数量")
    private Integer tagNum;

    private Integer unassignedCount;

    /**
     * 未分配数量更新时间
     */
    @Excel(name = "未分配数量更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "未分配数量更新时间")
    private Date snapshotTime;

}
