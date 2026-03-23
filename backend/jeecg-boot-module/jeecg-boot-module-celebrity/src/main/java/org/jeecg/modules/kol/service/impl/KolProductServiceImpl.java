package org.jeecg.modules.kol.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lark.oapi.service.bitable.v1.model.AppTableRecord;
import org.jeecg.common.system.api.IKolCelebrityApi;
import org.jeecg.common.system.vo.KolProductVo;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.kol.entity.KolProduct;
import org.jeecg.modules.kol.entity.KolProductAttributeRelation;
import org.jeecg.modules.kol.entity.KolProductCategory;
import org.jeecg.modules.kol.entity.KolTagProduct;
import org.jeecg.modules.feishu.config.FeishuAppConfig;
import org.jeecg.modules.feishu.service.IFeishuService;
import org.jeecg.modules.kol.mapper.KolProductMapper;
import org.jeecg.modules.kol.model.KolProductModel;
import org.jeecg.modules.kol.service.IKolProductAttributeRelationService;
import org.jeecg.modules.kol.service.IKolProductCategoryService;
import org.jeecg.modules.kol.service.IKolProductService;
import org.jeecg.modules.kol.service.IKolTagProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @Description: 品牌与产品关联表
 * @Author: nqr
 * @Date: 2025-05-24
 * @Version: V1.0
 */
@Service
public class KolProductServiceImpl extends ServiceImpl<KolProductMapper, KolProduct> implements IKolProductService {
    @Autowired
    private IKolProductCategoryService productCategoryService;
    @Autowired
    private IKolProductAttributeRelationService productAttributeRelationService;
    @Autowired
    private IFeishuService feishuService;
    @Autowired
    private FeishuAppConfig feishuAppConfig;
    @Autowired
    private IKolTagProductService tagProductService;
    @Autowired
    private IKolCelebrityApi kolCelebrityApi;
    @Override
    public IPage<KolProductModel> pageList(Page<KolProductModel> page, KolProductModel kolProductModel) {
        return this.baseMapper.pageList(page, kolProductModel);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveProduct(KolProduct productSave, KolProduct productUpdate, List<KolProductCategory> productCategoryList, List<KolProductAttributeRelation> productAttributeRelationList,
                            boolean isUpdatePromotionGoods) {
/*        if (productSave != null) {
            productCategoryService.lambdaUpdate().eq(KolProductCategory::getProductId, productSave.getId()).remove();
            this.save(productSave);
            if (isUpdatePromotionGoods) {
                // 更新推广商品
                KolProductVo productVo = new KolProductVo();
                BeanUtils.copyProperties(productSave, productVo);
                sysBaseAPI.updatePromotionGoodsInfo(productVo);

                // 更新标签产品数据
                tagProductService.lambdaUpdate().eq(KolTagProduct::getProductId, productSave.getId()).set(KolTagProduct::getProductName, productSave.getProductName()).update();
            }
            // 同步新增飞书数据
            insertFeiShuRecord(productSave);
        }
        if (productUpdate != null) {
            KolProduct kolProductOld = this.getById(productUpdate.getId());
            this.updateById(productUpdate);
            productCategoryService.lambdaUpdate().eq(KolProductCategory::getProductId, productUpdate.getId()).remove();
            if (isUpdatePromotionGoods) {
                // 更新推广商品
                KolProductVo productVo = new KolProductVo();
                BeanUtils.copyProperties(productUpdate, productVo);
                sysBaseAPI.updatePromotionGoodsInfo(productVo);

                // 更新标签产品数据
                tagProductService.lambdaUpdate().eq(KolTagProduct::getProductId, productUpdate.getId()).set(KolTagProduct::getProductName, productUpdate.getProductName()).update();
            }
            // 同步更新飞书数据
            updateFeiShuRecord(productUpdate, kolProductOld);
        }*/
        if (productSave != null || productUpdate != null) {
            KolProduct currentProduct = productSave != null ? productSave : productUpdate;
            boolean isSaveOperation = productSave != null;
            String productId = currentProduct.getId();

            // 获取旧产品数据（仅更新操作需要）
            KolProduct kolProductOld = null;
            if (!isSaveOperation) {
                kolProductOld = this.getById(productId);
            }

            // 删除关联的产品分类
            productCategoryService.lambdaUpdate()
                    .eq(KolProductCategory::getProductId, productId)
                    .remove();

            // 保存或更新产品
            if (isSaveOperation) {
                this.save(currentProduct);
            } else {
                this.updateById(currentProduct);
            }

            // 处理推广商品和标签更新
            if (isUpdatePromotionGoods) {
                KolProductVo productVo = new KolProductVo();
                BeanUtils.copyProperties(currentProduct, productVo);
                kolCelebrityApi.updatePromotionGoodsInfo(productVo);

                tagProductService.lambdaUpdate()
                        .eq(KolTagProduct::getProductId, productId)
                        .set(KolTagProduct::getProductName, currentProduct.getProductName())
                        .update();
            }

     /*       // 处理飞书数据同步
            if (isSaveOperation) {
                insertFeiShuRecord(currentProduct);
            } else {
                updateFeiShuRecord(currentProduct, kolProductOld);
            }*/
        }
        if (!productCategoryList.isEmpty()) {
            productCategoryService.saveBatch(productCategoryList);
        }
        if (!productAttributeRelationList.isEmpty()) {
            List<String> productIds = productAttributeRelationList.stream().map(KolProductAttributeRelation::getProductId).distinct().collect(Collectors.toList());
            productAttributeRelationService.lambdaUpdate().in(KolProductAttributeRelation::getProductId, productIds).remove();
            productAttributeRelationService.saveBatch(productAttributeRelationList);
        }
    }

    private void updateFeiShuRecord(KolProduct productUpdate, KolProduct kolProductOld) {
        String productAttributesOld = kolProductOld.getProductAttributes();
        String productModelOld;
        if (oConvertUtils.isNotEmpty(productAttributesOld)) {
            productModelOld = JSON.parseObject(productAttributesOld).getString("productModel");
        } else {
            productModelOld = "";
        }
        CompletableFuture.runAsync(() -> {
            try {
                // 获取原数据行id
                String recordId = feishuService.getRecordIds(feishuAppConfig.getTagProductTableId(), feishuAppConfig.getTagProductViewId(), "产品", String.format("%s-%s-%s", kolProductOld.getBrandName(), kolProductOld.getProductName(), productModelOld));
                String tableId = feishuAppConfig.getTagProductTableId();
                String appToken = feishuAppConfig.getAppToken();
                HashMap<String, Object> map = new HashMap<>();
                String productAttributes = productUpdate.getProductAttributes();
                JSONObject jsonObject = JSON.parseObject(productAttributes);
                String productModel = jsonObject.getString("productModel");
                map.put("产品", String.format("%s-%s-%s", productUpdate.getBrandName(), productUpdate.getProductName(), productModel));
                AppTableRecord[] recordArr = new AppTableRecord[1];
                recordArr[0] = AppTableRecord.newBuilder()
                        .recordId(recordId)
                        .fields(map)
                        .build();
                feishuService.batchUpdateRecords(appToken, tableId, recordArr);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importProduct(List<KolProduct> productSaveList, List<KolProduct> productUpdateList, List<KolProductCategory> productCategorySaveList, List<KolProductAttributeRelation> productAttributeRelations) {
        // 创建产品类目关联关系
        if (!productSaveList.isEmpty()) {
            saveBatch(productSaveList);
            // 同步新增飞书数据
            productSaveList.forEach(this::insertFeiShuRecord);
        }
        if (!productUpdateList.isEmpty()) {
            updateBatchById(productUpdateList);
            productCategoryService.lambdaUpdate().in(KolProductCategory::getProductId, productUpdateList.stream().map(KolProduct::getId).collect(Collectors.toList())).remove();
        }
        if (!productAttributeRelations.isEmpty()) {
            List<String> productIds = productAttributeRelations.stream().map(KolProductAttributeRelation::getProductId).distinct().collect(Collectors.toList());
            productAttributeRelationService.lambdaUpdate().in(KolProductAttributeRelation::getProductId, productIds).remove();
            productAttributeRelationService.saveBatch(productAttributeRelations);
        }
        productCategoryService.saveBatch(productCategorySaveList);
    }

    private void insertFeiShuRecord(KolProduct productSave) {
        String tableId = feishuAppConfig.getTagProductTableId();
        String appToken = feishuAppConfig.getAppToken();
        HashMap<String, Object> map = new HashMap<>();
        String productAttributes = productSave.getProductAttributes();
        if (oConvertUtils.isNotEmpty(productAttributes)) {
            JSONObject jsonObject = JSON.parseObject(productAttributes);
            String productModel = jsonObject.getString("productModel");
            map.put("产品", String.format("%s-%s-%s", productSave.getBrandName(), productSave.getProductName(), productModel));
        }

        CompletableFuture.runAsync(() -> {
            try {
                feishuService.insertRecord(appToken, tableId, map);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public IPage<KolProductModel> pageListNew(Page<KolProductModel> page, KolProductModel kolProductModel) {
        return this.baseMapper.pageListNew(page, kolProductModel);
    }

    @Override
    public List<KolProductModel> getProductListAll(KolProductModel kolProductModel) {
        return this.baseMapper.getProductListAll(kolProductModel);
    }

    @Override
    public boolean getPromGoods(String id) {
        return kolCelebrityApi.getPromGoods(id);
    }

    @Override
    public List<KolProduct> getProductListByBrandId(String brandId) {
        return this.baseMapper.getProductListByBrandId(brandId);
    }
}
