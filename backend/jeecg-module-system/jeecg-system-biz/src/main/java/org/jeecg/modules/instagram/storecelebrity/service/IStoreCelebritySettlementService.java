package org.jeecg.modules.instagram.storecelebrity.service;

import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebritySettlement;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.instagram.storecelebrity.model.StoreCelebritySettlementModel;

import java.util.List;

/**
 * @Description: 私有网红带货结算表 
 * @Author: jeecg-boot
 * @Date:   2021-01-03
 * @Version: V1.0
 */
public interface IStoreCelebritySettlementService extends IService<StoreCelebritySettlement> {
    List<StoreCelebritySettlementModel> statList(StoreCelebritySettlementModel storeCelebritySettlementModel);
    /**
     * 查询同年同月是否有该私有网红带货结算记录
     * @Param celebrityPrivateId
     * @Param year
     * @Param month
     * */
    StoreCelebritySettlement hasStoreCelebritySettlement(String celebrityPrivateId, Integer year, Integer month);
}
