package org.jeecg.modules.instagram.storepromotion.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.CelebrityPromStatus;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.enums.ApprovedStatusType;
import org.jeecg.common.constant.enums.PayStatusEnum;
import org.jeecg.common.constant.enums.PromotionGoodsType;
import org.jeecg.common.constant.enums.YesNoStatus;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivate;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityPrivateService;
import org.jeecg.modules.instagram.storecountry.entity.StoreCountry;
import org.jeecg.modules.instagram.storecountry.service.IStoreCountryService;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionGoods;
import org.jeecg.modules.instagram.storepromotion.entity.StoreSellerPromotion;
import org.jeecg.modules.instagram.storepromotion.model.StorePromotionGoodsModel;
import org.jeecg.modules.instagram.storepromotion.service.IBasePromotionService;
import org.jeecg.modules.instagram.storepromotion.service.IStorePromotionGoodsService;
import org.jeecg.modules.instagram.storepromotion.service.IStoreSellerPromotionService;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionGoodsCelebrity;
import org.jeecg.modules.instagram.storepromotion.model.GoodsCelebrityModel;
import org.jeecg.modules.instagram.storepromotion.model.StorePromotionGoodsCelebrityModel;
import org.jeecg.modules.instagram.storepromotion.service.IStorePromotionGoodsCelebrityService;
import org.jeecg.modules.system.entity.SysDictItem;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static org.jeecg.common.constant.CelebrityPromStatus.PROM_STATUS_3;
import static org.jeecg.common.constant.CelebrityPromStatus.PROM_STATUS_FINISH;

/**
 * @Description: 商家推广产品网红关联表
 * @Author: jeecg-boot
 * @Date: 2021-02-07
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "商家推广产品网红关联表")
@RestController
@RequestMapping("/storePromotionGoodsCelebrity")
public class StorePromotionGoodsCelebrityController extends JeecgController<StorePromotionGoodsCelebrity, IStorePromotionGoodsCelebrityService> {
    @Autowired
    private IStorePromotionGoodsCelebrityService goodsCelebrityService;
    @Autowired
    private IStoreSellerPromotionService storeSellerPromotionService;
    @Autowired
    private IStorePromotionGoodsService storePromotionGoodsService;
    @Autowired
    private IStoreCelebrityPrivateService privateService;
    @Autowired
    private IStoreCountryService storeCountryService;
    @Autowired
    private IBasePromotionService basePromotionService;
    @Autowired
    private ISysDictService sysDictService;

    /**
     * 分页列表查询
     *
     * @param storePromotionGoodsCelebrity
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "商家推广产品网红关联表-分页列表查询")
@Operation(summary = "商家推广产品网红关联表-分页列表查询", description = "商家推广产品网红关联表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StorePromotionGoodsCelebrity storePromotionGoodsCelebrity,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<StorePromotionGoodsCelebrity> queryWrapper = QueryGenerator.initQueryWrapper(storePromotionGoodsCelebrity, req.getParameterMap());
        Page<StorePromotionGoodsCelebrity> page = new Page<>(pageNo, pageSize);
        IPage<StorePromotionGoodsCelebrity> pageList = goodsCelebrityService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 网红费用统计总额
     *
     * @param storePromotionGoodsCelebrityModel
     * @return
     */
    @AutoLog(value = "商家推广产品网红关联表-分页列表查询")
@Operation(summary = "商家推广产品网红关联表-分页列表查询", description = "商家推广产品网红关联表-分页列表查询")
    @GetMapping(value = "/listAll")
    public Result<?> listAll(StorePromotionGoodsCelebrityModel storePromotionGoodsCelebrityModel) {
        List<StorePromotionGoodsCelebrityModel> list = goodsCelebrityService.listAll(storePromotionGoodsCelebrityModel);
        return Result.ok(list);
    }

    /**
     * 网红费用统计
     *
     * @param storePromotionGoodsCelebrityModel
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "商家推广产品网红关联表-分页列表查询")
@Operation(summary = "商家推广产品网红关联表-分页列表查询", description = "商家推广产品网红关联表-分页列表查询")
    @GetMapping(value = "/getCelebrityStatistics")
    public Result<?> getCelebrityStatistics(StorePromotionGoodsCelebrityModel storePromotionGoodsCelebrityModel,
                                            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                            HttpServletRequest req) {
        Page<StorePromotionGoodsCelebrityModel> page = new Page<>(pageNo, pageSize);
        IPage<StorePromotionGoodsCelebrityModel> pageList = goodsCelebrityService.getCelebrityStatistics(page, storePromotionGoodsCelebrityModel);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param storePromotionGoodsCelebrity
     * @return
     */
    @AutoLog(value = "商家推广产品网红关联表-添加")
@Operation(summary = "商家推广产品网红关联表-添加", description = "商家推广产品网红关联表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody StorePromotionGoodsCelebrity storePromotionGoodsCelebrity) {
        storePromotionGoodsCelebrity.setCreateBy(getUserNameByToken());
        storePromotionGoodsCelebrity.setCreateTime(new Date());
        goodsCelebrityService.save(storePromotionGoodsCelebrity);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param storePromotionGoodsCelebrity
     * @return
     */
    @AutoLog(value = "商家推广产品网红关联表-编辑")
@Operation(summary = "商家推广产品网红关联表-编辑", description = "商家推广产品网红关联表-编辑")
    @PutMapping(value = "/editOld")
    public Result<?> editOld(@RequestBody StorePromotionGoodsCelebrity storePromotionGoodsCelebrity) {
        Integer celebrityPromStatus = storePromotionGoodsCelebrity.getCelebrityPromStatus();
        String goodsCelebrityId = storePromotionGoodsCelebrity.getId();
        String promId = storePromotionGoodsCelebrity.getPromId();
        // 判断网红是否已经完成
        if (oConvertUtils.isNotEmpty(celebrityPromStatus)) {
            // 判断是否需要结束推广
            if (celebrityPromStatus.equals(YesNoStatus.FINISH.getCode())) {
                // 获取当前推广信息
                StoreSellerPromotion sellerPromotion = storeSellerPromotionService.getById(promId);
                BigDecimal totalAmount = oConvertUtils.isNotEmpty(sellerPromotion.getTotalAmount()) ? sellerPromotion.getTotalAmount() : BigDecimal.ZERO;
                // 获取当前推广下所有的产品
                List<StorePromotionGoods> promotionGoodsList = storePromotionGoodsService.getPromGoodsList(promId);
                // 获取所有产品id，获取产品下的所有网红
                List<String> promGoodsIds = promotionGoodsList.stream().map(StorePromotionGoods::getId).collect(Collectors.toList());
                Map<String, List<StorePromotionGoodsCelebrity>> map = goodsCelebrityService.getGoodsCelebrityList(promGoodsIds, goodsCelebrityId, celebrityPromStatus);
                // 判断产品状态
                JSONObject jsonObject = goodsCelebrityService.checkGoodsStatus(promotionGoodsList, map);
                List<StorePromotionGoods> promotionGoodsListNew = jsonObject.getJSONArray(CommonConstant.PROMOTION_GOODS_LIST_NEW).toJavaList(StorePromotionGoods.class);
                // 判断推广状态
                StoreSellerPromotion storeSellerPromotion = storeSellerPromotionService.checkPromStatusType(promId, promotionGoodsListNew, jsonObject, promGoodsIds);
                if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
                    boolean finishStatus = storeSellerPromotionService.checkFinishStatus(storeSellerPromotion);
                    if (!finishStatus) {
                        return Result.error("推广需求未付清，结束推广失败！");
                    } else {
                        // 修改对应数据
                        goodsCelebrityService.updateGoodsCelebrityById(promotionGoodsListNew, storeSellerPromotion);
                    }
                }
            }
        }
        goodsCelebrityService.updateById(storePromotionGoodsCelebrity);
        return Result.ok("编辑成功!");
    }

    /**
     * 功能描述:编辑
     *
     * @return org.jeecg.common.api.vo.Result<?>
     * @Date 2023-08-04 09:23:51
     */
    @AutoLog(value = "商家推广产品网红关联表-编辑")
@Operation(summary = "商家推广产品网红关联表-编辑", description = "商家推广产品网红关联表-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody StorePromotionGoodsCelebrity storePromotionGoodsCelebrity) {

        // 网红状态
        Integer celebrityPromStatus = storePromotionGoodsCelebrity.getCelebrityPromStatus();
        // 查询当前网红信息
        String goodsCelebrityId = storePromotionGoodsCelebrity.getId();
        StorePromotionGoodsCelebrity goodsCelebrityOld = goodsCelebrityService.getById(goodsCelebrityId);
        // 网红推广费
        BigDecimal promPrice = Optional.ofNullable(storePromotionGoodsCelebrity.getPromPrice()).orElse(goodsCelebrityOld.getPromPrice());
        // 网红费用
        BigDecimal celebrityPriceOld = Optional.ofNullable(goodsCelebrityOld.getCelebrityPrice()).orElse(BigDecimal.ZERO);
        BigDecimal celebrityPrice = Optional.ofNullable(storePromotionGoodsCelebrity.getCelebrityPrice()).orElse(celebrityPriceOld);
        // 判断当前网红是否已选中
        Integer celebrityStatus = goodsCelebrityOld.getCelebrityStatus();
        if (celebrityStatus == YesNoStatus.NO.getCode()) {
            return Result.error("请选择网红后再编辑");
        }
        // 网红状态
        Integer celebrityPromStatusOld = goodsCelebrityOld.getCelebrityPromStatus();
        if (celebrityPromStatusOld.equals(PROM_STATUS_FINISH) || celebrityPromStatusOld.equals(PROM_STATUS_3)) {
            return Result.error("修改失败，当前网红已完成或已终止");
        }
        String promId = storePromotionGoodsCelebrity.getPromId();
        StoreSellerPromotion sellerPromotion = null;
        StoreSellerPromotion sellerPromotionNew = new StoreSellerPromotion();
        sellerPromotionNew.setId(promId);
        // 判断推广费是否变化
        BigDecimal promPriceOld = goodsCelebrityOld.getPromPrice();
        if (promPriceOld.compareTo(promPrice) != 0) {
            BigDecimal subtract = promPrice.subtract(promPriceOld);
            sellerPromotion = storeSellerPromotionService.getById(promId);
            BigDecimal taxAmount = BigDecimal.ZERO;
            BigDecimal taxAmountOld = BigDecimal.ZERO;

            // 判断是否包含税费
            if (goodsCelebrityOld.getIsIncludingTax() == 1) {
                // 包含税费，重新计算税费，税费总金额
                // 获取税率
                BigDecimal taxRate = sellerPromotion.getTaxRate();
                // 旧税费
                taxAmountOld = promPriceOld.multiply(taxRate).divide(new BigDecimal(100), 4, RoundingMode.HALF_UP);
                // 新税费
                taxAmount = promPrice.multiply(taxRate).divide(new BigDecimal(100), 4, RoundingMode.HALF_UP);

                // 税费总额 = 原税费总额+新推广金额税费-原推广金额税费
                sellerPromotion.setTotalTaxAmount(sellerPromotion.getTotalTaxAmount().add(taxAmount).subtract(taxAmountOld));
                sellerPromotionNew.setTotalTaxAmount(sellerPromotion.getTotalTaxAmount().add(taxAmount).subtract(taxAmountOld));
            }

            // 获取汇率
            BigDecimal exchangeRate = sellerPromotion.getExchangeRate();
            // 订单总额 = 原税费总额+推广变化金额+新推广金额税费-原推广金额税费
            BigDecimal totalAmountNew = sellerPromotion.getTotalAmount().add(subtract).add(taxAmount).subtract(taxAmountOld);
            sellerPromotion.setTotalAmount(totalAmountNew);
            sellerPromotion.setTotalRmbAmount(totalAmountNew.multiply(exchangeRate));

            sellerPromotionNew.setTotalAmount(totalAmountNew);
            sellerPromotionNew.setTotalRmbAmount(totalAmountNew.multiply(exchangeRate));

        }
        // 判断网红费用时否变化
        if (celebrityPriceOld.compareTo(celebrityPrice) != 0) {
            // 判断网红费用是否大于推广费
            if (celebrityPrice.compareTo(promPrice) > 0) {
                return Result.error("修改失败，网红费用大于网红推广费");
            }
        }

        List<StorePromotionGoods> promotionGoodsListNew = new ArrayList<>();
        // 判断网红是否已经完成
        if (celebrityPromStatus.equals(PROM_STATUS_3) || celebrityPromStatus.equals(PROM_STATUS_FINISH)) {
            // 获取推广相关信息
            JSONObject jsonObject = basePromotionService.getPromotionData(promId);
            if (sellerPromotion == null) {
                // 获取当前推广信息
                sellerPromotion = jsonObject.getJSONObject("sellerPromotion").toJavaObject(StoreSellerPromotion.class);
            }
            // 获取当前推广下所有的产品
            List<StorePromotionGoods> promotionGoodsList = jsonObject.getJSONArray("promotionGoodsList").toJavaList(StorePromotionGoods.class);
            List<StorePromotionGoodsCelebrity> goodsCelebrities = jsonObject.getJSONArray("goodsCelebrities").toJavaList(StorePromotionGoodsCelebrity.class);
            // 修改当前网红状态
            if (oConvertUtils.isNotEmpty(goodsCelebrityId)) {
                goodsCelebrities.stream()
                        .filter(x -> x.getId().equals(goodsCelebrityId))
                        .findFirst()
                        .ifPresent(goodsCelebrity -> goodsCelebrity.setCelebrityPromStatus(celebrityPromStatus));
            }
            // 获取当前推广总金额
            BigDecimal totalAmount = Optional.ofNullable(sellerPromotion.getTotalAmount()).orElse(BigDecimal.ZERO);

            // 获取所有产品id，获取产品下的所有网红
            List<String> promGoodsIds = promotionGoodsList.stream().map(StorePromotionGoods::getId).collect(Collectors.toList());
            Map<String, List<StorePromotionGoodsCelebrity>> map = goodsCelebrities.stream().collect(Collectors.groupingBy(StorePromotionGoodsCelebrity::getGoodsId));
            // 判断产品状态
            JSONObject result = goodsCelebrityService.checkGoodsStatus(promotionGoodsList, map);
            promotionGoodsListNew = result.getJSONArray(CommonConstant.PROMOTION_GOODS_LIST_NEW).toJavaList(StorePromotionGoods.class);
            // 判断推广状态
            StoreSellerPromotion storeSellerPromotion = storeSellerPromotionService.checkPromStatusType(promId, promotionGoodsListNew, result, promGoodsIds);
            if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
                boolean finishStatus = storeSellerPromotionService.checkFinishStatus(storeSellerPromotion);
                if (!finishStatus) {
                    return Result.error("推广需求未付清，结束推广失败！");
                }
            }
            sellerPromotionNew.setPromStatus(storeSellerPromotion.getPromStatus());
        }
        // 修改对应数据
        goodsCelebrityService.editGoodsCelebrity(storePromotionGoodsCelebrity, promotionGoodsListNew, sellerPromotionNew);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "商家推广产品网红关联表-通过id删除")
@Operation(summary = "商家推广产品网红关联表-通过id删除", description = "商家推广产品网红关联表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        goodsCelebrityService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "商家推广产品网红关联表-批量删除")
@Operation(summary = "商家推广产品网红关联表-批量删除", description = "商家推广产品网红关联表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.goodsCelebrityService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "商家推广产品网红关联表-通过id查询")
@Operation(summary = "商家推广产品网红关联表-通过id查询", description = "商家推广产品网红关联表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        StorePromotionGoodsCelebrity storePromotionGoodsCelebrity = goodsCelebrityService.getById(id);
        return Result.ok(storePromotionGoodsCelebrity);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param storePromotionGoodsCelebrity
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StorePromotionGoodsCelebrity storePromotionGoodsCelebrity) {
        return super.exportXls(request, storePromotionGoodsCelebrity, StorePromotionGoodsCelebrity.class, "商家推广产品网红关联表");
    }

    /**
     * 导出excel
     *
     * @param request
     * @param goodsCelebrityModel
     */
    @RequestMapping(value = "/exportXlsCelUrl")
    public ModelAndView exportXlsCelUrl(HttpServletRequest request, GoodsCelebrityModel goodsCelebrityModel) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<GoodsCelebrityModel> exportList = goodsCelebrityService.getCelebrityStatisticsList(goodsCelebrityModel);
        // Step.3 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "网红费用统计");
        mv.addObject(NormalExcelConstants.CLASS, GoodsCelebrityModel.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("网红费用统计报表", "导出人:" + sysUser.getRealname(), "网红费用统计"));
        mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
        return mv;
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
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();
            String filename = file.getOriginalFilename();
            ImportParams params = new ImportParams();
            // params.setTitleRows(2);
            // params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<StorePromotionGoodsCelebrity> list = ExcelImportUtil.importExcel(file.getInputStream(), StorePromotionGoodsCelebrity.class, params);
                // 批量插入数据
                long start = System.currentTimeMillis();
                // TODO 处理插入，保存用户名


                log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
                return Result.ok("文件导入成功！数据行数：" + list.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败:" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.error("文件导入失败！");
    }


    /**
     * 功能描述:商家推广产品网红编辑
     *
     * @param storePromotionGoodsCelebrityModel
     * @return org.jeecg.common.api.vo.Result<?>
     * @Date 2021-08-18 16:31:02
     */
    @AutoLog(value = "商家推广产品网红关联表-编辑")
@Operation(summary = "商家推广产品网红关联表-编辑", description = "商家推广产品网红关联表-编辑")
    @PutMapping(value = "/editStorePromotionGoodsCelebrity")
    public Result<?> editStorePromotionGoodsCelebrity(@RequestBody StorePromotionGoodsCelebrityModel storePromotionGoodsCelebrityModel) {
        // 当前推广网红id
        String goodsCelebrityId = storePromotionGoodsCelebrityModel.getId();
        // 获取当前产品网红
        StorePromotionGoodsCelebrity goodsCelebrity = goodsCelebrityService.getById(goodsCelebrityId);
        BigDecimal promPriceOld = Optional.ofNullable(goodsCelebrity.getPromPrice()).orElse(BigDecimal.ZERO);
        BigDecimal promPriceNew = Optional.ofNullable(storePromotionGoodsCelebrityModel.getPromPrice()).orElse(BigDecimal.ZERO);
        // 需要修改的状态
        Integer celebrityPromStatus = storePromotionGoodsCelebrityModel.getCelebrityPromStatus();
        // 当前推广id
        String promId = storePromotionGoodsCelebrityModel.getPromId();
        // 获取当前推广信息
        StoreSellerPromotion sellerPromotion = storeSellerPromotionService.getById(promId);
        BigDecimal totalAmount = Optional.ofNullable(sellerPromotion.getTotalAmount()).orElse(BigDecimal.ZERO);
        Integer promStatus = sellerPromotion.getPromStatus();
        // 如果当前推广为已结束状态，判断当前修改是否改变推广状态和推广金额
        if (promStatus == PromotionGoodsType.FINISHEDABNORMAL.getCode() || promStatus == PromotionGoodsType.FINISHEDNORMAL.getCode()) {
            // 如果改变推广状态
            if (!goodsCelebrity.getCelebrityPromStatus().equals(celebrityPromStatus)) {
                return Result.error("当前推广已结束，网红推广状态不可修改！");
            }
            // 如果改变推广金额
            if (!promPriceOld.equals(promPriceNew)) {
                return Result.error("当前推广已结束，网红推广费不可修改！");
            }
        }
        // 判断网红状态，如果已经填写订单号修改状态为待上线
        if (oConvertUtils.isNotEmpty(storePromotionGoodsCelebrityModel.getGoodsWaybill()) && celebrityPromStatus == YesNoStatus.NO.getCode()) {
            storePromotionGoodsCelebrityModel.setCelebrityPromStatus(YesNoStatus.YES.getCode());
        }
        // 获取当前推广下所有的产品
        List<StorePromotionGoods> promotionGoodsList = storePromotionGoodsService.getPromGoodsList(promId);
        // 获取所有产品id，获取产品下的所有网红
        List<String> promGoodsIds = promotionGoodsList.stream().map(StorePromotionGoods::getId).collect(Collectors.toList());
        String celId = null;
        if (celebrityPromStatus == YesNoStatus.FINISH.getCode() || celebrityPromStatus == YesNoStatus.Exception.getCode()) {
            celId = goodsCelebrityId;
        }
        Map<String, List<StorePromotionGoodsCelebrity>> map = goodsCelebrityService.getGoodsCelebrityList(promGoodsIds, celId, celebrityPromStatus);
        // 判断产品状态
        JSONObject jsonObject = goodsCelebrityService.checkGoodsStatus(promotionGoodsList, map);
        List<StorePromotionGoods> promotionGoodsListNew = jsonObject.getJSONArray(CommonConstant.PROMOTION_GOODS_LIST_NEW).toJavaList(StorePromotionGoods.class);
        // 判断推广状态
        StoreSellerPromotion storeSellerPromotion = storeSellerPromotionService.checkPromStatusType(promId, promotionGoodsListNew, jsonObject, promGoodsIds);
        // 判断推广状态
        if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
            boolean finishStatus = storeSellerPromotionService.checkFinishStatus(storeSellerPromotion);
            if (!finishStatus) {
                return Result.error("推广需求未付清，请先付清再结束网红推广");
            }
        }
        // 判断当前网红是否已选中
        if (goodsCelebrity.getCelebrityStatus() == YesNoStatus.YES.getCode()) {
            // 获取其余网红费用
            BigDecimal celebrityTotalAmount = goodsCelebrityService.getCelebrityTotalAmount(promId, storePromotionGoodsCelebrityModel.getCelebrityPrice(), goodsCelebrityId);
            BigDecimal celebrityTotalAmountOld = Optional.ofNullable(celebrityTotalAmount).orElse(BigDecimal.ZERO);

            // 设置推广网红费用
            storeSellerPromotion.setCelebrityTotalAmount(celebrityTotalAmountOld);
        }
        // 修改网红信息
        goodsCelebrityService.updateById(storePromotionGoodsCelebrityModel);
        // 修改网红粉丝数、平均点赞数、平均互动率、平均观看量、总视频数、总点赞量
        StoreCelebrityPrivate storeCelebrityPrivate = new StoreCelebrityPrivate();
        storeCelebrityPrivate.setId(storePromotionGoodsCelebrityModel.getCelebrityPrivateId());
        storeCelebrityPrivate.setFollowersNum(storePromotionGoodsCelebrityModel.getFollowersNum());
        storeCelebrityPrivate.setLikeAvgNum(storePromotionGoodsCelebrityModel.getLikeAvgNum());
        storeCelebrityPrivate.setInteractAvgNum(storePromotionGoodsCelebrityModel.getInteractAvgNum());
        storeCelebrityPrivate.setPlayAvgNum(BigInteger.valueOf(storePromotionGoodsCelebrityModel.getPlayAvgNum()));
        storeCelebrityPrivate.setVideoCount(storePromotionGoodsCelebrityModel.getVideoCount());
        storeCelebrityPrivate.setLikeCount(storePromotionGoodsCelebrityModel.getLikeCount());
        goodsCelebrityService.updateGoodsCelebrityById(promotionGoodsListNew, storeSellerPromotion);
        return Result.ok("编辑成功!");
    }

    /**
     * 功能描述:商家推广产品网红编辑
     *
     * @param storePromotionGoodsCelebrityModel
     * @return org.jeecg.common.api.vo.Result<?>
     * @Date 2021-08-18 16:31:02
     */
    @AutoLog(value = "商家推广产品网红关联表-编辑")
@Operation(summary = "商家推广产品网红关联表-编辑", description = "商家推广产品网红关联表-编辑")
    @PutMapping(value = "/editGoodsCelebrityRemark")
    public Result<?> editGoodsCelebrityRemark(@RequestBody StorePromotionGoodsCelebrityModel storePromotionGoodsCelebrityModel) {
        String remark = storePromotionGoodsCelebrityModel.getRemark();
        LambdaUpdateWrapper<StorePromotionGoodsCelebrity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(StorePromotionGoodsCelebrity::getId, storePromotionGoodsCelebrityModel.getId());
        updateWrapper.set(StorePromotionGoodsCelebrity::getRemark, remark);
        goodsCelebrityService.update(updateWrapper);
        return Result.ok("编辑成功!");
    }

    /**
     * 功能描述:商家推广产品网红编辑
     *
     * @param storePromotionGoodsCelebrityModel
     * @return org.jeecg.common.api.vo.Result<?>
     * @Date 2021-08-18 16:31:02
     */
    @AutoLog(value = "商家推广产品网红关联表-编辑")
@Operation(summary = "商家推广产品网红关联表-编辑", description = "商家推广产品网红关联表-编辑")
    @PutMapping(value = "/editStorePromotionGoodsCelebrityNew")
    public Result<?> editStorePromotionGoodsCelebrityNew(@RequestBody StorePromotionGoodsCelebrityModel storePromotionGoodsCelebrityModel) {
        BigDecimal promPriceNow = Optional.ofNullable(storePromotionGoodsCelebrityModel.getPromPrice()).orElse(BigDecimal.ZERO);
        if (promPriceNow.compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error("请输入推广金额！");
        }
        // 当前推广网红id
        String goodsCelebrityId = storePromotionGoodsCelebrityModel.getId();
        // 获取当前产品网红
        StorePromotionGoodsCelebrity goodsCelebrity = goodsCelebrityService.getById(goodsCelebrityId);
        // 获取当前网红推广费
        BigDecimal promPriceOld = Optional.ofNullable(goodsCelebrity.getPromPrice()).orElse(BigDecimal.ZERO);
        // 需要修改的状态
        Integer celebrityPromStatus = storePromotionGoodsCelebrityModel.getCelebrityPromStatus();
        // 当前推广id
        String promId = storePromotionGoodsCelebrityModel.getPromId();
        // 获取当前推广信息
        StoreSellerPromotion sellerPromotion = storeSellerPromotionService.getById(promId);
        // 获取汇率
        BigDecimal exchangeRate = sellerPromotion.getExchangeRate();

        // 判断当前网红是否包含税费
        if (goodsCelebrity.getIsIncludingTax() == 1) {
            BigDecimal taxRate = Optional.ofNullable(sellerPromotion.getTaxRate()).orElse(BigDecimal.ZERO);
            BigDecimal taxRateAmount = promPriceNow.multiply(taxRate).divide(new BigDecimal(100), 4, RoundingMode.HALF_UP);
            storePromotionGoodsCelebrityModel.setTaxAmount(taxRateAmount);
        } else {
            storePromotionGoodsCelebrityModel.setTaxAmount(BigDecimal.ZERO);
        }


        BigDecimal paymentAmount = Optional.ofNullable(sellerPromotion.getPaymentAmount()).orElse(BigDecimal.ZERO);
        BigDecimal intentionAmount = Optional.ofNullable(sellerPromotion.getIntentionAmount()).orElse(BigDecimal.ZERO);
        BigDecimal refundAmount = Optional.ofNullable(sellerPromotion.getRefundAmount()).orElse(BigDecimal.ZERO);
        // 已付金额
        BigDecimal paidAmountOld = paymentAmount.add(intentionAmount).subtract(refundAmount);

        // 获取总推广费
        BigDecimal totalAmount = Optional.ofNullable(sellerPromotion.getTotalAmount()).orElse(BigDecimal.ZERO);
        // 获取当前推广状态
        Integer promStatus = sellerPromotion.getPromStatus();
        // 如果当前推广为已结束状态，判断当前修改是否改变推广状态和推广金额
        if (promStatus == PromotionGoodsType.FINISHEDABNORMAL.getCode() || promStatus == PromotionGoodsType.FINISHEDNORMAL.getCode()) {
            // 如果改变推广状态
            if (!goodsCelebrity.getCelebrityPromStatus().equals(celebrityPromStatus)) {
                return Result.error("当前推广已结束，网红推广状态不可修改！");
            }
            // 如果改变推广金额
            if (!promPriceOld.equals(promPriceNow)) {
                return Result.error("当前推广已结束，网红推广费不可修改！");
            }
        }
        // 查询paypal费率和固定费
        StoreCountry storeCountry = storeCountryService.getCountryByCode(CommonConstant.PAYPAL_CODE);
        BigDecimal paypalRate = storeCountry.getPaypalRate();
        BigDecimal paypalBaseFees = storeCountry.getPaypalBaseFees();

        // 获取网红费用
        BigDecimal celebrityPriceNow = storePromotionGoodsCelebrityModel.getCelebrityPrice();

        // 获取当前网红paypal费
        BigDecimal payPalNew = BigDecimal.ZERO;
        // 判断网红状态，如果已经填写订单号修改状态为待上线
        if (oConvertUtils.isNotEmpty(storePromotionGoodsCelebrityModel.getGoodsWaybill()) && celebrityPromStatus == YesNoStatus.NO.getCode()) {
            storePromotionGoodsCelebrityModel.setCelebrityPromStatus(YesNoStatus.YES.getCode());
        }
        List<StorePromotionGoods> promotionGoodsListNew = new ArrayList<>();
        StoreSellerPromotion storeSellerPromotion = new StoreSellerPromotion();
        storeSellerPromotion.setId(promId);

        // 获取需要修改的网红状态
        Integer celebrityStatus = Optional.ofNullable(storePromotionGoodsCelebrityModel.getCelebrityStatus()).orElse(YesNoStatus.NO.getCode());
        // 判断当前网红是否已选中
        if (celebrityStatus == YesNoStatus.YES.getCode()) {
            if (celebrityPromStatus == YesNoStatus.Exception.getCode()) {
                payPalNew = BigDecimal.ZERO;
                promPriceNow = BigDecimal.ZERO;
            }
            // 获取其余网红费用
            JSONObject jsonObject = goodsCelebrityService.getCelebrityTotalAmountNew(promId, celebrityPriceNow, payPalNew, promPriceNow, goodsCelebrityId);
            // 其余网红总paypal费用
            BigDecimal allCelebrityPaypalFees = jsonObject.getBigDecimal(CommonConstant.ALL_CELEBRITY_PAYPAL_FEES);
            // 其余网红推广费
            BigDecimal allCelebrityPromPrice = jsonObject.getBigDecimal(CommonConstant.ALL_CELEBRITY_PROM_PRICE);
            BigDecimal allTaxAmount = jsonObject.getBigDecimal(CommonConstant.ALL_TAX_AMOUNT);
            BigDecimal allAmount = allCelebrityPaypalFees.add(allCelebrityPromPrice).add(allTaxAmount);
            BigDecimal paidAmount = paidAmountOld.divide(exchangeRate, 2, RoundingMode.HALF_UP);
            // 判断修改后推广总金额是否小于已付款金额
            if (allAmount.compareTo(paidAmount) < 0) {
                return Result.error("修改失败，修改后推广总金额小于已付款金额！");
            }
            // 获取当前推广下所有的产品
            List<StorePromotionGoods> promotionGoodsList = storePromotionGoodsService.getPromGoodsList(promId);
            // 获取所有产品id，获取产品下的所有网红
            List<String> promGoodsIds = promotionGoodsList.stream().map(StorePromotionGoods::getId).distinct().collect(Collectors.toList());
            String celId = null;
            if (celebrityPromStatus == YesNoStatus.FINISH.getCode() || celebrityPromStatus == YesNoStatus.Exception.getCode()) {
                celId = goodsCelebrityId;
            }
            Map<String, List<StorePromotionGoodsCelebrity>> map = goodsCelebrityService.getGoodsCelebrityListNew(promGoodsIds, celId, celebrityPromStatus, YesNoStatus.YES.getCode(), YesNoStatus.NO.getCode());
            // 判断产品状态
            JSONObject json = goodsCelebrityService.checkGoodsStatusNew(promotionGoodsList, map);
            promotionGoodsListNew = json.getJSONArray(CommonConstant.PROMOTION_GOODS_LIST_NEW).toJavaList(StorePromotionGoods.class);
            BigDecimal totalAmountNew = json.getBigDecimal(CommonConstant.TOTAL_AMOUNT_NEW);
            // 判断推广状态
            storeSellerPromotion = storeSellerPromotionService.checkPromStatusTypeNew(promId, promotionGoodsListNew, json, promGoodsIds, null);
            Integer promStatusNew = storeSellerPromotion.getPromStatus();

            // 判断当前网红总推广费,如果总推广费为0，则无需判断支付状态
            if (allAmount.compareTo(BigDecimal.ZERO) != 0) {
                // 判断推广状态
                if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
                    boolean finishStatus = storeSellerPromotionService.checkFinishStatus(storeSellerPromotion);
                    if (!finishStatus) {
                        return Result.error("推广需求未付清，请先付清再结束网红推广");
                    }
                }
            }

            boolean flagCheckCelebrity = false;
            // 判断当前网红是否已选中,未选中则判断是否修改网红状态
            if (goodsCelebrity.getCelebrityStatus() == YesNoStatus.NO.getCode()) {
                // 修改网红状态，则设置当前网红为选中状态
                if (!goodsCelebrity.getCelebrityPromStatus().equals(celebrityPromStatus)) {
                    // 选中当前网红
                    storePromotionGoodsCelebrityModel.setCelebrityStatus(YesNoStatus.YES.getCode());
                    flagCheckCelebrity = true;
                }
            } else {
                flagCheckCelebrity = true;
            }
            // 判断当前网红状态是否是已终止
            // 已终止则扣除推广费，paypal费，计算扣除后推广费与当前已付金额是否一致
            if (celebrityPromStatus == YesNoStatus.Exception.getCode()) {
                storeSellerPromotion = goodsCelebrityService.updateAmount(promId, exchangeRate, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, goodsCelebrityId);
//                storePromotionGoodsCelebrityModel.setPromPrice(BigDecimal.ZERO);
//                storePromotionGoodsCelebrityModel.setCelebrityPaypalFees(BigDecimal.ZERO);
            } else {
                BigDecimal promPrice = storePromotionGoodsCelebrityModel.getPromPrice();
                if (flagCheckCelebrity) {
                    BigDecimal celebrityPrice = goodsCelebrity.getCelebrityPrice();
                    storeSellerPromotion = goodsCelebrityService.updateAmount(promId, exchangeRate, celebrityPrice, storePromotionGoodsCelebrityModel.getTaxAmount(), promPrice, goodsCelebrityId);
                } else {
                    storeSellerPromotion = goodsCelebrityService.updateAmount(promId, exchangeRate, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, goodsCelebrityId);
                }
            }
            // 判断当前已付金额和总推广金额是否一致
            // 当前订单总额 人民币
            BigDecimal totalAmountOld = storeSellerPromotion.getTotalAmount();
            // 判断当前已付金额与订单总额想等，修改推广状态为已结束，修改支付状态为已付全款，修改审核状态为匹配成功
            if (paidAmount.compareTo(totalAmountOld) == 0) {
                storeSellerPromotion.setPromStatus(PromotionGoodsType.FINISHEDABNORMAL.getCode());
                if (totalAmountOld.compareTo(BigDecimal.ZERO) == 0) {
                    storeSellerPromotion.setPayStatus(PayStatusEnum.DOWITHOUTPAY.getCode());
                } else {
                    storeSellerPromotion.setPayStatus(PayStatusEnum.PAIDINFULL.getCode());
                }
                storeSellerPromotion.setApprovedStatus(ApprovedStatusType.MATCH_SUCCESSFULLY.getCode());
            } else {
                if (paidAmount.compareTo(totalAmountOld) > 0) {
                    return Result.error("当前已付金额大于推广金额，操作失败！");
                }
                if (paidAmount.compareTo(BigDecimal.ZERO) > 0 && paidAmount.compareTo(totalAmountOld) < 0) {
                    storeSellerPromotion.setPayStatus(PayStatusEnum.DOWNPAYMENT.getCode());
                }
                if (paidAmount.compareTo(BigDecimal.ZERO) == 0) {
                    storeSellerPromotion.setPayStatus(PayStatusEnum.TOBEPAID.getCode());
                }
            }
            storeSellerPromotion.setPromStatus(promStatusNew);
        }
        // 修改网红信息
        goodsCelebrityService.updateById(storePromotionGoodsCelebrityModel);

        // 修改网红粉丝数、平均点赞数、平均互动率、平均观看量、总视频数、总点赞量
        StoreCelebrityPrivate storeCelebrityPrivate = new StoreCelebrityPrivate();
        storeCelebrityPrivate.setId(storePromotionGoodsCelebrityModel.getCelebrityPrivateId());
        storeCelebrityPrivate.setFollowersNum(storePromotionGoodsCelebrityModel.getFollowersNum());
        storeCelebrityPrivate.setLikeAvgNum(storePromotionGoodsCelebrityModel.getLikeAvgNum());
        storeCelebrityPrivate.setInteractAvgNum(storePromotionGoodsCelebrityModel.getInteractAvgNum());
        storeCelebrityPrivate.setPlayAvgNum(BigInteger.valueOf(storePromotionGoodsCelebrityModel.getPlayAvgNum()));
        storeCelebrityPrivate.setVideoCount(storePromotionGoodsCelebrityModel.getVideoCount());
        storeCelebrityPrivate.setLikeCount(storePromotionGoodsCelebrityModel.getLikeCount());
        goodsCelebrityService.updateGoodsCelebrityById(promotionGoodsListNew, storeSellerPromotion);
        return Result.ok("编辑成功!");
    }

    /**
     * 功能描述:商家推广产品网红编辑
     *
     * @param goodsCelebrityModel
     * @return org.jeecg.common.api.vo.Result<?>
     * @Date 2021-08-18 16:31:02
     */
    @AutoLog(value = "商家推广产品网红关联表-编辑")
@Operation(summary = "商家推广产品网红关联表-编辑", description = "商家推广产品网红关联表-编辑")
    @PutMapping(value = "/editGoodsCelebrity")
    public Result<?> editGoodsCelebrity(@RequestBody StorePromotionGoodsCelebrityModel goodsCelebrityModel) {
        // 当前推广网红id
        String goodsCelebrityId = goodsCelebrityModel.getId();
        // 需要修改的状态
        Integer celebrityPromStatus = goodsCelebrityModel.getCelebrityPromStatus();
        // 获取当前产品网红
        StorePromotionGoodsCelebrity goodsCelebrity = goodsCelebrityService.getById(goodsCelebrityId);
        StorePromotionGoodsCelebrity goodsCelebrityNew = new StorePromotionGoodsCelebrity();
        goodsCelebrityNew.setId(goodsCelebrityId);
        goodsCelebrityNew.setCelebrityPromStatus(celebrityPromStatus);
        if (oConvertUtils.isNotEmpty(goodsCelebrityModel.getMediaUploads())) {
            goodsCelebrityNew.setMediaUploads(goodsCelebrityModel.getMediaUploads());
        }
        if (oConvertUtils.isNotEmpty(goodsCelebrityModel.getOnlineTime())) {
            goodsCelebrityNew.setOnlineTime(goodsCelebrityModel.getOnlineTime());
        }

        String promId = goodsCelebrity.getPromId();
        // 获取当前推广信息
        StoreSellerPromotion sellerPromotion = storeSellerPromotionService.getById(promId);
        BigDecimal exchangeRate = sellerPromotion.getExchangeRate();
        // 已付总金额
        BigDecimal paidAmount = storeSellerPromotionService.getAmount(sellerPromotion);
        // 获取当前推广状态
        Integer promStatus = sellerPromotion.getPromStatus();
        // 如果当前推广为已结束状态，判断当前修改是否改变推广状态和推广金额
        if (promStatus == PromotionGoodsType.FINISHEDABNORMAL.getCode() || promStatus == PromotionGoodsType.FINISHEDNORMAL.getCode()) {
            return Result.error("修改失败，当前推广已结束！");
        }
        if (goodsCelebrity.getCelebrityStatus() != 1) {
            return Result.error("修改失败，当前网红未选中！");
        }
        // 获取当前推广所有网红
        List<StorePromotionGoodsCelebrity> goodsCelebrityList = goodsCelebrityService.getByPromId(promId);

        goodsCelebrityList.stream().filter(x -> x.getId().equals(goodsCelebrityId)).findFirst().get().setCelebrityPromStatus(celebrityPromStatus);
        StoreSellerPromotion sellerPromotionNew = new StoreSellerPromotion();
        sellerPromotionNew.setId(promId);
        BigDecimal totalAmt = BigDecimal.ZERO;
        // 正常结束
        if (celebrityPromStatus == YesNoStatus.FINISH.getCode()) {
            // 判断当前已付金额是否与订单总金额一致
            // 获取总推广费
            totalAmt = Optional.ofNullable(sellerPromotion.getTotalAmount()).orElse(BigDecimal.ZERO);
            if (paidAmount.compareTo(totalAmt) == 0 && totalAmt.compareTo(BigDecimal.ZERO) > 0) {
                // 一致则修改推广付款状态为已付全款
                sellerPromotionNew.setPayStatus(PayStatusEnum.PAIDINFULL.getCode());
            } else {
                if (paidAmount.compareTo(BigDecimal.ZERO) == 0) {
                    // 修改为待支付
                    sellerPromotionNew.setPayStatus(PayStatusEnum.TOBEPAID.getCode());
                } else {
                    // 修改为部分支付
                    sellerPromotionNew.setPayStatus(PayStatusEnum.DOWNPAYMENT.getCode());
                }
            }
        }
        // 终止
        if (celebrityPromStatus == YesNoStatus.Exception.getCode()) {
            // 重新计算推广总金额
            // 获取其余网红费用
            JSONObject promotionAmt = getPromotionAmt(goodsCelebrityList, goodsCelebrityId);
            BigDecimal promAmt = promotionAmt.getBigDecimal(CelebrityPromStatus.PRO_MAMT);
            BigDecimal celPrice = promotionAmt.getBigDecimal(CelebrityPromStatus.CEL_PRICE);
            BigDecimal taxAmt = promotionAmt.getBigDecimal(CelebrityPromStatus.TAX_AMT);
            totalAmt = promAmt.add(taxAmt);
            BigDecimal totalRmbAmt = totalAmt.multiply(exchangeRate);

            // 判断扣除当前网红金额后，已付总金额是否超出推广总金额
            if (totalAmt.compareTo(BigDecimal.ZERO) > 0 && totalAmt.compareTo(paidAmount) < 0) {
                return Result.error("修改失败，修改后推广总金额小于已付款金额,请退款后再操作！");
            }
            sellerPromotionNew.setPromAmount(promAmt);
            sellerPromotionNew.setCelebrityTotalAmount(celPrice);
            sellerPromotionNew.setTotalTaxAmount(taxAmt);
            sellerPromotionNew.setTotalAmount(totalAmt);
            sellerPromotionNew.setTotalRmbAmount(totalRmbAmt);

        }
        if (paidAmount.compareTo(BigDecimal.ZERO) > 0 && paidAmount.compareTo(totalAmt) == 0 && totalAmt.compareTo(BigDecimal.ZERO) > 0) {
            // 一致则修改推广付款状态为已付全款
            sellerPromotionNew.setPayStatus(PayStatusEnum.PAIDINFULL.getCode());
        }
        // 判断产品状态，网红状态，推广状态
        List<StorePromotionGoods> promotionGoodsListNew = new ArrayList<>();
        checkPromotionStatus(sellerPromotionNew, goodsCelebrityList, promotionGoodsListNew, paidAmount, totalAmt);

        // 修改推广、产品、网红信息
        goodsCelebrityService.editGoodsCelebrity(goodsCelebrityNew, promotionGoodsListNew, sellerPromotionNew);

        return Result.ok("编辑成功!");
    }

    /**
     * 功能描述:判断推广状态
     *
     * @return void
     * @Date 2023-11-24 08:31:54
     */
    private void checkPromotionStatus(StoreSellerPromotion sellerPromotionNew,
                                      List<StorePromotionGoodsCelebrity> goodsCelebrityList,
                                      List<StorePromotionGoods> promotionGoodsListNew,
                                      BigDecimal paidAmount, BigDecimal totalAmt) {
        String promId = sellerPromotionNew.getId();
        List<StorePromotionGoods> promGoodsList = storePromotionGoodsService.getPromGoodsList(promId);

        Map<String, List<StorePromotionGoodsCelebrity>> goodsCelebrityMap = goodsCelebrityList.stream().collect(Collectors.groupingBy(StorePromotionGoodsCelebrity::getGoodsId));
        for (Map.Entry<String, List<StorePromotionGoodsCelebrity>> entry : goodsCelebrityMap.entrySet()) {
            String goodsId = entry.getKey();
            StorePromotionGoods promotionGoodsNew = new StorePromotionGoods();
            promotionGoodsNew.setId(goodsId);
            List<StorePromotionGoodsCelebrity> goodsCelebrities = entry.getValue();
            if (goodsCelebrities.isEmpty()) {
                continue;
            }
            // 判断网红状态是否都已完成
            Optional<StorePromotionGoodsCelebrity> optional = goodsCelebrities.stream()
                    .filter(x -> !x.getCelebrityPromStatus().equals(PROM_STATUS_3)
                            && !x.getCelebrityPromStatus().equals(PROM_STATUS_FINISH))
                    .findFirst();
            if (optional.isPresent()) {
                // 存在未完成网红，修改产品状态为进行中
                promotionGoodsNew.setGoodsStatus(PromotionGoodsType.UNDERWAY.getCode());
            } else {
                // 判断是否存在异常结束网红
                Optional<StorePromotionGoodsCelebrity> celebrity = goodsCelebrities.stream()
                        .filter(x -> x.getCelebrityPromStatus().equals(PROM_STATUS_FINISH))
                        .findFirst();
                if (celebrity.isPresent()) {
                    // 存在未完成网红，修改产品状态为异常结束
                    promotionGoodsNew.setGoodsStatus(PromotionGoodsType.FINISHEDABNORMAL.getCode());
                } else {
                    // 存在未完成网红，修改产品状态为正常结束
                    promotionGoodsNew.setGoodsStatus(PromotionGoodsType.FINISHEDNORMAL.getCode());
                }
            }
            promotionGoodsListNew.add(promotionGoodsNew);
        }
        if (promotionGoodsListNew.size() != promGoodsList.size()) {
            List<String> goodsIds = promotionGoodsListNew.stream().map(StorePromotionGoods::getId).collect(Collectors.toList());
            List<StorePromotionGoods> promotionGoods = promGoodsList.stream().filter(x -> !goodsIds.contains(x.getId())).collect(Collectors.toList());
            promotionGoodsListNew.addAll(promotionGoods);
        }
        // 判断产品状态
        Optional<StorePromotionGoods> promotionGoods = promotionGoodsListNew.stream()
                .filter(x -> !x.getGoodsStatus().equals(PromotionGoodsType.FINISHEDABNORMAL.getCode())
                        && !x.getGoodsStatus().equals(PromotionGoodsType.FINISHEDNORMAL.getCode()))
                .findFirst();
        if (promotionGoods.isPresent()) {
            // 存在未完成产品，修改推广状态为进行中
            sellerPromotionNew.setPromStatus(PromotionGoodsType.UNDERWAY.getCode());
        } else {
            if (paidAmount.compareTo(totalAmt) == 0 && totalAmt.compareTo(BigDecimal.ZERO) > 0 && paidAmount.compareTo(BigDecimal.ZERO) > 0) {
                // 判断是否存在异常结束产品
                Optional<StorePromotionGoods> promotionGoodsOptional = promotionGoodsListNew.stream()
                        .filter(x -> x.getGoodsStatus().equals(PromotionGoodsType.FINISHEDABNORMAL.getCode()))
                        .findFirst();
                if (promotionGoodsOptional.isPresent()) {
                    // 存在未完成产品，修改推广状态为异常结束
                    sellerPromotionNew.setPromStatus(PromotionGoodsType.FINISHEDABNORMAL.getCode());
                } else {
                    // 存在未完成产品，修改推广状态为正常结束
                    sellerPromotionNew.setPromStatus(PromotionGoodsType.FINISHEDNORMAL.getCode());
                }
            } else {
                // 未付完全款，修改推广状态为进行中
                sellerPromotionNew.setPromStatus(PromotionGoodsType.UNDERWAY.getCode());
            }
        }
    }

    /**
     * 匹配网红
     *
     * @param storePromotionGoodsCelebrity
     * @return
     */
    @AutoLog(value = "商家推广产品网红关联表-匹配网红")
@Operation(summary = "商家推广产品网红关联表-匹配网红", description = "商家推广产品网红关联表-匹配网红")
    @PostMapping(value = "/matchCelebrity")
    public Result<?> matchCelebrity(@RequestBody StorePromotionGoodsCelebrity storePromotionGoodsCelebrity) {
        // 更改审核状态
        LambdaQueryWrapper<StoreSellerPromotion> promotionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        promotionLambdaQueryWrapper.eq(StoreSellerPromotion::getId, storePromotionGoodsCelebrity.getPromId());
        // 修改当前需求审核状态和推广状态
        StoreSellerPromotion storeSellerPromotion = storeSellerPromotionService.list(promotionLambdaQueryWrapper).get(0);
        // 判断需求为待审核或审核成功时修改状态，其余不修改
        /**    if (storeSellerPromotion.getApprovedStatus() == ApprovedStatusType.PENDING_REVIEW.getCode() || storeSellerPromotion.getApprovedStatus() == ApprovedStatusType.EXAMINATION_PASSED.getCode()) {
         //修改推广状态为已提供方案
         storeSellerPromotion.setApprovedStatus(ApprovedStatusType.PROVIDE_PLAN.getCode());
         }*/
        storeSellerPromotion.setApprovedUserId(getUserIdByToken());
        storeSellerPromotion.setApprovedDate(new Date());
        storeSellerPromotion.setPromStatus(PromotionGoodsType.UNDERWAY.getCode());
        // 设置产品网红属性
        storePromotionGoodsCelebrity.setSellerId(storeSellerPromotion.getSellerId());
        storePromotionGoodsCelebrity.setSellerName(storeSellerPromotion.getSellerName());
        storePromotionGoodsCelebrity.setExtensionCode(storeSellerPromotion.getExtensionCode());
        String celebrityPrivateIds = storePromotionGoodsCelebrity.getCelebrityPrivateId();
        // 获取当前匹配的网红id
        List<String> celebrityIdList = Arrays.asList(celebrityPrivateIds.split(","));
        List<String> list = celebrityIdList.stream().distinct().collect(Collectors.toList());
        if (celebrityIdList.size() != list.size()) {
            return Result.error("网红数据重复，请重新选择");
        }
        // 获取已经匹配的产品网红id
        List<StorePromotionGoodsCelebrity> storePromotionGoodsCelebrityExists = goodsCelebrityService.goodsCelebrityList(storePromotionGoodsCelebrity.getGoodsId(), celebrityIdList, YesNoStatus.NO.getCode());
        // 获取产品
        StorePromotionGoods storePromotionGoods = storePromotionGoodsService.getById(storePromotionGoodsCelebrity.getGoodsId());
        // 构建产品网红列表
        List<StorePromotionGoodsCelebrity> goodsCelebrityListNew;
        // 第一次添加匹配网红
        if (storePromotionGoodsCelebrityExists.isEmpty()) {
            // 更改产品状态为进行中
            storePromotionGoods.setGoodsStatus(PromotionGoodsType.UNDERWAY.getCode());
//            构架网红列表
            goodsCelebrityListNew = createGoodsCelebrity(celebrityIdList, storePromotionGoodsCelebrity);
        } else {
            // 已存在网红id，过滤出新添加的网红
            List<String> savePrivateIdList = storePromotionGoodsCelebrityExists.stream().map(StorePromotionGoodsCelebrity::getCelebrityPrivateId).distinct().collect(Collectors.toList());
            List<String> celebrityIdListNew = new ArrayList<>();
            for (String celebrityId : celebrityIdList) {
                if (!savePrivateIdList.contains(celebrityId)) {
                    celebrityIdListNew.add(celebrityId);
                }
            }
            // 构建新的网红列表
            goodsCelebrityListNew = createGoodsCelebrity(celebrityIdListNew, storePromotionGoodsCelebrity);
            // 批量匹配网红更改产品状态为进行中
            if (!goodsCelebrityListNew.isEmpty()) {
                storePromotionGoods.setGoodsStatus(PromotionGoodsType.UNDERWAY.getCode());
            }
        }
        // 保存或修改
        goodsCelebrityService.updateOrSave(storeSellerPromotion, storePromotionGoods, goodsCelebrityListNew);
        return Result.ok("添加成功！");
    }


    /**
     * 删除匹配网红,应付金额改变
     *
     * @param storePromotionGoodsCelebrity
     * @return
     * @Author: zhoushen
     */
    @AutoLog(value = "网红销售明细-删除匹配网红,应付金额改变")
@Operation(summary = "网红销售明细-删除匹配网红,应付金额改变", description = "网红销售明细-删除匹配网红,应付金额改变")
    @ResponseBody
    @PutMapping(value = "/handleDelete")
    public Result<?> handleDelete(@RequestBody StorePromotionGoodsCelebrity storePromotionGoodsCelebrity) {
        return goodsCelebrityService.handleDelete(storePromotionGoodsCelebrity);
    }

    /**
     * 功能描述:批量删除匹配网红
     *
     * @param ids
     * @return org.jeecg.common.api.vo.Result<?>
     * @Date 2021-06-29 15:02:35
     */
    @AutoLog(value = "推广匹配，批量删除网红")
    @ResponseBody
    @DeleteMapping(value = "/delCelebrityBatch")
    public Result<?> delCelebrityBatch(@RequestParam(name = "ids", required = true) String ids) {
        goodsCelebrityService.delCelebrityBatch(ids);
        return Result.ok("删除成功！");
    }

    /**
     * 功能描述:批量删除匹配网红
     *
     * @param ids
     * @return org.jeecg.common.api.vo.Result<?>
     * @Date 2021-06-29 15:02:35
     */
    @AutoLog(value = "推广匹配，批量删除网红")
    @ResponseBody
    @DeleteMapping(value = "/removeCelebrityBatch")
    public Result<?> removeCelebrityBatch(@RequestParam(name = "ids", required = true) String ids) {
        // 需要删除的网红id
        List<String> removeIdList = Arrays.asList(ids.split(","));
        // 获取需要删除的网红
        List<StorePromotionGoodsCelebrity> goodsCelebrityList = goodsCelebrityService.getCelebrityList(removeIdList);
        boolean flag = goodsCelebrityList.stream().anyMatch(x -> x.getCelebrityStatus() == YesNoStatus.YES.getCode());
        if (flag) {
            return Result.error("删除失败，当前删除网红中包含已选中网红！");
        }

        String promId = goodsCelebrityList.get(0).getPromId();
        BigDecimal celebrityPriceAll = BigDecimal.ZERO;
        // 获取当前需要删除的网红已付金额
        for (StorePromotionGoodsCelebrity storePromotionGoodsCelebrity : goodsCelebrityList) {
            if (storePromotionGoodsCelebrity.getCelebrityStatus() == YesNoStatus.YES.getCode()) {
                BigDecimal celebrityPayAmount = Optional.ofNullable(storePromotionGoodsCelebrity.getCelebrityPayAmount()).orElse(BigDecimal.ZERO);
                if (celebrityPayAmount.compareTo(BigDecimal.ZERO) > 0) {
                    return Result.error("删除失败！当前删除网红中，存在已支付网红金额！");
                }
                BigDecimal getCelebrityPrice = Optional.ofNullable(storePromotionGoodsCelebrity.getCelebrityPrice()).orElse(BigDecimal.ZERO);
                // 获取需要删除的网红金额
                celebrityPriceAll = celebrityPriceAll.add(getCelebrityPrice);
            }
        }

        // 获取当前推广
        StoreSellerPromotion sellerPromotion = storeSellerPromotionService.getById(promId);
        // 获取网红已付总金额
        BigDecimal celebrityPayTotalAmount = Optional.ofNullable(sellerPromotion.getCelebrityPayTotalAmount()).orElse(BigDecimal.ZERO);
        // 获取网红费用总金额
        BigDecimal celebrityTotalAmount = Optional.ofNullable(sellerPromotion.getCelebrityTotalAmount()).orElse(BigDecimal.ZERO);
        // 根据推广id，获取推广下所有产品
        List<StorePromotionGoods> promotionGoodsList = storePromotionGoodsService.getPromGoodsList(promId);
        // 获取所有产品id，获取产品下的所有网红
        List<String> promGoodsIds = promotionGoodsList.stream().map(StorePromotionGoods::getId).collect(Collectors.toList());
        Map<String, List<StorePromotionGoodsCelebrity>> map = goodsCelebrityService.getGoodsCelebrityList(promGoodsIds, null, null);
        // 判断网红状态
        JSONObject jsonObject = goodsCelebrityService.checkCelebrityStatus(goodsCelebrityList, promotionGoodsList, map);
        List<StorePromotionGoods> promotionGoodsListNew = jsonObject.getJSONArray(CommonConstant.PROMOTION_GOODS_LIST_NEW).toJavaList(StorePromotionGoods.class);
        // 判断推广状态
        StoreSellerPromotion storeSellerPromotion = storeSellerPromotionService.checkPromStatus(promId, promotionGoodsListNew, jsonObject, promGoodsIds);
        boolean finishStatus = storeSellerPromotionService.checkFinishStatus(storeSellerPromotion);
        if (!finishStatus) {
            return Result.error("推广需求未付清，请先付清再结束网红推广");
        }
        // 设置删除后的网红费用和网红已付金额
        storeSellerPromotion.setCelebrityPayTotalAmount(celebrityPayTotalAmount);
        storeSellerPromotion.setCelebrityTotalAmount(celebrityTotalAmount.subtract(celebrityPriceAll));
        // 处理结果
        goodsCelebrityService.updateData(removeIdList, promotionGoodsListNew, storeSellerPromotion);
        return Result.ok("删除成功！");
    }

    private List<StorePromotionGoodsCelebrity> createGoodsCelebrity(List<String> celebrityIdList, StorePromotionGoodsCelebrity storePromotionGoodsCelebrity) {
        List<StorePromotionGoodsCelebrity> goodsCelebrityList = new ArrayList<>();
        for (String celebrityId : celebrityIdList) {
            StoreCelebrityPrivate storeCelebrityPrivate = privateService.getById(celebrityId);
            StorePromotionGoodsCelebrity storePromotionGoodsCelebrityNew = new StorePromotionGoodsCelebrity();
            storePromotionGoodsCelebrityNew.setSellerId(storePromotionGoodsCelebrity.getSellerId());
            storePromotionGoodsCelebrityNew.setSellerName(storePromotionGoodsCelebrity.getSellerName());
            storePromotionGoodsCelebrityNew.setExtensionCode(storePromotionGoodsCelebrity.getExtensionCode());
            storePromotionGoodsCelebrityNew.setPromId(storePromotionGoodsCelebrity.getPromId());
            storePromotionGoodsCelebrityNew.setGoodsId(storePromotionGoodsCelebrity.getGoodsId());
            storePromotionGoodsCelebrityNew.setCelebrityPrivateId(celebrityId);
            storePromotionGoodsCelebrityNew.setPromPrice(storeCelebrityPrivate.getPromPrice());
            storePromotionGoodsCelebrityNew.setVideoTags(storeCelebrityPrivate.getVideoTags());
            storePromotionGoodsCelebrityNew.setPromLikeTags(storeCelebrityPrivate.getPromLikeTags());
            // 设置网红费用为签约费用
            storePromotionGoodsCelebrityNew.setCelebrityPrice(storeCelebrityPrivate.getContractAmount());
            // 网红送货地址
            storePromotionGoodsCelebrityNew.setFullAddress(storeCelebrityPrivate.getFullAddress());
            storePromotionGoodsCelebrityNew.setCelebrityPromStatus(storePromotionGoodsCelebrity.getCelebrityPromStatus());
            // 网红负责人
            storePromotionGoodsCelebrityNew.setCelebrityCounselorId(storeCelebrityPrivate.getCelebrityCounselorId());
            storePromotionGoodsCelebrityNew.setCelebrityCounselorName(storeCelebrityPrivate.getCelebrityCounselorName());
            goodsCelebrityList.add(storePromotionGoodsCelebrityNew);
        }
        return goodsCelebrityList;
    }

    /**
     * 选中网红
     *
     * @param id
     * @return
     */
    @AutoLog(value = "选中网红")
@Operation(summary = "选中网红", description = "选中网红")
    @GetMapping(value = "/checkStatus")
    public Result<?> checkStatus(@RequestParam(name = "id", required = true) String id,
                                 @RequestParam(name = "status", required = true) Integer status) {
        StorePromotionGoodsCelebrity storePromotionGoodsCelebrity = goodsCelebrityService.getById(id);
        String promId = storePromotionGoodsCelebrity.getPromId();
        BigDecimal celebrityPrice = storePromotionGoodsCelebrity.getCelebrityPrice();
        StoreSellerPromotion storeSellerPromotion = new StoreSellerPromotion();
        storeSellerPromotion.setId(promId);
        // 存储当前推广网红费用
        BigDecimal celebrityTotalAmount;
        // 已选中
        if (status == YesNoStatus.YES.getCode()) {
            // 获取其余网红费用
            celebrityTotalAmount = goodsCelebrityService.getCelebrityTotalAmount(promId, celebrityPrice, id);
        } else {
            // 取消选中，设置网红金额为其余网红费用
            celebrityTotalAmount = goodsCelebrityService.getCelebrityTotalAmount(promId, BigDecimal.ZERO, id);
        }
        BigDecimal celebrityTotalAmountNow = oConvertUtils.isNotEmpty(celebrityTotalAmount) ? celebrityTotalAmount : BigDecimal.ZERO;
        // 设置推广网红费用
        storeSellerPromotion.setCelebrityTotalAmount(celebrityTotalAmountNow);
        // 修改产品网红选中状态
        StorePromotionGoodsCelebrity promotionGoodsCelebrity = new StorePromotionGoodsCelebrity();
        promotionGoodsCelebrity.setId(id);
        promotionGoodsCelebrity.setCelebrityStatus(status);
        goodsCelebrityService.updateGoodsCelebrity(storeSellerPromotion, promotionGoodsCelebrity);
        return Result.ok("修改成功！");
    }

    /**
     * 功能描述:选中网红
     *
     * @param id
     * @param status
     * @return org.jeecg.common.api.vo.Result<?>
     * @Date 2022-04-19 16:03:15
     */
    @AutoLog(value = "选中网红")
@Operation(summary = "选中网红", description = "选中网红")
    @GetMapping(value = "/checkCelebrityStatusOld")
    public Result<?> checkCelebrityStatusOld(@RequestParam(name = "id", required = true) String id,
                                             @RequestParam(name = "status", required = true) Integer status) {

        if (oConvertUtils.isEmpty(id)) {
            return Result.error("请匹配网红后操作！");
        }

        // 查询产品网红信息
        StorePromotionGoodsCelebrity storePromotionGoodsCelebrity = goodsCelebrityService.getById(id);
        BigDecimal promPrice = Optional.ofNullable(storePromotionGoodsCelebrity.getPromPrice()).orElse(BigDecimal.ZERO);
        if (promPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error("请先设置当前网红推广费");
        }
        String promId = storePromotionGoodsCelebrity.getPromId();
        BigDecimal celebrityPrice = Optional.ofNullable(storePromotionGoodsCelebrity.getCelebrityPrice()).orElse(BigDecimal.ZERO);
        StoreSellerPromotion sellerPromotion = storeSellerPromotionService.getById(promId);

        // 判断当前推广状态
        Integer promStatus = sellerPromotion.getPromStatus();
        if (promStatus == PromotionGoodsType.FINISHEDNORMAL.getCode() || promStatus == PromotionGoodsType.FINISHEDABNORMAL.getCode()) {
            // 当前推广状态为结束，不允许选中网红
            return Result.error("当前推广已结束，不允许选中或取消网红");
        }
        // 查询paypa费率和固定费
        StoreCountry storeCountry = storeCountryService.getCountryByCode(CommonConstant.PAYPAL_CODE);
        BigDecimal paypalRate = storeCountry.getPaypalRate();
        BigDecimal paypalBaseFees = storeCountry.getPaypalBaseFees();

        // 获取汇率
        BigDecimal exchangeRate = sellerPromotion.getExchangeRate();

        // 获取当前网红paypal费
        BigDecimal payPalNew = goodsCelebrityService.getPayPalNew(promPrice, paypalRate, paypalBaseFees);

        // 构建推广实体
        StoreSellerPromotion storeSellerPromotion;

        // 修改产品网红选中状态
        StorePromotionGoodsCelebrity promotionGoodsCelebrity = new StorePromotionGoodsCelebrity();
        promotionGoodsCelebrity.setId(id);
        promotionGoodsCelebrity.setCelebrityStatus(status);

        // 已选中
        if (status == YesNoStatus.YES.getCode()) {
            if (sellerPromotion.getIsIncludePaypalFees() == YesNoStatus.NO.getCode()) {
                payPalNew = BigDecimal.ZERO;
            }
            storeSellerPromotion = goodsCelebrityService.updateAmount(promId, exchangeRate, celebrityPrice, payPalNew, promPrice, id);
            // 获取其余网红费用
            promotionGoodsCelebrity.setCelebrityPaypalFees(payPalNew);
        } else {
            // 取消选中，设置网红金额为其余网红费用
            storeSellerPromotion = goodsCelebrityService.updateAmount(promId, exchangeRate, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, id);
            promotionGoodsCelebrity.setCelebrityPromStatus(YesNoStatus.NO.getCode());
        }
        // 判断当前已付金额是否超过总推广费
        BigDecimal totalRmbAmountNow = Optional.ofNullable(storeSellerPromotion.getTotalRmbAmount()).orElse(BigDecimal.ZERO);
        BigDecimal intentionAmount = Optional.ofNullable(sellerPromotion.getIntentionAmount()).orElse(BigDecimal.ZERO);
        BigDecimal paymentAmount = Optional.ofNullable(sellerPromotion.getPaymentAmount()).orElse(BigDecimal.ZERO);
        BigDecimal refundAmount = Optional.ofNullable(sellerPromotion.getRefundAmount()).orElse(BigDecimal.ZERO);
        BigDecimal paidAmount = paymentAmount.add(intentionAmount).subtract(refundAmount);
        // 判断实付金额和当前订单总额是否一致
        int flagNum = paidAmount.compareTo(totalRmbAmountNow);
        if (flagNum > 0) {
            return Result.error("当前已付金额大于订单总金额，请退款后再操作");
        }
        if (flagNum < 0 || paymentAmount.subtract(refundAmount).compareTo(BigDecimal.ZERO) > 0) {
            storeSellerPromotion.setPayStatus(PayStatusEnum.DOWNPAYMENT.getCode());
        }
        if (flagNum == 0 && totalRmbAmountNow.compareTo(BigDecimal.ZERO) > 0) {
            storeSellerPromotion.setPayStatus(PayStatusEnum.PAIDINFULL.getCode());
        }
        if (paymentAmount.subtract(refundAmount).compareTo(BigDecimal.ZERO) == 0) {
            storeSellerPromotion.setPayStatus(PayStatusEnum.TOBEPAID.getCode());
        }
        boolean isIncludePaypalFees = sellerPromotion.getIsIncludePaypalFees() == YesNoStatus.YES.getCode();

        // 判断是否包含paypal
        if (!isIncludePaypalFees) {
            BigDecimal paypalFeesAmount = storeSellerPromotion.getPaypalFeesAmount();
            BigDecimal allPromPrice = storeSellerPromotion.getTotalAmount().subtract(paypalFeesAmount);
            storeSellerPromotion.setTotalAmount(allPromPrice);
            storeSellerPromotion.setTotalRmbAmount(allPromPrice.multiply(exchangeRate));
            storeSellerPromotion.setPaypalFeesAmount(BigDecimal.ZERO);
        }
        List<StorePromotionGoods> promotionGoodsListNew = new ArrayList<>();
        if (status == YesNoStatus.NO.getCode()) {

            if (storeSellerPromotion.getPayStatus() == PayStatusEnum.PAIDINFULL.getCode()) {
                // 获取当前推广下所有的产品
                List<StorePromotionGoods> promotionGoodsList = storePromotionGoodsService.getPromGoodsList(promId);
                // 获取所有产品id，获取产品下的所有网红
                List<String> promGoodsIds = promotionGoodsList.stream().map(StorePromotionGoods::getId).distinct().collect(Collectors.toList());
                Map<String, List<StorePromotionGoodsCelebrity>> map = goodsCelebrityService.getGoodsCelebrityListNew(promGoodsIds, id, null, YesNoStatus.YES.getCode(), YesNoStatus.YES.getCode());
                // 判断产品状态
                JSONObject json = goodsCelebrityService.checkGoodsStatus(promotionGoodsList, map);
                promotionGoodsListNew = json.getJSONArray(CommonConstant.PROMOTION_GOODS_LIST_NEW).toJavaList(StorePromotionGoods.class);
                // 判断推广状态
                StoreSellerPromotion sellerPromotionOld = storeSellerPromotionService.checkPromStatusTypeNew(promId, promotionGoodsListNew, json, promGoodsIds, PayStatusEnum.PAIDINFULL.getCode());
                Integer promStatusNow = sellerPromotionOld.getPromStatus();
                storeSellerPromotion.setPromStatus(promStatusNow);
            }
        }

        goodsCelebrityService.updateGoodsCelebrityNew(storeSellerPromotion, promotionGoodsCelebrity, promotionGoodsListNew);
        return Result.ok("修改成功！");
    }

    /**
     * 功能描述:选中网红
     *
     * @param id
     * @param status
     * @return org.jeecg.common.api.vo.Result<?>
     * @Date 2022-04-19 16:03:15
     */
    @AutoLog(value = "选中网红")
@Operation(summary = "选中网红", description = "选中网红")
    @GetMapping(value = "/checkCelebrityStatus")
    public Result<?> checkCelebrityStatus(@RequestParam(name = "id", required = true) String id,
                                          @RequestParam(name = "status", required = true) Integer status) {

        if (oConvertUtils.isEmpty(id)) {
            return Result.error("请匹配网红后操作！");
        }

        // 查询产品网红信息
        StorePromotionGoodsCelebrity storePromotionGoodsCelebrity = goodsCelebrityService.getById(id);
        BigDecimal promPrice = Optional.ofNullable(storePromotionGoodsCelebrity.getPromPrice()).orElse(BigDecimal.ZERO);
        if (promPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error(10001, "请先设置当前网红推广费");
        }
        String promId = storePromotionGoodsCelebrity.getPromId();
        BigDecimal celebrityPrice = Optional.ofNullable(storePromotionGoodsCelebrity.getCelebrityPrice()).orElse(BigDecimal.ZERO);
        StoreSellerPromotion sellerPromotion = storeSellerPromotionService.getById(promId);
        // 判断当前推广状态
        Integer promStatus = sellerPromotion.getPromStatus();
        if (promStatus == PromotionGoodsType.FINISHEDNORMAL.getCode() || promStatus == PromotionGoodsType.FINISHEDABNORMAL.getCode()) {
            // 当前推广状态为结束，不允许选中网红
            return Result.error("当前推广已结束，不允许选中或取消网红");
        }

        List<StorePromotionGoodsCelebrity> goodsCelebrityList = goodsCelebrityService.getByPromId(promId);
        // 获取汇率
        BigDecimal exchangeRate = sellerPromotion.getExchangeRate();

        // 构建推广实体
        StoreSellerPromotion storeSellerPromotionNew = new StoreSellerPromotion();
        storeSellerPromotionNew.setId(promId);

        // 修改产品网红选中状态
        StorePromotionGoodsCelebrity promotionGoodsCelebrity = new StorePromotionGoodsCelebrity();
        promotionGoodsCelebrity.setId(id);
        promotionGoodsCelebrity.setCelebrityStatus(status);

        // 当前推广所有网红推广费和税费
        JSONObject jsonObject = getPromotionAmt(goodsCelebrityList, "");
        BigDecimal promAmt = jsonObject.getBigDecimal(CelebrityPromStatus.PRO_MAMT);
        BigDecimal celPrice = jsonObject.getBigDecimal(CelebrityPromStatus.CEL_PRICE);
        BigDecimal taxAmt = jsonObject.getBigDecimal(CelebrityPromStatus.TAX_AMT);

        // 已选中
        if (status == YesNoStatus.YES.getCode()) {
            BigDecimal promAmtNow = promAmt.add(promPrice);
            storeSellerPromotionNew.setPromAmount(promAmtNow);
            storeSellerPromotionNew.setCelebrityTotalAmount(celPrice.add(celebrityPrice));
            storeSellerPromotionNew.setTotalAmount(promAmtNow.add(taxAmt));
            storeSellerPromotionNew.setTotalRmbAmount(storeSellerPromotionNew.getTotalAmount().multiply(exchangeRate));
            storeSellerPromotionNew.setApprovedStatus(ApprovedStatusType.MATCH_SUCCESSFULLY.getCode());
        } else {
            // 当前推广所有网红推广费和税费
            jsonObject = getPromotionAmt(goodsCelebrityList, id);
            promAmt = jsonObject.getBigDecimal(CelebrityPromStatus.PRO_MAMT);
            celPrice = jsonObject.getBigDecimal(CelebrityPromStatus.CEL_PRICE);
            taxAmt = jsonObject.getBigDecimal(CelebrityPromStatus.TAX_AMT);

            promotionGoodsCelebrity.setTaxAmount(BigDecimal.ZERO);
            promotionGoodsCelebrity.setIsIncludingTax(0);

            storeSellerPromotionNew.setIsIncludePaypalFees(0);
            storeSellerPromotionNew.setPromAmount(promAmt);
            storeSellerPromotionNew.setTotalTaxAmount(taxAmt);
            storeSellerPromotionNew.setCelebrityTotalAmount(celPrice);
            storeSellerPromotionNew.setTotalAmount(promAmt.add(taxAmt));
            storeSellerPromotionNew.setTotalRmbAmount(storeSellerPromotionNew.getTotalAmount().multiply(exchangeRate));
            storeSellerPromotionNew.setApprovedStatus(ApprovedStatusType.MATCH_SUCCESSFULLY.getCode());
            if (promAmt.compareTo(BigDecimal.ZERO) == 0) {
                storeSellerPromotionNew.setApprovedStatus(ApprovedStatusType.PROVIDE_PLAN.getCode());
            }
        }

        // 判断当前已付金额是否超过总推广费
        boolean flag = checkTotalAmt(sellerPromotion, storeSellerPromotionNew);
        if (!flag) {
            return Result.error("当前已付金额大于订单总金额，请退款后再操作");
        }
        goodsCelebrityService.updateGoodsCelebrity(storeSellerPromotionNew, promotionGoodsCelebrity);
        return Result.ok("修改成功！");
    }

    /**
     * 功能描述:获取金额
     *
     * @return java.util.Map<java.lang.String, java.math.BigDecimal>
     * @Date 2023-11-20 10:22:32
     */
    private JSONObject getPromotionAmt(List<StorePromotionGoodsCelebrity> goodsCelebrityList, String id) {
        JSONObject jsonObject = new JSONObject();
        BigDecimal promAmt;
        BigDecimal celPrice;
        BigDecimal taxAmt;
        if (oConvertUtils.isEmpty(id)) {
            promAmt = goodsCelebrityList.stream()
                    .filter(x -> x.getCelebrityStatus() == 1 && x.getCelebrityPromStatus() != -1)
                    .map(x -> Optional.ofNullable(x.getPromPrice()).orElse(BigDecimal.ZERO))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            celPrice = goodsCelebrityList.stream()
                    .filter(x -> x.getCelebrityStatus() == 1 && x.getCelebrityPromStatus() != -1)
                    .map(x -> Optional.ofNullable(x.getCelebrityPrice()).orElse(BigDecimal.ZERO))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            taxAmt = goodsCelebrityList.stream()
                    .filter(x -> x.getCelebrityStatus() == 1 && x.getCelebrityPromStatus() != -1)
                    .map(x -> Optional.ofNullable(x.getTaxAmount()).orElse(BigDecimal.ZERO))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            promAmt = goodsCelebrityList.stream()
                    .filter(x -> !x.getId().equals(id) && x.getCelebrityStatus() == 1 && x.getCelebrityPromStatus() != -1)
                    .map(x -> Optional.ofNullable(x.getPromPrice()).orElse(BigDecimal.ZERO))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            celPrice = goodsCelebrityList.stream()
                    .filter(x -> !x.getId().equals(id) && x.getCelebrityStatus() == 1 && x.getCelebrityPromStatus() != -1)
                    .map(x -> Optional.ofNullable(x.getCelebrityPrice()).orElse(BigDecimal.ZERO))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            taxAmt = goodsCelebrityList.stream()
                    .filter(x -> !x.getId().equals(id) && x.getCelebrityStatus() == 1 && x.getCelebrityPromStatus() != -1)
                    .map(x -> Optional.ofNullable(x.getTaxAmount()).orElse(BigDecimal.ZERO))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        jsonObject.put(CelebrityPromStatus.PRO_MAMT, promAmt);
        jsonObject.put(CelebrityPromStatus.CEL_PRICE, celPrice);
        jsonObject.put(CelebrityPromStatus.TAX_AMT, taxAmt);
        return jsonObject;
    }

    @AutoLog(value = "选择税费")
@Operation(summary = "选择税费", description = "选择税费")
    @GetMapping(value = "/selectTaxes")
    public Result<?> selectTaxes(@RequestParam(name = "id", required = true) String id,
                                 @RequestParam(name = "isIncludingTax", required = true) Integer isIncludingTax) {
        if (oConvertUtils.isEmpty(id)) {
            return Result.error("请匹配网红后操作！");
        }
        // 查询产品网红信息
        StorePromotionGoodsCelebrity storePromotionGoodsCelebrity = goodsCelebrityService.getById(id);
        BigDecimal promPrice = Optional.ofNullable(storePromotionGoodsCelebrity.getPromPrice()).orElse(BigDecimal.ZERO);
        if (promPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error("请先设置当前网红推广费");
        }
        Integer celebrityPromStatus = storePromotionGoodsCelebrity.getCelebrityPromStatus();
        if (celebrityPromStatus.equals(PROM_STATUS_3) || celebrityPromStatus.equals(PROM_STATUS_FINISH)) {
            return Result.error("修改税费失败，当前网红状态已结束");
        }
        String promId = storePromotionGoodsCelebrity.getPromId();
        StoreSellerPromotion sellerPromotion = storeSellerPromotionService.getById(promId);

        // 判断当前推广状态
        Integer promStatus = sellerPromotion.getPromStatus();
        if (promStatus == PromotionGoodsType.FINISHEDNORMAL.getCode() || promStatus == PromotionGoodsType.FINISHEDABNORMAL.getCode()) {
            // 当前推广状态为结束，不允许选中网红
            return Result.error("当前推广已结束，不允许修改税费");
        }

        // 获取汇率
        BigDecimal exchangeRate = sellerPromotion.getExchangeRate();
        // 查询默认税率
        SysDictItem sysDictItem = sysDictService.queryItemByDictCode(CommonConstant.PROMOTION_DEFAULT_TAX_RATE).get(0);
        BigDecimal promotionDefaultTaxRate = new BigDecimal(sysDictItem.getItemValue());
        BigDecimal taxRate = Optional.ofNullable(sellerPromotion.getTaxRate()).orElse(promotionDefaultTaxRate);

        // 修改产品网红税费选中状态
        StorePromotionGoodsCelebrity promotionGoodsCelebrity = new StorePromotionGoodsCelebrity();
        promotionGoodsCelebrity.setId(id);
        promotionGoodsCelebrity.setIsIncludingTax(isIncludingTax);

        // 查询当前推广下所有已选中网红
        List<StorePromotionGoodsCelebrity> goodsCelebrityList = goodsCelebrityService.getByPromId(promId);
        // 当前推广所有网红推广费和税费
        JSONObject jsonObject = getPromotionAmt(goodsCelebrityList, "");
        BigDecimal promAmt = jsonObject.getBigDecimal(CelebrityPromStatus.PRO_MAMT);
        BigDecimal taxAmt = jsonObject.getBigDecimal(CelebrityPromStatus.TAX_AMT);

        StoreSellerPromotion storeSellerPromotionNew = new StoreSellerPromotion();
        storeSellerPromotionNew.setId(promId);
        storeSellerPromotionNew.setTaxRate(taxRate);
        // 当前网红税费
        BigDecimal taxAmount;
        // 订单总金额
        BigDecimal totalAmt;
        // 订单总金额RMB
        BigDecimal totalRmbAmt;
        // 已选中
        if (isIncludingTax == YesNoStatus.YES.getCode()) {
            taxAmount = promPrice.multiply(taxRate).divide(new BigDecimal(100), 4, RoundingMode.HALF_UP);
            taxAmt = taxAmt.add(taxAmount);
            totalAmt = promAmt.add(taxAmt);
            totalRmbAmt = totalAmt.multiply(exchangeRate);
        } else {
            taxAmount = BigDecimal.ZERO;
            taxAmt = goodsCelebrityList.stream()
                    .filter(x -> !x.getId().equals(id))
                    .map(x -> Optional.ofNullable(x.getTaxAmount()).orElse(BigDecimal.ZERO))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            totalAmt = promAmt.add(taxAmt);
            totalRmbAmt = totalAmt.multiply(exchangeRate);
        }
        storeSellerPromotionNew.setTotalTaxAmount(taxAmt);
        storeSellerPromotionNew.setTotalAmount(promAmt.add(taxAmt));
        storeSellerPromotionNew.setTotalRmbAmount(totalRmbAmt);

        promotionGoodsCelebrity.setTaxAmount(taxAmount);

        // 判断当前已付金额是否超过总推广费
        boolean flag = checkTotalAmt(sellerPromotion, storeSellerPromotionNew);
        if (!flag) {
            return Result.error("当前已付金额大于订单总金额，请退款后再操作");
        }
        goodsCelebrityService.updateGoodsCelebrity(storeSellerPromotionNew, promotionGoodsCelebrity);

        return Result.ok("操作成功！");
    }

    /**
     * 功能描述:判断当前已付金额是否超过总推广费
     *
     * @return void
     * @Date 2023-11-20 10:19:13
     */
    private boolean checkTotalAmt(StoreSellerPromotion sellerPromotion, StoreSellerPromotion storeSellerPromotionNew) {
        BigDecimal totalRmbAmountNow = Optional.ofNullable(storeSellerPromotionNew.getTotalRmbAmount()).orElse(BigDecimal.ZERO);
        // 已付金额
        BigDecimal paymentAmount = Optional.ofNullable(sellerPromotion.getPaymentAmount()).orElse(BigDecimal.ZERO);
        // 退款金额
        BigDecimal refundAmount = Optional.ofNullable(sellerPromotion.getRefundAmount()).orElse(BigDecimal.ZERO);
        // 实付金额
        BigDecimal paidAmount = paymentAmount.subtract(refundAmount);
        // 判断实付金额和当前订单总额是否一致
        int flagNum = paidAmount.compareTo(totalRmbAmountNow);
        if (flagNum > 0) {
            return false;
        }
        if (flagNum < 0 || paidAmount.compareTo(BigDecimal.ZERO) > 0) {
            storeSellerPromotionNew.setPayStatus(PayStatusEnum.DOWNPAYMENT.getCode());
        }
        if (flagNum == 0 && totalRmbAmountNow.compareTo(BigDecimal.ZERO) > 0) {
            storeSellerPromotionNew.setPayStatus(PayStatusEnum.PAIDINFULL.getCode());
        }
        if (paidAmount.compareTo(BigDecimal.ZERO) == 0) {
            storeSellerPromotionNew.setPayStatus(PayStatusEnum.TOBEPAID.getCode());
        }
        return true;
    }

    /**
     * 修改折扣码
     *
     * @param storePromotionGoodsCelebrity
     * @return
     */
    @AutoLog(value = "修改折扣码")
@Operation(summary = "修改折扣码", description = "修改折扣码")
    @PostMapping(value = "/updateDiscountCode")
    public Result<?> updateDiscountCode(@RequestBody StorePromotionGoodsCelebrity storePromotionGoodsCelebrity) {
        goodsCelebrityService.updateById(storePromotionGoodsCelebrity);
        return Result.ok("操作成功！");
    }

    @AutoLog(value = "查看历史匹配网红")
@Operation(summary = "查看历史匹配网红", description = "查看历史匹配网红")
    @GetMapping(value = "/getOldCelebrity")
    public Result<?> getOldCelebrity(@RequestParam(name = "promId", required = true) String promId,
                                     @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<StorePromotionGoodsModel> page = new Page<>(pageNo, pageSize);
        IPage<StorePromotionGoodsModel> pageList = goodsCelebrityService.getOldCelebrity(page, promId);
        return Result.ok(pageList);
    }


    /**
     * 功能描述:批量修改产品网红推广费，网红状态，是否含税
     *
     * @return org.jeecg.common.api.vo.Result<?>
     * @Date 2023-11-17 16:23:42
     */
    @AutoLog(value = "批量修改产品网红")
@Operation(summary = "批量修改产品网红", description = "批量修改产品网红")
    @PostMapping(value = "/updateGoodsCelebrityBatch")
    public Result<?> updateGoodsCelebrityBatch(@RequestBody JSONObject jsonObject) {
        String promId = jsonObject.getString("promId");
        List<StorePromotionGoodsCelebrity> goodsCelebrityOldList = goodsCelebrityService.getByPromIdAll(promId);
        List<StorePromotionGoodsCelebrity> goodsCelebrityList = jsonObject.getJSONArray("goodsCelebrityList").toJavaList(StorePromotionGoodsCelebrity.class);
        if (goodsCelebrityList.isEmpty()) {
            return Result.error("请选择网红后再编辑！");
        }
        List<String> celebrityIds = goodsCelebrityList.stream().map(StorePromotionGoodsCelebrity::getId).collect(Collectors.toList());
        StoreSellerPromotion sellerPromotion = storeSellerPromotionService.getById(promId);
        // 判断当前推广状态
        Integer promStatus = sellerPromotion.getPromStatus();
        if (promStatus == PromotionGoodsType.FINISHEDNORMAL.getCode() || promStatus == PromotionGoodsType.FINISHEDABNORMAL.getCode()) {
            // 当前推广状态为结束，不允许选中网红
            return Result.error("当前推广已结束，不允许编辑网红");
        }

        // 查询默认税率
        SysDictItem sysDictItem = sysDictService.queryItemByDictCode(CommonConstant.PROMOTION_DEFAULT_TAX_RATE).get(0);
        BigDecimal promotionDefaultTaxRate = new BigDecimal(sysDictItem.getItemValue());
        BigDecimal taxRate = Optional.ofNullable(sellerPromotion.getTaxRate()).orElse(promotionDefaultTaxRate);

        List<JSONObject> errorList = new ArrayList<>();
        for (StorePromotionGoodsCelebrity goodsCelebrity : goodsCelebrityOldList) {
            JSONObject errorJson = new JSONObject();
            String celebrityId = goodsCelebrity.getId();
            String celebrityPrivateId = goodsCelebrity.getCelebrityPrivateId();
            StoreCelebrityPrivate celebrityPrivate = privateService.getById(celebrityPrivateId);
            Integer celebrityPromStatus = goodsCelebrity.getCelebrityPromStatus();
            Optional<StorePromotionGoodsCelebrity> optional = goodsCelebrityList.stream().filter(x -> x.getId().equals(celebrityId)).findFirst();
            if (optional.isPresent()) {
                if (celebrityPromStatus.equals(PROM_STATUS_3) || celebrityPromStatus.equals(PROM_STATUS_FINISH)) {
                    errorJson.put(CelebrityPromStatus.CELEBRITY_NAME, celebrityPrivate.getAccount());
                    errorJson.put(CelebrityPromStatus.MESSAGE, "当前网红状态已结束");
                    errorList.add(errorJson);
                    continue;
                }

                StorePromotionGoodsCelebrity celebrity = optional.get();
                Integer isIncludingTax = Optional.ofNullable(celebrity.getIsIncludingTax()).orElse(0);
                Integer celebrityStatus = celebrity.getCelebrityStatus();
                goodsCelebrity.setCelebrityStatus(celebrityStatus);
                goodsCelebrity.setPromPrice(celebrity.getPromPrice());
                goodsCelebrity.setCelebrityPrice(celebrity.getCelebrityPrice());
                goodsCelebrity.setIsIncludingTax(isIncludingTax);
                BigDecimal taxAmount = BigDecimal.ZERO;
                if (isIncludingTax == 1) {
                    if (celebrityStatus != 1) {
                        goodsCelebrity.setIsIncludingTax(0);
                        errorJson.put(CelebrityPromStatus.CELEBRITY_NAME, celebrityPrivate.getAccount());
                        errorJson.put(CelebrityPromStatus.MESSAGE, "请选中网红后再设置税费");
                        errorList.add(errorJson);
                        continue;
                    }
                    taxAmount = goodsCelebrity.getPromPrice().multiply(taxRate).divide(new BigDecimal(100), 4, RoundingMode.HALF_UP);
                }
                goodsCelebrity.setTaxAmount(taxAmount);
            }
        }
        JSONObject data = getPromotionAmt(goodsCelebrityOldList, "");
        BigDecimal promAmt = data.getBigDecimal(CelebrityPromStatus.PRO_MAMT);
        BigDecimal celPrice = data.getBigDecimal(CelebrityPromStatus.CEL_PRICE);
        BigDecimal taxAmt = data.getBigDecimal(CelebrityPromStatus.TAX_AMT);

        // 获取汇率
        BigDecimal exchangeRate = sellerPromotion.getExchangeRate();
        StoreSellerPromotion sellerPromotionNew = new StoreSellerPromotion();
        sellerPromotionNew.setId(promId);
        sellerPromotionNew.setPromAmount(promAmt);
        sellerPromotionNew.setCelebrityTotalAmount(celPrice);
        sellerPromotionNew.setTotalTaxAmount(taxAmt);
        sellerPromotionNew.setTotalAmount(promAmt.add(taxAmt));
        sellerPromotionNew.setTotalRmbAmount((promAmt.add(taxAmt)).multiply(exchangeRate));
        boolean match = goodsCelebrityOldList.stream().anyMatch(x -> x.getCelebrityStatus() == 1);
        if (match) {
            sellerPromotionNew.setApprovedStatus(ApprovedStatusType.MATCH_SUCCESSFULLY.getCode());
        } else {
            sellerPromotionNew.setApprovedStatus(ApprovedStatusType.EXAMINATION_PASSED.getCode());
        }

        // 判断当前已付金额是否超过总推广费
        boolean flag = checkTotalAmt(sellerPromotion, sellerPromotionNew);
        if (!flag) {
            return Result.error("当前已付金额大于订单总金额，请退款后再操作");
        }
        Result<List<JSONObject>> result = new Result<>();
        List<StorePromotionGoodsCelebrity> goodsCelebrities = goodsCelebrityOldList.stream().filter(x -> celebrityIds.contains(x.getId())).collect(Collectors.toList());
        if (errorList.isEmpty()) {
            goodsCelebrityService.updateGoodsCelebrityBatch(goodsCelebrities, sellerPromotionNew);
            result.setCode(200);
            result.setMessage("编辑成功！");
        } else {
            result.setCode(500);
            result.setMessage("编辑失败！");
            result.setResult(errorList);
        }
        return result;
    }
}
