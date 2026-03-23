package org.jeecg.modules.tiktok.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.ExamConstants;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.tiktok.entity.TiktokCelebrityProductCategory;
import org.jeecg.modules.tiktok.service.ITiktokCelebrityProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

 /**
 * @Description: TikTok产品与类目关联表
 * @Author: nqr
 * @Date:   2025-10-08
 * @Version: V1.0
 */
@Slf4j
@Tag(name="TikTok产品与类目关联表")
@RestController
@RequestMapping("/tiktok/tiktokCelebrityProductCategory")
public class TiktokCelebrityProductCategoryController extends JeecgController<TiktokCelebrityProductCategory, ITiktokCelebrityProductCategoryService> {
	@Autowired
	private ITiktokCelebrityProductCategoryService tiktokCelebrityProductCategoryService;
	
	/**
	 * 分页列表查询
	 *
	 * @param tiktokCelebrityProductCategory
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "TikTok产品与类目关联表-"+ ExamConstants.PAGE_LIST_QUERY)
	@Operation(summary ="TikTok产品与类目关联表-"+ExamConstants.PAGE_LIST_QUERY, description = "TikTok产品与类目关联表-"+ExamConstants.PAGE_LIST_QUERY)
	@GetMapping(value = "/list")
	public Result<?> queryPageList(TiktokCelebrityProductCategory tiktokCelebrityProductCategory,
								  @RequestParam(name = ExamConstants.PAGE_NO, defaultValue = ExamConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                  @RequestParam(name = ExamConstants.PAGE_SIZE, defaultValue = ExamConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TiktokCelebrityProductCategory> queryWrapper = QueryGenerator.initQueryWrapper(tiktokCelebrityProductCategory, req.getParameterMap());
		Page<TiktokCelebrityProductCategory> page = new Page<TiktokCelebrityProductCategory>(pageNo, pageSize);
		IPage<TiktokCelebrityProductCategory> pageList = tiktokCelebrityProductCategoryService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param tiktokCelebrityProductCategory
	 * @return
	 */
	@AutoLog(value = "TikTok产品与类目关联表-"+ExamConstants.ADD)
	@Operation(summary ="TikTok产品与类目关联表-" +ExamConstants.ADD, description = "TikTok产品与类目关联表-" +ExamConstants.ADD)
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody TiktokCelebrityProductCategory tiktokCelebrityProductCategory) {
		tiktokCelebrityProductCategoryService.save(tiktokCelebrityProductCategory);
		return Result.ok(ExamConstants.ADD_SUCCESS);
	}
	
	/**
	 * 编辑
	 *
	 * @param tiktokCelebrityProductCategory
	 * @return
	 */
	@AutoLog(value = "TikTok产品与类目关联表-"+ExamConstants.EDIT)
	@Operation(summary ="TikTok产品与类目关联表-"+ExamConstants.EDIT, description = "TikTok产品与类目关联表-" + ExamConstants.EDIT)
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody TiktokCelebrityProductCategory tiktokCelebrityProductCategory) {
		tiktokCelebrityProductCategoryService.updateById(tiktokCelebrityProductCategory);
		return Result.ok(ExamConstants.EDIT_SUCCESS);
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "TikTok产品与类目关联表-"+ExamConstants.DELETE_BY_ID)
	@Operation(summary ="TikTok产品与类目关联表-" +ExamConstants.DELETE_BY_ID, description = "TikTok产品与类目关联表-"+ExamConstants.DELETE_BY_ID)
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		tiktokCelebrityProductCategoryService.removeById(id);
		return Result.ok(ExamConstants.DELETE_SUCCESS);
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "TikTok产品与类目关联表-"+ ExamConstants.DELETE_BATCH)
	@Operation(summary ="TikTok产品与类目关联表-" + ExamConstants.DELETE_BATCH, description = "TikTok产品与类目关联表-" + ExamConstants.DELETE_BATCH)
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tiktokCelebrityProductCategoryService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok(ExamConstants.DELETE_BATCH_SUCCESS);
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "TikTok产品与类目关联表-" + ExamConstants.QUERY_BY_ID)
	@Operation(summary ="TikTok产品与类目关联表-" +ExamConstants.QUERY_BY_ID, description = "TikTok产品与类目关联表-"+ExamConstants.QUERY_BY_ID)
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		TiktokCelebrityProductCategory tiktokCelebrityProductCategory = tiktokCelebrityProductCategoryService.getById(id);
		return Result.ok(tiktokCelebrityProductCategory);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param tiktokCelebrityProductCategory
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, TiktokCelebrityProductCategory tiktokCelebrityProductCategory) {
      return super.exportXls(request, tiktokCelebrityProductCategory, TiktokCelebrityProductCategory.class, "TikTok产品与类目关联表");
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
      return super.importExcel(request, response, TiktokCelebrityProductCategory.class);
  }

}
