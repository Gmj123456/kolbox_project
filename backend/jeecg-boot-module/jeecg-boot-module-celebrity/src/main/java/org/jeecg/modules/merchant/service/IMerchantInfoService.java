package org.jeecg.modules.merchant.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.merchant.entity.MerchantInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 商家信息表
 * @Author: dongruyang
 * @Date: 2025-03-12
 * @Version: V1.0
 */
public interface IMerchantInfoService extends IService<MerchantInfo> {

  /**
   * 方法描述：分页查询商家信息
   *
   * @author nqr
   * @date 2025/3/12 14:54
   **/
  IPage<MerchantInfo> pageList(Page<MerchantInfo> page, MerchantInfo merchantInfo);
}
