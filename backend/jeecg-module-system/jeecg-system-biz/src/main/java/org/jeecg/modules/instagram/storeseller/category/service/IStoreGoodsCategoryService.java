package org.jeecg.modules.instagram.storeseller.category.service;

import org.jeecg.modules.instagram.storeseller.category.entity.StoreGoodsCategory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 商家商品类目表
 * @Author: jeecg-boot
 * @Date:   2020-10-12
 * @Version: V1.0
 */
public interface IStoreGoodsCategoryService extends IService<StoreGoodsCategory> {

    void addCategory(StoreGoodsCategory storeGoodsCategory);
    void editCategory(StoreGoodsCategory storeGoodsCategory);
    void deleteCategory(String id);
    List<StoreGoodsCategory> queryGoodsCategoryList(String id);
}
