package org.jeecg.modules.instagram.storebasics.service;

import org.jeecg.modules.instagram.storebasics.entity.Country;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 国家表
 * @Author: jeecg-boot
 * @Date:   2021-01-18
 * @Version: V1.0
 */
public interface ICountryService extends IService<Country> {

    /**
     * 通过英文名称查询国家查询
     * */
    Country queryByCountryEnName(String countryEnName);
}
