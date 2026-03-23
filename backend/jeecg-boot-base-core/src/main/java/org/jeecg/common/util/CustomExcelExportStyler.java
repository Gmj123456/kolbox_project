package org.jeecg.common.util;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.jeecgframework.poi.excel.entity.params.ExcelExportEntity;
import org.jeecgframework.poi.excel.export.styler.ExcelExportStylerDefaultImpl;

/**
 * @author lf
 * @version 1.0.0
 * @ClassName CustomExcelExportStyler.java
 * @Description TODO
 * @createTime 2024年05月31日 09:16:00
 */
public class CustomExcelExportStyler extends ExcelExportStylerDefaultImpl {

    public CustomExcelExportStyler(Workbook workbook) {
        super(workbook);
    }

    @Override
    public CellStyle getTitleStyle(short color) {
        CellStyle style = super.getTitleStyle(color);
        style.setAlignment(HorizontalAlignment.LEFT); // 标题左对齐
        return style;
    }

    @Override
    public CellStyle getStyles(boolean noneStyler, ExcelExportEntity entity) {
        CellStyle style = super.getStyles(noneStyler, entity);
        style.setAlignment(HorizontalAlignment.LEFT); // 内容左对齐
        return style;
    }

    @Override
    public CellStyle stringNoneStyle(Workbook workbook, boolean isWarp) {
        CellStyle style = super.stringNoneStyle(workbook, isWarp);
        style.setAlignment(HorizontalAlignment.LEFT); // 字符串内容左对齐
        return style;
    }
}
