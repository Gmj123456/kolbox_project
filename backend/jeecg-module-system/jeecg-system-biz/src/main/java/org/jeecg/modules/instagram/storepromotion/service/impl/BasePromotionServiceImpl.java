package org.jeecg.modules.instagram.storepromotion.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionGoods;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionGoodsCelebrity;
import org.jeecg.modules.instagram.storepromotion.entity.StoreSellerPromotion;
import org.jeecg.modules.instagram.storepromotion.service.IBasePromotionService;
import org.jeecg.modules.instagram.storepromotion.service.IStorePromotionGoodsCelebrityService;
import org.jeecg.modules.instagram.storepromotion.service.IStorePromotionGoodsService;
import org.jeecg.modules.instagram.storepromotion.service.IStoreSellerPromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BasePromotionServiceImpl implements IBasePromotionService {
    @Autowired
    private IStorePromotionGoodsCelebrityService goodsCelebrityService;
    @Autowired
    private IStoreSellerPromotionService storeSellerPromotionService;
    @Autowired
    private IStorePromotionGoodsService storePromotionGoodsService;

    /**
     * 功能描述:根据推广id查询推广信息，推广产品信息，推广网红信息
     *
     * @return com.alibaba.fastjson.JSONObject
     * @Date 2023-08-04 09:58:09
     */
    @Override
    public JSONObject getPromotionData(String promId) {
        JSONObject jsonObject = new JSONObject();
        //获取当前推广信息
        StoreSellerPromotion sellerPromotion = storeSellerPromotionService.getById(promId);
        //获取当前推广下所有的产品
        List<StorePromotionGoods> promotionGoodsList = storePromotionGoodsService.getPromGoodsList(promId);
        //获取产品下的所有网红
        List<StorePromotionGoodsCelebrity> goodsCelebrities = goodsCelebrityService.getByPromId(promId);

        jsonObject.put("sellerPromotion", sellerPromotion);
        jsonObject.put("promotionGoodsList", promotionGoodsList);
        jsonObject.put("goodsCelebrities", goodsCelebrities);

        return jsonObject;
    }
}
