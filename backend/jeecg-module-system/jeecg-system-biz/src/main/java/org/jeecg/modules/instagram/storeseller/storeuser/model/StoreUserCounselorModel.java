package org.jeecg.modules.instagram.storeseller.storeuser.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.modules.instagram.storeseller.storeuser.entity.StoreUserCounselor;

/**
 * @Description: 商家顾问管理
 * @Author: nqr
 * @Date: 2023-09-14
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreUserCounselorModel extends StoreUserCounselor {
    private String ids;
    private String updateBy;
}
