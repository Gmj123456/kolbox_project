package org.jeecg.modules.instagram.history.service.impl;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;

import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.SheetConstants;
import org.jeecg.modules.feishu.model.FeishuSheetConvertResult;
import org.jeecg.modules.feishu.model.FeishuSheetResponse;
import org.jeecg.modules.feishu.service.IFeishuService;
import org.jeecg.modules.instagram.history.entity.*;
import org.jeecg.modules.instagram.history.model.KolHistoryCelebrityDetailFeishuModel;
import org.jeecg.modules.instagram.history.service.*;
import org.jeecg.modules.instagram.history.util.HistoryDataConverter;
import org.jeecg.modules.kol.entity.KolSysUserFeishuSheet;
import org.jeecg.modules.kol.service.IKolSysUserFeishuSheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description: 历史提报飞书数据处理服务实现
 * @Author:
 * @Date: 2025-11-26
 * @Version: V1.0
 */
@Slf4j
@Service
public class HistoryFeishuServiceImpl implements IHistoryFeishuService {

    @Autowired
    private IFeishuService feishuService;

    @Autowired
    private IKolSysUserFeishuSheetService sheetService;

    @Autowired
    private IKolHistoryCelebrityDetailService kolHistoryCelebrityDetailService;

    @Autowired
    private IKolHistoryCelebrityService kolHistoryCelebrityService;

    @Autowired
    private IKolHistoryCelebrityKolTypeService kolHistoryCelebrityKolTypeService;

    @Autowired
    private IKolHistoryKolTypeService kolHistoryKolTypeService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> importFeishuExcelData() {
        try {
            String tenantAccessToken = feishuService.getInternalTenantAccessToken();
            KolSysUserFeishuSheet feishuSheet = sheetService.lambdaQuery()
                    .eq(KolSysUserFeishuSheet::getSpreadSheetType, SheetConstants.HISTORY)
                    .one();

            if (feishuSheet == null) {
                return Result.error("未找到历史提报飞书表格配置");
            }

            String url = "https://open.feishu.cn/open-apis/sheets/v2/spreadsheets/" +
                    feishuSheet.getSpreadSheetId() + "/values_batch_get?ranges=" +
                    feishuSheet.getSheetId() + "!A1:" + feishuSheet.getEndColumn() +
                    "&valueRenderOption=ToString&dateTimeRenderOption=FormattedString";

            log.info("获取飞书表格数据，URL: {}", url);
            FeishuSheetResponse sheetResponse = feishuService.getFeishuSheetData(url, tenantAccessToken);

            // 将表格数据转换为DTO对象
            FeishuSheetConvertResult<KolHistoryCelebrityDetailFeishuModel> convertResult = feishuService
                    .convertSheetDataToEntityNew(sheetResponse, KolHistoryCelebrityDetailFeishuModel.class);

            List<KolHistoryCelebrityDetailFeishuModel> feishuModelList = convertResult.getEntityList();

            // 过滤出未同步的数据
            List<KolHistoryCelebrityDetailFeishuModel> unSyncedList = feishuModelList.stream()
                    .filter(model -> !"是".equals(model.getIsUpdate()))
                    .collect(Collectors.toList());

            // 转换DTO对象到实体类
            List<KolHistoryCelebrityDetail> entityList = convertFeishuModelToEntity(unSyncedList);

            // 处理数据并保存到各个表中
            processDataAndSave(entityList);

            return Result.ok("导入成功，共处理 " + entityList.size() + " 条数据");
        } catch (Exception e) {
            log.error("导入飞书历史提报数据失败", e);
            return Result.error("导入失败: " + e.getMessage());
        }
    }

    /**
     * 转换飞书模型到实体类
     *
     * @param feishuModelList 飞书模型列表
     * @return 实体类列表
     */
    private List<KolHistoryCelebrityDetail> convertFeishuModelToEntity(
            List<KolHistoryCelebrityDetailFeishuModel> feishuModelList) {
        List<KolHistoryCelebrityDetail> entityList = new ArrayList<>();

        if (feishuModelList == null || feishuModelList.isEmpty()) {
            return entityList;
        }

        for (KolHistoryCelebrityDetailFeishuModel feishuModel : feishuModelList) {
            // 检查模型对象是否为空
            if (feishuModel == null) {
                continue;
            }

            KolHistoryCelebrityDetail entity = new KolHistoryCelebrityDetail();

            // 基本字段复制
            entity.setBrandName(feishuModel.getBrandName());
            entity.setProductName(feishuModel.getProductName());

            // 安全处理日期字段
            if (StringUtils.isNotEmpty(feishuModel.getSubmitDateText())) {
                try {
                    // 解析日期字符串，支持多种格式
                    Date submitDate = parseDate(feishuModel.getSubmitDateText());
                    if (submitDate != null) {
                        entity.setSubmitDate(submitDate);
                    } else {
                        log.warn("无法解析提报日期，日期文本: " + feishuModel.getSubmitDateText());
                    }
                } catch (Exception e) {
                    log.warn("解析提报日期失败，日期文本: " + feishuModel.getSubmitDateText(), e);
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
                    } else {
                        log.warn("无法解析粉丝数，粉丝数文本: " + feishuModel.getFollowersNumText());
                    }
                } catch (Exception e) {
                    log.warn("解析粉丝数失败，粉丝数文本: " + feishuModel.getFollowersNumText(), e);
                }
            }

            // 安全处理平均播放量
            if (StringUtils.isNotEmpty(feishuModel.getPlayAvgNumText())) {
                try {
                    Integer playAvgNum = parseIntegerWithUnit(feishuModel.getPlayAvgNumText());
                    if (playAvgNum != null) {
                        entity.setPlayAvgNum(playAvgNum);
                    } else {
                        log.warn("无法解析平均播放量，播放量文本: " + feishuModel.getPlayAvgNumText());
                    }
                } catch (Exception e) {
                    log.warn("解析平均播放量失败，播放量文本: " + feishuModel.getPlayAvgNumText(), e);
                }
            }

            entity.setKolType(feishuModel.getKolType());
            entity.setWebUrl(feishuModel.getWebUrl());
            entity.setCelebrityRemark(feishuModel.getCelebrityRemark());

            // 安全处理是否选中
            if (StringUtils.isNotEmpty(feishuModel.getIsSelectedText())) {
                try {
                    Integer isSelected = parseSelectedValue(feishuModel.getIsSelectedText());
                    if (isSelected != null) {
                        entity.setIsSelected(isSelected);
                    } else {
                        log.warn("无法解析是否选中，选中文本: " + feishuModel.getIsSelectedText());
                    }
                } catch (Exception e) {
                    log.warn("解析是否选中失败，选中文本: " + feishuModel.getIsSelectedText(), e);
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
                try {
                    Integer platformType = HistoryDataConverter.convertPlatformType(feishuModel.getPlatformTypeText());
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
                                continue;
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warn("转换平台类型失败，平台类型文本: " + feishuModel.getPlatformTypeText());
                    feishuModel.setFailReason("无法识别的平台类型: " + feishuModel.getPlatformTypeText());
                    continue;
                }
            }

            // 转换性别字符串到整数
            if (StringUtils.isNotEmpty(feishuModel.getGenderText())) {
                try {
                    // Integer gender = HistoryDataConverter.convertGender(feishuModel.getGenderText());
                    entity.setGender(feishuModel.getGenderText());
                } catch (Exception e) {
                    log.warn("转换性别失败，性别文本: " + feishuModel.getGenderText(), e);
                }
            }

            // 只有当关键字段不为空时才添加到列表
            if (StringUtils.isNotEmpty(entity.getCelebrityName())) {
                entityList.add(entity);
            } else {
                log.warn("网红名称为空，跳过该条数据");
            }
        }

        return entityList;
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
        String[] datePatterns = {
                "yyyy-MM-dd",
                "yyyy/MM/dd",
                "yyyy年M月d日",
                "yyyy-M-d"
        };

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
            case "Maybe":
                return 2;
            default:
                log.warn("未知的选中值: " + selectedText);
                return null;
        }
    }

    /**
     * 处理数据并保存到各个表中
     *
     * @param detailList 明细数据列表
     */
    private void processDataAndSave(List<KolHistoryCelebrityDetail> detailList) {
        // 1. 按网红名称+平台类型分组
        Map<String, List<KolHistoryCelebrityDetail>> groupedDetails = detailList.stream()
                .collect(Collectors.groupingBy(detail -> detail.getCelebrityName() + "_" + detail.getPlatformType()));

        // 2. 遍历每组数据进行处理
        for (Map.Entry<String, List<KolHistoryCelebrityDetail>> entry : groupedDetails.entrySet()) {
            List<KolHistoryCelebrityDetail> groupDetails = entry.getValue();

            // 获取最新的数据作为主表数据
            KolHistoryCelebrityDetail latestDetail = getLatestDetail(groupDetails);

            // 3. 处理主表 (kol_history_celebrity)
            KolHistoryCelebrity celebrity = processCelebrity(latestDetail);

            // 4. 保存明细表数据 (kol_history_celebrity_detail)
            saveDetailList(groupDetails, celebrity.getId());

            // 5. 处理达人类型相关表
            processKolTypes(groupDetails, celebrity);
        }

        // 6. 更新全局达人类型表 (kol_history_kol_type)
        updateGlobalKolTypes();
    }

    /**
     * 获取最新的明细数据（按提交日期）
     *
     * @param details 明细数据列表
     * @return 最新的明细数据
     */
    private KolHistoryCelebrityDetail getLatestDetail(List<KolHistoryCelebrityDetail> details) {
        return details.stream()
                .max(Comparator.comparing(KolHistoryCelebrityDetail::getSubmitDate,
                        Comparator.nullsFirst(Date::compareTo)))
                .orElse(details.get(0));
    }

    /**
     * 处理主表数据
     *
     * @param latestDetail 最新的明细数据
     * @return 主表实体
     */
    private KolHistoryCelebrity processCelebrity(KolHistoryCelebrityDetail latestDetail) {
        // 根据网红名称和平台类型查询是否已存在主表记录
        KolHistoryCelebrity existingCelebrity = kolHistoryCelebrityService.getByCelebrityNameAndPlatformType(
                latestDetail.getCelebrityName(), latestDetail.getPlatformType());

        KolHistoryCelebrity celebrity;
        if (existingCelebrity != null) {
            // 更新现有记录
            celebrity = existingCelebrity;
            updateCelebrityFromDetail(celebrity, latestDetail);
            kolHistoryCelebrityService.updateById(celebrity);
        } else {
            // 新增记录
            celebrity = new KolHistoryCelebrity();
            updateCelebrityFromDetail(celebrity, latestDetail);
            celebrity.setId(UUID.randomUUID().toString().replace("-", ""));
            kolHistoryCelebrityService.save(celebrity);
        }

        return celebrity;
    }

    /**
     * 从明细数据更新主表数据
     *
     * @param celebrity 主表实体
     * @param detail    明细数据
     */
    private void updateCelebrityFromDetail(KolHistoryCelebrity celebrity, KolHistoryCelebrityDetail detail) {
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
        // 更新时间和创建时间在service中处理
    }

    /**
     * 保存明细表数据
     *
     * @param details     明细数据列表
     * @param celebrityId 主表ID
     */
    private void saveDetailList(List<KolHistoryCelebrityDetail> details, String celebrityId) {
        for (KolHistoryCelebrityDetail detail : details) {
            detail.setCelebrityId(celebrityId);
            // 如果ID为空则设置新ID
            if (StringUtils.isEmpty(detail.getId())) {
                detail.setId(UUID.randomUUID().toString().replace("-", ""));
            }
            kolHistoryCelebrityDetailService.saveOrUpdate(detail);
        }
    }

    /**
     * 处理达人类型相关表
     *
     * @param details   明细数据列表
     * @param celebrity 主表实体
     */
    private void processKolTypes(List<KolHistoryCelebrityDetail> details, KolHistoryCelebrity celebrity) {
        // 收集所有达人类型
        Set<String> allKolTypes = new HashSet<>();
        for (KolHistoryCelebrityDetail detail : details) {
            if (StringUtils.isNotEmpty(detail.getKolType())) {
                // 达人类型可能是多个，用逗号分隔
                String[] types = detail.getKolType().split("[,，]");
                for (String type : types) {
                    String trimmedType = type.trim();
                    if (StringUtils.isNotEmpty(trimmedType)) {
                        allKolTypes.add(trimmedType);
                    }
                }
            }
        }

        // 处理每个达人类型
        for (String kolType : allKolTypes) {
            // 1. 保存到历史提报网红达人类型表 (kol_history_celebrity_kol_type)
            saveCelebrityKolType(celebrity, kolType);
        }
    }

    /**
     * 保存网红达人类型
     *
     * @param celebrity 主表实体
     * @param kolType   达人类型
     */
    private void saveCelebrityKolType(KolHistoryCelebrity celebrity, String kolType) {
        // 检查是否已存在该网红的该达人类型
        KolHistoryCelebrityKolType existing = kolHistoryCelebrityKolTypeService.getByCelebrityIdAndKolType(
                celebrity.getId(), kolType);

        if (existing == null) {
            // 不存在则新增
            KolHistoryCelebrityKolType celebrityKolType = new KolHistoryCelebrityKolType();
            celebrityKolType.setId(UUID.randomUUID().toString().replace("-", ""));
            celebrityKolType.setCelebrityId(celebrity.getId());
            celebrityKolType.setCelebrityName(celebrity.getCelebrityName());
            celebrityKolType.setPlatformType(celebrity.getPlatformType());
            celebrityKolType.setKolType(kolType);
            kolHistoryCelebrityKolTypeService.save(celebrityKolType);
        }
    }

    /**
     * 更新全局达人类型表
     */
    private void updateGlobalKolTypes() {
        // 获取所有不重复的达人类型
        List<KolHistoryCelebrityKolType> allCelebrityKolTypes = kolHistoryCelebrityKolTypeService.list();
        Set<String> uniqueKolTypes = allCelebrityKolTypes.stream()
                .map(KolHistoryCelebrityKolType::getKolType)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toSet());

        // 获取现有的全局达人类型
        List<KolHistoryKolType> existingGlobalKolTypes = kolHistoryKolTypeService.list();
        Set<String> existingTypes = existingGlobalKolTypes.stream()
                .map(KolHistoryKolType::getKolType)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toSet());

        // 找出需要新增的达人类型
        Set<String> newTypes = new HashSet<>(uniqueKolTypes);
        newTypes.removeAll(existingTypes);

        // 批量插入新的达人类型
        List<KolHistoryKolType> newGlobalKolTypes = new ArrayList<>();
        for (String kolType : newTypes) {
            // 检查是否已存在该达人类型
            KolHistoryKolType existing = kolHistoryKolTypeService.getByKolType(kolType);
            if (existing == null) {
                KolHistoryKolType globalKolType = new KolHistoryKolType();
                globalKolType.setId(UUID.randomUUID().toString().replace("-", ""));
                globalKolType.setKolType(kolType);
                globalKolType.setSortCode(0); // 默认排序码
                globalKolType.setIsDelete(0); // 正常状态
                newGlobalKolTypes.add(globalKolType);
            }
        }

        if (!newGlobalKolTypes.isEmpty()) {
            kolHistoryKolTypeService.saveBatch(newGlobalKolTypes);
        }
    }
}