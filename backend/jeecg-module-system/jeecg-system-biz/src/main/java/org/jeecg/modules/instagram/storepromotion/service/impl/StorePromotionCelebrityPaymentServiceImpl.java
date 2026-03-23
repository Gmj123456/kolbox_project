package org.jeecg.modules.instagram.storepromotion.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.constant.enums.YesNoStatus;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.storepromotion.entity.StoreSellerPromotion;
import org.jeecg.modules.instagram.storepromotion.service.IStoreSellerPromotionService;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionCelebrityPayment;
import org.jeecg.modules.instagram.storepromotion.mapper.StorePromotionCelebrityPaymentMapper;
import org.jeecg.modules.instagram.storepromotion.mdoel.StorePromotionCelebrityPaymentModel;
import org.jeecg.modules.instagram.storepromotion.service.IStorePromotionCelebrityPaymentService;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionGoodsCelebrity;
import org.jeecg.modules.instagram.storepromotion.service.IStorePromotionGoodsCelebrityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Description: 网红支付列表
 * @Author: jeecg-boot
 * @Date: 2021-09-27
 * @Version: V1.0
 */
@Service
public class StorePromotionCelebrityPaymentServiceImpl extends ServiceImpl<StorePromotionCelebrityPaymentMapper, StorePromotionCelebrityPayment> implements IStorePromotionCelebrityPaymentService {

    @Autowired
    private IStorePromotionGoodsCelebrityService goodsCelebrityService;
    @Autowired
    private IStoreSellerPromotionService storeSellerPromotionService;

    /**
     * 功能描述:保存网红支付明细
     *
     * @param storePromotionCelebrityPaymentModel 网红支付明细
     * @return void
     * @Date 2021-09-27 16:54:57
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveCelebrityPayment(StorePromotionCelebrityPaymentModel storePromotionCelebrityPaymentModel) {
        String goodsCelId = storePromotionCelebrityPaymentModel.getGoodsCelId();
        String promId = storePromotionCelebrityPaymentModel.getPromId();
        BigDecimal payAmount = storePromotionCelebrityPaymentModel.getPayAmount();
        Integer payCurrency = storePromotionCelebrityPaymentModel.getPayCurrency();
        BigDecimal exchangeRate = storePromotionCelebrityPaymentModel.getExchangeRate();
        BigDecimal payment;
        //判断支付货币类型
        //人民币
        if (payCurrency == YesNoStatus.YES.getCode()) {
            payment = payAmount.divide(exchangeRate, 4, BigDecimal.ROUND_HALF_UP);
        } else {
            payment = payAmount;
        }
        //获取当前推广
        StoreSellerPromotion sellerPromotion = storeSellerPromotionService.getById(promId);
        //获取网红已付总金额
        BigDecimal celebrityPayTotalAmount = oConvertUtils.isNotEmpty(sellerPromotion.getCelebrityPayTotalAmount()) ? sellerPromotion.getCelebrityPayTotalAmount() : BigDecimal.ZERO;
        //修改网红已付总金额
        StoreSellerPromotion sellerPromotionNew = new StoreSellerPromotion();
        sellerPromotionNew.setId(promId);
        sellerPromotionNew.setCelebrityPayTotalAmount(celebrityPayTotalAmount.add(payment).setScale(4, BigDecimal.ROUND_HALF_UP));
        storeSellerPromotionService.updateById(sellerPromotionNew);
        //获取产品网红已付金额
        StorePromotionGoodsCelebrity goodsCelebrity = goodsCelebrityService.getById(goodsCelId);
        BigDecimal celebrityPayAmount = oConvertUtils.isNotEmpty(goodsCelebrity.getCelebrityPayAmount()) ? goodsCelebrity.getCelebrityPayAmount() : BigDecimal.ZERO;
        //修改产品网红已付金额
        StorePromotionGoodsCelebrity goodsCelebrityNew = new StorePromotionGoodsCelebrity();
        goodsCelebrityNew.setId(goodsCelId);
        goodsCelebrityNew.setCelebrityPayAmount(celebrityPayAmount.add(payment).setScale(4, BigDecimal.ROUND_HALF_UP));
        goodsCelebrityNew.setOnlineTime(goodsCelebrity.getOnlineTime());
        goodsCelebrityService.updateById(goodsCelebrityNew);
        if (oConvertUtils.isEmpty(storePromotionCelebrityPaymentModel.getPayTime())) {
            storePromotionCelebrityPaymentModel.setPayTime(new Date());
        }
        storePromotionCelebrityPaymentModel.setSellerId(sellerPromotion.getSellerId());
        storePromotionCelebrityPaymentModel.setSellerName(sellerPromotion.getSellerName());
        storePromotionCelebrityPaymentModel.setExtensionCode(sellerPromotion.getExtensionCode());
        //保存网红支付明细
        save(storePromotionCelebrityPaymentModel);
    }

    /**
     * 功能描述:获取产品网红
     *
     * @param goodsId
     * @param celebrityPrivateId
     * @return org.jeecg.modules.instagram.storepromotiongoodscelebrity.entity.StorePromotionGoodsCelebrity
     * @Date 2021-09-30 18:01:07
     */
    @Override
    public StorePromotionGoodsCelebrity getGoodsCelebrity(String goodsId, String celebrityPrivateId, String promId) {
        LambdaQueryWrapper<StorePromotionGoodsCelebrity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StorePromotionGoodsCelebrity::getGoodsId, goodsId);
        queryWrapper.eq(StorePromotionGoodsCelebrity::getCelebrityPrivateId, celebrityPrivateId);
        queryWrapper.eq(StorePromotionGoodsCelebrity::getPromId, promId);
        List<StorePromotionGoodsCelebrity> list = goodsCelebrityService.list(queryWrapper);
        if (oConvertUtils.isNotEmpty(list) && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 功能描述:撤销网红付款
     *
     * @param storePromotionCelebrityPaymentModel
     * @return void
     * @Date 2021-10-05 08:14:12
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelPayment(StorePromotionCelebrityPaymentModel storePromotionCelebrityPaymentModel) {
        /**
         * 1、根据id获取当前支付明细
         * 2、获取支付金额，再从网红推广支付总金额里，产品网红支付金额，扣除
         * 3、删除当前支付明细
         */
        //根据当前id获取此条支付详情
        String celebrityPaymentId = storePromotionCelebrityPaymentModel.getId();
        StorePromotionCelebrityPayment promotionCelebrityPayment = this.getById(celebrityPaymentId);
        BigDecimal payAmount = promotionCelebrityPayment.getPayAmount();
        String promId = promotionCelebrityPayment.getPromId();
        String goodsId = promotionCelebrityPayment.getGoodsId();
        String celebrityPrivateId = promotionCelebrityPayment.getCelebrityPrivateId();
        //根据私有网红id和产品id获取当前产品网红
        StorePromotionGoodsCelebrity storePromotionGoodsCelebrity = this.getGoodsCelebrity(goodsId, celebrityPrivateId, promId);
        //获取网红已付金额
        BigDecimal celebrityPayAmount = storePromotionGoodsCelebrity.getCelebrityPayAmount();
        BigDecimal celebrityPayAmountNew = celebrityPayAmount.subtract(payAmount);
        celebrityPayAmountNew = celebrityPayAmountNew.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : celebrityPayAmountNew;
        StorePromotionGoodsCelebrity storePromotionGoodsCelebrityNew = new StorePromotionGoodsCelebrity();
        storePromotionGoodsCelebrityNew.setId(storePromotionGoodsCelebrity.getId());
        storePromotionGoodsCelebrityNew.setOnlineTime(storePromotionGoodsCelebrity.getOnlineTime());
        storePromotionGoodsCelebrityNew.setCelebrityPayAmount(celebrityPayAmountNew);
        //修改产品网红已付金额
        goodsCelebrityService.updateById(storePromotionGoodsCelebrityNew);
        //获取当前推广
        StoreSellerPromotion sellerPromotion = storeSellerPromotionService.getById(promId);
        BigDecimal celebrityPayTotalAmount = sellerPromotion.getCelebrityPayTotalAmount();
        BigDecimal celebrityPayTotalAmountNew = celebrityPayTotalAmount.subtract(payAmount);
        celebrityPayTotalAmountNew = celebrityPayTotalAmountNew.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : celebrityPayTotalAmountNew;
        StoreSellerPromotion sellerPromotionNew = new StoreSellerPromotion();
        sellerPromotionNew.setId(sellerPromotion.getId());
        sellerPromotionNew.setCelebrityPayTotalAmount(celebrityPayTotalAmountNew);
        //修改推广网红已付金额
        storeSellerPromotionService.updateById(sellerPromotionNew);
        this.removeById(celebrityPaymentId);
    }
}
