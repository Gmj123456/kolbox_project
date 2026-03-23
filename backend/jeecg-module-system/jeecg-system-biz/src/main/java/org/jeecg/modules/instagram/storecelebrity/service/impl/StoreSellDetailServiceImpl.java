package org.jeecg.modules.instagram.storecelebrity.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.enums.YesNoStatus;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.storecelebrity.model.StoreSellDetailModel;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebritySettlement;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebritySettlementService;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreSellDetail;
import org.jeecg.modules.instagram.storecelebrity.mapper.StoreSellDetailMapper;
import org.jeecg.modules.instagram.storecelebrity.model.CelebrityPrivateGoodsDetailModel;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreSellDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @Description: 网红销售明细
 * @Author: jeecg-boot
 * @Date: 2020-11-12
 * @Version: V1.0
 */
@Service
public class StoreSellDetailServiceImpl extends ServiceImpl<StoreSellDetailMapper, StoreSellDetail> implements IStoreSellDetailService {

    @Autowired
    private StoreSellDetailMapper storeSellDetailMapper;

    @Autowired
    private IStoreCelebritySettlementService storeCelebritySettlementService;

    /**
     * 处理数据:
     * 1、添加带货明细时如果已经生成了此月份的结算表则需要增加结算表的带货数量+1，如果没有生成网红此月份的结算表则同时生成结算表，并生成一条带货明细。
     * 2、如果删除带货明细时，需要删除相应结算数据的带货数量。
     * 3、如果添加带货明细所在月份已经结算，提示“您选择的月份此网红已经结算！”
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> handleSellDetailData(HttpServletRequest request, StoreSellDetailModel storeSellDetailModel) {
        Result result = new Result();
        String celebrityPrivateId = storeSellDetailModel.getCelebrityPrivateId();
        Integer year = storeSellDetailModel.getYear();
        Integer month = storeSellDetailModel.getMonth();
        StoreCelebritySettlement exitStoreCelebritySettlement = storeCelebritySettlementService.hasStoreCelebritySettlement(celebrityPrivateId, year, month);
        boolean updateFlag = true;
        boolean flagSave = true;
        String id;
        if (oConvertUtils.isNotEmpty(exitStoreCelebritySettlement)) {
            //  如果添加带货明细所在月份已经结算，提示“您选择的月份此网红已经结算！”
            if (exitStoreCelebritySettlement.getSettlementStatus() == YesNoStatus.YES.getCode()) {
                result.setSuccess(false);
                result.setMessage("您选择的月份此网红已经结算,请联系管理员");
                return result;
            }
            //  添加带货明细时如果已经生成了此月份的结算表则需要增加结算表的带货数量+1
            exitStoreCelebritySettlement.setGoodsNum(oConvertUtils.isNotEmpty(exitStoreCelebritySettlement.getGoodsNum()) ? exitStoreCelebritySettlement.getGoodsNum() + 1 : 1);
            exitStoreCelebritySettlement.setSettlementPrice(exitStoreCelebritySettlement.getSettlementPrice().add(exitStoreCelebritySettlement.getGoodsPrice()));
            exitStoreCelebritySettlement.setUpdateBy(JwtUtil.getUserNameByToken(request));
            exitStoreCelebritySettlement.setUpdateTime(new Date());

            updateFlag = storeCelebritySettlementService.updateById(exitStoreCelebritySettlement);
            id = exitStoreCelebritySettlement.getId();
        } else {
            //  如果没有生成网红此月份的结算表则同时生成结算表，并生成一条带货明细
            StoreCelebritySettlement storeCelebritySettlement = new StoreCelebritySettlement();
            storeCelebritySettlement.setCelebrityPrivateId(storeSellDetailModel.getCelebrityPrivateId());
            storeCelebritySettlement.setPrivateAccount(storeSellDetailModel.getPrivateAccount());
            storeCelebritySettlement.setYear(storeSellDetailModel.getYear());
            storeCelebritySettlement.setMonth(storeSellDetailModel.getMonth());
            storeCelebritySettlement.setGoodsNum(YesNoStatus.YES.getCode());
            storeCelebritySettlement.setGoodsPrice(storeSellDetailModel.getGoodsPrice());
            storeCelebritySettlement.setSettlementPrice(storeSellDetailModel.getGoodsPrice());
            storeCelebritySettlement.setSettlementStatus(YesNoStatus.NO.getCode());
            storeCelebritySettlement.setCreateBy(JwtUtil.getUserNameByToken(request));
            storeCelebritySettlement.setCreateTime(new Date());

            flagSave = storeCelebritySettlementService.save(storeCelebritySettlement);
            id = storeCelebritySettlement.getId();
        }
        if (updateFlag && flagSave && oConvertUtils.isNotEmpty(id)) {
            storeSellDetailModel.setCreateBy(JwtUtil.getUserNameByToken(request));
            storeSellDetailModel.setCreateTime(new Date());
            storeSellDetailModel.setSettlementId(id);
            storeSellDetailModel.setSettlementStatus(YesNoStatus.NO.getCode());

            insert(storeSellDetailModel);
            result.setSuccess(true);
            result.setMessage("添加成功");
            return result;
        }
        result.setSuccess(false);
        result.setMessage("添加失败");
        return result;
    }


    /**
     * 添加记录
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean insert(StoreSellDetail storeSellDetail) {
        Integer num = storeSellDetailMapper.insert(storeSellDetail);
        if (num > 0) {
            return true;
        }
        return false;
    }


    /**
     * 根据私人网红Id查询列表
     */
    @Override
    public IPage<StoreSellDetailModel> queryByCelebrityPrivateId(IPage<StoreSellDetailModel> page, StoreSellDetailModel storeSellDetailModel) {
        return storeSellDetailMapper.queryByCelebrityPrivateId(page, storeSellDetailModel);
    }


    /**
     * 网红带货明细分页列表查询
     * */
    @Override
    public IPage<CelebrityPrivateGoodsDetailModel> celebrityDetailList(IPage<CelebrityPrivateGoodsDetailModel> page, CelebrityPrivateGoodsDetailModel celebrityPrivateGoodsDetailModel) {
        return storeSellDetailMapper.celebrityDetailList(page,celebrityPrivateGoodsDetailModel);
    }


    /**
     * 编辑带货明细
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> handleEdit(HttpServletRequest request, StoreSellDetailModel storeSellDetailModel) {
        String celebrityPrivateId = storeSellDetailModel.getCelebrityPrivateId();
        Integer year = storeSellDetailModel.getYear();
        Integer month = storeSellDetailModel.getMonth();
        StoreCelebritySettlement exitStoreCelebritySettlement = storeCelebritySettlementService.hasStoreCelebritySettlement(celebrityPrivateId, year, month);
        if (oConvertUtils.isNotEmpty(exitStoreCelebritySettlement)) {
            if (exitStoreCelebritySettlement.getSettlementStatus() == YesNoStatus.YES.getCode()) {
                return Result.error("您选择的月份此网红已经结算无法编辑,请联系管理员");
            }
            storeSellDetailModel.setUpdateBy(JwtUtil.getUserNameByToken(request));
            storeSellDetailModel.setUpdateTime(new Date());
            Integer updateNum = storeSellDetailMapper.updateById(storeSellDetailModel);
            if(updateNum > 0) {
                return Result.ok("编辑成功");
            }
        }
        return Result.error("无对应结算数据,该明细记录无效,请联系管理员删除");
    }


    /**
     * 删除带货明细,相应结算表数量-1
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> handleDelete(HttpServletRequest request, StoreSellDetailModel storeSellDetailModel) {
        String id = storeSellDetailModel.getId();
        String celebrityPrivateId = storeSellDetailModel.getCelebrityPrivateId();
        Integer year = storeSellDetailModel.getYear();
        Integer month = storeSellDetailModel.getMonth();
        StoreCelebritySettlement exitStoreCelebritySettlement = storeCelebritySettlementService.hasStoreCelebritySettlement(celebrityPrivateId, year, month);
        if (oConvertUtils.isNotEmpty(exitStoreCelebritySettlement)) {
            if (exitStoreCelebritySettlement.getSettlementStatus() == YesNoStatus.YES.getCode()) {
                return Result.error("您选择的月份此网红已经结算无法删除,请联系管理员删除");
            }
            try {
                if (exitStoreCelebritySettlement.getGoodsNum() < YesNoStatus.YES.getCode() || exitStoreCelebritySettlement.getSettlementPrice().compareTo(exitStoreCelebritySettlement.getGoodsPrice()) == -1) {
                    return Result.error("数据错误");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return Result.error(e.getMessage());
            }
            exitStoreCelebritySettlement.setUpdateBy(JwtUtil.getUserNameByToken(request));
            exitStoreCelebritySettlement.setUpdateTime(new Date());
            exitStoreCelebritySettlement.setGoodsNum(exitStoreCelebritySettlement.getGoodsNum() - 1);
            exitStoreCelebritySettlement.setSettlementPrice(exitStoreCelebritySettlement.getSettlementPrice().subtract(exitStoreCelebritySettlement.getGoodsPrice()));

            boolean updateFlag = storeCelebritySettlementService.updateById(exitStoreCelebritySettlement);
            if (updateFlag) {
                Integer deleteNum = storeSellDetailMapper.deleteById(id);
                if (deleteNum > 0) {
                    return Result.ok("删除成功");
                }
            }
        }
        return Result.error("无对应结算数据,该明细记录无效,请联系管理员删除");
    }
}
