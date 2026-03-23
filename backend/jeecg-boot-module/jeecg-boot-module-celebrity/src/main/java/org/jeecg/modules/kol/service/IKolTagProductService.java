package org.jeecg.modules.kol.service;

import org.jeecg.modules.kol.entity.KolAttribute;
import org.jeecg.modules.kol.entity.KolTagProduct;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.kol.entity.KolTags;
import org.jeecg.modules.kol.model.KolSearchModel;

import java.util.List;

/**
 * @Description: 标签产品管理表
 * @Author: nqr
 * @Date: 2025-06-28
 * @Version: V1.0
 */
public interface IKolTagProductService extends IService<KolTagProduct> {
    /**
     * @description: 根据产品ID获取标签列表
     * @author: nqr
     * @date: 2025/7/23 16:35
     **/
    List<String> getByProductId(String productId, Integer platformType);

    void getProductTagNames(KolSearchModel searchModel);

    List<String> setBrandProductTags(List<String> productIds);

    /**
     * @description: 绑定产品标签
     * @author: nqr
     * @date: 2025/9/2 13:45
     **/
    void bindingProducts(List<KolTagProduct> newBindings, List<KolTags> tagsUpdateList,List<KolAttribute> attributeList);

    List<KolTagProduct> getTagProductDetails(List<String> tagIds);
}
