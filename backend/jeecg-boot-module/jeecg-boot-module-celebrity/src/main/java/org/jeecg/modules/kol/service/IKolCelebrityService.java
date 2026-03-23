package org.jeecg.modules.kol.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.modules.kol.model.KolSearchModel;

import java.util.List;

/**
 * @Author: nqr
 * @Date: 2025/7/18 11:19
 * @Description:
 **/
public interface IKolCelebrityService {
    void checkParams(KolSearchModel searchModel);

    public <T> void setAttributes(IPage<T> pageList,KolSearchModel searchModel);
    void createTempTable(KolSearchModel searchModel);

    void dropTempTable(String tempTableName);

    void createUniqueIdTempTable(String tempTableName, List<String> uniqueIdList);
}
