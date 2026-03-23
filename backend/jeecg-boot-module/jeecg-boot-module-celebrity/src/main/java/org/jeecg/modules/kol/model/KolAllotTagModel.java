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
 * @Description: 网红标签模型
 * @Author: xyc
 * @Date: 2024年12月3日 19:03:07
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class KolAllotTagModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * tag类型（tag类型：0=hashtag 1=keyword 2=rootVideo 3=rootAccount）
     */
    @Excel(name = "tag类型：0=hashtag 1=keyword 2=rootVideo 3=rootAccount", width = 15)
    @Schema(title =  "tag类型：0=hashtag 1=keyword 2=rootVideo 3=rootAccount")
    private Integer tagType;
    /**
     * tag名称（标签/种子账号）
     */
    @Excel(name = "tag名称（标签/种子账号）", width = 15)
    @Schema(title =  "tag名称（标签/种子账号）")
    private String tagName;

    /**
     * 是否分配 (0=未分配,1=已分配)
     */
    @Excel(name = "是否分配 (0=未分配,1=已分配)", width = 15)
    @Schema(title =  "是否分配 (0=未分配,1=已分配)")
    private Integer isAllot;

    /**
     * 分配时间
     */
    @Excel(name = "分配时间", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(title =  "分配时间")
    private Date allotTime;
}
