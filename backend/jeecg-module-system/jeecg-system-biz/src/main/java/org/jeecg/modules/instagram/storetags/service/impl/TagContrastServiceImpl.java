package org.jeecg.modules.instagram.storetags.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.modules.instagram.storetags.mapper.TagContrastMapper;
import org.jeecg.modules.instagram.storetags.entity.TagContrast;
import org.jeecg.modules.instagram.storetags.service.ITagContrastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 标签
 * @Author: jeecg-boot
 * @Date:   2021-01-18
 * @Version: V1.0
 */
@Service
public class TagContrastServiceImpl extends ServiceImpl<TagContrastMapper, TagContrast> implements ITagContrastService {

    @Autowired
    private TagContrastMapper tagContrastMapper;


    /**
     * 根据标签查询对应中文标签
     * */
    @Override
    public TagContrast queryByTag(String tag){
        LambdaQueryWrapper<TagContrast> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TagContrast::getTag, tag);
        return tagContrastMapper.selectOne(lambdaQueryWrapper);
    }
}
