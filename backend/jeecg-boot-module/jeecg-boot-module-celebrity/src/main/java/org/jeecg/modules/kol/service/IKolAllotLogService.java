package org.jeecg.modules.kol.service;

import org.jeecg.modules.kol.entity.KolAllotLog;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.kol.model.KolSearchModel;
import org.jeecg.modules.kol.model.KolTagsResultModel;

import java.util.List;

/**
 * @Description: 分配日志表
 * @Author: dongruyang
 * @Date:   2025-09-17
 * @Version: V1.0
 */
public interface IKolAllotLogService extends IService<KolAllotLog> {


    void createKolAllotLog(KolSearchModel searchModel, String allotLogId);

    void updateKolAllotLog(String allotLogId, int allotStatus, String message);

    String createResultJson(List<KolTagsResultModel> resultList);
}
