package org.jeecg.modules.instagram.storecelebrity.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.instagram.storecelebrity.model.StoreSellDetailModel;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreSellDetail;
import org.jeecg.modules.instagram.storecelebrity.model.CelebrityPrivateGoodsDetailModel;


/**
 * @Description: 网红销售明细
 * @Author: jeecg-boot
 * @Date:   2020-11-12
 * @Version: V1.0
 */
public interface StoreSellDetailMapper extends BaseMapper<StoreSellDetail> {

    /**
     * 根据私人网红Id查询列表
     * */
    IPage<StoreSellDetailModel> queryByCelebrityPrivateId(IPage<StoreSellDetailModel> page, StoreSellDetailModel storeSellDetailModel);
    IPage<CelebrityPrivateGoodsDetailModel> celebrityDetailList(IPage<CelebrityPrivateGoodsDetailModel> page, @Param("celebrityPrivateGoodsDetailModel") CelebrityPrivateGoodsDetailModel celebrityPrivateGoodsDetailModel);

}