package org.jeecg.modules.instagram.storetags.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivate;
import org.jeecg.modules.instagram.storetags.entity.StoreCelebrityPrivatePersonalTags;
import org.jeecg.modules.instagram.storetags.entity.StorePersonalTagCategory;
import org.jeecg.modules.instagram.storetags.entity.StorePersonalTags;
import org.jeecg.modules.instagram.storetags.model.StorePersonalTagsModel;

import java.util.List;

/**
 * @Description: 个性化标签类目关联表
 * @Author: jeecg-boot
 * @Date: 2025-04-27
 * @Version: V1.0
 */
public interface IStorePersonalTagCategoryService extends IService<StorePersonalTagCategory> {
    /**
     * @description:根据标签分类查询个性化标签
     * @author: nqr
     * @date: 2025/4/27 18:02
     **/
    IPage<StorePersonalTagsModel> queryByCategory(Page<StorePersonalTagsModel> page, StorePersonalTagsModel storePersonalTagsModel);

    /**
     * @description:保存标签类目关联表
     * @author: nqr
     * @date: 2025年4月28日09:02:42
     */
    void saveBatchData(List<StorePersonalTagCategory> storePersonalTagCategories, List<StorePersonalTags> storePersonalTags);

    List<StorePersonalTagCategory> getPersonalTagsCategory();

    /**
     * @description: 更新标签类目关联表
     * @author: nqr
     * @date: 2025/5/7 16:15
     **/

    void updateTagCategory(List<StorePersonalTagCategory> newTagCategories, StorePersonalTags tagToUpdate, List<StoreCelebrityPrivatePersonalTags> celebrityTagsToSave,
                           List<String> categoriesToDelete, boolean shouldDeleteOriginalTag,List<StoreCelebrityPrivate> celebrityPrivateUpdates);

    void saveData(List<StorePersonalTagCategory> personalTagCategoryToSave, StorePersonalTags storePersonalTagsToSave,
                  List<StoreCelebrityPrivatePersonalTags> celebrityTagsToUpdate, List<String> categoriesToDelete,StorePersonalTags personalTagsUpdate,
                  List<StoreCelebrityPrivate> celebrityPrivateListUpdate);

    void recoverTag(String tagId,List<StoreCelebrityPrivate> celebrityPrivateToUpdateList);
}
