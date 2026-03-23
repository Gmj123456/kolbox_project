package org.jeecg.modules.tiktok.controller;

import java.util.Arrays;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.SystemConstants;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.modules.tiktok.entity.TiktokTagsCategory;
import org.jeecg.modules.tiktok.service.ITiktokTagsCategoryService;
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
 * @Description: 标签类目表
 * @Author: dongruyang
 * @Date:   2024-07-18
 * @Version: V1.0
 */
@Slf4j
@Tag(name="标签类目表")
@RestController
@RequestMapping("/tiktokTagsCategory")
public class TiktokTagsCategoryController extends JeecgController<TiktokTagsCategory, ITiktokTagsCategoryService> {
	@Autowired
	private ITiktokTagsCategoryService tiktokTagsCategoryService;
	
	/**
	 * 分页列表查询
	 *
	 * @param tiktokTagsCategory
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "标签类目表-"+ SystemConstants.PAGE_LIST_QUERY)
	@Operation(summary ="标签类目表-"+ SystemConstants.PAGE_LIST_QUERY, description = "标签类目表-"+ SystemConstants.PAGE_LIST_QUERY)
	@GetMapping(value = "/list")
	public Result<?> queryPageList(TiktokTagsCategory tiktokTagsCategory,
								  @RequestParam(name = SystemConstants.PAGE_NO, defaultValue = SystemConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                  @RequestParam(name = SystemConstants.PAGE_SIZE, defaultValue = SystemConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TiktokTagsCategory> queryWrapper = QueryGenerator.initQueryWrapper(tiktokTagsCategory, req.getParameterMap());
		Page<TiktokTagsCategory> page = new Page<TiktokTagsCategory>(pageNo, pageSize);
		IPage<TiktokTagsCategory> pageList = tiktokTagsCategoryService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param tiktokTagsCategory
	 * @return
	 */
	@AutoLog(value = "标签类目表-"+ SystemConstants.ADD)
	@Operation(summary ="标签类目表-" + SystemConstants.ADD, description = "标签类目表-" + SystemConstants.ADD)
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody TiktokTagsCategory tiktokTagsCategory) {
		tiktokTagsCategoryService.save(tiktokTagsCategory);
		return Result.ok(SystemConstants.ADD_SUCCESS);
	}
	
	/**
	 * 编辑
	 *
	 * @param tiktokTagsCategory
	 * @return
	 */
	@AutoLog(value = "标签类目表-"+ SystemConstants.EDIT)
	@Operation(summary ="标签类目表-"+ SystemConstants.EDIT, description = "标签类目表-" + SystemConstants.EDIT)
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody TiktokTagsCategory tiktokTagsCategory) {
		tiktokTagsCategoryService.updateById(tiktokTagsCategory);
		return Result.ok(SystemConstants.EDIT_SUCCESS);
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "标签类目表-"+ SystemConstants.DELETE_BY_ID)
	@Operation(summary ="标签类目表-" + SystemConstants.DELETE_BY_ID, description = "标签类目表-"+ SystemConstants.DELETE_BY_ID)
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		tiktokTagsCategoryService.removeById(id);
		return Result.ok(SystemConstants.DELETE_SUCCESS);
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "标签类目表-"+ SystemConstants.DELETE_BATCH)
	@Operation(summary ="标签类目表-" + SystemConstants.DELETE_BATCH, description = "标签类目表-" + SystemConstants.DELETE_BATCH)
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tiktokTagsCategoryService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok(SystemConstants.DELETE_BATCH_SUCCESS);
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "标签类目表-" + SystemConstants.QUERY_BY_ID)
	@Operation(summary ="标签类目表-" + SystemConstants.QUERY_BY_ID, description = "标签类目表-"+ SystemConstants.QUERY_BY_ID)
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		TiktokTagsCategory tiktokTagsCategory = tiktokTagsCategoryService.getById(id);
		return Result.ok(tiktokTagsCategory);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param tiktokTagsCategory
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, TiktokTagsCategory tiktokTagsCategory) {
      return super.exportXls(request, tiktokTagsCategory, TiktokTagsCategory.class, "标签类目表");
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
      return super.importExcel(request, response, TiktokTagsCategory.class);
  }

}
