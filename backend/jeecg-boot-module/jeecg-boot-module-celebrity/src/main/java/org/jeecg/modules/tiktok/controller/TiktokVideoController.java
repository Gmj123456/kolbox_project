package org.jeecg.modules.tiktok.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.SystemConstants;
import org.jeecg.common.constant.enums.YesNoStatus;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.util.CustomExcelExportStyler;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.tiktok.entity.TiktokVideo;
import org.jeecg.modules.tiktok.model.TiktokVideoModel;
import org.jeecg.modules.tiktok.service.ITiktokVideoService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;

import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

 /**
 * @Description: Tiktok视频获取
 * @Author: dongruyang
 * @Date:   2024-05-30
 * @Version: V1.0
 */
@Slf4j
@RestController
@RequestMapping("/tiktokVideo")
public class TiktokVideoController extends JeecgController<TiktokVideo, ITiktokVideoService> {
	@Autowired
	private ITiktokVideoService tiktokVideoService;
	
	/**
	 * 分页列表查询
	 *
	 * @param tiktokVideoModel
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "Tiktok视频获取-"+ SystemConstants.PAGE_LIST_QUERY)
	@Operation(summary ="Tiktok视频获取-"+ SystemConstants.PAGE_LIST_QUERY, description = "Tiktok视频获取-"+ SystemConstants.PAGE_LIST_QUERY)
	@GetMapping(value = "/list")
	public Result<?> queryPageList(TiktokVideoModel tiktokVideoModel,
								  @RequestParam(name = SystemConstants.PAGE_NO, defaultValue = SystemConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                  @RequestParam(name = SystemConstants.PAGE_SIZE, defaultValue = SystemConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
								   HttpServletRequest req) {
		String userId = getUserIdByToken();
		tiktokVideoModel.setUserId(userId);
		if(!StringUtils.isEmpty(tiktokVideoModel.getStartDateTime())){
			Date startDate = DateUtils.str2Date(tiktokVideoModel.getStartDateTime(), new SimpleDateFormat("yyyy-MM-dd"));
			tiktokVideoModel.setStartDateTime(DateUtils.date2Str(startDate, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));
		}
		if (!StringUtils.isEmpty(tiktokVideoModel.getEndDateTime())){
			Date endDate = DateUtils.str2Date(tiktokVideoModel.getEndDateTime(), new SimpleDateFormat("yyyy-MM-dd"));
			endDate=DateUtils.getFetureDate(1,endDate);
			tiktokVideoModel.setEndDateTime(DateUtils.date2Str(endDate, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));
		}
		Page<TiktokVideo> page = new Page<TiktokVideo>(pageNo, pageSize);
		IPage<TiktokVideo> pageList = tiktokVideoService.queryPageList(page, tiktokVideoModel);
		return Result.ok(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param tiktokVideoModel
	 * @return
	 */
	@AutoLog(value = "Tiktok视频获取-"+ SystemConstants.ADD)
	@Operation(summary ="Tiktok视频获取-" + SystemConstants.ADD, description = "Tiktok视频获取-" + SystemConstants.ADD)
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody TiktokVideoModel tiktokVideoModel) {
		String userId = getUserIdByToken();
		String userName = getUserNameByToken();
		//存放错误视频链接
		List<String> videoUrlErrorList = new ArrayList<>();
		//存放要保存的视频链接
		List<TiktokVideo> tiktokVideoList = new ArrayList<>();

		//获取页面传输的视频链接列表
		List<String> videoUrlList = tiktokVideoModel.getVideoUrlList();
		if(videoUrlList.isEmpty()){
			return Result.error("请输入Tiktok视频链接");
		}
		for (String videoUrl : videoUrlList) {
			//验证视频链接是否合法
			if(!tiktokVideoService.checkVideoUrl(videoUrl.trim())){
				videoUrlErrorList.add(videoUrl);
				continue;
			}
			/*//查询数据库中是否已经存在视频链接
			LambdaQueryWrapper<TiktokVideo> queryWrapper = new LambdaQueryWrapper<>();
			queryWrapper.eq(TiktokVideo::getVideoUrl,videoUrl);
			if(tiktokVideoService.count(queryWrapper) > 0){
				videoUrlExistList.add(videoUrl);
				continue;
			}*/
			TiktokVideo tiktokVideo=new TiktokVideo();
			tiktokVideo.setVideoUrl(videoUrl.trim());
			tiktokVideo.setBatchNo(DateUtils.date2Str(new Date(),new SimpleDateFormat("yyyyMMdd")));
			tiktokVideo.setUserId(userId);
			tiktokVideo.setUserName(userName);
			tiktokVideoList.add(tiktokVideo);
		}
		JSONObject result = new JSONObject();
		result.put("videoUrlErrorList",videoUrlErrorList);
		if(tiktokVideoList.isEmpty()){
			return Result.error(CommonConstant.SC_INTERNAL_SERVER_ERROR_500,"没有要新增的Tiktok视频链接！",result);
		}
		//批量保存视频链接
		this.tiktokVideoService.saveBatch(tiktokVideoList);
		return Result.OK("新增TiktokVideoUrl成功！",result);
	}
	
	/**
	 * 编辑
	 *
	 * @param tiktokVideo
	 * @return
	 */
	@AutoLog(value = "Tiktok视频获取-"+ SystemConstants.EDIT)
	@Operation(summary ="Tiktok视频获取-"+ SystemConstants.EDIT, description = "Tiktok视频获取-" + SystemConstants.EDIT)
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody TiktokVideo tiktokVideo) {
		tiktokVideoService.updateById(tiktokVideo);
		return Result.ok(SystemConstants.EDIT_SUCCESS);
	}

	 /**
	  * Tiktok视频重置
	  *
	  * @param tiktokVideo
	  * @return
	  */
	 @AutoLog(value = "Tiktok视频重置")
	 @Operation(summary ="Tiktok视频重置", description = "Tiktok视频重置")
	 @PutMapping(value = "/resetVideo")
	 public Result<?> resetVideo(@RequestBody TiktokVideo tiktokVideo) {
		 if(StringUtils.isEmpty(tiktokVideo.getId())){
			 return Result.error("请选择要重置的视频！");
		 }
		 if (StringUtils.isEmpty(tiktokVideo.getVideoUrl())){
			 return Result.error("请输入Tiktok视频链接！");
		 }
		 if(!tiktokVideoService.checkVideoUrl(tiktokVideo.getVideoUrl().trim())){
			 return Result.error("Tiktok视频链接不合法！");
		 }
		 tiktokVideo.setVideoId("");
		 tiktokVideo.setUniqueId("");
		 tiktokVideo.setVideoDiggCount(YesNoStatus.NO.getCode());
		 tiktokVideo.setVideoCollectCount(YesNoStatus.NO.getCode());
		 tiktokVideo.setVideoCommentCount(YesNoStatus.NO.getCode());
		 tiktokVideo.setVideoShareCount(YesNoStatus.NO.getCode());
		 tiktokVideo.setVideoCreateTime(null);
		 tiktokVideo.setVideoDesc("");
		 tiktokVideo.setVideoUrl(tiktokVideo.getVideoUrl().trim());
		 tiktokVideo.setVideoPlayCount(YesNoStatus.NO.getCode());
		 tiktokVideoService.updateById(tiktokVideo);
		 return Result.ok("Tiktok视频重置成功！");
	 }
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "Tiktok视频获取-"+ SystemConstants.DELETE_BY_ID)
	@Operation(summary ="Tiktok视频获取-" + SystemConstants.DELETE_BY_ID, description = "Tiktok视频获取-"+ SystemConstants.DELETE_BY_ID)
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		tiktokVideoService.removeById(id);
		return Result.ok(SystemConstants.DELETE_SUCCESS);
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "Tiktok视频获取-"+ SystemConstants.DELETE_BATCH)
	@Operation(summary ="Tiktok视频获取-" + SystemConstants.DELETE_BATCH, description = "Tiktok视频获取-" + SystemConstants.DELETE_BATCH)
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tiktokVideoService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok(SystemConstants.DELETE_BATCH_SUCCESS);
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "Tiktok视频获取-" + SystemConstants.QUERY_BY_ID)
	@Operation(summary ="Tiktok视频获取-" + SystemConstants.QUERY_BY_ID, description = "Tiktok视频获取-"+ SystemConstants.QUERY_BY_ID)
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		TiktokVideo tiktokVideo = tiktokVideoService.getById(id);
		return Result.ok(tiktokVideo);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param tiktokVideoModel
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, TiktokVideoModel tiktokVideoModel) {
	  tiktokVideoModel.setUserId(getUserIdByToken());
	  if(!StringUtils.isEmpty(tiktokVideoModel.getStartDateTime())){
		  Date startDate = DateUtils.str2Date(tiktokVideoModel.getStartDateTime(), new SimpleDateFormat("yyyy-MM-dd"));
		  tiktokVideoModel.setStartDateTime(DateUtils.date2Str(startDate, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));
	  }
	  if (!StringUtils.isEmpty(tiktokVideoModel.getEndDateTime())){
		  Date endDate = DateUtils.str2Date(tiktokVideoModel.getEndDateTime(), new SimpleDateFormat("yyyy-MM-dd"));
		  endDate=DateUtils.getFetureDate(1,endDate);
		  tiktokVideoModel.setEndDateTime(DateUtils.date2Str(endDate, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));
	  }
	  List<TiktokVideo> tiktokVideos = tiktokVideoService.queryList(tiktokVideoModel);

	  String title = "网红视频信息";
	  ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
	  mv.addObject(NormalExcelConstants.FILE_NAME, title);
	  mv.addObject(NormalExcelConstants.CLASS, TiktokVideo.class);
	  ExportParams exportParams = new ExportParams(title + "详情", "导出人:" + getRealName(), title);
	  exportParams.setStyle(CustomExcelExportStyler.class);
	  mv.addObject(NormalExcelConstants.PARAMS, exportParams);
	  mv.addObject(NormalExcelConstants.DATA_LIST, tiktokVideos);
	  return mv;
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
      return super.importExcel(request, response, TiktokVideo.class);
  }

}
