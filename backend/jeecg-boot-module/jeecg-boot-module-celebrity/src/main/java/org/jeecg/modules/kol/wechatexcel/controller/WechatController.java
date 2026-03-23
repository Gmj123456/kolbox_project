package org.jeecg.modules.kol.wechatexcel.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.modules.kol.model.KolTagsModel;
import org.jeecg.modules.kol.service.IKolTagsService;
import org.jeecg.modules.kol.wechatexcel.config.WechatDocConfig;
import org.jeecg.modules.kol.wechatexcel.model.Record;
import org.jeecg.modules.kol.wechatexcel.service.WechatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.jeecg.common.constant.CommonConstant.SC_OK_200;

/**
 * @Author: nqr
 * @Date: 2025/7/16 15:39
 * @Description:
 **/
@Slf4j
@Tag(name = "企业微信 API")
@RestController
@RequestMapping("/kol/wechat")
class WechatController {
    @Resource
    private WechatService wechatService;
    @Resource
    private WechatDocConfig wechatDocConfig;
    @Resource
    private IKolTagsService kolTagsService;

    @AutoLog(value = "获取企业微信文档数据")
    @Operation(summary = "获取企业微信文档数据", description = "获取企业微信文档数据")
    @GetMapping(value = "/synchronizeData")
    public Result<?> synchronizeData() throws Exception {
        List<Record> allRecords = wechatService.getAllRecords(wechatDocConfig.getTemplateDocId(), wechatDocConfig.getPrivatePersonalTagsSheetId(), null, null);
        System.out.println(JSON.toJSONString(allRecords));
        return Result.ok();
    }

    /**
     * @description: 同步标签数据
     * @author: nqr
     * @date: 2025/9/8 8:33
     **/
    @GetMapping(value = "/synchronizeTagData")
    public Result<List<String>> synchronizeTagData(@RequestParam(value = "platformTypes", required = false) String platformTypes) throws Exception {
        String docId = wechatDocConfig.getTemplateDocId();
        Map<String, Integer> sheetMap = getSheetId(platformTypes);

        List<CompletableFuture<SheetProcessResult>> futures = new ArrayList<>();
        Map<String,List<String>> allRecordIds = new HashMap<>();
        // 异步获取每个 sheet 的原始导入结果
        for (Map.Entry<String, Integer> entry : sheetMap.entrySet()) {
            String sheetId = entry.getKey();
            int platformType = entry.getValue();
            futures.add(CompletableFuture.supplyAsync(() -> {
                Result<?> rawResult = getImportTagResult(allRecordIds, platformType, sheetId, docId);
                return new SheetProcessResult(sheetId, rawResult);
            }));
        }

        // 等待所有异步任务完成
        List<SheetProcessResult> sheetResults = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        // 聚合最终返回的消息
        List<String> aggregatedMessages = new ArrayList<>();
        // 用于异步更新的 sheetId -> records 映射
        Map<String, List<Map<String, Object>>> sheetToUpdateRecords = new ConcurrentHashMap<>();

        for (SheetProcessResult sheetResult : sheetResults) {
            String sheetId = sheetResult.getSheetId();
            Result<?> result = sheetResult.getResult();
            List<String> recordIds = allRecordIds.get(sheetId);
/*            if (!Objects.equals(result.getCode(), SC_OK_200) && !Objects.equals(result.getCode(), SC_OK_10001)) {
                // 如果是错误，直接收集错误信息
                List<String> messageList = (List<String>) result.getResult();
                String msg = String.join(",", messageList);
                aggregatedMessages.add(msg.split(",")[0]);
                continue;
            }*/

            @SuppressWarnings("unchecked")
            List<String> messageList = (List<String>) result.getResult();
            List<String> resultMsg = new ArrayList<>();
            List<Map<String, Object>> updateRecords = new ArrayList<>();

            if (messageList != null && !messageList.isEmpty()) {
                for (String msg : messageList) {
                    String[] split = msg.split(",", 2);
                    if (split.length > 1) {
                        String recordId = split[1];
                        // 移除已处理的
                        recordIds.remove(recordId);
                        resultMsg.add(split[0]);
                        HashMap<String, Object> map = getWechatUpdateMap(recordId, "否", split[0].split("，")[1]);
                        updateRecords.add(map);
                        allRecordIds.put(sheetId,recordIds);
                    } else {
                        resultMsg.add(msg);
                    }
                }
            }

            // 添加未处理的记录（标记为“是”）
            allRecordIds.get(sheetId).forEach((recordId) -> {
                HashMap<String, Object> map = getWechatUpdateMap(recordId, "是", "");
                updateRecords.add(map);
            });

            // 保存该 sheet 的更新数据，用于异步更新
            if (!updateRecords.isEmpty()) {
                sheetToUpdateRecords.put(sheetId, updateRecords);
            }

            // 聚合消息
            aggregatedMessages.addAll(resultMsg);
        }

        // 异步执行所有 sheet 的数据更新
        sheetToUpdateRecords.forEach((sheetId, records) -> {
            CompletableFuture.runAsync(() -> {
                try {
                    wechatService.updateRecords(docId, sheetId, records);
                } catch (Exception e) {
                    log.error("更新标签数据失败，sheetId: {}, docId: {}", sheetId, docId, e);
                }
            });
        });

        // 构建最终返回结果
        Result<List<String>> finalResult = new Result<>();
        finalResult.setCode(SC_OK_200);
        finalResult.setMessage(aggregatedMessages.isEmpty() ? "数据同步完成" : "数据同步完成，存在同步失败的数据");
        finalResult.setResult(aggregatedMessages);

        return finalResult;
    }

    @Data
    private class SheetProcessResult {
        private final String sheetId;
        private final Result<?> result;
    }

    private Result<?> getImportTagResult(Map<String,List<String>>  allRecordIds, Integer platformType, String sheetId, String docId) {
        List<Record> allRecords = wechatService.getAllRecords(docId, sheetId, null, null);
        System.out.println(JSON.toJSONString(allRecords));
        List<Map<String, Object>> list = allRecords.stream().map(Record::getValues).collect(Collectors.toList());
        List<KolTagsModel> kolTagsList = wechatService.convertToTagsModel(list);
        List<String> ids = kolTagsList.stream().map(KolTagsModel::getRecordId).collect(Collectors.toList());
        allRecordIds.put(sheetId,ids);
        return kolTagsService.importTags(kolTagsList, platformType, "", 2);
    }

    private static HashMap<String, Object> getWechatUpdateMap(String recordId, String status, String errorMsg) {
        HashMap<String, Object> map = new HashMap<String, Object>() {{
            put("record_id", recordId);
            put("values", new HashMap<String, Object>() {{
                put("是否同步", Collections.singletonList(new HashMap<String, Object>() {{
                    put("text", status);
                }}));
                put("异常原因", Collections.singletonList(new HashMap<String, Object>() {{
                    put("text", errorMsg);
                    put("type", "text");
                }}));
            }});
        }};
        return map;
    }

    private Map<String, Integer> getSheetId(String platformTypes) {
        Map<String, Integer> sheetMap = new HashMap<>();
        String[] split = platformTypes.split(",");
        for (String platformType : split) {
            int type = Integer.parseInt(platformType);
            switch (type) {
                case 0:
                    sheetMap.put(wechatDocConfig.getIgTagSheetId(), type);
                    break;
                case 1:
                    sheetMap.put(wechatDocConfig.getYtTagSheetId(), type);
                    break;
                default:
                    sheetMap.put(wechatDocConfig.getTkTagSheetId(), type);
                    break;
            }
        }
        return sheetMap;
    }

    /**
     * 创建企业微信文档
     * 私有网红模板
     * {
     * "url": "https://doc.weixin.qq.com/smartsheet/s3_AAEA7BQ7AMMCNlPsrkH7eRv0RPQqx_a?scode=AFgAbwdHABAEHdjL6sAAEA7BQ7AMM",
     * "docid": "dcVVw9r93yftxim-Z4-mF7MzUNCxv3cV_BW5eF8YjHHM4-I0OGsNOLhkWrpuFlhfkRSsJFdx-KbtNOOJncmiwZVQ",
     * "errcode": 0,
     * "errmsg": "ok"
     * }
     * "sheet_id": "q979lj",
     */
    @AutoLog(value = "创建企业微信文档")
    @Operation(summary = "创建企业微信文档", description = "创建企业微信文档")
    @GetMapping(value = "/createDocument")
    public Result<?> createDocument(@RequestParam(value = "docName", required = true) String docName, @RequestParam(value = "docType", required = false) Integer docType) throws Exception {
        if (docType == null) {
            // 文档类型, 3:文档 4:表格 10:智能表格
            docType = 10;
        }
        JSONObject result = wechatService.createDocument(docName, docType);
        System.out.println(result.toString());
        return Result.ok(result);
    }

    @AutoLog(value = "修改企业微信智能表格行记录")
    @Operation(summary = "修改企业微信智能表格行记录", description = "修改企业微信智能表格行记录")
    @GetMapping(value = "/updateRecords")
    public Result<?> updateRecords(@RequestParam(value = "docId", required = true) String docId, @RequestParam(value = "sheetId", required = true) String sheetId) throws Exception {
        List<Map<String, Object>> updateRecords = new ArrayList<>();
        /*
        [
          {
            "record_id": "rec001",
            "values": {
              "是否同步": [
                {
                  "text": "是"
                }
              ],
              "异常原因": [
                {
                  "text": "标题格式错误",
                  "type": "text"
                }
              ]
            }
          }
         */
        wechatService.updateRecords(docId, sheetId, updateRecords);
        return Result.ok();
    }

}
