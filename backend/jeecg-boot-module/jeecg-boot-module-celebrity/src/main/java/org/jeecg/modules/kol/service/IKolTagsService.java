package org.jeecg.modules.kol.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.kol.entity.KolTagAttributeRelation;
import org.jeecg.modules.kol.entity.KolTagBrand;
import org.jeecg.modules.kol.entity.KolTagProduct;
import org.jeecg.modules.kol.entity.KolTags;
import org.jeecg.modules.kol.model.KolSearchModel;
import org.jeecg.modules.kol.model.KolTagCountModel;
import org.jeecg.modules.kol.model.KolTagUpdateDTO;
import org.jeecg.modules.kol.model.KolTagsModel;

import java.util.List;

/**
 * @Description: tag表
 * @Author: dongruyang
 * @Date: 2023-10-10
 * @Version: V1.0
 */
public interface IKolTagsService extends IService<KolTags> {

    IPage<KolTags> queryPageList(Page<KolTags> page, KolTagsModel tiktokTagsModel);

    /**
     * 功能描述:根据标签名查询
     *
     * @return java.util.List<org.jeecg.modules.kol.entity.TiktokTags>
     * @Date 2023-11-03 15:03:50
     */
    List<KolTags> getByTagNames(List<String> tagNames, Integer platformType);


    List<KolTags> queryTagsListByName(KolTagsModel tiktokTagsModel);

    List<KolTags> listByNames(List<String> tagNames);

    /**
     * 根据 categoryId 查询标签，包括子类目的标签
     *
     * @param categoryId 类目 ID
     * @return 标签列表
     */
    List<KolTags> searchTagsByCategoryId(String categoryId);


    /**
     * 获取指定类目及其子类目的所有 ID
     *
     * @param categoryId 类目 ID
     * @return 类目 ID 列表
     */
    List<String> getAllCategoryIds(String categoryId);

    /**
     * 方法描述: 保存标签及其品牌信息
     *
     * @author nqr
     * @date 2025/01/14 09:10
     **/
    void saveKolTags(KolTags kolTags, List<KolTagBrand> kolTagBrands, List<String> kolTagBrandIds);

    void saveKolTagsNew(KolTags kolTags, List<KolTagProduct> tagProductList, List<KolTagAttributeRelation> tagCategoryRelationList);

    /**
     * @description: 分页列表查询
     * @author: nqr
     * @date: 2025/6/28 9:40
     **/
    IPage<KolTagsModel> queryPageListNew(Page<KolTagsModel> page, KolTagsModel tiktokTagsModel);

    /**
     * @description: 查询标签数量
     * @author: nqr
     * @date: 2025/6/30 14:14
     **/

    Integer countBySearch(KolTagsModel kolTagsModel);

    /**
     * @description: 从飞书表格导入标签
     * @author: nqr
     * @date: 2025/7/16 15:12
     **/
    String synchronizeData(String tableId);

    /**
     * @param importType 0: 飞书导入 1: excel导入
     * @description: 导入标签
     * @author: nqr
     * @date: 2025/7/16 16:42
     **/
    Result<?> importTags(List<KolTagsModel> kolTagsList, Integer platformType, String productId, Integer importType);

    IPage<KolTagCountModel> getUnAllottedTagCount(Page<KolTagCountModel> page, KolSearchModel searchModel);

    /**
     * @description: 获取需要更新的标签
     * @author: nqr
     * @date: 2025/7/24 9:39
     **/
    List<KolTags> getUpdateKolTags(KolSearchModel searchModel);

    void processWithTempTable(String tempTableName, List<KolTagUpdateDTO> kolTagUpdateDTOS, Integer platformType);
}
