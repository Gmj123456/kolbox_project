package org.jeecg.modules.merchant.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.merchant.entity.MerchantInfo;
import org.jeecg.modules.merchant.mapper.MerchantInfoMapper;
import org.jeecg.modules.merchant.service.IMerchantInfoService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 商家信息表
 * @Author: dongruyang
 * @Date: 2025-03-12
 * @Version: V1.0
 */
@Service
public class MerchantInfoServiceImpl extends ServiceImpl<MerchantInfoMapper, MerchantInfo> implements IMerchantInfoService {

  /**
   * 方法描述：分页查询商家信息
   *
   * @author nqr
   * @date 2025/3/12 14:54
   **/
  @Override
  public IPage<MerchantInfo> pageList(Page<MerchantInfo> page, MerchantInfo merchantInfo) {
    return this.baseMapper.pageList(page, merchantInfo);
  }
}
