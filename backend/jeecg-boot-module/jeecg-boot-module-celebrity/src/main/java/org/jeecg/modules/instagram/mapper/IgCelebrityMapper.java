package org.jeecg.modules.instagram.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.instagram.entity.IgCelebrity;
import org.jeecg.modules.instagram.model.IgCelebrityModel;
import org.jeecg.modules.kol.model.KolBaseModel;
import org.jeecg.modules.kol.model.KolCelebrityModel;
import org.jeecg.modules.kol.model.KolSearchModel;

/**
 * @Description: IG网红表
 * @Author: xyc
 * @Date: 2024年12月4日 19:27:17
 * @Version: V1.0
 */
public interface IgCelebrityMapper extends BaseMapper<IgCelebrity> {

    /**
     * ig网红总数
     *
     * @param searchModel
     * @return
     */
    Long getIgCelebrityCount(@Param("searchModel") KolSearchModel searchModel);


    /**
     * 获取ig网红列表
     *
     * @param page
     * @param searchModel
     * @return
     */
    IPage<IgCelebrityModel> getIgCelebrityModelList(Page<IgCelebrityModel> page, @Param("searchModel") KolSearchModel searchModel);


    /**
     * 查询已分配网红总数
     *
     * @param searchModel
     * @return
     */
    Integer getIgAllottedKolCount(@Param("searchModel") KolSearchModel searchModel);


    /**
     * 查询已分配网红列表
     *
     * @param page
     * @param searchModel
     * @return
     */
    IPage<IgCelebrityModel> getIgAllottedKolList(Page<IgCelebrityModel> page, @Param("searchModel") KolSearchModel searchModel);

    /**
     * 查询已分配网红列表
     *
     * @param page
     * @param searchModel
     * @return 返回通用分页结果
     */
    IPage<KolBaseModel> getAllottedKolList(Page<KolBaseModel> page, @Param("searchModel") KolSearchModel searchModel);

    /**
     * @description: 获取IG网红总数
     * @author: nqr
     * @date: 2025/7/17 19:20
     **/
    Integer getCelebrityCount(@Param("searchModel") KolSearchModel searchModel);

    Integer getCelebrityScreeningCount(@Param("searchModel") KolSearchModel searchModel);

    IPage<KolCelebrityModel> getCelebrityScreeningList(Page<KolCelebrityModel> page, KolSearchModel searchModel);

    Integer getAllottedKolCount(@Param("searchModel") KolSearchModel searchModel);

}
