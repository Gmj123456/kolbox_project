package org.jeecg.modules.instagram.storecelebrity.controller;

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
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivateAttributeRelation;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityPrivateAttributeRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

 /**
 * @Description: 私有网红社媒属性关联表
 * @Author: jeecg-boot
 * @Date:   2025-07-24
 * @Version: V1.0
 */
@Slf4j
@Tag(name ="私有网红社媒属性关联表")
@RestController
@RequestMapping("/privateAttributeRelation")
public class StoreCelebrityPrivateAttributeRelationController extends JeecgController<StoreCelebrityPrivateAttributeRelation, IStoreCelebrityPrivateAttributeRelationService> {
	@Autowired
	private IStoreCelebrityPrivateAttributeRelationService privateAttributeRelationService;
	
	/**
	 * 分页列表查询
	 *
	 * @param storeCelebrityPrivateAttributeRelation
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "私有网红社媒属性关联表-"+ ExamConstants.PAGE_LIST_QUERY)
	@Operation(summary ="私有网红社媒属性关联表-"+ExamConstants.PAGE_LIST_QUERY, description = "私有网红社媒属性关联表-"+ExamConstants.PAGE_LIST_QUERY)
	@GetMapping(value = "/list")
	public Result<?> queryPageList(StoreCelebrityPrivateAttributeRelation storeCelebrityPrivateAttributeRelation,
								  @RequestParam(name = ExamConstants.PAGE_NO, defaultValue = ExamConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                  @RequestParam(name = ExamConstants.PAGE_SIZE, defaultValue = ExamConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<StoreCelebrityPrivateAttributeRelation> queryWrapper = QueryGenerator.initQueryWrapper(storeCelebrityPrivateAttributeRelation, req.getParameterMap());
		Page<StoreCelebrityPrivateAttributeRelation> page = new Page<StoreCelebrityPrivateAttributeRelation>(pageNo, pageSize);
		IPage<StoreCelebrityPrivateAttributeRelation> pageList = privateAttributeRelationService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param storeCelebrityPrivateAttributeRelation
	 * @return
	 */
	@AutoLog(value = "私有网红社媒属性关联表-"+ExamConstants.ADD)
	@Operation(summary ="私有网红社媒属性关联表-" +ExamConstants.ADD, description = "私有网红社媒属性关联表-" +ExamConstants.ADD)
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody StoreCelebrityPrivateAttributeRelation storeCelebrityPrivateAttributeRelation) {
		privateAttributeRelationService.save(storeCelebrityPrivateAttributeRelation);
		return Result.ok(ExamConstants.ADD_SUCCESS);
	}
	
	/**
	 * 编辑
	 *
	 * @param storeCelebrityPrivateAttributeRelation
	 * @return
	 */
	@AutoLog(value = "私有网红社媒属性关联表-"+ExamConstants.EDIT)
	@Operation(summary ="私有网红社媒属性关联表-"+ExamConstants.EDIT, description = "私有网红社媒属性关联表-" + ExamConstants.EDIT)
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody StoreCelebrityPrivateAttributeRelation storeCelebrityPrivateAttributeRelation) {
		privateAttributeRelationService.updateById(storeCelebrityPrivateAttributeRelation);
		return Result.ok(ExamConstants.EDIT_SUCCESS);
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "私有网红社媒属性关联表-"+ExamConstants.DELETE_BY_ID)
	@Operation(summary ="私有网红社媒属性关联表-" +ExamConstants.DELETE_BY_ID, description = "私有网红社媒属性关联表-"+ExamConstants.DELETE_BY_ID)
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		privateAttributeRelationService.removeById(id);
		return Result.ok(ExamConstants.DELETE_SUCCESS);
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "私有网红社媒属性关联表-"+ ExamConstants.DELETE_BATCH)
	@Operation(summary ="私有网红社媒属性关联表-" + ExamConstants.DELETE_BATCH, description = "私有网红社媒属性关联表-" + ExamConstants.DELETE_BATCH)
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.privateAttributeRelationService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok(ExamConstants.DELETE_BATCH_SUCCESS);
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "私有网红社媒属性关联表-" + ExamConstants.QUERY_BY_ID)
	@Operation(summary ="私有网红社媒属性关联表-" +ExamConstants.QUERY_BY_ID, description = "私有网红社媒属性关联表-"+ExamConstants.QUERY_BY_ID)
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		StoreCelebrityPrivateAttributeRelation storeCelebrityPrivateAttributeRelation = privateAttributeRelationService.getById(id);
		return Result.ok(storeCelebrityPrivateAttributeRelation);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param storeCelebrityPrivateAttributeRelation
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, StoreCelebrityPrivateAttributeRelation storeCelebrityPrivateAttributeRelation) {
      return super.exportXls(request, storeCelebrityPrivateAttributeRelation, StoreCelebrityPrivateAttributeRelation.class, "私有网红社媒属性关联表");
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
      return super.importExcel(request, response, StoreCelebrityPrivateAttributeRelation.class);
  }

}
