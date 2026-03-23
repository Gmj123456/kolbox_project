package org.jeecg.modules.instagram.storepromotion.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.aspect.annotation.LimitSubmit;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.PaymentConstant;
import org.jeecg.common.constant.enums.*;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.storecountry.entity.StoreCountry;
import org.jeecg.modules.instagram.storecountry.service.IStoreCountryService;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionGoods;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionGoodsCelebrity;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionPayment;
import org.jeecg.modules.instagram.storepromotion.entity.StoreSellerPromotion;
import org.jeecg.modules.instagram.storepromotion.model.PromotionStatisticsModel;
import org.jeecg.modules.instagram.storepromotion.model.StorePromotionGoodsModel;
import org.jeecg.modules.instagram.storepromotion.model.StoreSellerPromotionModel;
import org.jeecg.modules.instagram.storepromotion.model.StoreSellerPromotionPayModel;
import org.jeecg.modules.instagram.storepromotion.service.IStorePromotionGoodsCelebrityService;
import org.jeecg.modules.instagram.storepromotion.service.IStorePromotionGoodsService;
import org.jeecg.modules.instagram.storepromotion.service.IStorePromotionPaymentService;
import org.jeecg.modules.instagram.storepromotion.service.IStoreSellerPromotionService;
import org.jeecg.modules.instagram.storeseller.storeuser.entity.StoreUserCounselor;
import org.jeecg.modules.instagram.storeseller.storeuser.service.IStoreUserCounselorService;
import org.jeecg.modules.kol.entity.KolProduct;
import org.jeecg.modules.kol.service.IKolProductService;
import org.jeecg.modules.system.entity.SysDictItem;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.enmus.ExcelType;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.jeecg.common.constant.ApprovedStatus.APPROVED_STATUS_0;
import static org.jeecg.common.constant.ApprovedStatus.APPROVED_STATUS_1;


/**
 * @Description: 商家推广管理
 * @Author: jeecg-boot
 * @Date: 2021-02-07
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "商家推广管理")
@RestController
@RequestMapping("/storeSellerPromotion")
public class StoreSellerPromotionController extends JeecgController<StoreSellerPromotion, IStoreSellerPromotionService> {

    @Autowired
    private IStoreSellerPromotionService storeSellerPromotionService;
    @Autowired
    private IStorePromotionGoodsService storePromotionGoodsService;
    @Autowired
    private IStorePromotionGoodsCelebrityService storePromotionGoodsCelebrityService;
    @Autowired
    private IStorePromotionPaymentService storePromotionPaymentService;
    @Autowired
    private IStoreCountryService storeCountryService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private ISysDictService sysDictService;
    @Autowired
    private IStoreUserCounselorService storeUserCounselorService;
    @Autowired
    private IKolProductService kolProductService;


    /**
     * 分页列表查询
     *
     * @param storeSellerPromotionModel
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "商家推广管理")
    @Operation(summary = "商家推广管理-分页列表查询", description = "商家推广管理-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StoreSellerPromotionModel storeSellerPromotionModel,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        Page<StoreSellerPromotion> page = new Page<>(pageNo, pageSize);
        if (!oConvertUtils.checkUserType(getUserNameByToken())) {
            storeSellerPromotionModel.setSellerId(getUserIdByToken());
        }
        storeSellerPromotionModel.setCounselorUserId(getUserIdByToken());
        Integer celebrityPromStatus = storeSellerPromotionModel.getCelebrityPromStatus();
        String account = storeSellerPromotionModel.getKolAccount();
        String sql = "";
        if (oConvertUtils.isNotEmpty(celebrityPromStatus) || oConvertUtils.isNotEmpty(account)) {
            sql = "LEFT JOIN store_promotion_goods_celebrity spgc ON ssp.id = spgc.prom_id\n" +
                    "LEFT JOIN store_celebrity_private scp ON scp.id = spgc.celebrity_private_id ";
        }
        IPage<StoreSellerPromotion> pageList = storeSellerPromotionService.pageList(page, storeSellerPromotionModel, sql);
        return Result.ok(pageList);
    }

    @AutoLog(value = "商家推广管理")
    @Operation(summary = "商家推广管理-分页列表查询", description = "商家推广管理-分页列表查询")
    @GetMapping(value = "/sellerList")
    public Result<?> queryPageSellerList(StoreSellerPromotionModel storeSellerPromotionModel,
                                         @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                         HttpServletRequest req) {
        Page<StoreSellerPromotion> page = new Page<>(pageNo, pageSize);
        if (!oConvertUtils.checkUserType(getUserNameByToken())) {
            storeSellerPromotionModel.setSellerId(getUserIdByToken());
        }
        storeSellerPromotionModel.setPromType(1);
        IPage<StoreSellerPromotion> pageList = storeSellerPromotionService.queryPageSellerList(page, storeSellerPromotionModel);
        return Result.ok(pageList);
    }

    @AutoLog(value = "更新商务人员列表查询")
    @Operation(summary = "商家推广管理-更新商务人员列表查询", description = "商家推广管理-更新商务人员列表查询")
    @GetMapping(value = "promotionList")
    public Result<?> getPromotionList(StoreSellerPromotionModel storeSellerPromotionModel) {
      /*  if (!oConvertUtils.checkUserType(getUserNameByToken())) {
            storeSellerPromotionModel.setSellerId(getUserIdByToken());
        }*/
        List<LinkedHashMap<String, Object>> promotionList = storeSellerPromotionService.getPromotionList(storeSellerPromotionModel);
        return Result.ok(promotionList);
    }

    /**
     * 添加
     *
     * @param storeSellerPromotionModel
     * @return
     */
    @AutoLog(value = "商家推广管理-添加")
    @Operation(summary = "商家推广管理-添加", description = "商家推广管理-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody StoreSellerPromotionModel storeSellerPromotionModel) {
       /* // 获取最小预算
        SysDictItem dictItem = sysDictItemService.selectValueByCode(DictCode.MIN_BUDGET);
        int minBudget = Integer.parseInt(dictItem.getItemValue());
        // 判断最小预算是否符合
        if (storeSellerPromotionModel.getPromMinBudget().compareTo(new BigDecimal(minBudget)) < 0) {
            return Result.error("最小预算不能小于$" + minBudget + "!");
        }
        // 判断推广日期是否小于当前日期
        if (storeSellerPromotionModel.getPromStartTime().before(new Date())) {
            return Result.error("推广日期不能小于当前日期！");
        }*/
        // String id = storeSellerPromotionModel.getId();
        // if (oConvertUtils.isEmpty(id)) {
        //     return Result.error("未获取到需求！");
        // }
        String userId = storeSellerPromotionModel.getCounselorUserId();
        if (oConvertUtils.isEmpty(userId)) {
            userId = getUserIdByToken();
        }
        SysUser sysUser = sysUserService.getById(userId);
        if (!oConvertUtils.isAdmin(getUserNameByToken())) {
            Integer userType = sysUser.getUserType();
            List<Integer> userTypes = new ArrayList<>();
            userTypes.add(UserType.SELLER.getCode());
            userTypes.add(UserType.BUYERS.getCode());
            userTypes.add(UserType.BUSINESS_PERSONNEL.getCode());
            if (!userTypes.contains(userType)) {
                return Result.error("当前用户无法新增需求");
            }
        }

        storeSellerPromotionModel.setCounselorUserId(userId);
        storeSellerPromotionModel.setCounselorUserName(sysUser.getRealname());
        String id = storeSellerPromotionModel.getId();
        if (oConvertUtils.isNotEmpty(id)) {
            if (oConvertUtils.isEmpty(id)) {
                return Result.error("未获取到需求！");
            }
            StoreSellerPromotion storeSellerPromotion = storeSellerPromotionService.getById(id);
            if (storeSellerPromotion == null) {
                return Result.error("未获取到需求！");
            }
            if (!storeSellerPromotion.getApprovedStatus().equals(APPROVED_STATUS_0)) {
                return Result.error("当前需求无法审核！");
            }
        }else{
            storeSellerPromotionModel.setId(UUID.randomUUID().toString().replace("-", ""));
        }

        List<StorePromotionGoods> goodsListNew = new ArrayList<>();

        List<StorePromotionGoods> goodsList = storeSellerPromotionModel.getGoodsList();
        if (goodsList == null || goodsList.isEmpty() || goodsList.stream().anyMatch(g -> oConvertUtils.isEmpty(g.getGoodsUrl()))) {
            return Result.error("请添加商品!");
        }

        boolean isNewData = Objects.equals(storeSellerPromotionModel.getIsNewData(), 1);

        List<String> productIds = goodsList.stream()
                .map(StorePromotionGoods::getKolProductId)
                .distinct()
                .collect(Collectors.toList());

        if (isNewData && productIds.size() != goodsList.size()) {
            return Result.error("请勿重复选择商品!");
        }
        String sellerCounselorId = storeSellerPromotionModel.getSellerCounselorId();
        String counselorUserId = storeSellerPromotionModel.getCounselorUserId();
        if (oConvertUtils.isEmpty(sellerCounselorId)) {
            return Result.error("请选择商家顾问！");
        }
        if (oConvertUtils.isEmpty(counselorUserId)) {
            return Result.error("请选择商务人员！");
        }
        String code = getCode();
//        String sellerPromotionId = UUID.randomUUID().toString().replace("-", "");

        Map<String, KolProduct> kolProductMap = Collections.emptyMap();
        if (isNewData) {
            List<KolProduct> kolProducts = kolProductService.listByIds(productIds);
            kolProductMap = kolProducts.stream().collect(Collectors.toMap(KolProduct::getId, Function.identity()));
        }
        StringBuilder kolBrandIds = new StringBuilder();
        StringBuilder kolBrandNames = new StringBuilder();

        for (StorePromotionGoods goods : goodsList) {
            // 填充推广相关字段
            goods.setSellerId(storeSellerPromotionModel.getSellerId());
            goods.setSellerName(storeSellerPromotionModel.getSellerName());
            goods.setExtensionCode(code);
            goods.setPromId(storeSellerPromotionModel.getId());
            goods.setGoodsStatus(YesNoStatus.NO.getCode());

            // 如果是新数据，补充商品信息
            if (isNewData) {
                KolProduct kolProduct = kolProductMap.get(goods.getKolProductId());
                if (kolProduct != null) {
                    goods.setGoodsTitle(kolProduct.getProductName());
                    goods.setGoodsPicUrl(kolProduct.getProductImage());
                    goods.setGoodsUrl(kolProduct.getProductUrl());
                    goods.setProductAttributes(kolProduct.getProductAttributes());
                    if (oConvertUtils.isNotEmpty(kolBrandIds.toString())) {
                        kolBrandIds.append(",").append(kolProduct.getBrandId());
                        kolBrandNames.append(",").append(kolProduct.getBrandName());
                    } else {
                        kolBrandIds.append(kolProduct.getBrandId());
                        kolBrandNames.append(kolProduct.getBrandName());
                    }
                }
            }
            goodsListNew.add(goods);
        }

        // 根据国家id获取货币符号
        String countryId = storeSellerPromotionModel.getPromCountry();
        StoreCountry country = storeCountryService.getById(countryId);

        // 查询汇率
        SysDictItem sysDictItem = sysDictService.queryItemByDictCode(CommonConstant.PROMOTION_DEFAULT_RATE).get(0);
        BigDecimal promotionDefaultRate = new BigDecimal(sysDictItem.getItemValue());
        // 查询默认税费
        SysDictItem taxRate = sysDictService.queryItemByDictCode(CommonConstant.PROMOTION_DEFAULT_TAX_RATE).get(0);
        BigDecimal promotionDefaultTaxRate = new BigDecimal(taxRate.getItemValue());

//        storeSellerPromotionModel.setId(sellerPromotionId);
        storeSellerPromotionModel.setExtensionCode(code);
        storeSellerPromotionModel.setPromStatus(YesNoStatus.NO.getCode());
        storeSellerPromotionModel.setApprovedStatus(APPROVED_STATUS_1);
        storeSellerPromotionModel.setPayStatus(YesNoStatus.NO.getCode());
        storeSellerPromotionModel.setPromCountry(country != null ? country.getShortCode() : "");
        storeSellerPromotionModel.setIsDel(YesNoStatus.NO.getCode());
        storeSellerPromotionModel.setExchangeRate(promotionDefaultRate);
        storeSellerPromotionModel.setTaxRate(promotionDefaultTaxRate);
        if (oConvertUtils.isNotEmpty(kolBrandIds) && oConvertUtils.isNotEmpty(kolBrandNames)) {
            storeSellerPromotionModel.setProductBrandId(kolBrandIds.toString());
            storeSellerPromotionModel.setProductBrand(kolBrandNames.toString());
        }
        // 设置顾问用户
        setCounselorUser(storeSellerPromotionModel);

        if (oConvertUtils.isNotEmpty(id)) {
            storeSellerPromotionService.updateList(storeSellerPromotionModel, goodsListNew);
            return Result.ok("审核成功！");
        }
        storeSellerPromotionService.saveList(storeSellerPromotionModel, goodsListNew);
        return Result.ok("添加成功！");
    }

    private void setCounselorUser(StoreSellerPromotionModel storeSellerPromotionModel) {
        String sellerId = storeSellerPromotionModel.getSellerId();
        String sellerCounselorId = storeSellerPromotionModel.getSellerCounselorId();
        String sellerCounselorName = storeSellerPromotionModel.getSellerCounselorName();
        String counselorUserId = storeSellerPromotionModel.getCounselorUserId();
        String counselorUserName = storeSellerPromotionModel.getCounselorUserName();
        SysUser user = new SysUser();
        user.setId(sellerId);
        user.setCounselorUserId(counselorUserId);
        user.setCounselorUserName(counselorUserName);
        user.setSellerCounselorId(sellerCounselorId);
        user.setSellerCounselorName(sellerCounselorName);
        sysUserService.updateById(user);
    }

    /**
     * 编辑
     *
     * @param storeSellerPromotionModel
     * @return
     */
    @AutoLog(value = "商家推广管理-编辑")
    @Operation(summary = "商家推广管理-编辑", description = "商家推广管理-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody StoreSellerPromotionModel storeSellerPromotionModel) {
        StoreSellerPromotion sellerPromotion = storeSellerPromotionService.getById(storeSellerPromotionModel.getId());
        // 判断是否已审核
        if (sellerPromotion.getApprovedStatus() > YesNoStatus.NO.getCode()) {
            return Result.error("需求已审核通过，修改失败");
        }
        List<StorePromotionGoods> goodsList = storeSellerPromotionModel.getGoodsList();
        if (goodsList.isEmpty()) {
            return Result.ok("请添加商品!");
        }
        // 查询当前推广需求下所有产品
        LambdaQueryWrapper<StorePromotionGoods> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StorePromotionGoods::getPromId, storeSellerPromotionModel.getId());
        List<StorePromotionGoods> goodsOldList = storePromotionGoodsService.list(lambdaQueryWrapper);
        // 判断数量是否一致，不一致表示有删除的
        List<String> delGoodsIdList = new ArrayList<>();
        if (goodsOldList.size() > goodsList.size()) {
            List<String> goodsIdsListNow = goodsList.stream().map(StorePromotionGoods::getId).collect(Collectors.toList());
            List<StorePromotionGoods> delGoodsList = goodsOldList.stream().filter(x -> !goodsIdsListNow.contains(x.getId())).collect(Collectors.toList());
            delGoodsIdList = delGoodsList.stream().map(StorePromotionGoods::getId).collect(Collectors.toList());
        }
        // 获取存在id的产品
        List<StorePromotionGoods> editGoodsList = goodsList.stream().filter(x -> oConvertUtils.isNotEmpty(x.getId())).collect(Collectors.toList());
        // 获取不存在id的产品
        List<StorePromotionGoods> saveGoodsList = goodsList.stream().filter(x -> oConvertUtils.isEmpty(x.getId())).collect(Collectors.toList());
        List<StorePromotionGoods> saveGoodsListNew = new ArrayList<>();
        if (!saveGoodsList.isEmpty()) {
            for (StorePromotionGoods storePromotionGoods : saveGoodsList) {
                storePromotionGoods.setSellerId(storeSellerPromotionModel.getSellerId());
                storePromotionGoods.setSellerName(storeSellerPromotionModel.getSellerName());
                storePromotionGoods.setGoodsStatus(YesNoStatus.NO.getCode());
                storePromotionGoods.setPromId(storeSellerPromotionModel.getId());
                storePromotionGoods.setExtensionCode(storeSellerPromotionModel.getExtensionCode());
                saveGoodsListNew.add(storePromotionGoods);
            }
        }
        // 根据国家id获取货币符号
        // String counrtyId = storeSellerPromotionModel.getPromCounrty();
        // StoreCountry country = storeCountryService.getById(counrtyId);
        // storeSellerPromotionModel.setPromCounrty(country.getShortCode());
        storeSellerPromotionModel.setCurrencySymbol("$");
        storeSellerPromotionService.updateGoods(editGoodsList, saveGoodsListNew, delGoodsIdList, storeSellerPromotionModel);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "商家推广管理-通过id删除")
    @Operation(summary = "商家推广管理-通过id删除", description = "商家推广管理-通过id删除")
    @DeleteMapping(value = "/delete")
    @Transactional
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            StoreSellerPromotion storeSellerPromotion = storeSellerPromotionService.getById(id);
            Integer approvedStatus = storeSellerPromotion.getApprovedStatus();
            // 状态为未审核
            if (approvedStatus == YesNoStatus.NO.getCode()) {
                // 查询推广产品列表
                LambdaQueryWrapper<StorePromotionGoods> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(StorePromotionGoods::getPromId, id);
                List<StorePromotionGoods> storePromotionGoodsList = storePromotionGoodsService.list(lambdaQueryWrapper);
                if (storePromotionGoodsList.size() > 0) {
                    // 删除该需求下的推广产品
                    List<String> goodsIdList = storePromotionGoodsList.stream().map(StorePromotionGoods::getId).collect(Collectors.toList());
                    storePromotionGoodsService.removeByIds(goodsIdList);
                    // 删除该需求下的网红
                    storePromotionGoodsCelebrityService.delProm(id, goodsIdList);
                }
                storeSellerPromotionService.removeById(id);
                return Result.ok("删除成功!");
            } else {
                return Result.error("需求已审核，不可删除!");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            // 手动回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.error("删除失败！");
        }
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "商家推广管理-通过id删除")
    @Operation(summary = "商家推广管理-通过id删除", description = "商家推广管理-通过id删除")
    @DeleteMapping(value = "/deleteBack")
    public Result<?> deleteBack(@RequestParam(name = "id", required = true) String id) {
        StoreSellerPromotion storeSellerPromotion = storeSellerPromotionService.getById(id);
        if (oConvertUtils.isEmpty(storeSellerPromotion)) {
            return Result.error("删除失败，未获取到该需求！");
        }
        // 判断当前推广付款金额是否 > 0
        // 已付金额
        BigDecimal paymentAmount = oConvertUtils.isNotEmpty(storeSellerPromotion.getPaymentAmount()) ? storeSellerPromotion.getPaymentAmount() : BigDecimal.ZERO;
        BigDecimal intentionAmount = oConvertUtils.isNotEmpty(storeSellerPromotion.getIntentionAmount()) ? storeSellerPromotion.getIntentionAmount() : BigDecimal.ZERO;
        BigDecimal refundAmount = oConvertUtils.isNotEmpty(storeSellerPromotion.getRefundAmount()) ? storeSellerPromotion.getRefundAmount() : BigDecimal.ZERO;
        BigDecimal amount = paymentAmount.add(intentionAmount).subtract(refundAmount);
        // 判断已付金额 > 0 不允许删除
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            return Result.error("删除失败，请退款后再删除！");
        }
        LambdaUpdateWrapper<StoreSellerPromotion> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(StoreSellerPromotion::getId, id);
        wrapper.set(StoreSellerPromotion::getIsDel, YesNoStatus.YES.getCode());
        storeSellerPromotionService.update(wrapper);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "商家推广管理-批量删除")
    @Operation(summary = "商家推广管理-批量删除", description = "商家推广管理-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.storeSellerPromotionService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "商家推广管理-通过id查询")
    @Operation(summary = "商家推广管理-通过id查询", description = "商家推广管理-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        StoreSellerPromotion storeSellerPromotion = storeSellerPromotionService.getById(id);
        //
        return Result.ok(storeSellerPromotion);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param storeSellerPromotion
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StoreSellerPromotion storeSellerPromotion) {
        return super.exportXls(request, storeSellerPromotion, StoreSellerPromotion.class, "商家推广管理");
    }

    /**
     * 导出推广人员统计excel
     */
    @RequestMapping(value = "/exportStatisticsXls")
    public ModelAndView exportStatisticsXls(PromotionStatisticsModel promotionStatisticsModel) {
        List<PromotionStatisticsModel> pageList;
        String msg;
        // 获取导出数据
        if (promotionStatisticsModel.getDataType() == YesNoStatus.YES.getCode()) {
            pageList = storeSellerPromotionService.getExtensionStaffStatistics(promotionStatisticsModel);
            msg = "商务人员统计报表  ";
        } else {
            pageList = storeSellerPromotionService.getPersonChargeStatistics(promotionStatisticsModel);
            msg = "网红负责人统计报表  ";
        }
        String title = msg + DateUtils.date2Str(new Date(), new SimpleDateFormat("yyyy-MM-dd"));
        // 总收入金额
        BigDecimal totalAmountSum = BigDecimal.ZERO;
        // 总单数
        BigDecimal totalPromCount = BigDecimal.ZERO;
        for (PromotionStatisticsModel statisticsModel : pageList) {
            totalPromCount = totalPromCount.add(new BigDecimal(statisticsModel.getPromCount()));
            totalAmountSum = totalAmountSum.add(new BigDecimal(statisticsModel.getTotalAmountSum()));
        }
        String secondTitle = "总单数 :" + totalPromCount + "            总收入金额 :" + totalAmountSum + "元";
        // Step.3 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, title);
        mv.addObject(NormalExcelConstants.CLASS, PromotionStatisticsModel.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams(title, secondTitle, title));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
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
        return super.importExcel(request, response, StoreSellerPromotion.class);
    }


    /**
     * 推广需求维护列表查询
     *
     * @param storeSellerPromotionModel
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "商家推广管理-推广需求维护列表查询")
    @Operation(summary = "商家推广管理-推广需求维护列表查询", description = "商家推广管理-推广需求维护列表查询")
    @GetMapping(value = "/queryStoreSellerPromotionManagementList")
    public Result<?> queryStoreSellerPromotionManagementList(StoreSellerPromotionModel storeSellerPromotionModel,
                                                             @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                             @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<StoreSellerPromotionModel> page = new Page<>(pageNo, pageSize);
        IPage<StoreSellerPromotionModel> pageList = storeSellerPromotionService.queryStoreSellerPromotionManagementList(page, storeSellerPromotionModel);
        return Result.ok(pageList);
    }


    /**
     * 编辑推广信息
     *
     * @param storeSellerPromotionModel
     * @return
     */
    @AutoLog(value = "商家推广管理-编辑推广信息")
    @Operation(summary = "商家推广管理-编辑推广信息", description = "商家推广管理-编辑推广信息")
    @PutMapping(value = "/editStoreSellerPromotionBack")
    public Result<?> editStoreSellerPromotionBack(@RequestBody StoreSellerPromotionModel storeSellerPromotionModel) {
        BigDecimal promAmount = storeSellerPromotionModel.getPromAmount();
        BigDecimal exchangeRate = storeSellerPromotionModel.getExchangeRate();
        String remark = storeSellerPromotionModel.getRemark();
        // 判断如果汇率或订单金额为空，则只修改商家顾问和备注
        if (oConvertUtils.isEmpty(promAmount) || oConvertUtils.isEmpty(exchangeRate)) {
            StoreSellerPromotion storeSellerPromotion = new StoreSellerPromotion();
            storeSellerPromotion.setId(storeSellerPromotionModel.getId());
            storeSellerPromotion.setRemark(remark);
            storeSellerPromotion.setProductBrand(storeSellerPromotionModel.getProductBrand());
            storeSellerPromotion.setCelebrityUserId(storeSellerPromotionModel.getCelebrityUserId());
            storeSellerPromotion.setCelebrityUserName(storeSellerPromotionModel.getCelebrityUserName());
            storeSellerPromotionService.updateById(storeSellerPromotion);
        } else {
            BigDecimal totalTaxAmount = storeSellerPromotionModel.getTotalTaxAmount();
            BigDecimal totalAmount = storeSellerPromotionModel.getTotalAmount();
            BigDecimal totalRmbAmount = storeSellerPromotionModel.getTotalRmbAmount();
            BigDecimal addAmount = totalTaxAmount.add(promAmount);
            BigDecimal multiplyAmount = addAmount.multiply(exchangeRate).setScale(2, RoundingMode.DOWN);
            // 获取修改前推广费
            StoreSellerPromotion sellerPromotionOld = storeSellerPromotionService.getById(storeSellerPromotionModel.getId());
            if (oConvertUtils.isNotEmpty(sellerPromotionOld.getPromAmount())) {
                BigDecimal promAmountOld = sellerPromotionOld.getPromAmount();
                if (promAmountOld.compareTo(promAmount) != 0) {
                    // 判断订单金额是否正确
                    if (addAmount.compareTo(totalAmount) != 0) {
                        return Result.error("参数有误，请重新输入!");
                    }
                    // 判断总金额(RMB是否正确)
                    if (totalRmbAmount.compareTo(multiplyAmount) != 0) {
                        return Result.error("参数有误，请重新输入!");
                    }
                } else {
                    // 判断待付金额
                    BigDecimal paymentAmount = Optional.ofNullable(sellerPromotionOld.getPaymentAmount()).orElse(BigDecimal.ZERO);
                    BigDecimal intentionAmount = Optional.ofNullable(sellerPromotionOld.getIntentionAmount()).orElse(BigDecimal.ZERO);
                    BigDecimal refundAmount = Optional.ofNullable(sellerPromotionOld.getRefundAmount()).orElse(BigDecimal.ZERO);
                    BigDecimal allPay = paymentAmount.add(intentionAmount).subtract(refundAmount);
                    // 订单金额与当前实付金额相等时
                    if (multiplyAmount.compareTo(allPay) == 0) {
                        storeSellerPromotionModel.setPayStatus(PayStatusEnum.PAIDINFULL.getCode());
                        // 匹配成功
                        storeSellerPromotionModel.setApprovedStatus(ApprovedStatusType.MATCH_SUCCESSFULLY.getCode());
                    }
                }
            }
            storeSellerPromotionModel.setApprovedStatus(null);
            storeSellerPromotionService.updateById(storeSellerPromotionModel);
        }
        if (oConvertUtils.isNotEmpty(storeSellerPromotionModel.getSid())) {
            // 根据商家顾问ID获取商家顾问
            SysUser sellerCounselor = sysUserService.getById(storeSellerPromotionModel.getSid());
            // 修改商家顾问
            SysUser sysUser = new SysUser();
            sysUser.setId(storeSellerPromotionModel.getSellerId());
            sysUser.setSellerCounselorId(storeSellerPromotionModel.getSid());
            sysUser.setSellerCounselorQr(sellerCounselor.getQrCode());
            sysUser.setSellerCounselorName(sellerCounselor.getNickname());
            sysUserService.updateById(sysUser);
        }
        return Result.ok("编辑成功!");
    }


    /**
     * 编辑推广信息
     *
     * @param storeSellerPromotionModel
     * @return
     */
    @AutoLog(value = "商家推广管理-编辑推广信息")
    @Operation(summary = "商家推广管理-编辑推广信息", description = "商家推广管理-编辑推广信息")
    @PutMapping(value = "/editStoreSellerPromotion")
    public Result<?> editStoreSellerPromotion(@RequestBody StoreSellerPromotionModel storeSellerPromotionModel) {
        String promId = storeSellerPromotionModel.getId();
        StoreSellerPromotion promotion = storeSellerPromotionService.getById(promId);
        Integer promStatus = promotion.getPromStatus();
        if (promStatus == PromotionGoodsType.FINISHEDNORMAL.getCode() || promStatus == PromotionGoodsType.FINISHEDABNORMAL.getCode()) {
            return Result.error("当前推广已结束，不能编辑");
        }
        BigDecimal promAmount = storeSellerPromotionModel.getPromAmount();
        BigDecimal exchangeRate = storeSellerPromotionModel.getExchangeRate();
        String remark = storeSellerPromotionModel.getRemark();
        StoreSellerPromotion storeSellerPromotion = new StoreSellerPromotion();
        // 判断如果汇率或订单金额为空，则只修改方案顾问、产品品牌、备注
        BigDecimal taxRate = storeSellerPromotionModel.getTaxRate();
        if (oConvertUtils.isEmpty(promAmount) || oConvertUtils.isEmpty(exchangeRate)) {
            storeSellerPromotion.setId(promId);
            storeSellerPromotion.setRemark(remark);
            storeSellerPromotion.setProductBrandId(storeSellerPromotionModel.getProductBrandId());
            storeSellerPromotion.setProductBrand(storeSellerPromotionModel.getProductBrand());
            storeSellerPromotion.setCelebrityUserId(storeSellerPromotionModel.getCelebrityUserId());
            storeSellerPromotion.setCelebrityUserName(storeSellerPromotionModel.getCelebrityUserName());
            storeSellerPromotion.setExchangeRate(exchangeRate);
            if (oConvertUtils.isNotEmpty(taxRate)) {
                storeSellerPromotion.setTaxRate(taxRate);
            }
        } else {
            BigDecimal totalAmountNew = storeSellerPromotionModel.getTotalAmount();
            BigDecimal totalRmbAmount = storeSellerPromotionModel.getTotalRmbAmount();
            BigDecimal multiplyAmount = promAmount.multiply(exchangeRate).setScale(2, RoundingMode.DOWN);
            // 获取修改前推广费
            if (oConvertUtils.isNotEmpty(promotion.getPromAmount())) {
                BigDecimal promAmountOld = promotion.getPromAmount();
                if (promAmountOld.compareTo(promAmount) != 0) {
                    // 判断订单金额是否正确
                    if (promAmount.compareTo(totalAmountNew) != 0) {
                        return Result.error("参数有误，请重新输入!");
                    }
                    // 判断总金额(RMB是否正确)
                    if (totalRmbAmount.compareTo(multiplyAmount) != 0) {
                        return Result.error("参数有误，请重新输入!");
                    }
                } else {
                    // 判断待付金额
                    BigDecimal paymentAmount = Optional.ofNullable(promotion.getPaymentAmount()).orElse(BigDecimal.ZERO);
                    BigDecimal intentionAmount = Optional.ofNullable(promotion.getIntentionAmount()).orElse(BigDecimal.ZERO);
                    BigDecimal refundAmount = Optional.ofNullable(promotion.getRefundAmount()).orElse(BigDecimal.ZERO);
                    BigDecimal allPay = paymentAmount.add(intentionAmount).subtract(refundAmount);
                    // 订单金额与当前实付金额相等时
                    if (multiplyAmount.compareTo(allPay) == 0 && multiplyAmount.compareTo(BigDecimal.ZERO) > 0 && allPay.compareTo(BigDecimal.ZERO) > 0) {
                        storeSellerPromotionModel.setPayStatus(PayStatusEnum.PAIDINFULL.getCode());
                        // 匹配成功
                        storeSellerPromotionModel.setApprovedStatus(ApprovedStatusType.MATCH_SUCCESSFULLY.getCode());
                    }
                    // 如果实付金额为0 并且订单金额也为0，则修改支付状态为待支付
                    if (multiplyAmount.compareTo(BigDecimal.ZERO) == 0) {
                        storeSellerPromotionModel.setPayStatus(PayStatusEnum.TOBEPAID.getCode());
                    }
                    try {
                        if (oConvertUtils.isNotEmpty(promotion.getPromotionType()) && promotion.getPromotionType() == YesNoStatus.YES.getCode()) {
                            // 判断汇率是否改变
                            BigDecimal exchangeRateOld = Optional.ofNullable(promotion.getExchangeRate()).orElse(BigDecimal.ZERO);
                            BigDecimal exchangeRateNew = Optional.ofNullable(storeSellerPromotionModel.getExchangeRate()).orElse(BigDecimal.ZERO);
                            if (exchangeRateNew.compareTo(exchangeRateOld) != 0) {
                                // 重新计算总人民币金额
                                BigDecimal totalAmount = promotion.getTotalAmount();
                                storeSellerPromotionModel.setTotalRmbAmount(totalAmount.multiply(exchangeRateNew));
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("旧推广调用新推广方法，跳过判断");
                    }
                }
            }
            storeSellerPromotionModel.setApprovedStatus(null);
            BeanUtils.copyProperties(storeSellerPromotionModel, storeSellerPromotion);
            // 取消paypal费 2023年10月26日15:23:32
            storeSellerPromotion.setIsIncludePaypalFees(0);
        }

        // 查询网红是否包含税费
        List<StorePromotionGoodsCelebrity> goodsCelebrityList = storePromotionGoodsCelebrityService.getByPromId(promId);
        if (!goodsCelebrityList.isEmpty()) {
            for (StorePromotionGoodsCelebrity storePromotionGoodsCelebrity : goodsCelebrityList) {
                BigDecimal promPrice = storePromotionGoodsCelebrity.getPromPrice();
                // 获取当前网红税费
                if (storePromotionGoodsCelebrity.getIsIncludingTax() == 1) {
                    storePromotionGoodsCelebrity.setTaxAmount(promPrice.multiply(taxRate).divide(new BigDecimal(100), 4, RoundingMode.HALF_UP));
                }
            }
            BigDecimal allPromPrice = goodsCelebrityList.stream().map(StorePromotionGoodsCelebrity::getPromPrice).reduce(BigDecimal.ZERO, BigDecimal::add);

            // 取消paypal费 2023年10月26日15:23:32
            // BigDecimal allCelebrityPaypalFees = goodsCelebrityList.stream().map(StorePromotionGoodsCelebrity::getCelebrityPaypalFees).reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal allTaxAmount = goodsCelebrityList.stream().map(StorePromotionGoodsCelebrity::getTaxAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            storeSellerPromotion.setTotalTaxAmount(allTaxAmount);
            storeSellerPromotion.setPaypalFeesAmount(BigDecimal.ZERO);
            BigDecimal totalAmountNew = allPromPrice.add(BigDecimal.ZERO).add(allTaxAmount);
            storeSellerPromotion.setTotalAmount(totalAmountNew);
            storeSellerPromotion.setTotalRmbAmount(totalAmountNew.multiply(exchangeRate));
            storePromotionGoodsCelebrityService.updateBatchById(goodsCelebrityList);
        }
        if (oConvertUtils.isNotEmpty(storeSellerPromotionModel.getPromTitle())) {
            storeSellerPromotion.setPromTitle(storeSellerPromotionModel.getPromTitle());
        }
        storeSellerPromotionService.updateById(storeSellerPromotion);
        return Result.ok("编辑成功!");
    }

    /**
     * 推广退款接口
     * <p>
     * this is a promotion refund interface, but it can't be refunded to a real account,
     * just add a new refund record.
     * <p>
     * 这是一个推广退款接口，但是不能直接退到真实账户，只是新增一条退款记录。
     *
     * @param storeSellerPromotionModel
     * @return
     */
    @AutoLog(value = "商家推广管理-推广退款")
    @Operation(summary = "商家推广管理-推广退款", description = "商家推广管理-推广退款")
    @PostMapping(value = "/promotionRefund")
    @Transactional
    public Result<?> promotionRefund(@RequestBody StoreSellerPromotionPayModel storeSellerPromotionModel) {
        BigDecimal refundAmount = Optional.ofNullable(storeSellerPromotionModel.getRefundAmount()).orElse(BigDecimal.ZERO);
        // 判断是否RMB支付
        boolean rmbPayFlag = storeSellerPromotionModel.getPayCurency().equals(PaymentConstant.RMB);
        BigDecimal refundAmountNew = refundAmount;
        try {
            StoreSellerPromotion storeSellerPromotion = storeSellerPromotionService.getById(storeSellerPromotionModel.getId());
            Integer promStatus = storeSellerPromotion.getPromStatus();
            BigDecimal totalRmbAmount = storeSellerPromotion.getTotalRmbAmount();
            if (promStatus.equals(PromotionGoodsType.FINISHEDNORMAL.getCode()) || promStatus.equals(PromotionGoodsType.FINISHEDABNORMAL.getCode())) {
                return Result.error("退款失败，当前推广已结束!");
            }
            // 此次付款金额RMB
            if (!rmbPayFlag) {
                refundAmountNew = refundAmount.multiply(storeSellerPromotion.getExchangeRate()).setScale(2, RoundingMode.DOWN);
            }
            // 实付金额
            BigDecimal paymentAmount = Optional.ofNullable(storeSellerPromotion.getPaymentAmount()).orElse(BigDecimal.ZERO);
            BigDecimal intentionAmount = Optional.ofNullable(storeSellerPromotion.getIntentionAmount()).orElse(BigDecimal.ZERO);
            // 历史退款金额
            BigDecimal refundAmountOld = Optional.ofNullable(storeSellerPromotion.getRefundAmount()).orElse(BigDecimal.ZERO);
            storeSellerPromotion.setRefundAmount(refundAmountOld.add(refundAmountNew));
            // 实付金额 - 退款金额
            if (oConvertUtils.isNotEmpty(refundAmountOld)) {
                paymentAmount = paymentAmount.add(intentionAmount).subtract(refundAmountOld).subtract(refundAmountNew);
            }
            // 实付金额小于0
            if (paymentAmount.compareTo(BigDecimal.ZERO) < 0) {
                return Result.error("退款失败，退款金额大于实付金额!");
            }

            // 实付金额为空或者0.00 待支付
            if (paymentAmount.compareTo(BigDecimal.ZERO) == 0) {
                storeSellerPromotion.setPayStatus(PayStatusEnum.TOBEPAID.getCode());
            }
            // 支付金额大于0 部分支付
            if (paymentAmount.compareTo(BigDecimal.ZERO) > 0) {
                if (totalRmbAmount.compareTo(BigDecimal.ZERO) == 0) {
                    storeSellerPromotion.setPayStatus(PayStatusEnum.TOBEPAID.getCode());
                } else if (paymentAmount.compareTo(totalRmbAmount) == 0) {
                    storeSellerPromotion.setPayStatus(PayStatusEnum.PAIDINFULL.getCode());
                } else {
                    // 部分支付
                    storeSellerPromotion.setPayStatus(PayStatusEnum.DOWNPAYMENT.getCode());
                }
            }

            storeSellerPromotionService.updateById(storeSellerPromotion);

            StorePromotionPayment storePromotionPayment = new StorePromotionPayment();

            storePromotionPayment.setConsumeDate(storeSellerPromotionModel.getConsumeDate());

            // 支付类型(退款)
            storePromotionPayment.setPayType(PaymentConstant.PAY_TYPE_REFUND);

            // 支付方式
            storePromotionPayment.setPayWay(storeSellerPromotionModel.getPayWay());

            // 商家id和name
            storePromotionPayment.setSellerId(storeSellerPromotionModel.getSellerId());
            storePromotionPayment.setSellerName(storeSellerPromotionModel.getSellerName());
            // 订单id和单号
            storePromotionPayment.setPromId(storeSellerPromotionModel.getId());
            // 推广单号
            storePromotionPayment.setExtensionCode(storeSellerPromotionModel.getExtensionCode());


            storePromotionPayment.setPayRmbAmount(refundAmountNew.negate());
            storePromotionPayment.setPayAmount(refundAmount.negate());

            // 货币符号
            if (rmbPayFlag) {
                storePromotionPayment.setCurrencySymbol(PaymentConstant.RMB_SYMBOL);
            } else {
                storePromotionPayment.setCurrencySymbol(PaymentConstant.DOLLER_SYMBOL);
            }

            // 支付时间
            storePromotionPayment.setPayTime(new Date());
            // 线下支付
            storePromotionPayment.setIsOffline(CommonConstant.PAY_ON_LINE);

            // 支付备注
            storePromotionPayment.setRemark(storeSellerPromotionModel.getPayRemark());
            storePromotionPaymentService.save(storePromotionPayment);

        } catch (Exception e) {
            e.printStackTrace();
            // 手动回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        return Result.ok("退款成功!");
    }


    /**
     * 推广支付维护
     *
     * @param storeSellerPromotionModel
     * @return
     */
    @AutoLog(value = "商家推广管理-推广支付")
    @Operation(summary = "商家推广管理-推广支付", description = "商家推广管理-推广支付")
    @PostMapping(value = "/promotionPayment")
    @Transactional
    public Result<?> promotionPayment(@RequestBody StoreSellerPromotionPayModel storeSellerPromotionModel) {

        StoreSellerPromotion storeSellerPromotion = storeSellerPromotionService.getById(storeSellerPromotionModel.getId());

        // 非空赋值
        BigDecimal payAmount = oConvertUtils.isNotEmpty(storeSellerPromotionModel.getPayAmount()) &&
                storeSellerPromotionModel.getPayAmount().compareTo(BigDecimal.ZERO) > 0 ?
                storeSellerPromotionModel.getPayAmount() : BigDecimal.ZERO;

        // 支付前的待付金额(rmb) = 总金额 - 支付前的实付金额( 意向金 + 已付金额 - 退款金额)
        BigDecimal payableAmountRmb = storeSellerPromotionModel.getPayableAmount();
        // 支付前的实付金额(rmb)
        BigDecimal oldPaymentAmountRMB = oConvertUtils.isNotEmpty(storeSellerPromotion.getPaymentAmount()) && storeSellerPromotion.getPaymentAmount().compareTo(BigDecimal.ZERO) > 0 ? storeSellerPromotion.getPaymentAmount() : BigDecimal.ZERO;

        // 支付类型判断
        // 定金
        if (storeSellerPromotionModel.getPayType().equals(PaymentConstant.PAY_TYPE_DEPOSIT)) {
            // 判断是否设置汇率
            if (oConvertUtils.isEmpty(storeSellerPromotion.getExchangeRate()) ||
                    storeSellerPromotion.getExchangeRate().compareTo(BigDecimal.ZERO) == 0) {
                return Result.error("支付异常,请先设置汇率!");
            }
            // 支付金额(rmb)
            BigDecimal payAmountRMB;
            // 支付金额换算rmb
            // 使用rmb支付
            if (PaymentConstant.RMB.equals(storeSellerPromotionModel.getPayCurency())) {
                payAmountRMB = storeSellerPromotionModel.getPayAmount();
                System.err.println("支付前待付金额：" + payableAmountRmb);
                if (payAmountRMB.compareTo(BigDecimal.ZERO) > 0 && payAmountRMB.compareTo(payableAmountRmb) > 0) {
                    return Result.error("支付金额不能超过待付金额!");
                }
            } else {
                // 使用推广国家币种支付
                payAmountRMB = storeSellerPromotionModel.getPayAmount().multiply(storeSellerPromotion.getExchangeRate()).setScale(2, RoundingMode.DOWN);
                System.err.println("支付前待付金额：" + payableAmountRmb);
                // 支付前的推广国家的金额
                BigDecimal payableAmountRate = payableAmountRmb.divide(storeSellerPromotion.getExchangeRate(), 2, RoundingMode.DOWN);
                if (storeSellerPromotionModel.getPayAmount().compareTo(BigDecimal.ZERO) > 0 &&
                        storeSellerPromotionModel.getPayAmount().compareTo(payableAmountRate) > 0) {
                    return Result.error("支付金额不能超过待付金额!");
                }
            }

            // 计算实付金额rmb(原实付金额 + 支付金额)
            BigDecimal nowPaymentAmountRMB = oldPaymentAmountRMB.add(payAmountRMB);


            // 实付金额为空或者0.00 待支付
            if (oConvertUtils.isEmpty(storeSellerPromotionModel.getPayAmount()) ||
                    storeSellerPromotionModel.getPayAmount().compareTo(BigDecimal.ZERO) == 0) {
                storeSellerPromotionModel.setPayStatus(PayStatusEnum.TOBEPAID.getCode());
            }

            // 支付金额小于应付金额 已付订金
            if (payAmountRMB.compareTo(BigDecimal.ZERO) > 0 && payAmountRMB.compareTo(payableAmountRmb) < 0) {
                storeSellerPromotionModel.setPayStatus(PayStatusEnum.DOWNPAYMENT.getCode());
            }

            // 已付全款
            if (PaymentConstant.PROMOTION_COUNTRY.equals(storeSellerPromotionModel.getPayCurency())) {
                // 误差值
                BigDecimal Difference = payableAmountRmb.subtract(payAmountRMB);
                // 其他币种支付时，应付金额于支付金额,误差小于0.1 已付全款
                if (payAmountRMB.compareTo(BigDecimal.ZERO) > 0 &&
                        payAmountRMB.compareTo(BigDecimal.ZERO) > 0 && Difference.compareTo(new BigDecimal(0.1)) == -1) {
                    // 实付金额
                    storeSellerPromotionModel.setPayStatus(PayStatusEnum.PAIDINFULL.getCode());
                    // 匹配成功
                    storeSellerPromotionModel.setApprovedStatus(ApprovedStatusType.MATCH_SUCCESSFULLY.getCode());
                }
            } else {
                // 人民币支付时，支付金额大于等于应付金额 已付全款
                if (payAmountRMB.compareTo(BigDecimal.ZERO) > 0 &&
                        payableAmountRmb.compareTo(BigDecimal.ZERO) > 0 && payAmountRMB.compareTo(payableAmountRmb) > -1) {
                    // 实付金额
                    storeSellerPromotionModel.setPayStatus(PayStatusEnum.PAIDINFULL.getCode());
                    // 匹配成功
                    storeSellerPromotionModel.setApprovedStatus(ApprovedStatusType.MATCH_SUCCESSFULLY.getCode());
                }
            }
            storeSellerPromotionModel.setPaymentAmount(nowPaymentAmountRMB);
        }


        // 意向金
        if (storeSellerPromotionModel.getPayType().equals(PaymentConstant.PAY_TYPE_EARNEST)) {

            if (oConvertUtils.isEmpty(storeSellerPromotion.getPaymentAmount())) {
                storeSellerPromotionModel.setPaymentAmount(new BigDecimal(0));
            } else {
                storeSellerPromotionModel.setPaymentAmount(storeSellerPromotion.getPaymentAmount());
            }
            // 意向金即为支付金额
            if (oConvertUtils.isEmpty(storeSellerPromotionModel.getIntentionAmount()) || storeSellerPromotionModel.getIntentionAmount().compareTo(BigDecimal.ZERO) == 0) {
                storeSellerPromotionModel.setIntentionAmount(payAmount);
            } else {
                // 意向金累加
                storeSellerPromotionModel.setIntentionAmount(payAmount.add(storeSellerPromotionModel.getIntentionAmount()));
            }

            // 实付金额为空或者0.00 待支付
            if (oConvertUtils.isEmpty(storeSellerPromotionModel.getPayAmount()) ||
                    storeSellerPromotionModel.getPayAmount().compareTo(BigDecimal.ZERO) == 0) {
                storeSellerPromotionModel.setPayStatus(PayStatusEnum.TOBEPAID.getCode());
            }

            // 实付金额
            BigDecimal payAmountRMB = storeSellerPromotionModel.getPayAmount();


            // 已付订金
            if (payAmountRMB.compareTo(BigDecimal.ZERO) > 0) {
                storeSellerPromotionModel.setPayStatus(PayStatusEnum.DOWNPAYMENT.getCode());
            }

            // 已付全款
            if (PaymentConstant.PROMOTION_COUNTRY.equals(storeSellerPromotionModel.getPayCurency())) {
                // 误差值
                BigDecimal Difference = payableAmountRmb.subtract(payAmountRMB);
                // 其他币种支付时，应付金额于支付金额,误差小于0.1 已付全款
                if (payAmountRMB.compareTo(BigDecimal.ZERO) > 0 &&
                        payAmountRMB.compareTo(BigDecimal.ZERO) > 0 && Difference.compareTo(new BigDecimal(0.1)) == -1) {
                    // 实付金额
                    storeSellerPromotionModel.setPayStatus(PayStatusEnum.PAIDINFULL.getCode());
                    // 匹配成功
                    storeSellerPromotionModel.setApprovedStatus(ApprovedStatusType.MATCH_SUCCESSFULLY.getCode());
                }
            } else {
                // 人民币支付时，支付金额大于等于应付金额 已付全款
                if (payAmountRMB.compareTo(BigDecimal.ZERO) > 0 &&
                        payableAmountRmb.compareTo(BigDecimal.ZERO) > 0 && payAmountRMB.compareTo(payableAmountRmb) > -1) {
                    // 实付金额
                    storeSellerPromotionModel.setPayStatus(PayStatusEnum.PAIDINFULL.getCode());
                    // 匹配成功
                    storeSellerPromotionModel.setApprovedStatus(ApprovedStatusType.MATCH_SUCCESSFULLY.getCode());
                }
            }
        }


        try {
            // 更新推广订单信息
            storeSellerPromotionModel.setApprovedStatus(null);
            storeSellerPromotionModel.setUpdateBy(getUserNameByToken());
            storeSellerPromotionModel.setUpdateTime(new Date());
            storeSellerPromotionService.updateById(storeSellerPromotionModel);


            // ------------------------ 新增支付记录 ------------------------------
            StorePromotionPayment storePromotionPayment = new StorePromotionPayment();

            // 支付时间
            if (oConvertUtils.isEmpty(storeSellerPromotionModel.getConsumeDate())) {
                storePromotionPayment.setConsumeDate(new Date());
            } else {
                storePromotionPayment.setConsumeDate(storeSellerPromotionModel.getConsumeDate());
            }

            // 支付类型
            storePromotionPayment.setPayType(storeSellerPromotionModel.getPayType());
            // 支付方式
            storePromotionPayment.setPayWay(storeSellerPromotionModel.getPayWay());
            if (oConvertUtils.isNotEmpty(storeSellerPromotionModel.getPayTradeCode())) {
                // 支付交易流水号
                storePromotionPayment.setPayTradeCode(storeSellerPromotionModel.getPayTradeCode());
            }

            // 商家id和name
            storePromotionPayment.setSellerId(storeSellerPromotionModel.getSellerId());
            storePromotionPayment.setSellerName(storeSellerPromotionModel.getSellerName());
            // 订单id和
            storePromotionPayment.setPromId(storeSellerPromotionModel.getId());
            // 单号
            storePromotionPayment.setExtensionCode(storeSellerPromotionModel.getExtensionCode());
            // 线下支付
            storePromotionPayment.setIsOffline(CommonConstant.PAY_OFF_LINE);

            // 支付时间
            storePromotionPayment.setPayTime(new Date());

            // 意向金
            if (storeSellerPromotionModel.getPayType().equals(PaymentConstant.PAY_TYPE_EARNEST)) {
                storePromotionPayment.setPayRmbAmount(payAmount);
                storePromotionPayment.setPayAmount(payAmount);
                // 货币符号
                storePromotionPayment.setCurrencySymbol(PaymentConstant.RMB_SYMBOL);
            }
            // 定金
            if (storeSellerPromotionModel.getPayType().equals(PaymentConstant.PAY_TYPE_DEPOSIT)) {
                // 支付金额
                storePromotionPayment.setPayAmount(payAmount);
                // 推广国家支付币种
                if (PaymentConstant.PROMOTION_COUNTRY.equals(storeSellerPromotionModel.getPayCurency())) {

                    // 支付金额(rmb) = 支付金额 * 汇率
                    storePromotionPayment.setPayRmbAmount(payAmount.multiply(storeSellerPromotion.getExchangeRate()));
                    // 货币符号
                    storePromotionPayment.setCurrencySymbol(storeSellerPromotionModel.getSymbol());
                }
                // rmb支付
                if (PaymentConstant.RMB.equals(storeSellerPromotionModel.getPayCurency())) {
                    // 支付金额(rmb)
                    storePromotionPayment.setPayRmbAmount(payAmount);
                    // 货币符号(¥)
                    storePromotionPayment.setCurrencySymbol(PaymentConstant.RMB_SYMBOL);
                }
            }

            // 支付备注
            storePromotionPayment.setRemark(storeSellerPromotionModel.getPayRemark());
            storePromotionPaymentService.save(storePromotionPayment);
        } catch (Exception e) {
            e.printStackTrace();
            // 手动回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        return Result.ok("支付成功!");
    }

    /**
     * 功能描述:推广支付维护
     *
     * @param storeSellerPromotionModel
     * @return org.jeecg.common.api.vo.Result<?>
     * @Date 2021-07-22 15:04:38
     */
    @AutoLog(value = "商家推广管理-推广支付")
    @Operation(summary = "商家推广管理-推广支付", description = "商家推广管理-推广支付")
    @PostMapping(value = "/promotionPaymentNow")
    @LimitSubmit(key = "promotionPay:%s:#promotionId", limit = 3, needAllWait = true)
    @Transactional
    public Result<?> promotionPaymentNow(@RequestBody StoreSellerPromotionPayModel storeSellerPromotionModel) {
        String promotionId = storeSellerPromotionModel.getId();
        // 根据推广id获取推广信息
        StoreSellerPromotion storeSellerPromotion = storeSellerPromotionService.getById(promotionId);
        // 支付类型
        Integer payType = storeSellerPromotionModel.getPayType();
        // 判断是否RMB支付
        boolean rmbPayFlag = storeSellerPromotionModel.getPayCurency().equals(PaymentConstant.RMB);
        if (oConvertUtils.isEmpty(storeSellerPromotionModel.getPayAmount()) || storeSellerPromotionModel.getPayAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error("支付异常,请输入付款金额!");
        }
        // 此次付款金额
        BigDecimal payAmount = storeSellerPromotionModel.getPayAmount();
        // 此次付款金额RMB
        BigDecimal payRmbAmount;
        // 支付记录
        StorePromotionPayment storePromotionPayment = new StorePromotionPayment();
        // 支付类型
        storePromotionPayment.setPayType(payType);
        // 支付方式
        storePromotionPayment.setPayWay(storeSellerPromotionModel.getPayWay());
        // 商家id和name
        storePromotionPayment.setSellerId(storeSellerPromotionModel.getSellerId());
        storePromotionPayment.setSellerName(storeSellerPromotionModel.getSellerName());
        // 订单id
        storePromotionPayment.setPromId(promotionId);
        // 推广单号
        storePromotionPayment.setExtensionCode(storeSellerPromotionModel.getExtensionCode());
        // 线下支付
        storePromotionPayment.setIsOffline(CommonConstant.PAY_OFF_LINE);
    /*    // 支付时间
        storePromotionPayment.setPayTime(new Date());*/
        // 推广需求
        StoreSellerPromotion sellerPromotionNow = new StoreSellerPromotion();
        sellerPromotionNow.setId(promotionId);
        // 判断当前审核状态
        if (storeSellerPromotion.getApprovedStatus() == ApprovedStatusType.PENDING_REVIEW.getCode()) {
            sellerPromotionNow.setApprovedStatus(ApprovedStatusType.EXAMINATION_PASSED.getCode());
        }
        // 获取当前已付金额
        BigDecimal paymentAmount = oConvertUtils.isNotEmpty(storeSellerPromotion.getPaymentAmount()) && storeSellerPromotion.getPaymentAmount().compareTo(BigDecimal.ZERO) > 0 ? storeSellerPromotion.getPaymentAmount() : BigDecimal.ZERO;
        // 获取意向金
        BigDecimal intentionAmount = oConvertUtils.isNotEmpty(storeSellerPromotion.getIntentionAmount()) && storeSellerPromotion.getIntentionAmount().compareTo(BigDecimal.ZERO) > 0 ? storeSellerPromotion.getIntentionAmount() : BigDecimal.ZERO;
        // 获取退款金额
        BigDecimal refundAmount = oConvertUtils.isNotEmpty(storeSellerPromotion.getRefundAmount()) && storeSellerPromotion.getRefundAmount().compareTo(BigDecimal.ZERO) > 0 ? storeSellerPromotion.getRefundAmount() : BigDecimal.ZERO;
        // 当前实付金额
        BigDecimal paymentRmbNow = paymentAmount.add(intentionAmount).subtract(refundAmount);
        // 获取当前订单总额
        BigDecimal totalRmbAmount = oConvertUtils.isNotEmpty(storeSellerPromotion.getTotalRmbAmount()) && storeSellerPromotion.getTotalRmbAmount().compareTo(BigDecimal.ZERO) > 0 ? storeSellerPromotion.getTotalRmbAmount() : BigDecimal.ZERO;
        if (oConvertUtils.isEmpty(storeSellerPromotion.getExchangeRate()) || storeSellerPromotion.getExchangeRate().compareTo(BigDecimal.ZERO) == 0) {
            return Result.error("支付异常,请先设置汇率!");
        }
        BigDecimal exchangeRate = storeSellerPromotion.getExchangeRate();
        if (totalRmbAmount.compareTo(BigDecimal.ZERO) == 0) {
            return Result.error("支付异常,请先设置订单总额!");
        }
        // 此次付款金额RMB
        if (rmbPayFlag) {
            payRmbAmount = payAmount;
        } else {
            payRmbAmount = payAmount.multiply(exchangeRate).setScale(2, RoundingMode.DOWN);
        }
        // 判断 订单总额 < 实付金额+此次付款金额
        if (totalRmbAmount.intValue() < (paymentRmbNow.add(payRmbAmount).intValue())) {
            return Result.error("支付金额不能超过待付金额!");
        }
        sellerPromotionNow.setPayStatus(PayStatusEnum.DOWNPAYMENT.getCode());
        // 全款
        if (payType.equals(PaymentConstant.PAY_TYPE_ALL)) {
            payRmbAmount = storeSellerPromotionModel.getPayableAmount();
            if (totalRmbAmount.compareTo(paymentRmbNow.add(payRmbAmount)) == 0) {
                sellerPromotionNow.setPayStatus(PayStatusEnum.PAIDINFULL.getCode());
                // 匹配成功
                sellerPromotionNow.setApprovedStatus(ApprovedStatusType.MATCH_SUCCESSFULLY.getCode());
                // 判断产品状态,修改推广状态
                Integer promStatus = storePromotionGoodsCelebrityService.checkPromStatus(sellerPromotionNow);
                sellerPromotionNow.setPromStatus(promStatus);
            } else {
                return Result.error("全款金额错误，请重新支付!");
            }
        }
        if (totalRmbAmount.compareTo(paymentRmbNow.add(payRmbAmount)) == 0) {
            sellerPromotionNow.setPayStatus(PayStatusEnum.PAIDINFULL.getCode());
            // 匹配成功
            sellerPromotionNow.setApprovedStatus(ApprovedStatusType.MATCH_SUCCESSFULLY.getCode());
            // 判断产品状态,修改推广状态
            Integer promStatus = storePromotionGoodsCelebrityService.checkPromStatus(sellerPromotionNow);
            sellerPromotionNow.setPromStatus(promStatus);
        }
        // 支付金额
        storePromotionPayment.setPayAmount(payAmount);
        // 判断当前支付类型
        if (rmbPayFlag) {
            // 支付金额(rmb)
            storePromotionPayment.setPayRmbAmount(payAmount);
            // 货币符号(¥)
            storePromotionPayment.setCurrencySymbol(PaymentConstant.RMB_SYMBOL);
        } else {
            // 支付金额(rmb)
            storePromotionPayment.setPayRmbAmount(payRmbAmount);
            // 货币符号
            storePromotionPayment.setCurrencySymbol(storeSellerPromotionModel.getSymbol());
        }
        // 设置当前已付金额
        System.err.println("设置当前已付金额");
        System.err.println(payRmbAmount.add(paymentAmount));
        sellerPromotionNow.setPaymentAmount(payRmbAmount.add(paymentAmount));
        // 支付时间
        if (oConvertUtils.isEmpty(storeSellerPromotionModel.getConsumeDate())) {
            storePromotionPayment.setConsumeDate(new Date());
            storePromotionPayment.setPayTime(new Date());
        } else {
            storePromotionPayment.setConsumeDate(storeSellerPromotionModel.getConsumeDate());
            storePromotionPayment.setPayTime(storeSellerPromotionModel.getConsumeDate());
        }
        // 支付备注
        storePromotionPayment.setRemark(storeSellerPromotionModel.getPayRemark());
        try {
            storePromotionPaymentService.save(storePromotionPayment);
            storeSellerPromotionService.updateById(sellerPromotionNow);
        } catch (Exception e) {
            e.printStackTrace();
            // 手动回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return Result.ok("支付成功!");
    }

    /**
     * 功能描述:推广支付维护
     *
     * @param storeSellerPromotionModel
     * @return org.jeecg.common.api.vo.Result<?>
     * @Date 2021-07-22 15:04:38
     */
    @AutoLog(value = "商家推广管理-推广支付")
    @Operation(summary = "商家推广管理-推广支付", description = "商家推广管理-推广支付")
    @PostMapping(value = "/promotionPaymentNew")
    @Transactional
    public Result<?> promotionPaymentNew(@RequestBody StoreSellerPromotionPayModel storeSellerPromotionModel) {
        String promId = storeSellerPromotionModel.getId();
        // 根据推广id获取推广信息
        StoreSellerPromotion storeSellerPromotion = storeSellerPromotionService.getById(promId);
        // 支付类型
        Integer payType = storeSellerPromotionModel.getPayType();
        // 判断是否RMB支付
        boolean rmbPayFlag = storeSellerPromotionModel.getPayCurency().equals(PaymentConstant.RMB);
        if (oConvertUtils.isEmpty(storeSellerPromotionModel.getPayAmount()) || storeSellerPromotionModel.getPayAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error("支付异常,请输入付款金额!");
        }
        // 此次付款金额
        BigDecimal payAmount = storeSellerPromotionModel.getPayAmount();
        // 此次付款金额RMB
        BigDecimal payRmbAmount;
        // 支付记录
        StorePromotionPayment storePromotionPayment = new StorePromotionPayment();
        // 支付类型
        storePromotionPayment.setPayType(payType);
        // 支付方式
        storePromotionPayment.setPayWay(storeSellerPromotionModel.getPayWay());
        // 商家id和name
        storePromotionPayment.setSellerId(storeSellerPromotionModel.getSellerId());
        storePromotionPayment.setSellerName(storeSellerPromotionModel.getSellerName());
        // 订单id
        storePromotionPayment.setPromId(promId);
        // 推广单号
        storePromotionPayment.setExtensionCode(storeSellerPromotionModel.getExtensionCode());
        // 线下支付
        storePromotionPayment.setIsOffline(CommonConstant.PAY_OFF_LINE);
    /*    // 支付时间
        storePromotionPayment.setPayTime(new Date());*/
        // 推广需求
        StoreSellerPromotion sellerPromotionNow = new StoreSellerPromotion();
        sellerPromotionNow.setId(promId);
        // 判断当前审核状态
        if (storeSellerPromotion.getApprovedStatus() == ApprovedStatusType.PENDING_REVIEW.getCode()) {
            sellerPromotionNow.setApprovedStatus(ApprovedStatusType.EXAMINATION_PASSED.getCode());
        }
        // 获取当前已付金额
        BigDecimal paymentAmount = oConvertUtils.isNotEmpty(storeSellerPromotion.getPaymentAmount()) && storeSellerPromotion.getPaymentAmount().compareTo(BigDecimal.ZERO) > 0 ? storeSellerPromotion.getPaymentAmount() : BigDecimal.ZERO;
        // 获取意向金
        BigDecimal intentionAmount = oConvertUtils.isNotEmpty(storeSellerPromotion.getIntentionAmount()) && storeSellerPromotion.getIntentionAmount().compareTo(BigDecimal.ZERO) > 0 ? storeSellerPromotion.getIntentionAmount() : BigDecimal.ZERO;
        // 获取退款金额
        BigDecimal refundAmount = oConvertUtils.isNotEmpty(storeSellerPromotion.getRefundAmount()) && storeSellerPromotion.getRefundAmount().compareTo(BigDecimal.ZERO) > 0 ? storeSellerPromotion.getRefundAmount() : BigDecimal.ZERO;
        // 当前实付金额
        BigDecimal paymentRmbNow = paymentAmount.add(intentionAmount).subtract(refundAmount);
        // 获取当前订单总额
        BigDecimal totalRmbAmount = oConvertUtils.isNotEmpty(storeSellerPromotion.getTotalRmbAmount()) && storeSellerPromotion.getTotalRmbAmount().compareTo(BigDecimal.ZERO) > 0 ? storeSellerPromotion.getTotalRmbAmount() : BigDecimal.ZERO;
        // 意向金
        if (payType.equals(PaymentConstant.PAY_TYPE_EARNEST)) {
            if (!rmbPayFlag) {
                return Result.error("支付异常,请检查支付类型!");
            }
            // 判断当前是否已付全款
            if (totalRmbAmount.compareTo(BigDecimal.ZERO) > 0) {
                if (totalRmbAmount.compareTo(paymentRmbNow) == 0) {
                    return Result.error("支付异常,当前推广已经支付全款!");
                }
            }
            sellerPromotionNow.setPayStatus(PayStatusEnum.DOWNPAYMENT.getCode());
            sellerPromotionNow.setIntentionAmount(payAmount.add(intentionAmount));
            storePromotionPayment.setPayRmbAmount(payAmount);
            storePromotionPayment.setPayAmount(payAmount);
            storePromotionPayment.setCurrencySymbol(PaymentConstant.RMB_SYMBOL);
        } else {
            if (oConvertUtils.isEmpty(storeSellerPromotion.getExchangeRate()) || storeSellerPromotion.getExchangeRate().compareTo(BigDecimal.ZERO) == 0) {
                return Result.error("支付异常,请先设置汇率!");
            }
            BigDecimal exchangeRate = storeSellerPromotion.getExchangeRate();
            if (totalRmbAmount.compareTo(BigDecimal.ZERO) == 0) {
                return Result.error("支付异常,请先设置订单总额!");
            }
            // 此次付款金额RMB
            if (rmbPayFlag) {
                payRmbAmount = payAmount;
            } else {
                payRmbAmount = payAmount.multiply(exchangeRate).setScale(2, RoundingMode.DOWN);
            }
            // 判断 订单总额 < 实付金额+此次付款金额
            if (totalRmbAmount.compareTo(paymentRmbNow.add(payRmbAmount)) < 0) {
                return Result.error("支付金额不能超过待付金额!");
            }
            // 定金
            if (payType.equals(PaymentConstant.PAY_TYPE_DEPOSIT)) {
                if (totalRmbAmount.compareTo(paymentRmbNow.add(payRmbAmount)) > 0) {
                    sellerPromotionNow.setPayStatus(PayStatusEnum.DOWNPAYMENT.getCode());
                } else {
                    return Result.error("支付金额不能超过待付金额!");
                }
            }
            // 尾款
            if (payType.equals(PaymentConstant.PAY_TYPE_FINAL)) {
                payRmbAmount = storeSellerPromotionModel.getPayableAmount();
                if (totalRmbAmount.compareTo(paymentRmbNow.add(payRmbAmount)) == 0) {
                    // 已付全款
                    sellerPromotionNow.setPayStatus(PayStatusEnum.PAIDINFULL.getCode());
                    // 匹配成功
                    sellerPromotionNow.setApprovedStatus(ApprovedStatusType.MATCH_SUCCESSFULLY.getCode());
                } else {
                    return Result.error("尾款金额错误，请重新支付!");
                }
            }
            // 全款
            if (payType.equals(PaymentConstant.PAY_TYPE_ALL)) {
                payRmbAmount = storeSellerPromotionModel.getPayableAmount();
                if (totalRmbAmount.compareTo(paymentRmbNow.add(payRmbAmount)) == 0) {
                    sellerPromotionNow.setPayStatus(PayStatusEnum.PAIDINFULL.getCode());
                    // 匹配成功
                    sellerPromotionNow.setApprovedStatus(ApprovedStatusType.MATCH_SUCCESSFULLY.getCode());
                    // 判断产品状态,修改推广状态
                    Integer promStatus = storePromotionGoodsCelebrityService.checkPromStatus(sellerPromotionNow);
                    sellerPromotionNow.setPromStatus(promStatus);
                } else {
                    return Result.error("全款金额错误，请重新支付!");
                }
            }
            // 支付金额
            storePromotionPayment.setPayAmount(payAmount);
            // 判断当前支付类型
            if (rmbPayFlag) {
                // 支付金额(rmb)
                storePromotionPayment.setPayRmbAmount(payAmount);
                // 货币符号(¥)
                storePromotionPayment.setCurrencySymbol(PaymentConstant.RMB_SYMBOL);
            } else {
                // 支付金额(rmb)
                storePromotionPayment.setPayRmbAmount(payRmbAmount);
                // 货币符号
                storePromotionPayment.setCurrencySymbol(storeSellerPromotionModel.getSymbol());
            }
            // 设置当前已付金额
            System.err.println("设置当前已付金额");
            System.err.println(payRmbAmount.add(paymentAmount));
            sellerPromotionNow.setPaymentAmount(payRmbAmount.add(paymentAmount));
        }
        // 支付时间
        if (oConvertUtils.isEmpty(storeSellerPromotionModel.getConsumeDate())) {
            storePromotionPayment.setConsumeDate(new Date());
            storePromotionPayment.setPayTime(new Date());
        } else {
            storePromotionPayment.setConsumeDate(storeSellerPromotionModel.getConsumeDate());
            storePromotionPayment.setPayTime(storeSellerPromotionModel.getConsumeDate());
        }
        // 支付备注
        storePromotionPayment.setRemark(storeSellerPromotionModel.getPayRemark());

        List<StorePromotionGoods> promotionGoodsListNew = new ArrayList<>();

        if (storeSellerPromotion.getPayStatus() == PayStatusEnum.PAIDINFULL.getCode()) {
            // 获取当前推广下所有的产品
            List<StorePromotionGoods> promotionGoodsList = storePromotionGoodsService.getPromGoodsList(promId);
            // 获取所有产品id，获取产品下的所有网红
            List<String> promGoodsIds = promotionGoodsList.stream().map(StorePromotionGoods::getId).distinct().collect(Collectors.toList());
            Map<String, List<StorePromotionGoodsCelebrity>> map = storePromotionGoodsCelebrityService.getGoodsCelebrityListNew(promGoodsIds, null, null, YesNoStatus.YES.getCode(), YesNoStatus.YES.getCode());
            // 判断产品状态
            JSONObject json = storePromotionGoodsCelebrityService.checkGoodsStatus(promotionGoodsList, map);
            promotionGoodsListNew = json.getJSONArray(CommonConstant.PROMOTION_GOODS_LIST_NEW).toJavaList(StorePromotionGoods.class);
            // 判断推广状态
            StoreSellerPromotion sellerPromotionOld = storeSellerPromotionService.checkPromStatusTypeNew(promId, promotionGoodsListNew, json, promGoodsIds, PayStatusEnum.PAIDINFULL.getCode());
            Integer promStatusNow = sellerPromotionOld.getPromStatus();
            sellerPromotionNow.setPromStatus(promStatusNow);
        }
        try {
            if (!promotionGoodsListNew.isEmpty()) {
                storePromotionGoodsService.updateBatchById(promotionGoodsListNew);
            }
            storePromotionPaymentService.save(storePromotionPayment);
            storeSellerPromotionService.updateById(sellerPromotionNow);
        } catch (Exception e) {
            e.printStackTrace();
            // 手动回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return Result.ok("支付成功!");
    }

    /**
     * 审核推广信息
     *
     * @param storeSellerPromotion
     * @return
     */
    @AutoLog(value = "商家推广管理-审核推广信息")
    @Operation(summary = "商家推广管理-审核推广信息", description = "商家推广管理-审核推广信息")
    @PutMapping(value = "/approvedStoreSellerPromotion")
    public Result<?> approvedStoreSellerPromotion(@RequestBody StoreSellerPromotion storeSellerPromotion) {
        storeSellerPromotion.setUpdateBy(getUserNameByToken());
        storeSellerPromotion.setUpdateTime(new Date());
        storeSellerPromotion.setApprovedUserId(getUserIdByToken());
        if (storeSellerPromotion.getApprovedStatus() == 1) {
            storeSellerPromotion.setApprovedDate(new Date());
            storeSellerPromotion.setApprovedFailReason("");
        }
        storeSellerPromotionService.updateById(storeSellerPromotion);
        return Result.ok("编辑成功!");
    }

    @AutoLog(value = "根据推广单号查询列表")
    @Operation(summary = "根据推广单号查询列表", description = "根据推广单号查询列表")
    @GetMapping(value = "/queryByExtensionCode")
    public Result<?> queryByExtensionCode(@RequestParam(name = "code", required = false) String code) {
        List<StoreSellerPromotion> promotionList = storeSellerPromotionService.queryByExtensionCode(code);
        return Result.ok(promotionList);
    }

    private String getCode() {
        String extensionCode;
        String date = new SimpleDateFormat("yyMMdd", Locale.CHINESE).format(new Date());
        if (redisUtil.hasKey(date)) {
            long num = Long.parseLong(String.valueOf(redisUtil.get(date)));
            extensionCode = oConvertUtils.getSequence(num + 1);
            redisUtil.set(date, extensionCode);
        } else {
            extensionCode = "001";
            redisUtil.set(date, Integer.parseInt(extensionCode));
        }
        return "T" + date + extensionCode;
    }

    /**
     * 功能描述:修改审核状态
     *
     * @param id
     * @param status
     * @return
     * @Date 2021-06-17 16:31:35
     */
    @AutoLog(value = "修改审核状态")
    @Operation(summary = "修改审核状态", description = "修改审核状态")
    @GetMapping(value = "/checkStatus")
    public Result<?> checkStatus(@RequestParam(name = "id", required = true) String id,
                                 @RequestParam(name = "status", required = true) String status) {
        StoreSellerPromotion sellerPromotion = storeSellerPromotionService.getById(id);
        if (!sellerPromotion.getPromStatus().equals(ApprovedStatusType.MATCH_SUCCESSFULLY.getCode())) {
            LambdaUpdateWrapper<StoreSellerPromotion> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(StoreSellerPromotion::getId, id);
            lambdaUpdateWrapper.set(StoreSellerPromotion::getApprovedStatus, status);
            storeSellerPromotionService.update(lambdaUpdateWrapper);
        }
        return Result.ok("修改成功！");
    }

    /**
     * 功能描述:推广统计
     *
     * @return org.jeecg.common.api.vo.Result<?>
     * @Date 2021-07-13 15:32:12
     */
    @AutoLog(value = "推广统计")
    @Operation(summary = "推广统计", description = "推广统计")
    @GetMapping(value = "/getPromotionStatistics")
    public Result<?> getPromotionStatistics(PromotionStatisticsModel promotionStatisticsModel) {
        return Result.ok(storeSellerPromotionService.getPromotionStatistics(promotionStatisticsModel));
    }

    /**
     * 功能描述:推广人员统计
     *
     * @return org.jeecg.common.api.vo.Result<?>
     * @Date 2021-07-13 15:32:12
     */
    @AutoLog(value = "推广人员统计")
    @Operation(summary = "推广人员统计", description = "推广人员统计")
    @GetMapping(value = "/getExtensionStaffStatistics")
    public Result<?> getExtensionStaffStatistics(PromotionStatisticsModel promotionStatisticsModel) {
        if (promotionStatisticsModel.getDataType() == YesNoStatus.YES.getCode()) {
            return Result.ok(storeSellerPromotionService.getExtensionStaffStatistics(promotionStatisticsModel));
        } else {
            return Result.ok(storeSellerPromotionService.getPersonChargeStatistics(promotionStatisticsModel));
        }
    }

    /**
     * 功能描述:推广统计明细
     *
     * @return org.jeecg.common.api.vo.Result<?>
     * @Date 2021-07-13 15:32:12
     */
    @AutoLog(value = "推广统计明细")
    @Operation(summary = "推广统计明细", description = "推广统计明细")
    @GetMapping(value = "/getPromotionStatisticsDetail")
    public Result<?> getPromotionStatisticsDetail(StoreSellerPromotionModel storeSellerPromotionModel,
                                                  @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<StoreSellerPromotionModel> page = new Page<>(pageNo, pageSize);
        IPage<StoreSellerPromotionModel> pageList = storeSellerPromotionService.getPromotionStatisticsDetail(page, storeSellerPromotionModel);
        return Result.ok(pageList);
    }

    /**
     * 分页列表查询 推广支付统计详情
     *
     * @param storeSellerPromotionModel
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "推广支付统计详情-分页列表查询")
    @Operation(summary = "推广支付统计详情-分页列表查询", description = "推广支付统计详情-分页列表查询")
    @GetMapping(value = "/getPaymentStatisticsDetail")
    public Result<?> getPaymentStatisticsDetail(StoreSellerPromotionModel storeSellerPromotionModel,
                                                @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                HttpServletRequest req) {

        if (oConvertUtils.isNotEmpty(storeSellerPromotionModel.getEndPayDate())) {
            // 往后加一天
            String dateStrAddOneDay = DateUtils.dateStrAddOneDay(storeSellerPromotionModel.getEndPayDate());
            storeSellerPromotionModel.setEndPayDate(dateStrAddOneDay);
        }
        Page<StorePromotionPayment> page = new Page<>(pageNo, pageSize);
        IPage<StoreSellerPromotionModel> pageList = null;
        if (storeSellerPromotionModel.getParams().equals(CommonConstant.PROMOTION_EXCEED_TIME_TYPE)) {
            pageList = storeSellerPromotionService.getPaymentStatisticsDetailExceedTime(page, storeSellerPromotionModel);
        } else {
            pageList = storeSellerPromotionService.getPaymentStatisticsDetail(page, storeSellerPromotionModel);
        }
        return Result.ok(pageList);
    }


    /**
     * 导出excel
     *
     * @param request
     */
    @RequestMapping(value = "/exportXlsAll")
    public String exportXlsAll(HttpServletRequest request, HttpServletResponse response, StoreSellerPromotionModel storeSellerPromotionModel) {
        if (oConvertUtils.isNotEmpty(storeSellerPromotionModel.getEndPayDate())) {
            // 往后加一天
            String dateStrAddOneDay = DateUtils.dateStrAddOneDay(storeSellerPromotionModel.getEndPayDate());
            storeSellerPromotionModel.setEndPayDate(dateStrAddOneDay);
        }

        // 多个map，对应了多个sheet
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();

        // 仅意向金
        storeSellerPromotionModel.setParams(CommonConstant.PROMOTION_INTENTION_TYPE);
        List<StoreSellerPromotionModel> intentions = storeSellerPromotionService.getPaymentStatisticsDetailByXls(storeSellerPromotionModel);
        Map<String, Object> intentionMap = new HashMap<String, Object>();
        intentionMap.put("title", getExportParams("仅意向金"));// 表格title
        intentionMap.put("entity", StoreSellerPromotionModel.class);// 表格对应实体
        intentionMap.put("data", calculationAmount(intentions));
        listMap.add(intentionMap);

        // 意向金加全款
        storeSellerPromotionModel.setParams(CommonConstant.PROMOTION_INTENTION_AND_FULL_PAYMENT_TYPE);
        List<StoreSellerPromotionModel> intentionAndFullPayments = storeSellerPromotionService.getPaymentStatisticsDetailByXls(storeSellerPromotionModel);
        Map<String, Object> intentionAndFullPaymentMap = new HashMap<String, Object>();
        intentionAndFullPaymentMap.put("title", getExportParams("意向金加全款"));// 表格title
        intentionAndFullPaymentMap.put("entity", StoreSellerPromotionModel.class);// 表格对应实体
        intentionAndFullPaymentMap.put("data", calculationAmount(intentionAndFullPayments));
        listMap.add(intentionAndFullPaymentMap);

        // 退款
        storeSellerPromotionModel.setParams(CommonConstant.PROMOTION_REFUND_TYPE);
        List<StoreSellerPromotionModel> refunds = storeSellerPromotionService.getPaymentStatisticsDetailByXls(storeSellerPromotionModel);
        Map<String, Object> refundMap = new HashMap<String, Object>();
        refundMap.put("title", getExportParams("退款"));// 表格title
        refundMap.put("entity", StoreSellerPromotionModel.class);// 表格对应实体
        refundMap.put("data", calculationAmount(refunds));
        listMap.add(refundMap);

        // 全款
        storeSellerPromotionModel.setParams(CommonConstant.PROMOTION_FULL_PAYMENT_TYPE);
        List<StoreSellerPromotionModel> fullPayments = storeSellerPromotionService.getPaymentStatisticsDetailByXls(storeSellerPromotionModel);
        Map<String, Object> fullPaymentMap = new HashMap<String, Object>();
        fullPaymentMap.put("title", getExportParams("全款"));// 表格title
        fullPaymentMap.put("entity", StoreSellerPromotionModel.class);// 表格对应实体
        fullPaymentMap.put("data", calculationAmount(fullPayments));
        listMap.add(fullPaymentMap);

        // 超期
        storeSellerPromotionModel.setParams(CommonConstant.PROMOTION_EXCEED_TIME_TYPE);
        List<StoreSellerPromotionModel> exceedTypes = storeSellerPromotionService.getPaymentStatisticsDetailExceedTimeByXls(storeSellerPromotionModel);
        Map<String, Object> exceedTypeMap = new HashMap<String, Object>();
        exceedTypeMap.put("title", getExportParams("推广超期"));// 表格title
        exceedTypeMap.put("entity", StoreSellerPromotionModel.class);// 表格对应实体
        exceedTypeMap.put("data", calculationAmount(exceedTypes));
        listMap.add(exceedTypeMap);
        Workbook workbook = ExcelExportUtil.exportExcel(listMap, ExcelType.XSSF);

        // 设置excel的文件名称
        String excelName = "推广支付统计";
        // 重置响应对象
        response.reset();
        // 当前日期，用于导出文件名称
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateStr = "[" + excelName + "-" + sdf.format(new Date()) + "]";
        // 指定下载的文件名--设置响应头
        response.setHeader("Content-Disposition", "attachment;filename=" + dateStr + ".xls");
        response.setContentType("application/vnd.ms-excel;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        // 写出数据输出流到页面
        try {
            OutputStream output = response.getOutputStream();
            BufferedOutputStream bufferedOutPut = new BufferedOutputStream(output);
            workbook.write(bufferedOutPut);
            bufferedOutPut.flush();
            bufferedOutPut.close();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 导出参数
    public static ExportParams getExportParams(String name) {
        // 表格名称,sheet名称,导出版本
        return new ExportParams(name, name, ExcelType.XSSF);
    }


    /**
     * 获取对象ID
     *
     * @return
     */
    private String getIds(StoreSellerPromotionModel item) {
        try {
            return PropertyUtils.getProperty(item, "id").toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 计算推广支付的待付和已付
     *
     * @param list
     * @return
     */
    private List<StoreSellerPromotionModel> calculationAmount(List<StoreSellerPromotionModel> list) {
        for (StoreSellerPromotionModel sellerPromotionModel : list) {
            if (oConvertUtils.isNotEmpty(sellerPromotionModel.getTotalRmbAmount())) {


                BigDecimal paymentAmount = new BigDecimal("0");
                BigDecimal payableAmount = new BigDecimal("0");


                // 实际支付金额=支付金额+意向金-退款金额
                if (oConvertUtils.isNotEmpty(sellerPromotionModel.getPaymentAmount())) {

                    paymentAmount = sellerPromotionModel.getPaymentAmount();
                }

                // 加意向金
                if (oConvertUtils.isNotEmpty(sellerPromotionModel.getIntentionAmount())) {
                    paymentAmount = paymentAmount.add(sellerPromotionModel.getIntentionAmount());
                }

//                // 减退款
//                if (oConvertUtils.isNotEmpty(sellerPromotionModel.getRefundAmount())) {
//                    paymentAmount = paymentAmount.subtract(sellerPromotionModel.getRefundAmount());
//                }
                // 实付
                sellerPromotionModel.setPaymentAmount(paymentAmount);

                //                // 减退款
//                if (oConvertUtils.isNotEmpty(sellerPromotionModel.getRefundAmount())) {
//                    paymentAmount = paymentAmount.subtract(sellerPromotionModel.getRefundAmount());
//                }

                // 总金额 - 实付
                payableAmount = sellerPromotionModel.getTotalRmbAmount().subtract(paymentAmount);

                // 待付+退款
                if (oConvertUtils.isNotEmpty(sellerPromotionModel.getRefundAmount())) {
                    payableAmount = payableAmount.add(sellerPromotionModel.getRefundAmount());
                }
                // 待付
                sellerPromotionModel.setPayableAmount(payableAmount);

            }

        }
        return list;
    }

    /**
     * 编辑推广状态
     *
     * @return
     */
    @AutoLog(value = "商家推广管理-编辑推广状态")
    @Operation(summary = "商家推广管理-编辑推广状态", description = "商家推广管理-编辑推广状态")
    @GetMapping(value = "/editPromotionStatus")
    public Result<?> editPromotionStatus(StorePromotionGoodsModel promotionGoodsModel) {
        Integer promotionStatus = promotionGoodsModel.getPromotionStatus();
        Integer status = promotionGoodsModel.getPromStatus();
        switch (promotionStatus) {
            case 0:
                // 推广状态
                String promId = promotionGoodsModel.getPromId();
                // 查询当前推广
                StoreSellerPromotion sellerPromotion = storeSellerPromotionService.getById(promId);
                if (sellerPromotion == null) {
                    return Result.error("未查询到当前推广！");
                }
                // 判断当前推广是否已结束
                Integer promStatusOld = sellerPromotion.getPromStatus();
                if (promStatusOld != PromotionGoodsType.FINISHEDABNORMAL.getCode() && promStatusOld != PromotionGoodsType.FINISHEDNORMAL.getCode()) {
                    return Result.error("只有已结束状态可以修改！");
                }
                StoreSellerPromotion sellerPromotionNew = new StoreSellerPromotion();
                sellerPromotionNew.setId(promId);
                sellerPromotionNew.setPromStatus(status);
                storeSellerPromotionService.updateById(sellerPromotionNew);
                break;
            case 1:
                // 产品状态
                String promoGoodsId = promotionGoodsModel.getPromoGoodsId();
                // 查询当前推广商品
                StorePromotionGoods storePromotionGoods = storePromotionGoodsService.getById(promoGoodsId);
                if (storePromotionGoods == null) {
                    return Result.error("未查询到当前推广商品！");
                }
                // 判断当前推广是否已结束
                Integer goodsStatus = storePromotionGoods.getGoodsStatus();
                if (goodsStatus != PromotionGoodsType.FINISHEDABNORMAL.getCode() && goodsStatus != PromotionGoodsType.FINISHEDNORMAL.getCode()) {
                    return Result.error("只有已结束状态可以修改！");
                }
                StorePromotionGoods promotionGoods = new StorePromotionGoods();
                promotionGoods.setId(promoGoodsId);
                promotionGoods.setGoodsStatus(status);
                storePromotionGoodsService.updateById(promotionGoods);
                break;
            default:
                // 网红状态
                String correId = promotionGoodsModel.getCorreId();
                // 查询当前推广网红
                StorePromotionGoodsCelebrity goodsCelebrity = storePromotionGoodsCelebrityService.getById(correId);
                if (goodsCelebrity == null) {
                    return Result.error("未查询到当前推广网红！");
                }
                // 判断当前推广是否已结束
                Integer celebrityStatus = goodsCelebrity.getCelebrityPromStatus();
                if (celebrityStatus != 3 && celebrityStatus != -1) {
                    return Result.error("只有已结束状态可以修改！");
                }
                StorePromotionGoodsCelebrity goodsCelebrityNew = new StorePromotionGoodsCelebrity();
                goodsCelebrityNew.setId(correId);
                goodsCelebrityNew.setCelebrityPromStatus(status);
                storePromotionGoodsCelebrityService.updateById(goodsCelebrityNew);
        }
        return Result.ok("编辑成功！");
    }


    /**
     * 更新商务人员
     *
     * @return
     */
    @AutoLog(value = "商家推广管理-更新商务人员")
    @Operation(summary = "商家推广管理-更新商务人员", description = "商家推广管理-更新商务人员")
    @GetMapping(value = "/updateCounselorUser")
    public Result<?> updateCounselorUser(@RequestParam(name = "counselorUserId", required = true) String counselorUserId,
                                         @RequestParam(name = "sellerCounselorId", required = false) String sellerCounselorId,
                                         @RequestParam(name = "sellerPromotionIds", required = true) String sellerPromotionIds) {
        List<String> promotionIds = Arrays.asList(sellerPromotionIds.split(","));
        SysUser sysUser = sysUserService.getById(counselorUserId);
        LambdaUpdateWrapper<StoreSellerPromotion> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(StoreSellerPromotion::getId, promotionIds);
        updateWrapper.set(StoreSellerPromotion::getCounselorUserId, counselorUserId);
        updateWrapper.set(StoreSellerPromotion::getCounselorUserName, sysUser.getRealname());
        if (oConvertUtils.isNotEmpty(sellerCounselorId)) {
            StoreUserCounselor storeUserCounselor = storeUserCounselorService.getById(sellerCounselorId);
            updateWrapper.set(StoreSellerPromotion::getSellerCounselorId, sellerCounselorId);
            updateWrapper.set(StoreSellerPromotion::getSellerCounselorName, storeUserCounselor.getUsername());
        }
        storeSellerPromotionService.update(updateWrapper);
        return Result.ok("修改成功");
    }

    /**
     * 添加
     *
     * @param storeSellerPromotionModel
     * @return
     */
    @AutoLog(value = "商家推广管理-商家添加")
    @Operation(summary = "商家推广管理-商家添加", description = "商家推广管理-商家添加")
    @PostMapping(value = "/sellerAdd")
    public Result<?> sellerAdd(@RequestBody StoreSellerPromotionModel storeSellerPromotionModel) {

        String userId = getUserIdByToken();
        SysUser sysUser = sysUserService.getById(userId);
        if (!oConvertUtils.isAdmin(getUserNameByToken())) {
            Integer userType = sysUser.getUserType();
            List<Integer> userTypes = new ArrayList<>();
            userTypes.add(UserType.SELLER.getCode());
            userTypes.add(UserType.BUYERS.getCode());
            userTypes.add(UserType.BUSINESS_PERSONNEL.getCode());
            if (!userTypes.contains(userType)) {
                return Result.error("当前用户无法新增需求");
            }
        }
        String promPlatform = storeSellerPromotionModel.getPromPlatform();
        if (oConvertUtils.isEmpty(promPlatform)) {
            return Result.error("请选择推广平台");
        }
        String productUrl = storeSellerPromotionModel.getProductUrl();
        if (oConvertUtils.isEmpty(productUrl)) {
            return Result.error("请输入产品链接");
        }
        String contactInfo = storeSellerPromotionModel.getContactInfo();
        if (oConvertUtils.isEmpty(contactInfo)) {
            return Result.error("请输入联系方式");
        }
//        String code = getCode();
        // 设置推广类型 0：商务顾问添加；1：商家添加
        storeSellerPromotionModel.setPromType(1);
//        storeSellerPromotionModel.setExtensionCode(code);
        storeSellerPromotionModel.setSellerId(userId);
        storeSellerPromotionModel.setSellerName(sysUser.getUsername());
        storeSellerPromotionModel.setSellerCounselorId(sysUser.getSellerCounselorId());
        storeSellerPromotionModel.setSellerCounselorName(sysUser.getSellerCounselorName());
        storeSellerPromotionModel.setCounselorUserId(sysUser.getCounselorUserId());
        storeSellerPromotionModel.setCounselorUserName(sysUser.getCounselorUserName());
        storeSellerPromotionService.save(storeSellerPromotionModel);
        storeSellerPromotionService.sendWeChatWorkNotification(storeSellerPromotionModel);
        return Result.ok("添加成功！");
    }

    /**
     * 添加
     *
     * @param storeSellerPromotionModel
     * @return
     */
    @AutoLog(value = "商家推广管理-商家编辑")
    @Operation(summary = "商家推广管理-商家编辑", description = "商家推广管理-商家编辑")
    @PutMapping(value = "/sellerEdit")
    public Result<?> sellerEdit(@RequestBody StoreSellerPromotionModel storeSellerPromotionModel) {

        String promotionId = storeSellerPromotionModel.getId();
        if (oConvertUtils.isEmpty(promotionId))
            return Result.error("未获取到推广信息");

        StoreSellerPromotion storeSellerPromotion = storeSellerPromotionService.getById(promotionId);
        if (storeSellerPromotion == null) {
            return Result.error("未获取到推广信息");
        }

        String userId = getUserIdByToken();
        SysUser sysUser = sysUserService.getById(userId);
        if (!oConvertUtils.isAdmin(getUserNameByToken())) {
            Integer userType = sysUser.getUserType();
            List<Integer> userTypes = new ArrayList<>();
            userTypes.add(UserType.SELLER.getCode());
            userTypes.add(UserType.BUYERS.getCode());
            userTypes.add(UserType.BUSINESS_PERSONNEL.getCode());
            if (!userTypes.contains(userType)) {
                return Result.error("当前用户无法编辑需求");
            }
        }
        String promPlatform = storeSellerPromotionModel.getPromPlatform();
        if (oConvertUtils.isEmpty(promPlatform)) {
            return Result.error("请选择推广平台");
        }
        String productUrl = storeSellerPromotionModel.getProductUrl();
        if (oConvertUtils.isEmpty(productUrl)) {
            return Result.error("请输入产品链接");
        }
        // 正则校验是否是正常链接，支持http和https协议
        String urlRegex = "^https?://[\\w.-]+(?:\\.[\\w\\.-]+)+[/\\w\\-\\.~:/?#%@\\[\\]!$&'()*+,;=]*$";
        if (!productUrl.matches(urlRegex)) {
            return Result.error("请输入正确的产品链接");
        }

        String contactInfo = storeSellerPromotionModel.getContactInfo();
        if (oConvertUtils.isEmpty(contactInfo)) {
            return Result.error("请输入联系方式");
        }
        // 设置推广类型 0：商务顾问添加；1：商家添加
        storeSellerPromotionModel.setPromType(1);
        storeSellerPromotionService.updateById(storeSellerPromotionModel);
        return Result.ok("编辑成功！");
    }

    /**
     * 功能描述:驳回推广信息
     *
     * @param storeSellerPromotion
     * @return org.jeecg.common.api.vo.Result<?>
     * @Date 2026-02-03
     */
    @AutoLog(value = "商家推广管理-驳回推广信息")
    @Operation(summary = "商家推广管理-驳回推广信息", description = "商家推广管理-驳回推广信息")
    @PutMapping(value = "/rejectPromotion")
    public Result<?> rejectPromotion(@RequestBody StoreSellerPromotion storeSellerPromotion) {
        String id = storeSellerPromotion.getId();
        String approvedFailReason = storeSellerPromotion.getApprovedFailReason();

        if (oConvertUtils.isEmpty(id)) {
            return Result.error("请选择要驳回的推广信息!");
        }

        StoreSellerPromotion promotion = storeSellerPromotionService.getById(id);
        if (promotion == null) {
            return Result.error("未找到对应的推广信息!");
        }

        // 设置驳回状态和驳回原因
        promotion.setApprovedStatus(-1); // 审核状态改为-1（驳回）
        promotion.setApprovedFailReason(oConvertUtils.isEmpty(approvedFailReason) ? "" : approvedFailReason); // 设置驳回原因
        promotion.setUpdateBy(getUserNameByToken());
        promotion.setUpdateTime(new Date());

        storeSellerPromotionService.updateById(promotion);

        return Result.ok("驳回成功!");
    }

}
