package org.jeecg.modules.tiktok.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.SystemConstants;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.kol.entity.KolBrand;
import org.jeecg.modules.kol.service.IKolBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 品牌表
 * @Author: dongruyang
 * @Date:   2023-10-10
 * @Version: V1.0
 */
@Slf4j
@Tag(name="品牌表")
@RestController
@RequestMapping("/tiktokbrand")
public class TiktokBrandController extends JeecgController<KolBrand, IKolBrandService> {
	@Autowired
	private IKolBrandService brandService;
	
	/**
	 * 分页列表查询
	 *
	 * @param kolBrand
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "品牌表-"+ SystemConstants.PAGE_LIST_QUERY)
	@Operation(summary ="品牌表-"+ SystemConstants.PAGE_LIST_QUERY, description = "品牌表-"+ SystemConstants.PAGE_LIST_QUERY)
	@GetMapping(value = "/list")
	public Result<?> queryPageList(KolBrand kolBrand,
								   @RequestParam(name = SystemConstants.PAGE_NO, defaultValue = SystemConstants.PAGE_NO_DEFAULT) Integer pageNo,
								   @RequestParam(name = SystemConstants.PAGE_SIZE, defaultValue = SystemConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
								   HttpServletRequest req) {
		LambdaQueryWrapper<KolBrand> queryWrapper = new LambdaQueryWrapper<>();
		if (oConvertUtils.isNotEmpty(kolBrand.getBrandName())) {
			queryWrapper.like(KolBrand::getBrandName, kolBrand.getBrandName());
		}
		Page<KolBrand> page = new Page<KolBrand>(pageNo, pageSize);
		IPage<KolBrand> pageList = brandService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	/**
	 * 分页列表查询
	 *
	 * @param kolBrand
	 * @return
	 */
	@AutoLog(value = "品牌表列表")
	@Operation(summary ="品牌表品牌表列表", description = "品牌表品牌表列表")
	@GetMapping(value = "/listAll")
	public Result<?> queryListAll(KolBrand kolBrand) {
		List<KolBrand> brandList = brandService.queryListAll(kolBrand.getBrandName());
		return Result.ok(brandList);
	}
	
	/**
	 * 添加
	 *
	 * @param kolBrand
	 * @return
	 */
	@AutoLog(value = "品牌表-"+ SystemConstants.ADD)
	@Operation(summary ="品牌表-" + SystemConstants.ADD, description = "品牌表-" + SystemConstants.ADD)
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody KolBrand kolBrand) {
		List<KolBrand> list = brandService.list();
		for (KolBrand existingBrand:list){
			if (existingBrand.getBrandName().equals(kolBrand.getBrandName())){
				return Result.error("该品牌已存在");
			}
		}
		brandService.save(kolBrand);
		return Result.ok(SystemConstants.ADD_SUCCESS);
	}
	
	/**
	 * 编辑
	 *
	 * @param kolBrand
	 * @return
	 */
	@AutoLog(value = "品牌表-"+ SystemConstants.EDIT)
	@Operation(summary ="品牌表-"+ SystemConstants.EDIT, description = "品牌表-" + SystemConstants.EDIT)
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody KolBrand kolBrand) {
		List<KolBrand> brandList =  brandService.list();
		for (KolBrand brandLists : brandList){
			if (brandLists.getBrandName().equals(kolBrand.getBrandName())){
				return Result.error("该品牌已存在");
			}
		}
		brandService.updateById(kolBrand);
		return Result.ok(SystemConstants.EDIT_SUCCESS);
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "品牌表-"+ SystemConstants.DELETE_BY_ID)
	@Operation(summary ="品牌表-" + SystemConstants.DELETE_BY_ID, description = "品牌表-"+ SystemConstants.DELETE_BY_ID)
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		brandService.removeById(id);
		return Result.ok(SystemConstants.DELETE_SUCCESS);
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "品牌表-"+ SystemConstants.DELETE_BATCH)
	@Operation(summary ="品牌表-" + SystemConstants.DELETE_BATCH, description = "品牌表-" + SystemConstants.DELETE_BATCH)
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.brandService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok(SystemConstants.DELETE_BATCH_SUCCESS);
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "品牌表-" + SystemConstants.QUERY_BY_ID)
	@Operation(summary ="品牌表-" + SystemConstants.QUERY_BY_ID, description = "品牌表-"+ SystemConstants.QUERY_BY_ID)
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		KolBrand kolBrand = brandService.getById(id);
		return Result.ok(kolBrand);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param kolBrand
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, KolBrand kolBrand) {
      return super.exportXls(request, kolBrand, KolBrand.class, "品牌表");
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
      return super.importExcel(request, response, KolBrand.class);
  }

}
