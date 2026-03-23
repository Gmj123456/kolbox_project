package org.jeecg.modules.kol.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.kol.entity.KolBlacklist;
import org.jeecg.modules.kol.mapper.KolBlacklistMapper;
import org.jeecg.modules.kol.model.KolBlacklistModel;
import org.jeecg.modules.kol.service.IKolBlacklistService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 网红黑名单
 * @Author: dongruyang
 * @Date:   2025-05-10
 * @Version: V1.0
 */
@Service
public class KolBlacklistServiceImpl extends ServiceImpl<KolBlacklistMapper, KolBlacklist> implements IKolBlacklistService {
    @Override
    public IPage<KolBlacklistModel> pageBlackList(Page<KolBlacklistModel> page, KolBlacklistModel kolBlacklistModel) {
        return this.baseMapper.pageBlackList(page,kolBlacklistModel);
    }
}
