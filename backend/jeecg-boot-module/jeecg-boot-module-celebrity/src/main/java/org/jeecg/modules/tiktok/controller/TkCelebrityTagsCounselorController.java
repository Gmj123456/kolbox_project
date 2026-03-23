package org.jeecg.modules.tiktok.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.SystemConstants;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.util.PageUtil;
import org.jeecg.modules.kol.model.KolSearchModel;
import org.jeecg.modules.kol.model.KolTagAllotModel;
import org.jeecg.modules.tiktok.entity.TiktokCelebrityTagsCounselor;
import org.jeecg.modules.tiktok.service.ITkCelebrityTagsCounselorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import static org.jeecg.common.constant.SystemConstants.LIST_URL;

/**
* @Description: tk标签网红顾问表
* @Author: xyc
* @Date:   2024-12-26 18:10:15
* @Version: V1.0
*/
@Slf4j
@Tag(name="tk标签网红顾问分配表")
@RestController
@RequestMapping("/tiktok/tkTagsCounselor")
public class TkCelebrityTagsCounselorController extends JeecgController<TiktokCelebrityTagsCounselor, ITkCelebrityTagsCounselorService> {
   @Autowired
   private ITkCelebrityTagsCounselorService tkCelebrityTagsCounselorService;

   /**
    * 分页列表查询
    *
    * @param searchModel
    * @param pageNo
    * @param pageSize
    * @param req
    * @return
    */
   @AutoLog(value = "tk标签网红顾问表-"+ SystemConstants.PAGE_LIST_QUERY)
   @Operation(summary ="tk标签网红顾问表-"+ SystemConstants.PAGE_LIST_QUERY, description = "tk标签网红顾问表-"+ SystemConstants.PAGE_LIST_QUERY)
   @PostMapping(value = LIST_URL)
   public Result<?> queryPageList(@RequestBody KolSearchModel searchModel,
                                  @RequestParam(name = SystemConstants.PAGE_NO, defaultValue = SystemConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                  @RequestParam(name = SystemConstants.PAGE_SIZE, defaultValue = SystemConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
                                  HttpServletRequest req) {
       Page<KolTagAllotModel> page = PageUtil.getOrderItems(pageNo, pageSize, req);
       IPage<KolTagAllotModel> pageList = tkCelebrityTagsCounselorService.getTagAllottedList(page, searchModel);
       return Result.ok(pageList);
   }



}
