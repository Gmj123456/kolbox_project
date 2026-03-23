package org.jeecg.modules.instagram.storepromotion.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionGoods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.instagram.storepromotion.model.StorePromotionGoodsModel;

/**
 * @Description: 商家推广产品表
 * @Author: jeecg-boot
 * @Date:   2021-02-07
 * @Version: V1.0
 */
public interface StorePromotionGoodsMapper extends BaseMapper<StorePromotionGoods> {

    /**
     * 通过sellerId查询
     * */
    List<StorePromotionGoods> queryByPromId(@Param("promId") String promId);

    List<StorePromotionGoodsModel> getList(@Param("promId")String promId);
}
