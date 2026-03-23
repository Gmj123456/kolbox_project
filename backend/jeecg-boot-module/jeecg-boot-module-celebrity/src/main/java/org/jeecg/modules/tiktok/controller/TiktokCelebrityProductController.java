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
import org.jeecg.modules.tiktok.entity.TiktokCelebrityProduct;
import org.jeecg.modules.tiktok.service.ITiktokCelebrityProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

 /**
 * @Description: TikTok网红带货产品信息表
 * @Author: nqr
 * @Date:   2025-10-08
 * @Version: V1.0
 */
@Slf4j
@Tag(name="TikTok网红带货产品信息表")
@RestController
@RequestMapping("/tiktok/tiktokCelebrityProduct")
public class TiktokCelebrityProductController extends JeecgController<TiktokCelebrityProduct, ITiktokCelebrityProductService> {
	@Autowired
	private ITiktokCelebrityProductService tiktokCelebrityProductService;
	
	/**
	 * 分页列表查询
	 *
	 * @param tiktokCelebrityProduct
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "TikTok网红带货产品信息表-"+ ExamConstants.PAGE_LIST_QUERY)
	@Operation(summary ="TikTok网红带货产品信息表-"+ExamConstants.PAGE_LIST_QUERY, description = "TikTok网红带货产品信息表-"+ExamConstants.PAGE_LIST_QUERY)
	@GetMapping(value = "/list")
	public Result<?> queryPageList(TiktokCelebrityProduct tiktokCelebrityProduct,
								  @RequestParam(name = ExamConstants.PAGE_NO, defaultValue = ExamConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                  @RequestParam(name = ExamConstants.PAGE_SIZE, defaultValue = ExamConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TiktokCelebrityProduct> queryWrapper = QueryGenerator.initQueryWrapper(tiktokCelebrityProduct, req.getParameterMap());
		Page<TiktokCelebrityProduct> page = new Page<TiktokCelebrityProduct>(pageNo, pageSize);
		IPage<TiktokCelebrityProduct> pageList = tiktokCelebrityProductService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param tiktokCelebrityProduct
	 * @return
	 */
	@AutoLog(value = "TikTok网红带货产品信息表-"+ExamConstants.ADD)
	@Operation(summary ="TikTok网红带货产品信息表-" +ExamConstants.ADD, description = "TikTok网红带货产品信息表-" +ExamConstants.ADD)
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody TiktokCelebrityProduct tiktokCelebrityProduct) {
		tiktokCelebrityProductService.save(tiktokCelebrityProduct);
		return Result.ok(ExamConstants.ADD_SUCCESS);
	}
	
	/**
	 * 编辑
	 *
	 * @param tiktokCelebrityProduct
	 * @return
	 */
	@AutoLog(value = "TikTok网红带货产品信息表-"+ExamConstants.EDIT)
	@Operation(summary ="TikTok网红带货产品信息表-"+ExamConstants.EDIT, description = "TikTok网红带货产品信息表-" + ExamConstants.EDIT)
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody TiktokCelebrityProduct tiktokCelebrityProduct) {
		tiktokCelebrityProductService.updateById(tiktokCelebrityProduct);
		return Result.ok(ExamConstants.EDIT_SUCCESS);
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "TikTok网红带货产品信息表-"+ExamConstants.DELETE_BY_ID)
	@Operation(summary ="TikTok网红带货产品信息表-" +ExamConstants.DELETE_BY_ID, description = "TikTok网红带货产品信息表-"+ExamConstants.DELETE_BY_ID)
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		tiktokCelebrityProductService.removeById(id);
		return Result.ok(ExamConstants.DELETE_SUCCESS);
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "TikTok网红带货产品信息表-"+ ExamConstants.DELETE_BATCH)
	@Operation(summary ="TikTok网红带货产品信息表-" + ExamConstants.DELETE_BATCH, description = "TikTok网红带货产品信息表-" + ExamConstants.DELETE_BATCH)
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tiktokCelebrityProductService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok(ExamConstants.DELETE_BATCH_SUCCESS);
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "TikTok网红带货产品信息表-" + ExamConstants.QUERY_BY_ID)
	@Operation(summary ="TikTok网红带货产品信息表-" +ExamConstants.QUERY_BY_ID, description = "TikTok网红带货产品信息表-"+ExamConstants.QUERY_BY_ID)
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		TiktokCelebrityProduct tiktokCelebrityProduct = tiktokCelebrityProductService.getById(id);
		return Result.ok(tiktokCelebrityProduct);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param tiktokCelebrityProduct
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, TiktokCelebrityProduct tiktokCelebrityProduct) {
      return super.exportXls(request, tiktokCelebrityProduct, TiktokCelebrityProduct.class, "TikTok网红带货产品信息表");
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
      return super.importExcel(request, response, TiktokCelebrityProduct.class);
  }

}
