package org.jeecg.modules.instagram.storemerchant.service;

import org.jeecg.modules.instagram.storemerchant.entity.StoreTask;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.instagram.storemerchant.model.StoreTaskModel;

import java.util.List;

/**
 * @Description: 爬取任务
 * @Author: nqr
 * @Date: 2020-04-21
 * @Version: V1.0
 */
public interface IStoreTaskService extends IService<StoreTask> {

    /**
     * 功能描述:批量重新执行任务
     *
     * @Author: nqr
     * @Date 2020-07-16 13:39:59
     */
    void agentStatusById(List<String> taskIdList);

    /**
     * 功能描述:查询未执行的爬虫任务
     *
     * @Author: nqr
     * @Date 2020-09-21 09:24:10
     */
    StoreTaskModel getTaskList();
}
