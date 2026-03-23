package org.jeecg.modules.instagram.storemerchant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.instagram.storemerchant.entity.StoreTask;
import org.jeecg.modules.instagram.storemerchant.model.StoreTaskModel;

import java.util.List;

/**
 * @Description: 爬取任务
 * @Author: nqr
 * @Date: 2020-04-21
 * @Version: V1.0
 */
public interface StoreTaskMapper extends BaseMapper<StoreTask> {

    /**
     * 功能描述:批量重新执行任务
     *
     * @Author: nqr
     * @Date 2020-07-16 13:41:50
     */
    void agentStatusById(List<String> list);

    StoreTaskModel getTaskList();
}
