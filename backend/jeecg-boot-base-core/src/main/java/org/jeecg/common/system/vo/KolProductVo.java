package org.jeecg.common.system.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Author: nqr
 * @Date: 2025/7/19 16:45
 * @Description:
 **/
@Data
public class KolProductVo {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
     @Schema(title = "主键")
    private String id;
    /**
     * 品牌ID（关联kol_brand表）
     */
    @Excel(name = "品牌ID（关联kol_brand表）", width = 15)
     @Schema(title = "品牌ID（关联kol_brand表）")
    private String brandId;
    /**
     * 品牌名称
     */
    @Excel(name = "品牌", width = 15)
     @Schema(title = "品牌名称")
    private String brandName;
    /**
     * 产品名称
     */
    @Excel(name = "名称", width = 15)
     @Schema(title = "名称")
    private String productName;
    /**
     * 产品图片
     */
    @Excel(name = "图片", width = 15)
     @Schema(title = "产品图片")
    private String productImage;
    /**
     * 产品链接
     */
    @Excel(name = "链接", width = 15)
     @Schema(title = "产品链接")
    private String productUrl;
    /**
     * 产品属性JSON（型号、尺寸、颜色等）
     */
    @Excel(name = "型号", width = 15)
     @Schema(title = "产品属性JSON（型号、尺寸、颜色等）")
    @TableField(exist = false)
    private String productAttributes;
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
    private Date createTime;
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
    private Date updateTime;
    /**
     * 删除标志（0=未删除,1=已删除）
     */
    @Excel(name = "删除标志（0=未删除,1=已删除）", width = 15)
     @Schema(title = "删除标志（0=未删除,1=已删除）")
    private Integer isDelete;
    private Integer sortCode;
}
