package org.jeecg.modules.kol.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.CategoryType;
import org.jeecg.common.constant.enums.YesNoStatus;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.util.PageUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.kol.entity.*;
import org.jeecg.modules.kol.model.KolSearchModel;
import org.jeecg.modules.kol.model.KolTagCountModel;
import org.jeecg.modules.kol.model.KolTagsModel;
import org.jeecg.modules.kol.model.TypeData;
import org.jeecg.modules.kol.service.*;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.jeecg.common.constant.SystemConstants.*;
import static org.jeecg.common.constant.enums.TagType.HASHTAG;

/**
 * @Description: tag表
 * @Author: xyc
 * @Date: 2025-01-02 18:45:31
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "tag表")
@RestController
@RequestMapping("/kol/tags")
public class KolTagsController extends JeecgController<KolTags, IKolTagsService> {

    @Autowired
    private IKolTagsService kolTagsService;
    @Autowired
    private IKolTagBrandService kolTagsBrandService;
    @Autowired
    private IKolBrandService kolBrandService;
    @Autowired
    private IKolTagCategoryService tagCategoryService;
    @Autowired
    private ISysBaseAPI sysBaseAPI;
    @Autowired
    private IKolTagAttributeRelationService tagAttributeRelationService;
    @Autowired
    private IKolProductService productService;
    @Autowired
    private IKolTagProductService tagProductService;
    @Autowired
    private IKolAttributeService attributeService;
    @Autowired
    private IKolAttributeTypeService attributeTypeService;

    /**
     * 分页列表查询
     *
     * @param kolTagsModel
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "tag表-" + PAGE_LIST_QUERY)
    @Operation(summary = "tag表-" + PAGE_LIST_QUERY, description = "tag表-" + PAGE_LIST_QUERY)
    @GetMapping(value = "/list")
    public Result<?> queryPageList(KolTagsModel kolTagsModel, @RequestParam(name = PAGE_NO, defaultValue = PAGE_NO_DEFAULT) Integer pageNo, @RequestParam(name = PAGE_SIZE, defaultValue = PAGE_SIZE_DEFAULT) Integer pageSize, HttpServletRequest req) {
        if (oConvertUtils.isNotEmpty(kolTagsModel.getColumn())) {
            kolTagsModel.setColumn(oConvertUtils.camelToUnderline(kolTagsModel.getColumn()));
        }
        // 增加获取类目查询条件
        if (oConvertUtils.isNotEmpty(kolTagsModel.getCategoryId())) {
            List<String> categoryIds = kolTagsService.getAllCategoryIds(kolTagsModel.getCategoryId());
            if (!categoryIds.isEmpty()) {
                kolTagsModel.setCategoryIdList(categoryIds);
            }
        }
        Page<KolTags> page = new Page<>(pageNo, pageSize);
        IPage<KolTags> pageList = kolTagsService.queryPageList(page, kolTagsModel);
        return Result.ok(pageList);
    }

    @GetMapping(value = "/listNew")
    public Result<?> queryPageListNew(KolTagsModel kolTagsModel, @RequestParam(name = PAGE_NO, defaultValue = PAGE_NO_DEFAULT) Integer pageNo, @RequestParam(name = PAGE_SIZE, defaultValue = PAGE_SIZE_DEFAULT) Integer pageSize, HttpServletRequest req) {
        if (oConvertUtils.isNotEmpty(kolTagsModel.getColumn())) {
            kolTagsModel.setColumn(oConvertUtils.camelToUnderline(kolTagsModel.getColumn()));
        }
        // 增加获取类目查询条件
        if (oConvertUtils.isNotEmpty(kolTagsModel.getCategoryId())) {
            List<String> categoryIds = kolTagsService.getAllCategoryIds(kolTagsModel.getCategoryId());
            if (!categoryIds.isEmpty()) {
                kolTagsModel.setCategoryIdList(categoryIds);
            }
        }
        Page<KolTagsModel> page = new Page<>(pageNo, pageSize);

        page.setSearchCount(false);
        Integer totalCount = 0;
        IPage<KolTagsModel> pageList = new Page<>();
        try {
            // 异步执行第一个查询
            CompletableFuture<Integer> countFuture = CompletableFuture.supplyAsync(() -> kolTagsService.countBySearch(kolTagsModel));

            // 异步执行第二个查询
            CompletableFuture<IPage<KolTagsModel>> pageListFuture = CompletableFuture.supplyAsync(() -> kolTagsService.queryPageListNew(page, kolTagsModel));

            // 等待两个查询都完成
            CompletableFuture.allOf(countFuture, pageListFuture).join();

            // 获取结果
            totalCount = countFuture.get();
            pageList = pageListFuture.get();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        if(pageList == null){
            return Result.ok(new Page<>());
        }
        List<KolTagsModel> records = pageList.getRecords();
        if (records != null && !records.isEmpty()) {
            processRecordsData(records,kolTagsModel);
        }
        pageList.setTotal(totalCount);
        return Result.ok(pageList);
    }

    private void processRecordsData(List<KolTagsModel> records,KolTagsModel kolTagsModel) {
        List<String> tagIds = records.stream().map(KolTagsModel::getId).collect(Collectors.toList());
        // 查询标签id对应的类型
        List<KolTagAttributeRelation> tagAttributeDetails = tagAttributeRelationService.getTagAttributeDetails(tagIds);
        // 查询标签id对应的产品
        List<KolTagProduct> tagProductDetails = tagProductService.getTagProductDetails(tagIds);
        Map<String, List<KolTagProduct>> tagProductDetailMap = tagProductDetails.stream().collect(Collectors.groupingBy(KolTagProduct::getTagId));
        Map<String, List<KolTagAttributeRelation>> map = tagAttributeDetails.stream().collect(Collectors.groupingBy(KolTagAttributeRelation::getTagId));

        records.parallelStream().forEach(model -> {
            if (map.containsKey(model.getId())) {
                setAttributeData(model, map.get(model.getId()),kolTagsModel.getProductId());
            }
            if (tagProductDetailMap.containsKey(model.getId())) {
                setProductData(model, tagProductDetailMap.get(model.getId()));
            }
        });
    }

    private void setProductData(KolTagsModel model, List<KolTagProduct> kolTagProducts) {
        if (kolTagProducts != null && !kolTagProducts.isEmpty()) {
            List<JSONObject> list = new ArrayList<>();
            kolTagProducts.forEach(product -> {
                JSONObject productJson = new JSONObject();
                productJson.put("productId", product.getProductId());
                productJson.put("productName", product.getProductName());
                productJson.put("brandId", product.getBrandId());
                productJson.put("brandName", product.getBrandName());
                list.add(productJson);
            });
            model.setProductData(JSONArray.toJSONString(list));
        }
    }

    private void setAttributeData(KolTagsModel model, List<KolTagAttributeRelation> kolTagAttributeRelations,String productId) {
        List<TypeData> typeDataList = new ArrayList<>();
        Map<String, List<KolTagAttributeRelation>> listMap = kolTagAttributeRelations.stream().collect(Collectors.groupingBy(KolTagAttributeRelation::getAttributeTypeId));
        listMap.forEach((key, relations) -> {
            TypeData typeData = new TypeData();
            typeData.setTypeId(key);
            if (oConvertUtils.isNotEmpty(productId)) {
                relations = relations.stream().filter(relation -> oConvertUtils.isNotEmpty(relation.getProductId()) && relation.getProductId().equals(productId)).collect(Collectors.toList());
            }
            Set<TypeData.CategoryItem> categoryItems = new HashSet<>();
            relations.forEach(relation -> {
                TypeData.CategoryItem categoryItem = new TypeData.CategoryItem();
                categoryItem.setLevel(relation.getLevel());
                categoryItem.setIsSel(true);
                categoryItem.setAttributeId(relation.getAttributeId());
                categoryItem.setAttributeName(relation.getAttributeName());
                categoryItems.add(categoryItem);
            });
            typeData.setList(new ArrayList<>(categoryItems));
            typeDataList.add(typeData);
        });
        model.setDataList(typeDataList);
    }


    /**
     * 添加
     *
     * @param kolTags
     * @return
     */
    @AutoLog(value = "tag表-" + ADD)
    @Operation(summary = "tag表-" + ADD, description = "tag表-" + ADD)
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody KolTags kolTags) {
        return getResult(kolTags);
    }

    @AutoLog(value = "tag表-" + ADD)
    @Operation(summary = "tag表-" + ADD, description = "tag表-" + ADD)
    @PostMapping(value = "/addNew")
    public Result<?> addNew(@RequestBody KolTagsModel kolTagsModel) {
        kolTagsModel.setCreateBy(getUserNameByToken());
        kolTagsModel.setUpdateBy(getUserNameByToken());
        return getResultNew(kolTagsModel);
    }

    /**
     * 添加
     *
     * @param kolTags
     * @return
     */
    @AutoLog(value = "tag表-" + ADD)
    @Operation(summary = "tag表-" + ADD, description = "tag表-" + ADD)
    @PostMapping(value = "/addStoreTags")
    public Result<?> addStoreTags(@RequestBody KolTags kolTags) {
        return getResult(kolTags);
    }


    @AutoLog(value = "tag表-" + ADD)
    @Operation(summary = "tag表-" + ADD, description = "tag表-" + ADD)
    @PostMapping(value = "/addStoreTagsNew")
    public Result<?> addStoreTagsNew(@RequestBody KolTagsModel kolTagsModel) {
        return getResultNew(kolTagsModel);
    }

    /**
     * 方法描述: 新增标签和品牌
     *
     * @author nqr
     * @date 2025/01/13 17:41
     **/
    private Result<Object> getResult(KolTags kolTags) {
        Integer platformType = kolTags.getPlatformType();
        String tagName = StringUtils.lowerCase(kolTags.getTagName().trim());
        kolTags.setTagName(tagName);
        String brandIds = kolTags.getBrandIds();
        if (oConvertUtils.isEmpty(platformType) || oConvertUtils.isEmpty(tagName)) {
            return Result.error("参数不能为空");
        }
        Integer tagType = kolTags.getTagType();
        if (tagName.contains(" ") && tagType.equals(HASHTAG.getCode())) {
            return Result.error("标签名称不能包含空格");
        }
        List<DictModel> importTagPlatform = sysBaseAPI.queryDictItemsByCode("import_tag_platform");
        List<DictModel> platformTypeList = sysBaseAPI.queryDictItemsByCode("platform_type");
        List<DictModel> tagTypeList = sysBaseAPI.queryDictItemsByCode("tag_type");

        DictModel dictModel = importTagPlatform.stream().filter(x -> x.getText().equals(platformType.toString())).findFirst().get();
        if (!dictModel.getValue().contains(String.valueOf(tagType))) {
            String platformTypeText = platformTypeList.stream().filter(x -> x.getValue().equals(platformType.toString())).findFirst().get().getText();
            String tagTypeText = tagTypeList.stream().filter(x -> x.getValue().equals(tagType.toString())).findFirst().get().getText();
            return Result.error(String.format("%s平台不支持的%s类型", platformTypeText, tagTypeText));
        }
        String id = kolTags.getId();
        if (oConvertUtils.isEmpty(id)) {
            // 新增
            id = IdWorker.get32UUID();
            kolTags.setId(id);
            kolTags.setFinalStatus(0);
            kolTags.setImportTime(new Date());
        }
        kolTags.setIsActive(1);
        // 设置主标签
        setStoreTags(kolTags);

        List<KolTagBrand> kolTagBrandsSave = new ArrayList<>();
        List<KolTagBrand> kolTagBrandsRemove = kolTagsBrandService.lambdaQuery().eq(KolTagBrand::getTagId, id).eq(KolTagBrand::getPlatformType, platformType).list();

        List<String> brandIdsList = Arrays.asList(brandIds.split(","));
        if (oConvertUtils.isNotEmpty(brandIds) && !brandIdsList.isEmpty()) {
            // 查询品牌
            List<KolBrand> kolBrands = kolBrandService.lambdaQuery().in(KolBrand::getId, brandIdsList).list();
            if (kolBrands.isEmpty()) {
                return Result.error("品牌不存在或标签已绑定该品牌");
            }
            for (KolBrand kolBrand : kolBrands) {
                // 构造标签品牌关联关系
                KolTagBrand tagBrandNew = new KolTagBrand();
                tagBrandNew.setId(IdWorker.get32UUID());
                tagBrandNew.setTagId(id);
                tagBrandNew.setTagName(tagName);
                tagBrandNew.setBrandId(kolBrand.getId());
                tagBrandNew.setBrandName(kolBrand.getBrandName());
                tagBrandNew.setPlatformType(platformType);
                if (oConvertUtils.isNotEmpty(kolTags.getStoreTags())) {
                    tagBrandNew.setStoreTags(kolTags.getStoreTags());
                }
                kolTagBrandsSave.add(tagBrandNew);
            }
        }
        kolTagsService.saveKolTags(kolTags, kolTagBrandsSave, kolTagBrandsRemove.stream().map(KolTagBrand::getId).collect(Collectors.toList()));
        return Result.ok(EDIT_SUCCESS);
    }

    /**
     * @description:新增标签，增加场景受众等关联
     * @author: nqr
     * @date: 2025/6/28 10:35
     **/
    private Result<?> getResultNew(KolTagsModel kolTagsModel) {
        Integer platformType = kolTagsModel.getPlatformType();
        String tagName = StringUtils.lowerCase(kolTagsModel.getTagName().trim());
        kolTagsModel.setTagName(tagName);
        String productIds = kolTagsModel.getProductIds();
       /* if (oConvertUtils.isEmpty(productIds)) {
            return Result.error("关联产品不能为空");
        }*/

        if (oConvertUtils.isEmpty(platformType) || oConvertUtils.isEmpty(tagName)) {
            return Result.error("参数不能为空");
        }

        Integer tagType = kolTagsModel.getTagType();
        if (tagName.contains(" ") && tagType.equals(HASHTAG.getCode())) {
            return Result.error("标签名称不能包含空格");
        }

        List<TypeData> dataList = kolTagsModel.getDataList();
        // 基础参数校验
        Result<?> validationResult = validateBasicParams(kolTagsModel, dataList);
        if (!validationResult.isSuccess()) {
            return validationResult;
        }

        List<DictModel> importTagPlatform = sysBaseAPI.queryDictItemsByCode("import_tag_platform");
        List<DictModel> platformTypeList = sysBaseAPI.queryDictItemsByCode("platform_type");
        List<DictModel> tagTypeList = sysBaseAPI.queryDictItemsByCode("tag_type");

        DictModel dictModel = importTagPlatform.stream().filter(x -> x.getText().equals(platformType.toString())).findFirst().get();
        if (!dictModel.getValue().contains(String.valueOf(tagType))) {
            String platformTypeText = platformTypeList.stream().filter(x -> x.getValue().equals(platformType.toString())).findFirst().get().getText();
            String tagTypeText = tagTypeList.stream().filter(x -> x.getValue().equals(tagType.toString())).findFirst().get().getText();
            return Result.error(String.format("%s平台不支持的%s类型", platformTypeText, tagTypeText));
        }
        String id = kolTagsModel.getId();
        if (oConvertUtils.isEmpty(id)) {
            // 查询标签是否存在
            Integer count = Math.toIntExact(kolTagsService.lambdaQuery().eq(KolTags::getTagName, tagName).eq(KolTags::getPlatformType, platformType).eq(KolTags::getTagType, tagType).count());
            if (count > 0) {
                return Result.error("标签已存在");
            }
            // 新增
            id = IdWorker.get32UUID();
            kolTagsModel.setId(id);
            kolTagsModel.setFinalStatus(0);
            kolTagsModel.setImportTime(new Date());
        }
        kolTagsModel.setIsActive(1);
        List<KolTagAttributeRelation> tagAttributeRelations = new ArrayList<>();
        if (!dataList.isEmpty()) {
            // 创建产品类目关联关系
            tagAttributeRelations = tagAttributeRelationService.createCategoryRelation(kolTagsModel, dataList);
        }

        List<KolTagProduct> tagProductList = new ArrayList<>();
        if (oConvertUtils.isNotEmpty(productIds)) {
            // 按照品牌产品名称查询对应的产品明细
            List<KolProduct> productList = (List<KolProduct>) productService.listByIds(Arrays.asList(productIds.split(",")));
            // 设置产品和标签关联关系
            tagProductList = createTagProduct(kolTagsModel, productList);
        }

        // 增加类目关联
        kolTagsService.saveKolTagsNew(kolTagsModel, tagProductList, tagAttributeRelations);
        return Result.ok("操作成功");
    }

    /**
     * @description: 创建标签和产品的关联关系
     * @author: nqr
     * @date: 2025/6/28 12:16
     **/
    private List<KolTagProduct> createTagProduct(KolTagsModel kolTagsModel, List<KolProduct> productList) {
        List<KolTagProduct> tagProductList = new ArrayList<>();
        for (KolProduct product : productList) {
            KolTagProduct tagProduct = new KolTagProduct();
            tagProduct.setId(IdWorker.get32UUID());
            tagProduct.setTagId(kolTagsModel.getId());
            tagProduct.setTagName(kolTagsModel.getTagName());
            tagProduct.setBrandId(product.getBrandId());
            tagProduct.setBrandName(product.getBrandName());
            tagProduct.setProductId(product.getId());
            tagProduct.setProductName(product.getProductName());
            tagProduct.setPlatformType(kolTagsModel.getPlatformType());
            tagProduct.setCreateBy(getUserNameByToken());
            tagProduct.setCreateTime(new Date());
            tagProduct.setUpdateBy(getUserNameByToken());
            tagProduct.setUpdateTime(new Date());
            tagProduct.setIsDelete(0);
            tagProductList.add(tagProduct);
        }
        return tagProductList;
    }

    /**
     * 基础参数校验
     */
    private Result<?> validateBasicParams(KolTagsModel kolTagsModel, List<TypeData> dataList) {
        Integer platformType = kolTagsModel.getPlatformType();
        String tagName = kolTagsModel.getTagName();
        Integer tagType = kolTagsModel.getTagType();

        if (oConvertUtils.isEmpty(platformType) || oConvertUtils.isEmpty(tagName) || oConvertUtils.isEmpty(tagType)) {
            return Result.error("平台类型、标签名称、标签类型不能为空！");
        }
        /*if (oConvertUtils.isEmpty(dataList)) {
            return Result.error("关联数据不能为空！");
        }
        // 检查是否存在空的关联数据
        boolean hasEmptyList = dataList.stream().anyMatch(typeData -> typeData.getList().isEmpty());
        if (hasEmptyList) {
            return Result.error("关联数据不能为空！");
        }*/
        return Result.ok();
    }

    /**
     * 方法描述: 根据类目，设置主类目信息
     *
     * @author nqr
     * @date 2025/01/13 17:44
     **/
    private void setStoreTags(KolTags kolTags) {
        // 查询主类目
        List<KolTagCategory> categoryList = tagCategoryService.list();
        categoryList.stream().filter(x -> x.getId().equals(kolTags.getCategoryId())).findFirst().ifPresent(tagCategory -> {
            kolTags.setCategoryName(tagCategory.getCategoryName());
            categoryList.stream().filter(x -> x.getId().equals(tagCategory.getParentId())).findFirst().ifPresent(x -> kolTags.setStoreTags(x.getCategoryName()));
        });
    }

    /**
     * 编辑
     *
     * @param kolTags
     * @return
     */
    @AutoLog(value = "tag表-" + EDIT)
    @Operation(summary = "tag表-" + EDIT, description = "tag表-" + EDIT)
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody KolTags kolTags) {
        kolTags.setUpdateBy(getUserNameByToken());
        LambdaQueryWrapper<KolTags> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(KolTags::getId, kolTags.getId());
        queryWrapper.eq(KolTags::getTagName, kolTags.getTagName());
        queryWrapper.eq(KolTags::getTagType, kolTags.getTagType());
        queryWrapper.eq(KolTags::getPlatformType, kolTags.getPlatformType());
        List<KolTags> list = kolTagsService.list(queryWrapper);
        if (!list.isEmpty()) {
            return Result.error("标签已存在");
        }

        return getResult(kolTags);
    }

    @AutoLog(value = "tag表-" + EDIT)
    @Operation(summary = "tag表-" + EDIT, description = "tag表-" + EDIT)
    @PutMapping(value = "/editNew")
    public Result<?> editNew(@RequestBody KolTagsModel kolTagsModel) {
        kolTagsModel.setUpdateBy(getUserNameByToken());
        LambdaQueryWrapper<KolTags> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(KolTags::getId, kolTagsModel.getId());
        queryWrapper.eq(KolTags::getTagName, kolTagsModel.getTagName());
        queryWrapper.eq(KolTags::getTagType, kolTagsModel.getTagType());
        queryWrapper.eq(KolTags::getPlatformType, kolTagsModel.getPlatformType());
        List<KolTags> list = kolTagsService.list(queryWrapper);
        if (!list.isEmpty()) {
            return Result.error("标签已存在");
        }
        return getResultNew(kolTagsModel);
    }

    /**
     * 功能描述:重置状态
     *
     * @return org.jeecg.common.api.vo.Result<?>
     * @Date 2023-11-02 14:05:54
     */
    @PutMapping(value = "/editStatus")
    public Result<?> editStatus(@RequestBody KolTags kolTags) {
        kolTagsService.lambdaUpdate().eq(KolTags::getId, kolTags.getId()).set(KolTags::getTagStatus, 0).set(KolTags::getAppStatus, 0).set(KolTags::getFinalStatus, 0).update();
        return Result.ok("重置成功！");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "tag表-" + DELETE_BY_ID)
    @Operation(summary = "tag表-" + DELETE_BY_ID, description = "tag表-" + DELETE_BY_ID)
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        kolTagsService.removeById(id);
        kolTagsBrandService.removeByTagsId(id);
        return Result.ok(DELETE_SUCCESS);
    }

    @DeleteMapping(value = "/deleteNew")
    public Result<?> deleteNew(@RequestParam(name = "id", required = true) String id) {
        kolTagsService.removeById(id);
        kolTagsBrandService.removeByTagsId(id);
        tagProductService.lambdaUpdate().eq(KolTagProduct::getTagId, id).remove();
        tagAttributeRelationService.lambdaUpdate().eq(KolTagAttributeRelation::getTagId, id).remove();
        return Result.ok(DELETE_SUCCESS);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "tag表-" + DELETE_BATCH)
    @Operation(summary = "tag表-" + DELETE_BATCH, description = "tag表-" + DELETE_BATCH)
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.kolTagsService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok(DELETE_BATCH_SUCCESS);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "tag表-" + QUERY_BY_ID)
    @Operation(summary = "tag表-" + QUERY_BY_ID, description = "tag表-" + QUERY_BY_ID)
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        KolTags kolTags = kolTagsService.getById(id);
        return Result.ok(kolTags);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param kolTags
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, KolTags kolTags) {
        return super.exportXls(request, kolTags, KolTags.class, "tag表");
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
        Integer platformType = Integer.valueOf(multipartRequest.getParameter("platformType"));
        String productId = multipartRequest.getParameter("productId");

        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();

        if (oConvertUtils.isEmpty(platformType)) {
            return Result.error("平台类型不能为空");
        }

        for (MultipartFile file : fileMap.values()) {
            try {
                // 导入Excel
                ImportParams params = new ImportParams();
                params.setHeadRows(1);
                params.setNeedSave(true);
                List<KolTagsModel> kolTagsList = ExcelImportUtil.importExcel(file.getInputStream(), KolTagsModel.class, params);

                return kolTagsService.importTags(kolTagsList, platformType, productId, 1);

            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败，请检查文件内容是否正确");
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    log.error("关闭文件流失败", e);
                }
            }
        }
        return Result.error("文件导入失败！");
    }

    /**
     * 下载模板
     */
    @RequestMapping(value = "/downloadXls", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadXls(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("Sheet");

        String[] header = {"标签", "标签类型", "视频链接", "达人类型1", "达人类型2", "达人类型3", "达人类型4", "达人类型5", "强制更新"};
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
        // 查询社媒类型
        List<KolAttributeType> attributeTypeList = attributeTypeService.list();

        // 查询社媒属性,只查询父类目
        List<KolAttribute> attributeList = attributeService.lambdaQuery().eq(KolAttribute::getParentId, "0").list();
        Map<String, List<KolAttribute>> map = attributeList.stream().collect(Collectors.groupingBy(KolAttribute::getAttributeTypeId));
        map.forEach((key, value) -> {
            attributeTypeList.stream().filter(x -> x.getId().equals(key)).findFirst().ifPresent(attributeType -> {
                String[] tagsDropdown = value.stream().map(KolAttribute::getAttributeName).toArray(String[]::new);
               /* // 物理空间
                if (attributeType.getTypeCode().equals(CategoryType.AUDIENCE_CODE)) {
                    createDropdownList(sheet, tagsDropdown, 3);
                    createDropdownList(sheet, tagsDropdown, 4);
                    createDropdownList(sheet, tagsDropdown, 5);
                }
                // 人群类型
                if (attributeType.getTypeCode().equals(CategoryType.SCENE_CODE)) {
                    createDropdownList(sheet, tagsDropdown, 6);
                    createDropdownList(sheet, tagsDropdown, 7);
                    createDropdownList(sheet, tagsDropdown, 8);
                }*/
                // 达人类型
                if (attributeType.getTypeCode().equals(CategoryType.INFLUENCER_TYPE_CODE)) {
                    createDropdownList(sheet, tagsDropdown, 3);
                    createDropdownList(sheet, tagsDropdown, 4);
                    createDropdownList(sheet, tagsDropdown, 5);
                    createDropdownList(sheet, tagsDropdown, 6);
                    createDropdownList(sheet, tagsDropdown, 7);
                }
            });
        });
        // 查询标签类型
        // DictModel dictModel = importTagPlatform.stream().filter(x -> x.getText().equals(platformType.toString())).findFirst().get();
        // String[] tagTypeDropdown = dictModel.getDescription().split(",");

        List<DictModel> tagTypeList = sysBaseAPI.queryDictItemsByCode("tag_type");
        String[] tagTypeDropdown = tagTypeList.stream().map(DictModel::getText).toArray(String[]::new);
        createDropdownList(sheet, tagTypeDropdown, 1);

        // 设置是否更新标签状态
        String[] updateTagType = {"否", "是"};
        createDropdownList(sheet, updateTagType, 8);
    }

    /**
     * 查询tag标签表
     *
     * @param
     * @return
     */
    @AutoLog(value = "tag表-" + QUERY_BY_ID)
    @Operation(summary = "tag表-" + QUERY_BY_ID, description = "tag表-" + QUERY_BY_ID)
    @GetMapping(value = "/queryTagsList")
    public Result<?> queryTagsList(@RequestParam("platformType") Integer platformType) {
        LambdaQueryWrapper<KolTags> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(KolTags::getTagStatus, 99);
        queryWrapper.eq(KolTags::getPlatformType, platformType);
        queryWrapper.orderByAsc(KolTags::getSortCode);
        List<KolTags> tagsList = kolTagsService.list(queryWrapper);
        return Result.ok(tagsList);
    }

    /**
     * 根据Tag标签名称获取
     *
     * @param
     * @return
     */
    @AutoLog(value = "tag表-" + QUERY_BY_ID)
    @Operation(summary = "tag表-" + QUERY_BY_ID, description = "tag表-" + QUERY_BY_ID)
    @PostMapping(value = "/queryTagsListByName")
    public Result<?> queryTagsListByName(@RequestBody KolTagsModel kolTagsModel) {
        // kolTagsModel.setTagStatus(CommonConstant.TAG_STATUS_FINISH);
        kolTagsModel.setIsDelete(YesNoStatus.NO.getCode());
        if (!oConvertUtils.isEmpty(kolTagsModel.getTagNames())) {
            String cleanedTagNames = Arrays.stream(kolTagsModel.getTagNames().split("\\|")).map(String::trim).collect(Collectors.joining("|"));
            kolTagsModel.setTagNames(cleanedTagNames);
        }
        List<KolTags> tagsList = kolTagsService.queryTagsListByName(kolTagsModel);
        List<Map<String, String>> list = new ArrayList<>();
        tagsList.forEach(tag -> {
            Map<String, String> map = new HashMap<>();
            map.put("tagName", tag.getTagName());
            list.add(map);
        });
        return Result.ok(list);
    }

    /**
     * 关联标签
     *
     * @param
     * @return
     */
    @AutoLog(value = "tag表-关联标签")
    @Operation(summary = "tag表-关联标签", description = "tag表-关联标签")
    @GetMapping(value = "/associatedTags")
    public Result<?> associatedTags(KolTagsModel tiktokTagsModel) {
        String tagNames = tiktokTagsModel.getTagNames();
        if (oConvertUtils.isEmpty(tagNames)) {
            return Result.error("请输入子标签");
        }
        String storeTagsNew = tiktokTagsModel.getStoreTags();
        if (oConvertUtils.isEmpty(storeTagsNew)) {
            return Result.error("请选择主标签");
        }
        List<String> list = Arrays.asList(tagNames.split(","));
        // 查询标签对应的数据
        List<KolTags> tagsList = kolTagsService.getByTagNames(list, tiktokTagsModel.getPlatformType());
        List<KolTags> tagsListNew = new ArrayList<>();
        for (KolTags kolTags : tagsList) {
            List<String> storeTagsList = new ArrayList<>();
            String storeTags = kolTags.getStoreTags();
            if (oConvertUtils.isNotEmpty(storeTags)) {
                storeTagsList = JSONArray.parseArray(storeTags, String.class);
            }
            if (!storeTagsList.contains(storeTagsNew)) {
                KolTags kolTagsNew = new KolTags();
                kolTagsNew.setId(kolTags.getId());
                storeTagsList.add(storeTagsNew);
                kolTagsNew.setStoreTags(JSONArray.toJSONString(storeTagsList));
                tagsListNew.add(kolTagsNew);
            }
        }
        if (!tagsListNew.isEmpty()) {
            kolTagsService.updateBatchById(tagsListNew);
        }
        return Result.ok("关联成功");
    }

    @AutoLog(value = "tag表-查询受众、类目、适合产品")
    @GetMapping(value = "/queryTagsListByStoreTags")
    public Result<?> queryTagsListByStoreTags(KolTags kolTags) {
        String subCategory = kolTags.getSubCategory();
        String tagProduct = kolTags.getTagProduct();
        String tagAudience = kolTags.getTagAudience();
        LambdaQueryWrapper<KolTags> queryWrapper = new LambdaQueryWrapper<>();
        List<String> resultList = new ArrayList<>();
        if (oConvertUtils.isNotEmpty(tagProduct)) {
            queryWrapper.select(KolTags::getTagProduct);
            queryWrapper.like(KolTags::getTagProduct, tagProduct);
            queryWrapper.groupBy(KolTags::getTagProduct);
            List<KolTags> list = kolTagsService.list(queryWrapper);
            resultList = list.stream().map(KolTags::getTagProduct).collect(Collectors.toList());
        }
        if (oConvertUtils.isNotEmpty(subCategory)) {
            queryWrapper.select(KolTags::getSubCategory);
            queryWrapper.like(KolTags::getSubCategory, subCategory);
            queryWrapper.groupBy(KolTags::getSubCategory);
            List<KolTags> list = kolTagsService.list(queryWrapper);
            resultList = list.stream().map(KolTags::getSubCategory).collect(Collectors.toList());
        }
        if (oConvertUtils.isNotEmpty(tagAudience)) {
            queryWrapper.select(KolTags::getTagAudience);
            queryWrapper.like(KolTags::getTagAudience, tagAudience);
            queryWrapper.groupBy(KolTags::getTagAudience);
            List<KolTags> list = kolTagsService.list(queryWrapper);
            resultList = list.stream().map(KolTags::getTagAudience).collect(Collectors.toList());
        }
        return Result.ok(resultList);
    }


    /**
     * 获取未分配的标签及数量汇总
     *
     * @param searchModel
     * @return
     */
    @Operation(summary = "TK未分配的标签及数量汇总查询", description = "TK未分配的标签及数量汇总查询")
    @PostMapping(value = "/getUnAllottedTagCount")
    public Result<?> getUnAllottedTagCount(@RequestBody KolSearchModel searchModel,
                                           @RequestParam(name = PAGE_NO, defaultValue = PAGE_NO_DEFAULT) Integer pageNo,
                                           @RequestParam(name = PAGE_SIZE, defaultValue = PAGE_SIZE_DEFAULT) Integer pageSize,
                                           HttpServletRequest req) {
        Integer platformType = searchModel.getPlatformType();
        if (oConvertUtils.isEmpty(platformType)) {
            return Result.error("平台类型不能为空");
        }
        // 根据产品进行分配,查询产品对应的标签
        if (oConvertUtils.isNotEmpty(searchModel.getProductId())) {
            List<String> tagNames = tagProductService.getByProductId(searchModel.getProductId(), platformType);
            if (!tagNames.isEmpty()) {
                searchModel.getTagNameList().addAll(tagNames);
            }
        }
        Page<KolTagCountModel> page = PageUtil.getOrderItems(pageNo, pageSize, req);
        IPage<KolTagCountModel> pageList = kolTagsService.getUnAllottedTagCount(page, searchModel);
        return Result.ok(pageList);
    }

    /**
     * 通过id查询
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "tag表-" + QUERY_BY_ID)
    @Operation(summary = "tag表-" + QUERY_BY_ID, description = "tag表-" + QUERY_BY_ID)
    @PostMapping(value = "/queryByIds")
    public Result<?> queryByIds(@RequestBody List<String> ids) {
        List<KolTags> kolTagsList = (List<KolTags>) kolTagsService.listByIds(ids);
        return Result.ok(kolTagsList.stream().map(KolTags::getTagName).distinct().collect(Collectors.joining("|")));
    }
}
