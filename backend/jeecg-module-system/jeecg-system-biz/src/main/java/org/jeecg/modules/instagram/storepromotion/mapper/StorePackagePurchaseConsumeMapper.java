package org.jeecg.modules.instagram.storepromotion.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.instagram.storepromotion.entity.StorePackagePurchaseConsume;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.instagram.storepromotion.model.StorePackagePurchaseConsumeModel;

/**
 * @Description: 商家消费流量包表
 * @Author: jeecg-boot
 * @Date:   2020-10-01
 * @Version: V1.0
 */
public interface StorePackagePurchaseConsumeMapper extends BaseMapper<StorePackagePurchaseConsume> {
    IPage<StorePackagePurchaseConsumeModel> getPurchaseConsumeList(Page<StorePackagePurchaseConsume> page, @Param("storePackagePurchaseConsumeModel") StorePackagePurchaseConsumeModel storePackagePurchaseConsumeModel);
}
