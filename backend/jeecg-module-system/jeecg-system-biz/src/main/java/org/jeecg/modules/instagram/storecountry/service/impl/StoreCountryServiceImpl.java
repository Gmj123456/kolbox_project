package org.jeecg.modules.instagram.storecountry.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.modules.instagram.storecountry.entity.StoreCountry;
import org.jeecg.modules.instagram.storecountry.mapper.StoreCountryMapper;
import org.jeecg.modules.instagram.storecountry.service.IStoreCountryService;
import org.jeecg.modules.instagram.storebasics.entity.StoreCurrencyType;
import org.jeecg.modules.instagram.storebasics.service.IStoreCurrencyTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 国家表
 * @Author: jeecg-boot
 * @Date: 2020-10-13
 * @Version: V1.0
 */
@Service
public class StoreCountryServiceImpl extends ServiceImpl<StoreCountryMapper, StoreCountry> implements IStoreCountryService {

    @Autowired
    private StoreCountryMapper storeCountryMapper;
    @Autowired
    private IStoreCurrencyTypeService storeCurrencyTypeService;

    /**
     * 根据国家简写标识, 查询国家类型
     *
     * @param shortCode
     * @return
     */
    @Override
    public StoreCurrencyType queryCurrencyTypeByShortCode(String shortCode) {
        StoreCountry storeCountry = storeCountryMapper.queryByShortCode(shortCode);
        return storeCurrencyTypeService.getById(storeCountry.getCurrencyTypeId());
    }

    /**
     * 功能描述:查询paypal固定费用
     *
     * @param code
     * @return org.jeecg.modules.instagram.storecountry.entity.StoreCountry
     * @Date 2022-04-19 13:49:38
     */
    @Override
    public StoreCountry getCountryByCode(String code) {
        LambdaQueryWrapper<StoreCountry> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreCountry::getShortCode, code);
        return this.list(lambdaQueryWrapper).stream().findFirst().orElse(null);
    }

    /**
     * 功能描述:根据国家名称查询
     *
     * @return org.jeecg.modules.instagram.storecountry.entity.StoreCountry
     * @Date 2023-09-15 16:44:04
     */
    @Override
    public StoreCountry getCountry(String countryName) {
        LambdaQueryWrapper<StoreCountry> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreCountry::getCountry, countryName);
        return this.list(lambdaQueryWrapper).stream().findFirst().orElse(null);
    }
}
