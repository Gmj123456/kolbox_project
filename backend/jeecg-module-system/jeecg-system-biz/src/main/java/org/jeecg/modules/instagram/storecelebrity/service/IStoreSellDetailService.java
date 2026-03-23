package org.jeecg.modules.instagram.storecelebrity.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.instagram.storecelebrity.model.StoreSellDetailModel;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreSellDetail;
import org.jeecg.modules.instagram.storecelebrity.model.CelebrityPrivateGoodsDetailModel;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @Description: 网红销售明细
 * @Author: jeecg-boot
 * @Date: 2020-11-12
 * @Version: V1.0
 */
public interface IStoreSellDetailService extends IService<StoreSellDetail> {

    /**
     * 处理数据:
     * 1、添加带货明细时如果已经生成了此月份的结算表则需要增加结算表的带货数量+1，如果没有生成网红此月份的结算表则同时生成结算表，并生成一条带货明细。
     * 2、如果删除带货明细时，需要删除相应结算数据的带货数量。
     * 3、如果添加带货明细所在月份已经结算，提示“您选择的月份此网红已经结算！”
     */
    Result<?> handleSellDetailData(HttpServletRequest request, StoreSellDetailModel storeSellDetailModel);

    /**
     * 编辑带货明细
     */
    Result<?> handleEdit(HttpServletRequest request, StoreSellDetailModel storeSellDetailModel);

    /**
     * 删除带货明细,相应结算表数量-1
     */
    Result<?> handleDelete(HttpServletRequest request, StoreSellDetailModel storeSellDetailModel);

    /**
     * 根据私人网红Id查询列表
     */
    IPage<StoreSellDetailModel> queryByCelebrityPrivateId(IPage<StoreSellDetailModel> page, StoreSellDetailModel storeSellDetailModel);

    /**
     * 网红带货明细分页列表查询
     * */
    IPage<CelebrityPrivateGoodsDetailModel> celebrityDetailList(IPage<CelebrityPrivateGoodsDetailModel> page, CelebrityPrivateGoodsDetailModel celebrityPrivateGoodsDetailModel);
}