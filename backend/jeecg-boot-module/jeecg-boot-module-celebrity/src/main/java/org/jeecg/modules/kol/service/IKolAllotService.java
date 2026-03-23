package org.jeecg.modules.kol.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.kol.model.KolAllotExcelModel;
import org.jeecg.modules.kol.model.KolBaseModel;
import org.jeecg.modules.kol.model.KolCelebrityModel;
import org.jeecg.modules.kol.model.KolSearchModel;

import java.util.List;

/***
 * @Description: 网红分配服务接口
 * @param <T>
 */
public interface IKolAllotService<T> {

    /**
     * 查询已分配网红总数
     *
     * @param searchModel
     * @return
     */
    Integer getAllottedKolCount(KolSearchModel searchModel);

    /**
     * 查询已分配网红列表
     *
     * @param page
     * @param searchModel
     * @return
     */
    IPage<T> getAllottedKolList(Page<T> page, KolSearchModel searchModel);

    /**
     * 从网红标签表中获取标签信息
     * 根据网红账号分组聚合标签列表
     *
     * @param uniqueIdList 网红id列表
     * @param kolModelList 网红模型列表，用于获取用户名并更新标签信息
     */
    void updateKolTagList(List<String> uniqueIdList, List<T> kolModelList, KolSearchModel searchModel);

    List<KolAllotExcelModel> getAllotListByIds(List<String> selectionList);

    Integer getCelebrityCount(KolSearchModel searchModel);

    void updateKolTagListNew(List<String> uniqueIdList, List<KolCelebrityModel> kolModelList, KolSearchModel searchModel);

    Integer getCelebrityScreeningCount(KolSearchModel searchModel);

    IPage<KolCelebrityModel> getCelebrityScreeningList(Page<KolCelebrityModel> page, KolSearchModel searchModel);

    IPage<KolBaseModel> getAllottedKolListNew(Page<KolBaseModel> page, KolSearchModel searchModel);

    IPage<KolCelebrityModel> getCelebrityScreeningListNew(Page<KolCelebrityModel> page, KolSearchModel searchModel);

}
