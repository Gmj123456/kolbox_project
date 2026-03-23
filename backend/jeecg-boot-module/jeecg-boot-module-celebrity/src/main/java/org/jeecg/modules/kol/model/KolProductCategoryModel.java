package org.jeecg.modules.kol.model;

import lombok.Data;
import org.jeecg.modules.kol.entity.KolProductCategory;

import java.util.List;

/**
 * @Description: 产品与标签分类类目关联表
 * @Author: nqr
 * @Date: 2025-05-24
 * @Version: V1.0
 */
@Data
public class KolProductCategoryModel extends KolProductCategory {
    private List<TypeData> dataList;
}
