package org.jeecg.modules.instagram.storeseller.storeuser.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityPrivateService;
import org.jeecg.modules.instagram.storecelebrity.model.StoreCelebrityPrivateCounselorModel;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityPrivateCounselorService;
import org.jeecg.modules.instagram.storeseller.storeuser.entity.StoreUserContactEmail;
import org.jeecg.modules.instagram.storeseller.storeuser.mapper.StoreUserContactEmailMapper;
import org.jeecg.modules.instagram.storeseller.storeuser.service.IStoreUserContactEmailService;
import org.jeecg.modules.system.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 网红顾问建联邮箱表
 * @Author: jeecg-boot
 * @Date:   2024-01-15
 * @Version: V1.0
 */
@Service
public class StoreUserContactEmailServiceImpl extends ServiceImpl<StoreUserContactEmailMapper, StoreUserContactEmail> implements IStoreUserContactEmailService {
    @Autowired(required = false)
    private IStoreCelebrityPrivateCounselorService privateCounselorService;
    @Autowired
    private IStoreCelebrityPrivateService privateService;

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCounselorByEmail(List<StoreUserContactEmail> filteredEmails, SysUser counselorUser, SysUser counselorOldUser) {
        //更新网红顾问的建联邮箱未新网红顾问
        for (StoreUserContactEmail contactEmail : filteredEmails) {
            contactEmail.setSysUserId(counselorUser.getId());
            contactEmail.setSysUserName(counselorUser.getUsername());
            this.updateById(contactEmail);
        }
        StoreCelebrityPrivateCounselorModel privateCounselorModel=new StoreCelebrityPrivateCounselorModel();
        privateCounselorModel.setCelebrityCounselorId(counselorUser.getId());
        privateCounselorModel.setCelebrityCounselorName(counselorUser.getUsername());
        privateCounselorModel.setCelebrityContactEmails(filteredEmails.stream()
                .map(StoreUserContactEmail::getContactEmail)
                .collect(Collectors.toList()));
        //更新私有网红建联邮箱对应的网红顾问为新网红顾问
        privateCounselorService.updateCounselorByEmails(privateCounselorModel);
        //更新私有网红建联邮箱对应的网红顾问
        privateService.updateCounselorByEmails(privateCounselorModel);


    }
}
