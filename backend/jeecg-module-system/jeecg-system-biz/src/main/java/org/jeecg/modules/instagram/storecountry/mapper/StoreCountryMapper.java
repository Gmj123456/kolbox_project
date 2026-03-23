package org.jeecg.modules.instagram.storecountry.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.instagram.storecountry.entity.StoreCountry;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 国家表
 * @Author: jeecg-boot
 * @Date:   2020-10-13
 * @Version: V1.0
 */
public interface StoreCountryMapper extends BaseMapper<StoreCountry> {

    StoreCountry queryByShortCode(@Param("shortCode") String shortCode);
}
