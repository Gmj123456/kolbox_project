package org.jeecg.modules.instagram.storecelebrity.controller;

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
import org.jeecg.modules.instagram.storecelebrity.entity.CelebrityConsignee;
import org.jeecg.modules.instagram.storecelebrity.service.ICelebrityConsigneeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

 /**
 * @Description: 网红收获地址信息
 * @Author: jeecg-boot
 * @Date:   2020-10-13
 * @Version: V1.0
 */
@Slf4j
@Tag(name ="网红收获地址信息")
@RestController
@RequestMapping("/celebrityconsignee/celebrityConsignee")
public class CelebrityConsigneeController extends JeecgController<CelebrityConsignee, ICelebrityConsigneeService> {
	@Autowired
	private ICelebrityConsigneeService celebrityConsigneeService;
	
	/**
	 * 分页列表查询
	 *
	 * @param celebrityConsignee
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "网红收获地址信息-分页列表查询")
	@Operation(summary ="网红收获地址信息-分页列表查询", description = "网红收获地址信息-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(CelebrityConsignee celebrityConsignee,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<CelebrityConsignee> queryWrapper = QueryGenerator.initQueryWrapper(celebrityConsignee, req.getParameterMap());
		Page<CelebrityConsignee> page = new Page<CelebrityConsignee>(pageNo, pageSize);
		IPage<CelebrityConsignee> pageList = celebrityConsigneeService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param celebrityConsignee
	 * @return
	 */
	@AutoLog(value = "网红收获地址信息-添加")
	@Operation(summary ="网红收获地址信息-添加", description = "网红收获地址信息-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody CelebrityConsignee celebrityConsignee) {
		celebrityConsigneeService.save(celebrityConsignee);
		return Result.ok("添加成功！");
	}
	
	/**
	 * 编辑
	 *
	 * @param celebrityConsignee
	 * @return
	 */
	@AutoLog(value = "网红收获地址信息-编辑")
	@Operation(summary ="网红收获地址信息-编辑", description = "网红收获地址信息-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody CelebrityConsignee celebrityConsignee) {
		celebrityConsigneeService.updateById(celebrityConsignee);
		return Result.ok("编辑成功!");
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "网红收获地址信息-通过id删除")
	@Operation(summary ="网红收获地址信息-通过id删除", description = "网红收获地址信息-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		celebrityConsigneeService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "网红收获地址信息-批量删除")
	@Operation(summary ="网红收获地址信息-批量删除", description = "网红收获地址信息-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.celebrityConsigneeService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "网红收获地址信息-通过id查询")
	@Operation(summary ="网红收获地址信息-通过id查询", description = "网红收获地址信息-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		CelebrityConsignee celebrityConsignee = celebrityConsigneeService.getById(id);
		return Result.ok(celebrityConsignee);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param celebrityConsignee
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, CelebrityConsignee celebrityConsignee) {
      return super.exportXls(request, celebrityConsignee, CelebrityConsignee.class, "网红收获地址信息");
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
      return super.importExcel(request, response, CelebrityConsignee.class);
  }

}
