package org.jeecg.modules.kol.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.kol.entity.KolAttribute;
import org.jeecg.modules.kol.entity.KolTagAttributeRelation;
import org.jeecg.modules.kol.entity.KolTagProduct;
import org.jeecg.modules.kol.entity.KolTags;
import org.jeecg.modules.kol.mapper.KolTagProductMapper;
import org.jeecg.modules.kol.model.KolSearchModel;
import org.jeecg.modules.kol.service.IKolTagAttributeRelationService;
import org.jeecg.modules.kol.service.IKolTagProductService;
import org.jeecg.modules.kol.service.IKolTagsService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Description: 标签产品管理表
 * @Author: nqr
 * @Date: 2025-06-28
 * @Version: V1.0
 */
@Service
public class KolTagProductServiceImpl extends ServiceImpl<KolTagProductMapper, KolTagProduct> implements IKolTagProductService {
    @Lazy
    @Resource
    private IKolTagsService kolTagsService;
    @Resource
    private IKolTagAttributeRelationService kolTagAttributeRelationService;


    @Override
    public List<String> getByProductId(String productId, Integer platformType) {
        List<KolTagProduct> tagProducts = lambdaQuery().eq(KolTagProduct::getProductId, productId).eq(KolTagProduct::getPlatformType, platformType).list();
        if (!tagProducts.isEmpty()) {
            return tagProducts.stream().map(KolTagProduct::getTagName).distinct().collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public void getProductTagNames(KolSearchModel searchModel) {
        String productId = searchModel.getProductId();
        Set<String> mergedTags = Stream.concat(
                // 产品标签流
                lambdaQuery()
                        .select(KolTagProduct::getTagName)
                        .eq(KolTagProduct::getProductId, productId)
                        .list()
                        .stream()
                        .map(KolTagProduct::getTagName)
                        .filter(StringUtils::isNotBlank),
                // 现有标签流
                Optional.ofNullable(searchModel.getTagNameList())
                        .map(Collection::stream)
                        .orElse(Stream.empty())
        ).collect(Collectors.toSet());

        if (!mergedTags.isEmpty()) {
            searchModel.setTagNameList(new ArrayList<>(mergedTags));
        }
    }

    @Override
    public List<String> setBrandProductTags(List<String> productIds) {
        return this.baseMapper.setBrandProductTags(productIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindingProducts(List<KolTagProduct> newBindings, List<KolTags> tagsUpdateList,
                                List<KolAttribute> attributeList) {
        // 1. 处理标签产品绑定关系：先删除所有相关的关联关系，再新增新的关联关系
        if (!newBindings.isEmpty()) {
            // 1.1 删除所有指定的标签-产品组合的绑定关系
            // 遍历所有新绑定关系，逐个删除对应的旧关联
            for (KolTagProduct binding : newBindings) {
                String tagId = binding.getTagId();
                String productId = binding.getProductId();
                // 删除该标签和产品之间的特定关联
                lambdaUpdate()
                        .eq(KolTagProduct::getTagId, tagId)
                        .eq(KolTagProduct::getProductId, productId)
                        .remove();
            }

            // 1.3 保存新的绑定关系
            saveBatch(newBindings);
        }

        // 2. 更新标签状态
        if (!tagsUpdateList.isEmpty()) {
            kolTagsService.updateBatchById(tagsUpdateList);
        }

        // 3. 处理标签产品社媒属性关联关系
        if (!newBindings.isEmpty() && attributeList != null && !attributeList.isEmpty()) {
            Date now = new Date();
            List<KolTagAttributeRelation> tagAttributeRelations = new ArrayList<>();

            // 仅处理newBindings中实际存在的新关联
            for (KolTagProduct binding : newBindings) {
                String tagId = binding.getTagId();
                String productId = binding.getProductId();

                // 3.1 删除已存在的标签产品对应的社媒属性关系
                kolTagAttributeRelationService.lambdaUpdate()
                        .eq(KolTagAttributeRelation::getTagId, tagId)
                        .eq(KolTagAttributeRelation::getProductId, productId)
                        .remove();

                // 3.2 构造新的标签产品社媒属性对照关系
                for (KolAttribute attribute : attributeList) {
                    KolTagAttributeRelation relation = new KolTagAttributeRelation();
                    relation.setId(com.baomidou.mybatisplus.core.toolkit.IdWorker.get32UUID());
                    relation.setTagId(tagId);
                    relation.setProductId(productId);
                    relation.setAttributeId(attribute.getId());
                    relation.setAttributeName(attribute.getAttributeName());
                    relation.setAttributeTypeId(attribute.getAttributeTypeId());
                    relation.setAttributeTypeName(attribute.getAttributeTypeName());
                    relation.setCreateTime(now);
                    relation.setIsDelete(0);
                    tagAttributeRelations.add(relation);
                }
            }

            // 3.3 批量保存新的标签产品社媒属性对照关系
            if (!tagAttributeRelations.isEmpty()) {
                kolTagAttributeRelationService.saveBatch(tagAttributeRelations);
            }
        }
    }


    @Override
    public List<KolTagProduct> getTagProductDetails(List<String> tagIds) {
        return this.baseMapper.getTagProductDetails(tagIds);
    }
}
