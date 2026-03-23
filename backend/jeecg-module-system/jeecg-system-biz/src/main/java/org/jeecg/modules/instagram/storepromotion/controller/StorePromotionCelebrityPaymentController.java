package org.jeecg.modules.instagram.storepromotion.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionCelebrityPayment;
import org.jeecg.modules.instagram.storepromotion.mdoel.StorePromotionCelebrityPaymentModel;
import org.jeecg.modules.instagram.storepromotion.service.IStorePromotionCelebrityPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Description: 网红支付列表
 * @Author: jeecg-boot
 * @Date: 2021-09-27
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "网红支付列表")
@RestController
@RequestMapping("/celebrityPayment")
public class StorePromotionCelebrityPaymentController extends JeecgController<StorePromotionCelebrityPayment, IStorePromotionCelebrityPaymentService> {
    @Autowired
    private IStorePromotionCelebrityPaymentService storePromotionCelebrityPaymentService;

    /**
     * 分页列表查询
     *
     * @param storePromotionCelebrityPayment
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "网红支付列表-分页列表查询")
@Operation(summary = "网红支付列表-分页列表查询", description = "网红支付列表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StorePromotionCelebrityPayment storePromotionCelebrityPayment,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<StorePromotionCelebrityPayment> queryWrapper = QueryGenerator.initQueryWrapper(storePromotionCelebrityPayment, req.getParameterMap());
        Page<StorePromotionCelebrityPayment> page = new Page<>(pageNo, pageSize);
        IPage<StorePromotionCelebrityPayment> pageList = storePromotionCelebrityPaymentService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param storePromotionCelebrityPaymentModel
     * @return
     */
    @AutoLog(value = "网红支付列表-添加")
@Operation(summary = "网红支付列表-添加", description = "网红支付列表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody StorePromotionCelebrityPaymentModel storePromotionCelebrityPaymentModel) {
        storePromotionCelebrityPaymentService.saveCelebrityPayment(storePromotionCelebrityPaymentModel);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param storePromotionCelebrityPayment
     * @return
     */
    @AutoLog(value = "网红支付列表-编辑")
@Operation(summary = "网红支付列表-编辑", description = "网红支付列表-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody StorePromotionCelebrityPayment storePromotionCelebrityPayment) {
        storePromotionCelebrityPaymentService.updateById(storePromotionCelebrityPayment);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "网红支付列表-通过id删除")
@Operation(summary = "网红支付列表-通过id删除", description = "网红支付列表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        storePromotionCelebrityPaymentService.removeById(id);
        return Result.ok("删除成功!");

    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "网红支付列表-批量删除")
@Operation(summary = "网红支付列表-批量删除", description = "网红支付列表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.storePromotionCelebrityPaymentService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "网红支付列表-通过id查询")
@Operation(summary = "网红支付列表-通过id查询", description = "网红支付列表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        StorePromotionCelebrityPayment storePromotionCelebrityPayment = storePromotionCelebrityPaymentService.getById(id);
        return Result.ok(storePromotionCelebrityPayment);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param storePromotionCelebrityPayment
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StorePromotionCelebrityPayment storePromotionCelebrityPayment) {
        return super.exportXls(request, storePromotionCelebrityPayment, StorePromotionCelebrityPayment.class, "网红支付列表");
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
        return super.importExcel(request, response, StorePromotionCelebrityPayment.class);
    }

    @AutoLog(value = "撤销支付")
@Operation(summary = "撤销支付", description = "撤销支付")
    @PostMapping(value = "/cancelPayment")
    public Result<?> cancelPayment(@RequestBody StorePromotionCelebrityPaymentModel storePromotionCelebrityPaymentModel) {
        storePromotionCelebrityPaymentService.cancelPayment(storePromotionCelebrityPaymentModel);
        return Result.ok("撤销成功！");
    }
}
