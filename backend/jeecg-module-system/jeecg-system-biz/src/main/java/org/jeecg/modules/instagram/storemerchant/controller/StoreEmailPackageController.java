package org.jeecg.modules.instagram.storemerchant.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import org.jeecg.modules.instagram.storemerchant.entity.StoreEmailPackage;
import org.jeecg.modules.instagram.storemerchant.service.IStoreEmailPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 流量包表
 * @Author: jeecg-boot
 * @Date: 2020-10-01
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "流量包表")
@RestController
@RequestMapping("/storeemailpackage/storeEmailPackage")
public class StoreEmailPackageController extends JeecgController<StoreEmailPackage, IStoreEmailPackageService> {
    @Autowired
    private IStoreEmailPackageService storeEmailPackageService;

    /**
     * 分页列表查询
     *
     * @param storeEmailPackage
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "流量包表-分页列表查询")
@Operation(summary = "流量包表-分页列表查询", description = "流量包表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StoreEmailPackage storeEmailPackage,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<StoreEmailPackage> queryWrapper = QueryGenerator.initQueryWrapper(storeEmailPackage, req.getParameterMap());
        String username = getUserNameByToken();
        if (!"admin".equals(username)) {
            queryWrapper.gt("package_price", 0);
        }
        Page<StoreEmailPackage> page = new Page<StoreEmailPackage>(pageNo, pageSize);
        IPage<StoreEmailPackage> pageList = storeEmailPackageService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param storeEmailPackage
     * @return
     */
    @AutoLog(value = "流量包表-添加")
@Operation(summary = "流量包表-添加", description = "流量包表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody StoreEmailPackage storeEmailPackage) {
        if (storeEmailPackage.getPackageType().equals(0)) {
            LambdaQueryWrapper<StoreEmailPackage> lambdaEmailPackageQueryWrapper = new LambdaQueryWrapper<>();
            lambdaEmailPackageQueryWrapper.eq(StoreEmailPackage::getPackageType, 0);
            List<StoreEmailPackage> storeEmailPackages = storeEmailPackageService.list(lambdaEmailPackageQueryWrapper);
            if (null != storeEmailPackages && storeEmailPackages.size() > 0) {
                return Result.error("已经存在邀请赠送流量表，请勿重复增加！");
            }
        }
        storeEmailPackageService.save(storeEmailPackage);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param storeEmailPackage
     * @return
     */
    @AutoLog(value = "流量包表-编辑")
@Operation(summary = "流量包表-编辑", description = "流量包表-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody StoreEmailPackage storeEmailPackage) {
        storeEmailPackageService.updateById(storeEmailPackage);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "流量包表-通过id删除")
@Operation(summary = "流量包表-通过id删除", description = "流量包表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        storeEmailPackageService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "流量包表-批量删除")
@Operation(summary = "流量包表-批量删除", description = "流量包表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.storeEmailPackageService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "流量包表-通过id查询")
@Operation(summary = "流量包表-通过id查询", description = "流量包表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        StoreEmailPackage storeEmailPackage = storeEmailPackageService.getById(id);
        return Result.ok(storeEmailPackage);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param storeEmailPackage
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StoreEmailPackage storeEmailPackage) {
        return super.exportXls(request, storeEmailPackage, StoreEmailPackage.class, "流量包表");
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
        return super.importExcel(request, response, StoreEmailPackage.class);
    }

    /**
     * 获取所有
     *
     * @param
     * @author zhoushen
     */
    @AutoLog(value = "流量包表-获取所有")
@Operation(summary = "流量包表-获取所有", description = "流量包表-获取所有")
    @GetMapping(value = "/queryAll")
    public Result<?> queryAll(StoreEmailPackage storeEmailPackage, HttpServletRequest request) {
        QueryWrapper<StoreEmailPackage> queryWrapper = QueryGenerator.initQueryWrapper(storeEmailPackage, request.getParameterMap());

        return Result.ok(storeEmailPackageService.list(queryWrapper));
    }

    /**
     * 根据ID获取流量包数量
     *
     * @param
     * @author zhoushen
     */
    @AutoLog(value = "流量包表-根据ID获取流量包数量")
@Operation(summary = "流量包表-根据ID获取流量包数量", description = "流量包表-根据ID获取流量包数量")
    @GetMapping(value = "/queryPackageNumById")
    public Result<?> queryPackageNumById(@RequestParam(name = "id", required = true) String id) {
        StoreEmailPackage storeEmailPackage = storeEmailPackageService.getById(id);
        if (storeEmailPackage != null) {
            return Result.ok(storeEmailPackage);
        }
        return Result.error("查询失败");
    }

}
