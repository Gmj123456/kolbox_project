package org.jeecg.modules.instagram.storecelebrity.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityFollowup;
import org.jeecg.modules.instagram.storecelebrity.model.StoreCelebrityFollowupModel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 物流管理
 * @Author: jeecg-boot
 * @Date: 2020-05-25
 * @Version: V1.0
 */
public interface StoreCelebrityFollowupMapper extends BaseMapper<StoreCelebrityFollowup> {

    /***
     * 功能描述:获取邮件发送成功后的列表
     * @Author: nqr
     * @Date 2020-05-25 14:33:55
     */
    IPage<StoreCelebrityFollowupModel> getFollowingUpList(Page<StoreCelebrityFollowupModel> page, @Param("followingUpModel") StoreCelebrityFollowupModel storeCelebrityFollowupModel);
}
