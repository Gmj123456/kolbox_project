package org.jeecg.modules.instagram.storepromotion.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.instagram.storepromotion.entity.StorePackagePurchaseConsume;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.instagram.storepromotion.model.StorePackagePurchaseConsumeModel;

/**
 * @Description: 商家消费流量包表
 * @Author: jeecg-boot
 * @Date:   2020-10-01
 * @Version: V1.0
 */
public interface IStorePackagePurchaseConsumeService extends IService<StorePackagePurchaseConsume> {
    IPage<StorePackagePurchaseConsumeModel> getPurchaseConsumeList(Page<StorePackagePurchaseConsume> page, StorePackagePurchaseConsumeModel storePackagePurchaseConsumeModel);
}
