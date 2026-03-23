package org.jeecg.modules.instagram.storetags.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.modules.instagram.storetags.entity.StoreTags;
import org.jeecg.modules.instagram.storetags.mapper.StoreTagsMapper;
import org.jeecg.modules.instagram.storetags.service.IStoreTagsService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 标签表
 * @Author: jeecg-boot
 * @Date: 2021-02-07
 * @Version: V1.0
 */
@Service
public class StoreTagsServiceImpl extends ServiceImpl<StoreTagsMapper, StoreTags> implements IStoreTagsService {

    @Override
    public List<StoreTags> listAll() {
        return this.list();
    }
}
