package org.jeecg.modules.kol.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.SystemConstants;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.kol.model.TreeSelectModel;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.PersonalTagsModel;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.kol.entity.KolAttributeType;
import org.jeecg.modules.kol.entity.KolTagCategory;
import org.jeecg.modules.kol.entity.KolTags;
import org.jeecg.modules.kol.model.KolTagCategoryTreeModel;
import org.jeecg.modules.kol.service.IKolAttributeTypeService;
import org.jeecg.modules.kol.service.IKolProductCategoryService;
import org.jeecg.modules.kol.service.IKolTagCategoryService;
import org.jeecg.modules.kol.service.IKolTagsService;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
@RequestMapping("/kol/tagCategory")
public class KolTagCategoryController extends JeecgController<KolTagCategory, IKolTagCategoryService> {

    @Autowired
    private IKolTagCategoryService kolTagCategoryService;
    @Autowired
    private ISysBaseAPI sysBaseAPI;
    @Autowired
    private IKolAttributeTypeService categoryTypeService;
    @Autowired
    private IKolProductCategoryService productCategoryService;
    @Autowired
    private IKolTagsService tagsService;

    /**
     * 分页列表查询
     *
     * @param kolTagCategory
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @GetMapping(value = "/rootList")
    @Operation(summary = "网红标签类目-" + PAGE_LIST_QUERY, description = "网红标签类目-" + PAGE_LIST_QUERY)
    public Result<IPage<KolTagCategory>> queryPageList(KolTagCategory kolTagCategory,
                                                       @RequestParam(name = SystemConstants.PAGE_NO, defaultValue = SystemConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                                       @RequestParam(name = SystemConstants.PAGE_SIZE, defaultValue = SystemConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
                                                       HttpServletRequest req) {

        QueryWrapper<KolTagCategory> queryWrapper = QueryGenerator.initQueryWrapper(kolTagCategory,
                req.getParameterMap());

        IPage<KolTagCategory> pageList;
        String categoryName = kolTagCategory.getCategoryName();
        if (oConvertUtils.isNotEmpty(categoryName)) {
            List<KolTagCategory> list = kolTagCategoryService.getTreeRootNodeList(queryWrapper);
            pageList = new Page<>(pageNo, pageSize, list.size());
            pageList.setRecords(list);
        } else {
            // 如果前端未执行条件查询 则默认 加载所有
            queryWrapper.lambda().eq(KolTagCategory::getParentId, ROOT_PARENT_ID_VALUE);
            pageList = kolTagCategoryService.page(new Page<>(pageNo, pageSize), queryWrapper);
        }
        Result<IPage<KolTagCategory>> result = new Result<>();
        result.setSuccess(true);
        result.setResult(pageList);
        return result;

        /*
         * if(oConvertUtils.isEmpty(kolTagCategory.getParentId())){
         * kolTagCategory.setParentId(YesNoStatus.NO.getCodeString());
         * }
         * Result<IPage<KolTagCategory>> result = new Result<>();
         *
         * //--author:os_chengtgen---date:20190804 -----for:
         * 分类字典页面显示错误,issues:377--------start
         * //QueryWrapper<KolTagCategory> queryWrapper =
         * QueryGenerator.initQueryWrapper(KolTagCategory, req.getParameterMap());
         * LambdaQueryWrapper<KolTagCategory> queryWrapper = new LambdaQueryWrapper<>();
         * queryWrapper.eq(KolTagCategory::getParentId, kolTagCategory.getParentId());
         * //--author:os_chengtgen---date:20190804 -----for:
         * 分类字典页面显示错误,issues:377--------end
         *
         * Page<KolTagCategory> page = new Page<>(pageNo, pageSize);
         * IPage<KolTagCategory> pageList = kolTagCategoryService.page(page,
         * queryWrapper);
         * result.setSuccess(true);
         * result.setResult(pageList);
         * return result;
         */
    }

    @GetMapping(value = "/childList")
    @Operation(summary = "网红标签类目-获取子节点列表", description = "网红标签类目-获取子节点列表")
    public Result<List<KolTagCategory>> queryPageList(KolTagCategory kolTagCategory, HttpServletRequest req) {
        Result<List<KolTagCategory>> result = new Result<>();
        QueryWrapper<KolTagCategory> queryWrapper = QueryGenerator.initQueryWrapper(kolTagCategory,
                req.getParameterMap());
        List<KolTagCategory> list = kolTagCategoryService.list(queryWrapper);
        result.setSuccess(true);
        result.setResult(list);
        return result;
    }

    /**
     * 添加
     *
     * @param kolTagCategory
     * @return
     */
    @PostMapping(value = ADD_URL)
    @Operation(summary = "网红标签类目-" + ADD, description = "网红标签类目-" + ADD)
    public Result<KolTagCategory> add(@RequestBody KolTagCategory kolTagCategory) {
        Result<KolTagCategory> result = new Result<>();
        String categoryName = kolTagCategory.getCategoryName().trim();
        KolTagCategory tagCategory = kolTagCategoryService.lambdaQuery().eq(KolTagCategory::getCategoryName, categoryName)
                .last("LIMIT 1").one();
        if (tagCategory != null) {
            result.error500("属性名称已存在!");
            return result;
        }
        try {
            // kolTagCategory.setCategoryTypeId("20250513000000");
            // KolCategoryType categoryType = categoryTypeService.getById(kolTagCategory.getCategoryTypeId());
            // if (categoryType == null) {
            //     result.error500("分类类型不存在!");
            //     return result;
            // }
            // kolTagCategory.setCategoryTypeName(categoryType.getTypeName());
            kolTagCategoryService.addKolTagCategory(kolTagCategory);
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
     * @param kolTagCategory
     * @return
     */
    @PutMapping(value = EDIT_URL)
    @Operation(summary = "网红标签类目-" + EDIT, description = "网红标签类目-" + EDIT)
    public Result<KolTagCategory> edit(@RequestBody KolTagCategory kolTagCategory) {
        Result<KolTagCategory> result = new Result<>();
        if (kolTagCategory.getParentId().equals(kolTagCategory.getId())) {
            result.error500("父类节点不能与自身相同！");
        } else {
            kolTagCategoryService.updateKolTagCategory(kolTagCategory);
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
    public Result<KolTagCategory> delete(@RequestParam(name = KEY_ID, required = true) String id) {
        Result<KolTagCategory> result = new Result<>();
        KolTagCategory KolTagCategory = kolTagCategoryService.getById(id);
        if (KolTagCategory == null) {
            result.error500("未找到对应实体");
        } else {
            List<KolTags> list = tagsService.lambdaQuery().eq(KolTags::getCategoryId, id).list();
            if (!list.isEmpty()) {
                result.error500("该分类下存在标签，不能删除！");
                return result;
            }
            if (KolTagCategory.getIsHasChild()==1) {
                result.error500("该分类下存在子分类，不能删除！");
                return result;
            }
            kolTagCategoryService.deleteByIds(id);
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
    public Result<KolTagCategory> deleteBatch(@RequestParam(name = KEY_IDS, required = true) String ids) {
        Result<KolTagCategory> result = new Result<>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("操作失败，请检查传入参数是否正确！");
        } else {
            this.kolTagCategoryService.deleteByIds(ids);
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
    public Result<KolTagCategory> queryById(@RequestParam(name = KEY_ID, required = true) String id) {
        Result<KolTagCategory> result = new Result<KolTagCategory>();
        KolTagCategory KolTagCategory = kolTagCategoryService.getById(id);
        if (KolTagCategory == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(KolTagCategory);
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
    public ModelAndView exportXls(HttpServletRequest request, KolTagCategory kolTagCategory) {
        // Step.1 组装查询条件查询数据
        QueryWrapper<KolTagCategory> queryWrapper = QueryGenerator.initQueryWrapper(kolTagCategory,
                request.getParameterMap());
        List<KolTagCategory> pageList = kolTagCategoryService.list(queryWrapper);
        // Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        // 过滤选中数据
        String selections = request.getParameter("selections");
        if (oConvertUtils.isEmpty(selections)) {
            mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        } else {
            List<String> selectionList = Arrays.asList(selections.split(","));
            List<KolTagCategory> exportList = pageList.stream().filter(item -> selectionList.contains(item.getId()))
                    .collect(Collectors.toList());
            mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
        }
        // 导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "分类字典列表");
        mv.addObject(NormalExcelConstants.CLASS, KolTagCategory.class);
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        mv.addObject(NormalExcelConstants.PARAMS,
                new ExportParams("分类字典列表数据", "导出人:" + user.getRealname(), "导出信息"));
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
                List<KolTagCategory> listKolTagCategorys = ExcelImportUtil.importExcel(file.getInputStream(),
                        KolTagCategory.class, params);
                for (KolTagCategory KolTagCategoryExcel : listKolTagCategorys) {
                    kolTagCategoryService.save(KolTagCategoryExcel);
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
    public Result<KolTagCategory> loadOne(@RequestParam(name = "field") String field,
                                          @RequestParam(name = "val") String val) {
        Result<KolTagCategory> result = new Result<KolTagCategory>();
        try {

            QueryWrapper<KolTagCategory> query = new QueryWrapper<KolTagCategory>();
            query.eq(field, val);
            List<KolTagCategory> ls = this.kolTagCategoryService.list(query);
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
            List<TreeSelectModel> ls = this.kolTagCategoryService.queryListByParentId(pid);
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
            List<TreeSelectModel> temp = this.kolTagCategoryService.queryListByParentId(tsm.getKey());
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
    public Result<List<TreeSelectModel>> loadDict(@RequestParam(name = "parentId", required = false) String parentId,
                                                  @RequestParam(name = "condition", required = false) String condition) {
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
        List<TreeSelectModel> ls = kolTagCategoryService.queryListByParentId(parentId, query);
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
        LambdaQueryWrapper<KolTagCategory> query = new LambdaQueryWrapper<KolTagCategory>().in(KolTagCategory::getId,
                ids);
        List<KolTagCategory> list = this.kolTagCategoryService.list(query);
        List<String> textList = new ArrayList<>();
        for (String id : ids.split(SPLIT_CHAR_COMMA)) {
            for (KolTagCategory c : list) {
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
        LambdaQueryWrapper<KolTagCategory> query = new LambdaQueryWrapper<KolTagCategory>();
        if (oConvertUtils.isNotEmpty(code) && !"0".equals(code)) {
            query.likeRight(KolTagCategory::getCategoryCode, code);
        }
        List<KolTagCategory> list = this.kolTagCategoryService.list(query);
        if (list == null || list.size() == 0) {
            result.setMessage("无数据,参数有误.[code]");
            result.setSuccess(false);
            return result;
        }
        List<DictModel> rdList = new ArrayList<DictModel>();
        for (KolTagCategory c : list) {
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
    public Result<?> loadTreeDataAll(@RequestParam(name = "typeCode", required = true) String typeCode) {
        // 2025年5月13日15:36:26 增加按照类目类型查询
        KolAttributeType kolAttributeType = categoryTypeService.lambdaQuery().eq(KolAttributeType::getTypeCode, typeCode).one();
        if (kolAttributeType == null) {
            return Result.error("类目类型不存在!");
        }
        String typeId = kolAttributeType.getId();
        try {
            // 从数据库中获取所有未删除的标签类目，并按照排序码降序排列
            List<KolTagCategory> tagCategories = kolTagCategoryService.lambdaQuery().eq(KolTagCategory::getCategoryTypeId, typeId).eq(KolTagCategory::getIsDelete, 0).orderByDesc(KolTagCategory::getSortCode).list();

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

            // 返回树形结构的值集合
            return Result.ok(treeModelMap.values());
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
    private List<KolTagCategoryTreeModel> convertToTreeModels(List<KolTagCategory> tagCategories, List<PersonalTagsModel> personalTagsModels) {
        List<KolTagCategoryTreeModel> treeModels = new ArrayList<>(tagCategories.size());

        for (KolTagCategory item : tagCategories) {
            KolTagCategoryTreeModel treeModel = new KolTagCategoryTreeModel();
            BeanUtils.copyProperties(item, treeModel);
            List<PersonalTagsModel> tagsModels = personalTagsModels.stream().filter(x -> x.getCategoryId().equals(item.getId())).collect(Collectors.toList());
            treeModel.setSize(tagsModels.size());
            treeModel.setPersonalTags(tagsModels);
            treeModels.add(treeModel);
        }
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
}
