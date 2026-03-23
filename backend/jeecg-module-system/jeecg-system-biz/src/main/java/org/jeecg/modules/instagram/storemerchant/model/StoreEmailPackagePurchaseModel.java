package org.jeecg.modules.instagram.storemerchant.model;

import lombok.Data;
import org.jeecg.modules.instagram.storemerchant.entity.StoreEmailPackagePurchase;

/**
 * @Description: 商家订购流量包表
 * @Author: jeecg-boot
 * @Date: 2020-10-01
 * @Version: V1.0
 */
@Data
public class StoreEmailPackagePurchaseModel extends StoreEmailPackagePurchase {
    /**
     * 创建时间
     */
    private String createDate;
    /**
     * 到期时间
     */
    private String expirationTime;
}
