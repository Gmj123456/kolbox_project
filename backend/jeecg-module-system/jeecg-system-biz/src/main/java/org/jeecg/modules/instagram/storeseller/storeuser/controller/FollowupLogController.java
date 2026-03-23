package org.jeecg.modules.instagram.storeseller.storeuser.controller;

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
import org.jeecg.modules.instagram.storeseller.storeuser.entity.FollowupLog;
import org.jeecg.modules.instagram.storeseller.storeuser.service.IFollowupLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Description: 跟进日志表
 * @Author: jeecg-boot
 * @Date: 2020-05-26
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "跟进日志表")
@RestController
@RequestMapping("/followupLog")
public class FollowupLogController extends JeecgController<FollowupLog, IFollowupLogService> {
    @Autowired
    private IFollowupLogService followupLogService;

    /**
     * 分页列表查询
     *
     * @param followupLog
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "跟进日志表-分页列表查询")
@Operation(summary = "跟进日志表-分页列表查询", description = "跟进日志表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(FollowupLog followupLog,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<FollowupLog> queryWrapper = QueryGenerator.initQueryWrapper(followupLog, req.getParameterMap());
        Page<FollowupLog> page = new Page<FollowupLog>(pageNo, pageSize);
        IPage<FollowupLog> pageList = followupLogService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param followupLog
     * @return
     */
    @AutoLog(value = "跟进日志表-添加")
@Operation(summary = "跟进日志表-添加", description = "跟进日志表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody FollowupLog followupLog) {
        followupLogService.save(followupLog);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param followupLog
     * @return
     */
    @AutoLog(value = "跟进日志表-编辑")
@Operation(summary = "跟进日志表-编辑", description = "跟进日志表-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody FollowupLog followupLog) {
        followupLogService.updateById(followupLog);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "跟进日志表-通过id删除")
@Operation(summary = "跟进日志表-通过id删除", description = "跟进日志表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        followupLogService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "跟进日志表-批量删除")
@Operation(summary = "跟进日志表-批量删除", description = "跟进日志表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.followupLogService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "跟进日志表-通过id查询")
@Operation(summary = "跟进日志表-通过id查询", description = "跟进日志表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        FollowupLog followupLog = followupLogService.getById(id);
        return Result.ok(followupLog);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param followupLog
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, FollowupLog followupLog) {
        return super.exportXls(request, followupLog, FollowupLog.class, "跟进日志表");
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
        return super.importExcel(request, response, FollowupLog.class);
    }

}
