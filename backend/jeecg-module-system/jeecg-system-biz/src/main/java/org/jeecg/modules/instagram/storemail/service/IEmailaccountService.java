package org.jeecg.modules.instagram.storemail.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.instagram.storemail.entity.Emailaccount;

/**
 * @Description: 邮箱账号
 * @Author: jeecg-boot
 * @Date: 2020-05-11
 * @Version: V1.0
 */
public interface IEmailaccountService extends IService<Emailaccount> {

    /**
     * 功能描述:判断access_token 是否过期，过期则刷新
     *
     * @Author: nqr
     * @Date 2020-10-24 11:41:42
     */
    Emailaccount checkTime(String sellerId, String email);
}
