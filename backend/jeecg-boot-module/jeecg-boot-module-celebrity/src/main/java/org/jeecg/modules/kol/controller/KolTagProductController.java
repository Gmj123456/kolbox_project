package org.jeecg.modules.kol.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.SystemConstants;
import org.jeecg.common.constant.enums.StoreTaskStatusEnum;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.kol.entity.KolAttribute;
import org.jeecg.modules.kol.entity.KolProduct;
import org.jeecg.modules.kol.entity.KolTagProduct;
import org.jeecg.modules.kol.entity.KolTags;
import org.jeecg.modules.kol.model.KolSearchModel;
import org.jeecg.modules.kol.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 标签产品管理表
 * @Author: nqr
 * @Date: 2025-06-28
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "标签产品管理表")
@RestController
@RequestMapping("/kol/kolTagProduct")
public class KolTagProductController extends JeecgController<KolTagProduct, IKolTagProductService> {
    @Autowired
    private IKolTagProductService kolTagProductService;
    @Autowired
    private IKolProductService productService;
    @Autowired
    private IKolTagsService kolTagsService;
    @Autowired
    private IKolAttributeService attributeService;
    @Autowired
    private IKolTagAttributeRelationService tagAttributeRelationService;

    /**
     * 分页列表查询
     *
     * @param kolTagProduct
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "标签产品管理表-" + SystemConstants.PAGE_LIST_QUERY)
    @Operation(summary = "标签产品管理表-" + SystemConstants.PAGE_LIST_QUERY, description = "标签产品管理表-" + SystemConstants.PAGE_LIST_QUERY)
    @GetMapping(value = "/list")
    public Result<?> queryPageList(KolTagProduct kolTagProduct,
                                   @RequestParam(name = SystemConstants.PAGE_NO, defaultValue = SystemConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                   @RequestParam(name = SystemConstants.PAGE_SIZE, defaultValue = SystemConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<KolTagProduct> queryWrapper = QueryGenerator.initQueryWrapper(kolTagProduct, req.getParameterMap());
        Page<KolTagProduct> page = new Page<KolTagProduct>(pageNo, pageSize);
        IPage<KolTagProduct> pageList = kolTagProductService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param kolTagProduct
     * @return
     */
    @AutoLog(value = "标签产品管理表-" + SystemConstants.ADD)
    @Operation(summary = "标签产品管理表-" + SystemConstants.ADD, description = "标签产品管理表-" + SystemConstants.ADD)
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody KolTagProduct kolTagProduct) {
        kolTagProductService.save(kolTagProduct);
        return Result.ok(SystemConstants.ADD_SUCCESS);
    }

    /**
     * 编辑
     *
     * @param kolTagProduct
     * @return
     */
    @AutoLog(value = "标签产品管理表-" + SystemConstants.EDIT)
    @Operation(summary = "标签产品管理表-" + SystemConstants.EDIT, description = "标签产品管理表-" + SystemConstants.EDIT)
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody KolTagProduct kolTagProduct) {
        kolTagProductService.updateById(kolTagProduct);
        return Result.ok(SystemConstants.EDIT_SUCCESS);
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "标签产品管理表-" + SystemConstants.DELETE_BY_ID)
    @Operation(summary = "标签产品管理表-" + SystemConstants.DELETE_BY_ID, description = "标签产品管理表-" + SystemConstants.DELETE_BY_ID)
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        kolTagProductService.removeById(id);
        return Result.ok(SystemConstants.DELETE_SUCCESS);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "标签产品管理表-" + SystemConstants.DELETE_BATCH)
    @Operation(summary = "标签产品管理表-" + SystemConstants.DELETE_BATCH, description = "标签产品管理表-" + SystemConstants.DELETE_BATCH)
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.kolTagProductService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok(SystemConstants.DELETE_BATCH_SUCCESS);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "标签产品管理表-" + SystemConstants.QUERY_BY_ID)
    @Operation(summary = "标签产品管理表-" + SystemConstants.QUERY_BY_ID, description = "标签产品管理表-" + SystemConstants.QUERY_BY_ID)
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        KolTagProduct kolTagProduct = kolTagProductService.getById(id);
        return Result.ok(kolTagProduct);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param kolTagProduct
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, KolTagProduct kolTagProduct) {
        return super.exportXls(request, kolTagProduct, KolTagProduct.class, "标签产品管理表");
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
        return super.importExcel(request, response, KolTagProduct.class);
    }

    /**
     * 通过tagId查询
     *
     * @param tagId
     * @return
     */
    @AutoLog(value = "标签产品管理表-通过tagId查询")
    @Operation(summary = "标签产品管理表-通过tagId查询", description = "标签产品管理表-通过tagId查询")
    @GetMapping(value = "/queryByTagId")
    public Result<?> queryByTagId(@RequestParam(name = "tagId", required = true) String tagId) {
        List<KolTagProduct> list = kolTagProductService.lambdaQuery().eq(KolTagProduct::getTagId, tagId).list();
        String productIds = "";
        if (!list.isEmpty()) {
            productIds = list.stream().map(KolTagProduct::getProductId).distinct().collect(Collectors.joining(","));
        }
        return Result.OK("", productIds);
    }

    /**
     * 标签绑定产品
     *
     * @param searchModel
     * @return
     */
    @AutoLog(value = "标签产品管理表-标签绑定产品")
    @Operation(summary = "标签绑定产品", description = "标签绑定产品")
    @PostMapping(value = "/bindingProducts")
    public Result<?> bindingProducts(@RequestBody KolSearchModel searchModel) {
        // 1. 参数校验
        if (oConvertUtils.isEmpty(searchModel.getTagIds())) {
            return Result.error("请选择标签");
        }
        if (oConvertUtils.isEmpty(searchModel.getProductIds())) {
            return Result.error("请选择产品");
        }
        if (oConvertUtils.isEmpty(searchModel.getAttributeIds())) {
            return Result.error("请选择社媒属性");
        }

        String[] tagIdArray = searchModel.getTagIds().split(",");
        String[] productIdArray = searchModel.getProductIds().split(",");
        String[] attributeIdArray = searchModel.getAttributeIds().split(",");


        List<String> tagIdList = Arrays.asList(tagIdArray);
        List<String> productIdList = Arrays.asList(productIdArray);
        List<String> attributeIdList = Arrays.asList(attributeIdArray);

        // 验证社媒属性是否都存在
        List<KolAttribute> attributeList = (List<KolAttribute>) attributeService.listByIds(attributeIdList);
        if (attributeList.size() != attributeIdList.size()) {
            return Result.error("部分社媒属性不存在，请重新选择");
        }

        // 2. 查询标签和产品
        List<KolTags> tagsList = (List<KolTags>) kolTagsService.listByIds(tagIdList);
        if (tagsList.size() != tagIdList.size()) {
            return Result.error("部分标签不存在，请重新选择");
        }

        List<KolProduct> productList = (List<KolProduct>) productService.listByIds(productIdList);
        if (productList.size() != productIdList.size()) {
            return Result.error("产品不存在，请重新选择");
        }

        // 3. 查询已存在的绑定关系，避免重复插入
       /* List<KolTagProduct> existingBindings = kolTagProductService.lambdaQuery()
                .in(KolTagProduct::getTagId, tagIdList)
                .in(KolTagProduct::getProductId, productIdList)
                .list();

        // 构建已存在绑定的唯一键集合（tagId + productId）
        Set<String> existingSet = existingBindings.stream()
                .map(binding -> binding.getTagId() + "_" + binding.getProductId())
                .collect(Collectors.toSet());*/

        // 4. 构造新绑定关系
        List<KolTagProduct> newBindings = new ArrayList<>();
        Date now = new Date();

        List<KolTags> tagsUpdateList = new ArrayList<>();

        for (KolTags tag : tagsList) {
            KolTags tags = new KolTags();
            tags.setId(tag.getId());
            if (oConvertUtils.isEmpty(searchModel.getForceUpdate()) || searchModel.getForceUpdate() == 0) {
                // 判断标签最后爬取时间是否超过30天，超过则更新
                if (tags.getLastExecutionTime() == null || tags.getLastExecutionTime().before(DateUtils.addDays(now, -30))) {
                    tags.setTagStatus(StoreTaskStatusEnum.NOT_START.getCode());
                }
            } else {
                tags.setTagStatus(StoreTaskStatusEnum.NOT_START.getCode());
            }
            tagsUpdateList.add(tags);
            for (KolProduct product : productList) {
               /* String bindingKey = tag.getId() + "_" + product.getId();
                if (existingSet.contains(bindingKey)) {
                    continue; // 已存在，跳过
                }*/

                KolTagProduct binding = new KolTagProduct();
                binding.setId(IdWorker.get32UUID());
                binding.setTagId(tag.getId());
                binding.setTagName(tag.getTagName());
                binding.setProductId(product.getId());
                binding.setProductName(product.getProductName());
                binding.setBrandId(product.getBrandId());
                binding.setBrandName(product.getBrandName());
                binding.setPlatformType(tag.getPlatformType());
                binding.setCreateTime(now);
                binding.setIsDelete(0);
                newBindings.add(binding);
            }
        }
        kolTagProductService.bindingProducts(newBindings, tagsUpdateList, attributeList);
        return Result.ok("绑定成功");
    }

    /**
     * 取消绑定产品
     *
     * @param searchModel
     * @return
     */
    @AutoLog(value = "标签产品管理表-取消绑定产品")
    @Operation(summary = "取消绑定产品", description = "取消绑定产品")
    @PostMapping(value = "/cancelProducts")
    public Result<?> cancelProducts(@RequestBody KolSearchModel searchModel) {
        // 1. 参数校验
        if (oConvertUtils.isEmpty(searchModel.getTagIds())) {
            return Result.error("请选择标签");
        }
        if (oConvertUtils.isEmpty(searchModel.getProductId())) {
            return Result.error("请选择产品");
        }

        String[] tagIdArray = searchModel.getTagIds().split(",");
        // 取消只能按照单个产品取消
        String productId = searchModel.getProductId();

        List<String> tagIdList = Arrays.asList(tagIdArray);

        // 2. 查询标签和产品
        List<KolTags> tagsList = (List<KolTags>) kolTagsService.listByIds(tagIdList);
        if (tagsList.size() != tagIdList.size()) {
            return Result.error("部分标签不存在，请重新选择");
        }

        KolProduct kolProduct = productService.getById(productId);
        if (kolProduct == null) {
            return Result.error("产品不存在，请重新选择");
        }

        // 删除已存在的绑定关系
        kolTagProductService.lambdaUpdate()
                .eq(KolTagProduct::getProductId, kolProduct.getId())
                .in(KolTagProduct::getTagId, tagIdList)
                .remove();

        return Result.ok("绑定成功");
    }
}
