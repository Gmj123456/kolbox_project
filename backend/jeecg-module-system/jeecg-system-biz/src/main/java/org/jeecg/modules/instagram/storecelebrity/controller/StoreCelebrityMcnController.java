package org.jeecg.modules.instagram.storecelebrity.controller;

import java.util.Arrays;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.SystemConstants;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityMcn;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityMcnService;

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
 * @Description: MCN管理
 * @Author: nqr
 * @Date: 2023-09-14
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "MCN管理")
@RestController
@RequestMapping("/storeCelebrityMcn")
public class StoreCelebrityMcnController extends JeecgController<StoreCelebrityMcn, IStoreCelebrityMcnService> {
    @Autowired
    private IStoreCelebrityMcnService storeCelebrityMcnService;

    /**
     * 分页列表查询
     *
     * @param storeCelebrityMcn
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "MCN管理-" + SystemConstants.PAGE_LIST_QUERY)
@Operation(summary = "MCN管理-" + SystemConstants.PAGE_LIST_QUERY, description = "MCN管理-" + SystemConstants.PAGE_LIST_QUERY)
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StoreCelebrityMcn storeCelebrityMcn,
                                   @RequestParam(name = SystemConstants.PAGE_NO, defaultValue = SystemConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                   @RequestParam(name = SystemConstants.PAGE_SIZE, defaultValue = SystemConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<StoreCelebrityMcn> queryWrapper = QueryGenerator.initQueryWrapper(storeCelebrityMcn, req.getParameterMap());
        Page<StoreCelebrityMcn> page = new Page<StoreCelebrityMcn>(pageNo, pageSize);
        IPage<StoreCelebrityMcn> pageList = storeCelebrityMcnService.page(page, queryWrapper);
        return Result.ok(pageList);
    }
    @AutoLog(value = "MCN管理-" + SystemConstants.PAGE_LIST_QUERY)
@Operation(summary = "MCN管理-" + SystemConstants.PAGE_LIST_QUERY, description = "MCN管理-" + SystemConstants.PAGE_LIST_QUERY)
    @GetMapping(value = "/getList")
    public Result<?> getList(StoreCelebrityMcn storeCelebrityMcn) {
        return Result.ok(storeCelebrityMcnService.list());
    }
    /**
     * 添加
     *
     * @param storeCelebrityMcn
     * @return
     */
    @AutoLog(value = "MCN管理-" + SystemConstants.ADD)
@Operation(summary = "MCN管理-" + SystemConstants.ADD, description = "MCN管理-" + SystemConstants.ADD)
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody StoreCelebrityMcn storeCelebrityMcn) {
        String mcnName = storeCelebrityMcn.getMcnName();
        StoreCelebrityMcn celebrityMcnOld = storeCelebrityMcnService.getByMcnName(mcnName);
        if (celebrityMcnOld != null) {
            return Result.error("当前MCN已存在");
        }
        storeCelebrityMcnService.save(storeCelebrityMcn);
        return Result.ok(SystemConstants.ADD_SUCCESS);
    }

    /**
     * 编辑
     *
     * @param storeCelebrityMcn
     * @return
     */
    @AutoLog(value = "MCN管理-" + SystemConstants.EDIT)
@Operation(summary = "MCN管理-" + SystemConstants.EDIT, description = "MCN管理-" + SystemConstants.EDIT)
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody StoreCelebrityMcn storeCelebrityMcn) {
        String mcnName = storeCelebrityMcn.getMcnName();
        StoreCelebrityMcn celebrityMcnOld = storeCelebrityMcnService.getByMcnName(mcnName);
        if(celebrityMcnOld!=null&&!celebrityMcnOld.getId().equals(storeCelebrityMcn.getId())){
            return Result.error("当前MCN已存在");
        }
        storeCelebrityMcnService.updateById(storeCelebrityMcn);
        return Result.ok(SystemConstants.EDIT_SUCCESS);
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "MCN管理-" + SystemConstants.DELETE_BY_ID)
@Operation(summary = "MCN管理-" + SystemConstants.DELETE_BY_ID, description = "MCN管理-" + SystemConstants.DELETE_BY_ID)
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        storeCelebrityMcnService.removeById(id);
        return Result.ok(SystemConstants.DELETE_SUCCESS);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "MCN管理-" + SystemConstants.DELETE_BATCH)
@Operation(summary = "MCN管理-" + SystemConstants.DELETE_BATCH, description = "MCN管理-" + SystemConstants.DELETE_BATCH)
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.storeCelebrityMcnService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok(SystemConstants.DELETE_BATCH_SUCCESS);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "MCN管理-" + SystemConstants.QUERY_BY_ID)
@Operation(summary = "MCN管理-" + SystemConstants.QUERY_BY_ID, description = "MCN管理-" + SystemConstants.QUERY_BY_ID)
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        StoreCelebrityMcn storeCelebrityMcn = storeCelebrityMcnService.getById(id);
        return Result.ok(storeCelebrityMcn);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param storeCelebrityMcn
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StoreCelebrityMcn storeCelebrityMcn) {
        return super.exportXls(request, storeCelebrityMcn, StoreCelebrityMcn.class, "MCN管理");
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
        return super.importExcel(request, response, StoreCelebrityMcn.class);
    }

}
