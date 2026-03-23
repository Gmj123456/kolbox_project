package org.jeecg.modules.instagram.storecelebrity.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityTrial;
import org.jeecg.modules.instagram.storecelebrity.model.StoreCelebrityTrialModel;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: store_celebrity 网红档案(试用)
 * @Author: jeecg-boot
 * @Date: 2020-11-06
 * @Version: V1.0
 */
public interface IStoreCelebrityTrialService extends IService<StoreCelebrityTrial> {

    /**
     * 列表查询
     */
    IPage<StoreCelebrityTrialModel> getStoreCelebrityTrialList(Page<StoreCelebrityTrial> page, StoreCelebrityTrialModel storeCelebrityTrialModel);

    /**
     * 获取总条数
     */
    Integer getCount(StoreCelebrityTrialModel storeCelebrityTrialModel);

    /**
     * 获取静态页数据
     */
    IPage<StoreCelebrityTrialModel> getStaticList(Page<StoreCelebrityTrial> page);

    /**
     * 获取静态页数据条数
     */
    Integer getStaticListCount();
}
