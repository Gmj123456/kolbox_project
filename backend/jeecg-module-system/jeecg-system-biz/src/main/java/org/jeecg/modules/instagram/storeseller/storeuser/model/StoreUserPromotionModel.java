package org.jeecg.modules.instagram.storeseller.storeuser.model;

import lombok.Data;
import org.jeecg.modules.instagram.storeseller.storeuser.entity.StoreUserPromotion;
import org.jeecg.modules.instagram.storeseller.storeuser.entity.StoreUserPromotionCelebrity;

import java.util.List;

/**
 * @Description: 商家产品促销
 * @Author: jeecg-boot
 * @Date: 2021-05-21
 * @Version: V1.0
 */
@Data
public class StoreUserPromotionModel extends StoreUserPromotion {

    /**
     * 网红明细
     */
    private List<StoreUserPromotionCelebrity> celebrityList;
}
