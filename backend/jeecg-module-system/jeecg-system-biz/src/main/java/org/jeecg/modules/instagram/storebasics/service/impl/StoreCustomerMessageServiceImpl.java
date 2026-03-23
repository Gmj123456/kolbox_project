package org.jeecg.modules.instagram.storebasics.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.instagram.storebasics.entity.StoreCustomerMessage;
import org.jeecg.modules.instagram.storebasics.mapper.StoreCustomerMessageMapper;
import org.jeecg.modules.instagram.storebasics.service.IStoreCustomerMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 客户信息表
 * @Author: jeecg-boot
 * @Date:   2020-12-19
 * @Version: V1.0
 */
@Service
public class StoreCustomerMessageServiceImpl extends ServiceImpl<StoreCustomerMessageMapper, StoreCustomerMessage> implements IStoreCustomerMessageService {

    @Autowired
    private StoreCustomerMessageMapper storeCustomerMessageMapper;

    /**
     * 客户留言信息列表查询
     */
    @Override
    public IPage<StoreCustomerMessage> queryAll(Page<StoreCustomerMessage> page, StoreCustomerMessage storeCustomerMessage){
        return storeCustomerMessageMapper.queryAll(page, storeCustomerMessage);
    }
}
