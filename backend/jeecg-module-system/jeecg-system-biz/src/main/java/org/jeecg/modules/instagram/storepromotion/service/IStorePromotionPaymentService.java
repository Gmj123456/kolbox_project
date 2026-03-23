package org.jeecg.modules.instagram.storepromotion.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.instagram.storepromotion.entity.StoreSellerPromotion;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionPayment;
import org.jeecg.modules.instagram.storepromotion.model.StorePromotionPaymentModel;
import org.jeecg.modules.system.entity.SysUser;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 推广支付记录
 * @Author: dong
 * @Date: 2021-03-09
 * @Version: V1.0
 */
public interface IStorePromotionPaymentService extends IService<StorePromotionPayment> {

    /**
     * 功能描述: 分页查询列表
     *
     * @Author: dong
     * @Date 2021-03-09 15:53:20
     */
    IPage<StorePromotionPaymentModel> pageList(Page<StorePromotionPayment> page, StorePromotionPaymentModel storePromotionPayment);


    BigDecimal getPaymentAmountRmb(StoreSellerPromotion storeSellerPromotion);

    /**
     * 功能描述: 修改账户余额，保存支付明细
     *
     * @param userNew
     * @param storePromotionPayment
     * @return void
     * @Date 2021-05-25 09:00:28
     */
    void savePayment(SysUser userNew, StorePromotionPayment storePromotionPayment, StoreSellerPromotion sellerPromotionNew);

    BigDecimal paymentAmountRmbnNoEARNEST(StoreSellerPromotion storeSellerPromotion);

    List<StorePromotionPaymentModel> selectlist(StorePromotionPaymentModel storePromotionPayment);

    List<StorePromotionPaymentModel> listAll(StorePromotionPaymentModel storePromotionPayment);

    /**
     * 根据查询条件查出所有推广支付统计，分页
     *
     * @param storePromotionPayment
     * @return
     */
    IPage<StorePromotionPaymentModel> paymentStatisticsList(Page<StorePromotionPayment> page, StorePromotionPaymentModel storePromotionPayment);

    /**
     * 根据查询条件查出所有推广支付统计，不分页
     *
     * @param storePromotionPayment
     * @return
     */
    List<StorePromotionPaymentModel> paymentStatistics(StorePromotionPaymentModel storePromotionPayment);

    /**
     * 功能描述:推广支付明细
     *
     * @param page
     * @param storePromotionPayment
     * @return com.baomidou.mybatisplus.core.metadata.IPage<org.jeecg.modules.instagram.storepromotionpayment.model.StorePromotionPaymentModel>
     * @Date 2021-10-07 14:30:34
     */
    IPage<StorePromotionPaymentModel> pagePromList(Page<StorePromotionPayment> page, StorePromotionPaymentModel storePromotionPayment);
}
