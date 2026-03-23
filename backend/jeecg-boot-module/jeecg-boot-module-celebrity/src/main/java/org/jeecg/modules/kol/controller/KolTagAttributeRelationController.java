package org.jeecg.modules.kol.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.SystemConstants;
import org.jeecg.common.constant.enums.StoreTaskStatusEnum;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.kol.entity.KolTagCategory;
import org.jeecg.modules.kol.entity.KolTagAttributeRelation;
import org.jeecg.modules.kol.entity.KolTags;
import org.jeecg.modules.kol.model.KolTagAttributeRelationModel;
import org.jeecg.modules.kol.model.TagCategoryRelationExcelModel;
import org.jeecg.modules.kol.model.TypeData;
import org.jeecg.modules.kol.service.IKolAttributeTypeService;
import org.jeecg.modules.kol.service.IKolTagAttributeRelationService;
import org.jeecg.modules.kol.service.IKolTagCategoryService;
import org.jeecg.modules.kol.service.IKolTagsService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.BeanUtils;
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
import java.util.stream.Collectors;

import static org.jeecg.common.constant.enums.TagType.ROOT_VIDEO;

/**
 * @Description: 标签与类目关联表
 * @Author: nqr
 * @Date: 2025-05-21
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "标签与类目关联表")
@RestController
@RequestMapping("/kol/kolTagCategoryRelation")
public class KolTagAttributeRelationController extends JeecgController<KolTagAttributeRelation, IKolTagAttributeRelationService> {
    @Autowired
    private IKolTagAttributeRelationService tagCategoryRelationService;
    @Autowired
    private IKolTagsService kolTagsService;
    @Autowired
    private IKolTagCategoryService tagCategoryService;
    @Autowired
    private IKolAttributeTypeService categoryTypeService;
    @Autowired
    private ISysBaseAPI sysBaseAPI;

    /**
     * 分页列表查询
     *
     * @param kolTagAttributeRelation
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "标签与类目关联表-" + SystemConstants.PAGE_LIST_QUERY)
    @Operation(summary = "标签与类目关联表-" + SystemConstants.PAGE_LIST_QUERY, description = "标签与类目关联表-" + SystemConstants.PAGE_LIST_QUERY)
    @GetMapping(value = "/list")
    public Result<?> queryPageList(KolTagAttributeRelation kolTagAttributeRelation, @RequestParam(name = SystemConstants.PAGE_NO, defaultValue = SystemConstants.PAGE_NO_DEFAULT) Integer pageNo, @RequestParam(name = SystemConstants.PAGE_SIZE, defaultValue = SystemConstants.PAGE_SIZE_DEFAULT) Integer pageSize, HttpServletRequest req) {
        LambdaQueryWrapper<KolTagAttributeRelation> queryWrapper = QueryGenerator.initQueryWrapper(kolTagAttributeRelation, req.getParameterMap()).lambda();
        Page<KolTagAttributeRelation> page = new Page<>(pageNo, pageSize);
        IPage<KolTagAttributeRelation> pageList = tagCategoryRelationService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param kolTagAttributeRelationModel
     * @return
     */
    @AutoLog(value = "标签与类目关联表-" + SystemConstants.ADD)
    @Operation(summary = "标签与类目关联表-" + SystemConstants.ADD, description = "标签与类目关联表-" + SystemConstants.ADD)
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody KolTagAttributeRelationModel kolTagAttributeRelationModel) {
        String categoryIds = kolTagAttributeRelationModel.getCategoryIds();
        if (oConvertUtils.isEmpty(categoryIds)) {
            return Result.error("类目不能为空");
        }
        String tagId = IdWorker.get32UUID();
        String tagName = kolTagAttributeRelationModel.getTagName().trim();
        Integer platformType = kolTagAttributeRelationModel.getPlatformType();
        Integer tagType = kolTagAttributeRelationModel.getTagType();
        int isRecover = kolTagAttributeRelationModel.getIsRecover();
        // 校验字段
        Result<Object> result = checkColumns(kolTagAttributeRelationModel);
        if (result != null && result.getCode() == 500) return result;

        KolTags kolTags = kolTagsService.lambdaQuery().eq(KolTags::getTagName, tagName).eq(KolTags::getPlatformType, platformType).eq(KolTags::getTagType, tagType).last("LIMIT 1").one();
        // 标签存在，删除原类目对照关系，重新创建类目对照关系
        boolean categoryRelationExists = false;
        if (kolTags != null) {
            // 判断是否删除
            if (kolTags.getIsDelete() == 1) {
                if (isRecover == 0) {
                    return Result.error(10001, "标签已删除，是否恢复已删除的标签及其关联网红？");
                }
                // 恢复标签状态
                kolTags.setIsDelete(0);
                kolTags.setUpdateBy(getUserNameByToken());
                kolTags.setUpdateTime(new Date());
                kolTagsService.updateById(kolTags);
            }
            tagId = kolTags.getId();
            categoryRelationExists = true;
        }
        kolTagAttributeRelationModel.setTagId(tagId);
        // 创建标签
        KolTags kolTagsNew = createKolTags(kolTagAttributeRelationModel);

        List<KolTagAttributeRelation> categoryRelationListSave = createTagCategoryRelation(kolTagAttributeRelationModel, categoryIds);

        // 查询标签是否存在
        tagCategoryRelationService.saveData(kolTagsNew, categoryRelationListSave, categoryRelationExists);
        return Result.ok(SystemConstants.ADD_SUCCESS);
    }

    private Result<Object> checkColumns(KolTagAttributeRelationModel kolTagAttributeRelationModel) {
        String tagName = kolTagAttributeRelationModel.getTagName().trim();
        Integer platformType = kolTagAttributeRelationModel.getPlatformType();
        if (tagName.contains(" ")) {
            return Result.error("标签名称不能包含空格");
        }
        Integer tagType = kolTagAttributeRelationModel.getTagType();
        if (platformType == null) {
            return Result.error("平台类型不能为空");
        }
        if (tagType == null) {
            return Result.error("标签类型不能为空");
        }
        // 验证种子视频类型
        if (ROOT_VIDEO.getCode() == tagType && oConvertUtils.isEmpty(kolTagAttributeRelationModel.getVideoUrl())) {
            return Result.error("视频链接不能为空");
        }
        return null;
    }

    /**
     * @description: 创建标签
     * @author: nqr
     * @date: 2025/5/21 16:19
     **/
    private KolTags createKolTags(KolTagAttributeRelationModel kolTagAttributeRelationModel) {
        KolTags kolTagsNew = new KolTags();
        BeanUtils.copyProperties(kolTagAttributeRelationModel, kolTagsNew);
        kolTagsNew.setId(kolTagAttributeRelationModel.getTagId());
        kolTagsNew.setCategoryId(null);
        kolTagsNew.setCategoryName(null);
        kolTagsNew.setTagStatus(StoreTaskStatusEnum.NOT_START.getCode());
        kolTagsNew.setIsDelete(0);
        kolTagsNew.setCreateBy(getUserNameByToken());
        kolTagsNew.setCreateTime(new Date());
        kolTagsNew.setUpdateBy(getUserNameByToken());
        kolTagsNew.setUpdateTime(new Date());
        return kolTagsNew;
    }

    /**
     * @description: 创建标签与类目关联关系
     * @author: nqr
     * @date: 2025/5/21 16:12
     **/
    private List<KolTagAttributeRelation> createTagCategoryRelation(KolTagAttributeRelationModel kolTagAttributeRelationModel, String categoryIds) {
        List<KolTagAttributeRelation> categoryRelationListSave = new ArrayList<>();
        List<String> categoryIdList = Arrays.asList(categoryIds.split(","));
        List<KolTagCategory> kolTagCategories = (List<KolTagCategory>) tagCategoryService.listByIds(categoryIdList);
        categoryIdList.forEach(categoryId -> kolTagCategories.stream().filter(x -> x.getId().equals(categoryId)).findFirst().ifPresent(x -> {
            KolTagAttributeRelation categoryRelation = new KolTagAttributeRelation();
            BeanUtils.copyProperties(kolTagAttributeRelationModel, categoryRelation);
            categoryRelation.setId(IdWorker.get32UUID());
            categoryRelation.setAttributeName(x.getCategoryName());
            categoryRelation.setAttributeId(categoryId);
            categoryRelation.setIsDelete(0);
            categoryRelation.setCreateBy(getUserNameByToken());
            categoryRelation.setCreateTime(new Date());
            categoryRelationListSave.add(categoryRelation);
        }));
        return categoryRelationListSave;
    }

    /**
     * 编辑
     *
     * @param kolTagAttributeRelationModel
     * @return
     */
    @AutoLog(value = "标签与类目关联表-" + SystemConstants.EDIT)
    @Operation(summary = "标签与类目关联表-" + SystemConstants.EDIT, description = "标签与类目关联表-" + SystemConstants.EDIT)
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody KolTagAttributeRelationModel kolTagAttributeRelationModel) {
        String tagCategoryRelationId = kolTagAttributeRelationModel.getId();
        KolTagAttributeRelation oldKolTagAttributeRelation = tagCategoryRelationService.getById(tagCategoryRelationId);
        String categoryIds = kolTagAttributeRelationModel.getCategoryIds();
        if (oConvertUtils.isEmpty(categoryIds)) {
            return Result.error("类目不能为空");
        }
        String tagIdOld = oldKolTagAttributeRelation.getTagId();
        String tagName = kolTagAttributeRelationModel.getTagName();
        int isRecover = kolTagAttributeRelationModel.getIsRecover();
        String tagIdNew = tagIdOld;

        // 校验字段
        Result<Object> result = checkColumns(kolTagAttributeRelationModel);
        if (result != null && result.getCode() == 500) return result;

        boolean tagNameExists = false;

        // 判断名称是否修改
        KolTags kolTags = kolTagsService.lambdaQuery().eq(KolTags::getId, tagIdOld).last("LIMIT 1").one();
        if (!tagName.equals(kolTags.getTagName()) || !Objects.equals(kolTagAttributeRelationModel.getPlatformType(), kolTags.getPlatformType()) || !Objects.equals(kolTagAttributeRelationModel.getTagType(), kolTags.getTagType())) {
            // 名称修改，查询名称是否存在
            KolTags kolTagsByName = kolTagsService.lambdaQuery()
                    .eq(KolTags::getTagName, tagName)
                    .eq(KolTags::getPlatformType, kolTagAttributeRelationModel.getPlatformType())
                    .eq(KolTags::getTagType, kolTagAttributeRelationModel.getTagType())
                    .last("LIMIT 1").one();
            if (kolTagsByName != null) {
                // 名称已存在, 更新标签对应的类目为新类目
                tagIdNew = kolTagsByName.getId();
                // 判断是否删除
                if (kolTags.getIsDelete() == 1) {
                    if (isRecover == 0) {
                        return Result.error(10001, "标签已删除，是否恢复已删除的标签及其关联网红？");
                    }
                    // 恢复标签状态
                    kolTags.setIsDelete(0);
                    kolTags.setUpdateBy(getUserNameByToken());
                    kolTags.setUpdateTime(new Date());
                    kolTagsService.updateById(kolTags);
                    tagCategoryRelationService.lambdaUpdate().eq(KolTagAttributeRelation::getTagId, tagIdNew).remove();
                }
                // 删除标签
                tagNameExists = true;
            }
        }

        kolTagAttributeRelationModel.setTagId(tagIdNew);
        // 创建标签
        KolTags kolTagsNew = createKolTags(kolTagAttributeRelationModel);
        List<KolTagAttributeRelation> categoryRelationListSave = createTagCategoryRelation(kolTagAttributeRelationModel, categoryIds);

        // 查询标签是否存在
        tagCategoryRelationService.updateData(kolTagsNew, categoryRelationListSave, tagIdOld, tagNameExists);
        return Result.ok(SystemConstants.EDIT_SUCCESS);
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "标签与类目关联表-" + SystemConstants.DELETE_BY_ID)
    @Operation(summary = "标签与类目关联表-" + SystemConstants.DELETE_BY_ID, description = "标签与类目关联表-" + SystemConstants.DELETE_BY_ID)
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        KolTagAttributeRelation tagCategoryRelation = tagCategoryRelationService.getById(id);
        tagCategoryRelationService.lambdaUpdate().set(KolTagAttributeRelation::getIsDelete, 1).eq(KolTagAttributeRelation::getTagId, tagCategoryRelation.getTagId()).update();
        kolTagsService.lambdaUpdate().set(KolTags::getIsDelete, 1).eq(KolTags::getId, tagCategoryRelation.getTagId()).update();
        return Result.ok(SystemConstants.DELETE_SUCCESS);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "标签与类目关联表-" + SystemConstants.DELETE_BATCH)
    @Operation(summary = "标签与类目关联表-" + SystemConstants.DELETE_BATCH, description = "标签与类目关联表-" + SystemConstants.DELETE_BATCH)
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        String[] idArr = ids.split(",");
        for (String id : idArr) {
            KolTagAttributeRelation tagCategoryRelation = tagCategoryRelationService.getById(id);
            tagCategoryRelationService.lambdaUpdate().set(KolTagAttributeRelation::getIsDelete, 1).eq(KolTagAttributeRelation::getTagId, tagCategoryRelation.getTagId()).update();
            kolTagsService.lambdaUpdate().set(KolTags::getIsDelete, 1).eq(KolTags::getId, tagCategoryRelation.getTagId()).update();
        }
        return Result.ok(SystemConstants.DELETE_BATCH_SUCCESS);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "标签与类目关联表-" + SystemConstants.QUERY_BY_ID)
    @Operation(summary = "标签与类目关联表-" + SystemConstants.QUERY_BY_ID, description = "标签与类目关联表-" + SystemConstants.QUERY_BY_ID)
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        KolTagAttributeRelation kolTagAttributeRelation = tagCategoryRelationService.getById(id);
        return Result.ok(kolTagAttributeRelation);
    }

    /**
     * 查询标签产品对应的社媒属性
     *
     * @param kolTagAttributeRelation
     * @return
     */
    @AutoLog(value = "标签与社媒属性关联表-" + SystemConstants.QUERY_BY_TAGID_PRODUCTID)
    @Operation(summary = "标签与社媒属性关联表-" + SystemConstants.QUERY_BY_TAGID_PRODUCTID, description = "标签与社媒属性关联表-"
            + SystemConstants.QUERY_BY_TAGID_PRODUCTID)
    @GetMapping(value = "/queryByTagProduct")
    public Result<?> queryByTagProduct(KolTagAttributeRelation kolTagAttributeRelation) {
        // 参数验证：id 和 productId 必须有值
        if (oConvertUtils.isEmpty(kolTagAttributeRelation) || oConvertUtils.isEmpty(kolTagAttributeRelation.getTagId())
                || oConvertUtils.isEmpty(kolTagAttributeRelation.getProductId())) {
            return Result.error("参数验证失败：tagId 和 productId 必须有值");
        }
        List<KolTagAttributeRelation> attributeRelations = tagCategoryRelationService
                .queryAttributeByTag(kolTagAttributeRelation);
        return Result.ok(attributeRelations);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param kolTagAttributeRelation
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, KolTagAttributeRelation kolTagAttributeRelation) {
        return super.exportXls(request, kolTagAttributeRelation, KolTagAttributeRelation.class, "标签与类目关联表");
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
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                long start = System.currentTimeMillis();
                List<TagCategoryRelationExcelModel> list = ExcelImportUtil.importExcel(file.getInputStream(), TagCategoryRelationExcelModel.class, params);
                if (list.isEmpty()) {
                    return Result.error("文件导入失败！数据行数为0");
                }

                // 获取标签和类目都不为空的数据
                List<TagCategoryRelationExcelModel> listFilter = list.stream().filter(x -> oConvertUtils.isNotEmpty(x.getTagName()) && oConvertUtils.isNotEmpty(x.getCategoryName())).collect(Collectors.toList());

                // 校验数据
                Result<?> result = validateAndFilterData(listFilter);
                if (result != null && result.getCode() == 500) return result;


                // 根据类目获取类目id
                Set<String> categoryNames = listFilter.stream().map(TagCategoryRelationExcelModel::getCategoryName).collect(Collectors.toSet());
                List<KolTagCategory> categoryList = tagCategoryService.lambdaQuery().in(KolTagCategory::getCategoryName, categoryNames).list();

                // 检查类目是否存在
                Set<String> categoryNamesInDB = categoryList.stream().map(KolTagCategory::getCategoryName).collect(Collectors.toSet());
                Set<String> missingCategoryNames = categoryNames.stream().filter(name -> !categoryNamesInDB.contains(name)).collect(Collectors.toSet());

                if (!missingCategoryNames.isEmpty()) {
                    return Result.error("文件导入失败！类目不存在：" + missingCategoryNames);
                }
                List<String> errorList = new ArrayList<>();
                int successCount = 0;
                // 组装数据
                Map<String, KolTagCategory> categoryMap = categoryList.stream().collect(Collectors.toMap(KolTagCategory::getCategoryName, x -> x));
                for (TagCategoryRelationExcelModel model : listFilter) {
                    try {
                        String tagName = model.getTagName().trim().toLowerCase();
                        KolTagCategory category = categoryMap.get(model.getCategoryName());
                        model.setCategoryId(category.getId());
                        // 去除标签前后空格，判断中间是否存在空格
                        if (model.getTagName().contains(" ")) {
                            errorList.add(String.format("标签名称不能包含空格：%s", model.getTagName()));
                            continue;
                        }
                        model.setTagName(tagName);
                        String tagId = IdWorker.get32UUID();
                        model.setTagId(tagId);
                        // 判断标签是否存在
                        KolTags kolTags = kolTagsService.lambdaQuery().eq(KolTags::getTagName, tagName).eq(KolTags::getPlatformType, model.getPlatformType()).last("LIMIT 1").one();
                        KolTagAttributeRelationModel kolTagAttributeRelationModel = new KolTagAttributeRelationModel();
                        BeanUtils.copyProperties(model, kolTagAttributeRelationModel);
                        if (kolTags != null) {
                            tagId = kolTags.getId();
                            if (kolTags.getIsDelete() == 1) {
                                errorList.add(String.format("标签已删除：%s", model.getTagName()));
                                continue;
                            }
                            // 删除原有关联关系
                            tagCategoryRelationService.lambdaUpdate().eq(KolTagAttributeRelation::getTagId, kolTags.getId()).remove();
                        } else {
                            // 创建标签
                            KolTags kolTagsNew = createKolTags(kolTagAttributeRelationModel);
                            // 保存标签
                            kolTagsService.save(kolTagsNew);
                        }
                        model.setTagId(tagId);
                        // 创建标签与类目关联关系
                        List<KolTagAttributeRelation> categoryRelationListSave = createTagCategoryRelation(kolTagAttributeRelationModel, category.getId());
                        // 保存标签与类目关联关系
                        tagCategoryRelationService.saveBatch(categoryRelationListSave);
                        successCount++;
                    } catch (Exception ignored) {

                    }
                }
                log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");

                return errorList.isEmpty() ? Result.ok("文件导入成功！数据行数：" + successCount) : Result.OK(10001, "文件导入成功！数据行数：" + successCount + "，失败行数：" + errorList.size(), errorList);
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

    private Result<?> validateAndFilterData(List<TagCategoryRelationExcelModel> list) {
        if (list.isEmpty()) {
            return Result.error("文件导入失败！所有数据的标签或类目为空");
        }

        // 检查平台类型是否为空
        List<String> tagsWithEmptyPlatform = list.stream().filter(x -> x.getPlatformType() == null).map(TagCategoryRelationExcelModel::getTagName).collect(Collectors.toList());
        if (!tagsWithEmptyPlatform.isEmpty()) {
            return Result.error("文件导入失败！平台类型不能为空：" + tagsWithEmptyPlatform);
        }

        // 检查标签类型是否为空
        List<String> tagsWithEmptyTagType = list.stream().filter(x -> x.getTagType() == null).map(TagCategoryRelationExcelModel::getTagName).collect(Collectors.toList());
        if (!tagsWithEmptyTagType.isEmpty()) {
            return Result.error("文件导入失败！标签类型不能为空：" + tagsWithEmptyTagType);
        }

        // 检查种子视频类型是否为空
        List<String> tagsWithEmptyVideoUrl = list.stream().filter(x -> x.getTagType() == ROOT_VIDEO.getCode() && oConvertUtils.isEmpty(x.getVideoUrl())).map(TagCategoryRelationExcelModel::getTagName).collect(Collectors.toList());
        if (!tagsWithEmptyVideoUrl.isEmpty()) {
            return Result.error("文件导入失败！视频链接不能为空：" + tagsWithEmptyVideoUrl);
        }
        return null;
    }

    @GetMapping(value = "/queryByCategoryId")
    public Result<?> queryByCategoryId(@RequestParam(name = "categoryId", required = true) String categoryId) {
        List<KolTagAttributeRelation> categoryRelationList = tagCategoryRelationService.lambdaQuery().eq(KolTagAttributeRelation::getAttributeId, categoryId).list();
        return Result.ok(categoryRelationList);
    }

    @GetMapping(value = "/queryByTagId")
    public Result<?> queryByTagId(@RequestParam(name = "tagId", required = true) String tagId) {
        List<KolTagAttributeRelation> categoryRelationList = tagCategoryRelationService.lambdaQuery().eq(KolTagAttributeRelation::getTagId, tagId).list();
        return Result.ok(categoryRelationList);
    }


    /**
     * 下载模板
     */
    @RequestMapping(value = "/downloadXls", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadXls(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("Sheet");
        String typeId = request.getParameter("typeId");
        String[] header = {"类目", "标签", "平台", "类型", "视频链接"};

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
        setDropdown(sheet, typeId);
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
        // Create a hidden sheet to store dropdown values
        HSSFWorkbook workbook = sheet.getWorkbook();
        HSSFSheet hiddenSheet = workbook.createSheet("HiddenSheet" + column);
        workbook.setSheetHidden(workbook.getSheetIndex(hiddenSheet), true);

        // Write dropdown values to the hidden sheet
        for (int i = 0; i < dropdownValues.length; i++) {
            HSSFRow row = hiddenSheet.createRow(i);
            HSSFCell cell = row.createCell(0);
            cell.setCellValue(dropdownValues[i]);
        }

        // Create a named range for the dropdown values
        String rangeName = "DropdownList" + column;
        Name namedRange = workbook.createName();
        namedRange.setNameName(rangeName);
        String reference = hiddenSheet.getSheetName() + "!$A$1:$A$" + dropdownValues.length;
        namedRange.setRefersToFormula(reference);

        // Create data validation using the named range
        CellRangeAddressList addressList = new CellRangeAddressList(1, 65535, column, column);
        DVConstraint dvConstraint = DVConstraint.createFormulaListConstraint(rangeName);
        DataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
        sheet.addValidationData(dataValidation);
    }

    /**
     * 功能描述:设置下拉框
     *
     * @param sheet
     * @author nqr
     * @date 2025年4月28日09:08:16
     */
    private void setDropdown(HSSFSheet sheet, String typeId) {
        // 创建下拉列表: 类目
        List<KolTagCategory> kolTagCategories = tagCategoryService.lambdaQuery()
                .eq(KolTagCategory::getCategoryTypeId, typeId)
                .eq(KolTagCategory::getIsDelete, 0)
                .eq(KolTagCategory::getIsHasChild, 0).list();
        String[] categoryDropdown = kolTagCategories.stream().map(KolTagCategory::getCategoryName).toArray(String[]::new);
        createDropdownList(sheet, categoryDropdown, 0);

        // 创建下拉列表: 平台类型
        String[] platformDropdown = sysBaseAPI.queryDictItemsByCode("platform_type").stream().map(DictModel::getText).toArray(String[]::new);
        createDropdownList(sheet, platformDropdown, 2);


        // 创建下拉列表: 标签类型
        String[] tagTypeDropdown = sysBaseAPI.queryDictItemsByCode("tag_type").stream().map(DictModel::getText).toArray(String[]::new);
        createDropdownList(sheet, tagTypeDropdown, 3);

    }


    /**
     * @description: 恢复标签
     * @author: nqr
     * @date: 2025/5/20 14:36
     **/

    @GetMapping(value = "/recoverByTag")
    public Result<?> recoverByTag(@RequestParam(name = "id", required = true) String id) {
        tagCategoryRelationService.recoverTag(id);
        return Result.ok("恢复成功");
    }
    /**
     * 查询当前标签已选中类目
     *
     * @param
     * @return
     */
    @AutoLog(value = "标签与类目关联表-查询当前标签已选中类目" )
    @Operation(summary = "标签与类目关联表-查询当前标签已选中类目", description = "标签与类目关联表-查询当前标签已选中类目" )
    @GetMapping(value = "/getSelectedCategories")
    public Result<?> getSelectedCategories(@RequestParam(name = "tagId", required = true) String tagId) {
        List<TypeData> list =tagCategoryRelationService.getSelectedCategories(tagId);
        return Result.ok(list);
    }

}
