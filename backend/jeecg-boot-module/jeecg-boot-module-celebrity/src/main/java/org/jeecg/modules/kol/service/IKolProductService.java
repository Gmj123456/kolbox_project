package org.jeecg.modules.kol.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.kol.entity.KolProductAttributeRelation;
import org.jeecg.modules.kol.entity.KolProductCategory;
import org.jeecg.modules.kol.entity.KolProduct;
import org.jeecg.modules.kol.model.KolProductModel;

import java.util.List;

/**
 * @Description: 品牌与产品关联表
 * @Author: nqr
 * @Date: 2025-05-24
 * @Version: V1.0
 */
public interface IKolProductService extends IService<KolProduct> {
    /**
     * @description:保存产品详情及其关联的品牌、产品、类目信息
     * @author: nqr
     * @date: 2025/5/24 9:29
     **/
    void saveProduct(KolProduct productSave, KolProduct productUpdate, List<KolProductCategory> productCategoryList, List<KolProductAttributeRelation> productAttributeRelationList, boolean isUpdatePromotionGoods);

    /**
     * @description: 分页查询产品详情及其关联的品牌、产品、类目信息
     * @author: nqr
     * @date: 2025/5/24 9:58
     **/
    IPage<KolProductModel> pageList(Page<KolProductModel> page, KolProductModel kolProductModel);

    void importProduct(List<KolProduct> productSaveList, List<KolProduct> productUpdateList, List<KolProductCategory> productCategorySaveList, List<KolProductAttributeRelation> productAttributeRelations);

    IPage<KolProductModel> pageListNew(Page<KolProductModel> page, KolProductModel kolProductModel);

    /**
     * @description: 根据产品ID查询产品详情及其关联的品牌、产品、类目信息
     * @author: nqr
     * @date: 2025/7/19 15:20
     **/
    List<KolProductModel> getProductListAll(KolProductModel kolProductModel);

    /**
     * @description: 根据产品ID查询推广产品
     * @author: nqr
     * @date: 2025/7/19 16:54
     **/
    boolean getPromGoods(String id);

    List<KolProduct> getProductListByBrandId(String brandId);
}
