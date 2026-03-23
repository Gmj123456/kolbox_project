package org.jeecg.modules.instagram.storepromotion.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionProject;

/**
 * @Description: 产品网红历史匹配方案
 * @Author: nqr
 * @Date: 2023-09-02
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StorePromotionProjectModel extends StorePromotionProject {
    private String account;
    private Integer platformType;

}
