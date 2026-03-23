package org.jeecg.modules.kol.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.SystemConstants;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.kol.entity.KolAttribute;
import org.jeecg.modules.kol.entity.KolAttributeType;
import org.jeecg.modules.kol.entity.KolCategory;
import org.jeecg.modules.kol.mapper.KolAttributeMapper;
import org.jeecg.modules.kol.service.IKolAttributeTypeService;
import org.jeecg.modules.kol.service.IKolCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

import static org.jeecg.common.constant.CategoryType.*;
import static org.jeecg.common.util.oConvertUtils.isIn;

/**
 * @Description: 类目类型表
 * @Author: dongruyang
 * @Date: 2025-05-13
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "类目类型表")
@RestController
@RequestMapping("/kol/kolAttributeType")
public class KolAttributeTypeController extends JeecgController<KolAttributeType, IKolAttributeTypeService> {
    @Autowired
    private IKolAttributeTypeService attributeTypeService;
    @Autowired
    private IKolCategoryService kolCategoryService;
    @Autowired
    private KolAttributeMapper kolAttributeMapper;

    /**
     * 分页列表查询
     *
     * @param kolAttributeType
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "类目类型表-" + SystemConstants.PAGE_LIST_QUERY)
    @Operation(summary = "类目类型表-" + SystemConstants.PAGE_LIST_QUERY, description = "类目类型表-" + SystemConstants.PAGE_LIST_QUERY)
    @GetMapping(value = "/list")
    public Result<?> queryPageList(KolAttributeType kolAttributeType, @RequestParam(name = SystemConstants.PAGE_NO, defaultValue = SystemConstants.PAGE_NO_DEFAULT) Integer pageNo, @RequestParam(name = SystemConstants.PAGE_SIZE, defaultValue = SystemConstants.PAGE_SIZE_DEFAULT) Integer pageSize, HttpServletRequest req) {
        LambdaQueryWrapper<KolAttributeType> queryWrapper = QueryGenerator.initQueryWrapper(kolAttributeType, req.getParameterMap()).lambda();
        Page<KolAttributeType> page = new Page<>(pageNo, pageSize);
        queryWrapper.orderByAsc(KolAttributeType::getSortCode);
        IPage<KolAttributeType> pageList = attributeTypeService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @GetMapping(value = "/listAll")
    public Result<?> listAll(KolAttributeType kolAttributeType) {
        List<KolAttributeType> list = attributeTypeService.listAll(kolAttributeType);
        return Result.ok(list);
    }

    /**
     * 添加
     *
     * @param kolAttributeType
     * @return
     */
    @AutoLog(value = "类目类型表-" + SystemConstants.ADD)
    @Operation(summary = "类目类型表-" + SystemConstants.ADD, description = "类目类型表-" + SystemConstants.ADD)
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody KolAttributeType kolAttributeType) {

        Result<Object> isExists = validateBasicFields(kolAttributeType);
        if (isExists != null) return isExists;

        attributeTypeService.save(kolAttributeType);
        return Result.ok(SystemConstants.ADD_SUCCESS);
    }

    /**
     * 编辑
     *
     * @param kolAttributeType
     * @return
     */
    @AutoLog(value = "类目类型表-" + SystemConstants.EDIT)
    @Operation(summary = "类目类型表-" + SystemConstants.EDIT, description = "类目类型表-" + SystemConstants.EDIT)
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody KolAttributeType kolAttributeType) {
        Result<Object> isExists = validateBasicFields(kolAttributeType);
        if (isExists != null) return isExists;
        // 修改类目对应的类型名称
        kolCategoryService.lambdaUpdate().eq(KolCategory::getCategoryTypeId, kolAttributeType.getId()).set(KolCategory::getCategoryTypeName, kolAttributeType.getTypeName()).update();
        attributeTypeService.updateById(kolAttributeType);
        return Result.ok(SystemConstants.EDIT_SUCCESS);
    }

    private Result<Object> validateBasicFields(KolAttributeType kolAttributeType) {
        String categoryTypeId = Optional.ofNullable(kolAttributeType.getId()).orElse(IdWorker.get32UUID());
        Integer type = kolAttributeType.getType();
        String typeCode = kolAttributeType.getTypeCode();
        String typeName = kolAttributeType.getTypeName();
        KolAttributeType attributeType = attributeTypeService.getById(categoryTypeId);
        // 基本非空校验
        if (oConvertUtils.isEmpty(typeCode)) {
            return Result.error("类型编码不能为空");
        }
        if (!typeCode.matches("^[a-zA-Z0-9_]+$")) {
            return Result.error("类型编码只能包含字母和数字、下划线");
        }
        if (oConvertUtils.isEmpty(type)) {
            return Result.error("类型不能为空");
        }
        if (attributeType != null) {
            String[] typeNames = {INFLUENCER_TYPE_NAME, SCENE_NAME, AUDIENCE_NAME};
            if (isIn(attributeType.getTypeName(), typeNames)) {
                return Result.error(attributeType.getTypeName() + ", 为系统默认值，无法修改");
            }
        }
        // 校验编码或名称重复
        LambdaQueryChainWrapper<KolAttributeType> query = attributeTypeService.lambdaQuery();
        if (oConvertUtils.isNotEmpty(categoryTypeId)) {
            query.ne(KolAttributeType::getId, categoryTypeId);
        }
        int count = Math.toIntExact(query.and(q -> q.eq(KolAttributeType::getTypeCode, typeCode).or().eq(KolAttributeType::getTypeName, typeName)).count());

        if (count > 0) {
            return Result.error("类型名称或编码已存在");
        }

        // 类型为 0 或 1 时，限制同一类型只允许存在一个
        if (type != 2) {
            KolAttributeType existingType = attributeTypeService.lambdaQuery().eq(KolAttributeType::getType, type).ne(KolAttributeType::getId, categoryTypeId).one();

            if (existingType != null) {
                String typeNameStr = (type == 0) ? "个性化标签类目" : (type == 1) ? "产品类目" : "未知类目";
                return Result.error(typeNameStr + "已存在");
            }
        }

        return null;
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "类目类型表-" + SystemConstants.DELETE_BY_ID)
    @Operation(summary = "类目类型表-" + SystemConstants.DELETE_BY_ID, description = "类目类型表-" + SystemConstants.DELETE_BY_ID)
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        KolAttributeType attributeType = attributeTypeService.getById(id);
        String[] typeNames = {INFLUENCER_TYPE_NAME, SCENE_NAME, AUDIENCE_NAME};
        if (isIn(attributeType.getTypeName(), typeNames)) {
            return Result.error(attributeType.getTypeName() + ", 为系统默认值，无法删除");
        }
        // 查询是否有关联数据
        Integer count = Math.toIntExact(kolCategoryService.lambdaQuery().eq(KolCategory::getCategoryTypeId, id).count());
        Integer attributeCount = Math.toIntExact(kolAttributeMapper.selectCount(new QueryWrapper<KolAttribute>().eq("attribute_type_id", id)));
        if (count > 0 || attributeCount > 0) {
            return Result.error("该类型下存在关联数据，不能删除");
        }
        attributeTypeService.removeById(id);
        return Result.ok(SystemConstants.DELETE_SUCCESS);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "类目类型表-" + SystemConstants.DELETE_BATCH)
    @Operation(summary = "类目类型表-" + SystemConstants.DELETE_BATCH, description = "类目类型表-" + SystemConstants.DELETE_BATCH)
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        /* this.attributeTypeService.removeByIds(Arrays.asList(ids.split(",")));*/
        return Result.ok("无权限操作！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "类目类型表-" + SystemConstants.QUERY_BY_ID)
    @Operation(summary = "类目类型表-" + SystemConstants.QUERY_BY_ID, description = "类目类型表-" + SystemConstants.QUERY_BY_ID)
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        KolAttributeType kolAttributeType = attributeTypeService.getById(id);
        return Result.ok(kolAttributeType);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param kolAttributeType
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, KolAttributeType kolAttributeType) {
        return super.exportXls(request, kolAttributeType, KolAttributeType.class, "类目类型表");
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
        return super.importExcel(request, response, KolAttributeType.class);
    }

}
