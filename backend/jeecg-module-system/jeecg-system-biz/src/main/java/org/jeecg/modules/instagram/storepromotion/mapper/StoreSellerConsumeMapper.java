package org.jeecg.modules.instagram.storepromotion.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.instagram.storepromotion.model.StoreSellerConsumeModel;
import org.jeecg.modules.instagram.storepromotion.entity.StoreSellerConsume;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 消费记录表
 * @Author: jeecg-boot
 * @Date:   2020-09-30
 * @Version: V1.0
 */
public interface StoreSellerConsumeMapper extends BaseMapper<StoreSellerConsume> {

    void deleteByUserId(String id);
    IPage<StoreSellerConsume> parentList(Page<StoreSellerConsume> page, @Param("storeSellerConsumeModel") StoreSellerConsumeModel storeSellerConsumeModel);
}
