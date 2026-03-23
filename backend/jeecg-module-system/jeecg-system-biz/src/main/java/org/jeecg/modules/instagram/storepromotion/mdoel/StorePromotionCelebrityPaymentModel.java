package org.jeecg.modules.instagram.storepromotion.mdoel;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionCelebrityPayment;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 网红支付列表
 * @Author: jeecg-boot
 * @Date: 2021-09-27
 * @Version: V1.0
 */
@Data
public class StorePromotionCelebrityPaymentModel extends StorePromotionCelebrityPayment {

    /**
     * 产品网红id
     */
    private String goodsCelId;
    /**
     * 货币类型 0 美元 ，1 人民币
     */
    private Integer payCurrency;
    /**
     * 网红上线时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date onlineTime;
}
