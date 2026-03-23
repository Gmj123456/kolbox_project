package org.jeecg.modules.instagram.storeseller.storeuser.service.impl;

import org.jeecg.modules.instagram.storeseller.storeuser.entity.StoreUserCompany;
import org.jeecg.modules.instagram.storeseller.storeuser.mapper.StoreUserCompanyMapper;
import org.jeecg.modules.instagram.storeseller.storeuser.service.IStoreUserCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 商家公司信息表
 * @Author: jeecg-boot
 * @Date:   2021-02-06
 * @Version: V1.0
 */
@Service
public class StoreUserCompanyServiceImpl extends ServiceImpl<StoreUserCompanyMapper, StoreUserCompany> implements IStoreUserCompanyService {

    @Autowired
    private StoreUserCompanyMapper storeUserCompanyMapper;


    /**
     * 根据userId查询公司信息
     * */
    @Override
    public StoreUserCompany getByUserId(String userId) {
        return storeUserCompanyMapper.getByUserId(userId);
    }
}
