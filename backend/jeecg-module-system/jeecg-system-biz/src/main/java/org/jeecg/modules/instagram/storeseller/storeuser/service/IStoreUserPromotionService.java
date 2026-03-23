package org.jeecg.modules.instagram.storeseller.storeuser.service;

import org.jeecg.modules.instagram.storeseller.storeuser.entity.StoreUserPromotion;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.instagram.storeseller.storeuser.entity.StoreUserPromotionCelebrity;

import java.util.List;

/**
 * @Description: 商家产品促销
 * @Author: jeecg-boot
 * @Date: 2021-05-21
 * @Version: V1.0
 */
public interface IStoreUserPromotionService extends IService<StoreUserPromotion> {

    /**
     * 功能描述: 保存商家数据
     *
     * @param storeUserPromotion 商家数据
     * @param celebrityList      网红列表
     * @return void
     * @Date 2021-05-21 16:52:07
     */
    void saveCelebrityList(StoreUserPromotion storeUserPromotion, List<StoreUserPromotionCelebrity> celebrityList);

    /**
     * 功能描述:修改推广状态
     *
     * @param id     方案ID
     * @param status 推广状态
     * @return void
     * @Date 2021-05-22 11:37:40
     */
    void updatePromStatus(String id, Integer status);

    /**
     * 功能描述:修改方案生成
     *
     * @param celebrityListNew   网红列表
     * @param storeUserPromotion 方案
     * @return void
     * @Date 2021-06-05 15:16:53
     */
    void updatePromotion(List<StoreUserPromotionCelebrity> celebrityListNew, StoreUserPromotion storeUserPromotion);
}
