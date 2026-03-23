package org.jeecg.modules.instagram.storepromotion.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.instagram.storepromotion.model.StorePromotionGoodsModel;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionGoodsCelebrity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.instagram.storepromotion.model.GoodsCelebrityModel;
import org.jeecg.modules.instagram.storepromotion.model.StorePromotionGoodsCelebrityModel;

/**
 * @Description: 商家推广产品网红关联表
 * @Author: jeecg-boot
 * @Date: 2021-02-07
 * @Version: V1.0
 */
public interface StorePromotionGoodsCelebrityMapper extends BaseMapper<StorePromotionGoodsCelebrity> {
    /**
     * 功能描述: 删除该需求下的网红
     *
     * @Date 2021-02-08 17:03:58
     */
    void delProm(@Param("id") String id, @Param("goodsIdList") List<String> goodsIdList);

    List<StorePromotionGoodsCelebrity> queryCelebritiesById(@Param("celebrityPrivateId") String id);

    /**
     * 功能描述:查看历史匹配网红
     *
     * @param promId 推广id
     * @return java.util.List<org.jeecg.modules.instagram.storepromotiongoodscelebrity.entity.StorePromotionGoodsCelebrity>
     * @Date 2021-06-29 17:32:55
     */
    IPage<StorePromotionGoodsModel> getOldCelebrity(Page<StorePromotionGoodsModel> page, @Param("promId") String promId);

    /**
     * 功能描述:获取网红费用
     *
     * @param promId           推广id
     * @param goodsCelebrityId 产品网红id
     * @return java.math.BigDecimal
     * @Date 2021-09-28 08:24:34
     */
    StorePromotionGoodsCelebrity getRemainingAmount(@Param("promId") String promId, @Param("goodsCelebrityId") String goodsCelebrityId);
    StorePromotionGoodsCelebrity getRemainingAmountNew(@Param("promId") String promId, @Param("goodsCelebrityId") String goodsCelebrityId);

    /**
     * 功能描述:网红费用统计
     *
     * @param page
     * @param storePromotionGoodsCelebrityModel
     * @return com.baomidou.mybatisplus.core.metadata.IPage<org.jeecg.modules.instagram.storepromotiongoodscelebrity.entity.StorePromotionGoodsCelebrity>
     * @Date 2021-09-28 15:18:52
     */
    IPage<StorePromotionGoodsCelebrityModel> getCelebrityStatistics(Page<StorePromotionGoodsCelebrityModel> page, @Param("storePromotionGoodsCelebrityModel") StorePromotionGoodsCelebrityModel storePromotionGoodsCelebrityModel);

    /**
     * 功能描述:网红费用统计excel
     *
     * @param goodsCelebrityModel
     * @return com.baomidou.mybatisplus.core.metadata.IPage<org.jeecg.modules.instagram.storepromotiongoodscelebrity.entity.StorePromotionGoodsCelebrity>
     * @Date 2021-09-28 15:18:52
     */
    List<GoodsCelebrityModel> getCelebrityStatisticsList(@Param("goodsCelebrityModel") GoodsCelebrityModel goodsCelebrityModel);

    /**
     * 功能描述:网红费用统计总额
     *
     * @param storePromotionGoodsCelebrityModel
     * @return java.util.List<org.jeecg.modules.instagram.storepromotiongoodscelebrity.model.StorePromotionGoodsCelebrityModel>
     * @Date 2021-09-28 16:11:38
     */
    List<StorePromotionGoodsCelebrityModel> listAll(@Param("storePromotionGoodsCelebrityModel") StorePromotionGoodsCelebrityModel storePromotionGoodsCelebrityModel);
}
