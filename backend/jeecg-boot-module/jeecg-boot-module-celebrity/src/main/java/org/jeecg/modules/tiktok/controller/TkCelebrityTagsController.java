package org.jeecg.modules.tiktok.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.enums.YesNoStatus;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.PageUtil;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.kol.entity.KolAllotLog;
import org.jeecg.modules.kol.entity.KolAllotLogDetail;
import org.jeecg.modules.kol.entity.KolAttribute;
import org.jeecg.modules.kol.entity.KolTags;
import org.jeecg.modules.kol.model.*;
import org.jeecg.modules.kol.model.allotmodel.AllocationResult;
import org.jeecg.modules.kol.model.allotmodel.InitializationData;
import org.jeecg.modules.kol.model.allotmodel.ProcessedTagData;
import org.jeecg.modules.kol.service.*;
import org.jeecg.modules.tiktok.entity.TiktokCelebrityRule;
import org.jeecg.modules.tiktok.entity.TiktokCelebrityTags;
import org.jeecg.modules.tiktok.entity.TiktokCelebrityTagsCounselor;
import org.jeecg.modules.tiktok.model.TiktokCelebrityTagsModel;
import org.jeecg.modules.tiktok.service.ITiktokCelebrityRuleService;
import org.jeecg.modules.tiktok.service.ITiktokCelebrityTagsService;
import org.jeecg.modules.tiktok.service.ITkCelebrityTagsCounselorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.jeecg.common.constant.SystemConstants.*;

/**
 * @Description: TK网红标签控制器
 * @Author: xyc
 * @Date: 2024-12-25 18:41:03
 * @Version: V1.0
 * @Remark: 替换原类TiktokCelebrityTagsController
 */
@Tag(name = "TikTok网红标签")
@RestController
@RequestMapping("/tiktok/tkCelebrityTags")
@Slf4j
public class TkCelebrityTagsController extends JeecgController<TiktokCelebrityTags, ITiktokCelebrityTagsService> {
    @Autowired
    private ITiktokCelebrityTagsService tkTagsService;
    @Autowired
    private ITkCelebrityTagsCounselorService tkTagsCounselorService;
    @Autowired
    private ISysBaseAPI sysBaseAPI;
    @Autowired
    private IKolTagsService kolTagsService;
    @Autowired
    private IKolTagProductService tagProductService;
    @Autowired
    private ITiktokCelebrityRuleService ruleService;
    @Resource
    private IKolTagAttributeRelationService tagAttributeRelationService;
    @Resource
    private IKolAttributeService attributeService;
    @Autowired
    private IKolCelebrityService kolCelebrityService;
    @Autowired
    private IKolAllotLogService kolAllotLogService;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 组合网红标签分配日志明细实体
     *
     * @param searchModel          查询条件视图模型
     * @param unallottedKolTagList 未分配网红标签列表
     * @return
     */
    private KolAllotLogDetail createKolAllotLogDetail(KolSearchModel searchModel,
                                                      List<UserTagAllotModel> unallottedKolTagList) {
        // 先获取网红顾问id和网红顾问名称
        String counselorId = searchModel.getCounselorId();
        String counselorName = searchModel.getCounselorName();

        // 将分配列表按标签分组统计其标签分配数量 转换为json格式存储
        JSONObject jsonObject = new JSONObject();
        // 完成分组统计出标签和对应网红数量
        Map<String, Long> tagCountMap = unallottedKolTagList.stream()
                .collect(Collectors.groupingBy(UserTagAllotModel::getTagName, Collectors.counting()));
        // 构建 JSON 对象
        jsonObject.putAll(tagCountMap);
        // 组合分配日志（暂不考虑主表问题）
        KolAllotLogDetail kolAllotLogDetailEntity = new KolAllotLogDetail();
        kolAllotLogDetailEntity.setId(IdWorker.get32UUID());
        kolAllotLogDetailEntity.setCounselorId(counselorId);
        kolAllotLogDetailEntity.setCounselorName(counselorName);
        kolAllotLogDetailEntity.setPlatformType(CommonConstant.TK);
        kolAllotLogDetailEntity.setAllotContent(jsonObject.toJSONString());
        kolAllotLogDetailEntity.setAllotTime(new Date());

        return kolAllotLogDetailEntity;
    }

    /**
     * 组合网红标签顾问实体
     *
     * @param tagAllotModel 当前要处理的标签模型
     * @param searchModel   前端传入的查询视图模型
     * @return
     */
    private TiktokCelebrityTagsCounselor createTagsCounselor(UserTagAllotModel tagAllotModel,
                                                             KolSearchModel searchModel) {
        TiktokCelebrityTagsCounselor tagsCounselor = new TiktokCelebrityTagsCounselor();
        tagsCounselor.setId(IdWorker.get32UUID());
        tagsCounselor.setAuthorId(tagAllotModel.getAuthorId());
        tagsCounselor.setTiktokCelebrityTagsId(tagAllotModel.getId());
        tagsCounselor.setCounselorId(searchModel.getCounselorId());
        tagsCounselor.setCounselorName(searchModel.getCounselorName());
        tagsCounselor.setAllotTime(new Date());
        tagsCounselor.setUniqueId(tagAllotModel.getUniqueId());
        tagsCounselor.setTagName(tagAllotModel.getTagName());
        tagsCounselor.setTagType(tagAllotModel.getTagType());
        tagsCounselor.setAuthorFollowerCount(tagAllotModel.getAuthorFollowerCount());
        tagsCounselor.setCountry(tagAllotModel.getCountry());
        return tagsCounselor;
    }

    /**
     * 处理标签分配
     *
     * @param tagAllotModel             当前要处理的标签模型
     * @param searchModel               前端传入的查询视图模型
     * @param tagsCounselorList         标签顾问列表
     * @param tkTagsUpdateList          网红标签需更新分配标识的列表
     * @param tkTagsCounselorAddList    新增的标签顾问列表
     * @param tkTagsCounselorUpdateList 更新的标签顾问列表
     */
    private void processTagAllot(KolSearchModel searchModel, UserTagAllotModel tagAllotModel,
                                 List<TiktokCelebrityTagsCounselor> tagsCounselorList, List<TiktokCelebrityTags> tkTagsUpdateList,
                                 List<TiktokCelebrityTagsCounselor> tkTagsCounselorAddList,
                                 List<TiktokCelebrityTagsCounselor> tkTagsCounselorUpdateList) {

        // 先获取网红顾问id和网红顾问名称
        String counselorId = searchModel.getCounselorId();
        String counselorName = searchModel.getCounselorName();

        // 更新网红标签表实体
        TiktokCelebrityTags tagsEntity = new TiktokCelebrityTags();
        tagsEntity.setId(tagAllotModel.getId());
        tagsEntity.setCelebrityCounselorId(counselorId);
        tagsEntity.setCelebrityCounselorName(counselorName);
        tagsEntity.setIsAllot(YesNoStatus.YES.getCode());
        tagsEntity.setAllotTime(new Date());
        tkTagsUpdateList.add(tagsEntity);

        // 构建或更新网红标签顾问表实体
        TiktokCelebrityTagsCounselor tagCounselor = createTagsCounselor(tagAllotModel, searchModel);
        Optional<TiktokCelebrityTagsCounselor> tagsCounselorOptional = tagsCounselorList.stream()
                .filter(x -> x.getTagName().equals(tagAllotModel.getTagName())
                        && x.getAuthorId().equals(tagAllotModel.getAuthorId()))
                .findFirst();
        // 判断是否已经存在该标签顾问
        tagsCounselorOptional.ifPresent(existing -> {
            tagCounselor.setId(existing.getId());
            tkTagsCounselorUpdateList.add(tagCounselor);
        });
        // 如果更新列表不存在 则添加到新增列表
        if (!tkTagsCounselorUpdateList.contains(tagCounselor)) {
            tkTagsCounselorAddList.add(tagCounselor);
        }
    }

    // endregion

    /**
     * 获取未分配的网红标签数量
     *
     * @param searchModel
     * @return
     */
    @Operation(summary = "TK网红标签未分配数量查询", description = "TK网红标签未分配数量查询")
    @PostMapping(value = "/getUnallottedTagCount")
    public Result<?> getUnallottedTagCount(@RequestBody KolSearchModel searchModel) {
        // 获取网红分配过期天数 默认30天
        int allotMaxDays = tkTagsService.getAllotExpireDays();
        searchModel.setAllotDays(allotMaxDays);
        // 查询非私有网红的标签数量
        searchModel.setIsPrivateKol(YesNoStatus.NO.getCode());

        if (searchModel.getTagNameList().isEmpty() && oConvertUtils.isEmpty(searchModel.getProductId())
                && oConvertUtils.isEmpty(searchModel.getAttributeIds())) {
            return Result.error("请选择要分配的标签、产品或社媒属性");
        }
        kolCelebrityService.checkParams(searchModel);
        // 判断是否存在查询条件
        if (oConvertUtils.isNotEmpty(searchModel.getAttributeIds())) {
            int total = tkTagsService.getAllotTotal();
            searchModel.setTotal(total);
        }

        if (oConvertUtils.isEmpty(searchModel.getMinFollowers())) {
            // 查询TK分配规则最小粉丝数
            Integer minNum = ruleService.getMinNum(2);
            searchModel.setMinFollowers(minNum);
        }

        if (searchModel.getTagNameList().isEmpty()) {
            return Result.ok(0);
        }
        String tempTableName = "temp_" + Thread.currentThread().getId() + "_" + System.currentTimeMillis();
        searchModel.setTempTableName(tempTableName);
        Long notAllotCount = tkTagsService.getUnallottedTagCountPlus(searchModel);
        return Result.ok(notAllotCount);
    }

    /**
     * 获取未分配的网红分页列表
     *
     * @param searchModel
     * @return
     */
    @Operation(summary = "获取未分配的网红分页列表", description = "获取未分配的网红分页列表")
    @PostMapping(value = "/getUnallottedTagList")
    public Result<?> getUnallottedTagList(@RequestBody KolSearchModel searchModel,
                                          @RequestParam(name = PAGE_NO, defaultValue = PAGE_NO_DEFAULT) Integer pageNo,
                                          @RequestParam(name = PAGE_SIZE, defaultValue = PAGE_SIZE_DEFAULT) Integer pageSize,
                                          HttpServletRequest req) {
        // 获取网红分配过期天数配置（默认15天）
        int allotMaxDays = tkTagsService.getAllotExpireDays();

        searchModel.setAllotDays(allotMaxDays);

        String productId = searchModel.getProductId();
        String attributeIds = searchModel.getAttributeIds();
        if (oConvertUtils.isNotEmpty(attributeIds) && oConvertUtils.isEmpty(productId)) {
            // 获取属性信息
            KolAttribute attribute = attributeService.getById(attributeIds);

            if (attribute != null)
                searchModel.setAttributeName(attribute.getAttributeName());

            // 获取社媒属性对应的标签
            tagAttributeRelationService.getAttributeTagNames(searchModel);

            // 默认查询10000条网红数据
            int total = tkTagsService.getAllotTotal();

            searchModel.setTotal(total);
        }

        // 获取最小粉丝数配置
        Integer minNum = ruleService.getMinNum(2);
        searchModel.setMinFollowers(minNum);

        // 参数校验
        kolCelebrityService.checkParams(searchModel);

        if (!searchModel.getTagNameList().isEmpty()) {
            String tempTableName = "temp_" + Thread.currentThread().getId() + "_" + System.currentTimeMillis();
            searchModel.setTempTableName(tempTableName);
            log.info("TK [分配初始化] 创建临时表名: {}", tempTableName);
        }
        Page<KolTagCountModel> page = PageUtil.getOrderItems(pageNo, pageSize, req);
        page.setSearchCount(false);
        // 从数据库查询未分配的网红标签列表
        IPage<TiktokCelebrityTagsModel> pageList = tkTagsService.getUnAlloteTagPageList(page, searchModel);
        return Result.ok(pageList);
    }

    /**
     * 获取未分配的标签及数量汇总
     *
     * @param searchModel
     * @return
     */
    @Operation(summary = "TK未分配的标签及数量汇总查询", description = "TK未分配的标签及数量汇总查询")
    @PostMapping(value = "/getUnallottedTagCountSummary")
    public Result<?> getUnallottedTagCountSummary(@RequestBody KolSearchModel searchModel,
                                                  @RequestParam(name = PAGE_NO, defaultValue = PAGE_NO_DEFAULT) Integer pageNo,
                                                  @RequestParam(name = PAGE_SIZE, defaultValue = PAGE_SIZE_DEFAULT) Integer pageSize,
                                                  HttpServletRequest req) {
        Page<KolTagCountModel> page = PageUtil.getOrderItems(pageNo, pageSize, req);
        // 获取网红分配过期天数 默认30天
        int allotMaxDays = tkTagsService.getAllotExpireDays();
        searchModel.setAllotDays(allotMaxDays);
        page.setSearchCount(false);
        int count = 5000;
        IPage<KolTagCountModel> pageList = tkTagsService.getUnallottedTagCountSummary(page, searchModel);
        if (pageList.getRecords().isEmpty()) {
            count = 0;
        } else if (pageList.getRecords().size() < pageSize) {
            count = pageList.getRecords().size();
        }
        pageList.setTotal(count);
        return Result.ok(pageList);
    }

    /**
     * @description: 根据平台获取最小粉丝数
     * @author: nqr
     * @date: 2025/7/24 14:16
     **/
    @AutoLog(value = "根据平台获取最小粉丝数")
    @Operation(summary = "根据平台获取最小粉丝数", description = "根据平台获取最小粉丝数")
    @GetMapping(value = "/getMinNum")
    public Result<?> getMinNum(@RequestParam(name = "platformType", required = true) Integer platformType) {
        return Result.ok(ruleService.getMinNum(platformType));
    }

    /**
     * 分配网红标签
     *
     * @param
     * @return
     */
    @AutoLog(value = "网红tag表-分配网红")
    @Operation(summary = "网红tag表-分配网红", description = "网红tag表-分配网红")
    @PostMapping(value = "/allotTags")
    public Result<?> allotTags(@RequestBody KolSearchModel searchModel) {
        // region ---参数校验---
        // 先获取网红顾问id和网红顾问名称
        String counselorId = searchModel.getCounselorId();
        String counselorName = searchModel.getCounselorName();
        if (oConvertUtils.isEmpty(counselorId) || oConvertUtils.isEmpty(counselorName)) {
            return Result.error("请选择分配网红顾问");
        }
        // 判断入参的标签列表是否为空 因标签较多 暂先控制必须传入要分配的标签
        if (oConvertUtils.listIsEmpty(searchModel.getTagNameList())) {
            return Result.error("请选择要分配的网红标签");
        }
        // endregion

        // 获取网红分配过期天数 默认30天
        int allotMaxDays = tkTagsService.getAllotExpireDays();
        searchModel.setAllotDays(allotMaxDays);
        // 从数据库中查询未分配网红标签列表
        // List<UserTagAllotModel> unallottedTagList =
        // tkTagsService.getUnallottedTagList(searchModel);
        List<UserTagAllotModel> unallottedTagList = tkTagsService.getUnallottedTagListPlus(searchModel);
        if (unallottedTagList.isEmpty()) {
            return Result.error("没有可分配的网红标签");
        }
        // 创建网红标签表更新实体列表
        List<TiktokCelebrityTags> tkTagsUpdateList = new ArrayList<>();
        // 创建网红标签顾问批量新增实体列表
        List<TiktokCelebrityTagsCounselor> tkTagsCounselorAddList = new ArrayList<>();
        // 创建网红标签顾问批量更新实体列表
        List<TiktokCelebrityTagsCounselor> tkTagsCounselorUpdateList = new ArrayList<>();
        // 根据网红顾问id查询网红标签顾问列表
        List<TiktokCelebrityTagsCounselor> tkTagsCounselorList = tkTagsCounselorService
                .getListByCounselorId(counselorId);

        // 循环未分配网红标签列表
        unallottedTagList.forEach(x -> {
            // 组合网红标签表更新列表参数(不区分私有和普通网红)
            // 组合标签顾问列表(更新列表和新增列表)
            processTagAllot(searchModel, x, tkTagsCounselorList, tkTagsUpdateList, tkTagsCounselorAddList,
                    tkTagsCounselorUpdateList);

        });

        // 组装分配日志实体
        KolAllotLogDetail kolAllotLogDetailEntity = createKolAllotLogDetail(searchModel, unallottedTagList);

        // 组装数据库更新数据
        tkTagsService.saveAllotTags(tkTagsUpdateList, tkTagsCounselorAddList, tkTagsCounselorUpdateList,
                kolAllotLogDetailEntity);

        // 处理返回值 不处理私有网红的数据统计 如果列表中含有私有网红数据 则返回的分配数量应进行过滤
        long allotNum = unallottedTagList.stream().filter(x -> oConvertUtils.isEmpty(x.getCelebrityPrivateId()))
                .count();
        int privateNum = unallottedTagList.size() - (int) allotNum;
        TagAllotResultModel tagAllotResultModel = new TagAllotResultModel();
        tagAllotResultModel.setCounselorId(counselorId);
        tagAllotResultModel.setCounselorName(counselorName);
        tagAllotResultModel.setPrivateNum(privateNum);
        tagAllotResultModel.setAllotNum((int) allotNum);

        return Result.OK("分配成功", tagAllotResultModel);
    }

    /**
     * @description: 分配网红标签
     * @author: nqr
     * @date: 2025/7/23 17:23
     **/
    @AutoLog(value = "网红tag表-分配网红")
    @Operation(summary = "网红tag表-分配网红", description = "网红tag表-分配网红")
    @PostMapping(value = "/allotTagsNew")
    public Result<?> allotTagsNew(@RequestBody KolSearchModel searchModel) {
        List<KolAllotLog> list = kolAllotLogService.lambdaQuery().eq(KolAllotLog::getAllotStatus, 1)
                .eq(KolAllotLog::getAllotType, searchModel.getAllotType()).list();
        if (!list.isEmpty()) {
            // 判断是否超过半小时
            KolAllotLog kolAllotLog = list.get(0);
            Date allotStartTime = kolAllotLog.getAllotStartTime();
            long diff = (new Date().getTime() - allotStartTime.getTime()) / 1000;
            if (diff < 1800) {
                return Result.error("当前有任务正在执行，请稍后再试");
            } else {
                kolAllotLogService.updateKolAllotLog(kolAllotLog.getId(), CommonConstant.KOL_ALOT_LOG_STATUS_FAIL,
                        "处理超时，请重新提交");
            }
        }
        // 保存分配日志
        String allotLogId = IdWorker.get32UUID();
        kolAllotLogService.createKolAllotLog(searchModel, allotLogId);
        String userName = getUserNameByToken();
        // 异步执行分配流程
        CompletableFuture.runAsync(() -> {
            long methodStartTime = System.currentTimeMillis();
            log.info("[网红标签分配] 开始执行网红标签分配流程，请求参数: 产品ID={}, 属性ID={}, 标签数量={}, 顾问数量={}",
                    searchModel.getProductId(), searchModel.getAttributeIds(),
                    searchModel.getTagNameList() != null ? searchModel.getTagNameList().size() : 0,
                    searchModel.getCelebrityCounselorList() != null ? searchModel.getCelebrityCounselorList().size()
                            : 0);

            try {
                // 1. 参数验证
                long startTime = System.currentTimeMillis();
                Result<?> validationResult = validateInputParameters(searchModel);
                long duration = System.currentTimeMillis() - startTime;
                log.info("[网红标签分配] 参数验证完成，耗时: {}ms", duration);

                if (!validationResult.isSuccess()) {
                    log.warn("[网红标签分配] 参数验证失败，原因: {}", validationResult.getMessage());
                    kolAllotLogService.updateKolAllotLog(allotLogId, CommonConstant.KOL_ALOT_LOG_STATUS_FAIL,
                            validationResult.getMessage());
                }

                // 2. 初始化配置和获取基础数据
                startTime = System.currentTimeMillis();
                InitializationData initData = initializeAllocationData(searchModel);
                duration = System.currentTimeMillis() - startTime;
                log.info("[网红标签分配] 初始化配置和基础数据完成，耗时: {}ms，是否有错误: {}", duration, initData.hasError());

                if (initData.hasError()) {
                    Result<?> errorResult = initData.getErrorResult();
                    log.error("[网红标签分配] 初始化数据失败，错误信息: {}", errorResult.getMessage());
                    kolAllotLogService.updateKolAllotLog(allotLogId, CommonConstant.KOL_ALOT_LOG_STATUS_FAIL,
                            errorResult.getMessage());
                    return;
                }

                // 3. 过滤和处理网红标签数据
                startTime = System.currentTimeMillis();
                ProcessedTagData processedData = processTagData(initData);
                duration = System.currentTimeMillis() - startTime;
                log.info("[网红标签分配] 过滤和处理网红标签数据完成，耗时: {}ms", duration);

                // 4. 处理私有网红标签
                startTime = System.currentTimeMillis();
                handlePrivateCelebrityTags(processedData, userName);
                duration = System.currentTimeMillis() - startTime;
                log.info("[网红标签分配] 处理私有网红标签完成，耗时: {}ms", duration);

                // 5. 执行网红标签分配
                startTime = System.currentTimeMillis();
                AllocationResult allocationResult = performTagAllocation(processedData, initData, userName);
                duration = System.currentTimeMillis() - startTime;
                log.info("[网红标签分配] 执行网红标签分配完成，耗时: {}ms", duration);

                // 6. 创建分配日志
                startTime = System.currentTimeMillis();
                createAllocationLogs(processedData, allocationResult, allotLogId);
                duration = System.currentTimeMillis() - startTime;
                log.info("[网红标签分配] 创建分配日志完成，耗时: {}ms", duration);

                // 7. 保存分配结果到数据库
                startTime = System.currentTimeMillis();
                saveAllocationResults(allocationResult, searchModel, userName);
                duration = System.currentTimeMillis() - startTime;
                log.info("[网红标签分配] 保存分配结果到数据库完成，耗时: {}ms", duration);

                // 8. 构建并返回响应结果
                // Result<?> response = buildSuccessResponse(allocationResult.getResultList());

                long totalDuration = System.currentTimeMillis() - methodStartTime;
                log.info("[网红标签分配] 构建响应结果完成，耗时: {}ms，总流程耗时: {}ms", duration, totalDuration);
                String resultList = kolAllotLogService.createResultJson(allocationResult.getResultList());
                kolAllotLogService.updateKolAllotLog(allotLogId, CommonConstant.KOL_ALOT_LOG_STATUS_FINISH, resultList);
            } catch (Exception e) {
                long totalDuration = System.currentTimeMillis() - methodStartTime;
                log.error("[网红标签分配] 分配流程执行异常，总耗时: {}ms，错误信息: {}", totalDuration, e.getMessage(), e);
                kolAllotLogService.updateKolAllotLog(allotLogId, CommonConstant.KOL_ALOT_LOG_STATUS_FAIL,
                        "网红标签分配失败，请稍后重试");
            }
        });
       /* // 分配后删除缓存，防止其他人查询错误
        try {
            redisUtil.del("screening_default");
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        return Result.ok("网红标签分配任务已提交，请稍后查看结果");
    }

    /**
     * 验证输入参数
     */
    private Result<?> validateInputParameters(KolSearchModel searchModel) {
        List<String> counselorIds = searchModel.getCelebrityCounselorList();

        // 验证顾问ID列表不能为空
        if (counselorIds.isEmpty()) {
            return Result.error("请选择网红顾问");
        }

        // 验证标签列表不能为空
        if (oConvertUtils.listIsEmpty(searchModel.getTagNameList()) && oConvertUtils.isEmpty(searchModel.getProductId())
                && oConvertUtils.isEmpty(searchModel.getAttributeIds())) {
            return Result.error("请选择要分配的标签、产品或社媒属性");
        }

        if (oConvertUtils.isEmpty(searchModel.getCountryCode())) {
            return Result.error("请选择国家");
        }

        if (!searchModel.getPlatformType().equals(CommonConstant.TK)) {
            return Result.error("方法调用错误,平台不一致");
        }
        return Result.ok();
    }

    /**
     * 初始化分配所需的基础数据
     */
    private InitializationData initializeAllocationData(KolSearchModel searchModel) {
        long methodStartTime = System.currentTimeMillis();
        log.info("TK [分配初始化] 开始初始化分配数据，查询条件: 产品ID={}, 社媒ID={}, 标签数量={}",
                searchModel.getProductId(), searchModel.getAttributeIds(),
                searchModel.getTagNameList() != null ? searchModel.getTagNameList().size() : 0);

        InitializationData data = new InitializationData();

        try {
            // 获取网红分配过期天数配置（默认15天）
            long startTime = System.currentTimeMillis();
            int allotMaxDays = tkTagsService.getAllotExpireDays();
            long duration = System.currentTimeMillis() - startTime;
            log.info("TK [分配初始化] 获取分配过期天数配置完成，耗时: {}ms，过期天数: {}天", duration, allotMaxDays);

            searchModel.setAllotDays(allotMaxDays);

            String productId = searchModel.getProductId();
            String attributeIds = searchModel.getAttributeIds();
            if (oConvertUtils.isNotEmpty(attributeIds) && oConvertUtils.isEmpty(productId)) {
                // 获取属性信息
                startTime = System.currentTimeMillis();
                KolAttribute attribute = attributeService.getById(attributeIds);
                duration = System.currentTimeMillis() - startTime;
                log.info("TK [分配初始化] 获取属性信息完成，耗时: {}ms，属性名称: {}", duration,
                        attribute != null ? attribute.getAttributeName() : "null");

                if (attribute != null)
                    searchModel.setAttributeName(attribute.getAttributeName());

                // 获取社媒属性对应的标签
                startTime = System.currentTimeMillis();
                tagAttributeRelationService.getAttributeTagNames(searchModel);
                duration = System.currentTimeMillis() - startTime;
                log.info("TK [分配初始化] 获取属性对应标签完成，耗时: {}ms，标签数量: {}", duration,
                        searchModel.getTagNameList() != null ? searchModel.getTagNameList().size() : 0);

                // 默认查询10000条网红数据
                startTime = System.currentTimeMillis();
                int total = tkTagsService.getAllotTotal();
                duration = System.currentTimeMillis() - startTime;
                log.info("TK [分配初始化] 获取分配总数配置完成，耗时: {}ms，总数: {}", duration, total);

                searchModel.setTotal(total);
            }

            // 获取最小粉丝数配置
            if (oConvertUtils.isEmpty(searchModel.getMinFollowers())) {
                startTime = System.currentTimeMillis();
                Integer minNum = ruleService.getMinNum(2);
                duration = System.currentTimeMillis() - startTime;
                log.info("TK [分配初始化] 获取最小粉丝数配置完成，耗时: {}ms，最小粉丝数: {}", duration, minNum);
                searchModel.setMinFollowers(minNum);
            }

            // 参数校验
            startTime = System.currentTimeMillis();
            kolCelebrityService.checkParams(searchModel);
            duration = System.currentTimeMillis() - startTime;
            log.info("TK [分配初始化] 参数校验完成，耗时: {}ms", duration);

            if (!searchModel.getTagNameList().isEmpty()) {
                String tempTableName = "temp_" + Thread.currentThread().getId() + "_" + System.currentTimeMillis();
                searchModel.setTempTableName(tempTableName);
                log.info("TK [分配初始化] 创建临时表名: {}", tempTableName);
            }

            // 从数据库查询未分配的网红标签列表
            startTime = System.currentTimeMillis();
            List<TiktokCelebrityTagsModel> unallottedTagList = tkTagsService.getUnAlloteTagList(searchModel);
            duration = System.currentTimeMillis() - startTime;
            log.info("TK [分配初始化] 查询未分配网红标签列表完成，耗时: {}ms，数量: {}", duration, unallottedTagList.size());

            if (unallottedTagList.isEmpty()) {
                log.warn("TK [分配初始化] 没有找到可分配的网红标签");
                data.setErrorResult(Result.error("没有可分配的网红标签"));
                return data;
            }

            // 批量设置产品id
            if (oConvertUtils.isNotEmpty(searchModel.getProductId())
                    || oConvertUtils.isNotEmpty(searchModel.getAttributeIds())) {
                startTime = System.currentTimeMillis();
                unallottedTagList.forEach(tag -> {
                    tag.setProductId(searchModel.getProductId());
                    tag.setAttributeId(searchModel.getAttributeIds());
                });
                duration = System.currentTimeMillis() - startTime;
                log.info("TK [分配初始化] 批量设置产品ID和属性ID完成，耗时: {}ms", duration);
            }

            // 获取顾问用户信息
            List<String> counselorIds = searchModel.getCelebrityCounselorList();
            startTime = System.currentTimeMillis();
            List<LoginUser> counselorList = sysBaseAPI.getUserByIds(counselorIds);
            duration = System.currentTimeMillis() - startTime;
            log.info("TK [分配初始化] 获取顾问用户信息完成，耗时: {}ms，顾问数量: {}", duration,
                    counselorList != null ? counselorList.size() : 0);

            // 获取分配规则
            startTime = System.currentTimeMillis();
            List<TiktokCelebrityRule> rules = ruleService.lambdaQuery()
                    .eq(TiktokCelebrityRule::getPlatformType, CommonConstant.TK)
                    .list();
            duration = System.currentTimeMillis() - startTime;
            log.info("TK [分配初始化] 获取分配规则完成，耗时: {}ms，规则数量: {}", duration, rules.size());

            if (rules.isEmpty()) {
                log.warn("TK [分配初始化] 未获取到分配规则");
                data.setErrorResult(Result.error("未获取到分配规则"));
                return data;
            }

            // 获取最低等级的最小粉丝数要求
            startTime = System.currentTimeMillis();
            Integer minFollowerThreshold = rules.stream()
                    .max(Comparator.comparing(TiktokCelebrityRule::getRuleLevel))
                    .get()
                    .getMinNum();
            duration = System.currentTimeMillis() - startTime;
            log.info("TK [分配初始化] 计算最小粉丝数阈值完成，耗时: {}ms，阈值: {}", duration, minFollowerThreshold);

            data.setTkUnallottedTagList(unallottedTagList);
            data.setCounselorList(counselorList);
            data.setRules(rules);
            data.setMinFollowerThreshold(minFollowerThreshold);

            long totalDuration = System.currentTimeMillis() - methodStartTime;
            log.info("TK [分配初始化] 初始化分配数据完成，总耗时: {}ms，未分配标签数: {}, 顾问数: {}, 规则数: {}",
                    totalDuration, unallottedTagList.size(),
                    counselorList != null ? counselorList.size() : 0, rules.size());

        } catch (Exception e) {
            long totalDuration = System.currentTimeMillis() - methodStartTime;
            log.error("TK [分配初始化] 初始化分配数据失败，耗时: {}ms，错误信息: {}", totalDuration, e.getMessage(), e);
            data.setErrorResult(Result.error("分配失败,请重试！"));
        }

        return data;
    }

    /**
     * 处理网红标签数据，过滤不符合条件的数据
     */
    private ProcessedTagData processTagData(InitializationData initData) {
        ProcessedTagData processedData = new ProcessedTagData();

        List<TiktokCelebrityTagsModel> unallottedTagList = initData.getTkUnallottedTagList();

        // 统计已有私有网红的分配情况
        Map<String, List<TiktokCelebrityTags>> existingPrivateTagMap = unallottedTagList.stream()
                .collect(Collectors
                        .groupingBy(tag -> tag.getCelebrityCounselorId() + "_" + tag.getCelebrityCounselorName()));

        processedData.setTkEligibleTagList(unallottedTagList);
        processedData.setExcludedCelebrityIds(new ArrayList<>());
        processedData.setTkExistingPrivateTagMap(existingPrivateTagMap);
        processedData.initializeLists();

        return processedData;
    }

    /**
     * 处理私有网红标签分配
     */
    private void handlePrivateCelebrityTags(ProcessedTagData processedData, String userName) {
        List<TiktokCelebrityTagsModel> eligibleTagList = processedData.getTkEligibleTagList();
        List<TiktokCelebrityTags> tkTagsUpdateList = processedData.getTkTagsUpdateList();

        // 筛选出拥有私有顾问ID的网红标签,判断建联时间是否超过一年 2025年7月24日13:48:46
        List<TiktokCelebrityTagsModel> privateCelebrityTags = eligibleTagList.stream()
                .filter(tag -> oConvertUtils.isNotEmpty(tag.getCelebrityPrivateId())
                        && tag.getContractTime() != null
                        && tag.getContractTime().after(DateUtils.addMonths(new Date(), -3)))
                .collect(Collectors.toList());

        if (!privateCelebrityTags.isEmpty()) {
            // 为私有网红创建标签分配记录
            privateCelebrityTags.forEach(tagModel -> {
                TiktokCelebrityTags celebrityTags = createTiktokCelebrityTags(
                        tagModel,
                        tagModel.getCelebrityCounselorId(),
                        tagModel.getCelebrityCounselorName(),
                        userName);
                tkTagsUpdateList.add(celebrityTags);
            });

            // 从待分配列表中移除已处理的私有网红标签
            List<String> processedUniqueIds = privateCelebrityTags.stream()
                    .map(TiktokCelebrityTags::getUniqueId)
                    .collect(Collectors.toList());

            List<TiktokCelebrityTagsModel> remainingTags = eligibleTagList.stream()
                    .filter(tag -> !processedUniqueIds.contains(tag.getUniqueId()))
                    .collect(Collectors.toList());

            processedData.setTkEligibleTagList(remainingTags);
        }
    }

    /**
     * 执行网红标签分配算法
     */
    private AllocationResult performTagAllocation(ProcessedTagData processedData, InitializationData initData,
                                                  String userName) {
        AllocationResult result = new AllocationResult();

        // 根据分配规则对网红进行分组
        List<List<TiktokCelebrityTagsModel>> groupedCelebrityTags = groupCelebritiesByRules(
                processedData.getTkEligibleTagList(),
                initData.getRules());

        // 执行轮询分配算法
        List<TiktokCelebrityTags> allocatedTags = performRoundRobinAllocation(
                groupedCelebrityTags,
                initData.getCounselorList(),
                processedData, userName);

        // 合并所有更新的标签记录
        if (!allocatedTags.isEmpty()) {
            processedData.getTkTagsUpdateList().addAll(allocatedTags);
        }

        // 生成分配结果统计
        List<KolTagsResultModel> resultList = generateAllocationResults(processedData);

        result.setTkAllocatedTags(allocatedTags);
        result.setResultList(resultList);
        result.setTkTagsUpdateList(processedData.getTkTagsUpdateList());
        result.setTkTagsCounselorAddList(processedData.getTkTagsCounselorAddList());
        return result;
    }

    /**
     * 根据分配规则对网红进行分组
     */
    private List<List<TiktokCelebrityTagsModel>> groupCelebritiesByRules(
            List<TiktokCelebrityTagsModel> eligibleTags,
            List<TiktokCelebrityRule> rules) {
        long methodStartTime = System.currentTimeMillis();
        log.info("TK [网红分组] 开始按规则对网红进行分组，待分组网红数量: {}, 规则数量: {}",
                eligibleTags.size(), rules.size());

        List<List<TiktokCelebrityTagsModel>> groupedTags = new ArrayList<>();
        int totalProcessed = 0;

        // 按规则等级对网红进行分组
        for (int i = 0; i < rules.size(); i++) {
            long ruleStartTime = System.currentTimeMillis();
            TiktokCelebrityRule rule = rules.get(i);
            String ruleLevel = rule.getRuleLevel();
            Integer minFollowers = rule.getMinNum();
            Integer maxFollowers = rule.getMaxNum();

            log.info("TK [网红分组] 开始处理第{}个规则，等级: {}, 最小粉丝数: {}, 最大粉丝数: {}",
                    i + 1, ruleLevel, minFollowers, maxFollowers);

            // 根据粉丝数范围筛选网红
            long filterStartTime = System.currentTimeMillis();
            List<TiktokCelebrityTagsModel> ruleMatchedTags = eligibleTags.stream()
                    .filter(celebrity -> {
                        long followerCount = celebrity.getAuthorFollowerCount().longValue();
                        // 最高等级规则(level=1)不设上限
                        if ("1".equals(ruleLevel)) {
                            return followerCount >= minFollowers;
                        } else {
                            return followerCount >= minFollowers && followerCount < maxFollowers;
                        }
                    })
                    .collect(Collectors.toList());

            long filterDuration = System.currentTimeMillis() - filterStartTime;
            log.info("TK [网红分组] 规则{}筛选完成，耗时: {}ms，符合条件的网红数量: {}",
                    ruleLevel, filterDuration, ruleMatchedTags.size());

            // 随机打乱顺序确保分配公平性
            long shuffleStartTime = System.currentTimeMillis();
            List<TiktokCelebrityTagsModel> shuffledTags = randomizeList(ruleMatchedTags);
            long shuffleDuration = System.currentTimeMillis() - shuffleStartTime;

            if (!shuffledTags.isEmpty()) {
                groupedTags.add(shuffledTags);
                totalProcessed += shuffledTags.size();

                // 统计该分组中的粉丝数分布
                long minFollowersInGroup = shuffledTags.stream()
                        .mapToLong(tag -> tag.getAuthorFollowerCount().longValue())
                        .min().orElse(0);
                long maxFollowersInGroup = shuffledTags.stream()
                        .mapToLong(tag -> tag.getAuthorFollowerCount().longValue())
                        .max().orElse(0);
                double avgFollowersInGroup = shuffledTags.stream()
                        .mapToLong(tag -> tag.getAuthorFollowerCount().longValue())
                        .average().orElse(0.0);

                long ruleCompleteDuration = System.currentTimeMillis() - ruleStartTime;
                log.info("TK [网红分组] 规则{}分组完成，总耗时: {}ms(筛选: {}ms, 打乱: {}ms)，" +
                                "分组网红数量: {}, 粉丝数范围: {}-{}, 平均粉丝数: {}",
                        ruleLevel, ruleCompleteDuration, filterDuration, shuffleDuration,
                        shuffledTags.size(), minFollowersInGroup, maxFollowersInGroup,
                        Math.round(avgFollowersInGroup));
            } else {
                long ruleCompleteDuration = System.currentTimeMillis() - ruleStartTime;
                log.warn("TK [网红分组] 规则{}没有符合条件的网红，耗时: {}ms",
                        ruleLevel, ruleCompleteDuration);
            }
        }

        long methodCompleteDuration = System.currentTimeMillis() - methodStartTime;
        log.info("TK [网红分组] 所有规则分组完成，总耗时: {}ms，" +
                        "初始网红数: {}, 处理后网红数: {}, 分组数: {}, 利用率: {}%",
                methodCompleteDuration, eligibleTags.size(), totalProcessed, groupedTags.size(),
                eligibleTags.size() > 0 ? Math.round((double) totalProcessed / eligibleTags.size() * 100) : 0);

        return groupedTags;
    }

    /**
     * @description: 现有分配方法，分配出问题的话改为这个方法
     * @author: nqr
     * @date: 2025/11/27 9:59
     **/
    /*
     * private List<TiktokCelebrityTags> performRoundRobinAllocation(
     * List<List<TiktokCelebrityTagsModel>> groupedTags,
     * List<LoginUser> counselorList,
     * ProcessedTagData processedData,
     * String userName) {
     *
     * List<TiktokCelebrityTags> allocatedTags = new ArrayList<>();
     *
     * // 初始化顾问分配计数器
     * Map<String, Integer> counselorAllocationCount = new HashMap<>();
     * for (LoginUser counselor : counselorList) {
     * counselorAllocationCount.put(counselor.getId(), 0);
     * }
     *
     * // 遍历每个规则分组
     * for (List<TiktokCelebrityTagsModel> ruleGroup : groupedTags) {
     * int totalTagsInGroup = ruleGroup.size();
     * if (totalTagsInGroup == 0) continue;
     *
     * int counselorCount = counselorList.size();
     *
     * // 对顾问按当前分配数量升序排序（数量少的优先）
     * List<LoginUser> sortedCounselors = counselorList.stream()
     * .sorted(Comparator.comparingInt(c ->
     * counselorAllocationCount.getOrDefault(c.getId(), 0)))
     * .collect(Collectors.toList());
     *
     * // 为当前规则组中的每个标签，按排序后的顾问顺序分配（类似轮询）
     * for (int i = 0; i < totalTagsInGroup; i++) {
     * TiktokCelebrityTagsModel tagModel = ruleGroup.get(i);
     * LoginUser assignedCounselor = sortedCounselors.get(i % counselorCount); //
     * 轮询分配
     * String counselorId = assignedCounselor.getId();
     * String counselorName = assignedCounselor.getUsername();
     *
     * // 创建网红标签更新记录
     * TiktokCelebrityTags celebrityTag = createTiktokCelebrityTags(
     * tagModel, counselorId, counselorName, userName
     * );
     * allocatedTags.add(celebrityTag);
     *
     * // 创建顾问-标签关联记录
     * TiktokCelebrityTagsCounselor tagsCounselor = createTagsCounselorNew(
     * tagModel, counselorId, counselorName
     * );
     * processedData.getTkTagsCounselorAddList().add(tagsCounselor);
     *
     * // 更新模型中的顾问信息
     * tagModel.setCelebrityCounselorId(counselorId);
     * tagModel.setCelebrityCounselorName(counselorName);
     *
     * // 更新该顾问的分配计数
     * counselorAllocationCount.put(counselorId,
     * counselorAllocationCount.get(counselorId) + 1);
     * }
     * }
     *
     * return allocatedTags;
     * }
     */

    /**
     * @description: 按照网红进行分配
     * @author: nqr
     * @date: 2025/11/27 10:20
     **/
    private List<TiktokCelebrityTags> performRoundRobinAllocation(
            List<List<TiktokCelebrityTagsModel>> groupedTags,
            List<LoginUser> counselorList,
            ProcessedTagData processedData,
            String userName) {

        List<TiktokCelebrityTags> allocatedTags = new ArrayList<>();

        // 初始化顾问分配计数器（记录每个顾问已分配的标签总数）
        Map<String, Integer> counselorAllocationCount = new HashMap<>();
        for (LoginUser counselor : counselorList) {
            counselorAllocationCount.put(counselor.getId(), 0);
        }

        // 遍历每个规则分组，但需要跨组协调顾问分配以实现整体均衡
        for (List<TiktokCelebrityTagsModel> ruleGroup : groupedTags) {
            if (ruleGroup.isEmpty())
                continue;

            // 在当前 ruleGroup 内按 authorId 聚合
            Map<String, List<TiktokCelebrityTagsModel>> authorGroupMap = ruleGroup.stream()
                    .collect(Collectors.groupingBy(TiktokCelebrityTagsModel::getAuthorId));

            // 将每个 authorId 的分组转为列表，并按标签数量降序排序（标签多的网红优先分配）
            List<Map.Entry<String, List<TiktokCelebrityTagsModel>>> authorGroups = new ArrayList<>(
                    authorGroupMap.entrySet());
            authorGroups.sort((e1, e2) -> Integer.compare(e2.getValue().size(), e1.getValue().size()));

            for (Map.Entry<String, List<TiktokCelebrityTagsModel>> authorEntry : authorGroups) {
                String authorId = authorEntry.getKey();
                List<TiktokCelebrityTagsModel> tagsForAuthor = authorEntry.getValue();
                int tagCount = tagsForAuthor.size();

                // 每次分配前，按当前分配总数升序排序顾问（分配少的优先）
                List<LoginUser> sortedCounselors = counselorList.stream()
                        .sorted(Comparator.comparingInt(c -> counselorAllocationCount.getOrDefault(c.getId(), 0)))
                        .collect(Collectors.toList());

                // 选择分配数最少的顾问（第一个）
                LoginUser assignedCounselor = sortedCounselors.get(0);
                String counselorId = assignedCounselor.getId();
                String counselorName = assignedCounselor.getUsername();

                // 为该 authorId 下的所有标签分配同一个顾问
                for (TiktokCelebrityTagsModel tagModel : tagsForAuthor) {
                    TiktokCelebrityTags celebrityTag = createTiktokCelebrityTags(
                            tagModel, counselorId, counselorName, userName);
                    allocatedTags.add(celebrityTag);

                    TiktokCelebrityTagsCounselor tagsCounselor = createTagsCounselorNew(
                            tagModel, counselorId, counselorName);
                    processedData.getTkTagsCounselorAddList().add(tagsCounselor);

                    tagModel.setCelebrityCounselorId(counselorId);
                    tagModel.setCelebrityCounselorName(counselorName);
                }

                // 更新顾问分配总数：增加的是该 authorId 的标签数量
                counselorAllocationCount.merge(counselorId, tagCount, Integer::sum);
            }
        }

        return allocatedTags;
    }

    /**
     * 计算特定顾问应分配的网红标签数量和具体标签
     */
    private List<TiktokCelebrityTagsModel> calculateCounselorAllocation(
            List<TiktokCelebrityTagsModel> tagList, int counselorCount, int counselorIndex) {

        int totalTags = tagList.size();
        if (totalTags == 0) {
            return Collections.emptyList();
        }

        // 计算平均分配数量和余数
        int tagsPerCounselor = totalTags / counselorCount;
        int remainderTags = totalTags % counselorCount;

        // 计算当前顾问的分配范围
        int startIndex = counselorIndex * tagsPerCounselor + Math.min(counselorIndex, remainderTags);
        int endIndex = startIndex + tagsPerCounselor + (counselorIndex < remainderTags ? 1 : 0);

        return tagList.subList(startIndex, Math.min(endIndex, totalTags));
    }

    /**
     * 生成分配结果统计
     */
    private List<KolTagsResultModel> generateAllocationResults(
            ProcessedTagData processedData) {

        List<KolTagsResultModel> resultList = new ArrayList<>();

        // 统计私有网红分配情况
        processedData.getTkExistingPrivateTagMap().forEach((key, tagList) -> {
            String[] keyParts = key.split("_");
            String counselorId = keyParts[0];
            String counselorName = oConvertUtils.isEmpty(keyParts[1]) || "null".equals(keyParts[1]) ? "未知顾问"
                    : keyParts[1];
            if (oConvertUtils.isNotEmpty(counselorId)) {
                int count = (int) tagList.stream().map(TiktokCelebrityTags::getUniqueId).distinct().count();
                createOrUpdateResult(counselorId, counselorName, count, 0, count, resultList);
            }
        });

        // 统计新分配的网红情况
        Map<String, List<TiktokCelebrityTags>> newAllocationMap = processedData.getTkEligibleTagList()
                .stream()
                .collect(Collectors
                        .groupingBy(tag -> tag.getCelebrityCounselorId() + "_" + tag.getCelebrityCounselorName()));

        newAllocationMap.forEach((key, tagList) -> {
            String[] keyParts = key.split("_");
            String counselorId = keyParts[0];
            String counselorName = keyParts[1];

            int count = (int) tagList.stream().map(TiktokCelebrityTags::getUniqueId).distinct().count();
            createOrUpdateResult(counselorId, counselorName, count, count, 0, resultList);
        });

        // 按分配数量和建联数量排序
        return resultList.stream()
                .sorted(Comparator.comparing(KolTagsResultModel::getIsAllotNum)
                        .thenComparing(KolTagsResultModel::getBuildAllianceNum)
                        .reversed())
                .collect(Collectors.toList());
    }

    /**
     * 创建或更新结果统计记录
     */
    private void createOrUpdateResult(String counselorId, String counselorName,
                                      int buildAllianceNum, int allotNum, int isPrivate,
                                      List<KolTagsResultModel> resultList) {

        // 查找是否已存在该顾问的记录
        Optional<KolTagsResultModel> existingResult = resultList.stream()
                .filter(result -> result.getId().equals(counselorId))
                .findFirst();

        if (existingResult.isPresent()) {
            // 更新现有记录
            KolTagsResultModel result = existingResult.get();
            result.setBuildAllianceNum(buildAllianceNum + result.getBuildAllianceNum());
            result.setIsAllotNum(allotNum + result.getIsAllotNum());
            result.setIsPrivate(isPrivate + result.getIsPrivate());
        } else {
            // 创建新记录
            if (oConvertUtils.isEmpty(counselorName)) {
                return;
            }
            KolTagsResultModel newResult = new KolTagsResultModel();
            newResult.setId(counselorId);
            newResult.setName(counselorName);
            newResult.setBuildAllianceNum(buildAllianceNum);
            newResult.setIsAllotNum(allotNum);
            newResult.setIsPrivate(isPrivate);
            resultList.add(newResult);
        }
    }

    /**
     * 创建分配日志记录
     */
    private void createAllocationLogs(ProcessedTagData processedData, AllocationResult allocationResult,
                                      String allotLogId) {
        if (processedData.getTkTagsUpdateList().isEmpty()) {
            return;
        }

        List<KolAllotLogDetail> logDetailList = allocationResult.getAllotLogDetailList();

        // 按顾问分组统计分配情况
        Map<String, List<TiktokCelebrityTagsModel>> counselorAllocationMap = processedData.getTkEligibleTagList()
                .stream()
                .collect(Collectors
                        .groupingBy(tag -> tag.getCelebrityCounselorId() + "_" + tag.getCelebrityCounselorName()));

        counselorAllocationMap.forEach((counselorKey, tagList) -> {
            String[] keyParts = counselorKey.split("_");
            String counselorId = keyParts[0];
            String counselorName = keyParts[1];

            // 按标签名称统计数量
            JSONObject allocationDetail = new JSONObject();
            Map<String, List<TiktokCelebrityTagsModel>> tagCountMap = tagList.stream()
                    .collect(Collectors.groupingBy(TiktokCelebrityTagsModel::getTagName));

            tagCountMap.forEach((tagName, tags) -> allocationDetail.put(tagName, tags.size()));

            // 创建日志详情记录
            KolAllotLogDetail logDetail = new KolAllotLogDetail();
            logDetail.setId(IdWorker.get32UUID());
            logDetail.setCounselorId(counselorId);
            logDetail.setCounselorName(counselorName);
            logDetail.setPlatformType(CommonConstant.TK);
            logDetail.setAllotContent(allocationDetail.toJSONString());
            logDetail.setAllotTime(new Date());
            logDetail.setIsDelete(0);
            logDetail.setAllotId(allotLogId);

            logDetailList.add(logDetail);
        });
    }

    /**
     * 保存分配结果到数据库
     */
    private void saveAllocationResults(AllocationResult allocationResult, KolSearchModel searchModel, String userName) {
        List<KolTags> kolTagsUpdateList = kolTagsService.getUpdateKolTags(searchModel);
        if (oConvertUtils.listIsNotEmpty(kolTagsUpdateList)) {
            kolTagsUpdateList.forEach(kolTags -> {
                kolTags.setUpdateBy(userName);
            });
        }
        tkTagsService.saveAllotTagsNew(
                allocationResult.getTkTagsUpdateList(),
                allocationResult.getTkTagsCounselorAddList(),
                allocationResult.getAllotLogDetailList(),
                kolTagsUpdateList);
    }

    /**
     * 构建成功响应结果
     */
    private Result<List<KolTagsResultModel>> buildSuccessResponse(List<KolTagsResultModel> resultList) {
        Result<List<KolTagsResultModel>> result = new Result<>();
        result.setSuccess(true);
        result.setCode(CommonConstant.SC_OK_200);
        result.setMessage("分配成功");
        result.setResult(resultList);
        return result;
    }

    /**
     * 创建网红标签顾问关联实体
     */
    private TiktokCelebrityTagsCounselor createTagsCounselorNew(
            TiktokCelebrityTagsModel tagModel, String counselorId, String counselorName) {

        TiktokCelebrityTagsCounselor tagsCounselor = new TiktokCelebrityTagsCounselor();
        tagsCounselor.setId(IdWorker.get32UUID());
        tagsCounselor.setAuthorId(tagModel.getAuthorId());
        tagsCounselor.setTiktokCelebrityTagsId(tagModel.getId());
        tagsCounselor.setCounselorId(counselorId);
        tagsCounselor.setCounselorName(counselorName);
        tagsCounselor.setAllotTime(new Date());
        tagsCounselor.setUniqueId(tagModel.getUniqueId());
        tagsCounselor.setTagName(tagModel.getTagName());
        tagsCounselor.setTagType(tagModel.getTagType());
        tagsCounselor.setAuthorFollowerCount(tagModel.getAuthorFollowerCount());
        tagsCounselor.setCountry(tagModel.getCountry());
        tagsCounselor.setProductId(tagModel.getProductId());
        tagsCounselor.setAttributeId(tagModel.getAttributeId());
        return tagsCounselor;
    }

    /**
     * 创建网红标签实体
     */
    private TiktokCelebrityTags createTiktokCelebrityTags(
            TiktokCelebrityTagsModel tagModel, String counselorId, String counselorName, String userName) {

        TiktokCelebrityTags celebrityTags = new TiktokCelebrityTags();
        celebrityTags.setId(tagModel.getId());
        celebrityTags.setUniqueId(tagModel.getUniqueId());
        celebrityTags.setTagName(tagModel.getTagName());
        celebrityTags.setIsAllot(YesNoStatus.YES.getCode());
        celebrityTags.setCelebrityCounselorId(counselorId);
        celebrityTags.setCelebrityCounselorName(counselorName);
        celebrityTags.setAllotTime(new Date());
        celebrityTags.setPlatformType(2);
        celebrityTags.setUpdateBy(userName);
        celebrityTags.setUpdateTime(new Date());
        return celebrityTags;
    }

    /**
     * 随机打乱列表顺序
     */
    private static List<TiktokCelebrityTagsModel> randomizeList(List<TiktokCelebrityTagsModel> list) {
        if (list.isEmpty()) {
            return Collections.emptyList();
        }
        Collections.shuffle(list, new Random());
        return list;
    }
}
