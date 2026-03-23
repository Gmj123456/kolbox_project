package org.jeecg.modules.instagram.storepromotion.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.SystemConstants;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionProjectDetail;
import org.jeecg.modules.instagram.storepromotion.service.IStorePromotionProjectDetailService;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 产品网红推广历史匹配详情
 * @Author: nqr
 * @Date: 2023-09-02
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "产品网红推广历史匹配详情")
@RestController
@RequestMapping("/projectDetail")
public class StorePromotionProjectDetailController extends JeecgController<StorePromotionProjectDetail, IStorePromotionProjectDetailService> {
    @Autowired
    private IStorePromotionProjectDetailService projectDetailService;

    /**
     * 分页列表查询
     *
     * @param storePromotionProjectDetail
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "产品网红推广历史匹配详情-" + SystemConstants.PAGE_LIST_QUERY)
@Operation(summary = "产品网红推广历史匹配详情-" + SystemConstants.PAGE_LIST_QUERY, description = "产品网红推广历史匹配详情-" + SystemConstants.PAGE_LIST_QUERY)
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StorePromotionProjectDetail storePromotionProjectDetail,
                                   @RequestParam(name = SystemConstants.PAGE_NO, defaultValue = SystemConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                   @RequestParam(name = SystemConstants.PAGE_SIZE, defaultValue = SystemConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<StorePromotionProjectDetail> queryWrapper = QueryGenerator.initQueryWrapper(storePromotionProjectDetail, req.getParameterMap());
        Page<StorePromotionProjectDetail> page = new Page<StorePromotionProjectDetail>(pageNo, pageSize);
        IPage<StorePromotionProjectDetail> pageList = projectDetailService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 分页列表查询
     *
     * @return
     */
    @AutoLog(value = "产品网红推广历史匹配详情-" + SystemConstants.PAGE_LIST_QUERY)
@Operation(summary = "产品网红推广历史匹配详情-" + SystemConstants.PAGE_LIST_QUERY, description = "产品网红推广历史匹配详情-" + SystemConstants.PAGE_LIST_QUERY)
    @GetMapping(value = "/getByProjectId")
    public Result<?> getByProjectId(StorePromotionProjectDetail storePromotionProjectDetail) {
        LambdaQueryWrapper<StorePromotionProjectDetail> queryWrapper = new LambdaQueryWrapper<>();
        if (oConvertUtils.isNotEmpty(storePromotionProjectDetail.getProjectId())) {
            queryWrapper.eq(StorePromotionProjectDetail::getProjectId, storePromotionProjectDetail.getProjectId());
        }
        if (oConvertUtils.isNotEmpty(storePromotionProjectDetail.getPlatformType())) {
            queryWrapper.eq(StorePromotionProjectDetail::getPlatformType, storePromotionProjectDetail.getPlatformType());
        }
        if (oConvertUtils.isNotEmpty(storePromotionProjectDetail.getAccount())) {
            queryWrapper.eq(StorePromotionProjectDetail::getAccount, storePromotionProjectDetail.getAccount());
        }
        if (oConvertUtils.isNotEmpty(storePromotionProjectDetail.getPromId())) {
            queryWrapper.eq(StorePromotionProjectDetail::getPromId, storePromotionProjectDetail.getPromId());
        }
        List<StorePromotionProjectDetail> list = projectDetailService.list(queryWrapper);
        return Result.ok(list);
    }

    /**
     * 添加
     *
     * @param storePromotionProjectDetail
     * @return
     */
    @AutoLog(value = "产品网红推广历史匹配详情-" + SystemConstants.ADD)
@Operation(summary = "产品网红推广历史匹配详情-" + SystemConstants.ADD, description = "产品网红推广历史匹配详情-" + SystemConstants.ADD)
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody StorePromotionProjectDetail storePromotionProjectDetail) {
        projectDetailService.save(storePromotionProjectDetail);
        return Result.ok(SystemConstants.ADD_SUCCESS);
    }

    /**
     * 编辑
     *
     * @param storePromotionProjectDetail
     * @return
     */
    @AutoLog(value = "产品网红推广历史匹配详情-" + SystemConstants.EDIT)
@Operation(summary = "产品网红推广历史匹配详情-" + SystemConstants.EDIT, description = "产品网红推广历史匹配详情-" + SystemConstants.EDIT)
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody StorePromotionProjectDetail storePromotionProjectDetail) {
        projectDetailService.updateById(storePromotionProjectDetail);
        return Result.ok(SystemConstants.EDIT_SUCCESS);
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "产品网红推广历史匹配详情-" + SystemConstants.DELETE_BY_ID)
@Operation(summary = "产品网红推广历史匹配详情-" + SystemConstants.DELETE_BY_ID, description = "产品网红推广历史匹配详情-" + SystemConstants.DELETE_BY_ID)
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        projectDetailService.removeById(id);
        return Result.ok(SystemConstants.DELETE_SUCCESS);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "产品网红推广历史匹配详情-" + SystemConstants.DELETE_BATCH)
@Operation(summary = "产品网红推广历史匹配详情-" + SystemConstants.DELETE_BATCH, description = "产品网红推广历史匹配详情-" + SystemConstants.DELETE_BATCH)
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.projectDetailService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok(SystemConstants.DELETE_BATCH_SUCCESS);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "产品网红推广历史匹配详情-" + SystemConstants.QUERY_BY_ID)
@Operation(summary = "产品网红推广历史匹配详情-" + SystemConstants.QUERY_BY_ID, description = "产品网红推广历史匹配详情-" + SystemConstants.QUERY_BY_ID)
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        StorePromotionProjectDetail storePromotionProjectDetail = projectDetailService.getById(id);
        return Result.ok(storePromotionProjectDetail);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param storePromotionProjectDetail
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StorePromotionProjectDetail storePromotionProjectDetail) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        List<StorePromotionProjectDetail> exportList = new ArrayList<>();
        String title = "产品网红历史匹配方案";

        // Step.3 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, title);
        mv.addObject(NormalExcelConstants.CLASS, StorePromotionProjectDetail.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams(title + "报表", "导出人:" + sysUser.getRealname(), title));
        mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
        return mv;
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
        return super.importExcel(request, response, StorePromotionProjectDetail.class);
    }

}
