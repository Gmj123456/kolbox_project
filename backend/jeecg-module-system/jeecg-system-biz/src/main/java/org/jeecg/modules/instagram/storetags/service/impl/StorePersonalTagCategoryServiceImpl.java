package org.jeecg.modules.instagram.storetags.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivate;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityPrivateService;
import org.jeecg.modules.instagram.storetags.entity.StoreCelebrityPrivatePersonalTags;
import org.jeecg.modules.instagram.storetags.entity.StorePersonalTagCategory;
import org.jeecg.modules.instagram.storetags.entity.StorePersonalTags;
import org.jeecg.modules.instagram.storetags.mapper.StorePersonalTagCategoryMapper;
import org.jeecg.modules.instagram.storetags.model.StorePersonalTagsModel;
import org.jeecg.modules.instagram.storetags.service.IStoreCelebrityPrivatePersonalTagsService;
import org.jeecg.modules.instagram.storetags.service.IStorePersonalTagCategoryService;
import org.jeecg.modules.instagram.storetags.service.IStorePersonalTagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 个性化标签类目关联表
 * @Author: jeecg-boot
 * @Date: 2025-04-27
 * @Version: V1.0
 */
@Service
public class StorePersonalTagCategoryServiceImpl extends ServiceImpl<StorePersonalTagCategoryMapper, StorePersonalTagCategory> implements IStorePersonalTagCategoryService {
    @Autowired
    private IStorePersonalTagsService personalTagsService;
    @Autowired
    private IStoreCelebrityPrivatePersonalTagsService privatePersonalTagsService;
    @Autowired
    private IStoreCelebrityPrivateService celebrityPrivateService;

    /**
     * 根据标签分类查询个性化标签
     *
     * @return 个性化标签列表
     */
    @Override
    public IPage<StorePersonalTagsModel> queryByCategory(Page<StorePersonalTagsModel> page, StorePersonalTagsModel storePersonalTagsModel) {
        return this.baseMapper.queryByCategory(page, storePersonalTagsModel);

    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveData(List<StorePersonalTagCategory> personalTagCategoryToSave, StorePersonalTags storePersonalTagsToSave,
                         List<StoreCelebrityPrivatePersonalTags> celebrityTagsToUpdate, List<String> categoriesToDelete, StorePersonalTags personalTagsUpdate,
                         List<StoreCelebrityPrivate> celebrityPrivateListUpdate) {

        // 保存标签
        if (storePersonalTagsToSave != null) {
            personalTagsService.saveOrUpdate(storePersonalTagsToSave);
        }
        // 删除标签与类目对照关系
        if (categoriesToDelete != null && !categoriesToDelete.isEmpty()) {
            lambdaUpdate().in(StorePersonalTagCategory::getId, categoriesToDelete).remove();
        }
        // 保存标签与类目对照关系
        if (!personalTagCategoryToSave.isEmpty()) {
            saveBatch(personalTagCategoryToSave);
        }
        // 编辑标签与网红对照关系
        if (!celebrityTagsToUpdate.isEmpty()) {
            privatePersonalTagsService.saveOrUpdateBatch(celebrityTagsToUpdate);
        }
        // 修改标签
        if (personalTagsUpdate != null) {
            personalTagsService.updateById(personalTagsUpdate);
        }
        // 修改私有网红信息
        if (celebrityPrivateListUpdate != null && !celebrityPrivateListUpdate.isEmpty()) {
            celebrityPrivateService.updateBatchById(celebrityPrivateListUpdate);
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatchData(List<StorePersonalTagCategory> storePersonalTagCategories, List<StorePersonalTags> storePersonalTags) {
        personalTagsService.saveBatch(storePersonalTags);
        saveBatch(storePersonalTagCategories);
    }

    @Override
    public List<StorePersonalTagCategory> getPersonalTagsCategory() {
        return this.baseMapper.getPersonalTagsCategory();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTagCategory(List<StorePersonalTagCategory> newTagCategories, StorePersonalTags tagToUpdate, List<StoreCelebrityPrivatePersonalTags> celebrityTagsToSave,
                                  List<String> categoriesToDelete, boolean shouldDeleteOriginalTag, List<StoreCelebrityPrivate> celebrityPrivateUpdates) {
        if (shouldDeleteOriginalTag) {
            tagToUpdate.setDelFlag(1);
        }
        personalTagsService.updateById(tagToUpdate);
        // 删除标签与类目对照关系
        if (categoriesToDelete != null && !categoriesToDelete.isEmpty()) {
            // lambdaUpdate().set(StorePersonalTagCategory::getDelFlag, 1).in(StorePersonalTagCategory::getId, categoriesToDelete).update();
            lambdaUpdate().in(StorePersonalTagCategory::getId, categoriesToDelete).remove();
        }
        // 编辑标签与类目对照关系
        if (newTagCategories != null && !newTagCategories.isEmpty()) {
            // lambdaUpdate().set(StorePersonalTagCategory::getDelFlag, 1).eq(StorePersonalTagCategory::getTagId, tagToUpdate.getId()).update();
            lambdaUpdate().eq(StorePersonalTagCategory::getTagId, tagToUpdate.getId()).remove();
            List<String> tagIds = newTagCategories.stream().map(StorePersonalTagCategory::getTagId).distinct().collect(Collectors.toList());
            lambdaUpdate().in(StorePersonalTagCategory::getTagId, tagIds).remove();
            saveOrUpdateBatch(newTagCategories);
        }
        // 新增或编辑标签与网红对照关系
        if (celebrityTagsToSave != null && !celebrityTagsToSave.isEmpty()) {
            privatePersonalTagsService.saveOrUpdateBatch(celebrityTagsToSave);
        }
        // 更新私有网红信息
        if (celebrityPrivateUpdates != null && !celebrityPrivateUpdates.isEmpty()) {
            celebrityPrivateService.updateBatchById(celebrityPrivateUpdates);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recoverTag(String tagId, List<StoreCelebrityPrivate> celebrityPrivateToUpdateList) {
        // 恢复标签
        personalTagsService.lambdaUpdate().set(StorePersonalTags::getDelFlag, 0).eq(StorePersonalTags::getId, tagId).update();
        // 恢复类目关联关系
        lambdaUpdate().set(StorePersonalTagCategory::getDelFlag, 0).eq(StorePersonalTagCategory::getTagId, tagId).update();
        // 恢复标签网红关联关系
        privatePersonalTagsService.lambdaUpdate().set(StoreCelebrityPrivatePersonalTags::getIsDel, 0).eq(StoreCelebrityPrivatePersonalTags::getTagId, tagId).update();
        // 恢复私有网红信息
        if (!celebrityPrivateToUpdateList.isEmpty()) {
            celebrityPrivateService.updateBatchById(celebrityPrivateToUpdateList);
        }
    }
}
