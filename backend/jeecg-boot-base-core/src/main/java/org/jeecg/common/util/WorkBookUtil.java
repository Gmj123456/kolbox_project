package org.jeecg.common.util;

import com.alibaba.excel.EasyExcel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;

/**
 * EasyExcel 3.x 工具类（替代旧版 WorkBookUtil）
 * 注意：调用方需负责关闭传入的 InputStream
 */
public class WorkBookUtil {

    private static final Logger log = LoggerFactory.getLogger(WorkBookUtil.class);
    private static final String DEFAULT_SHEET_NAME = "sheet";

    public static <T> List<T> getWorkbook(InputStream inStr, Class<T> cls) {
        try {
            return EasyExcel.read(inStr)
                    .head(cls)
                    .sheet()
                    .doReadSync();
        } catch (Exception e) {
            log.error("读取 Excel 失败", e);
            throw new RuntimeException("Excel 读取异常", e);
        }
    }
}