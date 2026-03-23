package org.jeecg.modules.kol.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.kol.entity.KolTagAttributeRelation;
import org.jeecg.modules.kol.entity.KolTags;
import org.jeecg.modules.kol.model.KolSearchModel;
import org.jeecg.modules.kol.model.KolTagsModel;
import org.jeecg.modules.kol.model.TypeData;

import java.util.List;
import java.util.Map;

/**
 * @Description: 标签与类目关联表
 * @Author: nqr
 * @Date: 2025-05-21
 * @Version: V1.0
 */
public interface IKolTagAttributeRelationService extends IService<KolTagAttributeRelation> {

    /**
     * @description: 保存标签与类目关联表数据
     * @author: nqr
     * @date: 2025/5/21 16:19
     **/

    void saveData(KolTags kolTagsNew, List<KolTagAttributeRelation> categoryRelationListSave, boolean categoryRelationExists);

    /**
     * @description: 更新标签与类目关联表数据
     * @author: nqr
     * @date: 2025/5/21 16:19
     **/
    void updateData(KolTags kolTagsNew, List<KolTagAttributeRelation> categoryRelationListSave, String tagId, boolean tagNameExists);

    /**
     * @description: 恢复标签
     * @author: nqr
     * @date: 2025/5/22 15:14
     **/

    void recoverTag(String id);

    /**
     * @description: 创建标签与类目关联表数据
     * @author: nqr
     * @date: 2025/6/28 10:45
     **/
    List<KolTagAttributeRelation> createCategoryRelation(KolTagsModel kolTagsModel, List<TypeData> dataList);

    /**
     * @description:查询标签对应的类目
     * @author: nqr
     * @date: 2025/6/30 14:00
     **/
    List<TypeData> getSelectedCategories(String tagId);

    /**
     * @description: 按照社媒属性查询标签
     * @author: nqr
     * @date: 2025/9/10 9:00
     **/
    void getAttributeTagNames(KolSearchModel searchModel);

    /**
     * @description: 查询标签对应的属性类型
     * @author: nqr
     * @date: 2025/9/19 18:38
     **/
    Map<String, String> getTagAttributeTypes(List<String> tagIds);
    /**
     * @description: 查询标签对应的属性详情
     * @author: nqr
     * @date: 2025/9/19 18:38
     **/
    List<KolTagAttributeRelation> getTagAttributeDetails(List<String> tagIds);

    /**
     * 功能描述：查询标签产品对应的属性
     * @Param:
     * @param kolTagAttributeRelation
     * @Author: fengLiu
     * @Date: 2025/12/25 15:20
     */
    List<KolTagAttributeRelation> queryAttributeByTag(KolTagAttributeRelation kolTagAttributeRelation);
}
