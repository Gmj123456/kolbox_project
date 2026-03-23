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
import org.jeecg.modules.instagram.storemail.entity.DomainAccount;
import org.jeecg.modules.instagram.storemail.service.IDomainAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;

 /**
 * @Description: 域名账号管理
 * @Author: jeecg-boot
 * @Date:   2020-11-27
 * @Version: V1.0
 */
@Slf4j
@Tag(name ="域名账号管理")
@RestController
@RequestMapping("/domainAccount")
public class DomainAccountController extends JeecgController<DomainAccount, IDomainAccountService> {
	@Autowired
	private IDomainAccountService domainAccountService;
	
	/**
	 * 分页列表查询
	 *
	 * @param domainAccount
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "域名账号管理-分页列表查询")
	@Operation(summary ="域名账号管理-分页列表查询", description = "域名账号管理-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(DomainAccount domainAccount,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<DomainAccount> queryWrapper = QueryGenerator.initQueryWrapper(domainAccount, req.getParameterMap());
		Page<DomainAccount> page = new Page<>(pageNo, pageSize);
		IPage<DomainAccount> pageList = domainAccountService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param domainAccount
	 * @return
	 */
	@AutoLog(value = "域名账号管理-添加")
	@Operation(summary ="域名账号管理-添加", description = "域名账号管理-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody DomainAccount domainAccount) {
		domainAccountService.save(domainAccount);
		return Result.ok("添加成功！");
	}
	
	/**
	 * 编辑
	 *
	 * @param domainAccount
	 * @return
	 */
	@AutoLog(value = "域名账号管理-编辑")
	@Operation(summary ="域名账号管理-编辑", description = "域名账号管理-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody DomainAccount domainAccount) {
		domainAccountService.updateById(domainAccount);
		return Result.ok("编辑成功!");
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "域名账号管理-通过id删除")
	@Operation(summary ="域名账号管理-通过id删除", description = "域名账号管理-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		domainAccountService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "域名账号管理-批量删除")
	@Operation(summary ="域名账号管理-批量删除", description = "域名账号管理-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.domainAccountService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}

}
