package org.jeecg.modules.instagram.history.controller;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.ExamConstants;
import org.jeecg.common.constant.SheetConstants;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.feishu.model.FeishuSheetConvertResult;
import org.jeecg.modules.feishu.model.FeishuSheetResponse;
import org.jeecg.modules.feishu.service.IFeishuService;
import org.jeecg.modules.instagram.history.entity.KolHistoryCelebrityDetail;
import org.jeecg.modules.instagram.history.model.ConversionResult;
import org.jeecg.modules.instagram.history.model.KolHistoryCelebrityDetailFeishuModel;
import org.jeecg.modules.instagram.history.model.KolHistoryCelebrityDetailQueryModel;
import org.jeecg.modules.instagram.history.service.IHistoryReportService;
import org.jeecg.modules.instagram.history.service.IKolHistoryCelebrityDetailService;
import org.jeecg.modules.kol.entity.KolSysUserFeishuSheet;
import org.jeecg.modules.kol.service.IKolSysUserFeishuSheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 历史提报网红明细表
 * @Author: jeecg-boot
 * @Date: 2025-11-26
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "历史提报网红明细表")
@RestController
@RequestMapping("/history/kolHistoryCelebrityDetail")
public class KolHistoryCelebrityDetailController
        extends JeecgController<KolHistoryCelebrityDetail, IKolHistoryCelebrityDetailService> {
    @Autowired
    private IKolHistoryCelebrityDetailService kolHistoryCelebrityDetailService;
    @Autowired
    private IFeishuService feishuService;
    @Resource
    private IKolSysUserFeishuSheetService sheetService;

    @Autowired
    private IHistoryReportService historyReportService;

    /**
     * 分页列表查询
     *
     * @param kolHistoryCelebrityDetail
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "历史提报网红明细表-" + ExamConstants.PAGE_LIST_QUERY)
    @Operation(summary = "历史提报网红明细表-" + ExamConstants.PAGE_LIST_QUERY, description = "历史提报网红明细表-"
            + ExamConstants.PAGE_LIST_QUERY)
    @GetMapping(value = "/list")
    public Result<?> queryPageList(KolHistoryCelebrityDetail kolHistoryCelebrityDetail,
                                   @RequestParam(name = ExamConstants.PAGE_NO, defaultValue = ExamConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                   @RequestParam(name = ExamConstants.PAGE_SIZE, defaultValue = ExamConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<KolHistoryCelebrityDetail> queryWrapper = QueryGenerator
                .initQueryWrapper(kolHistoryCelebrityDetail, req.getParameterMap());
        Page<KolHistoryCelebrityDetail> page = new Page<KolHistoryCelebrityDetail>(pageNo, pageSize);
        IPage<KolHistoryCelebrityDetail> pageList = kolHistoryCelebrityDetailService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param kolHistoryCelebrityDetail
     * @return
     */
    @AutoLog(value = "历史提报网红明细表-" + ExamConstants.ADD)
    @Operation(summary = "历史提报网红明细表-" + ExamConstants.ADD, description = "历史提报网红明细表-" + ExamConstants.ADD)
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody KolHistoryCelebrityDetail kolHistoryCelebrityDetail) {
        // 数据校验
        if (kolHistoryCelebrityDetail == null) {
            return Result.error("请求数据不能为空");
        }

        if (StringUtils.isEmpty(kolHistoryCelebrityDetail.getCelebrityName())) {
            return Result.error("网红名称不能为空");
        }

        try {
            kolHistoryCelebrityDetailService.save(kolHistoryCelebrityDetail);
            return Result.ok(ExamConstants.ADD_SUCCESS);
        } catch (Exception e) {
            log.error("添加历史提报网红明细失败", e);
            return Result.error("添加失败: " + e.getMessage());
        }
    }

    /**
     * 编辑
     *
     * @param kolHistoryCelebrityDetail
     * @return
     */
    @AutoLog(value = "历史提报网红明细表-" + ExamConstants.EDIT)
    @Operation(summary = "历史提报网红明细表-" + ExamConstants.EDIT, description = "历史提报网红明细表-" + ExamConstants.EDIT)
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody KolHistoryCelebrityDetail kolHistoryCelebrityDetail) {
        // 数据校验
        if (kolHistoryCelebrityDetail == null) {
            return Result.error("请求数据不能为空");
        }

        if (StringUtils.isEmpty(kolHistoryCelebrityDetail.getId())) {
            return Result.error("ID不能为空");
        }

        try {
            kolHistoryCelebrityDetailService.updateById(kolHistoryCelebrityDetail);
            return Result.ok(ExamConstants.EDIT_SUCCESS);
        } catch (Exception e) {
            log.error("编辑历史提报网红明细失败", e);
            return Result.error("编辑失败: " + e.getMessage());
        }
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "历史提报网红明细表-" + ExamConstants.DELETE_BY_ID)
    @Operation(summary = "历史提报网红明细表-" + ExamConstants.DELETE_BY_ID, description = "历史提报网红明细表-" + ExamConstants.DELETE_BY_ID)
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        if (StringUtils.isEmpty(id)) {
            return Result.error("ID不能为空");
        }

        try {
            kolHistoryCelebrityDetailService.removeById(id);
            return Result.ok(ExamConstants.DELETE_SUCCESS);
        } catch (Exception e) {
            log.error("删除历史提报网红明细失败，ID: " + id, e);
            return Result.error("删除失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "历史提报网红明细表-" + ExamConstants.DELETE_BATCH)
    @Operation(summary = "历史提报网红明细表-" + ExamConstants.DELETE_BATCH, description = "历史提报网红明细表-" + ExamConstants.DELETE_BATCH)
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        if (StringUtils.isEmpty(ids)) {
            return Result.error("IDs不能为空");
        }

        try {
            String[] idArray = ids.split(",");
            this.kolHistoryCelebrityDetailService.removeByIds(Arrays.asList(idArray));
            return Result.ok(ExamConstants.DELETE_BATCH_SUCCESS);
        } catch (Exception e) {
            log.error("批量删除历史提报网红明细失败，IDs: " + ids, e);
            return Result.error("批量删除失败: " + e.getMessage());
        }
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "历史提报网红明细表-" + ExamConstants.QUERY_BY_ID)
    @Operation(summary = "历史提报网红明细表-" + ExamConstants.QUERY_BY_ID, description = "历史提报网红明细表-" + ExamConstants.QUERY_BY_ID)
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        if (StringUtils.isEmpty(id)) {
            return Result.error("ID不能为空");
        }

        try {
            KolHistoryCelebrityDetail kolHistoryCelebrityDetail = kolHistoryCelebrityDetailService.getById(id);
            if (kolHistoryCelebrityDetail == null) {
                return Result.error("未找到对应数据");
            }
            return Result.ok(kolHistoryCelebrityDetail);
        } catch (Exception e) {
            log.error("查询历史提报网红明细失败，ID: " + id, e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 根据主表ID查询明细列表
     *
     * @param queryModel 查询模型（主表ID即KolHistoryCelebrity的ID）
     * @return
     */
    @AutoLog(value = "历史提报网红明细表-根据主表ID查询")
    @Operation(summary = "历史提报网红明细表-根据主表ID查询", description = "历史提报网红明细表-根据主表ID查询")
    @GetMapping(value = "/listByCelebrityId")
    public Result<?> listByCelebrityId(KolHistoryCelebrityDetailQueryModel queryModel) {
        // 参数校验
        if (queryModel == null || StringUtils.isEmpty(queryModel.getCelebrityId())) {
            return Result.error("主表ID不能为空");
        }

        try {
            LambdaQueryWrapper<KolHistoryCelebrityDetail> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(KolHistoryCelebrityDetail::getCelebrityId, queryModel.getCelebrityId());
            queryWrapper.orderByDesc(KolHistoryCelebrityDetail::getSubmitDate);
            List<KolHistoryCelebrityDetail> list = kolHistoryCelebrityDetailService.list(queryWrapper);
            return Result.ok(list);
        } catch (Exception e) {
            log.error("根据主表ID查询明细列表失败，主表ID: " + queryModel.getCelebrityId(), e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 按平台、产品、网红名称、达人类型进行查询
     *
     * @param queryModel 查询模型
     * @return
     */
    @AutoLog(value = "历史提报网红明细表-条件查询")
    @Operation(summary = "历史提报网红明细表-条件查询", description = "历史提报网红明细表-条件查询")
    @GetMapping(value = "/queryByConditions")
    public Result<?> queryByConditions(KolHistoryCelebrityDetailQueryModel queryModel) {

        try {
            LambdaQueryWrapper<KolHistoryCelebrityDetail> queryWrapper = new LambdaQueryWrapper<>();

            // 添加查询条件
            if (queryModel != null) {
                if (queryModel.getCelebrityId() != null) {
                    queryWrapper.eq(KolHistoryCelebrityDetail::getCelebrityId, queryModel.getCelebrityId());
                }
                if (queryModel.getPlatformType() != null) {
                    queryWrapper.eq(KolHistoryCelebrityDetail::getPlatformType, queryModel.getPlatformType());
                }
                if (StringUtils.isNotEmpty(queryModel.getProductName())) {
                    queryWrapper.like(KolHistoryCelebrityDetail::getProductName, queryModel.getProductName());
                }
                if (StringUtils.isNotEmpty(queryModel.getBrandName())) {
                    queryWrapper.like(KolHistoryCelebrityDetail::getBrandName, queryModel.getBrandName());
                }
                if (StringUtils.isNotEmpty(queryModel.getCelebrityName())) {
                    queryWrapper.like(KolHistoryCelebrityDetail::getCelebrityName, queryModel.getCelebrityName());
                }
                if (StringUtils.isNotEmpty(queryModel.getKolType())) {
                    queryWrapper.like(KolHistoryCelebrityDetail::getKolType, queryModel.getKolType());
                }
            }

            // 按提交日期降序排列
            queryWrapper.orderByDesc(KolHistoryCelebrityDetail::getSubmitDate);

            List<KolHistoryCelebrityDetail> celebrityDetails = kolHistoryCelebrityDetailService.list(queryWrapper);

            return Result.ok(celebrityDetails);
        } catch (Exception e) {
            log.error("条件查询历史提报网红明细失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 导出excel
     *
     * @param request
     * @param kolHistoryCelebrityDetail
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, KolHistoryCelebrityDetail kolHistoryCelebrityDetail) {
        try {
            return super.exportXls(request, kolHistoryCelebrityDetail, KolHistoryCelebrityDetail.class, "历史提报网红明细表");
        } catch (Exception e) {
            log.error("导出历史提报网红明细Excel失败", e);
            // 返回一个空的ModelAndView或者错误提示
            return new ModelAndView();
        }
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
        try {
            return super.importExcel(request, response, KolHistoryCelebrityDetail.class);
        } catch (Exception e) {
            log.error("导入历史提报网红明细Excel失败", e);
            return Result.error("导入失败: " + e.getMessage());
        }
    }

    /**
     * @description: 飞书同步
     * @author: nqr
     * @date: 2025/11/26 16:00
     **/
    @PostMapping(value = "/importFeishuExcel")
    public Result<?> importFeishuExcel() {
        try {
            String tenantAccessToken = feishuService.getInternalTenantAccessToken();
            if (StringUtils.isEmpty(tenantAccessToken)) {
                log.warn("获取飞书访问令牌失败，tenantAccessToken为空");
                return Result.error("获取飞书访问令牌失败");
            }

            KolSysUserFeishuSheet feishuSheet = sheetService.lambdaQuery()
                    .eq(KolSysUserFeishuSheet::getSpreadSheetType, SheetConstants.HISTORY).one();

            if (feishuSheet == null) {
                log.warn("未找到历史提报飞书表格配置");
                return Result.error("未找到历史提报飞书表格配置");
            }

            String url = "https://open.feishu.cn/open-apis/sheets/v2/spreadsheets/" + feishuSheet.getSpreadSheetId()
                    + "/values_batch_get?ranges=" + feishuSheet.getSheetId()
                    + "!A1:" + feishuSheet.getEndColumn()
                    + "&valueRenderOption=ToString&dateTimeRenderOption=FormattedString";
            log.info("获取飞书表格数据，URL: {}", url);

            FeishuSheetResponse sheetResponse = feishuService.getFeishuSheetData(url, tenantAccessToken);
            if (sheetResponse == null) {
                log.warn("获取飞书表格数据失败，sheetResponse为空");
                return Result.error("获取飞书表格数据失败");
            }

            // 2. 将表格数据转换为DTO对象
            FeishuSheetConvertResult<KolHistoryCelebrityDetailFeishuModel> convertResult = feishuService
                    .convertSheetDataToEntityNew(sheetResponse, KolHistoryCelebrityDetailFeishuModel.class);

            if (convertResult == null || convertResult.getEntityList() == null) {
                log.warn("转换飞书表格数据失败，convertResult为空或entityList为空");
                return Result.error("转换飞书表格数据失败");
            }

            List<KolHistoryCelebrityDetailFeishuModel> feishuModelList = convertResult.getEntityList();

            // 检查是否有数据
            if (feishuModelList.isEmpty()) {
                log.warn("飞书表格中没有数据");
                return Result.error("飞书表格中没有数据");
            }

            // 过滤出未同步的数据
            List<KolHistoryCelebrityDetailFeishuModel> unSyncedList = feishuModelList.stream()
                    .filter(model -> model != null && !"是".equals(model.getIsUpdate()))
                    .collect(Collectors.toList());

            if (unSyncedList.isEmpty()) {
                log.info("没有需要同步的数据");
                return Result.ok("没有需要同步的数据");
            }

            log.info("开始处理飞书数据，未同步数据数量: {}", unSyncedList.size());

            // 调用服务层处理数据
            ConversionResult conversionResult = historyReportService.processFeishuDataAndSave(unSyncedList);

            List<KolHistoryCelebrityDetailFeishuModel> successModels = conversionResult.getSuccessModels();
            List<KolHistoryCelebrityDetailFeishuModel> failedModels = conversionResult.getFailedModels();
            String result = conversionResult.getResult();
            boolean isSuccess = conversionResult.isSuccess();


            // 更新飞书文档状态
            if (!successModels.isEmpty()) {
                updateImportStatus(feishuSheet, successModels, "是");
            }
            if (!failedModels.isEmpty()) {
                updateImportStatus(feishuSheet, failedModels, "否");
            }

            log.info("飞书数据处理完成，结果: {}", result);
            return Result.OK("操作成功!",failedModels);
        } catch (Exception e) {
            log.error("导入飞书历史提报数据失败", e);
            return Result.error("操作失败!");
        }
    }

    private void updateImportStatus(KolSysUserFeishuSheet feishuSheet,
                                    List<KolHistoryCelebrityDetailFeishuModel> resultList,
                                    String importStatus) {
        List<Map<String, Object>> arrayList = new ArrayList<>();
        resultList.forEach(x -> {
            arrayList.add(new HashMap<>() {
                {
                    put("range", feishuSheet.getSheetId() + "!" + feishuSheet.getIsSynchronizeColumn() + x.getRowNum()
                            + ":" + feishuSheet.getErrorReasonColumn() + x.getRowNum());
                    put("values", Collections.singletonList(Arrays.asList(importStatus, x.getFailReason())));
                }
            });
        });
        Map<String, Object> values = new HashMap<>() {
            {
                put("valueRanges", arrayList);
            }
        };
        System.out.println(values);
        feishuService.updateValuesBatch(feishuSheet, values);
    }
}