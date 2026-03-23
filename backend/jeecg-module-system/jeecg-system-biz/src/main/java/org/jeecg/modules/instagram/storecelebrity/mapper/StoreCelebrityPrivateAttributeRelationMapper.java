package org.jeecg.modules.instagram.storecelebrity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivateAttributeRelation;
import org.jeecg.modules.kol.model.TypeData;

import java.util.List;
import java.util.Map;

/**
 * @Description: 私有网红社媒属性关联表
 * @Author: jeecg-boot
 * @Date:   2025-07-24
 * @Version: V1.0
 */
public interface StoreCelebrityPrivateAttributeRelationMapper extends BaseMapper<StoreCelebrityPrivateAttributeRelation> {

    List<Map<String, String>> selectAttributeStructureBatch(@Param("celebrityPrivateIds") List<String> celebrityPrivateIds);
    List<TypeData> getBatchCelebrityTypeData(@Param("celebrityPrivateIds") List<String> celebrityPrivateIds);
}
