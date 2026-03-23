package org.jeecg.modules.instagram.history.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.instagram.history.entity.KolHistoryCelebrity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 历史提报网红主表
 * @Author: jeecg-boot
 * @Date: 2025-11-26
 * @Version: V1.0
 */
public interface IKolHistoryCelebrityService extends IService<KolHistoryCelebrity> {

    /**
     * 根据网红名称和平台类型查询记录
     *
     * @param celebrityName 网红名称
     * @param platformType  平台类型
     * @return 网红主表记录
     */
    KolHistoryCelebrity getByCelebrityNameAndPlatformType(String celebrityName, Integer platformType);

    /**
     * @description: 分页查询
     * @author: nqr
     * @date: 2025/11/27 14:10
     **/
    IPage<KolHistoryCelebrity> pageList(Page<KolHistoryCelebrity> page, KolHistoryCelebrity kolHistoryCelebrity);
}