package org.jeecg.modules.instagram.storeseller.storeuser.service;

import org.jeecg.modules.instagram.storeseller.storeuser.entity.StoreUserCounselor;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 商家顾问管理
 * @Author: nqr
 * @Date: 2023-09-14
 * @Version: V1.0
 */
public interface IStoreUserCounselorService extends IService<StoreUserCounselor> {

    /**
     * 功能描述:查询自己的商家账号列表
     *
     * @return java.util.List<org.jeecg.modules.instagram.storeusercounselor.entity.StoreUserCounselor>
     * @Date 2023-10-14 17:18:12
     */
    List<StoreUserCounselor> getBySysUserId(String userId);
}
