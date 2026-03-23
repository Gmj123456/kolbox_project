package org.jeecg.modules.kol.mapper;


import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.kol.entity.KolTags;
import org.jeecg.modules.kol.model.KolSearchModel;
import org.jeecg.modules.kol.model.KolTagCountModel;
import org.jeecg.modules.kol.model.KolTagUpdateDTO;
import org.jeecg.modules.kol.model.KolTagsModel;

import java.util.List;
import java.util.Map;

/**
 * @Description: tag表
 * @Author: dongruyang
 * @Date:   2023-10-10
 * @Version: V1.0
 */
public interface KolTagsMapper extends BaseMapper<KolTags> {

    IPage<KolTags> queryPageList(Page<KolTags> page, @Param("tiktokTagsModel") KolTagsModel tiktokTagsModel);

    List<KolTags> queryTagsListByName(@Param("tiktokTagsModel") KolTagsModel tiktokTagsModel);

    IPage<KolTagsModel> queryPageListNew(Page<KolTagsModel> page, @Param("tiktokTagsModel") KolTagsModel tiktokTagsModel);

    Integer countBySearch(@Param("tiktokTagsModel") KolTagsModel kolTagsModel);
    @InterceptorIgnore(tenantLine = "true")
    IPage<KolTagCountModel> getUnAllottedTagCount(Page<KolTagCountModel> page, @Param("searchModel") KolSearchModel searchModel);

    void batchUpdateKolTags(@Param("list") List<KolTags> kolTagsList);

    void createTempUpdateTable(@Param("tableName") String tableName);

    void batchInsertIntoTempTable(@Param("tableName") String tableName, @Param("list") List<KolTagUpdateDTO> kolTagUpdateDTOS);

    int updateKolTagsFromTempTable(@Param("tableName") String tableName);

    void dropTempTable(@Param("tableName") String tableName);

    void deleteByTagIdAndProductIdBatch(@Param("params") List<Map<String, Object>> params);

}
