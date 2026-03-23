package org.jeecg.modules.kol.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.kol.entity.KolAllotLogDetail;
import org.jeecg.modules.kol.model.TagAllotResultModel;
import org.jeecg.modules.kol.service.IKolAllotLogDetailService;
import org.jeecg.modules.tiktok.model.TiktokAllotLogDetailModel;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 网红标签分配日志明细
 * @Author: xyc
 * @Date:   2024-12-30 19:29:28
 * @Version: V1.0
 */
@Tag(name="网红标签分配日志明细")
@RestController
@RequestMapping("/kol/kolAllotLogDetail")
@Slf4j
public class KolAllotLogDetailController extends JeecgController<KolAllotLogDetail, IKolAllotLogDetailService> {
	@Autowired
	private IKolAllotLogDetailService kolAllotLogDetailService;
	
	/**
	 * 分页列表查询
	 *
	 * @param kolAllotLogDetail
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "kol_allot_log_detail-分页列表查询")
	@Operation(summary ="kol_allot_log_detail-分页列表查询", description = "kol_allot_log_detail-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(KolAllotLogDetail kolAllotLogDetail,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<KolAllotLogDetail> queryWrapper = QueryGenerator.initQueryWrapper(kolAllotLogDetail, req.getParameterMap());
		Page<KolAllotLogDetail> page = new Page<KolAllotLogDetail>(pageNo, pageSize);
		IPage<KolAllotLogDetail> pageList = kolAllotLogDetailService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param kolAllotLogDetail
	 * @return
	 */
	@AutoLog(value = "kol_allot_log_detail-添加")
	@Operation(summary ="kol_allot_log_detail-添加", description = "kol_allot_log_detail-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody KolAllotLogDetail kolAllotLogDetail) {
		kolAllotLogDetailService.save(kolAllotLogDetail);
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param kolAllotLogDetail
	 * @return
	 */
	@AutoLog(value = "kol_allot_log_detail-编辑")
	@Operation(summary ="kol_allot_log_detail-编辑", description = "kol_allot_log_detail-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody KolAllotLogDetail kolAllotLogDetail) {
		kolAllotLogDetailService.updateById(kolAllotLogDetail);
		return Result.ok("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "kol_allot_log_detail-通过id删除")
	@Operation(summary ="kol_allot_log_detail-通过id删除", description = "kol_allot_log_detail-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		kolAllotLogDetailService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "kol_allot_log_detail-批量删除")
	@Operation(summary ="kol_allot_log_detail-批量删除", description = "kol_allot_log_detail-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.kolAllotLogDetailService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "kol_allot_log_detail-通过id查询")
	@Operation(summary ="kol_allot_log_detail-通过id查询", description = "kol_allot_log_detail-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		KolAllotLogDetail kolAllotLogDetail = kolAllotLogDetailService.getById(id);
		if(kolAllotLogDetail==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(kolAllotLogDetail);
	}

	/**
	 * 导出excel
	 *
	 * @param request
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, TiktokAllotLogDetailModel allotLogDetailModel) {
		String title = "分配日志";
		String realName = getRealName();
		String allotIds = allotLogDetailModel.getAllotIds();
		if (oConvertUtils.isEmpty(allotIds)) throw new JeecgBootException("请选择需要导出的分配日志");

		LambdaQueryWrapper<KolAllotLogDetail> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.in(KolAllotLogDetail::getId, Arrays.asList(allotIds.split(",")));
		List<KolAllotLogDetail> allotLogDetails = kolAllotLogDetailService.list(queryWrapper);

		List<TagAllotResultModel> detailModelList = new ArrayList<>();

		for (KolAllotLogDetail allotLogDetail : allotLogDetails) {
			Date allotTime = allotLogDetail.getAllotTime();
			String counselorName = allotLogDetail.getCounselorName();
			String allotContent = allotLogDetail.getAllotContent();
			JSONObject jsonObject = JSONObject.parseObject(allotContent);

			jsonObject.keySet().forEach(key -> {
				Object value = jsonObject.get(key);
				TagAllotResultModel detailModel = new TagAllotResultModel();
				detailModel.setAllotTime(allotTime);
				detailModel.setCounselorName(counselorName);
				detailModel.setAllotContent(key);
				detailModel.setAllotNum((Integer) value);
				detailModelList.add(detailModel);
			});
		}
		List<TagAllotResultModel> detailModels = detailModelList.stream().sorted(Comparator.comparing(TagAllotResultModel::getAllotTime).reversed()).collect(Collectors.toList());
		// 导出Excel
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		mv.addObject(NormalExcelConstants.FILE_NAME, title);
		mv.addObject(NormalExcelConstants.CLASS, TagAllotResultModel.class);
		mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("分配日志报表", "导出人:" + realName, title));
		mv.addObject(NormalExcelConstants.DATA_LIST, detailModels);
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
        return super.importExcel(request, response, KolAllotLogDetail.class);
    }

}
