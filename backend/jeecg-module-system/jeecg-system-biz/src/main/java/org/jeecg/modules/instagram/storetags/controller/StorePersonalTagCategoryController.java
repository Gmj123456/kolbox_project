package org.jeecg.modules.instagram.storetags.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.ServletOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.SystemConstants;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivate;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityPrivateService;
import org.jeecg.modules.instagram.storetags.entity.StoreCelebrityPrivatePersonalTags;
import org.jeecg.modules.instagram.storetags.entity.StorePersonalTagCategory;
import org.jeecg.modules.instagram.storetags.entity.StorePersonalTags;
import org.jeecg.modules.instagram.storetags.model.StorePersonalTagsModel;
import org.jeecg.modules.instagram.storetags.service.IStoreCelebrityPrivatePersonalTagsService;
import org.jeecg.modules.instagram.storetags.service.IStorePersonalTagCategoryService;
import org.jeecg.modules.instagram.storetags.service.IStorePersonalTagsService;
import org.jeecg.modules.kol.entity.KolAttributeType;
import org.jeecg.modules.kol.entity.KolTagCategory;
import org.jeecg.modules.kol.service.IKolAttributeTypeService;
import org.jeecg.modules.kol.service.IKolTagCategoryService;
import org.jeecg.modules.system.entity.SysDictItem;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Description: 个性化标签类目关联表
 * @Author: nqr
 * @Date: 2025-04-27
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "个性化标签类目关联表")
@RestController
@RequestMapping("/personalTagCategory")
public class StorePersonalTagCategoryController extends JeecgController<StorePersonalTagCategory, IStorePersonalTagCategoryService> {
    @Autowired
    private IStorePersonalTagCategoryService personalTagCategoryService;
    @Autowired
    private IKolTagCategoryService kolTagCategoryService;
    @Autowired
    private IStorePersonalTagsService personalTagsService;
    @Autowired
    private IStoreCelebrityPrivatePersonalTagsService privatePersonalTagsService;
    @Autowired
    private IStoreCelebrityPrivateService privateService;
    @Autowired
    private ISysDictService dictService;
    @Autowired
    private IKolAttributeTypeService categoryTypeService;

    /**
     * 分页列表查询
     *
     * @param storePersonalTagCategory
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "个性化标签类目关联表-" + SystemConstants.PAGE_LIST_QUERY)
@Operation(summary = "个性化标签类目关联表-" + SystemConstants.PAGE_LIST_QUERY, description = "个性化标签类目关联表-" + SystemConstants.PAGE_LIST_QUERY)
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StorePersonalTagCategory storePersonalTagCategory, @RequestParam(name = SystemConstants.PAGE_NO, defaultValue = SystemConstants.PAGE_NO_DEFAULT) Integer pageNo, @RequestParam(name = SystemConstants.PAGE_SIZE, defaultValue = SystemConstants.PAGE_SIZE_DEFAULT) Integer pageSize, HttpServletRequest req) {
        QueryWrapper<StorePersonalTagCategory> queryWrapper = QueryGenerator.initQueryWrapper(storePersonalTagCategory, req.getParameterMap());
        Page<StorePersonalTagCategory> page = new Page<StorePersonalTagCategory>(pageNo, pageSize);
        IPage<StorePersonalTagCategory> pageList = personalTagCategoryService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param personalTagsModel
     * @return
     */
    @AutoLog(value = "个性化标签类目关联表-" + SystemConstants.ADD)
@Operation(summary = "个性化标签类目关联表-" + SystemConstants.ADD, description = "个性化标签类目关联表-" + SystemConstants.ADD)
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody StorePersonalTagsModel personalTagsModel) {
        String categoryIds = personalTagsModel.getCategoryIds();
        String tagName = personalTagsModel.getTagName().trim().toLowerCase();
        int isRecover = personalTagsModel.getIsRecover();
        if (tagName.trim().contains(" ")) {
            return Result.error("标签名称不能包含空格");
        }

        String tagId = IdWorker.get32UUID();
        List<String> ids = Arrays.asList(categoryIds.split(","));
        // 查询类目是否存在
        List<KolTagCategory> kolTagCategories = (List<KolTagCategory>) kolTagCategoryService.listByIds(ids);
        if (!kolTagCategories.isEmpty() && kolTagCategories.size() != ids.size()) {
            return Result.error("类目数据错误，请重新选择");
        }

        StorePersonalTags personalTagsUpdate = new StorePersonalTags();
        List<StoreCelebrityPrivatePersonalTags> celebrityTagsToUpdate = new ArrayList<>();
        List<String> categoriesToDelete = new ArrayList<>();
        List<StoreCelebrityPrivate> celebrityPrivateListUpdate = new ArrayList<>();

        // 查询当前标签是否存在
        StorePersonalTags personalTags = personalTagsService.lambdaQuery().eq(StorePersonalTags::getTagName, tagName).last("LIMIT 1").one();
        // 标签存在
        if (personalTags != null) {
            tagId = personalTags.getId();
            // 判断是否是删除状态
            if (personalTags.getDelFlag() == 1) {
                // 标签已删除，判断是否需要恢复标签
                if (isRecover == 0) {
                    return Result.error(10001, "标签已删除，是否恢复已删除的标签及其关联网红？");
                }
                // 恢复标签，同时查询标签对应的网红关联关系，同时新增网红身上的个性化标签
                personalTagsUpdate.setId(personalTags.getId());
                personalTagsUpdate.setDelFlag(0);
                personalTagsUpdate.setUpdateBy(getUserNameByToken());
                personalTagsUpdate.setUpdateTime(new Date());

                // 查询标签对应的网红关联关系
                List<StoreCelebrityPrivatePersonalTags> existingRelations = privatePersonalTagsService.lambdaQuery().eq(StoreCelebrityPrivatePersonalTags::getTagId, personalTags.getId()).list();

                // 更新删除状态
                existingRelations.forEach(relation -> {
                    relation.setIsDel(0);
                    String celebrityId = relation.getCelebrityId();
                    StoreCelebrityPrivate celebrityPrivate = privateService.getById(celebrityId);
                    StoreCelebrityPrivate celebrityPrivateNew = new StoreCelebrityPrivate();
                    celebrityPrivateNew.setId(celebrityPrivate.getId());
                    String privatePersonalTags = celebrityPrivate.getPersonalTags();
                    if (oConvertUtils.isNotEmpty(privatePersonalTags)) {
                        privatePersonalTags += ",";
                    }
                    celebrityPrivateNew.setPersonalTags(privatePersonalTags + personalTags.getTagName());
                    celebrityPrivateListUpdate.add(celebrityPrivateNew);
                });
                celebrityTagsToUpdate.addAll(existingRelations);
            }

            // 查询并删除旧类目关联关系
            List<StorePersonalTagCategory> existingCategories = personalTagCategoryService.lambdaQuery().eq(StorePersonalTagCategory::getTagId, personalTags.getId()).list();
            categoriesToDelete.addAll(existingCategories.stream().map(StorePersonalTagCategory::getId).collect(Collectors.toList()));
        }

        // 新增标签
        StorePersonalTags storePersonalTagsToSave = new StorePersonalTags();
        storePersonalTagsToSave.setId(tagId);
        storePersonalTagsToSave.setTagName(tagName);
        storePersonalTagsToSave.setDelFlag(0);
        storePersonalTagsToSave.setCreateBy(getUserNameByToken());
        storePersonalTagsToSave.setCreateTime(new Date());

        // 新增当前标签与类目对照关系
        List<StorePersonalTagCategory> personalTagCategoryToSave = createTagCategory(tagId, ids);

        personalTagCategoryService.saveData(personalTagCategoryToSave, storePersonalTagsToSave, celebrityTagsToUpdate, categoriesToDelete, personalTagsUpdate, celebrityPrivateListUpdate);
        return Result.ok(SystemConstants.ADD_SUCCESS);
    }

    /**
     * 编辑
     *
     * @param personalTagsModel
     * @return
     */
    @AutoLog(value = "个性化标签类目关联表-" + SystemConstants.EDIT)
@Operation(summary = "个性化标签类目关联表-" + SystemConstants.EDIT, description = "个性化标签类目关联表-" + SystemConstants.EDIT)
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody StorePersonalTagsModel personalTagsModel) {
        // 参数校验
        String tagIdOld = personalTagsModel.getTagId();
        String tagNameNew = personalTagsModel.getTagName().trim().toLowerCase();

        if (tagNameNew.trim().contains(" ")) {
            return Result.error("标签名称不能包含空格");
        }

        // 获取旧标签及其关联数据
        StorePersonalTags existingTag = personalTagsService.getById(tagIdOld);
        if (existingTag == null) {
            return Result.error("标签不存在");
        }

        // 获取标签关联的网红和类目信息
        List<StoreCelebrityPrivatePersonalTags> existingCelebrityTags = privatePersonalTagsService.lambdaQuery().eq(StoreCelebrityPrivatePersonalTags::getTagId, tagIdOld).list();

        List<StorePersonalTagCategory> existingTagCategories = personalTagCategoryService.lambdaQuery().eq(StorePersonalTagCategory::getTagId, tagIdOld).list();

        // 处理类目信息
        List<String> categoryIds = Arrays.asList(personalTagsModel.getCategoryIds().split(","));
        List<KolTagCategory> kolTagCategories = (List<KolTagCategory>) kolTagCategoryService.listByIds(categoryIds);

        if (!kolTagCategories.isEmpty() && kolTagCategories.size() != categoryIds.size()) {
            return Result.error("类目数据错误，请重新选择");
        }

        // 准备更新数据
        StorePersonalTags tagToUpdate = new StorePersonalTags();
        tagToUpdate.setId(tagIdOld);

        List<StorePersonalTagCategory> newTagCategories;
        List<String> categoriesToDelete = new ArrayList<>();
        List<StoreCelebrityPrivatePersonalTags> celebrityTagsToSave = new ArrayList<>();
        List<StoreCelebrityPrivate> celebrityPrivateUpdates = new ArrayList<>();
        boolean shouldDeleteOriginalTag = false;
        String finalTagId = tagIdOld;

        // 处理标签名称变更
        if (!Objects.equals(existingTag.getTagName(), tagNameNew)) {
            // 检查新名称是否已存在
            StorePersonalTags tagWithNewName = personalTagsService.lambdaQuery().eq(StorePersonalTags::getTagName, tagNameNew).last("LIMIT 1").one();

            if (tagWithNewName != null) {
                // 如果新名称已存在，使用已存在标签的ID
                finalTagId = tagWithNewName.getId();
                shouldDeleteOriginalTag = true;

                // 处理已删除标签的恢复逻辑
                if (tagWithNewName.getDelFlag() == 1) {
                    if (personalTagsModel.getIsRecover() == 0) {
                        return Result.error(10001, "标签已删除，是否恢复已删除的标签及其关联网红？");
                    }

                    if (personalTagsModel.getIsRecover() == 1) {
                        // 恢复被删除的标签
                        tagToUpdate = new StorePersonalTags();
                        tagToUpdate.setId(finalTagId);
                        tagToUpdate.setDelFlag(0);

                        // 合并和去重网红关联关系
                        List<StoreCelebrityPrivatePersonalTags> existingRelations = privatePersonalTagsService.lambdaQuery().eq(StoreCelebrityPrivatePersonalTags::getTagId, finalTagId).list();

                        // 更新删除状态
                        existingRelations.forEach(relation -> relation.setIsDel(0));
                        existingRelations.addAll(existingCelebrityTags);

                        // 根据网红ID去重
                        celebrityTagsToSave = new ArrayList<>(new TreeSet<>(Comparator.comparing(StoreCelebrityPrivatePersonalTags::getCelebrityId)).addAll(existingRelations) ? existingRelations : Collections.emptyList());

                        // 查询并删除旧类目关联关系
                        List<StorePersonalTagCategory> existingCategories = personalTagCategoryService.lambdaQuery().eq(StorePersonalTagCategory::getTagId, finalTagId).list();

                        categoriesToDelete.addAll(existingCategories.stream().map(StorePersonalTagCategory::getId).collect(Collectors.toList()));
                    }
                }
            } else {
                // 如果新名称不存在，更新当前标签名称
                tagToUpdate.setTagName(tagNameNew);
                tagToUpdate.setUpdateBy(getUserNameByToken());
                tagToUpdate.setUpdateTime(new Date());
            }
            // 更新网红身上的个性化标签
            updatePrivateCelebritys(existingCelebrityTags, existingTag, tagNameNew, celebrityPrivateUpdates);
        }

        // 创建新的标签-类目关联
        newTagCategories = createTagCategory(finalTagId, categoryIds);

        // 添加旧的类目ID到删除列表
        categoriesToDelete.addAll(existingTagCategories.stream().map(StorePersonalTagCategory::getId).collect(Collectors.toList()));

        // 执行数据库更新操作
        personalTagCategoryService.updateTagCategory(newTagCategories, tagToUpdate, celebrityTagsToSave, categoriesToDelete, shouldDeleteOriginalTag, celebrityPrivateUpdates);

        return Result.ok(SystemConstants.EDIT_SUCCESS);
    }

    private void updatePrivateCelebritys(List<StoreCelebrityPrivatePersonalTags> existingCelebrityTags, StorePersonalTags existingTag, String tagNameNew, List<StoreCelebrityPrivate> celebrityPrivateUpdates) {
        List<String> celebrityIds = existingCelebrityTags.stream().map(StoreCelebrityPrivatePersonalTags::getCelebrityId).collect(Collectors.toList());
        if (celebrityIds.isEmpty()) {
            return;
        }
        List<StoreCelebrityPrivate> celebrityProfiles = (List<StoreCelebrityPrivate>) privateService.listByIds(celebrityIds);
        // 批量处理网红个性化标签
        celebrityProfiles.forEach(profile -> {
            StoreCelebrityPrivate celebrityPrivate = new StoreCelebrityPrivate();
            celebrityPrivate.setId(profile.getId());
            // 获取当前个性化标签
            String personalTags = profile.getPersonalTags();
            // 1. 使用正则表达式确保只替换完整标签名，避免部分匹配导致的错误
            // 例如：将",标签A,"替换为",新标签,"，而不会影响"标签A1"等
            String pattern = "(^|,)" + Pattern.quote(existingTag.getTagName()) + "($|,)";
            String replacement = "$1" + tagNameNew + "$2";
            String updatedTags = personalTags.replaceAll(pattern, replacement);

            // 2. 优化去重处理：分割、去重、重组
            String[] tagArray = updatedTags.split(",");
            String distinctTags = Arrays.stream(tagArray)
                    .filter(tag -> tag != null && !tag.trim().isEmpty()) // 过滤空标签
                    .distinct()
                    .collect(Collectors.joining(","));

            // 3. 更新对象
            celebrityPrivate.setPersonalTags(distinctTags);
            celebrityPrivate.setUpdateBy(getUserNameByToken());
            celebrityPrivate.setUpdateTime(new Date());
            celebrityPrivateUpdates.add(celebrityPrivate);
        });
    }

    /**
     * @description: 修改标签
     * @author: nqr
     * @date: 2025/5/7 16:11
     **/

    private List<StorePersonalTagCategory> updateTagCategory(String tagId, List<String> ids) {
        // 修改新标签对应的类目
        List<StorePersonalTagCategory> personalTagCategories = personalTagCategoryService.lambdaQuery().eq(StorePersonalTagCategory::getTagId, tagId).list();
        // 对比类目,去除已对照的类目
        personalTagCategories.forEach(personalTagCategory -> ids.removeIf(x -> x.equals(personalTagCategory.getCategoryId())));
        // 新增对照类目
        return createTagCategory(tagId, ids);
    }

    /**
     * @description:新增对照类目
     * @author: nqr
     * @date: 2025/5/7 16:29
     **/

    private List<StorePersonalTagCategory> createTagCategory(String tagId, List<String> ids) {
        return ids.stream().map(id -> {
            StorePersonalTagCategory storePersonalTagCategory = new StorePersonalTagCategory();
            storePersonalTagCategory.setTagId(tagId);
            storePersonalTagCategory.setCategoryId(id);
            storePersonalTagCategory.setCreateBy(getUserNameByToken());
            storePersonalTagCategory.setCreateTime(new Date());
            storePersonalTagCategory.setDelFlag(0);
            return storePersonalTagCategory;
        }).collect(Collectors.toList());
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "个性化标签类目关联表-" + SystemConstants.DELETE_BY_ID)
@Operation(summary = "个性化标签类目关联表-" + SystemConstants.DELETE_BY_ID, description = "个性化标签类目关联表-" + SystemConstants.DELETE_BY_ID)
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        // 查询标签是否存在
        delPersonalTagCategory(id);
        return Result.ok("禁用成功！");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "个性化标签类目关联表-" + SystemConstants.DELETE_BATCH)
@Operation(summary = "个性化标签类目关联表-" + SystemConstants.DELETE_BATCH, description = "个性化标签类目关联表-" + SystemConstants.DELETE_BATCH)
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        String[] idList = ids.split(",");
        for (String id : idList) {
            delPersonalTagCategory(id);
        }
        return Result.ok("禁用成功！");
    }

    /**
     * @description: 根据关联关系id删除标签
     * @author: nqr
     * @date: 2025/5/17 8:19
     **/

    private void delPersonalTagCategory(String personalTagCategoryId) {
        // 查询标签是否存在
        StorePersonalTagCategory storePersonalTagCategory = personalTagCategoryService.getById(personalTagCategoryId);
        String tagId = storePersonalTagCategory.getTagId();
        // 查询标签和网红关联关系
        List<StoreCelebrityPrivatePersonalTags> privatePersonalTags = privatePersonalTagsService.lambdaQuery().eq(StoreCelebrityPrivatePersonalTags::getTagId, tagId).list();
        // 标签存在，删除标签和网红关联关系
        if (!privatePersonalTags.isEmpty()) {
            // 删除标签和网红关联关系
            privatePersonalTagsService.lambdaUpdate()
                    .set(StoreCelebrityPrivatePersonalTags::getIsDel, 1)
                    .in(StoreCelebrityPrivatePersonalTags::getId, privatePersonalTags.stream().map(StoreCelebrityPrivatePersonalTags::getId).collect(Collectors.toList()));

            // 更新私有网红身上冗余的个性化标签
            List<String> categoryIds = privatePersonalTags.stream().map(StoreCelebrityPrivatePersonalTags::getCelebrityId).distinct().collect(Collectors.toList());
            List<StoreCelebrityPrivatePersonalTags> personalTags = privatePersonalTagsService.lambdaQuery().eq(StoreCelebrityPrivatePersonalTags::getIsDel, 0).in(StoreCelebrityPrivatePersonalTags::getCelebrityId, categoryIds).list();
            if (!personalTags.isEmpty()) {
                List<StoreCelebrityPrivate> storeCelebrityPrivates = new ArrayList<>();
                List<String> tagIds = personalTags.stream().map(StoreCelebrityPrivatePersonalTags::getTagId).distinct().collect(Collectors.toList());
                List<StorePersonalTags> personalTagsList = personalTagsService.lambdaQuery().in(StorePersonalTags::getId, tagIds).list();
                categoryIds.forEach(categoryId -> {
                    List<StoreCelebrityPrivatePersonalTags> tags = personalTags.stream().filter(x -> x.getCelebrityId().equals(categoryId) && !x.getTagId().equals(tagId)).collect(Collectors.toList());
                    List<String> privateTagIds = tags.stream().map(StoreCelebrityPrivatePersonalTags::getTagId).distinct().collect(Collectors.toList());
                    String tagNames = personalTagsList.stream().filter(x -> privateTagIds.contains(x.getId())).map(StorePersonalTags::getTagName).distinct().collect(Collectors.joining(","));
                    StoreCelebrityPrivate storeCelebrityPrivate = new StoreCelebrityPrivate();
                    storeCelebrityPrivate.setId(categoryId);
                    storeCelebrityPrivate.setPersonalTags(tagNames);
                    storeCelebrityPrivate.setUpdateBy(getUserNameByToken());
                    storeCelebrityPrivate.setUpdateTime(new Date());
                    storeCelebrityPrivates.add(storeCelebrityPrivate);
                });
                if (!storeCelebrityPrivates.isEmpty()) {
                    privateService.updateBatchById(storeCelebrityPrivates);
                }
            }
        }
        // 删除当前标签
        personalTagsService.lambdaUpdate().set(StorePersonalTags::getDelFlag, 1).eq(StorePersonalTags::getId, tagId).update();
        // 删除当前标签所有的关联关系
        personalTagCategoryService.lambdaUpdate().set(StorePersonalTagCategory::getDelFlag, 1).eq(StorePersonalTagCategory::getTagId, tagId).update();
        // 删除当前标签所对应的私有网红关联关系
        privatePersonalTagsService.lambdaUpdate().set(StoreCelebrityPrivatePersonalTags::getIsDel, 1).eq(StoreCelebrityPrivatePersonalTags::getTagId, tagId).update();
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "个性化标签类目关联表-" + SystemConstants.QUERY_BY_ID)
@Operation(summary = "个性化标签类目关联表-" + SystemConstants.QUERY_BY_ID, description = "个性化标签类目关联表-" + SystemConstants.QUERY_BY_ID)
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        StorePersonalTagCategory storePersonalTagCategory = personalTagCategoryService.getById(id);
        return Result.ok(storePersonalTagCategory);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param storePersonalTagCategory
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StorePersonalTagCategory storePersonalTagCategory) {
        return super.exportXls(request, storePersonalTagCategory, StorePersonalTagCategory.class, "个性化标签类目关联表");
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
            MultipartFile file = entity.getValue();
            ImportParams params = new ImportParams();
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                long start = System.currentTimeMillis();
                // 获取上传文件列表
                List<StorePersonalTagsModel> list = ExcelImportUtil.importExcel(file.getInputStream(), StorePersonalTagsModel.class, params);
                list = list.stream().filter(x -> oConvertUtils.isNotEmpty(x.getTagName())).distinct().collect(Collectors.toList());
                if (list.isEmpty()) {
                    return Result.error("文件导入失败，请检查文件内容是否正确！");
                }
                // 查询类目是否存在
                List<String> categoryNames = list.stream().map(StorePersonalTagsModel::getCategoryName).filter(oConvertUtils::isNotEmpty).distinct().collect(Collectors.toList());
                List<KolTagCategory> tagCategories = kolTagCategoryService.lambdaQuery().in(KolTagCategory::getCategoryName, categoryNames).list();
                if (tagCategories.isEmpty()) {
                    return Result.error(500, "类目不存在", categoryNames);
                }
                List<String> categoryNameList = tagCategories.stream().map(KolTagCategory::getCategoryName).distinct().collect(Collectors.toList());
                // 判断是否有非法类目
                List<String> errorCategoryNames = categoryNames.stream().filter(x -> !categoryNameList.contains(x)).distinct().collect(Collectors.toList());
                if (!errorCategoryNames.isEmpty()) {
                    return Result.error(500, "类目不存在", errorCategoryNames);
                }
                List<String> errorList = new ArrayList<>();
                // 查询未分类类目
                List<SysDictItem> dictItems = dictService.getItemList("personal_tags_category");
                // 设置类目id
                list.forEach(x -> {
                    Optional<KolTagCategory> tagCategoryOptional = tagCategories.stream().filter(y -> y.getCategoryName().equals(x.getCategoryName())).findFirst();
                    if (tagCategoryOptional.isPresent()) {
                        x.setCategoryId(tagCategoryOptional.get().getId());
                    } else {
                        // 添加到未分类
                        // 添加新标签到未分类类目
                        String categoryId = dictItems.stream().filter(a -> a.getItemText().equals("category_id")).findFirst().get().getItemValue();
                        x.setCategoryId(categoryId);
                    }
                });
                // 标签是否存在
                List<String> tagNames = list.stream().map(StorePersonalTagsModel::getTagName).distinct().collect(Collectors.toList());
                List<StorePersonalTags> personalTags = personalTagsService.lambdaQuery().in(StorePersonalTags::getTagName, tagNames).list();
                // 创建一个Map来存储有效的标签
                Map<String, StorePersonalTags> validTagMap = personalTags.stream()
                        .filter(tag -> tag.getDelFlag() != 1)
                        .collect(Collectors.toMap(StorePersonalTags::getTagName, tag -> tag));

                // 创建一个Set来存储已删除的标签名
                Set<String> deletedTagNames = personalTags.stream()
                        .filter(tag -> tag.getDelFlag() == 1)
                        .map(StorePersonalTags::getTagName)
                        .collect(Collectors.toSet());

                // 处理列表中的每个标签
                List<StorePersonalTagsModel> noExistingTagList = new ArrayList<>();
                for (StorePersonalTagsModel personalTagsModel : list) {
                    String tagName = personalTagsModel.getTagName();
                    StorePersonalTags existingTag = validTagMap.get(tagName);

                    if (existingTag != null) {
                        personalTagsModel.setTagId(existingTag.getId());
                    } else if (deletedTagNames.contains(tagName)) {
                        errorList.add(String.format("标签 %s 已删除", tagName));
                        continue;
                    }
                    noExistingTagList.add(personalTagsModel);
                }

                // 保存标签与类目对照关系
                List<StorePersonalTagCategory> storePersonalTagCategorySave = new ArrayList<>();
                List<StorePersonalTags> personalTagsSave = new ArrayList<>();
                // 查询当前已存在的对照关系
                List<StorePersonalTagCategory> storePersonalTagCategories = new ArrayList<>();
                if (!personalTags.isEmpty()) {
                    storePersonalTagCategories = personalTagCategoryService.lambdaQuery().in(StorePersonalTagCategory::getTagId, personalTags.stream().map(StorePersonalTags::getId).collect(Collectors.toList())).list();
                }
                for (StorePersonalTagsModel storePersonalTagsModel : noExistingTagList) {
                    String categoryId = storePersonalTagsModel.getCategoryId();
                    String tagId = storePersonalTagsModel.getTagId();
                    if (oConvertUtils.isEmpty(tagId)) {
                        // 新增标签
                        StorePersonalTags storePersonalTags = new StorePersonalTags();
                        storePersonalTags.setId(IdWorker.get32UUID());
                        storePersonalTags.setTagName(storePersonalTagsModel.getTagName());
                        storePersonalTags.setCreateBy(getUserNameByToken());
                        storePersonalTags.setCreateTime(new Date());
                        personalTagsSave.add(storePersonalTags);
                        // 新增对照关系
                        createTagCategory(categoryId, storePersonalTags.getId(), storePersonalTagCategorySave);
                    } else {
                        // 判断对照关系是否存在
                        Optional<StorePersonalTagCategory> tagCategoryOptional = storePersonalTagCategories.stream().filter(x -> x.getTagId().equals(tagId) && x.getCategoryId().equals(categoryId)).findFirst();
                        if (!tagCategoryOptional.isPresent()) {
                            // 创建对照关系
                            createTagCategory(categoryId, tagId, storePersonalTagCategorySave);
                        }
                    }
                }
                // 保存标签与类目对照关系
                personalTagCategoryService.saveBatchData(storePersonalTagCategorySave, personalTagsSave);
                log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
                String msg = "文件导入成功！数据行数：" + list.size();
                return errorList.isEmpty() ? Result.ok(msg) : Result.OK(10001, msg + "存在失败数据，失败行数：" + errorList.size(), errorList);
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

    private void createTagCategory(String categoryId, String tagId, List<StorePersonalTagCategory> storePersonalTagCategorySave) {
        StorePersonalTagCategory storePersonalTagCategory = new StorePersonalTagCategory();
        storePersonalTagCategory.setId(IdWorker.get32UUID());
        storePersonalTagCategory.setCategoryId(categoryId);
        storePersonalTagCategory.setTagId(tagId);
        storePersonalTagCategory.setCreateBy(getUserNameByToken());
        storePersonalTagCategory.setCreateTime(new Date());
        storePersonalTagCategory.setDelFlag(0);
        storePersonalTagCategorySave.add(storePersonalTagCategory);
    }

    /**
     * 根据标签分类查询个性化标签
     *
     * @return
     */
    @AutoLog(value = "个性化标签类目关联表-根据标签分类查询个性化标签")
@Operation(summary = "个性化标签类目关联表-根据标签分类查询个性化标签", description = "个性化标签类目关联表-根据标签分类查询个性化标签")
    @GetMapping(value = "/queryByCategory")
    public Result<?> queryByCategory(StorePersonalTagsModel storePersonalTagsModel, @RequestParam(name = SystemConstants.PAGE_NO, defaultValue = SystemConstants.PAGE_NO_DEFAULT) Integer pageNo, @RequestParam(name = SystemConstants.PAGE_SIZE, defaultValue = SystemConstants.PAGE_SIZE_DEFAULT) Integer pageSize) {
        Page<StorePersonalTagsModel> page = new Page<>(pageNo, pageSize);
        IPage<StorePersonalTagsModel> pageList = personalTagCategoryService.queryByCategory(page, storePersonalTagsModel);
        return Result.ok(pageList);
    }

    /**
     * 下载模板
     */
    @RequestMapping(value = "/downloadXls", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadXls(HttpServletResponse response) throws IOException {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("Sheet");

        String[] header = {"类目", "标签"};
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
        // Create a hidden sheet to store dropdown values
        HSSFWorkbook workbook = sheet.getWorkbook();
        HSSFSheet hiddenSheet = workbook.createSheet("HiddenSheet");
        workbook.setSheetHidden(workbook.getSheetIndex(hiddenSheet), true);

        // Write dropdown values to the hidden sheet
        for (int i = 0; i < dropdownValues.length; i++) {
            HSSFRow row = hiddenSheet.createRow(i);
            HSSFCell cell = row.createCell(0);
            cell.setCellValue(dropdownValues[i]);
        }

        // Create a named range for the dropdown values
        String rangeName = "DropdownList";
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
    private void setDropdown(HSSFSheet sheet) {
        KolAttributeType kolAttributeType = categoryTypeService.lambdaQuery().eq(KolAttributeType::getType, 0).last("LIMIT 1").one();
        // 类目
        List<KolTagCategory> kolTagCategories = kolTagCategoryService.lambdaQuery()
                .eq(KolTagCategory::getCategoryTypeId, kolAttributeType.getId())
                .eq(KolTagCategory::getIsDelete, 0)
                .eq(KolTagCategory::getIsHasChild, 0).list();

        // 创建下拉列表: 类目
        String[] categoryDropdown = kolTagCategories.stream().map(KolTagCategory::getCategoryName).toArray(String[]::new);
        createDropdownList(sheet, categoryDropdown, 0);
    }

    /**
     * 通过标签查询
     *
     * @return
     */
    @AutoLog(value = "个性化标签类目关联表-通过标签查询关联关系")
@Operation(summary = "个性化标签类目关联表-通过标签查询关联关系", description = "个性化标签类目关联表-通过标签查询关联关系")
    @GetMapping(value = "/queryByTag")
    public Result<?> queryByTag(@RequestParam(name = "tagId", required = true) String tagId) {
        return Result.ok(personalTagCategoryService.lambdaQuery().eq(StorePersonalTagCategory::getTagId, tagId).eq(StorePersonalTagCategory::getDelFlag, 0).list());
    }

    /**
     * @description: 恢复标签
     * @author: nqr
     * @date: 2025/5/20 14:36
     **/
    @GetMapping(value = "/recoverByTag")
    public Result<?> recoverByTag(@RequestParam(name = "id", required = true) String id) {
        StorePersonalTags storePersonalTags = personalTagsService.getById(id);
        if (storePersonalTags == null) {
            return Result.error("标签不存在");
        }

        List<StorePersonalTagCategory> personalTagCategories = personalTagCategoryService.lambdaQuery().eq(StorePersonalTagCategory::getTagId, id).list();
        if (!personalTagCategories.isEmpty()) {
            List<String> categoryIds = personalTagCategories.stream().map(StorePersonalTagCategory::getCategoryId).collect(Collectors.toList());
            List<KolTagCategory> kolTagCategories = kolTagCategoryService.lambdaQuery().in(KolTagCategory::getId, categoryIds).list();
            List<KolTagCategory> collect = kolTagCategories.stream().filter(x -> x.getIsHasChild() == 1).collect(Collectors.toList());
            if (!collect.isEmpty()) {
                return Result.error(String.format("标签对应类目%s存在子类目，不能恢复", collect.stream().map(KolTagCategory::getCategoryName).collect(Collectors.joining(","))));
            }
        }

        List<StoreCelebrityPrivatePersonalTags> privatePersonalTags = privatePersonalTagsService.lambdaQuery().eq(StoreCelebrityPrivatePersonalTags::getTagId, id).list();
        List<String> celebrityIds = privatePersonalTags.stream().map(StoreCelebrityPrivatePersonalTags::getCelebrityId).collect(Collectors.toList());
        List<StoreCelebrityPrivate> celebrityPrivateToUpdateList = new ArrayList<>();
        if (!celebrityIds.isEmpty()) {
            List<StoreCelebrityPrivate> celebrityPrivates = (List<StoreCelebrityPrivate>) privateService.listByIds(celebrityIds);
            for (StoreCelebrityPrivate celebrityPrivate : celebrityPrivates) {
                StoreCelebrityPrivate celebrityPrivateToUpdate = new StoreCelebrityPrivate();
                celebrityPrivateToUpdate.setId(celebrityPrivate.getId());
                String personalTags = "";
                if (oConvertUtils.isNotEmpty(celebrityPrivate.getPersonalTags())) {
                    personalTags += celebrityPrivate.getPersonalTags() + ",";
                }
                celebrityPrivateToUpdate.setPersonalTags(personalTags + storePersonalTags.getTagName());
                celebrityPrivateToUpdateList.add(celebrityPrivateToUpdate);
            }
        }
        personalTagCategoryService.recoverTag(id, celebrityPrivateToUpdateList);
        return Result.ok("恢复成功");
    }
}
