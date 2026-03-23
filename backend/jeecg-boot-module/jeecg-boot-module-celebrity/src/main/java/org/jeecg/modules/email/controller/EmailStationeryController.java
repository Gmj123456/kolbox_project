package org.jeecg.modules.email.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.email.entity.EmailStationery;
import org.jeecg.modules.email.service.IEmailStationeryService;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

 /**
 * @Description: 邮箱信纸表
 * @Author: jeecg-boot
 * @Date:   2026-02-11
 * @Version: V1.0
 */
@Slf4j
@Tag(name="邮箱信纸表")
@RestController
@RequestMapping("/email/emailStationery")
public class EmailStationeryController extends JeecgController<EmailStationery, IEmailStationeryService> {
	@Autowired
	private IEmailStationeryService emailStationeryService;
	
	/**
	 * 分页列表查询
	 *
	 * @param emailStationery
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "邮箱信纸表-分页列表查询")
	@Operation(summary="邮箱信纸表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(EmailStationery emailStationery,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<EmailStationery> queryWrapper = QueryGenerator.initQueryWrapper(emailStationery, req.getParameterMap());
		Page<EmailStationery> page = new Page<EmailStationery>(pageNo, pageSize);
		IPage<EmailStationery> pageList = emailStationeryService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	 @AutoLog(value = "邮箱信纸表-列表查询")
	 @Operation(summary="邮箱信纸表-列表查询")
	 @GetMapping(value = "/listAll")
	 public Result<?> queryList(EmailStationery emailStationery) {
		 return Result.OK(emailStationeryService.list());
	 }
	/**
	 * 添加
	 *
	 * @param emailStationery
	 * @return
	 */
	@AutoLog(value = "邮箱信纸表-添加")
	@Operation(summary="邮箱信纸表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody EmailStationery emailStationery) {
		emailStationeryService.save(emailStationery);
		return Result.OK("添加成功！");
	}
	
	/**
	 * 编辑
	 *
	 * @param emailStationery
	 * @return
	 */
	@AutoLog(value = "邮箱信纸表-编辑")
	@Operation(summary="邮箱信纸表-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<?> edit(@RequestBody EmailStationery emailStationery) {
		emailStationeryService.updateById(emailStationery);
		return Result.OK("编辑成功!");
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "邮箱信纸表-通过id删除")
	@Operation(summary="邮箱信纸表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		emailStationeryService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "邮箱信纸表-批量删除")
	@Operation(summary="邮箱信纸表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.emailStationeryService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "邮箱信纸表-通过id查询")
	@Operation(summary="邮箱信纸表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		EmailStationery emailStationery = emailStationeryService.getById(id);
		return Result.OK(emailStationery);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param emailStationery
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, EmailStationery emailStationery) {
      return super.exportXls(request, emailStationery, EmailStationery.class, "邮箱信纸表");
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
      return super.importExcel(request, response, EmailStationery.class);
  }

}
