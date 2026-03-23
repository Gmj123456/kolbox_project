package org.jeecg.modules.kol.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.ExamConstants;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.kol.entity.KolSysUserFeishuSheet;
import org.jeecg.modules.kol.service.IKolSysUserFeishuSheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

 /**
 * @Description: 用户飞书在线表格关联表
 * @Author: dongruyang
 * @Date:   2025-09-15
 * @Version: V1.0
 */
@Slf4j
@Tag(name="用户飞书在线表格关联表")
@RestController
@RequestMapping("/kolsysuserfeishusheet")
public class KolSysUserFeishuSheetController extends JeecgController<KolSysUserFeishuSheet, IKolSysUserFeishuSheetService> {
	@Autowired
	private IKolSysUserFeishuSheetService kolSysUserFeishuSheetService;
	 @Autowired
	 private ISysBaseAPI sysBaseAPI;
	
	/**
	 * 分页列表查询
	 *
	 * @param kolSysUserFeishuSheet
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "用户飞书在线表格关联表-"+ ExamConstants.PAGE_LIST_QUERY)
	@Operation(summary ="用户飞书在线表格关联表-"+ExamConstants.PAGE_LIST_QUERY, description = "用户飞书在线表格关联表-"+ExamConstants.PAGE_LIST_QUERY)
	@GetMapping(value = "/list")
	public Result<?> queryPageList(KolSysUserFeishuSheet kolSysUserFeishuSheet,
								  @RequestParam(name = ExamConstants.PAGE_NO, defaultValue = ExamConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                  @RequestParam(name = ExamConstants.PAGE_SIZE, defaultValue = ExamConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<KolSysUserFeishuSheet> queryWrapper = QueryGenerator.initQueryWrapper(kolSysUserFeishuSheet, req.getParameterMap());
		Page<KolSysUserFeishuSheet> page = new Page<KolSysUserFeishuSheet>(pageNo, pageSize);
		IPage<KolSysUserFeishuSheet> pageList = kolSysUserFeishuSheetService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param kolSysUserFeishuSheet
	 * @return
	 */
	@AutoLog(value = "用户飞书在线表格关联表-"+ExamConstants.ADD)
	@Operation(summary ="用户飞书在线表格关联表-" +ExamConstants.ADD, description = "用户飞书在线表格关联表-" +ExamConstants.ADD)
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody KolSysUserFeishuSheet kolSysUserFeishuSheet) {
		kolSysUserFeishuSheetService.save(kolSysUserFeishuSheet);
		return Result.ok(ExamConstants.ADD_SUCCESS);
	}
	
	/**
	 * 编辑
	 *
	 * @param kolSysUserFeishuSheet
	 * @return
	 */
	@AutoLog(value = "用户飞书在线表格关联表-"+ExamConstants.EDIT)
	@Operation(summary ="用户飞书在线表格关联表-"+ExamConstants.EDIT, description = "用户飞书在线表格关联表-" + ExamConstants.EDIT)
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody KolSysUserFeishuSheet kolSysUserFeishuSheet) {
		kolSysUserFeishuSheetService.updateById(kolSysUserFeishuSheet);
		return Result.ok(ExamConstants.EDIT_SUCCESS);
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "用户飞书在线表格关联表-"+ExamConstants.DELETE_BY_ID)
	@Operation(summary ="用户飞书在线表格关联表-" +ExamConstants.DELETE_BY_ID, description = "用户飞书在线表格关联表-"+ExamConstants.DELETE_BY_ID)
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		kolSysUserFeishuSheetService.removeById(id);
		return Result.ok(ExamConstants.DELETE_SUCCESS);
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "用户飞书在线表格关联表-"+ ExamConstants.DELETE_BATCH)
	@Operation(summary ="用户飞书在线表格关联表-" + ExamConstants.DELETE_BATCH, description = "用户飞书在线表格关联表-" + ExamConstants.DELETE_BATCH)
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.kolSysUserFeishuSheetService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok(ExamConstants.DELETE_BATCH_SUCCESS);
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "用户飞书在线表格关联表-" + ExamConstants.QUERY_BY_ID)
	@Operation(summary ="用户飞书在线表格关联表-" +ExamConstants.QUERY_BY_ID, description = "用户飞书在线表格关联表-"+ExamConstants.QUERY_BY_ID)
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		KolSysUserFeishuSheet kolSysUserFeishuSheet = kolSysUserFeishuSheetService.getById(id);
		return Result.ok(kolSysUserFeishuSheet);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param kolSysUserFeishuSheet
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, KolSysUserFeishuSheet kolSysUserFeishuSheet) {
      return super.exportXls(request, kolSysUserFeishuSheet, KolSysUserFeishuSheet.class, "用户飞书在线表格关联表");
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
      return super.importExcel(request, response, KolSysUserFeishuSheet.class);
  }

	/**
	 * 用户飞书在线表格关联表通过当前用户查询私有网红上传模版
	 *
	 * @return Result<KolSysUserFeishuSheet> 查询结果
	 */
	@AutoLog(value = "用户飞书在线表格关联表-" + ExamConstants.QUERY_BY_USER)
	@Operation(summary = "用户飞书在线表格关联表-" + ExamConstants.QUERY_BY_USER, 
			description = "用户飞书在线表格关联表-" + ExamConstants.QUERY_BY_USER)
	@GetMapping(value = "/queryByUser")
	public Result<?> queryByUser(@RequestParam(name="spreadSheetType",required=true) String spreadSheetType) {
		try {
			// 获取当前用户ID
			String userId = getUserIdByToken();
			if (userId == null || userId.trim().isEmpty()) {
				log.warn("获取用户ID失败，token可能无效");
				return Result.error("用户身份验证失败");
			}

			// 获取用户信息
			LoginUser sysUser = sysBaseAPI.getUserById(userId);
			if (sysUser == null) {
				log.warn("用户不存在，userId: {}", userId);
				return Result.error("用户不存在");
			}

			// 验证用户类型权限
			/*if (!sysUser.getUserType().equals(UserType.CELEBRITY_COUNSELOR.getCode())) {
				log.warn("用户权限不足，当前用户类型: {}, 需要类型: {}",
						sysUser.getUserType(), UserType.CELEBRITY_COUNSELOR.getCode());
				return Result.error("当前用户无法获取私有网红模版");
			}*/
			// 构建查询条件
			LambdaQueryWrapper<KolSysUserFeishuSheet> queryWrapper = new LambdaQueryWrapper<>();
			queryWrapper.eq(KolSysUserFeishuSheet::getSysUserId, sysUser.getId())
					.eq(KolSysUserFeishuSheet::getSpreadSheetType, spreadSheetType)
					.orderByDesc(KolSysUserFeishuSheet::getCreateTime)
					.last("LIMIT 1");

			// 查询数据
			KolSysUserFeishuSheet result = kolSysUserFeishuSheetService.getOne(queryWrapper);
			if (result == null) {
				log.info("用户 {} 尚未配置私有网红飞书表格模版", sysUser.getId());
				// 返回空对象而不是null，便于前端处理
				result = new KolSysUserFeishuSheet();
			}

			return Result.ok(result);

		} catch (Exception e) {
			log.error("查询用户飞书表格配置失败", e);
			return Result.error("查询失败，请稍后重试");
		}
	}
}
