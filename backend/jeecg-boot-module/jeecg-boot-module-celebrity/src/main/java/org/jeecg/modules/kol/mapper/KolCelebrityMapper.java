package org.jeecg.modules.kol.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.kol.model.KolCelebrityModel;
import org.jeecg.modules.kol.model.KolSearchModel;

import java.util.List;
import java.util.Map;

/**
 * @Description: 网红标签分类类目表
 * @Author: xyc
 * @Date: 2024年11月22日 18:08:42
 * @Version: V1.0
 */
public interface KolCelebrityMapper extends BaseMapper<KolCelebrityModel> {


    List<String> getAllTagNamesByConditions(Map<String, Object> params);

    List<String> getProductTags(@Param("searchModel") KolSearchModel searchModel);

    void batchInsertIntoTempTable(@Param("tableName") String tableName, @Param("tagNames") List<String> tagNames);

    void dropTempTable(@Param("tableName") String tableName);

    void createTempUpdateTable(@Param("tableName") String tableName);

    void batchInsertIntoUniqueIdTempTable(@Param("tableName") String tableName, @Param("uniqueIds") List<String> uniqueIds);

    void createUniqueIdTempTable(@Param("tableName") String tableName);

    List<String> getTagNameByCategoryIds(@Param("categoryIdList") List<String> categoryIdList, @Param("platformType") Integer platformType);

    List<String> getTagNameByAttributeIds(@Param("attributeIds") String attributeIds, @Param("searchModel") KolSearchModel searchModel);

    List<String> getTagNameByConditions(@Param("searchModel") KolSearchModel searchModel);

    List<String> getUniqueIdByConditions(@Param("searchModel") KolSearchModel searchModel);

    List<String> getTagNameByUniqueIds(@Param("searchModel") KolSearchModel searchModel);
}
