package org.jeecg.modules.instagram.storecelebrity.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityTrial;
import org.jeecg.modules.instagram.storecelebrity.model.StoreCelebrityTrialModel;
import org.jeecg.modules.instagram.storecelebrity.mapper.StoreCelebrityTrialMapper;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityTrialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: store_celebrity 网红档案(试用)
 * @Author: jeecg-boot
 * @Date: 2020-11-06
 * @Version: V1.0
 */
@Service
public class StoreCelebrityTrialServiceImpl extends ServiceImpl<StoreCelebrityTrialMapper, StoreCelebrityTrial> implements IStoreCelebrityTrialService {

    @Autowired
    private StoreCelebrityTrialMapper storeCelebrityTrialMapper;

    /**
     * 列表查询
     */
    @Override
    public IPage<StoreCelebrityTrialModel> getStoreCelebrityTrialList(Page<StoreCelebrityTrial> page, StoreCelebrityTrialModel storeCelebrityTrialModel) {
        return storeCelebrityTrialMapper.getStoreCelebrityTrialList(page, storeCelebrityTrialModel);
    }

    /**
     * 获取总条数
     */
    @Override
    public Integer getCount(StoreCelebrityTrialModel storeCelebrityTrialModel) {
        return storeCelebrityTrialMapper.getCount(storeCelebrityTrialModel);
    }

    /**
     * 获取静态页数据条数
     */
    @Override
    public Integer getStaticListCount() {
        return storeCelebrityTrialMapper.getStaticListCount();
    }

    /**
     * 获取静态页数据
     */
    @Override
    public IPage<StoreCelebrityTrialModel> getStaticList(Page<StoreCelebrityTrial> page) {
        return storeCelebrityTrialMapper.getStaticList(page);
    }
}
