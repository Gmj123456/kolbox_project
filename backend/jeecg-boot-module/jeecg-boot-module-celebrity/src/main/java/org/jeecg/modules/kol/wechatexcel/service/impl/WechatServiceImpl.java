package org.jeecg.modules.kol.wechatexcel.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.jeecg.common.aspect.annotation.SkipOnProfile;
import org.jeecg.common.system.vo.StoreCelebrityPrivateExcelModel;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.RestUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.kol.entity.KolProduct;
import org.jeecg.modules.kol.model.KolTagsModel;
import org.jeecg.modules.kol.service.IKolProductService;
import org.jeecg.modules.kol.wechatexcel.model.Record;
import org.jeecg.modules.kol.wechatexcel.model.SmartSheetResponse;
import org.jeecg.modules.kol.wechatexcel.service.WechatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * @Author: nqr
 * @Date: 2025/8/25 11:31
 * @Description:
 **/
@Service
public class WechatServiceImpl implements WechatService {
    @Resource
    private RedisUtil redisUtil;

    private static final String BASE_API_URL = "https://qyapi.weixin.qq.com/cgi-bin";
    private static final String CORP_ID = "ww1b3a822b00578337";
    private static final String CORP_SECRET = "JeA2X3D-Pp5CG8MI54ezkF9BtScZuvSPR9plcIV25Hk";
    @Resource
    private IKolProductService kolProductService;
    @Autowired
    private Environment environment;

    @Override
    public String getAccessToken() {
        String url = String.format(BASE_API_URL + "/gettoken?corpid=%s&corpsecret=%s", CORP_ID, CORP_SECRET);
        if (redisUtil.hasKey(CORP_ID)) {
            return redisUtil.get(CORP_ID).toString();
        } else {
            try {
                JSONObject jsonObject = RestUtil.get(url);
                String accessToken = jsonObject.getString("access_token");
                redisUtil.set(CORP_ID, accessToken, 6000);
                return accessToken;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }
    }

    @Override
    public List<Record> getAllRecords(String docId, String sheetId, String recordId, JSONObject filterSpec) {
        List<Record> allData = new ArrayList<>();
        long offset = 0;

        while (true) {
            JSONObject requestBody = new JSONObject();
            requestBody.put("docid", docId);
            requestBody.put("sheet_id", sheetId);
            requestBody.put("offset", offset);
            requestBody.put("limit", 1000);
            if (oConvertUtils.isNotEmpty(recordId)) {
                requestBody.put("record_ids", new String[]{recordId});
            }
            if (filterSpec != null && !filterSpec.isEmpty()) {
                requestBody.put("filter_spec", filterSpec);
            }
            String url = BASE_API_URL + "/wedoc/smartsheet/get_records?access_token=" + getAccessToken();
            SmartSheetResponse response = getSmartSheetResponse(url, requestBody);

            if (response == null || response.getErrcode() != 0) {
                throw new RuntimeException("API 错误: " + response.getErrmsg());
            }
            List<Record> records = response.getRecords();
            records.forEach(record -> {
                record.getValues().put("recordId", record.getRecordId());
            });
            allData.addAll(records);
            // 分页判断
            if (!response.isHasMore()) {
                break;
            }
            offset = response.getNext();
        }
        return allData;
    }

    @Override
    @SkipOnProfile()
    public List<Record> createRecords(String docId, String sheetId, List<Map<String, Object>> records) {

        JSONObject requestBody = new JSONObject();
        requestBody.put("docid", docId);
        requestBody.put("sheet_id", sheetId);
        requestBody.put("records", records);
        ResponseEntity<SmartSheetResponse> response = RestUtil.postNative(BASE_API_URL + "/wedoc/smartsheet/add_records?access_token=" + getAccessToken(), null, requestBody, SmartSheetResponse.class);
        SmartSheetResponse smartSheetResponse = response.getBody();
        System.out.println(JSONObject.toJSONString(smartSheetResponse));
        return smartSheetResponse.getRecords();
    }

    @Override
    @SkipOnProfile()
    public void updateRecords(String docId, String sheetId, List<Map<String, Object>> updateRecords) {

        JSONObject requestBody = new JSONObject();

        requestBody.put("docid", docId);
        requestBody.put("sheet_id", sheetId);
        requestBody.put("key_type", "CELL_VALUE_KEY_TYPE_FIELD_TITLE");
        requestBody.put("records", updateRecords);

        String url = BASE_API_URL + "/wedoc/smartsheet/update_records?access_token=" + getAccessToken();

        // 发起请求
        ResponseEntity<SmartSheetResponse> responseEntity = RestUtil.postNative(url, null, requestBody, SmartSheetResponse.class);
        SmartSheetResponse response = responseEntity.getBody();
        System.out.println(JSONObject.toJSONString(response));
        if (response == null || response.getErrcode() != 0) {
            throw new RuntimeException("API 错误: " + response.getErrmsg());
        }
    }

    private static SmartSheetResponse getSmartSheetResponse(String url, JSONObject requestBody) {
        // 发起请求
        ResponseEntity<SmartSheetResponse> responseEntity = RestUtil.postNative(url, null, requestBody, SmartSheetResponse.class);
        SmartSheetResponse response = responseEntity.getBody();
        System.out.println(JSONObject.toJSONString(response));
        return response;
    }

    @Override
    @SkipOnProfile()
    public void removeRecords(String docId, String sheetId, String recordIds) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject requestBody = new JSONObject();

        requestBody.put("docid", docId);
        requestBody.put("sheet_id", sheetId);
        requestBody.put("record_ids", recordIds.split(","));

        String url = BASE_API_URL + "/wedoc/smartsheet/delete_records?access_token=" + getAccessToken();

        // 发起请求
        SmartSheetResponse response = getSmartSheetResponse(url, requestBody);

        if (response == null || response.getErrcode() != 0) {
            throw new RuntimeException("API 错误: " + response.getErrmsg());
        }
    }

    @Override
    public JSONObject createDocument(String docName, Integer docType) {
        RestTemplate restTemplate = new RestTemplate();

        // 请求头设置
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // 请求体
        JSONObject requestBody = new JSONObject();
        requestBody.put("doc_type", docType);
        requestBody.put("doc_name", docName);

        // 请求URL
        String url = BASE_API_URL + "/wedoc/create_doc?access_token=" + getAccessToken();

        // 处理响应
        return RestUtil.post(url, requestBody);
    }

    @Override
    public JSONObject getSheet(String docId) {
        String url = BASE_API_URL + "/wedoc/get_sheet?access_token=" + getAccessToken();
        JSONObject requestBody = new JSONObject();
        requestBody.put("docid", docId);
        return RestUtil.post(url, requestBody);
    }

    @Override
    public List<StoreCelebrityPrivateExcelModel> convertToStoreCelebrityModel(List<Map<String, Object>> records) {
        List<StoreCelebrityPrivateExcelModel> resultList = new ArrayList<>();

        if (records == null || records.isEmpty()) {
            return resultList;
        }

        for (Map<String, Object> record : records) {
            StoreCelebrityPrivateExcelModel model = convertValuesToEntity(record);
            resultList.add(model);
        }

        return resultList;
    }

    public StoreCelebrityPrivateExcelModel convertValuesToEntity(Map<String, Object> values) {
        StoreCelebrityPrivateExcelModel model = new StoreCelebrityPrivateExcelModel();

        // 处理内容1 (videoTags)
        if (values.containsKey("内容1")) {
            List<Map<String, Object>> contentList = (List<Map<String, Object>>) values.get("内容1");
            if (contentList != null && !contentList.isEmpty()) {
                model.setVideoTags((String) contentList.get(0).get("text"));
            } else {
                model.setVideoTags("");
            }
        } else {
            model.setVideoTags("");
        }

        // 处理内容2 (videoTags2)
        if (values.containsKey("内容2")) {
            List<Map<String, Object>> contentList = (List<Map<String, Object>>) values.get("内容2");
            if (contentList != null && !contentList.isEmpty()) {
                model.setVideoTags2((String) contentList.get(0).get("text"));
            } else {
                model.setVideoTags2("");
            }
        } else {
            model.setVideoTags2("");
        }

        // 处理内容3 (videoTags3)
        if (values.containsKey("内容3")) {
            List<Map<String, Object>> contentList = (List<Map<String, Object>>) values.get("内容3");
            if (contentList != null && !contentList.isEmpty()) {
                model.setVideoTags3((String) contentList.get(0).get("text"));
            } else {
                model.setVideoTags3("");
            }
        } else {
            model.setVideoTags3("");
        }


        // 处理国家 (countryName)
        if (values.containsKey("国家")) {
            List<Map<String, Object>> countryList = (List<Map<String, Object>>) values.get("国家");
            if (countryList != null && !countryList.isEmpty()) {
                model.setCountryName((String) countryList.get(0).get("text"));
            } else {
                model.setCountryName("");
            }
        } else {
            model.setCountryName("");
        }

        // 处理备注 (remark)
        if (values.containsKey("备注")) {
            List<Map<String, Object>> remarkList = (List<Map<String, Object>>) values.get("备注");
            if (remarkList != null && !remarkList.isEmpty()) {
                model.setRemark((String) remarkList.get(0).get("text"));
            } else {
                model.setRemark("");
            }
        } else {
            model.setRemark("");
        }

        // 处理平台 (platformTypeText)
        if (values.containsKey("平台")) {
            List<Map<String, Object>> platformList = (List<Map<String, Object>>) values.get("平台");
            if (platformList != null && !platformList.isEmpty()) {
                model.setPlatformTypeText((String) platformList.get(0).get("text"));
            } else {
                model.setPlatformTypeText("");
            }
        } else {
            model.setPlatformTypeText("");
        }

        // 处理建联邮箱 (celebrityContactEmail)
        if (values.containsKey("建联邮箱")) {
            List<Map<String, Object>> emailList = (List<Map<String, Object>>) values.get("建联邮箱");
            if (emailList != null && !emailList.isEmpty()) {
                Map<String, Object> emailMap = emailList.get(0);
                model.setCelebrityContactEmail((String) emailMap.get("text"));
            } else {
                model.setCelebrityContactEmail("");
            }
        } else {
            model.setCelebrityContactEmail("");
        }

        // 处理性别 (genderText)
        if (values.containsKey("性别")) {
            List<Map<String, Object>> genderList = (List<Map<String, Object>>) values.get("性别");
            if (genderList != null && !genderList.isEmpty()) {
                model.setGenderText((String) genderList.get(0).get("text"));
            } else {
                model.setGenderText("");
            }
        } else {
            model.setGenderText("");
        }

        // 处理是否同步 (isUpdate)
        if (values.containsKey("是否同步")) {
            List<Map<String, Object>> syncList = (List<Map<String, Object>>) values.get("是否同步");
            if (syncList != null && !syncList.isEmpty()) {
                model.setIsUpdate((String) syncList.get(0).get("text"));
            } else {
                model.setIsUpdate("");
            }
        } else {
            model.setIsUpdate("");
        }

        // 处理签约费用1 (contractAmountStr)
        if (values.containsKey("签约费用1")) {
            Object fee1 = values.get("签约费用1");
            if (fee1 != null) {
                model.setContractAmountStr(fee1.toString());
            } else {
                model.setContractAmountStr("");
            }
        } else {
            model.setContractAmountStr("");
            model.setContractAmount(null);
        }

        // 处理签约费用2 (contractAmountStr2)
        if (values.containsKey("签约费用2")) {
            Object fee2 = values.get("签约费用2");
            if (fee2 != null) {
                model.setContractAmountStr2(fee2.toString());
            } else {
                model.setContractAmountStr2("");
                model.setContractAmount2(null);
            }
        } else {
            model.setContractAmountStr2("");
            model.setContractAmount2(null);
        }

        // 处理签约费用3 (contractAmountStr3)
        if (values.containsKey("签约费用3")) {
            Object fee3 = values.get("签约费用3");
            if (fee3 != null) {
                model.setContractAmountStr3(fee3.toString());
            } else {
                model.setContractAmountStr3("");
                model.setContractAmount3(null);
            }
        } else {
            model.setContractAmountStr3("");
            model.setContractAmount3(null);
        }

        // 处理网红邮箱 (email)
        if (values.containsKey("网红邮箱")) {
            Object email = values.get("网红邮箱");
            if (email != null) {
                model.setEmail(email.toString());
            } else {
                model.setEmail("");
            }
        } else {
            model.setEmail("");
        }

        // 处理账号 (account)
        if (values.containsKey("账号")) {
            List<Map<String, Object>> accountList = (List<Map<String, Object>>) values.get("账号");
            if (accountList != null && !accountList.isEmpty()) {
                model.setAccount((String) accountList.get(0).get("text"));
            } else {
                model.setAccount("");
            }
        } else {
            model.setAccount("");
        }
        // 处理顾问
        if (values.containsKey("顾问")) {
            List<Map<String, Object>> contactList = (List<Map<String, Object>>) values.get("顾问");
            if (contactList != null && !contactList.isEmpty()) {
                model.setUserName((String) contactList.get(0).get("text"));
            } else {
                model.setUserName("");
            }
        } else {
            model.setAccount("");
        }
        // 处理个性化标签
        if (values.containsKey("个性化标签")) {
            List<Map<String, Object>> personalTags = (List<Map<String, Object>>) values.get("个性化标签");
            if (personalTags != null && !personalTags.isEmpty()) {
                personalTags.forEach(personalTag -> {
                    model.setPersonalTags((oConvertUtils.isNotEmpty(model.getPersonalTags()) ? model.getPersonalTags() + "," : "") + personalTag.get("text"));
                });
            } else {
                model.setPersonalTags("");
            }
        } else {
            model.setAccount("");
        }
        if (values.containsKey("recordId")) {
            model.setRecordId((String) values.get("recordId"));
        }

        return model;
    }

    @Override
    @SkipOnProfile()
    public void updateFields(String docId, String sheetId, List<Map<String, Object>> records) {
        /**
         * records 结构如下：
         * Collections.singletonList(new HashMap<String, Object>() {{
         *             put("field_id", "f45qGP");
         *             put("field_title", "内容2");
         *             put("field_type", "FIELD_TYPE_SINGLE_SELECT");
         *             put("property_single_select", new HashMap<String, Object>() {{
         *                 put("is_multiple", false);
         *                 put("is_quick_add", false);
         *                 put("options", new ArrayList<Map<String, String>>() {{
         *                     add(Collections.singletonMap("text", "TK视频"));
         *                     add(Collections.singletonMap("text", "TK直播"));
         *                     add(Collections.singletonMap("text", "YT插播1-3"));
         *                     add(Collections.singletonMap("text", "YT专题5-10"));
         *                     add(Collections.singletonMap("text", "YTshorts"));
         *                     add(Collections.singletonMap("text", "IG Reel"));
         *                     add(Collections.singletonMap("text", "IG Story"));
         *                     add(Collections.singletonMap("text", "IG Post"));
         *                 }});
         *             }});
         *         }})
         */
        String url = BASE_API_URL + "/wedoc/smartsheet/update_fields?access_token=" + getAccessToken();
        JSONObject requestBody = new JSONObject();
        requestBody.put("docid", "dcVVw9r93yftxim-Z4-mF7MzUNCxv3cV_BW5eF8YjHHM4-I0OGsNOLhkWrpuFlhfkRSsJFdx-KbtNOOJncmiwZVQ");
        requestBody.put("sheet_id", "q979lj");
        requestBody.put("fields", records);
        JSONObject jsonObject = RestUtil.post(url, requestBody);
        System.out.println(jsonObject);
    }

    @Override
    public List<KolTagsModel> convertToTagsModel(List<Map<String, Object>> list) {
        return getKolTagsModel(list);
    }

    public List<KolTagsModel> getKolTagsModel(List<Map<String, Object>> records) {
        List<KolTagsModel> resultList = new ArrayList<>();

        if (records == null || records.isEmpty()) {
            return resultList;
        }

        for (Map<String, Object> record : records) {
            KolTagsModel model = convertToTagEntity(record);

            if ((oConvertUtils.isEmpty(model.getIsUpdate()) || "否".equals(model.getIsUpdate())) && oConvertUtils.isNotEmpty(model.getTagName().trim())) {
                resultList.add(model);
            }
        }

        return resultList;
    }

    private KolTagsModel convertToTagEntity(Map<String, Object> values) {
        KolTagsModel model = new KolTagsModel();

        // 定义字段映射，键为 JSON 字段名，值为对应的 setter 方法和字段类型
        Map<String, BiConsumer<KolTagsModel, String>> stringFieldMap = new HashMap<>();
        stringFieldMap.put("标签", KolTagsModel::setTagName);
        stringFieldMap.put("达人类型1", KolTagsModel::setInfluencerType);
        stringFieldMap.put("达人类型2", KolTagsModel::setInfluencerType2);
        stringFieldMap.put("达人类型3", KolTagsModel::setInfluencerType3);
        stringFieldMap.put("达人类型4", KolTagsModel::setInfluencerType4);
        stringFieldMap.put("达人类型5", KolTagsModel::setInfluencerType5);
        stringFieldMap.put("强制更新", KolTagsModel::setChangeStatus);
        stringFieldMap.put("是否同步", KolTagsModel::setIsUpdate);
        stringFieldMap.put("视频链接", KolTagsModel::setVideoUrl);
        stringFieldMap.put("标签类型", KolTagsModel::setTagTypeStr);

        // 处理字符串类型的字段
        stringFieldMap.forEach((key, setter) -> {
            List<Map<String, Object>> list = getListFromValues(values, key);
            String text = (list != null && !list.isEmpty()) ? (String) list.get(0).get("text") : "";
            setter.accept(model, text.trim());
        });
        // 处理产品品牌
        List<String> productList = (List<String>) values.get("产品");
        if (!productList.isEmpty()) {
            String recordId = productList.get(0);
            KolProduct kolProduct = kolProductService.lambdaQuery().eq(KolProduct::getRecordId, recordId).one();
            if (kolProduct != null) {
                model.setProductId(kolProduct.getId());
                model.setProductName(kolProduct.getProductName());
                model.setBrandId(kolProduct.getBrandId());
                model.setBrandName(kolProduct.getBrandName());
            }
        } else {
            model.setProductId(null);
            model.setProductName(null);
        }
   /*     List<String> attributeList = (List<String>) values.get("达人类型");
        if (attributeList != null && !attributeList.isEmpty()) {
            List<KolAttribute> kolAttributes = attributeService.lambdaQuery().in(KolAttribute::getRecordId, attributeList).list();
            String attributeName = kolAttributes.stream().map(KolAttribute::getAttributeName).distinct().collect(Collectors.joining(","));
            model.setInfluencerType(attributeName);
        }*/
        // 处理 recordId
        model.setRecordId(values.getOrDefault("recordId", "").toString());

        return model;
    }

    // 辅助方法：获取 JSON 中的 List<Map> 数据
    private List<Map<String, Object>> getListFromValues(Map<String, Object> values, String key) {
        return values.containsKey(key) ? (List<Map<String, Object>>) values.get(key) : null;
    }
}
