package org.jeecg.modules.instagram.storebasics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.modules.instagram.storebasics.entity.Country;
import org.jeecg.modules.instagram.storebasics.mapper.CountryMapper;
import org.jeecg.modules.instagram.storebasics.service.ICountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 国家表
 * @Author: jeecg-boot
 * @Date:   2021-01-18
 * @Version: V1.0
 */
@Service
public class CountryServiceImpl extends ServiceImpl<CountryMapper, Country> implements ICountryService {

    @Autowired
    private CountryMapper countryMapper;

    /**
     * 通过英文名称查询国家查询
     * */
    @Override
    public Country queryByCountryEnName(String countryEnName) {
        LambdaQueryWrapper<Country> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        return countryMapper.selectOne(lambdaQueryWrapper.eq(Country::getCountryEnName, countryEnName));
    }
}
