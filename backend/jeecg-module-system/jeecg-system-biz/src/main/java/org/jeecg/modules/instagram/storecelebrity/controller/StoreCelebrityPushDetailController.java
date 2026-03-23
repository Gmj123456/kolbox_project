package org.jeecg.modules.instagram.storecelebrity.controller;

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
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPushDetail;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityPushDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Description: 邮件发送历史表
 * @Author: jeecg-boot
 * @Date: 2020-09-29
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "邮件发送历史表")
@RestController
@RequestMapping("/pushdetail")
public class StoreCelebrityPushDetailController extends JeecgController<StoreCelebrityPushDetail, IStoreCelebrityPushDetailService> {
    @Autowired
    private IStoreCelebrityPushDetailService storeCelebrityPushDetailService;

    /**
     * 分页列表查询
     *
     * @param storeCelebrityPushDetail
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "邮件发送历史表-分页列表查询")
@Operation(summary = "邮件发送历史表-分页列表查询", description = "邮件发送历史表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StoreCelebrityPushDetail storeCelebrityPushDetail,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<StoreCelebrityPushDetail> queryWrapper = QueryGenerator.initQueryWrapper(storeCelebrityPushDetail, req.getParameterMap());
        Page<StoreCelebrityPushDetail> page = new Page<StoreCelebrityPushDetail>(pageNo, pageSize);
        IPage<StoreCelebrityPushDetail> pageList = storeCelebrityPushDetailService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param storeCelebrityPushDetail
     * @return
     */
    @AutoLog(value = "邮件发送历史表-添加")
@Operation(summary = "邮件发送历史表-添加", description = "邮件发送历史表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody StoreCelebrityPushDetail storeCelebrityPushDetail) {
        storeCelebrityPushDetailService.save(storeCelebrityPushDetail);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param storeCelebrityPushDetail
     * @return
     */
    @AutoLog(value = "邮件发送历史表-编辑")
@Operation(summary = "邮件发送历史表-编辑", description = "邮件发送历史表-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody StoreCelebrityPushDetail storeCelebrityPushDetail) {
        storeCelebrityPushDetailService.updateById(storeCelebrityPushDetail);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "邮件发送历史表-通过id删除")
@Operation(summary = "邮件发送历史表-通过id删除", description = "邮件发送历史表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        storeCelebrityPushDetailService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "邮件发送历史表-批量删除")
@Operation(summary = "邮件发送历史表-批量删除", description = "邮件发送历史表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.storeCelebrityPushDetailService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "邮件发送历史表-通过id查询")
@Operation(summary = "邮件发送历史表-通过id查询", description = "邮件发送历史表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        StoreCelebrityPushDetail storeCelebrityPushDetail = storeCelebrityPushDetailService.getById(id);
        return Result.ok(storeCelebrityPushDetail);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param storeCelebrityPushDetail
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StoreCelebrityPushDetail storeCelebrityPushDetail) {
        return super.exportXls(request, storeCelebrityPushDetail, StoreCelebrityPushDetail.class, "邮件发送历史表");
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
        return super.importExcel(request, response, StoreCelebrityPushDetail.class);
    }

}
