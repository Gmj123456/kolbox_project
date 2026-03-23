package org.jeecg.modules.instagram.storepromotion.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.constant.PaymentConstant;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.storepromotion.entity.StoreSellerPromotion;
import org.jeecg.modules.instagram.storepromotion.mapper.StoreSellerPromotionMapper;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionPayment;
import org.jeecg.modules.instagram.storepromotion.mapper.StorePromotionPaymentMapper;
import org.jeecg.modules.instagram.storepromotion.model.StorePromotionPaymentModel;
import org.jeecg.modules.instagram.storepromotion.service.IStorePromotionPaymentService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Service
public class StorePromotionPaymentServiceImpl extends ServiceImpl<StorePromotionPaymentMapper, StorePromotionPayment>
        implements IStorePromotionPaymentService {

    @Autowired
    private StorePromotionPaymentMapper storePromotionPaymentMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private StoreSellerPromotionMapper storeSellerPromotionMapper;

    @Override
    public IPage<StorePromotionPaymentModel> pageList(Page<StorePromotionPayment> page, StorePromotionPaymentModel storePromotionPayment) {
        return storePromotionPaymentMapper.pageList(page, storePromotionPayment);
    }



    /**
     * 计算实际支付金额
     *
     * @return
     */
    @Override
    public BigDecimal getPaymentAmountRmb(StoreSellerPromotion storeSellerPromotion) {
        LambdaQueryWrapper<StorePromotionPayment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StorePromotionPayment::getPromId, storeSellerPromotion.getId());
        List<StorePromotionPayment> paymentList = storePromotionPaymentMapper.selectList(queryWrapper);

        if (paymentList.size() == 0) {
            return new BigDecimal(0);
        }

        BigDecimal totalPayAmountRmb = new BigDecimal(0);
        for (StorePromotionPayment s : paymentList) {
            if (PaymentConstant.RMB_SYMBOL.equals(s.getCurrencySymbol())) {
                // 如果rmb直接相加
                totalPayAmountRmb = totalPayAmountRmb.add(s.getPayAmount());
            } else {
                BigDecimal currentPayAmountRmb = s.getPayAmount().multiply(storeSellerPromotion.getExchangeRate());
                // 其他货币转换汇率
                totalPayAmountRmb = totalPayAmountRmb.add(currentPayAmountRmb);
            }
        }
        System.err.println("当前总费用" + totalPayAmountRmb);
        return totalPayAmountRmb;
    }

    /**
     * 功能描述: 修改账户余额，保存支付明细
     *
     * @param userNew
     * @param storePromotionPayment
     * @return void
     * @Date 2021-05-25 09:00:28
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePayment(SysUser userNew, StorePromotionPayment storePromotionPayment, StoreSellerPromotion sellerPromotionNew) {
        if (oConvertUtils.isNotEmpty(userNew.getId())) {
            sysUserMapper.updateById(userNew);
        }
        if (oConvertUtils.isNotEmpty(storePromotionPayment)) {
            save(storePromotionPayment);
        }
        if (oConvertUtils.isNotEmpty(sellerPromotionNew.getId())) {
            storeSellerPromotionMapper.updateById(sellerPromotionNew);
        }
    }

    @Override
    public BigDecimal paymentAmountRmbnNoEARNEST(StoreSellerPromotion storeSellerPromotion) {
        LambdaQueryWrapper<StorePromotionPayment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StorePromotionPayment::getPromId, storeSellerPromotion.getId());
        List<Integer> list = new ArrayList<>();
        list.add(PaymentConstant.PAY_TYPE_DEPOSIT);
        list.add(PaymentConstant.PAY_TYPE_FINAL);
        list.add(PaymentConstant.PAY_TYPE_ALL);
        queryWrapper.in(StorePromotionPayment::getPayType,list);
        List<StorePromotionPayment> paymentList = list(queryWrapper);

        if (paymentList.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalPayAmountRmb = BigDecimal.ZERO;
        for (StorePromotionPayment s : paymentList) {
            if (PaymentConstant.RMB_SYMBOL.equals(s.getCurrencySymbol())) {
                // 如果rmb直接相加
                totalPayAmountRmb = totalPayAmountRmb.add(s.getPayAmount());
            } else {
                BigDecimal currentPayAmountRmb = s.getPayAmount().multiply(storeSellerPromotion.getExchangeRate());
                // 其他货币转换汇率
                totalPayAmountRmb = totalPayAmountRmb.add(currentPayAmountRmb);
            }
        }
        System.err.println("当前总费用" + totalPayAmountRmb);
        return totalPayAmountRmb;
    }

    @Override
    public List<StorePromotionPaymentModel> selectlist(StorePromotionPaymentModel storePromotionPayment) {
        return storePromotionPaymentMapper.selectlist(storePromotionPayment);
    }

    @Override
    public List<StorePromotionPaymentModel> listAll(StorePromotionPaymentModel storePromotionPayment) {
        return storePromotionPaymentMapper.listAll(storePromotionPayment);
    }

    @Override
    public IPage<StorePromotionPaymentModel> paymentStatisticsList(Page<StorePromotionPayment> page, StorePromotionPaymentModel storePromotionPayment) {
        return storePromotionPaymentMapper.paymentStatisticsList(page,storePromotionPayment);
    }

    @Override
    public List<StorePromotionPaymentModel> paymentStatistics(StorePromotionPaymentModel storePromotionPayment) {
        return storePromotionPaymentMapper.paymentStatistics(storePromotionPayment);
    }
    /**
     * 功能描述:推广支付明细
     * @param page
    	 * @param storePromotionPayment

    * @return com.baomidou.mybatisplus.core.metadata.IPage<org.jeecg.modules.instagram.storepromotionpayment.model.StorePromotionPaymentModel>
    * @Date 2021-10-07 14:30:48
    */
    @Override
    public IPage<StorePromotionPaymentModel> pagePromList(Page<StorePromotionPayment> page, StorePromotionPaymentModel storePromotionPayment) {
        return storePromotionPaymentMapper.pagePromList(page, storePromotionPayment);
    }
}
