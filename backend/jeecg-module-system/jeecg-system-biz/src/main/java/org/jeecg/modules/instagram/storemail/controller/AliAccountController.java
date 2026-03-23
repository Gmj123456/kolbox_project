package org.jeecg.modules.instagram.storemail.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.instagram.storemail.entity.AliAccount;
import org.jeecg.modules.instagram.storemail.service.IAliAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

 /**
 * @Description: 阿里云账号管理
 * @Author: jeecg-boot
 * @Date:   2020-11-27
 * @Version: V1.0
 */
@Slf4j
@Tag(name ="阿里云账号管理")
@RestController
@RequestMapping("/aliAccount")
public class AliAccountController extends JeecgController<AliAccount, IAliAccountService> {
	@Autowired
	private IAliAccountService aliAccountService;
	
	/**
	 * 分页列表查询
	 *
	 * @param aliAccount
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "阿里云账号管理-分页列表查询")
	@Operation(summary ="阿里云账号管理-分页列表查询", description = "阿里云账号管理-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(AliAccount aliAccount,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<AliAccount> queryWrapper = QueryGenerator.initQueryWrapper(aliAccount, req.getParameterMap());
		Page<AliAccount> page = new Page<AliAccount>(pageNo, pageSize);
		IPage<AliAccount> pageList = aliAccountService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param aliAccount
	 * @return
	 */
	@AutoLog(value = "阿里云账号管理-添加")
	@Operation(summary ="阿里云账号管理-添加", description = "阿里云账号管理-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody AliAccount aliAccount) {
		aliAccountService.save(aliAccount);
		return Result.ok("添加成功！");
	}
	
	/**
	 * 编辑
	 *
	 * @param aliAccount
	 * @return
	 */
	@AutoLog(value = "阿里云账号管理-编辑")
	@Operation(summary ="阿里云账号管理-编辑", description = "阿里云账号管理-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody AliAccount aliAccount) {
		aliAccountService.updateById(aliAccount);
		return Result.ok("编辑成功!");
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "阿里云账号管理-通过id删除")
	@Operation(summary ="阿里云账号管理-通过id删除", description = "阿里云账号管理-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		aliAccountService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "阿里云账号管理-批量删除")
	@Operation(summary ="阿里云账号管理-批量删除", description = "阿里云账号管理-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.aliAccountService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "阿里云账号管理-通过id查询")
	@Operation(summary ="阿里云账号管理-通过id查询", description = "阿里云账号管理-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		AliAccount aliAccount = aliAccountService.getById(id);
		return Result.ok(aliAccount);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param aliAccount
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, AliAccount aliAccount) {
      return super.exportXls(request, aliAccount, AliAccount.class, "阿里云账号管理");
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
      return super.importExcel(request, response, AliAccount.class);
  }

}
