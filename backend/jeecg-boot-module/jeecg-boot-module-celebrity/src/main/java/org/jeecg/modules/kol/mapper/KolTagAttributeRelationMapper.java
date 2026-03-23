package org.jeecg.modules.kol.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.kol.entity.KolTagAttributeRelation;

import java.util.List;
import java.util.Map;

/**
 * @Description: 标签与类目关联表
 * @Author: nqr
 * @Date:   2025-05-21
 * @Version: V1.0
 */
public interface KolTagAttributeRelationMapper extends BaseMapper<KolTagAttributeRelation> {

    void insertBatch(@Param("tagAttributeRelations") List<KolTagAttributeRelation> tagAttributeRelations);

    Map<String, String> getTagAttributeTypes(@Param("tagIds") List<String> tagIds);

    List<KolTagAttributeRelation> getTagAttributeDetails(@Param("tagIds") List<String> tagIds);
}
