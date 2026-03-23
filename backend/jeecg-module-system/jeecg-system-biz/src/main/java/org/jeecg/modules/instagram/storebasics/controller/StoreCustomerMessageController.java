package org.jeecg.modules.instagram.storebasics.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.enums.YesNoStatus;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.instagram.storebasics.entity.StoreCustomerMessage;
import org.jeecg.modules.instagram.storebasics.service.IStoreCustomerMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

 /**
 * @Description: 客户信息表
 * @Author: zhoushen
 * @Date:   2020-12-19
 * @Version: V1.0
 */
@Slf4j
@Tag(name ="客户信息表")
@RestController
@RequestMapping("/storecustomermessage/storeCustomerMessage")
public class StoreCustomerMessageController extends JeecgController<StoreCustomerMessage, IStoreCustomerMessageService> {
	@Autowired
	private IStoreCustomerMessageService storeCustomerMessageService;
	
	/**
	 * 分页列表查询
	 *
	 * @param storeCustomerMessage
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "客户信息表-分页列表查询")
	@Operation(summary ="客户信息表-分页列表查询", description = "客户信息表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(StoreCustomerMessage storeCustomerMessage,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		Page<StoreCustomerMessage> page = new Page<StoreCustomerMessage>(pageNo, pageSize);
		IPage<StoreCustomerMessage> pageList = storeCustomerMessageService.queryAll(page, storeCustomerMessage);
		return Result.ok(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param storeCustomerMessage
	 * @return
	 */
	@AutoLog(value = "客户信息表-添加")
	@Operation(summary ="客户信息表-添加", description = "客户信息表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody StoreCustomerMessage storeCustomerMessage) {
		storeCustomerMessage.setCreateBy(getUserNameByToken());
		storeCustomerMessage.setCreateTime(new Date());
		storeCustomerMessageService.save(storeCustomerMessage);
		return Result.ok("添加成功！");
	}
	
	/**
	 * 编辑
	 *
	 * @param storeCustomerMessage
	 * @return
	 */
	@AutoLog(value = "客户信息表-编辑")
	@Operation(summary ="客户信息表-编辑", description = "客户信息表-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody StoreCustomerMessage storeCustomerMessage) {
		storeCustomerMessage.setUpdateBy(getUserNameByToken());
		storeCustomerMessage.setUpdateTime(new Date());
		storeCustomerMessageService.updateById(storeCustomerMessage);
		return Result.ok("编辑成功!");
	}


	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "客户信息表-通过id查询")
	@Operation(summary ="客户信息表-通过id查询", description = "客户信息表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		StoreCustomerMessage storeCustomerMessage = storeCustomerMessageService.getById(id);
		return Result.ok(storeCustomerMessage);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param storeCustomerMessage
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, StoreCustomerMessage storeCustomerMessage) {
      return super.exportXls(request, storeCustomerMessage, StoreCustomerMessage.class, "客户信息表");
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
      return super.importExcel(request, response, StoreCustomerMessage.class);
  }


	 /**
	  * 标记已读
	  *
	  * @param ids
	  * @return
	  */
	 @AutoLog(value = "客户信息表-标记已读")
	 @Operation(summary ="客户信息表-标记已读", description = "客户信息表-标记已读")
	 @GetMapping(value = "/remarkIsReadBatch")
	 public Result<?> remarkIsReadBatch(@RequestParam(name="ids",required=true) String ids) {
		 List<String> idList = Arrays.asList(ids.split(","));
		 List<StoreCustomerMessage> storeCustomerMessageList = new ArrayList<>();
		 idList.forEach(id -> {
			 StoreCustomerMessage storeCustomerMessage = storeCustomerMessageService.getById(id);
			 if(storeCustomerMessage.getIsRead() != YesNoStatus.YES.getCode()){
				 storeCustomerMessage.setIsRead(YesNoStatus.YES.getCode());
				 storeCustomerMessage.setReadUserId(getUserIdByToken());
				 storeCustomerMessage.setReadUserName(getUserNameByToken());
				 storeCustomerMessage.setUpdateBy(getUserNameByToken());
				 storeCustomerMessage.setUpdateTime(new Date());
				 storeCustomerMessageList.add(storeCustomerMessage);
			 }
		 });

		 if(storeCustomerMessageList != null && storeCustomerMessageList.size() > 0){
			 storeCustomerMessageService.updateBatchById(storeCustomerMessageList);
		 }
		 return Result.ok("标记成功!");
	 }


	 /**
	  * 删除
	  *
	  * @param ids
	  * @return
	  */
	 @AutoLog(value = "客户信息表-删除")
	 @Operation(summary ="客户信息表-删除", description = "客户信息表-删除")
	 @GetMapping(value = "/deleteBatch")
	 public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		 List<String> idList = Arrays.asList(ids.split(","));
		 List<StoreCustomerMessage> storeCustomerMessageList = new ArrayList<>();
		 idList.forEach(id -> {
			 StoreCustomerMessage storeCustomerMessage = storeCustomerMessageService.getById(id);
			 storeCustomerMessage.setIsDel(YesNoStatus.YES.getCode());
			 storeCustomerMessage.setUpdateBy(getUserNameByToken());
			 storeCustomerMessage.setUpdateTime(new Date());
			 storeCustomerMessageList.add(storeCustomerMessage);
		 });

		 storeCustomerMessageService.updateBatchById(storeCustomerMessageList);
		 return Result.ok("删除成功!");
	 }

}
