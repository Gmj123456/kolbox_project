package org.jeecg.modules.instagram.storepromotion.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.instagram.storepromotion.entity.StorePackagePurchaseConsume;
import org.jeecg.modules.instagram.storepromotion.mapper.StorePackagePurchaseConsumeMapper;
import org.jeecg.modules.instagram.storepromotion.model.StorePackagePurchaseConsumeModel;
import org.jeecg.modules.instagram.storepromotion.service.IStorePackagePurchaseConsumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 商家消费流量包表
 * @Author: jeecg-boot
 * @Date:   2020-10-01
 * @Version: V1.0
 */
@Service
public class StorePackagePurchaseConsumeServiceImpl extends ServiceImpl<StorePackagePurchaseConsumeMapper, StorePackagePurchaseConsume> implements IStorePackagePurchaseConsumeService {

    @Autowired
    private StorePackagePurchaseConsumeMapper storePackagePurchaseConsumeMapper;
    @Override
    public IPage<StorePackagePurchaseConsumeModel> getPurchaseConsumeList(Page<StorePackagePurchaseConsume> page, StorePackagePurchaseConsumeModel storePackagePurchaseConsumeModel) {
        return storePackagePurchaseConsumeMapper.getPurchaseConsumeList(page,storePackagePurchaseConsumeModel);
    }
}
