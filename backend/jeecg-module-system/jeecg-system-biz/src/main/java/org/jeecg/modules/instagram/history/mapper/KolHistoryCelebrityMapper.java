package org.jeecg.modules.instagram.history.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.instagram.history.entity.KolHistoryCelebrity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 历史提报网红主表
 * @Author: jeecg-boot
 * @Date:   2025-11-26
 * @Version: V1.0
 */
public interface KolHistoryCelebrityMapper extends BaseMapper<KolHistoryCelebrity> {

    IPage<KolHistoryCelebrity> pageList(Page<KolHistoryCelebrity> page, @Param("kolHistoryCelebrity") KolHistoryCelebrity kolHistoryCelebrity);
}
