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
import org.jeecg.modules.instagram.storebasics.entity.Country;
import org.jeecg.modules.instagram.storebasics.service.ICountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

 /**
 * @Description: 国家表
 * @Author: jeecg-boot
 * @Date:   2021-01-18
 * @Version: V1.0
 */
@Slf4j
@Tag(name ="国家表")
@RestController
@RequestMapping("/country/country")
public class CountryController extends JeecgController<Country, ICountryService> {
	@Autowired
	private ICountryService countryService;
	
	/**
	 * 分页列表查询
	 *
	 * @param country
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "国家表-分页列表查询")
	@Operation(summary ="国家表-分页列表查询", description = "国家表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(Country country,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<Country> queryWrapper = QueryGenerator.initQueryWrapper(country, req.getParameterMap());
		Page<Country> page = new Page<Country>(pageNo, pageSize);
		IPage<Country> pageList = countryService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param country
	 * @return
	 */
	@AutoLog(value = "国家表-添加")
	@Operation(summary ="国家表-添加", description = "国家表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody Country country) {
		countryService.save(country);
		return Result.ok("添加成功！");
	}
	
	/**
	 * 编辑
	 *
	 * @param country
	 * @return
	 */
	@AutoLog(value = "国家表-编辑")
	@Operation(summary ="国家表-编辑", description = "国家表-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody Country country) {
		countryService.updateById(country);
		return Result.ok("编辑成功!");
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "国家表-通过id删除")
	@Operation(summary ="国家表-通过id删除", description = "国家表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		countryService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "国家表-批量删除")
	@Operation(summary ="国家表-批量删除", description = "国家表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.countryService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "国家表-通过id查询")
	@Operation(summary ="国家表-通过id查询", description = "国家表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		Country country = countryService.getById(id);
		return Result.ok(country);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param country
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, Country country) {
      return super.exportXls(request, country, Country.class, "国家表");
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
      return super.importExcel(request, response, Country.class);
  }


	 /**
	  * 通过英文名称查询国家查询
	  *
	  * @param countryEnName
	  * @return
	  */
	 @AutoLog(value = "国家表-通过英文名称查询国家查询")
	 @Operation(summary ="国家表-通过英文名称查询国家查询", description = "国家表-通过英文名称查询国家查询")
	 @GetMapping(value = "/queryByCountryEnName")
	 public Result<?> queryByCountryEnName(@RequestParam(name="countryEnName",required=true) String countryEnName) {
		 Country country = countryService.queryByCountryEnName(countryEnName);
		 return Result.ok(country);
	 }




}
