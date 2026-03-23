package org.jeecg.modules.instagram.storeseller.storeuser.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.instagram.storeseller.storeuser.entity.StoreUserCompany;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 商家公司信息表
 * @Author: jeecg-boot
 * @Date:   2021-02-06
 * @Version: V1.0
 */
public interface StoreUserCompanyMapper extends BaseMapper<StoreUserCompany> {
    /**
     * 根据userId查询公司信息
     * */
    StoreUserCompany getByUserId(@Param("userId") String userId);
}
