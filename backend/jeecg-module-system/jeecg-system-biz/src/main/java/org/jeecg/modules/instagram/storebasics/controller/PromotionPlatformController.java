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
import org.jeecg.modules.instagram.storebasics.entity.PromotionPlatform;
import org.jeecg.modules.instagram.storebasics.service.IPromotionPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * @Description: 推广平台管理
 * @Author: jeecg-boot
 * @Date: 2021-01-23
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "推广平台管理")
@RestController
@RequestMapping("/promotionPlatform")
public class PromotionPlatformController extends JeecgController<PromotionPlatform, IPromotionPlatformService> {
    @Autowired
    private IPromotionPlatformService promotionPlatformService;

    /**
     * 分页列表查询
     *
     * @param promotionPlatform
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "推广平台管理")
@Operation(summary = "推广平台管理-分页列表查询", description = "推广平台管理-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(PromotionPlatform promotionPlatform,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<PromotionPlatform> queryWrapper = QueryGenerator.initQueryWrapper(promotionPlatform, req.getParameterMap());
        Page<PromotionPlatform> page = new Page<PromotionPlatform>(pageNo, pageSize);
        IPage<PromotionPlatform> pageList = promotionPlatformService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param promotionPlatform
     * @return
     */
    @AutoLog(value = "推广平台管理-添加")
@Operation(summary = "推广平台管理-添加", description = "推广平台管理-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody PromotionPlatform promotionPlatform) {
        promotionPlatformService.save(promotionPlatform);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param promotionPlatform
     * @return
     */
    @AutoLog(value = "推广平台管理-编辑")
@Operation(summary = "推广平台管理-编辑", description = "推广平台管理-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody PromotionPlatform promotionPlatform) {
        promotionPlatformService.updateById(promotionPlatform);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "推广平台管理-通过id删除")
@Operation(summary = "推广平台管理-通过id删除", description = "推广平台管理-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        promotionPlatformService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "推广平台管理-批量删除")
@Operation(summary = "推广平台管理-批量删除", description = "推广平台管理-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.promotionPlatformService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "推广平台管理-通过id查询")
@Operation(summary = "推广平台管理-通过id查询", description = "推广平台管理-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        PromotionPlatform promotionPlatform = promotionPlatformService.getById(id);
        return Result.ok(promotionPlatform);
    }

}
