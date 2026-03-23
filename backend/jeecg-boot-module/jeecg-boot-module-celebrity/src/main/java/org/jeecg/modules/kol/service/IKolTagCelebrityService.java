package org.jeecg.modules.kol.service;

import org.jeecg.modules.kol.model.KolSearchModel;
import org.jeecg.modules.tiktok.model.TiktokTagsNumModel;

import java.util.List;

/**
 * @author nqr
 * @description 标签网红查询接口
 * @date 2025-01-04 2025/01/04:09:47
 */
public interface IKolTagCelebrityService {
    /**
     * 查询导出的数据
     *
     * @param searchModel
     * @return
     */
    List<TiktokTagsNumModel> exportTagsExcel(KolSearchModel searchModel);

    /**
     * 分配历史，更新网红顾问
     *
     */
    void updateCounselor(KolSearchModel kolSearchModel);
}
