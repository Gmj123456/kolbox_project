package org.jeecg.modules.instagram.history.service;

import org.jeecg.modules.instagram.history.entity.KolHistoryKolType;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 历史提报达人类型表
 * @Author: jeecg-boot
 * @Date: 2025-11-26
 * @Version: V1.0
 */
public interface IKolHistoryKolTypeService extends IService<KolHistoryKolType> {

    /**
     * 根据达人类型查询记录
     * 
     * @param kolType 达人类型
     * @return 达人类型记录
     */
    KolHistoryKolType getByKolType(String kolType);

    List<KolHistoryKolType> listAll(KolHistoryKolType kolHistoryKolType);
}