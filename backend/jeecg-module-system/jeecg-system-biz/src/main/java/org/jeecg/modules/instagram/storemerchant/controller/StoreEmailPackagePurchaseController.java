package org.jeecg.modules.instagram.storemerchant.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.util.PageUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.storemerchant.entity.StoreEmailPackagePurchase;
import org.jeecg.modules.instagram.storemerchant.model.StoreEmailPackagePurchaseModel;
import org.jeecg.modules.instagram.storemerchant.service.IStoreEmailPackagePurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Description: 商家订购流量包表
 * @Author: jeecg-boot
 * @Date: 2020-10-01
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "商家订购流量包表")
@RestController
@RequestMapping("/storeemailpackagepurchase/storeEmailPackagePurchase")
public class StoreEmailPackagePurchaseController extends JeecgController<StoreEmailPackagePurchase, IStoreEmailPackagePurchaseService> {
    @Autowired
    private IStoreEmailPackagePurchaseService storeEmailPackagePurchaseService;

    /**
     * 分页列表查询
     *
     * @param storeEmailPackagePurhcaseModel
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "商家订购流量包表-分页列表查询")
@Operation(summary = "商家订购流量包表-分页列表查询", description = "商家订购流量包表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StoreEmailPackagePurchaseModel storeEmailPackagePurhcaseModel,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,HttpServletRequest req) {
        String userId = getUserIdByToken();
        LambdaQueryWrapper<StoreEmailPackagePurchase> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.and(i -> i.eq(StoreEmailPackagePurchase::getSellerId, userId).or().eq(StoreEmailPackagePurchase::getInviteSellerId, userId));
        if (oConvertUtils.isNotEmpty(storeEmailPackagePurhcaseModel.getCreateDate())) {
            lambdaQueryWrapper.like(StoreEmailPackagePurchase::getCreateTime, storeEmailPackagePurhcaseModel.getCreateDate());
        }
        if (oConvertUtils.isNotEmpty(storeEmailPackagePurhcaseModel.getExpirationTime())) {
            lambdaQueryWrapper.like(StoreEmailPackagePurchase::getExpirationDate, storeEmailPackagePurhcaseModel.getExpirationTime());
        }
        if (oConvertUtils.isNotEmpty(storeEmailPackagePurhcaseModel.getPackageName())) {
            lambdaQueryWrapper.eq(StoreEmailPackagePurchase::getPackageName, storeEmailPackagePurhcaseModel.getPackageName());
        }
        if (oConvertUtils.isNotEmpty(storeEmailPackagePurhcaseModel.getPurchaseType())) {
            lambdaQueryWrapper.eq(StoreEmailPackagePurchase::getPurchaseType, storeEmailPackagePurhcaseModel.getPurchaseType());
        }
        Page<StoreEmailPackagePurchase> page = new Page<>(pageNo, pageSize);
        IPage<StoreEmailPackagePurchase> pageList = storeEmailPackagePurchaseService.page(PageUtil.getOrderItems(pageNo, pageSize, req), lambdaQueryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param storeEmailPackagePurchase
     * @return
     */
    @AutoLog(value = "商家订购流量包表-添加")
@Operation(summary = "商家订购流量包表-添加", description = "商家订购流量包表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody StoreEmailPackagePurchase storeEmailPackagePurchase) {
        storeEmailPackagePurchaseService.save(storeEmailPackagePurchase);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param storeEmailPackagePurchase
     * @return
     */
    @AutoLog(value = "商家订购流量包表-编辑")
@Operation(summary = "商家订购流量包表-编辑", description = "商家订购流量包表-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody StoreEmailPackagePurchase storeEmailPackagePurchase) {
        storeEmailPackagePurchaseService.updateById(storeEmailPackagePurchase);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "商家订购流量包表-通过id删除")
@Operation(summary = "商家订购流量包表-通过id删除", description = "商家订购流量包表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        storeEmailPackagePurchaseService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "商家订购流量包表-批量删除")
@Operation(summary = "商家订购流量包表-批量删除", description = "商家订购流量包表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.storeEmailPackagePurchaseService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "商家订购流量包表-通过id查询")
@Operation(summary = "商家订购流量包表-通过id查询", description = "商家订购流量包表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        StoreEmailPackagePurchase storeEmailPackagePurchase = storeEmailPackagePurchaseService.getById(id);
        return Result.ok(storeEmailPackagePurchase);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param storeEmailPackagePurchase
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StoreEmailPackagePurchase storeEmailPackagePurchase) {
        return super.exportXls(request, storeEmailPackagePurchase, StoreEmailPackagePurchase.class, "商家订购流量包表");
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
        return super.importExcel(request, response, StoreEmailPackagePurchase.class);
    }

}
