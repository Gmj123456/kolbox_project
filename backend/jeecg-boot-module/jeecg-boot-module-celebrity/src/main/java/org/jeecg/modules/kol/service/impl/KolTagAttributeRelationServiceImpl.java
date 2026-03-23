package org.jeecg.modules.kol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.kol.entity.KolAttribute;
import org.jeecg.modules.kol.entity.KolTagAttributeRelation;
import org.jeecg.modules.kol.entity.KolTags;
import org.jeecg.modules.kol.mapper.KolTagAttributeRelationMapper;
import org.jeecg.modules.kol.model.KolSearchModel;
import org.jeecg.modules.kol.model.KolTagsModel;
import org.jeecg.modules.kol.model.TypeData;
import org.jeecg.modules.kol.service.IKolAttributeService;
import org.jeecg.modules.kol.service.IKolTagAttributeRelationService;
import org.jeecg.modules.kol.service.IKolTagsService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 标签与类目关联表
 * @Author: nqr
 * @Date: 2025-05-21
 * @Version: V1.0
 */
@Service
public class KolTagAttributeRelationServiceImpl extends ServiceImpl<KolTagAttributeRelationMapper, KolTagAttributeRelation> implements IKolTagAttributeRelationService {
    @Lazy
    @Autowired
    private IKolTagsService kolTagsService;
    @Autowired
    private IKolAttributeService attributeService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveData(KolTags kolTagsNew, List<KolTagAttributeRelation> categoryRelationListSave, boolean categoryRelationExists) {
        updateCategoryRelation(kolTagsNew, categoryRelationListSave, categoryRelationExists, 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateData(KolTags kolTagsNew, List<KolTagAttributeRelation> categoryRelationListSave, String tagId, boolean tagNameExists) {
        if (tagNameExists) {
            lambdaUpdate().set(KolTagAttributeRelation::getIsDelete, 1).eq(KolTagAttributeRelation::getTagId, tagId).update();
        }
        updateCategoryRelation(kolTagsNew, categoryRelationListSave, true, 1);
    }


    private void updateCategoryRelation(KolTags kolTagsNew, List<KolTagAttributeRelation> categoryRelationListSave, boolean categoryRelationExists, int isAdd) {
        // 删除原有标签与类目关联关系
        LambdaUpdateChainWrapper<KolTagAttributeRelation> updateChainWrapper = lambdaUpdate().eq(KolTagAttributeRelation::getTagId, kolTagsNew.getId());
        if (categoryRelationExists) {
            boolean b = isAdd == 0 ? updateChainWrapper.remove() : updateChainWrapper.set(KolTagAttributeRelation::getIsDelete, 1).update();
        }

        // 新增或修改标签
        kolTagsService.saveOrUpdate(kolTagsNew);
        // 新增或修改标签与类目关联关系
        if (!categoryRelationListSave.isEmpty()) {
            List<String> tagIds = categoryRelationListSave.stream().map(KolTagAttributeRelation::getTagId).distinct().collect(Collectors.toList());
            lambdaUpdate().in(KolTagAttributeRelation::getTagId, tagIds).remove();
            saveBatch(categoryRelationListSave);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recoverTag(String id) {
        KolTagAttributeRelation tagCategoryRelation = getById(id);
        String tagId = tagCategoryRelation.getTagId();
        lambdaUpdate().set(KolTagAttributeRelation::getIsDelete, 0).eq(KolTagAttributeRelation::getTagId, tagId).update();
        kolTagsService.lambdaUpdate().set(KolTags::getIsDelete, 0).eq(KolTags::getId, tagId).update();
    }

    @Override
    public List<KolTagAttributeRelation> createCategoryRelation(KolTagsModel kolTagsModel, List<TypeData> dataList) {
        List<String> attributeIds = dataList.stream()
                .flatMap(x -> x.getList().stream())
                .map(TypeData.CategoryItem::getAttributeId)
                .collect(Collectors.toList());
        List<KolAttribute> attributeList = (List<KolAttribute>) attributeService.listByIds(attributeIds);
        return buildCategoryRelations(kolTagsModel, attributeList, dataList);
    }

    /**
     * 构建分类关联数据
     */
    private List<KolTagAttributeRelation> buildCategoryRelations(KolTagsModel kolTagsModel, List<KolAttribute> attributeList, List<TypeData> dataList) {
        return dataList.stream().flatMap(typeData -> typeData.getList().stream()).map(categoryItem -> createAttribute(kolTagsModel, attributeList, categoryItem)).collect(Collectors.toList());
    }

    private KolTagAttributeRelation createAttribute(KolTagsModel kolTagsModel, List<KolAttribute> attributeList, TypeData.CategoryItem categoryItem) {
        KolAttribute attribute = attributeList.stream().filter(x -> x.getId().equals(categoryItem.getAttributeId())).findFirst().get();

        KolTagAttributeRelation attributeRelation = new KolTagAttributeRelation();
        BeanUtils.copyProperties(kolTagsModel, attributeRelation);
        attributeRelation.setId(IdWorker.get32UUID());
        attributeRelation.setTagId(kolTagsModel.getId());
        attributeRelation.setAttributeId(attribute.getId());
        attributeRelation.setAttributeName(attribute.getAttributeName());
        attributeRelation.setAttributeTypeId(attribute.getAttributeTypeId());
        attributeRelation.setAttributeTypeName(attribute.getAttributeTypeName());
        attributeRelation.setIsDelete(0);
        attributeRelation.setLevel(categoryItem.getLevel());
        attributeRelation.setCreateBy(kolTagsModel.getCreateBy());
        attributeRelation.setCreateTime(new Date());
        attributeRelation.setUpdateBy(kolTagsModel.getUpdateBy());
        attributeRelation.setUpdateTime(new Date());
        attributeRelation.setProductId(kolTagsModel.getProductIds());
        return attributeRelation;
    }

    @Override
    public List<TypeData> getSelectedCategories(String tagId) {
        List<KolTagAttributeRelation> categoryRelationList = this.lambdaQuery().eq(KolTagAttributeRelation::getTagId, tagId).list();
        // 转换为结果列表
        List<TypeData> resultList = new ArrayList<>();
        // 不存在父类关联数据，直接返回当前分类关联数据
        Map<String, List<KolTagAttributeRelation>> map = categoryRelationList.stream().collect(Collectors.groupingBy(KolTagAttributeRelation::getAttributeTypeId));

        for (Map.Entry<String, List<KolTagAttributeRelation>> entry : map.entrySet()) {
            TypeData typeData = new TypeData();
            typeData.setTypeId(entry.getKey());
            List<TypeData.CategoryItem> categoryItems = new ArrayList<>();
            entry.getValue().forEach(relation -> {
                TypeData.CategoryItem item = new TypeData.CategoryItem();
                item.setLevel(relation.getLevel());
                item.setCategoryId(relation.getAttributeId());
                item.setIsSel(true);
                categoryItems.add(item);
            });
            typeData.setList(categoryItems);
            resultList.add(typeData);
        }
        return resultList;
    }

    @Override
    public void getAttributeTagNames(KolSearchModel searchModel) {
        String attributeIds = searchModel.getAttributeIds();
        if (attributeIds == null || attributeIds.isEmpty()) {
            return;
        }
        List<KolTagAttributeRelation> tagAttributeRelations = lambdaQuery().eq(KolTagAttributeRelation::getAttributeId, attributeIds).eq(KolTagAttributeRelation::getPlatformType, searchModel.getPlatformType()).list();
        List<String> tagName = tagAttributeRelations.stream().map(KolTagAttributeRelation::getTagName).distinct().collect(Collectors.toList());
        if (tagName.isEmpty()) {
            return;
        }
        List<String> tagNameList = Optional.ofNullable(searchModel.getTagNameList()).orElse(new ArrayList<>());
        tagNameList.addAll(tagName);
        searchModel.setTagNameList(tagNameList);
    }

    @Override
    public Map<String, String> getTagAttributeTypes(List<String> tagIds) {
        return this.baseMapper.getTagAttributeTypes(tagIds);
    }

    @Override
    public List<KolTagAttributeRelation> getTagAttributeDetails(List<String> tagIds) {
        return this.baseMapper.getTagAttributeDetails(tagIds);
    }

    /**
     * 功能描述：查询标签产品对应的属性
     * @Param:
     * @param kolTagAttributeRelation
     * @Author: fengLiu
     * @Date: 2025/12/25 15:20
     */
    @Override
    public List<KolTagAttributeRelation> queryAttributeByTag(KolTagAttributeRelation kolTagAttributeRelation) {

        LambdaQueryWrapper<KolTagAttributeRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(KolTagAttributeRelation::getTagId, kolTagAttributeRelation.getTagId());
        queryWrapper.eq(KolTagAttributeRelation::getProductId, kolTagAttributeRelation.getProductId());
        return this.list(queryWrapper);
    }
}
