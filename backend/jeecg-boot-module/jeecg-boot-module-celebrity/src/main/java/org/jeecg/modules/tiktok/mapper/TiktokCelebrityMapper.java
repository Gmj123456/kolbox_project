package org.jeecg.modules.tiktok.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.kol.model.KolBaseModel;
import org.jeecg.modules.kol.model.KolCelebrityModel;
import org.jeecg.modules.kol.model.KolSearchModel;
import org.jeecg.modules.tiktok.entity.TiktokCelebrity;
import org.jeecg.modules.tiktok.model.TkCelebrityModel;

import java.util.List;

/**
 * @Description: tiktok网红表
 * @Author: xyc
 * @Date:   2024-12-23 11:12:24
 * @Version: V1.1
 * @History:  V1.1 - [2024-12-23] - [根据实际业务，新增方法替换原有方法，测试通过后 将删除历史不规范的命名方法] - [xyc]
 */

@Mapper
public interface TiktokCelebrityMapper extends BaseMapper<TiktokCelebrity> {

    /**
     * tk网红总数
     *
     * @param searchModel
     * @return
     */
    Long getTkCelebrityCount(@Param("searchModel") KolSearchModel searchModel);


    /**
     * 获取tk网红列表
     * @param page
     * @param searchModel
     * @return
     */
    IPage<TkCelebrityModel> getTkCelebrityModelList(Page<TkCelebrityModel> page, @Param("searchModel") KolSearchModel searchModel);


    /**
     * 查询已分配网红总数
     *
     * @param searchModel
     * @return
     */
    Integer getTkAllottedKolCount(@Param("searchModel") KolSearchModel searchModel);


    /**
     * 查询已分配网红列表
     * @param page
     * @param searchModel
     * @return
     */
    IPage<TkCelebrityModel> getTkAllottedKolList(Page<TkCelebrityModel> page, @Param("searchModel") KolSearchModel searchModel);

    /**
     * 查询已分配网红列表
     * @param page
     * @param searchModel
     * @return 返回通用分页结果
     */
    IPage<KolBaseModel> getAllottedKolList(Page<KolBaseModel> page, @Param("searchModel") KolSearchModel searchModel);

    IPage<KolCelebrityModel> getCelebrityScreeningList(Page<KolCelebrityModel> page, @Param("searchModel") KolSearchModel searchModel);

    Integer getCelebrityScreeningCount(@Param("searchModel") KolSearchModel searchModel);

    List<String> getAllottedKolUniqueIdList(@Param("searchModel") KolSearchModel searchModel);

}
