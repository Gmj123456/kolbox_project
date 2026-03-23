package org.jeecg.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ReadExcelTools {
    private static final Gson gson = new GsonBuilder().serializeNulls().create();
    private final static String xls = "xls";
    private final static String xlsx = "xlsx";

    /**
     * 读入excel文件，解析后返回
     * @param file
     * @throws IOException
     */
    public static List<String[]> readExcel(MultipartFile file) throws IOException{
        //检查文件
        checkFile(file);
        //获得Workbook工作薄对象
        Workbook workbook = getWorkBook(file);
        //创建返回对象，把每行中的值作为一个数组，所有行作为一个集合返回
        List<String[]> list = new ArrayList<String[]>();
        if(workbook != null){
            for(int sheetNum = 0;sheetNum < workbook.getNumberOfSheets();sheetNum++){
                //获得当前sheet工作表
                Sheet sheet = workbook.getSheetAt(sheetNum);
                if(sheet == null){
                    continue;
                }
                //获得当前sheet的开始行
                int firstRowNum  = sheet.getFirstRowNum();
                //获得当前sheet的结束行
                int lastRowNum = sheet.getLastRowNum();
                //循环除了第一行的所有行
                for(int rowNum = firstRowNum+1;rowNum <= lastRowNum;rowNum++){ //为了过滤到第一行因为我的第一行是数据库的列
                    //获得当前行
                    Row row = sheet.getRow(rowNum);
                    if(row == null){
                        continue;
                    }
                    //获得当前行的开始列
                    int firstCellNum = row.getFirstCellNum();
                    //获得当前行的列数
                    int lastCellNum = row.getLastCellNum();//为空列获取
//                    int lastCellNum = row.getPhysicalNumberOfCells();//为空列不获取
//                    String[] cells = new String[row.getPhysicalNumberOfCells()];
                    String[] cells = new String[row.getLastCellNum()];
                    //循环当前行
                    for(int cellNum = firstCellNum; cellNum < lastCellNum;cellNum++){
                        Cell cell = row.getCell(cellNum);
                        cells[cellNum] = getCellValue(cell);
                    }
                    list.add(cells);
                }
            }
        }
        log.info(gson.toJson(list));
        return list;
    }
    public static void checkFile(MultipartFile file) throws IOException{
        //判断文件是否存在
        if(null == file){
            throw new FileNotFoundException("文件不存在！");
        }
        //获得文件名
        String fileName = file.getOriginalFilename();
        //判断文件是否是excel文件
        if(!fileName.endsWith(xls) && !fileName.endsWith(xlsx)){
            throw new IOException(fileName + "不是excel文件");
        }
    }
    public static Workbook getWorkBook(MultipartFile file) {
        //获得文件名
        String fileName = file.getOriginalFilename();
        //创建Workbook工作薄对象，表示整个excel
        Workbook workbook = null;
        try {
            //获取excel文件的io流
            InputStream is = file.getInputStream();
            //根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象
            if(fileName.endsWith(xls)){
                //2003
                workbook = new HSSFWorkbook(is);
            }else if(fileName.endsWith(xlsx)){
                //2007
                workbook = new XSSFWorkbook(is);
            }
        } catch (IOException e) {
            log.info(e.getMessage());
        }
        return workbook;
    }
    public static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        CellType cellType = cell.getCellType();
        String cellValue = "";

        switch (cellType) {
            case NUMERIC:
                // 先判断是否为日期格式
                if (DateUtil.isCellDateFormatted(cell)) {
                    // 你可以自定义日期格式，例如：yyyy-MM-dd
                    cellValue = cell.getDateCellValue().toString();
                } else {
                    // 处理普通数字：避免 1 → "1.0"
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == Math.floor(numericValue) && !Double.isInfinite(numericValue)) {
                        // 是整数，转为 long 再转字符串，避免 .0
                        cellValue = String.valueOf((long) numericValue);
                    } else {
                        // 保留小数，可选：使用 DecimalFormat 控制精度
                        cellValue = String.valueOf(numericValue);
                    }
                }
                break;

            case STRING:
                cellValue = cell.getStringCellValue();
                break;

            case BOOLEAN:
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;

            case FORMULA:
                // 公式单元格：尝试按字符串读取（兼容性更好）
                try {
                    cellValue = cell.getStringCellValue();
                } catch (Exception e) {
                    // 如果公式结果是数字，fallback 到数值处理
                    try {
                        double formulaValue = cell.getNumericCellValue();
                        if (formulaValue == Math.floor(formulaValue) && !Double.isInfinite(formulaValue)) {
                            cellValue = String.valueOf((long) formulaValue);
                        } else {
                            cellValue = String.valueOf(formulaValue);
                        }
                    } catch (Exception ex) {
                        cellValue = "[公式]";
                    }
                }
                break;

            case BLANK:
                cellValue = "";
                break;

            case ERROR:
                cellValue = "错误值";
                break;

            default:
                cellValue = "未知类型";
                break;
        }

        return cellValue != null ? cellValue.trim() : "";
    }
}

