package org.jeecg.modules.instagram.storemerchant.service.impl;

import org.jeecg.modules.instagram.storemerchant.entity.StoreTask;
import org.jeecg.modules.instagram.storemerchant.mapper.StoreTaskMapper;
import org.jeecg.modules.instagram.storemerchant.model.StoreTaskModel;
import org.jeecg.modules.instagram.storemerchant.service.IStoreTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 爬取任务
 * @Author: nqr
 * @Date: 2020-04-21
 * @Version: V1.0
 */
@Service
public class StoreTaskServiceImpl extends ServiceImpl<StoreTaskMapper, StoreTask> implements IStoreTaskService {

    @Autowired
    private StoreTaskMapper storeTaskMapper;

    @Override
    public void agentStatusById(List<String> taskIdList) {
        storeTaskMapper.agentStatusById(taskIdList);
    }

    @Override
    public StoreTaskModel getTaskList() {
        return storeTaskMapper.getTaskList();
    }
}
