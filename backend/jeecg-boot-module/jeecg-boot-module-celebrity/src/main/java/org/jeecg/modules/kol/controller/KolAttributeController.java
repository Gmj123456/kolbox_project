package org.jeecg.modules.kol.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.SheetConstants;
import org.jeecg.common.constant.SystemConstants;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.PersonalTagsModel;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.kol.entity.*;
import org.jeecg.modules.feishu.service.IFeishuService;
import org.jeecg.modules.kol.model.KolAttributeTreeModel;
import org.jeecg.modules.kol.model.KolTagCategoryTreeModel;
import org.jeecg.modules.kol.service.*;
import org.jeecg.modules.kol.wechatexcel.config.WechatDocConfig;
import org.jeecg.modules.kol.wechatexcel.model.Record;
import org.jeecg.modules.kol.wechatexcel.service.WechatService;
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
import org.jeecg.modules.kol.model.TreeSelectModel;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.jeecg.common.constant.SystemConstants.*;

/**
 * @Description: 网红社媒属性表
 * @Author: xyc
 * @Date: 2024年11月22日 18:23:04
 * @Version: V1.0
 */
@Slf4j
@Tag(name =  "网红社媒属性")
@RestController
@RequestMapping("/kol/attribute")
public class KolAttributeController extends JeecgController<KolAttribute, IKolAttributeService> {

    @Autowired
    private IKolAttributeService attributeService;
    @Autowired
    private ISysBaseAPI sysBaseAPI;
    @Autowired
    private IKolAttributeTypeService attributeTypeService;
    @Autowired
    private IKolCategoryAttributeRelationService categoryRelationService;
    @Autowired
    private IKolCategoryService categoryService;
    @Resource
    private WechatService wechatService;
    @Resource
    private WechatDocConfig wechatDocConfig;
    @Resource
    private IFeishuService feishuService;

    /**
     * 分页列表查询
     *
     * @param kolAttribute
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @GetMapping(value = "/rootList")
@Operation(summary = "网红社媒属性-" + PAGE_LIST_QUERY, description = "网红社媒属性-" + PAGE_LIST_QUERY)
    public Result<IPage<KolAttributeTreeModel>> queryPageList(KolAttribute kolAttribute, @RequestParam(name = SystemConstants.PAGE_NO, defaultValue = SystemConstants.PAGE_NO_DEFAULT) Integer pageNo, @RequestParam(name = SystemConstants.PAGE_SIZE, defaultValue = SystemConstants.PAGE_SIZE_DEFAULT) Integer pageSize, HttpServletRequest req) {

        QueryWrapper<KolAttribute> queryWrapper = QueryGenerator.initQueryWrapper(kolAttribute, req.getParameterMap());

        IPage<KolAttributeTreeModel> pageList;
        String attributeTypeId = kolAttribute.getAttributeTypeId();
        String attributeName = kolAttribute.getAttributeName();
        if (oConvertUtils.isNotEmpty(attributeName) && oConvertUtils.isNotEmpty(attributeTypeId)) {
            queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                    .eq(KolAttribute::getAttributeTypeId, attributeTypeId)
                    .and(x -> x.like(KolAttribute::getAttributeEnName, attributeName).or().like(KolAttribute::getAttributeName, attributeName));
            List<KolAttribute> list = attributeService.getTreeRootNodeList(queryWrapper);
            // 构建树形结构
            List<KolAttributeTreeModel> treeList = buildAttributeTree(list);
            pageList = new Page<>(pageNo, pageSize, treeList.size());
            // 手动分页
            int fromIndex = (pageNo - 1) * pageSize;
            int toIndex = Math.min(fromIndex + pageSize, treeList.size());
            pageList.setRecords(treeList.subList(fromIndex, toIndex));
        } else {
            // 如果前端未执行条件查询 则默认 加载所有根节点并构建树形结构
            queryWrapper.lambda().eq(KolAttribute::getParentId, ROOT_PARENT_ID_VALUE).orderByAsc(KolAttribute::getSortCode);
            IPage<KolAttribute> attributePage = attributeService.page(new Page<>(pageNo, pageSize), queryWrapper);
            
            // 获取当前页的所有根节点ID
            List<String> rootIds = attributePage.getRecords().stream().map(KolAttribute::getId).collect(Collectors.toList());
            
            if (!rootIds.isEmpty()) {
                // 查询这些根节点及其所有子孙节点
                List<KolAttribute> allNodes = new ArrayList<>();
                for (String rootId : rootIds) {
                    // 获取当前根节点的所有子孙节点
                    List<KolAttribute> descendants = getAllDescendants(rootId);
                    allNodes.addAll(descendants);
                }
                // 添加根节点本身
                allNodes.addAll(attributePage.getRecords());
                
                // 去重
                allNodes = allNodes.stream().distinct().collect(Collectors.toList());
                
                // 构建树形结构
                List<KolAttributeTreeModel> treeList = buildAttributeTree(allNodes);
                pageList = new Page<>(pageNo, pageSize, attributePage.getTotal());
                pageList.setRecords(treeList);
            } else {
                pageList = new Page<>(pageNo, pageSize, 0);
                pageList.setRecords(new ArrayList<>());
            }
        }
        Result<IPage<KolAttributeTreeModel>> result = new Result<>();
        result.setSuccess(true);
        result.setResult(pageList);
        return result;

        /*
         * if(oConvertUtils.isEmpty(kolAttribute.getParentId())){
         * kolAttribute.setParentId(YesNoStatus.NO.getCodeString());
         * }
         * Result<IPage<KolAttribute>> result = new Result<>();
         *
         * //--author:os_chengtgen---date:20190804 -----for:
         * 分类字典页面显示错误,issues:377--------start
         * //QueryWrapper<KolAttribute> queryWrapper =
         * QueryGenerator.initQueryWrapper(KolAttribute, req.getParameterMap());
         * LambdaQueryWrapper<KolAttribute> queryWrapper = new LambdaQueryWrapper<>();
         * queryWrapper.eq(KolAttribute::getParentId, kolAttribute.getParentId());
         * //--author:os_chengtgen---date:20190804 -----for:
         * 分类字典页面显示错误,issues:377--------end
         *
         * Page<KolAttribute> page = new Page<>(pageNo, pageSize);
         * IPage<KolAttribute> pageList = kolAttributeService.page(page,
         * queryWrapper);
         * result.setSuccess(true);
         * result.setResult(pageList);
         * return result;
         */
    }
    
    /**
     * 递归获取所有子孙节点
     * @param parentId 父节点ID
     * @return 所有子孙节点列表
     */
    private List<KolAttribute> getAllDescendants(String parentId) {
        List<KolAttribute> descendants = new ArrayList<>();
        // 查询直接子节点
        List<KolAttribute> children = attributeService.lambdaQuery()
                .eq(KolAttribute::getParentId, parentId)
                .list();
        
        // 添加直接子节点
        descendants.addAll(children);
        
        // 递归获取子节点的子节点
        for (KolAttribute child : children) {
            descendants.addAll(getAllDescendants(child.getId()));
        }
        
        return descendants;
    }
    
    /**
     * 构建属性树形结构
     * @param allNodes 所有节点
     * @return 树形结构列表
     */
    private List<KolAttributeTreeModel> buildAttributeTree(List<KolAttribute> allNodes) {
        // 创建Map存储所有节点，key为id，value为对应的树节点
        Map<String, KolAttributeTreeModel> nodeMap = new HashMap<>();
        
        // 先将所有数据转换为树节点并存入map
        for (KolAttribute attribute : allNodes) {
            KolAttributeTreeModel treeModel = new KolAttributeTreeModel();
            BeanUtils.copyProperties(attribute, treeModel);
            treeModel.setChildren(new ArrayList<>());
            nodeMap.put(attribute.getId(), treeModel);
        }
        
        // 构建父子关系
        List<KolAttributeTreeModel> rootNodes = new ArrayList<>();
        for (KolAttribute attribute : allNodes) {
            KolAttributeTreeModel treeModel = nodeMap.get(attribute.getId());
            String parentId = attribute.getParentId();
            
            if (parentId != null && !ROOT_PARENT_ID_VALUE.equals(parentId)) {
                // 如果有父节点，则将当前节点添加到父节点的children中
                KolAttributeTreeModel parentModel = nodeMap.get(parentId);
                if (parentModel != null) {
                    parentModel.getChildren().add(treeModel);
                } else {
                    // 如果找不到父节点，则作为根节点处理
                    rootNodes.add(treeModel);
                }
            } else {
                // 如果没有父节点或者父节点为根节点，则作为根节点处理
                rootNodes.add(treeModel);
            }
        }
        
        // 对根节点按sortCode排序
        rootNodes.sort(Comparator.comparing(KolAttributeTreeModel::getSortCode, Comparator.nullsLast(Integer::compareTo)));
        
        // 对每个节点的子节点按sortCode排序
        sortChildren(rootNodes);
        
        return rootNodes;
    }
    
    /**
     * 递归对子节点按sortCode排序
     * @param nodes 节点列表
     */
    private void sortChildren(List<KolAttributeTreeModel> nodes) {
        for (KolAttributeTreeModel node : nodes) {
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                // 对子节点排序
                node.getChildren().sort(Comparator.comparing(KolAttributeTreeModel::getSortCode, Comparator.nullsLast(Integer::compareTo)));
                // 递归处理子节点的子节点
                sortChildren(node.getChildren());
            }
        }
    }

    @GetMapping(value = "/childList")
@Operation(summary = "网红社媒属性-获取子节点列表", description = "网红社媒属性-获取子节点列表")
    public Result<List<KolAttribute>> queryChildList(KolAttribute kolAttribute, HttpServletRequest req) {
        Result<List<KolAttribute>> result = new Result<>();
        LambdaQueryWrapper<KolAttribute> queryWrapper = QueryGenerator.initQueryWrapper(kolAttribute, req.getParameterMap()).lambda();
        queryWrapper.orderByAsc(KolAttribute::getSortCode);
        List<KolAttribute> list = attributeService.list(queryWrapper);
        result.setSuccess(true);
        result.setResult(list);
        return result;
    }

    /**
     * 添加
     *
     * @param kolAttribute
     * @return
     */
    @PostMapping(value = ADD_URL)
@Operation(summary = "网红社媒属性-" + ADD, description = "网红社媒属性-" + ADD)
    public Result<KolAttribute> add(@RequestBody KolAttribute kolAttribute) {
        Result<KolAttribute> result = new Result<>();
        String categoryName = kolAttribute.getAttributeName().trim();
        if (categoryName.contains("|")) {
            return result.error500("属性名称不能包含'|'符号");
        }
        String categoryEnName = kolAttribute.getAttributeEnName();
        if (oConvertUtils.isNotEmpty(categoryEnName) && categoryEnName.contains("|")) {
            return result.error500("分类英文名称不能包含'|'符号");
        }
        LambdaQueryWrapper<KolAttribute> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(KolAttribute::getAttributeName, categoryName);
        if (oConvertUtils.isNotEmpty(categoryEnName)) {
            queryWrapper.or().eq(KolAttribute::getAttributeEnName, categoryEnName);
        }
        long count = attributeService.count(queryWrapper);
        if (count > 0) {
            result.error500("属性名称已存在!");
            return result;
        }
        // 如果排序字段为空，默认设置为 1 2025年8月4日10:19:07 nqr
        kolAttribute.setSortCode(oConvertUtils.isEmpty(kolAttribute.getSortCode()) ? 1 : kolAttribute.getSortCode());
        try {
            KolAttributeType categoryType = attributeTypeService.getById(kolAttribute.getAttributeTypeId());
            if (categoryType == null) {
                result.error500("分类类型不存在!");
                return result;
            }
            // 查询父类是否设置关联关系
            String parentId = kolAttribute.getParentId();

            List<KolCategoryAttributeRelation> dimensionCategoryList = categoryRelationService.lambdaQuery().eq(KolCategoryAttributeRelation::getAttributeCategoryId, parentId).list();
            List<KolCategoryAttributeRelation> list = categoryRelationService.lambdaQuery().eq(KolCategoryAttributeRelation::getCategoryId, parentId).list();
            if (!list.isEmpty() || !dimensionCategoryList.isEmpty()) {
                Set<String> ids = Stream.concat(dimensionCategoryList.stream().map(KolCategoryAttributeRelation::getId), list.stream().map(KolCategoryAttributeRelation::getId)).collect(Collectors.toSet());
                categoryRelationService.lambdaUpdate().in(KolCategoryAttributeRelation::getId, ids).set(KolCategoryAttributeRelation::getLevel, null).update();
            }

            kolAttribute.setAttributeTypeName(categoryType.getTypeName());
            attributeService.addKolAttribute(kolAttribute);
            // 异步更新企微文档,只有父级添加
            if (kolAttribute.getParentId().equals(ROOT_PARENT_ID_VALUE)) {
                // asyncUpdateLabels(1, kolAttribute);
                asyncFeishuUpdateLabels(0);
            }
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
     * @param kolAttribute
     * @return
     */
    @PutMapping(value = EDIT_URL)
@Operation(summary = "网红社媒属性-" + EDIT, description = "网红社媒属性-" + EDIT)
    public Result<KolAttribute> edit(@RequestBody KolAttribute kolAttribute) {
        Result<KolAttribute> result = new Result<>();
        if (kolAttribute.getParentId().equals(kolAttribute.getId())) {
            result.error500("父类节点不能与自身相同！");
        } else {
            attributeService.updateKolAttribute(kolAttribute);
            KolAttribute attribute = attributeService.getById(kolAttribute.getId());
            if (attribute.getParentId().equals(ROOT_PARENT_ID_VALUE)) {
                // asyncUpdateLabels(2, attribute);
                asyncFeishuUpdateLabels(0);
            }
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
@Operation(summary = "网红社媒属性-" + DELETE_BY_ID, description = "网红社媒属性-" + DELETE_BY_ID)
    public Result<KolAttribute> delete(@RequestParam(name = KEY_ID, required = true) String id) {
        Result<KolAttribute> result = new Result<>();
        KolAttribute KolAttribute = attributeService.getById(id);
        if (KolAttribute == null) {
            result.error500("未找到对应实体");
        } else {
            if (KolAttribute.getIsHasChild() != 0) {
                result.error500("当前节点下有子节点，不能删除！");
                return result;
            } else {
                // 判断是否存在关联记录
                Integer count = Math.toIntExact(categoryRelationService.lambdaQuery().eq(KolCategoryAttributeRelation::getCategoryId, id).count());
                Integer attributeCount = Math.toIntExact(categoryRelationService.lambdaQuery().eq(KolCategoryAttributeRelation::getAttributeCategoryId, id).count());
                if (count != 0 || attributeCount != 0) {
                    result.error500("当前节点下存在关联记录，不能删除！");
                    return result;
                }
            }
            KolAttribute attribute = attributeService.getById(id);
            attributeService.deleteByIds(id);
            result.success(DELETE_SUCCESS);
            if (attribute.getParentId().equals(ROOT_PARENT_ID_VALUE)) {
                // asyncUpdateLabels(0, attribute);
                asyncFeishuUpdateLabels(1);
            }
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
@Operation(summary = "网红社媒属性-" + DELETE_BATCH, description = "网红社媒属性-" + DELETE_BATCH)
    public Result<KolAttribute> deleteBatch(@RequestParam(name = KEY_IDS, required = true) String ids) {
        Result<KolAttribute> result = new Result<>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("操作失败，请检查传入参数是否正确！");
        } else {
            this.attributeService.deleteByIds(ids);
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
@Operation(summary = "网红社媒属性-" + QUERY_BY_ID, description = "网红社媒属性-" + QUERY_BY_ID)
    public Result<KolAttribute> queryById(@RequestParam(name = KEY_ID, required = true) String id) {
        Result<KolAttribute> result = new Result<KolAttribute>();
        KolAttribute KolAttribute = attributeService.getById(id);
        if (KolAttribute == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(KolAttribute);
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
    public ModelAndView exportXls(HttpServletRequest request, KolAttribute kolAttribute) {
        // Step.1 组装查询条件查询数据
        QueryWrapper<KolAttribute> queryWrapper = QueryGenerator.initQueryWrapper(kolAttribute, request.getParameterMap());
        List<KolAttribute> pageList = attributeService.list(queryWrapper);
        // Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        // 过滤选中数据
        String selections = request.getParameter("selections");
        if (oConvertUtils.isEmpty(selections)) {
            mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        } else {
            List<String> selectionList = Arrays.asList(selections.split(","));
            List<KolAttribute> exportList = pageList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
            mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
        }
        // 导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "分类字典列表");
        mv.addObject(NormalExcelConstants.CLASS, KolAttribute.class);
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
                List<KolAttribute> listKolTagCategorys = ExcelImportUtil.importExcel(file.getInputStream(), KolAttribute.class, params);
                for (KolAttribute KolTagCategoryExcel : listKolTagCategorys) {
                    attributeService.save(KolTagCategoryExcel);
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
    public Result<KolAttribute> loadOne(@RequestParam(name = "field") String field, @RequestParam(name = "val") String val) {
        Result<KolAttribute> result = new Result<KolAttribute>();
        try {

            QueryWrapper<KolAttribute> query = new QueryWrapper<KolAttribute>();
            query.eq(field, val);
            List<KolAttribute> ls = this.attributeService.list(query);
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
            List<TreeSelectModel> ls = this.attributeService.queryListByParentId(pid);
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
            List<TreeSelectModel> temp = this.attributeService.queryListByParentId(tsm.getKey());
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
        List<TreeSelectModel> ls = attributeService.queryListByParentId(parentId, query);
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
        LambdaQueryWrapper<KolAttribute> query = new LambdaQueryWrapper<KolAttribute>().in(KolAttribute::getId, ids);
        List<KolAttribute> list = this.attributeService.list(query);
        List<String> textList = new ArrayList<>();
        for (String id : ids.split(SPLIT_CHAR_COMMA)) {
            for (KolAttribute c : list) {
                if (id.equals(c.getId())) {
                    textList.add(c.getAttributeName());
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
        LambdaQueryWrapper<KolAttribute> query = new LambdaQueryWrapper<KolAttribute>();
        if (oConvertUtils.isNotEmpty(code) && !"0".equals(code)) {
            query.likeRight(KolAttribute::getAttributeCode, code);
        }
        List<KolAttribute> list = this.attributeService.list(query);
        if (list == null || list.size() == 0) {
            result.setMessage("无数据,参数有误.[code]");
            result.setSuccess(false);
            return result;
        }
        List<DictModel> rdList = new ArrayList<DictModel>();
        for (KolAttribute c : list) {
            rdList.add(new DictModel(c.getId(), c.getAttributeName(), ""));
        }
        result.setSuccess(true);
        result.setResult(rdList);
        return result;
    }

    /**
     * 网红标签类目树
     */
@Operation(summary = "网红社媒属性-加载类目树", description = "网红社媒属性-加载类目树")
    @RequestMapping(value = "/loadTreeDataAll", method = RequestMethod.GET)
    public Result<?> loadTreeDataAll(@RequestParam(name = "typeCode", required = false) String typeCode) {
        // 2025年5月13日15:36:26 增加按照类目类型查询
        String typeId = "";
        if (oConvertUtils.isNotEmpty(typeCode)) {

            KolAttributeType kolAttributeType = attributeTypeService.lambdaQuery().eq(KolAttributeType::getTypeCode, typeCode).eq(KolAttributeType::getDelFlag, 0).one();
            if (kolAttributeType == null) {
                return Result.error("类目类型不存在!");
            }
            typeId = kolAttributeType.getId();
        }
        try {
            List<String> typeIds = new ArrayList<>();
            if (oConvertUtils.isEmpty(typeId)) {
                List<KolAttributeType> attributeTypes = attributeTypeService.lambdaQuery().eq(KolAttributeType::getDelFlag, 0).list();
                typeIds = attributeTypes.stream().map(KolAttributeType::getId).collect(Collectors.toList());
            }
            // 从数据库中获取所有未删除的标签类目，并按照排序码降序排列
            LambdaQueryChainWrapper<KolAttribute> lambdaedQuery = attributeService.lambdaQuery();
            if (oConvertUtils.isNotEmpty(typeId)) {
                lambdaedQuery.eq(KolAttribute::getAttributeTypeId, typeId);
            } else {
                lambdaedQuery.in(KolAttribute::getAttributeTypeId, typeIds);
            }
            lambdaedQuery.eq(KolAttribute::getIsDelete, 0);
            lambdaedQuery.orderByAsc(KolAttribute::getSortCode).list();
            List<KolAttribute> tagCategories = lambdaedQuery.list();

            // 检查查询结果是否为空
            if (tagCategories == null || tagCategories.isEmpty()) {
                return Result.ok(Collections.emptyList());
            }

            // 查询所有关联关系
            List<PersonalTagsModel> personalTagsModels = sysBaseAPI.getPersonalTagsCategory();

            // 将实体类转换为树形模型
            List<KolTagCategoryTreeModel> tagCategoryTreeModels = convertToTreeModels(tagCategories, personalTagsModels);
            Map<String, List<KolTagCategoryTreeModel>> collect = tagCategoryTreeModels.stream().collect(Collectors.groupingBy(x -> x.getAttributeTypeId() + "_" + x.getAttributeTypeName()));
            List<Map<String, Object>> categoryList = new ArrayList<>();
            collect.forEach((k, v) -> {
                Map<String, Object> treeModelMap = new HashMap<>();
                Map<String, KolTagCategoryTreeModel> data = buildTree(v);
                treeModelMap.put("typeId", k.split("_")[0]);
                treeModelMap.put("typeName", k.split("_")[1]);
                Collection<KolTagCategoryTreeModel> values = data.values();
                List<KolTagCategoryTreeModel> models = values.stream().sorted(Comparator.comparing(KolTagCategoryTreeModel::getSortCode)).collect(Collectors.toList());
                models.forEach(x -> {
                    List<KolTagCategoryTreeModel> children = x.getChildren();
                    children.sort(Comparator.comparing(KolTagCategoryTreeModel::getSortCode));
                    x.setChildren(children);
                });
                treeModelMap.put("data", models);
                categoryList.add(treeModelMap);
            });
            // 返回树形结构的值集合
            return Result.ok(categoryList);
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
    private List<KolTagCategoryTreeModel> convertToTreeModels(List<KolAttribute> tagCategories, List<PersonalTagsModel> personalTagsModels) {
        List<KolTagCategoryTreeModel> treeModels = new ArrayList<>(tagCategories.size());

        for (KolAttribute item : tagCategories) {
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


    @GetMapping(value = "/getKolAttribute")
@Operation(summary = "网红社媒属性-类目关联其他", description = "网红社媒属性-类目关联其他")
    public Result<?> getKolAttribute(@RequestParam(name = "type", required = false) Integer type,
                                     @RequestParam(name = "typeCode", required = false) String typeCode,
                                     @RequestParam(name = "isParent", required = false) Integer isParent) {

        List<Map<String, Object>> kolAttributeLeafNodes = attributeService.getKolAttributeLeafNodes(type, typeCode, isParent);
        return Result.ok(kolAttributeLeafNodes);
    }

    /**
     * 功能描述：获取社媒属性叶子节点
     *
     * @param type
     * @Param:
     * @Author: fengLiu
     * @Date: 2025-07-28 14:05
     */
    @GetMapping(value = "/getKolAttributeLeafNodes")
@Operation(summary = "网红社媒属性-类目关联其他", description = "网红社媒属性-类目关联其他")
    public Result<?> getKolAttributeLeafNodes(@RequestParam(name = "type", required = false) Integer type,
                                              @RequestParam(name = "typeCode", required = false) String typeCode,
                                              @RequestParam(name = "isParent", required = false) Integer isParent) {
        List<Map<String, Object>> kolAttributeLeafNodes = attributeService.getKolAttributeLeafNodes(type, typeCode, isParent);
        return Result.ok(kolAttributeLeafNodes);
    }


    /**
     * 通过类目id查询
     *
     * @param categoryId
     * @return
     */
    @GetMapping(value = "/queryByCategoryId")
@Operation(summary = "网红社媒属性-通过类目id查询", description = "网红社媒属性-通过类目id查询")
    public Result<?> queryByCategoryId(@RequestParam(name = "categoryId", required = true) String categoryId) {
        try {
            // 参数验证
            if (oConvertUtils.isEmpty(categoryId)) {
                return Result.error("类目ID不能为空");
            }

            QueryWrapper<KolCategory> categoryWrapper = new QueryWrapper<>();
            categoryWrapper.eq("id", categoryId);

            KolCategory category = categoryService.getOne(categoryWrapper);
            if (category == null) {
                return Result.error("类目不存在");
            }

            List<String> attributeParentIds = categoryService.queryAttributeParentIdsByCategoryId(categoryId);

            return Result.ok(attributeParentIds);

        } catch (Exception e) {
            log.error("批量查询类目属性父级ID失败, categoryId: {}", categoryId, e);
            return Result.error("查询失败，请稍后重试");
        }
    }


    /**
     * 通过产品id查询
     *
     * @param productId
     * @return
     */
    @GetMapping(value = "/queryByProductId")
@Operation(summary = "网红社媒属性-通过产品id查询", description = "网红社媒属性-通过产品id查询")
    public Result<?> queryByProductIdOptimized(@RequestParam(name = "productId", required = true) String productId,
                                               @RequestParam(name = "platformType", required = false) Integer platformType) {
        try {
            if (oConvertUtils.isEmpty(productId)) {
                return Result.error("产品ID不能为空");
            }

            List<String> attributeIds = attributeService.queryAttributeIdsByProductId(productId,platformType);

            return Result.ok(attributeIds);

        } catch (Exception e) {
            log.error("批量查询产品属性失败, productId: {}", productId, e);
            return Result.error("查询失败，请稍后重试");
        }
    }

    /**
     * 通过产品id查询
     *
     * @param productId
     * @return
     */
    @GetMapping(value = "/queryProductAttributes")
@Operation(summary = "网红社媒属性-通过产品id查询", description = "网红社媒属性-通过产品id查询")
    public Result<?> queryProductAttributes(@RequestParam(name = "productId", required = true) String productId) {
        try {
            if (oConvertUtils.isEmpty(productId)) {
                return Result.error("产品ID不能为空");
            }

            List<String> attributeIds = attributeService.queryProductAttributes(productId);

            return Result.ok(attributeIds);

        } catch (Exception e) {
            log.error("批量查询产品属性失败, productId: {}", productId, e);
            return Result.error("查询失败，请稍后重试");
        }
    }

    private void asyncUpdateLabels(int type, KolAttribute kolAttribute) {
        // 同步更新企微选项
        try {
            CompletableFuture.runAsync(() -> {
                // 获取当前操作
                switch (type) {
                    case 0:
                        // 删除记录
                        wechatService.removeRecords(wechatDocConfig.getTemplateDocId(), wechatDocConfig.getAttributeSheetId(), kolAttribute.getRecordId());
                        break;
                    case 1:
                        // 新增记录
                        List<Map<String, Object>> recordList = new ArrayList<Map<String, Object>>() {{
                            add(new HashMap<String, Object>() {{
                                put("values", new HashMap<String, Object>() {{
                                    put("达人类型", new ArrayList<Map<String, Object>>() {{
                                        add(new HashMap<String, Object>() {{
                                            put("type", "text");
                                            put("text", kolAttribute.getAttributeName());
                                        }});
                                    }});
                                }});
                            }});
                        }};
                        List<Record> records = wechatService.createRecords(wechatDocConfig.getTemplateDocId(), wechatDocConfig.getAttributeSheetId(), recordList);
                        // 更新数据库中对应标签的recordId
                        KolAttribute attribute = new KolAttribute();
                        attribute.setId(kolAttribute.getId());
                        attribute.setRecordId(records.get(0).getRecordId());
                        attributeService.updateById(attribute);
                        break;
                    case 2:
                        if (oConvertUtils.isEmpty(kolAttribute.getRecordId())) {
                            break;
                        }
                        // 编辑记录
                        List<Map<String, Object>> updateRecords = Collections.singletonList(new HashMap<String, Object>() {{
                            put("record_id", kolAttribute.getRecordId());
                            put("values", Collections.singletonMap("达人类型", Collections.singletonList(new HashMap<String, Object>() {{
                                put("text", kolAttribute.getAttributeName());
                                put("type", "text");
                            }})));
                        }});
                        wechatService.updateRecords(wechatDocConfig.getTemplateDocId(), wechatDocConfig.getAttributeSheetId(), updateRecords);
                        break;
                }
            });
        } catch (Exception e) {
            log.error("同步企微产品失败", e);
        }
    }

    private void asyncFeishuUpdateLabels(int type) {
        List<KolAttribute> attributeList = attributeService.lambdaQuery().eq(KolAttribute::getParentId, "0").list();
        List<String> attributeName = attributeList.stream().map(KolAttribute::getAttributeName).filter(oConvertUtils::isNotEmpty).collect(Collectors.toList());
        // 获取当前操作
        switch (type) {
            case 0:
                feishuService.insertOrUpdatePersonalTagsSheetBatch(0, SheetConstants.ATTRIBUTE, attributeName);
                break;
            case 1:
                // 删除记录
                feishuService.insertOrUpdatePersonalTagsSheetBatch(1, SheetConstants.ATTRIBUTE, attributeName);
                break;
        }
    }
}
