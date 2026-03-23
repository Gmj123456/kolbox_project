package org.jeecg.modules.kol.entity;

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

import java.util.Date;

/**
 * @Description: 产品与标签分类类目关联表
 * @Author: nqr
 * @Date: 2025-05-24
 * @Version: V1.0
 */
@Data
@TableName("kol_product_attribute_relation")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class KolProductAttributeRelation {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
    /**
     * 品牌ID
     */
    @Excel(name = "品牌ID", width = 15)
    @Schema(title =  "品牌ID")
    private String brandId;
    /**
     * 产品ID
     */
    @Excel(name = "产品ID", width = 15)
    @Schema(title =  "产品ID")
    private String productId;
    /**
     * 类目ID
     */
    @Excel(name = "类目ID", width = 15)
    @Schema(title =  "类目ID")
    private String attributeId;
    /**
     * 类目类型ID
     */
    @Excel(name = "类目类型ID", width = 15)
    @Schema(title =  "类目类型ID")
    private String attributeTypeId;
    /**
     * 产品名称
     */
    @Excel(name = "产品名称", width = 15)
    @Schema(title =  "产品名称")
    private String brandName;
    /**
     * 产品名称
     */
    @Excel(name = "产品名称", width = 15)
    @Schema(title =  "产品名称")
    private String productName;
    /**
     * 类目名称
     */
    @Excel(name = "类目名称", width = 15)
    @Schema(title =  "类目名称")
    private String attributeName;
    /**
     * 类目类型名称
     */
    @Excel(name = "类目类型名称", width = 15)
    @Schema(title =  "类目类型名称")
    private String attributeTypeName;
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
    private Integer level;
    private Integer sortCode;
}
