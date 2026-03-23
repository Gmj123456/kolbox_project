package org.jeecg.modules.merchant.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.merchant.entity.CooperationInfo;
import org.jeecg.modules.merchant.mapper.CooperationInfoMapper;
import org.jeecg.modules.merchant.service.ICooperationInfoService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 商家合作信息表
 * @Author: dongruyang
 * @Date:   2025-03-12
 * @Version: V1.0
 */
@Service
public class CooperationInfoServiceImpl extends ServiceImpl<CooperationInfoMapper, CooperationInfo> implements ICooperationInfoService {
  /**
   * 方法描述：分页列表查询
   * @author nqr
   * @date 2025/3/12 14:48
   **/
  @Override
  public IPage<CooperationInfo> pageList(Page<CooperationInfo> page, CooperationInfo cooperationInfo) {
    return this.baseMapper.pageList(page, cooperationInfo);
  }
}
