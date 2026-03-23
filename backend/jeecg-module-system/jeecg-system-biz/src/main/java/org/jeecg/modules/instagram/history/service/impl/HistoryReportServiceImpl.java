package org.jeecg.modules.instagram.history.service.impl;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.history.entity.*;
import org.jeecg.modules.instagram.history.model.ConversionResult;
import org.jeecg.modules.instagram.history.model.KolHistoryCelebrityDetailFeishuModel;
import org.jeecg.modules.instagram.history.service.*;
import org.jeecg.modules.instagram.history.util.HistoryDataConverter;
import org.jeecg.modules.instagram.storecountry.entity.StoreCountry;
import org.jeecg.modules.instagram.storecountry.service.IStoreCountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Comparator;

/**
 * @Description: 历史提报业务处理服务实现类
 * @Author:
 * @Date: 2025-11-26
 * @Version: V1.0
 */
@Slf4j
@Service
public class HistoryReportServiceImpl implements IHistoryReportService {

    @Autowired
    private IKolHistoryCelebrityService kolHistoryCelebrityService;

    @Autowired
    private IKolHistoryCelebrityDetailService kolHistoryCelebrityDetailService;

    @Autowired
    private IKolHistoryCelebrityKolTypeService kolHistoryCelebrityKolTypeService;

    @Autowired
    private IKolHistoryKolTypeService kolHistoryKolTypeService;

    @Autowired
    private IStoreCountryService storeCountryService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConversionResult processFeishuDataAndSave(List<KolHistoryCelebrityDetailFeishuModel> unSyncedList) {
        // 转换DTO对象到实体类，同时获取转换成功和失败的数据
        ConversionResult conversionResult = convertFeishuModelToEntity(unSyncedList);
        try {
            List<KolHistoryCelebrityDetail> entityList = conversionResult.getImportData();
            List<KolHistoryCelebrityDetailFeishuModel> failedList = conversionResult.getFailedModels();

            // 检查转换后的数据
            if (entityList.isEmpty() && failedList.isEmpty()) {
                log.warn("转换飞书模型数据后结果为空");
                conversionResult.setResult("导入完成，无数据");
                conversionResult.setSuccess(true);
                return conversionResult;
            }

            // 处理数据并保存到各个表中（仅处理转换成功的数据）
            if (!entityList.isEmpty()) {
                processDataAndSave(entityList);
            }
            conversionResult.setResult("导入完成，成功：" + entityList.size() + "条，失败：" + failedList.size() + "条");
            return conversionResult;
        } catch (Exception e) {
            log.error("处理飞书历史提报数据失败", e);
            conversionResult.setResult("处理飞书历史提报数据失败,请联系管理员");
            conversionResult.setSuccess(false);
            return conversionResult;
        }
    }

    @Override
    public void processDataAndSave(List<KolHistoryCelebrityDetail> entityList) throws RuntimeException {
        try {
            // 1. 检查输入数据
            if (entityList == null || entityList.isEmpty()) {
                log.warn("处理数据时输入数据为空");
                return;
            }

            // 按网红名称+平台类型分组
            Map<String, List<KolHistoryCelebrityDetail>> groupedDetails = entityList.stream().filter(detail -> detail != null && StringUtils.isNotEmpty(detail.getCelebrityName()) && detail.getPlatformType() != null).collect(Collectors.groupingBy(detail -> detail.getCelebrityName() + "_" + detail.getPlatformType()));

            // 收集所有需要保存或更新的数据
            List<KolHistoryCelebrity> celebritiesToSave = new ArrayList<>();
            List<KolHistoryCelebrity> celebritiesToUpdate = new ArrayList<>();
            List<KolHistoryCelebrityDetail> detailsToSave = new ArrayList<>();
            List<KolHistoryCelebrityKolType> celebrityKolTypesToSave = new ArrayList<>();
            Set<String> uniqueKolTypes = new HashSet<>();

            List<KolHistoryCelebrity> historyCelebrities = kolHistoryCelebrityService.list();

            // 2. 遍历每组数据进行处理
            for (Map.Entry<String, List<KolHistoryCelebrityDetail>> entry : groupedDetails.entrySet()) {
                List<KolHistoryCelebrityDetail> groupDetails = entry.getValue();

                // 检查分组数据是否为空
                if (groupDetails == null || groupDetails.isEmpty()) {
                    continue;
                }

                try {
                    // 获取最新的数据作为主表数据
                    KolHistoryCelebrityDetail latestDetail = getLatestDetail(groupDetails);

                    // 检查最新数据是否为空
                    if (latestDetail == null) {
                        log.warn("获取最新明细数据失败，跳过处理，分组标识: " + entry.getKey());
                        continue;
                    }

                    // 3. 处理主表 (kol_history_celebrity)
                    KolHistoryCelebrity celebrity = processCelebrity(latestDetail, historyCelebrities);

                    // 检查主表处理结果
                    if (celebrity == null) {
                        log.error("处理主表数据失败，跳过明细数据保存，网红名称: " + latestDetail.getCelebrityName());
                        continue;
                    }

                    // 根据是否有ID判断是新增还是更新（这个逻辑现在是多余的，因为我们已经在processCelebrity中处理了）
                    if (oConvertUtils.isNotEmpty(celebrity.getId())) {
                        // 已存在的记录
                        celebritiesToUpdate.add(celebrity);
                    } else {
                        // 新记录
                        KolHistoryCelebrity finalCelebrity = celebrity;
                        Optional<KolHistoryCelebrity> celebrityOptional = celebritiesToSave.stream().filter(x -> x.getCelebrityName().equalsIgnoreCase(finalCelebrity.getCelebrityName()) && x.getPlatformType().equals(finalCelebrity.getPlatformType())).findFirst();
                        if (!celebrityOptional.isPresent()) {
                            celebrity.setId(IdWorker.get32UUID());
                            celebritiesToSave.add(celebrity);
                        } else {
                            celebrity = celebrityOptional.get();
                        }
                    }

                    // 4. 收集明细表数据 (kol_history_celebrity_detail)
                    collectDetailList(groupDetails, celebrity.getId(), detailsToSave);

                    // 5. 收集达人类型相关数据
                    collectKolTypes(groupDetails, celebrity, celebrityKolTypesToSave, uniqueKolTypes);
                } catch (Exception e) {
                    log.error("处理分组数据时发生错误，分组标识: " + entry.getKey(), e);
                    // 不抛出异常，继续处理其他分组数据
                }
            }

            // 批量保存或更新主表数据
            if (!celebritiesToSave.isEmpty()) {
                boolean saveResult = kolHistoryCelebrityService.saveBatch(celebritiesToSave, 500);
                if (!saveResult) {
                    log.error("批量保存主表数据失败");
                } else {
                    log.info("成功保存 {} 条主表数据", celebritiesToSave.size());
                }
            }

            if (!celebritiesToUpdate.isEmpty()) {
                boolean updateResult = kolHistoryCelebrityService.updateBatchById(celebritiesToUpdate);
                if (!updateResult) {
                    log.error("批量更新主表数据失败");
                } else {
                    log.info("成功更新 {} 条主表数据", celebritiesToUpdate.size());
                }
            }

            // 批量保存明细表数据
            if (!detailsToSave.isEmpty()) {
                boolean saveResult = kolHistoryCelebrityDetailService.saveBatch(detailsToSave);
                if (!saveResult) {
                    log.error("批量保存明细表数据失败");
                } else {
                    log.info("成功保存 {} 条明细表数据", detailsToSave.size());
                }
            }

            // 批量保存达人类型数据
            if (!celebrityKolTypesToSave.isEmpty()) {
                // 先查询已存在的记录，避免重复插入
                Set<String> existingCelebrityKolTypes = new HashSet<>();

                // 分批查询已存在的记录，避免一次性查询太多数据
                for (int i = 0; i < celebrityKolTypesToSave.size(); i += 100) {
                    int endIndex = Math.min(i + 100, celebrityKolTypesToSave.size());
                    List<KolHistoryCelebrityKolType> batch = celebrityKolTypesToSave.subList(i, endIndex);

                    List<String> celebrityIds = batch.stream().map(KolHistoryCelebrityKolType::getCelebrityId).distinct().collect(Collectors.toList());

                    if (!celebrityIds.isEmpty()) {
                        List<KolHistoryCelebrityKolType> existingList = kolHistoryCelebrityKolTypeService.list(new LambdaQueryWrapper<KolHistoryCelebrityKolType>().in(KolHistoryCelebrityKolType::getCelebrityId, celebrityIds));

                        for (KolHistoryCelebrityKolType existing : existingList) {
                            existingCelebrityKolTypes.add(existing.getCelebrityId() + "_" + existing.getKolType());
                        }
                    }
                }

                // 过滤掉已存在的记录
                List<KolHistoryCelebrityKolType> filteredKolTypes = celebrityKolTypesToSave.stream().filter(kolType -> !existingCelebrityKolTypes.contains(kolType.getCelebrityId() + "_" + kolType.getKolType())).collect(Collectors.toList());

                // 保存过滤后的记录（即使是空列表也要尝试保存，确保逻辑正确）
                if (!filteredKolTypes.isEmpty()) {
                    boolean saveResult = kolHistoryCelebrityKolTypeService.saveBatch(filteredKolTypes);
                    if (!saveResult) {
                        log.error("批量保存达人类型数据失败");
                    } else {
                        log.info("成功保存 {} 条达人类型数据", filteredKolTypes.size());
                    }
                } else {
                    log.info("没有新的达人类型数据需要保存");
                }
            }

            // 6. 更新全局达人类型表 (kol_history_kol_type)
            updateGlobalKolTypes(uniqueKolTypes);

            log.info("数据处理完成：主表新增{}条，主表更新{}条，明细表{}条，达人类型{}条", celebritiesToSave.size(), celebritiesToUpdate.size(), detailsToSave.size(), celebrityKolTypesToSave.size());
        } catch (Exception e) {
            log.error("处理数据并保存时发生错误", e);
            throw new RuntimeException("数据处理失败: " + e.getMessage(), e);
        }
    }

    /**
     * 转换飞书模型到实体类，同时返回转换成功和失败的数据
     *
     * @param feishuModelList 飞书模型列表
     * @return 转换结果包装对象
     */
    private ConversionResult convertFeishuModelToEntity(List<KolHistoryCelebrityDetailFeishuModel> feishuModelList) {
        List<KolHistoryCelebrityDetailFeishuModel> successModels = new ArrayList<>();
        List<KolHistoryCelebrityDetailFeishuModel> failedModels = new ArrayList<>();
        List<KolHistoryCelebrityDetail> importData = new ArrayList<>();
        List<StoreCountry> countries = storeCountryService.list();

        for (KolHistoryCelebrityDetailFeishuModel feishuModel : feishuModelList) {
            // 检查模型对象是否为空
            if (feishuModel == null) {
                continue;
            }

            KolHistoryCelebrityDetail entity = new KolHistoryCelebrityDetail();

            try {
                // 基本字段复制
                entity.setBrandName(feishuModel.getBrandName());
                entity.setProductName(feishuModel.getProductName());

                // 安全处理日期字段
                if (StringUtils.isNotEmpty(feishuModel.getSubmitDateText())) {
                    try {
                        Date submitDate = parseDate(feishuModel.getSubmitDateText());
                        if (submitDate != null) {
                            entity.setSubmitDate(submitDate);
                        }
                    } catch (Exception e) {
                        log.warn("无法解析提报日期，日期文本: " + feishuModel.getSubmitDateText());
                        feishuModel.setFailReason("无法解析提报日期: " + feishuModel.getSubmitDateText());
                        failedModels.add(feishuModel);
                        continue;
                    }
                }

                entity.setCelebrityName(feishuModel.getCelebrityName());
                entity.setDeliveryContent(feishuModel.getDeliveryContent());
                entity.setReferencePrice(feishuModel.getReferencePriceText());
                entity.setCountryName(feishuModel.getCountryName());

                // 安全处理粉丝数
                if (StringUtils.isNotEmpty(feishuModel.getFollowersNumText())) {
                    try {
                        Integer followersNum = parseIntegerWithUnit(feishuModel.getFollowersNumText());
                        if (followersNum != null) {
                            entity.setFollowersNum(followersNum);
                        }
                    } catch (Exception e) {
                        log.warn("无法解析粉丝数，粉丝数文本: " + feishuModel.getFollowersNumText());
                        feishuModel.setFailReason("无法解析粉丝数: " + feishuModel.getFollowersNumText());
                        failedModels.add(feishuModel);
                        continue;
                    }
                }

                // 安全处理平均播放量
                if (StringUtils.isNotEmpty(feishuModel.getPlayAvgNumText())) {
                    Integer playAvgNum = parseIntegerWithUnit(feishuModel.getPlayAvgNumText());
                    try {
                        if (playAvgNum != null) {
                            entity.setPlayAvgNum(playAvgNum);
                        }
                    } catch (Exception e) {
                        log.warn("无法解析平均播放量，播放量文本: " + feishuModel.getPlayAvgNumText());
                        feishuModel.setFailReason("无法解析平均播放量: " + feishuModel.getPlayAvgNumText());
                        failedModels.add(feishuModel);
                        continue;
                    }
                }

                entity.setKolType(feishuModel.getKolType());
                entity.setWebUrl(feishuModel.getWebUrl());
                entity.setCelebrityRemark(feishuModel.getCelebrityRemark());

                // 安全处理是否选中
                if (StringUtils.isNotEmpty(feishuModel.getIsSelectedText())) {
                    Integer isSelected = parseSelectedValue(feishuModel.getIsSelectedText());
                    try {
                        if (isSelected != null) {
                            entity.setIsSelected(isSelected);
                        }
                    } catch (Exception e) {
                        log.warn("无法解析是否选中，选中文本: " + feishuModel.getIsSelectedText());
                        feishuModel.setFailReason("无法解析是否选中: " + feishuModel.getIsSelectedText());
                        failedModels.add(feishuModel);
                        continue;
                    }
                }

                entity.setPartyAFeedback(feishuModel.getPartyAFeedback());
                entity.setCelebrityCounselorName(feishuModel.getCelebrityCounselorName());
                entity.setCost(feishuModel.getCostText());
                entity.setCooperationResult(feishuModel.getCooperationResult());
                entity.setCooperationPrice(feishuModel.getCooperationPriceText());
                entity.setRemark(feishuModel.getRemark());

                // 新增字段
                entity.setFailReason(feishuModel.getFailReason());
                entity.setIsUpdate(feishuModel.getIsUpdate());

                // 转换平台类型字符串到整数
                if (StringUtils.isNotEmpty(feishuModel.getPlatformTypeText())) {
                    Integer platformType = HistoryDataConverter.convertPlatformType(feishuModel.getPlatformTypeText());
                    try {
                        if (platformType != null) {
                            entity.setPlatformType(platformType);
                        } else {
                            // 根据链接获取
                            String webUrl = feishuModel.getWebUrl();
                            if (StringUtils.isNotEmpty(webUrl)) {
                                if (webUrl.contains("tiktok")) {
                                    entity.setPlatformType(CommonConstant.TK);
                                } else if (webUrl.contains("youtube")) {
                                    entity.setPlatformType(CommonConstant.YT);
                                } else if (webUrl.contains("instagram")) {
                                    entity.setPlatformType(CommonConstant.IG);
                                } else {
                                    log.warn("转换平台类型失败，平台类型文本: " + feishuModel.getPlatformTypeText());
                                    feishuModel.setFailReason("无法识别的平台类型: " + feishuModel.getPlatformTypeText());
                                    failedModels.add(feishuModel);
                                    continue;
                                }
                            } else {
                                log.warn("转换平台类型失败，平台类型文本: " + feishuModel.getPlatformTypeText());
                                feishuModel.setFailReason("无法识别的平台类型: " + feishuModel.getPlatformTypeText());
                                failedModels.add(feishuModel);
                                continue;
                            }
                        }
                    } catch (Exception e) {
                        log.warn("转换平台类型失败，平台类型文本: " + feishuModel.getPlatformTypeText());
                        feishuModel.setFailReason("无法识别的平台类型: " + feishuModel.getPlatformTypeText());
                        failedModels.add(feishuModel);
                        continue;
                    }
                }

                // 转换性别字符串到整数
                if (StringUtils.isNotEmpty(feishuModel.getGenderText())) {
                    // Integer gender = HistoryDataConverter.convertGender(feishuModel.getGenderText());
                    entity.setGender(feishuModel.getGenderText());
                }

                // 判断国家是否存在
                if (StringUtils.isNotEmpty(feishuModel.getCountryName())) {
                    Optional<StoreCountry> countryOptional = countries.stream().filter(x -> x.getCountry().equals(feishuModel.getCountryName())).findFirst();
                    if (countryOptional.isPresent()) {
                        entity.setCountryName(feishuModel.getCountryName());
                    } else {
                        log.warn("国家不存在，跳过该条数据");
                        feishuModel.setFailReason("国家不存在");
                        failedModels.add(feishuModel);
                        continue;
                    }
                }

                // 检查关键字段是否为空
                if (StringUtils.isEmpty(entity.getCelebrityName())) {
                    log.warn("网红名称为空，跳过该条数据");
                    feishuModel.setFailReason("网红名称为空");
                    failedModels.add(feishuModel);
                    continue;
                }

                // 如果平台类型为空，也视为失败
                if (entity.getPlatformType() == null) {
                    log.warn("平台类型为空，跳过该条数据，网红名称: " + entity.getCelebrityName());
                    feishuModel.setFailReason("平台类型为空");
                    failedModels.add(feishuModel);
                    continue;
                }

                // 只有当关键字段不为空时才添加到成功列表
                importData.add(entity);
                successModels.add(feishuModel);
            } catch (Exception e) {
                log.error("转换飞书模型数据时发生异常，模型数据: " + feishuModel, e);
                feishuModel.setFailReason("转换过程发生异常");
                failedModels.add(feishuModel);
            }
        }

        return new ConversionResult(importData, successModels, failedModels);
    }

    /**
     * 解析日期字符串，支持多种格式
     *
     * @param dateText 日期文本
     * @return 解析后的日期对象
     */
    private Date parseDate(String dateText) {
        if (StringUtils.isEmpty(dateText)) {
            return null;
        }

        // 清理日期文本
        String cleanedDateText = dateText.trim();

        // 定义支持的日期格式
        String[] datePatterns = {"yyyy-MM-dd", "yyyy/MM/dd", "yyyy年M月d日", "yyyy-M-d"};

        for (String pattern : datePatterns) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                sdf.setLenient(false); // 严格解析
                return sdf.parse(cleanedDateText);
            } catch (Exception e) {
                // 继续尝试下一个格式
            }
        }

        log.warn("无法解析日期格式: " + dateText);
        return null;
    }

    /**
     * 解析带单位的整数（如 138.9K, 20K）
     *
     * @param numberText 数字文本
     * @return 解析后的整数
     */
    private Integer parseIntegerWithUnit(String numberText) {
        if (StringUtils.isEmpty(numberText)) {
            return null;
        }

        try {
            // 清理数字文本
            String cleanedNumberText = numberText.trim().toUpperCase();

            // 处理带单位的数字
            double multiplier = 1.0;
            if (cleanedNumberText.endsWith("K")) {
                multiplier = 1000.0;
                cleanedNumberText = cleanedNumberText.substring(0, cleanedNumberText.length() - 1);
            } else if (cleanedNumberText.endsWith("M")) {
                multiplier = 1000000.0;
                cleanedNumberText = cleanedNumberText.substring(0, cleanedNumberText.length() - 1);
            } else if (cleanedNumberText.endsWith("B")) {
                multiplier = 1000000000.0;
                cleanedNumberText = cleanedNumberText.substring(0, cleanedNumberText.length() - 1);
            }

            // 解析数字
            double value = Double.parseDouble(cleanedNumberText);
            return (int) (value * multiplier);
        } catch (Exception e) {
            log.warn("解析带单位的整数失败，数字文本: " + numberText, e);
            return null;
        }
    }

    /**
     * 解析十进制数
     *
     * @param decimalText 十进制数文本
     * @return 解析后的BigDecimal
     */
    private BigDecimal parseDecimal(String decimalText) {
        if (StringUtils.isEmpty(decimalText)) {
            return null;
        }

        try {
            // 清理数字文本
            String cleanedDecimalText = decimalText.trim();
            return new BigDecimal(cleanedDecimalText);
        } catch (Exception e) {
            log.warn("解析十进制数失败，数字文本: " + decimalText, e);
            return null;
        }
    }

    /**
     * 解析选中值（支持Yes/No/是/否等）
     *
     * @param selectedText 选中文本
     * @return 解析后的整数值（0=否, 1=是, 2=未确定）
     */
    private Integer parseSelectedValue(String selectedText) {
        if (StringUtils.isEmpty(selectedText)) {
            return null;
        }

        String cleanedText = selectedText.trim().toLowerCase();

        switch (cleanedText) {
            case "1":
            case "是":
            case "yes":
            case "y":
            case "true":
                return 1;
            case "0":
            case "否":
            case "no":
            case "n":
            case "false":
                return 0;
            case "2":
            case "未确定":
            case "不确定":
            case "unknown":
            case "maybe":
                return 2;
            default:
                log.warn("未知的选中值: " + selectedText);
                return null;
        }
    }

    /**
     * 获取最新的明细数据（按提交日期）
     *
     * @param details 明细数据列表
     * @return 最新的明细数据
     */
    private KolHistoryCelebrityDetail getLatestDetail(List<KolHistoryCelebrityDetail> details) {
        if (details == null || details.isEmpty()) {
            return null;
        }

        return details.stream().filter(Objects::nonNull).max(Comparator.comparing(KolHistoryCelebrityDetail::getSubmitDate, Comparator.nullsFirst(Date::compareTo))).orElse(null);
    }

    /**
     * 处理主表数据
     *
     * @param latestDetail 最新的明细数据
     * @return 主表实体
     */
    private KolHistoryCelebrity processCelebrity(KolHistoryCelebrityDetail latestDetail, List<KolHistoryCelebrity> historyCelebrities) {
        // 检查输入参数
        if (latestDetail == null || StringUtils.isEmpty(latestDetail.getCelebrityName())) {
            log.warn("处理主表数据时输入参数不完整");
            return null;
        }

        try {
            // 根据网红名称和平台类型查询是否已存在主表记录
            Optional<KolHistoryCelebrity> celebrityOptional = historyCelebrities.stream().filter(x -> x.getCelebrityName().equalsIgnoreCase(latestDetail.getCelebrityName()) && x.getPlatformType().equals(latestDetail.getPlatformType())).findFirst();

            KolHistoryCelebrity celebrity;
            if (celebrityOptional.isPresent()) {
                // 更新现有记录
                celebrity = celebrityOptional.get();
                updateCelebrityFromDetail(celebrity, latestDetail);
            } else {
                // 新增记录
                celebrity = new KolHistoryCelebrity();
                updateCelebrityFromDetail(celebrity, latestDetail);
            }

            return celebrity;
        } catch (Exception e) {
            log.error("处理主表数据时发生错误，网红名称: " + latestDetail.getCelebrityName(), e);
            return null;
        }
    }

    /**
     * 从明细数据更新主表数据
     *
     * @param celebrity 主表实体
     * @param detail    明细数据
     */
    private void updateCelebrityFromDetail(KolHistoryCelebrity celebrity, KolHistoryCelebrityDetail detail) {
        if (celebrity == null || detail == null) {
            return;
        }

        celebrity.setCelebrityName(detail.getCelebrityName());
        celebrity.setPlatformType(detail.getPlatformType());
        celebrity.setGender(detail.getGender());
        celebrity.setCountryName(detail.getCountryName());
        celebrity.setFollowersNum(detail.getFollowersNum());
        celebrity.setPlayAvgNum(detail.getPlayAvgNum());
        celebrity.setWebUrl(detail.getWebUrl());
        celebrity.setCelebrityRemark(detail.getCelebrityRemark());
        celebrity.setCelebrityCounselorName(detail.getCelebrityCounselorName());
        celebrity.setBrandName(detail.getBrandName());
        celebrity.setProductName(detail.getProductName());
        celebrity.setLastSubmitDate(detail.getSubmitDate());
    }

    /**
     * 收集明细表数据
     *
     * @param details       明细数据列表
     * @param celebrityId   主表ID
     * @param detailsToSave 收集的明细数据列表
     */
    private void collectDetailList(List<KolHistoryCelebrityDetail> details, String celebrityId, List<KolHistoryCelebrityDetail> detailsToSave) {
        if (details == null || details.isEmpty() || StringUtils.isEmpty(celebrityId)) {
            return;
        }

        for (KolHistoryCelebrityDetail detail : details) {
            if (detail == null) {
                continue;
            }

            detail.setCelebrityId(celebrityId);
            // 如果ID为空则设置新ID
            if (StringUtils.isEmpty(detail.getId())) {
                detail.setId(UUID.randomUUID().toString().replace("-", ""));
            }
            detailsToSave.add(detail);
        }

        log.debug("收集到 {} 条明细数据", detailsToSave.size());
    }

    /**
     * 收集达人类型相关数据
     *
     * @param details                 明细数据列表
     * @param celebrity               主表实体
     * @param celebrityKolTypesToSave 收集的达人类型数据列表
     * @param uniqueKolTypes          收集的唯一达人类型集合
     */
    private void collectKolTypes(List<KolHistoryCelebrityDetail> details, KolHistoryCelebrity celebrity, List<KolHistoryCelebrityKolType> celebrityKolTypesToSave, Set<String> uniqueKolTypes) {
        if (details == null || details.isEmpty() || celebrity == null || StringUtils.isEmpty(celebrity.getId())) {
            log.warn("处理达人类型时参数不完整");
            return;
        }

        // 收集所有达人类型
        Set<String> allKolTypes = new HashSet<>();
        for (KolHistoryCelebrityDetail detail : details) {
            if (detail == null || StringUtils.isEmpty(detail.getKolType())) {
                continue;
            }

            try {
                // 达人类型可能是多个，用逗号分隔，同时处理中英文逗号和分号
                String[] types = detail.getKolType().split("[,，;；]");
                for (String type : types) {
                    if (type != null) {
                        String trimmedType = type.trim();
                        if (StringUtils.isNotEmpty(trimmedType)) {
                            allKolTypes.add(trimmedType);
                            uniqueKolTypes.add(trimmedType);
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("处理达人类型时发生错误，达人类型: " + detail.getKolType(), e);
            }
        }

        // 处理每个达人类型
        for (String kolType : allKolTypes) {
            if (StringUtils.isEmpty(kolType)) {
                continue;
            }

            try {
                // 创建网红达人类型对象
                KolHistoryCelebrityKolType celebrityKolType = new KolHistoryCelebrityKolType();
                celebrityKolType.setId(UUID.randomUUID().toString().replace("-", ""));
                celebrityKolType.setCelebrityId(celebrity.getId());
                celebrityKolType.setCelebrityName(celebrity.getCelebrityName());
                celebrityKolType.setPlatformType(celebrity.getPlatformType());
                celebrityKolType.setKolType(kolType);
                celebrityKolTypesToSave.add(celebrityKolType);
            } catch (Exception e) {
                log.error("创建网红达人类型时发生错误，达人类型: " + kolType, e);
                // 不抛出异常，继续处理其他达人类型
            }
        }

        log.debug("收集到 {} 个达人类型，其中唯一类型 {} 个", allKolTypes.size(), uniqueKolTypes.size());
    }

    /**
     * 更新全局达人类型表
     */
    private void updateGlobalKolTypes(Set<String> uniqueKolTypes) {
        try {
            if (uniqueKolTypes == null || uniqueKolTypes.isEmpty()) {
                log.info("没有需要更新的全局达人类型");
                return;
            }

            // 获取现有的全局达人类型
            List<KolHistoryKolType> existingGlobalKolTypes = kolHistoryKolTypeService.list();
            Set<String> existingTypes = existingGlobalKolTypes.stream().filter(type -> type != null && StringUtils.isNotEmpty(type.getKolType())).map(KolHistoryKolType::getKolType).collect(Collectors.toSet());

            // 找出需要新增的达人类型
            Set<String> newTypes = new HashSet<>(uniqueKolTypes);
            newTypes.removeAll(existingTypes);

            // 批量插入新的达人类型
            List<KolHistoryKolType> newGlobalKolTypes = new ArrayList<>();
            for (String kolType : newTypes) {
                if (StringUtils.isEmpty(kolType)) {
                    continue;
                }

                try {
                    KolHistoryKolType globalKolType = new KolHistoryKolType();
                    globalKolType.setId(UUID.randomUUID().toString().replace("-", ""));
                    globalKolType.setKolType(kolType);
                    globalKolType.setSortCode(0); // 默认排序码
                    globalKolType.setIsDelete(0); // 正常状态
                    newGlobalKolTypes.add(globalKolType);
                } catch (Exception e) {
                    log.error("创建全局达人类型时发生错误，达人类型: " + kolType, e);
                    // 不抛出异常，继续处理其他达人类型
                }
            }

            if (!newGlobalKolTypes.isEmpty()) {
                boolean saveBatchResult = kolHistoryKolTypeService.saveBatch(newGlobalKolTypes);
                if (!saveBatchResult) {
                    log.warn("批量保存全局达人类型失败");
                } else {
                    log.info("成功保存 {} 条全局达人类型数据", newGlobalKolTypes.size());
                }
            } else {
                log.info("没有新的全局达人类型需要保存");
            }
        } catch (Exception e) {
            log.error("更新全局达人类型表时发生错误", e);
        }
    }
}