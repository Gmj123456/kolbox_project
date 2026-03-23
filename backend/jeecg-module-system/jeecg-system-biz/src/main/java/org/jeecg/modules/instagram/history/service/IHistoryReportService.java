package org.jeecg.modules.instagram.history.service;

import com.alibaba.fastjson.JSONObject;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.instagram.history.entity.KolHistoryCelebrityDetail;
import org.jeecg.modules.instagram.history.model.ConversionResult;
import org.jeecg.modules.instagram.history.model.KolHistoryCelebrityDetailFeishuModel;

import java.util.List;

/**
 * @Description: 历史提报业务处理服务接口
 * @Author:
 * @Date: 2025-11-26
 * @Version: V1.0
 */
public interface IHistoryReportService {

    /**
     * 处理飞书模型数据并保存到各个表中
     *
     * @param unSyncedList 飞书模型列表
     * @return 处理结果
     */
    ConversionResult processFeishuDataAndSave(List<KolHistoryCelebrityDetailFeishuModel> unSyncedList);

    /**
     * 处理实体数据并保存到各个表中
     *
     * @param entityList 实体列表
     */
    void processDataAndSave(List<KolHistoryCelebrityDetail> entityList);
}