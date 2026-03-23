package org.jeecg.modules.instagram.storecelebrity.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebritySettlement;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.instagram.storecelebrity.model.StoreCelebritySettlementModel;

import java.util.List;

/**
 * @Description: 私有网红带货结算表 
 * @Author: jeecg-boot
 * @Date:   2021-01-03
 * @Version: V1.0
 */
public interface StoreCelebritySettlementMapper extends BaseMapper<StoreCelebritySettlement> {

    List<StoreCelebritySettlementModel>  statList(@Param("storeCelebritySettlementModel")StoreCelebritySettlementModel storeCelebritySettlementModel);
}
