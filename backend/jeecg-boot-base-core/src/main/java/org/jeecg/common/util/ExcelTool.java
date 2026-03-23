package org.jeecg.common.util;


import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.DecimalFormat;

public class ExcelTool {
    private static Logger logger = LoggerFactory.getLogger(ExcelTool.class);
    private static ExcelTool tool = new ExcelTool();

    public static ExcelTool getInstance() {
        Class var0 = ExcelTool.class;
        synchronized (ExcelTool.class) {
            if (tool == null) {
                tool = new ExcelTool();
            }
        }

        return tool;
    }

    private ExcelTool() {
    }

    public Workbook getExcelWorkbook(String filePath) throws Exception {
        return this.getExcelWorkbook(new File(filePath));
    }

    public Workbook getExcelWorkbook(File file) throws Exception {
        FileInputStream is = new FileInputStream(file);
        Workbook wb = this.getExcelWorkbook((InputStream) is);
        return wb;
    }

    public Workbook getExcelWorkbook(InputStream in) throws InvalidFormatException, IOException {
        Workbook wb = WorkbookFactory.create(in);
        return wb;
    }

    public String getCellContent(Sheet sheet, int rowNum, int colNum) {
        Row row = sheet.getRow(rowNum);
        String contents = "";
        if (row != null && row.getCell(colNum) != null) {
            contents = this.getCellContent(row.getCell(colNum)).trim();
        }

        return contents;
    }

    public String getCellContent(Cell cell) {
        DecimalFormat df = new DecimalFormat("0");
        if (cell == null) {
            return "";
        }

        CellType cellType = cell.getCellType();
        String value;

        switch (cellType) {
            case NUMERIC:
                double numericValue = cell.getNumericCellValue();
                // 判断是否为整数（避免 123.0 → "123.0"）
                if (numericValue == Math.floor(numericValue) && !Double.isInfinite(numericValue)) {
                    value = String.valueOf((long) numericValue);
                } else {
                    value = String.valueOf(numericValue);
                }
                break;

            case STRING:
                value = cell.getStringCellValue().trim();
                break;

            case FORMULA:
                try {
                    // 尝试按数值解析公式结果
                    double formulaValue = cell.getNumericCellValue();
                    if (formulaValue == Math.floor(formulaValue) && !Double.isInfinite(formulaValue)) {
                        value = String.valueOf((long) formulaValue);
                    } else {
                        value = String.valueOf(formulaValue);
                    }
                } catch (Exception e) {
                    // 公式结果可能是字符串
                    logger.info("公式单元格数值解析失败，尝试字符串解析", e);
                    try {
                        value = cell.getStringCellValue().trim();
                    } catch (Exception ex) {
                        value = cell.toString().trim();
                    }
                }
                break;

            case BOOLEAN:
                value = String.valueOf(cell.getBooleanCellValue()).trim();
                break;

            case BLANK:
            case _NONE:
            default:
                value = "";
        }

        return value != null ? value.trim() : "";
    }

    public boolean isNullRow(Sheet sheet, int rowNum) {
        int rowCount = sheet.getLastRowNum();
        return rowCount < rowNum;
    }

    public int getSheetCount(Workbook wb) {
        return wb.getNumberOfSheets();
    }

    public int getSheetRows(Sheet sheet) {
        return sheet.getPhysicalNumberOfRows();
    }

    public HSSFWorkbook createWorkbook() {
        HSSFWorkbook wb = new HSSFWorkbook();
        return wb;
    }

    public HSSFSheet createSheet(HSSFWorkbook wb, String sheetName) {
        HSSFSheet sheet = wb.createSheet(sheetName);
        return sheet;
    }

    public HSSFWorkbook createHead(HSSFWorkbook wb, HSSFSheet sheet, String[] heads) {
        HSSFRow row = sheet.createRow(0);

        for (int i = 0; i < heads.length; ++i) {
            HSSFCell cell = row.createCell(i);
            cell.setCellType(CellType.forInt(1));
            cell.setCellValue(new HSSFRichTextString(heads[i]));
            HSSFCellStyle cellStyle = wb.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.forInt((short) 2));
            cellStyle.setVerticalAlignment(VerticalAlignment.forInt((short) 1));
            cellStyle.setWrapText(true);
            HSSFFont font = wb.createFont();
            font.setFontName("宋体");
            font.setFontHeight((short) 250);
            cellStyle.setFont(font);
            cell.setCellStyle(cellStyle);
        }

        return wb;
    }

    public HSSFWorkbook createRow(HSSFWorkbook wb, HSSFSheet sheet, String[] params) {
        HSSFRow row = sheet.createRow(sheet.getLastRowNum() + 1);

        for (int i = 0; i < params.length; ++i) {
            HSSFCell cell = row.createCell(i);
            cell.setCellType(CellType.forInt(1));
            cell.setCellValue(new HSSFRichTextString(params[i]));
            HSSFCellStyle cellStyle = wb.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.forInt((short) 2));
            cellStyle.setVerticalAlignment(VerticalAlignment.forInt((short) 1));
            cellStyle.setWrapText(true);
            HSSFFont font = wb.createFont();
            font.setFontHeight((short) 250);
            cellStyle.setFont(font);
            cell.setCellStyle(cellStyle);
            sheet.autoSizeColumn(i);
        }

        return wb;
    }

    public void exportExcel(HSSFWorkbook wb, String fileName) {
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(new File(fileName));
            wb.write(fos);
        } catch (FileNotFoundException var15) {
            logger.error("文件未找到,fileName" + fileName, var15);
        } catch (IOException var16) {
            logger.error("导出失败", var16);
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException var14) {
            }

        }

    }
}

