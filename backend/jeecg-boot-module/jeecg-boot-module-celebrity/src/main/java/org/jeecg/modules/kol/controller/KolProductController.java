package org.jeecg.modules.kol.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.SystemConstants;
import org.jeecg.common.system.api.IKolCelebrityApi;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.kol.entity.*;
import org.jeecg.modules.feishu.config.FeishuAppConfig;
import org.jeecg.modules.feishu.service.IFeishuService;
import org.jeecg.modules.kol.model.KolProductModel;
import org.jeecg.modules.kol.model.TypeData;
import org.jeecg.modules.kol.service.*;
import org.jeecg.modules.kol.wechatexcel.config.WechatDocConfig;
import org.jeecg.modules.kol.wechatexcel.model.Record;
import org.jeecg.modules.kol.wechatexcel.service.WechatService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @Description: 品牌与产品关联表
 * @Author: nqr
 * @Date: 2025-05-24
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "品牌与产品关联表")
@RestController
@RequestMapping("/kol/kolProduct")
public class KolProductController extends JeecgController<KolProduct, IKolProductService> {
    @Autowired
    private IKolProductService productService;
    @Autowired
    private IKolProductCategoryService productCategoryService;
    @Autowired
    private IKolProductAttributeRelationService productAttributeRelationService;
    @Autowired
    private IKolBrandService brandService;
    @Autowired
    private IKolCategoryService categoryService;
    @Autowired
    private IKolCategoryAttributeRelationService categoryRelationService;
    @Autowired
    private IKolTagProductService tagProductService;
    @Autowired
    private IKolAttributeTypeService categoryTypeService;
    @Autowired
    private IKolCelebrityApi storeCelebrityApi;
    @Autowired
    private IFeishuService feishuService;
    @Autowired
    private FeishuAppConfig feishuAppConfig;
    @Resource
    private WechatService wechatService;
    @Resource
    private WechatDocConfig wechatDocConfig;

    /**
     * 分页列表查询
     *
     * @param kolProductModel
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "品牌与产品关联表-" + SystemConstants.PAGE_LIST_QUERY)
    @Operation(summary = "品牌与产品关联表-" + SystemConstants.PAGE_LIST_QUERY, description = "品牌与产品关联表-" + SystemConstants.PAGE_LIST_QUERY)
    @GetMapping(value = "/list")
    public Result<?> queryPageList(KolProductModel kolProductModel, @RequestParam(name = SystemConstants.PAGE_NO, defaultValue = SystemConstants.PAGE_NO_DEFAULT) Integer pageNo, @RequestParam(name = SystemConstants.PAGE_SIZE, defaultValue = SystemConstants.PAGE_SIZE_DEFAULT) Integer pageSize, HttpServletRequest req) {
        String categoryId = kolProductModel.getCategoryId();
        if (oConvertUtils.isNotEmpty(categoryId)) {
            KolCategory category = categoryService.getById(categoryId);
            kolProductModel.setCategoryCode(category.getCategoryCode());
            kolProductModel.setCategoryId(null);
        }
        Page<KolProductModel> page = new Page<>(pageNo, pageSize);
        IPage<KolProductModel> pageList = productService.pageListNew(page, kolProductModel);
        // 处理产品对应的社媒属性
        List<KolProductModel> productList = pageList.getRecords();
        for (KolProductModel product : productList) {
            List<KolProductAttributeRelation> attributeRelationList = productAttributeRelationService.lambdaQuery().eq(KolProductAttributeRelation::getProductId, product.getId()).list();
            Map<String, List<KolProductAttributeRelation>> map = attributeRelationList.stream().collect(Collectors.groupingBy(KolProductAttributeRelation::getAttributeTypeId));
            List<TypeData> dataList = new ArrayList<>();
            map.forEach((key, value) -> {
                TypeData typeData = new TypeData();
                typeData.setTypeId(key);
                List<TypeData.CategoryItem> list = new ArrayList<>();
                value.forEach(item -> {
                    TypeData.CategoryItem categoryItem = new TypeData.CategoryItem();
                    categoryItem.setLevel(item.getLevel());
                    categoryItem.setIsSel(true);
                    categoryItem.setAttributeId(item.getAttributeId());
                    categoryItem.setAttributeName(item.getAttributeName());
                    list.add(categoryItem);
                });
                typeData.setList(list);
                dataList.add(typeData);
            });
            product.setDataList(dataList);
        }
        pageList.setRecords(productList);
        return Result.ok(pageList);
    }

    @GetMapping(value = "/listAll")
    public Result<?> listAll(KolProductModel kolProductModel) {
        List<KolProductModel> list = productService.getProductListAll(kolProductModel);
        return Result.ok(list);
    }

    /**
     * 添加
     *
     * @param productModel
     * @return
     */
    @AutoLog(value = "品牌与产品关联表-" + SystemConstants.ADD)
    @Operation(summary = "品牌与产品关联表-" + SystemConstants.ADD, description = "品牌与产品关联表-" + SystemConstants.ADD)
    @Valid
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody KolProductModel productModel) {
        String brandId = productModel.getBrandId();
        KolBrand brand = brandService.getById(brandId);
        if (brand == null) {
            return Result.error("品牌不存在！");
        }
        // nqr 2025年6月27日09:19:08 不校验链接是否重复，允许用户添加相同品牌、产品、链接 数据
        // 2025年8月30日16:35:22 校验品牌产品名称唯一，郭梦娇说的
        Integer productCount = Math.toIntExact(productService.lambdaQuery()
                .eq(KolProduct::getBrandId, productModel.getBrandId())
                .eq(KolProduct::getProductName, productModel.getProductName())
                .ne(KolProduct::getId, productModel.getId())
                .count());

        if (productCount > 0) {
            return Result.error(String.format("品牌：%s, 产品：%s 已存在！", brand.getBrandName(), productModel.getProductName()));
        }
        // 去除空格
        cleanProductModel(productModel);
        String productName = productModel.getProductName();
        if (productName.contains("-")) {
            return Result.error("'-'为系统保留字符，产品名称不能包含该符号！");
        }


        List<TypeData> dataList = productModel.getDataList();

        // 基础参数校验
        Result<?> validationResult = validateBasicParams(productModel, dataList);
        if (!validationResult.isSuccess()) {
            return validationResult;
        }

        boolean isUpdatePromotionGoods = false;
        // 判断产品是否修改
        if (oConvertUtils.isNotEmpty(productModel.getId())) {
            isUpdatePromotionGoods = true;
        }
        KolProduct productSave = null;
        KolProduct productUpdate = null;
        // 修改产品
        if (oConvertUtils.isNotEmpty(productModel.getId())) {
            productUpdate = new KolProduct();
            BeanUtils.copyProperties(productModel, productUpdate);
            productUpdate.setId(productModel.getId());
            productUpdate.setIsDelete(0);
            productUpdate.setBrandName(brand.getBrandName());
            productModel.setId(productUpdate.getId());
        } else {
            // 新增产品
            productSave = new KolProduct();
            BeanUtils.copyProperties(productModel, productSave);
            productSave.setId(IdWorker.get32UUID());
            productSave.setIsDelete(0);
            productSave.setBrandName(brand.getBrandName());
            productModel.setId(productSave.getId());
        }
        productModel.setBrandName(brand.getBrandName());
        // 创建产品类目关联关系
        List<KolProductCategory> productCategoryList = productCategoryService.createProductCategory(productModel);
        List<KolProductAttributeRelation> productAttributeRelationList = productCategoryService.createAttributeRelation(productSave == null ? productUpdate : productSave, dataList);

        productService.saveProduct(productSave, productUpdate, productCategoryList, productAttributeRelationList, isUpdatePromotionGoods);
       /* // 异步更新企微文档
        if (productSave != null) {
            asyncUpdateLabels(1, productSave);
        } else {
            KolProduct kolProduct = productService.getById(productUpdate.getId());
            asyncUpdateLabels(2, kolProduct);
        }*/
        return Result.ok(SystemConstants.ADD_SUCCESS);
    }

    /**
     * 基础参数校验
     */
    private Result<?> validateBasicParams(KolProductModel productModel, List<TypeData> dataList) {
        String brandId = productModel.getBrandId();
        String productName = productModel.getProductName();
        String productUrl = productModel.getProductUrl();

        if (oConvertUtils.isEmpty(productUrl) || oConvertUtils.isEmpty(productName) || oConvertUtils.isEmpty(brandId)) {
            return Result.error("产品名称、品牌名称、产品链接不能为空");
        }
        if (oConvertUtils.isEmpty(dataList)) {
            return Result.error("关联数据不能为空！");
        }
        // 检查是否存在空的关联数据
        boolean hasEmptyList = dataList.stream().anyMatch(typeData -> typeData.getList().isEmpty());
        if (hasEmptyList) {
            return Result.error("关联数据不能为空！");
        }
        return Result.ok();
    }

    /**
     * 编辑
     *
     * @param kolProduct
     * @return
     */
    @AutoLog(value = "品牌与产品关联表-" + SystemConstants.EDIT)
    @Operation(summary = "品牌与产品关联表-" + SystemConstants.EDIT, description = "品牌与产品关联表-" + SystemConstants.EDIT)
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody KolProduct kolProduct) {
        return Result.ok(SystemConstants.EDIT_SUCCESS);
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "品牌与产品关联表-" + SystemConstants.DELETE_BY_ID)
    @Operation(summary = "品牌与产品关联表-" + SystemConstants.DELETE_BY_ID, description = "品牌与产品关联表-" + SystemConstants.DELETE_BY_ID)
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        // 查询当前产品是否在标签中使用
        Integer count = Math.toIntExact(tagProductService.lambdaQuery().eq(KolTagProduct::getProductId, id).count());
        if (count > 0) {
            return Result.error("该产品已被标签使用，不能删除！");
        }
        Integer celebrityCount = storeCelebrityApi.getByProductId(id);
        if (celebrityCount > 0) {
            return Result.error("该产品已关联网红，不能删除！");
        }
        boolean hasPromGoods = productService.getPromGoods(id);
        if (hasPromGoods) {
            return Result.error("该产品已被推广商品使用，不能删除！");
        }
        // KolProduct kolProduct = productService.getById(id);
        productService.removeById(id);
        productCategoryService.lambdaUpdate().eq(KolProductCategory::getProductId, id).remove();
        // 异步更新企微文档
        // asyncUpdateLabels(0, kolProduct);
       /* // 删除飞书中对应数据
        try {
            String productAttributes = kolProduct.getProductAttributes();
            JSONObject jsonObject = JSON.parseObject(productAttributes);
            String productModel = jsonObject.getString("productModel");
            String format = String.format("%s-%s-%s", kolProduct.getBrandName(), kolProduct.getProductName(), productModel);
            CompletableFuture.runAsync(() -> {
                // 获取原数据行id
                String recordId = feishuService.getRecordIds(feishuAppConfig.getTagProductTableId(), feishuAppConfig.getTagProductViewId(), "产品", format);
                if (oConvertUtils.isNotEmpty(recordId)) {
                    feishuService.batchDeleteRecords(feishuAppConfig.getAppToken(), feishuAppConfig.getTagProductTableId(), recordId);
                }
            });
        } catch (Exception ignored) {
            System.err.println("删除产品同步删除飞书数据错误");
        }*/

        return Result.ok(SystemConstants.DELETE_SUCCESS);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return0
     */
    @AutoLog(value = "品牌与产品关联表-" + SystemConstants.DELETE_BATCH)
    @Operation(summary = "品牌与产品关联表-" + SystemConstants.DELETE_BATCH, description = "品牌与产品关联表-" + SystemConstants.DELETE_BATCH)
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        return Result.error("无法批量删除，请逐条删除！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "品牌与产品关联表-" + SystemConstants.QUERY_BY_ID)
    @Operation(summary = "品牌与产品关联表-" + SystemConstants.QUERY_BY_ID, description = "品牌与产品关联表-" + SystemConstants.QUERY_BY_ID)
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        KolProduct kolProduct = productService.getById(id);
        return Result.ok(kolProduct);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param kolProduct
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, KolProduct kolProduct) {
        return super.exportXls(request, kolProduct, KolProduct.class, "品牌与产品关联表");
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
        try {
            // 1. 获取上传文件
            MultipartFile file = getUploadedFile(request);
            if (file == null) {
                return Result.error("未找到上传文件");
            }

            // 2. 解析Excel数据
            List<KolProductModel> productModels = parseExcelFile(file);
            if (productModels.isEmpty()) {
                return Result.error("未获取到有效数据！");
            }

            // 3. 数据校验
            Result<?> validationResult = validateData(productModels);
            if (!validationResult.isSuccess()) {
                return validationResult;
            }

            // 4. 处理数据导入
            return processDataImport(productModels);

        } catch (Exception e) {
            log.error("Excel导入失败", e);
            return Result.error("文件导入失败，请检查文件内容是否正确！");
        }
    }

    /**
     * 获取上传的文件
     */
    private MultipartFile getUploadedFile(HttpServletRequest request) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();

        return fileMap.values().stream()
                .findFirst()
                .orElse(null);
    }

    /**
     * 解析Excel文件
     */
    private List<KolProductModel> parseExcelFile(MultipartFile file) throws Exception {
        ImportParams params = new ImportParams();
        params.setHeadRows(1);

        try (InputStream inputStream = file.getInputStream()) {
            List<KolProductModel> list = ExcelImportUtil.importExcel(inputStream, KolProductModel.class, params);

            // 过滤空数据并清理字符串
            return list.stream()
                    .filter(x -> oConvertUtils.isNotEmpty(x.getProductName()))
                    .peek(this::cleanProductModel)
                    .collect(Collectors.toList());
        }
    }

    /**
     * 清理产品模型数据
     */
    private void cleanProductModel(KolProductModel model) {
        if (model.getProductName() != null) {
            model.setProductName(model.getProductName().trim());
        }
        if (model.getProductUrl() != null) {
            model.setProductUrl(model.getProductUrl().trim());
        }
        if (model.getBrandName() != null) {
            model.setBrandName(model.getBrandName().trim());
        }
        if (model.getCategoryName() != null) {
            model.setCategoryName(model.getCategoryName().trim());
        }
    }

    /**
     * 数据校验
     */
    private Result<?> validateData(List<KolProductModel> productModels) {
        List<String> errorList = new ArrayList<>();

        // 校验品牌
        Result<?> brandValidation = validateBrands(productModels);
        if (!brandValidation.isSuccess()) {
            return brandValidation;
        }

        // 校验类目
        Result<?> categoryValidation = validateCategories(productModels, errorList);
        if (!categoryValidation.isSuccess()) {
            return categoryValidation;
        }

        // 校验产品URL
        validateProductUrls(productModels, errorList);

        // 处理产品属性
        processProductAttributes(productModels);

        if (!errorList.isEmpty()) {
            return Result.error(100001, "导入失败", errorList);
        }

        return Result.ok();
    }

    /**
     * 校验品牌
     */
    private Result<?> validateBrands(List<KolProductModel> productModels) {
        Set<String> brandNames = productModels.stream()
                .map(KolProductModel::getBrandName)
                .filter(name -> name != null && !name.isEmpty())
                .collect(Collectors.toSet());

        if (brandNames.isEmpty()) {
            return Result.error("没有有效的品牌名称");
        }

        List<KolBrand> existingBrands = brandService.lambdaQuery()
                .in(KolBrand::getBrandName, brandNames)
                .list();

        if (existingBrands.size() != brandNames.size()) {
            Set<String> existingBrandNames = existingBrands.stream()
                    .map(KolBrand::getBrandName)
                    .collect(Collectors.toSet());

            String missingBrands = brandNames.stream()
                    .filter(name -> !existingBrandNames.contains(name))
                    .collect(Collectors.joining(","));

            return Result.error(String.format("品牌：'%s' 不存在", missingBrands));
        }

        return Result.ok();
    }

    /**
     * 校验类目
     */
    private Result<?> validateCategories(List<KolProductModel> productModels, List<String> errorList) {
        List<String> categoryNames = productModels.stream()
                .map(KolProductModel::getCategoryName)
                .distinct()
                .collect(Collectors.toList());

        List<KolCategory> existingCategories = categoryService.lambdaQuery()
                .in(KolCategory::getCategoryName, categoryNames)
                .list();

        // 检查类目是否存在
        if (existingCategories.size() != categoryNames.size()) {
            Set<String> existingCategoryNames = existingCategories.stream()
                    .map(KolCategory::getCategoryName)
                    .collect(Collectors.toSet());

            String missingCategories = categoryNames.stream()
                    .filter(name -> !existingCategoryNames.contains(name))
                    .collect(Collectors.joining(","));

            return Result.error(oConvertUtils.isEmpty(missingCategories) ? "类目不能为空" : String.format("类目：'%s' 不存在", missingCategories));
        }

        // 检查类目配置
        validateCategoryConfiguration(existingCategories, errorList);

        return Result.ok();
    }

    /**
     * 校验类目配置
     */
    private void validateCategoryConfiguration(List<KolCategory> categories, List<String> errorList) {
        List<String> categoryIds = categories.stream()
                .map(KolCategory::getId)
                .collect(Collectors.toList());

        List<KolCategoryAttributeRelation> categoryRelations = categoryRelationService.lambdaQuery()
                .in(KolCategoryAttributeRelation::getCategoryId, categoryIds)
                .list();

        Map<String, List<KolCategoryAttributeRelation>> relationMap = categoryRelations.stream()
                .collect(Collectors.groupingBy(KolCategoryAttributeRelation::getCategoryId));

        for (KolCategory category : categories) {
            // 检查是否为子类目
            if (category.getIsHasChild() == 1) {
                errorList.add(String.format("类目：'%s'，不是子类目无法对照", category.getCategoryName()));
                continue;
            }
            // 检查是否有对照关系
            if (!relationMap.containsKey(category.getId())) {
                errorList.add(String.format("类目：'%s'，未设置对照关系", category.getCategoryName()));
            }
        }
    }

    /**
     * 校验产品URL
     */
    private void validateProductUrls(List<KolProductModel> productModels, List<String> errorList) {
        String urlRegex = "^(http|https)://[a-zA-Z0-9\\-\\.]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\-~!@#$%^&*+?:_/=.]*)?$";

        for (KolProductModel model : productModels) {
            String productUrl = model.getProductUrl();
            String productName = model.getProductName();

            if (oConvertUtils.isEmpty(productUrl)) {
                errorList.add(String.format("产品：'%s'，链接不能为空", productName));
                continue;
            }

            if (!productUrl.matches(urlRegex)) {
                errorList.add(String.format("产品：'%s'，链接格式不正确", productName));
            }
        }
    }

    /**
     * 处理产品属性
     */
    private void processProductAttributes(List<KolProductModel> productModels) {
        for (KolProductModel model : productModels) {
            String productAttributes = model.getProductAttributes();
            if (oConvertUtils.isNotEmpty(productAttributes)) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("productModel", productAttributes);
                model.setProductAttributes(jsonObject.toJSONString());
            }
        }
    }

    /**
     * 处理数据导入
     */
    private Result<?> processDataImport(List<KolProductModel> productModels) {
        long startTime = System.currentTimeMillis();

        try {
            // 获取基础数据
            Map<String, KolBrand> brandMap = getBrandMap(productModels);
            Map<String, KolCategory> categoryMap = getCategoryMap(productModels);
            List<KolProduct> existingProducts = productService.list();

            // 分类处理产品数据
            List<KolProduct> productsToSave = new ArrayList<>();
            List<KolProduct> productsToUpdate = new ArrayList<>();
            List<KolProductCategory> productCategories = new ArrayList<>();
            List<KolProductAttributeRelation> productAttributeRelations = new ArrayList<>();

            categorizeProducts(productModels, brandMap, categoryMap, existingProducts,
                    productsToSave, productsToUpdate, productCategories, productAttributeRelations);

            // 执行导入
            productService.importProduct(productsToSave, productsToUpdate, productCategories, productAttributeRelations);

            long duration = System.currentTimeMillis() - startTime;
            log.info("Excel导入完成，耗时: {} 毫秒", duration);

            return Result.ok("文件导入成功！数据行数：" + productModels.size());

        } catch (Exception e) {
            log.error("数据导入处理失败", e);
            throw new RuntimeException("数据导入处理失败", e);
        }
    }

    /**
     * 获取品牌映射
     */
    private Map<String, KolBrand> getBrandMap(List<KolProductModel> productModels) {
        Set<String> brandNames = productModels.stream()
                .map(KolProductModel::getBrandName)
                .collect(Collectors.toSet());

        List<KolBrand> brands = brandService.lambdaQuery()
                .in(KolBrand::getBrandName, brandNames)
                .list();

        return brands.stream()
                .collect(Collectors.toMap(KolBrand::getBrandName, brand -> brand));
    }

    /**
     * 获取类目映射
     */
    private Map<String, KolCategory> getCategoryMap(List<KolProductModel> productModels) {
        List<String> categoryNames = productModels.stream()
                .map(KolProductModel::getCategoryName)
                .distinct()
                .collect(Collectors.toList());

        List<KolCategory> categories = categoryService.lambdaQuery()
                .in(KolCategory::getCategoryName, categoryNames)
                .list();

        return categories.stream()
                .collect(Collectors.toMap(KolCategory::getCategoryName, category -> category));
    }

    /**
     * 分类处理产品数据
     */
    private void categorizeProducts(List<KolProductModel> productModels,
                                    Map<String, KolBrand> brandMap,
                                    Map<String, KolCategory> categoryMap,
                                    List<KolProduct> existingProducts,
                                    List<KolProduct> productsToSave,
                                    List<KolProduct> productsToUpdate,
                                    List<KolProductCategory> productCategories,
                                    List<KolProductAttributeRelation> productAttributeRelations) {

        ObjectMapper mapper = new ObjectMapper();

        for (KolProductModel model : productModels) {
            KolProduct product = createKolProduct(model, brandMap);

            // 检查产品是否已存在
            Optional<KolProduct> existingProduct = findExistingProduct(model, existingProducts, mapper);

            if (existingProduct.isPresent()) {
                product.setId(existingProduct.get().getId());
                productsToUpdate.add(product);
            } else {
                product.setId(IdWorker.get32UUID());
                productsToSave.add(product);
            }

            // 创建产品类目关系
            createProductCategoryRelations(product, model, brandMap, categoryMap, productCategories, productAttributeRelations);
        }
    }

    /**
     * 创建KolProduct对象
     */
    private KolProduct createKolProduct(KolProductModel model, Map<String, KolBrand> brandMap) {
        KolBrand brand = brandMap.get(model.getBrandName());

        KolProduct product = new KolProduct();
        BeanUtils.copyProperties(model, product);
        product.setProductName(model.getProductName());
        product.setProductUrl(model.getProductUrl());
        product.setBrandName(brand.getBrandName());
        product.setBrandId(brand.getId());
        product.setIsDelete(0);

        return product;
    }

    /**
     * 查找已存在的产品
     */
    private Optional<KolProduct> findExistingProduct(KolProductModel model, List<KolProduct> existingProducts, ObjectMapper mapper) {
        return existingProducts.stream()
                .filter(product -> isProductMatch(product, model, mapper))
                .findFirst();
    }

    /**
     * 判断产品是否匹配
     */
    private boolean isProductMatch(KolProduct existingProduct, KolProductModel model, ObjectMapper mapper) {
        try {
            Map<String, Object> existingAttr = mapper.readValue(existingProduct.getProductAttributes(), Map.class);
            if (existingAttr.isEmpty()) {
                return true;
            }
            Map<String, Object> modelAttr = mapper.readValue(model.getProductAttributes(), Map.class);

            return existingProduct.getProductName().equals(model.getProductName()) &&
                    existingProduct.getBrandName().equals(model.getBrandName()) &&
                    existingProduct.getProductUrl().equals(model.getProductUrl()) &&
                    existingAttr.equals(modelAttr);

        } catch (Exception e) {
            log.warn("产品属性比较失败", e);
            return false;
        }
    }

    /**
     * 创建产品类目关系
     */
    private void createProductCategoryRelations(KolProduct product, KolProductModel model,
                                                Map<String, KolBrand> brandMap,
                                                Map<String, KolCategory> categoryMap,
                                                List<KolProductCategory> productCategories,
                                                List<KolProductAttributeRelation> productAttributeRelations) {
        KolCategory category = categoryMap.get(model.getCategoryName());
        KolBrand brand = brandMap.get(model.getBrandName());

        // 获取类目对照数据
        List<TypeData> typeData = categoryRelationService.getProductCategory(category);
        model.setId(product.getId());
        model.setBrandId(brand.getId());
        model.setCategoryId(category.getId());
        List<KolProductCategory> categories = productCategoryService.createProductCategory(model);
        List<KolProductAttributeRelation> attributeRelations = productCategoryService.createAttributeRelation(product, typeData);

        if (!categories.isEmpty()) {
            productCategories.addAll(categories);
        }
        if (!attributeRelations.isEmpty()) {
            productAttributeRelations.addAll(attributeRelations);
        }
        // 创建自定义类目关系
        // createCategory(brand, product, category, productCategories);
    }

    private void createCategory(KolBrand brand, KolProduct kolProduct, KolCategory kolCategory, List<KolProductCategory> productCategorySaveList) {
        KolProductCategory productCategory = new KolProductCategory();
        productCategory.setId(IdWorker.get32UUID());
        productCategory.setBrandId(brand.getId());
        productCategory.setProductId(kolProduct.getId());
        productCategory.setCategoryId(kolCategory.getId());
        productCategory.setCategoryTypeId(kolCategory.getCategoryTypeId());
        productCategory.setBrandName(brand.getBrandName());
        productCategory.setProductName(kolProduct.getProductName());
        productCategory.setCategoryName(kolCategory.getCategoryName());
        productCategory.setCategoryTypeName(kolCategory.getCategoryTypeName());
        productCategory.setIsDelete(0);
        productCategorySaveList.add(productCategory);
    }

    /**
     * 下载模板
     */
    @RequestMapping(value = "/downloadXls", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadXls(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("Sheet");

        String[] header = {"产品名称", "品牌名称", "型号", "产品链接", "图片链接", "类目"};
        Row row = sheet.createRow(0);
        // 创建一个居中格式
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式
        // 水平居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        for (int i = 0; i < header.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(header[i]);
            cell.setCellStyle(style);  // 设置该单元格的样式为居中
            sheet.setColumnWidth(i, 4000);  // 调整列宽以适应内容
        }
        setDropdown(sheet);
        try {
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("", "UTF-8"));
            ServletOutputStream outputStream = response.getOutputStream();
            wb.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createDropdownList(HSSFSheet sheet, String[] dropdownValues, int column) {
        DVConstraint dvConstraint = DVConstraint.createExplicitListConstraint(dropdownValues);

        if (dropdownValues.length > 20) {
            // 创建命名范围
            String rangeName = "dropdown" + column; // 可以给命名范围起一个名称
            HSSFWorkbook workbook = sheet.getWorkbook();
            Name name = workbook.createName();
            name.setNameName(rangeName);

            // 在某个工作表中创建一个临时工作表，保存所有下拉列表的选项
            HSSFSheet tempSheet = workbook.createSheet("sheet" + column);

            // 将所有 dropdownValues 填充到临时工作表中
            for (int i = 0; i < dropdownValues.length; i++) {
                Row row = tempSheet.createRow(i);
                Cell cell = row.createCell(0);
                cell.setCellValue(dropdownValues[i]);
            }

            // 设置命名范围，指向临时工作表的所有数据
            name.setRefersToFormula(tempSheet.getSheetName() + "!$A$1:$A$" + dropdownValues.length);
            dvConstraint = DVConstraint.createFormulaListConstraint(rangeName);
            workbook.setSheetHidden(workbook.getSheetIndex(tempSheet), true);
        }
        CellRangeAddressList addressList = new CellRangeAddressList(1, 65535, column, column);
        DataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
        sheet.addValidationData(dataValidation);
    }

    /**
     * 方法描述:设置下拉框
     *
     * @author nqr
     * @date 2025/01/13 10:31
     **/
    private void setDropdown(HSSFSheet sheet) {
        // 查询类目,只查询子类目
        List<KolCategory> tagsList = categoryService.lambdaQuery().eq(KolCategory::getIsHasChild, 0).list();
        // 查询品牌
        List<KolBrand> brandList = brandService.queryListAll("");

        // 创建下拉列表: 品牌
        String[] brandDropdown = brandList.stream().map(KolBrand::getBrandName).distinct().toArray(String[]::new);
        createDropdownList(sheet, brandDropdown, 1);

        // 创建下拉列表: 类目
        tagsList.sort(Comparator.comparing(KolCategory::getCategoryCode, Comparator.nullsLast(String::compareTo)));
        String[] tagsDropdown = tagsList.stream().map(KolCategory::getCategoryName).distinct().toArray(String[]::new);
        createDropdownList(sheet, tagsDropdown, 5);
    }

    private void asyncUpdateLabels(int type, KolProduct kolProduct) {
        // 同步更新企微选项
        try {
            CompletableFuture.runAsync(() -> {
                // 获取当前操作
                switch (type) {
                    case 0:
                        // 删除记录
                        wechatService.removeRecords(wechatDocConfig.getTemplateDocId(), wechatDocConfig.getProductTagSheetId(), kolProduct.getRecordId());
                        break;
                    case 1:
                        // 新增记录
                        List<Map<String, Object>> recordList = new ArrayList<Map<String, Object>>() {{
                            add(new HashMap<String, Object>() {{
                                put("values", new HashMap<String, Object>() {{
                                    put("产品", new ArrayList<Map<String, Object>>() {{
                                        add(new HashMap<String, Object>() {{
                                            put("type", "text");
                                            put("text", kolProduct.getBrandName() + "-" + kolProduct.getProductName());
                                        }});
                                    }});
                                }});
                            }});
                        }};
                        List<Record> records = wechatService.createRecords(wechatDocConfig.getTemplateDocId(), wechatDocConfig.getProductTagSheetId(), recordList);
                        // 更新数据库中对应标签的recordId
                        KolProduct product = new KolProduct();
                        product.setId(kolProduct.getId());
                        product.setRecordId(records.get(0).getRecordId());
                        productService.updateById(product);
                        break;
                    case 2:
                        if (oConvertUtils.isEmpty(kolProduct.getRecordId())) {
                            break;
                        }
                        // 编辑记录
                        List<Map<String, Object>> updateRecords = Collections.singletonList(new HashMap<String, Object>() {{
                            put("record_id", kolProduct.getRecordId());
                            put("values", Collections.singletonMap("产品", Collections.singletonList(new HashMap<String, Object>() {{
                                put("text", kolProduct.getBrandName() + "-" + kolProduct.getProductName());
                                put("type", "text");
                            }})));
                        }});
                        wechatService.updateRecords(wechatDocConfig.getTemplateDocId(), wechatDocConfig.getProductTagSheetId(), updateRecords);
                        break;
                }
            });
        } catch (Exception e) {
            log.error("同步企微产品失败", e);
        }
    }
}
