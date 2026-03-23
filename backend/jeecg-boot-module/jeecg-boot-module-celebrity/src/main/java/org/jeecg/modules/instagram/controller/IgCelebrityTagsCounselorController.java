package org.jeecg.modules.instagram.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.util.PageUtil;
import org.jeecg.modules.instagram.entity.IgCelebrityTagsCounselor;
import org.jeecg.modules.instagram.service.IIgCelebrityTagsCounselorService;
import org.jeecg.modules.kol.model.KolSearchModel;
import org.jeecg.modules.kol.model.KolTagAllotModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

import static org.jeecg.common.constant.SystemConstants.*;

/**
 * @Description: IG标签网红分配（顾问）任务明细
 * @Author: xyc
 * @Date:   2024-12-02
 * @Version: V1.0
 */
@Tag(name="IG标签网红分配（顾问）任务明细")
@RestController
@RequestMapping("/instagram/igTagsCounselor")
@Slf4j
public class IgCelebrityTagsCounselorController extends JeecgController<IgCelebrityTagsCounselor, IIgCelebrityTagsCounselorService> {
	@Autowired
	private IIgCelebrityTagsCounselorService igCelebrityTagsCounselorService;
	
	/**
	 * 分页列表查询
	 *
	 * @param searchModel
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "标签网红分配明细-分页列表查询")
	@Operation(summary ="标签网红分配明细-分页列表查询", description = "标签网红分配明细-分页列表查询")
	@PostMapping(value = LIST_URL)
	public Result<?> queryPageList(@RequestBody KolSearchModel searchModel,
								   @RequestParam(name = PAGE_NO, defaultValue = PAGE_NO_DEFAULT) Integer pageNo,
								   @RequestParam(name = PAGE_SIZE, defaultValue = PAGE_SIZE_DEFAULT) Integer pageSize,
								   HttpServletRequest req) {
		Page<KolTagAllotModel> page = PageUtil.getOrderItems(pageNo, pageSize, req);
		IPage<KolTagAllotModel> pageList = igCelebrityTagsCounselorService.getIgTagAllottedList(page, searchModel);
		return Result.ok(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param igCelebrityTagsCounselor
	 * @return
	 */
	@AutoLog(value = "标签网红分配明细-添加")
	@Operation(summary ="标签网红分配明细-添加", description = "标签网红分配明细-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody IgCelebrityTagsCounselor igCelebrityTagsCounselor) {
		igCelebrityTagsCounselorService.save(igCelebrityTagsCounselor);
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param igCelebrityTagsCounselor
	 * @return
	 */
	@AutoLog(value = "ig_celebrity_tags_counselor-编辑")
	@Operation(summary ="ig_celebrity_tags_counselor-编辑", description = "ig_celebrity_tags_counselor-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody IgCelebrityTagsCounselor igCelebrityTagsCounselor) {
		igCelebrityTagsCounselorService.updateById(igCelebrityTagsCounselor);
		return Result.ok("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "ig_celebrity_tags_counselor-通过id删除")
	@Operation(summary ="ig_celebrity_tags_counselor-通过id删除", description = "ig_celebrity_tags_counselor-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		igCelebrityTagsCounselorService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "ig_celebrity_tags_counselor-批量删除")
	@Operation(summary ="ig_celebrity_tags_counselor-批量删除", description = "ig_celebrity_tags_counselor-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.igCelebrityTagsCounselorService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "ig_celebrity_tags_counselor-通过id查询")
	@Operation(summary ="ig_celebrity_tags_counselor-通过id查询", description = "ig_celebrity_tags_counselor-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		IgCelebrityTagsCounselor igCelebrityTagsCounselor = igCelebrityTagsCounselorService.getById(id);
		if(igCelebrityTagsCounselor==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(igCelebrityTagsCounselor);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param igCelebrityTagsCounselor
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, IgCelebrityTagsCounselor igCelebrityTagsCounselor) {
        return super.exportXls(request, igCelebrityTagsCounselor, IgCelebrityTagsCounselor.class, "ig_celebrity_tags_counselor");
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
        return super.importExcel(request, response, IgCelebrityTagsCounselor.class);
    }

}
