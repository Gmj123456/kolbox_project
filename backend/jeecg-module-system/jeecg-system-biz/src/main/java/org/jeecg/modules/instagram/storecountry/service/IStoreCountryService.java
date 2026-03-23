package org.jeecg.modules.instagram.storecountry.service;

import org.jeecg.modules.instagram.storecountry.entity.StoreCountry;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.instagram.storebasics.entity.StoreCurrencyType;

/**
 * @Description: 国家表
 * @Author: jeecg-boot
 * @Date: 2020-10-13
 * @Version: V1.0
 */
public interface IStoreCountryService extends IService<StoreCountry> {

    /**
     * 根据国家简写标识, 查询国家类型
     *
     * @param shortCode
     * @return
     */
    StoreCurrencyType queryCurrencyTypeByShortCode(String shortCode);

    /**
     * 功能描述:查询paypal固定费用
     *
     * @param code
     * @return org.jeecg.modules.instagram.storecountry.entity.StoreCountry
     * @Date 2022-04-19 13:49:24
     */
    StoreCountry getCountryByCode(String code);

    /**
     * 功能描述:根据国家名称查询
     *
     * @return org.jeecg.modules.instagram.storecountry.entity.StoreCountry
     * @Date 2023-09-15 16:43:52
     */
    StoreCountry getCountry(String countryName);
}
