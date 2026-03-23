package org.jeecg.modules.instagram.storepromotion.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionGoods;
import org.jeecg.modules.instagram.storepromotion.entity.StoreSellerPromotion;
import org.jeecg.modules.instagram.storepromotion.model.StorePromotionGoodsModel;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionGoodsCelebrity;
import org.jeecg.modules.instagram.storepromotion.model.GoodsCelebrityModel;
import org.jeecg.modules.instagram.storepromotion.model.StorePromotionGoodsCelebrityModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Description: 商家推广产品网红关联表
 * @Author: jeecg-boot
 * @Date: 2021-02-07
 * @Version: V1.0
 */
public interface IStorePromotionGoodsCelebrityService extends IService<StorePromotionGoodsCelebrity> {

    /**
     * 功能描述: 删除该需求下的网红
     *
     * @Author: nqr
     * @Date 2021-02-08 17:03:58
     */
    void delProm(String id, List<String> goodsIdList);

    /**
     * 编辑私有网红
     */
    void updateStorePromotionGoodsCelebrityById(StorePromotionGoodsCelebrityModel storePromotionGoodsCelebrityModel);

    /**
     * 删除匹配网红,应付金额改变
     */
    Result<?> handleDelete(StorePromotionGoodsCelebrity storePromotionGoodsCelebrity);

    List<StorePromotionGoodsCelebrity> queryCelebritiesById(String id);

    /**
     * 功能描述:批量删除匹配网红
     *
     * @param ids
     * @return org.jeecg.common.api.vo.Result<?>
     * @Date 2021-06-29 15:02:44
     */
    void delCelebrityBatch(String ids);

    /**
     * 功能描述:查看历史匹配网红
     *
     * @param promId 推广id
     * @return java.util.List<org.jeecg.modules.instagram.storepromotiongoodscelebrity.entity.StorePromotionGoodsCelebrity>
     * @Date 2021-06-29 17:31:12
     */
    IPage<StorePromotionGoodsModel> getOldCelebrity(Page<StorePromotionGoodsModel> page, String promId);

    /**
     * 功能描述:获取产品网红列表
     *
     * @param goodsId         产品id
     * @param celebrityIdList 网红列表id
     * @param isDel           删除状态
     * @return java.util.List<org.jeecg.modules.instagram.storepromotiongoodscelebrity.entity.StorePromotionGoodsCelebrity>
     * @Date 2021-08-11 17:06:16
     */
    List<StorePromotionGoodsCelebrity> goodsCelebrityList(String goodsId, List<String> celebrityIdList, int isDel);

    /**
     * 功能描述:保存或修改
     *
     * @param storeSellerPromotion  推广需求
     * @param storePromotionGoods   推广产品
     * @param goodsCelebrityListNew 网红列表
     * @return void
     * @Date 2021-08-11 17:08:54
     */
    void updateOrSave(StoreSellerPromotion storeSellerPromotion, StorePromotionGoods storePromotionGoods, List<StorePromotionGoodsCelebrity> goodsCelebrityListNew);

    /**
     * 功能描述:获取需要删除的网红
     *
     * @param removeIdList 网红id
     * @return java.util.List<org.jeecg.modules.instagram.storepromotiongoodscelebrity.entity.StorePromotionGoodsCelebrity>
     * @Date 2021-08-12 17:15:21
     */
    List<StorePromotionGoodsCelebrity> getCelebrityList(List<String> removeIdList);

    /**
     * 功能描述:获取产品下的所有网红
     *
     * @param promGoodsIds 所有推广产品id
     * @return java.util.Map<java.lang.String, org.jeecg.modules.instagram.storepromotiongoodscelebrity.entity.StorePromotionGoodsCelebrity>
     * @Date 2021-08-12 17:23:46
     */
    Map<String, List<StorePromotionGoodsCelebrity>> getGoodsCelebrityList(List<String> promGoodsIds, String goodsCelebrityId, Integer celebrityPromStatus);

    Map<String, List<StorePromotionGoodsCelebrity>> getGoodsCelebrityListNew(List<String> promGoodsIds, String goodsCelebrityId, Integer celebrityPromStatus, Integer flag, Integer removeCurrentFlag);

    /**
     * 功能描述:判断网红状态
     *
     * @param goodsCelebrityList 需要删除的网红
     * @param map                所有网红
     * @return java.util.Map<java.lang.String, java.util.List < org.jeecg.modules.instagram.storepromotiongoodscelebrity.entity.StorePromotionGoodsCelebrity>>
     * @Date 2021-08-12 17:30:59
     */
    JSONObject checkCelebrityStatus(List<StorePromotionGoodsCelebrity> goodsCelebrityList, List<StorePromotionGoods> promotionGoodsList, Map<String, List<StorePromotionGoodsCelebrity>> map);

    /**
     * 功能描述:处理结果
     *
     * @param removeIdList          需要删除的产品网红id
     * @param promotionGoodsListNew 需要修改状态的产品
     * @param storeSellerPromotion  需要修改状态的推广
     * @return void
     * @Date 2021-08-13 08:31:08
     */
    void updateData(List<String> removeIdList, List<StorePromotionGoods> promotionGoodsListNew, StoreSellerPromotion storeSellerPromotion);

    /**
     * 功能描述:编辑网红属性
     *
     * @param promotionGoodsListNew 需要修改的产品状态
     * @param storeSellerPromotion  需要修改的推广状态
     * @return void
     * @Date 2021-08-13 10:18:42
     */
    void updateGoodsCelebrityById(List<StorePromotionGoods> promotionGoodsListNew, StoreSellerPromotion storeSellerPromotion);

    /**
     * 功能描述:判断产品状态
     *
     * @param map 所有网红
     * @return java.util.Map<java.lang.String, java.util.List < org.jeecg.modules.instagram.storepromotiongoodscelebrity.entity.StorePromotionGoodsCelebrity>>
     * @Date 2021-08-12 17:30:59
     */
    JSONObject checkGoodsStatus(List<StorePromotionGoods> promotionGoodsList, Map<String, List<StorePromotionGoodsCelebrity>> map);

    JSONObject checkGoodsStatusNew(List<StorePromotionGoods> promotionGoodsList, Map<String, List<StorePromotionGoodsCelebrity>> map);

    /**
     * 功能描述:获取当前推广下所有网红金额
     *
     * @param promId
     * @param celebrityPrice
     * @param goodsCelebrityId
     * @return java.math.BigDecimal
     * @Date 2021-09-28 08:12:11
     */
    BigDecimal getCelebrityTotalAmount(String promId, BigDecimal celebrityPrice, String goodsCelebrityId);

    /**
     * 功能描述:获取当前推广下所有网红金额
     *
     * @param promId
     * @param celebrityPrice
     * @param goodsCelebrityId
     * @return java.math.BigDecimal
     * @Date 2021-09-28 08:12:11
     */
    JSONObject getCelebrityTotalAmountNew(String promId, BigDecimal celebrityPrice, BigDecimal celebrityPaypalFees, BigDecimal promPrice, String goodsCelebrityId);

    /**
     * 功能描述:网红费用统计
     *
     * @param page
     * @param storePromotionGoodsCelebrityModel
     * @return com.baomidou.mybatisplus.core.metadata.IPage<org.jeecg.modules.instagram.storepromotiongoodscelebrity.entity.StorePromotionGoodsCelebrity>
     * @Date 2021-09-28 15:17:57
     */
    IPage<StorePromotionGoodsCelebrityModel> getCelebrityStatistics(Page<StorePromotionGoodsCelebrityModel> page, StorePromotionGoodsCelebrityModel storePromotionGoodsCelebrityModel);

    /**
     * 功能描述:网红费用统计 excel
     *
     * @param goodsCelebrityModel
     * @return java.util.List<org.jeecg.modules.instagram.storepromotiongoodscelebrity.model.StorePromotionGoodsCelebrityModel>
     * @Date 2021-09-28 16:11:38
     */
    List<GoodsCelebrityModel> getCelebrityStatisticsList(GoodsCelebrityModel goodsCelebrityModel);

    /**
     * 功能描述:网红费用统计总额
     *
     * @param storePromotionGoodsCelebrityModel
     * @return java.util.List<org.jeecg.modules.instagram.storepromotiongoodscelebrity.model.StorePromotionGoodsCelebrityModel>
     * @Date 2021-09-28 16:11:38
     */
    List<StorePromotionGoodsCelebrityModel> listAll(StorePromotionGoodsCelebrityModel storePromotionGoodsCelebrityModel);

    /**
     * 功能描述:修改产品网红状态，推广网红费用
     *
     * @param storeSellerPromotion
     * @param promotionGoodsCelebrity
     * @return void
     * @Date 2021-10-18 11:29:35
     */
    void updateGoodsCelebrity(StoreSellerPromotion storeSellerPromotion, StorePromotionGoodsCelebrity promotionGoodsCelebrity);

    void updateGoodsCelebrityNew(StoreSellerPromotion storeSellerPromotion, StorePromotionGoodsCelebrity promotionGoodsCelebrity, List<StorePromotionGoods> promotionGoodsListNew);

    /**
     * 功能描述:判断推广状态
     *
     * @param sellerPromotionNow
     * @return void
     * @Date 2022-02-09 10:03:47
     */
    Integer checkPromStatus(StoreSellerPromotion sellerPromotionNow);

    /**
     * 功能描述:计算网红paypal费
     *
     * @return java.math.BigDecimal
     * @Date 2022-04-19 09:17:01
     */
    BigDecimal getPayPalNew(BigDecimal promPriceNew, BigDecimal paypalRate, BigDecimal paypalBaseFees);

    /**
     * 功能描述: 计算推广费，网红成本，网红paypal费用，总推广费，总推广费金额
     *
     * @param promId           推广id
     * @param exchangeRate     汇率
     * @param celebrityPrice   网红费用
     * @param taxAmount        网红税费
     * @param promPrice        网红推广费
     * @param goodsCelebrityId 产品网红id
     * @return org.jeecg.modules.instagram.storesellerpromotion.entity.StoreSellerPromotion
     * @Date 2022-04-20 18:06:56
     */
    StoreSellerPromotion updateAmount(String promId, BigDecimal exchangeRate, BigDecimal celebrityPrice, BigDecimal taxAmount, BigDecimal promPrice, String goodsCelebrityId);

    /**
     * 功能描述:查询当前推广下所有已选中网红
     *
     * @param promId
     * @return java.util.List<org.jeecg.modules.instagram.storepromotiongoodscelebrity.entity.StorePromotionGoodsCelebrity>
     * @Date 2022-04-23 14:28:47
     */
    List<StorePromotionGoodsCelebrity> getByPromId(String promId);

    List<StorePromotionGoodsCelebrity> getByPromIdAll(String promId);

    /**
     * 功能描述:编辑推广网红
     *
     * @return void
     * @Date 2023-08-04 09:36:01
     */
    void editGoodsCelebrity(StorePromotionGoodsCelebrity storePromotionGoodsCelebrity, List<StorePromotionGoods> promotionGoodsListNew, StoreSellerPromotion storeSellerPromotion);

    /**
     * 功能描述:
     *
     * @return java.util.List<org.jeecg.modules.instagram.storepromotiongoodscelebrity.entity.StorePromotionGoodsCelebrity>
     * @Date 2023-10-06 11:43:48
     */
    List<StorePromotionGoodsCelebrity> getByGoodsId(List<String> goodsIds, String id);

    /**
     * 功能描述:批量修改网红
     *
     * @return void
     * @Date 2023-11-20 14:43:46
     */
    void updateGoodsCelebrityBatch(List<StorePromotionGoodsCelebrity> goodsCelebrityOldList, StoreSellerPromotion sellerPromotionNew);
}
