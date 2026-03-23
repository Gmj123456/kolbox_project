package org.jeecg.modules.tiktok.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.kol.model.KolBaseModel;
import org.jeecg.modules.kol.model.KolSearchModel;
import org.jeecg.modules.kol.service.IKolAllotService;
import org.jeecg.modules.tiktok.entity.TiktokCelebrity;
import org.jeecg.modules.tiktok.model.TkCelebrityModel;


/**
 * @Description: tiktok网红表
 * @Author: xyc
 * @Date:   2024-12-23 17:21:40
 * @Version: V1.2
 * @History:  V1.1 - [2024-12-23] - [根据实际业务，新增方法替换原有方法，测试通过后 将删除历史不规范的命名方法] - [xyc]
 *            V1.2 - [2024-12-31] - [增加继承接口IKolAllotService] - [xyc]
 */
public interface ITiktokCelebrityService extends IService<TiktokCelebrity> , IKolAllotService<KolBaseModel> {


    TiktokCelebrity getUniqueId(String uniqueId);

//    region 以下为新增接口方法 2024-12-23 17:21:00

    /**
     * tk网红总数
     *
     * @param searchModel
     * @return
     */
    Long getTkCelebrityCount(KolSearchModel searchModel);


    /**
     * 获取tk网红列表
     * @param page
     * @param searchModel
     * @return
     */
    IPage<TkCelebrityModel> getTkCelebrityModelList(Page<TkCelebrityModel> page, KolSearchModel searchModel);


    /**
     * 查询已分配网红总数
     *
     * @param searchModel
     * @return
     */
    Integer getTkAllottedKolCount(KolSearchModel searchModel);


    /**
     * 查询已分配网红列表
     * @param page
     * @param searchModel
     * @return
     */
    IPage<TkCelebrityModel> getTkAllottedKolList(Page<TkCelebrityModel> page,  KolSearchModel searchModel);

    /**
     * 查询已分配网红列表
     * @param page
     * @param searchModel
     * @return 返回通用分页结果
     */
    IPage<KolBaseModel> getAllottedKolList(Page<KolBaseModel> page, KolSearchModel searchModel);


//    endregion


}
