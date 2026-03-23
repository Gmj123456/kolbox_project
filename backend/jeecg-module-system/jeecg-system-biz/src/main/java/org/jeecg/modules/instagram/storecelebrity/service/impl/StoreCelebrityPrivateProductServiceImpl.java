package org.jeecg.modules.instagram.storecelebrity.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivate;
import org.jeecg.modules.instagram.storecelebrity.mapper.StoreCelebrityPrivateProductMapper;
import org.jeecg.modules.instagram.storecelebrity.model.ImportResult;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivateProduct;
import org.jeecg.modules.instagram.storecelebrity.model.StoreCelebrityPrivateProductModel;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityPrivateProductService;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityPrivateService;
import org.jeecg.modules.kol.entity.KolProduct;
import org.jeecg.modules.kol.entity.KolSysUserFeishuSheet;
import org.jeecg.modules.feishu.model.FeishuPrivateProductDto;
import org.jeecg.modules.feishu.model.FeishuSheetConvertResult;
import org.jeecg.modules.feishu.model.FeishuSheetResponse;
import org.jeecg.modules.kol.service.IKolProductService;
import org.jeecg.modules.feishu.service.IFeishuService;
import org.springframework.context.annotation.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.jeecg.common.util.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 私有网红品牌产品关联表
 * @Author: nqr
 * @Date:   2025-06-03
 * @Version: V1.0
 */
@Slf4j
@Service
public class StoreCelebrityPrivateProductServiceImpl extends ServiceImpl<StoreCelebrityPrivateProductMapper, StoreCelebrityPrivateProduct> implements IStoreCelebrityPrivateProductService {

    @Autowired
    private IFeishuService feishuService;
    @Resource
    @Lazy
    private IStoreCelebrityPrivateService storeCelebrityPrivateService;
    @Autowired
    @Lazy
    private IKolProductService kolProductService;

    /**
     * 通过历史合作、历史选中状态查询产品列表
     *
     * @param relationStatus
     * @return
     */
    @Override
    public List<KolProduct> queryProducts(Integer relationStatus) {
        return this.baseMapper.queryProducts(relationStatus);
    }

    @Override
    public List<StoreCelebrityPrivateProductModel> getProductList(List<String> celebrityPrivateIds) {
        return this.baseMapper.getProductList(celebrityPrivateIds);
    }

    @Override
    public Result<?> importFromFeishuSheet(String spreadSheetUrl, String accessToken, KolSysUserFeishuSheet feishuSheet) {
        try {
            log.info("开始从飞书表格导入私有网红产品数据，URL: {}", spreadSheetUrl);
            
            // 参数校验
            if (oConvertUtils.isEmpty(spreadSheetUrl)) {
                return Result.error("飞书表格URL不能为空");
            }
            if (oConvertUtils.isEmpty(accessToken)) {
                return Result.error("访问令牌不能为空");
            }
            if (feishuSheet == null) {
                return Result.error("飞书表格配置不能为空");
            }
            
            // 1. 获取飞书表格数据
            FeishuSheetResponse sheetResponse = feishuService.getFeishuSheetData(spreadSheetUrl, accessToken);
            if (sheetResponse == null) {
                log.error("获取飞书表格数据失败，返回的响应为null");
                return Result.error("获取飞书表格数据失败");
            }
            
            if (sheetResponse.getCode() != null && sheetResponse.getCode() != 0) {
                log.error("飞书API返回错误，错误码: {}, 错误信息: {}", sheetResponse.getCode(), sheetResponse.getMsg());
                return Result.error("飞书API错误: " + sheetResponse.getMsg());
            }
            
            // 2. 将表格数据转换为DTO对象
            FeishuSheetConvertResult<StoreCelebrityPrivateProductModel> convertResult =
                feishuService.convertSheetDataToEntityNew(sheetResponse, StoreCelebrityPrivateProductModel.class);
            
            if (convertResult == null || CollectionUtils.isEmpty(convertResult.getEntityList())) {
                log.warn("飞书表格没有有效数据，转换结果为空");
                return Result.error("表格中没有有效数据");
            }
            
            // 3. 过滤需要导入的数据
            List<StoreCelebrityPrivateProductModel> entityList = convertResult.getEntityList();
            List<StoreCelebrityPrivateProductModel> privateExcelModels = entityList.stream()
                    .filter(x -> oConvertUtils.isEmpty(x.getIsUpdate()) || "否".equals(x.getIsUpdate()))
                    .collect(Collectors.toList());
            
            if (privateExcelModels.isEmpty()) {
                log.info("表格中没有需要导入的数据，所有数据均已导入");
                return Result.error("没有需要导入的数据");
            }
            
            log.info("待处理数据总数: {}, 需要导入数据数: {}", entityList.size(), privateExcelModels.size());
            
            // 4. 转换成可以保存的内容
            ImportResult importResult = convertToEntity(privateExcelModels, feishuSheet);
            
            // 5. 根据处理结果返回响应
            if (importResult.isHasError()) {
                log.warn("数据导入完成，成功: {}, 失败: {}", importResult.getSuccessCount(), importResult.getErrorCount());
                return Result.ok(String.format("导入完成，成功 %d 条，失败 %d 条", 
                        importResult.getSuccessCount(), importResult.getErrorCount()));
            } else {
                log.info("数据导入成功，成功导入 {} 条数据", importResult.getSuccessCount());
                return Result.ok("导入成功，共导入 " + importResult.getSuccessCount() + " 条数据");
            }
            
        } catch (Exception e) {
            log.error("从飞书表格导入数据异常", e);
            return Result.error("导入失败: " + e.getMessage());
        }
    }

    /**
     * 将模型数据转换为数据库实体并保存
     * @param privateProductModels 待处理的数据模型列表
     * @param feishuSheet 飞书表格配置信息
     * @return 导入结果
     */
    public ImportResult convertToEntity(List<StoreCelebrityPrivateProductModel> privateProductModels, KolSysUserFeishuSheet feishuSheet) {
        ImportResult importResult = new ImportResult();
        
        // 过滤有效数据（账号不为空）
        privateProductModels = privateProductModels.stream()
                .filter(x -> oConvertUtils.isNotEmpty(x.getAccount()))
                .collect(Collectors.toList());
        
        // 根据网红名称与平台过滤出重复的数据，并标记为重复数据
        markDuplicateData(privateProductModels);
        
        if (privateProductModels.isEmpty()) {
            log.warn("没有找到有效的网红账号数据");
            return importResult;
        }

        Set<StoreCelebrityPrivateProductModel> errorList = new HashSet<>();
        List<StoreCelebrityPrivateProduct> insertPrivateProducts = new ArrayList<>();
        List<StoreCelebrityPrivateProduct> updatePrivateProducts = new ArrayList<>();
        List<StoreCelebrityPrivateProductModel> successList = new ArrayList<>();

        log.info("开始处理 {} 条数据", privateProductModels.size());

        // 循环privateProductModels
        for (StoreCelebrityPrivateProductModel model : privateProductModels) {
            boolean hasError = false;

            try {
                // 检查是否为重复数据，如果是则直接跳过后续处理
                if (model.getIsRepeat() == 1) {
                    errorList.add(model);
                    log.debug("跳过重复数据处理：网红名称={}, 平台={}, 品牌={}, 产品={}, 行号={}", 
                            model.getAccount(), model.getPlatformTypeStr(), model.getBrandName(), model.getProductName(), model.getRowNum());
                    continue;
                }
                // 1、处理startDateStr 字符串类型转日期类型，设置model的startTime
                if (oConvertUtils.isNotEmpty(model.getStartDateStr())) {
                    try {
                        Date startTime = parseStartDate(model.getStartDateStr());
                        model.setStartTime(startTime);
                        log.debug("解析开始时间成功：{} -> {}", model.getStartDateStr(), startTime);
                    } catch (Exception e) {
                        addToErrorList(model, "开始时间格式无效: " + model.getStartDateStr(), errorList);
                        hasError = true;
                        log.warn("解析开始时间失败：{}", model.getStartDateStr(), e);
                    }
                }

                // 2、验证产品是否为空，为空则返回错误信息
                if (oConvertUtils.isEmpty(model.getProductName())) {
                    addToErrorList(model, "产品信息不能为空", errorList);
                    hasError = true;
                }

                // 3、验证产品是否存在，根据产品名称、品牌名称查询产品表，设置model的产品ID，品牌ID
                if (oConvertUtils.isNotEmpty(model.getProductName()) && oConvertUtils.isNotEmpty(model.getBrandName())) {
                    try {
                        KolProduct kolProduct = findProductByNameAndBrand(model.getProductName().trim(), model.getBrandName().trim());
                        if (kolProduct != null) {
                            model.setProductId(kolProduct.getId());
                            model.setBrandId(kolProduct.getBrandId());
                            log.debug("找到匹配产品：{} - {}, 产品ID: {}, 品牌ID: {}", 
                                    model.getBrandName(), model.getProductName(), kolProduct.getId(), kolProduct.getBrandId());
                        } else {
                            addToErrorList(model, String.format("产品不存在：品牌[%s] - 产品[%s]", 
                                    model.getBrandName(), model.getProductName()), errorList);
                            hasError = true;
                        }
                    } catch (Exception e) {
                        addToErrorList(model, "查询产品信息异常: " + e.getMessage(), errorList);
                        hasError = true;
                        log.error("查询产品异常：品牌[{}] - 产品[{}]", model.getBrandName(), model.getProductName(), e);
                    }
                } else if (oConvertUtils.isNotEmpty(model.getProductName())) {
                    addToErrorList(model, "品牌名称不能为空", errorList);
                    hasError = true;
                }

                // 2、更新平台，需要获取加号之前的平台，例子：TK+IG 获取平台为TK
                String platformStr = model.getPlatformTypeStr();
                if (oConvertUtils.isNotEmpty(platformStr) && platformStr.contains("+")) {
                    platformStr = platformStr.split("\\+")[0];
                }
                Integer platformType = getPlatformType(platformStr);
                if (platformType == null) {
                    addToErrorList(model, "平台类型无效: " + model.getPlatformTypeStr(), errorList);
                    hasError = true;
                } else {
                    model.setPlatformType(platformType);
                }


                List<StoreCelebrityPrivate> celebrityPrivates = null;
                StoreCelebrityPrivateProduct existingProduct = null;

                if (!hasError) {
                    // 3、根据平台与网红名称获取私有网红是否存在，如果不存在则返回错误信息
                    String account = model.getAccount().trim().toLowerCase();
                    celebrityPrivates = storeCelebrityPrivateService.lambdaQuery()
                            .eq(StoreCelebrityPrivate::getAccount, account)
                            .eq(StoreCelebrityPrivate::getPlatformType, platformType).list();

                    if(celebrityPrivates.isEmpty()) {
                        addToErrorList(model, String.format("私有网红不存在，平台：%s，账号：%s", platformStr, account), errorList);
                        hasError = true;
                    }
                    for (StoreCelebrityPrivate celebrityPrivate : celebrityPrivates) {
                        // 4、判断是否已经存在历史合作产品，存在则添加到更新列表
                        List<StoreCelebrityPrivateProduct> existingProducts = this.lambdaQuery()
                                .eq(StoreCelebrityPrivateProduct::getCelebrityId, celebrityPrivate.getId())
                                .eq(StoreCelebrityPrivateProduct::getProductId, model.getProductId())
                                .eq(StoreCelebrityPrivateProduct::getBrandId, model.getBrandId()).list();
                        if(!existingProducts.isEmpty()) {
                            existingProduct=existingProducts.get(0);
                        }

                        // 如果没有错误，准备数据库操作
                        if (!hasError) {
                            // 创建或更新产品关联
                            StoreCelebrityPrivateProduct privateProduct = new StoreCelebrityPrivateProduct();
                            if (existingProduct != null) {
                                // 存在则更新
                                privateProduct.setId(existingProduct.getId());
                                privateProduct.setUpdateTime(new Date());
                                updatePrivateProducts.add(privateProduct);
                                log.debug("更新产品关联：网红ID={}, 产品ID={}", celebrityPrivate.getId(), model.getProductId());
                            } else {

                                //判断插入数据集中是否已经存在
                                Optional<StoreCelebrityPrivateProduct> celebrityPrivateProduct = insertPrivateProducts.stream().filter(x -> x.getCelebrityId().equals(model.getProductId()) &&
                                        x.getCelebrityId().equals(celebrityPrivate.getId()) && x.getBrandId().equals(model.getBrandId())).findFirst();
                                if(!celebrityPrivateProduct.isPresent()){
                                    // 5、如果不存在则添加到新增列表
                                    privateProduct.setId(IdWorker.get32UUID());
                                    privateProduct.setCreateTime(new Date());
                                    privateProduct.setSelectionTime(new Date());
                                    insertPrivateProducts.add(privateProduct);
                                }
                                log.debug("新增产品关联：网红ID={}, 产品ID={}", celebrityPrivate.getId(), model.getProductId());
                            }

                            // 设置公共属性
                            privateProduct.setCelebrityId(celebrityPrivate.getId());
                            privateProduct.setProductId(model.getProductId());
                            privateProduct.setBrandId(model.getBrandId());
                            privateProduct.setStartTime(model.getStartTime());
                            privateProduct.setRelationType(0); // 默认为产品推广
                            privateProduct.setRelationStatus(1); // 默认为已选中
                            privateProduct.setDelFlag(0);

                            // 添加到成功列表
                            successList.add(model);
                        }

                    }

                }


                
            } catch (Exception e) {
                log.error("处理数据时发生异常，行号: {}, 账号: {}", model.getRowNum(), model.getAccount(), e);
                addToErrorList(model, "处理异常: " + e.getMessage(), errorList);
            }
        }

        // 6、保存成功的记录到数据库
        if (!successList.isEmpty()) {
            try {
                if (!insertPrivateProducts.isEmpty()) {
                    boolean insertResult = this.saveBatch(insertPrivateProducts);
                    if (!insertResult) {
                        log.error("批量新增数据失败");
                        throw new RuntimeException("批量新增数据失败");
                    }
                    log.info("成功新增 {} 条数据", insertPrivateProducts.size());
                }
                if (!updatePrivateProducts.isEmpty()) {
                    boolean updateResult = this.updateBatchById(updatePrivateProducts);
                    if (!updateResult) {
                        log.error("批量更新数据失败");
                        throw new RuntimeException("批量更新数据失败");
                    }
                    log.info("成功更新 {} 条数据", updatePrivateProducts.size());
                }
                
                // 标记成功的数据
                successList.forEach(model -> {
                    model.setIsUpdate("是");
                    model.setFailReason("");
                    importResult.incrementSuccess();
                });
                
            } catch (Exception e) {
                log.error("数据库保存异常", e);
                // 数据库保存异常，将成功列表中的记录标记为失败
                successList.forEach(model -> {
                    model.setFailReason("保存失败: " + e.getMessage());
                    model.setIsUpdate("否");
                    errorList.add(model);
                    importResult.incrementError();
                });
            }
        }
        
        // 统计错误数量
        errorList.forEach(model -> importResult.incrementError());
        
        log.info("数据处理完成，成功: {}, 失败: {}", importResult.getSuccessCount(), importResult.getErrorCount());
        
        // 更新飞书表格导入状态
        try {
            updateImportStatus(feishuSheet, privateProductModels);
        } catch (Exception e) {
            log.error("更新飞书表格状态失败", e);
            // 这里不影响数据导入结果，只记录日志
        }
        
        return importResult;
    }

    /**
     * 修改飞书导入状态
     * @param feishuSheet 飞书表格配置
     * @param resultList 结果列表
     */
    private void updateImportStatus(KolSysUserFeishuSheet feishuSheet, List<StoreCelebrityPrivateProductModel> resultList) {
        try {
            List<Map<String, Object>> arrayList = new ArrayList<>();
            resultList.forEach(x -> {
                arrayList.add(new HashMap<String, Object>() {{
                    put("range", feishuSheet.getSheetId() + "!" + feishuSheet.getIsSynchronizeColumn() + x.getRowNum() + ":" + feishuSheet.getErrorReasonColumn() + x.getRowNum());
                    put("values", Collections.singletonList(Arrays.asList(x.getIsUpdate(), x.getFailReason()
                    )));
                }});
            });
            
            Map<String, Object> values = new HashMap<String, Object>() {{
                put("valueRanges", arrayList);
            }};
            
            log.debug("开始更新飞书表格导入状态，更新数量: {}", arrayList.size());
            feishuService.updateValuesBatch(feishuSheet, values);
            log.info("成功更新飞书表格导入状态");
            
        } catch (Exception e) {
            log.error("更新飞书表格导入状态失败", e);
            throw e; // 重新抛出异常，让调用方处理
        }
    }
    /**
     * 获取平台类型
     */
    private Integer getPlatformType(String platformTypeStr) {
        if (oConvertUtils.isEmpty(platformTypeStr)) {
            return null;
        }
        switch (platformTypeStr.toUpperCase()) {
            case "TK":
                return 2;
            case "YT":
                return 1;
            case "IG":
                return 0;
            default:
                return null;
        }
    }
    /**
     * 添加到错误列表
     */
    private void addToErrorList(StoreCelebrityPrivateProductModel model, String failReason, Set<StoreCelebrityPrivateProductModel> errorList) {
        Integer recordId = model.getRowNum();
        Optional<StoreCelebrityPrivateProductModel> modelOptional = errorList.stream()
                .filter(x -> x.getRowNum().equals(recordId))
                .findFirst();
        if (modelOptional.isPresent()) {
            StoreCelebrityPrivateProductModel existingModel = modelOptional.get();
            if (!existingModel.getFailReason().contains(failReason)) {
                existingModel.setFailReason(existingModel.getFailReason() + "、" + failReason);
            }
        } else {
            model.setFailReason(failReason);
            model.setIsUpdate("否");
            errorList.add(model);
        }
    }

    /**
     * 将FeishuPrivateProductDto转换为StoreCelebrityPrivateProduct实体
     */
    private List<StoreCelebrityPrivateProduct> convertDtoToEntity(List<FeishuPrivateProductDto> dtoList) {
        List<StoreCelebrityPrivateProduct> entityList = new ArrayList<>();
        
        for (FeishuPrivateProductDto dto : dtoList) {
            try {
                StoreCelebrityPrivateProduct entity = new StoreCelebrityPrivateProduct();
                
                // 基本信息映射
                entity.setSelectionTime(dto.getDate() != null ? dto.getDate() : new Date());
                entity.setCampaignId(dto.getPromotionNumber());
                entity.setBrandId(dto.getBrandName());
                entity.setProductId(dto.getProductName());
                entity.setCelebrityId(dto.getSelectedCelebrity());
                
                // 设置默认的关系类型和状态
                entity.setRelationType(0); // 默认为产品推广
                entity.setRelationStatus(1); // 默认为已选中
                
                // 设置创建时间
                entity.setCreateTime(new Date());
                
                // 将其他信息存储到details字段
                entity.setDetails(createDetailsMap(dto));
                
                entityList.add(entity);
                
            } catch (Exception e) {
                log.warn("转换单条数据失败，跳过该条记录: {}", dto.getPromotionNumber(), e);
            }
        }
        
        return entityList;
    }

    /**
     * 创建details数据映射
     */
    private Map<String, Object> createDetailsMap(FeishuPrivateProductDto dto) {
        Map<String, Object> details = new HashMap<>();
        
        details.put("商务顾问", dto.getBusinessConsultant());
        details.put("网红顾问", dto.getCelebrityConsultant());
        details.put("平台", dto.getPlatform());
        details.put("类目", dto.getCategory());
        details.put("网红报价", dto.getCelebrityQuote());
        details.put("网红成本$", dto.getCelebrityCost());
        details.put("备注", dto.getRemarks());
        details.put("上线链接", dto.getOnlineLink());
        details.put("是否同步", dto.getIsSynchronized());
        details.put("异常原因", dto.getErrorReason());
        
        return details;
    }

    /**
     * 解析开始日期字符串
     * @param startDateStr 日期字符串，支持多种格式
     * @return 解析后的Date对象
     * @throws Exception 解析失败时抛出异常
     */
    private Date parseStartDate(String startDateStr) throws Exception {
        if (oConvertUtils.isEmpty(startDateStr)) {
            return null;
        }
        
        // 去除空格和特殊字符
        String dateStr = startDateStr.trim();
        
        // 支持的日期格式列表
        String[] patterns = {
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd",
            "yyyy/MM/dd HH:mm:ss",
            "yyyy/MM/dd",
            "dd/MM/yyyy",
            "MM/dd/yyyy",
            "yyyy年MM月dd日",
            "yyyyMMdd",
            "dd-MM-yyyy",
            "MM-dd-yyyy"
        };
        
        // 尝试不同的日期格式进行解析
        for (String pattern : patterns) {
            try {
                return DateUtils.parseDate(dateStr, pattern);
            } catch (Exception e) {
                // 继续尝试下一个格式
                log.debug("日期格式[{}]解析失败，尝试下一个格式", pattern);
            }
        }
        
        // 如果所有格式都失败，抛出异常
        throw new IllegalArgumentException("不支持的日期格式: " + startDateStr);
    }

    /**
     * 根据产品名称和品牌名称查找产品
     * @param productName 产品名称
     * @param brandName 品牌名称
     * @return 匹配的产品，如果未找到返回null
     */
    private KolProduct findProductByNameAndBrand(String productName, String brandName) {
        if (oConvertUtils.isEmpty(productName) || oConvertUtils.isEmpty(brandName)) {
            return null;
        }
        
        try {
            // 查询匹配的产品
            KolProduct kolProduct = kolProductService.lambdaQuery()
                    .eq(KolProduct::getProductName, productName)
                    .eq(KolProduct::getBrandName, brandName)
                    .eq(KolProduct::getIsDelete, 0) // 未删除的产品
                    .one();
            
            return kolProduct;
        } catch (Exception e) {
            log.error("查询产品时发生异常，产品名称: {}, 品牌名称: {}", productName, brandName, e);
            throw e;
        }
    }

    /**
     * 根据网红名称、平台、品牌名称、产品名称过滤出重复的数据，并标记为重复数据
     * @param privateProductModels 待处理的数据列表
     */
    private void markDuplicateData(List<StoreCelebrityPrivateProductModel> privateProductModels) {
        if (privateProductModels == null || privateProductModels.isEmpty()) {
            return;
        }
        
        // 使用Map来记录每个组合的首次出现
        Map<String, Boolean> combinationMap = new HashMap<>();
        
        for (StoreCelebrityPrivateProductModel model : privateProductModels) {
            // 检查必要字段是否为空
            if (oConvertUtils.isEmpty(model.getAccount()) || 
                oConvertUtils.isEmpty(model.getProductName()) || 
                oConvertUtils.isEmpty(model.getBrandName())) {
                continue;
            }
            
            // 先解析平台类型
            String platformStr = model.getPlatformTypeStr();
            if (oConvertUtils.isNotEmpty(platformStr) && platformStr.contains("+")) {
                platformStr = platformStr.split("\\+")[0];
            }
            Integer platformType = getPlatformType(platformStr);
            
            if (platformType == null) {
                continue;
            }
            
            // 创建组合键：网红名称(小写) + 平台类型 + 品牌名称 + 产品名称
            String combinationKey = model.getAccount().trim().toLowerCase() + "_" + 
                                  platformType + "_" + 
                                  model.getBrandName().trim() + "_" + 
                                  model.getProductName().trim();
            
            // 检查是否已经存在该组合
            if (combinationMap.containsKey(combinationKey)) {
                // 已经存在，标记为重复数据
                model.setIsRepeat(1);
                model.setIsUpdate("否"); // 重复数据不同步
                model.setFailReason("根据网红名称[" + model.getAccount() + 
                                  "]、平台[" + platformStr + 
                                  "]、品牌[" + model.getBrandName() + 
                                  "]、产品[" + model.getProductName() + 
                                  "]组合过滤出的重复数据");
                log.debug("发现重复数据：网红名称={}, 平台={}, 品牌={}, 产品={}, 行号={}", 
                        model.getAccount(), platformStr, model.getBrandName(), model.getProductName(), model.getRowNum());
            } else {
                // 首次出现，记录该组合
                combinationMap.put(combinationKey, true);
                model.setIsRepeat(0); // 明确标记为非重复数据
                log.debug("首次出现的组合：网红名称={}, 平台={}, 品牌={}, 产品={}, 行号={}", 
                        model.getAccount(), platformStr, model.getBrandName(), model.getProductName(), model.getRowNum());
            }
        }
        
        // 统计重复数据数量
        long duplicateCount = privateProductModels.stream()
                .filter(model -> model.getIsRepeat() == 1)
                .count();
        
        if (duplicateCount > 0) {
            log.info("发现并标记了 {} 条重复数据", duplicateCount);
        } else {
            log.info("未发现重复数据");
        }
    }
}
