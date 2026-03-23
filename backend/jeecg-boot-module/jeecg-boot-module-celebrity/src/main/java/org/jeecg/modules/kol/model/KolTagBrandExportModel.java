package org.jeecg.modules.kol.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.modules.kol.entity.KolTagBrand;
import org.jeecgframework.poi.excel.annotation.Excel;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class KolTagBrandExportModel extends KolTagBrand {

    /**
     * 平台类型名称
     */
    @TableField(exist = false)
    private String platformTypeName;
}
