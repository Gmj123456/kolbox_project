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
import org.jeecg.modules.instagram.storebasics.entity.CommodityServiceFee;
import org.jeecg.modules.instagram.storebasics.service.ICommodityServiceFeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Description: 商品服务费
 * @Author: jeecg-boot
 * @Date: 2021-04-13
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "商品服务费")
@RestController
@RequestMapping("/commodityservicefee")
public class CommodityServiceFeeController extends JeecgController<CommodityServiceFee, ICommodityServiceFeeService> {
    @Autowired
    private ICommodityServiceFeeService commodityServiceFeeService;

    /**
     * 分页列表查询
     *
     * @param commodityServiceFee
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "商品服务费-分页列表查询")
@Operation(summary = "商品服务费-分页列表查询", description = "商品服务费-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(CommodityServiceFee commodityServiceFee,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<CommodityServiceFee> queryWrapper = QueryGenerator.initQueryWrapper(commodityServiceFee, req.getParameterMap());
        Page<CommodityServiceFee> page = new Page<CommodityServiceFee>(pageNo, pageSize);
        IPage<CommodityServiceFee> pageList = commodityServiceFeeService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param commodityServiceFee
     * @return
     */
    @AutoLog(value = "商品服务费-添加")
@Operation(summary = "商品服务费-添加", description = "商品服务费-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody CommodityServiceFee commodityServiceFee) {
        commodityServiceFeeService.save(commodityServiceFee);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param commodityServiceFee
     * @return
     */
    @AutoLog(value = "商品服务费-编辑")
@Operation(summary = "商品服务费-编辑", description = "商品服务费-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody CommodityServiceFee commodityServiceFee) {
        commodityServiceFeeService.updateById(commodityServiceFee);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "商品服务费-通过id删除")
@Operation(summary = "商品服务费-通过id删除", description = "商品服务费-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        commodityServiceFeeService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "商品服务费-批量删除")
@Operation(summary = "商品服务费-批量删除", description = "商品服务费-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.commodityServiceFeeService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "商品服务费-通过id查询")
@Operation(summary = "商品服务费-通过id查询", description = "商品服务费-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        CommodityServiceFee commodityServiceFee = commodityServiceFeeService.getById(id);
        return Result.ok(commodityServiceFee);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param commodityServiceFee
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, CommodityServiceFee commodityServiceFee) {
        return super.exportXls(request, commodityServiceFee, CommodityServiceFee.class, "商品服务费");
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
        return super.importExcel(request, response, CommodityServiceFee.class);
    }

}
