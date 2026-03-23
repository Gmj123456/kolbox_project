package org.jeecg.modules.instagram.storepromotion.service;

import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionCelebrityPayment;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.instagram.storepromotion.mdoel.StorePromotionCelebrityPaymentModel;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionGoodsCelebrity;

/**
 * @Description: 网红支付列表
 * @Author: jeecg-boot
 * @Date: 2021-09-27
 * @Version: V1.0
 */
public interface IStorePromotionCelebrityPaymentService extends IService<StorePromotionCelebrityPayment> {

    /**
     * 功能描述:保存网红支付明细
     *
     * @param storePromotionCelebrityPaymentModel 网红支付明细
     * @return void
     * @Date 2021-09-27 16:54:57
     */
    void saveCelebrityPayment(StorePromotionCelebrityPaymentModel storePromotionCelebrityPaymentModel);

    /**
     * 功能描述:获取产品网红
     *
     * @param goodsId
     * @param celebrityPrivateId
     * @return org.jeecg.modules.instagram.storepromotiongoodscelebrity.entity.StorePromotionGoodsCelebrity
     * @Date 2021-09-30 18:01:07
     */
    StorePromotionGoodsCelebrity getGoodsCelebrity(String goodsId, String celebrityPrivateId, String promId);

    void cancelPayment(StorePromotionCelebrityPaymentModel storePromotionCelebrityPaymentModel);
}
