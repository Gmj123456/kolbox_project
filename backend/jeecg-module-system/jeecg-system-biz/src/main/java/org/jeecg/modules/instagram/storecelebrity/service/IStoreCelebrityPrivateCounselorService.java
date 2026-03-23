package org.jeecg.modules.instagram.storecelebrity.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityCounselorChangeDetail;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityCounselorChangeLog;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivate;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivateCounselor;
import org.jeecg.modules.instagram.storecelebrity.model.StoreCelebrityPrivateCounselorModel;
import org.jeecg.modules.instagram.storeseller.storeuser.entity.StoreUserContactEmail;
import org.jeecg.modules.system.entity.SysUser;

import java.util.List;

/**
 * @Description: 私有网红网红顾问签约表
 * @Author: nqr
 * @Date: 2023-09-05
 * @Version: V1.0
 */
public interface IStoreCelebrityPrivateCounselorService extends IService<StoreCelebrityPrivateCounselor> {

    /**
     * 功能描述:创建并保存网红顾问签约数据
     *
     * @return void
     * @Date 2023-09-05 10:31:11
     */
    void createPrivateCounselor(StoreCelebrityPrivate storeCelebrityPrivate, String privateCounselorId);

    StoreCelebrityPrivateCounselor getByCelebrityCounselorId(String celebrityCounselorId, String privateId);


    List<StoreCelebrityPrivateCounselorModel> queryByCelebrityPrivateId(String id);

    /**
    * 功能描述：更新建联邮箱对应网红顾问
    * @Param:
     * @param counselorModel
    * @Author: fengLiu
    * @Date: 2024-01-15 18:02
    */
    void updateCounselorByEmails(StoreCelebrityPrivateCounselorModel counselorModel);

    /**
     * 更新网红顾问和相关私有网红信息
     *
     * @param counselorSaveList 替换网红顾问的私有网红顾问关联列表
     * @param counselorDelList 删除网红顾问的私有网红顾问关联列表
     * <p>
     * 备注：
     * 该方法用于更新网红顾问和相关私有网红信息。传入替换的网红顾问用户对象、替换网红顾问的私有网红顾问关联列表以及被替换网红顾问的私有网红列表。
     */
    void updateCounselorUser(List<StoreCelebrityPrivate> celebrityPrivateList, List<StoreCelebrityPrivateCounselor> counselorSaveList, List<StoreCelebrityPrivateCounselor> counselorDelList, List<StoreUserContactEmail>contactEmailNewList, StoreCelebrityCounselorChangeLog counselorChangeLog, List<StoreCelebrityCounselorChangeDetail> counselorChangeDetails);

    List<StoreCelebrityPrivateCounselor> getByCelebrityIds(List<String> accounts);

}
