package org.jeecg.modules.instagram.storecelebrity.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityFollowup;
import org.jeecg.modules.instagram.storecelebrity.model.StoreCelebrityFollowupModel;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityFollowupService;
import org.jeecg.modules.instagram.storeseller.storeuser.entity.FollowupLog;
import org.jeecg.modules.instagram.storeseller.storeuser.service.IFollowupLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;

/**
 * @Description: 物流管理
 * @Author: jeecg-boot
 * @Date: 2020-05-25
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "物流管理")
@RestController
@RequestMapping("/follow")
public class StoreCelebrityFollowupController extends JeecgController<StoreCelebrityFollowup, IStoreCelebrityFollowupService> {
    @Autowired
    private IStoreCelebrityFollowupService storeCelebrityFollowupService;

    @Autowired
    private IFollowupLogService followupLogService;

    /**
     * 分页列表查询
     *
     * @param storeCelebrityFollowupModel
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "物流管理-分页列表查询")
@Operation(summary = "物流管理-分页列表查询", description = "物流管理-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StoreCelebrityFollowupModel storeCelebrityFollowupModel,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        Page<StoreCelebrityFollowupModel> page = new Page<>(pageNo, pageSize);
        IPage<StoreCelebrityFollowupModel> pageList = storeCelebrityFollowupService.getFollowingUpList(page, storeCelebrityFollowupModel);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param storeCelebrityFollowup
     * @return
     */
    @AutoLog(value = "物流管理-添加")
@Operation(summary = "物流管理-添加", description = "物流管理-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody StoreCelebrityFollowup storeCelebrityFollowup) {
        storeCelebrityFollowupService.save(storeCelebrityFollowup);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param storeCelebrityFollowup
     * @return
     */
    @AutoLog(value = "物流管理-编辑")
@Operation(summary = "物流管理-编辑", description = "物流管理-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody StoreCelebrityFollowup storeCelebrityFollowup) {
        //存在订单号，默认发货状态为已发货
       /* if (oConvertUtils.isNotEmpty(storeCelebrityFollowup.getOrderId())) {
            storeCelebrityFollowup.setIsShipment(1);
        }*/
        Integer followUpStatus = storeCelebrityFollowup.getFollowUpStatus();
        //添加到跟进历史表
        if (followUpStatus != 0) {
            FollowupLog followupLog = new FollowupLog();
            followupLog.setFollowupId(storeCelebrityFollowup.getId());
            followupLog.setFollowUpStatus(storeCelebrityFollowup.getFollowUpStatus());
            followupLog.setFollowUpDate(storeCelebrityFollowup.getFollowUpDate());
            followupLog.setFollowUpContent(storeCelebrityFollowup.getFollowUpRemark());
            followupLog.setCreateBy(getUserNameByToken());
            followupLog.setCreateTime(new Date());
            followupLogService.save(followupLog);
        }
        storeCelebrityFollowupService.updateById(storeCelebrityFollowup);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "物流管理-通过id删除")
@Operation(summary = "物流管理-通过id删除", description = "物流管理-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        storeCelebrityFollowupService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "物流管理-批量删除")
@Operation(summary = "物流管理-批量删除", description = "物流管理-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.storeCelebrityFollowupService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "物流管理-通过id查询")
@Operation(summary = "物流管理-通过id查询", description = "物流管理-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        StoreCelebrityFollowup storeCelebrityFollowup = storeCelebrityFollowupService.getById(id);
        return Result.ok(storeCelebrityFollowup);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param storeCelebrityFollowup
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StoreCelebrityFollowup storeCelebrityFollowup) {
        return super.exportXls(request, storeCelebrityFollowup, StoreCelebrityFollowup.class, "物流管理");
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
        return super.importExcel(request, response, StoreCelebrityFollowup.class);
    }

}
