package org.jeecg.modules.instagram.storecelebrity.model;

import lombok.Data;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivateProduct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lf
 * @version 1.0.0
 * @ClassName ImportPrivateProductResult.java
 * @Description TODO
 * @createTime 2025年08月07日 11:25:00
 */
@Data
public class ImportPrivateProductResult {
    private Map<String, String> errorMap = new HashMap<>();
    private List<StoreCelebrityPrivateProduct> insertPrivateProducts = new ArrayList<>();
    private List<StoreCelebrityPrivateProduct> updatePrivateProducts = new ArrayList<>();
}
