package org.jeecg.modules.instagram.storepromotion.controller;

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
import org.jeecg.common.constant.enums.ApprovedStatusType;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.storepromotion.entity.StoreSellerPromotion;
import org.jeecg.modules.instagram.storepromotion.service.IStoreSellerPromotionService;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionProject;
import org.jeecg.modules.instagram.storepromotion.model.StorePromotionProjectModel;
import org.jeecg.modules.instagram.storepromotion.service.IStorePromotionProjectService;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionProjectDetail;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 产品网红历史匹配方案
 * @Author: nqr
 * @Date: 2023-09-02
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "产品网红历史匹配方案")
@RestController
@RequestMapping("/promotionProject")
public class StorePromotionProjectController extends JeecgController<StorePromotionProject, IStorePromotionProjectService> {
    @Autowired
    private IStorePromotionProjectService projectService;
    @Autowired
    private IStoreSellerPromotionService promotionService;

    /**
     * 分页列表查询
     *
     * @param storePromotionProject
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "产品网红历史匹配方案-" + SystemConstants.PAGE_LIST_QUERY)
@Operation(summary = "产品网红历史匹配方案-" + SystemConstants.PAGE_LIST_QUERY, description = "产品网红历史匹配方案-" + SystemConstants.PAGE_LIST_QUERY)
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StorePromotionProjectModel storePromotionProjectModel,
                                   @RequestParam(name = SystemConstants.PAGE_NO, defaultValue = SystemConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                   @RequestParam(name = SystemConstants.PAGE_SIZE, defaultValue = SystemConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
                                   HttpServletRequest req) {
        Page<StorePromotionProjectModel> page = new Page<>(pageNo, pageSize);
        IPage<StorePromotionProjectModel> pageList = projectService.pageList(page, storePromotionProjectModel);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param storePromotionProject
     * @return
     */
    @AutoLog(value = "产品网红历史匹配方案-" + SystemConstants.ADD)
@Operation(summary = "产品网红历史匹配方案-" + SystemConstants.ADD, description = "产品网红历史匹配方案-" + SystemConstants.ADD)
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody StorePromotionProject storePromotionProject) {
        projectService.save(storePromotionProject);
        return Result.ok(SystemConstants.ADD_SUCCESS);
    }

    /**
     * 编辑
     *
     * @param storePromotionProject
     * @return
     */
    @AutoLog(value = "产品网红历史匹配方案-" + SystemConstants.EDIT)
@Operation(summary = "产品网红历史匹配方案-" + SystemConstants.EDIT, description = "产品网红历史匹配方案-" + SystemConstants.EDIT)
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody StorePromotionProject storePromotionProject) {
        projectService.updateById(storePromotionProject);
        return Result.ok(SystemConstants.EDIT_SUCCESS);
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "产品网红历史匹配方案-" + SystemConstants.DELETE_BY_ID)
@Operation(summary = "产品网红历史匹配方案-" + SystemConstants.DELETE_BY_ID, description = "产品网红历史匹配方案-" + SystemConstants.DELETE_BY_ID)
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        projectService.removeData(id);
        return Result.ok(SystemConstants.DELETE_SUCCESS);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "产品网红历史匹配方案-" + SystemConstants.DELETE_BATCH)
@Operation(summary = "产品网红历史匹配方案-" + SystemConstants.DELETE_BATCH, description = "产品网红历史匹配方案-" + SystemConstants.DELETE_BATCH)
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.projectService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok(SystemConstants.DELETE_BATCH_SUCCESS);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "产品网红历史匹配方案-" + SystemConstants.QUERY_BY_ID)
@Operation(summary = "产品网红历史匹配方案-" + SystemConstants.QUERY_BY_ID, description = "产品网红历史匹配方案-" + SystemConstants.QUERY_BY_ID)
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        StorePromotionProject storePromotionProject = projectService.getById(id);
        return Result.ok(storePromotionProject);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param storePromotionProject
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StorePromotionProject storePromotionProject) {
        return super.exportXls(request, storePromotionProject, StorePromotionProject.class, "产品网红历史匹配方案");
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
        String extensionCode = multipartRequest.getParameter("extensionCode");
        String name = multipartRequest.getParameter("name");
        StoreSellerPromotion sellerPromotion = promotionService.getByExtensionCode(extensionCode);
        if (sellerPromotion == null) {
            return Result.error("未获取到推广信息");
        }
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();
            ImportParams params = new ImportParams();
//            params.setTitleRows(1);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                long start = System.currentTimeMillis();
                //获取上传文件列表
                List<StorePromotionProjectDetail> list = ExcelImportUtil.importExcel(file.getInputStream(), StorePromotionProjectDetail.class, params);
                if (list.isEmpty()) {
                    return Result.error("文件导入失败，请检查文件内容是否正确！");
                }
                StorePromotionProject storePromotionProjectNew = createStorePromotionProject(sellerPromotion, name);
                list = list.stream().filter(x -> oConvertUtils.isNotEmpty(x.getAccount())).collect(Collectors.toList());
                List<StorePromotionProjectDetail> listNew = new ArrayList<>();
                for (StorePromotionProjectDetail projectDetail : list) {
                    projectDetail.setId(IdWorker.get32UUID());
                    projectDetail.setSellerId(sellerPromotion.getSellerId());
                    projectDetail.setSellerName(sellerPromotion.getSellerName());
                    projectDetail.setExtensionCode(extensionCode);
                    projectDetail.setPromId(sellerPromotion.getId());
                    projectDetail.setProjectId(storePromotionProjectNew.getId());
                    projectDetail.setProjectName(name);
                    String interactAvgNum = projectDetail.getInteractAvgNum();
                    if (oConvertUtils.isNotEmpty(interactAvgNum) && !interactAvgNum.contains("%")) {
                        String interactAvgNumStr = new BigDecimal(interactAvgNum).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP).toString();
                        String[] split = interactAvgNumStr.split("\\.");
                        if ("00".equals(split[1])) {
                            interactAvgNumStr = split[0];
                        }
                        projectDetail.setInteractAvgNum(interactAvgNumStr + "%");
                    }
                    listNew.add(projectDetail);
                }
                StoreSellerPromotion sellerPromotionNew = new StoreSellerPromotion();
                sellerPromotionNew.setId(sellerPromotion.getId());
                sellerPromotionNew.setApprovedStatus(ApprovedStatusType.PROVIDE_PLAN.getCode());
                projectService.saveImportData(storePromotionProjectNew, listNew, sellerPromotionNew);
                log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
                return Result.ok("文件导入成功！成功条数：" + list.size());
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return Result.error("文件导入失败，请检查文件内容是否正确");
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

    /**
     * 查询方案名称是否存在
     *
     * @param name
     * @return
     */
    @AutoLog(value = "产品网红历史匹配方案-查询方案名称是否存在")
@Operation(summary = "产品网红历史匹配方案-查询方案名称是否存在", description = "产品网红历史匹配方案-查询方案名称是否存在")
    @GetMapping(value = "/queryByName")
    public Result<?> queryByName(@RequestParam(name = "name", required = true) String name) {
        LambdaQueryWrapper<StorePromotionProject> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StorePromotionProject::getName, name);
        List<StorePromotionProject> list = projectService.list();
        return list.isEmpty() ? Result.ok() : Result.error("当前名称已存在");
    }

    /**
     * 功能描述:创建产品网红历史匹配方案
     *
     * @return org.jeecg.modules.instagram.storepromotionproject.entity.StorePromotionProject
     * @Date 2023-09-02 14:46:25
     */
    private StorePromotionProject createStorePromotionProject(StoreSellerPromotion sellerPromotion, String name) {
        StorePromotionProject storePromotionProject = new StorePromotionProject();
        storePromotionProject.setId(IdWorker.get32UUID());
        storePromotionProject.setSellerId(sellerPromotion.getSellerId());
        storePromotionProject.setSellerName(sellerPromotion.getSellerName());
        storePromotionProject.setExtensionCode(sellerPromotion.getExtensionCode());
        storePromotionProject.setPromId(sellerPromotion.getId());
        storePromotionProject.setName(name);
        return storePromotionProject;
    }
}
