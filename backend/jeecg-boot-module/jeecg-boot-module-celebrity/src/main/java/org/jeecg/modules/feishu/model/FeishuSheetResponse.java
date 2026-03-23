package org.jeecg.modules.feishu.model;

import lombok.Data;
import java.util.List;

/**
 * 飞书在线表格响应数据模型
 * @Author: dongruyang
 * @Date: 2025-09-15
 * @Version: V1.0
 */
@Data
public class FeishuSheetResponse {
    
    /**
     * 返回码
     */
    private Integer code;
    
    /**
     * 返回消息
     */
    private String msg;
    
    /**
     * 响应数据
     */
    private FeishuSheetData data;
    
    @Data
    public static class FeishuSheetData {
        
        /**
         * 版本号
         */
        private Integer revision;
        
        /**
         * 表格token
         */
        private String spreadsheetToken;
        
        /**
         * 总单元格数
         */
        private Integer totalCells;
        
        /**
         * 值范围列表
         */
        private List<ValueRange> valueRanges;
    }
    
    @Data
    public static class ValueRange {
        
        /**
         * 主要维度
         */
        private String majorDimension;
        
        /**
         * 范围
         */
        private String range;
        
        /**
         * 版本号
         */
        private Integer revision;
        
        /**
         * 值列表
         */
        private List<List<Object>> values;
    }
}