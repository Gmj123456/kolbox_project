package org.jeecg.modules.instagram.storepromotion.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionPayment;
import org.jeecg.modules.instagram.storepromotion.model.StorePromotionPaymentModel;

import java.util.List;

public interface StorePromotionPaymentMapper extends BaseMapper<StorePromotionPayment> {


    IPage<StorePromotionPaymentModel> pageList(Page<StorePromotionPayment> page,@Param("storePromotionPayment") StorePromotionPaymentModel storePromotionPayment);
    IPage<StorePromotionPaymentModel> pagePromList(Page<StorePromotionPayment> page,@Param("storePromotionPayment") StorePromotionPaymentModel storePromotionPayment);

    List<StorePromotionPayment> queryStorePromotionPaymentListByPromId(@Param("promId") String promId);

    List<StorePromotionPaymentModel> selectlist(@Param("storePromotionPayment") StorePromotionPaymentModel storePromotionPayment);

    List<StorePromotionPaymentModel> listAll(@Param("storePromotionPayment")StorePromotionPaymentModel storePromotionPayment);

    IPage<StorePromotionPaymentModel> paymentStatisticsList(Page<StorePromotionPayment> page,@Param("storePromotionPayment") StorePromotionPaymentModel storePromotionPayment);

    List<StorePromotionPaymentModel> paymentStatistics(@Param("storePromotionPayment") StorePromotionPaymentModel storePromotionPayment);
}
