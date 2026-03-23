package org.jeecg.modules.instagram.storeseller.storeuser.service;

import org.jeecg.modules.instagram.storeseller.storeuser.entity.StoreUserCompany;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 商家公司信息表
 * @Author: jeecg-boot
 * @Date:   2021-02-06
 * @Version: V1.0
 */
public interface IStoreUserCompanyService extends IService<StoreUserCompany> {
    /**
     * 根据userId查询公司信息
     * */
    StoreUserCompany getByUserId(String userId);
}
