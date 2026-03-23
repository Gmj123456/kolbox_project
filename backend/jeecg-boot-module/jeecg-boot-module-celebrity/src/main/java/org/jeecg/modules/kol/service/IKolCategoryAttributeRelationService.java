package org.jeecg.modules.kol.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.kol.entity.KolCategory;
import org.jeecg.modules.kol.entity.KolCategoryAttributeRelation;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.kol.model.KolCategoryAttributeRelationModel;
import org.jeecg.modules.kol.model.TypeData;

import java.util.List;

/**
 * @Description: 类目关联表
 * @Author: dongruyang
 * @Date: 2025-06-21
 * @Version: V1.0
 */
public interface IKolCategoryAttributeRelationService extends IService<KolCategoryAttributeRelation> {
    /**
     * @description:分页查询类目关联表
     * @author: nqr
     * @date: 2025/6/21 8:58
     **/
    IPage<KolCategoryAttributeRelationModel> pageList(Page<KolCategoryAttributeRelationModel> page, KolCategoryAttributeRelationModel kolCategoryAttributeRelationModel);

    /**
     * @description: 批量保存类目关联表数据
     * @author: nqr
     * @date: 2025/6/21 10:40
     **/

    void saveData(String categoryId, List<KolCategoryAttributeRelation> saveList, List<String> childRemoveIds);

    /**
     * @description:按照类目id查询
     * @author: nqr
     * @date: 2025/6/21 18:19
     **/
    List<TypeData> queryByCategoryId(KolCategory category);

    List<KolCategoryAttributeRelationModel> getCategoryList(KolCategoryAttributeRelationModel kolCategoryAttributeRelationModel);

    List<TypeData> getProductCategory(KolCategory kolCategory);

    List<String> queryCategoryAttributes(String categoryId);

    List<KolCategoryAttributeRelationModel> getAllCategoryRelations(List<String> categoryIds);

    IPage<KolCategoryAttributeRelationModel> pageRelationList(Page<KolCategoryAttributeRelationModel> pageAttribute, KolCategoryAttributeRelationModel kolCategoryAttributeRelationModel);
}
