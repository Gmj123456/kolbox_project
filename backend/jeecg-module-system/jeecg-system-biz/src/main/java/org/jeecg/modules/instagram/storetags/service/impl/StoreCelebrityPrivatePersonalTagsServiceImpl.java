package org.jeecg.modules.instagram.storetags.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.instagram.storetags.entity.StoreCelebrityPrivatePersonalTags;
import org.jeecg.modules.instagram.storetags.mapper.StoreCelebrityPrivatePersonalTagsMapper;
import org.jeecg.modules.instagram.storetags.service.IStoreCelebrityPrivatePersonalTagsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: 私有网红个性标签关联表
 * @Author: nqr
 * @Date: 2025-05-07
 * @Version: V1.0
 */
@Service
public class StoreCelebrityPrivatePersonalTagsServiceImpl extends ServiceImpl<StoreCelebrityPrivatePersonalTagsMapper, StoreCelebrityPrivatePersonalTags> implements IStoreCelebrityPrivatePersonalTagsService {
    /***
     * @description: 保存私有网红个性化标签关联关系，先删除现有关系后再保存新的关联关系
     * @author: nqr
     * @date: 2025/5/8 10:38
     **/

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePrivatePersonalTags(String privateId, List<StoreCelebrityPrivatePersonalTags> privatePersonalTagsList) {
        lambdaUpdate().eq(StoreCelebrityPrivatePersonalTags::getCelebrityId, privateId).remove();
        saveBatch(privatePersonalTagsList);
    }

    @Override
    public void savePrivatePersonalTagsBatch(List<String> privateIds, List<StoreCelebrityPrivatePersonalTags> privatePersonalTagsList) {
        lambdaUpdate().in(StoreCelebrityPrivatePersonalTags::getCelebrityId, privateIds).remove();
        saveBatch(privatePersonalTagsList);
    }
}
