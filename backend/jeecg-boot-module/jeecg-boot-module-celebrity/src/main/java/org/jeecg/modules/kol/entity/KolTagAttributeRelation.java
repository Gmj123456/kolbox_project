package org.jeecg.modules.kol.entity;

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
import java.util.List;

/**
 * @Description: 标签与类目关联表
 * @Author: nqr
 * @Date: 2025-05-21
 * @Version: V1.0
 */
@Data
@TableName("kol_tag_attribute_relation")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class KolTagAttributeRelation {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
    /**
     * 标签ID
     */
    @Excel(name = "标签ID", width = 15)
    @Schema(title =  "标签ID")
    private String tagId;
    private String productId;
    /**
     * 标签名称
     */
    @Excel(name = "标签名称", width = 15)
    @Schema(title =  "标签名称")
    private String tagName;
    /**
     * 平台类型（0:IG;1:YT;2:TK）
     */
    private Integer platformType;
    /**
     * 标签类型：0=hashtag 1=keyword 2=rootVideo 3=rootAccount
     */
    private Integer tagType;
    private String videoUrl;
    /**
     * 类目ID
     */
    @Excel(name = "类目ID", width = 15)
    @Schema(title =  "类目ID")
    private String attributeId;
    /**
     * 类目名称
     */
    @Excel(name = "类目", width = 15)
    @Schema(title =  "类目名称")
    private String attributeName;
    /**
     * 类目类型ID
     */
    @Excel(name = "类目类型ID", width = 15)
    @Schema(title =  "类目类型ID")
    private String attributeTypeId;
    /**
     * 类目类型名称
     */
    @Excel(name = "类目类型", width = 15)
    @Schema(title =  "类目类型名称")
    private String attributeTypeName;
    /**
     * 删除状态（0=未删除,1=已删除）
     */
    @Schema(title =  "删除状态（0=未删除,1=已删除）")
    private Integer isDelete;
    /**
     * 创建人
     */
    @Schema(title =  "创建人")
    private String createBy;
    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "创建时间")
    private Date createTime;
    /**
     * 修改人
     */
    @Schema(title =  "修改人")
    private String updateBy;
    /**
     * 修改时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "修改时间")
    private Date updateTime;
    private Integer sortCode;
    private Integer level;

    @TableField(exist = false)
    List<KolTagCategory> tagCategoryList;
    @TableField(exist = false)
    List<KolCategory> categoryList;
    @TableField(exist = false)
    private String brandId;
    @TableField(exist = false)
    private String brandName;
    @TableField(exist = false)
    private String productName;
}
