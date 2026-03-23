package org.jeecg.common.system.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Description: 个性化标签
 * @Author: nqr
 * @Version: V1.0
 */
@Data
public class PersonalTagsModel {
    /**
     * id
     */
     @Schema(title = "id")
    private String id;
    /**
     * 标签名称
     */
    @Excel(name = "标签名称", width = 15)
     @Schema(title = "标签名称")
    private String tagName;
    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
     @Schema(title = "备注")
    private String remark;
    /**
     * 创建人
     */
    @Excel(name = "创建人", width = 15)
     @Schema(title = "创建人")
    private String createBy;
    /**
     * 创建时间
     */
    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
     @Schema(title = "创建时间")
    private java.util.Date createTime;
    /**
     * 修改人
     */
    @Excel(name = "修改人", width = 15)
     @Schema(title = "修改人")
    private String updateBy;
    /**
     * 修改时间
     */
    @Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
     @Schema(title = "修改时间")
    private java.util.Date updateTime;
    private String categoryId;
    private String categoryIds;
    private String categoryName;
    private String tagId;
    private String attributeId;
    private String attributeIds;
    private String attributeName;
}
