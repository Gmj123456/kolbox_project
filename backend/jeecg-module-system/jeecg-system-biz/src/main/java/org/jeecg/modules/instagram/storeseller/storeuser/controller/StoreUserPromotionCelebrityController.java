package org.jeecg.modules.instagram.storeseller.storeuser.controller;

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
import org.jeecg.modules.instagram.storeseller.storeuser.entity.StoreUserPromotionCelebrity;
import org.jeecg.modules.instagram.storeseller.storeuser.service.IStoreUserPromotionCelebrityService;
import org.jeecg.modules.instagram.storeseller.storeuser.service.IStoreUserPromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 商家产品促销网红
 * @Author: jeecg-boot
 * @Date: 2021-05-21
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "商家产品促销网红")
@RestController
@RequestMapping("/storeuserpromotioncelebrity")
public class StoreUserPromotionCelebrityController extends JeecgController<StoreUserPromotionCelebrity, IStoreUserPromotionCelebrityService> {
    @Autowired
    private IStoreUserPromotionCelebrityService storeUserPromotionCelebrityService;
    @Autowired
    private IStoreUserPromotionService storeUserPromotionService;

    /**
     * 分页列表查询
     *
     * @param storeUserPromotionCelebrity
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "商家产品促销网红-分页列表查询")
@Operation(summary = "商家产品促销网红-分页列表查询", description = "商家产品促销网红-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StoreUserPromotionCelebrity storeUserPromotionCelebrity,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<StoreUserPromotionCelebrity> queryWrapper = QueryGenerator.initQueryWrapper(storeUserPromotionCelebrity, req.getParameterMap());
        Page<StoreUserPromotionCelebrity> page = new Page<StoreUserPromotionCelebrity>(pageNo, pageSize);
        IPage<StoreUserPromotionCelebrity> pageList = storeUserPromotionCelebrityService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param storeUserPromotionCelebrity
     * @return
     */
    @AutoLog(value = "商家产品促销网红-添加")
@Operation(summary = "商家产品促销网红-添加", description = "商家产品促销网红-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody StoreUserPromotionCelebrity storeUserPromotionCelebrity) {
        storeUserPromotionCelebrityService.save(storeUserPromotionCelebrity);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param storeUserPromotionCelebrity
     * @return
     */
    @AutoLog(value = "商家产品促销网红-编辑")
@Operation(summary = "商家产品促销网红-编辑", description = "商家产品促销网红-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody StoreUserPromotionCelebrity storeUserPromotionCelebrity) {
        Integer status = storeUserPromotionCelebrity.getStatus();
        String promId = storeUserPromotionCelebrity.getPromId();
        //网红状态为选中，修改总状态为匹配完成
        if (status == YesNoStatus.YES.getCode()) {
            storeUserPromotionService.updatePromStatus(promId, YesNoStatus.YES.getCode());
        } else {
            //网红状态为未选中，查询当前需求下是否有选中状态的网红
            LambdaQueryWrapper<StoreUserPromotionCelebrity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.ne(StoreUserPromotionCelebrity::getId,storeUserPromotionCelebrity.getId());
            lambdaQueryWrapper.eq(StoreUserPromotionCelebrity::getPromId, storeUserPromotionCelebrity.getPromId());
            List<StoreUserPromotionCelebrity> celebrityList = storeUserPromotionCelebrityService.list(lambdaQueryWrapper);
            List<StoreUserPromotionCelebrity> celebrities = celebrityList.stream().filter(x -> x.getStatus() == YesNoStatus.YES.getCode()).collect(Collectors.toList());
            //没有选中状态的网红
            if (celebrities.isEmpty()) {
                //修改总状态为匹配中
                storeUserPromotionService.updatePromStatus(promId, YesNoStatus.NO.getCode());
            }
        }
        storeUserPromotionCelebrityService.updateById(storeUserPromotionCelebrity);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "商家产品促销网红-通过id删除")
@Operation(summary = "商家产品促销网红-通过id删除", description = "商家产品促销网红-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        storeUserPromotionCelebrityService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "商家产品促销网红-批量删除")
@Operation(summary = "商家产品促销网红-批量删除", description = "商家产品促销网红-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.storeUserPromotionCelebrityService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "商家产品促销网红-通过id查询")
@Operation(summary = "商家产品促销网红-通过id查询", description = "商家产品促销网红-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        StoreUserPromotionCelebrity storeUserPromotionCelebrity = storeUserPromotionCelebrityService.getById(id);
        return Result.ok(storeUserPromotionCelebrity);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param storeUserPromotionCelebrity
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StoreUserPromotionCelebrity storeUserPromotionCelebrity) {
        return super.exportXls(request, storeUserPromotionCelebrity, StoreUserPromotionCelebrity.class, "商家产品促销网红");
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
        return super.importExcel(request, response, StoreUserPromotionCelebrity.class);
    }

    /**
     * 根据方案id获取网红明细
     */
    @AutoLog(value = "商家产品促销网红-分页列表查询")
@Operation(summary = "商家产品促销网红-分页列表查询", description = "商家产品促销网红-分页列表查询")
    @GetMapping(value = "/innerTableList")
    public Result<?> innerTableList(@RequestParam(name = "promId", required = true) String promId) {
        LambdaQueryWrapper<StoreUserPromotionCelebrity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StoreUserPromotionCelebrity::getPromId, promId);
        return Result.ok(storeUserPromotionCelebrityService.list(queryWrapper));
    }
}
