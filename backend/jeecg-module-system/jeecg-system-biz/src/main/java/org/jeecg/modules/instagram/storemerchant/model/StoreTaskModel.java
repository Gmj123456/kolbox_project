package org.jeecg.modules.instagram.storemerchant.model;

import lombok.Data;
import org.jeecg.modules.instagram.storemerchant.entity.StoreTask;

/**
 * @Description: 爬取任务
 * @Author: nqr
 * @Date: 2020-04-21
 * @Version: V1.0
 */
@Data
public class StoreTaskModel extends StoreTask {
    private Integer flag;
    /**
     * 未执行的ins任务
     */
    private Integer insTaskCount;
    /**
     * 未执行的youtube任务
     */
    private Integer youtubeTaskCount;
}
