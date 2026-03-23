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

/**
 * @Description: 类目类型表
 * @Author: dongruyang
 * @Date: 2025-05-13
 * @Version: V1.0
 */
@Data
@TableName("kol_attribute_type")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class KolAttributeType {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
    /**
     * 类型编码
     */
    @Excel(name = "类型编码", width = 15)
    @Schema(title =  "类型编码")
    private String typeCode;
    /**
     * 类型名称
     */
    @Excel(name = "类型名称", width = 15)
    @Schema(title =  "类型名称")
    private String typeName;
    /**
     * 类型描述
     */
    @Excel(name = "类型描述", width = 15)
    @Schema(title =  "类型描述")
    private String description;
    /**
     * 排序码
     */
    @Excel(name = "排序码", width = 15)
    @Schema(title =  "排序码")
    private Integer sortCode;
    /**
     * 类型 0:个性化标签；1：产品类目；2：普通类型
     */
    private Integer type;
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
     * 删除标志（0=未删除,1=已删除）
     */
    @Excel(name = "删除标志（0=未删除,1=已删除）", width = 15)
    @Schema(title =  "删除标志（0=未删除,1=已删除）")
    private Integer delFlag;

    /**
     * 分类数量
     */
    @TableField(exist = false)
    private Integer categoryCount;
}
