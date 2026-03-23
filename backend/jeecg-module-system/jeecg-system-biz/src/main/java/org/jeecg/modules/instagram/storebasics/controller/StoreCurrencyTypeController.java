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
import org.jeecg.modules.instagram.storebasics.entity.StoreCurrencyType;
import org.jeecg.modules.instagram.storebasics.service.IStoreCurrencyTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

 /**
 * @Description: 货币表
 * @Author: jeecg-boot
 * @Date:   2020-10-06
 * @Version: V1.0
 */
@Slf4j
@Tag(name ="货币表")
@RestController
@RequestMapping("/storecurrencytype/storeCurrencyType")
public class StoreCurrencyTypeController extends JeecgController<StoreCurrencyType, IStoreCurrencyTypeService> {
	@Autowired
	private IStoreCurrencyTypeService storeCurrencyTypeService;
	
	/**
	 * 分页列表查询
	 *
	 * @param storeCurrencyType
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "货币表-分页列表查询")
	@Operation(summary ="货币表-分页列表查询", description = "货币表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(StoreCurrencyType storeCurrencyType,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<StoreCurrencyType> queryWrapper = QueryGenerator.initQueryWrapper(storeCurrencyType, req.getParameterMap());
		Page<StoreCurrencyType> page = new Page<StoreCurrencyType>(pageNo, pageSize);
		IPage<StoreCurrencyType> pageList = storeCurrencyTypeService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	 /**
	  * 查询所有货币信息
	  */
	 @RequestMapping(value = "/findAll", method = RequestMethod.POST)
	 public Result<?> getCurrencyType() {
		 return Result.ok(storeCurrencyTypeService.list());
	 }
	
	/**
	 * 添加
	 *
	 * @param storeCurrencyType
	 * @return
	 */
	@AutoLog(value = "货币表-添加")
	@Operation(summary ="货币表-添加", description = "货币表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody StoreCurrencyType storeCurrencyType) {
		storeCurrencyTypeService.save(storeCurrencyType);
		return Result.ok("添加成功！");
	}
	
	/**
	 * 编辑
	 *
	 * @param storeCurrencyType
	 * @return
	 */
	@AutoLog(value = "货币表-编辑")
	@Operation(summary ="货币表-编辑", description = "货币表-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody StoreCurrencyType storeCurrencyType) {
		storeCurrencyTypeService.updateById(storeCurrencyType);
		return Result.ok("编辑成功!");
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "货币表-通过id删除")
	@Operation(summary ="货币表-通过id删除", description = "货币表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		storeCurrencyTypeService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "货币表-批量删除")
	@Operation(summary ="货币表-批量删除", description = "货币表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.storeCurrencyTypeService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "货币表-通过id查询")
	@Operation(summary ="货币表-通过id查询", description = "货币表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		StoreCurrencyType storeCurrencyType = storeCurrencyTypeService.getById(id);
		return Result.ok(storeCurrencyType);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param storeCurrencyType
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, StoreCurrencyType storeCurrencyType) {
      return super.exportXls(request, storeCurrencyType, StoreCurrencyType.class, "货币表");
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
      return super.importExcel(request, response, StoreCurrencyType.class);
  }

}
