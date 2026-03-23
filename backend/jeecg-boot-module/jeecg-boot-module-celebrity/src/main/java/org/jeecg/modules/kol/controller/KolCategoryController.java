package org.jeecg.modules.kol.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.SystemConstants;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.PersonalTagsModel;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.kol.entity.KolAttribute;
import org.jeecg.modules.kol.entity.KolCategory;
import org.jeecg.modules.kol.entity.KolCategoryAttributeRelation;
import org.jeecg.modules.kol.entity.KolProductCategory;
import org.jeecg.modules.kol.model.KolCategoryTreeModel;
import org.jeecg.modules.kol.model.KolTagCategoryTreeModel;
import org.jeecg.modules.kol.model.TreeSelectModel;
import org.jeecg.modules.kol.service.IKolAttributeTypeService;
import org.jeecg.modules.kol.service.IKolCategoryAttributeRelationService;
import org.jeecg.modules.kol.service.IKolCategoryService;
import org.jeecg.modules.kol.service.IKolProductCategoryService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.jeecg.common.constant.SystemConstants.*;

/**
 * @Description: 网红标签分类类目表
 * @Author: xyc
 * @Date: 2024年11月22日 18:23:04
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "网红标签类目")
@RestController
@RequestMapping("/kol/category")
public class KolCategoryController extends JeecgController<KolCategory, IKolCategoryService> {

    @Autowired
    private IKolCategoryService kolCategoryService;
    @Autowired
    private ISysBaseAPI sysBaseAPI;
    @Autowired
    private IKolAttributeTypeService attributeTypeService;
    @Autowired
    private IKolProductCategoryService productCategoryService;
    @Autowired
    private IKolCategoryAttributeRelationService categoryRelationService;


    /**
     * 分页列表查询
     *
     * @param kolCategory
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @GetMapping(value = "/rootList")
    @Operation(summary = "网红标签类目-" + PAGE_LIST_QUERY, description = "网红标签类目-" + PAGE_LIST_QUERY)
    public Result<IPage<KolCategoryTreeModel>> queryPageList(KolCategory kolCategory, @RequestParam(name = SystemConstants.PAGE_NO, defaultValue = SystemConstants.PAGE_NO_DEFAULT) Integer pageNo, @RequestParam(name = SystemConstants.PAGE_SIZE, defaultValue = SystemConstants.PAGE_SIZE_DEFAULT) Integer pageSize, HttpServletRequest req) {

        QueryWrapper<KolCategory> queryWrapper = QueryGenerator.initQueryWrapper(kolCategory, req.getParameterMap());
        String categoryName = kolCategory.getCategoryName();
        if (oConvertUtils.isNotEmpty(categoryName)) {
            queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().like(KolCategory::getCategoryEnName, categoryName).or().like(KolCategory::getCategoryName, categoryName);
        }
        // 获取所有符合条件的数据（不分页）
        List<KolCategory> allList = kolCategoryService.list(queryWrapper);

        // 转换为树形结构
        List<KolCategoryTreeModel> treeList = buildTreeNew(allList);

        // 创建分页对象
        IPage<KolCategoryTreeModel> pageList = new Page<>(pageNo, pageSize, treeList.size());

        // 手动分页
        int fromIndex = (pageNo - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, treeList.size());
        List<KolCategoryTreeModel> pageRecords = treeList.subList(fromIndex, toIndex);

        pageList.setRecords(pageRecords);

        Result<IPage<KolCategoryTreeModel>> result = new Result<>();
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 构建树形结构
     *
     * @param allList 所有分类数据
     * @return 树形结构列表
     */
    private List<KolCategoryTreeModel> buildTreeNew(List<KolCategory> allList) {
        // 创建Map存储所有节点，key为id，value为对应的树节点
        Map<String, KolCategoryTreeModel> nodeMap = new HashMap<>();
        List<KolCategoryTreeModel> rootNodes = new ArrayList<>();

        // 先将所有数据转换为树节点并存入map
        for (KolCategory category : allList) {
            nodeMap.put(category.getId(), new KolCategoryTreeModel(category));
        }

        // 构建父子关系
        for (KolCategory category : allList) {
            KolCategoryTreeModel node = nodeMap.get(category.getId());
            String parentId = category.getParentId();

            // 如果父id为null或者是根节点标识，则为根节点
            if (parentId == null || "0".equals(parentId)) {
                rootNodes.add(node);
            } else {
                // 否则找到父节点，建立父子关系
                KolCategoryTreeModel parentNode = nodeMap.get(parentId);
                if (parentNode != null) {
                    parentNode.addChild(node);
                } else {
                    // 如果找不到父节点，则作为根节点处理
                    rootNodes.add(node);
                }
            }
        }

        return rootNodes;
    }

    @GetMapping(value = "/childList")
    @Operation(summary = "网红标签类目-获取子节点列表", description = "网红标签类目-获取子节点列表")
    public Result<List<KolCategory>> queryPageList(KolCategory kolCategory, HttpServletRequest req) {
        Result<List<KolCategory>> result = new Result<>();
        LambdaQueryWrapper<KolCategory> queryWrapper = QueryGenerator.initQueryWrapper(kolCategory, req.getParameterMap()).lambda();
        queryWrapper.orderByAsc(KolCategory::getSortCode);
        List<KolCategory> list = kolCategoryService.list(queryWrapper);
        result.setSuccess(true);
        result.setResult(list);
        return result;
    }

    /**
     * 添加
     *
     * @param kolCategory
     * @return
     */
    @PostMapping(value = ADD_URL)
    @Operation(summary = "网红标签类目-" + ADD, description = "网红标签类目-" + ADD)
    public Result<KolCategory> add(@RequestBody KolCategory kolCategory) {
        Result<KolCategory> result = new Result<>();
        String categoryName = kolCategory.getCategoryName().trim();
        if (categoryName.contains("|")) {
            return result.error500("类目名称不能包含'|'符号");
        }
        String categoryEnName = kolCategory.getCategoryEnName();
        if (oConvertUtils.isNotEmpty(categoryEnName) && categoryEnName.contains("|")) {
            return result.error500("类目英文名称不能包含'|'符号");
        }
        LambdaQueryWrapper<KolCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(KolCategory::getCategoryName, categoryName);
        if (oConvertUtils.isNotEmpty(categoryEnName)) {
            queryWrapper.or().eq(KolCategory::getCategoryEnName, categoryEnName);
        }
        long count = kolCategoryService.count(queryWrapper);
        if (count > 0) {
            result.error500("类目名称已存在!");
            return result;
        }
        try {
            // 查询父类是否设置关联关系
            String parentId = kolCategory.getParentId();

            // 类目有了社媒属性匹配关系或者已经有产品关联了这个类目
            if (oConvertUtils.isNotEmpty(parentId) && !parentId.equals("0")) {
                // 判断是否存在关联记录
                Integer parentCount = Math.toIntExact(categoryRelationService.lambdaQuery().eq(KolCategoryAttributeRelation::getCategoryId, parentId).count());
                if (parentCount > 0) {
                    // 存在关联关系，则不能添加子类目
                    result.error500("父类目已关联社媒属性，不能添加子类目！");
                    return result;
                }
                // 判断是否存在产品关联记录
                Integer productCount = Math.toIntExact(productCategoryService.lambdaQuery().eq(KolProductCategory::getCategoryId, parentId).count());
                if (productCount > 0) {
                    // 存在产品关联记录，则不能添加子类目
                    result.error500("父类目已关联产品，不能添加子类目！");
                    return result;
                }
            }

            // KolAttributeType attributeType = attributeTypeService.getById(kolCategory.getCategoryTypeId());
            // if (attributeType == null) {
            //     result.error500("分类类型不存在!");
            //     return result;
            // }


            List<KolCategoryAttributeRelation> dimensionCategoryList = categoryRelationService.lambdaQuery().eq(KolCategoryAttributeRelation::getAttributeCategoryId, parentId).list();
            List<KolCategoryAttributeRelation> list = categoryRelationService.lambdaQuery().eq(KolCategoryAttributeRelation::getCategoryId, parentId).list();
            if (!list.isEmpty() || !dimensionCategoryList.isEmpty()) {
                Set<String> ids = Stream.concat(dimensionCategoryList.stream().map(KolCategoryAttributeRelation::getId), list.stream().map(KolCategoryAttributeRelation::getId)).collect(Collectors.toSet());
                categoryRelationService.lambdaUpdate().in(KolCategoryAttributeRelation::getId, ids).set(KolCategoryAttributeRelation::getLevel, null).update();
            }

            // kolCategory.setCategoryTypeName(attributeType.getTypeName());
            kolCategoryService.addKolCategory(kolCategory);
            result.success(ADD_SUCCESS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500(OPERATE_FAIL);
        }
        return result;
    }

    /**
     * 编辑
     *
     * @param kolCategory
     * @return
     */
    @PutMapping(value = EDIT_URL)
    @Operation(summary = "网红标签类目-" + EDIT, description = "网红标签类目-" + EDIT)
    public Result<KolCategory> edit(@RequestBody KolCategory kolCategory) {
        Result<KolCategory> result = new Result<>();
        if (kolCategory.getParentId().equals(kolCategory.getId())) {
            result.error500("父类节点不能与自身相同！");
        } else {
            kolCategoryService.updateKolCategory(kolCategory);
            result.success(EDIT_SUCCESS);
        }
        return result;
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @DeleteMapping(value = DELETE_URL)
    @Operation(summary = "网红标签类目-" + DELETE_BY_ID, description = "网红标签类目-" + DELETE_BY_ID)
    public Result<KolCategory> delete(@RequestParam(name = KEY_ID, required = true) String id) {
        Result<KolCategory> result = new Result<>();
        KolCategory KolCategory = kolCategoryService.getById(id);
        if (KolCategory == null) {
            result.error500("未找到对应实体");
        } else {
            if (KolCategory.getIsHasChild() != 0) {
                result.error500("当前节点下有子节点，不能删除！");
                return result;
            } else {
                // 判断是否存在关联记录
                Integer count = Math.toIntExact(categoryRelationService.lambdaQuery().eq(KolCategoryAttributeRelation::getCategoryId, id).count());
                Integer dimensionCategoryCount = Math.toIntExact(categoryRelationService.lambdaQuery().eq(KolCategoryAttributeRelation::getAttributeCategoryId, id).count());
                Integer productCategoryCount = Math.toIntExact(productCategoryService.lambdaQuery().eq(KolProductCategory::getCategoryId, id).count());
                boolean flag = count != 0 || dimensionCategoryCount != 0 || productCategoryCount != 0;
                if (count != 0) {
                    result.error500("当前类目已关联社媒属性，不能删除！");
                }
                if (dimensionCategoryCount != 0) {
                    result.error500("当前类目已关联社媒属性，不能删除！");
                }
                if (productCategoryCount != 0) {
                    result.error500("当前类目已关联产品，不能删除！");
                }
                if (flag) {
                    return result;
                }
            }
            kolCategoryService.deleteByIds(id);
            result.success(DELETE_SUCCESS);
        }
        return result;
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping(value = DELETE_BATCH_URL)
    @Operation(summary = "网红标签类目-" + DELETE_BATCH, description = "网红标签类目-" + DELETE_BATCH)
    public Result<KolCategory> deleteBatch(@RequestParam(name = KEY_IDS, required = true) String ids) {
        Result<KolCategory> result = new Result<>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("操作失败，请检查传入参数是否正确！");
        } else {
            this.kolCategoryService.deleteByIds(ids);
            result.success(DELETE_SUCCESS);
        }
        return result;
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @GetMapping(value = QUERY_BY_ID_URL)
    @Operation(summary = "网红标签类目-" + QUERY_BY_ID, description = "网红标签类目-" + QUERY_BY_ID)
    public Result<KolCategory> queryById(@RequestParam(name = KEY_ID, required = true) String id) {
        Result<KolCategory> result = new Result<KolCategory>();
        KolCategory KolCategory = kolCategoryService.getById(id);
        if (KolCategory == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(KolCategory);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * 导出excel
     *
     * @param request
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, KolCategory kolCategory) {
        // Step.1 组装查询条件查询数据
        QueryWrapper<KolCategory> queryWrapper = QueryGenerator.initQueryWrapper(kolCategory, request.getParameterMap());
        List<KolCategory> pageList = kolCategoryService.list(queryWrapper);
        // Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        // 过滤选中数据
        String selections = request.getParameter("selections");
        if (oConvertUtils.isEmpty(selections)) {
            mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        } else {
            List<String> selectionList = Arrays.asList(selections.split(","));
            List<KolCategory> exportList = pageList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
            mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
        }
        // 导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "分类字典列表");
        mv.addObject(NormalExcelConstants.CLASS, KolCategory.class);
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("分类字典列表数据", "导出人:" + user.getRealname(), "导出信息"));
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
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<KolCategory> listKolTagCategorys = ExcelImportUtil.importExcel(file.getInputStream(), KolCategory.class, params);
                for (KolCategory KolTagCategoryExcel : listKolTagCategorys) {
                    kolCategoryService.save(KolTagCategoryExcel);
                }
                return Result.ok("文件导入成功！数据行数：" + listKolTagCategorys.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败：" + e.getMessage());
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
     * 加载单个数据 用于回显
     */
    @RequestMapping(value = "/loadOne", method = RequestMethod.GET)
    public Result<KolCategory> loadOne(@RequestParam(name = "field") String field, @RequestParam(name = "val") String val) {
        Result<KolCategory> result = new Result<KolCategory>();
        try {

            QueryWrapper<KolCategory> query = new QueryWrapper<KolCategory>();
            query.eq(field, val);
            List<KolCategory> ls = this.kolCategoryService.list(query);
            if (ls == null || ls.size() == 0) {
                result.setMessage("查询无果");
                result.setSuccess(false);
            } else if (ls.size() > 1) {
                result.setMessage("查询数据异常,[" + field + "]存在多个值:" + val);
                result.setSuccess(false);
            } else {
                result.setSuccess(true);
                result.setResult(ls.get(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage(e.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 加载节点的子数据
     */
    @RequestMapping(value = "/loadTreeChildren", method = RequestMethod.GET)
    public Result<List<TreeSelectModel>> loadTreeChildren(@RequestParam(name = "pid") String pid) {
        Result<List<TreeSelectModel>> result = new Result<List<TreeSelectModel>>();
        try {
            List<TreeSelectModel> ls = this.kolCategoryService.queryListByParentId(pid);
            result.setResult(ls);
            result.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage(e.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 递归求子节点 同步加载用到
     */
    private void loadAllCategoryChildren(List<TreeSelectModel> ls) {
        for (TreeSelectModel tsm : ls) {
            List<TreeSelectModel> temp = this.kolCategoryService.queryListByParentId(tsm.getKey());
            if (temp != null && temp.size() > 0) {
                tsm.setChildren(temp);
                loadAllCategoryChildren(temp);
            }
        }
    }

    /**
     * 分类字典树控件 加载节点
     *
     * @param parentId
     * @param condition
     * @return
     */
    @RequestMapping(value = "/loadTreeData", method = RequestMethod.GET)
    public Result<List<TreeSelectModel>> loadDict(@RequestParam(name = "parentId", required = false) String parentId, @RequestParam(name = "condition", required = false) String condition) {
        Result<List<TreeSelectModel>> result = new Result<>();
        if (oConvertUtils.isEmpty(parentId)) {
            result.setSuccess(false);
            result.setMessage("加载分类字典树参数有误.[null]!");
            return result;

        }
        Map<String, String> query = null;
        if (oConvertUtils.isNotEmpty(condition)) {
            query = JSON.parseObject(condition, Map.class);
        }
        List<TreeSelectModel> ls = kolCategoryService.queryListByParentId(parentId, query);
        result.setSuccess(true);
        result.setResult(ls);
        return result;
    }

    /**
     * 分类字典控件数据回显[表单页面]
     *
     * @return
     */
    @RequestMapping(value = "/loadDictItem", method = RequestMethod.GET)
    public Result<List<String>> loadDictItem(@RequestParam(name = KEY_IDS) String ids) {
        Result<List<String>> result = new Result<>();
        LambdaQueryWrapper<KolCategory> query = new LambdaQueryWrapper<KolCategory>().in(KolCategory::getId, ids);
        List<KolCategory> list = this.kolCategoryService.list(query);
        List<String> textList = new ArrayList<>();
        for (String id : ids.split(SPLIT_CHAR_COMMA)) {
            for (KolCategory c : list) {
                if (id.equals(c.getId())) {
                    textList.add(c.getCategoryName());
                    break;
                }
            }
        }
        result.setSuccess(true);
        result.setResult(textList);
        return result;
    }

    /**
     * [列表页面]加载分类字典数据 用于值的替换
     *
     * @param code
     * @return
     */
    @RequestMapping(value = "/loadAllData", method = RequestMethod.GET)
    public Result<List<DictModel>> loadAllData(@RequestParam(name = "code", required = true) String code) {
        Result<List<DictModel>> result = new Result<List<DictModel>>();
        LambdaQueryWrapper<KolCategory> query = new LambdaQueryWrapper<KolCategory>();
        if (oConvertUtils.isNotEmpty(code) && !"0".equals(code)) {
            query.likeRight(KolCategory::getCategoryCode, code);
        }
        List<KolCategory> list = this.kolCategoryService.list(query);
        if (list == null || list.size() == 0) {
            result.setMessage("无数据,参数有误.[code]");
            result.setSuccess(false);
            return result;
        }
        List<DictModel> rdList = new ArrayList<DictModel>();
        for (KolCategory c : list) {
            rdList.add(new DictModel(c.getId(), c.getCategoryName(), ""));
        }
        result.setSuccess(true);
        result.setResult(rdList);
        return result;
    }

    /**
     * 网红标签类目树
     */
    @Operation(summary = "网红标签类目-加载类目树", description = "网红标签类目-加载类目树")
    @RequestMapping(value = "/loadTreeDataAll", method = RequestMethod.GET)
    public Result<?> loadTreeDataAll() {
        // 2025年5月13日15:36:26 增加按照类目类型查询
/*        KolAttributeType kolAttributeType = attributeTypeService.lambdaQuery().eq(KolAttributeType::getTypeCode, typeCode).one();
        if (kolAttributeType == null) {
            return Result.error("类目类型不存在!");
        }
        String typeId = kolAttributeType.getId();*/
        try {
            // 从数据库中获取所有未删除的标签类目，并按照排序码降序排列
            List<KolCategory> tagCategories = kolCategoryService.lambdaQuery().eq(KolCategory::getIsDelete, 0).orderByDesc(KolCategory::getSortCode).list();

            // 检查查询结果是否为空
            if (tagCategories == null || tagCategories.isEmpty()) {
                return Result.ok(Collections.emptyList());
            }

            // 查询所有关联关系
            List<PersonalTagsModel> personalTagsModels = sysBaseAPI.getPersonalTagsCategory();

            // 将实体类转换为树形模型
            List<KolTagCategoryTreeModel> tagCategoryTreeModels = convertToTreeModels(tagCategories, personalTagsModels);

            // 构建树形结构
            Map<String, KolTagCategoryTreeModel> treeModelMap = buildTree(tagCategoryTreeModels);
            Collection<KolTagCategoryTreeModel> values = treeModelMap.values();
            List<KolTagCategoryTreeModel> collect = values.stream().sorted(Comparator.comparing(KolTagCategoryTreeModel::getSortCode)).collect(Collectors.toList());
            // 返回树形结构的值集合
            return Result.ok(collect);
        } catch (Exception e) {
            log.error("加载网红标签类目树失败", e);
            return Result.error("加载类目树失败：" + e.getMessage());
        }
    }

    /**
     * 将实体类列表转换为树形模型列表
     *
     * @param tagCategories 标签类目实体列表
     * @return 树形模型列表
     */
    private List<KolTagCategoryTreeModel> convertToTreeModels(List<KolCategory> tagCategories, List<PersonalTagsModel> personalTagsModels) {
        List<KolTagCategoryTreeModel> treeModels = new ArrayList<>(tagCategories.size());

        for (KolCategory item : tagCategories) {
            KolTagCategoryTreeModel treeModel = new KolTagCategoryTreeModel();
            BeanUtils.copyProperties(item, treeModel);
            List<PersonalTagsModel> tagsModels = personalTagsModels.stream().filter(x -> x.getCategoryId().equals(item.getId())).collect(Collectors.toList());
            treeModel.setSize(tagsModels.size());
            treeModel.setPersonalTags(tagsModels);
            treeModels.add(treeModel);
        }
        treeModels.sort(Comparator.comparing(KolTagCategoryTreeModel::getSortCode));
        return treeModels;
    }

    /**
     * 构建标签类目树
     *
     * @param categoryList 类目列表
     * @return 树形结构Map，键为类目ID，值为树节点
     */
    public Map<String, KolTagCategoryTreeModel> buildTree(List<KolTagCategoryTreeModel> categoryList) {
        if (categoryList == null || categoryList.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, KolTagCategoryTreeModel> categoryMap = new HashMap<>();
        Map<String, KolTagCategoryTreeModel> treeMap = new HashMap<>();

        try {
            // 1. 将所有节点放入Map中，方便后续查找
            for (KolTagCategoryTreeModel category : categoryList) {
                // 初始化children列表
                category.setChildren(new ArrayList<>());
                // 使用类目ID作为键存储类目对象
                if (category.getId() != null) {
                    categoryMap.put(category.getId(), category);
                }
            }

            // 2. 找到所有根节点并开始递归构建树形结构
            for (KolTagCategoryTreeModel category : categoryList) {
                // 判断当前节点是否为根节点（父ID为空或父节点不存在）
                if (category.getId() != null && (category.getParentId() == null || !categoryMap.containsKey(category.getParentId()))) {
                    treeMap.put(category.getId(), buildChildTree(category, categoryMap));
                }
            }

            // 3. 遍历构建好的树，计算父节点的 size
            for (KolTagCategoryTreeModel root : treeMap.values()) {
                calculateParentSize(root);
            }
        } catch (Exception e) {
            log.error("构建类目树失败", e);
            throw new RuntimeException("构建类目树失败: " + e.getMessage(), e);
        }

        return treeMap;
    }

    /**
     * 递归构建子树
     *
     * @param parent      父节点
     * @param categoryMap 类目Map
     * @return 构建好子树的父节点
     */
    private static KolTagCategoryTreeModel buildChildTree(KolTagCategoryTreeModel parent, Map<String, KolTagCategoryTreeModel> categoryMap) {
        if (parent == null || categoryMap == null || categoryMap.isEmpty()) {
            return parent;
        }

        List<KolTagCategoryTreeModel> children = new ArrayList<>();
        int totalSize = parent.getSize(); // 初始化当前节点的 size 为其直接关联的数量

        // 遍历类目Map，找出所有父ID等于当前节点ID的子节点
        for (Map.Entry<String, KolTagCategoryTreeModel> entry : categoryMap.entrySet()) {
            KolTagCategoryTreeModel currentCategory = entry.getValue();
            if (currentCategory != null && currentCategory.getParentId() != null && parent.getId() != null && currentCategory.getParentId().equals(parent.getId())) {
                // 递归构建子树
                KolTagCategoryTreeModel childNode = buildChildTree(currentCategory, categoryMap);
                children.add(childNode);
                totalSize += childNode.getSize(); // 累加子节点的 size
            }
        }

        // 设置子节点列表
        parent.setChildren(children);
        parent.setSize(totalSize);
        return parent;
    }

    /**
     * 递归计算父节点的 size (在树构建完成后调用)
     *
     * @param node 当前节点
     * @return 当前节点的 size
     */
    private static int calculateParentSize(KolTagCategoryTreeModel node) {
        int totalSize = node.getSize();
        if (node.getChildren() != null && !node.getChildren().isEmpty()) {
            totalSize = 0; // 重置当前节点的 size，以便累加子节点的 size
            for (KolTagCategoryTreeModel child : node.getChildren()) {
                totalSize += calculateParentSize(child);
            }
            node.setSize(totalSize);
        }
        return totalSize;
    }


    @GetMapping(value = "/getKolCategory")
    @Operation(summary = "网红标签类目-类目关联其他", description = "网红标签类目-类目关联其他")
    public Result<?> getKolCategory(@RequestParam(name = "type", required = false) Integer type) {
        List<KolCategory> list = kolCategoryService.lambdaQuery()
                .eq(KolCategory::getIsHasChild, 0)
                .orderByAsc(KolCategory::getSortCode, KolCategory::getCategoryCodePath)
                .list();
        for (KolCategory kolCategory : list) {
            String[] split = kolCategory.getCategoryNamePath().split("\\|");
            Collections.reverse(Arrays.asList(split)); // 倒序
            kolCategory.setCategoryNamePath(String.join("|", split));
        }
        return Result.ok(list);
    }

    @GetMapping(value = "/queryCategoryAttributes")
    @Operation(summary = "网红标签类目-类目关联其他", description = "网红标签类目-类目关联其他")
    public Result<?> queryCategoryAttributes(@RequestParam(name = "categoryId", required = true) String categoryId) {
        try {
            if (oConvertUtils.isEmpty(categoryId)) {
                return Result.error("类目ID不能为空");
            }

            List<String> attributeIds = categoryRelationService.queryCategoryAttributes(categoryId);

            return Result.ok(attributeIds);

        } catch (Exception e) {
            log.error("批量查询产品属性失败, productId: {}", categoryId, e);
            return Result.error("查询失败，请稍后重试");
        }
    }
}
