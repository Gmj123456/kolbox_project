package org.jeecg.modules.feishu.model;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * 飞书表格数据转换结果模型
 * @Author: lf
 * @Date: 2025-09-15
 * @Version: V1.0
 */
@Data
public class FeishuSheetConvertResult<T> {
    
    /**
     * 表头字段映射
     */
    private Map<String, String> headerMapping;
    
    /**
     * 转换后的实体对象列表
     */
    private List<T> entityList;
    
    /**
     * 转换成功的行数
     */
    private Integer successCount;
    
    /**
     * 转换失败的行数
     */
    private Integer errorCount;
    
    /**
     * 错误详情
     */
    private List<String> errorMessages;
    
    /**
     * 原始表头数据
     */
    private List<String> originalHeaders;
    
    /**
     * 原始数据行数
     */
    private Integer totalRows;
}