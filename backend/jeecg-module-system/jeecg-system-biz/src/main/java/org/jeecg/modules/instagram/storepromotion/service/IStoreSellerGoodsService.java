package org.jeecg.modules.instagram.storepromotion.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.instagram.storecelebrity.model.StoreSellDetailModel;
import org.jeecg.modules.instagram.storepromotion.entity.StoreSellerGoods;
import org.jeecg.modules.instagram.storepromotion.model.SellerCommerceModel;
import org.jeecg.modules.instagram.storepromotion.model.StoreSellerGoodsModel;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 商家商品库表
 * @Author: jeecg-boot
 * @Date:   2020-10-02
 * @Version: V1.0
 */
public interface IStoreSellerGoodsService extends IService<StoreSellerGoods> {

    /**
     * 根据Id查询商品及卖家商家顾问
     */
    StoreSellerGoods queryGoodsAndSellerCounseloById(String id);

    /**
     * 根据id查询商品及商品细节和私人网红
     */
    List<StoreSellerGoodsModel> queryStoreSwllerGoodsAndDetailAndCelebrityPrivateById(String id);

    /**
     * 商家带货统计管理列表查询
     */
    List<SellerCommerceModel> sellerCommerceManagementList(SellerCommerceModel sellerCommerceModel);

    /**
     * 根据商家id查询带货统计列表
     */
    IPage<StoreSellDetailModel> queryBySellerId(Page<StoreSellDetailModel> page, SellerCommerceModel sellerCommerceModel);
}
