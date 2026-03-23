package org.jeecg.modules.instagram.history.service.impl;

import org.jeecg.modules.instagram.history.entity.KolHistoryCelebrityKolType;
import org.jeecg.modules.instagram.history.mapper.KolHistoryCelebrityKolTypeMapper;
import org.jeecg.modules.instagram.history.service.IKolHistoryCelebrityKolTypeService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description: 历史提报网红达人类型表
 * @Author: jeecg-boot
 * @Date: 2025-11-26
 * @Version: V1.0
 */
@Slf4j
@Service
public class KolHistoryCelebrityKolTypeServiceImpl
        extends ServiceImpl<KolHistoryCelebrityKolTypeMapper, KolHistoryCelebrityKolType>
        implements IKolHistoryCelebrityKolTypeService {

    /**
     * 根据主表ID和达人类型查询记录
     * 
     * @param celebrityId 主表ID
     * @param kolType     达人类型
     * @return 网红达人类型记录
     */
    public KolHistoryCelebrityKolType getByCelebrityIdAndKolType(String celebrityId, String kolType) {
        // 参数校验
        if (StringUtils.isEmpty(celebrityId)) {
            log.warn("查询网红达人类型记录时主表ID为空");
            return null;
        }

        if (StringUtils.isEmpty(kolType)) {
            log.warn("查询网红达人类型记录时达人类型为空");
            return null;
        }

        try {
            LambdaQueryWrapper<KolHistoryCelebrityKolType> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(KolHistoryCelebrityKolType::getCelebrityId, celebrityId);
            queryWrapper.eq(KolHistoryCelebrityKolType::getKolType, kolType);

            return this.getOne(queryWrapper);
        } catch (Exception e) {
            log.error("根据主表ID和达人类型查询记录失败，主表ID: " + celebrityId + "，达人类型: " + kolType, e);
            return null;
        }
    }
}