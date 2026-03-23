package org.jeecg.modules.merchant.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.merchant.entity.CooperationInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 商家合作信息表
 * @Author: dongruyang
 * @Date: 2025-03-12
 * @Version: V1.0
 */
public interface CooperationInfoMapper extends BaseMapper<CooperationInfo> {

  /**
   * 方法描述：分页查询
   *
   * @author nqr
   * @date 2025/3/12 14:48
   **/
  IPage<CooperationInfo> pageList(Page<CooperationInfo> page, @Param("cooperationInfo") CooperationInfo cooperationInfo);
}
