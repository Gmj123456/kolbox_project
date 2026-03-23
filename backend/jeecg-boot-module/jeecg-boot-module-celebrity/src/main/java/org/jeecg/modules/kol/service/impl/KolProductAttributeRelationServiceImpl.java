package org.jeecg.modules.kol.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.kol.entity.KolCategory;
import org.jeecg.modules.kol.entity.KolProduct;
import org.jeecg.modules.kol.entity.KolProductAttributeRelation;
import org.jeecg.modules.kol.mapper.KolProductAttributeRelationMapper;
import org.jeecg.modules.kol.model.TypeData;
import org.jeecg.modules.kol.service.IKolCategoryService;
import org.jeecg.modules.kol.service.IKolProductAttributeRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 产品与标签分类类目关联表
 * @Author: nqr
 * @Date: 2025-05-24
 * @Version: V1.0
 */
@Service
public class KolProductAttributeRelationServiceImpl extends ServiceImpl<KolProductAttributeRelationMapper, KolProductAttributeRelation> implements IKolProductAttributeRelationService {
    @Autowired
    private IKolCategoryService categoryService;

    @Override
    public List<KolProductAttributeRelation> createProductCategory(KolProduct productSave, List<TypeData> dataList) {
        List<String> categoryIds = dataList.stream()
                .flatMap(x -> x.getList().stream())
                .map(TypeData.CategoryItem::getCategoryId)
                .collect(Collectors.toList());
        List<KolCategory> categoryList = (List<KolCategory>) categoryService.listByIds(categoryIds);
        return buildCategoryRelations(productSave, categoryList, dataList);
    }

    /**
     * 构建分类关联数据
     */
    private List<KolProductAttributeRelation> buildCategoryRelations(KolProduct productSave, List<KolCategory> categoryList, List<TypeData> dataList) {
        return dataList.stream().flatMap(typeData -> typeData.getList().stream()).map(categoryItem -> createCategory(productSave, categoryList, categoryItem)).collect(Collectors.toList());
    }

    private KolProductAttributeRelation createCategory(KolProduct productSave, List<KolCategory> categoryList, TypeData.CategoryItem categoryItem) {
        KolCategory kolCategory = categoryList.stream().filter(x -> x.getId().equals(categoryItem.getCategoryId())).findFirst().get();

        KolProductAttributeRelation productAttributeRelation = new KolProductAttributeRelation();
        productAttributeRelation.setId(IdWorker.get32UUID());
        productAttributeRelation.setBrandId(productSave.getBrandId());
        productAttributeRelation.setLevel(categoryItem.getLevel());
        productAttributeRelation.setProductId(productSave.getId());
        productAttributeRelation.setAttributeId(categoryItem.getCategoryId());
        productAttributeRelation.setAttributeTypeId(kolCategory.getCategoryTypeId());
        productAttributeRelation.setBrandName(productSave.getBrandName());
        productAttributeRelation.setProductName(productSave.getProductName());
        productAttributeRelation.setAttributeName(kolCategory.getCategoryName());
        productAttributeRelation.setAttributeTypeName(kolCategory.getCategoryTypeName());
        productAttributeRelation.setIsDelete(0);
        return productAttributeRelation;
    }
}
