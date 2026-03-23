package org.jeecg.modules.feishu.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.SheetConstants;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.feishu.model.FeishuSheetResponse;
import org.jeecg.modules.kol.entity.KolProduct;
import org.jeecg.modules.kol.entity.KolSysUserFeishuSheet;
import org.jeecg.modules.feishu.config.FeishuAppConfig;
import org.jeecg.modules.feishu.model.FeishuSheetConvertResult;
import org.jeecg.modules.feishu.service.IFeishuService;
import org.jeecg.modules.kol.model.KolTagsFeishuModel;
import org.jeecg.modules.kol.model.KolTagsModel;
import org.jeecg.modules.kol.service.IKolProductService;
import org.jeecg.modules.kol.service.IKolSysUserFeishuSheetService;
import org.jeecg.modules.kol.service.IKolTagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: nqr
 * @Date: 2025/7/16 15:39
 * @Description:
 **/
@Slf4j
@Tag(name = "feishu API")
@RestController
@RequestMapping("/kol/feishu")
public class FeishuController {
    @Autowired
    private FeishuAppConfig feishuAppConfig;
    @Autowired
    private IFeishuService feishuService;
    @Resource
    private IKolSysUserFeishuSheetService sheetService;
    @Autowired
    private IKolTagsService kolTagsService;
    @Autowired
    private IKolProductService productService;

    @AutoLog(value = "从飞书导入标签")
    @Operation(summary = "从飞书导入标签", description = "从飞书导入标签")
    @GetMapping(value = "/synchronizeData")
    public Result<?> synchronizeData(@RequestParam(name = "platformType", required = true) Integer platformType) throws Exception {
        String data = feishuService.synchronizeData(platformType);
        return feishuService.extracted(platformType, data);
    }

    @AutoLog(value = "从飞书导入标签")
    @Operation(summary = "从飞书导入标签", description = "从飞书导入标签")
    @GetMapping(value = "/importTagsData")
    public Result<?> importTagsData(@RequestParam(name = "platformTypes", required = false) String platformTypes) throws Exception {
        try {
            System.out.println("开始从飞书表格导入标签数据");
            String tenantAccessToken = feishuService.getInternalTenantAccessToken();
            LambdaQueryChainWrapper<KolSysUserFeishuSheet> lambdaQuery = getLambdaQuery("0,1,2");
            List<KolSysUserFeishuSheet> feishuSheets = lambdaQuery.list();
            Map<String, List<KolSysUserFeishuSheet>> spreadSheetMap = feishuSheets.stream().collect(Collectors.groupingBy(x -> x.getSpreadSheetType() + "_" + x.getSheetId()));
            List<KolSysUserFeishuSheet> feishuSheetList = new ArrayList<>();
            for (String key : spreadSheetMap.keySet()) {
                List<KolSysUserFeishuSheet> list = spreadSheetMap.get(key);
                feishuSheetList.add(list.get(0));
            }
            for (KolSysUserFeishuSheet feishuSheet : feishuSheetList) {
                String url = "https://open.feishu.cn/open-apis/sheets/v2/spreadsheets/" + feishuSheet.getSpreadSheetId() + "/values_batch_get?ranges=" + feishuSheet.getSheetId() + "!A1:" + feishuSheet.getEndColumn() + "&valueRenderOption=ToString&dateTimeRenderOption=FormattedString";
                System.out.println(url);
                FeishuSheetResponse sheetResponse = feishuService.getFeishuSheetData(url, tenantAccessToken);
                // 2. 将表格数据转换为DTO对象
                FeishuSheetConvertResult<KolTagsFeishuModel> convertResult = feishuService.convertSheetDataToEntityNew(sheetResponse, KolTagsFeishuModel.class);
                List<KolTagsFeishuModel> entityList = convertResult.getEntityList();
                List<KolTagsModel> tagsModelList = oConvertUtils.entityListToModelList(entityList, KolTagsModel.class);
                if (tagsModelList == null) {
                    continue;
                }
                List<KolTagsModel> models = tagsModelList.stream().filter(x -> (oConvertUtils.isEmpty(x.getIsUpdate()) || x.getIsUpdate().equals("否")) && oConvertUtils.isNotEmpty(x.getTagName())).collect(Collectors.toList());
                if (models.isEmpty()) {
                    System.out.println(feishuSheet.getSpreadSheetType() + " 没有需要导入的数据");
                    continue;
                }
                // 处理产品品牌
                updateBrandAndProduct(models);
                int platformType = feishuSheet.getSpreadSheetType().equals(SheetConstants.IG_TAG) ? 0 : (feishuSheet.getSpreadSheetType().equals(SheetConstants.YT_TAG) ? 1 : 2);
                Result<?> result = kolTagsService.importTags(models, platformType, "", 2);
                List<String> messageList = (List<String>) result.getResult();
                List<Integer> rowNums = models.stream().map(KolTagsModel::getRowNum).collect(Collectors.toList());
                List<Map<String, Object>> arrayList = new ArrayList<>();
                if (messageList != null && !messageList.isEmpty()) {
                    for (String msg : messageList) {
                        String[] split = msg.split(",", 2);
                        if (split.length > 1) {
                            int rowNum = 0;
                            try {
                                rowNum = Integer.parseInt(split[1]);
                            } catch (Exception e) {
                                continue;
                            }
                            // 按值移除已处理的行号
                            rowNums.remove(Integer.valueOf(rowNum));
                            int finalRowNum = rowNum;
                            HashMap<String, Object> map = new HashMap<String, Object>() {{
                                put("range", feishuSheet.getSheetId() + "!" + feishuSheet.getIsSynchronizeColumn() + finalRowNum + ":" + feishuSheet.getErrorReasonColumn() + finalRowNum);
                                put("values", Collections.singletonList(Arrays.asList("否", split[0])));
                            }};
                            arrayList.add(map);
                        }
                    }
                }
                String status = (result.getCode() == 200 || result.getCode() == 10001) ? "是" : "否";
                for (KolTagsModel model : models) {
                    Integer rowNum = model.getRowNum();
                    if (!rowNums.contains(rowNum)) {
                        continue;
                    }
                    HashMap<String, Object> map = new HashMap<String, Object>() {{
                        put("range", feishuSheet.getSheetId() + "!" + feishuSheet.getIsSynchronizeColumn() + rowNum + ":" + feishuSheet.getErrorReasonColumn() + rowNum);
                        put("values", Collections.singletonList(Arrays.asList(status, "")));
                    }};
                    arrayList.add(map);
                }
                Map<String, Object> values = new HashMap<String, Object>() {{
                    put("valueRanges", arrayList);
                }};
                feishuService.updateValuesBatch(feishuSheet, values);
            }
        } catch (Exception e) {
            System.out.println("从飞书表格导入数据异常" + e.getMessage());
        }
        return Result.ok("操作成功");
    }

    private LambdaQueryChainWrapper<KolSysUserFeishuSheet> getLambdaQuery(String platformTypes) {
        LambdaQueryChainWrapper<KolSysUserFeishuSheet> lambdaQuery = sheetService.lambdaQuery();
        if (oConvertUtils.isNotEmpty(platformTypes)) {
            String[] split = platformTypes.split(",");
            lambdaQuery.and(x -> {
                for (String platformType : split) {
                    int type = Integer.parseInt(platformType);
                    if (type == 0) {
                        x.eq(KolSysUserFeishuSheet::getSpreadSheetType, SheetConstants.IG_TAG).or();
                    } else if (type == 1) {
                        x.eq(KolSysUserFeishuSheet::getSpreadSheetType, SheetConstants.YT_TAG).or();
                    } else if (type == 2) {
                        x.eq(KolSysUserFeishuSheet::getSpreadSheetType, SheetConstants.TK_TAG).or();
                    }
                }
            });
        } else {
            lambdaQuery.and(x -> x.eq(KolSysUserFeishuSheet::getSpreadSheetType, SheetConstants.IG_TAG)
                    .or()
                    .eq(KolSysUserFeishuSheet::getSpreadSheetType, SheetConstants.YT_TAG)
                    .or()
                    .eq(KolSysUserFeishuSheet::getSpreadSheetType, SheetConstants.TK_TAG));
        }
        return lambdaQuery;
    }

    private void updateBrandAndProduct(List<KolTagsModel> models) {
        List<KolProduct> kolProducts = productService.list();
        models.forEach(x -> {
            // List<KolProduct> products = productService.lambdaQuery().eq(KolProduct::getProductName, x.getProductName()).eq(KolProduct::getBrandName, x.getBrandName()).list();
            List<KolProduct> products = kolProducts.stream().filter(a -> x.getProductName().equals(a.getProductName()) && x.getBrandName().equals(a.getBrandName())).collect(Collectors.toList());
            if (!products.isEmpty()) {
                KolProduct product = products.get(0);
                x.setProductId(product.getId());
                x.setBrandId(product.getBrandId());
            }
        });
    }
}
