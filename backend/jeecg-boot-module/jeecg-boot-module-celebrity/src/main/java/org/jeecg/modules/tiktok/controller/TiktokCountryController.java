package org.jeecg.modules.tiktok.controller;

import java.util.Arrays;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.tiktok.entity.TiktokCountry;
import org.jeecg.modules.tiktok.service.ITiktokCountryService;
import org.jeecg.common.constant.SystemConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

 /**
 * @Description: 国家表
 * @Author: dongruyang
 * @Date:   2023-10-11
 * @Version: V1.0
 */
@Slf4j
@Tag(name="tiktok国家表")
@RestController
@RequestMapping("/tiktokcountry")
public class TiktokCountryController extends JeecgController<TiktokCountry, ITiktokCountryService> {
	@Autowired
	private ITiktokCountryService tiktokCountryService;
	
	/**
	 * 分页列表查询
	 *
	 * @param tiktokCountry
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "国家表-"+ SystemConstants.PAGE_LIST_QUERY)
	@Operation(summary ="国家表-"+ SystemConstants.PAGE_LIST_QUERY, description = "国家表-"+ SystemConstants.PAGE_LIST_QUERY)
	@GetMapping(value = "/list")
	public Result<?> queryPageList(TiktokCountry tiktokCountry,
								  @RequestParam(name = SystemConstants.PAGE_NO, defaultValue = SystemConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                  @RequestParam(name = SystemConstants.PAGE_SIZE, defaultValue = SystemConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TiktokCountry> queryWrapper = QueryGenerator.initQueryWrapper(tiktokCountry, req.getParameterMap());
		Page<TiktokCountry> page = new Page<TiktokCountry>(pageNo, pageSize);
		IPage<TiktokCountry> pageList = tiktokCountryService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param tiktokCountry
	 * @return
	 */
	@AutoLog(value = "国家表-"+ SystemConstants.ADD)
	@Operation(summary ="国家表-" + SystemConstants.ADD, description = "国家表-" + SystemConstants.ADD)
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody TiktokCountry tiktokCountry) {
		tiktokCountryService.save(tiktokCountry);
		return Result.ok(SystemConstants.ADD_SUCCESS);
	}
	
	/**
	 * 编辑
	 *
	 * @param tiktokCountry
	 * @return
	 */
	@AutoLog(value = "国家表-"+ SystemConstants.EDIT)
	@Operation(summary ="国家表-"+ SystemConstants.EDIT, description = "国家表-" + SystemConstants.EDIT)
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody TiktokCountry tiktokCountry) {
		tiktokCountryService.updateById(tiktokCountry);
		return Result.ok(SystemConstants.EDIT_SUCCESS);
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "国家表-"+ SystemConstants.DELETE_BY_ID)
	@Operation(summary ="国家表-" + SystemConstants.DELETE_BY_ID, description = "国家表-"+ SystemConstants.DELETE_BY_ID)
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		tiktokCountryService.removeById(id);
		return Result.ok(SystemConstants.DELETE_SUCCESS);
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "国家表-"+ SystemConstants.DELETE_BATCH)
	@Operation(summary ="国家表-" + SystemConstants.DELETE_BATCH, description = "国家表-" + SystemConstants.DELETE_BATCH)
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tiktokCountryService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok(SystemConstants.DELETE_BATCH_SUCCESS);
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "国家表-" + SystemConstants.QUERY_BY_ID)
	@Operation(summary ="国家表-" + SystemConstants.QUERY_BY_ID, description = "国家表-"+ SystemConstants.QUERY_BY_ID)
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		TiktokCountry tiktokCountry = tiktokCountryService.getById(id);
		return Result.ok(tiktokCountry);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param tiktokCountry
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, TiktokCountry tiktokCountry) {
      return super.exportXls(request, tiktokCountry, TiktokCountry.class, "国家表");
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
      return super.importExcel(request, response, TiktokCountry.class);
  }

	 /**
	  * 查询国家列表
	  *
	  * @param
	  * @return
	  */
	 @AutoLog(value = "国家表-" + SystemConstants.QUERY_BY_ID)
	 @Operation(summary ="国家表-" + SystemConstants.QUERY_BY_ID, description = "国家表-"+ SystemConstants.QUERY_BY_ID)
	 @GetMapping(value = "/getCountryList")
	 public Result<?> getCountryList() {
		 List<TiktokCountry> countryList = tiktokCountryService.list();
		 List<TiktokCountry> collect = countryList.stream().sorted(Comparator.comparing(TiktokCountry::getSortCode)).collect(Collectors.toList());
		 return Result.ok(collect);
	 }

}
