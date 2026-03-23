package org.jeecg.modules.instagram.storecelebrity.model;

import lombok.Data;

/**
 * 导入结果封装类
 * @Description: 用于封装飞书表格导入的结果信息
 * @Author: nqr
 * @Date: 2025-09-16
 * @Version: V1.0
 */
@Data
public class ImportResult {
    
    /**
     * 成功导入的数量
     */
    private int successCount = 0;
    
    /**
     * 失败的数量
     */
    private int errorCount = 0;
    
    /**
     * 是否有错误
     */
    private boolean hasError = false;
    
    /**
     * 详细错误信息
     */
    private String errorMessage;
    
    /**
     * 增加成功计数
     */
    public void incrementSuccess() {
        this.successCount++;
    }
    
    /**
     * 增加失败计数
     */
    public void incrementError() {
        this.errorCount++;
        this.hasError = true;
    }
    
    /**
     * 获取总处理数量
     */
    public int getTotalCount() {
        return successCount + errorCount;
    }
    
    /**
     * 创建成功结果
     */
    public static ImportResult success(int count) {
        ImportResult result = new ImportResult();
        result.successCount = count;
        return result;
    }
    
    /**
     * 创建失败结果
     */
    public static ImportResult error(String message) {
        ImportResult result = new ImportResult();
        result.hasError = true;
        result.errorMessage = message;
        return result;
    }
}