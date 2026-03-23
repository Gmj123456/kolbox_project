package org.jeecg.modules.instagram.storecelebrity.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivate;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivateAttributeRelation;
import org.jeecg.modules.instagram.storecelebrity.mapper.StoreCelebrityPrivateAttributeRelationMapper;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityPrivateAttributeRelationService;
import org.jeecg.modules.kol.entity.KolAttribute;
import org.jeecg.modules.kol.model.TypeData;
import org.jeecg.modules.kol.service.IKolAttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 私有网红社媒属性关联表
 * @Author: jeecg-boot
 * @Date:   2025-07-24
 * @Version: V1.0
 */
@Service
public class StoreCelebrityPrivateAttributeRelationServiceImpl extends ServiceImpl<StoreCelebrityPrivateAttributeRelationMapper, StoreCelebrityPrivateAttributeRelation> implements IStoreCelebrityPrivateAttributeRelationService {
    @Autowired
    private IKolAttributeService attributeService;

    @Override
    public List<StoreCelebrityPrivateAttributeRelation> createAttributeRelation(StoreCelebrityPrivate celebrityPrivate, List<TypeData> dataList) {
        List<String> attributeIds = dataList.stream()
                .flatMap(x -> x.getList().stream())
                .map(TypeData.CategoryItem::getAttributeId)
                .filter(oConvertUtils::isNotEmpty)
                .collect(Collectors.toList());
        List<KolAttribute> attributeList = (List<KolAttribute>) attributeService.listByIds(attributeIds);
        return buildPrivateAttributeRelation(celebrityPrivate, attributeList, dataList);
    }
    /**
     * 构建分类关联数据
     */
    private List<StoreCelebrityPrivateAttributeRelation> buildPrivateAttributeRelation(StoreCelebrityPrivate celebrityPrivate, List<KolAttribute> attributeList, List<TypeData> dataList) {
        return dataList.stream().flatMap(typeData -> typeData.getList().stream()).map(categoryItem -> createProductAttributeRelation(celebrityPrivate, attributeList, categoryItem)).collect(Collectors.toList());
    }

    private StoreCelebrityPrivateAttributeRelation createProductAttributeRelation(StoreCelebrityPrivate celebrityPrivate, List<KolAttribute> attributeList, TypeData.CategoryItem categoryItem) {
        KolAttribute kolAttribute = attributeList.stream().filter(x -> x.getId().equals(categoryItem.getAttributeId())).findFirst().get();

        StoreCelebrityPrivateAttributeRelation attributeRelation = new StoreCelebrityPrivateAttributeRelation();
        attributeRelation.setId(IdWorker.get32UUID());
        attributeRelation.setCelebrityPrivateId(celebrityPrivate.getId());
        attributeRelation.setCelebrityId(celebrityPrivate.getCelebrityId());
        attributeRelation.setAccount(celebrityPrivate.getAccount());
        attributeRelation.setAttributeId(kolAttribute.getId());
        attributeRelation.setAttributeCode(kolAttribute.getAttributeCode());
        attributeRelation.setAttributeName(kolAttribute.getAttributeName());
        attributeRelation.setAttributeTypeId(kolAttribute.getAttributeTypeId());
        attributeRelation.setAttributeTypeName(kolAttribute.getAttributeTypeName());
        attributeRelation.setRepeatQty(0);
        attributeRelation.setTotalQty(0);
        attributeRelation.setPercentage(BigDecimal.ZERO);
        attributeRelation.setRank(categoryItem.getRank());
        return attributeRelation;
    }

    @Override
    public List<Map<String, String>> selectAttributeStructureBatch(List<String> celebrityPrivateIds) {
        return this.baseMapper.selectAttributeStructureBatch(celebrityPrivateIds);
    }

    @Override
    public List<TypeData> getCelebrityAttributes(List<String> celebrityPrivateIds) {
        return this.baseMapper.getBatchCelebrityTypeData(celebrityPrivateIds);
    }
    /**
     * 处理单个celebrity的数据
     * @param allTypeData 从数据库查询的所有TypeData（每行一个CategoryItem）
     * @param targetCelebrityId 目标celebrity ID
     * @return 该celebrity的完整TypeData列表
     */
    public static List<TypeData> processForCelebrity(List<TypeData> allTypeData, String targetCelebrityId) {

        // 过滤出目标celebrity的数据，并按typeId分组
        Map<String, List<TypeData.CategoryItem>> groupedByType = allTypeData.stream()
                .filter(typeData -> targetCelebrityId.equals(typeData.getCelebrityPrivateId())) // 假设TypeData有这个字段
                .flatMap(typeData -> typeData.getList().stream()
                        .map(item -> {
                            // 将typeId信息传递给CategoryItem（或者从SQL中获取）
                            item.setTypeId(typeData.getTypeId()); // 假设CategoryItem有这个临时字段
                            return new AbstractMap.SimpleEntry<>(typeData.getTypeId(), item);
                        }))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));

        // 组装最终结果
        return groupedByType.entrySet().stream()
                .map(entry -> {
                    TypeData result = new TypeData();
                    result.setTypeId(entry.getKey());
                    result.setList(entry.getValue());
                    return result;
                })
                .collect(Collectors.toList());
    }
    /**
     * 批量处理多个celebrity的数据
     * @param allTypeData 从数据库查询的所有TypeData
     * @return 按celebrity_private_id分组的结果
     */
    public static Map<String, List<TypeData>> processBatch(List<TypeData> allTypeData) {

        // 按celebrity_private_id分组
        Map<String, List<TypeData>> groupedByCelebrity = allTypeData.stream()
                .collect(Collectors.groupingBy(TypeData::getCelebrityPrivateId));

        Map<String, List<TypeData>> result = new HashMap<>();

        for (Map.Entry<String, List<TypeData>> entry : groupedByCelebrity.entrySet()) {
            String celebrityId = entry.getKey();
            List<TypeData> celebrityData = entry.getValue();

            // 对每个celebrity的数据进行处理
            result.put(celebrityId, processForCelebrity(allTypeData, celebrityId));
        }

        return result;
    }
}
