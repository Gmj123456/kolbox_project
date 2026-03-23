package org.jeecg.modules.instagram.storepromotion.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.instagram.storepromotion.entity.StoreSellerEmailLimit;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.instagram.storepromotion.model.SellerEmaillimitModel;

/**
 * @Description: 邮件发送额度明细表
 * @Author: jeecg-boot
 * @Date:   2020-10-02
 * @Version: V1.0
 */
public interface IStoreSellerEmailLimitService extends IService<StoreSellerEmailLimit> {
    IPage<SellerEmaillimitModel> getLimitList(Page<StoreSellerEmailLimit> page, StoreSellerEmailLimit storeSellerEmailLimit);
}
