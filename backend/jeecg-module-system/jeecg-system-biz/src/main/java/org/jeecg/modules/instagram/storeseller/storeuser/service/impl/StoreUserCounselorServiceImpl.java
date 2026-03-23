package org.jeecg.modules.instagram.storeseller.storeuser.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.modules.instagram.storeseller.storeuser.entity.StoreUserCounselor;
import org.jeecg.modules.instagram.storeseller.storeuser.mapper.StoreUserCounselorMapper;
import org.jeecg.modules.instagram.storeseller.storeuser.service.IStoreUserCounselorService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 商家顾问管理
 * @Author: nqr
 * @Date: 2023-09-14
 * @Version: V1.0
 */
@Service
public class StoreUserCounselorServiceImpl extends ServiceImpl<StoreUserCounselorMapper, StoreUserCounselor> implements IStoreUserCounselorService {

    /**
     * 功能描述:查询自己的商家账号列表
     *
     * @return java.util.List<org.jeecg.modules.instagram.storeusercounselor.entity.StoreUserCounselor>
     * @Date 2023-10-14 17:18:31
     */
    @Override
    public List<StoreUserCounselor> getBySysUserId(String userId) {
        LambdaQueryWrapper<StoreUserCounselor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StoreUserCounselor::getSysUserId, userId);
        return this.list(queryWrapper);
    }
}
