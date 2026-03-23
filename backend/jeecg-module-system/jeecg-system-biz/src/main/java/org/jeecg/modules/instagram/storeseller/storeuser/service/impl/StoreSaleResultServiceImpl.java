package org.jeecg.modules.instagram.storeseller.storeuser.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.instagram.storeseller.storeuser.entity.StoreSaleResult;
import org.jeecg.modules.instagram.storeseller.storeuser.mapper.StoreSaleResultMapper;
import org.jeecg.modules.instagram.storeseller.storeuser.model.StoreSaleResultModel;
import org.jeecg.modules.instagram.storeseller.storeuser.service.IStoreSaleResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: storeSaleResult
 * @Author: jeecg-boot
 * @Date:   2020-10-16
 * @Version: V1.0
 */
@Service
public class StoreSaleResultServiceImpl extends ServiceImpl<StoreSaleResultMapper, StoreSaleResult> implements IStoreSaleResultService {

    @Autowired
    StoreSaleResultMapper storeSaleResultMapper;


    @Override
    public IPage<StoreSaleResult> parentList(Page<StoreSaleResult> page, StoreSaleResultModel StoreSaleResult) {
        return storeSaleResultMapper.parentList(page,StoreSaleResult);
    }





}
