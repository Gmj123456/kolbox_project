package org.jeecg.modules.instagram.storecelebrity.controller;

import java.util.Arrays;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.enums.UserType;
import org.jeecg.common.constant.enums.YesNoStatus;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrity;
import org.jeecg.modules.instagram.storecelebrity.model.StoreCelebrityFeedbackModel;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityService;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityFeedback;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityFeedbackService;
import java.util.Date;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

 /**
 * @Description: 反馈功能
 * @Author: jeecg-boot
 * @Date:   2021-03-01
 * @Version: V1.0
 */
@Slf4j
@Tag(name ="反馈功能")
@RestController
@RequestMapping("/storecelebrityfeedback/storeCelebrityFeedback")
public class StoreCelebrityFeedbackController extends JeecgController<StoreCelebrityFeedback, IStoreCelebrityFeedbackService> {
	@Autowired
	private IStoreCelebrityFeedbackService storeCelebrityFeedbackService;
	 @Autowired
	 private ISysUserService sysUserService;
	 @Autowired
	 private IStoreCelebrityService storeCelebrityService;
	 /**
	 * 分页列表查询
	 *
	 * @param storeCelebrityFeedbackModel
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "反馈功能-分页列表查询")
	@Operation(summary ="反馈功能-分页列表查询", description = "反馈功能-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(StoreCelebrityFeedbackModel storeCelebrityFeedbackModel,
                                   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                   HttpServletRequest req) {

		Page<StoreCelebrityFeedback> page = new Page<StoreCelebrityFeedback>(pageNo, pageSize);
		IPage<StoreCelebrityFeedback> pageList = storeCelebrityFeedbackService.feedbackPageList(page, storeCelebrityFeedbackModel);
		return Result.ok(pageList);
	}


	 /**
	  * 功能描述:根据网红id获取反馈列表信息
	  *
	  */
	 @RequestMapping(value = "/getCelebrityFeedback", method = RequestMethod.GET)
	 public Result<?> getCelebrityFeedback(@RequestParam(name = "celebrityId", required = false) String celebrityId) {
		 List<StoreCelebrityFeedback> list = storeCelebrityFeedbackService.getCelebrityFeedback(celebrityId);
		 return Result.ok(list);
	 }
	
	/**
	 * 添加
	 *
	 * @param storeCelebrityFeedback
	 * @return
	 */
	@AutoLog(value = "反馈功能-添加")
	@Operation(summary ="反馈功能-添加", description = "反馈功能-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody StoreCelebrityFeedback storeCelebrityFeedback) {
		String userId = getUserIdByToken();
		storeCelebrityFeedback.setCreateBy(userId);
		storeCelebrityFeedback.setCreateTime(new Date());
		SysUser sysUser = sysUserService.getById(userId);
		if (sysUser.getUserType()==1){
			storeCelebrityFeedback.setCreateUserType(UserType.SELLER.getCode()); //商家
		}
		storeCelebrityFeedback.setCreateUserType(UserType.PLATFORM.getCode()); // 除商家外所有人都是后台人员

		LambdaQueryWrapper<StoreCelebrityFeedback> lambdaQueryWrapper = new LambdaQueryWrapper<>();
		lambdaQueryWrapper.eq(StoreCelebrityFeedback::getCreateBy,storeCelebrityFeedback.getCreateBy());
		lambdaQueryWrapper.eq(StoreCelebrityFeedback::getCelebrityId,storeCelebrityFeedback.getCelebrityId());
		List<StoreCelebrityFeedback> list = storeCelebrityFeedbackService.list(lambdaQueryWrapper);
		if (list.size()>0){
			storeCelebrityFeedback.setId(list.get(0).getId());
/*			storeCelebrityFeedback.setUpdateBy(userId);
			storeCelebrityFeedback.setUpdateTime(new Date());*/
			storeCelebrityFeedbackService.updateById(storeCelebrityFeedback);

		}else {
			storeCelebrityFeedbackService.save(storeCelebrityFeedback);
		}

		return Result.ok("添加成功！");
	}

	 /**
	  * 编辑
	  *
	  * @param storeCelebrity
	  * @return
	  */
	 @AutoLog(value = "反馈功能-编辑")
	 @Operation(summary ="反馈功能-编辑", description = "反馈功能-编辑")
	 @PutMapping(value = "/editCelebrity")
	 public Result<?> editCelebrity(@RequestBody StoreCelebrity storeCelebrity) {
		 boolean byId = storeCelebrityService.updateById(storeCelebrity);
		 if (byId){
			String userId= getUserIdByToken();
			 String userName=  getUserNameByToken();
			 LambdaQueryWrapper<StoreCelebrityFeedback> lambdaQueryWrapper = new LambdaQueryWrapper<>();
			 lambdaQueryWrapper.eq(StoreCelebrityFeedback::getCelebrityId,storeCelebrity.getId());
			 List<StoreCelebrityFeedback> storeCelebrityFeedbackList = storeCelebrityFeedbackService.list(lambdaQueryWrapper);
			 if(storeCelebrityFeedbackList.size()>0){
				 for(StoreCelebrityFeedback storeCelebrityFeedback: storeCelebrityFeedbackList){
					 storeCelebrityFeedback.setIsDispose(YesNoStatus.YES.getCode());
					 storeCelebrityFeedback.setDisposeUserId(userId);
					storeCelebrityFeedback.setDisposeUserName(userName);
					 storeCelebrityFeedbackService.updateById(storeCelebrityFeedback);
				 }
			 }
		 }
		 return Result.ok("编辑成功!");
	 }




	 /**
	 * 编辑
	 *
	 * @param storeCelebrityFeedback
	 * @return
	 */
	@AutoLog(value = "反馈功能-编辑")
	@Operation(summary ="反馈功能-编辑", description = "反馈功能-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody StoreCelebrityFeedback storeCelebrityFeedback) {
		 storeCelebrityFeedbackService.updateById(storeCelebrityFeedback);

		return Result.ok("编辑成功!");
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "反馈功能-通过id删除")
	@Operation(summary ="反馈功能-通过id删除", description = "反馈功能-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		storeCelebrityFeedbackService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "反馈功能-批量删除")
	@Operation(summary ="反馈功能-批量删除", description = "反馈功能-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.storeCelebrityFeedbackService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "反馈功能-通过id查询")
	@Operation(summary ="反馈功能-通过id查询", description = "反馈功能-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		StoreCelebrityFeedback storeCelebrityFeedback = storeCelebrityFeedbackService.getById(id);
		return Result.ok(storeCelebrityFeedback);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param storeCelebrityFeedback
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, StoreCelebrityFeedback storeCelebrityFeedback) {
      return super.exportXls(request, storeCelebrityFeedback, StoreCelebrityFeedback.class, "反馈功能");
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
      return super.importExcel(request, response, StoreCelebrityFeedback.class);
  }

}
