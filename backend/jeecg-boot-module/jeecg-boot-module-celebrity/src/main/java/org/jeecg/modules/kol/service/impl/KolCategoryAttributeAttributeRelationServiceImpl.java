package org.jeecg.modules.kol.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.kol.entity.KolCategory;
import org.jeecg.modules.kol.entity.KolCategoryAttributeRelation;
import org.jeecg.modules.kol.mapper.KolCategoryAttributeRelationMapper;
import org.jeecg.modules.kol.model.KolCategoryAttributeRelationModel;
import org.jeecg.modules.kol.model.TypeData;
import org.jeecg.modules.kol.service.IKolCategoryAttributeRelationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: 类目关联表
 * @Author: nqr
 * @Date: 2025-06-21
 * @Version: V1.0
 */
@Service
public class KolCategoryAttributeAttributeRelationServiceImpl extends ServiceImpl<KolCategoryAttributeRelationMapper, KolCategoryAttributeRelation> implements IKolCategoryAttributeRelationService {
    @Override
    public IPage<KolCategoryAttributeRelationModel> pageList(Page<KolCategoryAttributeRelationModel> page, KolCategoryAttributeRelationModel kolCategoryAttributeRelationModel) {
        IPage<KolCategoryAttributeRelationModel> modelIPage = this.baseMapper.pageList(page, kolCategoryAttributeRelationModel);
        List<KolCategoryAttributeRelationModel> records = getKolCategoryRelationModels(modelIPage.getRecords());
        modelIPage.setRecords(records);
        return modelIPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveData(String categoryId, List<KolCategoryAttributeRelation> saveList, List<String> childRemoveIds) {
        lambdaUpdate().eq(KolCategoryAttributeRelation::getCategoryId, categoryId).remove();
        if (!childRemoveIds.isEmpty()) {
            this.removeByIds(childRemoveIds);
        }
        this.saveBatch(saveList);
    }

    @Override
    public List<TypeData> queryByCategoryId(KolCategory category) {
        String categoryId = category.getId();

        // 查询父类和当前分类的关联数据
        String parentId = category.getParentId();
        List<KolCategoryAttributeRelationModel> parentList = oConvertUtils.isEmpty(parentId) || "0".equals(parentId) ? Collections.emptyList() : this.baseMapper.queryByCategoryId(parentId);
        ;

        List<KolCategoryAttributeRelationModel> currentList = this.baseMapper.queryByCategoryId(categoryId);

        Map<String, KolCategoryAttributeRelationModel> currentMap = currentList.stream().collect(Collectors.toMap(KolCategoryAttributeRelationModel::getAttributeCategoryId, Function.identity(), (existing, replacement) -> existing));

        // 转换为结果列表
        List<TypeData> resultList = new ArrayList<>();

        // 父类和当前分类的关联数据合并
        if (!parentList.isEmpty()) {

            // 父类关联数据按照类型分组
            Map<String, List<KolCategoryAttributeRelationModel>> map = parentList.stream().collect(Collectors.groupingBy(KolCategoryAttributeRelationModel::getSubAttributeTypeId));

            for (Map.Entry<String, List<KolCategoryAttributeRelationModel>> entry : map.entrySet()) {
                TypeData typeData = new TypeData();
                typeData.setTypeId(entry.getKey());

                List<TypeData.CategoryItem> categoryItems = entry.getValue().stream().map(relation -> {
                    TypeData.CategoryItem item = new TypeData.CategoryItem();
                    item.setAttributeId(relation.getAttributeCategoryId());

                    // 判断子类中是否存在当前数据，设置级别和选中状态
                    KolCategoryAttributeRelationModel current = currentMap.get(relation.getAttributeCategoryId());
                    item.setIsSel(current != null);
                    item.setLevel(current != null ? current.getLevel() : relation.getLevel());
                    return item;
                }).collect(Collectors.toList());

                typeData.setList(categoryItems);
                resultList.add(typeData);
            }
        } else {

            // 不存在父类关联数据，直接返回当前分类关联数据
            Map<String, List<KolCategoryAttributeRelationModel>> map = currentList.stream().collect(Collectors.groupingBy(KolCategoryAttributeRelationModel::getSubAttributeTypeId));

            for (Map.Entry<String, List<KolCategoryAttributeRelationModel>> entry : map.entrySet()) {
                TypeData typeData = new TypeData();
                typeData.setTypeId(entry.getKey());
                List<TypeData.CategoryItem> categoryItems = new ArrayList<>();
                entry.getValue().forEach(relation -> {
                    TypeData.CategoryItem item = new TypeData.CategoryItem();
                    item.setLevel(relation.getLevel());
                    item.setAttributeId(relation.getAttributeCategoryId());
                    item.setIsSel(true);
                    categoryItems.add(item);
                });
                typeData.setList(categoryItems);
                resultList.add(typeData);
            }
        }
        return resultList;
    }

    @Override
    public List<KolCategoryAttributeRelationModel> getCategoryList(KolCategoryAttributeRelationModel kolCategoryAttributeRelationModel) {
        List<KolCategoryAttributeRelationModel> list = this.baseMapper.getCategoryList(kolCategoryAttributeRelationModel);
        return getKolCategoryRelationModels(list);
    }

    private static List<KolCategoryAttributeRelationModel> getKolCategoryRelationModels(List<KolCategoryAttributeRelationModel> list) {
        ObjectMapper mapper = new ObjectMapper();
        list.forEach(item -> {
            String dataListStr = item.getDataListStr();
            try {
                // 直接反序列化为 List<TypeData>
                List<TypeData> dataList = mapper.readValue(dataListStr, new TypeReference<List<TypeData>>() {
                });
                item.setDataListStr(null);
                item.setDataList(dataList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return list;
    }

    @Override
    public List<TypeData> getProductCategory(KolCategory category) {
        String categoryId = category.getId();
        List<KolCategoryAttributeRelationModel> currentList = this.baseMapper.queryByCategoryId(categoryId);

        // 转换为结果列表
        List<TypeData> resultList = new ArrayList<>();
        // 不存在父类关联数据，直接返回当前分类关联数据
        Map<String, List<KolCategoryAttributeRelationModel>> map = currentList.stream().collect(Collectors.groupingBy(KolCategoryAttributeRelationModel::getSubAttributeTypeId));

        for (Map.Entry<String, List<KolCategoryAttributeRelationModel>> entry : map.entrySet()) {
            TypeData typeData = new TypeData();
            typeData.setTypeId(entry.getKey());
            List<TypeData.CategoryItem> categoryItems = new ArrayList<>();
            entry.getValue().forEach(relation -> {
                TypeData.CategoryItem item = new TypeData.CategoryItem();
                item.setLevel(relation.getLevel());
                item.setAttributeId(relation.getAttributeCategoryId());
                item.setIsSel(true);
                categoryItems.add(item);
            });
            typeData.setList(categoryItems);
            resultList.add(typeData);
        }
        return resultList;
    }

    @Override
    public List<String> queryCategoryAttributes(String categoryId) {
        return this.baseMapper.queryCategoryAttributes(categoryId);
    }

    @Override
    public List<KolCategoryAttributeRelationModel> getAllCategoryRelations(List<String> categoryIds) {
        return this.baseMapper.getAllCategoryRelations(categoryIds);
    }

    @Override
    public IPage<KolCategoryAttributeRelationModel> pageRelationList(Page<KolCategoryAttributeRelationModel> pageAttribute, KolCategoryAttributeRelationModel kolCategoryAttributeRelationModel) {
        IPage<KolCategoryAttributeRelationModel> kolCategoryAttributeRelationModelIPage = this.baseMapper.pageRelationList(pageAttribute, kolCategoryAttributeRelationModel);
        List<KolCategoryAttributeRelationModel> records = getKolCategoryRelationModels(kolCategoryAttributeRelationModelIPage.getRecords());
        kolCategoryAttributeRelationModelIPage.setRecords(records);
        return kolCategoryAttributeRelationModelIPage;
    }
}
