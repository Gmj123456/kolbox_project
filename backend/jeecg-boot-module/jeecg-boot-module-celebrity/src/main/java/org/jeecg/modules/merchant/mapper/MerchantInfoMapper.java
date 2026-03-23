package org.jeecg.modules.merchant.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.merchant.entity.MerchantInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 商家信息表
 * @Author: dongruyang
 * @Date:   2025-03-12
 * @Version: V1.0
 */
public interface MerchantInfoMapper extends BaseMapper<MerchantInfo> {

  IPage<MerchantInfo> pageList(Page<MerchantInfo> page, @Param("merchantInfo") MerchantInfo merchantInfo);
}
