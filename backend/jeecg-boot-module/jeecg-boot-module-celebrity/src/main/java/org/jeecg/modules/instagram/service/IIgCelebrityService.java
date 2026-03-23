package org.jeecg.modules.instagram.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.instagram.entity.IgCelebrity;
import org.jeecg.modules.instagram.model.IgCelebrityModel;
import org.jeecg.modules.kol.model.KolBaseModel;
import org.jeecg.modules.kol.model.KolSearchModel;
import org.jeecg.modules.kol.service.IKolAllotService;

/**
 * @Description: IG网红接口层
 * @Author: xyc
 * @Date: 2024年12月4日 19:09:37
 * @Version: V1.0
 */
public interface IIgCelebrityService extends IService<IgCelebrity>, IKolAllotService<KolBaseModel> {


    /**
     * ig网红总数
     *
     * @param igSearchModel
     * @return
     */
    long getIgCelebrityCount(KolSearchModel igSearchModel);


    /**
     * ig网红列表
     * @param page
     * @param igSearchModel
     * @return
     */
    IPage<IgCelebrityModel> getIgCelebrityModelList(Page<IgCelebrityModel> page, KolSearchModel igSearchModel);



    /**
     * 查询已分配网红总数
     *
     * @param igSearchModel
     * @return
     */
    Integer getIgAllottedKolCount(KolSearchModel igSearchModel);


    /**
     * 查询已分配网红列表
     * @param page
     * @param igSearchModel
     * @return
     */
    IPage<IgCelebrityModel> getIgAllottedKolList(Page<IgCelebrityModel> page, KolSearchModel igSearchModel);

}
