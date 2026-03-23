package org.jeecg.modules.kol.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.SystemConstants;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.kol.entity.KolAllotLog;
import org.jeecg.modules.kol.service.IKolAllotLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Description: 分配日志表
 * @Author: dongruyang
 * @Date: 2025-09-17
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "分配日志表")
@RestController
@RequestMapping("/kol/kolAllotLog")
public class KolAllotLogController extends JeecgController<KolAllotLog, IKolAllotLogService> {
    @Autowired
    private IKolAllotLogService kolAllotLogService;
    @Autowired
    private ISysBaseAPI sysBaseAPI;

    /**
     * 分页列表查询
     *
     * @param kolAllotLog
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "分配日志表-" + SystemConstants.PAGE_LIST_QUERY)
    @Operation(summary = "分配日志表-" + SystemConstants.PAGE_LIST_QUERY, description = "分配日志表-" + SystemConstants.PAGE_LIST_QUERY)
    @GetMapping(value = "/list")
    public Result<?> queryPageList(KolAllotLog kolAllotLog,
                                   @RequestParam(name = SystemConstants.PAGE_NO, defaultValue = SystemConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                   @RequestParam(name = SystemConstants.PAGE_SIZE, defaultValue = SystemConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
                                   HttpServletRequest req) {
        if (oConvertUtils.isNotEmpty(kolAllotLog.getAllotCounselorNames())) {
            kolAllotLog.setAllotCounselorNames("*" + kolAllotLog.getAllotCounselorNames() + "*");
        }
        if (oConvertUtils.isNotEmpty(kolAllotLog.getAllotContent())) {
            kolAllotLog.setAllotContent("*" + kolAllotLog.getAllotContent() + "*");
        }
        LambdaQueryWrapper<KolAllotLog> queryWrapper = QueryGenerator.initQueryWrapper(kolAllotLog, req.getParameterMap()).lambda();
        Page<KolAllotLog> page = new Page<>(pageNo, pageSize);
        IPage<KolAllotLog> pageList = kolAllotLogService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param kolAllotLog
     * @return
     */
    @AutoLog(value = "分配日志表-" + SystemConstants.ADD)
    @Operation(summary = "分配日志表-" + SystemConstants.ADD, description = "分配日志表-" + SystemConstants.ADD)
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody KolAllotLog kolAllotLog) {
        kolAllotLogService.save(kolAllotLog);
        return Result.ok(SystemConstants.ADD_SUCCESS);
    }

    /**
     * 编辑
     *
     * @param kolAllotLog
     * @return
     */
    @AutoLog(value = "分配日志表-" + SystemConstants.EDIT)
    @Operation(summary = "分配日志表-" + SystemConstants.EDIT, description = "分配日志表-" + SystemConstants.EDIT)
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody KolAllotLog kolAllotLog) {
        kolAllotLogService.updateById(kolAllotLog);
        return Result.ok(SystemConstants.EDIT_SUCCESS);
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "分配日志表-" + SystemConstants.DELETE_BY_ID)
    @Operation(summary = "分配日志表-" + SystemConstants.DELETE_BY_ID, description = "分配日志表-" + SystemConstants.DELETE_BY_ID)
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        kolAllotLogService.removeById(id);
        return Result.ok(SystemConstants.DELETE_SUCCESS);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "分配日志表-" + SystemConstants.DELETE_BATCH)
    @Operation(summary = "分配日志表-" + SystemConstants.DELETE_BATCH, description = "分配日志表-" + SystemConstants.DELETE_BATCH)
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.kolAllotLogService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok(SystemConstants.DELETE_BATCH_SUCCESS);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "分配日志表-" + SystemConstants.QUERY_BY_ID)
    @Operation(summary = "分配日志表-" + SystemConstants.QUERY_BY_ID, description = "分配日志表-" + SystemConstants.QUERY_BY_ID)
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        KolAllotLog kolAllotLog = kolAllotLogService.getById(id);
        return Result.ok(kolAllotLog);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param kolAllotLog
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, KolAllotLog kolAllotLog) {
        return super.exportXls(request, kolAllotLog, KolAllotLog.class, "分配日志表");
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
        return super.importExcel(request, response, KolAllotLog.class);
    }

}
