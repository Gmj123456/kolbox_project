package org.jeecg.modules.instagram.storepromotion.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionProject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.instagram.storepromotion.model.StorePromotionProjectModel;

/**
 * @Description: 产品网红历史匹配方案
 * @Author: nqr
 * @Date:   2023-09-02
 * @Version: V1.0
 */
public interface StorePromotionProjectMapper extends BaseMapper<StorePromotionProject> {

    IPage<StorePromotionProjectModel> pageList(Page<StorePromotionProjectModel> page, @Param("storePromotionProjectModel") StorePromotionProjectModel storePromotionProjectModel);
}
