package org.jeecg.modules.instagram.storeseller.storeuser.service;

import org.jeecg.modules.instagram.storeseller.storeuser.entity.StoreUserContactEmail;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.system.entity.SysUser;

import java.util.List;

/**
 * @Description: 网红顾问建联邮箱表
 * @Author: jeecg-boot
 * @Date:   2024-01-15
 * @Version: V1.0
 */
public interface IStoreUserContactEmailService extends IService<StoreUserContactEmail> {

    /**
    * 功能描述：更新邮箱变更网红顾问
     * 		   更新私有网红变更私有网红建联邮箱对应的网红顾问
     * 		   更新私有网红建联邮箱对应的网红顾问
    * @Param:
     * @param filteredEmails
     * @param counselorUser
     * @param counselorOldUser
    * @Author: fengLiu
    * @Date: 2024-01-16 10:08
    */
    void updateCounselorByEmail(List<StoreUserContactEmail> filteredEmails, SysUser counselorUser, SysUser counselorOldUser);

}
