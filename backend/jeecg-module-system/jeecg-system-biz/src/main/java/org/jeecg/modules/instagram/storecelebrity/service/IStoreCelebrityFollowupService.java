package org.jeecg.modules.instagram.storecelebrity.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityFollowup;
import org.jeecg.modules.instagram.storecelebrity.model.StoreCelebrityFollowupModel;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 物流管理
 * @Author: jeecg-boot
 * @Date: 2020-05-25
 * @Version: V1.0
 */
public interface IStoreCelebrityFollowupService extends IService<StoreCelebrityFollowup> {

    /***
     * 功能描述:获取邮件发送成功后的列表
     * @Author: nqr
     * @Date 2020-05-25 14:33:55
     */
    IPage<StoreCelebrityFollowupModel> getFollowingUpList(Page<StoreCelebrityFollowupModel> page, StoreCelebrityFollowupModel storeCelebrityFollowupModel);
}
