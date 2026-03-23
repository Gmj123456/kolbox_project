package org.jeecg.modules.feishu.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.SystemConstants;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.feishu.entity.KolVideoMetrics;
import org.jeecg.modules.feishu.service.IKolVideoMetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

 /**
 * @Description: 上线视频指标表
 * @Author: nqr
 * @Date:   2025-10-20
 * @Version: V1.0
 */
@Slf4j
@Tag(name="上线视频指标表")
@RestController
@RequestMapping("/feishu/kolVideoMetrics")
public class KolVideoMetricsController extends JeecgController<KolVideoMetrics, IKolVideoMetricsService> {
	@Autowired
	private IKolVideoMetricsService kolVideoMetricsService;
	
	/**
	 * 分页列表查询
	 *
	 * @param kolVideoMetrics
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "上线视频指标表-"+ SystemConstants.PAGE_LIST_QUERY)
	@Operation(summary ="上线视频指标表-"+SystemConstants.PAGE_LIST_QUERY, description = "上线视频指标表-"+SystemConstants.PAGE_LIST_QUERY)
	@GetMapping(value = "/list")
	public Result<?> queryPageList(KolVideoMetrics kolVideoMetrics,
								  @RequestParam(name = SystemConstants.PAGE_NO, defaultValue = SystemConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                  @RequestParam(name = SystemConstants.PAGE_SIZE, defaultValue = SystemConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<KolVideoMetrics> queryWrapper = QueryGenerator.initQueryWrapper(kolVideoMetrics, req.getParameterMap());
		Page<KolVideoMetrics> page = new Page<KolVideoMetrics>(pageNo, pageSize);
		IPage<KolVideoMetrics> pageList = kolVideoMetricsService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param kolVideoMetrics
	 * @return
	 */
	@AutoLog(value = "上线视频指标表-"+SystemConstants.ADD)
	@Operation(summary ="上线视频指标表-" +SystemConstants.ADD, description = "上线视频指标表-" +SystemConstants.ADD)
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody KolVideoMetrics kolVideoMetrics) {
		kolVideoMetricsService.save(kolVideoMetrics);
		return Result.ok(SystemConstants.ADD_SUCCESS);
	}
	
	/**
	 * 编辑
	 *
	 * @param kolVideoMetrics
	 * @return
	 */
	@AutoLog(value = "上线视频指标表-"+SystemConstants.EDIT)
	@Operation(summary ="上线视频指标表-"+SystemConstants.EDIT, description = "上线视频指标表-" + SystemConstants.EDIT)
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody KolVideoMetrics kolVideoMetrics) {
		kolVideoMetricsService.updateById(kolVideoMetrics);
		return Result.ok(SystemConstants.EDIT_SUCCESS);
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "上线视频指标表-"+SystemConstants.DELETE_BY_ID)
	@Operation(summary ="上线视频指标表-" +SystemConstants.DELETE_BY_ID, description = "上线视频指标表-"+SystemConstants.DELETE_BY_ID)
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		kolVideoMetricsService.removeById(id);
		return Result.ok(SystemConstants.DELETE_SUCCESS);
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "上线视频指标表-"+ SystemConstants.DELETE_BATCH)
	@Operation(summary ="上线视频指标表-" + SystemConstants.DELETE_BATCH, description = "上线视频指标表-" + SystemConstants.DELETE_BATCH)
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.kolVideoMetricsService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok(SystemConstants.DELETE_BATCH_SUCCESS);
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "上线视频指标表-" + SystemConstants.QUERY_BY_ID)
	@Operation(summary ="上线视频指标表-" +SystemConstants.QUERY_BY_ID, description = "上线视频指标表-"+SystemConstants.QUERY_BY_ID)
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		KolVideoMetrics kolVideoMetrics = kolVideoMetricsService.getById(id);
		return Result.ok(kolVideoMetrics);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param kolVideoMetrics
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, KolVideoMetrics kolVideoMetrics) {
      return super.exportXls(request, kolVideoMetrics, KolVideoMetrics.class, "上线视频指标表");
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
      return super.importExcel(request, response, KolVideoMetrics.class);
  }

}
