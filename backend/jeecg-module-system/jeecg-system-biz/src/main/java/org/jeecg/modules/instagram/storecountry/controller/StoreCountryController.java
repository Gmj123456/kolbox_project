package org.jeecg.modules.instagram.storecountry.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import org.jeecg.modules.instagram.storecountry.entity.StoreCountry;
import org.jeecg.modules.instagram.storecountry.service.IStoreCountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 国家表
 * @Author: jeecg-boot
 * @Date: 2020-10-13
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "国家表")
@RestController
@RequestMapping("/storecountry/storeCountry")
public class StoreCountryController extends JeecgController<StoreCountry, IStoreCountryService> {
    @Autowired
    private IStoreCountryService storeCountryService;

    /**
     * 分页列表查询
     *
     * @param storeCountry
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "国家表-分页列表查询")
@Operation(summary = "国家表-分页列表查询", description = "国家表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StoreCountry storeCountry,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<StoreCountry> queryWrapper = QueryGenerator.initQueryWrapper(storeCountry, req.getParameterMap());
        Page<StoreCountry> page = new Page<StoreCountry>(pageNo, pageSize);
        IPage<StoreCountry> pageList = storeCountryService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 查询所有国家列表
     */
    @RequestMapping(value = "/findAll", method = RequestMethod.POST)
    public Result<?> getAllCountry() {
        LambdaQueryWrapper<StoreCountry> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByAsc(StoreCountry::getSortCode);
        return Result.ok(storeCountryService.list(lambdaQueryWrapper));
    }

    /**
     * 查询所有国家列表
     */
    @RequestMapping(value = "/getCommonCountry", method = RequestMethod.POST)
    public Result<?> getCommonCountry() {
        LambdaQueryWrapper<StoreCountry> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.eq(StoreCountry::getIsPromotion, YesNoStatus.YES.getCode());
        lambdaQueryWrapper.orderByAsc(StoreCountry::getSortCode);
        return Result.ok(storeCountryService.list(lambdaQueryWrapper));
    }

    /**
     * 添加
     *
     * @param storeCountry
     * @return
     */
    @AutoLog(value = "国家表-添加")
@Operation(summary = "国家表-添加", description = "国家表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody StoreCountry storeCountry) {
        storeCountryService.save(storeCountry);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param storeCountry
     * @return
     */
    @AutoLog(value = "国家表-编辑")
@Operation(summary = "国家表-编辑", description = "国家表-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody StoreCountry storeCountry) {
        storeCountryService.updateById(storeCountry);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "国家表-通过id删除")
@Operation(summary = "国家表-通过id删除", description = "国家表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        storeCountryService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "国家表-批量删除")
@Operation(summary = "国家表-批量删除", description = "国家表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.storeCountryService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "国家表-通过id查询")
@Operation(summary = "国家表-通过id查询", description = "国家表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        StoreCountry storeCountry = storeCountryService.getById(id);
        return Result.ok(storeCountry);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param storeCountry
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StoreCountry storeCountry) {
        return super.exportXls(request, storeCountry, StoreCountry.class, "国家表");
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
        return super.importExcel(request, response, StoreCountry.class);
    }

    /**
     * 功能描述: 获取推广国家
     *
     * @Author: nqr
     * @Date 2021-03-12 08:33:56
     */
    @GetMapping(value = "/getCountryList")
    public Result<?> getCountryList() {
        LambdaQueryWrapper<StoreCountry> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreCountry::getIsPromotion, YesNoStatus.YES.getCode());
        List<StoreCountry> list = storeCountryService.list(lambdaQueryWrapper);
        return Result.ok(list);
    }

    /**
     * 功能描述: 根据shortCode获取国家信息
     *
     * @Author: nqr
     * @Date 2021-03-23 08:21:21
     */
    @GetMapping(value = "/getCountryByCode")
    public Result<?> getCountryByCode(@RequestParam(name = "code") String code) {
        StoreCountry storeCountry = storeCountryService.getCountryByCode(code);
        return Result.ok(storeCountry);
    }
}
