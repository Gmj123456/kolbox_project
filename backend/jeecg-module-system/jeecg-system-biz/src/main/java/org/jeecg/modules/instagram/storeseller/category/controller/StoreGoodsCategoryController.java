package org.jeecg.modules.instagram.storeseller.category.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.enums.YesNoStatus;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.storeseller.category.entity.StoreGoodsCategory;
import org.jeecg.modules.instagram.storeseller.category.model.GoodsCategoryTree;
import org.jeecg.modules.instagram.storeseller.category.service.IStoreGoodsCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

 /**
 * @Description: 商家商品类目表
 * @Author: jeecg-boot
 * @Date:   2020-10-12
 * @Version: V1.0
 */
@Slf4j
@Tag(name ="商家商品类目表")
@RestController
@RequestMapping("/storegoodscategory")
public class StoreGoodsCategoryController extends JeecgController<StoreGoodsCategory, IStoreGoodsCategoryService> {
	@Autowired
	private IStoreGoodsCategoryService storeGoodsCategoryService;

	//查询二级类目
	@GetMapping("/storeGoodsCategoryList")
	public Result<?> queryList(String parentId){
		return Result.ok(storeGoodsCategoryService.queryGoodsCategoryList(parentId));
	}

	/**
	 * 分页列表查询
	 *
	 * @param storeGoodsCategory
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "商家商品类目表-分页列表查询")
	@Operation(summary ="商家商品类目表-分页列表查询", description = "商家商品类目表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(StoreGoodsCategory storeGoodsCategory,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<StoreGoodsCategory> queryWrapper = QueryGenerator.initQueryWrapper(storeGoodsCategory, req.getParameterMap());
		Page<StoreGoodsCategory> page = new Page<StoreGoodsCategory>(pageNo, pageSize);
		IPage<StoreGoodsCategory> pageList = storeGoodsCategoryService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	 /**
	  * 加载类目Tree
	  *
	  * @return
	  */
	 @RequestMapping(value = "/listTree", method = RequestMethod.GET)
	 public Result<List<GoodsCategoryTree>> listTree() {
		 long start = System.currentTimeMillis();
		 Result<List<GoodsCategoryTree>> result = new Result<>();
		 try {
			 LambdaQueryWrapper<StoreGoodsCategory> query = new LambdaQueryWrapper<StoreGoodsCategory>();
			 query.eq(StoreGoodsCategory::getStatus, YesNoStatus.YES.getCode());
			 query.eq(StoreGoodsCategory::isDelFlag,YesNoStatus.NO.getCode());
			 query.orderByAsc(StoreGoodsCategory::getSortCode);
			 List<StoreGoodsCategory> list = storeGoodsCategoryService.list(query);
			 List<GoodsCategoryTree> treeList = new ArrayList<>();
			 getTreeList(treeList, list, null);
			 result.setResult(treeList);
			 result.setSuccess(true);
			 log.info("======获取全部类目数据=====耗时:" + (System.currentTimeMillis() - start) + "毫秒");
		 } catch (Exception e) {
			 log.error(e.getMessage(), e);
		 }
		 return result;
	 }


	 /**
	  * 分页列表查询
	  *
	  * @param storeGoodsCategory
	  * @param req
	  * @return
	  */
	 @AutoLog(value = "商家商品类目表-分页列表查询")
	 @Operation(summary ="商家商品类目表-分页列表查询", description = "商家商品类目表-分页列表查询")
	 @GetMapping(value = "/listRoot")
	 public Result<?> queryRootList(StoreGoodsCategory storeGoodsCategory, HttpServletRequest req) {
		 LambdaQueryWrapper<StoreGoodsCategory> lambdaQueryWrapper = new LambdaQueryWrapper<>();
		 lambdaQueryWrapper.eq(StoreGoodsCategory::getParentId,"").or().isNull(StoreGoodsCategory::getParentId);

		 List<StoreGoodsCategory> pageList = storeGoodsCategoryService.list(lambdaQueryWrapper);
		 return Result.ok(pageList);
	 }

	 /**
	  * 加载类目Tree层级
	  * @param treeList
	  * @param metaList
	  * @param temp
	  */
	 private void getTreeList(List<GoodsCategoryTree> treeList, List<StoreGoodsCategory> metaList, GoodsCategoryTree temp) {
		 for (StoreGoodsCategory storeGoodsCategory : metaList) {
			 String tempPid = storeGoodsCategory.getParentId();
			 GoodsCategoryTree tree = new GoodsCategoryTree(storeGoodsCategory);
			 if (temp == null && oConvertUtils.isEmpty(tempPid)) {
				 treeList.add(tree);
				 if (!tree.isLeaf()) {
					 getTreeList(treeList, metaList, tree);
				 }
			 } else if (temp != null && tempPid != null && tempPid.equals(temp.getId())) {
				 temp.getChildren().add(tree);
				 if (!tree.isLeaf()) {
					 getTreeList(treeList, metaList, tree);
				 }
			 }

		 }
	 }

	
	/**
	 * 添加
	 *
	 * @param storeGoodsCategory
	 * @return
	 */
	@AutoLog(value = "商家商品类目表-添加")
	@Operation(summary ="商家商品类目表-添加", description = "商家商品类目表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody StoreGoodsCategory storeGoodsCategory) {
		storeGoodsCategory.setStatus(YesNoStatus.YES.getCode());
		storeGoodsCategoryService.addCategory(storeGoodsCategory);
		return Result.ok("添加成功！");
	}
	
	/**
	 * 编辑
	 *
	 * @param storeGoodsCategory
	 * @return
	 */
	@AutoLog(value = "商家商品类目表-编辑")
	@Operation(summary ="商家商品类目表-编辑", description = "商家商品类目表-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody StoreGoodsCategory storeGoodsCategory) {
		storeGoodsCategoryService.editCategory(storeGoodsCategory);
		return Result.ok("编辑成功!");
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "商家商品类目表-通过id删除")
	@Operation(summary ="商家商品类目表-通过id删除", description = "商家商品类目表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		storeGoodsCategoryService.deleteCategory(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "商家商品类目表-批量删除")
	@Operation(summary ="商家商品类目表-批量删除", description = "商家商品类目表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.storeGoodsCategoryService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "商家商品类目表-通过id查询")
	@Operation(summary ="商家商品类目表-通过id查询", description = "商家商品类目表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		StoreGoodsCategory storeGoodsCategory = storeGoodsCategoryService.getById(id);
		return Result.ok(storeGoodsCategory);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param storeGoodsCategory
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, StoreGoodsCategory storeGoodsCategory) {
      return super.exportXls(request, storeGoodsCategory, StoreGoodsCategory.class, "商家商品类目表");
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
      return super.importExcel(request, response, StoreGoodsCategory.class);
  }



}
