package org.jeecg.modules.instagram.storeseller.storeuser.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.instagram.storeseller.storeuser.entity.StoreUserPromotion;
import org.jeecg.modules.instagram.storeseller.storeuser.mapper.StoreUserPromotionMapper;
import org.jeecg.modules.instagram.storeseller.storeuser.service.IStoreUserPromotionService;
import org.jeecg.modules.instagram.storeseller.storeuser.entity.StoreUserPromotionCelebrity;
import org.jeecg.modules.instagram.storeseller.storeuser.service.IStoreUserPromotionCelebrityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: 商家产品促销
 * @Author: jeecg-boot
 * @Date: 2021-05-21
 * @Version: V1.0
 */
@Service
public class StoreUserPromotionServiceImpl extends ServiceImpl<StoreUserPromotionMapper, StoreUserPromotion> implements IStoreUserPromotionService {
    @Autowired
    private IStoreUserPromotionCelebrityService celebrityService;

    /**
     * 功能描述: 保存商家数据
     *
     * @param storeUserPromotion 商家数据
     * @param celebrityList      网红列表
     * @return void
     * @Date 2021-05-21 16:52:07
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveCelebrityList(StoreUserPromotion storeUserPromotion, List<StoreUserPromotionCelebrity> celebrityList) {
        save(storeUserPromotion);
        celebrityService.saveBatch(celebrityList);
    }

    /**
     * 功能描述:修改推广状态
     *
     * @param id     方案ID
     * @param status 推广状态
     * @return void
     * @Date 2021-05-22 11:37:40
     */
    @Override
    public void updatePromStatus(String id, Integer status) {
        StoreUserPromotion storeUserPromotion = new StoreUserPromotion();
        storeUserPromotion.setId(id);
        storeUserPromotion.setPromStatus(status);
        updateById(storeUserPromotion);
    }

    /**
     * 功能描述:修改方案生成
     *
     * @param celebrityListNew   网红列表
     * @param storeUserPromotion 方案
     * @return void
     * @Date 2021-06-05 15:16:53
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePromotion(List<StoreUserPromotionCelebrity> celebrityListNew, StoreUserPromotion storeUserPromotion) {
        celebrityService.saveBatch(celebrityListNew);
        updateById(storeUserPromotion);
    }
}
