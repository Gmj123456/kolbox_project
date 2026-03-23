package org.jeecg.modules.kol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.constant.enums.YesNoStatus;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.kol.entity.*;
import org.jeecg.modules.kol.mapper.KolBrandMapper;
import org.jeecg.modules.kol.service.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 品牌表
 * @Author: xyc
 * @Date: 2024-12-18 19:48:28
 * @Version: V1.0
 */
@Service
public class KolBrandServiceImpl extends ServiceImpl<KolBrandMapper, KolBrand> implements IKolBrandService {
    @Resource
    private IKolProductService productService;
    @Resource
    private IKolContactService kolContactService;
    @Resource
    private IKolProductAttributeRelationService productAttributeRelationService;
    @Resource
    private IKolProductCategoryService productCategoryService;
    @Lazy
    @Resource
    private IKolTagProductService tagProductService;
    @Resource
    private ISysBaseAPI sysBaseAPI;

    @Override
    public KolBrand getBrandName(String brandName) {
        LambdaQueryWrapper<KolBrand> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(KolBrand::getBrandName, brandName);
        List<KolBrand> lists = this.list(queryWrapper);
        return lists.stream().findFirst().orElse(null);
    }

    @Override
    public List<KolBrand> listByNames(List<String> brandNames) {
        LambdaQueryWrapper<KolBrand> queryWrapper = new LambdaQueryWrapper<>();
        if (brandNames != null && !brandNames.isEmpty()) {
            queryWrapper.in(KolBrand::getBrandName, brandNames);
        }
        return this.list(queryWrapper);
    }

    @Override
    public List<KolBrand> queryListAll(String brandName) {
        LambdaQueryWrapper<KolBrand> queryWrapper = new LambdaQueryWrapper<>();
        if (oConvertUtils.isNotEmpty(brandName)) {
            queryWrapper.like(KolBrand::getBrandName, brandName);
        }
        queryWrapper.eq(KolBrand::getIsDelete, YesNoStatus.NO.getCode());
        queryWrapper.orderByAsc(KolBrand::getBrandName);
        return this.list(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateImportDataBatch(List<KolBrand> brandListSave, List<KolBrand> brandListUpdate) {
        if (brandListSave != null && !brandListSave.isEmpty()) {
            this.saveBatch(brandListSave);
        }
        if (brandListUpdate != null && !brandListUpdate.isEmpty()) {
            this.updateBatchById(brandListUpdate);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editBrandData(KolBrand kolBrand) {
        String brandId = kolBrand.getId();
        KolBrand brand = this.getById(brandId);
        if (!brand.getBrandName().equals(kolBrand.getBrandName())) {
            productService.lambdaUpdate().eq(KolProduct::getBrandId, brandId).set(KolProduct::getBrandName, kolBrand.getBrandName()).update();
            kolContactService.lambdaUpdate().eq(KolContact::getBrandId, brandId).set(KolContact::getBrandName, kolBrand.getBrandName()).update();
            productAttributeRelationService.lambdaUpdate().eq(KolProductAttributeRelation::getBrandId, brandId).set(KolProductAttributeRelation::getBrandName, kolBrand.getBrandName()).update();
            productCategoryService.lambdaUpdate().eq(KolProductCategory::getBrandId, brandId).set(KolProductCategory::getBrandName, kolBrand.getBrandName()).update();
            tagProductService.lambdaUpdate().eq(KolTagProduct::getBrandId, brandId).set(KolTagProduct::getBrandName, kolBrand.getBrandName()).update();
            // 更新推广品牌
            List<KolProduct> products = productService.lambdaQuery().eq(KolProduct::getBrandId, brandId).list();
            if (products != null && !products.isEmpty()) {
                List<String> productIds = products.stream().map(KolProduct::getId).collect(Collectors.toList());
                sysBaseAPI.updatePromotionBrand(productIds,kolBrand.getId(), kolBrand.getBrandName());
            }
        }
        this.updateById(kolBrand);
    }
}
