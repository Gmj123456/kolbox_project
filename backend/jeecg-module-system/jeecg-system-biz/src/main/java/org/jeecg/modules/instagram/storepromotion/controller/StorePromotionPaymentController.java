package org.jeecg.modules.instagram.storepromotion.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.PaymentConstant;
import org.jeecg.common.constant.enums.PayStatusEnum;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.storepromotion.entity.StoreSellerPromotion;
import org.jeecg.modules.instagram.storepromotion.service.IStoreSellerPromotionService;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionPayment;
import org.jeecg.modules.instagram.storepromotion.model.StorePromotionPaymentModel;
import org.jeecg.modules.instagram.storepromotion.service.IStorePromotionPaymentService;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 推广支付记录
 * @Author: dong
 * @Date: 2021-03-09
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "推广支付记录")
@RestController
@RequestMapping("/storeSellerPayment")
public class StorePromotionPaymentController extends JeecgController<StorePromotionPayment, IStorePromotionPaymentService> {


    @Autowired
    private IStorePromotionPaymentService storePromotionPaymentService;

    @Autowired
    private IStoreSellerPromotionService storeSellerPromotionService;


    /**
     * 分页列表查询
     *
     * @param storePromotionPayment
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "推广支付记录-分页列表查询")
@Operation(summary = "推广支付记录-分页列表查询", description = "推广支付记录-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StorePromotionPaymentModel storePromotionPayment,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        if (oConvertUtils.checkUserType(getUserNameByToken())) {
            storePromotionPayment.setSellerId(getUserIdByToken());
        }
        Page<StorePromotionPayment> page = new Page<>(pageNo, pageSize);
        IPage<StorePromotionPaymentModel> pageList = storePromotionPaymentService.pageList(page, storePromotionPayment);
        return Result.ok(pageList);
    }

    /**
     * 分页列表查询 推广支付统计
     *
     * @param storePromotionPayment
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "推广支付统计-分页列表查询")
@Operation(summary = "推广支付统计-分页列表查询", description = "推广支付统计-分页列表查询")
    @GetMapping(value = "/paymentStatisticsList")
    public Result<?> paymentStatisticsList(StorePromotionPaymentModel storePromotionPayment,
                                           @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                           @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                           HttpServletRequest req) {
        if (oConvertUtils.isNotEmpty(storePromotionPayment.getEndPayDate())) {
            String dateStrAddOneDay = DateUtils.dateStrAddOneDay(storePromotionPayment.getEndPayDate());
            storePromotionPayment.setEndPayDate(dateStrAddOneDay);
        }
        Page<StorePromotionPayment> page = new Page<>(pageNo, pageSize);
        IPage<StorePromotionPaymentModel> pageList = storePromotionPaymentService.paymentStatisticsList(page, storePromotionPayment);
        return Result.ok(pageList);
    }


    /**
     * 不分页推广支付统计
     *
     * @param storePromotionPayment
     * @param req
     * @return
     */
    @AutoLog(value = "推广支付统计")
@Operation(summary = "推广支付统计", description = "推广支付统计")
    @GetMapping(value = "/paymentStatistics")
    public Result<?> paymentStatistics(StorePromotionPaymentModel storePromotionPayment,
                                       HttpServletRequest req) {
        if (oConvertUtils.isNotEmpty(storePromotionPayment.getEndPayDate())) {
            String dateStrAddOneDay = DateUtils.dateStrAddOneDay(storePromotionPayment.getEndPayDate());
            storePromotionPayment.setEndPayDate(dateStrAddOneDay);
        }
        // 根据查询条件查出所有，不分页
        List<StorePromotionPaymentModel> listAll = storePromotionPaymentService.paymentStatistics(storePromotionPayment);

        // 仅意向金单数
        Long intentionCount = 0L;
        // 意向金/全款单数
        Long intentionAndFullCount = 0L;
        //
        Long paidInFullCount = 0L;
        // 退款单数
        Long totalCount = 0L;
        // 合计
        Long refundCount = 0L;
        // 超期推广单数
        Long exceedTimeCount = 0L;

        // 统计
        for (StorePromotionPaymentModel storePromotionPaymentModel : listAll) {
            intentionCount += storePromotionPaymentModel.getIntentionCount();
            intentionAndFullCount += storePromotionPaymentModel.getIntentionAndFullCount();
            paidInFullCount += storePromotionPaymentModel.getPaidInFullCount();
            totalCount += storePromotionPaymentModel.getTotalCount();
            refundCount += storePromotionPaymentModel.getRefundCount();
            exceedTimeCount += storePromotionPaymentModel.getExceedTimeCount();
        }
        StorePromotionPaymentModel storePromotionPaymentResult = new StorePromotionPaymentModel();
        storePromotionPaymentResult.setIntentionCount(intentionCount);
        storePromotionPaymentResult.setIntentionAndFullCount(intentionAndFullCount);
        storePromotionPaymentResult.setPaidInFullCount(paidInFullCount);
        storePromotionPaymentResult.setTotalCount(totalCount);
        storePromotionPaymentResult.setRefundCount(refundCount);
        storePromotionPaymentResult.setExceedTimeCount(exceedTimeCount);
        return Result.ok(storePromotionPaymentResult);
    }

    /**
     * 不分页推广支付统计
     *
     * @param storePromotionPayment
     * @param req
     * @return
     */
    @AutoLog(value = "推广支付统计")
@Operation(summary = "推广支付统计", description = "推广支付统计")
    @GetMapping(value = "/statistics")
    public Result<?> statistics(StorePromotionPaymentModel storePromotionPayment,
                                HttpServletRequest req) {
        if (oConvertUtils.isNotEmpty(storePromotionPayment.getEndPayDate())) {
            String dateStrAddOneDay = DateUtils.dateStrAddOneDay(storePromotionPayment.getEndPayDate());
            storePromotionPayment.setEndPayDate(dateStrAddOneDay);
        }
        // 根据查询条件查出所有，不分页
        List<StorePromotionPaymentModel> listAll = storePromotionPaymentService.listAll(storePromotionPayment);

        // 统计收入
        BigDecimal statisticsIncome = new BigDecimal(0);

        // 统计支出
        BigDecimal statisticsExpend = new BigDecimal(0);

        // 统计实际收入
        BigDecimal statisticsRealIncome = new BigDecimal(0);

        // 统计
        for (StorePromotionPaymentModel storePromotionPaymentModel : listAll) {
            // 大于0
            if (storePromotionPaymentModel.getPayRmbAmount().compareTo(BigDecimal.ZERO) >= 0) {
                if (oConvertUtils.isNotEmpty(storePromotionPaymentModel.getPayRmbAmount())) {
                    statisticsIncome = statisticsIncome.add(storePromotionPaymentModel.getPayRmbAmount());
                }

            } else {
                if (oConvertUtils.isNotEmpty(storePromotionPaymentModel.getPayRmbAmount())) {
                    statisticsExpend = statisticsExpend.add(storePromotionPaymentModel.getPayRmbAmount().abs());
                }
            }
        }
        statisticsRealIncome = statisticsIncome.subtract(statisticsExpend);

        StorePromotionPaymentModel storePromotionPaymentResult = new StorePromotionPaymentModel();
        storePromotionPaymentResult.setStatisticsRealIncome(statisticsRealIncome);
        storePromotionPaymentResult.setStatisticsIncome(statisticsIncome);
        storePromotionPaymentResult.setStatisticsExpend(statisticsExpend);
        return Result.ok(storePromotionPaymentResult);
    }


    /**
     * 分页列表查询
     *
     * @param storePromotionPayment
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "推广支付记录-分页列表查询")
@Operation(summary = "推广支付记录-分页列表查询", description = "推广支付记录-分页列表查询")
    @GetMapping(value = "/pageList")
    public Result<?> pageList(StorePromotionPaymentModel storePromotionPayment,
                              @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                              HttpServletRequest req) {
        Page<StorePromotionPayment> page = new Page<>(pageNo, pageSize);
        if (oConvertUtils.isNotEmpty(storePromotionPayment.getEndPayDate())) {
            // 往后加一天
            String dateStrAddOneDay = DateUtils.dateStrAddOneDay(storePromotionPayment.getEndPayDate());
            storePromotionPayment.setEndPayDate(dateStrAddOneDay);
        }
        IPage<StorePromotionPaymentModel> pageList = storePromotionPaymentService.pageList(page, storePromotionPayment);
        return Result.ok(pageList);
    }

    /**
     * 分页列表查询
     *
     * @param storePromotionPayment
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "推广支付记录-分页列表查询")
@Operation(summary = "推广支付记录-分页列表查询", description = "推广支付记录-分页列表查询")
    @GetMapping(value = "/pagePromList")
    public Result<?> pagePromList(StorePromotionPaymentModel storePromotionPayment,
                              @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                              HttpServletRequest req) {
        Page<StorePromotionPayment> page = new Page<>(pageNo, pageSize);
        if (oConvertUtils.isNotEmpty(storePromotionPayment.getEndPayDate())) {
            // 往后加一天
            String dateStrAddOneDay = DateUtils.dateStrAddOneDay(storePromotionPayment.getEndPayDate());
            storePromotionPayment.setEndPayDate(dateStrAddOneDay);
        }
        IPage<StorePromotionPaymentModel> pageList = storePromotionPaymentService.pagePromList(page, storePromotionPayment);
        return Result.ok(pageList);
    }


    /**
     * 通过id删除
     * 撤销支付
     *
     * @param id
     * @return
     */
    @DeleteMapping(value = "/delete")
    @Transactional
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {

        // 获取当前支付记录
        StorePromotionPayment storePromotionPayment = storePromotionPaymentService.getById(id);

        // 线上支付
        if (storePromotionPayment.getIsOffline().equals(PaymentConstant.PAY_OFF_LINE)) {
            return Result.error("线上支付记录不能撤销!");
        }

        // 获取推广订单信息
        StoreSellerPromotion storeSellerPromotion = storeSellerPromotionService.getById(storePromotionPayment.getPromId());

        BigDecimal paymentAmount = new BigDecimal(0);

        // 定金操作
        if (storePromotionPayment.getPayType().equals(PaymentConstant.PAY_TYPE_DEPOSIT) ||
                storePromotionPayment.getPayType().equals(PaymentConstant.PAY_TYPE_FINAL) ||
                storePromotionPayment.getPayType().equals(PaymentConstant.PAY_TYPE_ALL)) {

            // 退回前的实付总金额(rmb),所有支付记录相加(只含定金)
            BigDecimal oldPaymentAmount = storePromotionPaymentService.paymentAmountRmbnNoEARNEST(storeSellerPromotion);
            // 需要退回的金额(rmb)
            BigDecimal payAmountRmb = new BigDecimal(0);
            // rmb
            if (PaymentConstant.RMB_SYMBOL.equals(storePromotionPayment.getCurrencySymbol())) {
                payAmountRmb = storePromotionPayment.getPayAmount();
            } else {
                payAmountRmb = storePromotionPayment.getPayAmount().multiply(storeSellerPromotion.getExchangeRate());
            }

            // 退回后的实付总金额(原实付金额-回退金额)（rmb）
            BigDecimal paymentAmountNow = oldPaymentAmount.subtract(payAmountRmb);
            storeSellerPromotion.setPaymentAmount(paymentAmountNow);

            paymentAmount = paymentAmountNow;

        }

        // 意向金
        if (storePromotionPayment.getPayType().equals(PaymentConstant.PAY_TYPE_EARNEST)) {
            BigDecimal IntentionAmount = storeSellerPromotion.getIntentionAmount().subtract(storePromotionPayment.getPayRmbAmount());
            storeSellerPromotion.setIntentionAmount(IntentionAmount);
        }

        // refund operation
        // because refund amount is nagetive, The refund operation is add , not a sub.
        if (storePromotionPayment.getPayType().equals(PaymentConstant.PAY_TYPE_REFUND)) {
            BigDecimal refunAmount = storeSellerPromotion.getRefundAmount().add(storePromotionPayment.getPayRmbAmount());
            storeSellerPromotion.setRefundAmount(refunAmount);
        }


        if (oConvertUtils.isNotEmpty(storeSellerPromotion.getIntentionAmount())) {
            paymentAmount = paymentAmount.add(storeSellerPromotion.getIntentionAmount());
        }

        if (oConvertUtils.isNotEmpty(storeSellerPromotion.getRefundAmount())) {
            paymentAmount = paymentAmount.subtract(storeSellerPromotion.getRefundAmount());
        }

        // 应付金额(rmb)
        BigDecimal payableAmount = new BigDecimal(0);
        if (oConvertUtils.isNotEmpty(storeSellerPromotion.getTotalRmbAmount())) {
            // 应付金额(rmb)
            payableAmount = storeSellerPromotion.getTotalRmbAmount().subtract(paymentAmount);
        } else {
            payableAmount = payableAmount.subtract(paymentAmount);
        }


        //实付金额为空或者0.00 待支付
        if (oConvertUtils.isEmpty(paymentAmount) || paymentAmount.compareTo(BigDecimal.ZERO) == 0) {
            storeSellerPromotion.setPayStatus(PayStatusEnum.TOBEPAID.getCode());
        }

        //支付金额大于0 已付订金
        if (paymentAmount.compareTo(BigDecimal.ZERO) > 0) {
            storeSellerPromotion.setPayStatus(PayStatusEnum.DOWNPAYMENT.getCode());
        }


        try {
            // 更新订单信息
            storeSellerPromotionService.updateById(storeSellerPromotion);

            // 删除支付记录
            storePromotionPaymentService.removeById(id);
        } catch (Exception e) {
            e.printStackTrace();
            // 手动回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        return Result.ok("撤销支付成功!");
    }

    /**
     * 功能描述: 根据推广需求id获取支付金额
     *
     * @Author: nqr
     * @Date 2021-03-10 14:31:36
     */
    @GetMapping(value = "/getPayList")
    public Result<?> getPayListByPromId(@RequestParam(name = "promId", required = true) String promId) {
        LambdaQueryWrapper<StorePromotionPayment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StorePromotionPayment::getPromId, promId);
        List<StorePromotionPayment> payList = storePromotionPaymentService.list(lambdaQueryWrapper);
        return Result.ok(payList);
    }


    /**
     * 导出excel
     *
     * @param request
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                  @RequestParam(name = "pageSize", defaultValue = "30") Integer pageSize, StorePromotionPaymentModel storePromotionPaymentModel) {
        Page<StorePromotionPayment> page = new Page<>(pageNo, pageSize);
        IPage<StorePromotionPaymentModel> pageList = storePromotionPaymentService.pageList(page, storePromotionPaymentModel);
        return exportXlsBylist(request, pageList.getRecords(), StorePromotionPaymentModel.class, "推广支付记录");
    }

    /**
     * 导出excel
     *
     * @param request
     */
    @RequestMapping(value = "/exportXlsAll")
    public ModelAndView exportXlsAll(HttpServletRequest request, StorePromotionPaymentModel storePromotionPaymentModel) {
        List<StorePromotionPaymentModel> list = storePromotionPaymentService.selectlist(storePromotionPaymentModel);
        return exportXlsBylist(request, list, StorePromotionPaymentModel.class, "推广支付记录");
    }


    /**
     * 导出excel
     *
     * @param request
     */
    protected ModelAndView exportXlsBylist(HttpServletRequest request, List<StorePromotionPaymentModel> pageList, Class<StorePromotionPaymentModel> clazz, String title) {

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();


        List<StorePromotionPaymentModel> exportList = null;

        // 过滤选中数据
        String selections = request.getParameter("selections");
        if (oConvertUtils.isNotEmpty(selections)) {
            List<String> selectionList = Arrays.asList(selections.split(","));
            exportList = pageList.stream().filter(item -> selectionList.contains(getIds(item))).collect(Collectors.toList());
        } else {
            exportList = pageList;
        }

        // Step.3 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, title); //此处设置的filename无效 ,前端会重更新设置一下
        mv.addObject(NormalExcelConstants.CLASS, clazz);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams(title + "报表", "导出人:" + sysUser.getRealname(), title));
        mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
        return mv;
    }

    /**
     * 获取对象ID
     *
     * @return
     */
    private String getIds(StorePromotionPaymentModel item) {
        try {
            return PropertyUtils.getProperty(item, "id").toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
        return super.importExcel(request, response, StorePromotionPayment.class);
    }
}
