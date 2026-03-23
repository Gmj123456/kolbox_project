package org.jeecg.modules.kol.model;

import lombok.Data;

/**
 * @Author: nqr
 * @Date: 2025/5/24 10:27
 * @Description:
 **/
@Data
public class CategoryModel {
    private String categoryId;
    private String categoryName;

    public CategoryModel(String categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }
}