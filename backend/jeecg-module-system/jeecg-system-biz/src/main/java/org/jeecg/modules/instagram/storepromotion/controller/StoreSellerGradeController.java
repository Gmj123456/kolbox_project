package org.jeecg.modules.instagram.storepromotion.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.enums.YesNoStatus;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.instagram.storepromotion.entity.StoreSellerGrade;
import org.jeecg.modules.instagram.storepromotion.service.IStoreSellerGradeService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 商家等级表
 * @Author: jeecg-boot
 * @Date: 2020-09-29
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "商家等级表")
@RestController
@RequestMapping("/storesellergrade/storeSellerGrade")
public class StoreSellerGradeController extends JeecgController<StoreSellerGrade, IStoreSellerGradeService> {
    @Autowired
    private IStoreSellerGradeService storeSellerGradeService;
    @Autowired
    private ISysUserService sysUserService;

    /**
     * 分页列表查询
     *
     * @param storeSellerGrade
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "商家等级表-分页列表查询")
@Operation(summary = "商家等级表-分页列表查询", description = "商家等级表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StoreSellerGrade storeSellerGrade,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<StoreSellerGrade> queryWrapper = QueryGenerator.initQueryWrapper(storeSellerGrade, req.getParameterMap());
        Page<StoreSellerGrade> page = new Page<StoreSellerGrade>(pageNo, pageSize);
        IPage<StoreSellerGrade> pageList = storeSellerGradeService.page(page, queryWrapper);
//        List<StoreSellerGrade> records = pageList.getRecords();
//        List<StoreSellerGrade> collect = records.stream().filter(x -> x.getGradeLever() > 0).collect(Collectors.toList());
//        pageList.setRecords(collect);
        return Result.ok(pageList);
    }

    //查询等级列表
    @GetMapping(value = "/queryGradeList")
    public Result<?> queryGradeList() {
        LambdaQueryWrapper<StoreSellerGrade> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreSellerGrade::getIsEnabled, YesNoStatus.YES.getCode());//有效标识
        lambdaQueryWrapper.last("order by create_time desc");
        List<StoreSellerGrade> list = storeSellerGradeService.list(lambdaQueryWrapper);
        return Result.ok(list);
    }

    @GetMapping(value = "/queryList")
    public Result<?> queryList() {
        List<StoreSellerGrade> list = storeSellerGradeService.getList();
        return Result.ok(list);
    }

    @GetMapping(value = "/oneGradeList")
    public Result<?> oneGradeList(StoreSellerGrade storeSellerGrade) {
        LambdaQueryWrapper<StoreSellerGrade> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreSellerGrade::getGradeLever, storeSellerGrade.getGradeLever());
        return Result.ok(storeSellerGradeService.list(lambdaQueryWrapper));
    }

    @GetMapping(value = "/updateGradeList")
    public Result<?> updateGradeList(StoreSellerGrade storeSellerGrade) {
        String userId = getUserIdByToken();
        SysUser sysUser = sysUserService.getById(userId);
        StoreSellerGrade sellerGrade = storeSellerGradeService.getById(sysUser.getGradeId());
        return Result.ok(storeSellerGradeService.updateGradeList(sellerGrade));
    }

    /**
     * 添加
     *
     * @param storeSellerGrade
     * @return
     */
    @AutoLog(value = "商家等级表-添加")
@Operation(summary = "商家等级表-添加", description = "商家等级表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody StoreSellerGrade storeSellerGrade) {
        storeSellerGradeService.save(storeSellerGrade);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param storeSellerGrade
     * @return
     */
    @AutoLog(value = "商家等级表-编辑")
@Operation(summary = "商家等级表-编辑", description = "商家等级表-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody StoreSellerGrade storeSellerGrade) {
        storeSellerGradeService.updateById(storeSellerGrade);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "商家等级表-通过id删除")
@Operation(summary = "商家等级表-通过id删除", description = "商家等级表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        storeSellerGradeService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "商家等级表-批量删除")
@Operation(summary = "商家等级表-批量删除", description = "商家等级表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.storeSellerGradeService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "商家等级表-通过id查询")
@Operation(summary = "商家等级表-通过id查询", description = "商家等级表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        StoreSellerGrade storeSellerGrade = storeSellerGradeService.getById(id);
        return Result.ok(storeSellerGrade);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param storeSellerGrade
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StoreSellerGrade storeSellerGrade) {
        return super.exportXls(request, storeSellerGrade, StoreSellerGrade.class, "商家等级表");
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
        return super.importExcel(request, response, StoreSellerGrade.class);
    }

}
