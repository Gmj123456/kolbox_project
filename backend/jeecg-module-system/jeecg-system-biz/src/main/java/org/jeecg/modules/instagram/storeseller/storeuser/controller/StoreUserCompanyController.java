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
import org.jeecg.modules.instagram.storeseller.storeuser.entity.StoreUserCompany;
import org.jeecg.modules.instagram.storeseller.storeuser.service.IStoreUserCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Description: 商家公司信息表
 * @Author: jeecg-boot
 * @Date: 2021-02-06
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "商家公司信息表")
@RestController
@RequestMapping("/storeusercompany/storeUserCompany")
public class StoreUserCompanyController extends JeecgController<StoreUserCompany, IStoreUserCompanyService> {
    @Autowired
    private IStoreUserCompanyService storeUserCompanyService;

    /**
     * 分页列表查询
     *
     * @param storeUserCompany
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "商家公司信息表-分页列表查询")
@Operation(summary = "商家公司信息表-分页列表查询", description = "商家公司信息表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StoreUserCompany storeUserCompany,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<StoreUserCompany> queryWrapper = QueryGenerator.initQueryWrapper(storeUserCompany, req.getParameterMap());
        Page<StoreUserCompany> page = new Page<StoreUserCompany>(pageNo, pageSize);
        IPage<StoreUserCompany> pageList = storeUserCompanyService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param storeUserCompany
     * @return
     */
    @AutoLog(value = "商家公司信息表-添加")
@Operation(summary = "商家公司信息表-添加", description = "商家公司信息表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody StoreUserCompany storeUserCompany) {
        storeUserCompany.setUserId(getUserIdByToken());
        storeUserCompanyService.save(storeUserCompany);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param storeUserCompany
     * @return
     */
    @AutoLog(value = "商家公司信息表-编辑")
@Operation(summary = "商家公司信息表-编辑", description = "商家公司信息表-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody StoreUserCompany storeUserCompany) {
        storeUserCompanyService.updateById(storeUserCompany);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "商家公司信息表-通过id删除")
@Operation(summary = "商家公司信息表-通过id删除", description = "商家公司信息表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        storeUserCompanyService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "商家公司信息表-批量删除")
@Operation(summary = "商家公司信息表-批量删除", description = "商家公司信息表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.storeUserCompanyService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "商家公司信息表-通过id查询")
@Operation(summary = "商家公司信息表-通过id查询", description = "商家公司信息表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        StoreUserCompany storeUserCompany = storeUserCompanyService.getById(id);
        return Result.ok(storeUserCompany);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param storeUserCompany
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StoreUserCompany storeUserCompany) {
        return super.exportXls(request, storeUserCompany, StoreUserCompany.class, "商家公司信息表");
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
        return super.importExcel(request, response, StoreUserCompany.class);
    }


    /**
     * 查询当前用户公司信息
     *
     * @param
     * @return
     */
    @AutoLog(value = "商家公司信息表-查询当前用户公司信息")
@Operation(summary = "商家公司信息表-查询当前用户公司信息", description = "商家公司信息表-查询当前用户公司信息")
    @GetMapping(value = "/queryPresentCompanyInfo")
    public Result<?> queryPresentCompanyInfo() {
	 	String userId = getUserIdByToken();
        StoreUserCompany storeUserCompany = storeUserCompanyService.getByUserId(userId);
        return Result.ok(storeUserCompany);
    }
}
