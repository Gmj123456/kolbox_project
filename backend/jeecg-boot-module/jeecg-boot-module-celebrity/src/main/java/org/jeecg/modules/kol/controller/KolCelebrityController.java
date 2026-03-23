package org.jeecg.modules.kol.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.StopWatch;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.api.IKolCelebrityApi;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.CelebrityPrivateModel;
import org.jeecg.common.util.*;
import org.jeecg.modules.instagram.service.IIgCelebrityService;
import org.jeecg.modules.instagram.service.IIgCelebrityTagsService;
import org.jeecg.modules.kol.entity.KolAttribute;
import org.jeecg.modules.kol.entity.KolContact;
import org.jeecg.modules.kol.model.KolBaseModel;
import org.jeecg.modules.kol.model.KolCelebrityModel;
import org.jeecg.modules.kol.model.KolSearchModel;
import org.jeecg.modules.kol.service.*;
import org.jeecg.modules.tiktok.service.ITiktokCelebrityRuleService;
import org.jeecg.modules.tiktok.service.ITiktokCelebrityService;
import org.jeecg.modules.tiktok.service.ITiktokCelebrityTagsService;
import org.jeecg.modules.youtube.service.IYtCelebrityService;
import org.jeecg.modules.youtube.service.IYtCelebrityTagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @Author: nqr
 * @Date: 2025/7/17 18:59
 * @Description:
 **/
@Tag(name = "网红筛选")
@RestController
@RequestMapping("/kol/screening")
@Slf4j
public class KolCelebrityController {
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
    private ITiktokCelebrityTagsService tkTagsService;
    @Resource
    private IYtCelebrityTagsService ytTagsService;
    @Resource
    private IIgCelebrityTagsService igTagsService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ITiktokCelebrityRuleService ruleService;
    @Resource
    private IKolAttributeService attributeService;
    @Resource
    private IKolTagAttributeRelationService tagAttributeRelationService;
    @Autowired
    private IKolCelebrityApi kolCelebrityApi;

    /**
     * 分页列表查询
     *
     * @param searchModel
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "网红筛选-分页列表查询")
    @Operation(summary = "网红筛选-分页列表查询", description = "网红筛选-分页列表查询")
    @PostMapping(value = "/list")
    public Result<?> queryPageList(@RequestBody KolSearchModel searchModel, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        // 记录起始执行时间
        long startExecTime = System.currentTimeMillis();
        long stepStartTime;

        // 1. 参数验证
        Integer platformType = searchModel.getPlatformType();
        if (oConvertUtils.isEmpty(platformType)) {
            return Result.error("平台类型不能为空");
        }

/*        boolean isSpecial = false;
        // 默认加载列表第一页查询redis缓存
        if (isSpecialCondition(searchModel)) {
            //判断是否存在redis缓存
            if (redisUtil.hasKey("screening_default")) {
                return handleSpecialCondition(pageNo, pageSize);
            } else {
                isSpecial = true;
            }
        }*/

        // 2. 检查查询参数
        stepStartTime = System.currentTimeMillis();

        // 获取最小粉丝数配置
        if (oConvertUtils.isEmpty(searchModel.getMinFollowers())) {
            // 获取最小粉丝数配置
            Integer minNum = ruleService.getMinNum(searchModel.getPlatformType());
            searchModel.setMinFollowers(minNum);
        }
        kolCelebrityService.checkParams(searchModel);
        log.info("[分配网红] 参数检查处理耗时: {}秒,查询标签数: {}", (System.currentTimeMillis() - stepStartTime) / 1000.0, searchModel.getTagNameList().size());

        // 3. 验证标签查询条件
        if (oConvertUtils.isNotEmpty(searchModel.getAttributeIds())
                || oConvertUtils.isNotEmpty(searchModel.getCategoryIds())
                || oConvertUtils.isNotEmpty(searchModel.getProductId())
                || oConvertUtils.isNotEmpty(searchModel.getBrandId())) {
            if (searchModel.getTagNameList().isEmpty()) {
                log.info("[分配网红] 标签条件验证耗时: {}秒 (无匹配标签，直接返回)", (System.currentTimeMillis() - stepStartTime) / 1000.0);
                redisUtil.set(String.valueOf(searchModel.hashCode()), 0, 300);
                return Result.ok(new Page<>());
            }
        }

        // 4. 初始化分页对象
        CustomPage<KolBaseModel> page = new CustomPage<>(pageNo, pageSize, pageSize + 1, req);
        page.setSearchCount(false);
        IKolAllotService allotService = getKolAllotService(platformType);

        Integer allottedKolCount;
        IPage pageList = null;

        try {
            // 5. 处理临时表名设置
            if (!searchModel.getTagNameList().isEmpty()) {
                String tempTableName = "temp_" + Thread.currentThread().getId() + "_" + System.currentTimeMillis();
                searchModel.setTempTableName(tempTableName);
            }
            // 6. 获取总数设置
            if (oConvertUtils.isNotEmpty(searchModel.getAttributeIds())) {
                // 默认查询10000条网红数据
                int total = tkTagsService.getAllotTotal();
                searchModel.setTotal(total);
            }

            // 7. 异步查询KOL数据
            stepStartTime = System.currentTimeMillis();
            CompletableFuture<IPage<KolCelebrityModel>> pageListFuture = CompletableFuture.
                    supplyAsync(() -> allotService.getCelebrityScreeningListNew(page, searchModel));
            pageList = pageListFuture.get();
            log.info("[分配网红] 异步查询KOL数据耗时: {}秒", (System.currentTimeMillis() - stepStartTime) / 1000.0);

        } catch (Exception e) {
            pageList = allotService.getCelebrityScreeningListNew(page, searchModel);
            log.error("[分配网红] 查询KOL数据异常: {}", e.getMessage());
            System.out.println(e.getMessage());
        }

        // 8. 处理分页数据
        pageList = PageUtil.processPagination(pageList, pageNo, pageSize);
        allottedKolCount = Math.toIntExact(pageList.getTotal());

        // 9. 处理KOL开发历史和标签列表
        stepStartTime = System.currentTimeMillis();
        processKolModelList(allottedKolCount, pageList, allotService, platformType, searchModel);
        log.info("[分配网红] 处理KOL开发历史和标签列表总耗时: {}秒", (System.currentTimeMillis() - stepStartTime) / 1000.0);

        // 10. 设置属性信息
        stepStartTime = System.currentTimeMillis();
        kolCelebrityService.setAttributes(pageList, searchModel);
        log.info("[分配网红] 社媒属性信息设置耗时: {}秒", (System.currentTimeMillis() - stepStartTime) / 1000.0);

        // 11. 处理KOL是否私有网红
        List<KolCelebrityModel> kolModelList = pageList.getRecords();
        if (oConvertUtils.isEmpty(searchModel.getIsPrivateKol())) {
            stepStartTime = System.currentTimeMillis();
            // 1、获取pageList中的所有网红名,TK网红是account Youtube、IG 是username
            List<String> celebrityNames = new ArrayList<>();
            for (KolCelebrityModel kolModel : kolModelList) {
                if (platformType == CommonConstant.TK) {
                    // TK网红使用account字段
                    if (oConvertUtils.isNotEmpty(kolModel.getAccount())) {
                        celebrityNames.add(kolModel.getAccount());
                    }
                } else {
                    // Youtube、IG 使用username字段
                    if (oConvertUtils.isNotEmpty(kolModel.getUsername())) {
                        celebrityNames.add(kolModel.getUsername());
                    }
                }
            }

            // 2、根据网红名称获取私有网红列表
            if (!celebrityNames.isEmpty()) {
                List<CelebrityPrivateModel> privateCelebrityList = kolCelebrityApi
                        .getPrivateCelebrityList(celebrityNames);

                // 创建一个Map用于快速查找私有网红ID，键为用户名/账号，值为私有网红ID
                Map<String, String> privateCelebrityMap = new HashMap<>();
                for (CelebrityPrivateModel privateModel : privateCelebrityList) {
                    if (oConvertUtils.isNotEmpty(privateModel.getAccount())) {
                        privateCelebrityMap.put(privateModel.getAccount(), privateModel.getId());
                    }
                }

                // 3、遍历pageList，设置 celebrityPrivateId，返回前端，如果celebrityPrivateId不为空则是私有网红，如果为空则不是
                for (KolCelebrityModel kolModel : kolModelList) {
                    String key;
                    if (platformType == CommonConstant.TK) {
                        key = kolModel.getAccount();
                    } else {
                        key = kolModel.getUsername();
                    }

                    if (privateCelebrityMap.containsKey(key)) {
                        kolModel.setCelebrityPrivateId(privateCelebrityMap.get(key));
                    } else {
                        kolModel.setCelebrityPrivateId(null);
                    }
                }
            }

            log.info("[分配网红] 是否私有网红设置耗时: {}秒", (System.currentTimeMillis() - stepStartTime) / 1000.0);

        }
        long totalTime = System.currentTimeMillis() - startExecTime;
        log.info("[分配网红] 总耗时: {}秒", totalTime / 1000.0);

        redisUtil.set(String.valueOf(searchModel.hashCode()), pageList.getRecords().size(), 300);
      /*  if (isSpecial) {
            redisUtil.set("screening_default", JSON.toJSONString(pageList.getRecords()), -1);
        }*/
        return Result.OK(pageList, (int) allottedKolCount);
    }

    @PostMapping(value = "/getScreeningListCount")
    public Result<?> getScreeningListCount(@RequestBody KolSearchModel searchModel) {
        // 记录起始执行时间
        Integer platformType = searchModel.getPlatformType();
        if (oConvertUtils.isEmpty(platformType)) {
            return Result.error("平台类型不能为空");
        }
        JSONObject result = new JSONObject();

        // 获取最小粉丝数配置
        if (oConvertUtils.isEmpty(searchModel.getMinFollowers())) {
            // 获取最小粉丝数配置
            Integer minNum = ruleService.getMinNum(searchModel.getPlatformType());
            searchModel.setMinFollowers(minNum);
        }

        // 判断是否存在查询条件
        kolCelebrityService.checkParams(searchModel);

        boolean hasOtherFilters = oConvertUtils.isEmpty(searchModel.getAttributeIds())
                && oConvertUtils.isEmpty(searchModel.getCategoryIds())
                && oConvertUtils.isEmpty(searchModel.getProductId());

        boolean tagNameListEmpty = searchModel.getTagNameList().isEmpty();
        // 标识列表是否存在总条数
        boolean totalFlag = false;
        if (redisUtil.hasKey(String.valueOf(searchModel.hashCode()))) {
            int o = (int) redisUtil.get(String.valueOf(searchModel.hashCode()));
            if (o == 0) {
                totalFlag = true;
            }
        }

        // 如果 tagNameList 为空，且没有其他筛选条件，则返回零值
        if (tagNameListEmpty && (totalFlag || hasOtherFilters)) {
            result.put("allottedKolCount", 0);
            result.put("notAllotCount", 0);
            result.put("allotCount", 0);
            return Result.ok(result);
        }
        // 否则继续执行（即使 tagNameList 为空，但有其他条件）
        IKolAllotService allotService = getKolAllotService(platformType);
        Integer allottedKolCount = 0;
        try {
            if (!searchModel.getTagNameList().isEmpty()) {
                String tempTableName = "temp_" + Thread.currentThread().getId() + "_" + System.currentTimeMillis();
                searchModel.setTempTableName(tempTableName);
            }
    /*        if (oConvertUtils.isNotEmpty(searchModel.getAttributeIds())) {
                // 默认查询10000条网红数据
                int total = tkTagsService.getAllotTotal();
                searchModel.setTotal(total);
            }*/
            long stepStartTime = System.currentTimeMillis();
            allottedKolCount = allotService.getCelebrityScreeningCount(searchModel);
            log.info("[分配网红] 查询总条数: {}秒", (System.currentTimeMillis() - stepStartTime) / 1000.0);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        int notAllotCount = 0;
        try {
            long stepStartTime = System.currentTimeMillis();
            notAllotCount = getNotAllotCount(searchModel);
            log.info("[分配网红] 查询已分配条数: {}秒", (System.currentTimeMillis() - stepStartTime) / 1000.0);
        } catch (Exception e) {
            log.error("[分配网红] 查询KOL数据异常: {}", e.getMessage());
            kolCelebrityService.dropTempTable(searchModel.getTempTableName());
        }
        int allotCount = 0;
        if (!searchModel.getTagNameList().isEmpty()) {
            allotCount = Math.max(allottedKolCount - notAllotCount, 0);
        }
        result.put("allottedKolCount", allottedKolCount);
        result.put("notAllotCount", Math.min(allottedKolCount, notAllotCount));
        result.put("allotCount", allotCount);
        return Result.ok(result);
    }

    private int getNotAllotCount(KolSearchModel searchModel) {
        // 获取网红分配过期天数配置（默认15天）
        int allotMaxDays = tkTagsService.getAllotExpireDays();

        searchModel.setAllotDays(allotMaxDays);
        searchModel.setAllotDaysDate(DateUtils.getFetureDate(-allotMaxDays));

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
        if (searchModel.getTagNameList().isEmpty()) {
            kolCelebrityService.dropTempTable(searchModel.getTempTableName());
            return 0;
        }
        int count = 0;
        // 从数据库查询未分配的网红标签列表
        switch (searchModel.getPlatformType()) {
            case CommonConstant.IG:
                count = igTagsService.getNotAllotCount(searchModel);
                break;
            case CommonConstant.YT:
                count = ytTagsService.getNotAllotCount(searchModel);
                break;
            case CommonConstant.TK:
                count = tkTagsService.getNotAllotCount(searchModel);
        }
        return count;
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
     * 处理网红视图模型列表 更新建联字段和标签字段
     *
     * @param kolModelPageList
     * @return
     */
    private long processKolModelList(Integer kolCount, IPage<KolCelebrityModel> kolModelPageList, IKolAllotService allotService, int platformType, KolSearchModel searchModel) {
/*
        // 处理分页页码数据
        // 获取字典配置最大返回记录，默认为 5000
        int configMaxRecordCount = sysBaseAPI.queryDictItemsByCode(CommonConstant.RESULT_COUNT).stream().filter(x -> KOL_MAX_RECORD.equals(x.getValue())).findFirst().map(dict -> Integer.parseInt(dict.getText())).orElse(KOL_MAX_RECORD_DEFAULT);
        // 比较配置和查询结果，取较大值作为返回值
        int maxRecordCount = configMaxRecordCount;
        configMaxRecordCount = Math.max(configMaxRecordCount, kolCount);
        kolCount = Math.min(maxRecordCount, kolCount);
*/

        // 处理分页记录
        List<KolCelebrityModel> kolModelList = kolModelPageList.getRecords();
        if (!kolModelList.isEmpty()) {
            List<String> uniqueIdList;
            if (platformType == CommonConstant.TK) {
                uniqueIdList = kolModelList.stream().map(KolCelebrityModel::getAuthorId).collect(Collectors.toList());
            } else {
                uniqueIdList = kolModelList.stream().map(KolCelebrityModel::getAccount).collect(Collectors.toList());
            }
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            // 更新开发建联历史字段 纵向变横向
            List<KolContact> kolContactList = kolContactService.getContactListByIds(uniqueIdList);
            stopWatch.stop();
            stopWatch.reset();
            log.info("[分配网红] 查询开发建联,耗时{}秒", String.format("%.2f", stopWatch.getTime() / 1000.0));

            stopWatch.start();
            kolContactList = kolContactList.stream().filter(x -> x.getPlatformType() == platformType).collect(Collectors.toList());
            if (!kolContactList.isEmpty()) {
                if (platformType == CommonConstant.TK) {
                    kolContactService.updateContactHistoryAllot(kolModelList, kolContactList, KolCelebrityModel::getAuthorId, KolCelebrityModel::setContactHistory, KolCelebrityModel::setContactCount);
                } else {
                    kolContactService.updateContactHistoryAllot(kolModelList, kolContactList, KolCelebrityModel::getAccount, KolCelebrityModel::setContactHistory, KolCelebrityModel::setContactCount);
                }
            }
            stopWatch.stop();
            stopWatch.reset();
            log.info("[分配网红] 更新开发建联历史,耗时{}秒", String.format("%.2f", stopWatch.getTime() / 1000.0));

            stopWatch.start();
            searchModel.setTempTableName("temp_" + Thread.currentThread().getId() + "_" + System.currentTimeMillis());
            // 处理标签字段赋值
            allotService.updateKolTagListNew(uniqueIdList, kolModelList, searchModel);
            stopWatch.stop();
            log.info("[分配网红] 标签字段赋值,耗时{}秒", String.format("%.2f", stopWatch.getTime() / 1000.0));
        }
        kolModelPageList.setTotal(kolCount);
        return kolCount;
    }

    /**
     * 判断是否满足特殊条件
     *
     * @param searchModel 查询参数模型
     * @return true-满足特殊条件，false-不满足
     */
    private boolean isSpecialCondition(KolSearchModel searchModel) {
        // 判断条件：platformType=2 且 countryCode="US" 且 isHasEmail="" 且 isNonPinned=1
        // 注意：column和order字段在当前模型中不存在，所以不参与判断
        return searchModel.getPlatformType() != null && searchModel.getPlatformType() == 2
                && "US".equals(searchModel.getCountryCode())
                && oConvertUtils.isEmpty(searchModel.getIsHasEmail())
                && searchModel.getIsNonPinned() != null && searchModel.getIsNonPinned() == 1;
    }

    /**
     * 默认加载列表第一页查询redis缓存
     *
     * @param pageNo   页码
     * @param pageSize 每页大小
     * @return 处理结果
     */
    private Result<?> handleSpecialCondition(Integer pageNo, Integer pageSize) {
        log.info("[分配网红] 检测到特殊参数条件，进入单独处理逻辑");

        // 安全地从Redis获取数据并进行类型检查
        Object redisData = redisUtil.get("screening_default");
        List<KolCelebrityModel> screeningDefault = new ArrayList<>();

        if (redisData != null) {
            try {
                // 检查是否为List类型
                if (redisData instanceof String) {
                    // 进一步检查List中的元素类型
                    screeningDefault = JSONObject.parseArray((String) redisData, KolCelebrityModel.class);
                } else {
                    log.warn("[分配网红] Redis中的数据不是List类型，使用空列表");
                }
            } catch (ClassCastException e) {
                log.error("[分配网红] 类型转换错误: {}", e.getMessage());
                screeningDefault = new ArrayList<>();
            }
        }

        // 创建分页结果
        Page<KolCelebrityModel> resultPage = new Page<>(pageNo, pageSize);
        resultPage.setRecords(screeningDefault);
        resultPage.setTotal((long) screeningDefault.size());

        log.info("[分配网红] 特殊条件处理完成，返回 {} 条记录", screeningDefault.size());
        return Result.OK(resultPage, screeningDefault.size());
    }
}
