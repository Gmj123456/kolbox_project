package org.jeecg.modules.instagram.history.service;

import org.jeecg.modules.instagram.history.entity.KolHistoryCelebrityKolType;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 历史提报网红达人类型表
 * @Author: jeecg-boot
 * @Date: 2025-11-26
 * @Version: V1.0
 */
public interface IKolHistoryCelebrityKolTypeService extends IService<KolHistoryCelebrityKolType> {

    /**
     * 根据主表ID和达人类型查询记录
     * 
     * @param celebrityId 主表ID
     * @param kolType     达人类型
     * @return 网红达人类型记录
     */
    KolHistoryCelebrityKolType getByCelebrityIdAndKolType(String celebrityId, String kolType);
}