package org.jeecg.modules.instagram.storebasics.controller;

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
import org.jeecg.modules.instagram.storebasics.entity.StoreLevel;
import org.jeecg.modules.instagram.storebasics.service.IStoreLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;

/**
 * 功能描述:红人层级表
 *
 * @Author: nqr
 * @Date 2020-09-10 10:05:45
 */
@Slf4j
@Tag(name = "红人层级表")
@RestController
@RequestMapping("/storelevel/storeLevel")
public class StoreLevelController extends JeecgController<StoreLevel, IStoreLevelService> {
    @Autowired
    private IStoreLevelService storeLevelService;

    /**
     * 分页列表查询
     *
     * @param storeLevel
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "红人层级表-分页列表查询")
    @Operation(summary = "红人层级表-分页列表查询", description = "红人层级表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StoreLevel storeLevel,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<StoreLevel> queryWrapper = QueryGenerator.initQueryWrapper(storeLevel, req.getParameterMap());
        Page<StoreLevel> page = new Page<StoreLevel>(pageNo, pageSize);
        IPage<StoreLevel> pageList = storeLevelService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param storeLevel
     * @return
     */
    @AutoLog(value = "红人层级表-添加")
    @Operation(summary = "红人层级表-添加", description = "红人层级表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody StoreLevel storeLevel) {
        storeLevelService.save(storeLevel);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param storeLevel
     * @return
     */
    @AutoLog(value = "红人层级表-编辑")
    @Operation(summary = "红人层级表-编辑", description = "红人层级表-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody StoreLevel storeLevel) {
        storeLevelService.updateById(storeLevel);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "红人层级表-通过id删除")
    @Operation(summary = "红人层级表-通过id删除", description = "红人层级表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        storeLevelService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "红人层级表-批量删除")
    @Operation(summary = "红人层级表-批量删除", description = "红人层级表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.storeLevelService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "红人层级表-通过id查询")
    @Operation(summary = "红人层级表-通过id查询", description = "红人层级表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        StoreLevel storeLevel = storeLevelService.getById(id);
        return Result.ok(storeLevel);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param storeLevel
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StoreLevel storeLevel) {
        return super.exportXls(request, storeLevel, StoreLevel.class, "红人层级表");
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
        return super.importExcel(request, response, StoreLevel.class);
    }

}
