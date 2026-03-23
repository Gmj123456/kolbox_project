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
import org.jeecg.modules.tiktok.entity.TiktokCelebrityProductVideo;
import org.jeecg.modules.tiktok.service.ITiktokCelebrityProductVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

 /**
 * @Description: TikTok网红产品与视频关联表
 * @Author: nqr
 * @Date:   2025-10-08
 * @Version: V1.0
 */
@Slf4j
@Tag(name="TikTok网红产品与视频关联表")
@RestController
@RequestMapping("/tiktok/tiktokCelebrityProductVideo")
public class TiktokCelebrityProductVideoController extends JeecgController<TiktokCelebrityProductVideo, ITiktokCelebrityProductVideoService> {
	@Autowired
	private ITiktokCelebrityProductVideoService tiktokCelebrityProductVideoService;
	
	/**
	 * 分页列表查询
	 *
	 * @param tiktokCelebrityProductVideo
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "TikTok网红产品与视频关联表-"+ ExamConstants.PAGE_LIST_QUERY)
	@Operation(summary ="TikTok网红产品与视频关联表-"+ExamConstants.PAGE_LIST_QUERY, description = "TikTok网红产品与视频关联表-"+ExamConstants.PAGE_LIST_QUERY)
	@GetMapping(value = "/list")
	public Result<?> queryPageList(TiktokCelebrityProductVideo tiktokCelebrityProductVideo,
								  @RequestParam(name = ExamConstants.PAGE_NO, defaultValue = ExamConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                  @RequestParam(name = ExamConstants.PAGE_SIZE, defaultValue = ExamConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TiktokCelebrityProductVideo> queryWrapper = QueryGenerator.initQueryWrapper(tiktokCelebrityProductVideo, req.getParameterMap());
		Page<TiktokCelebrityProductVideo> page = new Page<TiktokCelebrityProductVideo>(pageNo, pageSize);
		IPage<TiktokCelebrityProductVideo> pageList = tiktokCelebrityProductVideoService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param tiktokCelebrityProductVideo
	 * @return
	 */
	@AutoLog(value = "TikTok网红产品与视频关联表-"+ExamConstants.ADD)
	@Operation(summary ="TikTok网红产品与视频关联表-" +ExamConstants.ADD, description = "TikTok网红产品与视频关联表-" +ExamConstants.ADD)
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody TiktokCelebrityProductVideo tiktokCelebrityProductVideo) {
		tiktokCelebrityProductVideoService.save(tiktokCelebrityProductVideo);
		return Result.ok(ExamConstants.ADD_SUCCESS);
	}
	
	/**
	 * 编辑
	 *
	 * @param tiktokCelebrityProductVideo
	 * @return
	 */
	@AutoLog(value = "TikTok网红产品与视频关联表-"+ExamConstants.EDIT)
	@Operation(summary ="TikTok网红产品与视频关联表-"+ExamConstants.EDIT, description = "TikTok网红产品与视频关联表-" + ExamConstants.EDIT)
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody TiktokCelebrityProductVideo tiktokCelebrityProductVideo) {
		tiktokCelebrityProductVideoService.updateById(tiktokCelebrityProductVideo);
		return Result.ok(ExamConstants.EDIT_SUCCESS);
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "TikTok网红产品与视频关联表-"+ExamConstants.DELETE_BY_ID)
	@Operation(summary ="TikTok网红产品与视频关联表-" +ExamConstants.DELETE_BY_ID, description = "TikTok网红产品与视频关联表-"+ExamConstants.DELETE_BY_ID)
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		tiktokCelebrityProductVideoService.removeById(id);
		return Result.ok(ExamConstants.DELETE_SUCCESS);
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "TikTok网红产品与视频关联表-"+ ExamConstants.DELETE_BATCH)
	@Operation(summary ="TikTok网红产品与视频关联表-" + ExamConstants.DELETE_BATCH, description = "TikTok网红产品与视频关联表-" + ExamConstants.DELETE_BATCH)
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tiktokCelebrityProductVideoService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok(ExamConstants.DELETE_BATCH_SUCCESS);
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "TikTok网红产品与视频关联表-" + ExamConstants.QUERY_BY_ID)
	@Operation(summary ="TikTok网红产品与视频关联表-" +ExamConstants.QUERY_BY_ID, description = "TikTok网红产品与视频关联表-"+ExamConstants.QUERY_BY_ID)
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		TiktokCelebrityProductVideo tiktokCelebrityProductVideo = tiktokCelebrityProductVideoService.getById(id);
		return Result.ok(tiktokCelebrityProductVideo);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param tiktokCelebrityProductVideo
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, TiktokCelebrityProductVideo tiktokCelebrityProductVideo) {
      return super.exportXls(request, tiktokCelebrityProductVideo, TiktokCelebrityProductVideo.class, "TikTok网红产品与视频关联表");
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
      return super.importExcel(request, response, TiktokCelebrityProductVideo.class);
  }

}
