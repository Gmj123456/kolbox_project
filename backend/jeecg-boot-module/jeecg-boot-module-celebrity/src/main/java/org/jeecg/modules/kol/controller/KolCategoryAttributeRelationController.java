package org.jeecg.modules.kol.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.SystemConstants;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.kol.entity.KolCategory;
import org.jeecg.modules.kol.entity.KolCategoryAttributeRelation;
import org.jeecg.modules.kol.model.KolCategoryAttributeRelationModel;
import org.jeecg.modules.kol.model.KolCategoryModel;
import org.jeecg.modules.kol.model.TypeData;
import org.jeecg.modules.kol.service.IKolAttributeTypeService;
import org.jeecg.modules.kol.service.IKolCategoryAttributeRelationService;
import org.jeecg.modules.kol.service.IKolCategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 类目关联表
 * @Author: dongruyang
 * @Date: 2025-06-21
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "类目关联表")
@RestController
@RequestMapping("/kol/kolCategoryRelation")
public class KolCategoryAttributeRelationController extends JeecgController<KolCategoryAttributeRelation, IKolCategoryAttributeRelationService> {
    @Autowired
    private IKolCategoryAttributeRelationService kolCategoryRelationService;
    @Autowired
    private IKolAttributeTypeService categoryTypeService;
    @Autowired
    private IKolCategoryService categoryService;


    /**
     * 分页列表查询
     *
     * @param kolCategoryAttributeRelationModel
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "类目关联表-" + SystemConstants.PAGE_LIST_QUERY)
    @Operation(summary = "类目关联表-" + SystemConstants.PAGE_LIST_QUERY, description = "类目关联表-" + SystemConstants.PAGE_LIST_QUERY)
    @GetMapping(value = "/list")
    public Result<?> queryPageList(KolCategoryAttributeRelationModel kolCategoryAttributeRelationModel, @RequestParam(name = SystemConstants.PAGE_NO, defaultValue = SystemConstants.PAGE_NO_DEFAULT) Integer pageNo, @RequestParam(name = SystemConstants.PAGE_SIZE, defaultValue = SystemConstants.PAGE_SIZE_DEFAULT) Integer pageSize, HttpServletRequest req) {
        String categoryId = kolCategoryAttributeRelationModel.getCategoryId();
        Page<KolCategory> page = new Page<>(pageNo, pageSize);
        IPage<KolCategory> pageList = new Page<>();
        LambdaQueryWrapper<KolCategory> queryWrapper = new LambdaQueryWrapper<>();
        if (oConvertUtils.isNotEmpty(categoryId)) {
            // 查询类目顶级类目
            KolCategory category = categoryService.getById(categoryId);
            /*Integer count = kolCategoryRelationService.lambdaQuery().eq(KolCategoryAttributeRelation::getCategoryId, categoryId).count();
            if (count == 0) {
                return Result.ok(pageList);
            }*/
            if (!category.getParentId().equals("0")) {
                String rootPathCode = category.getCategoryCodePath().split("\\|")[0];
                queryWrapper.eq(KolCategory::getCategoryCodePath, rootPathCode);
                kolCategoryAttributeRelationModel.setRootPathCode(rootPathCode);
                kolCategoryAttributeRelationModel.setAttributeId(null);
            } else {
                queryWrapper.eq(KolCategory::getCategoryCodePath, category.getCategoryCodePath());
                kolCategoryAttributeRelationModel.setRootPathCode(category.getCategoryCodePath());
            }
        }
        if (oConvertUtils.isNotEmpty(kolCategoryAttributeRelationModel.getSubAttributeName())) {
            Page<KolCategoryAttributeRelationModel> pageAttribute = new Page<>(pageNo, pageSize);
            // 走原查询逻辑
            IPage<KolCategoryAttributeRelationModel> pageAttributeList = kolCategoryRelationService.pageRelationList(pageAttribute, kolCategoryAttributeRelationModel);
            List<KolCategoryAttributeRelationModel> list = pageAttributeList.getRecords();
            buildCategoryTree(list);
            pageAttributeList.setRecords(list);
            return Result.ok(pageAttributeList);
        }

        queryWrapper.eq(KolCategory::getParentId, "0");
        queryWrapper.orderByAsc(KolCategory::getSortCode);
        pageList = categoryService.page(page, queryWrapper);
        List<KolCategory> list = pageList.getRecords();
        List<KolCategoryModel> categoryModelList = buildCategoryTreeNew(list);
        IPage<KolCategoryModel> categoryModelIPage = new Page<>();
        categoryModelIPage.setPages(pageList.getPages());
        categoryModelIPage.setRecords(categoryModelList);
        categoryModelIPage.setTotal(pageList.getTotal());
        categoryModelIPage.setSize(pageList.getSize());
        categoryModelIPage.setCurrent(pageList.getCurrent());
        return Result.ok(categoryModelIPage);
    }

    private List<KolCategoryModel> buildCategoryTreeNew(List<KolCategory> list) {
        List<KolCategoryModel> kolCategoryAll = new ArrayList<>();
        List<String> categoryIds = new ArrayList<>();
        List<KolCategory> categoryListAll = categoryService.list();

        // 递归获取所有子分类
        for (KolCategory kolCategory : list) {
            categoryIds.add(kolCategory.getId());
            KolCategoryModel categoryModel = new KolCategoryModel();
            BeanUtils.copyProperties(kolCategory, categoryModel);
            getAllChildCategoriesNew(categoryModel, categoryIds,categoryListAll);
            kolCategoryAll.add(categoryModel);
        }
        // 查询所有的社媒属性
        List<KolCategoryAttributeRelationModel> attributeRelationListAll = kolCategoryRelationService.getAllCategoryRelations(categoryIds);
        setAttributeRelation(kolCategoryAll, attributeRelationListAll);
        return kolCategoryAll;
    }

    private static void setAttributeRelation(List<KolCategoryModel> kolCategoryAll, List<KolCategoryAttributeRelationModel> attributeRelationListAll) {
        // 构建分类树
        for (KolCategoryModel categoryModel : kolCategoryAll) {
            String id = categoryModel.getId();
            List<KolCategoryAttributeRelationModel> attributeRelationModels = attributeRelationListAll.stream().filter(item -> item.getCategoryId().equals(id)).collect(Collectors.toList());
            Map<String, List<KolCategoryAttributeRelationModel>> map = attributeRelationModels.stream().collect(Collectors.groupingBy(KolCategoryAttributeRelationModel::getAttributeTypeId));
            List<TypeData> dataList = new ArrayList<>();
            for (String categoryTypeId : map.keySet()) {
                TypeData typeData = new TypeData();
                List<TypeData.CategoryItem> categoryItems = new ArrayList<>();
                typeData.setTypeId(categoryTypeId);
                List<KolCategoryAttributeRelationModel> attributeRelations = map.get(categoryTypeId);
                attributeRelations.forEach(item -> {
                    TypeData.CategoryItem categoryItem = new TypeData.CategoryItem();
                    categoryItem.setLevel(item.getLevel());
                    categoryItem.setIsSel(true);
                    categoryItem.setCategoryId(item.getCategoryId());
                    categoryItem.setCategoryName(item.getCategoryName());
                    categoryItem.setAttributeId(item.getId());
                    categoryItem.setAttributeName(item.getAttributeName());
                    categoryItem.setAttributeEnName(item.getAttributeEnName());
                    categoryItems.add(categoryItem);
                });
                typeData.setList(categoryItems);
                dataList.add(typeData);
            }
            categoryModel.setDataList(dataList);
            if (categoryModel.getChildren() != null && !categoryModel.getChildren().isEmpty()) {
                setAttributeRelation(categoryModel.getChildren(), attributeRelationListAll);
            }
        }
    }

    @GetMapping(value = "/getCategoryList")
    public Result<?> getCategoryList(KolCategoryAttributeRelationModel kolCategoryAttributeRelationModel) {
        List<KolCategoryAttributeRelationModel> list = kolCategoryRelationService.getCategoryList(kolCategoryAttributeRelationModel);
        buildCategoryTree(list);
        return Result.ok(list);
    }

    private void buildCategoryTree(List<KolCategoryAttributeRelationModel> categoryList) {
        for (KolCategoryAttributeRelationModel categoryRelationModel : categoryList) {
            String categoryId = categoryRelationModel.getCategoryId();

            // 查询当前分类的子分类
            List<KolCategory> list = categoryService.lambdaQuery().eq(KolCategory::getParentId, categoryId).list();
            if (list.isEmpty()) {
                continue;
            }
            List<KolCategoryAttributeRelationModel> childrenList = new ArrayList<>();
            for (KolCategory kolCategory : list) {
                KolCategoryAttributeRelationModel queryModel = new KolCategoryAttributeRelationModel();
                queryModel.setCategoryId(kolCategory.getId());
                List<KolCategoryAttributeRelationModel> children = kolCategoryRelationService.getCategoryList(queryModel);
                // 如果有子分类，递归处理子分类
                if (children != null && !children.isEmpty()) {
                    buildCategoryTree(children);
                    childrenList.addAll(children);
                }
            }
            // 设置子分类
            categoryRelationModel.setChildren(childrenList);
        }
    }

    /**
     * 添加
     *
     * @param kolCategoryAttributeRelationModel
     * @return
     */
    @AutoLog(value = "类目关联表-" + SystemConstants.ADD)
    @Operation(summary = "类目关联表-" + SystemConstants.ADD, description = "类目关联表-" + SystemConstants.ADD)
    @PostMapping(value = "/add")
    // @LimitSubmit(key = "relation:#authentication.principal.userId", limit = 3, needAllWait = true)
    public Result<?> add(@RequestBody KolCategoryAttributeRelationModel kolCategoryAttributeRelationModel) {
        // 参数提取
        String categoryId = kolCategoryAttributeRelationModel.getCategoryId();
        List<TypeData> dataList = kolCategoryAttributeRelationModel.getDataList();

        // 基础参数校验
        Result<?> validationResult = validateBasicParams(categoryId, dataList);
        if (!validationResult.isSuccess()) {
            return validationResult;
        }

        // 获取分类信息并校验
        KolCategory category = categoryService.getById(categoryId);
        if (category == null) {
            return Result.error("分类不存在！");
        }

        // 业务规则校验
        Result<?> businessValidationResult = validateBusinessRules(category, dataList);
        if (!businessValidationResult.isSuccess()) {
            return businessValidationResult;
        }

        List<String> childRemoveIds = new ArrayList<>();

        // 子分类冲突检查
        checkChildCategoryConflict(categoryId, dataList, childRemoveIds);

        // if(!childRemoveIds.isEmpty()){
        //     return Result.error("子分类冲突，请重新选择！");
        // }

        // 构建并保存关联数据
        List<KolCategoryAttributeRelation> saveList = buildCategoryRelations(categoryId, dataList);
        kolCategoryRelationService.saveData(categoryId, saveList, childRemoveIds);

        return Result.ok(SystemConstants.ADD_SUCCESS);
    }

    /**
     * 添加
     *
     * @param models
     * @return
     */
    @AutoLog(value = "类目关联表-" + SystemConstants.ADD)
    @Operation(summary = "类目关联表-" + SystemConstants.ADD, description = "类目关联表-" + SystemConstants.ADD)
    @PostMapping(value = "/addBatch")
    public Result<?> addBatch(@RequestBody List<KolCategoryAttributeRelationModel> models) {
        if (oConvertUtils.listIsEmpty(models)) {
            return Result.error("参数不能为空！");
        }
        for (KolCategoryAttributeRelationModel relationModel : models) {
            // 参数提取
            String categoryId = relationModel.getCategoryId();
            List<TypeData> dataList = relationModel.getDataList();

            // 基础参数校验
            Result<?> validationResult = validateBasicParams(categoryId, dataList);
            if (!validationResult.isSuccess()) {
                return validationResult;
            }

            // 获取分类信息并校验
            KolCategory category = categoryService.getById(categoryId);
            if (category == null) {
                return Result.error("分类不存在！");
            }

            // 业务规则校验
            Result<?> businessValidationResult = validateBusinessRules(category, dataList);
            if (!businessValidationResult.isSuccess()) {
                return businessValidationResult;
            }

            List<String> childRemoveIds = new ArrayList<>();

            // 子分类冲突检查
            checkChildCategoryConflict(categoryId, dataList, childRemoveIds);

            // 构建并保存关联数据
            List<KolCategoryAttributeRelation> saveList = buildCategoryRelations(categoryId, dataList);
            kolCategoryRelationService.saveData(categoryId, saveList, childRemoveIds);
        }
        return Result.ok("操作成功");
    }

    /**
     * 基础参数校验
     */
    private Result<?> validateBasicParams(String categoryId, List<TypeData> dataList) {
        if (oConvertUtils.isEmpty(categoryId)) {
            return Result.error("类目不能为空！");
        }
        if (oConvertUtils.isEmpty(dataList)) {
            return Result.error("关联数据不能为空！");
        }
        return Result.ok();
    }

    /**
     * 业务规则校验
     */
    private Result<?> validateBusinessRules(KolCategory category, List<TypeData> dataList) {
        // 检查是否存在空的关联数据
        boolean hasEmptyList = dataList.stream().anyMatch(typeData -> typeData.getList().isEmpty());
        if (hasEmptyList) {
            return Result.error("关联数据不能为空！");
        }

        // 叶子节点必须设置级别
        if (category.getIsHasChild() == 0) {
            boolean hasNullLevel = dataList.stream().flatMap(typeData -> typeData.getList().stream()).anyMatch(item -> item.getLevel() == null);
            if (hasNullLevel) {
                return Result.error("子节点必须设置关联等级！");
            }
        }

        return Result.ok();
    }

    /**
     * 处理子分类冲突数据
     */
    private void checkChildCategoryConflict(String categoryId, List<TypeData> dataList, List<String> childRemoveIds) {
        // 递归查询所有子分类
        List<KolCategory> allChildCategories = getAllChildCategories(categoryId);

        if (allChildCategories.isEmpty()) {
            return;
        }

        // 获取当前要关联的分类ID
        Set<String> currentCategoryIds = dataList.stream().flatMap(typeData -> typeData.getList().stream()).map(TypeData.CategoryItem::getAttributeId).collect(Collectors.toSet());

        // 删除子级中存在但父级不存在的关联数据
        removeConflictChildRelations(allChildCategories, currentCategoryIds, childRemoveIds);

    }

    /**
     * 递归获取所有子分类
     */
    private void getAllChildCategoriesNew(KolCategoryModel kolCategory, List<String> categoryIds,List<KolCategory> categoryListAll) {
        // 获取直接子分类
        List<KolCategory> directChildren = categoryListAll.stream().filter(x -> x.getParentId().equals(kolCategory.getId())).sorted(Comparator.comparing(KolCategory::getSortCode)).collect(Collectors.toList());
        if (!directChildren.isEmpty()) {
            List<KolCategoryModel> kolCategoryModels = oConvertUtils.entityListToModelList(directChildren, KolCategoryModel.class);
            kolCategory.setChildren(kolCategoryModels);
            // 递归获取每个子分类的子分类
            for (KolCategoryModel childModel : kolCategoryModels) {
                categoryIds.add(childModel.getId());
                getAllChildCategoriesNew(childModel, categoryIds,categoryListAll);
            }
        }
    }

    /**
     * 递归获取所有子分类
     */
    private List<KolCategory> getAllChildCategories(String parentId) {
        List<KolCategory> allChildren = new ArrayList<>();

        // 获取直接子分类
        List<KolCategory> directChildren = categoryService.lambdaQuery().eq(KolCategory::getParentId, parentId).list();

        if (!directChildren.isEmpty()) {
            allChildren.addAll(directChildren);

            // 递归获取每个子分类的子分类
            for (KolCategory child : directChildren) {
                List<KolCategory> grandChildren = getAllChildCategories(child.getId());
                allChildren.addAll(grandChildren);
            }
        }

        return allChildren;
    }

    /**
     * 删除子级中存在但父级不存在的关联数据
     */
    private void removeConflictChildRelations(List<KolCategory> childCategories, Set<String> parentCategoryIds, List<String> childRemoveIds) {
        if (childCategories.isEmpty()) {
            return;
        }

        List<String> childCategoryIds = childCategories.stream().map(KolCategory::getId).collect(Collectors.toList());

        // 查询所有子分类的关联数据
        List<KolCategoryAttributeRelation> childRelations = kolCategoryRelationService.lambdaQuery().in(KolCategoryAttributeRelation::getCategoryId, childCategoryIds).list();

        if (childRelations.isEmpty()) {
            return;
        }

        // 找出需要删除的关联数据（子级存在但父级不存在的）
        List<String> relationsToDelete = childRelations.stream().filter(relation -> !parentCategoryIds.contains(relation.getAttributeCategoryId())).map(KolCategoryAttributeRelation::getId).collect(Collectors.toList());

        // 批量删除冲突的关联数据
        if (!relationsToDelete.isEmpty()) {
            childRemoveIds.addAll(relationsToDelete);
        }
    }

    /**
     * 构建分类关联数据
     */
    private List<KolCategoryAttributeRelation> buildCategoryRelations(String categoryId, List<TypeData> dataList) {
        return dataList.stream().flatMap(typeData -> typeData.getList().stream()).map(categoryItem -> createCategoryRelation(categoryId, categoryItem)).collect(Collectors.toList());
    }

    /**
     * 创建单个分类关联对象
     */
    private KolCategoryAttributeRelation createCategoryRelation(String categoryId, TypeData.CategoryItem categoryItem) {
        KolCategoryAttributeRelation relation = new KolCategoryAttributeRelation();
        relation.setId(IdWorker.get32UUID());
        relation.setCategoryId(categoryId);
        relation.setAttributeCategoryId(categoryItem.getAttributeId());
        relation.setLevel(categoryItem.getLevel());
        relation.setIsDelete(0);
        return relation;
    }


    /**
     * 编辑
     *
     * @param kolCategoryAttributeRelation
     * @return
     */
    @AutoLog(value = "类目关联表-" + SystemConstants.EDIT)
    @Operation(summary = "类目关联表-" + SystemConstants.EDIT, description = "类目关联表-" + SystemConstants.EDIT)
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody KolCategoryAttributeRelation kolCategoryAttributeRelation) {
        // kolCategoryRelationService.updateById(kolCategoryRelation);
        return Result.ok(SystemConstants.EDIT_SUCCESS);
    }

    /**
     * 通过id删除
     *
     * @param categoryId
     * @return
     */
    @AutoLog(value = "类目关联表-" + SystemConstants.DELETE_BY_ID)
    @Operation(summary = "类目关联表-" + SystemConstants.DELETE_BY_ID, description = "类目关联表-" + SystemConstants.DELETE_BY_ID)
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "categoryId", required = true) String categoryId) {
        // 查询当前类目是否有子类目
        List<KolCategory> list = categoryService.lambdaQuery().eq(KolCategory::getParentId, categoryId).list();
        if (list != null && !list.isEmpty()) {
            List<String> childCategoryIds = list.stream().map(KolCategory::getId).collect(Collectors.toList());
            List<KolCategoryAttributeRelation> relationList = kolCategoryRelationService.lambdaQuery().in(KolCategoryAttributeRelation::getCategoryId, childCategoryIds).list();
            if (relationList != null && !relationList.isEmpty()) {
                return Result.error("当前类目下有子类目正在使用相关数据，无法删除！");
            }
        }
        kolCategoryRelationService.lambdaUpdate().eq(KolCategoryAttributeRelation::getCategoryId, categoryId).remove();
        return Result.ok(SystemConstants.DELETE_SUCCESS);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "类目关联表-" + SystemConstants.DELETE_BATCH)
    @Operation(summary = "类目关联表-" + SystemConstants.DELETE_BATCH, description = "类目关联表-" + SystemConstants.DELETE_BATCH)
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.kolCategoryRelationService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok(SystemConstants.DELETE_BATCH_SUCCESS);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "类目关联表-" + SystemConstants.QUERY_BY_ID)
    @Operation(summary = "类目关联表-" + SystemConstants.QUERY_BY_ID, description = "类目关联表-" + SystemConstants.QUERY_BY_ID)
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        KolCategoryAttributeRelation kolCategoryAttributeRelation = kolCategoryRelationService.getById(id);
        return Result.ok(kolCategoryAttributeRelation);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param kolCategoryAttributeRelation
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, KolCategoryAttributeRelation kolCategoryAttributeRelation) {
        return super.exportXls(request, kolCategoryAttributeRelation, KolCategoryAttributeRelation.class, "类目关联表");
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
        return super.importExcel(request, response, KolCategoryAttributeRelation.class);
    }

    /**
     * @description:根据类目id查询关联数据
     * @author: nqr
     * @date: 2025/6/21 17:16
     **/
    @AutoLog(value = "类目关联表-根据类目id查询关联数据")
    @Operation(summary = "类目关联表-根据类目id查询关联数据", description = "类目关联表-根据类目id查询关联数据")
    @GetMapping(value = "/queryByCategoryId")
    public Result<?> queryByCategoryId(@RequestParam(name = "categoryId", required = true) String categoryId) {
        // 获取分类信息
        KolCategory category = categoryService.getById(categoryId);
        if (category == null) {
            return Result.error("分类不存在！");
        }
        List<TypeData> list = kolCategoryRelationService.queryByCategoryId(category);

        return Result.ok(list);
    }
}
