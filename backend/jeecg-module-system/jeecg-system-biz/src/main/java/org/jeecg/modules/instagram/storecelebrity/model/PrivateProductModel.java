package org.jeecg.modules.instagram.storecelebrity.model;

import lombok.Data;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivateProduct;

/**
 * @author lf
 * @version 1.0.0
 * @ClassName PrivateProductModel.java
 * @Description TODO
 * @createTime 2025年09月08日 18:46:00
 */
@Data
public class PrivateProductModel extends StoreCelebrityPrivateProduct {
    private String platformTypeText;
    private String productName;


}
