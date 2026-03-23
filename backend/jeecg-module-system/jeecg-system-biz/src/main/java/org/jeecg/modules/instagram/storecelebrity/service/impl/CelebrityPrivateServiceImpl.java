package org.jeecg.modules.instagram.storecelebrity.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.constant.CategoryType;
import org.jeecg.common.system.vo.StoreCelebrityPrivateExcelModel;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.entity.IgCelebrity;
import org.jeecg.modules.instagram.service.IIgCelebrityService;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivate;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivateAttributeRelation;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivateCounselor;
import org.jeecg.modules.instagram.storecelebrity.service.ICelebrityPrivateService;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityPrivateAttributeRelationService;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityPrivateCounselorService;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityPrivateService;
import org.jeecg.modules.instagram.storecountry.entity.StoreCountry;
import org.jeecg.modules.instagram.storecountry.service.IStoreCountryService;
import org.jeecg.modules.instagram.storeseller.storeuser.entity.StoreUserContactEmail;
import org.jeecg.modules.instagram.storeseller.storeuser.service.IStoreUserContactEmailService;
import org.jeecg.modules.instagram.storetags.entity.StoreCelebrityPrivatePersonalTags;
import org.jeecg.modules.instagram.storetags.entity.StorePersonalTags;
import org.jeecg.modules.instagram.storetags.service.IStoreCelebrityPrivatePersonalTagsService;
import org.jeecg.modules.instagram.storetags.service.IStorePersonalTagsService;
import org.jeecg.modules.kol.entity.KolAttribute;
import org.jeecg.modules.kol.entity.KolAttributeType;
import org.jeecg.modules.system.entity.SysDictItem;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecg.modules.tiktok.entity.TiktokCelebrity;
import org.jeecg.modules.tiktok.service.ITiktokCelebrityService;
import org.jeecg.modules.youtube.entity.YoutubeCelebrity;
import org.jeecg.modules.youtube.service.IYtCelebrityService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: nqr
 * @Date: 2025/5/9 11:00
 * @Description:
 **/
@Service
public class CelebrityPrivateServiceImpl implements ICelebrityPrivateService {

    @Autowired
    private IStoreCelebrityPrivateService storeCelebrityPrivateService;

    @Autowired
    private IStoreCelebrityPrivateCounselorService privateCounselorService;

    @Autowired
    private IStoreCelebrityPrivatePersonalTagsService privatePersonalTagsService;
    @Autowired
    private IStoreCelebrityPrivateAttributeRelationService relationService;

    @Autowired
    private IStoreCelebrityPrivateAttributeRelationService privateAttributeRelationService;

    @Autowired
    private IStorePersonalTagsService storePersonalTagsService;

    @Autowired
    private ISysDictService dictService;

    @Autowired
    private IStoreCountryService storeCountryService;

    @Autowired
    private IStoreUserContactEmailService contactEmailService;

    @Autowired
    private ITiktokCelebrityService tiktokCelebrityService;

    @Autowired
    private IYtCelebrityService youtubeCelebrityService;

    @Autowired
    private IIgCelebrityService instagramCelebrityService;


    @Transactional(rollbackFor = Exception.class)
    public void saveAll(List<StoreCelebrityPrivate> listNew, List<StoreCelebrityPrivateCounselor> privateCounselors, List<StoreCelebrityPrivateAttributeRelation> privateAttributeRelations, List<StoreCelebrityPrivatePersonalTags> personalTagsList) {
        if (!listNew.isEmpty()) {
            storeCelebrityPrivateService.saveOrUpdateBatch(listNew);
        }
        if (!privateCounselors.isEmpty()) {
            privateCounselorService.saveOrUpdateBatch(privateCounselors);
        }
        if (!privateAttributeRelations.isEmpty()) {
            List<String> typeIds = privateAttributeRelations.stream().map(StoreCelebrityPrivateAttributeRelation::getAttributeTypeId).distinct().collect(Collectors.toList());
            relationService.lambdaUpdate().in(StoreCelebrityPrivateAttributeRelation::getAttributeTypeId, typeIds).in(StoreCelebrityPrivateAttributeRelation::getCelebrityPrivateId, listNew.stream().map(StoreCelebrityPrivate::getId).collect(Collectors.toList())).remove();
            relationService.saveOrUpdateBatch(privateAttributeRelations);
        }
        if (!personalTagsList.isEmpty()) {
            privatePersonalTagsService.saveBatch(personalTagsList);
        }
    }

    @Override
    public void saveBatchData(List<StoreCelebrityPrivate> processedCelebrities, List<StoreCelebrityPrivateCounselor> counselorRelations, List<StoreCelebrityPrivateAttributeRelation> privateAttributeRelations, List<StoreCelebrityPrivatePersonalTags> privatePersonalTagsList, List<TiktokCelebrity> tiktokCelebrities, List<YoutubeCelebrity> youtubeCelebrities, List<IgCelebrity> igCelebrities) {
        storeCelebrityPrivateService.saveOrUpdateBatch(processedCelebrities);

        if (!counselorRelations.isEmpty()) {
            privateCounselorService.saveOrUpdateBatch(counselorRelations);
        }
        List<String> celebrityPrivateIds = processedCelebrities.stream().map(StoreCelebrityPrivate::getId).collect(Collectors.toList());
        if (!privateAttributeRelations.isEmpty()) {
            privateAttributeRelationService.lambdaUpdate().in(StoreCelebrityPrivateAttributeRelation::getCelebrityPrivateId, celebrityPrivateIds).remove();
            privateAttributeRelationService.saveBatch(privateAttributeRelations);
        }
        if (!privatePersonalTagsList.isEmpty()) {
            List<StorePersonalTags> storePersonalTags = storePersonalTagsService.lambdaQuery().eq(StorePersonalTags::getTagName, "无").list();
            if (!storePersonalTags.isEmpty()) {
                privatePersonalTagsService.lambdaUpdate().eq(StoreCelebrityPrivatePersonalTags::getTagId, storePersonalTags.get(0).getId()).remove();
            }
            privatePersonalTagsService.saveBatch(privatePersonalTagsList);
        }
        if(!tiktokCelebrities.isEmpty()){
            tiktokCelebrityService.saveOrUpdateBatch(tiktokCelebrities);
        }
        if(!youtubeCelebrities.isEmpty()){
            youtubeCelebrityService.saveOrUpdateBatch(youtubeCelebrities);
        }
        if(!igCelebrities.isEmpty()){
            instagramCelebrityService.saveOrUpdateBatch(igCelebrities);
        }
    }

    /**
     * @description: 更新私有网红信息并处理社媒属性（删除旧属性，创建新属性）
     * @author: lf
     * @date: 2025/8/18
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCelebrityWithAttributes(StoreCelebrityPrivate celebrityPrivate, List<StoreCelebrityPrivateAttributeRelation> attributeRelations) {
        // 1. 更新私有网红信息（包括 is_attribute_confirmed）
        storeCelebrityPrivateService.updateById(celebrityPrivate);

        if (!attributeRelations.isEmpty()) {
            // 2. 删除现有的社媒属性关联
            privateAttributeRelationService.lambdaUpdate().in(StoreCelebrityPrivateAttributeRelation::getCelebrityPrivateId, celebrityPrivate.getId()).remove();
            // 4. 保存新的社媒属性关联
            privateAttributeRelationService.saveBatch(attributeRelations);
        }
    }

    /**
     * @description: 批量更新私有网红信息并处理社媒属性（删除旧属性，创建新属性）
     * @author: Grok
     * @date: 2025/8/19
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCelebritiesWithAttributes(List<StoreCelebrityPrivate> celebrities, List<StoreCelebrityPrivateAttributeRelation> allAttributeRelations) {
        // 1. 按 celebrityPrivateId 分组属性关联
        Map<String, List<StoreCelebrityPrivateAttributeRelation>> relationsByCelebrityId = allAttributeRelations.stream().collect(Collectors.groupingBy(StoreCelebrityPrivateAttributeRelation::getCelebrityPrivateId));

        // 2. 遍历每个网红，更新信息并处理属性
        for (StoreCelebrityPrivate celebrity : celebrities) {
            // 1. 更新私有网红信息（包括 is_attribute_confirmed）
            storeCelebrityPrivateService.updateById(celebrity);
            // 获取该网红的新属性关联
            List<StoreCelebrityPrivateAttributeRelation> attributeRelations = relationsByCelebrityId.getOrDefault(celebrity.getId(), new ArrayList<>());

            // 保存新的社媒属性关联
            if (!attributeRelations.isEmpty()) {
                // 2. 删除现有的社媒属性关联
                privateAttributeRelationService.lambdaUpdate().in(StoreCelebrityPrivateAttributeRelation::getCelebrityPrivateId, celebrity.getId()).remove();
                // 4. 保存新的社媒属性关联
                privateAttributeRelationService.saveBatch(attributeRelations);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> importStoreCelebrityPrivate(List<StoreCelebrityPrivateExcelModel> list, String userId, String userName) {

        Map<String, Object> resultMap = new HashMap<>();

        list = list.stream().filter(x -> oConvertUtils.isNotEmpty(x.getAccount())).collect(Collectors.toList());
        if (list.isEmpty()) {
            return null;
        }
        Set<StoreCelebrityPrivateExcelModel> errorList = new HashSet<>();
        // 判断是否存在重复数据
        List<StoreCelebrityPrivateExcelModel> duplicates = findDuplicateEntries(list);
        if (!duplicates.isEmpty()) {
            // return Result.ok("文件导入失败，存在重复数据！", duplicates);
            duplicates.forEach(x -> addToErrorList(x, "存在重复数据", errorList));
            // 从list中删除重复数据
            list.removeAll(duplicates);
        }

        // -------------增加校验 网红邮箱字段 不能为空 2024年11月30日 08:22:37 ----------------------------
        List<StoreCelebrityPrivateExcelModel> emptyEmailList = list.stream().filter(x -> oConvertUtils.isEmpty(x.getEmail())).collect(Collectors.toList());
        if (!emptyEmailList.isEmpty()) {
            emptyEmailList.forEach(x -> addToErrorList(x, "网红邮箱不能为空", errorList));
            list.removeAll(emptyEmailList);
        }
        //-------------------------------------end----------------------------------------------------
        List<StoreCelebrityPrivateExcelModel> emptyContactEmailList = list.stream().filter(x -> oConvertUtils.isEmpty(x.getCelebrityContactEmail())).collect(Collectors.toList());
        if (!emptyContactEmailList.isEmpty()) {
            emptyContactEmailList.forEach(x -> addToErrorList(x, "建联邮箱不能为空", errorList));
            list.removeAll(emptyContactEmailList);
        }
        List<StoreCelebrityPrivateExcelModel> excelModelList = list;
        // 处理多个类目
        /*        for (StoreCelebrityPrivateExcelModel model : list) {
         *//*Set<String> influencerTypeSet = Stream.of(model.getInfluencerType1(), model.getInfluencerType2(), model.getInfluencerType3())
                            .filter(Objects::nonNull)
                            .filter(oConvertUtils::isNotEmpty)
                            .collect(Collectors.toSet());
                    if (influencerTypeSet.isEmpty()) {
                        addToErrorList(model, "至少选择一个达人类型", errorList);
                        continue;
                    }
                    model.setInfluencerTypes(String.join(",", influencerTypeSet));
                    Set<String> sceneSet = Stream.of(model.getScene1(), model.getScene2(), model.getScene3())
                            .filter(Objects::nonNull)
                            .filter(oConvertUtils::isNotEmpty)
                            .collect(Collectors.toSet());
                    model.setScenes(String.join(",", sceneSet));
                    Set<String> audienceSet = Stream.of(model.getAudience1(), model.getAudience2(), model.getAudience3())
                            .filter(Objects::nonNull)
                            .filter(oConvertUtils::isNotEmpty)
                            .collect(Collectors.toSet());
                    model.setAudiences(String.join(",", audienceSet));*//*
            model.setAccount(model.getAccount().trim());
            model.setPlayAvgNumStr(appendKIfMissing(model.getPlayAvgNumStr()));
            model.setFollowersNumStr(appendKIfMissing(model.getFollowersNumStr()));
            excelModelList.add(model);
        }*/
        List<StoreCountry> countries = storeCountryService.list();

        List<SysDictItem> platformTypeList = dictService.getItemList("platform_type");


        List<StoreCelebrityPrivate> listNew = new ArrayList<>();
        List<StoreCelebrityPrivatePersonalTags> personalTagsList = new ArrayList<>();
        List<StoreCelebrityPrivateAttributeRelation> privateAttributeRelations = new ArrayList<>();
        List<StoreCelebrityPrivateExcelModel> successImportList = new ArrayList<>();

        // 校验上传字段，并创建私有网红数据
        createPrivateCelebrityList(excelModelList, errorList, platformTypeList, listNew, countries, personalTagsList, privateAttributeRelations, userId, userName, successImportList);

      /*  if (!errorList.isEmpty()) {
            return Result.ok("文件导入失败，存在错误数据！", errorList);
        }*/

        List<StoreCelebrityPrivateCounselor> privateCounselors = new ArrayList<>();
        // 创建网红顾问和网红关联关系
        createPrivateCounselors(listNew, userId, userName, privateCounselors);

        saveAll(listNew, privateCounselors, privateAttributeRelations, personalTagsList);

        resultMap.put("successImportList", successImportList);
        resultMap.put("errorList", errorList);
        return resultMap;
    }

    private void createPrivateCounselors(List<StoreCelebrityPrivate> listNew, String userId, String userName, List<StoreCelebrityPrivateCounselor> privateCounselors) {
        List<String> celebrityPrivateIds = listNew.stream().map(StoreCelebrityPrivate::getId).collect(Collectors.toList());
        if (celebrityPrivateIds.isEmpty()) {
            return;
        }
        List<StoreCelebrityPrivateCounselor> celebrityPrivateCounselors = privateCounselorService.lambdaQuery().eq(StoreCelebrityPrivateCounselor::getCelebrityCounselorId, userId).in(StoreCelebrityPrivateCounselor::getCelebrityPrivateId, celebrityPrivateIds).list();
        // 查询网红顾问是否存在
        for (StoreCelebrityPrivate storeCelebrityPrivate : listNew) {
            StoreCelebrityPrivateCounselor privateCounselor = new StoreCelebrityPrivateCounselor();
            // 查询是否存在相同网红顾问
            StoreCelebrityPrivateCounselor counselor = celebrityPrivateCounselors.stream().filter(x -> x.getCelebrityPrivateId().equals(storeCelebrityPrivate.getId()) && x.getCelebrityCounselorId().equals(userId)).findFirst().orElse(null);
            if (counselor == null) {
                // 判断新增列表中是否存在
                Optional<StoreCelebrityPrivateCounselor> privateCounselorOptional = privateCounselors.stream().filter(x -> x.getCelebrityPrivateId().equals(storeCelebrityPrivate.getId()) && x.getCelebrityCounselorId().equals(userId)).findFirst();
                if (privateCounselorOptional.isPresent()) {
                    continue;
                }
                privateCounselor.setId(IdWorker.get32UUID());
            } else {
                privateCounselor.setId(counselor.getId());
            }
            // storeCelebrityPrivate.setId(IdWorker.get32UUID());
            privateCounselor.setCelebrityPrivateId(storeCelebrityPrivate.getId());
            privateCounselor.setCelebrityCounselorId(userId);
            privateCounselor.setCelebrityCounselorName(userName);
            privateCounselor.setContractAmount(storeCelebrityPrivate.getContractAmount());
            privateCounselor.setCelebrityContactEmail(storeCelebrityPrivate.getCelebrityContactEmail());
            privateCounselor.setEmail(storeCelebrityPrivate.getEmail());
            privateCounselor.setContractTime(new Date());
            privateCounselor.setSort(1);
            privateCounselor.setContractInfo(storeCelebrityPrivate.getContractInfo());
            privateCounselor.setRemark(storeCelebrityPrivate.getRemark());
            // storeCelebrityPrivate.setRemark(null);
            privateCounselors.add(privateCounselor);
        }
    }

    private void createPrivateCelebrityList(List<StoreCelebrityPrivateExcelModel> list, Set<StoreCelebrityPrivateExcelModel> errorList,
                                            List<SysDictItem> platformTypeList, List<StoreCelebrityPrivate> listNew, List<StoreCountry> countries,
                                            List<StoreCelebrityPrivatePersonalTags> personalTagsList, List<StoreCelebrityPrivateAttributeRelation> privateAttributeRelations,
                                            String userId, String userName, List<StoreCelebrityPrivateExcelModel> successImportList) {
        LambdaQueryWrapper<StoreUserContactEmail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StoreUserContactEmail::getSysUserId, userId);
        List<StoreUserContactEmail> contactEmailList = contactEmailService.list(queryWrapper);
        // 查询所有 kol_attribute 记录
        // List<KolAttribute> kolAttributes = kolAttributeService.list();
        List<KolAttribute> kolAttributes = new ArrayList<>();

        // 获取属性类型 ID（假设存在 KolAttributeType 表）
        // List<KolAttributeType> kolAttributeTypes = kolAttributeTypeService.list();
        List<KolAttributeType> kolAttributeTypes = new ArrayList<>();
        for (StoreCelebrityPrivateExcelModel storeCelebrityPrivateExcelModel : list) {
            String celebrityContactEmail = storeCelebrityPrivateExcelModel.getCelebrityContactEmail();
            // 基础字段验证
            if (!validateBasicFields(storeCelebrityPrivateExcelModel, errorList)) {
                continue;
            }

            // 校验建联邮箱是否正确
            if (contactEmailList.stream().noneMatch(x -> x.getContactEmail().equals(celebrityContactEmail))) {
                addToErrorList(storeCelebrityPrivateExcelModel, "建联邮箱不存在", errorList);
                continue;
            }

            // 账号验证,去除空格,转小写
            String account = StringUtils.lowerCase(storeCelebrityPrivateExcelModel.getAccount().trim());
            storeCelebrityPrivateExcelModel.setAccount(account);

            // 2025年8月27日09:33:15  注释根据内容判断平台，改为在excel中获取平台
            // String videoTags = storeCelebrityPrivateExcelModel.getVideoTags();
            // int platformType = getPlatformType(platformTypeList, videoTags);

            int platformType = storeCelebrityPrivateExcelModel.getPlatformType();

           /* if (isDuplicateAccount(account, platformType)) {
                addToErrorList(storeCelebrityPrivateExcelModel, "当前账号已存在", errorList);
                continue;
            }*/

            // 国家验证
            StoreCountry storeCountry = validateCountry(storeCelebrityPrivateExcelModel, countries, errorList);
            if (storeCountry == null) {
                addToErrorList(storeCelebrityPrivateExcelModel, "国家不存在", errorList);
                continue;
            }
            StoreCelebrityPrivate storeCelebrityPrivate = new StoreCelebrityPrivate();
            // 检查网红账号是否已存在，如存在则更新ID
            updateExistingEntityId(storeCelebrityPrivate, account, platformType);
            // 创建实体并设置属性
            StoreCelebrityPrivate celebrityPrivate = createCelebrityEntity(storeCelebrityPrivateExcelModel, storeCelebrityPrivate, platformType, storeCountry.getId(), personalTagsList, privateAttributeRelations, errorList, kolAttributes, kolAttributeTypes, userId, userName);
            if (celebrityPrivate == null) {
                continue;
            }

            Optional<StoreCelebrityPrivate> privateOptional = listNew.stream().filter(x -> x.getAccount().equals(account) && x.getPlatformType().equals(platformType)).findFirst();
            if (privateOptional.isPresent()) {
                continue;
            }
            successImportList.add(storeCelebrityPrivateExcelModel);
            listNew.add(celebrityPrivate);
        }
    }

    /**
     * @description: 判断是否存在重复数据
     * @author: nqr
     * @date: 2025/7/11 9:50
     **/
    public List<StoreCelebrityPrivateExcelModel> findDuplicateEntries(List<StoreCelebrityPrivateExcelModel> list) {
        // 按照账号和平台进行分组，判断是否存在相同数据
        Map<String, List<StoreCelebrityPrivateExcelModel>> groupedData = list.stream().collect(Collectors.groupingBy(model -> model.getAccount() + "#" + model.getPlatformTypeText()));
        // 筛选出计数大于 1 的重复项
        List<StoreCelebrityPrivateExcelModel> duplicates = new ArrayList<>();
        groupedData.forEach((key, modelList) -> {
            if (modelList.size() > 1) {
                modelList.forEach(model -> model.setFailReason("数据重复"));
                duplicates.addAll(modelList);
            }
        });
        return duplicates;
    }


    /**
     * 验证基本字段
     */
    private boolean validateBasicFields(StoreCelebrityPrivateExcelModel model, Set<StoreCelebrityPrivateExcelModel> errorList) {
        // 验证账号
        String account = model.getAccount();
        if (oConvertUtils.isEmpty(account)) {
            addToErrorList(model, "账号不能为空", errorList);
            return false;
        }

        if (account.trim().contains(" ")) {
            addToErrorList(model, "账号不能包含空格", errorList);
            return false;
        }

        // 验证邮箱
        if (!oConvertUtils.isValidEmail(model.getEmail())) {
            addToErrorList(model, "邮箱格式不正确", errorList);
            return false;
        }

        // 验证内容标签
        String videoTags = model.getVideoTags();
        String videoTags2 = model.getVideoTags2();
        String videoTags3 = model.getVideoTags3();
        if (oConvertUtils.isEmpty(videoTags) && oConvertUtils.isEmpty(videoTags2) && oConvertUtils.isEmpty(videoTags3)) {
            addToErrorList(model, "内容不能为空", errorList);
            return false;
        }
        Set<String> videoTagsSet = new HashSet<>();
        List<String> duplicateTags = new ArrayList<>();

        if (oConvertUtils.isNotEmpty(videoTags)) {
            videoTagsSet.add(videoTags);
        }
        if (oConvertUtils.isNotEmpty(videoTags2) && !videoTagsSet.add(videoTags2)) {
            duplicateTags.add(videoTags2);
        }
        if (oConvertUtils.isNotEmpty(videoTags3) && !videoTagsSet.add(videoTags3)) {
            duplicateTags.add(videoTags3);
        }

        if (!duplicateTags.isEmpty()) {
            addToErrorList(model, "内容重复", errorList);
            return false;
        }

        List<SysDictItem> platformTypeDict = dictService.getItemList("platform_type");
        SysDictItem sysDictItem = platformTypeDict.stream().filter(x -> x.getItemText().equals(model.getPlatformTypeText())).findFirst().orElse(null);
        if (sysDictItem == null && oConvertUtils.isEmpty(model.getPlatformType())) {
            addToErrorList(model, "平台类型不正确", errorList);
            return false;
        }
        int platformType;
        if (sysDictItem != null) {
            platformType = Integer.parseInt(sysDictItem.getItemValue());
        } else {
            platformType = model.getPlatformType();
        }
        model.setPlatformType(platformType);

        List<SysDictItem> videoTagsList = dictService.getItemList("celebrity_content");

        SysDictItem dictItem = videoTagsList.stream().filter(x -> x.getItemText().equals(String.valueOf(platformType))).findFirst().orElse(null);
        if (dictItem == null) {
            addToErrorList(model, "平台类型不正确", errorList);
            return false;
        }
        String videoTagsDict = dictItem.getItemValue();
        if ((oConvertUtils.isNotEmpty(videoTags) && !videoTagsDict.contains(videoTags)) || (oConvertUtils.isNotEmpty(videoTags2) && !videoTagsDict.contains(videoTags2)) || (oConvertUtils.isNotEmpty(videoTags3) && !videoTagsDict.contains(videoTags3))) {
            addToErrorList(model, "内容不正确", errorList);
            return false;
        }

        // 验证性别
        String genderText = Optional.ofNullable(model.getGenderText()).orElse("");
        Integer gender = model.getGender();
        if (oConvertUtils.isEmpty(genderText) && oConvertUtils.isEmpty(gender)) {
            addToErrorList(model, "性别不能为空", errorList);
            return false;
        }

//        // 验证类目
//        if (oConvertUtils.isEmpty(model.getLikeTags())) {
//            addToErrorList(model, "类目不能为空", errorList);
//            return false;
//        }

        // 验证粉丝数
        /*String followersNumStr = model.getFollowersNumStr();
        if (oConvertUtils.isEmpty(followersNumStr)) {
            addToErrorList(model, "粉丝数不能为空", errorList);
            return false;
        }
        try {
            int followersNum = getNum(followersNumStr);
            model.setFollowersNum(followersNum);
        } catch (Exception e) {
            addToErrorList(model, "粉丝数类型错误", errorList);
            return false;
        }
      if (oConvertUtils.isEmpty(model.getPlayAvgNumStr())) {
            model.setFailReason("均播不能为空");
            errorList.add(model);
            return false;
        }*/
        // 验证签约费用
        String contractAmountStr = model.getContractAmountStr();
        String contractAmountStr2 = model.getContractAmountStr2();
        String contractAmountStr3 = model.getContractAmountStr3();
        if (oConvertUtils.isEmpty(contractAmountStr) && oConvertUtils.isEmpty(contractAmountStr2) && oConvertUtils.isEmpty(contractAmountStr3)) {
            addToErrorList(model, "签约费用不能为空", errorList);
            return false;
        }

        try {
            if (oConvertUtils.isNotEmpty(contractAmountStr)) {
                model.setContractAmount(new BigDecimal(contractAmountStr));
            }
            if (oConvertUtils.isNotEmpty(contractAmountStr2)) {
                model.setContractAmount2(new BigDecimal(contractAmountStr2));
            }
            if (oConvertUtils.isNotEmpty(contractAmountStr3)) {
                model.setContractAmount3(new BigDecimal(contractAmountStr3));
            }
        } catch (Exception e) {
            addToErrorList(model, "签约费用格式不正确", errorList);
            return false;
        }

        return true;
    }

    /**
     * 检查账号是否重复
     */
    private boolean isDuplicateAccount(String account, int platformType) {
        StoreCelebrityPrivate existing = storeCelebrityPrivateService.checkCelebrityPrivate(account, platformType);
        return existing != null;
    }


    private void addToErrorList(StoreCelebrityPrivateExcelModel model, String failReason, Set<StoreCelebrityPrivateExcelModel> errorList) {
        String account = model.getAccount();
        Optional<StoreCelebrityPrivateExcelModel> modelOptional = errorList.stream().filter(x -> x.getAccount().equals(account)).findFirst();
        if (modelOptional.isPresent()) {
            model = modelOptional.get();
            String existingFailReason = model.getFailReason();

            // 将现有的失败原因按分隔符拆分，并去重
            Set<String> reasonSet = new LinkedHashSet<>();
            if (oConvertUtils.isNotEmpty(existingFailReason)) {
                String[] existingReasons = existingFailReason.split("、");
                for (String reason : existingReasons) {
                    String trimmedReason = reason.trim();
                    if (oConvertUtils.isNotEmpty(trimmedReason)) {
                        reasonSet.add(trimmedReason);
                    }
                }
            }

            // 添加新的失败原因（如果不存在）
            if (oConvertUtils.isNotEmpty(failReason)) {
                reasonSet.add(failReason.trim());
            }

            // 重新组装去重后的失败原因
            model.setFailReason(String.join("、", reasonSet));
        } else {
            model.setFailReason(failReason);
            errorList.add(model);
        }
    }

    /**
     * 验证国家信息
     */
    private StoreCountry validateCountry(StoreCelebrityPrivateExcelModel model, List<StoreCountry> countries, Set<StoreCelebrityPrivateExcelModel> errorList) {
        return countries.stream().filter(x -> x.getCountry().equals(model.getCountryName())).findFirst().orElseGet(() -> {
            addToErrorList(model, "国家不存在", errorList);
            return null;
        });
    }

    /**
     * 创建实体并设置属性
     */
    private StoreCelebrityPrivate createCelebrityEntity(StoreCelebrityPrivateExcelModel storeCelebrityPrivateExcelModel, StoreCelebrityPrivate storeCelebrityPrivate,
                                                        int platformType, String countryId, List<StoreCelebrityPrivatePersonalTags> personalTagsList,
                                                        List<StoreCelebrityPrivateAttributeRelation> privateAttributeRelations, Set<StoreCelebrityPrivateExcelModel> errorList,
                                                        List<KolAttribute> kolAttributes, List<KolAttributeType> kolAttributeTypes, String userId, String userName) {

        BeanUtils.copyProperties(storeCelebrityPrivateExcelModel, storeCelebrityPrivate, "id", "remark");

        String newRemark = oConvertUtils.isNotEmpty(storeCelebrityPrivateExcelModel.getRemark()) ? storeCelebrityPrivateExcelModel.getRemark() : null;
        String existingRemark = oConvertUtils.isNotEmpty(storeCelebrityPrivate.getRemark()) ? storeCelebrityPrivate.getRemark() : null;
        storeCelebrityPrivate.setRemark(newRemark != null && existingRemark != null ? newRemark + "\n" + existingRemark : newRemark != null ? newRemark : existingRemark);

        storeCelebrityPrivate.setPlatformType(platformType);
        String genderText = Optional.ofNullable(storeCelebrityPrivateExcelModel.getGenderText()).orElse("");
        storeCelebrityPrivate.setGender(genderText.equals("男") ? 0 : 1);

        // String playAvgNumStr = storeCelebrityPrivateExcelModel.getPlayAvgNumStr();
        // String followersNumStr = storeCelebrityPrivateExcelModel.getFollowersNumStr();

        // storeCelebrityPrivate.setPlayAvgNum(getNum(playAvgNumStr));
        // storeCelebrityPrivate.setFollowersNum(getNum(followersNumStr));
        storeCelebrityPrivate.setContractTime(new Date());
        storeCelebrityPrivate.setUpdateTime(new Date());
        storeCelebrityPrivate.setCountryId(countryId);
        storeCelebrityPrivate.setIsMcn(0);
        storeCelebrityPrivate.setCelebrityCounselorId(userId);
        storeCelebrityPrivate.setCelebrityCounselorName(userName);
        storeCelebrityPrivate.setCelebrityContactEmail(storeCelebrityPrivateExcelModel.getCelebrityContactEmail());

        // 拼接内容和费用 2025年8月27日09:50:58 [{"videoTag": "TikTok视频", "contractAmount": 1}, {"videoTag": "TK直播", "contractAmount": 1}]
        String videoTags = storeCelebrityPrivateExcelModel.getVideoTags();
        String videoTags2 = storeCelebrityPrivateExcelModel.getVideoTags2();
        String videoTags3 = storeCelebrityPrivateExcelModel.getVideoTags3();
        BigDecimal contractAmount = storeCelebrityPrivateExcelModel.getContractAmount();
        BigDecimal contractAmount2 = storeCelebrityPrivateExcelModel.getContractAmount2();
        BigDecimal contractAmount3 = storeCelebrityPrivateExcelModel.getContractAmount3();

        // 创建结果列表
        List<Map<String, Object>> resultList = new ArrayList<>();

        if (videoTags != null && !videoTags.trim().isEmpty() && contractAmount != null) {
            Map<String, Object> pair = new HashMap<>();
            pair.put("videoTag", videoTags.trim());
            pair.put("contractAmount", contractAmount);
            resultList.add(pair);
        }

        if (videoTags2 != null && !videoTags2.trim().isEmpty() && contractAmount2 != null) {
            Map<String, Object> pair = new HashMap<>();
            pair.put("videoTag", videoTags2.trim());
            pair.put("contractAmount", contractAmount2);
            resultList.add(pair);
        }

        if (videoTags3 != null && !videoTags3.trim().isEmpty() && contractAmount3 != null) {
            Map<String, Object> pair = new HashMap<>();
            pair.put("videoTag", videoTags3.trim());
            pair.put("contractAmount", contractAmount3);
            resultList.add(pair);
        }

        storeCelebrityPrivate.setContractInfo(JSON.toJSONString(resultList));
        // 处理个性化标签 2025年5月9日11:42:50
        // 1、对标签进行去空格和转小写
        // 2、查询标签是否存在，不存在需要创建标签，并设置新增标签类目为未分类（字典配置）
        // 3、新增标签和网红关联关系
        // 4、判断标签是否删除，已删除则跳过当前行数据

        String personalTags = storeCelebrityPrivate.getPersonalTags();
        String labelsErrorMsg = storeCelebrityPrivateService.processingPersonalizedLabels(personalTagsList, storeCelebrityPrivate.getId(), personalTags);
        if (oConvertUtils.isNotEmpty(labelsErrorMsg)) {
            addToErrorList(storeCelebrityPrivateExcelModel, labelsErrorMsg, errorList);
            return null;
        }
        // 2025年8月18日17:40:41 去除私有网红导入社媒属性
        // boolean flag = setAttributeRelation(privateAttributeRelations, storeCelebrityPrivate, errorList, storeCelebrityPrivateExcelModel, kolAttributes, kolAttributeTypes);
        return storeCelebrityPrivate;
        // return storeCelebrityPrivate;
    }

    /**
     * 设置属性关联
     */
    private boolean setAttributeRelation(List<StoreCelebrityPrivateAttributeRelation> privateAttributeRelations, StoreCelebrityPrivate storeCelebrityPrivate, Set<StoreCelebrityPrivateExcelModel> errorList, StoreCelebrityPrivateExcelModel storeCelebrityPrivateExcelModel, List<KolAttribute> kolAttributes, List<KolAttributeType> kolAttributeTypes, String createBy, String userName) {


        String celebrityPrivateId = storeCelebrityPrivate.getId();
        String account = storeCelebrityPrivate.getAccount();
        Date createTime = new Date();
        boolean isValid = true;

        // 处理 influencerTypes
        if (oConvertUtils.isNotEmpty(storeCelebrityPrivateExcelModel.getInfluencerTypes())) {
            if (!createAttributeRelations(storeCelebrityPrivateExcelModel.getInfluencerTypes(), CategoryType.INFLUENCER_TYPE_ID, CategoryType.INFLUENCER_TYPE_NAME, celebrityPrivateId, account, kolAttributes, privateAttributeRelations, errorList, storeCelebrityPrivateExcelModel, createBy, createTime)) {
                isValid = false;
            }
        }

        // 处理 scenes
        if (oConvertUtils.isNotEmpty(storeCelebrityPrivateExcelModel.getScenes())) {
            if (!createAttributeRelations(storeCelebrityPrivateExcelModel.getScenes(), CategoryType.SCENE_ID, CategoryType.SCENE_NAME, celebrityPrivateId, account, kolAttributes, privateAttributeRelations, errorList, storeCelebrityPrivateExcelModel, createBy, createTime)) {
                isValid = false;
            }
        }

        // 处理 audiences
        if (oConvertUtils.isNotEmpty(storeCelebrityPrivateExcelModel.getAudiences())) {
            if (!createAttributeRelations(storeCelebrityPrivateExcelModel.getAudiences(), CategoryType.AUDIENCE_ID, CategoryType.AUDIENCE_NAME, celebrityPrivateId, account, kolAttributes, privateAttributeRelations, errorList, storeCelebrityPrivateExcelModel, createBy, createTime)) {
                isValid = false;
            }
        }

        return isValid;
    }

    /**
     * 创建属性关联记录
     */
    private boolean createAttributeRelations(String attributeString, String attributeTypeCode, String attributeTypeName, String celebrityPrivateId, String account, List<KolAttribute> kolAttributes, List<StoreCelebrityPrivateAttributeRelation> privateAttributeRelations, Set<StoreCelebrityPrivateExcelModel> errorList, StoreCelebrityPrivateExcelModel model, String createBy, Date createTime) {
        boolean isValid = true;
        String[] attributes = attributeString.split(",");

        for (String attrName : attributes) {
            if (oConvertUtils.isEmpty(attrName)) {
                continue;
            }
            String trimmedAttrName = attrName.trim(); // 创建新的局部变量

            KolAttribute attribute = kolAttributes.stream().filter(a -> a.getAttributeName().equals(trimmedAttrName) && a.getAttributeTypeId().equals(attributeTypeCode)).findFirst().orElse(null);

            if (attribute == null) {
                addToErrorList(model, String.format("属性 [%s] 在类型 [%s] 中不存在", trimmedAttrName, attributeTypeName), errorList);
                isValid = false;
                continue;
            }

            // 避免重复添加
            if (privateAttributeRelations.stream().anyMatch(r -> r.getCelebrityPrivateId().equals(celebrityPrivateId) && r.getAttributeId().equals(attribute.getId()))) {
                continue;
            }

            StoreCelebrityPrivateAttributeRelation relation = new StoreCelebrityPrivateAttributeRelation();
            relation.setId(IdWorker.getIdStr());
            relation.setCelebrityPrivateId(celebrityPrivateId);
            relation.setAccount(account);
            relation.setAttributeId(attribute.getId());
            relation.setAttributeCode(attribute.getAttributeCode());
            relation.setAttributeName(attribute.getAttributeName());
            relation.setAttributeTypeId(attributeTypeCode);
            relation.setAttributeTypeName(attributeTypeName);
            relation.setCreateBy(createBy);
            relation.setCreateTime(createTime);
            relation.setRepeatQty(0);
            relation.setTotalQty(0);
            relation.setPercentage(BigDecimal.valueOf(0));
            privateAttributeRelations.add(relation);
        }

        return isValid;
    }

    /**
     * @description: 处理个性化标签
     * 1、对标签进行去空格和转小写
     * 2、查询标签是否存在，不存在需要创建标签，并把新增标签类目为未分类（字典配置）
     * 3、新增标签和网红关联关系
     * @author: nqr
     * @date: 2025/5/9 10:48
     **/
    private boolean setPersonalTags(List<StorePersonalTags> personalTagsList, StoreCelebrityPrivate storeCelebrityPrivate, Set<StoreCelebrityPrivateExcelModel> errorList, StoreCelebrityPrivateExcelModel storeCelebrityPrivateExcelModel) {
        String personalTags = storeCelebrityPrivate.getPersonalTags();
        if (oConvertUtils.isEmpty(personalTags)) {
            return true;
        }
        // // 查询未分类类目
        // List<SysDictItem> dictItems = dictService.getItemList("personal_tags_category");

        // 更新并标准化标签格式
        personalTags = storeCelebrityPrivateService.updatePersonalTags(personalTags);
        // storeCelebrityPrivate.setPersonalTags(personalTags);

        // 分割标签并去重
        Set<String> personalTagSet = new HashSet<>(Arrays.asList(personalTags.split(",")));

        // 查询已存在的标签
        List<StorePersonalTags> existingTags = storePersonalTagsService.lambdaQuery().in(StorePersonalTags::getTagName, personalTagSet).list();

        // 获取已存在的标签名
        Set<String> existingTagNames = existingTags.stream().map(StorePersonalTags::getTagName).collect(Collectors.toSet());

        // 找出需要新增的标签
        Set<String> newTagNames = personalTagSet.stream().filter(tag -> !existingTagNames.contains(tag)).collect(Collectors.toSet());
        if (!newTagNames.isEmpty()) {
            addToErrorList(storeCelebrityPrivateExcelModel, "个性化标签不存在：" + String.join(",", newTagNames), errorList);
            return false;
        }
        // // 处理新增标签逻辑
        // for (String tagName : newTagNames) {
        //     StorePersonalTags newTag = new StorePersonalTags();
        //     newTag.setId(IdWorker.get32UUID());
        //     newTag.setTagName(tagName);
        //     newTag.setCreateBy(getUserNameByToken());
        //     newTag.setCreateTime(new Date());
        //     personalTagsList.add(newTag);
        //
        // }
        return true;
    }

    /**
     * 更新已存在实体的ID
     */
    private void updateExistingEntityId(StoreCelebrityPrivate entity, String account, int platformType) {
        StoreCelebrityPrivate existing = storeCelebrityPrivateService.checkCelebrityPrivate(account, platformType);
        entity.setId(existing != null ? existing.getId() : IdWorker.get32UUID());
        entity.setRemark(existing != null ? existing.getRemark() : "");
    }

    private String appendKIfMissing(String numStr) {
        if (numStr == null || numStr.trim().isEmpty()) {
            return numStr; // 处理空字符串
        }
        String cleanedStr = numStr.trim().toUpperCase().replace("M", "").replace(" ", "");
        return cleanedStr.contains("K") ? numStr : numStr + "K";
    }

    private int getPlatformType(List<SysDictItem> platformTypeList, String videoTags) {
        String platformTypeText = "";
        if (containsIgnoreCase(videoTags, "TikTok") || containsIgnoreCase(videoTags, "TK")) {
            platformTypeText = "TK";
        }
        if (containsIgnoreCase(videoTags, "IG")) {
            platformTypeText = "IG";
        }
        if (containsIgnoreCase(videoTags, "YT")) {
            platformTypeText = "YT";
        }
        String finalPlatformTypeText = platformTypeText;
        SysDictItem sysDictItem = platformTypeList.stream().filter(x -> x.getItemText().trim().equals(finalPlatformTypeText.trim())).findFirst().orElse(null);
        int platformType = 0;
        if (sysDictItem != null) {
            platformType = Integer.parseInt(sysDictItem.getItemValue());
        }
        return platformType;
    }


    public static boolean containsIgnoreCase(String mainString, String subString) {
        if (mainString == null || subString == null) {
            return false; // 处理空字符串的情况
        }
        return mainString.toLowerCase().contains(subString.toLowerCase());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> importFeishuCelebrityPrivate(List<StoreCelebrityPrivateExcelModel> list, String userId, String userName) {
        Map<String, Object> resultMap = new HashMap<>();

        list = list.stream().filter(x -> oConvertUtils.isNotEmpty(x.getAccount())).collect(Collectors.toList());
        if (list.isEmpty()) {
            return null;
        }
        Set<StoreCelebrityPrivateExcelModel> errorList = new HashSet<>();
        // 判断是否存在重复数据
        List<StoreCelebrityPrivateExcelModel> duplicates = findDuplicateEntries(list);
        if (!duplicates.isEmpty()) {
            duplicates.forEach(x -> addToErrorList(x, "存在重复数据", errorList));
            // 从list中删除重复数据
            list.removeAll(duplicates);
        }

        // -------------增加校验 网红邮箱字段 不能为空 2024年11月30日 08:22:37 ----------------------------
        List<StoreCelebrityPrivateExcelModel> emptyEmailList = list.stream().filter(x -> oConvertUtils.isEmpty(x.getEmail())).collect(Collectors.toList());
        if (!emptyEmailList.isEmpty()) {
            emptyEmailList.forEach(x -> addToErrorList(x, "网红邮箱不能为空", errorList));
            list.removeAll(emptyEmailList);
        }
        //-------------------------------------end----------------------------------------------------
        List<StoreCelebrityPrivateExcelModel> emptyContactEmailList = list.stream().filter(x -> oConvertUtils.isEmpty(x.getCelebrityContactEmail())).collect(Collectors.toList());
        if (!emptyContactEmailList.isEmpty()) {
            emptyContactEmailList.forEach(x -> addToErrorList(x, "建联邮箱不能为空", errorList));
            list.removeAll(emptyContactEmailList);
        }
        List<StoreCelebrityPrivateExcelModel> excelModelList = new ArrayList<>();
        List<StoreCountry> countries = storeCountryService.list();

        List<SysDictItem> platformTypeList = dictService.getItemList("platform_type");


        List<StoreCelebrityPrivate> listNew = new ArrayList<>();
        List<StoreCelebrityPrivatePersonalTags> personalTagsList = new ArrayList<>();
        List<StoreCelebrityPrivateAttributeRelation> privateAttributeRelations = new ArrayList<>();
        List<StoreCelebrityPrivateExcelModel> successImportList = new ArrayList<>();

        // 校验上传字段，并创建私有网红数据
        createPrivateCelebrityList(list, errorList, platformTypeList, listNew, countries, personalTagsList, privateAttributeRelations, userId, userName, successImportList);

        List<StoreCelebrityPrivateCounselor> privateCounselors = new ArrayList<>();
        // 创建网红顾问和网红关联关系
        createPrivateCounselors(listNew, userId, userName, privateCounselors);

        saveAll(listNew, privateCounselors, privateAttributeRelations, personalTagsList);

        resultMap.put("successImportList", successImportList);
        resultMap.put("errorList", errorList);
        return resultMap;
    }
}