package org.jeecg.modules.instagram.storeseller.storeuser.controller;

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
import org.jeecg.modules.instagram.storeseller.storeuser.entity.StoreUserRemind;
import org.jeecg.modules.instagram.storeseller.storeuser.service.IStoreUserRemindService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

 /**
 * @Description: 用户监控提醒
 * @Author: jeecg-boot
 * @Date:   2021-04-29
 * @Version: V1.0
 */
@Slf4j
@Tag(name ="用户监控提醒")
@RestController
@RequestMapping("/storeuserremind/storeUserRemind")
public class StoreUserRemindController extends JeecgController<StoreUserRemind, IStoreUserRemindService> {
	@Autowired
	private IStoreUserRemindService storeUserRemindService;
	
	/**
	 * 分页列表查询
	 *
	 * @param storeUserRemind
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "用户监控提醒")
	@Operation(summary ="用户监控提醒-分页列表查询", description = "用户监控提醒-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(StoreUserRemind storeUserRemind,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<StoreUserRemind> queryWrapper = QueryGenerator.initQueryWrapper(storeUserRemind, req.getParameterMap());
		Page<StoreUserRemind> page = new Page<StoreUserRemind>(pageNo, pageSize);
		IPage<StoreUserRemind> pageList = storeUserRemindService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param storeUserRemind
	 * @return
	 */
	@AutoLog(value = "用户监控提醒-添加")
	@Operation(summary ="用户监控提醒-添加", description = "用户监控提醒-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody StoreUserRemind storeUserRemind) {
		storeUserRemindService.save(storeUserRemind);
		return Result.ok("添加成功！");
	}
	
	/**
	 * 编辑
	 *
	 * @param storeUserRemind
	 * @return
	 */
	@AutoLog(value = "用户监控提醒-编辑")
	@Operation(summary ="用户监控提醒-编辑", description = "用户监控提醒-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody StoreUserRemind storeUserRemind) {
		storeUserRemindService.updateById(storeUserRemind);
		return Result.ok("编辑成功!");
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "用户监控提醒-通过id删除")
	@Operation(summary ="用户监控提醒-通过id删除", description = "用户监控提醒-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		storeUserRemindService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "用户监控提醒-批量删除")
	@Operation(summary ="用户监控提醒-批量删除", description = "用户监控提醒-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.storeUserRemindService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "用户监控提醒-通过id查询")
	@Operation(summary ="用户监控提醒-通过id查询", description = "用户监控提醒-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		StoreUserRemind storeUserRemind = storeUserRemindService.getById(id);
		return Result.ok(storeUserRemind);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param storeUserRemind
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, StoreUserRemind storeUserRemind) {
      return super.exportXls(request, storeUserRemind, StoreUserRemind.class, "用户监控提醒");
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
      return super.importExcel(request, response, StoreUserRemind.class);
  }

}
