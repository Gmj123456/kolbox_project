package org.jeecg.modules.instagram.storepromotion.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.system.vo.KolProductVo;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionGoods;
import org.jeecg.modules.instagram.storepromotion.model.StorePromotionGoodsModel;

import java.util.List;

/**
 * @Description: 商家推广产品表
 * @Author: jeecg-boot
 * @Date: 2021-02-07
 * @Version: V1.0
 */
public interface IStorePromotionGoodsService extends IService<StorePromotionGoods> {
    /**
     * 通过sellerId查询
     */
    List<StorePromotionGoods> queryByPromId(@Param("promId") String promId);

    /**
     * 功能描述:修改推广产品状态
     *
     * @Author: nqr
     * @Date 2021-03-12 12:01:48
     */
    void updateGoodsStatus(StorePromotionGoods storePromotionGoods, boolean flag);

    /**
     * 功能描述:根据推广需求id获取产品和网红信息
     *
     * @Author: nqr
     * @Date 2021-03-15 11:43:14
     */
    List<StorePromotionGoodsModel> getList(String promId);

    /**
     * 功能描述:修改产品状态
     *
     * @param goodsList 产品列表
     * @return void
     * @Date 2021-06-29 15:43:17
     */
    void updateGoods(List<StorePromotionGoods> goodsList);

    /**
     * 功能描述:根据推广id，获取推广下所有产品
     *
     * @param promId 推广id
     * @return java.util.List<org.jeecg.modules.instagram.storepromotiongoods.entity.StorePromotionGoods>
     * @Date 2021-08-12 17:20:43
     */
    List<StorePromotionGoods> getPromGoodsList(String promId);
    /**
     * @description: 更新产品信息
     * @author: nqr
     * @date: 2025/7/19 16:47
     **/
    void updatePromotionGoodsInfo(KolProductVo productVo);
    /**
     * @description: 根据id获取产品信息
     * @author: nqr
     * @date: 2025/7/19 16:47
     **/
    boolean getPromGoods(String id);
}
