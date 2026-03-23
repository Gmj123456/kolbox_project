package org.jeecg.modules.instagram.history.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.instagram.history.entity.KolHistoryKolType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 历史提报达人类型表
 * @Author: jeecg-boot
 * @Date:   2025-11-26
 * @Version: V1.0
 */
public interface KolHistoryKolTypeMapper extends BaseMapper<KolHistoryKolType> {

    List<KolHistoryKolType> listAll(@Param("kolHistoryKolType") KolHistoryKolType kolHistoryKolType);
}
