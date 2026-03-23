package org.jeecg.modules.email.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.ExamConstants;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.email.entity.EmailPushDetail;
import org.jeecg.modules.email.entity.EmailPushMain;
import org.jeecg.modules.email.model.EmailMonthQueryVO;
import org.jeecg.modules.email.service.IEmailPushDetailService;
import org.jeecg.modules.email.service.IEmailPushMainService;
import org.jeecg.modules.email.service.IEmailReplyService;
import org.jeecg.modules.instagram.entity.IgCelebrity;
import org.jeecg.modules.instagram.service.IIgCelebrityService;
import org.jeecg.modules.kol.service.IKolContactService;
import org.jeecg.modules.tiktok.entity.TiktokCelebrity;
import org.jeecg.modules.tiktok.service.ITiktokCelebrityService;
import org.jeecg.modules.youtube.entity.YoutubeCelebrity;
import org.jeecg.modules.youtube.service.IYtCelebrityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.*;

/**
 * @Description: 邮件消息表
 * @Author: jeecgboot
 * @Date: 2025-11-07
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "邮件消息表")
@RestController
@RequestMapping("/email/emailPushDetail")
public class EmailPushDetailController extends JeecgController<EmailPushDetail, IEmailPushDetailService> {
    @Autowired
    private IEmailPushDetailService emailPushDetailService;
    @Autowired
    @Lazy
    private IEmailPushMainService emailPushMainService;
    @Autowired
    private IEmailReplyService emailReplyService;
    @Autowired
    private IKolContactService kolContactService;
    @Autowired
    private ITiktokCelebrityService tkCelebrityService;
    @Autowired
    private IYtCelebrityService ytCelebrityService;
    @Autowired
    private IIgCelebrityService igCelebrityService;

    /**
     * 分页列表查询
     *
     * @param emailPushDetail
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "邮件消息表-" + ExamConstants.PAGE_LIST_QUERY)
    @Operation(summary = "邮件消息表-" + ExamConstants.PAGE_LIST_QUERY, description = "邮件消息表-"
            + ExamConstants.PAGE_LIST_QUERY)
    @GetMapping(value = "/list")
    public Result<?> queryPageList(EmailPushDetail emailPushDetail,
                                   @RequestParam(name = ExamConstants.PAGE_NO, defaultValue = ExamConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                   @RequestParam(name = ExamConstants.PAGE_SIZE, defaultValue = ExamConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
                                   HttpServletRequest req) {
        LambdaQueryWrapper<EmailPushDetail> queryWrapper = QueryGenerator
                .initQueryWrapper(emailPushDetail, req.getParameterMap()).lambda();
        if (!oConvertUtils.checkUserType(getUserNameByToken())) {
            queryWrapper.eq(EmailPushDetail::getCounselorId, getUserIdByToken());
        }
        Page<EmailPushDetail> page = new Page<EmailPushDetail>(pageNo, pageSize);
        IPage<EmailPushDetail> pageList = emailPushDetailService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param emailPushDetail
     * @return
     */
    @AutoLog(value = "邮件消息表-" + ExamConstants.ADD)
    @Operation(summary = "邮件消息表-" + ExamConstants.ADD, description = "邮件消息表-" + ExamConstants.ADD)
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody EmailPushDetail emailPushDetail) {
        emailPushDetailService.save(emailPushDetail);
        return Result.ok(ExamConstants.ADD_SUCCESS);
    }

    /**
     * 编辑
     *
     * @param emailPushDetail
     * @return
     */
    @AutoLog(value = "邮件消息表-" + ExamConstants.EDIT)
    @Operation(summary = "邮件消息表-" + ExamConstants.EDIT, description = "邮件消息表-" + ExamConstants.EDIT)
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody EmailPushDetail emailPushDetail) {
        String email = emailPushDetail.getEmail();
        Date planSendTime = emailPushDetail.getPlanSendTime();
        EmailPushDetail pushDetail = emailPushDetailService.getById(emailPushDetail.getId());
        String account = pushDetail.getAccount();
        Integer platformType = pushDetail.getPlatformType();
        switch (platformType) {
            case CommonConstant.TK:
                if (oConvertUtils.isNotEmpty(email)) {
                    tkCelebrityService.lambdaUpdate().eq(TiktokCelebrity::getAuthorId, account)
                            .set(TiktokCelebrity::getEmail, email).update();
                }
                break;
            case CommonConstant.YT:
                if (oConvertUtils.isNotEmpty(email)) {
                    ytCelebrityService.lambdaUpdate().eq(YoutubeCelebrity::getAccount, account)
                            .set(YoutubeCelebrity::getEmail, email).update();
                }
                break;
            case CommonConstant.IG:
                if (oConvertUtils.isNotEmpty(email)) {
                    igCelebrityService.lambdaUpdate().eq(IgCelebrity::getAccount, account)
                            .set(IgCelebrity::getEmail, email).update();
                }
        }
        emailPushDetailService.lambdaUpdate()
                .set(EmailPushDetail::getPlanSendTime, planSendTime)
                .set(EmailPushDetail::getEmail, email)
                .set(EmailPushDetail::getIsSend, 0)
                .set(EmailPushDetail::getSendTime, null)
                .set(EmailPushDetail::getErrorMessage, null)
                .eq(EmailPushDetail::getId, emailPushDetail.getId())
                .update();
        // emailPushDetailService.updateById(emailPushDetail);
        // 查询所有明细，根据明细状态更新主表状态
        List<EmailPushDetail> list = emailPushDetailService.lambdaQuery()
                .eq(EmailPushDetail::getMainId, pushDetail.getMainId()).list();

        // 统计各种状态的明细数量
        long unSentCount = list.stream().filter(x -> x.getIsSend() == 0).count(); // 未发送
        long failCount = list.stream().filter(x -> x.getIsSend() == -1).count(); // 发送失败
        long cancelCount = list.stream().filter(x -> x.getIsSend() == -2).count(); // 取消发送

        // 根据明细状态确定主表状态
        Integer mainStatus;
        if (unSentCount > 0) {
            // 如果有未发送的，主表状态改为0：未发送
            mainStatus = 0;
        } else if (cancelCount == list.size()) {
            // 如果全部取消，主表状态改为-2：取消发送
            mainStatus = -2;
        } else if (failCount > 0) {
            // 如果没有未发送的，但有发送失败的，主表状态改为10：部分发送
            mainStatus = 10;
        } else {
            // 全部已发送，主表状态改为99：已发送
            mainStatus = 99;
        }

        emailPushMainService.lambdaUpdate()
                .eq(EmailPushMain::getId, pushDetail.getMainId())
                .set(EmailPushMain::getIsSend, mainStatus)
                .update();

        // 增加该邮件明细对应的单个网红的标记开发记录（通过mainId和celebrityId精确恢复）
        kolContactService.resendKolContactByEmailPushMainIdAndCelebrityId(pushDetail.getMainId(),
                pushDetail.getAccount());

        return Result.ok(ExamConstants.EDIT_SUCCESS);
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "邮件消息表-" + ExamConstants.DELETE_BY_ID)
    @Operation(summary = "邮件消息表-" + ExamConstants.DELETE_BY_ID, description = "邮件消息表-" + ExamConstants.DELETE_BY_ID)
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        emailPushDetailService.removeById(id);
        return Result.ok(ExamConstants.DELETE_SUCCESS);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "邮件消息表-" + ExamConstants.DELETE_BATCH)
    @Operation(summary = "邮件消息表-" + ExamConstants.DELETE_BATCH, description = "邮件消息表-" + ExamConstants.DELETE_BATCH)
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.emailPushDetailService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok(ExamConstants.DELETE_BATCH_SUCCESS);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "邮件消息表-" + ExamConstants.QUERY_BY_ID)
    @Operation(summary = "邮件消息表-" + ExamConstants.QUERY_BY_ID, description = "邮件消息表-" + ExamConstants.QUERY_BY_ID)
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        EmailPushDetail emailPushDetail = emailPushDetailService.getById(id);
        return Result.ok(emailPushDetail);
    }

    /**
     * 通过mainId查询列表
     *
     * @return
     */
    @AutoLog(value = "邮件消息表-通过mainId查询列表")
    @Operation(summary = "邮件消息表-通过mainId查询列表", description = "邮件消息表-通过mainId查询列表")
    @GetMapping(value = "/listByMainId")
    public Result<?> listByMainId(EmailPushDetail emailPushDetail) {
        List<EmailPushDetail> list = emailPushDetailService.listByMainId(emailPushDetail);
        return Result.ok(list);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param emailPushDetail
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, EmailPushDetail emailPushDetail) {
        return super.exportXls(request, emailPushDetail, EmailPushDetail.class, "邮件消息表");
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
        return super.importExcel(request, response, EmailPushDetail.class);
    }

    @GetMapping(value = "/cancelSend")
    public Result<?> cancelSend(@RequestParam(name = "id", required = true) String id) {
        EmailPushDetail pushDetail = emailPushDetailService.getById(id);
        if (pushDetail.getIsSend() != 0) {
            return Result.error("该邮件已发送，无法取消发送");
        }
        emailPushDetailService.lambdaUpdate().eq(EmailPushDetail::getId, id).set(EmailPushDetail::getIsSend, -2)
                .update();
        // 查询所有明细，根据明细状态更新主表状态
        List<EmailPushDetail> list = emailPushDetailService.lambdaQuery()
                .eq(EmailPushDetail::getMainId, pushDetail.getMainId()).list();

        // 统计各种状态的明细数量
        long sentCount = list.stream().filter(x -> x.getIsSend() == 1).count(); // 已发送
        long unSentCount = list.stream().filter(x -> x.getIsSend() == 0).count(); // 未发送
        long failCount = list.stream().filter(x -> x.getIsSend() == -1).count(); // 发送失败
        long cancelCount = list.stream().filter(x -> x.getIsSend() == -2).count(); // 取消发送

        // 根据明细状态确定主表状态
        int mainStatus;
        if (unSentCount > 0) {
            // 如果有未发送的，主表状态改为0：未发送
            mainStatus = 0;
        } else if (cancelCount == list.size()) {
            // 如果全部取消，主表状态改为-2：取消发送
            mainStatus = -2;
        } else if (failCount > 0 && sentCount == 0) {
            // 如果没有已发送的，但有发送失败的，主表状态改为-1：发送失败
            mainStatus = -1;
        } else if (failCount > 0 && sentCount > 0) {
            // 如果有发送失败的，同时也有已发送的，主表状态改为10：部分发送
            mainStatus = 10;
        } else if (sentCount > 0 && sentCount == list.size()) {
            // 如果全部已发送，主表状态改为99：已发送
            mainStatus = 99;
        } else if (sentCount > 0 && sentCount < list.size()) {
            // 如果部分已发送，主表状态改为10：部分发送
            mainStatus = 10;
        } else {
            // 其他情况，主表状态改为0：未发送
            mainStatus = 0;
        }

        emailPushMainService.lambdaUpdate()
                .eq(EmailPushMain::getId, pushDetail.getMainId())
                .set(EmailPushMain::getIsSend, mainStatus)
                .update();

        // 删除该邮件明细对应的单个网红的标记开发记录（通过mainId和celebrityId精确删除）
        kolContactService.deleteKolContactByEmailPushMainIdAndCelebrityId(pushDetail.getMainId(),
                pushDetail.getAccount());

        return Result.ok("取消发送成功");
    }

    @GetMapping(value = "/resend")
    public Result<?> resend(@RequestParam(name = "id", required = true) String id) {
        EmailPushDetail pushDetail = emailPushDetailService.getById(id);
        if (pushDetail.getIsSend() != -2) {
            return Result.error("只有已取消发送的邮件才能重新发送");
        }
        emailPushDetailService.lambdaUpdate().eq(EmailPushDetail::getId, id).set(EmailPushDetail::getIsSend, 0)
                .update();
        // 查询所有明细，根据明细状态更新主表状态
        updatePushMainSendStatus(pushDetail.getMainId());

        // 增加该邮件明细对应的单个网红的标记开发记录（通过mainId和celebrityId精确恢复）
        kolContactService.resendKolContactByEmailPushMainIdAndCelebrityId(pushDetail.getMainId(),
                pushDetail.getAccount());

        return Result.ok("恢复发送成功");
    }

    private void updatePushMainSendStatus(String mainId) {
        List<EmailPushDetail> list = emailPushDetailService.lambdaQuery()
                .eq(EmailPushDetail::getMainId, mainId).list();

        // 统计各种状态的明细数量
        long unSentCount = list.stream().filter(x -> x.getIsSend() == 0).count(); // 未发送
        long failCount = list.stream().filter(x -> x.getIsSend() == -1).count(); // 发送失败
        long cancelCount = list.stream().filter(x -> x.getIsSend() == -2).count(); // 取消发送

        // 根据明细状态确定主表状态
        Integer mainStatus;
        if (unSentCount > 0) {
            // 如果有未发送的，主表状态改为0：未发送
            mainStatus = 0;
        } else if (cancelCount == list.size()) {
            // 如果全部取消，主表状态改为-2：取消发送
            mainStatus = -2;
        } else if (failCount > 0) {
            // 如果没有未发送的，但有发送失败的，主表状态改为10：部分发送
            mainStatus = 10;
        } else {
            // 全部已发送，主表状态改为99：已发送
            mainStatus = 99;
        }

        emailPushMainService.lambdaUpdate()
                .eq(EmailPushMain::getId, mainId)
                .set(EmailPushMain::getIsSend, mainStatus)
                .update();
    }

    /**
     * @description: 统计邮件发送情况
     * @author: nqr
     * @date: 2025/11/24 9:15
     **/
    @PostMapping("/dashboard")
    public Result<?> dashboard(@RequestBody EmailMonthQueryVO query) {
        if (query.getStartDate() == null || query.getEndDate() == null) {
            return Result.error("开始日期和结束日期不能为空");
        }
        if (query.getEndDate().before(query.getStartDate())) {
            return Result.error("结束日期不能早于开始日期");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("summary", emailPushDetailService.getMonthSummary(query));
        result.put("top10", emailPushDetailService.getCounselorTop(query));
        result.put("trend", emailPushDetailService.getDailyTrend(query));

        return Result.ok(result);
    }

    /**
     * 1. 顶部（全月汇总）
     */
    @PostMapping("/summary")
    public Result<?> summary(@RequestBody EmailMonthQueryVO query) {
        validateMonth(query.getMonth());
        // 管理员查看所有数据
        if (oConvertUtils.checkUserType(getUserNameByToken())) {
            query.setCounselorId(null);
        }
        return Result.ok(emailPushDetailService.getMonthSummary(query));
    }

    /**
     * 2. 网红顾问 排行榜
     */
    @PostMapping("/counselorTop")
    public Result<?> counselorTop10(@RequestBody EmailMonthQueryVO query) {
        validateMonth(query.getMonth());
        return Result.ok(emailPushDetailService.getCounselorTop(query));
    }

    /**
     * 3. 该月每日趋势数据（单独查询）
     */
    @PostMapping("/dailyTrend")
    public Result<?> dailyTrend(@RequestBody EmailMonthQueryVO query) {
        validateMonth(query.getMonth());
        return Result.ok(emailPushDetailService.getDailyTrend(query));
    }

    private void validateMonth(String month) {
        if (oConvertUtils.isNotEmpty(month)) {
            if (!month.matches("\\d{4}-\\d{2}")) {
                throw new JeecgBootException("月份格式必须为 yyyy-MM，例如：2025-11");
            }
        }
    }

    @AutoLog(value = "商务发信明细-" + ExamConstants.PAGE_LIST_QUERY)
    @Operation(summary = "商务发信明细-" + ExamConstants.PAGE_LIST_QUERY, description = "商务发信明细-"
            + ExamConstants.PAGE_LIST_QUERY)
    @GetMapping(value = "/businessList")
    public Result<?> queryBusinessPageList(EmailPushDetail emailPushDetail,
                                           @RequestParam(name = ExamConstants.PAGE_NO, defaultValue = ExamConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                           @RequestParam(name = ExamConstants.PAGE_SIZE, defaultValue = ExamConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
                                           HttpServletRequest req) {
        LambdaQueryWrapper<EmailPushDetail> queryWrapper = QueryGenerator
                .initQueryWrapper(emailPushDetail, req.getParameterMap()).lambda();

        String mainId = emailPushDetail.getMainId();
        if (oConvertUtils.isNotEmpty(mainId)) {
            EmailPushMain main = emailPushMainService.getById(mainId);
            if (main != null && main.getBusinessType() != 1) {
                return Result.error("该主表记录不是商务发信记录");
            }
        }

        if (!oConvertUtils.checkUserType(getUserNameByToken())) {
            queryWrapper.eq(EmailPushDetail::getCounselorId, getUserIdByToken());
        }
        Page<EmailPushDetail> page = new Page<>(pageNo, pageSize);
        IPage<EmailPushDetail> pageList = emailPushDetailService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @AutoLog(value = "商务发信明细-通过mainId查询列表")
    @Operation(summary = "商务发信明细-通过mainId查询列表", description = "商务发信明细-通过mainId查询列表")
    @GetMapping(value = "/businessListByMainId")
    public Result<?> listBusinessByMainId(EmailPushDetail emailPushDetail) {
        List<EmailPushDetail> list = emailPushDetailService.listBusinessByMainId(emailPushDetail.getMainId());
        return Result.ok(list);
    }

    @AutoLog(value = "商务发信明细-" + ExamConstants.EDIT)
    @Operation(summary = "商务发信明细-" + ExamConstants.EDIT, description = "商务发信明细-" + ExamConstants.EDIT)
    @PutMapping(value = "/businessEdit")
    public Result<?> businessEdit(@RequestBody EmailPushDetail emailPushDetail) {
        String email = emailPushDetail.getEmail();
        Date planSendTime = emailPushDetail.getPlanSendTime();
        String brandName = emailPushDetail.getBrandName();

        EmailPushDetail pushDetail = emailPushDetailService.getById(emailPushDetail.getId());
        if (pushDetail == null) {
            return Result.error("未找到当前记录");
        }

        EmailPushMain main = emailPushMainService.getById(pushDetail.getMainId());
        if (main == null) {
            return Result.error("未找到主表记录");
        }

        if (main.getBusinessType() != 1) {
            return Result.error("该记录不是商务发信记录");
        }

        emailPushDetailService.lambdaUpdate()
                .set(EmailPushDetail::getPlanSendTime, planSendTime)
                .set(EmailPushDetail::getEmail, email)
                .set(EmailPushDetail::getIsSend, 0)
                .set(EmailPushDetail::getSendTime, null)
                .set(EmailPushDetail::getErrorMessage, null)
                .eq(EmailPushDetail::getId, emailPushDetail.getId())
                .update();

        List<EmailPushDetail> list = emailPushDetailService.lambdaQuery()
                .eq(EmailPushDetail::getMainId, pushDetail.getMainId()).list();

        long unSentCount = list.stream().filter(x -> x.getIsSend() == 0).count();
        long failCount = list.stream().filter(x -> x.getIsSend() == -1).count();
        long cancelCount = list.stream().filter(x -> x.getIsSend() == -2).count();

        Integer mainStatus;
        if (unSentCount > 0) {
            mainStatus = 0;
        } else if (cancelCount == list.size()) {
            mainStatus = -2;
        } else if (failCount > 0) {
            mainStatus = 10;
        } else {
            mainStatus = 99;
        }

        emailPushMainService.lambdaUpdate()
                .eq(EmailPushMain::getId, pushDetail.getMainId())
                .set(EmailPushMain::getIsSend, mainStatus)
                .update();

        return Result.ok(ExamConstants.EDIT_SUCCESS);
    }

    @AutoLog(value = "商务发信明细-重新发送")
    @Operation(summary = "商务发信明细-重新发送", description = "商务发信明细-重新发送")
    @GetMapping(value = "/businessResend")
    public Result<?> businessResend(@RequestParam(name = "id", required = true) String id) {
        EmailPushDetail pushDetail = emailPushDetailService.getById(id);
        if (pushDetail == null) {
            return Result.error("未找到当前记录");
        }

        EmailPushMain main = emailPushMainService.getById(pushDetail.getMainId());
        if (main == null) {
            return Result.error("未找到主表记录");
        }

        if (main.getBusinessType() != 1) {
            return Result.error("该记录不是商务发信记录");
        }

        if (pushDetail.getIsSend() == 0) {
            return Result.error("该邮件尚未发送，无需重新发送");
        }

        emailPushDetailService.lambdaUpdate()
                .set(EmailPushDetail::getIsSend, 0)
                .set(EmailPushDetail::getSendTime, null)
                .set(EmailPushDetail::getErrorMessage, null)
                .eq(EmailPushDetail::getId, id)
                .update();

        List<EmailPushDetail> list = emailPushDetailService.lambdaQuery()
                .eq(EmailPushDetail::getMainId, pushDetail.getMainId()).list();

        long unSentCount = list.stream().filter(x -> x.getIsSend() == 0).count();
        long failCount = list.stream().filter(x -> x.getIsSend() == -1).count();
        long cancelCount = list.stream().filter(x -> x.getIsSend() == -2).count();

        Integer mainStatus;
        if (unSentCount > 0) {
            mainStatus = 0;
        } else if (cancelCount == list.size()) {
            mainStatus = -2;
        } else if (failCount > 0) {
            mainStatus = 10;
        } else {
            mainStatus = 99;
        }

        emailPushMainService.lambdaUpdate()
                .eq(EmailPushMain::getId, pushDetail.getMainId())
                .set(EmailPushMain::getIsSend, mainStatus)
                .update();

        return Result.ok(ExamConstants.EDIT_SUCCESS);
    }

    /**
     * 商务发信统计 - 1. 商务发信月度汇总
     */
    @PostMapping("/businessSummary")
    public Result<?> businessSummary(@RequestBody EmailMonthQueryVO query) {
        if (query.getStartDate() == null || query.getEndDate() == null) {
            return Result.error("开始日期和结束日期不能为空");
        }
        if (query.getEndDate().before(query.getStartDate())) {
            return Result.error("结束日期不能早于开始日期");
        }
        return Result.ok(emailPushDetailService.getBusinessMonthSummary(query));
    }

    /**
     * 商务发信统计 - 2. 商务发信顾问排行榜
     */
    @PostMapping("/businessCounselorTop")
    public Result<?> businessCounselorTop(@RequestBody EmailMonthQueryVO query) {
        if (query.getStartDate() == null || query.getEndDate() == null) {
            return Result.error("开始日期和结束日期不能为空");
        }
        if (query.getEndDate().before(query.getStartDate())) {
            return Result.error("结束日期不能早于开始日期");
        }
        return Result.ok(emailPushDetailService.getBusinessCounselorTop(query));
    }

    /**
     * 商务发信统计 - 3. 商务发信每日趋势数据（单独查询）
     */
    @PostMapping("/businessDailyTrend")
    public Result<?> businessDailyTrend(@RequestBody EmailMonthQueryVO query) {
        if (query.getStartDate() == null || query.getEndDate() == null) {
            return Result.error("开始日期和结束日期不能为空");
        }
        if (query.getEndDate().before(query.getStartDate())) {
            return Result.error("结束日期不能早于开始日期");
        }
        return Result.ok(emailPushDetailService.getBusinessDailyTrend(query));
    }
}