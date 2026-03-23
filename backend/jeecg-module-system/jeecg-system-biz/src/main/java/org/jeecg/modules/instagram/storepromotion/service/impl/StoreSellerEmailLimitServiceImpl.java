package org.jeecg.modules.instagram.storepromotion.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.instagram.storepromotion.entity.StoreSellerEmailLimit;
import org.jeecg.modules.instagram.storepromotion.mapper.StoreSellerEmailLimitMapper;
import org.jeecg.modules.instagram.storepromotion.model.SellerEmaillimitModel;
import org.jeecg.modules.instagram.storepromotion.service.IStoreSellerEmailLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 邮件发送额度明细表
 * @Author: jeecg-boot
 * @Date:   2020-10-02
 * @Version: V1.0
 */
@Service
public class StoreSellerEmailLimitServiceImpl extends ServiceImpl<StoreSellerEmailLimitMapper, StoreSellerEmailLimit> implements IStoreSellerEmailLimitService {

    @Autowired
    private StoreSellerEmailLimitMapper storeSellerEmailLimitMapper;
    @Override
    public IPage<SellerEmaillimitModel> getLimitList(Page<StoreSellerEmailLimit> page, StoreSellerEmailLimit storeSellerEmailLimit) {
        return storeSellerEmailLimitMapper.getLimitList(page,storeSellerEmailLimit);
    }
}
