package org.jeecg.modules.instagram.storepromotion.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.SystemConstants;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionalArticle;
import org.jeecg.modules.instagram.storepromotion.service.IStorePromotionalArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 海玛media 资讯模块
 * @Author: nqr
 * @Date: 2023-03-13
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "海玛media 资讯模块")
@RestController
@RequestMapping("/promotionalArticle")
public class StorePromotionalArticleController extends JeecgController<StorePromotionalArticle, IStorePromotionalArticleService> {
    @Autowired
    private IStorePromotionalArticleService storePromotionalArticleService;

    /**
     * 分页列表查询
     *
     * @param storePromotionalArticle
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "海玛media 资讯模块-" + SystemConstants.PAGE_LIST_QUERY)
@Operation(summary = "海玛media 资讯模块-" + SystemConstants.PAGE_LIST_QUERY, description = "海玛media 资讯模块-" + SystemConstants.PAGE_LIST_QUERY)
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StorePromotionalArticle storePromotionalArticle,
                                   @RequestParam(name = SystemConstants.PAGE_NO, defaultValue = SystemConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                   @RequestParam(name = SystemConstants.PAGE_SIZE, defaultValue = SystemConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<StorePromotionalArticle> queryWrapper = QueryGenerator.initQueryWrapper(storePromotionalArticle, req.getParameterMap());
        Page<StorePromotionalArticle> page = new Page<StorePromotionalArticle>(pageNo, pageSize);
        IPage<StorePromotionalArticle> pageList = storePromotionalArticleService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 分页列表查询
     *
     * @param storePromotionalArticle
     * @param req
     * @return
     */
    @AutoLog(value = "海玛media 资讯模块-" + SystemConstants.PAGE_LIST_QUERY)
@Operation(summary = "海玛media 资讯模块-" + SystemConstants.PAGE_LIST_QUERY, description = "海玛media 资讯模块-" + SystemConstants.PAGE_LIST_QUERY)
    @GetMapping(value = "/listAll")
    public Result<?> listAll(StorePromotionalArticle storePromotionalArticle, HttpServletRequest req) {
        QueryWrapper<StorePromotionalArticle> queryWrapper = QueryGenerator.initQueryWrapper(storePromotionalArticle, req.getParameterMap());
        List<StorePromotionalArticle> articleList = storePromotionalArticleService.list(queryWrapper);
        return Result.ok(articleList);
    }

    /**
     * 添加
     *
     * @param storePromotionalArticle
     * @return
     */
    @AutoLog(value = "海玛media 资讯模块-" + SystemConstants.ADD)
@Operation(summary = "海玛media 资讯模块-" + SystemConstants.ADD, description = "海玛media 资讯模块-" + SystemConstants.ADD)
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody StorePromotionalArticle storePromotionalArticle) {
        storePromotionalArticleService.save(storePromotionalArticle);
        return Result.ok(SystemConstants.ADD_SUCCESS);
    }

    /**
     * 编辑
     *
     * @param storePromotionalArticle
     * @return
     */
    @AutoLog(value = "海玛media 资讯模块-" + SystemConstants.EDIT)
@Operation(summary = "海玛media 资讯模块-" + SystemConstants.EDIT, description = "海玛media 资讯模块-" + SystemConstants.EDIT)
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody StorePromotionalArticle storePromotionalArticle) {
        storePromotionalArticleService.updateById(storePromotionalArticle);
        return Result.ok(SystemConstants.EDIT_SUCCESS);
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "海玛media 资讯模块-" + SystemConstants.DELETE_BY_ID)
@Operation(summary = "海玛media 资讯模块-" + SystemConstants.DELETE_BY_ID, description = "海玛media 资讯模块-" + SystemConstants.DELETE_BY_ID)
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        storePromotionalArticleService.removeById(id);
        return Result.ok(SystemConstants.DELETE_SUCCESS);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "海玛media 资讯模块-" + SystemConstants.DELETE_BATCH)
@Operation(summary = "海玛media 资讯模块-" + SystemConstants.DELETE_BATCH, description = "海玛media 资讯模块-" + SystemConstants.DELETE_BATCH)
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.storePromotionalArticleService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok(SystemConstants.DELETE_BATCH_SUCCESS);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "海玛media 资讯模块-" + SystemConstants.QUERY_BY_ID)
@Operation(summary = "海玛media 资讯模块-" + SystemConstants.QUERY_BY_ID, description = "海玛media 资讯模块-" + SystemConstants.QUERY_BY_ID)
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        StorePromotionalArticle storePromotionalArticle = storePromotionalArticleService.getById(id);
        return Result.ok(storePromotionalArticle);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param storePromotionalArticle
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StorePromotionalArticle storePromotionalArticle) {
        return super.exportXls(request, storePromotionalArticle, StorePromotionalArticle.class, "海玛media 资讯模块");
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
        return super.importExcel(request, response, StorePromotionalArticle.class);
    }

}
