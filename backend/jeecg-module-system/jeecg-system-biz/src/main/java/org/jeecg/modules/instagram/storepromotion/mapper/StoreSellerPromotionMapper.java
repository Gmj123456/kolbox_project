package org.jeecg.modules.instagram.storepromotion.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionPayment;
import org.jeecg.modules.instagram.storepromotion.entity.StoreSellerPromotion;
import org.jeecg.modules.instagram.storepromotion.model.PromotionStatisticsModel;
import org.jeecg.modules.instagram.storepromotion.model.StoreSellerPromotionModel;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @Description: 商家推广管理
 * @Author: jeecg-boot
 * @Date: 2021-02-07
 * @Version: V1.0
 */
public interface StoreSellerPromotionMapper extends BaseMapper<StoreSellerPromotion> {

    /**
     * 功能描述:获取列表
     *
     * @Author: nqr
     * @Date 2021-02-08 15:54:19
     */
    IPage<StoreSellerPromotion> pageList(Page<StoreSellerPromotion> page, @Param("storeSellerPromotionModel") StoreSellerPromotionModel storeSellerPromotionModel, @Param("sql") String sql);

    /**
     * 推广需求维护列表查询
     */
    IPage<StoreSellerPromotionModel> queryStoreSellerPromotionManagementList(Page<StoreSellerPromotionModel> page, @Param("storeSellerPromotionModel") StoreSellerPromotionModel storeSellerPromotionModel);

    /**
     * 功能描述:根据推广单号查询列表
     *
     * @param code
     * @return java.util.List<org.jeecg.modules.instagram.storesellerpromotion.entity.StoreSellerPromotion>
     * @Date 2021-06-05 10:37:48
     */
    List<StoreSellerPromotion> queryByExtensionCode(@Param("code") String code);

    /**
     * 功能描述:推广统计
     *
     * @param promotionStatisticsModel
     * @return com.baomidou.mybatisplus.core.metadata.IPage<org.jeecg.modules.instagram.storesellerpromotion.model.StoreSellerPromotionModel>
     * @Date 2021-07-13 16:00:23
     */
    PromotionStatisticsModel getPromotionStatistics(@Param("promotionStatisticsModel") PromotionStatisticsModel promotionStatisticsModel);

    /**
     * 功能描述:推广统计明细
     *
     * @return org.jeecg.common.api.vo.Result<?>
     * @Date 2021-07-13 15:32:12
     */
    IPage<StoreSellerPromotionModel> getPromotionStatisticsDetail(Page<StoreSellerPromotionModel> page, StoreSellerPromotionModel storeSellerPromotionModel);

    /**
     * 功能描述:推广人员统计
     *
     * @param promotionStatisticsModel
     * @return org.jeecg.modules.instagram.storesellerpromotion.model.PromotionStatisticsModel
     * @Date 2021-07-23 10:01:56
     */
    List<PromotionStatisticsModel> getExtensionStaffStatistics(@Param("promotionStatisticsModel") PromotionStatisticsModel promotionStatisticsModel);

    /**
     * 功能描述:网红负责人统计
     *
     * @param promotionStatisticsModel
     * @return java.util.List<org.jeecg.modules.instagram.storesellerpromotion.model.PromotionStatisticsModel>
     * @Date 2021-07-23 15:49:58
     */
    List<PromotionStatisticsModel> getPersonChargeStatistics(@Param("promotionStatisticsModel") PromotionStatisticsModel promotionStatisticsModel);

    IPage<StoreSellerPromotionModel> getPaymentStatisticsDetail(Page<StorePromotionPayment> page, @Param("promotionStatisticsModel") StoreSellerPromotionModel storeSellerPromotionModel);

    List<StoreSellerPromotionModel> getPaymentStatisticsDetailByXls(@Param("promotionStatisticsModel") StoreSellerPromotionModel storeSellerPromotionModel);

    IPage<StoreSellerPromotionModel> getPaymentStatisticsDetailExceedTime(Page<StorePromotionPayment> page, @Param("promotionStatisticsModel") StoreSellerPromotionModel storeSellerPromotionModel);

    List<StoreSellerPromotionModel> getPaymentStatisticsDetailExceedTimeByXls(@Param("promotionStatisticsModel") StoreSellerPromotionModel storeSellerPromotionModel);

    /**
     * 功能描述:更新商务人员列表查询
     *
     * @return java.util.List<org.jeecg.modules.instagram.storesellerpromotion.entity.StoreSellerPromotion>
     * @Date 2023-08-03 15:32:48
     */
    List<LinkedHashMap<String, Object>> getPromotionList(@Param("storeSellerPromotionModel") StoreSellerPromotionModel storeSellerPromotionModel);
    /**
     * @description: 商家查询推广需求列表
     * @author: nqr
     * @date: 2026/2/3 9:36
     **/
    IPage<StoreSellerPromotion> queryPageSellerList(Page<StoreSellerPromotion> page, @Param("storeSellerPromotionModel") StoreSellerPromotionModel storeSellerPromotionModel);
}
