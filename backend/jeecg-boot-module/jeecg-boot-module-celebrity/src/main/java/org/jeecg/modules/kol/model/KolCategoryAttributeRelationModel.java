package org.jeecg.modules.kol.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

/**
 * @Description: 类目关联表
 * @Author: dongruyang
 * @Date: 2025-06-21
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class KolCategoryAttributeRelationModel {
    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
    /**
     * 类目ID
     */
    @Excel(name = "类目ID", width = 15)
    @Schema(title =  "类目ID")
    private String attributeId;
    private String attributeIds;
    /**
     * 场景、达人类型或受众类目ID
     */
    @Excel(name = "场景、达人类型或受众类目ID", width = 15)
    @Schema(title =  "场景、达人类型或受众类目ID")
    private String attributeCategoryId;
    /**
     * 关联级别：1=高度关联,2=中度关联,3=低度关联
     */
    @Excel(name = "关联级别：1=高度关联,2=中度关联,3=低度关联", width = 15)
    @Schema(title =  "关联级别：1=高度关联,2=中度关联,3=低度关联")
    private Integer level;
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
     * 删除标记 0=正常,1=已删除
     */
    @Excel(name = "删除标记 0=正常,1=已删除", width = 15)
    @Schema(title =  "删除标记 0=正常,1=已删除")
    private Integer isDelete;
    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @Schema(title =  "备注")
    private String remark;
    private String categoryId;
    private String categoryIds;
    private String categoryName;
    private String categoryEnName;
    private String categoryNamePath;
    private String attributeName;
    private String attributeEnName;
    private String attributeNamePath;
    private String attributeEnNamePath;
    private String subAttributeName;
    private String subAttributeTypeId;
    private String subAttributeTypeName;
    private String parentId;
    private String dataListStr;
    private String rootPathCode;
    private List<TypeData> dataList;
    private List<KolCategoryAttributeRelationModel> children;
    private Integer sortCode;
    private String attributeTypeId;
    private String attributeTypeName;
}
