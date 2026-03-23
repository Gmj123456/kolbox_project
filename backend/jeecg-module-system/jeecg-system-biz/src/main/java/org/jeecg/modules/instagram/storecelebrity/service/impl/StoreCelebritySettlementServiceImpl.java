package org.jeecg.modules.instagram.storecelebrity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebritySettlement;
import org.jeecg.modules.instagram.storecelebrity.mapper.StoreCelebritySettlementMapper;
import org.jeecg.modules.instagram.storecelebrity.model.StoreCelebritySettlementModel;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebritySettlementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 私有网红带货结算表 
 * @Author: jeecg-boot
 * @Date:   2021-01-03
 * @Version: V1.0
 */
@Service
public class StoreCelebritySettlementServiceImpl extends ServiceImpl<StoreCelebritySettlementMapper, StoreCelebritySettlement> implements IStoreCelebritySettlementService {

    @Autowired
    private StoreCelebritySettlementMapper storeCelebritySettlementMapper;

    /**
     * 查询同年同月是否有该私有网红带货结算记录
     * @Param celebrityPrivateId
     * @Param year
     * @Param month
     * */
    @Override
    public StoreCelebritySettlement hasStoreCelebritySettlement(String celebrityPrivateId, Integer year, Integer month){
        LambdaQueryWrapper<StoreCelebritySettlement> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreCelebritySettlement::getCelebrityPrivateId, celebrityPrivateId).eq(StoreCelebritySettlement::getYear, year).eq(StoreCelebritySettlement::getMonth, month);

        return storeCelebritySettlementMapper.selectOne(lambdaQueryWrapper);
    }


    @Override
    public List<StoreCelebritySettlementModel> statList(StoreCelebritySettlementModel storeCelebritySettlementModel) {
        return storeCelebritySettlementMapper.statList(storeCelebritySettlementModel);
    }
}
