package org.jeecg.modules.kol.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.CustomPage;
import org.jeecg.common.util.PageUtil;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.service.IIgCelebrityService;
import org.jeecg.modules.kol.entity.KolContact;
import org.jeecg.modules.kol.model.KolAllotExcelModel;
import org.jeecg.modules.kol.model.KolBaseModel;
import org.jeecg.modules.kol.model.KolSearchModel;
import org.jeecg.modules.kol.service.IKolAllotService;
import org.jeecg.modules.kol.service.IKolCelebrityService;
import org.jeecg.modules.kol.service.IKolContactService;
import org.jeecg.modules.kol.service.IKolShieldsService;
import org.jeecg.modules.tiktok.service.ITiktokCelebrityService;
import org.jeecg.modules.youtube.service.IYtCelebrityService;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 网红标签分配控制器
 * @Author: xyc
 * @Date: 2024-12-30 19:29:28
 * @Version: V1.0
 */
@Tag(name = "网红标签分配")
@RestController
@RequestMapping("/kol/kolAllot")
@Slf4j
public class KolAllotController {

    @Autowired
    private ISysBaseAPI sysBaseAPI;
    @Autowired
    private IKolContactService kolContactService;
    @Autowired
    private IIgCelebrityService igCelebrityService;
    @Autowired
    private IYtCelebrityService ytCelebrityService;
    @Autowired
    private ITiktokCelebrityService tkCelebrityService;
    @Autowired
    private IKolCelebrityService kolCelebrityService;
    @Autowired
    private IKolShieldsService kolShieldsService;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 分页列表查询
     *
     * @param searchModel
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "已分配网红列表-分页列表查询")
    @Operation(summary = "已分配网红列表-分页列表查询", description = "已分配网红列表-分页列表查询")
    @PostMapping(value = "/getAllottedKolList")
    public Result<?> queryPageList(@RequestBody KolSearchModel searchModel,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        try {
            log.info("[我的网红] 列表查询参数：{}", JSON.toJSONString(searchModel));
        } catch (Exception e) {
            e.printStackTrace();
        }
        CustomPage<KolBaseModel> page = new CustomPage<>(pageNo, pageSize, pageSize + 1, req);
        page.setSearchCount(false);
    /*    Page<KolBaseModel> page = PageUtil.getOrderItems(pageNo, actualPageSize, req);
        page.setSearchCount(false);
*/
        // 记录起始执行时间
        long startExecTime = System.currentTimeMillis();
        Integer platformType = searchModel.getPlatformType();
        // 获取当前登录用户
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String loginUserId = sysUser.getId();
        // 目前暂定将查询的网红顾问id设置为当前登录用户，后续如果需要类似超管等查看指定人员的网红列表，可修改此处
        searchModel.setCounselorId(loginUserId);
        // String attributeIds = searchModel.getAttributeIds();
        // searchModel.setAttributeIds(null);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 判断是否存在查询条件
        kolCelebrityService.checkParams(searchModel);
        stopWatch.stop();
        log.info("[我的网红] 判断是否存在查询条件,耗时{}秒", String.format("%.2f", stopWatch.getTime() / 1000.0));

        IPage<KolBaseModel> pageList = new Page<>();

        // 处理类目参数
        if (oConvertUtils.isNotEmpty(searchModel.getCategoryIds()) || oConvertUtils.isNotEmpty(searchModel.getAttributeIds())) {
            if (oConvertUtils.listIsEmpty(searchModel.getTagNameList())) {
                return Result.ok(pageList);
            }
        }
        // searchModel.setAttributeIds(attributeIds);
        IKolAllotService allotService = getKolAllotService(platformType);
        Integer allottedKolCount = 0;

        if (!searchModel.getTagNameList().isEmpty()) {
            String tempTableName = "temp_" + Thread.currentThread().getId() + "_" + System.currentTimeMillis();
            searchModel.setTempTableName(tempTableName);
        }

        stopWatch.reset();
        stopWatch.start();

        try {
            pageList = allotService.getAllottedKolListNew(page, searchModel);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        stopWatch.stop();
        log.info("[我的网红] 开始查询已分配网红列表,耗时{}秒", String.format("%.2f", stopWatch.getTime() / 1000.0));

        // 不查询分页时，处理分页数据，需要比查询条数多一条，判断是否存在下一页
        pageList = PageUtil.processPagination(pageList, pageNo, pageSize);
        allottedKolCount = Math.toIntExact(pageList.getTotal());

        stopWatch.reset();
        stopWatch.start();
        processKolModelList(allottedKolCount, pageList, allotService, searchModel);
        stopWatch.stop();
        log.info("[我的网红] 设置历史开发,耗时{}秒", String.format("%.2f", stopWatch.getTime() / 1000.0));

        stopWatch.reset();
        stopWatch.start();
        kolCelebrityService.setAttributes(pageList, searchModel);
        stopWatch.stop();
        log.info("[我的网红] 设置社媒属性,耗时{}秒", String.format("%.2f", stopWatch.getTime() / 1000.0));

        stopWatch.reset();
        stopWatch.start();
        kolShieldsService.setShields(pageList, searchModel);
        stopWatch.stop();
        log.info("[我的网红] 设置社媒属性,耗时{}秒", String.format("%.2f", stopWatch.getTime() / 1000.0));

        log.info("[我的网红] 列表查询耗时：{}秒", (System.currentTimeMillis() - startExecTime) / 1000.0);

        try {
            log.info("[我的网红] 列表查询结果：{}", JSON.toJSONString(pageList.getRecords()));
        } catch (Exception e) {
            e.printStackTrace();

        }
        return Result.OK(pageList, (int) allottedKolCount);
    }

    @PostMapping(value = "/getAllotListCount")
    public Result<?> getAllotListCount(@RequestBody KolSearchModel searchModel) {
        Subject currentSubject = SecurityUtils.getSubject();
        LoginUser sysUser = (LoginUser) currentSubject.getPrincipal();
        if (sysUser == null) {
            return Result.error("用户未登录或会话已过期");
        }
        String loginUserId = sysUser.getId();

        try {
            searchModel.setCounselorId(loginUserId);
            // 获取平台类型
            Integer platformType = searchModel.getPlatformType();

            // 保存属性 ID，临时置空以便后续处理
            // String attributeIds = searchModel.getAttributeIds();
            // searchModel.setAttributeIds(null);

            // 校验查询参数
            kolCelebrityService.checkParams(searchModel);

            // 初始化分页对象
            IPage<KolBaseModel> pageList = new Page<>();

            // 处理类目和属性参数
            if (oConvertUtils.isNotEmpty(searchModel.getCategoryIds()) || oConvertUtils.isNotEmpty(searchModel.getAttributeIds())) {
                if (oConvertUtils.listIsEmpty(searchModel.getTagNameList())) {
                    return Result.ok(pageList); // 返回空结果
                }
            }

            // 恢复属性 ID
            // searchModel.setAttributeIds(attributeIds);

            // 如果标签列表不为空，生成临时表名
            if (searchModel.getTagNameList() != null && !searchModel.getTagNameList().isEmpty()) {
                String tempTableName = "temp_" + Thread.currentThread().getId() + "_" + System.currentTimeMillis();
                searchModel.setTempTableName(tempTableName);
            }

            // 获取分配服务并查询网红数量
            IKolAllotService allotService = getKolAllotService(platformType);
            Integer screeningCount = allotService.getAllottedKolCount(searchModel);

            return Result.ok(screeningCount);

        } catch (Exception e) {
            log.error("获取分配列表数量异常", e);
            return Result.error("获取数据异常：" + e.getMessage());
        }
    }

    /**
     * 处理网红视图模型列表 更新建联字段和标签字段
     *
     * @param kolModelPageList
     * @return
     */
    private void processKolModelList(Integer kolCount, IPage<KolBaseModel> kolModelPageList, IKolAllotService allotService, KolSearchModel searchModel) {
        Integer platformType = searchModel.getPlatformType();
        // 处理分页记录
        List<KolBaseModel> kolModelList = kolModelPageList.getRecords();
        if (!kolModelList.isEmpty()) {
            List<String> uniqueIdList;
            // 获取账号id列表
            if (platformType == CommonConstant.TK) {
                uniqueIdList = kolModelList.stream().map(KolBaseModel::getAuthorId).collect(Collectors.toList());
            } else {
                uniqueIdList = kolModelList.stream().map(KolBaseModel::getAccount).collect(Collectors.toList());
            }
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            // 更新开发建联历史字段 纵向变横向
            List<KolContact> kolContactList = kolContactService.getContactListByIds(uniqueIdList);
            stopWatch.stop();
            log.info("[我的网红] 查询开发建联,耗时{}秒", String.format("%.2f", stopWatch.getTime() / 1000.0));
            stopWatch.reset();

            stopWatch.start();
            kolContactList = kolContactList.stream().filter(x -> Objects.equals(x.getPlatformType(), platformType)).collect(Collectors.toList());
            if (!kolContactList.isEmpty()) {
                if (platformType == CommonConstant.TK) {
                    kolContactService.updateContactHistoryAllot(kolModelList, kolContactList, KolBaseModel::getAuthorId,
                            KolBaseModel::setContactHistory, KolBaseModel::setContactCount);
                } else {
                    kolContactService.updateContactHistoryAllot(kolModelList, kolContactList, KolBaseModel::getAccount,
                            KolBaseModel::setContactHistory, KolBaseModel::setContactCount);
                }
            }
            stopWatch.stop();
            stopWatch.reset();
            log.info("[我的网红] 更新开发建联历史,耗时{}秒", String.format("%.2f", stopWatch.getTime() / 1000.0));

            stopWatch.start();
            // 处理标签字段赋值
            String tempTableName = "temp_" + Thread.currentThread().getId() + "_" + System.currentTimeMillis();
            searchModel.setTempTableName(tempTableName);
            allotService.updateKolTagList(uniqueIdList, kolModelList, searchModel);
            stopWatch.stop();
            log.info("[我的网红] 标签字段赋值,耗时{}秒", String.format("%.2f", stopWatch.getTime() / 1000.0));

        }
        kolModelPageList.setTotal(kolCount);
    }


    /**
     * 根据平台类型获取对应的服务
     *
     * @param platformType 0=IG 1=YT 2=TK
     * @return
     */
    private IKolAllotService getKolAllotService(Integer platformType) {
        switch (platformType) {
            case CommonConstant.IG:
                return igCelebrityService;
            case CommonConstant.YT:
                return ytCelebrityService;
            case CommonConstant.TK:
                return tkCelebrityService;
            default:
                throw new IllegalArgumentException("未知的平台类型参数: " + platformType);
        }
    }

    /**
     * 导出excel
     *
     * @param request            请求对象
     * @param kolAllotExcelModel KOL分配Excel模型
     * @return Excel导出视图
     */
    @Operation(summary = "导出我的网红Excel", description = "导出我的网红Excel")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, KolAllotExcelModel kolAllotExcelModel) {
        // 1. 参数验证和初始化
        String selections = request.getParameter("selections");
        if (oConvertUtils.isEmpty(selections)) {
            throw new IllegalArgumentException("导出数据不能为空");
        }

        Integer platformType = kolAllotExcelModel.getPlatformType();
        if (platformType == null) {
            throw new IllegalArgumentException("平台类型不能为空");
        }

        List<String> selectionList = Arrays.asList(selections.split(","));

        // 2. 获取导出数据
        IKolAllotService allotService = getKolAllotService(platformType);
        List<KolAllotExcelModel> modelList = allotService.getAllotListByIds(selectionList);

        // 3. 处理联系历史信息（仅当有数据时处理）
        if (!modelList.isEmpty()) {
            updateContactHistoryForExport(modelList, platformType);
        }

        // 4. 构建并返回Excel视图
        return createExcelView(modelList);
    }

    /**
     * 更新导出数据的联系历史信息
     *
     * @param modelList    模型列表
     * @param platformType 平台类型
     */
    private void updateContactHistoryForExport(List<KolAllotExcelModel> modelList, Integer platformType) {
        // 提取唯一ID列表（根据平台类型选择不同的ID字段）
        List<String> uniqueIdList = extractUniqueIds(modelList, platformType);

        // 获取并过滤联系记录
        List<KolContact> kolContactList = kolContactService.getContactListByIds(uniqueIdList)
                .stream()
                .filter(contact -> Objects.equals(contact.getPlatformType(), platformType))
                .collect(Collectors.toList());

        // 针对每个网红，按产品名-品牌名组合去重（保留相同组合的第一个记录）
        Map<String, List<KolContact>> contactsByUniqueId = kolContactList.stream()
                .collect(Collectors.groupingBy(contact ->
                        platformType == CommonConstant.TK ? contact.getCelebrityId() : contact.getUniqueId()));

        List<KolContact> deduplicatedContacts = new ArrayList<>();
        for (List<KolContact> contacts : contactsByUniqueId.values()) {
            // 对每个网红的联系记录，按产品名-品牌名组合去重
            Map<String, KolContact> uniqueProductBrandMap = new LinkedHashMap<>();
            for (KolContact contact : contacts) {
                String productBrandKey = (oConvertUtils.isEmpty(contact.getProductName()) ? "" : contact.getProductName()) + "-" + contact.getBrandName();
                // 只保留第一个出现的产品名-品牌名组合
                uniqueProductBrandMap.putIfAbsent(productBrandKey, contact);
            }
            deduplicatedContacts.addAll(uniqueProductBrandMap.values());
        }

        // 更新联系历史（仅当有联系记录时）
        if (!deduplicatedContacts.isEmpty()) {
            updateContactHistoryByPlatform(modelList, deduplicatedContacts, platformType);
        }
    }

    /**
     * 根据平台类型提取唯一ID列表
     *
     * @param modelList    模型列表
     * @param platformType 平台类型
     * @return 唯一ID列表
     */
    private List<String> extractUniqueIds(List<KolAllotExcelModel> modelList, Integer platformType) {
        if (platformType == CommonConstant.TK) {
            return modelList.stream()
                    .map(KolAllotExcelModel::getAuthorId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } else {
            return modelList.stream()
                    .map(KolAllotExcelModel::getUniqueId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
    }

    /**
     * 根据平台类型更新联系历史
     *
     * @param modelList      模型列表
     * @param kolContactList 联系记录列表
     * @param platformType   平台类型
     */
    private void updateContactHistoryByPlatform(List<KolAllotExcelModel> modelList,
                                                List<KolContact> kolContactList,
                                                Integer platformType) {
        if (platformType == CommonConstant.TK) {
            kolContactService.updateContactHistoryAllotOld(
                    modelList,
                    kolContactList,
                    KolAllotExcelModel::getAuthorId,
                    KolAllotExcelModel::setContactHistory,
                    KolAllotExcelModel::setContactCount
            );
        } else {
            kolContactService.updateContactHistoryAllotOld(
                    modelList,
                    kolContactList,
                    KolAllotExcelModel::getUniqueId,
                    KolAllotExcelModel::setContactHistory,
                    KolAllotExcelModel::setContactCount
            );
        }
    }

    /**
     * 创建Excel导出视图
     *
     * @param modelList 数据列表
     * @return Excel视图
     */
    private ModelAndView createExcelView(List<KolAllotExcelModel> modelList) {
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "我的网红");
        mv.addObject(NormalExcelConstants.CLASS, KolAllotExcelModel.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams());
        mv.addObject(NormalExcelConstants.DATA_LIST, modelList);
        return mv;
    }
}
