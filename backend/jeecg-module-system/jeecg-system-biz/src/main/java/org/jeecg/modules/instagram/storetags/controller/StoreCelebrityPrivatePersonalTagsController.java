package org.jeecg.modules.instagram.storetags.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.SystemConstants;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.instagram.storetags.entity.StoreCelebrityPrivatePersonalTags;
import org.jeecg.modules.instagram.storetags.service.IStoreCelebrityPrivatePersonalTagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
* @Description: 私有网红个性标签关联表
* @Author: nqr
* @Date:   2025-05-07
* @Version: V1.0
*/
@Slf4j
@Tag(name ="私有网红个性标签关联表")
@RestController
@RequestMapping("/privatePersonalTags")
public class StoreCelebrityPrivatePersonalTagsController extends JeecgController<StoreCelebrityPrivatePersonalTags, IStoreCelebrityPrivatePersonalTagsService> {
   @Autowired
   private IStoreCelebrityPrivatePersonalTagsService storeCelebrityPrivatePersonalTagsService;

   /**
    * 分页列表查询
    *
    * @param storeCelebrityPrivatePersonalTags
    * @param pageNo
    * @param pageSize
    * @param req
    * @return
    */
   @AutoLog(value = "私有网红个性标签关联表-"+SystemConstants.PAGE_LIST_QUERY)
   @Operation(summary ="私有网红个性标签关联表-"+SystemConstants.PAGE_LIST_QUERY, description = "私有网红个性标签关联表-"+SystemConstants.PAGE_LIST_QUERY)
   @GetMapping(value = "/list")
   public Result<?> queryPageList(StoreCelebrityPrivatePersonalTags storeCelebrityPrivatePersonalTags,
                                 @RequestParam(name = SystemConstants.PAGE_NO, defaultValue = SystemConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                 @RequestParam(name = SystemConstants.PAGE_SIZE, defaultValue = SystemConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
                                  HttpServletRequest req) {
       QueryWrapper<StoreCelebrityPrivatePersonalTags> queryWrapper = QueryGenerator.initQueryWrapper(storeCelebrityPrivatePersonalTags, req.getParameterMap());
       Page<StoreCelebrityPrivatePersonalTags> page = new Page<StoreCelebrityPrivatePersonalTags>(pageNo, pageSize);
       IPage<StoreCelebrityPrivatePersonalTags> pageList = storeCelebrityPrivatePersonalTagsService.page(page, queryWrapper);
       return Result.ok(pageList);
   }

   /**
    * 添加
    *
    * @param storeCelebrityPrivatePersonalTags
    * @return
    */
   @AutoLog(value = "私有网红个性标签关联表-"+SystemConstants.ADD)
   @Operation(summary ="私有网红个性标签关联表-" +SystemConstants.ADD, description = "私有网红个性标签关联表-" +SystemConstants.ADD)
   @PostMapping(value = "/add")
   public Result<?> add(@RequestBody StoreCelebrityPrivatePersonalTags storeCelebrityPrivatePersonalTags) {
       storeCelebrityPrivatePersonalTagsService.save(storeCelebrityPrivatePersonalTags);
       return Result.ok(SystemConstants.ADD_SUCCESS);
   }

   /**
    * 编辑
    *
    * @param storeCelebrityPrivatePersonalTags
    * @return
    */
   @AutoLog(value = "私有网红个性标签关联表-"+SystemConstants.EDIT)
   @Operation(summary ="私有网红个性标签关联表-"+SystemConstants.EDIT, description = "私有网红个性标签关联表-" + SystemConstants.EDIT)
   @PutMapping(value = "/edit")
   public Result<?> edit(@RequestBody StoreCelebrityPrivatePersonalTags storeCelebrityPrivatePersonalTags) {
       storeCelebrityPrivatePersonalTagsService.updateById(storeCelebrityPrivatePersonalTags);
       return Result.ok(SystemConstants.EDIT_SUCCESS);
   }

   /**
    * 通过id删除
    *
    * @param id
    * @return
    */
   @AutoLog(value = "私有网红个性标签关联表-"+SystemConstants.DELETE_BY_ID)
   @Operation(summary ="私有网红个性标签关联表-" +SystemConstants.DELETE_BY_ID, description = "私有网红个性标签关联表-"+SystemConstants.DELETE_BY_ID)
   @DeleteMapping(value = "/delete")
   public Result<?> delete(@RequestParam(name="id",required=true) String id) {
       storeCelebrityPrivatePersonalTagsService.removeById(id);
       return Result.ok(SystemConstants.DELETE_SUCCESS);
   }

   /**
    * 批量删除
    *
    * @param ids
    * @return
    */
   @AutoLog(value = "私有网红个性标签关联表-"+ SystemConstants.DELETE_BATCH)
   @Operation(summary ="私有网红个性标签关联表-" + SystemConstants.DELETE_BATCH, description = "私有网红个性标签关联表-" + SystemConstants.DELETE_BATCH)
   @DeleteMapping(value = "/deleteBatch")
   public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
       this.storeCelebrityPrivatePersonalTagsService.removeByIds(Arrays.asList(ids.split(",")));
       return Result.ok(SystemConstants.DELETE_BATCH_SUCCESS);
   }

   /**
    * 通过id查询
    *
    * @param id
    * @return
    */
   @AutoLog(value = "私有网红个性标签关联表-" + SystemConstants.QUERY_BY_ID)
   @Operation(summary ="私有网红个性标签关联表-" +SystemConstants.QUERY_BY_ID, description = "私有网红个性标签关联表-"+SystemConstants.QUERY_BY_ID)
   @GetMapping(value = "/queryById")
   public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
       StoreCelebrityPrivatePersonalTags storeCelebrityPrivatePersonalTags = storeCelebrityPrivatePersonalTagsService.getById(id);
       return Result.ok(storeCelebrityPrivatePersonalTags);
   }

 /**
  * 导出excel
  *
  * @param request
  * @param storeCelebrityPrivatePersonalTags
  */
 @RequestMapping(value = "/exportXls")
 public ModelAndView exportXls(HttpServletRequest request, StoreCelebrityPrivatePersonalTags storeCelebrityPrivatePersonalTags) {
     return super.exportXls(request, storeCelebrityPrivatePersonalTags, StoreCelebrityPrivatePersonalTags.class, "私有网红个性标签关联表");
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
     return super.importExcel(request, response, StoreCelebrityPrivatePersonalTags.class);
 }

}
