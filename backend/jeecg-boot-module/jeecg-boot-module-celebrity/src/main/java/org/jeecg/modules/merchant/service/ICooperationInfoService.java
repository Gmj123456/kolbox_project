package org.jeecg.modules.merchant.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.merchant.entity.CooperationInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 商家合作信息表
 * @Author: dongruyang
 * @Date: 2025-03-12
 * @Version: V1.0
 */
public interface ICooperationInfoService extends IService<CooperationInfo> {

  /**
   * 方法描述：分页条件查询
   *
   * @author nqr
   * @date 2025/3/12 14:47
   **/
  IPage<CooperationInfo> pageList(Page<CooperationInfo> page, CooperationInfo cooperationInfo);
}
