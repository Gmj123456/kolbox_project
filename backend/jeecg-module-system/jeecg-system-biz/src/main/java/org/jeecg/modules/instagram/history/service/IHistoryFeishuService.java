package org.jeecg.modules.instagram.history.service;

import org.jeecg.common.api.vo.Result;

/**
 * @Description: 历史提报飞书数据处理服务
 * @Author:
 * @Date: 2025-11-26
 * @Version: V1.0
 */
public interface IHistoryFeishuService {

    /**
     * 从飞书导入历史提报数据
     * 
     * @return 处理结果
     */
    Result<?> importFeishuExcelData();
}