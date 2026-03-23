package org.jeecg.modules.instagram.storebasics.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.instagram.storebasics.entity.AccessManagement;
import org.jeecg.modules.instagram.storebasics.model.AccessManagementModel;

import java.util.List;

/**
 * @Description: 访问管理
 * @Author: jeecg-boot
 * @Date: 2021-01-23
 * @Version: V1.0
 */
public interface IAccessManagementService extends IService<AccessManagement> {

    /**
     * 功能描述:获取列表
     *
     * @Author: nqr
     * @Date 2021-01-23 13:39:33
     */
    List<AccessManagement> getAloneList(AccessManagementModel accessManagementModel);

    IPage<AccessManagement> pageAllList(Page<AccessManagement> page, AccessManagementModel accessManagementModel);
}
