package org.jeecg.modules.promotion.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.promotion.entity.KolPromotionRequest;

import java.util.Date;

public interface IKolPromotionRequestService extends IService<KolPromotionRequest> {

    IPage<KolPromotionRequest> queryPageList(
            Page<KolPromotionRequest> page,
            String name,
            String companyName,
            String phone,
            String email,
            String startDate,
            String endDate
    );

    void addPromotionRequest(KolPromotionRequest promotionRequest);

    void updatePromotionRequest(KolPromotionRequest promotionRequest);

    void deletePromotionRequest(String id);
}
