package org.jeecg.modules.instagram.storecelebrity.model;

import lombok.Data;
import org.jeecg.modules.instagram.storepromotion.entity.StoreSellerConsume;

@Data
public class FinancialStatisticsModel extends StoreSellerConsume {



    /**
     * 消费开始时间
     * */
    private String startConsumeDate;
    /**
     * 消费结束时间
     * */
    private String endConsumeDate;
}
