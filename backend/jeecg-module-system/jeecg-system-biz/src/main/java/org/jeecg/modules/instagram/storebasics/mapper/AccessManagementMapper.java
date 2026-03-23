package org.jeecg.modules.instagram.storebasics.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.instagram.storebasics.entity.AccessManagement;
import org.jeecg.modules.instagram.storebasics.model.AccessManagementModel;

import java.util.List;

/**
 * @Description: 访问管理
 * @Author: jeecg-boot
 * @Date: 2021-01-23
 * @Version: V1.0
 */
public interface AccessManagementMapper extends BaseMapper<AccessManagement> {

    /**
     * 功能描述:获取列表
     *
     * @Author: nqr
     * @Date 2021-01-23 13:40:17
     */
    List<AccessManagement> getAloneList(@Param("accessManagementModel") AccessManagementModel accessManagementModel);

    IPage<AccessManagement> pageAllList(Page<AccessManagement> page, @Param("accessManagementModel") AccessManagementModel accessManagementModel);

}
