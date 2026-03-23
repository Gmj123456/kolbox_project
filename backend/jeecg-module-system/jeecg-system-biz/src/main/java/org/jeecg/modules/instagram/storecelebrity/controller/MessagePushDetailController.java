package org.jeecg.modules.instagram.storecelebrity.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.ServletOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.enums.YesNoStatus;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.util.*;
import org.jeecg.modules.instagram.storemail.model.DataList;
import org.jeecg.modules.instagram.storemail.service.IEmailaccountService;
import org.jeecg.modules.instagram.storecelebrity.entity.MessagePushDetail;
import org.jeecg.modules.instagram.storecelebrity.model.*;
import org.jeecg.modules.instagram.storecelebrity.service.IMessagePushDetailService;
import org.jeecg.modules.instagram.storemerchant.entity.StoreEmailPackagePurchase;
import org.jeecg.modules.instagram.storemerchant.service.IStoreEmailPackagePurchaseService;
import org.jeecg.modules.instagram.storepromotion.entity.StorePushRefund;
import org.jeecg.modules.instagram.storepromotion.service.IStorePushRefundService;
import org.jeecg.modules.instagram.utils.ToolHttpClient;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 邮件发送日志表
 * @Author: jeecg-boot
 * @Date: 2020-05-11
 * @Version: V1.0
 */
@Slf4j
@Tag(name = " 邮件推送日志表")
@RestController
@RequestMapping("/instagram/messagepushdetail")
public class MessagePushDetailController extends JeecgController<MessagePushDetail, IMessagePushDetailService> {
    @Autowired
    private IMessagePushDetailService messagePushDetailService;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private IStorePushRefundService storePushRefundService;

    @Autowired
    private IStoreEmailPackagePurchaseService emailPackagePurchaseService;

    @Autowired
    private IEmailaccountService emailaccountService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 分页列表查询
     *
     * @param messagePushDetailModel
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = " 邮件推送日志表-分页列表查询")
@Operation(summary = " 邮件推送日志表-分页列表查询", description = " 邮件推送日志表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(MessagePushDetailModel messagePushDetailModel,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        String userId = getUserIdByToken();
        String username = getUserNameByToken();
        if (!"admin".equals(username)) {
            messagePushDetailModel.setSellerId(userId);
        }
        Page<MessagePushDetail> page = new Page<>(pageNo, pageSize);
        IPage<MessagePushDetail> pageList = messagePushDetailService.pageList(page, messagePushDetailModel);
        return Result.ok(pageList);
    }

    /**
     * 财务统计查询
     *
     * @param financialStatisticsModel
     * @param req
     * @return
     */
    @AutoLog(value = " 邮件推送日志表-财务统计查询")
@Operation(summary = " 邮件推送日志表-财务统计查询", description = " 邮件推送日志表-财务统计查询")
    @GetMapping(value = "/financial")
    public Result<?> financialStatistics(FinancialStatisticsModel financialStatisticsModel,
                                         HttpServletRequest req) {
        /*
         * 0, "等级"
         * 3, "升级"
         * 4, "续费"
         * */
        if (null != financialStatisticsModel.getStartConsumeDate() && financialStatisticsModel.getStartConsumeDate().equals("Invalid date")) {
            financialStatisticsModel.setStartConsumeDate("");
        }
        if (null != financialStatisticsModel.getEndConsumeDate() && financialStatisticsModel.getEndConsumeDate().equals("Invalid date")) {
            financialStatisticsModel.setEndConsumeDate("");
        }

/*        if (messagePushEmailCountModel.getOrderWord() == null) {
            messagePushEmailCountModel.setOrderWord("emailCount");
            messagePushEmailCountModel.setOrder("desc");
        }*/
        List<FinancialStatisticsModel> pageList = messagePushDetailService.financialStatistics(financialStatisticsModel);
        System.out.println(pageList);
        return Result.ok(pageList);
    }


    /**
     * 分页商家明细查询
     *
     * @param detailsMerchantEmailModel
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = " 邮件推送日志表-分页商家明细查询")
@Operation(summary = " 邮件推送日志表-分页商家明细查询", description = " 邮件推送日志表-分页商家明细查询")
    @GetMapping(value = "/detailsPageList")
    public Result<?> detailsPageList(DetailsMerchantEmailModel detailsMerchantEmailModel,
                                     @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                     HttpServletRequest req) {
        String userIdBytoken = getUserIdByToken();
        String username = getUserNameByToken();

        /*user_type = 1*/
        if (!"admin".equals(username)) {
            detailsMerchantEmailModel.setSellerId(userIdBytoken);

        }
        if (null != detailsMerchantEmailModel.getStartSendTime() && detailsMerchantEmailModel.getStartSendTime().equals("Invalid date")) {
            detailsMerchantEmailModel.setStartSendTime("");
        }
        if (null != detailsMerchantEmailModel.getEndSendTime() && detailsMerchantEmailModel.getEndSendTime().equals("Invalid date")) {
            detailsMerchantEmailModel.setEndSendTime("");
        }
        Page<MessagePushDetail> page = new Page<>(pageNo, pageSize);
        IPage<MessagePushDetail> pageList = messagePushDetailService.detailsPageList(page, detailsMerchantEmailModel);
        return Result.ok(pageList);
    }

    /**
     * 分页商家统计查询
     *
     * @param messagePushEmailCountModel
     * @return
     */
    @AutoLog(value = " 邮件推送日志表-分页商家统计查询")
@Operation(summary = " 邮件推送日志表-分页商家统计查询", description = " 邮件推送日志表-分页商家统计查询")
    @GetMapping(value = "/queryCountPageList")
    public Result<?> queryCountPageList(MessagePushEmailCountModel messagePushEmailCountModel) {
        List<MessagePushEmailCountModel> pageList = messagePushDetailService.queryCountPageList(messagePushEmailCountModel);
        System.out.println(pageList);
        return Result.ok(pageList);
    }


    /**
     * 分页列表查询
     *
     * @param messagePushDetail
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = " 邮件推送日志表-分页列表查询")
@Operation(summary = " 邮件推送日志表-分页列表查询", description = " 邮件推送日志表-分页列表查询")
    @GetMapping(value = "/pagePackageList")
    public Result<?> pagePackageList(MessagePushDetail messagePushDetail,
                                     @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                     HttpServletRequest req) {
        String userId = getUserIdByToken();
  /*      QueryWrapper<MessagePushDetail> queryWrapper = QueryGenerator.initQueryWrapper(messagePushDetail, req.getParameterMap());
        queryWrapper.eq("seller_id", userId);
        queryWrapper.isNotNull("package_purchase_id");
        queryWrapper.ne("package_purchase_id", "");
        Page<MessagePushDetail> page = new Page<>(pageNo, pageSize);*/
        LambdaQueryWrapper<MessagePushDetail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(MessagePushDetail::getSellerId, userId);
        lambdaQueryWrapper.isNotNull(MessagePushDetail::getPackagePurchaseId);
        lambdaQueryWrapper.ne(MessagePushDetail::getPackagePurchaseId, StringUtils.EMPTY);
        if (oConvertUtils.isNotEmpty(messagePushDetail.getPackageName())) {
            lambdaQueryWrapper.like(MessagePushDetail::getPackageName, messagePushDetail.getPackageName());
        }
        IPage<MessagePushDetail> pageList = messagePushDetailService.page(PageUtil.getOrderItems(pageNo, pageSize, req), lambdaQueryWrapper);

        return Result.ok(pageList);
    }


    /**
     * 分页列表网红邮箱发送统计
     *
     * @param celebrityEmailStatisticsModel
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = " 邮件推送日志表-分页列表网红邮箱发送统计")
@Operation(summary = " 邮件推送日志表-分页列表网红邮箱发送统计", description = " 邮件推送日志表-分页列表网红邮箱发送统计")
    @GetMapping(value = "/queryCelebrityCount")
    public Result<?> queryCelebrityCount(CelebrityEmailStatisticsModel celebrityEmailStatisticsModel,
                                         @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                         HttpServletRequest req) {
/*
        if (null != celebrityEmailStatisticsModel.getStartEmailPushTime() && celebrityEmailStatisticsModel.getStartEmailPushTime().equals("Invalid date")) {
            celebrityEmailStatisticsModel.setStartEmailPushTime("");
        }
        if (null != celebrityEmailStatisticsModel.getEndEmailPushTime() && celebrityEmailStatisticsModel.getEndEmailPushTime().equals("Invalid date")) {
            celebrityEmailStatisticsModel.setEndEmailPushTime("");
        }
*/

        Page<CelebrityEmailStatisticsModel> page = new Page<>(pageNo, pageSize);
        IPage<CelebrityEmailStatisticsModel> pageList = messagePushDetailService.queryCelebrityCount(page, celebrityEmailStatisticsModel);
        return Result.ok(pageList);
    }

    /**
     * 商家给网红发送统计
     *
     * @param celebrityEmailStatisticsModel
     * @param req
     * @return
     */
    @AutoLog(value = " 邮件推送日志表-商家给网红发送统计")
@Operation(summary = " 邮件推送日志表-商家给网红发送统计", description = " 邮件推送日志表-商家给网红发送统计")
    @GetMapping(value = "/querySellerCount")
    public Result<?> querySellerCount(CelebrityEmailStatisticsModel celebrityEmailStatisticsModel,
                                      HttpServletRequest req) {
        List<CelebrityEmailStatisticsModel> pageList = messagePushDetailService.querySellerCount(celebrityEmailStatisticsModel);
        System.out.println(pageList);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param messagePushDetail
     * @return
     */
    @AutoLog(value = " 邮件推送日志表-添加")
@Operation(summary = " 邮件推送日志表-添加", description = " 邮件推送日志表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody MessagePushDetail messagePushDetail) {
        String userIdBytoken = getUserIdByToken();
        String userNameBytoken = getUserNameByToken();
        messagePushDetail.setSellerId(userIdBytoken);
        messagePushDetail.setSellerName(userNameBytoken);
        messagePushDetailService.save(messagePushDetail);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param messagePushDetail
     * @return
     */
    @AutoLog(value = " 邮件推送日志表-编辑")
@Operation(summary = " 邮件推送日志表-编辑", description = " 邮件推送日志表-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody MessagePushDetail messagePushDetail) {
        messagePushDetailService.updateById(messagePushDetail);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = " 邮件推送日志表-通过id删除")
@Operation(summary = " 邮件推送日志表-通过id删除", description = " 邮件推送日志表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        messagePushDetailService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = " 邮件推送日志表-批量删除")
@Operation(summary = " 邮件推送日志表-批量删除", description = " 邮件推送日志表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.messagePushDetailService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功！");
    }

    /**
     * 通过id取消
     *
     * @param id
     * @return
     */
    @AutoLog(value = " 邮件推送日志表-通过id取消")
@Operation(summary = " 邮件推送日志表-通过id取消", description = " 邮件推送日志表-通过id取消")
    @GetMapping(value = "/cancel")
    public Result<?> cancel(String id) {
        LambdaQueryWrapper<MessagePushDetail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(MessagePushDetail::getId, id);
        lambdaQueryWrapper.eq(MessagePushDetail::getIsSend, YesNoStatus.NO.getCode());
        MessagePushDetail messagePushDetail = messagePushDetailService.getOne(lambdaQueryWrapper);
        //标识
        if (oConvertUtils.isNotEmpty(messagePushDetail)) {
            //修改邮件发送状态为已取消
            LambdaUpdateWrapper<MessagePushDetail> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(MessagePushDetail::getId, id);
            //取消发送
            lambdaUpdateWrapper.set(MessagePushDetail::getIsSend, YesNoStatus.CANCEL.getCode());
            messagePushDetailService.update(lambdaUpdateWrapper);
            //返还额度
            addPushCancelDetails(messagePushDetail);
            return Result.ok("取消成功!");
        }
        return Result.ok("取消失败，邮件已发送或发送失败!");
    }

    /**
     * 批量取消
     *
     * @param ids
     * @return
     */
    @AutoLog(value = " 邮件推送日志表-批量取消")
@Operation(summary = " 邮件推送日志表-批量取消", description = " 邮件推送日志表-批量取消")
    @GetMapping(value = "/cancelBatch")
    public Result<?> cancelBatch(String ids) {
        List<String> list = Arrays.asList(ids.split(","));
        LambdaQueryWrapper<MessagePushDetail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(MessagePushDetail::getId, list);
        lambdaQueryWrapper.eq(MessagePushDetail::getIsSend, YesNoStatus.NO.getCode());
        List<MessagePushDetail> listResult = messagePushDetailService.list(lambdaQueryWrapper);
        if (listResult.size() > 0) {
            //可以取消的发送明细id
            List<String> cancelIdList = listResult.stream().map(MessagePushDetail::getId).collect(Collectors.toList());
            //修改邮件发送状态
            LambdaUpdateWrapper<MessagePushDetail> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.in(MessagePushDetail::getId, cancelIdList);
            //取消发送
            lambdaUpdateWrapper.set(MessagePushDetail::getIsSend, YesNoStatus.CANCEL.getCode());
            //返还额度
            listResult.forEach(this::addPushCancelDetails);
            this.messagePushDetailService.update(lambdaUpdateWrapper);
        }
        return Result.ok("批量取消成功！");
//        if (listResult.size() > 0 && listResult.size() == list.size()) {
//            //修改网红当天接收数量
////            listResult.forEach(x -> updateRedisKolNum(x.getKolId(), x.getEmailPushDate()));
//            //修改邮件发送状态
//            LambdaUpdateWrapper<MessagePushDetail> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
//            lambdaUpdateWrapper.in(MessagePushDetail::getId, list);
//            //取消发送
//            lambdaUpdateWrapper.set(MessagePushDetail::getIsSend, YesNoStatus.CANCEL.getCode());
//            //返还额度
//            listResult.forEach(this::addPushCancelDetails);
//            this.messagePushDetailService.update(lambdaUpdateWrapper);
//            return Result.ok("批量取消成功！");
//        } else {
//            return Result.error("批量取消失败，所选邮件中有已发送或发送失败！");
//        }
    }


    /**
     * 功能描述:增加取消发送额度返还明细
     *
     * @Author: nqr
     * @Date 2020-11-03 10:47:08
     */
    private void addPushCancelDetails(MessagePushDetail messagePushDetail) {
        String sellerId = messagePushDetail.getSellerId();
        String packagePurchaseId = messagePushDetail.getPackagePurchaseId();
        SysUser sellerUser = userService.getById(sellerId);
        StorePushRefund storePushRefund = new StorePushRefund();
        storePushRefund.setMessagePushDetailId(messagePushDetail.getId());
        storePushRefund.setSellerId(sellerId);
        storePushRefund.setSellerName(messagePushDetail.getSellerName());
        storePushRefund.setKolId(messagePushDetail.getKolId());
        storePushRefund.setKolName(messagePushDetail.getKolName());
        storePushRefund.setKolEmail(messagePushDetail.getKolEmail());
        SysUser sysUser = new SysUser();
        //判断是套餐还是邮件包 为空表示套餐
        if (oConvertUtils.isEmpty(packagePurchaseId)) {
            sysUser.setId(sellerId);
            sysUser.setEmailsDay(sellerUser.getEmailsDay() + 1);
        } else {
            storePushRefund.setPackageId(messagePushDetail.getPackageId());
            storePushRefund.setPackageName(messagePushDetail.getPackageName());
            storePushRefund.setPackagePurchaseId(packagePurchaseId);
            //修改邮件包额度
            updatePackageNum(packagePurchaseId, sellerId);
        }
        storePushRefund.setRefundDate(new Date());
        storePushRefund.setRefundStatus(YesNoStatus.YES.getCode());
        storePushRefund.setRefundType(YesNoStatus.CANCELSEND.getCode());
        storePushRefundService.save(storePushRefund);
        userService.updateById(sysUser);
    }

    /**
     * 功能描述: 返还邮件包额度
     *
     * @Author: nqr
     * @Date 2020-11-03 10:57:46
     */
    private void updatePackageNum(String packagePurchaseId, String sellerId) {
        StoreEmailPackagePurchase emailPackagePurchase = emailPackagePurchaseService.getById(packagePurchaseId);
        StoreEmailPackagePurchase packagePurchaseNew = new StoreEmailPackagePurchase();
        if (oConvertUtils.isNotEmpty(emailPackagePurchase)) {
            packagePurchaseNew.setId(emailPackagePurchase.getId());
            String pushSellerId = emailPackagePurchase.getSellerId();
            String inviteSellerId = emailPackagePurchase.getInviteSellerId();
            if (sellerId.equals(pushSellerId)) {
                packagePurchaseNew.setCurrentPackageNum(emailPackagePurchase.getCurrentPackageNum() + 1);
            } else if (sellerId.equals(inviteSellerId)) {
                packagePurchaseNew.setInviteCurrentPackageNum(emailPackagePurchase.getInviteCurrentPackageNum() + 1);
            }
            emailPackagePurchaseService.updateById(packagePurchaseNew);
        }
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = " 邮件推送日志表-通过id查询")
@Operation(summary = " 邮件推送日志表-通过id查询", description = " 邮件推送日志表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        MessagePushDetail messagePushDetail = messagePushDetailService.getById(id);
        return Result.ok(messagePushDetail);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param messagePushDetail
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, MessagePushDetail messagePushDetail) {
        return super.exportXls(request, messagePushDetail, MessagePushDetail.class, " 邮件推送日志表");
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
        return super.importExcel(request, response, MessagePushDetail.class);
    }

    /**
     * 功能描述:获取发送比例
     *
     * @Author: nqr
     * @Date 2020-11-14 16:04:45
     */
    @AutoLog(value = "获取发送比例")
@Operation(summary = "获取发送比例", description = "获取发送比例获取发送比例")
    @GetMapping(value = "/getSendRatio")
    public Result<?> getSendRatio() {
        //获取日期
        List<JSONObject> list = new ArrayList<>();
        try {
            Date date1 = DateUtils.str2dateS("2020-11-12");
            List<Date> dateList = getDateList(date1);
            for (Date date : dateList) {
                String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(date);
                String url = "http://api.sendcloud.net/apiv2/statday/list?apiUser=gmail_addgmail&apiKey=19379de53728caea1d74f588eaa32704&startDate=" + dateStr + "&endDate=" + dateStr + "&aggregate=1";
                JSONObject jsonObject = (JSONObject) ToolHttpClient.get(url, new HashMap<>());
                JSONObject jsonInfo = jsonObject.getJSONObject("info");
                DataList dataList = JSONObject.toJavaObject(jsonInfo.getJSONObject("dataList"), DataList.class);
                JSONObject object = new JSONObject();
                object.put("date", dateStr);
                if (oConvertUtils.isNotEmpty(dataList)) {
                    object.put("requestNum", dataList.getRequestNum());
                    object.put("deliveredNum", dataList.getDeliveredNum());
                    object.put("invalidEmailsNum", dataList.getInvalidEmailsNum());
                    object.put("deliveredPercent", dataList.getDeliveredPercent());
                    object.put("invalidEmailsPercent", dataList.getInvalidEmailsPercent());
                    list.add(object);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        for (JSONObject jsonObject : list) {
            System.out.println(jsonObject.toJSONString());
        }
        return Result.ok(list);
    }

    @GetMapping(value = "/getSendStatDay")
    public Result<?> getSendStatDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String url = "http://api.sendcloud.net/apiv2/statday/list?apiUser=gmail_addgmail&apiKey=19379de53728caea1d74f588eaa32704&startDate=2020-11-12&endDate=" + sdf.format(new Date()) + "&aggregate=1";
        return Result.ok(RestUtil.get(url));
    }

    /**
     * 功能描述:获取发送比例
     *
     * @Author: nqr
     * @Date 2020-11-14 16:04:45
     */
    @AutoLog(value = "获取发送比例")
@Operation(summary = "获取发送比例", description = "获取发送比例获取发送比例")
    @GetMapping(value = "/getSendRatioBySeller")
    public Result<?> getSendRatioBySeller() {
        //获取日期
        List<JSONObject> list = new ArrayList<>();
        try {
            Date date1 = DateUtils.str2dateS("2020-11-12");
            List<Date> dateList = getDateList(date1);
            for (Date date : dateList) {
                String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(date);
                String url = "http://api.sendcloud.net/apiv2/statday/list?apiUser=yuleikuajing_kolbox&apiKey=15f45307c7dc9cfbc0f478769d7a047e&startDate=" + dateStr + "&endDate=" + dateStr;
                JSONObject jsonObject = (JSONObject) ToolHttpClient.get(url, new HashMap<>());
                JSONObject jsonInfo = jsonObject.getJSONObject("info");
                DataList dataList = JSONObject.toJavaObject(jsonInfo.getJSONObject("dataList"), DataList.class);
                JSONObject object = new JSONObject();
                object.put("date", dateStr);
                if (oConvertUtils.isNotEmpty(dataList)) {
                    object.put("requestNum", dataList.getRequestNum());
                    object.put("deliveredNum", dataList.getDeliveredNum());
                    object.put("invalidEmailsNum", dataList.getInvalidEmailsNum());
                    object.put("deliveredPercent", dataList.getDeliveredPercent());
                    object.put("invalidEmailsPercent", dataList.getInvalidEmailsPercent());
                    list.add(object);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        for (JSONObject jsonObject : list) {
            System.out.println(jsonObject.toJSONString());
        }
        return Result.ok(list);
    }

    @GetMapping(value = "/getSendStatDayBySeller")
    public Result<?> getSendStatDayBySeller() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String url = "http://api.sendcloud.net/apiv2/statday/list?apiUser=yuleikuajing_kolbox&apiKey=15f45307c7dc9cfbc0f478769d7a047e&startDate=2020-11-12&endDate=" + sdf.format(new Date()) + "&aggregate=1";
        return Result.ok(RestUtil.get(url));
    }


    /**
     * 功能描述:获取日期区间
     *
     * @Author: nqr
     * @Date 2020-11-16 15:03:45
     */
    private List<Date> getDateList(Date date1) {
        int i = DateUtils.differentDays(date1, new Date());
        List<Date> dateList;
        if (i > 7) {
            dateList = DateUtils.findDates(DateUtils.getBeforeByDayTime(7), new Date());
        } else {
            dateList = DateUtils.findDates(date1, new Date());
        }
        return dateList;
    }

    /**
     * 功能描述:获取邮件点击率
     *
     * @Author: nqr
     * @Date 2020-11-28 15:03:56
     */
    @GetMapping(value = "/img")
    public void getImg(HttpServletRequest request, HttpServletResponse response) {
        String pushDetailId = request.getParameter("id");
        System.out.println("ip:" + request.getRemoteAddr());
        System.out.println("邮件点击--邮件明细ID：" + pushDetailId);
        String s = (String) ToolHttpClient.get("http://whois.pconline.com.cn/ip.jsp?ip=" + request.getRemoteAddr(), new HashMap<>());
        MessagePushDetail pushDetail = messagePushDetailService.getById(pushDetailId);
        if (oConvertUtils.isNotEmpty(pushDetail) && pushDetail.getIsConfirm() == YesNoStatus.NO.getCode()) {
            //修改此邮件为打开
            MessagePushDetail messagePushDetail = new MessagePushDetail();
            messagePushDetail.setId(pushDetailId);
            messagePushDetail.setIsConfirm(YesNoStatus.YES.getCode());
            messagePushDetail.setIp(request.getRemoteAddr());
            assert s != null;
            messagePushDetail.setIpAddress(s.trim());
            messagePushDetailService.updateById(messagePushDetail);
        }

        //返回的图片（不要修改）
        String valueData = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAIAAACQd1PeAAAADElEQVR42mP4//8/AAX+Av4zEpUUAAAAAElFTkSuQmCC";
        byte[] decode = Base64.getDecoder().decode(valueData);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            output.write(decode);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            ServletOutputStream out = response.getOutputStream();
            output.writeTo(out);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * 功能描述:获取发送总数（成功，失败）
     *
     * @Author: nqr
     * @Date 2020-11-28 15:03:56
     */
    @GetMapping(value = "/getEmailSendResult")
    public Result<?> getEmailSendResult() {
        JSONObject jsonResult = new JSONObject();
        List<MessagePushDetail> list = messagePushDetailService.getEmailSendResult();
        //发送总数
        int sendAll = list.size();
        //发送成功
        List<MessagePushDetail> successList = list.stream().filter(x -> x.getIsSend() == YesNoStatus.YES.getCode()).collect(Collectors.toList());
        int sendSuccess = successList.size();
        //发送失败
        List<MessagePushDetail> errorList = list.stream().filter(x -> x.getIsSend() == YesNoStatus.Exception.getCode()).collect(Collectors.toList());
        int sendError = errorList.size();
        //发送失败
        List<MessagePushDetail> openList = list.stream().filter(x -> x.getIsConfirm() == YesNoStatus.YES.getCode()).collect(Collectors.toList());
        int openNum = openList.size();
        //获取昨天日期
        Date beforeDate = DateUtils.getFetureDate(YesNoStatus.Exception.getCode());
        SimpleDateFormat sft = new SimpleDateFormat("yyyy-MM-dd");
        Date date = DateUtils.str2dateS(sft.format(beforeDate));
        //获取最近7天日期
        List<Date> dateList = DateUtils.findDates(DateUtils.getBeforeByDayTime(7), date);
        List<JSONObject> sendDetailDayList = new ArrayList<>();
        for (Date sendDay : dateList) {
            JSONObject jsonObject = new JSONObject();
            int successCountDay = (int) successList.stream().filter(x -> x.getEmailPushDate().equals(DateUtils.date2Str(sendDay, sft))).count();
            int errorCountDay = (int) errorList.stream().filter(x -> x.getEmailPushDate().equals(DateUtils.date2Str(sendDay, sft))).count();
            int openCountDay = (int) openList.stream().filter(x -> x.getEmailPushDate().equals(DateUtils.date2Str(sendDay, sft))).count();
            jsonObject.put("sendDay", sendDay);
            jsonObject.put("success", successCountDay);
            jsonObject.put("error", errorCountDay);
            jsonObject.put("open", openCountDay);
            sendDetailDayList.add(jsonObject);
        }
        List<MessagePushDetailModel> domainList = messagePushDetailService.getDomainResult(sft.format(beforeDate));
        jsonResult.put("sendAll", sendAll);
        jsonResult.put("sendSuccess", sendSuccess);
        jsonResult.put("sendError", sendError);
        jsonResult.put("openNum", openNum);
        jsonResult.put("sendDetailDayList", sendDetailDayList);
        jsonResult.put("domainList", domainList);
        return Result.ok(jsonResult);
    }

    /**
     * 功能描述:获取商家邮件详情
     *
     * @return org.jeecg.common.api.vo.Result<?>
     * @Author: nqr
     * @Date 2020-12-31 08:54:42
     */
    @GetMapping(value = "/getSellerEmailDetail")
    public Result<?> getSellerEmailDetail(MessagePushDetailModel messagePushDetailModel, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                          HttpServletRequest req) {
        Page<MessagePushDetail> page = new Page<>(pageNo, pageSize);
        IPage<MessagePushDetail> pageList = messagePushDetailService.pageList(page, messagePushDetailModel);
        return Result.ok(pageList);
    }

}
