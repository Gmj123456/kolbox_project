package org.jeecg.modules.kol.wechatexcel.service;

import com.alibaba.fastjson.JSONObject;
import org.jeecg.common.system.vo.StoreCelebrityPrivateExcelModel;
import org.jeecg.modules.kol.model.KolTagsModel;
import org.jeecg.modules.kol.wechatexcel.model.Record;

import java.util.List;
import java.util.Map;

public interface WechatService {
    /**
     * @description: 获取企微access_token
     * @author: nqr
     * @date: 2025/8/25 14:00
     **/
    String getAccessToken();

    /**
     * @param docId      文档id
     * @param sheetId    子表id
     * @param recordId   记录id
     * @param filterSpec 过滤条件
     *                   {
     *                   "conjunction": "CONJUNCTION_AND",
     *                   "conditions": [
     *                   {
     *                   "field_id": "fPFo18",
     *                   "field_type": "FIELD_TYPE_TEXT",
     *                   "operator": "OPERATOR_IS",
     *                   "string_value": {
     *                   "value": [
     *                   "测试标题1"
     *                   ]
     *                   }
     *                   }
     *                   ]
     *                   }
     * @description: 获取表格全部数据
     * @author: nqr
     * @date: 2025/8/25 14:00
     **/
    List<Record> getAllRecords(String docId, String sheetId, String recordId, JSONObject filterSpec);

    /**
     * @param docId   文档id
     * @param sheetId 子表id
     * @param records 记录数据
     *                {
     *                "records": [
     *                {
     *                "values": {
     *                "个性化标签": [
     *                {
     *                "type": "text",
     *                "text": "测试标题1"
     *                }
     *                ]
     *                }
     *                }
     *                ]
     *                }
     * @description: 创建记录
     * @author: nqr
     * @date: 2025/8/25 14:56
     **/
    List<Record>  createRecords(String docId, String sheetId, List<Map<String, Object>> records);

    /**
     * @param docId         文档id
     * @param sheetId       子表id
     * @param updateRecords 记录更新数据
     *                      [
     *                      {
     *                      "record_id": "rpS0P9",
     *                      "values": {
     *                      "是否同步": [
     *                      {
     *                      "text": "否"
     *                      }
     *                      ]
     *                      }
     *                      }]
     * @description: 更新记录
     * @author: nqr
     * @date: 2025/8/25 14:56
     **/
    void updateRecords(String docId, String sheetId, List<Map<String, Object>> updateRecords);

    /**
     * @description: 删除记录
     * @author: nqr
     * @date: 2025/8/25 14:56
     **/
    void removeRecords(String docId, String sheetId, String recordIds);

    /**
     * @description: 创建文档
     * @author: nqr
     * @date: 2025/8/29 15:02
     **/
    JSONObject createDocument(String docName, Integer docType);

    /**
     * @description:查询子表
     * @author: nqr
     * @date: 2025/8/29 15:54
     **/
    JSONObject getSheet(String docId);

    /**
     * @description: 处理私有网红模板数据
     * @author: nqr
     * @date: 2025/8/29 15:00
     **/
    List<StoreCelebrityPrivateExcelModel> convertToStoreCelebrityModel(List<Map<String, Object>> records);

    /**
     * @param docId   文档id
     * @param sheetId 子表id
     * @param records 字段更新数据
     *                [
     *                {
     *                "field_id": "fVgDhL",
     *                "field_title": "个性化标签",
     *                "field_type": "FIELD_TYPE_SELECT",
     *                "property_select": {
     *                "is_multiple": true,
     *                "is_quick_add": false,
     *                "options": [
     *                { "text": "宠物/郊区" }
     *                ]
     *                }
     *                }
     *                ]
     * @description: 更新字段
     * @author: nqr
     * @date: 2025/8/30 11:37
     **/
    void updateFields(String docId, String sheetId, List<Map<String, Object>> records);


    /**
     * @description: 处理标签数据
     * @author: nqr
     * @date: 2025/9/5 15:14
     **/
    List<KolTagsModel> convertToTagsModel(List<Map<String, Object>> list);
}
