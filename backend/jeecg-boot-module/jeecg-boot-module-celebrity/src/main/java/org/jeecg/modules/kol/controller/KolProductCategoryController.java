package org.jeecg.modules.kol.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.SystemConstants;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.kol.entity.KolProductAttributeRelation;
import org.jeecg.modules.kol.entity.KolProductCategory;
import org.jeecg.modules.kol.model.TypeData;
import org.jeecg.modules.kol.service.IKolProductAttributeRelationService;
import org.jeecg.modules.kol.service.IKolProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 产品与标签分类类目关联表
 * @Author: nqr
 * @Date: 2025-05-24
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "产品与标签分类类目关联表")
@RestController
@RequestMapping("/kol/kolProductCategory")
public class KolProductCategoryController extends JeecgController<KolProductCategory, IKolProductCategoryService> {
    @Autowired
    private IKolProductCategoryService kolProductCategoryService;
    @Autowired
    private IKolProductAttributeRelationService productAttributeRelationService;

    /**
     * 分页列表查询
     *
     * @param kolProductCategory
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "产品与标签分类类目关联表-" + SystemConstants.PAGE_LIST_QUERY)
    @Operation(summary = "产品与标签分类类目关联表-" + SystemConstants.PAGE_LIST_QUERY, description = "产品与标签分类类目关联表-" + SystemConstants.PAGE_LIST_QUERY)
    @GetMapping(value = "/list")
    public Result<?> queryPageList(KolProductCategory kolProductCategory,
                                   @RequestParam(name = SystemConstants.PAGE_NO, defaultValue = SystemConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                   @RequestParam(name = SystemConstants.PAGE_SIZE, defaultValue = SystemConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<KolProductCategory> queryWrapper = QueryGenerator.initQueryWrapper(kolProductCategory, req.getParameterMap());
        Page<KolProductCategory> page = new Page<KolProductCategory>(pageNo, pageSize);
        IPage<KolProductCategory> pageList = kolProductCategoryService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param kolProductCategory
     * @return
     */
    @AutoLog(value = "产品与标签分类类目关联表-" + SystemConstants.ADD)
    @Operation(summary = "产品与标签分类类目关联表-" + SystemConstants.ADD, description = "产品与标签分类类目关联表-" + SystemConstants.ADD)
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody KolProductCategory kolProductCategory) {
        kolProductCategoryService.save(kolProductCategory);
        return Result.ok(SystemConstants.ADD_SUCCESS);
    }

    /**
     * 编辑
     *
     * @param kolProductCategory
     * @return
     */
    @AutoLog(value = "产品与标签分类类目关联表-" + SystemConstants.EDIT)
    @Operation(summary = "产品与标签分类类目关联表-" + SystemConstants.EDIT, description = "产品与标签分类类目关联表-" + SystemConstants.EDIT)
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody KolProductCategory kolProductCategory) {
        kolProductCategoryService.updateById(kolProductCategory);
        return Result.ok(SystemConstants.EDIT_SUCCESS);
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "产品与标签分类类目关联表-" + SystemConstants.DELETE_BY_ID)
    @Operation(summary = "产品与标签分类类目关联表-" + SystemConstants.DELETE_BY_ID, description = "产品与标签分类类目关联表-" + SystemConstants.DELETE_BY_ID)
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        kolProductCategoryService.removeById(id);
        return Result.ok(SystemConstants.DELETE_SUCCESS);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "产品与标签分类类目关联表-" + SystemConstants.DELETE_BATCH)
    @Operation(summary = "产品与标签分类类目关联表-" + SystemConstants.DELETE_BATCH, description = "产品与标签分类类目关联表-" + SystemConstants.DELETE_BATCH)
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.kolProductCategoryService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok(SystemConstants.DELETE_BATCH_SUCCESS);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "产品与标签分类类目关联表-" + SystemConstants.QUERY_BY_ID)
    @Operation(summary = "产品与标签分类类目关联表-" + SystemConstants.QUERY_BY_ID, description = "产品与标签分类类目关联表-" + SystemConstants.QUERY_BY_ID)
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        KolProductCategory kolProductCategory = kolProductCategoryService.getById(id);
        return Result.ok(kolProductCategory);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param kolProductCategory
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, KolProductCategory kolProductCategory) {
        return super.exportXls(request, kolProductCategory, KolProductCategory.class, "产品与标签分类类目关联表");
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
        return super.importExcel(request, response, KolProductCategory.class);
    }

    /**
     * @description:根据类目id查询关联数据
     * @author: nqr
     * @date: 2025/6/21 17:16
     **/
    @AutoLog(value = "类目关联表-根据类目id查询关联数据")
    @Operation(summary = "类目关联表-根据类目id查询关联数据", description = "类目关联表-根据类目id查询关联数据")
    @GetMapping(value = "/queryByProductId")
    public Result<?> queryByCategoryId(@RequestParam(name = "productId", required = true) String productId) {
        List<KolProductAttributeRelation> list = productAttributeRelationService.lambdaQuery().eq(KolProductAttributeRelation::getProductId, productId).list();

        Map<String, List<KolProductAttributeRelation>> map = list.stream().collect(Collectors.groupingBy(KolProductAttributeRelation::getAttributeTypeId));

        // 转换为结果列表
        List<TypeData> resultList = new ArrayList<>();

        for (Map.Entry<String, List<KolProductAttributeRelation>> entry : map.entrySet()) {
            TypeData typeData = new TypeData();
            typeData.setTypeId(entry.getKey());
            List<TypeData.CategoryItem> categoryItems = new ArrayList<>();
            entry.getValue().forEach(category -> {
                TypeData.CategoryItem item = new TypeData.CategoryItem();
                item.setLevel(category.getLevel());
                item.setCategoryId(category.getAttributeId());
                item.setIsSel(true);
                categoryItems.add(item);
            });
            typeData.setList(categoryItems);
            resultList.add(typeData);
        }
        return Result.ok(resultList);
    }

}
