package org.jeecg.modules.kol.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.kol.entity.KolShields;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.kol.model.KolShieldsModel;

/**
 * @Description: 网红屏蔽名单表
 * @Author: dongruyang
 * @Date:   2025-10-18
 * @Version: V1.0
 */
public interface KolShieldsMapper extends BaseMapper<KolShields> {

    IPage<KolShieldsModel> pageShields(Page<KolShieldsModel> page, @Param("kolShieldsModel")KolShieldsModel kolShieldsModel);
}
