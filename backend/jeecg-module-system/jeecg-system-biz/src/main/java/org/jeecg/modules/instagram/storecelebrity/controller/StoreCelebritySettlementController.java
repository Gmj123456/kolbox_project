package org.jeecg.modules.instagram.storecelebrity.controller;

import java.util.Arrays;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebritySettlement;
import org.jeecg.modules.instagram.storecelebrity.model.StoreCelebritySettlementModel;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebritySettlementService;

import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * @Description: 私有网红带货结算表
 * @Author: jeecg-boot
 * @Date: 2021-01-03
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "私有网红带货结算表 ")
@RestController
@RequestMapping("/storecelebritysettlement/storeCelebritySettlement")
public class StoreCelebritySettlementController extends JeecgController<StoreCelebritySettlement, IStoreCelebritySettlementService> {
    @Autowired
    private IStoreCelebritySettlementService storeCelebritySettlementService;

    /**
     * 分页列表查询
     *
     * @param storeCelebritySettlement
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "私有网红带货结算表 -分页列表查询")
@Operation(summary = "私有网红带货结算表 -分页列表查询", description = "私有网红带货结算表 -分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StoreCelebritySettlement storeCelebritySettlement,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<StoreCelebritySettlement> queryWrapper = QueryGenerator.initQueryWrapper(storeCelebritySettlement, req.getParameterMap());
        Page<StoreCelebritySettlement> page = new Page<StoreCelebritySettlement>(pageNo, pageSize);
        IPage<StoreCelebritySettlement> pageList = storeCelebritySettlementService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

	 /**
	  * 列表查询
	  *
	  * @param storeCelebritySettlementModel
	  * @param pageNo
	  * @param pageSize
	  * @param req
	  * @return
	  */
	 @AutoLog(value = "私有网红带货结算表 -列表查询")
	 @Operation(summary ="私有网红带货结算表 -列表查询", description = "私有网红带货结算表 -列表查询")
	 @GetMapping(value = "/statList")
	 public Result<?> statList(StoreCelebritySettlementModel storeCelebritySettlementModel,
                               @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                               @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                               HttpServletRequest req) {


		 if(oConvertUtils.isNotEmpty(storeCelebritySettlementModel.getMonthNew()) && storeCelebritySettlementModel.getMonthNew().equals("Invalid date")){
			 storeCelebritySettlementModel.setMonth(null);
		 }else{
		 	int months  =  storeCelebritySettlementModel.getMonthNew().startsWith("0") ?  Integer.parseInt(storeCelebritySettlementModel.getMonthNew().substring(storeCelebritySettlementModel.getMonthNew().length()-1)) : Integer.parseInt(storeCelebritySettlementModel.getMonthNew());

			 storeCelebritySettlementModel.setMonth(months);
		 }

		 List<StoreCelebritySettlementModel> list = storeCelebritySettlementService.statList(storeCelebritySettlementModel);

		 return Result.ok(list);
	 }

    /**
     * 添加
     *
     * @param storeCelebritySettlement
     * @return
     */
    @AutoLog(value = "私有网红带货结算表 -添加")
@Operation(summary = "私有网红带货结算表 -添加", description = "私有网红带货结算表 -添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody StoreCelebritySettlement storeCelebritySettlement) {
        storeCelebritySettlement.setCreateBy(getUserNameByToken());
        storeCelebritySettlement.setCreateTime(new Date());
        storeCelebritySettlementService.save(storeCelebritySettlement);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param storeCelebritySettlement
     * @return
     */
    @AutoLog(value = "私有网红带货结算表 -编辑")
@Operation(summary = "私有网红带货结算表 -编辑", description = "私有网红带货结算表 -编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody StoreCelebritySettlement storeCelebritySettlement) {
        storeCelebritySettlement.setUpdateBy(getUserNameByToken());
        storeCelebritySettlement.setUpdateTime(new Date());
        storeCelebritySettlementService.updateById(storeCelebritySettlement);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "私有网红带货结算表 -通过id删除")
@Operation(summary = "私有网红带货结算表 -通过id删除", description = "私有网红带货结算表 -通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        storeCelebritySettlementService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "私有网红带货结算表 -批量删除")
@Operation(summary = "私有网红带货结算表 -批量删除", description = "私有网红带货结算表 -批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.storeCelebritySettlementService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "私有网红带货结算表 -通过id查询")
@Operation(summary = "私有网红带货结算表 -通过id查询", description = "私有网红带货结算表 -通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        StoreCelebritySettlement storeCelebritySettlement = storeCelebritySettlementService.getById(id);
        return Result.ok(storeCelebritySettlement);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param storeCelebritySettlement
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StoreCelebritySettlement storeCelebritySettlement) {
        return super.exportXls(request, storeCelebritySettlement, StoreCelebritySettlement.class, "私有网红带货结算表 ");
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
        return super.importExcel(request, response, StoreCelebritySettlement.class);
    }


    /**
     * 通过私有网红id查询
     *
     * @param celebrityPrivateId
     * @return
     */
    @AutoLog(value = "私有网红带货结算表 -通过私有网红id查询")
@Operation(summary = "私有网红带货结算表 -通过私有网红id查询", description = "私有网红带货结算表 -通过私有网红id查询")
    @GetMapping(value = "/queryByCelebrityPrivateId")
    public Result<?> queryByCelebrityPrivateId(@RequestParam(name = "celebrityPrivateId", required = true) String celebrityPrivateId) {
        LambdaQueryWrapper<StoreCelebritySettlement> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        return Result.ok(storeCelebritySettlementService.list(lambdaQueryWrapper.eq(StoreCelebritySettlement::getCelebrityPrivateId, celebrityPrivateId).orderByDesc(StoreCelebritySettlement::getYear, StoreCelebritySettlement::getMonth)));
    }


    /**
     * 结算
     *
     * @param storeCelebritySettlement
     * @return
     */
    @AutoLog(value = "私有网红带货结算表 -结算")
@Operation(summary = "私有网红带货结算表 -结算", description = "私有网红带货结算表 -结算")
    @PutMapping(value = "/settlement")
    public Result<?> settlement(@RequestBody StoreCelebritySettlement storeCelebritySettlement) {
    	storeCelebritySettlement.setSettlementStatus(1);
    	storeCelebritySettlement.setUpdateBy(getUserNameByToken());
	 	storeCelebritySettlement.setUpdateTime(new Date());
        storeCelebritySettlementService.updateById(storeCelebritySettlement);
        return Result.ok("结算成功");
    }


    /**
     * 撤销结算
     *
     * @param storeCelebritySettlement
     * @return
     */
    @AutoLog(value = "私有网红带货结算表 -撤销结算")
@Operation(summary = "私有网红带货结算表 -撤销结算", description = "私有网红带货结算表 -撤销结算")
    @PutMapping(value = "/cancelSettlement")
    public Result<?> cancelSettlement(@RequestBody StoreCelebritySettlement storeCelebritySettlement) {
        storeCelebritySettlement.setUpdateBy(getUserNameByToken());
        storeCelebritySettlement.setUpdateTime(new Date());
        storeCelebritySettlementService.updateById(storeCelebritySettlement);
        return Result.ok("撤销成功!");
    }

}
