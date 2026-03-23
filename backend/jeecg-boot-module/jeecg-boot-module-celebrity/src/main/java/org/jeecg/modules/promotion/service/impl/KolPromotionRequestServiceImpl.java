package org.jeecg.modules.promotion.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.promotion.entity.KolPromotionRequest;
import org.jeecg.modules.promotion.mapper.KolPromotionRequestMapper;
import org.jeecg.modules.promotion.service.IKolPromotionRequestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Service
public class KolPromotionRequestServiceImpl extends ServiceImpl<KolPromotionRequestMapper, KolPromotionRequest> implements IKolPromotionRequestService {

    @Override
    public IPage<KolPromotionRequest> queryPageList(
            Page<KolPromotionRequest> page,
            String name,
            String companyName,
            String phone,
            String email,
            String startDate,
            String endDate) {

        LambdaQueryWrapper<KolPromotionRequest> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(name)) {
            queryWrapper.like(KolPromotionRequest::getName, name);
        }
        if (StringUtils.isNotBlank(companyName)) {
            queryWrapper.like(KolPromotionRequest::getCompanyName, companyName);
        }
        if (StringUtils.isNotBlank(phone)) {
            queryWrapper.eq(KolPromotionRequest::getPhone, phone);
        }
        if (StringUtils.isNotBlank(email)) {
            queryWrapper.eq(KolPromotionRequest::getEmail, email);
        }
        if (startDate != null) {
            queryWrapper.ge(KolPromotionRequest::getCreateTime, startDate);
        }
        if (endDate != null) {
            queryWrapper.le(KolPromotionRequest::getCreateTime, endDate);
        }

        queryWrapper.eq(KolPromotionRequest::getDelFlag, 0);
        queryWrapper.orderByDesc(KolPromotionRequest::getCreateTime);

        return this.page(page, queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPromotionRequest(KolPromotionRequest promotionRequest) {
        promotionRequest.setDelFlag(0);
        this.save(promotionRequest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePromotionRequest(KolPromotionRequest promotionRequest) {
        promotionRequest.setDelFlag(0);
        this.updateById(promotionRequest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePromotionRequest(String id) {
        KolPromotionRequest entity = this.getById(id);
        if (entity != null) {
            entity.setDelFlag(1);
            this.updateById(entity);
        }
    }
}
