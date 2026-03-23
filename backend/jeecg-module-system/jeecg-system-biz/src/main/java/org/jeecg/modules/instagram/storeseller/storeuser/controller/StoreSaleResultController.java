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
import org.jeecg.common.util.PageUtil;
import org.jeecg.modules.instagram.storeseller.storeuser.entity.StoreSaleResult;
import org.jeecg.modules.instagram.storeseller.storeuser.model.StoreSaleResultModel;
import org.jeecg.modules.instagram.storeseller.storeuser.service.IStoreSaleResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

 /**
 * @Description: storeSaleResult
 * @Author: jeecg-boot
 * @Date:   2020-10-16
 * @Version: V1.0
 */
@Tag(name ="storeSaleResult")
@RestController
@RequestMapping("/storesaleresult/storeSaleResult")
@Slf4j
public class StoreSaleResultController extends JeecgController<StoreSaleResult, IStoreSaleResultService> {
	@Autowired
	private IStoreSaleResultService storeSaleResultService;


	 /**
	  * 分页列表查询
	  *
	  * @param storeSaleResultModel
	  * @param pageNo
	  * @param pageSize
	  * @return
	  */
	 @AutoLog(value = "战报列表-分页列表查询")
	 @Operation(summary = "战报列表-分页列表查询", description = "战报列表-分页列表查询")
	 @GetMapping(value = "/parentList")
	 public Result<?> parentList(StoreSaleResultModel storeSaleResultModel,
								 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
								 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
								 HttpServletRequest req) {
		 IPage<StoreSaleResult> pageList = storeSaleResultService.parentList(PageUtil.getOrderItems(pageNo, pageSize, req), storeSaleResultModel);
		 return Result.ok(pageList);
	 }


	/**
	 * 分页列表查询
	 *
	 * @param storeSaleResult
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "storeSaleResult-分页列表查询")
	@Operation(summary ="storeSaleResult-分页列表查询", description = "storeSaleResult-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(StoreSaleResult storeSaleResult,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<StoreSaleResult> queryWrapper = QueryGenerator.initQueryWrapper(storeSaleResult, req.getParameterMap());
		Page<StoreSaleResult> page = new Page<StoreSaleResult>(pageNo, pageSize);
		IPage<StoreSaleResult> pageList = storeSaleResultService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param storeSaleResult
	 * @return
	 */
	@AutoLog(value = "storeSaleResult-添加")
	@Operation(summary ="storeSaleResult-添加", description = "storeSaleResult-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody StoreSaleResult storeSaleResult) {
		storeSaleResultService.save(storeSaleResult);
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param storeSaleResult
	 * @return
	 */
	@AutoLog(value = "storeSaleResult-编辑")
	@Operation(summary ="storeSaleResult-编辑", description = "storeSaleResult-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody StoreSaleResult storeSaleResult) {
		storeSaleResultService.updateById(storeSaleResult);
		return Result.ok("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "storeSaleResult-通过id删除")
	@Operation(summary ="storeSaleResult-通过id删除", description = "storeSaleResult-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		storeSaleResultService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "storeSaleResult-批量删除")
	@Operation(summary ="storeSaleResult-批量删除", description = "storeSaleResult-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.storeSaleResultService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "storeSaleResult-通过id查询")
	@Operation(summary ="storeSaleResult-通过id查询", description = "storeSaleResult-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		StoreSaleResult storeSaleResult = storeSaleResultService.getById(id);
		if(storeSaleResult==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(storeSaleResult);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param storeSaleResult
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StoreSaleResult storeSaleResult) {
        return super.exportXls(request, storeSaleResult, StoreSaleResult.class, "storeSaleResult");
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
        return super.importExcel(request, response, StoreSaleResult.class);
    }

}
