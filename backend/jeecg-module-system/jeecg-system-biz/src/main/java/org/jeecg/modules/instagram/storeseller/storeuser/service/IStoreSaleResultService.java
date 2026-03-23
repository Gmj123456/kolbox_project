package org.jeecg.modules.instagram.storeseller.storeuser.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.instagram.storeseller.storeuser.entity.StoreSaleResult;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.instagram.storeseller.storeuser.model.StoreSaleResultModel;

/**
 * @Description: storeSaleResult
 * @Author: jeecg-boot
 * @Date:   2020-10-16
 * @Version: V1.0
 */
public interface IStoreSaleResultService extends IService<StoreSaleResult> {

    IPage<StoreSaleResult> parentList(Page<StoreSaleResult> page,StoreSaleResultModel StoreSaleResult);

}
