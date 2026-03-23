package org.jeecg.modules.youtube.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.kol.model.KolBaseModel;
import org.jeecg.modules.kol.model.KolCelebrityModel;
import org.jeecg.modules.kol.model.KolSearchModel;
import org.jeecg.modules.youtube.entity.YoutubeCelebrity;
import org.jeecg.modules.youtube.model.YtCelebrityModel;

import java.util.Map;

/**
 * @Description: YT网红Mapper
 * @Author: xyc
 * @Date: 2024-12-26 10:55:22
 * @Version: V1.0
 */
@Mapper
public interface YtCelebrityMapper extends BaseMapper<YoutubeCelebrity> {
    /**
     * tk网红总数
     *
     * @param searchModel
     * @return
     */
    Integer getCelebrityCount(@Param("searchModel") KolSearchModel searchModel);


    /**
     * 获取tk网红列表
     *
     * @param page
     * @param searchModel
     * @return
     */
    IPage<YtCelebrityModel> getCelebrityModelList(Page<YtCelebrityModel> page, @Param("searchModel") KolSearchModel searchModel);


    /**
     * 查询已分配网红总数
     *
     * @param searchModel
     * @return
     */
    Integer getAllottedKolCount(@Param("searchModel") KolSearchModel searchModel);


    /**
     * 查询已分配网红列表
     *
     * @param page
     * @param searchModel
     * @return
     */
    IPage<YtCelebrityModel> getYtAllottedKolList(Page<YtCelebrityModel> page, @Param("searchModel") KolSearchModel searchModel);

    /**
     * 查询已分配网红列表
     *
     * @param page
     * @param searchModel
     * @return 返回通用分页结果
     */
    IPage<KolBaseModel> getAllottedKolList(Page<KolBaseModel> page, @Param("searchModel") KolSearchModel searchModel);

    /**
     * 获取未分配完标签网红数量
     *
     * @param page
     * @return
     */
    IPage<Map<String, Object>> getNotAllotTagsList(Page<YtCelebrityModel> page, @Param("searchModel") KolSearchModel kolSearchModel);

    int getNotAllotTagsListCount(@Param("searchModel") KolSearchModel kolSearchModel);

    IPage<KolCelebrityModel> getCelebrityScreeningList(Page<KolCelebrityModel> page, @Param("searchModel") KolSearchModel searchModel);

    Integer getCelebrityScreeningCount(@Param("searchModel") KolSearchModel searchModel);
}
