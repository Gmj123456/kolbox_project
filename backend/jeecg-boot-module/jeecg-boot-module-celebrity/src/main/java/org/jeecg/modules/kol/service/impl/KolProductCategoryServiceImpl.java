package org.jeecg.modules.kol.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.kol.entity.*;
import org.jeecg.modules.kol.mapper.KolProductCategoryMapper;
import org.jeecg.modules.kol.model.KolProductModel;
import org.jeecg.modules.kol.model.TypeData;
import org.jeecg.modules.kol.service.IKolAttributeService;
import org.jeecg.modules.kol.service.IKolProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 产品与标签分类类目关联表
 * @Author: nqr
 * @Date: 2025-05-24
 * @Version: V1.0
 */
@Service
public class KolProductCategoryServiceImpl extends ServiceImpl<KolProductCategoryMapper, KolProductCategory> implements IKolProductCategoryService {
    @Autowired
    private IKolAttributeService attributeService;
    @Override
    public List<KolProductCategory> createProductCategory(KolProductModel productModel) {
        KolProductCategory productCategory = new KolProductCategory();
        productCategory.setId(IdWorker.get32UUID());
        productCategory.setBrandId(productModel.getBrandId());
        productCategory.setLevel(productModel.getLevel());
        productCategory.setProductId(productModel.getId());
        productCategory.setCategoryId(productModel.getCategoryId());
        productCategory.setBrandName(productModel.getBrandName());
        productCategory.setProductName(productModel.getProductName());
        productCategory.setCategoryName(productModel.getCategoryName());
        productCategory.setIsDelete(0);
        return Collections.singletonList(productCategory);
    }
    /**
     * 构建分类关联数据
     */
    private List<KolProductCategory> buildProductCategory(KolProduct productSave, List<KolCategory> categoryList, List<TypeData> dataList) {
        return dataList.stream().flatMap(typeData -> typeData.getList().stream()).map(categoryItem -> createCategory(productSave, categoryList, categoryItem)).collect(Collectors.toList());
    }

    private KolProductCategory createCategory(KolProduct productSave, List<KolCategory> categoryList, TypeData.CategoryItem categoryItem) {
        KolCategory kolCategory = categoryList.stream().filter(x -> x.getId().equals(categoryItem.getCategoryId())).findFirst().get();

        KolProductCategory productCategory = new KolProductCategory();
        productCategory.setId(IdWorker.get32UUID());
        productCategory.setBrandId(productSave.getBrandId());
        productCategory.setLevel(categoryItem.getLevel());
        productCategory.setProductId(productSave.getId());
        productCategory.setCategoryId(categoryItem.getCategoryId());
        productCategory.setCategoryTypeId(kolCategory.getCategoryTypeId());
        productCategory.setBrandName(productSave.getBrandName());
        productCategory.setProductName(productSave.getProductName());
        productCategory.setCategoryName(kolCategory.getCategoryName());
        productCategory.setCategoryTypeName(kolCategory.getCategoryTypeName());
        productCategory.setIsDelete(0);
        return productCategory;
    }


    @Override
    public List<KolProductAttributeRelation> createAttributeRelation(KolProduct product, List<TypeData> dataList) {
        List<String> attributeIds = dataList.stream()
                .flatMap(x -> x.getList().stream())
                .map(TypeData.CategoryItem::getAttributeId)
                .filter(oConvertUtils::isNotEmpty)
                .collect(Collectors.toList());
        List<KolAttribute> attributeList = (List<KolAttribute>) attributeService.listByIds(attributeIds);
        return buildProductAttributeRelation(product, attributeList, dataList);
    }
    /**
     * 构建分类关联数据
     */
    private List<KolProductAttributeRelation> buildProductAttributeRelation(KolProduct product, List<KolAttribute> attributeList, List<TypeData> dataList) {
        return dataList.stream().flatMap(typeData -> typeData.getList().stream()).map(categoryItem -> createProductAttributeRelation(product, attributeList, categoryItem)).collect(Collectors.toList());
    }

    private KolProductAttributeRelation createProductAttributeRelation(KolProduct product, List<KolAttribute> attributeList, TypeData.CategoryItem categoryItem) {
        KolAttribute kolAttribute = attributeList.stream().filter(x -> x.getId().equals(categoryItem.getAttributeId())).findFirst().get();

        KolProductAttributeRelation attributeRelation = new KolProductAttributeRelation();
        attributeRelation.setId(IdWorker.get32UUID());
        attributeRelation.setBrandId(product.getBrandId());
        attributeRelation.setLevel(categoryItem.getLevel());
        attributeRelation.setProductId(product.getId());
        attributeRelation.setAttributeId(categoryItem.getAttributeId());
        attributeRelation.setAttributeTypeId(kolAttribute.getAttributeTypeId());
        attributeRelation.setBrandName(product.getBrandName());
        attributeRelation.setProductName(product.getProductName());
        attributeRelation.setAttributeName(kolAttribute.getAttributeName());
        attributeRelation.setAttributeTypeName(kolAttribute.getAttributeTypeName());
        attributeRelation.setIsDelete(0);
        return attributeRelation;
    }

}
