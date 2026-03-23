package org.jeecg.modules.kol.model;

import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.List;

/**
 * @Description: 网红品牌产品表
 * @Author: nqr
 * @Date: 2025-04-07
 * @Version: V1.0
 */
@Data
@TableName("kol_product")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class KolProductModel {
    private String id;
    /**
     * 品牌名称
     */
    @Excel(name = "品牌名称", width = 15)
    @Schema(title =  "品牌名称")
    private String brandName;
    /**
     * 产品名称
     */
    @Excel(name = "产品名称", width = 15)
    @Schema(title =  "名称")
    private String productName;
    /**
     * 产品链接
     */
    @Excel(name = "产品链接", width = 15)
    @Schema(title =  "产品链接")
    private String productUrl;
    /**
     * 图片链接
     */
    @Excel(name = "图片链接", width = 15)
    @Schema(title =  "图片链接")
    private String productImage;
    /**
     * 产品属性JSON（型号、尺寸、颜色等）
     */
    @Excel(name = "型号", width = 15)
    @Schema(title =  "产品属性JSON（型号、尺寸、颜色等）")
    private String productAttributes;
    /**
     * 品牌ID（关联kol_brand表）
     */
    private String brandId;

    /**
     * 关联级别：1=高度关联,2=中度关联,3=低度关联
     */
    @Excel(name = "关联级别：1=高度关联,2=中度关联,3=低度关联", width = 15)
    @Schema(title =  "关联级别：1=高度关联,2=中度关联,3=低度关联")
    private Integer level;

    private List<ProductCategory> productCategory;

    private String remark;

    private String categoryId;
    @Excel(name = "类目", width = 15)
    private String categoryName;
    private String categoryCode;

    private List<TypeData> dataList;

    private Integer isUpdate;
}
