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
import org.jeecg.modules.instagram.storepromotion.entity.StorePushRefund;
import org.jeecg.modules.instagram.storepromotion.service.IStorePushRefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Description: 商家消费失败退还记录表
 * @Author: jeecg-boot
 * @Date: 2020-11-03
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "商家消费失败退还记录表")
@RestController
@RequestMapping("/storePushRefund")
public class StorePushRefundController extends JeecgController<StorePushRefund, IStorePushRefundService> {
    @Autowired
    private IStorePushRefundService storePushRefundService;

    /**
     * 分页列表查询
     *
     * @param storePushRefund
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "商家消费失败退还记录表-分页列表查询")
@Operation(summary = "商家消费失败退还记录表-分页列表查询", description = "商家消费失败退还记录表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StorePushRefund storePushRefund,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        String userId = getUserIdByToken();
        String username = getUserNameByToken();
        QueryWrapper<StorePushRefund> queryWrapper = QueryGenerator.initQueryWrapper(storePushRefund, req.getParameterMap());
        if (!"admin".equals(username)) {
            queryWrapper.eq("seller_id", userId);
        }
        Page<StorePushRefund> page = new Page<StorePushRefund>(pageNo, pageSize);
        IPage<StorePushRefund> pageList = storePushRefundService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param storePushRefund
     * @return
     */
    @AutoLog(value = "商家消费失败退还记录表-添加")
@Operation(summary = "商家消费失败退还记录表-添加", description = "商家消费失败退还记录表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody StorePushRefund storePushRefund) {
        storePushRefundService.save(storePushRefund);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param storePushRefund
     * @return
     */
    @AutoLog(value = "商家消费失败退还记录表-编辑")
@Operation(summary = "商家消费失败退还记录表-编辑", description = "商家消费失败退还记录表-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody StorePushRefund storePushRefund) {
        storePushRefundService.updateById(storePushRefund);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "商家消费失败退还记录表-通过id删除")
@Operation(summary = "商家消费失败退还记录表-通过id删除", description = "商家消费失败退还记录表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        storePushRefundService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "商家消费失败退还记录表-批量删除")
@Operation(summary = "商家消费失败退还记录表-批量删除", description = "商家消费失败退还记录表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.storePushRefundService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "商家消费失败退还记录表-通过id查询")
@Operation(summary = "商家消费失败退还记录表-通过id查询", description = "商家消费失败退还记录表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        StorePushRefund storePushRefund = storePushRefundService.getById(id);
        return Result.ok(storePushRefund);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param storePushRefund
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StorePushRefund storePushRefund) {
        return super.exportXls(request, storePushRefund, StorePushRefund.class, "商家消费失败退还记录表");
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
        return super.importExcel(request, response, StorePushRefund.class);
    }

}
