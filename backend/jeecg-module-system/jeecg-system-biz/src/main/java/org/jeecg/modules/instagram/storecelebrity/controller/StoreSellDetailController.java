package org.jeecg.modules.instagram.storecelebrity.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.enums.YesNoStatus;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.storecelebrity.model.StoreSellDetailModel;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreSellDetail;
import org.jeecg.modules.instagram.storecelebrity.model.CelebrityPrivateGoodsDetailModel;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreSellDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

/**
 * @Description: 网红销售明细
 * @Author: jeecg-boot
 * @Date: 2020-11-12
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "网红销售明细")
@RestController
@RequestMapping("/storeSellDetail/storeSellDetail")
public class StoreSellDetailController extends JeecgController<StoreSellDetail, IStoreSellDetailService> {
    @Autowired
    private IStoreSellDetailService storeSellDetailService;

    /**
     * 分页列表查询
     *
     * @param storeSellDetail
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "网红销售明细-分页列表查询")
@Operation(summary = "网红销售明细-分页列表查询", description = "网红销售明细-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StoreSellDetail storeSellDetail,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<StoreSellDetail> queryWrapper = QueryGenerator.initQueryWrapper(storeSellDetail, req.getParameterMap());
        Page<StoreSellDetail> page = new Page<StoreSellDetail>(pageNo, pageSize);
        IPage<StoreSellDetail> pageList = storeSellDetailService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 网红带货明细分页列表查询
     *
     * @param celebrityPrivateGoodsDetailModel
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "网红销售明细-网红带货明细分页列表查询")
@Operation(summary = "网红销售明细-网红带货明细分页列表查询", description = "网红销售明细-网红带货明细分页列表查询")
    @GetMapping(value = "/celebrityDetailList")
    public Result<?> celebrityDetailList(CelebrityPrivateGoodsDetailModel celebrityPrivateGoodsDetailModel,
                                         @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                         HttpServletRequest req) {


        if(oConvertUtils.isNotEmpty(celebrityPrivateGoodsDetailModel.getMonthNew()) && celebrityPrivateGoodsDetailModel.getMonthNew().equals("Invalid date")){
            celebrityPrivateGoodsDetailModel.setMonth(null);
        }else{
            int months  =  celebrityPrivateGoodsDetailModel.getMonthNew().startsWith("0") ?  Integer.parseInt(celebrityPrivateGoodsDetailModel.getMonthNew().substring(celebrityPrivateGoodsDetailModel.getMonthNew().length()-1)) : Integer.parseInt(celebrityPrivateGoodsDetailModel.getMonthNew());

            celebrityPrivateGoodsDetailModel.setMonth(months);
        }

        Page<CelebrityPrivateGoodsDetailModel> page = new Page<CelebrityPrivateGoodsDetailModel>(pageNo, pageSize);
        IPage<CelebrityPrivateGoodsDetailModel> pageList = storeSellDetailService.celebrityDetailList(page, celebrityPrivateGoodsDetailModel);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param storeSellDetailModel
     * @return
     */
    @AutoLog(value = "网红销售明细-添加")
@Operation(summary = "网红销售明细-添加", description = "网红销售明细-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody StoreSellDetailModel storeSellDetailModel) {
        storeSellDetailModel.setCreateBy(getUserNameByToken());
        storeSellDetailModel.setCreateTime(new Date());
        storeSellDetailService.save(storeSellDetailModel);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param storeSellDetail
     * @return
     */
    @AutoLog(value = "网红销售明细-编辑")
@Operation(summary = "网红销售明细-编辑", description = "网红销售明细-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody StoreSellDetail storeSellDetail) {
        storeSellDetail.setUpdateBy(getUserNameByToken());
        storeSellDetail.setUpdateTime(new Date());
        storeSellDetailService.updateById(storeSellDetail);
        return Result.ok("编辑成功!");
    }

    /**
     * 结算
     *
     * @param storeSellDetail
     * @return
     */
    @AutoLog(value = "网红销售明细-结算")
@Operation(summary = "网红销售明细-结算", description = "网红销售明细-结算")
    @PutMapping(value = "/clearing")
    public Result<?> clearing(@RequestBody StoreSellDetail storeSellDetail) {
        storeSellDetail.setSettlementTime(new Date());
        storeSellDetail.setSettlementStatus(YesNoStatus.YES.getCode());
        storeSellDetail.setUpdateBy(getUserNameByToken());
        storeSellDetail.setUpdateTime(new Date());
        storeSellDetailService.updateById(storeSellDetail);
        return Result.ok("结算成功!");
    }
    /**
     * 结算
     *
     * @param id
     * @return
     */
    @AutoLog(value = "网红销售明细-取消结算")
@Operation(summary = "网红销售明细-取消结算", description = "网红销售明细-取消结算")
    @GetMapping(value = "/repealClearing")
    public Result<?> repealClearing(String id) {

        StoreSellDetail sellDetail = storeSellDetailService.getById(id);

        sellDetail.setSettlementTime(null);
        sellDetail.setSettlementAmount(new BigDecimal(0));
        sellDetail.setSettlementStatus(YesNoStatus.NO.getCode());
        sellDetail.setUpdateBy(getUserNameByToken());
        sellDetail.setUpdateTime(new Date());
        storeSellDetailService.updateById(sellDetail);
        return Result.ok("取消结算!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "网红销售明细-通过id删除")
@Operation(summary = "网红销售明细-通过id删除", description = "网红销售明细-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        storeSellDetailService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "网红销售明细-批量删除")
@Operation(summary = "网红销售明细-批量删除", description = "网红销售明细-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.storeSellDetailService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "网红销售明细-通过id查询")
@Operation(summary = "网红销售明细-通过id查询", description = "网红销售明细-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        StoreSellDetail storeSellDetail = storeSellDetailService.getById(id);
        return Result.ok(storeSellDetail);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param storeSellDetail
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StoreSellDetail storeSellDetail) {
        return super.exportXls(request, storeSellDetail, StoreSellDetail.class, "网红销售明细");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, StoreSellDetail.class);
    }

    /**
     * 处理数据:
     * 1、添加带货明细时如果已经生成了此月份的结算表则需要增加结算表的带货数量+1，如果没有生成网红此月份的结算表则同时生成结算表，并生成一条带货明细。
     * 2、如果删除带货明细时，需要删除相应结算数据的带货数量。
     * 3、如果添加带货明细所在月份已经结算，提示“您选择的月份此网红已经结算！”
     *
     * @param storeSellDetailModel
     * @return
     * @Author: zhoushen
     */
    @AutoLog(value = "网红销售明细-添加")
@Operation(summary = "网红销售明细-添加", description = "网红销售明细-添加")
    @PostMapping(value = "/handleSellDetailData")
    public Result<?> handleSellDetailData(HttpServletRequest request, @RequestBody StoreSellDetailModel storeSellDetailModel) {
        return storeSellDetailService.handleSellDetailData(request, storeSellDetailModel);
    }


    /**
     * 通过私有网红id查询
     *
     * @param storeSellDetailModel
     * @return
     * @Author: zhoushen
     */
    @AutoLog(value = "网红销售明细-通过id查询")
@Operation(summary = "网红销售明细-通过id查询", description = "网红销售明细-通过id查询")
    @GetMapping(value = "/queryByCelebrityPrivateId")
    public Result<?> queryByCelebrityPrivateId(StoreSellDetailModel storeSellDetailModel,
                                               @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<StoreSellDetailModel> page = new Page<>(pageNo, pageSize);
        return Result.ok(storeSellDetailService.queryByCelebrityPrivateId(page, storeSellDetailModel));
    }


    /**
     * 编辑带货明细
     *
     * @param storeSellDetailModel
     * @return
     * @Author: zhoushen
     */
    @AutoLog(value = "网红销售明细-编辑带货明细")
@Operation(summary = "网红销售明细-编辑带货明细", description = "编辑带货明细")
    @PutMapping(value = "/handleEdit")
    public Result<?> handleEdit(HttpServletRequest request, @RequestBody StoreSellDetailModel storeSellDetailModel) {
        return storeSellDetailService.handleEdit(request, storeSellDetailModel);
    }


    /**
     * 删除带货明细,相应结算表数量-1
     *
     * @param storeSellDetailModel
     * @return
     * @Author: zhoushen
     */
    @AutoLog(value = "网红销售明细-删除带货明细,相应结算表数量-1")
@Operation(summary = "网红销售明细-删除带货明细,相应结算表数量-1", description = "网红销售明细-删除带货明细,相应结算表数量-1")
    @PutMapping(value = "/handleDelete")
    public Result<?> handleDelete(HttpServletRequest request, @RequestBody StoreSellDetailModel storeSellDetailModel) {
        return storeSellDetailService.handleDelete(request, storeSellDetailModel);
    }
}
