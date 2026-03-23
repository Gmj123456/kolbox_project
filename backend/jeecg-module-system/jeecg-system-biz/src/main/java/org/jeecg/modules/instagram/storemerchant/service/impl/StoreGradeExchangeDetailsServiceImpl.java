package org.jeecg.modules.instagram.storemerchant.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.storemerchant.entity.StoreGradeExchangeDetails;
import org.jeecg.modules.instagram.storemerchant.mapper.StoreGradeExchangeDetailsMapper;
import org.jeecg.modules.instagram.storemerchant.service.IStoreGradeExchangeDetailsService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * @Description: 商家兑换等级明细表
 * @Author: jeecg-boot
 * @Date: 2021-02-03
 * @Version: V1.0
 */
@Service
public class StoreGradeExchangeDetailsServiceImpl extends ServiceImpl<StoreGradeExchangeDetailsMapper, StoreGradeExchangeDetails> implements IStoreGradeExchangeDetailsService {
    @Autowired
    private ISysUserService sysUserService;

    @Override
    @Transactional
    public void updateGrade(StoreGradeExchangeDetails storeGradeExchangeDetails, SysUser sysUserNew) {
        if (oConvertUtils.isNotEmpty(storeGradeExchangeDetails)) {
            this.updateById(storeGradeExchangeDetails);
        }
        if (oConvertUtils.isNotEmpty(sysUserNew)) {
            sysUserService.updateById(sysUserNew);
        }
    }
}
