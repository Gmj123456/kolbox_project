package org.jeecg.modules.feishu.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lark.oapi.Client;
import com.lark.oapi.core.utils.Jsons;
import com.lark.oapi.service.auth.v3.model.InternalTenantAccessTokenReq;
import com.lark.oapi.service.auth.v3.model.InternalTenantAccessTokenReqBody;
import com.lark.oapi.service.auth.v3.model.InternalTenantAccessTokenResp;
import com.lark.oapi.service.bitable.v1.model.*;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.SkipOnProfile;
import org.jeecg.common.util.RestUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.feishu.config.FeishuAppConfig;
import org.jeecg.modules.feishu.model.FeishuSheetConvertResult;
import org.jeecg.modules.feishu.model.FeishuSheetResponse;
import org.jeecg.modules.feishu.model.Fields;
import org.jeecg.modules.feishu.model.Record;
import org.jeecg.modules.feishu.service.IFeishuService;
import org.jeecg.modules.kol.entity.KolSysUserFeishuSheet;
import org.jeecg.modules.kol.model.KolTagsModel;
import org.jeecg.modules.kol.service.IKolSysUserFeishuSheetService;
import org.jeecg.modules.kol.service.IKolTagsService;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.jeecg.common.constant.CommonConstant.SC_OK_10001;
import static org.jeecg.common.constant.CommonConstant.SC_OK_200;

/**
 * @Author: nqr
 * @Date: 2025/7/17 16:55
 * @Description:
 **/
@Slf4j
@Service
public class FeishuServiceImpl implements IFeishuService {
    @Autowired
    private FeishuAppConfig feishuAppConfig;
    @Autowired
    @Lazy
    private IKolTagsService kolTagsService;
    @Autowired
    private Client client;
    @Resource
    private IKolSysUserFeishuSheetService feishuSheetService;

    @Override
    public String synchronizeData(Integer platformType) throws Exception {
        // 构建client
        String pageToken = "";
        List<AppTableRecord> records = new ArrayList<>();
        while (pageToken != null) {
            // 创建请求对象
            SearchAppTableRecordReq req = SearchAppTableRecordReq.newBuilder()
                    .appToken(feishuAppConfig.getAppToken())
                    .tableId(feishuAppConfig.getTableId())
                    .pageToken(pageToken)
                    .pageSize(500)
                    .searchAppTableRecordReqBody(SearchAppTableRecordReqBody.newBuilder()
                            .viewId(feishuAppConfig.getViewId())
                            .sort(new Sort[]{
                                    Sort.newBuilder()
                                            .fieldName("标签")
                                            .desc(true)
                                            .build()
                            })
                            .filter(FilterInfo.newBuilder()
                                    .conjunction("and")
                                    .conditions(new Condition[]{
                                            Condition.newBuilder()
                                                    .fieldName("是否同步")
                                                    .operator("is")
                                                    .value(new String[]{
                                                            "否"
                                                    })
                                                    .build(),
                                            Condition.newBuilder()
                                                    .fieldName("平台")
                                                    .operator("is")
                                                    .value(new String[]{
                                                            platformType == 0 ? "IG" : platformType.equals(1) ? "YT" : "TK"
                                                    })
                                                    .build()
                                    })
                                    .build())
                            .automaticFields(false)
                            .build())
                    .build();
            // 发起请求
            SearchAppTableRecordResp resp = client.bitable().v1().appTableRecord().search(req);

            // 处理服务端错误
            if (!resp.success()) {
                System.out.println();
                break;
            }
            SearchAppTableRecordRespBody data = resp.getData();
            pageToken = data.getHasMore() ? data.getPageToken() : null;
            // 业务数据处理
            System.out.println(Jsons.DEFAULT.toJson(data));
            AppTableRecord[] items = data.getItems();
            records.addAll(Arrays.stream(items).filter(item -> item.getFields() != null && !item.getFields().isEmpty()).collect(Collectors.toList()));
        }
        String data = JSON.toJSONString(records);
        System.out.println(data);
        return data;
    }

    @Override
    @SkipOnProfile()
    public void batchUpdateRecords(String appToken, String tableId, AppTableRecord[] records) {
        // 创建请求对象
        BatchUpdateAppTableRecordReq req = BatchUpdateAppTableRecordReq.newBuilder()
                .appToken(appToken)
                .tableId(tableId)
                .batchUpdateAppTableRecordReqBody(BatchUpdateAppTableRecordReqBody.newBuilder()
                        .records(records)
                        .build())
                .build();

        try {
            // 发起请求
            BatchUpdateAppTableRecordResp resp = client.bitable().v1().appTableRecord().batchUpdate(req);

            // 处理服务端错误
            if (!resp.success()) {
                System.out.println(JSON.toJSONString(resp));
            }

            // 业务数据处理
            System.out.println(Jsons.DEFAULT.toJson(resp.getData()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @SkipOnProfile()
    public void batchDeleteRecords(String appToken, String tableId, String recordIds) {
        String[] recordIdsArr = recordIds.split(",");
        // 创建请求对象
        BatchDeleteAppTableRecordReq req = BatchDeleteAppTableRecordReq.newBuilder()
                .appToken(appToken)
                .tableId(tableId)
                .batchDeleteAppTableRecordReqBody(BatchDeleteAppTableRecordReqBody.newBuilder()
                        .records(recordIdsArr)
                        .build())
                .build();

        try {
            // 发起请求
            BatchDeleteAppTableRecordResp resp = client.bitable().v1().appTableRecord().batchDelete(req);
            if (!resp.success()) {
                System.out.println(resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Result<?> extracted(Integer platformType, String data) throws IOException {
        // 使用 Jackson 解析 JSON
        ObjectMapper objectMapper = new ObjectMapper();
        List<Record> records = objectMapper.readValue(data, objectMapper.getTypeFactory().constructCollectionType(List.class, Record.class));
        List<String> recordIds = records.stream().map(Record::getRecordId).collect(Collectors.toList());
        // 过滤掉 fields 为空的记录并打印结果
        records.stream().filter(record -> record.getFields() != null && !oConvertUtils.isAllFieldNull(record.getFields())).forEach(record -> {
            Fields fields = record.getFields();
            System.out.println("标签: " + fields.getTagName());
            System.out.println("平台: " + fields.getPlatformTypeStr());
            System.out.println("标签类型: " + fields.getTagTypeStr());
            System.out.println("达人类型: " + fields.getInfluencerType());
            System.out.println("受众: " + fields.getAudience());
            System.out.println("使用场景: " + fields.getUsageScenarios());
            System.out.println("强制更新: " + fields.getChangeStatus());
            System.out.println("视频链接: " + fields.getVideoUrl());
            System.out.println("产品: " + fields.getProductImportName());
            System.out.println("---");
        });

        List<KolTagsModel> kolTagsModels = records.stream().filter(record -> record.getFields() != null && !oConvertUtils.isAllFieldNull(record.getFields())).map(record -> {
            Fields fields = record.getFields();
            KolTagsModel kolTagsModel = new KolTagsModel();
            kolTagsModel.setPlatformType(platformType);
            kolTagsModel.setInfluencerType(fields.getInfluencerType());
            kolTagsModel.setAudience(fields.getAudience());
            kolTagsModel.setUsageScenarios(fields.getUsageScenarios());
            kolTagsModel.setTagName(fields.getTagName());
            kolTagsModel.setChangeStatus(fields.getChangeStatus());
            kolTagsModel.setVideoUrl(fields.getVideoUrl());
            kolTagsModel.setTagType(getTagType(fields.getTagTypeStr()));
            kolTagsModel.setRecordId(record.getRecordId());
            kolTagsModel.setProductImportName(fields.getProductImportName());
            return kolTagsModel;
        }).collect(Collectors.toList());
        if (kolTagsModels.isEmpty()) {
            return Result.ok("没有需要同步的标签数据");
        }
        Result<List<String>> resultNew = new Result<>();
        Result<?> result = kolTagsService.importTags(kolTagsModels, platformType, "", 0);
        BeanUtils.copyProperties(result, resultNew);
        if (Objects.equals(result.getCode(), SC_OK_200) || Objects.equals(result.getCode(), SC_OK_10001)) {
            List<String> messageList = (List<String>) result.getResult();
            List<String> resultMsg = new ArrayList<>();
            if (messageList != null && !messageList.isEmpty()) {
                for (String msg : messageList) {
                    String[] split = msg.split(",");
                    if (split.length > 1) {
                        String recordId = split[1];
                        recordIds.remove(recordId);
                        resultMsg.add(split[0]);
                        try {
                            CompletableFuture.runAsync(() -> updateRecord(split, recordId));
                        } catch (Exception ignored) {
                        }
                    } else {
                        resultMsg.add(msg);
                    }
                }
            }
            resultNew.setResult(resultMsg);
            if (!recordIds.isEmpty()) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("是否同步", "是");
                map.put("异常信息", "");
                AppTableRecord[] recordArr = new AppTableRecord[recordIds.size()];
                for (int i = 0; i < recordIds.size(); i++) {
                    recordArr[i] = AppTableRecord.newBuilder()
                            .recordId(recordIds.get(i))
                            .fields(map)
                            .build();
                }
                try {
                    CompletableFuture.runAsync(() -> {
                        batchUpdateRecords(feishuAppConfig.getAppToken(), feishuAppConfig.getTableId(), recordArr);
                    });
                } catch (Exception ignored) {
                }
            }
        }
        return resultNew;
    }

    @SkipOnProfile()
    private void updateRecord(String[] split, String recordId) {
        HashMap<String, Object> errorMap = new HashMap<>();
        errorMap.put("异常信息", split[0]);
        AppTableRecord[] recordArr = new AppTableRecord[1];
        recordArr[0] = AppTableRecord.newBuilder()
                .recordId(recordId)
                .fields(errorMap)
                .build();
        batchUpdateRecords(feishuAppConfig.getAppToken(), feishuAppConfig.getTableId(), recordArr);
    }

    private Integer getTagType(String tagTypeStr) {
        Integer tagType = null;
        if (oConvertUtils.isEmpty(tagTypeStr)) {
            return null;
        }
        switch (tagTypeStr) {
            case "标签":
                tagType = 0;
                break;
            case "关键词":
                tagType = 1;
                break;
            case "种子视频":
                tagType = 2;
                break;
            case "种子账号":
                tagType = 3;
                break;
            case "关注列表":
                tagType = 4;
                break;
            default:
                break;
        }
        return tagType;
    }

    @Override
    public String getRecordIds(String tableId, String viewId, String fieldName, String name) {
// 创建请求对象
        SearchAppTableRecordReq req = SearchAppTableRecordReq.newBuilder()
                .appToken(feishuAppConfig.getAppToken())
                .tableId(tableId)
                .pageSize(20)
                .searchAppTableRecordReqBody(SearchAppTableRecordReqBody.newBuilder()
                        .viewId(viewId)
                        .filter(FilterInfo.newBuilder()
                                .conjunction("and")
                                .conditions(new Condition[]{
                                        Condition.newBuilder()
                                                .fieldName(fieldName)
                                                .operator("is")
                                                .value(new String[]{name})
                                                .build()
                                })
                                .build())
                        .automaticFields(false)
                        .build())
                .build();

        try {
            // 发起请求
            SearchAppTableRecordResp resp = client.bitable().v1().appTableRecord().search(req);

            // 处理服务端错误
            if (!resp.success()) {
                System.out.println(JSON.toJSONString(resp));
            }
            // 业务数据处理
            System.out.println(Jsons.DEFAULT.toJson(resp.getData()));
            return resp.getData() != null ? resp.getData().getItems()[0].getRecordId() : "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    @SkipOnProfile()
    public void insertRecord(String appToken, String tableId, HashMap<String, Object> map) throws Exception {
        // 创建请求对象
        CreateAppTableRecordReq req = CreateAppTableRecordReq.newBuilder()
                .appToken(appToken)
                .tableId(tableId)
                .appTableRecord(AppTableRecord.newBuilder()
                        .fields(map)
                        .build())
                .build();
        // 发起请求
        CreateAppTableRecordResp resp = client.bitable().v1().appTableRecord().create(req);

        // 处理服务端错误
        if (!resp.success()) {
            System.out.println(JSON.toJSONString(resp));
            return;
        }
        // 业务数据处理
        System.out.println(Jsons.DEFAULT.toJson(resp.getData()));
    }

    @Override
    public String synchronizePrivateProducts() throws Exception {
        // 构建client
        String pageToken = "";
        List<AppTableRecord> records = new ArrayList<>();
        while (pageToken != null) {
            // 创建请求对象
            SearchAppTableRecordReq req = SearchAppTableRecordReq.newBuilder()
                    .appToken(feishuAppConfig.getPrivateProductAppToken())
                    .tableId(feishuAppConfig.getPrivateProductTableId())
                    .pageToken(pageToken)
                    .pageSize(500)
                    .searchAppTableRecordReqBody(SearchAppTableRecordReqBody.newBuilder()
                            .viewId(feishuAppConfig.getProductViewId())
                            .sort(new Sort[]{
                                    Sort.newBuilder()
                                            .fieldName("网红名称")
                                            .desc(true)
                                            .build()
                            })
                            .filter(FilterInfo.newBuilder()
                                    .conjunction("and")
                                    .conditions(new Condition[]{
                                            Condition.newBuilder()
                                                    .fieldName("是否同步")
                                                    .operator("is")
                                                    .value(new String[]{
                                                            "否"
                                                    })
                                                    .build()
                                    })
                                    .build())
                            .automaticFields(false)
                            .build())
                    .build();
            // 发起请求
            SearchAppTableRecordResp resp = client.bitable().v1().appTableRecord().search(req);

            // 处理服务端错误
            if (!resp.success()) {
                System.out.println();
                break;
            }
            SearchAppTableRecordRespBody data = resp.getData();
            pageToken = data.getHasMore() ? data.getPageToken() : null;
            // 业务数据处理
            System.out.println(Jsons.DEFAULT.toJson(data));
            AppTableRecord[] items = data.getItems();
            records.addAll(Arrays.stream(items).filter(item -> item.getFields() != null && !item.getFields().isEmpty()).collect(Collectors.toList()));
        }
        String data = JSON.toJSONString(records);
        System.out.println(data);
        return data;
    }

    @Override
    public FeishuSheetResponse getFeishuSheetData(String spreadSheetUrl, String accessToken) {
        try {
            log.info("开始获取飞书在线表格数据，URL: {}", spreadSheetUrl);

            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (StringUtils.hasText(accessToken)) {
                headers.set("Authorization", "Bearer " + accessToken);
            } else {
                log.warn("未提供 accessToken，尝试直接访问");
            }

            // 发送 GET 请求
            ResponseEntity<String> responseEntity = RestUtil.request(
                    spreadSheetUrl, HttpMethod.GET, headers, null, null, String.class);

            // 检查 HTTP 状态码
            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                log.error("HTTP 请求失败，状态码: {}", responseEntity.getStatusCodeValue());
                return null;
            }

            // 获取响应内容
            String responseBody = responseEntity.getBody();
            log.debug("原始响应内容: {}", responseBody);
            if (responseBody == null || responseBody.trim().isEmpty()) {
                log.error("响应内容为空");
                return null;
            }

            // 解析 JSON 响应
            JSONObject response;
            try {
                response = JSONObject.parseObject(responseBody);
            } catch (Exception e) {
                log.error("解析 JSON 失败，响应内容: {}", responseBody, e);
                return null;
            }

            // 检查飞书 API 的错误码
            if (response.containsKey("code")) {
                Integer code = response.getInteger("code");
                if (code != null && code != 0) {
                    String msg = response.getString("msg");
                    log.error("飞书 API 返回错误，错误码: {}, 错误信息: {}", code, msg);
                    switch (code) {
                        case 99991663:
                            log.error("建议：检查 accessToken 是否有效且未过期");
                            break;
                        case 99991661:
                            log.error("建议：检查应用是否有访问该表格的权限");
                            break;
                        case 99991400:
                            log.error("建议：检查请求参数是否正确");
                            break;
                        default:
                            log.error("建议：查看飞书开放平台文档获取更多信息");
                            break;
                    }
                    return null;
                }
            }

            // 将 JSON 转换为 FeishuSheetResponse 对象
            ObjectMapper objectMapper = new ObjectMapper();
            FeishuSheetResponse feishuResponse;
            try {
                feishuResponse = objectMapper.readValue(response.toJSONString(), FeishuSheetResponse.class);
                log.debug("成功将 JSON 响应转换为 FeishuSheetResponse 对象");
            } catch (Exception e) {
                log.error("转换 JSON 响应为 FeishuSheetResponse 对象失败", e);
                log.error("原始 JSON 结构: {}", response.toJSONString());
                return null;
            }

            // 检查响应数据
            if (feishuResponse.getData() == null) {
                log.warn("飞书 API 响应中没有 data 字段");
                return feishuResponse;
            }

            if (feishuResponse.getData().getValueRanges() == null ||
                    feishuResponse.getData().getValueRanges().isEmpty()) {
                log.warn("飞书表格数据为空或没有数据范围");
            } else {
                int totalRows = 0;
                for (FeishuSheetResponse.ValueRange valueRange : feishuResponse.getData().getValueRanges()) {
                    if (valueRange.getValues() != null) {
                        totalRows += valueRange.getValues().size();
                    }
                }
                log.info("成功获取飞书表格数据，共 {} 个值范围，总行数: {}",
                        feishuResponse.getData().getValueRanges().size(), totalRows);
            }

            return feishuResponse;

        } catch (Exception e) {
            log.error("获取飞书表格数据失败", e);
            return null;
        }
    }


    @Override
    public String getInternalTenantAccessToken(String appId, String appSecret) {
        try {
            log.info("开始获取飞书租户访问令牌，appId: {}", appId);

            // 创建请求对象（参考飞书官方示例）
            InternalTenantAccessTokenReq req = InternalTenantAccessTokenReq.newBuilder()
                    .internalTenantAccessTokenReqBody(InternalTenantAccessTokenReqBody.newBuilder()
                            .appId(appId)
                            .appSecret(appSecret)
                            .build())
                    .build();

            // 发起请求
            InternalTenantAccessTokenResp resp = client.auth().v3().tenantAccessToken().internal(req);

            // 处理服务端错误（参考飞书官方示例的错误处理方式）
            // 处理服务端错误
            if (!resp.success()) {
                System.out.println(String.format("code:%s,msg:%s,reqId:%s, resp:%s", resp.getCode(), resp.getMsg(), resp.getRequestId(), JSON.toJSONString(new String(resp.getRawResponse().getBody(), StandardCharsets.UTF_8))));
                return null;
            }
            String accessToken = JSON.parseObject(new String(resp.getRawResponse().getBody(), StandardCharsets.UTF_8)).getString("tenant_access_token");
            System.out.println(accessToken);
            return accessToken;

        } catch (Exception e) {
            log.error("获取飞书tenantAccessToken异常", e);
            return null;
        }
    }

    @Override
    public String getInternalTenantAccessToken() {
        // 使用配置中的默认appId和appSecret
        String appId = feishuAppConfig.getAppId();
        String appSecret = feishuAppConfig.getAppSecret();

        if (!StringUtils.hasText(appId) || !StringUtils.hasText(appSecret)) {
            log.error("飞书应用配置不完整，请检查appId和appSecret配置");
            return null;
        }

        return getInternalTenantAccessToken(appId, appSecret);
    }


    @Override
    public <T> FeishuSheetConvertResult<T> convertSheetDataToEntity(FeishuSheetResponse response, Class<T> clazz) {
        FeishuSheetConvertResult<T> result = new FeishuSheetConvertResult<>();
        List<T> entityList = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();
        int successCount = 0;
        int errorCount = 0;

        try {
            log.info("开始转换飞书表格数据为实体对象，目标类型: {}", clazz.getSimpleName());

            if (response == null || response.getData() == null ||
                    CollectionUtils.isEmpty(response.getData().getValueRanges())) {
                log.warn("飞书表格响应数据为空");
                result.setEntityList(entityList);
                result.setSuccessCount(0);
                result.setErrorCount(0);
                result.setErrorMessages(errorMessages);
                result.setTotalRows(0);
                return result;
            }

            // 获取第一个值范围（通常表格数据在第一个范围中）
            FeishuSheetResponse.ValueRange valueRange = response.getData().getValueRanges().get(0);
            List<List<Object>> values = valueRange.getValues();

            if (CollectionUtils.isEmpty(values)) {
                log.warn("表格值数据为空");
                result.setEntityList(entityList);
                result.setSuccessCount(0);
                result.setErrorCount(0);
                result.setErrorMessages(errorMessages);
                result.setTotalRows(0);
                return result;
            }

            // 第一行作为表头
            List<Object> headers = values.get(0);
            List<String> headerList = new ArrayList<>();
            Map<String, String> headerMapping = new HashMap<>();

            // 处理表头
            for (int i = 0; i < headers.size(); i++) {
                String header = headers.get(i) != null ? headers.get(i).toString().trim() : "";
                headerList.add(header);
                headerMapping.put(String.valueOf(i), header);
            }

            result.setOriginalHeaders(headerList);
            result.setHeaderMapping(headerMapping);
            result.setTotalRows(values.size() - 1); // 除去表头行

            // 处理数据行（从第二行开始）
            for (int rowIndex = 1; rowIndex < values.size(); rowIndex++) {
                try {
                    List<Object> rowData = values.get(rowIndex);

                    // 跳过空行
                    if (CollectionUtils.isEmpty(rowData) || isEmptyRow(rowData)) {
                        continue;
                    }

                    // 创建实体对象实例
                    T entity = clazz.getDeclaredConstructor().newInstance();

                    // 使用反射设置字段值
                    boolean hasValidData = false;
                    Field[] fields = clazz.getDeclaredFields();

                    for (Field field : fields) {
                        field.setAccessible(true);

                        // 根据字段名匹配表头
                        String fieldName = field.getName();
                        int columnIndex = findColumnIndex(headerList, fieldName);

                        if (columnIndex >= 0 && columnIndex < rowData.size()) {
                            Object cellValue = rowData.get(columnIndex);

                            if (cellValue != null && !cellValue.toString().trim().isEmpty()) {
                                try {
                                    Object convertedValue = convertCellValue(cellValue, field.getType());
                                    if (convertedValue != null) {
                                        field.set(entity, convertedValue);
                                        hasValidData = true;
                                    }
                                } catch (Exception e) {
                                    log.warn("字段值转换失败，行: {}, 字段: {}, 值: {}, 错误: {}",
                                            rowIndex + 1, fieldName, cellValue, e.getMessage());
                                }
                            }
                        }
                    }

                    if (hasValidData) {
                        entityList.add(entity);
                        successCount++;
                        log.debug("成功转换第{}行数据", rowIndex + 1);
                    } else {
                        errorCount++;
                        String errorMsg = String.format("第%d行没有有效数据", rowIndex + 1);
                        errorMessages.add(errorMsg);
                        log.warn(errorMsg);
                    }

                } catch (Exception e) {
                    errorCount++;
                    String errorMsg = String.format("第%d行数据转换失败: %s", rowIndex + 1, e.getMessage());
                    errorMessages.add(errorMsg);
                    log.error(errorMsg, e);
                }
            }

        } catch (Exception e) {
            log.error("转换飞书表格数据异常", e);
            errorMessages.add("数据转换异常: " + e.getMessage());
        }

        // 设置结果
        result.setEntityList(entityList);
        result.setSuccessCount(successCount);
        result.setErrorCount(errorCount);
        result.setErrorMessages(errorMessages);

        log.info("飞书表格数据转换完成，成功: {}, 失败: {}, 总计: {}",
                successCount, errorCount, successCount + errorCount);

        return result;
    }

    /**
     * 判断是否为空行
     */
    private boolean isEmptyRow(List<Object> rowData) {
        if (CollectionUtils.isEmpty(rowData)) {
            return true;
        }

        for (Object cell : rowData) {
            if (cell != null && !cell.toString().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 根据字段名查找对应的列索引
     */
    private int findColumnIndex(List<String> headers, String fieldName) {
        // 定义字段名到表头的映射关系
        Map<String, String> fieldToHeaderMap = createFieldToHeaderMapping();

        String targetHeader = fieldToHeaderMap.get(fieldName);
        if (targetHeader != null) {
            for (int i = 0; i < headers.size(); i++) {
                if (targetHeader.equals(headers.get(i))) {
                    return i;
                }
            }
        }

        // 如果没有找到映射，尝试直接匹配字段名
        for (int i = 0; i < headers.size(); i++) {
            if (fieldName.equals(headers.get(i))) {
                return i;
            }
        }

        return -1; // 未找到
    }

    /**
     * 创建字段名到表头的映射关系
     */
    private Map<String, String> createFieldToHeaderMapping() {
        Map<String, String> mapping = new HashMap<>();

        // FeishuPrivateProductDto字段映射
        mapping.put("date", "日期");
        mapping.put("promotionNumber", "推广单号");
        mapping.put("businessConsultant", "商务顾问");
        mapping.put("celebrityConsultant", "网红顾问");
        mapping.put("brandName", "品牌名称");
        mapping.put("productName", "产品名称");
        mapping.put("selectedCelebrity", "选中网红");
        mapping.put("platform", "平台");
        mapping.put("category", "类目");
        mapping.put("celebrityQuote", "网红报价");
        mapping.put("celebrityCost", "网红成本$");
        mapping.put("remarks", "备注");
        mapping.put("onlineLink", "上线链接");
        mapping.put("isSynchronized", "是否同步");
        mapping.put("errorReason", "异常原因");

        return mapping;
    }

    /**
     * 转换单元格值为指定类型
     */
    private Object convertCellValue(Object cellValue, Class<?> targetType) {
        if (cellValue == null) {
            return null;
        }

        String cellStr = cellValue.toString().trim();
        if (cellStr.isEmpty()) {
            return null;
        }

        try {
            if (targetType == String.class) {
                return cellStr;
            } else if (targetType == Integer.class || targetType == int.class) {
                return Integer.valueOf(cellStr);
            } else if (targetType == Long.class || targetType == long.class) {
                return Long.valueOf(cellStr);
            } else if (targetType == Double.class || targetType == double.class) {
                return Double.valueOf(cellStr);
            } else if (targetType == Float.class || targetType == float.class) {
                return Float.valueOf(cellStr);
            } else if (targetType == Boolean.class || targetType == boolean.class) {
                return Boolean.valueOf(cellStr);
            } else if (targetType == Date.class) {
                // 尝试多种日期格式
                return parseDate(cellStr);
            }
        } catch (Exception e) {
            log.warn("值转换失败，原值: {}, 目标类型: {}, 错误: {}", cellStr, targetType.getSimpleName(), e.getMessage());
        }

        return null;
    }

    /**
     * 解析日期字符串
     */
    private Date parseDate(String dateStr) {
        if (StringUtils.isEmpty(dateStr)) {
            return null;
        }

        // 支持的日期格式
        String[] dateFormats = {
                "yyyy年M月d日",
                "yyyy-MM-dd",
                "yyyy/MM/dd",
                "yyyy-MM-dd HH:mm:ss",
                "yyyy/MM/dd HH:mm:ss"
        };

        for (String format : dateFormats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                return sdf.parse(dateStr);
            } catch (ParseException e) {
                // 继续尝试下一种格式
            }
        }

        log.warn("无法解析日期字符串: {}", dateStr);
        return null;
    }

    /**
     * @description: 修改引用表数据
     * @author: nqr
     * @date: 2025/9/16 7:55
     **/
    @Override
    @SkipOnProfile()
    public void insertSheetRecord(KolSysUserFeishuSheet feishuSheet, List<String> insertData) {
        // 定义 API 端点
        String url = "https://open.feishu.cn/open-apis/sheets/v2/spreadsheets/" + feishuSheet.getSpreadSheetId() + "/values";
        // 创建请求体
        JSONObject params = new JSONObject();
        JSONObject valueRange = new JSONObject();
        valueRange.put("range", feishuSheet.getSheetId() + "!A1:A" + insertData.size());

        JSONArray values = new JSONArray();
        for (int i = 0; i < insertData.size(); i++) {
            JSONArray row = new JSONArray();
            String data = insertData.get(i) != null ? insertData.get(i) : "";
            if (data.isEmpty()) {
                continue;
            }
            row.add(data);
            values.add(row);
        }


        valueRange.put("values", values);
        params.put("valueRange", valueRange);

        // 设置自定义请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.add("Authorization", "Bearer " + getInternalTenantAccessToken());

        // 发送 PUT 请求
        ResponseEntity<JSONObject> response = RestUtil.request(url, HttpMethod.PUT, headers, null, params, JSONObject.class);
        // 返回响应体
        System.out.println(response.getBody());
    }

    /**
     * 将飞书表格数据转换为实体对象列表
     *
     * @param sheetResponse 飞书表格响应数据
     * @param clazz         目标实体类
     * @return 转换结果
     */
    public <T> FeishuSheetConvertResult<T> convertSheetDataToEntityNew(FeishuSheetResponse sheetResponse, Class<T> clazz) {
        FeishuSheetConvertResult<T> result = new FeishuSheetConvertResult<>();
        List<T> entityList = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();
        Map<String, String> headerMapping = new HashMap<>(); // 表头 -> 字段名
        List<String> originalHeaders = new ArrayList<>();
        int totalRows = 0;
        int successCount = 0;
        int errorCount = 0;

        try {
            // 验证响应数据
            if (sheetResponse == null || sheetResponse.getCode() != 0 || sheetResponse.getData() == null) {
                String msg = (sheetResponse != null ? sheetResponse.getMsg() : "空响应");
                errorMessages.add("飞书响应数据无效或错误: " + msg);
                result.setErrorMessages(errorMessages);
                result.setErrorCount(1);
                return result;
            }

            // 获取值范围
            List<FeishuSheetResponse.ValueRange> valueRanges = sheetResponse.getData().getValueRanges();
            if (valueRanges == null || valueRanges.isEmpty()) {
                errorMessages.add("飞书表格数据为空");
                result.setErrorMessages(errorMessages);
                result.setErrorCount(1);
                return result;
            }

            // 获取表头和数据
            List<List<Object>> values = valueRanges.get(0).getValues();
            if (values == null || values.size() < 1) {
                errorMessages.add("表格数据为空");
                result.setErrorMessages(errorMessages);
                result.setErrorCount(1);
                return result;
            }

            // 处理表头
            List<String> headers = new ArrayList<>();
            if (values.get(0) != null) {
                for (Object headerObj : values.get(0)) {
                    String header = headerObj != null ? headerObj.toString() : "";
                    headers.add(header);
                    originalHeaders.add(header);
                }
            }
            result.setOriginalHeaders(originalHeaders);

            // 构建表头到字段的映射 (表头中文 -> 字段英文)
            List<Field> fields = getExcelFields(clazz);
            Map<String, Field> headerToFieldMap = mapHeaderToField(fields);
            for (String header : headers) {
                Field field = headerToFieldMap.get(header);
                if (field != null) {
                    headerMapping.put(header, field.getName());
                } else {
                    log.warn("表头未匹配到字段: {}", header);
                }
            }

            // 添加行号字段映射
            headerMapping.put("行号", "rowNum");
            result.setHeaderMapping(headerMapping);

            // 验证表头匹配率
            int matchedHeaders = headerMapping.size() - 1; // 减去行号字段
            log.info("表头匹配情况: 总表头={}, 匹配字段数={}", headers.size(), matchedHeaders);
            if (matchedHeaders == 0) {
                errorMessages.add("无表头匹配到实体字段，请检查 @Excel(name) 注解");
                result.setErrorMessages(errorMessages);
                result.setErrorCount(1);
                return result;
            }

            // 遍历数据行（跳过表头）
            totalRows = values.size() - 1;
            result.setTotalRows(totalRows);
            for (int i = 1; i < values.size(); i++) {
                List<Object> row = values.get(i);
                T entity;
                try {
                    entity = clazz.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    errorMessages.add(String.format("第%d行: 创建实体实例失败 - %s", i, e.getMessage()));
                    errorCount++;
                    continue;
                }

                List<String> rowErrors = new ArrayList<>();
                boolean rowSuccess = true;

                // 首先设置行号
                try {
                    Field rowNumField = getRowNumField(clazz);
                    if (rowNumField != null) {
                        // i 就是当前行号（从1开始的数据行）
                        setFieldValue(entity, rowNumField, i + 1);
                    }
                } catch (Exception e) {
                    String err = String.format("行号字段设置失败 - %s", e.getMessage());
                    rowErrors.add(err);
                    rowSuccess = false;
                    log.warn("第{}行设置行号失败: {}", i, e.getMessage());
                }

                // 遍历每一列
                for (int j = 0; j < headers.size() && j < row.size(); j++) {
                    String header = headers.get(j);
                    Field field = headerToFieldMap.get(header);
                    if (field != null) {
                        try {
                            setFieldValue(entity, field, row.get(j));
                        } catch (Exception e) {
                            String err = String.format("字段'%s'转换失败 - %s", field.getName(), e.getMessage());
                            rowErrors.add(err);
                            rowSuccess = false;
                            log.warn("第{}行, 列{}({}), 值{}: {}", i, j, header, row.get(j), e.getMessage());
                        }
                    }
                }

                if (rowSuccess) {
                    entityList.add(entity);
                    successCount++;
                } else {
                    errorCount++;
                    for (String rowErr : rowErrors) {
                        errorMessages.add(String.format("第%d行: %s", i, rowErr));
                    }
                }
            }

            result.setEntityList(entityList);
            result.setSuccessCount(successCount);
            result.setErrorCount(errorCount);
            result.setErrorMessages(errorMessages);

        } catch (Exception e) {
            log.error("飞书表格数据转换异常", e);
            errorMessages.add("整体转换失败: " + e.getMessage());
            result.setErrorMessages(errorMessages);
            result.setErrorCount(errorCount + 1);
        }

        log.info("转换完成: 总行数={}, 成功={}, 失败={}, 错误详情数={}", totalRows, successCount, errorCount, errorMessages.size());
        return result;
    }

    /**
     * 获取行号字段
     */
    private Field getRowNumField(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if ("rowNum".equals(field.getName())) {
                field.setAccessible(true);
                return field;
            }
        }
        return null;
    }

    // 获取类中带有 @Excel 注解的字段
    private List<Field> getExcelFields(Class<?> clazz) {
        List<Field> excelFields = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Excel.class)) {
                excelFields.add(field);
            }
        }
        return excelFields;
    }

    // 构建表头到字段的映射
    private Map<String, Field> mapHeaderToField(List<Field> fields) {
        Map<String, Field> headerToFieldMap = new HashMap<>();
        for (Field field : fields) {
            Excel excel = field.getAnnotation(Excel.class);
            if (excel != null && excel.name() != null && !excel.name().isEmpty()) {
                headerToFieldMap.put(excel.name(), field);
            }
        }
        return headerToFieldMap;
    }

    // 设置字段值
    private void setFieldValue(Object entity, Field field, Object value) throws IllegalAccessException {
        field.setAccessible(true);
        if (value == null || (value instanceof String && ((String) value).trim().isEmpty())) {
            field.set(entity, getDefaultValue(field.getType()));
            return;
        }

        Class<?> fieldType = field.getType();
        String stringValue = value.toString().trim();

        try {
            Object convertedValue = null;
            if (fieldType == String.class) {
                convertedValue = stringValue;
            } else if (fieldType == Integer.class || fieldType == int.class) {
                convertedValue = Integer.valueOf(stringValue);
            } else if (fieldType == BigDecimal.class) {
                convertedValue = new BigDecimal(stringValue);
            } else if (fieldType == Long.class || fieldType == long.class) {
                convertedValue = Long.valueOf(stringValue);
            } // 可以扩展更多类型，如 Boolean、Date 等
            else {
                throw new UnsupportedOperationException("不支持的字段类型: " + fieldType.getSimpleName());
            }
            field.set(entity, convertedValue);
        } catch (NumberFormatException e) {
            throw new RuntimeException("数值转换失败: " + stringValue + " -> " + fieldType.getSimpleName());
        }
    }

    // 获取字段默认值
    private Object getDefaultValue(Class<?> type) {
        if (type == Integer.class || type == int.class) return 0;
        if (type == BigDecimal.class) return BigDecimal.ZERO;
        if (type == Long.class || type == long.class) return 0L;
        return null; // String 等为 null
    }

    /**
     * @description: 批量更新飞书表格数据
     * @author: nqr
     * @date: 2025/9/16 10:01
     **/
    @Override
    @SkipOnProfile()
    public void updateValuesBatch(KolSysUserFeishuSheet feishuSheet, Map<String, Object> values) {
        // 定义 API 端点
        String url = "https://open.feishu.cn/open-apis/sheets/v2/spreadsheets/" + feishuSheet.getSpreadSheetId() + "/values_batch_update";
        // 创建查询参数
        // Map 转为 json
        JSONObject params = JSONObject.parseObject(JSONObject.toJSONString(values));
        // 设置自定义请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + getInternalTenantAccessToken());
        // 发送 GET 请求
        ResponseEntity<JSONObject> response = RestUtil.request(url, HttpMethod.POST, headers, null, params, JSONObject.class);
        System.out.println(response.getBody());
    }

    /**
     * @description: 修改飞书sheet值
     * type：
     * 1 删除；
     * 0:新增或编辑
     * @author: nqr
     * @date: 2025/9/16 14:07
     **/
    @Override
    @SkipOnProfile()
    public void insertOrUpdatePersonalTagsSheetBatch(int type, String spreadSheetType, List<String> insertData) {
        try {
            List<Map<String, Object>> arrayList = new ArrayList<>();
            feishuSheetService.lambdaQuery()
                    .eq(KolSysUserFeishuSheet::getSpreadSheetType, spreadSheetType)
                    .list()
                    .forEach(feishuSheet -> {
                        System.out.println("userId：" + feishuSheet.getSysUserId() + "_" + feishuSheet.getSpreadSheetUrl());
                        arrayList.add(new HashMap<String, Object>() {{
                            // 判断,如果是删除,则range为A1:A5000,如果是新增,则range为A1:插入行数
                            put("range", feishuSheet.getSheetId() + "!A1:A" + (type == 0 ? insertData.size() : 1000));
                            List<List<String>> values = new ArrayList<>();
                            if (type == 0) {
                                insertData.forEach(data -> {
                                    values.add(Collections.singletonList(data));

                                });
                            } else {
                                for (int i = 0; i < 1000; i++) {
                                    values.add(Collections.singletonList(""));
                                }
                            }
                            put("values", values);
                        }});
                        Map<String, Object> values = new HashMap<String, Object>() {{
                            put("valueRanges", arrayList);
                        }};
                        try {
                            updateValuesBatch(feishuSheet, values);
                        } catch (Exception e) {
                            log.error("同步飞书数据失败", e);
                        }
                    });
            if (type == 1) {
                Thread.sleep(100);
                insertOrUpdatePersonalTagsSheetBatch(0, spreadSheetType, insertData);
            }
        } catch (Exception e) {
            log.error("同步飞书数据失败", e);
        }
    }

    @Override
    @SkipOnProfile()
    public void insertOrUpdatePersonalTagsSheet(int type, KolSysUserFeishuSheet feishuSheet, List<String> insertData) {
        try {
            CompletableFuture.runAsync(() -> {
                List<Map<String, Object>> arrayList = new ArrayList<>();
                System.out.println("userId：" + feishuSheet.getSysUserId() + "_" + feishuSheet.getSpreadSheetUrl());
                arrayList.add(new HashMap<String, Object>() {{
                    put("range", feishuSheet.getSheetId() + "!A1:A" + (type == 0 ? insertData.size() : insertData.size() + 1));
                    List<List<String>> values = new ArrayList<>();
                    insertData.forEach(data -> {
                        if (type == 0) {
                            values.add(Collections.singletonList(data));
                        } else {
                            values.add(Collections.singletonList(null));
                        }
                    });
                    if (type == 1) {
                        values.add(Collections.singletonList(null));
                    }
                    put("values", values);
                }});
                Map<String, Object> values = new HashMap<String, Object>() {{
                    put("valueRanges", arrayList);
                }};
                updateValuesBatch(feishuSheet, values);
            });
            if (type == 1) {
                Thread.sleep(100);
                insertOrUpdatePersonalTagsSheet(0, feishuSheet, insertData);
            }
        } catch (Exception e) {
            log.error("同步飞书数据失败", e);
        }
    }
}
