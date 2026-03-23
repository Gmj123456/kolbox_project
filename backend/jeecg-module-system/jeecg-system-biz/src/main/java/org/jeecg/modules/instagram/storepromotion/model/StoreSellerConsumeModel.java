package org.jeecg.modules.instagram.storepromotion.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.jeecg.modules.instagram.storepromotion.entity.StoreSellerConsume;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class StoreSellerConsumeModel extends StoreSellerConsume {
    /**
     * 消费日期开始时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String startConsumeDate;
    /**
     * 消费日期开始时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String endConsumeDate;
}
