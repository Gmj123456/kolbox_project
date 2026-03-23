package org.jeecg.modules.kol.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.ExamConstants;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.kol.entity.KolBlacklist;
import org.jeecg.modules.kol.entity.KolProduct;
import org.jeecg.modules.kol.entity.KolShields;
import org.jeecg.modules.kol.model.KolBlacklistModel;
import org.jeecg.modules.kol.model.KolShieldsModel;
import org.jeecg.modules.kol.service.IKolProductService;
import org.jeecg.modules.kol.service.IKolShieldsService;
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
 * @Description: 网红屏蔽名单表
 * @Author: dongruyang
 * @Date:   2025-10-18
 * @Version: V1.0
 */
@Slf4j
@Tag(name="网红屏蔽名单表")
@RestController
@RequestMapping("/kol/kolshields")
public class KolShieldsController extends JeecgController<KolShields, IKolShieldsService> {
	@Autowired
	private IKolShieldsService kolShieldsService;
	@Autowired
	private IKolProductService productService;
	
	/**
	 * 分页列表查询
	 *
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "网红屏蔽名单表-"+ ExamConstants.PAGE_LIST_QUERY)
	@Operation(summary ="网红屏蔽名单表-"+ExamConstants.PAGE_LIST_QUERY, description = "网红屏蔽名单表-"+ExamConstants.PAGE_LIST_QUERY)
	@GetMapping(value = "/list")
	public Result<?> queryPageList(KolShieldsModel kolShieldsModel,
								   @RequestParam(name = ExamConstants.PAGE_NO, defaultValue = ExamConstants.PAGE_NO_DEFAULT) Integer pageNo,
								   @RequestParam(name = ExamConstants.PAGE_SIZE, defaultValue = ExamConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
								   HttpServletRequest req) {
		Page<KolShieldsModel> page = new Page<>(pageNo, pageSize);
		//查询当前用户的屏蔽网红列表
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		String counselorId = sysUser.getId();
		kolShieldsModel.setCounselorId(counselorId);
		IPage<KolShieldsModel> pageList = kolShieldsService.pageShields(page, kolShieldsModel);
		return Result.ok(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param kolShields
	 * @return
	 */
	@AutoLog(value = "网红屏蔽名单表-"+ExamConstants.ADD)
	@Operation(summary ="网红屏蔽名单表-" +ExamConstants.ADD, description = "网红屏蔽名单表-" +ExamConstants.ADD)
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody List<KolShields> kolShields) {
		if (kolShields.isEmpty()) {
			return Result.error("请选择要屏蔽的网红");
		}
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		String counselorId = sysUser.getId();
		String counselorName = sysUser.getUsername();
		String shortName = sysUser.getShortName();

		// 获取第一个网红的产品信息用于验证
		KolShields firstKolShield = kolShields.get(0);
		String productId = firstKolShield.getProductId();

		// 验证产品ID
		if (oConvertUtils.isEmpty(productId)) {
			return Result.error("产品ID不能为空");
		}

		// 查询并验证产品是否存在，获取产品和品牌信息
		KolProduct product = productService.getById(productId);
		if (product == null) {
			return Result.error("指定的产品不存在");
		}

		String productName = product.getProductName();
		String brandId = product.getBrandId();
		String brandName = product.getBrandName();

		List<KolShields> kolShieldsSave = new ArrayList<>();
		List<String> errorMsgList = new ArrayList<>();
		List<String> successMsgList = new ArrayList<>();

		for (int i = 0; i < kolShields.size(); i++) {
			KolShields kolShield = kolShields.get(i);
			// 网红唯一ID
			String celebrityId = kolShield.getCelebrityId();
			if (oConvertUtils.isEmpty(celebrityId)) {
				errorMsgList.add("第" + (i + 1) + "个网红唯一ID不能为空");
				continue;
			}
			Integer platformType = kolShield.getPlatformType();
			if (platformType == null) {
				errorMsgList.add("第" + (i + 1) + "个平台类型不能为空");
				continue;
			}



			// 为每个网红设置完整的产品和品牌信息
			kolShield.setProductId(productId);
			kolShield.setProductName(productName);
			kolShield.setBrandId(brandId);
			kolShield.setBrandName(brandName);

			// 查询是否重复屏蔽（同一个顾问对同一个网红在同一个产品上的屏蔽）
			KolShields shield = kolShieldsService.lambdaQuery()
					.eq(KolShields::getCelebrityId, celebrityId)
					.eq(KolShields::getCounselorId, counselorId)
					.eq(KolShields::getPlatformType, platformType)
					.eq(KolShields::getProductId, productId)
					.eq(KolShields::getIsDelete, 0)
					.last("limit 1").one();
			// 如果已经屏蔽，则跳过处理
			if (shield != null) {
				successMsgList.add("第" + (i + 1) + "个网红已屏蔽，跳过处理");
				continue;
			}
			kolShield.setCounselorShortName(shortName);
			kolShield.setCounselorId(counselorId);
			kolShield.setCounselorName(counselorName);
			kolShield.setIsDelete(0);
			kolShield.setShieldTime(new Date());
			kolShield.setCreateBy(counselorName);
			kolShield.setCreateTime(new Date());
			kolShieldsSave.add(kolShield);
			successMsgList.add("第" + (i + 1) + "个网红屏蔽成功");
		}

		// 执行保存操作
		if (!kolShieldsSave.isEmpty()) {
			kolShieldsService.saveBatch(kolShieldsSave);
		}

		// 构建返回结果
		if (!errorMsgList.isEmpty()) {
			// 如果有错误，返回JSON格式的错误信息
			StringBuilder errorMsg = new StringBuilder();
			for (String msg : errorMsgList) {
				errorMsg.append(msg).append(";");
			}
			return Result.error(errorMsg.toString());
		}

		// 如果没有错误，返回成功信息
		return Result.ok("屏蔽成功");
	}
	
	/**
	 * 编辑
	 *
	 * @param kolShields
	 * @return
	 */
	@AutoLog(value = "网红屏蔽名单表-"+ExamConstants.EDIT)
	@Operation(summary ="网红屏蔽名单表-"+ExamConstants.EDIT, description = "网红屏蔽名单表-" + ExamConstants.EDIT)
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody KolShields kolShields) {
		kolShieldsService.updateById(kolShields);
		return Result.ok(ExamConstants.EDIT_SUCCESS);
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "网红屏蔽名单表-"+ExamConstants.DELETE_BY_ID)
	@Operation(summary ="网红屏蔽名单表-" +ExamConstants.DELETE_BY_ID, description = "网红屏蔽名单表-"+ExamConstants.DELETE_BY_ID)
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		kolShieldsService.removeById(id);
		return Result.ok("操作成功");
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "网红屏蔽名单表-"+ ExamConstants.DELETE_BATCH)
	@Operation(summary ="网红屏蔽名单表-" + ExamConstants.DELETE_BATCH, description = "网红屏蔽名单表-" + ExamConstants.DELETE_BATCH)
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.kolShieldsService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok(ExamConstants.DELETE_BATCH_SUCCESS);
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "网红屏蔽名单表-" + ExamConstants.QUERY_BY_ID)
	@Operation(summary ="网红屏蔽名单表-" +ExamConstants.QUERY_BY_ID, description = "网红屏蔽名单表-"+ExamConstants.QUERY_BY_ID)
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		KolShields kolShields = kolShieldsService.getById(id);
		return Result.ok(kolShields);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param kolShields
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, KolShields kolShields) {
      return super.exportXls(request, kolShields, KolShields.class, "网红屏蔽名单表");
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
      return super.importExcel(request, response, KolShields.class);
  }

}
