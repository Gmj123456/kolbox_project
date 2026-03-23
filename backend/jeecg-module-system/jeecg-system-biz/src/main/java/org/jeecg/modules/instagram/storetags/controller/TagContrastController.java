package org.jeecg.modules.instagram.storetags.controller;

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
import org.jeecg.modules.instagram.storetags.entity.TagContrast;
import org.jeecg.modules.instagram.storetags.service.ITagContrastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

 /**
 * @Description: 标签
 * @Author: jeecg-boot
 * @Date:   2021-01-18
 * @Version: V1.0
 */
@Slf4j
@Tag(name ="标签")
@RestController
@RequestMapping("/tagcontrast/tagContrast")
public class TagContrastController extends JeecgController<TagContrast, ITagContrastService> {
	@Autowired
	private ITagContrastService tagContrastService;
	
	/**
	 * 分页列表查询
	 *
	 * @param tagContrast
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "标签列表")
	@Operation(summary ="标签-分页列表查询", description = "标签-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(TagContrast tagContrast,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TagContrast> queryWrapper = QueryGenerator.initQueryWrapper(tagContrast, req.getParameterMap());
		Page<TagContrast> page = new Page<TagContrast>(pageNo, pageSize);
		IPage<TagContrast> pageList = tagContrastService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param tagContrast
	 * @return
	 */
	@AutoLog(value = "标签-添加")
	@Operation(summary ="标签-添加", description = "标签-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody TagContrast tagContrast) {
		tagContrastService.save(tagContrast);
		return Result.ok("添加成功！");
	}
	
	/**
	 * 编辑
	 *
	 * @param tagContrast
	 * @return
	 */
	@AutoLog(value = "标签-编辑")
	@Operation(summary ="标签-编辑", description = "标签-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody TagContrast tagContrast) {
		tagContrastService.updateById(tagContrast);
		return Result.ok("编辑成功!");
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "标签-通过id删除")
	@Operation(summary ="标签-通过id删除", description = "标签-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		tagContrastService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "标签-批量删除")
	@Operation(summary ="标签-批量删除", description = "标签-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tagContrastService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "标签-通过id查询")
	@Operation(summary ="标签-通过id查询", description = "标签-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		TagContrast tagContrast = tagContrastService.getById(id);
		return Result.ok(tagContrast);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param tagContrast
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, TagContrast tagContrast) {
      return super.exportXls(request, tagContrast, TagContrast.class, "标签");
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
      return super.importExcel(request, response, TagContrast.class);
  }

}
