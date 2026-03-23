package org.jeecg.modules.instagram.storebasics.controller;

import java.util.Arrays;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.modules.instagram.storebasics.entity.ForexRates;
import org.jeecg.modules.instagram.storebasics.service.IForexRatesService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * @Description: 实时汇率
 * @Author: jeecg-boot
 * @Date: 2021-04-19
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "实时汇率")
@RestController
@RequestMapping("/forexrates")
public class ForexRatesController extends JeecgController<ForexRates, IForexRatesService> {
    @Autowired
    private IForexRatesService forexRatesService;

    /**
     * 分页列表查询
     *
     * @param forexRates
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "实时汇率-分页列表查询")
@Operation(summary = "实时汇率-分页列表查询", description = "实时汇率-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(ForexRates forexRates,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<ForexRates> queryWrapper = QueryGenerator.initQueryWrapper(forexRates, req.getParameterMap());
        Page<ForexRates> page = new Page<>(pageNo, pageSize);
        IPage<ForexRates> pageList = forexRatesService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param forexRates
     * @return
     */
    @AutoLog(value = "实时汇率-添加")
@Operation(summary = "实时汇率-添加", description = "实时汇率-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody ForexRates forexRates) {
        forexRatesService.save(forexRates);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param forexRates
     * @return
     */
    @AutoLog(value = "实时汇率-编辑")
@Operation(summary = "实时汇率-编辑", description = "实时汇率-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody ForexRates forexRates) {
        forexRatesService.updateById(forexRates);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "实时汇率-通过id删除")
@Operation(summary = "实时汇率-通过id删除", description = "实时汇率-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        forexRatesService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "实时汇率-批量删除")
@Operation(summary = "实时汇率-批量删除", description = "实时汇率-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.forexRatesService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "实时汇率-通过id查询")
@Operation(summary = "实时汇率-通过id查询", description = "实时汇率-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        ForexRates forexRates = forexRatesService.getById(id);
        return Result.ok(forexRates);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param forexRates
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ForexRates forexRates) {
        return super.exportXls(request, forexRates, ForexRates.class, "实时汇率");
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
        return super.importExcel(request, response, ForexRates.class);
    }

}
