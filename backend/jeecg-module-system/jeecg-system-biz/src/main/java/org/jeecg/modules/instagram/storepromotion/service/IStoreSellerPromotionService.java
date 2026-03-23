package org.jeecg.modules.instagram.storepromotion.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionGoods;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionPayment;
import org.jeecg.modules.instagram.storepromotion.entity.StoreSellerConsume;
import org.jeecg.modules.instagram.storepromotion.entity.StoreSellerPromotion;
import org.jeecg.modules.instagram.storepromotion.model.PromotionStatisticsModel;
import org.jeecg.modules.instagram.storepromotion.model.StoreSellerPromotionModel;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @Description: 商家推广管理
 * @Author: jeecg-boot
 * @Date: 2021-02-07
 * @Version: V1.0
 */
public interface IStoreSellerPromotionService extends IService<StoreSellerPromotion> {

    /**
     * 推广需求维护列表查询
     */
    IPage<StoreSellerPromotionModel> queryStoreSellerPromotionManagementList(Page<StoreSellerPromotionModel> page, StoreSellerPromotionModel storeSellerPromotionModel);

    /**
     * 功能描述:查询列表
     *
     * @Author: nqr
     * @Date 2021-02-08 15:53:20
     */
    IPage<StoreSellerPromotion> pageList(Page<StoreSellerPromotion> page, StoreSellerPromotionModel storeSellerPromotionModel, String sql);

    /**
     * 功能描述:保存数据
     *
     * @Author: nqr
     * @Date 2021-02-07 17:51:57
     */
    void saveList(StoreSellerPromotionModel storeSellerPromotionModel, List<StorePromotionGoods> goodsListNew);

    /**
     * 功能描述:修改推广需求
     *
     * @Author: nqr
     * @Date 2021-03-09 10:22:44
     */
    void updateGoods(List<StorePromotionGoods> editGoodsList, List<StorePromotionGoods> saveGoodsList, List<String> delGoodsIdList, StoreSellerPromotionModel storeSellerPromotionModel);

    /**
     * 功能描述:修改消费明细状态，修改推广需求付款金额，保存支付记录
     *
     * @param storeSellerConsumeNew 消费明细
     * @param sellerPromotionNew    推广需求
     * @param storePromotionPayment 支付记录
     * @return void
     * @Date 2021-05-25 10:09:05
     */
    void saveAndUpade(StoreSellerConsume storeSellerConsumeNew, StoreSellerPromotion sellerPromotionNew, StorePromotionPayment storePromotionPayment);

    /**
     * 功能描述:根据推广单号查询列表
     *
     * @param code
     * @return java.util.List<org.jeecg.modules.instagram.storesellerpromotion.entity.StoreSellerPromotion>
     * @Date 2021-06-05 10:37:48
     */
    List<StoreSellerPromotion> queryByExtensionCode(String code);

    /**
     * 功能描述:推广统计
     *
     * @param promotionStatisticsModel
     * @return com.baomidou.mybatisplus.core.metadata.IPage<org.jeecg.modules.instagram.storesellerpromotion.model.StoreSellerPromotionModel>
     * @Date 2021-07-13 16:00:23
     */
    PromotionStatisticsModel getPromotionStatistics(PromotionStatisticsModel promotionStatisticsModel);

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
     * @return com.baomidou.mybatisplus.core.metadata.IPage<org.jeecg.modules.instagram.storesellerpromotion.model.StoreSellerPromotionModel>
     * @Date 2021-07-13 16:00:23
     */
    List<PromotionStatisticsModel> getExtensionStaffStatistics(PromotionStatisticsModel promotionStatisticsModel);

    /**
     * 功能描述:网红负责人统计
     *
     * @param promotionStatisticsModel
     * @return java.util.List<org.jeecg.modules.instagram.storesellerpromotion.model.PromotionStatisticsModel>
     * @Date 2021-07-23 15:49:19
     */
    List<PromotionStatisticsModel> getPersonChargeStatistics(PromotionStatisticsModel promotionStatisticsModel);

    /**
     * 功能描述:根据产品状态判断推广状态
     *
     * @param promotionGoodsListNew 推广产品
     * @return org.jeecg.modules.instagram.storesellerpromotion.entity.StoreSellerPromotion
     * @Date 2021-08-12 18:06:27
     */
    StoreSellerPromotion checkPromStatus(String promId, List<StorePromotionGoods> promotionGoodsListNew, JSONObject jsonObject, List<String> promGoodsIds);

    /**
     * 功能描述:判断当前推广状态
     *
     * @param storeSellerPromotion 推广需求
     * @return boolean
     * @Date 2021-08-13 09:59:09
     */
    boolean checkFinishStatus(StoreSellerPromotion storeSellerPromotion);

    /**
     * 功能描述:根据当前商务人员id和时间 查询推广支付详情，分页
     *
     * @param storeSellerPromotionModel
     * @return
     */
    IPage<StoreSellerPromotionModel> getPaymentStatisticsDetail(Page<StorePromotionPayment> page, StoreSellerPromotionModel storeSellerPromotionModel);

    /**
     * 功能描述:根据当前商务人员id和时间 查询推广支付详情，不分页
     *
     * @param storeSellerPromotionModel
     * @return
     */
    List<StoreSellerPromotionModel> getPaymentStatisticsDetailByXls(StoreSellerPromotionModel storeSellerPromotionModel);

    IPage<StoreSellerPromotionModel> getPaymentStatisticsDetailExceedTime(Page<StorePromotionPayment> page, StoreSellerPromotionModel storeSellerPromotionModel);

    List<StoreSellerPromotionModel> getPaymentStatisticsDetailExceedTimeByXls(StoreSellerPromotionModel storeSellerPromotionModel);

    /**
     * 功能描述:根据产品状态判断推广状态
     *
     * @param promotionGoodsListNew 推广产品
     * @return org.jeecg.modules.instagram.storesellerpromotion.entity.StoreSellerPromotion
     * @Date 2021-08-12 18:06:27
     */
    StoreSellerPromotion checkPromStatusType(String promId, List<StorePromotionGoods> promotionGoodsListNew, JSONObject jsonObject, List<String> promGoodsIds);

    StoreSellerPromotion checkPromStatusTypeNew(String promId, List<StorePromotionGoods> promotionGoodsListNew, JSONObject jsonObject, List<String> promGoodsIds, Integer payStatus);

    /**
     * 功能描述:更新商务人员列表查询
     *
     * @return java.util.List<org.jeecg.modules.instagram.storesellerpromotion.entity.StoreSellerPromotion>
     * @Date 2023-08-03 15:32:11
     */
    List<LinkedHashMap<String, Object>> getPromotionList(StoreSellerPromotionModel storeSellerPromotionModel);

    /**
     * 功能描述:根据推广单号查询推广信息
     *
     * @return org.jeecg.modules.instagram.storesellerpromotion.entity.StoreSellerPromotion
     * @Date 2023-09-02 14:41:44
     */
    StoreSellerPromotion getByExtensionCode(String extensionCode);

    /**
     * 功能描述:获取推广已付总金额
     *
     * @return java.math.BigDecimal
     * @Date 2023-11-23 15:35:39
     */
    BigDecimal getAmount(StoreSellerPromotion sellerPromotion);

    /**
     * @description: 商务顾问审核推广推广需求
     * @author: nqr
     * @date: 2026/2/3 9:31
     **/
    void updateList(StoreSellerPromotionModel storeSellerPromotionModel, List<StorePromotionGoods> goodsListNew);
    /**
     * @description: 根据条件查询推广需求列表
     * @author: nqr
     * @date: 2026/2/3 9:36
     **/
    IPage<StoreSellerPromotion> queryPageSellerList(Page<StoreSellerPromotion> page, StoreSellerPromotionModel storeSellerPromotionModel);
    /**
     * @description: 发送微信企业号机器人通知
     * @param storeSellerPromotionModel 推广需求模型
     * @return void
     * @author: nqr
     * @date: 2026/2/4
     **/
    void sendWeChatWorkNotification(StoreSellerPromotionModel storeSellerPromotionModel);
}
