package org.jeecg.modules.instagram.storemerchant.service;

import org.jeecg.modules.instagram.storemerchant.entity.StoreGradeExchangeDetails;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.system.entity.SysUser;

/**
 * @Description: 商家兑换等级明细表
 * @Author: jeecg-boot
 * @Date: 2021-02-03
 * @Version: V1.0
 */
public interface IStoreGradeExchangeDetailsService extends IService<StoreGradeExchangeDetails> {
    /**
     * 功能描述:修改等级兑换码状态，修改用户等级
     *
     * @Author: nqr
     * @Date 2021-02-03 10:44:00
     */
    void updateGrade(StoreGradeExchangeDetails storeGradeExchangeDetails, SysUser sysUserNew);
}
