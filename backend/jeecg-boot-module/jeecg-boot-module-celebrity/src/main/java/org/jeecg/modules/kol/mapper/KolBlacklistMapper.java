package org.jeecg.modules.kol.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.kol.entity.KolBlacklist;
import org.jeecg.modules.kol.model.KolBlacklistModel;

/**
 * @Description: 网红黑名单
 * @Author: dongruyang
 * @Date:   2025-05-10
 * @Version: V1.0
 */
public interface KolBlacklistMapper extends BaseMapper<KolBlacklist> {

    IPage<KolBlacklistModel> pageBlackList(Page<KolBlacklistModel> page, @Param("kolBlacklistModel") KolBlacklistModel kolBlacklistModel);
}
