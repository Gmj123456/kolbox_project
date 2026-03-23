package org.jeecg.modules.instagram.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.util.PageUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.entity.IgCelebrity;
import org.jeecg.modules.instagram.entity.IgCelebrityTags;
import org.jeecg.modules.instagram.model.IgAllotTagModel;
import org.jeecg.modules.instagram.model.IgCelebrityModel;
import org.jeecg.modules.instagram.service.IIgCelebrityService;
import org.jeecg.modules.instagram.service.IIgCelebrityTagsService;
import org.jeecg.modules.kol.entity.KolContact;
import org.jeecg.modules.kol.model.KolSearchModel;
import org.jeecg.modules.kol.service.IKolCelebrityService;
import org.jeecg.modules.kol.service.IKolContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

import static org.jeecg.common.constant.CelebrityPromStatus.KOL_MAX_RECORD;
import static org.jeecg.common.constant.CelebrityPromStatus.KOL_MAX_RECORD_DEFAULT;
import static org.jeecg.common.constant.SystemConstants.*;

/**
 * @Description: Instagram网红表
 * @Author: xyc
 * @Date: 2024-12-02
 * @Version: V1.0
 */
@Tag(name = "Instagram网红表")
@RestController
@RequestMapping("/instagram/igCelebrity")
@Slf4j
public class IgCelebrityController extends JeecgController<IgCelebrity, IIgCelebrityService> {
    @Autowired
    private IIgCelebrityService igCelebrityService;
    @Autowired
    private ISysBaseAPI sysBaseAPI;
    @Autowired
    private IKolContactService kolContactService;
    @Autowired
    private IIgCelebrityTagsService igCelebrityTagsService;
    @Autowired
    private IKolCelebrityService kolCelebrityService;

    /**
     * KOL分页列表查询
     *
     * @param searchModel
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "IG网红-分页列表查询")
    @Operation(summary = "IG网红-分页列表查询", description = "IG网红-分页列表查询")
    @PostMapping(value = LIST_URL)
    public Result<?> getKolPageList(@RequestBody KolSearchModel searchModel, @RequestParam(name = PAGE_NO, defaultValue = PAGE_NO_DEFAULT) Integer pageNo, @RequestParam(name = PAGE_SIZE, defaultValue = PAGE_SIZE_DEFAULT) Integer pageSize, HttpServletRequest req) {
        // 记录起始执行时间
        long startExecTime = System.currentTimeMillis();
        Page<IgCelebrityModel> page = PageUtil.getOrderItems(pageNo, pageSize, req);
        page.setSearchCount(false);
        // 获取根据查询条件统计出记录行数
        long kolCount = 0l;
        // 获取分页结果
        IPage<IgCelebrityModel> pageList = null;

        // 判断是否存在查询条件
        kolCelebrityService.checkParams(searchModel);

       /* try {
            // 异步执行第一个查询
            CompletableFuture<Long> kolCountFuture = CompletableFuture.supplyAsync(() -> igCelebrityService.getIgCelebrityCount(searchModel));

            // 异步执行第二个查询
            CompletableFuture<IPage<IgCelebrityModel>> pageListFuture = CompletableFuture.supplyAsync(() -> igCelebrityService.getIgCelebrityModelList(page, searchModel));

            // 等待两个查询都完成
            CompletableFuture.allOf(kolCountFuture, pageListFuture).join();

            // 获取结果
            kolCount = kolCountFuture.get();
            pageList = pageListFuture.get();
        } catch (Exception e) {
            // 获取根据查询条件统计出记录行数
            kolCount = igCelebrityService.getIgCelebrityCount(searchModel);
            // 获取分页结果
            pageList = igCelebrityService.getIgCelebrityModelList(page, searchModel);
        }*/
        pageList = igCelebrityService.getIgCelebrityModelList(page, searchModel);
        if (pageList.getRecords().size() < pageSize) {
            kolCount = pageList.getRecords().size();
        } else {
            kolCount = 5000;
        }
        long configMaxRecordCount = processKolModelList(kolCount, pageList, searchModel);
        // 设置社媒属性
        kolCelebrityService.setAttributes(pageList, searchModel);
        log.info("消耗时间" + (System.currentTimeMillis() - startExecTime) + "毫秒");
        return Result.OK(pageList, (int) configMaxRecordCount);
    }

    /**
     * 已分配网红分页列表查询
     *
     * @param searchModel
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "网红顾问已分配网红分页列表查询")
    @Operation(summary = "网红顾问已分配网红-分页列表查询", description = "网红顾问已分配网红-分页列表查询")
    @PostMapping(value = "/getIgAllottedKolList")
    public Result<?> getIgAllottedKolList(@RequestBody KolSearchModel searchModel, @RequestParam(name = PAGE_NO, defaultValue = PAGE_NO_DEFAULT) Integer pageNo, @RequestParam(name = PAGE_SIZE, defaultValue = PAGE_SIZE_DEFAULT) Integer pageSize, HttpServletRequest req) {
        // 记录起始执行时间
        long startExecTime = System.currentTimeMillis();
        // 获取当前登录用户
        String loginUserId = getUserIdByToken();
        // 目前暂定将查询的网红顾问id设置为当前登录用户，后续如果需要类似超管等查看指定人员的网红列表，可修改此处
        searchModel.setCounselorId(loginUserId);
        Page<IgCelebrityModel> page = PageUtil.getOrderItems(pageNo, pageSize, req);
        page.setSearchCount(false);
        // 获取根据查询条件统计出记录行数
        long celebrityCount = igCelebrityService.getIgAllottedKolCount(searchModel);
        IPage<IgCelebrityModel> pageList = igCelebrityService.getIgAllottedKolList(page, searchModel);
        long configMaxRecordCount = processKolModelList(celebrityCount, pageList, searchModel);
        log.info("消耗时间" + (System.currentTimeMillis() - startExecTime) + "毫秒");
        return Result.OK(pageList, (int) configMaxRecordCount);
    }


    /**
     * 处理网红视图模型列表 更新建联字段和标签字段
     *
     * @param kolModelPageList
     * @return
     */
    private long processKolModelList(Long kolCount, IPage<IgCelebrityModel> kolModelPageList, KolSearchModel searchModel) {
        // 处理分页页码数据
        // 获取字典配置最大返回记录，默认为 5000
        long configMaxRecordCount = sysBaseAPI.queryDictItemsByCode(CommonConstant.RESULT_COUNT).stream().filter(x -> KOL_MAX_RECORD.equals(x.getValue())).findFirst().map(dict -> Integer.parseInt(dict.getText())).orElse(KOL_MAX_RECORD_DEFAULT);
        // 比较配置和查询结果，取较大值作为返回值
        long maxRecordCount = configMaxRecordCount;
        configMaxRecordCount = Math.max(configMaxRecordCount, kolCount);
        kolCount = Math.min(maxRecordCount, kolCount);

        // 处理分页记录
        List<IgCelebrityModel> kolModelList = kolModelPageList.getRecords();
        if (!kolModelList.isEmpty()) {
            // 获取账号id列表
            List<String> uniqueIdList = kolModelList.stream().map(IgCelebrityModel::getAccount).collect(Collectors.toList());
            // 更新开发建联历史字段 纵向变横向
            List<KolContact> kolContactListByIds = kolContactService.getContactListByIds(uniqueIdList);
            if (!kolContactListByIds.isEmpty()) {
//                updateKolContactList(kolModelList, kolContactListByIds);
                kolContactService.updateContactHistory(kolModelList, kolContactListByIds, IgCelebrityModel::getAccount, IgCelebrityModel::setContactHistory);
            }
            // 处理标签字段赋值
            updateKolTagList(kolModelList, searchModel);
        }
        kolModelPageList.setTotal(kolCount);
        return configMaxRecordCount;
    }

    /**
     * 更新网红开发建联历史信息 格式为 品牌名称-顾问简称 用逗号分割
     *
     * @param celebrityModels
     * @param kolContactList
     */
    private void updateKolContactList(List<IgCelebrityModel> celebrityModels, List<KolContact> kolContactList) {
        Map<String, String> uniqueIdContactMap = kolContactList.stream().collect(Collectors.groupingBy(KolContact::getUniqueId, Collectors.mapping(p -> p.getBrandName() + "-" + p.getCounselorShortName(), Collectors.joining(","))));
        for (Map.Entry<String, String> entry : uniqueIdContactMap.entrySet()) {
            String uniqueId = entry.getKey();
            String contactValue = entry.getValue();
            Optional<IgCelebrityModel> celebrityModelOptional = celebrityModels.stream().filter(x -> x.getIgUsername().equals(uniqueId)).findFirst();
            if (celebrityModelOptional.isPresent()) {
                IgCelebrityModel celebrityModel = celebrityModelOptional.get();
                celebrityModel.setContactHistory(contactValue);
            }
        }
    }

    /**
     * 从网红标签表中获取标签信息
     * 根据网红账号分组聚合标签列表
     *
     * @param kolModelList 网红模型列表，用于获取用户名并更新标签信息
     */
    private void updateKolTagList(List<IgCelebrityModel> kolModelList, KolSearchModel searchModel) {
        // 提取网红的用户名列表，以便后续查询使用
        List<String> igUsernameList = kolModelList.stream().map(IgCelebrityModel::getIgUsername).collect(Collectors.toList());

        // 创建查询条件，配置部分必要字段
        LambdaQueryWrapper<IgCelebrityTags> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(IgCelebrityTags::getIgUsername, IgCelebrityTags::getTagName, IgCelebrityTags::getIsAllot, IgCelebrityTags::getTagType, IgCelebrityTags::getAllotTime);
        queryWrapper.in(IgCelebrityTags::getIgUsername, igUsernameList);
        if (oConvertUtils.isNotEmpty(searchModel.getCountryCode())) {
            queryWrapper.eq(IgCelebrityTags::getCountry, searchModel.getCountryCode());
        }

        // 获取网标签列表
        List<IgCelebrityTags> igCelebrityTagsList = igCelebrityTagsService.list(queryWrapper);

        // 将查询到的标签信息转换为IgTagModel对象列表，并按IgUsername分组
        Map<String, List<IgAllotTagModel>> kolTagMap = igCelebrityTagsList.stream().map(tag -> {
            IgAllotTagModel igTagModel = new IgAllotTagModel();
            igTagModel.setIgUsername(tag.getIgUsername());
            igTagModel.setTagName(tag.getTagName());
            igTagModel.setIsAllot(tag.getIsAllot());
            igTagModel.setAllotTime(tag.getAllotTime());
            igTagModel.setTagType(tag.getTagType());
            return igTagModel;
        }).collect(Collectors.groupingBy(IgAllotTagModel::getIgUsername));

        // 遍历网红模型列表，为每个网红模型添加对应的标签信息
        kolModelList.forEach(x -> {
            // 筛选出与当前网红用户名匹配的标签信息列表
            List<IgAllotTagModel> tagModelList = kolTagMap.getOrDefault(x.getIgUsername(), Collections.emptyList());
            // 将筛选出的标签信息列表设置为网红模型的属性
            if (oConvertUtils.listIsNotEmpty(tagModelList)) {
                x.setTagList(tagModelList);
            }
        });
    }

    /**
     * 添加
     *
     * @param igCelebrity
     * @return
     */
    @AutoLog(value = "ig_celebrity-添加")
    @Operation(summary = "ig_celebrity-添加", description = "ig_celebrity-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody IgCelebrity igCelebrity) {
        igCelebrityService.save(igCelebrity);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param igCelebrity
     * @return
     */
    @AutoLog(value = "ig_celebrity-编辑")
    @Operation(summary = "ig_celebrity-编辑", description = "ig_celebrity-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody IgCelebrity igCelebrity) {
        igCelebrityService.updateById(igCelebrity);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "ig_celebrity-通过id删除")
    @Operation(summary = "ig_celebrity-通过id删除", description = "ig_celebrity-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        igCelebrityService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "ig_celebrity-批量删除")
    @Operation(summary = "ig_celebrity-批量删除", description = "ig_celebrity-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.igCelebrityService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "ig_celebrity-通过id查询")
    @Operation(summary = "ig_celebrity-通过id查询", description = "ig_celebrity-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        IgCelebrity igCelebrity = igCelebrityService.getById(id);
        if (igCelebrity == null) {
            return Result.error("未找到对应数据");
        }
        return Result.ok(igCelebrity);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param igCelebrity
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, IgCelebrity igCelebrity) {
        return super.exportXls(request, igCelebrity, IgCelebrity.class, "ig_celebrity");
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
        return super.importExcel(request, response, IgCelebrity.class);
    }

    @PutMapping(value = "/editEmail")
    public Result<?> editEmail(@RequestBody IgCelebrity igCelebrity) {
        IgCelebrity celebrity = new IgCelebrity();
        celebrity.setId(igCelebrity.getId());
        celebrity.setEmail(igCelebrity.getEmail());
        igCelebrityService.updateById(celebrity);
        return Result.ok("编辑成功!");
    }
}
