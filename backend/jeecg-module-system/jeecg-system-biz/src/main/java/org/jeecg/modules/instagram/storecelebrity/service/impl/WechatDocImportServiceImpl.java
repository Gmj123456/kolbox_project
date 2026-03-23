package org.jeecg.modules.instagram.storecelebrity.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.system.vo.StoreCelebrityPrivateExcelModel;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.storecelebrity.service.*;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivate;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivateAttributeRelation;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivateCounselor;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivateProduct;
import org.jeecg.modules.instagram.storecelebrity.model.StoreCelebrityPrivateProductModel;
import org.jeecg.modules.instagram.storecountry.entity.StoreCountry;
import org.jeecg.modules.instagram.storecountry.service.IStoreCountryService;
import org.jeecg.modules.instagram.storetags.entity.StoreCelebrityPrivatePersonalTags;
import org.jeecg.modules.instagram.storeseller.storeuser.entity.StoreUserContactEmail;
import org.jeecg.modules.instagram.storeseller.storeuser.service.IStoreUserContactEmailService;
import org.jeecg.modules.kol.entity.KolAttribute;
import org.jeecg.modules.kol.entity.KolAttributeType;
import org.jeecg.modules.kol.wechatexcel.service.WechatService;
import org.jeecg.modules.system.entity.SysDictItem;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class WechatDocImportServiceImpl implements WechatDocImportService {
    @Resource
    private IStoreCelebrityPrivateService storeCelebrityPrivateService;

    @Resource
    private IStoreCelebrityPrivateCounselorService privateCounselorService;

    @Resource
    private ISysDictService dictService;

    @Resource
    private IStoreCountryService storeCountryService;

    @Resource
    private IStoreUserContactEmailService contactEmailService;

    @Resource
    private ISysUserService sysUserService;

    @Resource
    private WechatService wechatService;

    @Resource
    private ICelebrityPrivateService celebrityPrivateService;

    @Resource
    private IStoreCelebrityPrivateProductService privateProductService;

    @Override
    public void importStoreCelebrityPrivate(List<StoreCelebrityPrivateExcelModel> list, String docid, String sheetId) {
        list = list.stream().filter(x -> oConvertUtils.isNotEmpty(x.getAccount())).collect(Collectors.toList());
        if (list.isEmpty()) {
            return;
        }


        Set<StoreCelebrityPrivateExcelModel> errorList = new HashSet<>();

        // 去除重复数据, 并记录错误数据
        list = removeDuplicateEntries(list, errorList);


        // -------------增加校验 网红邮箱字段 不能为空 2024年11月30日 08:22:37 ----------------------------
        List<StoreCelebrityPrivateExcelModel> emptyEmailList = list.stream().filter(x -> oConvertUtils.isEmpty(x.getEmail())).collect(Collectors.toList());
        if (!emptyEmailList.isEmpty()) {
            emptyEmailList.forEach(x -> {
                addToErrorList(x, "网红邮箱不能为空", errorList);
            });
        }
        //-------------------------------------end----------------------------------------------------
        List<StoreCelebrityPrivateExcelModel> emptyContactEmailList = list.stream().filter(x -> oConvertUtils.isEmpty(x.getCelebrityContactEmail())).collect(Collectors.toList());
        if (!emptyContactEmailList.isEmpty()) {
            emptyContactEmailList.forEach(x -> {
                addToErrorList(x, "建联邮箱不能为空", errorList);
            });
        }
        List<String> usernames = list.stream().map(StoreCelebrityPrivateExcelModel::getUserName).distinct().collect(Collectors.toList());
        List<SysUser> loginUsers = sysUserService.lambdaQuery().in(SysUser::getUsername, usernames).list();

        List<StoreCelebrityPrivateExcelModel> excelModelList = new ArrayList<>();
        for (StoreCelebrityPrivateExcelModel model : list) {
            model.setAccount(model.getAccount().trim());
            loginUsers.stream().filter(x -> x.getUsername().equals(model.getUserName())).findFirst().ifPresent(loginUser -> {
                model.setUserId(loginUser.getId());
            });
            excelModelList.add(model);
        }
        List<StoreCountry> countries = storeCountryService.list();

        List<SysDictItem> platformTypeList = dictService.getItemList("platform_type");


        List<StoreCelebrityPrivate> listNew = new ArrayList<>();
        List<StoreCelebrityPrivatePersonalTags> personalTagsList = new ArrayList<>();
        List<StoreCelebrityPrivateAttributeRelation> privateAttributeRelations = new ArrayList<>();
        // 校验上传字段，并创建私有网红数据
        createPrivateCelebrityList(excelModelList, errorList, platformTypeList, listNew, countries, personalTagsList, privateAttributeRelations);

        if (!errorList.isEmpty()) {
            updateImportResult(docid, sheetId, new ArrayList<>(errorList), "否");
            return;
        }

        List<StoreCelebrityPrivateCounselor> privateCounselors = new ArrayList<>();
        // 创建网红顾问和网红关联关系
        createPrivateCounselors(listNew, privateCounselors);
        celebrityPrivateService.saveAll(listNew, privateCounselors, privateAttributeRelations, personalTagsList);
        updateImportResult(docid, sheetId, list, "是");
    }

    @Override
    public void importPrivateCelebrityProducts(List<StoreCelebrityPrivateProductModel> privateProductModels, String templateDocId, String privateProductSheetId) {
        privateProductModels = privateProductModels.stream().filter(x -> oConvertUtils.isNotEmpty(x.getAccount())).collect(Collectors.toList());
        if (privateProductModels.isEmpty()) {
            return;
        }
        
        Set<StoreCelebrityPrivateProductModel> errorList = new HashSet<>();
        List<StoreCelebrityPrivateProduct> insertPrivateProducts = new ArrayList<>();
        List<StoreCelebrityPrivateProduct> updatePrivateProducts = new ArrayList<>();
        List<StoreCelebrityPrivateProductModel> successList = new ArrayList<>();
        
        // 循环privateProductModels
        for (StoreCelebrityPrivateProductModel model : privateProductModels) {
            boolean hasError = false;
            
            // 1、验证产品是否为空，为空则返回错误信息
            if (oConvertUtils.isEmpty(model.getProductId()) || oConvertUtils.isEmpty(model.getProductName())) {
                addToErrorList(model, "产品信息不能为空", errorList);
                hasError = true;
            }
            
            // 2、更新平台，需要获取加号之前的平台，例子：TK+IG 获取平台为TK
            String platformStr = model.getPlatformTypeStr();
            if (oConvertUtils.isNotEmpty(platformStr) && platformStr.contains("+")) {
                platformStr = platformStr.split("\\+")[0];
            }
            Integer platformType = getPlatformType(platformStr);
            if (platformType == null) {
                addToErrorList(model, "平台类型无效", errorList);
                hasError = true;
            } else {
                model.setPlatformType(platformType);
            }
            
            StoreCelebrityPrivate celebrityPrivate = null;
            StoreCelebrityPrivateProduct existingProduct = null;
            
            if (!hasError) {
                // 3、根据平台与网红名称获取私有网红是否存在，如果不存在则返回错误信息
                String account = model.getAccount().trim().toLowerCase();
                celebrityPrivate = storeCelebrityPrivateService.lambdaQuery()
                        .eq(StoreCelebrityPrivate::getAccount, account)
                        .eq(StoreCelebrityPrivate::getPlatformType, platformType)
                        .one();
                
                if (celebrityPrivate == null) {
                    addToErrorList(model, "私有网红不存在，平台：" + platformStr + "，账号：" + account, errorList);
                    hasError = true;
                } else {
                    // 4、判断是否已经存在历史合作产品，存在则添加到更新列表
                    existingProduct = privateProductService.lambdaQuery()
                            .eq(StoreCelebrityPrivateProduct::getCelebrityId, celebrityPrivate.getId())
                            .eq(StoreCelebrityPrivateProduct::getProductId, model.getProductId())
                            .eq(StoreCelebrityPrivateProduct::getBrandId, model.getBrandId())
                            .one();
                }
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
                } else {
                    // 5、如果不存在则添加到新增列表
                    privateProduct.setId(IdWorker.get32UUID());
                    privateProduct.setCreateTime(new Date());
                    privateProduct.setSelectionTime(new Date());
                    insertPrivateProducts.add(privateProduct);
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
        
        // 先更新错误记录的企微文档
        if (!errorList.isEmpty()) {
            updatePrivateProductImportResult(templateDocId, privateProductSheetId, new ArrayList<>(errorList), "否");
        }
        
        // 6、保存成功的记录到数据库
        if (!successList.isEmpty()) {
            try {
                if (!insertPrivateProducts.isEmpty()) {
                    privateProductService.saveBatch(insertPrivateProducts);
                }
                if (!updatePrivateProducts.isEmpty()) {
                    privateProductService.updateBatchById(updatePrivateProducts);
                }
                
                // 更新成功记录的企微文档
                updatePrivateProductImportResult(templateDocId, privateProductSheetId, successList, "是");
                
            } catch (Exception e) {
                // 数据库保存异常，将成功列表中的记录标记为失败
                successList.forEach(model -> {
                    model.setFailReason("保存失败：" + e.getMessage());
                });
                updatePrivateProductImportResult(templateDocId, privateProductSheetId, successList, "否");
            }
        }
    }

    private void updateImportResult(String docid, String sheetId, List<StoreCelebrityPrivateExcelModel> list, String isUpdate) {
        List<Map<String, Object>> updateRecords = new ArrayList<>();
        list.forEach(x -> {
            /*Map<String, Object> record = new HashMap<>();
            record.put("record_id", x.getRecordId());

            Map<String, Object> values = new HashMap<>();
            values.put("是否同步", Collections.singletonList(Collections.singletonMap("text", isUpdate)));
            if (oConvertUtils.isNotEmpty(x.getFailReason()) || isUpdate.equals("是")) {
                values.put("异常原因", Collections.singletonList(new HashMap<String, Object>() {{
                    put("text", x.getFailReason());
                    put("type", "text");
                }}));
            }
            record.put("values", values);*/

            Map<String, Object> record = new HashMap<>();
            record.put("record_id", x.getRecordId());
            record.put("values", new HashMap<String, Object>() {{
                put("是否同步", Collections.singletonList(Collections.singletonMap("text", isUpdate)));
                if (oConvertUtils.isNotEmpty(x.getFailReason()) || isUpdate.equals("是")) {
                    put("异常原因", Collections.singletonList(new HashMap<String, Object>() {{
                        put("text", oConvertUtils.isEmpty(x.getFailReason()) ? "" : x.getFailReason());
                        put("type", "text");
                    }}));
                }
            }});
            updateRecords.add(record);
        });
        CompletableFuture.runAsync(() -> {
            try {
                // 更新企微表格同步结果
                wechatService.updateRecords(docid, sheetId, updateRecords);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void createPrivateCounselors(List<StoreCelebrityPrivate> listNew, List<StoreCelebrityPrivateCounselor> privateCounselors) {
        List<String> celebrityPrivateIds = listNew.stream().map(StoreCelebrityPrivate::getId).collect(Collectors.toList());
        List<String> userIds = listNew.stream().map(StoreCelebrityPrivate::getCelebrityCounselorId).distinct().collect(Collectors.toList());

        List<StoreCelebrityPrivateCounselor> celebrityPrivateCounselors = privateCounselorService.lambdaQuery().in(StoreCelebrityPrivateCounselor::getCelebrityCounselorId, userIds).in(StoreCelebrityPrivateCounselor::getCelebrityPrivateId, celebrityPrivateIds).list();
        // 查询网红顾问是否存在
        for (StoreCelebrityPrivate storeCelebrityPrivate : listNew) {
            String userId = storeCelebrityPrivate.getCelebrityCounselorId();
            String userName = storeCelebrityPrivate.getCelebrityCounselorName();
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
            storeCelebrityPrivate.setRemark(null);
            privateCounselors.add(privateCounselor);
        }
    }

    private void createPrivateCelebrityList(List<StoreCelebrityPrivateExcelModel> list, Set<StoreCelebrityPrivateExcelModel> errorList, List<SysDictItem> platformTypeList,
                                            List<StoreCelebrityPrivate> listNew, List<StoreCountry> countries, List<StoreCelebrityPrivatePersonalTags> personalTagsList,
                                            List<StoreCelebrityPrivateAttributeRelation> privateAttributeRelations) {
        List<String> userIds = list.stream().map(StoreCelebrityPrivateExcelModel::getUserId).distinct().collect(Collectors.toList());
        LambdaQueryWrapper<StoreUserContactEmail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(StoreUserContactEmail::getSysUserId, userIds);
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
            StoreCelebrityPrivate celebrityPrivate = createCelebrityEntity(storeCelebrityPrivateExcelModel, storeCelebrityPrivate,
                    platformType, storeCountry.getId(), personalTagsList, privateAttributeRelations, errorList, kolAttributes, kolAttributeTypes);
            if (celebrityPrivate == null) {
                continue;
            }
            Optional<StoreCelebrityPrivate> privateOptional = listNew.stream().filter(x -> x.getAccount().equals(account) && x.getPlatformType().equals(platformType)).findFirst();
            if (privateOptional.isPresent()) {
                continue;
            }
            listNew.add(celebrityPrivate);
        }
    }

    /**
     * @description: 判断是否存在重复数据
     * @author: nqr
     * @date: 2025/7/11 9:50
     **/
    public List<StoreCelebrityPrivateExcelModel> removeDuplicateEntries(List<StoreCelebrityPrivateExcelModel> list, Set<StoreCelebrityPrivateExcelModel> errorList) {

        // 用于记录去重后的数据
        Map<String, StoreCelebrityPrivateExcelModel> uniqueMap = new LinkedHashMap<>();

        for (StoreCelebrityPrivateExcelModel model : list) {
            String key = model.getAccount() + "#" + model.getPlatformTypeText();

            if (uniqueMap.containsKey(key)) {
                // 如果是重复数据，添加到错误列表
                addToErrorList(model, "重复数据", errorList);
            } else {
                // 如果是唯一数据，添加到唯一map
                uniqueMap.put(key, model);
            }
        }

        return new ArrayList<>(uniqueMap.values());
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
        String recordId = model.getRecordId();
        Optional<StoreCelebrityPrivateProductModel> modelOptional = errorList.stream()
                .filter(x -> x.getRecordId().equals(recordId))
                .findFirst();
        if (modelOptional.isPresent()) {
            StoreCelebrityPrivateProductModel existingModel = modelOptional.get();
            if (!existingModel.getFailReason().contains(failReason)) {
                existingModel.setFailReason(existingModel.getFailReason() + "、" + failReason);
            }
        } else {
            model.setFailReason(failReason);
            errorList.add(model);
        }
    }

    /**
     * 更新私有网红产品导入结果
     */
    private void updatePrivateProductImportResult(String docId, String sheetId, List<StoreCelebrityPrivateProductModel> list, String isUpdate) {
        List<Map<String, Object>> updateRecords = new ArrayList<>();
        list.forEach(x -> {
            Map<String, Object> record = new HashMap<>();
            record.put("record_id", x.getRecordId());
            record.put("values", new HashMap<String, Object>() {{
                put("是否同步", Collections.singletonList(Collections.singletonMap("text", isUpdate)));
                if (oConvertUtils.isNotEmpty(x.getFailReason()) || isUpdate.equals("是")) {
                    put("异常原因", Collections.singletonList(new HashMap<String, Object>() {{
                        put("text", oConvertUtils.isEmpty(x.getFailReason()) ? "" : x.getFailReason());
                        put("type", "text");
                    }}));
                }
            }});
            updateRecords.add(record);
        });
        CompletableFuture.runAsync(() -> {
            try {
                // 更新企微表格同步结果
                wechatService.updateRecords(docId, sheetId, updateRecords);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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
        if (sysDictItem == null) {
            addToErrorList(model, "平台类型不正确", errorList);
            return false;
        }
        int platformType = Integer.parseInt(sysDictItem.getItemValue());

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
        if (oConvertUtils.isEmpty(genderText)) {
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

    private void addToErrorList(StoreCelebrityPrivateExcelModel model, String failReason, Set<StoreCelebrityPrivateExcelModel> errorList) {
        String account = model.getAccount();
        Optional<StoreCelebrityPrivateExcelModel> modelOptional = errorList.stream().filter(x -> x.getAccount().equals(account)).findFirst();
        if (modelOptional.isPresent()) {
            model = modelOptional.get();
            if (!model.getFailReason().contains(failReason)) {
                model.setFailReason(model.getFailReason() + "、" + failReason);
            }
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
                                                        List<KolAttribute> kolAttributes, List<KolAttributeType> kolAttributeTypes) {

        BeanUtils.copyProperties(storeCelebrityPrivateExcelModel, storeCelebrityPrivate);
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
        storeCelebrityPrivate.setCelebrityCounselorId(storeCelebrityPrivateExcelModel.getUserId());
        storeCelebrityPrivate.setCelebrityCounselorName(storeCelebrityPrivateExcelModel.getUserName());
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
     * 更新已存在实体的ID
     */
    private void updateExistingEntityId(StoreCelebrityPrivate entity, String account, int platformType) {
        StoreCelebrityPrivate existing = storeCelebrityPrivateService.checkCelebrityPrivate(account, platformType);
        entity.setId(existing != null ? existing.getId() : IdWorker.get32UUID());
    }


}
