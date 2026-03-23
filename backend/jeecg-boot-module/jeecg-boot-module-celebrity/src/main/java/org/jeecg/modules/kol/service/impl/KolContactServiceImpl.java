package org.jeecg.modules.kol.service.impl;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.enums.YesNoStatus;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.kol.entity.KolContact;
import org.jeecg.modules.kol.entity.KolProduct;
import org.jeecg.modules.kol.mapper.KolContactMapper;
import org.jeecg.modules.kol.model.KolBrandModel;
import org.jeecg.modules.kol.service.IKolConnectionService;
import org.jeecg.modules.kol.service.IKolContactService;
import org.jeecg.modules.kol.service.IKolProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: 网红品牌开发表-重命名
 * @Author: xyc
 * @Date: 2024年12月4日 14:15:13
 * @Version: V1.1
 * @History: V1.1 - [2024-12-23] - [新增updateContactHistory 用于兼容不同类型的网红更新历史开发记录] - [xyc]
 */
@Service
public class KolContactServiceImpl extends ServiceImpl<KolContactMapper, KolContact> implements IKolContactService {

    @Autowired
    private IKolProductService kolProductService;

    @Autowired
    @Qualifier("igConnectionService")
    private IKolConnectionService igConnectionService;

    @Autowired
    @Qualifier("tkConnectionService")
    private IKolConnectionService tkConnectionService;

    @Autowired
    @Qualifier("ytConnectionService")
    private IKolConnectionService ytConnectionService;

    /**
     * 根据多个ID获取KolContact列表
     * 该方法用于查询特定ID列表对应的KolContact对象，并返回这些对象的列表
     * 主要用于需要批量获取Kol联系人信息的场景
     *
     * @param ids KOL联系人的唯一标识符列表，用于指定需要查询的KOL联系人
     * @return 返回一个KolContact对象列表，包含所查询的KOL联系人信息
     */
    @Override
    public List<KolContact> getKolContactListByIds(List<String> ids) {
        LambdaQueryWrapper<KolContact> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(KolContact::getUniqueId, KolContact::getBrandName, KolContact::getCounselorShortName, KolContact::getPlatformType);
        queryWrapper.in(KolContact::getUniqueId, ids);
        queryWrapper.groupBy(KolContact::getUniqueId, KolContact::getBrandName, KolContact::getCounselorShortName);
        return this.list(queryWrapper);
    }

    @Override
    public List<KolContact> getContactListByIds(List<String> ids) {
        LambdaQueryWrapper<KolContact> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(KolContact::getCelebrityId, KolContact::getUniqueId, KolContact::getBrandName, KolContact::getProductName, KolContact::getCounselorShortName, KolContact::getPlatformType);
        queryWrapper.in(KolContact::getCelebrityId, ids);
        queryWrapper.ge(KolContact::getContactDate, DateUtils.getMonthsAgoDate(6));
        queryWrapper.eq(KolContact::getIsDelete, YesNoStatus.NO.getCode());
        queryWrapper.isNotNull(KolContact::getBrandId);
        queryWrapper.groupBy(KolContact::getCelebrityId, KolContact::getProductId, KolContact::getCounselorShortName);
        return this.list(queryWrapper);
    }

    /**
     * 功能描述：保存网红建联开发明细
     *
     * @param kolBrandModel
     * @throws JeecgBootException
     */
    @Override
    public void saveKolContact(KolBrandModel kolBrandModel) throws JeecgBootException {
        String productId = kolBrandModel.getProductId();
        KolProduct kolProduct = kolProductService.getById(productId);
        if (kolProduct == null) {
            throw new JeecgBootException("未获取到产品信息");
        }
        List<KolBrandModel.KolSimpleModel> kolSimpleModelList = kolBrandModel.getKolList();
        if (oConvertUtils.listIsEmpty(kolSimpleModelList)) {
            throw new JeecgBootException("标记失败，未获取到网红账号或邮箱");
        }
        // 去除校验邮箱是否为空  2025年10月17日10:00:18
    /*    boolean hasEmptyIdOrEmail = kolSimpleModelList.stream().anyMatch(x -> oConvertUtils.isEmpty(x.getId()) || oConvertUtils.isEmpty(x.getKolEmail()));
        if (hasEmptyIdOrEmail) {
            throw new JeecgBootException("网红账号或邮箱为空，不能标记");
        }*/
        boolean hasId = kolSimpleModelList.stream().anyMatch(x -> oConvertUtils.isEmpty(x.getId()));
        if (hasId) {
            throw new JeecgBootException("网红账号为空，不能标记");
        }
        LoginUser logUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<String> kolIdList = kolSimpleModelList.stream().map(KolBrandModel.KolSimpleModel::getId).collect(Collectors.toList());
        // 将 kolSimpleModelList 转换为 id -> kolEmail 的映射，以便高效查找
        Map<String, String> emailMap = kolSimpleModelList.stream()
            .collect(Collectors.toMap(
                KolBrandModel.KolSimpleModel::getId,
                model -> model.getKolEmail() != null ? model.getKolEmail() : "",
                (existing, replacement) -> replacement
            ));
        Integer platformType = kolBrandModel.getPlatformType();

        // 根据平台类型动态选择平台服务类
        IKolConnectionService kolConnectionService = getKolConnectionService(platformType);
        // 根据id列表获取对应网红列表 重点处理了邮箱更新问题
        List<?> kolList = kolConnectionService.getKolList(kolIdList, emailMap);
        // 创建建联列表 用于批量新增操作
        List<KolContact> kolContactList = new ArrayList<>();
        kolList.forEach(x -> {
            // 需要处理邮箱更新问题
            KolContact kolContact = kolConnectionService.createKolContact(x);
            kolContact.setId(IdWorker.get32UUID());
            kolContact.setBrandId(kolProduct.getBrandId());
            kolContact.setBrandName(kolProduct.getBrandName());
            kolContact.setProductId(kolProduct.getId());
            kolContact.setProductName(kolProduct.getProductName());
            kolContact.setCounselorId(logUser.getId());
            kolContact.setCounselorName(logUser.getUsername());
            kolContact.setCounselorShortName(logUser.getShortName());
            kolContact.setContactDate(new Date());
            if (kolBrandModel.getEmailPushMainId() != null) {
                kolContact.setEmailPushMainId(kolBrandModel.getEmailPushMainId());
            }
            kolContactList.add(kolContact);
        });

        // 需要组合网红更新列表，重点是更新网红邮箱 开发建联明细中也应保存网红邮箱
        batchSaveKolContact(kolContactList, kolList, platformType);
    }

    /**
     * 功能描述：根据邮件推送主表ID删除标记开发记录
     *
     * @param emailPushMainId 邮件推送主表ID
     */
    @Override
    public void deleteKolContactByEmailPushMainId(String emailPushMainId) {
        if (oConvertUtils.isNotEmpty(emailPushMainId)) {
            this.lambdaUpdate()
                    .eq(KolContact::getEmailPushMainId, emailPushMainId)
                    .set(KolContact::getIsDelete,1)
                    .update();
        }
    }

    /**
     * 功能描述：根据邮件推送主表ID和网红ID删除单个网红的标记开发记录
     *
     * @param emailPushMainId 邮件推送主表ID
     * @param celebrityId     网红ID
     */
    @Override
    public void deleteKolContactByEmailPushMainIdAndCelebrityId(String emailPushMainId, String celebrityId) {
        if (oConvertUtils.isNotEmpty(emailPushMainId) && oConvertUtils.isNotEmpty(celebrityId)) {
            this.lambdaUpdate()
                    .eq(KolContact::getEmailPushMainId, emailPushMainId)
                    .eq(KolContact::getCelebrityId, celebrityId)
                    .set(KolContact::getIsDelete,1)
                    .update();
        }
    }

    /**
     * 功能描述：更新网红建联开发历史
     *
     * @param kolModels
     * @param kolContactList
     * @param getUniqueId
     * @param setContactHistory
     */
    public <T> void updateContactHistoryBack(List<T> kolModels, List<KolContact> kolContactList, Function<T, String> getUniqueId, BiConsumer<T, String> setContactHistory) {
        // 聚合组装建联开发历史
        Map<String, String> uniqueIdContactMap = kolContactList.stream().collect(Collectors.groupingBy(KolContact::getCelebrityId, Collectors.mapping(p -> (oConvertUtils.isEmpty(p.getProductName()) ? "" : (p.getProductName() + "-")) + p.getBrandName(), Collectors.joining(","))));

        for (Map.Entry<String, String> entry : uniqueIdContactMap.entrySet()) {
            String celebrityId = entry.getKey();
            String contactValue = entry.getValue();

            // 查找并更新对应的网红模型
            Optional<T> kolModelOptional = kolModels.stream().filter(x -> getUniqueId.apply(x).equals(celebrityId)).findFirst();

            kolModelOptional.ifPresent(x -> {
                setContactHistory.accept(x, contactValue);
            });
        }
    }

    @Override
    public <T> void updateContactHistory(List<T> kolModels, List<KolContact> kolContactList, Function<T, String> getUniqueId, BiConsumer<T, String> setContactHistory) {

        // 按 celebrityId 分组，并统计每个 (productName, brandName) 的出现次数
        Map<String, List<ContactHistoryItem>> uniqueIdToContactList = kolContactList.stream().collect(Collectors.groupingBy(KolContact::getCelebrityId, Collectors.collectingAndThen(Collectors.groupingBy(contact -> new Object[]{
                // 使用数组作为临时组合键（避免定义类），也可用 Pair，但这里保持简洁
                Optional.ofNullable(contact.getProductName()).orElse(""), contact.getBrandName()}, Collectors.summingInt(e -> 1)), map -> map.entrySet().stream().map(entry -> {
                    Object[] key = entry.getKey();
                    return new ContactHistoryItem((String) key[0], (String) key[1], entry.getValue());
                }).sorted(Comparator.comparing(ContactHistoryItem::getNum).reversed()) // 可选：按数量降序
                .collect(Collectors.toList()))));

        // 遍历每个网红 ID，更新对应的模型
        for (Map.Entry<String, List<ContactHistoryItem>> entry : uniqueIdToContactList.entrySet()) {
            String celebrityId = entry.getKey();
            List<ContactHistoryItem> contactItems = entry.getValue();

            // 查找对应的模型
            Optional<T> kolModelOptional = kolModels.stream().filter(x -> getUniqueId.apply(x).equals(celebrityId)).findFirst();

            kolModelOptional.ifPresent(x -> {
                // 转为 JSON 字符串
                String contactHistoryJson = JSON.toJSONString(contactItems);
                setContactHistory.accept(x, contactHistoryJson);
            });
        }
    }

    /**
     * 功能描述：更新网红建联开发历史
     *
     * @param kolModels
     * @param kolContactList
     * @param getUniqueId
     * @param setContactHistory
     */
    @Override
    public <T> void updateContactHistoryAllotOld(List<T> kolModels, List<KolContact> kolContactList, Function<T, String> getUniqueId, BiConsumer<T, String> setContactHistory, BiConsumer<T, Integer> setContactCount) {
        // 聚合组装建联开发历史
        Map<String, String> uniqueIdContactMap = kolContactList.stream().collect(Collectors.groupingBy(KolContact::getCelebrityId, Collectors.mapping(p -> (oConvertUtils.isEmpty(p.getProductName()) ? "" : (p.getProductName() + "-")) + p.getBrandName(), Collectors.joining(","))));

        for (Map.Entry<String, String> entry : uniqueIdContactMap.entrySet()) {
            String celebrityId = entry.getKey();
            String contactValue = entry.getValue();

            // 查找并更新对应的网红模型
            Optional<T> kolModelOptional = kolModels.stream().filter(x -> getUniqueId.apply(x).equals(celebrityId)).findFirst();

            kolModelOptional.ifPresent(x -> {
                setContactHistory.accept(x, contactValue);
                setContactCount.accept(x, contactValue.split(",").length);
            });
        }
    }

    private static class ProductBrandKey {
        private String productName;
        private String brandName;

        public ProductBrandKey(String productName, String brandName) {
            this.productName = Optional.ofNullable(productName).orElse("");
            this.brandName = Optional.ofNullable(brandName).orElse("");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ProductBrandKey that = (ProductBrandKey) o;
            return Objects.equals(productName, that.productName) && Objects.equals(brandName, that.brandName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(productName, brandName);
        }

        // Getter 方法用于 JSON 序列化
        public String getProductName() {
            return productName;
        }

        public String getBrandName() {
            return brandName;
        }
    }

    // 用于 JSON 输出的 DTO
    private static class ContactHistoryItem {
        private String productName;
        private String brandName;
        private int num;

        public ContactHistoryItem(String productName, String brandName, int num) {
            this.productName = productName;
            this.brandName = brandName;
            this.num = num;
        }

        // Getters
        public String getProductName() {
            return productName;
        }

        public String getBrandName() {
            return brandName;
        }

        public int getNum() {
            return num;
        }
    }

    public <T> void updateContactHistoryAllot(List<T> kolModels, List<KolContact> kolContactList, Function<T, String> getUniqueId, BiConsumer<T, String> setContactHistory, BiConsumer<T, Integer> setContactCount) {

        // 按 celebrityId 聚合建联记录，并统计每个 product + brand 的数量
        Map<String, List<ContactHistoryItem>> uniqueIdToContactList = kolContactList.stream().collect(Collectors.groupingBy(KolContact::getCelebrityId, Collectors.collectingAndThen(Collectors.groupingBy(contact -> new ProductBrandKey(contact.getProductName(), contact.getBrandName()), Collectors.summingInt(e -> 1)), map -> map.entrySet().stream().map(entry -> new ContactHistoryItem(entry.getKey().getProductName(), entry.getKey().getBrandName(), entry.getValue())).sorted(Comparator.comparing(ContactHistoryItem::getNum).reversed()) // 可选排序
                .collect(Collectors.toList()))));

        // 遍历结果，更新对应的 kolModel
        for (Map.Entry<String, List<ContactHistoryItem>> entry : uniqueIdToContactList.entrySet()) {
            String celebrityId = entry.getKey();
            List<ContactHistoryItem> contactItems = entry.getValue();

            // 查找对应的模型
            Optional<T> kolModelOptional = kolModels.stream().filter(x -> getUniqueId.apply(x).equals(celebrityId)).findFirst();

            kolModelOptional.ifPresent(x -> {
                // 转为 JSON 字符串
                String contactHistoryJson = JSON.toJSONString(contactItems);
                setContactHistory.accept(x, contactHistoryJson);
                // 设置总数（所有记录的 num 总和）
                int totalCount = contactItems.stream().mapToInt(ContactHistoryItem::getNum).sum();
                setContactCount.accept(x, totalCount);
            });
        }
    }

    /**
     * 批量保存网红关联信息
     *
     * @param kolContactList
     * @param kolList
     * @param platformType
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveKolContact(List<KolContact> kolContactList, List<?> kolList, Integer platformType) {
        // 根据平台类型动态选择平台服务类
        IKolConnectionService kolConnectionService = getKolConnectionService(platformType);
        // 批量更新网红邮箱数据
        kolConnectionService.batchUpdateKolList(kolList);
        // 批量保存建联记录
        this.saveBatch(kolContactList);
    }


    /**
     * 根据平台类型获取对应的服务
     *
     * @param platformType 0=IG 1=YT 2=TK
     * @return
     */
    private IKolConnectionService getKolConnectionService(Integer platformType) {
        switch (platformType) {
            case CommonConstant.IG:
                return igConnectionService;
            case CommonConstant.YT:
                return ytConnectionService;
            case CommonConstant.TK:
                return tkConnectionService;
            default:
                throw new IllegalArgumentException("未知的平台类型参数: " + platformType);
        }
    }

    @Override
    public void resendKolContactByEmailPushMainIdAndCelebrityId(String mainId, String celebrityId) {
        if (oConvertUtils.isNotEmpty(mainId) && oConvertUtils.isNotEmpty(celebrityId)) {
            this.lambdaUpdate()
                    .eq(KolContact::getEmailPushMainId, mainId)
                    .eq(KolContact::getCelebrityId, celebrityId)
                    .set(KolContact::getIsDelete, YesNoStatus.NO.getCode())
                    .update();
        }
    }
}
