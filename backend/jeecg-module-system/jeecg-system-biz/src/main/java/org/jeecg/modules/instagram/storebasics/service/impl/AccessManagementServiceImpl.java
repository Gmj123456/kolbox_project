package org.jeecg.modules.instagram.storebasics.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.instagram.storebasics.entity.AccessManagement;
import org.jeecg.modules.instagram.storebasics.mapper.AccessManagementMapper;
import org.jeecg.modules.instagram.storebasics.model.AccessManagementModel;
import org.jeecg.modules.instagram.storebasics.service.IAccessManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 访问管理
 * @Author: jeecg-boot
 * @Date: 2021-01-23
 * @Version: V1.0
 */
@Service
public class AccessManagementServiceImpl extends ServiceImpl<AccessManagementMapper, AccessManagement> implements IAccessManagementService {
    @Autowired
    private AccessManagementMapper accessManagementMapper;

    @Override
    public IPage<AccessManagement> pageAllList(Page<AccessManagement> page, AccessManagementModel accessManagementModel) {
        return  accessManagementMapper.pageAllList(page, accessManagementModel);
    }

    @Override
    public List<AccessManagement> getAloneList(AccessManagementModel accessManagementModel) {
        return accessManagementMapper.getAloneList( accessManagementModel);
    }
}
