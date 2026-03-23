package org.jeecg.modules.instagram.history.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.ExamConstants;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.history.entity.KolHistoryCelebrity;
import org.jeecg.modules.instagram.history.service.IKolHistoryCelebrityService;
import java.util.Date;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.constant.SystemConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

 /**
 * @Description: 历史提报网红主表
 * @Author: jeecg-boot
 * @Date:   2025-11-26
 * @Version: V1.0
 */
@Slf4j
@RestController
@RequestMapping("/history/kolHistoryCelebrity")
public class KolHistoryCelebrityController extends JeecgController<KolHistoryCelebrity, IKolHistoryCelebrityService> {
	@Autowired
	private IKolHistoryCelebrityService kolHistoryCelebrityService;
	
	/**
	 * 分页列表查询
	 *
	 * @param kolHistoryCelebrity
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "历史提报网红主表-"+ExamConstants.PAGE_LIST_QUERY)
	@Operation(summary ="历史提报网红主表-"+ExamConstants.PAGE_LIST_QUERY,description ="历史提报网红主表-"+ExamConstants.PAGE_LIST_QUERY)
	@GetMapping(value = "/list")
	public Result<?> queryPageList(KolHistoryCelebrity kolHistoryCelebrity,
								  @RequestParam(name = ExamConstants.PAGE_NO, defaultValue = ExamConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                  @RequestParam(name = ExamConstants.PAGE_SIZE, defaultValue = ExamConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
								   HttpServletRequest req) {
		Page<KolHistoryCelebrity> page = new Page<>(pageNo, pageSize);
		IPage<KolHistoryCelebrity> pageList = kolHistoryCelebrityService.pageList(page, kolHistoryCelebrity);
		return Result.ok(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param kolHistoryCelebrity
	 * @return
	 */
	@AutoLog(value = "历史提报网红主表-"+ ExamConstants.ADD)
	@Operation(summary ="历史提报网红主表-" +ExamConstants.ADD,description ="历史提报网红主表-" +ExamConstants.ADD)
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody KolHistoryCelebrity kolHistoryCelebrity) {
		kolHistoryCelebrityService.save(kolHistoryCelebrity);
		return Result.ok(ExamConstants.ADD_SUCCESS);
	}
	
	/**
	 * 编辑
	 *
	 * @param kolHistoryCelebrity
	 * @return
	 */
	@AutoLog(value = "历史提报网红主表-"+ExamConstants.EDIT)
	@Operation(summary ="历史提报网红主表-"+ExamConstants.EDIT,description ="历史提报网红主表-" + ExamConstants.EDIT)
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody KolHistoryCelebrity kolHistoryCelebrity) {
		kolHistoryCelebrityService.updateById(kolHistoryCelebrity);
		return Result.ok(ExamConstants.EDIT_SUCCESS);
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "历史提报网红主表-"+ExamConstants.DELETE_BY_ID)
	@Operation(summary ="历史提报网红主表-" +ExamConstants.DELETE_BY_ID,description ="历史提报网红主表-"+ExamConstants.DELETE_BY_ID)
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		kolHistoryCelebrityService.removeById(id);
		return Result.ok(ExamConstants.DELETE_SUCCESS);
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "历史提报网红主表-"+ ExamConstants.DELETE_BATCH)
	@Operation(summary ="历史提报网红主表-" + ExamConstants.DELETE_BATCH,description ="历史提报网红主表-" + ExamConstants.DELETE_BATCH)
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.kolHistoryCelebrityService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok(ExamConstants.DELETE_BATCH_SUCCESS);
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "历史提报网红主表-" + ExamConstants.QUERY_BY_ID)
	@Operation(summary ="历史提报网红主表-" +ExamConstants.QUERY_BY_ID,description ="历史提报网红主表-"+ExamConstants.QUERY_BY_ID)
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		KolHistoryCelebrity kolHistoryCelebrity = kolHistoryCelebrityService.getById(id);
		return Result.ok(kolHistoryCelebrity);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param kolHistoryCelebrity
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, KolHistoryCelebrity kolHistoryCelebrity) {
      return super.exportXls(request, kolHistoryCelebrity, KolHistoryCelebrity.class, "历史提报网红主表");
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
      return super.importExcel(request, response, KolHistoryCelebrity.class);
  }

}
