package org.jeecg.modules.instagram.history.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.instagram.history.entity.KolHistoryCelebrity;
import org.jeecg.modules.instagram.history.mapper.KolHistoryCelebrityMapper;
import org.jeecg.modules.instagram.history.service.IKolHistoryCelebrityService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description: 历史提报网红主表
 * @Author: jeecg-boot
 * @Date: 2025-11-26
 * @Version: V1.0
 */
@Slf4j
@Service
public class KolHistoryCelebrityServiceImpl extends ServiceImpl<KolHistoryCelebrityMapper, KolHistoryCelebrity> implements IKolHistoryCelebrityService {
    @Override
    public IPage<KolHistoryCelebrity> pageList(Page<KolHistoryCelebrity> page, KolHistoryCelebrity kolHistoryCelebrity) {
        return baseMapper.pageList(page, kolHistoryCelebrity);
    }

    /**
     * 根据网红名称和平台类型查询记录
     *
     * @param celebrityName 网红名称
     * @param platformType  平台类型
     * @return 网红主表记录
     */
    public KolHistoryCelebrity getByCelebrityNameAndPlatformType(String celebrityName, Integer platformType) {
        // 参数校验
        if (StringUtils.isEmpty(celebrityName)) {
            log.warn("查询网红主表记录时网红名称为空");
            return null;
        }

        // 平台类型可以为null，表示不指定平台类型
        try {
            LambdaQueryWrapper<KolHistoryCelebrity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(KolHistoryCelebrity::getCelebrityName, celebrityName);

            // 如果平台类型不为空，则添加平台类型条件
            if (platformType != null) {
                queryWrapper.eq(KolHistoryCelebrity::getPlatformType, platformType);
            }

            return this.getOne(queryWrapper);
        } catch (Exception e) {
            log.error("根据网红名称和平台类型查询记录失败，网红名称: " + celebrityName + "，平台类型: " + platformType, e);
            return null;
        }
    }
}