package org.jeecg.modules.instagram.storebasics.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.instagram.storebasics.entity.StoreCustomerMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 客户信息表
 * @Author: jeecg-boot
 * @Date:   2020-12-19
 * @Version: V1.0
 */
public interface StoreCustomerMessageMapper extends BaseMapper<StoreCustomerMessage> {

    /**
     * 客户留言信息列表查询
     */
    IPage<StoreCustomerMessage> queryAll(Page<StoreCustomerMessage> page, StoreCustomerMessage storeCustomerMessage);
}
