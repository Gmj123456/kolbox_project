package org.jeecg.modules.instagram.storetags.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.instagram.storetags.entity.StoreCelebrityPrivatePersonalTags;

import java.util.List;

/**
 * @Description: 私有网红个性标签关联表
 * @Author: nqr
 * @Date: 2025-05-07
 * @Version: V1.0
 */
public interface IStoreCelebrityPrivatePersonalTagsService extends IService<StoreCelebrityPrivatePersonalTags> {
    /**
     * @description:保存私有网红个性化标签
     * @author: nqr
     * @date: 2025/5/8 10:25
     **/

    void savePrivatePersonalTags(String privateId, List<StoreCelebrityPrivatePersonalTags> privatePersonalTagsList);
    void savePrivatePersonalTagsBatch(List<String> privateIds, List<StoreCelebrityPrivatePersonalTags> privatePersonalTagsList);
}
