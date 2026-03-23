package org.jeecg.modules.instagram.history.service.impl;

import org.jeecg.modules.instagram.history.entity.KolHistoryKolType;
import org.jeecg.modules.instagram.history.mapper.KolHistoryKolTypeMapper;
import org.jeecg.modules.instagram.history.service.IKolHistoryKolTypeService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * @Description: 历史提报达人类型表
 * @Author: jeecg-boot
 * @Date: 2025-11-26
 * @Version: V1.0
 */
@Slf4j
@Service
public class KolHistoryKolTypeServiceImpl extends ServiceImpl<KolHistoryKolTypeMapper, KolHistoryKolType>
        implements IKolHistoryKolTypeService {

    /**
     * 根据达人类型查询记录
     * 
     * @param kolType 达人类型
     * @return 达人类型记录
     */
    public KolHistoryKolType getByKolType(String kolType) {
        // 参数校验
        if (StringUtils.isEmpty(kolType)) {
            log.warn("查询达人类型记录时达人类型为空");
            return null;
        }

        try {
            LambdaQueryWrapper<KolHistoryKolType> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(KolHistoryKolType::getKolType, kolType);

            return this.getOne(queryWrapper);
        } catch (Exception e) {
            log.error("根据达人类型查询记录失败，达人类型: " + kolType, e);
            return null;
        }
    }

    @Override
    public List<KolHistoryKolType> listAll(KolHistoryKolType kolHistoryKolType) {
        return baseMapper.listAll(kolHistoryKolType);
    }
}