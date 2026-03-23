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

import java.io.Serializable;

/**
 * @Description: 网红标签分类类目表
 * @Author: xyc
 * @Date: 2024年11月22日 15:22:48
 * @Version: V1.0
 */
@Data
@TableName("kol_tag_category")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class KolTagCategory implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;

    /**
     * 父级节点
     */
    @Excel(name = "父级节点", width = 15)
    @Schema(title =  "父类id")
    private String parentId;

    /**
     * 类目编码
     */
    @Excel(name = "类目编码", width = 15)
    @Schema(title =  "类目编码")
    private String categoryCode;

    /**
     * 类目名称
     */
    @Excel(name = "类目名称", width = 15)
    @Schema(title =  "类目名称")
    private String categoryName;

    /**
     * 排序码
     */
    @Excel(name = "排序", width = 15)
    @Schema(title =  "排序")
    private Integer sortCode;

    /**
     * 删除状态（0=未删除,1=已删除）
     */
    @Schema(title =  "删除状态（0=未删除,1=已删除）")
    private Integer isDelete;

    /**
     * 是否有子节点
     */
    @Excel(name = "是否有子节点", width = 15)
    @Schema(title =  "是否有子节点")
    private Integer isHasChild;

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
     * 创建日期
     */
    @Excel(name = "创建日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "创建日期")
    private java.util.Date createTime;

    /**
     * 更新人
     */
    @Excel(name = "更新人", width = 15)
    @Schema(title =  "更新人")
    private String updateBy;

    /**
     * 更新日期
     */
    @Excel(name = "更新日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "更新日期")
    private java.util.Date updateTime;
    /**
     * 类目Code路径
     */
    private String categoryCodePath;
    /**
     * 类目名称路径
     */
    private String categoryNamePath;
    /**
     * 类目类型ID
     */
    private String categoryTypeId;
    /**
     * 类目类型名称
     */
    private String categoryTypeName;

}
