package org.jeecg.modules.kol.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.kol.entity.KolCategoryAttributeRelation;
import org.jeecg.modules.kol.model.KolCategoryAttributeRelationModel;

import java.util.List;

/**
 * @Description: 类目关联表
 * @Author: nqr
 * @Date:   2025-06-21
 * @Version: V1.0
 */
public interface KolCategoryAttributeRelationMapper extends BaseMapper<KolCategoryAttributeRelation> {

    IPage<KolCategoryAttributeRelationModel> pageList(Page<KolCategoryAttributeRelationModel> page, @Param("model") KolCategoryAttributeRelationModel kolCategoryAttributeRelationModel);

    List<KolCategoryAttributeRelationModel> queryByCategoryId(@Param("categoryId") String categoryId);

    List<KolCategoryAttributeRelationModel> getCategoryList(@Param("model") KolCategoryAttributeRelationModel kolCategoryAttributeRelationModel);

    List<String> queryCategoryAttributes(@Param("categoryId") String categoryId);

    List<KolCategoryAttributeRelationModel> getAllCategoryRelations(@Param("categoryIds") List<String> categoryIds);

    IPage<KolCategoryAttributeRelationModel> pageRelationList(Page<KolCategoryAttributeRelationModel> pageAttribute, @Param("model") KolCategoryAttributeRelationModel kolCategoryAttributeRelationModel);
}
