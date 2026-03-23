package org.jeecg.modules.instagram.storecelebrity.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivateCounselor;

import java.util.List;

/**
 * @Description: 私有网红网红顾问签约表
 * @Author: nqr
 * @Date: 2023-09-05
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreCelebrityPrivateCounselorModel extends StoreCelebrityPrivateCounselor {
    /**
     * 是否离职 是否离职 0：否；1：是
     */
    private Integer isLeave;

    private List<String> celebrityContactEmails;
}
