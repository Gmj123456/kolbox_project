package org.jeecg.modules.instagram.storetags.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.instagram.storetags.entity.StorePersonalTagCategory;
import org.jeecg.modules.instagram.storetags.model.StorePersonalTagsModel;

import java.util.List;

/**
 * @Description: 个性化标签类目关联表
 * @Author: jeecg-boot
 * @Date: 2025-04-27
 * @Version: V1.0
 */
public interface StorePersonalTagCategoryMapper extends BaseMapper<StorePersonalTagCategory> {
    /**
     * @description:根据标签分类查询个性化标签
     * @author: nqr
     * @date: 2025年4月27日18:03:29
     **/
    IPage<StorePersonalTagsModel> queryByCategory(Page<StorePersonalTagsModel> page, @Param("storePersonalTagsModel") StorePersonalTagsModel storePersonalTagsModel);

    List<StorePersonalTagCategory> getPersonalTagsCategory();
}
