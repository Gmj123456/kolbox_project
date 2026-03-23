package org.jeecg.modules.feishu.service;

import com.lark.oapi.service.bitable.v1.model.AppTableRecord;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.feishu.model.FeishuSheetResponse;
import org.jeecg.modules.kol.entity.KolSysUserFeishuSheet;
import org.jeecg.modules.feishu.model.FeishuSheetConvertResult;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: nqr
 * @Date: 2025/7/17 16:55
 * @Description:
 **/
public interface IFeishuService {

    String synchronizeData(Integer platformType) throws Exception;

    void batchUpdateRecords(String appToken, String tableId, AppTableRecord[] records);

    void insertRecord(String appToken, String tableId, HashMap<String, Object> map) throws Exception;

    Result<?> extracted(Integer platformType, String data) throws IOException;

    void batchDeleteRecords(String appToken, String tableId, String recordIds);

    /**
     * 根据条件查询记录id
     *
     * @param tableId
     * @param attributeNameOld
     * @return
     */
    String getRecordIds(String tableId, String viewId, String fieldName, String attributeNameOld);

    String synchronizePrivateProducts() throws Exception;

    /**
     * 获取飞书在线表格数据
     *
     * @param spreadSheetUrl 飞书在线表格URL
     * @param accessToken 访问令牌
     * @return 飞书表格响应数据
     */
    /**
     * 获取飞书在线表格数据
     *
     * @param spreadSheetUrl 飞书在线表格 URL
     * @param accessToken    访问令牌
     * @return 飞书表格响应数据
     */
    FeishuSheetResponse getFeishuSheetData(String spreadSheetUrl, String accessToken);

    /**
     * 获取飞书租户访问令牌
     *
     * @param appId     应用ID
     * @param appSecret 应用密钥
     * @return 租户访问令牌
     */
    String getInternalTenantAccessToken(String appId, String appSecret);

    /**
     * 获取飞书租户访问令牌（使用默认配置）
     *
     * @return 租户访问令牌
     */
    String getInternalTenantAccessToken();

    <T> FeishuSheetConvertResult<T> convertSheetDataToEntity(FeishuSheetResponse response, Class<T> clazz);

    public <T> FeishuSheetConvertResult<T> convertSheetDataToEntityNew(FeishuSheetResponse sheetResponse, Class<T> clazz);

    void insertSheetRecord(KolSysUserFeishuSheet feishuSheet, List<String> insertData);

    /**
     * @description: 批量更新飞书表格数据
     * @author: nqr
     * @date: 2025/9/16 10:01
     **/
    void updateValuesBatch(KolSysUserFeishuSheet feishuSheet, Map<String, Object> values);

    /**
     * @description: 修改飞书sheet值
     * type：
     * 1 删除；
     * 0:新增或编辑
     * @author: nqr
     * @date: 2025/9/16 14:07
     **/
    void insertOrUpdatePersonalTagsSheetBatch(int type, String spreadSheetType, List<String> insertData);
    void insertOrUpdatePersonalTagsSheet(int type, KolSysUserFeishuSheet feishuSheet,List<String> insertData);
}
