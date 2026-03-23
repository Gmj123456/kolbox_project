package org.jeecg.modules.instagram.storecelebrity.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.lark.oapi.Client;
import com.lark.oapi.core.request.RequestOptions;
import com.lark.oapi.core.utils.Jsons;
import com.lark.oapi.service.drive.v1.model.*;
import com.lark.oapi.service.sheets.v3.model.QuerySpreadsheetSheetReq;
import com.lark.oapi.service.sheets.v3.model.QuerySpreadsheetSheetResp;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.ServletOutputStream;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.aspect.annotation.PermissionData;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.SheetConstants;
import org.jeecg.common.constant.SystemConstants;
import org.jeecg.common.constant.enums.AttributeConfirmationStatus;
import org.jeecg.common.constant.enums.UserType;
import org.jeecg.common.constant.enums.YesNoStatus;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.StoreCelebrityPrivateExcelModel;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.RestUtil;
import org.jeecg.common.util.WorkBookUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.feishu.model.FeishuSheetConvertResult;
import org.jeecg.modules.feishu.model.FeishuSheetResponse;
import org.jeecg.modules.feishu.service.IFeishuService;
import org.jeecg.modules.instagram.entity.IgCelebrity;
import org.jeecg.modules.instagram.history.entity.KolHistoryCelebrityKolType;
import org.jeecg.modules.instagram.history.service.IKolHistoryCelebrityKolTypeService;
import org.jeecg.modules.instagram.history.service.IKolHistoryKolTypeService;
import org.jeecg.modules.instagram.service.IIgCelebrityService;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivate;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivateAttributeRelation;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivateCounselor;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivateProduct;
import org.jeecg.modules.instagram.storecelebrity.model.*;
import org.jeecg.modules.instagram.storecelebrity.service.*;
import org.jeecg.modules.instagram.storecountry.entity.StoreCountry;
import org.jeecg.modules.instagram.storecountry.service.IStoreCountryService;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionGoodsCelebrity;
import org.jeecg.modules.instagram.storepromotion.service.IStorePromotionGoodsCelebrityService;
import org.jeecg.modules.instagram.storeseller.storeuser.entity.StoreUserContactEmail;
import org.jeecg.modules.instagram.storeseller.storeuser.service.IStoreUserContactEmailService;
import org.jeecg.modules.instagram.storetags.entity.StoreCelebrityPrivatePersonalTags;
import org.jeecg.modules.instagram.storetags.entity.StorePersonalTags;
import org.jeecg.modules.instagram.storetags.service.IStoreCelebrityPrivatePersonalTagsService;
import org.jeecg.modules.instagram.storetags.service.IStorePersonalTagsService;
import org.jeecg.modules.kol.entity.KolProduct;
import org.jeecg.modules.kol.entity.KolSysUserFeishuSheet;
import org.jeecg.modules.kol.model.TypeData;
import org.jeecg.modules.kol.service.IKolAttributeService;
import org.jeecg.modules.kol.service.IKolCategoryAttributeRelationService;
import org.jeecg.modules.kol.service.IKolProductService;
import org.jeecg.modules.kol.service.IKolSysUserFeishuSheetService;
import org.jeecg.modules.kol.wechatexcel.config.WechatDocConfig;
import org.jeecg.modules.kol.wechatexcel.model.Record;
import org.jeecg.modules.kol.wechatexcel.service.WechatService;
import org.jeecg.modules.system.entity.SysDictItem;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.tiktok.entity.TiktokCelebrity;
import org.jeecg.modules.tiktok.entity.TiktokCelebrityProduct;
import org.jeecg.modules.tiktok.entity.TiktokCelebrityProductCategory;
import org.jeecg.modules.tiktok.service.ITiktokCelebrityProductCategoryService;
import org.jeecg.modules.tiktok.service.ITiktokCelebrityProductService;
import org.jeecg.modules.tiktok.service.ITiktokCelebrityService;
import org.jeecg.modules.youtube.entity.YoutubeCelebrity;
import org.jeecg.modules.youtube.service.IYtCelebrityService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import jakarta.annotation.Resource;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.jeecg.common.util.oConvertUtils.convertEmptyToNull;


/**
 * @Description: 私有网红表
 * @Author: jeecg-boot
 * @Date: 2020-11-11
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "私有网红表")
@RestController
@RequestMapping("/storeCelebrityPrivate/storeCelebrityPrivate")
public class StoreCelebrityPrivateController extends JeecgController<StoreCelebrityPrivate, IStoreCelebrityPrivateService> {

    @Autowired
    private IStoreCelebrityPrivateService storeCelebrityPrivateService;
    @Autowired
    private IStorePromotionGoodsCelebrityService storePromotionGoodsCelebrityService;
    @Autowired
    private IStorePersonalTagsService personalTagsService;
    @Autowired
    private IStoreCelebrityPrivateCounselorService counselorService;
    @Autowired
    private ISysDictService dictService;
    @Autowired
    private IStoreCountryService storeCountryService;
    @Autowired
    private IStoreCelebrityPrivateCounselorService privateCounselorService;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private IStoreUserContactEmailService contactEmailService;
    @Autowired
    private ICelebrityPrivateService privateService;
    @Autowired
    private IKolProductService kolProductService;
    @Autowired
    private IStoreCelebrityPrivateAttributeRelationService privateAttributeRelationService;
    @Autowired
    private IKolAttributeService attributeService;
    @Autowired
    private IFeishuService feishuService;
    @Autowired
    private IKolProductService productService;
    @Autowired
    private ISysDictService sysDictService;
    @Resource
    private WechatService wechatService;
    @Resource
    private WechatDocConfig wechatDocConfig;
    @Resource
    private WechatDocImportService wechatDocImportService;
    @Resource
    private IKolCategoryAttributeRelationService categoryRelationService;
    @Resource
    private IStoreCelebrityPrivateProductService privateProductService;
    @Resource
    private ISysBaseAPI sysBaseAPI;
    @Resource
    private ISysUserService userService;
    @Resource
    private IKolSysUserFeishuSheetService sheetService;
    @Resource
    private ITiktokCelebrityProductService celebrityProductService;
    @Resource
    private ITiktokCelebrityProductCategoryService celebrityProductCategoryService;
    @Resource
    private IStoreCelebrityPrivatePersonalTagsService privatePersonalTagsService;
    @Autowired
    private ITiktokCelebrityService tkCelebrityService;
    @Autowired
    private IYtCelebrityService ytCelebrityService;
    @Autowired
    private IIgCelebrityService igCelebrityService;
    @Autowired
    private IKolHistoryCelebrityKolTypeService historyCelebrityKolTypeService;
    /**
     * IStorePromotionGoodsCelebrityService 分页列表查询
     *
     * @param storeCelebrityPrivate
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "私有网红表")
    @Operation(summary = "私有网红表-分页列表查询", description = "私有网红表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StoreCelebrityPrivate storeCelebrityPrivate, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        QueryWrapper<StoreCelebrityPrivate> queryWrapper = QueryGenerator.initQueryWrapper(storeCelebrityPrivate, req.getParameterMap());
        Page<StoreCelebrityPrivate> page = new Page<StoreCelebrityPrivate>(pageNo, pageSize);
        IPage<StoreCelebrityPrivate> pageList = storeCelebrityPrivateService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 分页列表查询
     *
     * @param storeCelebrityPrivateModel
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "私有网红表")
    @Operation(summary = "私有网红表-分页列表查询", description = "私有网红表-分页列表查询")
    @GetMapping(value = "/getPageList")
    public Result<?> getPageList(StoreCelebrityPrivateModel storeCelebrityPrivateModel, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {

        Page<StoreCelebrityPrivateModel> page = new Page<StoreCelebrityPrivateModel>(pageNo, pageSize);
        IPage<StoreCelebrityPrivateModel> pageList = storeCelebrityPrivateService.getPageList(page, storeCelebrityPrivateModel);
        return Result.ok(pageList);
    }

    /**
     * 私有网红列表查询
     *
     * @param storeCelebrityPrivateModel
     * @param pageNo
     * @param pageSize
     * @return
     * @Author : zhoushen
     */
    @AutoLog(value = "私有网红列表查询")
    @Operation(summary = "私有网红列表查询-分页列表查询", description = "私有网红列表查询-分页列表查询")
    @GetMapping(value = "/getPrivatePageList")
    @PermissionData(pageComponent = "instagram/storeCelebrityPrivate/StoreCelebrityPrivateManagementList")
    public Result<?> getPrivatePageList(StoreCelebrityPrivateModel storeCelebrityPrivateModel, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        if (oConvertUtils.isNotEmpty(storeCelebrityPrivateModel.getFollowersEndNum())) {
            Integer followersEndNum = storeCelebrityPrivateModel.getFollowersEndNum() * 1000;
            storeCelebrityPrivateModel.setFollowersEndNum(followersEndNum);
        }
        if (oConvertUtils.isNotEmpty(storeCelebrityPrivateModel.getFollowersStartNum())) {
            Integer postsStartNum = storeCelebrityPrivateModel.getFollowersStartNum() * 1000;
            storeCelebrityPrivateModel.setFollowersStartNum(postsStartNum);
        }
        if (oConvertUtils.isNotEmpty(storeCelebrityPrivateModel.getPlayAvgStartNum())) {
            Integer playAvgStartNum = storeCelebrityPrivateModel.getPlayAvgStartNum() * 1000;
            storeCelebrityPrivateModel.setPlayAvgStartNum(playAvgStartNum);
        }
        if (oConvertUtils.isNotEmpty(storeCelebrityPrivateModel.getPlayAvgEndNum())) {
            Integer playAvgEndNum = storeCelebrityPrivateModel.getPlayAvgEndNum() * 1000;
            storeCelebrityPrivateModel.setPlayAvgEndNum(playAvgEndNum);
        }
        Page<StoreCelebrityPrivateModel> page = new Page<StoreCelebrityPrivateModel>(pageNo, pageSize);
        // 就是调用这个QueryGenerator.installAuthJdbc方法获取权限sql
        String sql = QueryGenerator.installAuthJdbc(StoreCelebrityPrivateModel.class);
        IPage<StoreCelebrityPrivateModel> pageList = storeCelebrityPrivateService.getPrivatePageList(page, storeCelebrityPrivateModel, sql);
        return Result.ok(pageList);
    }

    @AutoLog(value = "私有网红列表查询")
    @Operation(summary = "私有网红列表查询-分页列表查询", description = "私有网红列表查询-列表查询")
    @GetMapping(value = "/getCelebrityPrivateList")
    @PermissionData(pageComponent = "instagram/storeCelebrityPrivate/StoreCelebrityPrivateManagementList")
    public Result<?> getCelebrityPrivateList(StoreCelebrityPrivateModel storeCelebrityPrivateModel, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        if (oConvertUtils.isNotEmpty(storeCelebrityPrivateModel.getColumn())) {
            storeCelebrityPrivateModel.setColumn(oConvertUtils.camelToUnderline(storeCelebrityPrivateModel.getColumn()));
        }
        Page<StoreCelebrityPrivateModel> page = new Page<>(pageNo, pageSize);

        storeCelebrityPrivateModel.setUserId(getUserIdByToken());

        // 2025年9月1日18:05:41 按产品查询时取消社媒属性选项，需要后台查询后添加。刘工说的
        if (oConvertUtils.isNotEmpty(storeCelebrityPrivateModel.getProductId()) && oConvertUtils.isEmpty(storeCelebrityPrivateModel.getAttributeIds())) {
            List<String> attributeIds = attributeService.queryProductAttributes(storeCelebrityPrivateModel.getProductId());
            storeCelebrityPrivateModel.setAttributeIds(String.join(",", attributeIds));
        }
        if (oConvertUtils.isNotEmpty(storeCelebrityPrivateModel.getCategoryIds()) && oConvertUtils.isEmpty(storeCelebrityPrivateModel.getAttributeIds())) {
            List<String> attributeIds = categoryRelationService.queryCategoryAttributes(storeCelebrityPrivateModel.getCategoryIds());
            if (attributeIds.isEmpty()) {
                return Result.ok(new Page<>());
            }
            storeCelebrityPrivateModel.setAttributeIds(String.join(",", attributeIds));
        }

        try {
            IPage<StoreCelebrityPrivateModel> pageList = storeCelebrityPrivateService.getCelebrityPrivateList(page, storeCelebrityPrivateModel, "");
            if (pageList.getRecords().isEmpty()) {
                return Result.ok(pageList);
            }
            setAttributesAndProduct(storeCelebrityPrivateModel, pageList);
            return Result.ok(pageList);
        } catch (Exception e) {
            log.error("获取私有网红列表时发生错误: ", e);
            return Result.error("获取私有网红列表失败");
        }
    }

    /**
     * @description: 设置属性和产品
     * @author: nqr
     * @date: 2025/9/23 18:22
     **/
    private void setAttributesAndProduct(StoreCelebrityPrivateModel storeCelebrityPrivateModel, IPage<StoreCelebrityPrivateModel> pageList) {
        List<StoreCelebrityPrivateModel> privateModelList = pageList.getRecords();
        List<String> celebrityPrivateIds = privateModelList.stream().map(StoreCelebrityPrivate::getId).toList();

        List<String> accounts = privateModelList.stream().map(StoreCelebrityPrivate::getAccount).toList();

        List<TypeData> celebrityAttributes;
        List<StoreCelebrityPrivateProductModel> privateProducts;
        List<StoreCelebrityPrivateCounselor> privateCounselors;
        List<KolHistoryCelebrityKolType> celebrityKolTypes;
        long stepStartTime = System.currentTimeMillis();

        try {
            CompletableFuture<List<TypeData>> celebrityAttributesFuture = CompletableFuture.supplyAsync(() -> privateAttributeRelationService.getCelebrityAttributes(celebrityPrivateIds));
            celebrityAttributes = celebrityAttributesFuture.get();
            CompletableFuture<List<StoreCelebrityPrivateProductModel>> privateProductsFuture = CompletableFuture.supplyAsync(() -> privateProductService.getProductList(celebrityPrivateIds));
            privateProducts = privateProductsFuture.get();
            CompletableFuture<List<StoreCelebrityPrivateCounselor>> privateCounselorsFuture = CompletableFuture.supplyAsync(() -> counselorService.lambdaQuery().in(StoreCelebrityPrivateCounselor::getCelebrityPrivateId, celebrityPrivateIds).list());
            privateCounselors = privateCounselorsFuture.get();
            CompletableFuture<List<KolHistoryCelebrityKolType>> historyCelebrityKolTypeFuture = CompletableFuture.supplyAsync(() -> historyCelebrityKolTypeService.lambdaQuery().in(KolHistoryCelebrityKolType::getCelebrityName, accounts).list());
            celebrityKolTypes = historyCelebrityKolTypeFuture.get();
        } catch (Exception e) {
            celebrityAttributes = privateAttributeRelationService.getCelebrityAttributes(celebrityPrivateIds);
            privateProducts = privateProductService.getProductList(celebrityPrivateIds);
            privateCounselors = counselorService.lambdaQuery().in(StoreCelebrityPrivateCounselor::getCelebrityPrivateId, celebrityPrivateIds).list();
            celebrityKolTypes = historyCelebrityKolTypeService.lambdaQuery().in(KolHistoryCelebrityKolType::getCelebrityName, accounts).list();
        }
        // List<TypeData> celebrityAttributes =
        // privateAttributeRelationService.getCelebrityAttributes(celebrityPrivateIds);
        // List<StoreCelebrityPrivateProductModel> privateProducts =
        // privateProductService.getProductList(celebrityPrivateIds);
        // List<StoreCelebrityPrivateCounselor> privateCounselors =
        // counselorService.lambdaQuery().in(StoreCelebrityPrivateCounselor::getCelebrityPrivateId,
        // celebrityPrivateIds).list();
        log.info("[私有网红] 查询社媒属性,带货产品,网红顾问: {}秒", (System.currentTimeMillis() - stepStartTime) / 1000.0);

        List<String> celebrityIds = privateModelList.stream().filter(x -> x.getPlatformType() == CommonConstant.TK).map(StoreCelebrityPrivate::getCelebrityId).toList();
        List<TiktokCelebrityProduct> tiktokCelebrityProducts = new ArrayList<>();
        List<TiktokCelebrityProductCategory> tiktokCelebrityProductCategories;
        if (!celebrityIds.isEmpty()) {
            celebrityIds = celebrityIds.stream().filter(Objects::nonNull).toList();
            tiktokCelebrityProducts = getTKCelebrityProductList(celebrityIds);
            tiktokCelebrityProductCategories = getTKCelebrityProductCategoryList(celebrityIds);
        } else {
            tiktokCelebrityProductCategories = new ArrayList<>();
        }

        for (StoreCelebrityPrivateModel record : privateModelList) {
            String id = record.getId();
            /*
             * String videoData = record.getVideoData();
             * if (oConvertUtils.isNotEmpty(videoData)) {
             * JSONObject json = JSONObject.parseObject(videoData);
             * JSONObject total = json.getJSONObject("total");
             * if (oConvertUtils.isNotEmpty(total)) {
             * record.setVideoCreateTimeLast(DateUtils.str2date(total.getString(
             * "video_create_time_last")));
             * }
             * }
             */
            String celebrityCounselorId = record.getCelebrityCounselorId();
            List<StoreCelebrityPrivateCounselor> privateCounselorList = privateCounselors.stream().filter(x -> x.getCelebrityPrivateId().equals(id)).toList();
            /*
             * List<StoreCelebrityPrivateCounselor> counselors =
             * privateCounselorList.stream().filter(x ->
             * !x.getCelebrityCounselorId().equals(celebrityCounselorId))
             * //.sorted(Comparator.comparing(StoreCelebrityPrivateCounselor::getCreateTime)
             * .reversed())
             * .toList();
             */
            // 如果搜索网红顾问，获取历史报价中当前网红顾问的费用、邮箱设置到网红身上 2025年5月6日08:04:35
            String counselorId = storeCelebrityPrivateModel.getCelebrityCounselorId();
            // 设置网红顾问最新报价
            setPrivateContractInfoList(record, counselorId, privateCounselorList, celebrityCounselorId);
            List<StoreCelebrityPrivateCounselor> counselors = sortCounselorsByGroupLatestTime(privateCounselorList);
            // record.setPrivateCounselorList(counselors);
            try {
                setPrivateCounselorMap(record, counselors);
            } catch (Exception e) {
                System.out.println("私有网红设置历史报价分组报错");
                System.out.println(e.getMessage());
                record.setPrivateCounselorList(counselors);
            }
            List<TypeData> dataList = celebrityAttributes.stream().filter(x -> x.getCelebrityPrivateId().equals(id)).toList();
            List<StoreCelebrityPrivateProductModel> productModels = privateProducts.stream().filter(x -> x.getCelebrityId().equals(id)).toList();
            if (!productModels.isEmpty()) {
                List<StoreCelebrityPrivateProductModel> privateProductRelationModels = productModels.stream().filter(x -> oConvertUtils.isNotEmpty(x.getRelationStatus()) && x.getRelationStatus() == 1).toList();
                // 构造json结构
                String productSelectIds = productModels.stream().map(StoreCelebrityPrivateProductModel::getProductId).collect(Collectors.joining(","));
                String productSelectInfo = productModels.stream().map(StoreCelebrityPrivateProductModel::getProductName).collect(Collectors.joining(","));
                List<JSONObject> productCooperateIdList = new ArrayList<>();
                List<JSONObject> productCooperateInfoList = new ArrayList<>();
                for (StoreCelebrityPrivateProductModel model : privateProductRelationModels) {
                    JSONObject productCooperateIds = new JSONObject();
                    String brandId = model.getBrandId();
                    String productId = model.getProductId();
                    productCooperateIds.put("brandId", brandId);
                    productCooperateIds.put("productId", productId);
                    productCooperateIds.put("celebrityCounselorId", model.getCelebrityCounselorId());
                    productCooperateIdList.add(productCooperateIds);

                    JSONObject productCooperateInfo = new JSONObject();
                    productCooperateInfo.put("brandName", model.getBrandName());
                    productCooperateInfo.put("productName", model.getProductName());
                    productCooperateInfo.put("celebrityCounselorName", model.getCelebrityCounselorName());
                    productCooperateInfoList.add(productCooperateInfo);
                }

                record.setProductSelectIds(productSelectIds);
                record.setProductSelectInfo(productSelectInfo);
                record.setProductCooperateIds(JSON.toJSONString(productCooperateIdList));
                record.setProductCooperateInfo(JSON.toJSONString(productCooperateInfoList));
            }

            if (!dataList.isEmpty()) {
                record.setDataList(dataList);
            }

            // 判断平台是否是TK，如果是，则查询对应的产品数据
            if (record.getPlatformType() == CommonConstant.TK) {
                List<TiktokCelebrityProduct> celebrityProducts = tiktokCelebrityProducts.stream().filter(x -> x.getSecUid().equals(record.getCelebrityId())).toList();
                if (!celebrityProducts.isEmpty()) {
                    celebrityProducts.forEach(x -> {
                        String productId = x.getProductId();
                        String secUid = x.getSecUid();
                        List<TiktokCelebrityProductCategory> categories = tiktokCelebrityProductCategories.stream().filter(y -> y.getSecUid().equals(secUid) && y.getProductId().equals(productId)).toList();
                        if (!categories.isEmpty()) {
                            x.setCategories(categories);
                        }
                    });
                    record.setTiktokCelebrityProductList(celebrityProducts);
                }
            }

            // 设置历史提报达人类型
            List<KolHistoryCelebrityKolType> kolTypeList = celebrityKolTypes.stream().filter(x -> x.getCelebrityName().equalsIgnoreCase(record.getAccount()) && Objects.equals(x.getPlatformType(), record.getPlatformType())).toList();
            if (!kolTypeList.isEmpty()) {
                record.setKolTypes(kolTypeList.stream().map(KolHistoryCelebrityKolType::getKolType).distinct().collect(Collectors.joining(",")));
            }
        }
    }

    private static void setPrivateContractInfoList(StoreCelebrityPrivateModel record, String counselorId, List<StoreCelebrityPrivateCounselor> privateCounselorList, String celebrityCounselorId) {
        if (oConvertUtils.isNotEmpty(counselorId)) {
            privateCounselorList.stream().filter(x -> x.getCelebrityCounselorId().equals(counselorId)).max(Comparator.comparing(StoreCelebrityPrivateCounselor::getContractTime, Comparator.nullsLast(Comparator.naturalOrder()))).ifPresent(y -> {
                record.setContractAmount(y.getContractAmount());
                record.setCelebrityContactEmail(y.getCelebrityContactEmail());
                record.setEmail(y.getEmail());
                record.setCelebrityCounselorId(counselorId);
                record.setCelebrityCounselorName(y.getCelebrityCounselorName());
                // 将 JSON 字符串反序列化为 List<PrivateContractInfo>
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    String contractInfoJson = y.getContractInfo();
                    List<PrivateContractInfo> contractInfoList = Collections.emptyList();
                    if (contractInfoJson != null && !contractInfoJson.trim().isEmpty()) {
                        try {
                            JsonNode root = objectMapper.readTree(contractInfoJson);
                            if (root.isArray()) {
                                JavaType type = TypeFactory.defaultInstance().constructCollectionType(List.class, PrivateContractInfo.class);
                                contractInfoList = objectMapper.readValue(contractInfoJson, type);
                            } else {
                                log.warn("Expected JSON array, but got non-array for celebrityCounselorId: {}, JSON: {}", celebrityCounselorId, contractInfoJson);
                            }
                        } catch (Exception e) {
                            log.error("Failed to deserialize contractInfoJson: {}", contractInfoJson, e);
                            // 根据业务决定：抛异常 or 置空
                            contractInfoList = new ArrayList<>();
                            // throw new RuntimeException("Deserialization failed", e);
                        }
                    }
                    record.setPrivateContractInfoList(contractInfoList);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to deserialize contract info from JSON", e);
                }
            });
            // 按照网红顾问查询时，历史报价中也增加当前网红顾问历史报价
            /*
             * counselors = privateCounselorList.stream().filter(x ->
             * !x.getCelebrityCounselorId().equals(counselorId))
             * //.sorted(Comparator.comparing(StoreCelebrityPrivateCounselor::getCreateTime)
             * .reversed())
             * .toList();
             */
        } else {
            privateCounselorList.stream().filter(x -> x.getCelebrityCounselorId().equals(celebrityCounselorId)).max(Comparator.comparing(StoreCelebrityPrivateCounselor::getContractTime, Comparator.nullsLast(Comparator.naturalOrder()))).ifPresent(y -> {
                ObjectMapper objectMapper = new ObjectMapper();
                String contractInfoJson = y.getContractInfo();
                List<PrivateContractInfo> contractInfoList = new ArrayList<>();

                if (contractInfoJson != null && !contractInfoJson.trim().isEmpty()) {
                    try {
                        JsonNode root = objectMapper.readTree(contractInfoJson);
                        if (root.isArray()) {
                            JavaType type = TypeFactory.defaultInstance().constructCollectionType(List.class, PrivateContractInfo.class);
                            contractInfoList = objectMapper.readValue(contractInfoJson, type);
                        } else {
                            log.warn("Expected JSON array, but got non-array for celebrityCounselorId: {}, JSON: {}", celebrityCounselorId, contractInfoJson);
                        }
                    } catch (Exception e) {
                        log.error("Failed to deserialize contractInfoJson: {}", contractInfoJson, e);
                        // 根据业务决定：抛异常 or 置空
                        contractInfoList = new ArrayList<>();
                        // throw new RuntimeException("Deserialization failed", e);
                    }
                }
                record.setPrivateContractInfoList(contractInfoList);
            });
        }
    }

    public List<StoreCelebrityPrivateCounselor> sortCounselorsByGroupLatestTime(List<StoreCelebrityPrivateCounselor> privateCounselorList) {

        if (privateCounselorList == null || privateCounselorList.isEmpty()) {
            return new ArrayList<>();
        }

        // 1. 按 celebrityCounselorId 分组
        Map<Object, List<StoreCelebrityPrivateCounselor>> grouped = privateCounselorList.stream().collect(Collectors.groupingBy(StoreCelebrityPrivateCounselor::getCelebrityCounselorId));

        // 2. 每组内部按 contractTime 倒序排序（null 最后）
        grouped.values().forEach(list -> list.sort(Comparator.comparing(StoreCelebrityPrivateCounselor::getContractTime, Comparator.nullsLast(Comparator.reverseOrder()))));

        // 3. 按每组最新时间（即每组第一个元素的时间）对组进行降序排序
        return grouped.entrySet().stream().sorted((e1, e2) -> {
            Date time1 = e1.getValue().isEmpty() ? null : e1.getValue().get(0).getContractTime();
            Date time2 = e2.getValue().isEmpty() ? null : e2.getValue().get(0).getContractTime();
            Comparator<Date> dateComparator = Comparator.nullsLast(Comparator.reverseOrder());
            return dateComparator.compare(time1, time2);
        }).flatMap(entry -> entry.getValue().stream()).toList();
    }

    /*
     * private void setPrivateCounselorMap(StoreCelebrityPrivateModel record,
     * List<StoreCelebrityPrivateCounselor> counselors) {
     * ObjectMapper objectMapper = new ObjectMapper();
     *
     * // Step 1: 展开所有 contract 条目，生成 flat list
     * List<StoreCelebrityPrivateCounselor> expandedList = counselors.stream()
     * .flatMap(counselor -> {
     * String contractInfo = counselor.getContractInfo();
     * if (oConvertUtils.isEmpty(contractInfo)) {
     * return Stream.empty();
     * }
     * try {
     * JsonNode contracts = objectMapper.readTree(contractInfo);
     * return StreamSupport.stream(contracts.spliterator(), false)
     * .filter(node -> node.has("videoTag") && node.has("contractAmount"))
     * .map(node -> {
     * StoreCelebrityPrivateCounselor copy = new StoreCelebrityPrivateCounselor();
     * BeanUtils.copyProperties(counselor, copy);
     *
     * BigDecimal amount = new BigDecimal(node.get("contractAmount").asText());
     * copy.setContractAmount(amount);
     * String videoTag = node.get("videoTag").asText();
     * copy.setContractInfo(videoTag); // 这里 contractInfo 被覆盖为 videoTag
     *
     * if (node.has("contractTime") && !node.get("contractTime").isNull()) {
     * try {
     * copy.setContractTime(DateUtils.str2date(node.get("contractTime").asText()));
     * } catch (Exception e) {
     * // 解析失败则设为 null，由后续排序处理
     * copy.setContractTime(null);
     * }
     * }
     * return copy;
     * });
     * } catch (Exception e) {
     * e.printStackTrace();
     * return Stream.empty();
     * }
     * })
     * .toList();
     *
     * // Step 2: 对 expandedList 按 celebrityCounselorId 分组，并按组最新 contractTime 排序
     * if (expandedList.isEmpty()) {
     * record.setPrivateCounselorList(new ArrayList<>());
     * return;
     * }
     *
     * Map<Object, List<StoreCelebrityPrivateCounselor>> groupedById =
     * expandedList.stream()
     * .collect(Collectors.groupingBy(StoreCelebrityPrivateCounselor::
     * getCelebrityCounselorId));
     *
     * // 组内按 contractTime 倒序
     * groupedById.values().forEach(list ->
     * list.sort(Comparator.comparing(StoreCelebrityPrivateCounselor::
     * getContractTime,
     * Comparator.nullsLast(Comparator.reverseOrder())))
     * );
     *
     * // 按每组最新时间（即 get(0)）降序排组
     * List<StoreCelebrityPrivateCounselor> sortedResult =
     * groupedById.entrySet().stream()
     * .sorted(Comparator.comparing(
     * (Map.Entry<Object, List<StoreCelebrityPrivateCounselor>> entry) ->
     * entry.getValue().isEmpty() ? null :
     * entry.getValue().get(0).getContractTime(),
     * Comparator.nullsLast(Comparator.<Date>reverseOrder())
     * ))
     * .flatMap(entry -> entry.getValue().stream())
     * .toList();
     *
     * record.setPrivateCounselorList(sortedResult);
     * }
     */
    private void setPrivateCounselorMap(StoreCelebrityPrivateModel record, List<StoreCelebrityPrivateCounselor> counselors) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, List<StoreCelebrityPrivateCounselor>> grouped = counselors.stream().flatMap(counselor -> {
            String contractInfo = counselor.getContractInfo();
            // 关键：增加 null 和空字符串判断
            if (oConvertUtils.isEmpty(contractInfo)) {
                return Stream.empty();
            }
            try {
                JsonNode contracts = objectMapper.readTree(contractInfo);
                return StreamSupport.stream(contracts.spliterator(), false).filter(node -> node.has("videoTag") && node.has("contractAmount")).map(node -> {
                    StoreCelebrityPrivateCounselor copy = new StoreCelebrityPrivateCounselor();
                    BeanUtils.copyProperties(counselor, copy);

                    BigDecimal amount = new BigDecimal(node.get("contractAmount").asText());
                    copy.setContractAmount(amount);
                    String videoTag = node.get("videoTag").asText();
                    copy.setContractInfo(videoTag);
                    if (oConvertUtils.isNotEmpty(node.get("contractTime"))) {
                        copy.setContractTime(DateUtils.str2date(node.get("contractTime").asText()));
                    }
                    return new AbstractMap.SimpleEntry<>(videoTag, copy);
                });
            } catch (Exception e) {
                e.printStackTrace();
                return Stream.empty();
            }
        }).collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));

        List<StoreCelebrityPrivateCounselor> collect = grouped.values().stream().flatMap(List::stream).sorted(Comparator.comparing(StoreCelebrityPrivateCounselor::getContractInfo).thenComparing(StoreCelebrityPrivateCounselor::getContractTime, Comparator.nullsFirst(Date::compareTo)).reversed()).toList();

        record.setPrivateCounselorList(collect);
    }

    /**
     * @description: 获取网红带货产品类目
     * @author: nqr
     * @date: 2025/10/8 13:49
     **/
    private List<TiktokCelebrityProductCategory> getTKCelebrityProductCategoryList(List<String> celebrityIds) {
        if (celebrityIds == null || celebrityIds.isEmpty()) {
            return new ArrayList<>();
        }
        return celebrityProductCategoryService.lambdaQuery().select(TiktokCelebrityProductCategory::getId, TiktokCelebrityProductCategory::getSecUid, TiktokCelebrityProductCategory::getProductId, TiktokCelebrityProductCategory::getParentId, TiktokCelebrityProductCategory::getIsLeaf, TiktokCelebrityProductCategory::getLevel, TiktokCelebrityProductCategory::getCategoryName).in(TiktokCelebrityProductCategory::getSecUid, celebrityIds).list();
    }

    /**
     * @description: 获取tiktok网红带货数据
     * @author: nqr
     * @date: 2025/10/8 13:47
     **/
    private List<TiktokCelebrityProduct> getTKCelebrityProductList(List<String> celebrityIds) {
        if (celebrityIds == null || celebrityIds.isEmpty()) {
            return new ArrayList<>();
        }
        return celebrityProductService.lambdaQuery().select(TiktokCelebrityProduct::getId, TiktokCelebrityProduct::getProductId, TiktokCelebrityProduct::getTitle, TiktokCelebrityProduct::getAuthorId, TiktokCelebrityProduct::getSecUid, TiktokCelebrityProduct::getSeoUrl, TiktokCelebrityProduct::getCoverUrl).in(TiktokCelebrityProduct::getSecUid, celebrityIds).list();
    }

    /**
     * 查询网红带货明细展示
     */
    @AutoLog(value = "查询网红带货明细展示")
    @Operation(summary = "查询网红带货明细展示", description = "查询网红带货明细展示")
    @GetMapping(value = "/getByIdDetail")
    public Result<?> getDetailById(StoreSellDetailModel storeSellDetailModel, HttpServletRequest req) {

        List<StoreSellDetailModel> list = storeCelebrityPrivateService.getDetailById(storeSellDetailModel);
        return Result.ok(list);
    }

    /**
     * 添加
     *
     * @param storeCelebrityPrivateModel
     * @return
     */
    @AutoLog(value = "私有网红表-添加")
    @Operation(summary = "私有网红表-添加", description = "私有网红表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@Valid @RequestBody StoreCelebrityPrivateModel storeCelebrityPrivateModel) {
        String userId = getUserIdByToken();
        SysUser sysUser = sysUserService.getById(userId);
        if (!sysUser.getUserType().equals(UserType.CELEBRITY_COUNSELOR.getCode())) {
            return Result.error("当前用户无法新增网红");
        }
        storeCelebrityPrivateModel.setCelebrityCounselorId(userId);
        storeCelebrityPrivateModel.setCelebrityCounselorName(getUserNameByToken());
        String account = storeCelebrityPrivateModel.getAccount().trim();
        storeCelebrityPrivateModel.setAccount(account);
        if (account.contains(" ")) {
            return Result.error("账号不能包含空格");
        }
        Integer platformType = storeCelebrityPrivateModel.getPlatformType();
        String celebrityCounselorId = storeCelebrityPrivateModel.getCelebrityCounselorId();
        String personalTagIds = storeCelebrityPrivateModel.getPersonalTagIds();
        if (oConvertUtils.isNotEmpty(personalTagIds)) {
            List<StoreCelebrityPrivatePersonalTags> personalTags = privatePersonalTagsService.lambdaQuery().eq(StoreCelebrityPrivatePersonalTags::getCelebrityId, storeCelebrityPrivateModel.getId()).list();
            List<StorePersonalTags> storePersonalTags = personalTagsService.lambdaQuery().in(StorePersonalTags::getId, Arrays.asList(personalTagIds.split(","))).list();
            boolean match = storePersonalTags.stream().anyMatch(x -> "无".equalsIgnoreCase(x.getTagName()));
            if (match && !personalTags.isEmpty()) {
                return Result.error("私有失败,个性化标签已存在,不能设置为'无'");
            }
        }
        StoreCelebrityPrivate celebrityPrivates = storeCelebrityPrivateService.checkCelebrityPrivate(account, platformType);
        String privateCounselorId = IdWorker.get32UUID();
        if (celebrityPrivates != null) {
            String privateId = celebrityPrivates.getId();
            LambdaQueryWrapper<StoreCelebrityPrivateCounselor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(StoreCelebrityPrivateCounselor::getCelebrityCounselorId, celebrityCounselorId);
            StoreCelebrityPrivateCounselor privateCounselor = counselorService.getByCelebrityCounselorId(celebrityCounselorId, privateId);
            if (privateCounselor != null) {
                privateCounselorId = privateCounselor.getId();
            }
            storeCelebrityPrivateModel.setId(privateId);
        } else {
            storeCelebrityPrivateModel.setId(IdWorker.get32UUID());
            storeCelebrityPrivateModel.setContractTime(storeCelebrityPrivateModel.getContractTime() != null ? storeCelebrityPrivateModel.getContractTime() : new Date());
            storeCelebrityPrivateModel.setUpdateTime(new Date());
            storeCelebrityPrivateModel.setUpdateBy(getUserNameByToken());
            storeCelebrityPrivateModel.setUpdateTime(new Date());
        }
        // 编辑个性化标签
        // storeCelebrityPrivateService.updatePersonalTags(personalTags);
        List<StoreCelebrityPrivatePersonalTags> privatePersonalTagsList = new ArrayList<>();
        storeCelebrityPrivateService.processingPersonalizedTags(privatePersonalTagsList, storeCelebrityPrivateModel.getId(), personalTagIds);

        StoreCelebrityPrivate storeCelebrityPrivate = new StoreCelebrityPrivate();
        BeanUtils.copyProperties(storeCelebrityPrivateModel, storeCelebrityPrivate);

        // nqr 2025年6月30日15:03:31 判断是否有历史选中和历史合作
        List<StoreCelebrityPrivateProduct> privateProductListSave = new ArrayList<>();
        createPrivateProduct(storeCelebrityPrivateModel, storeCelebrityPrivateModel, privateProductListSave);

        counselorService.createPrivateCounselor(storeCelebrityPrivateModel, privateCounselorId);

        // 新增网红信息
        storeCelebrityPrivateService.addData(storeCelebrityPrivate, privateProductListSave, privatePersonalTagsList);
        return Result.ok("添加成功！");
    }

    /**
     * @description: 创建私有网红产品关联关系
     * @author: nqr
     * @date: 2025/6/30 15:03:31
     **/

    private void createPrivateProduct(StoreCelebrityPrivateModel storeCelebrityPrivateModel, StoreCelebrityPrivate storeCelebrityPrivate, List<StoreCelebrityPrivateProduct> privateProductListSave) {
        String productSelectIds = storeCelebrityPrivateModel.getProductSelectIds();
        String productCooperateIds = storeCelebrityPrivateModel.getProductCooperateIds();

        List<String> productIds = new ArrayList<>();
        if (oConvertUtils.isNotEmpty(productSelectIds)) {
            String[] productSelectIdsArr = productSelectIds.split(",");
            productIds.addAll(Arrays.asList(productSelectIdsArr));
        }
        if (oConvertUtils.isNotEmpty(productCooperateIds)) {
            String[] productCooperateIdsArr = productCooperateIds.split(",");
            productIds.addAll(Arrays.asList(productCooperateIdsArr));
        }
        if (!productIds.isEmpty()) {
            // 查询产品明细
            List<KolProduct> list = kolProductService.lambdaQuery().in(KolProduct::getId, productIds).list();
            // 分别构造历史选中和历史合作数据
            for (KolProduct product : list) {
                StoreCelebrityPrivateProduct privateProduct = new StoreCelebrityPrivateProduct();
                privateProduct.setId(IdWorker.get32UUID());
                privateProduct.setCelebrityId(storeCelebrityPrivate.getId());
                privateProduct.setBrandId(product.getBrandId());
                privateProduct.setProductId(product.getId());
                privateProduct.setSelectionTime(new Date());
                if (oConvertUtils.isNotEmpty(productCooperateIds)) {
                    privateProduct.setRelationStatus(productCooperateIds.contains(product.getId()) ? 1 : 0);
                }
                privateProduct.setStartTime(new Date());
                // privateProduct.setCampaignId();
                // privateProduct.setRelationType();
                // privateProduct.setEndTime();
                // privateProduct.setDetails();
                privateProduct.setCreateBy(getUserNameByToken());
                privateProduct.setCreateTime(new Date());
                privateProduct.setDelFlag(0);
                privateProduct.setCelebrityId(storeCelebrityPrivateModel.getId());
                privateProductListSave.add(privateProduct);
            }
        }
    }

    /**
     * @Description 创建私有网红产品关联关系
     * @Date 17:37 2026/2/27
     * @Author nqr
     **/

    private void createPrivateProductNew(StoreCelebrityPrivateModel storeCelebrityPrivateModel, StoreCelebrityPrivate storeCelebrityPrivate, List<StoreCelebrityPrivateProduct> privateProductListSave) {
        List<StoreCelebrityPrivateProduct> privateProducts = storeCelebrityPrivateModel.getPrivateProducts();
        String celebrityId = storeCelebrityPrivate.getId();
        List<StoreCelebrityPrivateProduct> products = privateProductService.lambdaQuery().eq(StoreCelebrityPrivateProduct::getCelebrityId, celebrityId).list();
        // 分别构造历史选中和历史合作数据
        for (StoreCelebrityPrivateProduct privateProduct : privateProducts) {
            privateProduct.setId(IdWorker.get32UUID());
            privateProduct.setCelebrityId(celebrityId);
            privateProduct.setRelationStatus(1);
//            privateProduct.setCounselorUserId();
//            privateProduct.setCounselorUserName();
            privateProduct.setStartTime(new Date());
            privateProduct.setCreateBy(getUserNameByToken());
            privateProduct.setCreateTime(new Date());
            privateProduct.setDelFlag(0);

            Optional<StoreCelebrityPrivateProduct> productOptional = products.stream().filter(product -> product.getId().equals(privateProduct.getId()) && product.getProductId().equals(privateProduct.getProductId())).findFirst();
            if (productOptional.isPresent()) {
                StoreCelebrityPrivateProduct product = productOptional.get();
                privateProduct.setSelectionTime(product.getSelectionTime());
            }else {
                privateProduct.setSelectionTime(new Date());
            }
            privateProductListSave.add(privateProduct);
        }

    }

    /**
     * 编辑
     *
     * @param storeCelebrityPrivateModel
     * @return
     */
    @AutoLog(value = "私有网红表-编辑")
    @Operation(summary = "私有网红表-编辑", description = "私有网红表-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody StoreCelebrityPrivateModel storeCelebrityPrivateModel) {

        String privateId = storeCelebrityPrivateModel.getId();

        // 查询是否存在
        List<StoreCelebrityPrivate> list = storeCelebrityPrivateService.lambdaQuery().eq(StoreCelebrityPrivate::getAccount, storeCelebrityPrivateModel.getAccount()).eq(StoreCelebrityPrivate::getPlatformType, storeCelebrityPrivateModel.getPlatformType()).list();
        if (!list.isEmpty()) {
            StoreCelebrityPrivate celebrityPrivate = list.get(0);
            if (!celebrityPrivate.getId().equals(privateId)) {
                return Result.error("该账号已存在，请重新输入！");
            }
        }

        storeCelebrityPrivateModel.setCelebrityCounselorId(null);
        storeCelebrityPrivateModel.setCelebrityCounselorName(null);
        storeCelebrityPrivateModel.setContractTime(null);
        storeCelebrityPrivateModel.setVideoData(null);

        // 设置空字符串值为null
        convertEmptyToNull(storeCelebrityPrivateModel);
        storeCelebrityPrivateService.updateById(storeCelebrityPrivateModel);
        // 更新推广网红收货地址
        List<StorePromotionGoodsCelebrity> storePromotionGoodsCelebrities = storePromotionGoodsCelebrityService.queryCelebritiesById(privateId);

        for (StorePromotionGoodsCelebrity spgc : storePromotionGoodsCelebrities) {
            // 订单号为null强制更新送货地址
            if (!oConvertUtils.isNotEmpty(spgc.getGoodsWaybill())) {
                spgc.setFullAddress(storeCelebrityPrivateModel.getFullAddress());
                storePromotionGoodsCelebrityService.updateById(spgc);
            }
        }
        // 更新个性化标签
        String personalTagIds = storeCelebrityPrivateModel.getPersonalTagIds();
        List<StoreCelebrityPrivatePersonalTags> privatePersonalTagsList = new ArrayList<>();
        // 编辑个性化标签
        storeCelebrityPrivateService.processingPersonalizedTags(privatePersonalTagsList, storeCelebrityPrivateModel.getId(), personalTagIds);

        StoreCelebrityPrivate storeCelebrityPrivate = new StoreCelebrityPrivate();
        BeanUtils.copyProperties(storeCelebrityPrivateModel, storeCelebrityPrivate);

        // nqr 2025年6月30日15:03:31 判断是否有历史选中和历史合作
        List<StoreCelebrityPrivateProduct> privateProductListSave = new ArrayList<>();
        createPrivateProductNew(storeCelebrityPrivateModel, storeCelebrityPrivate, privateProductListSave);

        storeCelebrityPrivateService.editData(storeCelebrityPrivate, privateProductListSave, privatePersonalTagsList);
        return Result.ok("编辑成功!");
    }

    /**
     * 验证网红信息
     *
     * @param storeCelebrityPrivate 网红信息
     * @param storeCelebrityPrivate 网红信息
     * @return 处理结果
     */
    @PostMapping(value = "/verifyCelebrityInfo")
    public Result<?> verifyCelebrityInfo(@RequestBody StoreCelebrityPrivate storeCelebrityPrivate) {
        try {
            int isValid = storeCelebrityPrivate.getIsAbnormalAccount();
            boolean success = storeCelebrityPrivateService.verifyCelebrityInfo(storeCelebrityPrivate);
            if (success) {
                String message = isValid == 0 ? "验证通过，信息已更新" : "标记为需要重新获取";
                return Result.ok(message);
            } else {
                return Result.error("验证处理失败");
            }
        } catch (Exception e) {
            return Result.error("验证处理异常：" + e.getMessage());
        }
    }

    /**
     * 批量更新网红异常状态
     *
     * @param request 网红ID列表
     * @return 处理结果
     */
    @PostMapping(value = "/batchUpdateAbnormalStatus")
    public Result<?> batchUpdateAbnormalStatus(@RequestBody CelebrityAbnormalStatusRequest request) {
        try {
            // 检查 celebrityIds 是否为空或无效
            if (request.getCelebrityIds() == null || request.getCelebrityIds().trim().isEmpty()) {
                return Result.error("网红ID列表不能为空");
            }

            // 将逗号分隔的字符串转换为 List<String>
            List<String> celebrityIds = Arrays.asList(request.getCelebrityIds().split(","));

            // 检查 isAbnormalAccount 是否合法
            Integer isAbnormalAccount = request.getIsAbnormalAccount();
            if (!isAbnormalAccount.equals(0) && !isAbnormalAccount.equals(2)) {
                return Result.error("异常状态值必须为0或2");
            }

            Map<String, Object> result = storeCelebrityPrivateService.batchUpdateAbnormalStatus(celebrityIds, isAbnormalAccount);
            boolean success = (boolean) result.get("success");

            if (!success) {
                if (result.containsKey("notExistingIds")) {
                    return Result.error(result.get("message") + "，无效ID: " + result.get("notExistingIds"));
                }
                return Result.error((String) result.get("message"));
            }

            return Result.ok(result.get("message"));
        } catch (Exception e) {
            return Result.error("批量更新异常：" + e.getMessage());
        }
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "私有网红表-通过id删除")
    @Operation(summary = "私有网红表-通过id删除", description = "私有网红表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        /*
         * storeCelebrityPrivateService.removeById(id);
         * LambdaQueryWrapper<StoreCelebritySettlement> lambdaQueryWrapper = new
         * LambdaQueryWrapper<>();
         * List<StoreCelebritySettlement> list =
         * storeCelebritySettlementService.list(lambdaQueryWrapper.eq(
         * StoreCelebritySettlement::getCelebrityPrivateId, id));
         * if (list.size() > YesNoStatus.NO.getCode()) {
         * for (StoreCelebritySettlement storeCelebritySettlement : list) {
         * storeCelebritySettlementService.removeById(storeCelebritySettlement.getId());
         * }
         * }
         */
        return Result.error("删除失败，当前网红不可删除!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "私有网红表-批量删除")
    @Operation(summary = "私有网红表-批量删除", description = "私有网红表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        // this.storeCelebrityPrivateService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("删除失败，当前网红不可删除!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "私有网红表-通过id查询")
    @Operation(summary = "私有网红表-通过id查询", description = "私有网红表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        StoreCelebrityPrivate storeCelebrityPrivate = storeCelebrityPrivateService.getById(id);
        return Result.ok(storeCelebrityPrivate);
    }

    /**
     * 导出excel
     *
     * @param request
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, @RequestBody StoreCelebrityPrivateModel storeCelebrityPrivateModel) {

        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "私有网红导出");
        mv.addObject(NormalExcelConstants.CLASS, CelebrityPrivateExportModel.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams());

        List<CelebrityPrivateExportModel> celebrityPrivateList = storeCelebrityPrivateService.getCelebrityPrivateExportList(storeCelebrityPrivateModel);
        if (celebrityPrivateList.isEmpty()) {
            mv.addObject(NormalExcelConstants.DATA_LIST, new ArrayList<>());
            return mv;
        }
        List<String> celebrityPrivateIds = celebrityPrivateList.stream().map(CelebrityPrivateExportModel::getId).toList();

        List<SysDictItem> platformTypeList = dictService.getItemList("platform_type");

        List<CelebrityPrivateExportModel> celebrityPrivateListNew = new ArrayList<>();
        List<StoreCelebrityPrivateCounselor> privateCounselors = counselorService.lambdaQuery().in(StoreCelebrityPrivateCounselor::getCelebrityPrivateId, celebrityPrivateIds).list();
        List<StoreCelebrityPrivateProductModel> privateProducts;
        try {
            CompletableFuture<List<StoreCelebrityPrivateProductModel>> privateProductsFuture = CompletableFuture.supplyAsync(() -> privateProductService.getProductList(celebrityPrivateIds));
            privateProducts = privateProductsFuture.get();
        } catch (Exception e) {
            privateProducts = privateProductService.getProductList(celebrityPrivateIds);
        }


        for (CelebrityPrivateExportModel celebrityPrivateExportModel : celebrityPrivateList) {
            String id = celebrityPrivateExportModel.getId();
            String account = celebrityPrivateExportModel.getAccount();
            Integer gender = celebrityPrivateExportModel.getGender();
            if (gender != null) {
                celebrityPrivateExportModel.setGenderText(gender == 0 ? "男" : "女");
            }
            // 平台类型
            Integer platformType = celebrityPrivateExportModel.getPlatformType();
            Optional<SysDictItem> itemOptional = platformTypeList.stream().filter(x -> x.getItemValue().equals(String.valueOf(platformType))).findFirst();
            itemOptional.ifPresent(sysDictItem -> celebrityPrivateExportModel.setPlatformTypeText(sysDictItem.getItemText()));

            // 需要把数字换成单位 例3000 -> 3k
            Integer followersNum = celebrityPrivateExportModel.getFollowersNum();
            if (followersNum != null) {
                celebrityPrivateExportModel.setFollowersNumStr(oConvertUtils.formatLargeNumber(followersNum));
            }

            // 处理 GMV
            String videoGmvValue = formatGmvValue(celebrityPrivateExportModel.getVideoGmv(), celebrityPrivateExportModel.getMedGmvRevenueRange());
            celebrityPrivateExportModel.setVideoGmvValue(videoGmvValue);

            // 处理均播数据，格式化显示
            String avgNum = celebrityPrivateExportModel.getAvgNum();
            if (avgNum != null && !avgNum.equals("0")) {
                try {
                    Integer avgNumInt = Integer.parseInt(avgNum);
                    celebrityPrivateExportModel.setAvgNum(oConvertUtils.formatLargeNumber(avgNumInt));
                } catch (NumberFormatException e) {
                    log.warn("均播数据格式化失败: {}", avgNum, e);
                    celebrityPrivateExportModel.setAvgNum(avgNum);
                }
            } else {
                celebrityPrivateExportModel.setAvgNum("0");
            }

            // 过滤网红顾问列表
            List<StoreCelebrityPrivateCounselor> privateCounselorList = privateCounselors.stream().filter(x -> x.getCelebrityPrivateId().equals(id)).toList();

            String webUrl = "";
            switch (platformType) {
                case 0:
                    webUrl = "https://www.instagram.com/" + account + "/reels/";
                    break;
                case 1:
                    webUrl = "https://www.youtube.com/@" + account + "/videos";
                    break;
                case 2:
                    webUrl = "https://www.tiktok.com/@" + account;
                    break;
                default:
                    webUrl = "";
                    break;
            }
            celebrityPrivateExportModel.setWebUrl(webUrl);
            if (!privateCounselorList.isEmpty()) {

                for (StoreCelebrityPrivateCounselor privateCounselor : privateCounselorList) {
                    // 检查是否满足合同金额条件
                    if (!isContractAmountMatch(privateCounselor, storeCelebrityPrivateModel)) {
                        continue; // 不满足条件，跳过此条记录
                    }

                    CelebrityPrivateExportModel exportModelNew = new CelebrityPrivateExportModel();
                    BeanUtils.copyProperties(celebrityPrivateExportModel, exportModelNew);
                    exportModelNew.setCounselorName(privateCounselor.getCelebrityCounselorName());
                    exportModelNew.setCounselorId(privateCounselor.getCelebrityCounselorId());
                    // exportModelNew.setContractAmount(privateCounselor.getContractAmount());

                    // 处理contract_info字段，格式化显示
                    String contractInfo = privateCounselor.getContractInfo();
                    if (contractInfo != null && !contractInfo.trim().isEmpty()) {
                        String formattedContractInfo = formatContractInfo(contractInfo);
                        exportModelNew.setContractInfoDetails(formattedContractInfo);
                    }

                    // 处理签约时间
                    Date contractTime = privateCounselor.getContractTime();
                    if (contractTime != null) {
                        String formattedContractTime = DateUtils.date2Str(contractTime, new SimpleDateFormat("yyyy-MM-dd"));
                        exportModelNew.setContractTime(formattedContractTime);
                    } else {
                        exportModelNew.setContractTime("");
                    }
                    //处理网红顾问历史合作产品
                    List<StoreCelebrityPrivateProductModel> productModels = privateProducts.stream().filter(x -> x.getCelebrityId().equals(id) && Objects.equals(x.getCelebrityCounselorId(), privateCounselor.getCelebrityCounselorId())).toList();
                    if (!productModels.isEmpty()) {
                        List<StoreCelebrityPrivateProductModel> privateProductRelationModels = productModels.stream().filter(x -> oConvertUtils.isNotEmpty(x.getRelationStatus()) && x.getRelationStatus() == 1).toList();
                        StringBuilder productCooperateInfo = new StringBuilder();
                        for (StoreCelebrityPrivateProductModel model : privateProductRelationModels) {
                            productCooperateInfo.append(model.getBrandName()).append("-").append(model.getProductName()).append(",");
                        }
                        exportModelNew.setProductCooperateInfo(productCooperateInfo.substring(0, productCooperateInfo.length() - 1));
                    }
                    celebrityPrivateListNew.add(exportModelNew);
                }
            } else {
                celebrityPrivateListNew.add(celebrityPrivateExportModel);
            }
        }
        celebrityPrivateListNew.sort(Comparator.comparing(CelebrityPrivateExportModel::getAccount).thenComparing(CelebrityPrivateExportModel::getContractTime).reversed());
        mv.addObject(NormalExcelConstants.DATA_LIST, celebrityPrivateListNew);
        return mv;
    }


    /**
     * 私有网红导出 - GMV 格式化（复用 formatLargeNumber）
     */
    public static String formatGmvValue(String videoGmvJson, String medGmvJson) {
        if (StringUtils.isBlank(videoGmvJson) || "null".equalsIgnoreCase(videoGmvJson)) {
            videoGmvJson = null;
        }
        if (StringUtils.isBlank(medGmvJson) || "null".equalsIgnoreCase(medGmvJson)) {
            medGmvJson = null;
        }

        String symbol = "$";
        String rawValue = null;

        try {
            ObjectMapper mapper = new ObjectMapper(); // JEECG 项目里 Jackson 肯定有，直接 new 就行

            // 1. 优先解析 videoGmv
            if (videoGmvJson != null) {
                JsonNode node = mapper.readTree(videoGmvJson);
                boolean authorized = node.has("is_authorized") && node.get("is_authorized").asBoolean();
                if (authorized && node.has("value") && node.path("value").has("value")) {
                    rawValue = node.path("value").get("value").asText();
                    if (node.path("value").has("symbol")) {
                        symbol = node.path("value").get("symbol").asText();
                    }
                }
            }

            // 2. videoGmv 无值 → 解析 medGmvRevenueRange
            if (rawValue == null && medGmvJson != null) {
                JsonNode node = mapper.readTree(medGmvJson);
                boolean authorized = node.has("is_authorized") && node.get("is_authorized").asBoolean();
                if (authorized && node.has("value")) {
                    return node.get("value").asText(); // 直接返回 "$25K-$60K"
                }
            }

            // 3. 都没值
            if (rawValue == null) {
                return "";
            }

            // 4. 核心：复用你已有的 formatLargeNumber 方法！
            // "101.30" → 101
            String cleanNumber = rawValue.replaceAll("[,\\s]", ""); // 去掉千分位逗号和空格
            BigDecimal bd = new BigDecimal(cleanNumber);

            String formatted = oConvertUtils.formatLargeNumber(bd.doubleValue());
            return symbol + formatted;

        } catch (Exception e) {
            log.warn("GMV 格式化失败 videoGmvJson={}, medGmvJson={}", videoGmvJson, medGmvJson, e);
            return "";
        }
    }

    /**
     * 下载模板
     */
    @RequestMapping(value = "/downloadXls", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadXls(HttpServletResponse response) throws IOException {
        String userId = getUserIdByToken();
        XSSFWorkbook wb = new XSSFWorkbook(); // 使用 XSSFWorkbook 支持 .xlsx
        XSSFSheet mainSheet = wb.createSheet("Sheet");
        XSSFSheet hiddenSheet = wb.createSheet("HiddenSheet");
        // 设置隐藏 Sheet
        wb.setSheetHidden(wb.getSheetIndex(hiddenSheet), true);
        /*
         * String[] header = {"账号", "性别", "邮箱", "国家", "内容", "签约费用", "建联邮箱", "备注",
         * "达人类型1", "达人类型2", "达人类型3", "物理空间1", "物理空间2", "物理空间3",
         * "人群类型1", "人群类型2", "人群类型3"};
         */
        String[] header = {"账号", "性别", "网红邮箱", "国家", "平台", "内容1", "签约费用1", "内容2", "签约费用2", "内容3", "签约费用3", "建联邮箱", "备注"};
        Row row = mainSheet.createRow(0);
        // 创建一个居中格式
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        for (int i = 0; i < header.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(header[i]);
            cell.setCellStyle(style);
            mainSheet.setColumnWidth(i, 4000);
        }
        setDropdown(wb, mainSheet, hiddenSheet, userId);
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("template.xlsx", "UTF-8"));
            ServletOutputStream outputStream = response.getOutputStream();
            wb.write(outputStream);
            outputStream.flush();
            log.info("Excel file written successfully");
        } catch (IOException e) {
            log.error("Failed to write Excel file", e);
            throw new RuntimeException("Error generating Excel file", e);
        } finally {
            try {
                wb.close();
            } catch (IOException e) {
                log.error("Failed to close workbook", e);
            }
        }
    }

    /**
     * 创建下拉框并将选项存储在隐藏 Sheet 中
     */
    private void createDropdownList(XSSFWorkbook wb, XSSFSheet mainSheet, XSSFSheet hiddenSheet, String[] dropdownValues, int column, String rangeName, int hiddenRowStart) {
        // 检查命名范围是否已存在
        Name existingName = wb.getName(rangeName);
        if (existingName != null) {
            log.warn("Name {} already exists, skipping creation", rangeName);
            return;
        }

        // 将下拉框选项写入隐藏 Sheet
        log.info("Writing dropdown values for range {} at column {}, start row {}: {}", rangeName, column, hiddenRowStart, Arrays.toString(dropdownValues));
        for (int i = 0; i < dropdownValues.length; i++) {
            Row row = hiddenSheet.getRow(i + hiddenRowStart);
            if (row == null) {
                row = hiddenSheet.createRow(i + hiddenRowStart);
            }
            Cell cell = row.createCell(column);
            cell.setCellValue(dropdownValues[i]);
        }

        // 创建命名范围
        Name namedRange = wb.createName();
        namedRange.setNameName(rangeName);
        String reference = String.format("%s!$%s$%d:$%s$%d", hiddenSheet.getSheetName(), getColumnLetter(column), hiddenRowStart + 1, getColumnLetter(column), hiddenRowStart + dropdownValues.length);
        namedRange.setRefersToFormula(reference);
        log.info("Created named range {}: {}", rangeName, reference);

        // 创建数据验证
        CellRangeAddressList addressList = new CellRangeAddressList(1, 100, column, column); // 缩小范围以提高性能
        XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(mainSheet);
        DataValidationConstraint dvConstraint = dvHelper.createFormulaListConstraint(rangeName);
        DataValidation dataValidation = dvHelper.createValidation(dvConstraint, addressList);
        mainSheet.addValidationData(dataValidation);
    }

    /**
     * 将列索引转换为 Excel 列字母（如 0 -> A, 1 -> B, 26 -> AA）
     */
    private String getColumnLetter(int column) {
        StringBuilder colLetter = new StringBuilder();
        while (column >= 0) {
            colLetter.insert(0, (char) ('A' + (column % 26)));
            column = (column / 26) - 1;
        }
        return colLetter.toString();
    }

    /**
     * 设置下拉框
     */
    private void setDropdown(XSSFWorkbook wb, XSSFSheet mainSheet, XSSFSheet hiddenSheet, String userId) {
        // 性别下拉框
        List<String> genderValues = Arrays.asList("男", "女");
        log.info("genderValues: {}", genderValues);
        createDropdownList(wb, mainSheet, hiddenSheet, genderValues.toArray(new String[0]), 1, "GenderList", 0);

        // 平台
        List<DictModel> platformTypeList = sysDictService.queryDictItemsByCode("platform_type");
        String[] platformArray = platformTypeList.stream().map(DictModel::getText).toArray(String[]::new);
        createDropdownList(wb, mainSheet, hiddenSheet, platformArray, 4, "platformType", genderValues.size() + 1);

        // 查询内容
        List<SysDictItem> videoTagsList = dictService.getItemList("output_content");
        List<String> videoTagsValues = new ArrayList<>();
        videoTagsList.forEach(x -> videoTagsValues.add(x.getItemText()));
        log.info("videoTagsValues: {}", videoTagsValues);
        int hiddenRowOffset = genderValues.size() + 1 + platformTypeList.size() + 1;
        if (!videoTagsValues.isEmpty()) {
            createDropdownList(wb, mainSheet, hiddenSheet, videoTagsValues.toArray(new String[0]), 5, "ContentList", hiddenRowOffset);
            createDropdownList(wb, mainSheet, hiddenSheet, videoTagsValues.toArray(new String[0]), 7, "ContentList1", hiddenRowOffset);
            createDropdownList(wb, mainSheet, hiddenSheet, videoTagsValues.toArray(new String[0]), 9, "ContentList2", hiddenRowOffset);
            hiddenRowOffset += videoTagsValues.size() + 1;
        } else {
            log.warn("videoTagsValues is empty, skipping ContentList dropdown");
        }

        // 查询建联邮箱
        List<String> buildEmailValues = new ArrayList<>();
        LambdaQueryWrapper<StoreUserContactEmail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StoreUserContactEmail::getSysUserId, userId);
        queryWrapper.eq(StoreUserContactEmail::getEmailStatus, 1);
        List<StoreUserContactEmail> buildEmails = contactEmailService.list(queryWrapper);
        buildEmails.forEach(x -> buildEmailValues.add(x.getContactEmail()));
        log.info("buildEmailValues: {}", buildEmailValues);
        if (!buildEmailValues.isEmpty()) {
            createDropdownList(wb, mainSheet, hiddenSheet, buildEmailValues.toArray(new String[0]), 11, "EmailList", hiddenRowOffset);
        } else {
            log.warn("buildEmailValues is empty, skipping EmailList dropdown");
        }

        /*
         * // 查询社媒类型和属性
         * List<KolAttributeType> attributeTypeList = attributeTypeService.list();
         * List<Map<String, Object>> kolAttributeLeafNodes =
         * attributeService.getKolAttributeLeafNodes(null, null);
         * int hiddenColOffset = 0;
         *
         * for (Map<String, Object> node : kolAttributeLeafNodes) {
         * String typeId = (String) node.get("typeId");
         * String typeName = (String) node.get("typeName");
         *
         * @SuppressWarnings("unchecked")
         * List<KolAttribute> leafNodes = (List<KolAttribute>) node.get("data");
         * String[] tagsDropdown = leafNodes.stream()
         * .map(KolAttribute::getAttributeName)
         * .map(name -> name.length() > 255 ? name.substring(0, 255) : name)
         * .map(name -> name.replaceAll("[\\n\\r\\t]", ""))
         * .toArray(String[]::new);
         * log.info("tagsDropdown for typeId {}: {}", typeId,
         * Arrays.toString(tagsDropdown));
         *
         * for (KolAttributeType attributeType : attributeTypeList) {
         * if (attributeType.getId().equals(typeId)) {
         * if (attributeType.getTypeCode().equals(CategoryType.INFLUENCER_TYPE_CODE)) {
         * createDropdownList(wb, mainSheet, hiddenSheet, tagsDropdown, 8,
         * "InfluencerType1", hiddenRowOffset);
         * createDropdownList(wb, mainSheet, hiddenSheet, tagsDropdown, 9,
         * "InfluencerType2", hiddenRowOffset);
         * createDropdownList(wb, mainSheet, hiddenSheet, tagsDropdown, 10,
         * "InfluencerType3", hiddenRowOffset);
         * } else if (attributeType.getTypeCode().equals(CategoryType.SCENE_CODE)) {
         * createDropdownList(wb, mainSheet, hiddenSheet, tagsDropdown, 11, "Scene1",
         * hiddenRowOffset);
         * createDropdownList(wb, mainSheet, hiddenSheet, tagsDropdown, 12, "Scene2",
         * hiddenRowOffset);
         * createDropdownList(wb, mainSheet, hiddenSheet, tagsDropdown, 13, "Scene3",
         * hiddenRowOffset);
         * } else if (attributeType.getTypeCode().equals(CategoryType.AUDIENCE_CODE)) {
         * createDropdownList(wb, mainSheet, hiddenSheet, tagsDropdown, 14, "Audience1",
         * hiddenRowOffset);
         * createDropdownList(wb, mainSheet, hiddenSheet, tagsDropdown, 15, "Audience2",
         * hiddenRowOffset);
         * createDropdownList(wb, mainSheet, hiddenSheet, tagsDropdown, 16, "Audience3",
         * hiddenRowOffset);
         * }
         * }
         * }
         * hiddenRowOffset += tagsDropdown.length + 1;
         * hiddenColOffset++;
         * }
         */
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @AutoLog(value = "私有网红表-通过excel导入数据")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        String userId = getUserIdByToken();

        SysUser sysUser = sysUserService.getById(userId);
        // sysUser.setUserType(4);
        if (!sysUser.getUserType().equals(UserType.CELEBRITY_COUNSELOR.getCode())) {
            return Result.error("当前用户无法导入网红");
        }
        String userName = getUserNameByToken();
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();
            ImportParams params = new ImportParams();
            // params.setTitleRows(1);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                // 获取上传文件列表
                /*
                 * List<StoreCelebrityPrivateExcelModel> list =
                 * ExcelImportUtil.importExcel(file.getInputStream(),
                 * StoreCelebrityPrivateExcelModel.class, params);
                 * return privateService.importStoreCelebrityPrivate(list, userId, userName);
                 */
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.getMessage());
                return Result.error("文件导入失败,请检查文件内容是否正确！");
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.error("文件导入失败，请检查文件内容是否正确");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importContactEmailExcel", method = RequestMethod.POST)
    public Result<?> importContactEmailExcel(HttpServletRequest request, HttpServletResponse response) {
        String userId = getUserIdByToken();

        SysUser sysUser = sysUserService.getById(userId);
        // sysUser.setUserType(4);
        if (!sysUser.getUserType().equals(UserType.CELEBRITY_COUNSELOR.getCode())) {
            return Result.error("当前用户无法导入网红");
        }
        String userName = getUserNameByToken();
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();
            ImportParams params = new ImportParams();
            // params.setTitleRows(1);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                long start = System.currentTimeMillis();
                // 获取上传文件列表
                List<StoreCelebrityPrivateExcelModel> list = ExcelImportUtil.importExcel(file.getInputStream(), StoreCelebrityPrivateExcelModel.class, params);
                list = list.stream().filter(x -> oConvertUtils.isNotEmpty(x.getAccount())).toList();
                if (list.isEmpty()) {
                    return Result.error("文件导入失败，请检查文件内容是否正确！");
                }
                Set<StoreCelebrityPrivateExcelModel> errorList = new HashSet<>();

                List<StoreCelebrityPrivateExcelModel> emptyContactEmailList = list.stream().filter(x -> oConvertUtils.isEmpty(x.getCelebrityContactEmail())).toList();
                if (!emptyContactEmailList.isEmpty()) {
                    emptyContactEmailList.forEach(x -> addToErrorList(x, "建联邮箱不能为空", errorList));
                }
                List<StoreCelebrityPrivateExcelModel> emptyAccount = list.stream().filter(x -> oConvertUtils.isEmpty(x.getAccount())).toList();
                if (!emptyAccount.isEmpty()) {
                    emptyAccount.forEach(x -> addToErrorList(x, "账号不能为空", errorList));
                }

                if (!errorList.isEmpty()) {
                    return Result.OK("文件导入失败，请检查以下数据", errorList);
                }

                List<StoreCelebrityPrivate> listNew = new ArrayList<>();
                for (StoreCelebrityPrivateExcelModel storeCelebrityPrivateExcelModel : list) {
                    String account = storeCelebrityPrivateExcelModel.getAccount();
                    String celebrityContactEmail = storeCelebrityPrivateExcelModel.getCelebrityContactEmail();
                    String platformTypeText = storeCelebrityPrivateExcelModel.getPlatformTypeText();
                    List<SysDictItem> platformTypeList = dictService.getItemList("platform_type");
                    SysDictItem sysDictItem = platformTypeList.stream().filter(x -> x.getItemText().trim().equals(platformTypeText.trim())).findFirst().orElse(null);
                    int platformType = 0;
                    if (sysDictItem != null) {
                        platformType = Integer.parseInt(sysDictItem.getItemValue());
                    }

                    // 数据去重
                    int platformTypeFinal = platformType;
                    boolean flag = listNew.stream().anyMatch(x -> x.getAccount().equals(account) && x.getPlatformType().equals(platformTypeFinal));
                    if (flag) {
                        continue;
                    }
                    StoreCelebrityPrivate storeCelebrityPrivate = new StoreCelebrityPrivate();
                    BeanUtils.copyProperties(storeCelebrityPrivateExcelModel, storeCelebrityPrivate);
                    storeCelebrityPrivate.setPlatformType(platformType);
                    // 查询当前网红和平台是否存在
                    StoreCelebrityPrivate checked = storeCelebrityPrivateService.checkCelebrityPrivate(account, platformType);
                    if (checked != null) {
                        storeCelebrityPrivate.setId(checked.getId());
                    }
                    storeCelebrityPrivate.setContractTime(new Date());
                    storeCelebrityPrivate.setUpdateTime(new Date());
                    storeCelebrityPrivate.setIsMcn(0);
                    storeCelebrityPrivate.setCelebrityCounselorId(userId);
                    storeCelebrityPrivate.setCelebrityCounselorName(userName);
                    storeCelebrityPrivate.setCelebrityContactEmail(celebrityContactEmail);
                    /*
                     * if (oConvertUtils.isNotEmpty(storeCelebrityPrivate.getPersonalTags())) {
                     * updatePersonalTags(storeCelebrityPrivate.getPersonalTags());
                     * }
                     */
                    listNew.add(storeCelebrityPrivate);
                }

                List<StoreCelebrityPrivateCounselor> privateCounselors = new ArrayList<>();
                Map<Integer, List<StoreCelebrityPrivate>> listMap = listNew.stream().collect(Collectors.groupingBy(StoreCelebrityPrivate::getPlatformType));
                for (Map.Entry<Integer, List<StoreCelebrityPrivate>> entry : listMap.entrySet()) {
                    Integer platformType = entry.getKey();
                    List<StoreCelebrityPrivate> celebrityPrivates = entry.getValue();
                    List<String> accounts = celebrityPrivates.stream().map(StoreCelebrityPrivate::getAccount).toList();
                    // 查询数据库中前端传输的网红是否存在非MCN的网红列表
                    List<StoreCelebrityPrivate> celebrityPrivateList = storeCelebrityPrivateService.getMcnByAccounts(accounts, 0, platformType);
                    if (!celebrityPrivateList.isEmpty()) {
                        celebrityPrivateList.forEach(x -> {
                            String account = x.getAccount();
                            Optional<StoreCelebrityPrivate> first = celebrityPrivates.stream().filter(a -> a.getAccount().equals(account)).findFirst();
                            first.ifPresent(storeCelebrityPrivate -> {
                                // 查询是否存在相同网红顾问
                                StoreCelebrityPrivateCounselor celebrityCounselor = privateCounselorService.getByCelebrityCounselorId(userId, x.getId());
                                storeCelebrityPrivate.setId(x.getId());
                                if (celebrityCounselor == null) {
                                    StoreCelebrityPrivateCounselor privateCounselor = new StoreCelebrityPrivateCounselor();
                                    privateCounselor.setCelebrityPrivateId(x.getId());
                                    privateCounselor.setCelebrityCounselorId(userId);
                                    privateCounselor.setCelebrityCounselorName(userName);
                                    privateCounselor.setContractAmount(storeCelebrityPrivate.getContractAmount());
                                    privateCounselor.setEmail(storeCelebrityPrivate.getEmail());
                                    privateCounselor.setCelebrityContactEmail(storeCelebrityPrivate.getCelebrityContactEmail());
                                    privateCounselor.setContractTime(storeCelebrityPrivate.getContractTime());
                                    privateCounselor.setSort(1);
                                    privateCounselors.add(privateCounselor);
                                } else {
                                    StoreCelebrityPrivateCounselor privateCounselor = new StoreCelebrityPrivateCounselor();
                                    privateCounselor.setId(celebrityCounselor.getId());
                                    // privateCounselor.setContractAmount(storeCelebrityPrivate.getContractAmount());
                                    privateCounselor.setEmail(storeCelebrityPrivate.getEmail());
                                    privateCounselor.setContractTime(storeCelebrityPrivate.getContractTime());
                                    privateCounselor.setCelebrityContactEmail(storeCelebrityPrivate.getCelebrityContactEmail());
                                    privateCounselor.setSort(1);
                                    privateCounselors.add(privateCounselor);
                                }
                            });
                        });
                    }
                }
                List<StoreCelebrityPrivate> celebrityPrivateList = listNew.stream().filter(x -> oConvertUtils.isEmpty(x.getId())).toList();
                for (StoreCelebrityPrivate storeCelebrityPrivate : celebrityPrivateList) {
                    storeCelebrityPrivate.setId(IdWorker.get32UUID());
                    StoreCelebrityPrivateCounselor privateCounselor = new StoreCelebrityPrivateCounselor();
                    privateCounselor.setCelebrityPrivateId(storeCelebrityPrivate.getId());
                    privateCounselor.setCelebrityCounselorId(userId);
                    privateCounselor.setCelebrityCounselorName(userName);
                    privateCounselor.setContractAmount(storeCelebrityPrivate.getContractAmount());
                    privateCounselor.setCelebrityContactEmail(storeCelebrityPrivate.getCelebrityContactEmail());
                    privateCounselor.setEmail(storeCelebrityPrivate.getEmail());
                    privateCounselor.setSort(1);
                    privateCounselors.add(privateCounselor);
                }
                storeCelebrityPrivateService.saveOrUpdateBatch(listNew);
                if (!privateCounselors.isEmpty()) {
                    privateCounselorService.saveOrUpdateBatch(privateCounselors);
                }
                log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
                return Result.ok("文件导入成功！数据行数：" + listNew.size());
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.getMessage());
                return Result.error("文件导入失败,请检查文件内容是否正确！");
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.error("文件导入失败，请检查文件内容是否正确");
    }

    private void addToErrorList(StoreCelebrityPrivateExcelModel model, String failReason, Set<StoreCelebrityPrivateExcelModel> errorList) {
        String account = model.getAccount();
        Optional<StoreCelebrityPrivateExcelModel> modelOptional = errorList.stream().filter(x -> x.getAccount().equals(account)).findFirst();
        if (modelOptional.isPresent()) {
            model = modelOptional.get();
            String existingFailReason = model.getFailReason();

            // 将现有的失败原因按分隔符拆分，并去重
            Set<String> reasonSet = new LinkedHashSet<>();
            if (oConvertUtils.isNotEmpty(existingFailReason)) {
                String[] existingReasons = existingFailReason.split("、");
                for (String reason : existingReasons) {
                    String trimmedReason = reason.trim();
                    if (oConvertUtils.isNotEmpty(trimmedReason)) {
                        reasonSet.add(trimmedReason);
                    }
                }
            }

            // 添加新的失败原因（如果不存在）
            if (oConvertUtils.isNotEmpty(failReason)) {
                reasonSet.add(failReason.trim());
            }

            // 重新组装去重后的失败原因
            model.setFailReason(String.join("、", reasonSet));
        } else {
            model.setFailReason(failReason);
            errorList.add(model);
        }
    }

    /**
     * 功能描述:获取字典值
     *
     * @return java.lang.Integer
     * @Date 2023-09-18 11:08:00
     */
    private Integer getItemVal(List<SysDictItem> dictItems, String val) {
        try {
            return Integer.valueOf(dictItems.stream().filter(x -> x.getItemText().equals(val)).findFirst().get().getItemValue());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importMcnExcel", method = RequestMethod.POST)
    public Result<?> importMcnExcel(HttpServletRequest request, HttpServletResponse response) {
        String userId = getUserIdByToken();
        String userName = getUserNameByToken();
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();
            ImportParams params = new ImportParams();
            // params.setTitleRows(1);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                long start = System.currentTimeMillis();
                // 获取上传文件列表
                List<StoreCelebrityPrivateExcelModel> list = ExcelImportUtil.importExcel(file.getInputStream(), StoreCelebrityPrivateExcelModel.class, params);
                if (list.isEmpty()) {
                    return Result.error("文件导入失败，请检查文件内容是否正确！");
                }
                List<StoreCelebrityPrivate> listNew = new ArrayList<>();
                for (StoreCelebrityPrivateExcelModel storeCelebrityPrivateExcelModel : list) {
                    if (oConvertUtils.isEmpty(storeCelebrityPrivateExcelModel.getAccount())) {
                        continue;
                    }
                    StoreCelebrityPrivate storeCelebrityPrivate = new StoreCelebrityPrivate();
                    BeanUtils.copyProperties(storeCelebrityPrivateExcelModel, storeCelebrityPrivate);
                    // String playAvgNumStr = storeCelebrityPrivateExcelModel.getPlayAvgNumStr();
                    // String followersNumStr =
                    // storeCelebrityPrivateExcelModel.getFollowersNumStr();
                    // storeCelebrityPrivate.setPlayAvgNum(getNum(playAvgNumStr));
                    // storeCelebrityPrivate.setFollowersNum(getNum(followersNumStr));
                    storeCelebrityPrivate.setCelebrityCounselorId(userId);
                    storeCelebrityPrivate.setCelebrityCounselorName(userName);
                    storeCelebrityPrivate.setContractTime(new Date());
                    storeCelebrityPrivate.setUpdateTime(new Date());
                    storeCelebrityPrivate.setIsMcn(1);
                    listNew.add(storeCelebrityPrivate);
                }
                if (listNew.isEmpty()) {
                    return Result.error("文件导入失败，请检查文件内容是否正确！");
                }
                List<StoreCelebrityPrivateCounselor> privateCounselors = new ArrayList<>();
                // 去重
                List<String> accounts = listNew.stream().map(StoreCelebrityPrivate::getAccount).toList();
                List<StoreCelebrityPrivate> celebrityPrivateList = storeCelebrityPrivateService.getMcnByAccounts(accounts, 1, null);
                if (!celebrityPrivateList.isEmpty()) {
                    celebrityPrivateList.forEach(x -> {
                        String account = x.getAccount();
                        Optional<StoreCelebrityPrivate> first = listNew.stream().filter(a -> a.getAccount().equals(account)).findFirst();
                        first.ifPresent(storeCelebrityPrivate -> {
                            storeCelebrityPrivate.setId(x.getId());
                            // 查询是否存在相同网红顾问
                            StoreCelebrityPrivateCounselor counselor = privateCounselorService.getByCelebrityCounselorId(userId, x.getId());
                            if (counselor == null) {
                                StoreCelebrityPrivateCounselor privateCounselor = new StoreCelebrityPrivateCounselor();
                                privateCounselor.setCelebrityPrivateId(x.getId());
                                privateCounselor.setCelebrityCounselorId(userId);
                                privateCounselor.setCelebrityCounselorName(userName);
                                privateCounselor.setContractAmount(x.getContractAmount());
                                privateCounselor.setEmail(x.getEmail());
                                privateCounselor.setSort(1);
                                privateCounselor.setContractTime(storeCelebrityPrivate.getContractTime());
                                privateCounselors.add(privateCounselor);
                            }
                        });
                    });
                }
                List<StoreCelebrityPrivate> celebrityPrivates = listNew.stream().filter(x -> oConvertUtils.isEmpty(x.getId())).toList();
                for (StoreCelebrityPrivate storeCelebrityPrivate : celebrityPrivates) {
                    storeCelebrityPrivate.setId(IdWorker.get32UUID());
                    StoreCelebrityPrivateCounselor privateCounselor = new StoreCelebrityPrivateCounselor();
                    privateCounselor.setCelebrityPrivateId(storeCelebrityPrivate.getId());
                    privateCounselor.setCelebrityCounselorId(userId);
                    privateCounselor.setCelebrityCounselorName(userName);
                    privateCounselor.setContractAmount(storeCelebrityPrivate.getContractAmount());
                    privateCounselor.setEmail(storeCelebrityPrivate.getEmail());
                    privateCounselor.setSort(1);
                    privateCounselor.setContractTime(storeCelebrityPrivate.getContractTime());
                    privateCounselors.add(privateCounselor);
                }
                storeCelebrityPrivateService.saveOrUpdateBatch(listNew);
                if (!privateCounselors.isEmpty()) {
                    privateCounselorService.saveBatch(privateCounselors);
                }
                log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
                return Result.ok("文件导入成功！数据行数：" + listNew.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败,请检查文件内容是否正确！");
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.error("文件导入失败，请检查文件内容是否正确");
    }

    private Integer getNum(String str) {
        System.out.println(str);
        if (oConvertUtils.isEmpty(str)) {
            return null;
        }
        str = str.toUpperCase();
        try {
            if (str.contains("K")) {
                return new BigDecimal(str.replace("K", "")).multiply(new BigDecimal(1000)).intValue();
            } else if (str.contains("M")) {
                return new BigDecimal(str.replace("M", "")).multiply(new BigDecimal(1000000)).intValue();
            }
            return new BigDecimal(str).intValue();
        } catch (Exception e) {
            log.info("字符串转换数字错误：{}", str);
        }
        return null;
    }

    /**
     * 查询所有网红
     *
     * @Author zhoushen
     */
    @AutoLog(value = "查询所有网红")
    @Operation(summary = "查询所有网红", description = "查询所有网红")
    @GetMapping(value = "/queryAll")
    public Result<?> queryAll() {
        LambdaQueryWrapper<StoreCelebrityPrivate> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreCelebrityPrivate::getIsMcn, 0);
        List<StoreCelebrityPrivate> list = storeCelebrityPrivateService.list(lambdaQueryWrapper);
        return Result.ok(list);
    }

    /**
     * 功能描述:根据产品id获取分配网红信息
     *
     * @Author: nqr
     * @Date 2021-02-08 10:56:43
     */
    @RequestMapping(value = "/getKolData", method = RequestMethod.GET)
    public Result<?> getKolData(@RequestParam(name = "id", required = false) String id) {
        List<StoreCelebrityPrivateModel> list = storeCelebrityPrivateService.getKolData(id);
        return Result.ok(list);
    }

    /**
     * 功能描述:根据产品id获取分配网红信息
     *
     * @Author: nqr
     * @Date 2021-02-08 10:56:43
     */
    @RequestMapping(value = "/getCelebrityPrivateData", method = RequestMethod.GET)
    public Result<?> getCelebrityPrivateData(@RequestParam(name = "account", required = true) String account, @RequestParam(name = "platformType", required = true) Integer platformType) {
        StoreCelebrityPrivate celebrityPrivates = storeCelebrityPrivateService.checkCelebrityPrivate(account, platformType);
        return Result.ok(celebrityPrivates);
    }

    /**
     * 功能描述:根据推广id获取网红信息
     *
     * @param promId
     * @return
     * @Date 2021-06-17 16:31:35
     */
    @AutoLog(value = "根据推广id获取网红信息")
    @Operation(summary = "根据推广id获取网红信息", description = "根据推广id获取网红信息")
    @GetMapping(value = "/getCelebrityData")
    public Result<?> getCelebrityData(@RequestParam(name = "promId", required = true) String promId) {
        return Result.ok(storeCelebrityPrivateService.getCelebrityData(promId));
    }

    @AutoLog(value = "私有网红表-获取所有符合条件的私有网红列表")
    @Operation(summary = "私有网红表-获取所有符合条件的私有网红列表", description = "私有网红表-获取所有符合条件的私有网红列表")
    @GetMapping(value = "/getAllCelebrityPrivates")
    public Result<?> getAllCelebrityPrivates(StoreCelebrityPrivateModel storeCelebrityPrivateModel) {
        if (oConvertUtils.isNotEmpty(storeCelebrityPrivateModel.getFollowersEndNum())) {
            Integer followersEndNum = storeCelebrityPrivateModel.getFollowersEndNum() * 1000;
            storeCelebrityPrivateModel.setFollowersEndNum(followersEndNum);
        }
        if (oConvertUtils.isNotEmpty(storeCelebrityPrivateModel.getFollowersStartNum())) {
            Integer postsStartNum = storeCelebrityPrivateModel.getFollowersStartNum() * 1000;
            storeCelebrityPrivateModel.setFollowersStartNum(postsStartNum);
        }
        if (oConvertUtils.isNotEmpty(storeCelebrityPrivateModel.getPlayAvgStartNum())) {
            Integer playAvgStartNum = storeCelebrityPrivateModel.getPlayAvgStartNum() * 1000;
            storeCelebrityPrivateModel.setPlayAvgStartNum(playAvgStartNum);
        }
        if (oConvertUtils.isNotEmpty(storeCelebrityPrivateModel.getPlayAvgEndNum())) {
            Integer playAvgEndNum = storeCelebrityPrivateModel.getPlayAvgEndNum() * 1000;
            storeCelebrityPrivateModel.setPlayAvgEndNum(playAvgEndNum);
        }
        if (oConvertUtils.isNotEmpty(storeCelebrityPrivateModel.getColumn())) {
            storeCelebrityPrivateModel.setColumn(oConvertUtils.camelToUnderline(storeCelebrityPrivateModel.getColumn()));
        }
        List<StoreCelebrityPrivateModel> celebrityPrivateModels = storeCelebrityPrivateService.getAllCelebrityPrivates(storeCelebrityPrivateModel);

        return Result.ok(celebrityPrivateModels);
    }

    @AutoLog(value = "私有网红-一键私有-" + SystemConstants.ADD)
    @Operation(summary = "私有网红-一键私有-" + SystemConstants.ADD, description = "私有网红-一键私有-" + SystemConstants.ADD)
    @PostMapping(value = "/celebrityPrivateAdd")
    public Result<?> celebrityPrivateAdd(@RequestBody StoreCelebrityPrivateParams privateParams) {

        // 1. 基础验证
        Result<?> validationResult = validateInput(privateParams);
        if (!validationResult.isSuccess()) {
            return validationResult;
        }

        // 2. 用户权限验证
        String userId = getUserIdByToken();
        String userName = getUserNameByToken();
        Result<?> userValidation = validateUser(userId);
        if (!userValidation.isSuccess()) {
            return userValidation;
        }

        try {
            long start = System.currentTimeMillis();
            List<StoreCelebrityPrivateAttributeRelation> privateAttributeRelations = new ArrayList<>();
            List<StoreCelebrityPrivatePersonalTags> privatePersonalTagsList = new ArrayList<>();
            HashMap<String, List<PrivateContractInfo>> privateContractInfos = new HashMap<>();
            List<TiktokCelebrity> tiktokCelebrities = new ArrayList<>();
            List<YoutubeCelebrity> youtubeCelebrities = new ArrayList<>();
            List<IgCelebrity> igCelebrities = new ArrayList<>();
            // 3. 处理网红数据
            List<StoreCelebrityPrivate> processedCelebrities = processCelebrityData(privateParams.getCelebrityPrivates(), privateAttributeRelations, userId, userName, privateContractInfos, privatePersonalTagsList);

            // 4. 处理顾问关系数据
            List<StoreCelebrityPrivateCounselor> counselorRelations = processCounselorRelations(processedCelebrities, userId, userName, privateContractInfos);

            //6、处理网红ID
            getCelebritys(privateParams, tiktokCelebrities, youtubeCelebrities, igCelebrities);

            // 5. 批量保存数据
            privateService.saveBatchData(processedCelebrities, counselorRelations, privateAttributeRelations, privatePersonalTagsList, tiktokCelebrities, youtubeCelebrities, igCelebrities);


            log.info("网红私有导入完成，耗时: {}ms", System.currentTimeMillis() - start);
            return Result.ok("一键私有成功");

        } catch (Exception ex) {
            log.error("网红私有导入失败", ex);
            return Result.error("一键私有失败");
        }
    }

    private void getCelebritys(StoreCelebrityPrivateParams privateParams, List<TiktokCelebrity> tiktokCelebrities, List<YoutubeCelebrity> youtubeCelebrities, List<IgCelebrity> igCelebrities) {
        for (StoreCelebrityPrivateModel cp : privateParams.getCelebrityPrivates()) {
            String email = cp.getEmail();
            String kolId = cp.getKolId();
            Integer platformType = cp.getPlatformType();
            if (oConvertUtils.isNotEmpty(email) && oConvertUtils.isNotEmpty(kolId)) {
                switch (platformType) {
                    case CommonConstant.TK:
                        TiktokCelebrity tiktokCelebrity = new TiktokCelebrity();
                        tiktokCelebrity.setId(kolId);
                        tiktokCelebrity.setEmail(email);
                        tiktokCelebrities.add(tiktokCelebrity);
                        break;
                    case CommonConstant.YT:
                        YoutubeCelebrity youtubeCelebrity = new YoutubeCelebrity();
                        youtubeCelebrity.setId(kolId);
                        youtubeCelebrity.setEmail(email);
                        youtubeCelebrities.add(youtubeCelebrity);
                        break;
                    case CommonConstant.IG:
                        IgCelebrity igCelebrity = new IgCelebrity();
                        igCelebrity.setId(kolId);
                        igCelebrity.setEmail(email);
                        igCelebrities.add(igCelebrity);
                }
            }
        }
    }

    /**
     * 输入验证
     */
    private Result<?> validateInput(StoreCelebrityPrivateParams privateParams) {
        List<StoreCelebrityPrivateModel> celebrityPrivates = privateParams.getCelebrityPrivates();

        if (celebrityPrivates.isEmpty()) {
            return Result.error("请选择网红");
        }

        /*
         * // 验证签约费用
         * boolean hasInvalidAmount = celebrityPrivates.stream()
         * .anyMatch(cp -> cp.getContractAmount().compareTo(BigDecimal.ZERO) <= 0);
         *
         * if (hasInvalidAmount) {
         * return Result.error("列表中网红签约费用不能小于0");
         * }
         */

        // 验证签约费用
        boolean isContractInfoListEmpty = celebrityPrivates.stream().anyMatch(cp -> cp.getPrivateContractInfoList().isEmpty());

        if (isContractInfoListEmpty) {
            return Result.error("视频内容签约费用不能为空");
        }

        // 验证签约费用
        boolean hasInvalidAmount = celebrityPrivates.stream().anyMatch(cp -> cp.getPrivateContractInfoList().stream().anyMatch(pc -> pc.getContractAmount().compareTo(BigDecimal.ZERO) <= 0));

        if (hasInvalidAmount) {
            return Result.error("列表中网红签约费用不能小于0");
        }
        return Result.ok();
    }

    /**
     * 用户权限验证
     */
    private Result<?> validateUser(String userId) {
        SysUser sysUser = sysUserService.getById(userId);
        int code = UserType.CELEBRITY_COUNSELOR.getCode();
        if (code != sysUser.getUserType()) {
            return Result.error("当前用户无法导入网红");
        }
        return Result.ok();
    }

    /**
     * 处理网红数据
     */
    private List<StoreCelebrityPrivate> processCelebrityData(List<StoreCelebrityPrivateModel> celebrityModels, List<StoreCelebrityPrivateAttributeRelation> privateAttributeRelations, String userId, String userName, HashMap<String, List<PrivateContractInfo>> privateContractInfos, List<StoreCelebrityPrivatePersonalTags> privatePersonalTagsList) {
        List<StoreCelebrityPrivate> celebrityPrivates = new ArrayList<>();
        for (StoreCelebrityPrivateModel model : celebrityModels) {
            StoreCelebrityPrivate celebrityPrivate = createCelebrityPrivate(model, privateAttributeRelations, userId, userName, privateContractInfos, privatePersonalTagsList);
            celebrityPrivates.add(celebrityPrivate);
        }
        return celebrityPrivates;
    }

    /**
     * 创建网红私有对象
     */
    private StoreCelebrityPrivate createCelebrityPrivate(StoreCelebrityPrivateModel model, List<StoreCelebrityPrivateAttributeRelation> privateAttributeRelations, String userId, String userName, HashMap<String, List<PrivateContractInfo>> privateContractInfos, List<StoreCelebrityPrivatePersonalTags> privatePersonalTagsList) {
        List<PrivateContractInfo> privateContractInfoList = model.getPrivateContractInfoList();
        // BigInteger playAvgNum = BigInteger.ZERO;

        StoreCelebrityPrivate celebrity = new StoreCelebrityPrivate();
        BeanUtils.copyProperties(model, celebrity);
        /*
         * if (model.getPlatformType() == 2) {
         * TiktokCelebrity tiktokCelebrity =
         * tiktokCelebrityService.lambdaQuery().eq(TiktokCelebrity::getSecUid,
         * model.getCelebrityId()).one();
         * playAvgNum = BigInteger.valueOf(tiktokCelebrity.getPlayAvgNum());
         * } else if (model.getPlatformType() == 1) {
         * YoutubeCelebrity ytCelebrity =
         * ytCelebrityService.lambdaQuery().eq(YoutubeCelebrity::getAccount,
         * model.getCelebrityId()).one();
         * playAvgNum = ytCelebrity.getPlayAvgNum();
         * } else {
         * IgCelebrity igCelebrity =
         * igCelebrityService.lambdaQuery().eq(IgCelebrity::getAccount,
         * model.getCelebrityId()).one();
         * playAvgNum = BigInteger.valueOf(igCelebrity.getAvgPlayCount());
         * }
         */
        // 检查是否已存在
        StoreCelebrityPrivate existing = storeCelebrityPrivateService.checkCelebrityPrivate(model.getAccount(), model.getPlatformType());

        if (existing != null) {
            celebrity.setId(existing.getId());
        } else {
            celebrity.setId(IdWorker.get32UUID());
            // celebrity.setPlayAvgNum(playAvgNum);
        }
        privateContractInfos.put(celebrity.getId(), privateContractInfoList);
        // 设置基础字段
        celebrity.setContractTime(new Date());
        celebrity.setUpdateTime(new Date());
        celebrity.setIsMcn(0);
        celebrity.setCelebrityCounselorId(userId);
        celebrity.setCelebrityCounselorName(userName);
        celebrity.setCelebrityContactEmail(model.getCelebrityContactEmail());

        // 设置国家信息
        StoreCountry country = storeCountryService.getCountryByCode(model.getCountryName());
        if (country != null) {
            celebrity.setCountryId(country.getId());
            celebrity.setCountryName(country.getCountry());
        }
        // 处理个人标签
        if (oConvertUtils.isNotEmpty(model.getPersonalTagIds())) {
            storeCelebrityPrivateService.processingPersonalizedTagsNew(privatePersonalTagsList, celebrity.getId(), model.getPersonalTagIds());
        }

        // 5. 创建网红社媒属性关联关系
        // if (!dataList.isEmpty()) {
        // List<StoreCelebrityPrivateAttributeRelation> attributeRelations =
        // privateAttributeRelationService.createAttributeRelation(celebrity, dataList);
        // privateAttributeRelations.addAll(attributeRelations);
        // }
        return celebrity;
    }

    /**
     * 处理顾问关系数据
     */
    private List<StoreCelebrityPrivateCounselor> processCounselorRelations(List<StoreCelebrityPrivate> celebrities, String userId, String userName, HashMap<String, List<PrivateContractInfo>> privateContractInfos) {

        return celebrities.stream().map(celebrity -> createCounselorRelation(celebrity, userId, userName, privateContractInfos)).toList();
    }

    /**
     * 创建顾问关系对象
     */
    private StoreCelebrityPrivateCounselor createCounselorRelation(StoreCelebrityPrivate celebrity, String userId, String userName, HashMap<String, List<PrivateContractInfo>> privateContractInfos) {

        // 检查是否已存在顾问关系
        StoreCelebrityPrivateCounselor existing = privateCounselorService.getByCelebrityCounselorId(userId, celebrity.getId());

        StoreCelebrityPrivateCounselor counselor = new StoreCelebrityPrivateCounselor();
        List<PrivateContractInfo> newContractInfoList = privateContractInfos.get(celebrity.getId());

        // 如果存在原有记录，保留原有建联信息，但contractInfo需要合并处理
        if (existing != null) {
            counselor.setId(existing.getId());

            // 合并合同信息：保留原有的合同信息
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                List<PrivateContractInfo> mergedContractInfoList = new ArrayList<>();

                // 获取原有的合同信息
                String existingContractInfo = existing.getContractInfo();
                if (existingContractInfo != null && !existingContractInfo.equals("[]") && !existingContractInfo.isEmpty()) {
                    List<PrivateContractInfo> existingContractInfoList = objectMapper.readValue(existingContractInfo, objectMapper.getTypeFactory().constructCollectionType(List.class, PrivateContractInfo.class));
                    mergedContractInfoList.addAll(existingContractInfoList);
                }

                // 添加新的合同信息，如果有重复的videoTag则替换
                if (newContractInfoList != null && !newContractInfoList.isEmpty()) {
                    // 创建一个Map来快速查找现有的videoTag
                    Map<String, PrivateContractInfo> existingVideoTagMap = mergedContractInfoList.stream().collect(Collectors.toMap(PrivateContractInfo::getVideoTag, Function.identity(), (oldValue, newValue) -> newValue));

                    // 处理新的合同信息
                    for (PrivateContractInfo newContractInfo : newContractInfoList) {
                        if (existingVideoTagMap.containsKey(newContractInfo.getVideoTag())) {
                            // 如果已存在相同的videoTag，替换它
                            mergedContractInfoList.remove(existingVideoTagMap.get(newContractInfo.getVideoTag()));
                            mergedContractInfoList.add(newContractInfo);
                        } else {
                            // 如果不存在相同的videoTag，直接添加
                            mergedContractInfoList.add(newContractInfo);
                        }
                    }
                }

                // 序列化合并后的合同信息为JSON字符串
                String contractInfoJson = objectMapper.writeValueAsString(mergedContractInfoList);
                counselor.setContractInfo(contractInfoJson);
            } catch (Exception e) {
                throw new RuntimeException("Failed to merge and serialize contract info to JSON", e);
            }
        } else {
            // 如果不存在原有记录，创建新记录
            // 序列化新的合同信息为JSON字符串
            try {
                if (newContractInfoList != null && !newContractInfoList.isEmpty()) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    String contractInfoJson = objectMapper.writeValueAsString(newContractInfoList);
                    counselor.setContractInfo(contractInfoJson);
                } else {
                    // 如果新的合同信息为空，设置contract_info为null或空JSON数组
                    counselor.setContractInfo("[]");
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to serialize contract info to JSON", e);
            }
        }
        counselor.setCelebrityPrivateId(celebrity.getId());
        counselor.setCelebrityCounselorId(userId);
        counselor.setCelebrityCounselorName(userName);
        counselor.setContractAmount(celebrity.getContractAmount());
        counselor.setEmail(celebrity.getEmail());
        counselor.setCelebrityContactEmail(celebrity.getCelebrityContactEmail());
        counselor.setSort(1);
        counselor.setContractTime(celebrity.getContractTime());
        return counselor;
    }

    /**
     * 方法描述:私有网红模块增加导入网红成本功能， 导入网红成本包括（日期、网红顾问、网红名（id/昵称）、平台、网红成本），
     * 如果EXCEL中同一个网红有多个成本，则取最新、最高成本的那条记录，不能更新网红的签约时间
     *
     * @author nqr
     * @date 2024/10/21 15:46
     **/
    @RequestMapping(value = "/importContractAmt", method = RequestMethod.POST)
    public Result<?> importContractAmt(HttpServletRequest request, HttpServletResponse response) {
        String userId = getUserIdByToken();
        SysUser sysUser = sysUserService.getById(userId);
        if (!sysUser.getUserType().equals(UserType.CELEBRITY_COUNSELOR.getCode())) {
            return Result.error("当前用户无法导入网红");
        }
        List<SysUser> users = sysUserService.getCounselorList();
        List<SysDictItem> platformTypeList = dictService.getItemList("platform_type");

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();
            ImportParams params = new ImportParams();
            // params.setTitleRows(1);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                long start = System.currentTimeMillis();
                // 获取上传文件列表
                List<ContractAmtModel> list = WorkBookUtil.getWorkbook(file.getInputStream(), ContractAmtModel.class);

                // List<ContractAmtModel> list =
                // ExcelImportUtil.importExcel(file.getInputStream(), ContractAmtModel.class,
                // params);
                list = list.stream().filter(x -> oConvertUtils.isNotEmpty(x.getAccount())).toList();
                if (list.isEmpty()) {
                    return Result.error("文件导入失败，请检查文件内容是否正确！");
                }
                List<String> accounts = list.stream().map(ContractAmtModel::getAccount).distinct().toList();
                List<StoreCelebrityPrivate> celebrityPrivates = storeCelebrityPrivateService.listByAccounts(accounts);
                List<String> celebrityIds = celebrityPrivates.stream().map(StoreCelebrityPrivate::getId).toList();
                List<StoreCelebrityPrivateCounselor> privateCounselors = privateCounselorService.getByCelebrityIds(celebrityIds);
                List<String> errors = new ArrayList<>();
                List<StoreCelebrityPrivate> listNew = new ArrayList<>();
                List<StoreCelebrityPrivateCounselor> privateCounselorsNew = new ArrayList<>();

                // 根据网红账号和平台分组
                Map<String, List<ContractAmtModel>> map = list.stream().collect(Collectors.groupingBy(x -> x.getAccount() + "-" + x.getPlatformTypeText()));
                for (Map.Entry<String, List<ContractAmtModel>> entry : map.entrySet()) {
                    List<ContractAmtModel> contractAmtModels = entry.getValue();
                    String account = entry.getKey().split("-")[0];
                    String platformTypeText = entry.getKey().split("-")[1];

                    if (oConvertUtils.isEmpty(platformTypeText)) {
                        errors.add(String.format("网红：%s，平台未获取到", account));
                        continue;
                    }

                    SysDictItem sysDictItem = platformTypeList.stream().filter(x -> x.getItemText().trim().equals(platformTypeText.trim())).findFirst().orElse(null);
                    int platformType;
                    if (sysDictItem != null) {
                        platformType = Integer.parseInt(sysDictItem.getItemValue());
                    } else {
                        errors.add(String.format("网红：%s，平台未获取到", account));
                        continue;
                    }
                    // 查询当前网红和平台是否存在
                    StoreCelebrityPrivate celebrityPrivate = celebrityPrivates.stream().filter(x -> x.getAccount().equals(account) && x.getPlatformType().equals(platformType)).findFirst().orElse(null);

                    if (celebrityPrivate == null) {
                        errors.add(String.format("平台：%s，网红：%s，网红不存在", platformTypeText, account));
                        continue;
                    }

                    String celebrityCounselorId = celebrityPrivate.getCelebrityCounselorId();
                    SysUser kolUser = users.stream().filter(x -> x.getId().equals(celebrityCounselorId)).findFirst().orElse(null);

                    contractAmtModels.forEach(x -> {
                        try {
                            Date contractTime = DateUtils.parseDate(x.getContractTimeStr(), "yyyy-MM-dd HH:mm:ss");
                            x.setContractTime(contractTime);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    contractAmtModels.sort(Comparator.comparing(ContractAmtModel::getContractTime).thenComparing(ContractAmtModel::getContractAmount).reversed());

                    for (ContractAmtModel contractAmtModel : contractAmtModels) {
                        String counselorName = contractAmtModel.getCelebrityCounselorName();
                        SysUser user = users.stream().filter(x -> x.getRealname().equals(counselorName)).findFirst().orElse(null);
                        if (user == null) {
                            errors.add(String.format("平台：%s，网红：%s，当前用户无法导入", platformTypeText, account));
                            continue;
                        }

                        BigDecimal contractAmt = contractAmtModel.getContractAmount();
                        Date contractTime = contractAmtModel.getContractTime();
                        if (contractTime == null) {
                            errors.add(String.format("平台：%s，网红：%s，签约时间未获取到", platformTypeText, account));
                            continue;
                        }
                        try {
                            // 判断网红顾问是否存在
                            StoreCelebrityPrivateCounselor privateCounselor = privateCounselors.stream().filter(x -> x.getCelebrityPrivateId().equals(celebrityPrivate.getId()) && x.getCelebrityCounselorId().equals(user.getId())).findFirst().orElse(null);

                            if (privateCounselor == null) {
                                errors.add(String.format("平台：%s，网红：%s，未找到网红顾问'%s'", platformTypeText, account, counselorName));
                                continue;
                            }

                            // 修改网红顾问签约费用
                            StoreCelebrityPrivateCounselor privateCounselorNew = new StoreCelebrityPrivateCounselor();
                            privateCounselorNew.setId(privateCounselor.getId());
                            privateCounselorNew.setContractAmount(contractAmt);
                            privateCounselorsNew.add(privateCounselorNew);

                            // 判断网红身上的网红顾问和excel中一致，修改网红身上的签约费用和网红顾问关联的签约费用
                            if (!kolUser.getId().equals(user.getId())) {
                                continue;
                            }

                            Optional<StoreCelebrityPrivate> privateOptional = listNew.stream().filter(x -> x.getId().equals(celebrityPrivate.getId())).findFirst();
                            if (privateOptional.isPresent()) {
                                StoreCelebrityPrivate storeCelebrityPrivate = privateOptional.get();
                                if (contractTime.after(storeCelebrityPrivate.getContractTime())) {
                                    storeCelebrityPrivate.setContractAmount(contractAmt);
                                }
                                continue;
                            }

                            StoreCelebrityPrivate storeCelebrityPrivate = new StoreCelebrityPrivate();
                            storeCelebrityPrivate.setId(celebrityPrivate.getId());
                            storeCelebrityPrivate.setContractTime(contractTime);
                            storeCelebrityPrivate.setContractAmount(contractAmt);
                            listNew.add(storeCelebrityPrivate);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (!listNew.isEmpty()) {
                    listNew.forEach(x -> x.setContractTime(null));
                    storeCelebrityPrivateService.updateBatchById(listNew);
                }
                if (!privateCounselorsNew.isEmpty()) {
                    privateCounselorService.updateBatchById(privateCounselorsNew);
                }
                log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
                if (!errors.isEmpty() && listNew.isEmpty()) {
                    return Result.error(500, "文件导入失败", errors);
                }
                if (!errors.isEmpty()) {
                    return Result.OK(String.format("文件导入成功！成功行数：%d,失败行数：%d", listNew.size(), errors.size()), errors);
                }
                return Result.ok(String.format("文件导入成功！成功行数：%d", listNew.size()));
            } catch (Exception e) {
                log.error(e.getMessage());
                return Result.error("文件导入失败,请检查文件内容是否正确！");
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.error("文件导入失败，请检查文件内容是否正确");
    }

    @AutoLog(value = "私有网红表-分页获取所有符合条件的私有网红列表")
    @Operation(summary = "私有网红表-分页获取所有符合条件的私有网红列表", description = "私有网红表-分页获取所有符合条件的私有网红列表")
    @GetMapping(value = "/getPageCelebrityPrivates")
    public Result<?> getPageCelebrityPrivates(StoreCelebrityPrivateModel storeCelebrityPrivateModel, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "100") Integer pageSize, HttpServletRequest req) {
        if (oConvertUtils.isNotEmpty(storeCelebrityPrivateModel.getFollowersEndNum())) {
            Integer followersEndNum = storeCelebrityPrivateModel.getFollowersEndNum() * 1000;
            storeCelebrityPrivateModel.setFollowersEndNum(followersEndNum);
        }
        if (oConvertUtils.isNotEmpty(storeCelebrityPrivateModel.getFollowersStartNum())) {
            Integer postsStartNum = storeCelebrityPrivateModel.getFollowersStartNum() * 1000;
            storeCelebrityPrivateModel.setFollowersStartNum(postsStartNum);
        }
        if (oConvertUtils.isNotEmpty(storeCelebrityPrivateModel.getPlayAvgStartNum())) {
            Integer playAvgStartNum = storeCelebrityPrivateModel.getPlayAvgStartNum() * 1000;
            storeCelebrityPrivateModel.setPlayAvgStartNum(playAvgStartNum);
        }
        if (oConvertUtils.isNotEmpty(storeCelebrityPrivateModel.getPlayAvgEndNum())) {
            Integer playAvgEndNum = storeCelebrityPrivateModel.getPlayAvgEndNum() * 1000;
            storeCelebrityPrivateModel.setPlayAvgEndNum(playAvgEndNum);
        }
        if (oConvertUtils.isNotEmpty(storeCelebrityPrivateModel.getColumn())) {
            storeCelebrityPrivateModel.setColumn(oConvertUtils.camelToUnderline(storeCelebrityPrivateModel.getColumn()));
        }
        Page<StoreCelebrityPrivateModel> page = new Page<StoreCelebrityPrivateModel>(pageNo, pageSize);
        String sql = "";
        IPage<StoreCelebrityPrivateModel> celebrityPrivateModels = storeCelebrityPrivateService.getCelebrityPrivateList(page, storeCelebrityPrivateModel, "");

        return Result.ok(celebrityPrivateModels);
    }

    @AutoLog(value = "私有网红表-调整顾问列表查询")
    @Operation(summary = "私有网红表-调整顾问列表查询", description = "私有网红表-调整顾问列表查询")
    @GetMapping(value = "/getPageCelebrityPrivatesNew")
    public Result<?> getPageCelebrityPrivatesNew(StoreCelebrityPrivateModel storeCelebrityPrivateModel, HttpServletRequest req) {
        List<StoreCelebrityPrivateModel> celebrityPrivateModels = storeCelebrityPrivateService.getCelebrityPrivateListNew(storeCelebrityPrivateModel);
        return Result.ok(celebrityPrivateModels);
    }

    @GetMapping("/distribution")
    public Result<?> getPlatformDistribution() {
        List<PlatformDistributionModel> platformDistributions = storeCelebrityPrivateService.getPlatformDistribution();
        return Result.ok(platformDistributions);
    }

    @GetMapping("/signed")
    public Result<?> getSignedStats() {
        CelebritySignedModel celebritySignedModel = storeCelebrityPrivateService.getCelebritySignedStats();
        return Result.ok(celebritySignedModel);
    }

    /*
     * 功能描述：获取网红各平台粉丝数区间汇总
     *
     * @Param:
     *
     * @Author: fengLiu
     *
     * @Date: 2025-03-17 15:04
     */
    @GetMapping("/getFollowersStats")
    public Result<?> getCelebrityStats(@RequestParam(value = "platformType", required = false) Integer platformType) {
        List<CelebrityFlowersStatsModel> stats = storeCelebrityPrivateService.getCelebrityFollowersStats(platformType);
        return Result.ok(stats);
    }

    /**
     * 功能描述：获取各平台网红签约费用区间汇总
     *
     * @param platformType
     * @Param:
     * @Author: fengLiu
     * @Date: 2025-03-17 17:54
     */
    @GetMapping("/getCostStats")
    public Result<?> getCelebrityCostStats(@RequestParam(value = "platformType", required = false) Integer platformType) {
        List<CelebrityCostStatsModel> stats = storeCelebrityPrivateService.getCelebrityCostStats(platformType);
        return Result.ok(stats);
    }

    /**
     * 功能描述：获取网红顾问签约数量汇总
     *
     * @param platformType
     * @Param:
     * @Author: fengLiu
     * @Date: 2025-03-17 18:20
     */
    @GetMapping("/getCounselorStats")
    public Result<?> getCelebrityCounselorStats(@RequestParam(value = "platformType", required = false) Integer platformType, @RequestParam(value = "contractStartTime", required = false) String contractStartTime, @RequestParam(value = "contractEndTime", required = false) String contractEndTime) {
        Map<String, Object> stats = storeCelebrityPrivateService.getCelebrityCounselorStats(platformType, contractStartTime, contractEndTime);
        return Result.ok(stats);
    }

    /**
     * 功能描述：获取各平台网红类目数量汇总
     *
     * @param platformType
     * @Param:
     * @Author: fengLiu
     * @Date: 2025-03-17 18:51
     */
    @GetMapping("/getCategoryStats")
    public Result<?> getCelebrityCategoryStats(@RequestParam(value = "platformType", required = false) Integer platformType) {
        Map<String, Object> stats = storeCelebrityPrivateService.getCelebrityCategoryStats(platformType);
        return Result.ok(stats);
    }

    /**
     * 功能描述：获取网红顾问各个平台、各个类目数量汇总
     *
     * @param platformType
     * @Param:
     * @Author: fengLiu
     * @Date: 2025-03-18 11:45
     */
    @GetMapping("/getCounselorCategoryStats")
    public Result<?> getCounselorCategoryStats(@RequestParam(required = false) Integer platformType) {
        List<CounselorCategoryStatsModel> stats = storeCelebrityPrivateService.getCounselorCategoryStats(platformType);
        return Result.ok(stats);
    }

    @AutoLog(value = "从飞书导入私有网红历史合作、历史选中")
    @Operation(summary = "从飞书导入私有网红历史合作、历史选中", description = "从飞书导入私有网红历史合作、历史选中")
    @GetMapping(value = "/synchronizePrivateProducts")
    public Result<?> synchronizePrivateProducts() throws Exception {
        String data = feishuService.synchronizePrivateProducts();
        return storeCelebrityPrivateService.extractedPrivateProducts(data);
    }

    /**
     * 下载模板
     */
    @RequestMapping(value = "/downloadPrivateProductXls", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadPrivateProductXls(HttpServletResponse response) throws IOException {
        String userId = getUserIdByToken();
        XSSFWorkbook wb = new XSSFWorkbook(); // 使用 XSSFWorkbook 支持 .xlsx
        XSSFSheet mainSheet = wb.createSheet("Sheet");
        XSSFSheet hiddenSheet = wb.createSheet("HiddenSheet");
        // 设置隐藏 Sheet
        wb.setSheetHidden(wb.getSheetIndex(hiddenSheet), true);
        // String[] header = {"网红名称", "网红平台", "产品名称", "合作状态"};
        String[] header = {"网红名称", "网红平台", "产品名称"};
        Row row = mainSheet.createRow(0);
        // 创建一个居中格式
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        for (int i = 0; i < header.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(header[i]);
            cell.setCellStyle(style);
            mainSheet.setColumnWidth(i, 4000);
        }
        setPrivateProductsDropdown(wb, mainSheet, hiddenSheet, userId);
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("template.xlsx", "UTF-8"));
            ServletOutputStream outputStream = response.getOutputStream();
            wb.write(outputStream);
            outputStream.flush();
            log.info("Excel file written successfully");
        } catch (IOException e) {
            log.error("Failed to write Excel file", e);
            throw new RuntimeException("Error generating Excel file", e);
        } finally {
            try {
                wb.close();
            } catch (IOException e) {
                log.error("Failed to close workbook", e);
            }
        }
    }

    /**
     * 设置下拉框
     *
     * @param wb          XSSFWorkbook 实例
     * @param mainSheet   主工作表，用于显示下拉框
     * @param hiddenSheet 隐藏工作表，用于存储下拉框数据
     * @param userId      用户ID
     */
    private void setPrivateProductsDropdown(XSSFWorkbook wb, XSSFSheet mainSheet, XSSFSheet hiddenSheet, String userId) {
        // 输入参数验证
        if (wb == null || mainSheet == null || hiddenSheet == null || StringUtils.isBlank(userId)) {
            log.error("setPrivateProductsDropdown 的输入参数无效，用户ID: {}", userId);
            return;
        }

        // 平台类型下拉框
        List<String> platformTypeValues = Arrays.asList("IG", "YT", "TK");
        log.info("设置平台类型下拉框: {}", platformTypeValues);
        createDropdownList(wb, mainSheet, hiddenSheet, platformTypeValues.toArray(new String[0]), 1, "platformTypeValues", 0);

        /*
         * // 合作状态下拉框
         * List<String> relationStatusValues = Arrays.asList("选中", "合作");
         * log.info("设置合作状态下拉框: {}", relationStatusValues);
         * int hiddenRowOffset = platformTypeValues.size() + 1;
         * createDropdownList(wb, mainSheet, hiddenSheet,
         * relationStatusValues.toArray(new String[0]), 3, "relationStatus",
         * hiddenRowOffset);
         */
        int hiddenRowOffset = platformTypeValues.size() + 1;
        // 产品名称下拉框
        try {
            LambdaQueryWrapper<KolProduct> productQuery = new LambdaQueryWrapper<>();
            productQuery.eq(KolProduct::getIsDelete, YesNoStatus.NO.getCode());
            List<KolProduct> products = productService.list(productQuery);

            // hiddenRowOffset += relationStatusValues.size() + 1;

            if (products != null && !products.isEmpty()) {
                List<String> productNames = products.stream().filter(Objects::nonNull).map(this::formatProductInfo).filter(Objects::nonNull).toList();

                if (!productNames.isEmpty()) {
                    log.info("设置产品下拉框: {}", productNames);
                    createDropdownList(wb, mainSheet, hiddenSheet, productNames.toArray(new String[0]), 2, "productList", hiddenRowOffset);
                } else {
                    log.warn("未找到有效的产品名称用于下拉框，用户ID: {}", userId);
                }
            } else {
                log.info("未找到数据，用户ID: {}", userId);
            }
        } catch (Exception e) {
            log.error("创建产品下拉框失败，用户ID: {}", userId, e);
        }
    }

    /**
     * 格式化产品信息，处理空值情况
     *
     * @param product KolProduct 实例
     * @return 格式化后的产品字符串，或 null 如果数据无效
     */
    private String formatProductInfo(KolProduct product) {
        if (product == null || StringUtils.isAnyBlank(product.getBrandName(), product.getProductName())) {
            log.warn("数据无效，品牌名或产品名为空");
            return null;
        }

        try {
            /*
             * String productModel = "";
             * if (StringUtils.isNotBlank(product.getProductAttributes())) {
             * JSONObject jsonObject = JSON.parseObject(product.getProductAttributes());
             * productModel = jsonObject != null ?
             * StringUtils.defaultString(jsonObject.getString("productModel"), "") : "";
             * }
             * return String.format("%s-%s%s",
             * product.getBrandName(),
             * product.getProductName(),
             * StringUtils.isNotBlank(productModel) ? "-" + productModel : ""
             */
            return String.format("%s-%s", product.getBrandName(), product.getProductName());
        } catch (Exception e) {
            log.warn("格式化产品信息失败，产品名: {}", product.getProductName(), e);
            return null;
        }
    }

    /**
     * 通过excel导入历史选中、历史合作数据
     *
     * @param request
     * @param response
     * @return
     */
    @AutoLog(value = "私有网红表-通过excel导入历史选中、历史合作数据")
    @RequestMapping(value = "/importPrivateProductsExcel", method = RequestMethod.POST)
    public Result<?> importPrivateProductsExcel(HttpServletRequest request, HttpServletResponse response) {
        String userId = getUserIdByToken();
        SysUser sysUser = sysUserService.getById(userId);
        if (!sysUser.getUserType().equals(UserType.CELEBRITY_COUNSELOR.getCode())) {
            return Result.error("当前用户无权限导入网红数据");
        }

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap.isEmpty()) {
            return Result.error("未上传文件");
        }

        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();
            ImportParams params = new ImportParams();
            params.setHeadRows(1);
            params.setNeedSave(true);

            try {
                long start = System.currentTimeMillis();
                // 读取 Excel 文件
                List<StoreCelebrityPrivateProductExcelModel> list = ExcelImportUtil.importExcel(file.getInputStream(), StoreCelebrityPrivateProductExcelModel.class, params);
                // 过滤空账号数据
                list = list.stream().filter(x -> oConvertUtils.isNotEmpty(x.getAccount())).toList();
                if (list.isEmpty()) {
                    return Result.error("文件导入失败，未获取到网红账号数据");
                }

                // 调用 Service 层处理导入数据
                ImportPrivateProductResult importResult = storeCelebrityPrivateService.importPrivateProducts(list, userId);
                log.info("导入耗时 {} 毫秒", System.currentTimeMillis() - start);

                if (!importResult.getErrorMap().isEmpty()) {
                    // 格式化错误信息，包含行号
                    List<String> errorList = importResult.getErrorMap().entrySet().stream().map(entry -> String.format("%s: %s", entry.getKey(), entry.getValue())).toList();
                    return Result.error(500, "文件导入失败，存在错误数据！", errorList);
                }
                // 保存导入数据
                storeCelebrityPrivateService.saveImportData(importResult);
                return Result.ok("文件导入成功！数据行数：" + list.size());
            } catch (Exception e) {
                log.error("文件导入失败，用户ID: {}，错误: {}", userId, e.getMessage(), e);
                return Result.error("文件导入失败，请检查文件内容是否正确");
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    log.error("关闭文件流失败: {}", e.getMessage(), e);
                }
            }
        }
        return Result.error("文件导入失败，请检查文件内容是否正确");

    }

    @AutoLog(value = "私有网红-更新社媒属性-" + SystemConstants.EDIT)
    @Operation(summary = "私有网红-更新社媒属性-" + SystemConstants.EDIT, description = "私有网红-更新社媒属性-" + SystemConstants.EDIT)
    @PostMapping(value = "/celebrityAttributeUpdate")
    public Result<?> celebrityAttributeUpdate(@RequestBody StoreCelebrityPrivateModel privateModel) {

        // 1. 验证 privateModel 字段
        if (oConvertUtils.isEmpty(privateModel.getId())) {
            return Result.error("私有网红ID不能为空");
        }
        /*
         * if (oConvertUtils.isEmpty(privateModel.getCelebrityId())) {
         * return Result.error("网红ID不能为空");
         * }
         * if (oConvertUtils.isEmpty(privateModel.getAccount())) {
         * return Result.error("账号不能为空");
         * }
         */
        if (privateModel.getDataList() == null || privateModel.getDataList().isEmpty()) {
            return Result.error("社媒属性数据列表不能为空");
        }
        StoreCelebrityPrivate storeCelebrityPrivate = storeCelebrityPrivateService.getById(privateModel.getId());
        String celebrityCounselorId = storeCelebrityPrivate.getCelebrityCounselorId();
        if (!celebrityCounselorId.equals(getUserIdByToken())) {
            return Result.error("当前用户无法修改该网红社媒属性");
        }
        // 3. 用户权限验证
        String userId = getUserIdByToken();
        String userName = getUserNameByToken();
        Result<?> userValidation = validateUser(userId);
        if (!userValidation.isSuccess()) {
            return userValidation;
        }

        try {
            long start = System.currentTimeMillis();

            StoreCelebrityPrivate celebrityPrivate = new StoreCelebrityPrivate();
            celebrityPrivate.setId(privateModel.getId());
            // celebrityPrivate.setCelebrityId(privateModel.getCelebrityId());
            // celebrityPrivate.setAccount(privateModel.getAccount());
            celebrityPrivate.setIsAttributeConfirmed(AttributeConfirmationStatus.CONFIRMED.getCode());
            celebrityPrivate.setGetAttributeStatus(101);
            celebrityPrivate.setUpdateBy(userId);
            celebrityPrivate.setUpdateTime(new Date());
            celebrityPrivate.setAttributeConfirmedUserId(getUserIdByToken());
            celebrityPrivate.setAttributeConfirmedUsername(getUserNameByToken());
            celebrityPrivate.setAttributeConfirmedConfirmedTime(new Date());
            List<TypeData> dataList = privateModel.getDataList();
            List<StoreCelebrityPrivateAttributeRelation> attributeRelations = privateAttributeRelationService.createAttributeRelation(celebrityPrivate, dataList);

            // 5. 调用服务层方法更新网红信息和社媒属性
            privateService.updateCelebrityWithAttributes(celebrityPrivate, attributeRelations);

            log.info("网红社媒属性更新完成，耗时: {}ms", System.currentTimeMillis() - start);
            return Result.ok("社媒属性确认成功");

        } catch (Exception ex) {
            log.error("网红社媒属性确认失败", ex);
            return Result.error("社媒属性确认失败");
        }
    }

    @AutoLog(value = "私有网红-更新社媒属性-" + SystemConstants.EDIT)
    @Operation(summary = "私有网红-更新社媒属性-" + SystemConstants.EDIT, description = "私有网红-更新社媒属性-" + SystemConstants.EDIT)
    @PostMapping(value = "/celebritiesAttributeUpdate")
    public Result<?> celebritiesAttributeUpdate(@RequestBody List<StoreCelebrityPrivateModel> privateModels) {
        // 1. 验证 privateModels 列表
        if (privateModels == null || privateModels.isEmpty()) {
            return Result.error("网红数据列表不能为空");
        }

        // 2. 验证每个 privateModel 的字段
        for (StoreCelebrityPrivateModel privateModel : privateModels) {
            if (oConvertUtils.isEmpty(privateModel.getId())) {
                return Result.error("私有网红ID不能为空，错误数据ID: " + privateModel.getId());
            }
            if (oConvertUtils.isEmpty(privateModel.getCelebrityId())) {
                return Result.error("网红ID不能为空，错误数据ID: " + privateModel.getId());
            }
            if (oConvertUtils.isEmpty(privateModel.getAccount())) {
                return Result.error("账号不能为空，错误数据ID: " + privateModel.getId());
            }
            if (privateModel.getDataList() == null || privateModel.getDataList().isEmpty()) {
                return Result.error("社媒属性数据列表不能为空，错误数据ID: " + privateModel.getId());
            }
        }

        // 3. 用户权限验证
        String userId = getUserIdByToken();
        String userName = getUserNameByToken();
        Result<?> userValidation = validateUser(userId);
        if (!userValidation.isSuccess()) {
            return userValidation;
        }

        try {
            long start = System.currentTimeMillis();

            // 4. 构建私有网红实体和属性关联列表
            List<StoreCelebrityPrivate> celebrities = new ArrayList<>();
            List<StoreCelebrityPrivateAttributeRelation> allAttributeRelations = new ArrayList<>();
            for (StoreCelebrityPrivateModel privateModel : privateModels) {
                StoreCelebrityPrivate celebrityPrivate = new StoreCelebrityPrivate();
                celebrityPrivate.setId(privateModel.getId());
                celebrityPrivate.setCelebrityId(privateModel.getCelebrityId());
                celebrityPrivate.setAccount(privateModel.getAccount());
                celebrityPrivate.setIsAttributeConfirmed(1);
                celebrityPrivate.setUpdateBy(userId);
                celebrityPrivate.setUpdateTime(new Date());
                celebrities.add(celebrityPrivate);

                // 创建社媒属性关联
                List<TypeData> dataList = privateModel.getDataList();
                List<StoreCelebrityPrivateAttributeRelation> attributeRelations = privateAttributeRelationService.createAttributeRelation(celebrityPrivate, dataList);
                allAttributeRelations.addAll(attributeRelations);
            }

            // 5. 调用服务层方法批量更新网红信息和社媒属性
            privateService.updateCelebritiesWithAttributes(celebrities, allAttributeRelations);

            log.info("网红社媒属性更新完成，耗时: {}ms", System.currentTimeMillis() - start);
            return Result.ok("社媒属性确认成功");

        } catch (Exception ex) {
            log.error("网红社媒属性确认失败", ex);
            return Result.error("社媒属性确认失败");
        }
    }

    @AutoLog(value = "获取企业微信文档数据")
    @Operation(summary = "获取企业微信文档数据", description = "获取企业微信文档数据")
    @GetMapping(value = "/synchronizeData")
    public Result<?> synchronizeData() throws Exception {
        List<Record> allRecords = wechatService.getAllRecords(wechatDocConfig.getTemplateDocId(), wechatDocConfig.getPrivateSheetId(), null, null);
        System.out.println(JSON.toJSONString(allRecords));
        List<Map<String, Object>> list = allRecords.stream().map(Record::getValues).toList();
        List<StoreCelebrityPrivateExcelModel> excelModelList = convertToStoreCelebrityModel(list);
        List<StoreCelebrityPrivateExcelModel> privateExcelModels = excelModelList.stream().filter(x -> (x.getIsUpdate().equals("否") || oConvertUtils.isEmpty(x.getIsUpdate())) && oConvertUtils.isNotEmpty(x.getAccount())).toList();
        wechatDocImportService.importStoreCelebrityPrivate(privateExcelModels, wechatDocConfig.getTemplateDocId(), wechatDocConfig.getPrivateSheetId());
        return Result.ok();
    }

    public List<StoreCelebrityPrivateExcelModel> convertToStoreCelebrityModel(List<Map<String, Object>> records) {
        List<StoreCelebrityPrivateExcelModel> resultList = new ArrayList<>();

        if (records == null || records.isEmpty()) {
            return resultList;
        }

        for (Map<String, Object> record : records) {
            StoreCelebrityPrivateExcelModel model = convertValuesToEntity(record);
            resultList.add(model);
        }

        return resultList;
    }

    private StoreCelebrityPrivateExcelModel convertValuesToEntity(Map<String, Object> values) {
        StoreCelebrityPrivateExcelModel model = new StoreCelebrityPrivateExcelModel();

        // 处理内容1 (videoTags)
        if (values.containsKey("内容1")) {
            List<Map<String, Object>> contentList = (List<Map<String, Object>>) values.get("内容1");
            if (contentList != null && !contentList.isEmpty()) {
                model.setVideoTags((String) contentList.get(0).get("text"));
            } else {
                model.setVideoTags("");
            }
        } else {
            model.setVideoTags("");
        }

        // 处理内容2 (videoTags2)
        if (values.containsKey("内容2")) {
            List<Map<String, Object>> contentList = (List<Map<String, Object>>) values.get("内容2");
            if (contentList != null && !contentList.isEmpty()) {
                model.setVideoTags2((String) contentList.get(0).get("text"));
            } else {
                model.setVideoTags2("");
            }
        } else {
            model.setVideoTags2("");
        }

        // 处理内容3 (videoTags3)
        if (values.containsKey("内容3")) {
            List<Map<String, Object>> contentList = (List<Map<String, Object>>) values.get("内容3");
            if (contentList != null && !contentList.isEmpty()) {
                model.setVideoTags3((String) contentList.get(0).get("text"));
            } else {
                model.setVideoTags3("");
            }
        } else {
            model.setVideoTags3("");
        }

        // 处理国家 (countryName)
        if (values.containsKey("国家")) {
            List<Map<String, Object>> countryList = (List<Map<String, Object>>) values.get("国家");
            if (countryList != null && !countryList.isEmpty()) {
                model.setCountryName((String) countryList.get(0).get("text"));
            } else {
                model.setCountryName("");
            }
        } else {
            model.setCountryName("");
        }

        // 处理备注 (remark)
        if (values.containsKey("备注")) {
            List<Map<String, Object>> remarkList = (List<Map<String, Object>>) values.get("备注");
            if (remarkList != null && !remarkList.isEmpty()) {
                model.setRemark((String) remarkList.get(0).get("text"));
            } else {
                model.setRemark("");
            }
        } else {
            model.setRemark("");
        }

        // 处理平台 (platformTypeText)
        if (values.containsKey("平台")) {
            List<Map<String, Object>> platformList = (List<Map<String, Object>>) values.get("平台");
            if (platformList != null && !platformList.isEmpty()) {
                model.setPlatformTypeText((String) platformList.get(0).get("text"));
            } else {
                model.setPlatformTypeText("");
            }
        } else {
            model.setPlatformTypeText("");
        }

        // 处理建联邮箱 (celebrityContactEmail)
        if (values.containsKey("建联邮箱")) {
            List<Map<String, Object>> emailList = (List<Map<String, Object>>) values.get("建联邮箱");
            if (emailList != null && !emailList.isEmpty()) {
                Map<String, Object> emailMap = emailList.get(0);
                model.setCelebrityContactEmail((String) emailMap.get("text"));
            } else {
                model.setCelebrityContactEmail("");
            }
        } else {
            model.setCelebrityContactEmail("");
        }

        // 处理性别 (genderText)
        if (values.containsKey("性别")) {
            List<Map<String, Object>> genderList = (List<Map<String, Object>>) values.get("性别");
            if (genderList != null && !genderList.isEmpty()) {
                model.setGenderText((String) genderList.get(0).get("text"));
            } else {
                model.setGenderText("");
            }
        } else {
            model.setGenderText("");
        }

        // 处理是否同步 (isUpdate)
        if (values.containsKey("是否同步")) {
            List<Map<String, Object>> syncList = (List<Map<String, Object>>) values.get("是否同步");
            if (syncList != null && !syncList.isEmpty()) {
                model.setIsUpdate((String) syncList.get(0).get("text"));
            } else {
                model.setIsUpdate("");
            }
        } else {
            model.setIsUpdate("");
        }

        // 处理签约费用1 (contractAmountStr)
        if (values.containsKey("签约费用1")) {
            Object fee1 = values.get("签约费用1");
            if (fee1 != null) {
                model.setContractAmountStr(fee1.toString());
            } else {
                model.setContractAmountStr("");
            }
        } else {
            model.setContractAmountStr("");
            model.setContractAmount(null);
        }

        // 处理签约费用2 (contractAmountStr2)
        if (values.containsKey("签约费用2")) {
            Object fee2 = values.get("签约费用2");
            if (fee2 != null) {
                model.setContractAmountStr2(fee2.toString());
            } else {
                model.setContractAmountStr2("");
                model.setContractAmount2(null);
            }
        } else {
            model.setContractAmountStr2("");
            model.setContractAmount2(null);
        }

        // 处理签约费用3 (contractAmountStr3)
        if (values.containsKey("签约费用3")) {
            Object fee3 = values.get("签约费用3");
            if (fee3 != null) {
                model.setContractAmountStr3(fee3.toString());
            } else {
                model.setContractAmountStr3("");
                model.setContractAmount3(null);
            }
        } else {
            model.setContractAmountStr3("");
            model.setContractAmount3(null);
        }

        // 处理网红邮箱 (email)
        if (values.containsKey("网红邮箱")) {
            Object email = values.get("网红邮箱");
            if (email != null) {
                model.setEmail(email.toString());
            } else {
                model.setEmail("");
            }
        } else {
            model.setEmail("");
        }

        // 处理账号 (account)
        if (values.containsKey("账号")) {
            List<Map<String, Object>> accountList = (List<Map<String, Object>>) values.get("账号");
            if (accountList != null && !accountList.isEmpty()) {
                model.setAccount((String) accountList.get(0).get("text"));
            } else {
                model.setAccount("");
            }
        } else {
            model.setAccount("");
        }

        // 处理顾问
        if (values.containsKey("顾问")) {
            List<Map<String, Object>> contactList = (List<Map<String, Object>>) values.get("顾问");
            if (contactList != null && !contactList.isEmpty()) {
                model.setUserName((String) contactList.get(0).get("text"));
            } else {
                model.setUserName("");
            }
        } else {
            model.setAccount("");
        }
        // 处理个性化标签
        if (values.containsKey("个性化标签")) {
            /*
             * List<Map<String, Object>> personalTags = (List<Map<String, Object>>)
             * values.get("个性化标签");
             * if (personalTags != null && !personalTags.isEmpty()) {
             * personalTags.forEach(personalTag -> {
             * model.setPersonalTags((oConvertUtils.isNotEmpty(model.getPersonalTags()) ?
             * model.getPersonalTags() + "," : "" )+ personalTag.get("text"));
             * });
             * } else {
             * model.setPersonalTags("");
             * }
             */

            List<String> personalTagIds = (List<String>) values.get("个性化标签");
            if (personalTagIds != null && !personalTagIds.isEmpty()) {
                List<StorePersonalTags> storePersonalTags = personalTagsService.lambdaQuery().in(StorePersonalTags::getRecordId, personalTagIds).list();
                String tagNames = storePersonalTags.stream().map(StorePersonalTags::getTagName).collect(Collectors.joining(","));
                model.setPersonalTags(tagNames);
            } else {
                model.setPersonalTags("");
            }
        } else {
            model.setPersonalTags("");
        }

        if (values.containsKey("recordId")) {
            model.setRecordId((String) values.get("recordId"));
        }

        return model;
    }

    @AutoLog(value = "获取企业微信文档数据")
    @Operation(summary = "获取企业微信文档数据", description = "获取企业微信文档数据")
    @GetMapping(value = "/synchronizePrivateProductData")
    public Result<?> synchronizePrivateProductData() throws Exception {
        List<Record> allRecords = wechatService.getAllRecords(wechatDocConfig.getTemplateDocId(), wechatDocConfig.getPrivateProductSheetId(), null, null);
        System.out.println(JSON.toJSONString(allRecords));
        List<Map<String, Object>> list = allRecords.stream().map(Record::getValues).toList();
        List<StoreCelebrityPrivateProductModel> excelModelList = convertToPrivateProductModel(list);
        List<StoreCelebrityPrivateProductModel> privateProductModels = excelModelList.stream().filter(x -> (x.getIsUpdate().equals("否") || oConvertUtils.isEmpty(x.getIsUpdate())) && oConvertUtils.isNotEmpty(x.getAccount())).toList();
        wechatDocImportService.importPrivateCelebrityProducts(privateProductModels, wechatDocConfig.getTemplateDocId(), wechatDocConfig.getPrivateProductSheetId());
        return Result.ok();
    }

    private List<StoreCelebrityPrivateProductModel> convertToPrivateProductModel(List<Map<String, Object>> records) {
        List<StoreCelebrityPrivateProductModel> resultList = new ArrayList<>();

        if (records == null || records.isEmpty()) {
            return resultList;
        }

        for (Map<String, Object> record : records) {
            StoreCelebrityPrivateProductModel model = convertPrivateProductValuesToEntity(record);
            resultList.add(model);
        }

        return resultList;
    }

    private StoreCelebrityPrivateProductModel convertPrivateProductValuesToEntity(Map<String, Object> values) {
        StoreCelebrityPrivateProductModel privateProductModel = new StoreCelebrityPrivateProductModel();
        // 处理产品品牌
        List<String> productList = (List<String>) values.get("产品");
        if (!productList.isEmpty()) {
            String recordId = productList.get(0);
            KolProduct kolProduct = kolProductService.lambdaQuery().eq(KolProduct::getRecordId, recordId).one();
            if (kolProduct != null) {
                privateProductModel.setProductId(kolProduct.getId());
                privateProductModel.setProductName(kolProduct.getProductName());
                privateProductModel.setBrandId(kolProduct.getBrandId());
                privateProductModel.setBrandName(kolProduct.getBrandName());
            }
        } else {
            privateProductModel.setProductId(null);
            privateProductModel.setProductName(null);
        }

        // 处理平台 (platformTypeText)
        if (values.containsKey("平台")) {
            List<Map<String, Object>> platformList = (List<Map<String, Object>>) values.get("平台");
            if (platformList != null && !platformList.isEmpty()) {
                privateProductModel.setPlatformTypeStr((String) platformList.get(0).get("text"));
            } else {
                privateProductModel.setPlatformTypeStr("");
            }
        } else {
            privateProductModel.setPlatformTypeStr("");
        }

        // 处理是否同步 (isUpdate)
        if (values.containsKey("是否同步")) {
            List<Map<String, Object>> syncList = (List<Map<String, Object>>) values.get("是否同步");
            if (syncList != null && !syncList.isEmpty()) {
                privateProductModel.setIsUpdate((String) syncList.get(0).get("text"));
            } else {
                privateProductModel.setIsUpdate("");
            }
        } else {
            privateProductModel.setIsUpdate("");
        }

        // 处理账号 (account)
        if (values.containsKey("选中网红")) {
            List<Map<String, Object>> accountList = (List<Map<String, Object>>) values.get("选中网红");
            if (accountList != null && !accountList.isEmpty()) {
                privateProductModel.setAccount((String) accountList.get(0).get("text"));
            } else {
                privateProductModel.setAccount("");
            }
        } else {
            privateProductModel.setAccount("");
        }

        // 处理合作开始时间 (startTime)
        if (values.containsKey("日期")) {
            Object dateValue = values.get("日期");
            if (dateValue instanceof String) {
                // 直接是字符串格式的时间戳
                privateProductModel.setStartTime(parseDate((String) dateValue));
            } else if (dateValue instanceof List) {
                // List格式，提取text字段
                List<Map<String, Object>> startTimeList = (List<Map<String, Object>>) dateValue;
                if (startTimeList != null && !startTimeList.isEmpty()) {
                    String timeText = (String) startTimeList.get(0).get("text");
                    privateProductModel.setStartTime(parseDate(timeText));
                }
            }
        }

        // 处理 recordId
        privateProductModel.setRecordId(values.getOrDefault("recordId", "").toString());
        return privateProductModel;
    }

    /**
     * 解析日期字符串
     */
    private Date parseDate(String dateStr) {
        if (oConvertUtils.isEmpty(dateStr)) {
            return null;
        }

        try {
            // 首先尝试解析时间戳（毫秒）
            if (dateStr.matches("\\d+")) {
                long timestamp = Long.parseLong(dateStr);
                // 判断是秒级还是毫秒级时间戳
                if (String.valueOf(timestamp).length() == 10) {
                    // 秒级时间戳，转换为毫秒
                    timestamp = timestamp * 1000;
                }
                return new Date(timestamp);
            }

            // 支持多种日期格式
            String[] patterns = {"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM-dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM/dd", "MM/dd/yyyy HH:mm:ss", "MM/dd/yyyy HH:mm", "MM/dd/yyyy"};

            for (String pattern : patterns) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                    return sdf.parse(dateStr.trim());
                } catch (ParseException ignored) {
                    // 继续尝试下一个格式
                }
            }
        } catch (Exception e) {
            log.warn("日期解析失败: {}", dateStr, e);
        }

        return null;
    }

    /**
     * @description:从飞书表格导入私有网红数据
     * @author: nqr
     * @date: 2025/9/15 19:10
     **/
    @AutoLog(value = "私有网红-飞书表格导入")
    @Operation(summary = "从飞书表格导入私有网红数据")
    @GetMapping("/importPrivateDataBack")
    public Result<?> importPrivateDataBack() {
        String userId = getUserIdByToken();
        String username = getUserNameByToken();
        LoginUser loginUser = sysBaseAPI.getUserById(userId);
        try {
            log.info("开始从飞书表格导入私有网红数据，用户: {}", loginUser.getUsername());
            String tenantAccessToken = feishuService.getInternalTenantAccessToken();
            KolSysUserFeishuSheet feishuSheet = sheetService.lambdaQuery().eq(KolSysUserFeishuSheet::getSysUserId, userId).eq(KolSysUserFeishuSheet::getSpreadSheetType, SheetConstants.PRIVATE_CELEBRITY).one();
            if (feishuSheet == null) {
                return Result.error("未获取到飞书文档，请先配置");
            }
            String url = "https://open.feishu.cn/open-apis/sheets/v2/spreadsheets/" + feishuSheet.getSpreadSheetId() + "/values_batch_get?ranges=" + feishuSheet.getSheetId() + "!A1:" + feishuSheet.getEndColumn() + "&valueRenderOption=ToString&dateTimeRenderOption=FormattedString";
            System.out.println(url);
            FeishuSheetResponse sheetResponse = feishuService.getFeishuSheetData(url, tenantAccessToken);
            // 2. 将表格数据转换为DTO对象
            FeishuSheetConvertResult<StoreCelebrityPrivateExcelModel> convertResult = feishuService.convertSheetDataToEntityNew(sheetResponse, StoreCelebrityPrivateExcelModel.class);
            List<StoreCelebrityPrivateExcelModel> entityList = convertResult.getEntityList();
            // 过滤需要导入的数据
            List<StoreCelebrityPrivateExcelModel> privateExcelModels = entityList.stream().filter(x -> (oConvertUtils.isEmpty(x.getIsUpdate()) || x.getIsUpdate().equals("否")) && oConvertUtils.isNotEmpty(x.getAccount())).toList();
            if (privateExcelModels.isEmpty()) {
                return Result.error("没有需要导入的数据");
            }
            Map<String, Object> map = privateService.importStoreCelebrityPrivate(privateExcelModels, userId, username);
            List<StoreCelebrityPrivateExcelModel> successImportList = (List<StoreCelebrityPrivateExcelModel>) map.get("successImportList");
            if (!successImportList.isEmpty()) {
                updateImportStatus(feishuSheet, successImportList, "是");
            }
            Set<StoreCelebrityPrivateExcelModel> errorList = (Set<StoreCelebrityPrivateExcelModel>) map.get("errorList");

            // 修改飞书表格导入状态
            updateImportStatus(feishuSheet, new ArrayList<>(errorList), "否");
            return errorList.isEmpty() ? Result.ok("导入成功") : Result.OK(500, String.format("导入成功，成功条数：%d,失败条数：%d", successImportList.size(), errorList.size()), new ArrayList<>(errorList));
        } catch (Exception e) {
            log.error("从飞书表格导入数据异常", e);
            return Result.error("导入失败: 请检查内容是否正确");
        }
    }

    @AutoLog(value = "私有网红-飞书表格导入")
    @Operation(summary = "从飞书表格导入私有网红数据")
    @GetMapping("/importPrivateData")
    public Result<?> importPrivateData() {
        List<SysUser> list = userService.lambdaQuery().eq(SysUser::getDelFlag, 0).eq(SysUser::getUserType, 4).eq(SysUser::getIsLeave, 0).eq(SysUser::getStatus, 1).list();
        List<KolSysUserFeishuSheet> feishuSheets = sheetService.lambdaQuery().eq(KolSysUserFeishuSheet::getSpreadSheetType, SheetConstants.PRIVATE_CELEBRITY).list();
        for (KolSysUserFeishuSheet feishuSheet : feishuSheets) {
            try {
                Optional<SysUser> userOptional = list.stream().filter(x -> x.getId().equals(feishuSheet.getSysUserId())).findFirst();
                if (!userOptional.isPresent()) {
                    continue;
                }
                SysUser sysUser = userOptional.get();
                String userId = sysUser.getId();
                String username = sysUser.getUsername();
                System.out.println("开始从飞书表格导入私有网红数据，用户: " + username);
                String tenantAccessToken = feishuService.getInternalTenantAccessToken();
                String url = "https://open.feishu.cn/open-apis/sheets/v2/spreadsheets/" + feishuSheet.getSpreadSheetId() + "/values_batch_get?ranges=" + feishuSheet.getSheetId() + "!A1:" + feishuSheet.getEndColumn() + "&valueRenderOption=ToString&dateTimeRenderOption=FormattedString";
                System.out.println(url);
                FeishuSheetResponse sheetResponse = feishuService.getFeishuSheetData(url, tenantAccessToken);
                // 2. 将表格数据转换为DTO对象
                FeishuSheetConvertResult<StoreCelebrityPrivateExcelModel> convertResult = feishuService.convertSheetDataToEntityNew(sheetResponse, StoreCelebrityPrivateExcelModel.class);
                List<StoreCelebrityPrivateExcelModel> entityList = convertResult.getEntityList();
                // 过滤需要导入的数据
                List<StoreCelebrityPrivateExcelModel> privateExcelModels = entityList.stream().filter(x -> (oConvertUtils.isEmpty(x.getIsUpdate()) || x.getIsUpdate().equals("否")) && oConvertUtils.isNotEmpty(x.getAccount())).toList();
                if (privateExcelModels.isEmpty()) {
                    System.out.printf("用户：%s,没有需要导入的数据%n", username);
                    continue;
                }
                Map<String, Object> map = privateService.importFeishuCelebrityPrivate(privateExcelModels, userId, username);
                List<StoreCelebrityPrivateExcelModel> successImportList = (List<StoreCelebrityPrivateExcelModel>) map.get("successImportList");
                if (!successImportList.isEmpty()) {
                    updateImportStatus(feishuSheet, successImportList, "是");
                }
                Set<StoreCelebrityPrivateExcelModel> errorList = (Set<StoreCelebrityPrivateExcelModel>) map.get("errorList");
                if (!errorList.isEmpty()) {
                    // 修改飞书表格导入状态
                    updateImportStatus(feishuSheet, new ArrayList<>(errorList), "否");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return Result.ok("操作成功");
    }

    /**
     * @description: 修改飞书导入状态
     * @author: nqr
     * @date: 2025/9/16 7:52
     **/
    private void updateImportStatus(KolSysUserFeishuSheet feishuSheet, List<StoreCelebrityPrivateExcelModel> resultList, String importStatus) {
        List<Map<String, Object>> arrayList = new ArrayList<>();
        resultList.forEach(x -> {
            arrayList.add(new HashMap<String, Object>() {
                {
                    put("range", feishuSheet.getSheetId() + "!O" + x.getRowNum() + ":P" + x.getRowNum());
                    put("values", Collections.singletonList(Arrays.asList(importStatus, x.getFailReason())));
                }
            });
        });
        Map<String, Object> values = new HashMap<String, Object>() {
            {
                put("valueRanges", arrayList);
            }
        };
        System.out.println(values);
        feishuService.updateValuesBatch(feishuSheet, values);
    }

    /**
     * 检查合同金额是否符合筛选条件
     *
     * @param privateCounselor 私有网红顾问对象
     * @param model            筛选条件
     * @return 是否符合条件
     */
    private boolean isContractAmountMatch(StoreCelebrityPrivateCounselor privateCounselor, StoreCelebrityPrivateModel model) {
        // 如果没有金额筛选条件，直接返回true
        if (model.getMixContractAmount() == null && model.getMaxContractAmount() == null) {
            return true;
        }

        String contractInfo = privateCounselor.getContractInfo();
        if (StringUtils.isBlank(contractInfo)) {
            return false; // 没有合同信息，不符合条件
        }

        try {
            // 解析JSON数组
            List<JSONObject> contractList = JSON.parseArray(contractInfo, JSONObject.class);
            if (contractList == null || contractList.isEmpty()) {
                return false;
            }

            // 检查是否有任意一个合同金额符合条件
            for (JSONObject contract : contractList) {
                BigDecimal contractAmount = contract.getBigDecimal("contractAmount");
                if (contractAmount != null) {
                    boolean matchesMin = model.getMixContractAmount() == null || contractAmount.compareTo(model.getMixContractAmount()) >= 0;
                    boolean matchesMax = model.getMaxContractAmount() == null || contractAmount.compareTo(model.getMaxContractAmount()) <= 0;

                    if (matchesMin && matchesMax) {
                        return true; // 找到符合条件的合同
                    }
                }
            }

            return false; // 没有符合条件的合同

        } catch (Exception e) {
            log.warn("解析contract_info失败，跳过该记录: {}", contractInfo, e);
            return false;
        }
    }

    /**
     * 格式化合同信息显示
     */
    private String formatContractInfo(String contractInfo) {
        if (StringUtils.isBlank(contractInfo)) {
            return "";
        }

        try {
            // 解析JSON数组
            List<JSONObject> contractList = JSON.parseArray(contractInfo, JSONObject.class);
            if (contractList == null || contractList.isEmpty()) {
                return "";
            }

            // 格式化每个合同项目
            List<String> formattedItems = new ArrayList<>();
            for (JSONObject contract : contractList) {
                String videoTag = contract.getString("videoTag");
                BigDecimal contractAmount = contract.getBigDecimal("contractAmount");

                if (StringUtils.isNotBlank(videoTag) && contractAmount != null) {
                    // 格式化金额，去掉小数点后的0
                    String amountStr = contractAmount.stripTrailingZeros().toPlainString();
                    formattedItems.add(videoTag + "：" + amountStr);
                }
            }

            // 用逗号连接所有项目
            return String.join(",", formattedItems);

        } catch (Exception e) {
            log.warn("解析contract_info失败: {}", contractInfo, e);
            return contractInfo; // 解析失败时返回原始字符串
        }
    }

    @AutoLog(value = "个性化标签-确认标签")
    @Operation(summary = "个性化标签-确认标签", description = "个性化标签-确认标签")
    @PostMapping(value = "/confirmedPersonalTags")
    public Result<?> confirmedPersonalTags(@RequestBody StoreCelebrityPrivateModel storeCelebrityPrivateModel) {

        String privateId = storeCelebrityPrivateModel.getId();
        if (oConvertUtils.isEmpty(privateId)) return Result.error("参数错误，请重新输入");
        // 更新个性化标签
        String personalTagIds = storeCelebrityPrivateModel.getPersonalTagIds();
        if (oConvertUtils.isEmpty(personalTagIds)) {
            // 2025年10月24日10:55:24 刘工要求增加个性化标签确认的时候，如果不存在个性化标签必须选择一个无，不能空着不选
            return Result.error("个性化标签不能为空");
            // 我觉的需要改一下，如果不存在个性化标签默认：“无”，这样减少用户操作
            // StorePersonalTags personalTags =
            // personalTagsService.lambdaQuery().eq(StorePersonalTags::getTagName,
            // "无").list().get(0);
            // storeCelebrityPrivateModel.setPersonalTagIds(personalTags.getId());
        }
        List<StoreCelebrityPrivatePersonalTags> privatePersonalTagsList = new ArrayList<>();
        // 编辑个性化标签
        storeCelebrityPrivateService.processingPersonalizedTags(privatePersonalTagsList, privateId, personalTagIds);

        storeCelebrityPrivateService.confirmedPersonalTags(privateId, privatePersonalTagsList);

        return Result.ok("确认成功");
    }

    /**
     * @description: 私有网红-编辑国家和性别
     * @author: nqr
     * @date: 2025/10/27 17:27
     **/
    @AutoLog(value = "私有网红-编辑国家和性别")
    @Operation(summary = "个性化标签-确认标签", description = "个性化标签-确认标签")
    @GetMapping(value = "/editCelebrityPrivate")
    public Result<?> editCelebrityPrivate(StoreCelebrityPrivateModel storeCelebrityPrivateModel) {
        String privateId = storeCelebrityPrivateModel.getId();
        String countryId = storeCelebrityPrivateModel.getCountryId();
        Integer gender = storeCelebrityPrivateModel.getGender();
        String account = storeCelebrityPrivateModel.getAccount();
        Integer isTopStar = storeCelebrityPrivateModel.getIsTopStar();
        if (oConvertUtils.isEmpty(privateId) || (oConvertUtils.isEmpty(countryId) || gender == null))
            return Result.error("参数错误，请重新输入");
        StoreCelebrityPrivate storeCelebrityPrivate = new StoreCelebrityPrivate();
        storeCelebrityPrivate.setId(privateId);
        if (oConvertUtils.isNotEmpty(countryId)) {
            StoreCountry country = storeCountryService.getById(countryId);
            storeCelebrityPrivate.setCountryId(country.getId());
            storeCelebrityPrivate.setCountryName(country.getCountry());
        }
        if (oConvertUtils.isNotEmpty(gender)) {
            storeCelebrityPrivate.setGender(gender);
        }
        if (oConvertUtils.isNotEmpty(account)) {
            storeCelebrityPrivate.setAccount(account);
        }
        if (oConvertUtils.isNotEmpty(isTopStar)) {
            storeCelebrityPrivate.setIsTopStar(isTopStar);
        }
        storeCelebrityPrivateService.updateById(storeCelebrityPrivate);
        return Result.ok("修改成功");
    }

    @AutoLog(value = "私有网红-发送记录-一键私有")
    @Operation(summary = "私有网红-发送记录-一键私有", description = "私有网红-发送记录-一键私有")
    @PostMapping(value = "/push/celebrityAdd")
    public Result<?> celebrityAdd(@RequestBody List<StoreCelebrityPrivateExcelModel> privateExcelModels) {
        String userId = getUserIdByToken();
        String username = getUserNameByToken();
        for (StoreCelebrityPrivateExcelModel model : privateExcelModels) {
            String account = model.getAccount();
            Integer platformType = model.getPlatformType();
            if (oConvertUtils.isEmpty(account) || oConvertUtils.isEmpty(platformType)) {
                return Result.error("账号或平台不能为空");
            }
            switch (platformType) {
                case CommonConstant.TK:
                    List<TiktokCelebrity> tkCelebrities = tkCelebrityService.lambdaQuery().eq(TiktokCelebrity::getUniqueId, account).list();
                    if (!tkCelebrities.isEmpty()) {
                        TiktokCelebrity tiktokCelebrity = tkCelebrities.get(0);
                        model.setCelebrityId(tiktokCelebrity.getSecUid());
                        model.setFollowersNumStr(String.valueOf(tiktokCelebrity.getAuthorFollowerCount()));
                        model.setFollowersNum(tiktokCelebrity.getAuthorFollowerCount());
                        model.setPlayAvgNumStr(String.valueOf(tiktokCelebrity.getPlayAvgNum()));
                        model.setPlayAvgNum(BigInteger.valueOf(tiktokCelebrity.getPlayAvgNum()));
                    }
                    break;
                case CommonConstant.YT:
                    List<YoutubeCelebrity> ytCelebrities = ytCelebrityService.lambdaQuery().eq(YoutubeCelebrity::getUsername, account).list();
                    if (!ytCelebrities.isEmpty()) {
                        YoutubeCelebrity ytCelebrity = ytCelebrities.get(0);
                        model.setCelebrityId(ytCelebrity.getAccount());
                        model.setFollowersNumStr(String.valueOf(ytCelebrity.getFollowersNum()));
                        model.setFollowersNum(Math.toIntExact(ytCelebrity.getFollowersNum()));
                        model.setPlayAvgNumStr(String.valueOf(ytCelebrity.getPlayAvgNum()));
                        model.setPlayAvgNum(ytCelebrity.getPlayAvgNum());
                    }
                    break;
                case CommonConstant.IG:
                    List<IgCelebrity> igCelebrities = igCelebrityService.lambdaQuery().eq(IgCelebrity::getIgUsername, account).list();
                    if (!igCelebrities.isEmpty()) {
                        IgCelebrity igCelebrity = igCelebrities.get(0);
                        model.setCelebrityId(igCelebrity.getAccount());
                        model.setFollowersNumStr(String.valueOf(igCelebrity.getFollowerCount()));
                        model.setFollowersNum(igCelebrity.getFollowerCount());
                        model.setPlayAvgNumStr(String.valueOf(igCelebrity.getAvgPlayCount()));
                        model.setPlayAvgNum(BigInteger.valueOf(igCelebrity.getAvgPlayCount()));
                    }
            }
            List<StoreCelebrityPrivateExcelModel.PrivateContractInfo> contractInfoList = model.getPrivateContractInfoList();
            if (contractInfoList != null && !contractInfoList.isEmpty()) {
                for (int i = 0; i < contractInfoList.size(); i++) {
                    StoreCelebrityPrivateExcelModel.PrivateContractInfo info = contractInfoList.get(i);
                    if (i == 0) {
                        model.setVideoTags(info.getVideoTag());
                        model.setContractAmountStr(String.valueOf(info.getContractAmount()));
                    } else if (i == 1) {
                        model.setVideoTags2(info.getVideoTag());
                        model.setContractAmountStr2(String.valueOf(info.getContractAmount()));
                    } else if (i == 2) {
                        model.setVideoTags3(info.getVideoTag());
                        model.setContractAmountStr3(String.valueOf(info.getContractAmount()));
                    }
                }
            }
        }
        Map<String, Object> map = privateService.importFeishuCelebrityPrivate(privateExcelModels, userId, username);
        return Result.OK("操作成功", map);
    }

    @AutoLog(value = "私有网红-创建私有网红模板")
    @Operation(summary = "私有网红-创建私有网红模板", description = "私有网红-创建私有网红模板")
    @GetMapping(value = "/createCelebritySheet")

    public Result<?> createSheet(@RequestParam(name = "userAccessToken") String userAccessToken, @RequestParam(name = "userId") String userId, @RequestParam(name = "openId") String openId) {
        SysUser user = userService.getById(userId);

        // 构建client
        Client client = Client.newBuilder("cli_a75019eb8c3f901c", "nY5jbozz0QhH1wwmwHEusdZTPnlJahXB").build();

        // 创建请求对象
        try {
            CopyFileReq req = CopyFileReq.newBuilder().fileToken("Z2uSsM1rxhRm7JtvkySclhSUnXb").copyFileReqBody(CopyFileReqBody.newBuilder().name("私有网红模板-" + user.getRealname()).type("sheet").folderToken("HxTAfPO1ClacUqduqu5c2dYVnQf").build()).build();
            String feishuOpenId = user.getFeishuOpenId();
            if (oConvertUtils.isEmpty(feishuOpenId)) {
                user.setFeishuOpenId(openId);
                userService.lambdaUpdate().set(SysUser::getFeishuOpenId, openId).eq(SysUser::getId, userId).update();
            }

            // 发起请求
            CopyFileResp resp = client.drive().v1().file().copy(req, RequestOptions.newBuilder().userAccessToken(userAccessToken).build());

            // 处理服务端错误
            if (!resp.success()) {
                System.out.printf("code:%s,msg:%s,reqId:%s, resp:%s%n", resp.getCode(), resp.getMsg(), resp.getRequestId(), JSON.toJSONString(new String(resp.getRawResponse().getBody(), StandardCharsets.UTF_8)));
                return Result.error("操作失败，请联系管理员");
            }
            CopyFileRespBody data = resp.getData();
            File file = data.getFile();
            String sheetId = file.getToken();
            String fileUrl = file.getUrl();
            // 保存sheet信息
            getSheet(sheetId, client, user, fileUrl, userAccessToken);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            // 修改飞书sheet
            updateSheetData(0, userId);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return Result.ok("创建成功");
    }

    public void sheetsBatchUpdate(String sheetId, String viewId, String openId, String userAccessToken) {
        String url = "https://open.feishu.cn/open-apis/sheets/v2/spreadsheets/" + sheetId + "/sheets_batch_update?user_id_type=open_id";
        List<String> list = new ArrayList<>();
        list.add("ou_99142b454d89d29c66af8e348a0838d0");
        list.add("ou_72f614c4a6182af5355f20e070d4e0d5");
        try {
            if (oConvertUtils.isNotEmpty(openId)) {
                list.add(openId);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        Map<String, Object> body = new HashMap<>();
        body.put("requests", Collections.singletonList(Collections.singletonMap("updateSheet", Collections.singletonMap("properties", new HashMap<String, Object>() {{
            put("sheetId", viewId);
            put("protect", new HashMap<String, Object>() {{
                put("lock", "LOCK");
                put("userIDs", list);
            }});
        }}))));
        JSONObject params = (JSONObject) JSON.toJSON(body);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + userAccessToken);
        headers.add("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
        ResponseEntity<JSONObject> response = RestUtil.request(url, HttpMethod.POST, headers, null, params, JSONObject.class);
        // 输出响应
        System.out.println("响应状态码: " + response.getStatusCodeValue());
        System.out.println("响应内容: " + response.getBody());
    }

    public void getSheet(String sheetId, Client client, SysUser user, String fileUrl, String userAccessToken) {
        try {
            // 创建请求对象
            QuerySpreadsheetSheetReq req = QuerySpreadsheetSheetReq.newBuilder().spreadsheetToken(sheetId).build();

            // 发起请求
            QuerySpreadsheetSheetResp resp = client.sheets().v3().spreadsheetSheet().query(req);

            com.lark.oapi.service.sheets.v3.model.Sheet[] sheets = resp.getData().getSheets();
            System.out.println(sheets);

            List<KolSysUserFeishuSheet> feishuSheets = new ArrayList<>();
            for (com.lark.oapi.service.sheets.v3.model.Sheet sheet : sheets) {
                String spreadSheetType = getSpreadSheetType(sheet.getTitle());
                if (oConvertUtils.isEmpty(spreadSheetType)) {
                    System.out.println("sheetId:" + sheet.getSheetId() + " title:" + sheet.getTitle() + " 未找到对应类型");
                    continue;
                }

                String viewId = sheet.getSheetId();
                KolSysUserFeishuSheet feishuSheet = new KolSysUserFeishuSheet();
                feishuSheet.setId(IdWorker.get32UUID());
                feishuSheet.setSysUserId(user.getId());
                feishuSheet.setUsername(user.getUsername());
                feishuSheet.setRealname(user.getRealname());
                feishuSheet.setSpreadSheetId(sheetId);
                feishuSheet.setSpreadSheetUrl(fileUrl + "?sheet=" + viewId);
                feishuSheet.setSheetId(viewId);
                feishuSheet.setSpreadSheetType(spreadSheetType);
                if (spreadSheetType.equals(SheetConstants.PRIVATE_CELEBRITY)) {
                    feishuSheet.setStartColumn("A");
                    feishuSheet.setEndColumn("P");
                    feishuSheet.setHeaderRow(1);
                    feishuSheet.setIsSynchronizeColumn("O");
                    feishuSheet.setErrorReasonColumn("P");
                    sheetsBatchUpdate(sheetId, viewId, user.getFeishuOpenId(), userAccessToken);
                    // 修改sheet权限
                    createPermission(client, sheetId, user.getFeishuOpenId(), userAccessToken);
                } else {
                    sheetsBatchUpdate(sheetId, viewId, "", userAccessToken);
                }
                feishuSheet.setCreateBy("system");
                feishuSheet.setCreateTime(new Date());
                feishuSheet.setDelFlag(0);
                feishuSheets.add(feishuSheet);
            }
            sheetService.saveBatch(feishuSheets);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createPermission(Client client, String sheetId, String openId, String userAccessToken) {
        // 创建请求对象
        CreatePermissionMemberReq req = CreatePermissionMemberReq.newBuilder().token(sheetId).type("sheet").needNotification(false).baseMember(BaseMember.newBuilder().memberType("openid").memberId(openId).perm("edit").type("user").build()).build();

        try {
            // 发起请求
            CreatePermissionMemberResp resp = client.drive().v1().permissionMember().create(req, RequestOptions.newBuilder().userAccessToken(userAccessToken).build());

            // 处理服务端错误
            if (!resp.success()) {
                System.out.println(String.format("code:%s,msg:%s,reqId:%s, resp:%s", resp.getCode(), resp.getMsg(), resp.getRequestId(), JSON.toJSONString(new String(resp.getRawResponse().getBody(), StandardCharsets.UTF_8))));
                return;
            }

            // 业务数据处理
            System.out.println(Jsons.DEFAULT.toJson(resp.getData()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private String getSpreadSheetType(String title) {
        switch (title) {
            case "私有网红模版":
                return SheetConstants.PRIVATE_CELEBRITY;
            case "个性化标签":
                return SheetConstants.PERSONAL_TAGS;
            case "国家":
                return SheetConstants.COUNTRY;
            case "建联邮箱":
                return SheetConstants.CONTACT_EMAIL;
            case "内容":
                return SheetConstants.CONTENT;
        }
        return null;
    }

    private void updateSheetData(int type, String userId) {
        List<StoreUserContactEmail> list = contactEmailService.lambdaQuery()
                .eq(StoreUserContactEmail::getSysUserId, userId).eq(StoreUserContactEmail::getEmailStatus, 1).list();
        List<String> emails = list.stream().map(StoreUserContactEmail::getContactEmail).toList();
        KolSysUserFeishuSheet feishuSheet = sheetService.lambdaQuery()
                .eq(KolSysUserFeishuSheet::getSysUserId, userId)
                .eq(KolSysUserFeishuSheet::getSpreadSheetType, SheetConstants.CONTACT_EMAIL).one();
        if (feishuSheet == null) {
            System.out.println("userId" + userId + "，没有飞书建联邮箱文档");
            return;
        }
        feishuService.insertOrUpdatePersonalTagsSheet(type, feishuSheet, emails);
    }
}
