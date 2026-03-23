package org.jeecg.modules.kol.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.kol.entity.KolBlacklist;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.kol.model.KolBlacklistModel;

/**
 * @Description: 网红黑名单
 * @Author: dongruyang
 * @Date:   2025-05-10
 * @Version: V1.0
 */
public interface IKolBlacklistService extends IService<KolBlacklist> {

    IPage<KolBlacklistModel> pageBlackList(Page<KolBlacklistModel> page, KolBlacklistModel kolBlacklistModel);
}
