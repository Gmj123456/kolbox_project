package org.jeecg.modules.instagram.storecelebrity.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.ExamConstants;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityCounselorChangeDetail;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityCounselorChangeLog;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivate;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivateCounselor;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityCounselorChangeDetailService;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityCounselorChangeLogService;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityPrivateCounselorService;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityPrivateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 网红顾问变更日志明细
 * @Author: jeecg-boot
 * @Date: 2025-10-30
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "网红顾问变更日志明细")
@RestController
@RequestMapping("/counselorChangeDetail")
public class StoreCelebrityCounselorChangeDetailController extends JeecgController<StoreCelebrityCounselorChangeDetail, IStoreCelebrityCounselorChangeDetailService> {
    @Autowired
    private IStoreCelebrityCounselorChangeDetailService changeDetailService;
    @Autowired
    private IStoreCelebrityCounselorChangeLogService changeLogService;
    @Resource
    private IStoreCelebrityPrivateService celebrityPrivateService;
    @Resource
    private IStoreCelebrityPrivateCounselorService privateCounselorService;

    /**
     * 分页列表查询
     *
     * @param storeCelebrityCounselorChangeDetail
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "网红顾问变更日志明细-" + ExamConstants.PAGE_LIST_QUERY)
@Operation(summary = "网红顾问变更日志明细-" + ExamConstants.PAGE_LIST_QUERY, description = "网红顾问变更日志明细-" + ExamConstants.PAGE_LIST_QUERY)
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StoreCelebrityCounselorChangeDetail storeCelebrityCounselorChangeDetail,
                                   @RequestParam(name = ExamConstants.PAGE_NO, defaultValue = ExamConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                   @RequestParam(name = ExamConstants.PAGE_SIZE, defaultValue = ExamConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<StoreCelebrityCounselorChangeDetail> queryWrapper = QueryGenerator.initQueryWrapper(storeCelebrityCounselorChangeDetail, req.getParameterMap());
        Page<StoreCelebrityCounselorChangeDetail> page = new Page<StoreCelebrityCounselorChangeDetail>(pageNo, pageSize);
        IPage<StoreCelebrityCounselorChangeDetail> pageList = changeDetailService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @GetMapping(value = "/listAll")
    public Result<?> queryList(StoreCelebrityCounselorChangeDetail changeDetail) {
        List<Map<String, Object>> changeDetails = changeDetailService.queryList(changeDetail);
        return Result.ok(changeDetails);
    }

    /**
     * 添加
     *
     * @param storeCelebrityCounselorChangeDetail
     * @return
     */
    @AutoLog(value = "网红顾问变更日志明细-" + ExamConstants.ADD)
@Operation(summary = "网红顾问变更日志明细-" + ExamConstants.ADD, description = "网红顾问变更日志明细-" + ExamConstants.ADD)
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody StoreCelebrityCounselorChangeDetail storeCelebrityCounselorChangeDetail) {
        changeDetailService.save(storeCelebrityCounselorChangeDetail);
        return Result.ok(ExamConstants.ADD_SUCCESS);
    }

    /**
     * 编辑
     *
     * @param storeCelebrityCounselorChangeDetail
     * @return
     */
    @AutoLog(value = "网红顾问变更日志明细-" + ExamConstants.EDIT)
@Operation(summary = "网红顾问变更日志明细-" + ExamConstants.EDIT, description = "网红顾问变更日志明细-" + ExamConstants.EDIT)
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody StoreCelebrityCounselorChangeDetail storeCelebrityCounselorChangeDetail) {
        changeDetailService.updateById(storeCelebrityCounselorChangeDetail);
        return Result.ok(ExamConstants.EDIT_SUCCESS);
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "网红顾问变更日志明细-" + ExamConstants.DELETE_BY_ID)
@Operation(summary = "网红顾问变更日志明细-" + ExamConstants.DELETE_BY_ID, description = "网红顾问变更日志明细-" + ExamConstants.DELETE_BY_ID)
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        changeDetailService.removeById(id);
        return Result.ok(ExamConstants.DELETE_SUCCESS);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "网红顾问变更日志明细-" + ExamConstants.DELETE_BATCH)
@Operation(summary = "网红顾问变更日志明细-" + ExamConstants.DELETE_BATCH, description = "网红顾问变更日志明细-" + ExamConstants.DELETE_BATCH)
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.changeDetailService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok(ExamConstants.DELETE_BATCH_SUCCESS);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "网红顾问变更日志明细-" + ExamConstants.QUERY_BY_ID)
@Operation(summary = "网红顾问变更日志明细-" + ExamConstants.QUERY_BY_ID, description = "网红顾问变更日志明细-" + ExamConstants.QUERY_BY_ID)
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        StoreCelebrityCounselorChangeDetail storeCelebrityCounselorChangeDetail = changeDetailService.getById(id);
        return Result.ok(storeCelebrityCounselorChangeDetail);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param storeCelebrityCounselorChangeDetail
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StoreCelebrityCounselorChangeDetail storeCelebrityCounselorChangeDetail) {
        return super.exportXls(request, storeCelebrityCounselorChangeDetail, StoreCelebrityCounselorChangeDetail.class, "网红顾问变更日志明细");
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
        return super.importExcel(request, response, StoreCelebrityCounselorChangeDetail.class);
    }


    /**
     * 取消顾问调整
     *
     * @param logId
     * @return
     */
    @AutoLog(value = "网红顾问变更日志-取消顾问调整")
@Operation(summary = "网红顾问变更日志-取消顾问调整", description = "网红顾问变更日志-取消顾问调整")
    @GetMapping(value = "/cancelAdjustmentCounselor")
    public Result<?> cancelAdjustmentCounselor(@RequestParam(name = "logId", required = true) String logId) {
        StoreCelebrityCounselorChangeLog changeLog = changeLogService.getById(logId);
        if (changeLog == null || Objects.equals(changeLog.getChangeStatus(), Integer.valueOf(CommonConstant.STATUS__2))) {
            return Result.error("该日志已取消或不存在！");
        }
        // 判断接收人是否存在新的记录
        String toCounselorId = changeLog.getToCounselorId();
        List<StoreCelebrityCounselorChangeLog> list = changeLogService.lambdaQuery()
                .eq(StoreCelebrityCounselorChangeLog::getFromCounselorId, toCounselorId)
                .gt(StoreCelebrityCounselorChangeLog::getCreateTime, changeLog.getCreateTime())
                .list();
        if(!list.isEmpty()){
            return Result.error("该顾问已有新的调整记录，无法取消！");
        }

        List<StoreCelebrityPrivateCounselor> privateCounselorsSave = new ArrayList<>();
        List<String> privateCounselorsIdsDel = new ArrayList<>();
        List<StoreCelebrityCounselorChangeDetail> changeDetails = changeDetailService.lambdaQuery().eq(StoreCelebrityCounselorChangeDetail::getChangeLogId, logId).list();
        // 按照类型分组
        changeDetails.stream().collect(Collectors.groupingBy(StoreCelebrityCounselorChangeDetail::getOperationType)).forEach((k, v) -> {
            // 新增的数据，需要删除
            if (Objects.equals(k, Integer.valueOf(CommonConstant.STATUS_1))) {
                List<String> ids = v.stream().map(StoreCelebrityCounselorChangeDetail::getSourceRecordId).collect(Collectors.toList());
                privateCounselorsIdsDel.addAll(ids);
            } else {
                // 修改的数据，需要恢复原数据
                v.forEach(detail -> {
                    StoreCelebrityPrivateCounselor privateCounselor = new StoreCelebrityPrivateCounselor();
                    privateCounselor.setId(detail.getSourceRecordId());
                    privateCounselor.setCelebrityPrivateId(detail.getCelebrityPrivateId());
                    privateCounselor.setCelebrityCounselorId(detail.getCelebrityCounselorId());
                    privateCounselor.setCelebrityCounselorName(detail.getCelebrityCounselorName());
                    privateCounselor.setEmail(detail.getEmail());
                    privateCounselor.setContractAmount(detail.getContractAmount());
                    privateCounselor.setSort(detail.getSort());
                    privateCounselor.setCelebrityContactEmail(detail.getCelebrityContactEmail());
                    privateCounselor.setContractTime(detail.getContractTime());
                    privateCounselor.setContractInfo(detail.getContractInfo());
                    privateCounselorsSave.add(privateCounselor);
                });
            }
        });
        changeDetailService.cancelAdjustmentCounselor(logId, privateCounselorsSave, privateCounselorsIdsDel);
        List<String> privateIds = changeDetails.stream().map(StoreCelebrityCounselorChangeDetail::getCelebrityPrivateId).distinct().collect(Collectors.toList());
        List<StoreCelebrityPrivateCounselor> celebrityPrivateCounselors = privateCounselorService.lambdaQuery().in(StoreCelebrityPrivateCounselor::getCelebrityPrivateId, privateIds).list();

        List<StoreCelebrityPrivate> celebrityPrivates = new ArrayList<>();
        // 更新私密顾问信息
        privateIds.forEach(id -> {
            Optional<StoreCelebrityPrivateCounselor> counselorOptional = celebrityPrivateCounselors.stream()
                    .filter(item -> Objects.equals(item.getCelebrityPrivateId(), id))
                    .max(Comparator.comparing(
                            StoreCelebrityPrivateCounselor::getContractTime,
                            Comparator.nullsLast(Comparator.naturalOrder())
                    ));
            if (counselorOptional.isPresent()) {
                StoreCelebrityPrivate celebrityPrivate = new StoreCelebrityPrivate();
                celebrityPrivate.setId(id);
                celebrityPrivate.setCelebrityCounselorId(counselorOptional.get().getCelebrityCounselorId());
                celebrityPrivate.setCelebrityCounselorName(counselorOptional.get().getCelebrityCounselorName());
                celebrityPrivate.setCelebrityContactEmail(counselorOptional.get().getCelebrityContactEmail());
                celebrityPrivate.setContractAmount(counselorOptional.get().getContractAmount());
                celebrityPrivates.add(celebrityPrivate);
            }
        });

        celebrityPrivateService.updateBatchById(celebrityPrivates);
        return Result.ok("取消成功！");
    }
}
