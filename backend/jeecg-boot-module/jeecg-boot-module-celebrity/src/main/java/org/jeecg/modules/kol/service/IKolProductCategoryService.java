package org.jeecg.modules.kol.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.kol.entity.KolProduct;
import org.jeecg.modules.kol.entity.KolProductAttributeRelation;
import org.jeecg.modules.kol.entity.KolProductCategory;
import org.jeecg.modules.kol.model.KolProductModel;
import org.jeecg.modules.kol.model.TypeData;

import java.util.List;

/**
 * @Description: 产品与标签分类类目关联表
 * @Author: nqr
 * @Date: 2025-05-24
 * @Version: V1.0
 */
public interface IKolProductCategoryService extends IService<KolProductCategory> {
    /**
     * @description: 创建产品与标签分类类目关联表
     * @author: nqr
     * @date: 2025/5/24 9:47
     **/
    List<KolProductCategory> createProductCategory(KolProductModel productModel);

    List<KolProductAttributeRelation> createAttributeRelation(KolProduct productSave, List<TypeData> dataList);

}
