package org.jeecg.modules.tiktok.controller;

import java.util.Arrays;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.SystemConstants;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.modules.tiktok.entity.TiktokAllotLog;
import org.jeecg.modules.tiktok.service.ITiktokAllotLogService;
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
 * @Description: 分配日志
 * @Author: nqr
 * @Date:   2023-11-03
 * @Version: V1.0
 */
@Slf4j
@Tag(name="分配日志")
@RestController
@RequestMapping("/tiktokAllotLog")
public class TiktokAllotLogController extends JeecgController<TiktokAllotLog, ITiktokAllotLogService> {
	@Autowired
	private ITiktokAllotLogService tiktokAllotLogService;
	
	/**
	 * 分页列表查询
	 *
	 * @param tiktokAllotLog
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "分配日志-"+ SystemConstants.PAGE_LIST_QUERY)
	@Operation(summary ="分配日志-"+ SystemConstants.PAGE_LIST_QUERY, description = "分配日志-"+ SystemConstants.PAGE_LIST_QUERY)
	@GetMapping(value = "/list")
	public Result<?> queryPageList(TiktokAllotLog tiktokAllotLog,
								  @RequestParam(name = SystemConstants.PAGE_NO, defaultValue = SystemConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                  @RequestParam(name = SystemConstants.PAGE_SIZE, defaultValue = SystemConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TiktokAllotLog> queryWrapper = QueryGenerator.initQueryWrapper(tiktokAllotLog, req.getParameterMap());
		Page<TiktokAllotLog> page = new Page<>(pageNo, pageSize);
		IPage<TiktokAllotLog> pageList = tiktokAllotLogService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param tiktokAllotLog
	 * @return
	 */
	@AutoLog(value = "分配日志-"+ SystemConstants.ADD)
	@Operation(summary ="分配日志-" + SystemConstants.ADD, description = "分配日志-" + SystemConstants.ADD)
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody TiktokAllotLog tiktokAllotLog) {
		tiktokAllotLogService.save(tiktokAllotLog);
		return Result.ok(SystemConstants.ADD_SUCCESS);
	}
	
	/**
	 * 编辑
	 *
	 * @param tiktokAllotLog
	 * @return
	 */
	@AutoLog(value = "分配日志-"+ SystemConstants.EDIT)
	@Operation(summary ="分配日志-"+ SystemConstants.EDIT, description = "分配日志-" + SystemConstants.EDIT)
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody TiktokAllotLog tiktokAllotLog) {
		tiktokAllotLogService.updateById(tiktokAllotLog);
		return Result.ok(SystemConstants.EDIT_SUCCESS);
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "分配日志-"+ SystemConstants.DELETE_BY_ID)
	@Operation(summary ="分配日志-" + SystemConstants.DELETE_BY_ID, description = "分配日志-"+ SystemConstants.DELETE_BY_ID)
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		tiktokAllotLogService.removeById(id);
		return Result.ok(SystemConstants.DELETE_SUCCESS);
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "分配日志-"+ SystemConstants.DELETE_BATCH)
	@Operation(summary ="分配日志-" + SystemConstants.DELETE_BATCH, description = "分配日志-" + SystemConstants.DELETE_BATCH)
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tiktokAllotLogService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok(SystemConstants.DELETE_BATCH_SUCCESS);
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "分配日志-" + SystemConstants.QUERY_BY_ID)
	@Operation(summary ="分配日志-" + SystemConstants.QUERY_BY_ID, description = "分配日志-"+ SystemConstants.QUERY_BY_ID)
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		TiktokAllotLog tiktokAllotLog = tiktokAllotLogService.getById(id);
		return Result.ok(tiktokAllotLog);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param tiktokAllotLog
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, TiktokAllotLog tiktokAllotLog) {
      return super.exportXls(request, tiktokAllotLog, TiktokAllotLog.class, "分配日志");
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
      return super.importExcel(request, response, TiktokAllotLog.class);
  }

}
