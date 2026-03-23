package org.jeecg.modules.instagram.storepromotion.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.instagram.storecelebrity.model.StoreSellDetailModel;
import org.jeecg.modules.instagram.storepromotion.entity.StoreSellerGoods;
import org.jeecg.modules.instagram.storepromotion.mapper.StoreSellerGoodsMapper;
import org.jeecg.modules.instagram.storepromotion.model.SellerCommerceModel;
import org.jeecg.modules.instagram.storepromotion.model.StoreSellerGoodsModel;
import org.jeecg.modules.instagram.storepromotion.service.IStoreSellerGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 商家商品库表
 * @Author: jeecg-boot
 * @Date:   2020-10-02
 * @Version: V1.0
 */
@Service
public class StoreSellerGoodsServiceImpl extends ServiceImpl<StoreSellerGoodsMapper, StoreSellerGoods> implements IStoreSellerGoodsService {

    @Autowired
    private StoreSellerGoodsMapper storeSellerGoodsMapper;

    /**
     * 根据Id查询商品及卖家商家顾问
     */
    @Override
    public StoreSellerGoods queryGoodsAndSellerCounseloById(String id) {
        return storeSellerGoodsMapper.queryGoodsAndSellerCounseloById(id);
    }

    /**
     * 根据id查询商品及商品细节和私人网红
     */
    @Override
    public List<StoreSellerGoodsModel> queryStoreSwllerGoodsAndDetailAndCelebrityPrivateById(String id) {
        return storeSellerGoodsMapper.queryStoreSwllerGoodsAndDetailAndCelebrityPrivateById(id);
    }

    /**
     * 商家带货统计管理列表查询
     */
    @Override
    public List<SellerCommerceModel> sellerCommerceManagementList(SellerCommerceModel sellerCommerceModel){
        return storeSellerGoodsMapper.sellerCommerceManagementList(sellerCommerceModel);
    }

    /**
     * 根据商家id查询带货统计列表
     */
    @Override
    public IPage<StoreSellDetailModel> queryBySellerId(Page<StoreSellDetailModel> page, SellerCommerceModel sellerCommerceModel){
        return storeSellerGoodsMapper.queryBySellerId(page, sellerCommerceModel);
    }
}
