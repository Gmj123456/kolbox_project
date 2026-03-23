package org.jeecg.modules.instagram.controller;

import com.alibaba.fastjson.JSONObject;
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
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.enums.YesNoStatus;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.PageUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.entity.IgCelebrityTags;
import org.jeecg.modules.instagram.entity.IgCelebrityTagsCounselor;
import org.jeecg.modules.instagram.model.IgCelebrityTagsModel;
import org.jeecg.modules.instagram.service.IIgCelebrityTagsCounselorService;
import org.jeecg.modules.instagram.service.IIgCelebrityTagsService;
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
import org.jeecg.modules.tiktok.service.ITiktokCelebrityRuleService;
import org.jeecg.modules.tiktok.service.ITiktokCelebrityTagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.jeecg.common.constant.CelebrityPromStatus.KOL_ALLOT_MAX_DAYS;
import static org.jeecg.common.constant.CelebrityPromStatus.KOL_ALLOT_MAX_DAYS_DEFAULT;
import static org.jeecg.common.constant.SystemConstants.*;

/**
 * @Description: IG网红标签控制器
 * @Author: xyc
 * @Date: 2024年12月7日 18:37:05
 * @Version: V1.0
 */
@Tag(name = "Instagram网红标签")
@RestController
@RequestMapping("/instagram/igCelebrityTags")
@Slf4j
public class IgCelebrityTagsController extends JeecgController<IgCelebrityTags, IIgCelebrityTagsService> {
    @Autowired
    private IIgCelebrityTagsService igTagsService;
    @Autowired
    private ITiktokCelebrityTagsService tkTagsService;
    @Autowired
    private ISysBaseAPI sysBaseAPI;
    @Autowired
    private IIgCelebrityTagsCounselorService igTagsCounselorService;
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

    /**
     * 分页列表查询
     *
     * @param igCelebrityTags
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "ig_celebrity_tags-分页列表查询")
    @Operation(summary = "ig_celebrity_tags-分页列表查询", description = "ig_celebrity_tags-分页列表查询")
    @GetMapping(value = LIST_URL)
    public Result<?> queryPageList(IgCelebrityTags igCelebrityTags,
            @RequestParam(name = PAGE_NO, defaultValue = PAGE_NO_DEFAULT) Integer pageNo,
            @RequestParam(name = PAGE_SIZE, defaultValue = PAGE_SIZE_DEFAULT) Integer pageSize,
            HttpServletRequest req) {
        QueryWrapper<IgCelebrityTags> queryWrapper = QueryGenerator.initQueryWrapper(igCelebrityTags,
                req.getParameterMap());
        Page<IgCelebrityTags> page = new Page<IgCelebrityTags>(pageNo, pageSize);
        IPage<IgCelebrityTags> pageList = igTagsService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 获取未分配的网红数量
     *
     * @param searchModel
     * @return
     */
    @AutoLog(value = "ig_celebrity_tags-IG网红未分配数量查询")
    @Operation(summary = "IG网红未分配数量查询", description = "IG网红未分配数量查询")
    @PostMapping(value = "/getUnallottedTagCount")
    public Result<?> getUnallottedTagCount(@RequestBody KolSearchModel searchModel) {
        // 获取网红分配过期天数 默认30天
        int allotMaxDays = getAllotExpireDays();
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
            // 查询IG分配规则最小粉丝数
            Integer minNum = ruleService.getMinNum(0);
            searchModel.setMinFollowers(minNum);
        }
        /*
         * // 判断是否按照产品查询
         * if (oConvertUtils.isNotEmpty(searchModel.getProductId())) {
         * // 获取产品对应的标签
         * tagProductService.getProductTagNames(searchModel);
         * // 判断是否有标签
         * if (oConvertUtils.listIsEmpty(searchModel.getTagNameList())) {
         * return Result.ok(0);
         * }
         * }
         */
        if (searchModel.getTagNameList().isEmpty()) {
            return Result.ok(0);
        }
        String tempTableName = "temp_" + Thread.currentThread().getId() + "_" + System.currentTimeMillis();
        searchModel.setTempTableName(tempTableName);
        Long notAllotCount = igTagsService.getUnallottedTagCount(searchModel);
        return Result.ok(notAllotCount);
    }

    /**
     * 获取未分配的网红数量
     *
     * @param searchModel
     * @return
     */
    @AutoLog(value = "ig_celebrity_tags-获取未分配的网红数量")
    @Operation(summary = "获取未分配的网红数量", description = "获取未分配的网红数量")
    @PostMapping(value = "/getUnallottedTagPageList")
    public Result<?> getUnallottedTagPageList(@RequestBody KolSearchModel searchModel,
            @RequestParam(name = PAGE_NO, defaultValue = PAGE_NO_DEFAULT) Integer pageNo,
            @RequestParam(name = PAGE_SIZE, defaultValue = PAGE_SIZE_DEFAULT) Integer pageSize,
            HttpServletRequest req) {
        Page<IgCelebrityTagsModel> page = PageUtil.getOrderItems(pageNo, pageSize, req);
        page.setSearchCount(false);
        // 获取网红分配过期天数配置（默认15天）
        int allotMaxDays = tkTagsService.getAllotExpireDays();

        searchModel.setAllotDays(allotMaxDays);

        String attributeIds = searchModel.getAttributeIds();
        if (oConvertUtils.isNotEmpty(attributeIds)) {
            // 获取属性信息
            KolAttribute attribute = attributeService.getById(attributeIds);

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
        }

        // 从数据库查询未分配的网红标签列表
        IPage<IgCelebrityTagsModel> pageList = igTagsService.getUnAlloteTagPageList(page, searchModel);
        return Result.ok(pageList);
    }

    /**
     * 获取未分配的标签及数量汇总
     *
     * @param igSearchModel
     * @return
     */
    @Operation(summary = "IG未分配的标签及数量汇总查询", description = "IG未分配的标签及数量汇总查询")
    @PostMapping(value = "/getUnallottedTagCountSummary")
    public Result<?> getUnallottedTagCountSummary(@RequestBody KolSearchModel igSearchModel,
            @RequestParam(name = PAGE_NO, defaultValue = PAGE_NO_DEFAULT) Integer pageNo,
            @RequestParam(name = PAGE_SIZE, defaultValue = PAGE_SIZE_DEFAULT) Integer pageSize,
            HttpServletRequest req) {
        Page<KolTagCountModel> page = PageUtil.getOrderItems(pageNo, pageSize, req);
        // 获取网红分配过期天数 默认30天
        int allotMaxDays = getAllotExpireDays();
        igSearchModel.setAllotDays(allotMaxDays);
        // 查询可分配的标签数量分类汇总
        IPage<KolTagCountModel> pageList = igTagsService.getUnallottedTagCountSummary(page, igSearchModel);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param igCelebrityTags
     * @return
     */
    @AutoLog(value = "ig_celebrity_tags-添加")
    @Operation(summary = "ig_celebrity_tags-添加", description = "ig_celebrity_tags-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody IgCelebrityTags igCelebrityTags) {
        igTagsService.save(igCelebrityTags);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param igCelebrityTags
     * @return
     */
    @AutoLog(value = "ig_celebrity_tags-编辑")
    @Operation(summary = "ig_celebrity_tags-编辑", description = "ig_celebrity_tags-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody IgCelebrityTags igCelebrityTags) {
        igTagsService.updateById(igCelebrityTags);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "ig_celebrity_tags-通过id删除")
    @Operation(summary = "ig_celebrity_tags-通过id删除", description = "ig_celebrity_tags-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        igTagsService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "ig_celebrity_tags-批量删除")
    @Operation(summary = "ig_celebrity_tags-批量删除", description = "ig_celebrity_tags-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.igTagsService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "ig_celebrity_tags-通过id查询")
    @Operation(summary = "ig_celebrity_tags-通过id查询", description = "ig_celebrity_tags-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        IgCelebrityTags igCelebrityTags = igTagsService.getById(id);
        if (igCelebrityTags == null) {
            return Result.error("未找到对应数据");
        }
        return Result.ok(igCelebrityTags);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param igCelebrityTags
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, IgCelebrityTags igCelebrityTags) {
        return super.exportXls(request, igCelebrityTags, IgCelebrityTags.class, "ig_celebrity_tags");
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
        return super.importExcel(request, response, IgCelebrityTags.class);
    }

    /**
     * 分配网红
     *
     * @param
     * @return
     */
    @AutoLog(value = "网红tag表-分配网红")
    @Operation(summary = "网红tag表-分配网红", description = "网红tag表-分配网红")
    @PostMapping(value = "/allotTags")
    public Result<?> allotTags(@RequestBody KolSearchModel igSearchModel) {

        // region ---参数校验---
        // 先获取网红顾问id和网红顾问名称
        String counselorId = igSearchModel.getCounselorId();
        String counselorName = igSearchModel.getCounselorName();
        if (oConvertUtils.isEmpty(counselorId) || oConvertUtils.isEmpty(counselorName)) {
            return Result.error("请选择分配网红顾问");
        }
        // 判断入参的标签列表是否为空 因标签较多 暂先控制必须传入要分配的标签
        if (oConvertUtils.listIsEmpty(igSearchModel.getTagNameList())) {
            return Result.error("请选择要分配的网红标签");
        }
        // endregion

        // 获取网红分配过期天数 默认30天
        int allotMaxDays = getAllotExpireDays();
        igSearchModel.setAllotDays(allotMaxDays);
        // 从数据库中查询未分配网红标签列表
        List<UserTagAllotModel> unallottedTagList = igTagsService.getUnallottedTagList(igSearchModel);
        if (unallottedTagList.isEmpty()) {
            return Result.error("没有可分配的网红标签");
        }
        // 创建网红标签表更新实体列表
        List<IgCelebrityTags> igTagsUpdateList = new ArrayList<>();
        // 创建网红标签顾问批量新增实体列表
        List<IgCelebrityTagsCounselor> igTagsCounselorAddList = new ArrayList<>();
        // 创建网红标签顾问批量更新实体列表
        List<IgCelebrityTagsCounselor> igTagsCounselorUpdateList = new ArrayList<>();
        // 根据网红顾问id查询网红标签顾问列表
        List<IgCelebrityTagsCounselor> igTagsCounselorList = igTagsCounselorService.getListByCounselorId(counselorId);

        // 循环未分配网红标签列表
        unallottedTagList.forEach(x -> {
            // 组合网红标签表更新列表参数(不区分私有和普通网红)
            // 组合标签顾问列表(更新列表和新增列表)
            processTagAllot(igSearchModel, x, igTagsCounselorList, igTagsUpdateList, igTagsCounselorAddList,
                    igTagsCounselorUpdateList);
        });

        // 组装分配日志实体
        KolAllotLogDetail kolAllotLogDetailEntity = createKolAllotLogDetail(igSearchModel, unallottedTagList);

        // 组装数据库更新数据
        igTagsService.saveAllotTags(igTagsUpdateList, igTagsCounselorAddList, igTagsCounselorUpdateList,
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
     * 获取网红分配过期天数
     *
     * @return
     */
    private int getAllotExpireDays() {
        // 获取网红分配过期天数 默认30天
        int allotMaxDays = sysBaseAPI.queryDictItemsByCode(CommonConstant.ALLOT_DAYS).stream()
                .filter(x -> KOL_ALLOT_MAX_DAYS.equals(x.getValue())).findFirst()
                .map(dict -> Integer.parseInt(dict.getText())).orElse(KOL_ALLOT_MAX_DAYS_DEFAULT);
        return allotMaxDays;
    }

    /**
     * 组合网红标签顾问实体
     *
     * @param tagAllotModel 当前要处理的标签模型
     * @param igSearchModel 前端传入的查询视图模型
     * @return
     */
    private IgCelebrityTagsCounselor createTagsCounselor(UserTagAllotModel tagAllotModel,
            KolSearchModel igSearchModel) {
        IgCelebrityTagsCounselor tagsCounselor = new IgCelebrityTagsCounselor();
        tagsCounselor.setId(IdWorker.get32UUID());
        tagsCounselor.setAccount(tagAllotModel.getAccount());
        tagsCounselor.setIgCelebrityTagsId(tagAllotModel.getId());
        tagsCounselor.setCounselorId(igSearchModel.getCounselorId());
        tagsCounselor.setCounselorName(igSearchModel.getCounselorName());
        tagsCounselor.setAllotTime(new Date());
        tagsCounselor.setIgUsername(tagAllotModel.getUniqueId());
        tagsCounselor.setTagName(tagAllotModel.getTagName());
        tagsCounselor.setTagType(tagAllotModel.getTagType());
        tagsCounselor.setFollowerCount(tagAllotModel.getAuthorFollowerCount());
        tagsCounselor.setCountry(tagAllotModel.getCountry());
        return tagsCounselor;
    }

    /**
     * 处理标签分配
     *
     * @param tagAllotModel             当前要处理的标签模型
     * @param igSearchModel             前端传入的查询视图模型
     * @param tagsCounselorList         标签顾问列表
     * @param igTagsUpdateList          网红标签需更新分配标识的列表
     * @param igTagsCounselorAddList    新增的标签顾问列表
     * @param igTagsCounselorUpdateList 更新的标签顾问列表
     */
    private void processTagAllot(KolSearchModel igSearchModel, UserTagAllotModel tagAllotModel,
            List<IgCelebrityTagsCounselor> tagsCounselorList, List<IgCelebrityTags> igTagsUpdateList,
            List<IgCelebrityTagsCounselor> igTagsCounselorAddList,
            List<IgCelebrityTagsCounselor> igTagsCounselorUpdateList) {

        // 先获取网红顾问id和网红顾问名称
        String counselorId = igSearchModel.getCounselorId();
        String counselorName = igSearchModel.getCounselorName();

        // 更新网红标签表实体
        IgCelebrityTags igTagsEntity = new IgCelebrityTags();
        igTagsEntity.setId(tagAllotModel.getId());
        igTagsEntity.setCounselorId(counselorId);
        igTagsEntity.setCounselorName(counselorName);
        igTagsEntity.setIsAllot(YesNoStatus.YES.getCode());
        igTagsEntity.setAllotTime(new Date());
        igTagsUpdateList.add(igTagsEntity);

        // 构建或更新网红标签顾问表实体
        IgCelebrityTagsCounselor tagCounselor = createTagsCounselor(tagAllotModel, igSearchModel);
        Optional<IgCelebrityTagsCounselor> tagsCounselorOptional = tagsCounselorList.stream()
                .filter(x -> x.getTagName().equals(tagAllotModel.getTagName())
                        && x.getIgUsername().equals(tagAllotModel.getUniqueId()))
                .findFirst();
        // 判断是否已经存在该标签顾问
        tagsCounselorOptional.ifPresent(existing -> {
            tagCounselor.setId(existing.getId());
            igTagsCounselorUpdateList.add(tagCounselor);
        });
        // 如果更新列表不存在 则添加到新增列表
        if (!igTagsCounselorUpdateList.contains(tagCounselor)) {
            igTagsCounselorAddList.add(tagCounselor);
        }
    }

    /**
     * 组合网红标签分配日志明细实体
     *
     * @param igSearchModel        查询条件视图模型
     * @param unallottedKolTagList 未分配网红标签列表
     * @return
     */
    private KolAllotLogDetail createKolAllotLogDetail(KolSearchModel igSearchModel,
            List<UserTagAllotModel> unallottedKolTagList) {
        // 先获取网红顾问id和网红顾问名称
        String counselorId = igSearchModel.getCounselorId();
        String counselorName = igSearchModel.getCounselorName();

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
        kolAllotLogDetailEntity.setPlatformType(CommonConstant.IG);
        kolAllotLogDetailEntity.setAllotContent(jsonObject.toJSONString());
        kolAllotLogDetailEntity.setAllotTime(new Date());

        return kolAllotLogDetailEntity;
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
            log.info("[网红标签分配] 开始执行网红标签分配流程，请求参数: 产品ID={}, 属性ID={}, 标签数量={}, 顾问数量={}", searchModel.getProductId(),
                    searchModel.getAttributeIds(),
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
                    return;
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
                saveAllocationResults(allocationResult, searchModel);
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
        if (searchModel.getTagNameList().isEmpty() && oConvertUtils.isEmpty(searchModel.getProductId())
                && oConvertUtils.isEmpty(searchModel.getAttributeIds())) {
            return Result.error("请选择要分配的标签、产品或社媒属性");
        }

        if (oConvertUtils.isEmpty(searchModel.getCountryCode())) {
            return Result.error("请选择国家");
        }

        return Result.ok();
    }

    /**
     * 初始化分配所需的基础数据
     */
    private InitializationData initializeAllocationData(KolSearchModel searchModel) {
        long methodStartTime = System.currentTimeMillis();
        log.info("IG [分配初始化] 开始初始化分配数据，查询条件: 产品ID={}, 属性ID={}, 标签数量={}", searchModel.getProductId(),
                searchModel.getAttributeIds(),
                searchModel.getTagNameList() != null ? searchModel.getTagNameList().size() : 0);

        InitializationData data = new InitializationData();

        try {
            // 获取网红分配过期天数配置（默认15天）
            long startTime = System.currentTimeMillis();
            int allotMaxDays = tkTagsService.getAllotExpireDays();
            long duration = System.currentTimeMillis() - startTime;
            log.info("IG [分配初始化] 获取分配过期天数配置完成，耗时: {}ms，过期天数: {}天", duration, allotMaxDays);

            searchModel.setAllotDays(allotMaxDays);

            String productId = searchModel.getProductId();
            String attributeIds = searchModel.getAttributeIds();
            if (oConvertUtils.isNotEmpty(attributeIds) && oConvertUtils.isEmpty(productId)) {
                // 获取属性信息
                startTime = System.currentTimeMillis();
                KolAttribute attribute = attributeService.getById(attributeIds);
                duration = System.currentTimeMillis() - startTime;
                log.info("IG [分配初始化] 获取属性信息完成，耗时: {}ms，属性名称: {}", duration,
                        attribute != null ? attribute.getAttributeName() : "null");

                searchModel.setAttributeName(attribute.getAttributeName());

                // 获取社媒属性对应的标签
                startTime = System.currentTimeMillis();
                tagAttributeRelationService.getAttributeTagNames(searchModel);
                duration = System.currentTimeMillis() - startTime;
                log.info("IG [分配初始化] 获取属性对应标签完成，耗时: {}ms，标签数量: {}", duration,
                        searchModel.getTagNameList() != null ? searchModel.getTagNameList().size() : 0);

                // 默认查询10000条网红数据
                startTime = System.currentTimeMillis();
                int total = tkTagsService.getAllotTotal();
                duration = System.currentTimeMillis() - startTime;
                log.info("IG [分配初始化] 获取分配总数配置完成，耗时: {}ms，总数: {}", duration, total);

                searchModel.setTotal(total);
            }
            // 获取最小粉丝数配置
            if (oConvertUtils.isEmpty(searchModel.getMinFollowers())) {
                startTime = System.currentTimeMillis();
                Integer minNum = ruleService.getMinNum(0);
                duration = System.currentTimeMillis() - startTime;
                log.info("IG [分配初始化] 获取最小粉丝数配置完成，耗时: {}ms，最小粉丝数: {}", duration, minNum);
                searchModel.setMinFollowers(minNum);
            }
            // 参数校验
            startTime = System.currentTimeMillis();
            kolCelebrityService.checkParams(searchModel);
            duration = System.currentTimeMillis() - startTime;
            log.info("IG [分配初始化] 参数校验完成，耗时: {}ms", duration);

            if (!searchModel.getTagNameList().isEmpty()) {
                String tempTableName = "temp_" + Thread.currentThread().getId() + "_" + System.currentTimeMillis();
                searchModel.setTempTableName(tempTableName);
                log.info("IG [分配初始化] 创建临时表名: {}", tempTableName);
            }

            // 从数据库查询未分配的网红标签列表
            startTime = System.currentTimeMillis();
            List<IgCelebrityTagsModel> unallottedTagList = igTagsService.getUnAlloteTagList(searchModel);
            duration = System.currentTimeMillis() - startTime;
            log.info("IG [分配初始化] 查询未分配网红标签列表完成，耗时: {}ms，数量: {}", duration, unallottedTagList.size());

            if (unallottedTagList.isEmpty()) {
                log.warn("IG [分配初始化] 没有找到可分配的网红标签");
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
                log.info("IG [分配初始化] 批量设置产品ID和属性ID完成，耗时: {}ms", duration);
            }

            // 获取顾问用户信息
            List<String> counselorIds = searchModel.getCelebrityCounselorList();
            startTime = System.currentTimeMillis();
            List<LoginUser> counselorList = sysBaseAPI.getUserByIds(counselorIds);
            duration = System.currentTimeMillis() - startTime;
            log.info("IG [分配初始化] 获取顾问用户信息完成，耗时: {}ms，顾问数量: {}", duration,
                    counselorList != null ? counselorList.size() : 0);

            // 获取分配规则
            startTime = System.currentTimeMillis();
            List<TiktokCelebrityRule> rules = ruleService.lambdaQuery()
                    .eq(TiktokCelebrityRule::getPlatformType, CommonConstant.IG).list();
            duration = System.currentTimeMillis() - startTime;
            log.info("IG [分配初始化] 获取分配规则完成，耗时: {}ms，规则数量: {}", duration, rules.size());

            if (rules.isEmpty()) {
                log.warn("IG [分配初始化] 未获取到分配规则");
                data.setErrorResult(Result.error("未获取到分配规则"));
                return data;
            }

            // 获取最低等级的最小粉丝数要求
            startTime = System.currentTimeMillis();
            Integer minFollowerThreshold = rules.stream().max(Comparator.comparing(TiktokCelebrityRule::getRuleLevel))
                    .get().getMinNum();
            duration = System.currentTimeMillis() - startTime;
            log.info("IG [分配初始化] 计算最小粉丝数阈值完成，耗时: {}ms，阈值: {}", duration, minFollowerThreshold);

            data.setIgUnallottedTagList(unallottedTagList);
            data.setCounselorList(counselorList);
            data.setRules(rules);
            data.setMinFollowerThreshold(minFollowerThreshold);

            long totalDuration = System.currentTimeMillis() - methodStartTime;
            log.info("IG [分配初始化] 初始化分配数据完成，总耗时: {}ms，未分配标签数: {}, 顾问数: {}, 规则数: {}", totalDuration,
                    unallottedTagList.size(), counselorList != null ? counselorList.size() : 0, rules.size());

        } catch (Exception e) {
            long totalDuration = System.currentTimeMillis() - methodStartTime;
            log.error("IG [分配初始化] 初始化分配数据失败，耗时: {}ms，错误信息: {}", totalDuration, e.getMessage(), e);
            data.setErrorResult(Result.error("分配失败,请重试！"));
        }

        return data;
    }

    /**
     * 处理网红标签数据，过滤不符合条件的数据
     */
    private ProcessedTagData processTagData(InitializationData initData) {
        ProcessedTagData processedData = new ProcessedTagData();

        List<IgCelebrityTagsModel> unallottedTagList = initData.getIgUnallottedTagList();

        // 统计已有私有网红的分配情况
        Map<String, List<IgCelebrityTags>> existingPrivateTagMap = unallottedTagList.stream()
                .collect(Collectors.groupingBy(tag -> tag.getCounselorId() + "_" + tag.getCounselorName()));

        processedData.setIgEligibleTagList(unallottedTagList);
        processedData.setExcludedCelebrityIds(new ArrayList<>());
        processedData.setIgExistingPrivateTagMap(existingPrivateTagMap);
        processedData.initializeLists();

        return processedData;
    }

    /**
     * 处理私有网红标签分配
     */
    private void handlePrivateCelebrityTags(ProcessedTagData processedData, String userName) {
        List<IgCelebrityTagsModel> eligibleTagList = processedData.getIgEligibleTagList();
        List<IgCelebrityTags> igTagsUpdateList = processedData.getIgTagsUpdateList();

        // 筛选出拥有私有顾问ID的网红标签,判断建联时间是否超过一年 2025年7月24日13:48:46
        List<IgCelebrityTagsModel> privateCelebrityTags = eligibleTagList.stream()
                .filter(tag -> oConvertUtils.isNotEmpty(tag.getCelebrityPrivateId()) && tag.getContractTime() != null
                        && tag.getContractTime().before(DateUtils.addMonths(new Date(), -3)))
                .collect(Collectors.toList());

        if (!privateCelebrityTags.isEmpty()) {
            // 为私有网红创建标签分配记录
            privateCelebrityTags.forEach(tagModel -> {
                IgCelebrityTags celebrityTags = createIgCelebrityTags(tagModel, tagModel.getCounselorId(),
                        tagModel.getCounselorName(), userName);
                igTagsUpdateList.add(celebrityTags);
            });

            // 从待分配列表中移除已处理的私有网红标签
            List<String> processedUniqueIds = igTagsUpdateList.stream().map(IgCelebrityTags::getAccount)
                    .collect(Collectors.toList());

            List<IgCelebrityTagsModel> remainingTags = eligibleTagList.stream()
                    .filter(tag -> !processedUniqueIds.contains(tag.getAccount())).collect(Collectors.toList());

            processedData.setIgEligibleTagList(remainingTags);
        }
    }

    /**
     * 执行网红标签分配算法
     */
    private AllocationResult performTagAllocation(ProcessedTagData processedData, InitializationData initData,
            String userName) {
        long methodStartTime = System.currentTimeMillis();
        log.info("[IG标签分配] 开始执行标签分配，合格网红数: {}, 规则数: {}, 顾问数: {}",
                processedData.getIgEligibleTagList() != null ? processedData.getIgEligibleTagList().size() : 0,
                initData.getRules() != null ? initData.getRules().size() : 0,
                initData.getCounselorList() != null ? initData.getCounselorList().size() : 0);

        AllocationResult result = new AllocationResult();

        // 根据分配规则对网红进行分组
        long groupStartTime = System.currentTimeMillis();
        List<List<IgCelebrityTagsModel>> groupedCelebrityTags = groupCelebritiesByRules(
                processedData.getIgEligibleTagList(), initData.getRules());
        long groupDuration = System.currentTimeMillis() - groupStartTime;

        int totalGroupedCount = groupedCelebrityTags.stream().mapToInt(List::size).sum();
        log.info("[IG标签分配] 网红分组完成，耗时: {}ms，分组数: {}, 分组后网红总数: {}", groupDuration, groupedCelebrityTags.size(),
                totalGroupedCount);

        // 执行轮询分配算法
        long allocationStartTime = System.currentTimeMillis();
        List<IgCelebrityTags> allocatedTags = performRoundRobinAllocationNew(groupedCelebrityTags,
                initData.getCounselorList(), processedData, userName);
        long allocationDuration = System.currentTimeMillis() - allocationStartTime;
        log.info("[IG标签分配] 轮询分配完成，耗时: {}ms，分配成功数: {}", allocationDuration, allocatedTags.size());

        // 合并所有更新的标签记录
        long mergeStartTime = System.currentTimeMillis();
        if (!allocatedTags.isEmpty()) {
            processedData.getIgTagsUpdateList().addAll(allocatedTags);
        }
        long mergeDuration = System.currentTimeMillis() - mergeStartTime;
        log.info("[IG标签分配] 合并更新记录完成，耗时: {}ms，更新列表大小: {}", mergeDuration, processedData.getIgTagsUpdateList().size());

        // 生成分配结果统计
        long resultStartTime = System.currentTimeMillis();
        List<KolTagsResultModel> resultList = generateAllocationResults(processedData);
        long resultDuration = System.currentTimeMillis() - resultStartTime;
        log.info("[IG标签分配] 生成结果统计完成，耗时: {}ms，结果数: {}", resultDuration, resultList != null ? resultList.size() : 0);

        result.setIgAllocatedTags(allocatedTags);
        result.setResultList(resultList);
        result.setIgTagsUpdateList(processedData.getIgTagsUpdateList());
        result.setIgTagsCounselorAddList(processedData.getIgTagsCounselorAddList());

        long methodCompleteDuration = System.currentTimeMillis() - methodStartTime;
        log.info("[IG标签分配] 标签分配完成，总耗时: {}ms(分组: {}ms, 分配: {}ms, 合并: {}ms, 统计: {}ms)，" + "分配数: {}, 顾问关联数: {}",
                methodCompleteDuration, groupDuration, allocationDuration, mergeDuration, resultDuration,
                allocatedTags.size(), processedData.getIgTagsCounselorAddList().size());

        return result;
    }

    /**
     * 根据分配规则对网红进行分组
     */
    private List<List<IgCelebrityTagsModel>> groupCelebritiesByRules(List<IgCelebrityTagsModel> eligibleTags,
            List<TiktokCelebrityRule> rules) {
        long methodStartTime = System.currentTimeMillis();
        log.info("[IG网红分组] 开始按规则对网红进行分组，待分组网红数量: {}, 规则数量: {}", eligibleTags.size(), rules.size());

        List<List<IgCelebrityTagsModel>> groupedTags = new ArrayList<>();
        int totalProcessed = 0;

        // 按规则等级对网红进行分组
        for (int i = 0; i < rules.size(); i++) {
            long ruleStartTime = System.currentTimeMillis();
            TiktokCelebrityRule rule = rules.get(i);
            String ruleLevel = rule.getRuleLevel();
            Integer minFollowers = rule.getMinNum();
            Integer maxFollowers = rule.getMaxNum();

            log.info("[IG网红分组] 开始处理第{}个规则，等级: {}, 最小粉丝数: {}, 最大粉丝数: {}", i + 1, ruleLevel, minFollowers, maxFollowers);

            // 根据粉丝数范围筛选网红
            long filterStartTime = System.currentTimeMillis();
            List<IgCelebrityTagsModel> ruleMatchedTags = eligibleTags.stream().filter(celebrity -> {
                long followerCount = celebrity.getFollowerCount().longValue();
                // 最高等级规则(level=1)不设上限
                if ("1".equals(ruleLevel)) {
                    return followerCount >= minFollowers;
                } else {
                    return followerCount >= minFollowers && followerCount < maxFollowers;
                }
            }).collect(Collectors.toList());

            long filterDuration = System.currentTimeMillis() - filterStartTime;
            log.info("[IG网红分组] 规则{}筛选完成，耗时: {}ms，符合条件的网红数量: {}", ruleLevel, filterDuration, ruleMatchedTags.size());

            // 随机打乱顺序确保分配公平性
            long shuffleStartTime = System.currentTimeMillis();
            List<IgCelebrityTagsModel> shuffledTags = randomizeList(ruleMatchedTags);
            long shuffleDuration = System.currentTimeMillis() - shuffleStartTime;

            if (!shuffledTags.isEmpty()) {
                groupedTags.add(shuffledTags);
                totalProcessed += shuffledTags.size();

                // 统计该分组中的粉丝数分布
                long minFollowersInGroup = shuffledTags.stream().mapToLong(tag -> tag.getFollowerCount().longValue())
                        .min().orElse(0);
                long maxFollowersInGroup = shuffledTags.stream().mapToLong(tag -> tag.getFollowerCount().longValue())
                        .max().orElse(0);
                double avgFollowersInGroup = shuffledTags.stream().mapToLong(tag -> tag.getFollowerCount().longValue())
                        .average().orElse(0.0);

                long ruleCompleteDuration = System.currentTimeMillis() - ruleStartTime;
                log.info("[IG网红分组] 规则{}分组完成，总耗时: {}ms(筛选: {}ms, 打乱: {}ms)，" + "分组网红数量: {}, 粉丝数范围: {}-{}, 平均粉丝数: {}",
                        ruleLevel, ruleCompleteDuration, filterDuration, shuffleDuration, shuffledTags.size(),
                        minFollowersInGroup, maxFollowersInGroup, Math.round(avgFollowersInGroup));
            } else {
                long ruleCompleteDuration = System.currentTimeMillis() - ruleStartTime;
                log.warn("[IG网红分组] 规则{}没有符合条件的网红，耗时: {}ms", ruleLevel, ruleCompleteDuration);
            }
        }

        long methodCompleteDuration = System.currentTimeMillis() - methodStartTime;
        log.info("[IG网红分组] 所有规则分组完成，总耗时: {}ms，" + "初始网红数: {}, 处理后网红数: {}, 分组数: {}, 利用率: {}%", methodCompleteDuration,
                eligibleTags.size(), totalProcessed, groupedTags.size(),
                eligibleTags.size() > 0 ? Math.round((double) totalProcessed / eligibleTags.size() * 100) : 0);

        return groupedTags;
    }

    /**
     * 执行轮询分配算法
     */
    private List<IgCelebrityTags> performRoundRobinAllocation(List<List<IgCelebrityTagsModel>> groupedTags,
            List<LoginUser> counselorList, ProcessedTagData processedData, String userName) {
        long methodStartTime = System.currentTimeMillis();
        log.info("[IG轮询分配] 开始执行轮询分配，分组数: {}, 顾问数: {}", groupedTags.size(), counselorList.size());

        List<IgCelebrityTags> allocatedTags = new ArrayList<>();
        int counselorCount = counselorList.size();
        int totalAllocated = 0;

        // 统计各分组的网红总数
        int totalCelebritiesAvailable = groupedTags.stream().mapToInt(List::size).sum();
        log.info("[IG轮询分配] 可分配网红总数: {}", totalCelebritiesAvailable);

        // 为每个顾问分配网红标签
        for (int counselorIndex = 0; counselorIndex < counselorCount; counselorIndex++) {
            long counselorStartTime = System.currentTimeMillis();
            LoginUser counselor = counselorList.get(counselorIndex);
            String counselorId = counselor.getId();
            String counselorName = counselor.getUsername();
            int counselorAllocatedCount = 0;

            log.info("[IG轮询分配] 开始为顾问分配，顾问ID: {}, 顾问名称: {}, 顾问索引: {}/{}", counselorId, counselorName, counselorIndex + 1,
                    counselorCount);

            // 遍历每个规则分组
            for (int groupIndex = 0; groupIndex < groupedTags.size(); groupIndex++) {
                long groupStartTime = System.currentTimeMillis();
                List<IgCelebrityTagsModel> ruleGroup = groupedTags.get(groupIndex);

                log.debug("[IG轮询分配] 处理第{}个分组，分组大小: {}", groupIndex + 1, ruleGroup.size());

                // 计算当前顾问应分配的网红标签
                long calculationStartTime = System.currentTimeMillis();
                List<IgCelebrityTagsModel> assignedTags = calculateCounselorAllocation(ruleGroup, counselorCount,
                        counselorIndex);
                long calculationDuration = System.currentTimeMillis() - calculationStartTime;

                log.debug("[IG轮询分配] 分组{}分配计算完成，耗时: {}ms，分配数量: {}", groupIndex + 1, calculationDuration,
                        assignedTags.size());

                // 为分配的标签创建记录
                long recordCreationStartTime = System.currentTimeMillis();
                assignedTags.forEach(tagModel -> {
                    // 创建网红标签更新记录
                    IgCelebrityTags celebrityTag = createIgCelebrityTags(tagModel, counselorId, counselorName,
                            userName);
                    allocatedTags.add(celebrityTag);

                    // 创建顾问-标签关联记录
                    IgCelebrityTagsCounselor tagsCounselor = createTagsCounselorNew(tagModel, counselorId,
                            counselorName);
                    processedData.getIgTagsCounselorAddList().add(tagsCounselor);

                    // 更新模型中的顾问信息
                    tagModel.setCounselorId(counselorId);
                    tagModel.setCounselorName(counselorName);
                });

                long recordCreationDuration = System.currentTimeMillis() - recordCreationStartTime;
                counselorAllocatedCount += assignedTags.size();

                long groupDuration = System.currentTimeMillis() - groupStartTime;
                log.debug("[IG轮询分配] 分组{}处理完成，总耗时: {}ms(计算: {}ms, 记录创建: {}ms)，分配数: {}", groupIndex + 1, groupDuration,
                        calculationDuration, recordCreationDuration, assignedTags.size());
            }

            totalAllocated += counselorAllocatedCount;
            long counselorDuration = System.currentTimeMillis() - counselorStartTime;

            log.info("[IG轮询分配] 顾问分配完成，顾问: {} ({})，耗时: {}ms，分配数量: {}", counselorName, counselorId, counselorDuration,
                    counselorAllocatedCount);
        }

        long methodCompleteDuration = System.currentTimeMillis() - methodStartTime;
        double allocationRate = totalCelebritiesAvailable > 0
                ? (double) totalAllocated / totalCelebritiesAvailable * 100
                : 0;

        log.info("[IG轮询分配] 轮询分配完成，总耗时: {}ms，" + "可分配网红数: {}, 实际分配数: {}, 分配率: {}%, 平均每人分配: {}", methodCompleteDuration,
                totalCelebritiesAvailable, totalAllocated, Math.round(allocationRate * 100) / 100.0,
                counselorCount > 0 ? Math.round((double) totalAllocated / counselorCount * 100) / 100.0 : 0);

        return allocatedTags;
    }

    /**
     * 执行轮询分配算法(改进版，按网红聚合分配)
     */
    private List<IgCelebrityTags> performRoundRobinAllocationNew(
            List<List<IgCelebrityTagsModel>> groupedTags,
            List<LoginUser> counselorList,
            ProcessedTagData processedData,
            String userName) {
        long methodStartTime = System.currentTimeMillis();
        log.info("[IG轮询分配-新] 开始执行轮询分配，分组数: {}, 顾问数: {}",
                groupedTags.size(), counselorList.size());

        List<IgCelebrityTags> allocatedTags = new ArrayList<>();

        // 初始化顾问分配计数器（记录每个顾问已分配的标签总数）
        Map<String, Integer> counselorAllocationCount = new HashMap<>();
        for (LoginUser counselor : counselorList) {
            counselorAllocationCount.put(counselor.getId(), 0);
        }

        // 遍历每个规则分组
        for (List<IgCelebrityTagsModel> ruleGroup : groupedTags) {
            if (ruleGroup.isEmpty())
                continue;

            // 在当前 ruleGroup 内按 account 聚合（Instagram使用account作为唯一标识）
            Map<String, List<IgCelebrityTagsModel>> authorGroupMap = ruleGroup.stream()
                    .collect(Collectors.groupingBy(IgCelebrityTagsModel::getAccount));

            // 将每个 account 的分组转为列表，并按标签数量降序排序（标签多的网红优先分配）
            List<Map.Entry<String, List<IgCelebrityTagsModel>>> authorGroups = new ArrayList<>(
                    authorGroupMap.entrySet());
            authorGroups.sort((e1, e2) -> Integer.compare(e2.getValue().size(), e1.getValue().size()));

            for (Map.Entry<String, List<IgCelebrityTagsModel>> authorEntry : authorGroups) {
                String account = authorEntry.getKey();
                List<IgCelebrityTagsModel> tagsForAuthor = authorEntry.getValue();
                int tagCount = tagsForAuthor.size();

                // 每次分配前，按当前分配总数升序排序顾问（分配少的优先）
                List<LoginUser> sortedCounselors = counselorList.stream()
                        .sorted(Comparator.comparingInt(c -> counselorAllocationCount.getOrDefault(c.getId(), 0)))
                        .collect(Collectors.toList());

                // 选择分配数最少的顾问（第一个）
                LoginUser assignedCounselor = sortedCounselors.get(0);
                String counselorId = assignedCounselor.getId();
                String counselorName = assignedCounselor.getUsername();

                // 为该 account 下的所有标签分配同一个顾问
                for (IgCelebrityTagsModel tagModel : tagsForAuthor) {
                    // 创建网红标签更新记录
                    IgCelebrityTags celebrityTag = createIgCelebrityTags(
                            tagModel, counselorId, counselorName, userName);
                    allocatedTags.add(celebrityTag);

                    // 创建顾问-标签关联记录
                    IgCelebrityTagsCounselor tagsCounselor = createTagsCounselorNew(
                            tagModel, counselorId, counselorName);
                    processedData.getIgTagsCounselorAddList().add(tagsCounselor);

                    // 更新模型中的顾问信息
                    tagModel.setCounselorId(counselorId);
                    tagModel.setCounselorName(counselorName);
                }

                // 更新顾问分配总数：增加的是该 account 的标签数量
                counselorAllocationCount.merge(counselorId, tagCount, Integer::sum);
            }
        }

        long methodCompleteDuration = System.currentTimeMillis() - methodStartTime;
        int totalAllocated = allocatedTags.size();
        log.info("[IG轮询分配-新] 轮询分配完成，总耗时: {}ms，实际分配数: {}", methodCompleteDuration, totalAllocated);

        return allocatedTags;
    }

    /**
     * 计算特定顾问应分配的网红标签数量和具体标签
     */
    private List<IgCelebrityTagsModel> calculateCounselorAllocation(List<IgCelebrityTagsModel> tagList,
            int counselorCount, int counselorIndex) {

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
    private List<KolTagsResultModel> generateAllocationResults(ProcessedTagData processedData) {

        List<KolTagsResultModel> resultList = new ArrayList<>();

        // 统计私有网红分配情况
        processedData.getIgExistingPrivateTagMap().forEach((key, tagList) -> {
            String[] keyParts = key.split("_");
            String counselorId = keyParts[0];
            String counselorName = oConvertUtils.isEmpty(keyParts[1]) || "null".equals(keyParts[1]) ? "未知顾问"
                    : keyParts[1];
            if (oConvertUtils.isNotEmpty(counselorId)) {
                int count = (int) tagList.stream().map(IgCelebrityTags::getAccount).distinct().count();
                createOrUpdateResult(counselorId, counselorName, count, 0, count, resultList);
            }
        });

        // 统计新分配的网红情况
        Map<String, List<IgCelebrityTags>> newAllocationMap = processedData.getIgEligibleTagList().stream()
                .collect(Collectors.groupingBy(tag -> tag.getCounselorId() + "_" + tag.getCounselorName()));

        newAllocationMap.forEach((key, tagList) -> {
            String[] keyParts = key.split("_");
            String counselorId = keyParts[0];
            String counselorName = keyParts[1];

            int count = (int) tagList.stream().map(IgCelebrityTags::getAccount).distinct().count();
            createOrUpdateResult(counselorId, counselorName, count, count, 0, resultList);
        });

        // 按分配数量和建联数量排序
        return resultList.stream()
                .sorted(Comparator.comparing(KolTagsResultModel::getIsAllotNum)
                        .thenComparing(KolTagsResultModel::getBuildAllianceNum).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 创建或更新结果统计记录
     */
    private void createOrUpdateResult(String counselorId, String counselorName, int buildAllianceNum, int allotNum,
            int isPrivate, List<KolTagsResultModel> resultList) {

        // 查找是否已存在该顾问的记录
        Optional<KolTagsResultModel> existingResult = resultList.stream()
                .filter(result -> result.getId().equals(counselorId)).findFirst();

        if (existingResult.isPresent()) {
            // 更新现有记录
            KolTagsResultModel result = existingResult.get();
            result.setBuildAllianceNum(buildAllianceNum);
            result.setIsAllotNum(allotNum);
            result.setIsPrivate(isPrivate);
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
        if (processedData.getIgTagsUpdateList().isEmpty()) {
            return;
        }

        List<KolAllotLogDetail> logDetailList = allocationResult.getAllotLogDetailList();

        // 按顾问分组统计分配情况
        Map<String, List<IgCelebrityTagsModel>> counselorAllocationMap = processedData.getIgEligibleTagList().stream()
                .collect(Collectors.groupingBy(tag -> tag.getCounselorId() + "_" + tag.getCounselorName()));

        counselorAllocationMap.forEach((counselorKey, tagList) -> {
            String[] keyParts = counselorKey.split("_");
            String counselorId = keyParts[0];
            String counselorName = keyParts[1];

            // 按标签名称统计数量
            JSONObject allocationDetail = new JSONObject();
            Map<String, List<IgCelebrityTagsModel>> tagCountMap = tagList.stream()
                    .collect(Collectors.groupingBy(IgCelebrityTagsModel::getTagName));

            tagCountMap.forEach((tagName, tags) -> {
                allocationDetail.put(tagName, tags.size());
            });

            // 创建日志详情记录
            KolAllotLogDetail logDetail = new KolAllotLogDetail();
            logDetail.setId(IdWorker.get32UUID());
            logDetail.setAllotId(allotLogId);
            logDetail.setCounselorId(counselorId);
            logDetail.setCounselorName(counselorName);
            logDetail.setPlatformType(CommonConstant.IG);
            logDetail.setAllotContent(allocationDetail.toJSONString());
            logDetail.setAllotTime(new Date());
            logDetail.setIsDelete(0);

            logDetailList.add(logDetail);
        });
    }

    /**
     * 保存分配结果到数据库
     */
    private void saveAllocationResults(AllocationResult allocationResult, KolSearchModel searchModel) {
        List<KolTags> kolTagsUpdateList = kolTagsService.getUpdateKolTags(searchModel);
        igTagsService.saveAllotTagsNew(allocationResult.getIgTagsUpdateList(),
                allocationResult.getIgTagsCounselorAddList(), allocationResult.getAllotLogDetailList(),
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
    private IgCelebrityTagsCounselor createTagsCounselorNew(IgCelebrityTagsModel tagModel, String counselorId,
            String counselorName) {

        IgCelebrityTagsCounselor tagsCounselor = new IgCelebrityTagsCounselor();
        tagsCounselor.setId(IdWorker.get32UUID());
        tagsCounselor.setIgUsername(tagModel.getIgUsername());
        tagsCounselor.setIgCelebrityTagsId(tagModel.getId());
        tagsCounselor.setCounselorId(counselorId);
        tagsCounselor.setCounselorName(counselorName);
        tagsCounselor.setAllotTime(new Date());
        tagsCounselor.setAccount(tagModel.getAccount());
        tagsCounselor.setTagName(tagModel.getTagName());
        tagsCounselor.setTagType(tagModel.getTagType());
        tagsCounselor.setFollowerCount(BigInteger.valueOf(tagModel.getFollowerCount()));
        tagsCounselor.setCountry(tagModel.getCountry());
        tagsCounselor.setProductId(tagModel.getProductId());
        tagsCounselor.setAttributeId(tagModel.getAttributeId());
        return tagsCounselor;
    }

    /**
     * 创建网红标签实体
     */
    private static IgCelebrityTags createIgCelebrityTags(IgCelebrityTagsModel tagModel, String counselorId,
            String counselorName, String userName) {

        IgCelebrityTags celebrityTags = new IgCelebrityTags();
        celebrityTags.setId(tagModel.getId());
        celebrityTags.setIgUsername(tagModel.getIgUsername());
        celebrityTags.setTagName(tagModel.getTagName());
        celebrityTags.setIsAllot(YesNoStatus.YES.getCode());
        celebrityTags.setCounselorId(counselorId);
        celebrityTags.setCounselorName(counselorName);
        celebrityTags.setAllotTime(new Date());
        celebrityTags.setUpdateBy(userName);
        celebrityTags.setUpdateTime(new Date());
        return celebrityTags;
    }

    /**
     * 随机打乱列表顺序
     */
    private static List<IgCelebrityTagsModel> randomizeList(List<IgCelebrityTagsModel> list) {
        if (list.isEmpty()) {
            return Collections.emptyList();
        }
        Collections.shuffle(list, new Random());
        return list;
    }

}
