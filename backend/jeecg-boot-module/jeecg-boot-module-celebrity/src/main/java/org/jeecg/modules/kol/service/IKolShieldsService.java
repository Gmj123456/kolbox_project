package org.jeecg.modules.kol.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.kol.entity.KolShields;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.kol.model.KolBaseModel;
import org.jeecg.modules.kol.model.KolBlacklistModel;
import org.jeecg.modules.kol.model.KolSearchModel;
import org.jeecg.modules.kol.model.KolShieldsModel;

/**
 * @Description: 网红屏蔽名单表
 * @Author: dongruyang
 * @Date:   2025-10-18
 * @Version: V1.0
 */
public interface IKolShieldsService extends IService<KolShields> {

    IPage<KolShieldsModel> pageShields(Page<KolShieldsModel> page, KolShieldsModel kolShieldsModel);

    void setShields(IPage<KolBaseModel> pageList, KolSearchModel searchModel);
}
