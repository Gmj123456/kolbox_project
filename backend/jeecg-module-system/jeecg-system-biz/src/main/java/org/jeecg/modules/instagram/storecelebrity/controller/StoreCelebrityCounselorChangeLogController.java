package org.jeecg.modules.instagram.storecelebrity.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.ExamConstants;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityCounselorChangeLog;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityCounselorChangeDetailService;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityCounselorChangeLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Description: 网红顾问变更日志
 * @Author: jeecg-boot
 * @Date: 2025-10-30
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "网红顾问变更日志")
@RestController
@RequestMapping("/counselorChangeLog")
public class StoreCelebrityCounselorChangeLogController extends JeecgController<StoreCelebrityCounselorChangeLog, IStoreCelebrityCounselorChangeLogService> {
    @Autowired
    private IStoreCelebrityCounselorChangeLogService storeCelebrityCounselorChangeLogService;
    /**
     * 分页列表查询
     *
     * @param storeCelebrityCounselorChangeLog
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "网红顾问变更日志-" + ExamConstants.PAGE_LIST_QUERY)
@Operation(summary = "网红顾问变更日志-" + ExamConstants.PAGE_LIST_QUERY, description = "网红顾问变更日志-" + ExamConstants.PAGE_LIST_QUERY)
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StoreCelebrityCounselorChangeLog storeCelebrityCounselorChangeLog, @RequestParam(name = ExamConstants.PAGE_NO, defaultValue = ExamConstants.PAGE_NO_DEFAULT) Integer pageNo, @RequestParam(name = ExamConstants.PAGE_SIZE, defaultValue = ExamConstants.PAGE_SIZE_DEFAULT) Integer pageSize, HttpServletRequest req) {
        QueryWrapper<StoreCelebrityCounselorChangeLog> queryWrapper = QueryGenerator.initQueryWrapper(storeCelebrityCounselorChangeLog, req.getParameterMap());
        Page<StoreCelebrityCounselorChangeLog> page = new Page<StoreCelebrityCounselorChangeLog>(pageNo, pageSize);
        IPage<StoreCelebrityCounselorChangeLog> pageList = storeCelebrityCounselorChangeLogService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param storeCelebrityCounselorChangeLog
     * @return
     */
    @AutoLog(value = "网红顾问变更日志-" + ExamConstants.ADD)
@Operation(summary = "网红顾问变更日志-" + ExamConstants.ADD, description = "网红顾问变更日志-" + ExamConstants.ADD)
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody StoreCelebrityCounselorChangeLog storeCelebrityCounselorChangeLog) {
        storeCelebrityCounselorChangeLogService.save(storeCelebrityCounselorChangeLog);
        return Result.ok(ExamConstants.ADD_SUCCESS);
    }

    /**
     * 编辑
     *
     * @param storeCelebrityCounselorChangeLog
     * @return
     */
    @AutoLog(value = "网红顾问变更日志-" + ExamConstants.EDIT)
@Operation(summary = "网红顾问变更日志-" + ExamConstants.EDIT, description = "网红顾问变更日志-" + ExamConstants.EDIT)
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody StoreCelebrityCounselorChangeLog storeCelebrityCounselorChangeLog) {
        storeCelebrityCounselorChangeLogService.updateById(storeCelebrityCounselorChangeLog);
        return Result.ok(ExamConstants.EDIT_SUCCESS);
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "网红顾问变更日志-" + ExamConstants.DELETE_BY_ID)
@Operation(summary = "网红顾问变更日志-" + ExamConstants.DELETE_BY_ID, description = "网红顾问变更日志-" + ExamConstants.DELETE_BY_ID)
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        storeCelebrityCounselorChangeLogService.removeById(id);
        return Result.ok(ExamConstants.DELETE_SUCCESS);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "网红顾问变更日志-" + ExamConstants.DELETE_BATCH)
@Operation(summary = "网红顾问变更日志-" + ExamConstants.DELETE_BATCH, description = "网红顾问变更日志-" + ExamConstants.DELETE_BATCH)
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.storeCelebrityCounselorChangeLogService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok(ExamConstants.DELETE_BATCH_SUCCESS);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "网红顾问变更日志-" + ExamConstants.QUERY_BY_ID)
@Operation(summary = "网红顾问变更日志-" + ExamConstants.QUERY_BY_ID, description = "网红顾问变更日志-" + ExamConstants.QUERY_BY_ID)
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        StoreCelebrityCounselorChangeLog storeCelebrityCounselorChangeLog = storeCelebrityCounselorChangeLogService.getById(id);
        return Result.ok(storeCelebrityCounselorChangeLog);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param storeCelebrityCounselorChangeLog
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StoreCelebrityCounselorChangeLog storeCelebrityCounselorChangeLog) {
        return super.exportXls(request, storeCelebrityCounselorChangeLog, StoreCelebrityCounselorChangeLog.class, "网红顾问变更日志");
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
        return super.importExcel(request, response, StoreCelebrityCounselorChangeLog.class);
    }

}
