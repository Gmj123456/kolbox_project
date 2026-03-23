package org.jeecg.modules.kol.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 标签品牌表
 * @Author: xyc
 * @Date: 2025-01-02 18:29:30
 * @Version: V1.0
 */
@Data
@TableName("kol_tag_brand")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class KolTagBrand extends ExtendedEntity{


    /**
     * 标签ID
     */
    @Schema(title =  "标签ID")
    private String tagId;
    /**
     * 品牌ID
     */
    @Schema(title =  "品牌ID")
    private String brandId;

    
    /**
     * 大类标签JSON
     */
    @TableField(exist = false)
    private String storeTags;
    /**
     * tag名称
     */
    @Excel(name = "标签", width = 15, orderNum = "1")
    @Schema(title =  "tag名称")
    private String tagName;
    /**
     * 品牌名称
     */
    @Excel(name = "品牌", width = 15, orderNum = "2")
    @Schema(title =  "品牌名称")
    private String brandName;
    /**
     * 产品备注
     */
    @Excel(name = "产品备注", width = 15, orderNum = "3")
    @Schema(title =  "产品备注")
    private String tagProduct;

    /**
     * 平台类型（0:IG;1:YT;2:TK）
     */
    @Excel(name = "平台", width = 15, orderNum = "4",dicCode = "platform_type")
    private Integer platformType;


}
