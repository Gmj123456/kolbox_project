package org.jeecg.modules.instagram.storecelebrity.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityFollowup;
import org.jeecg.modules.instagram.storecelebrity.model.StoreCelebrityFollowupModel;
import org.jeecg.modules.instagram.storecelebrity.mapper.StoreCelebrityFollowupMapper;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityFollowupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description: 物流管理
 * @Author: jeecg-boot
 * @Date: 2020-05-25
 * @Version: V1.0
 */
@Service
public class StoreCelebrityFollowupServiceImpl extends ServiceImpl<StoreCelebrityFollowupMapper, StoreCelebrityFollowup> implements IStoreCelebrityFollowupService {

    @Autowired
    private StoreCelebrityFollowupMapper followupMapper;

    /***
     * 功能描述:获取邮件发送成功后的列表
     * @Author: nqr
     * @Date 2020-05-25 14:33:55
     */
    @Override
    public IPage<StoreCelebrityFollowupModel> getFollowingUpList(Page<StoreCelebrityFollowupModel> page, StoreCelebrityFollowupModel storeCelebrityFollowupModel) {
        return followupMapper.getFollowingUpList(page, storeCelebrityFollowupModel);
    }
}
