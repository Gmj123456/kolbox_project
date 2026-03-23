package org.jeecg.modules.kol.model;

import lombok.Data;

import java.util.List;

/**
 * @Author: nqr
 * @Date: 2025/5/24 10:26
 * @Description:
 **/
@Data
public class ProductCategory {
    private String categoryTypeId;
    private String categoryTypeName;
    private List<CategoryModel> categoryList;
}
