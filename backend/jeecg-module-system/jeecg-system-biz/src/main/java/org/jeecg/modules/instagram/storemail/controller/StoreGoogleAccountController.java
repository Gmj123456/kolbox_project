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
import org.jeecg.modules.instagram.storemail.entity.StoreGoogleAccount;
import org.jeecg.modules.instagram.storemail.service.IStoreGoogleAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

 /**
 * @Description: 谷歌账号
 * @Author: jeecg-boot
 * @Date:   2020-04-23
 * @Version: V1.0
 */
@Slf4j
@Tag(name ="谷歌账号")
@RestController
@RequestMapping("/instagram/storeGoogleAccount")
public class StoreGoogleAccountController extends JeecgController<StoreGoogleAccount, IStoreGoogleAccountService> {
	@Autowired
	private IStoreGoogleAccountService storeGoogleAccountService;
	
	/**
	 * 分页列表查询
	 *
	 * @param storeGoogleAccount
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "谷歌账号-分页列表查询")
	@Operation(summary ="谷歌账号-分页列表查询", description = "谷歌账号-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(StoreGoogleAccount storeGoogleAccount,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<StoreGoogleAccount> queryWrapper = QueryGenerator.initQueryWrapper(storeGoogleAccount, req.getParameterMap());
		Page<StoreGoogleAccount> page = new Page<StoreGoogleAccount>(pageNo, pageSize);
		IPage<StoreGoogleAccount> pageList = storeGoogleAccountService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param storeGoogleAccount
	 * @return
	 */
	@AutoLog(value = "谷歌账号-添加")
	@Operation(summary ="谷歌账号-添加", description = "谷歌账号-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody StoreGoogleAccount storeGoogleAccount) {
		storeGoogleAccountService.save(storeGoogleAccount);
		return Result.ok("添加成功！");
	}
	
	/**
	 * 编辑
	 *
	 * @param storeGoogleAccount
	 * @return
	 */
	@AutoLog(value = "谷歌账号-编辑")
	@Operation(summary ="谷歌账号-编辑", description = "谷歌账号-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody StoreGoogleAccount storeGoogleAccount) {
		storeGoogleAccountService.updateById(storeGoogleAccount);
		return Result.ok("编辑成功!");
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "谷歌账号-通过id删除")
	@Operation(summary ="谷歌账号-通过id删除", description = "谷歌账号-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		storeGoogleAccountService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "谷歌账号-批量删除")
	@Operation(summary ="谷歌账号-批量删除", description = "谷歌账号-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.storeGoogleAccountService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "谷歌账号-通过id查询")
	@Operation(summary ="谷歌账号-通过id查询", description = "谷歌账号-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		StoreGoogleAccount storeGoogleAccount = storeGoogleAccountService.getById(id);
		return Result.ok(storeGoogleAccount);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param storeGoogleAccount
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, StoreGoogleAccount storeGoogleAccount) {
      return super.exportXls(request, storeGoogleAccount, StoreGoogleAccount.class, "谷歌账号");
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
      return super.importExcel(request, response, StoreGoogleAccount.class);
  }

}
