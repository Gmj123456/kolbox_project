package org.jeecg.modules.youtube.controller;

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
import org.jeecg.modules.kol.entity.KolContact;
import org.jeecg.modules.kol.model.KolSearchModel;
import org.jeecg.modules.kol.service.IKolCelebrityService;
import org.jeecg.modules.kol.service.IKolContactService;
import org.jeecg.modules.youtube.entity.YoutubeCelebrity;
import org.jeecg.modules.youtube.entity.YoutubeCelebrityTags;
import org.jeecg.modules.youtube.model.YtAllotTagModel;
import org.jeecg.modules.youtube.model.YtCelebrityModel;
import org.jeecg.modules.youtube.service.IYtCelebrityService;
import org.jeecg.modules.youtube.service.IYtCelebrityTagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.jeecg.common.constant.CelebrityPromStatus.*;
import static org.jeecg.common.constant.SystemConstants.*;

/**
 * @Description: YT网红列表控制层
 * @Author: xyc
 * @Date: 2024-12-26 17:30:58
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "YT网红列表")
@RestController
@RequestMapping("/youtube/ytCelebrity")
public class YtCelebrityController extends JeecgController<YoutubeCelebrity, IYtCelebrityService> {
    @Autowired
    private ISysBaseAPI sysBaseAPI;
    @Autowired
    private IYtCelebrityService ytCelebrityService;
    @Autowired
    private IKolContactService kolContactService;
    @Autowired
    private IYtCelebrityTagsService ytTagService;
    @Autowired
    private IKolCelebrityService kolCelebrityService;

//   region =================新增重构方法，如有遗漏的方法 可参考对应的过期控制层类(YoutubeCelebrityController)进行补充完善===============================

    /**
     * KOL分页列表查询
     *
     * @param searchModel
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "TK网红-分页列表查询")
    @Operation(summary = "TK网红-分页列表查询", description = "TK网红-分页列表查询")
    @PostMapping(value = LIST_URL)
    public Result<?> getKolPageList(@RequestBody KolSearchModel searchModel, @RequestParam(name = PAGE_NO, defaultValue = PAGE_NO_DEFAULT) Integer pageNo, @RequestParam(name =
            PAGE_SIZE, defaultValue = PAGE_SIZE_DEFAULT) Integer pageSize, HttpServletRequest req) {
        // 记录起始执行时间
        long startExecTime = System.currentTimeMillis();
        Page<YtCelebrityModel> page = PageUtil.getOrderItems(pageNo, pageSize, req);
        page.setSearchCount(false);
        // 获取根据查询条件统计出记录行数
        Integer kolCount = 0;
        // 获取分页结果
        IPage<YtCelebrityModel> pageList = null;

        // 判断是否存在查询条件
        kolCelebrityService.checkParams(searchModel);

        /*try {
            // 异步执行第一个查询
            CompletableFuture<Integer> kolCountFuture = CompletableFuture.supplyAsync(() -> ytCelebrityService.getCelebrityCount(searchModel));

            // 异步执行第二个查询
            CompletableFuture<IPage<YtCelebrityModel>> pageListFuture = CompletableFuture.supplyAsync(() -> ytCelebrityService.getCelebrityModelList(page, searchModel));

            // 等待两个查询都完成
            CompletableFuture.allOf(kolCountFuture, pageListFuture).join();

            // 获取结果
            kolCount = kolCountFuture.get();
            pageList = pageListFuture.get();
        } catch (Exception e) {
            kolCount = ytCelebrityService.getCelebrityCount(searchModel);
            // 获取分页结果
            pageList = ytCelebrityService.getCelebrityModelList(page, searchModel);
        }*/
        pageList = ytCelebrityService.getCelebrityModelList(page, searchModel);

        if (pageList.getRecords().size() < pageSize) {
            kolCount = pageList.getRecords().size();
        } else {
            kolCount = 5000;
        }
        long configMaxRecordCount = processKolModelList(kolCount, pageList,searchModel);
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
    @PostMapping(value = "/getTkAllottedKolList")
    public Result<?> getTkAllottedKolList(@RequestBody KolSearchModel searchModel, @RequestParam(name = PAGE_NO, defaultValue = PAGE_NO_DEFAULT) Integer pageNo,
                                          @RequestParam(name = PAGE_SIZE, defaultValue = PAGE_SIZE_DEFAULT) Integer pageSize, HttpServletRequest req) {
        // 记录起始执行时间
        long startExecTime = System.currentTimeMillis();
        // 获取当前登录用户
        String loginUserId = getUserIdByToken();
        // 目前暂定将查询的网红顾问id设置为当前登录用户，后续如果需要类似超管等查看指定人员的网红列表，可修改此处
        searchModel.setCounselorId(loginUserId);
        Page<YtCelebrityModel> page = PageUtil.getOrderItems(pageNo, pageSize, req);
        page.setSearchCount(false);
        // 获取根据查询条件统计出记录行数
        Integer celebrityCount = ytCelebrityService.getAllottedKolCount(searchModel);
        IPage<YtCelebrityModel> pageList = ytCelebrityService.getYtAllottedKolList(page, searchModel);
        long configMaxRecordCount = processKolModelList(celebrityCount, pageList,searchModel);
        log.info("消耗时间" + (System.currentTimeMillis() - startExecTime) + "毫秒");
        return Result.OK(pageList, (int) configMaxRecordCount);
    }


    /**
     * 处理网红视图模型列表 更新建联字段和标签字段
     *
     * @param kolModelPageList
     * @return
     */
    private long processKolModelList(Integer kolCount, IPage<YtCelebrityModel> kolModelPageList,KolSearchModel searchModel) {
        // 处理分页页码数据
        // 获取字典配置最大返回记录，默认为 5000
        Integer configMaxRecordCount =
                sysBaseAPI.queryDictItemsByCode(CommonConstant.RESULT_COUNT).stream().filter(x -> KOL_MAX_RECORD.equals(x.getValue())).findFirst().map(dict -> Integer.parseInt(dict.getText())).orElse(KOL_MAX_RECORD_DEFAULT);
        // 比较配置和查询结果，取较大值作为返回值
        Integer maxRecordCount = configMaxRecordCount;
        configMaxRecordCount = Math.max(configMaxRecordCount, kolCount);
        kolCount = Math.min(maxRecordCount, kolCount);

        // 处理分页记录
        List<YtCelebrityModel> kolModelList = kolModelPageList.getRecords();
        if (!kolModelList.isEmpty()) {
            // 获取账号id列表
            List<String> uniqueIdList = kolModelList.stream().map(YtCelebrityModel::getAccount).collect(Collectors.toList());
            // 更新开发建联历史字段 纵向变横向
            List<KolContact> kolContactListByIds = kolContactService.getContactListByIds(uniqueIdList);
            if (!kolContactListByIds.isEmpty()) {
                // 更新网红开发建联历史信息 格式为 品牌名称-顾问简称 用逗号分割
                kolContactService.updateContactHistory(kolModelList, kolContactListByIds, YtCelebrityModel::getAccount, YtCelebrityModel::setContactHistory);
            }
            // 处理标签字段赋值
            updateKolTagList(kolModelList,searchModel);
        }
        kolModelPageList.setTotal(kolCount);
        return configMaxRecordCount;
    }


    /**
     * 从网红标签表中获取标签信息
     * 根据网红账号分组聚合标签列表
     *
     * @param kolModelList 网红模型列表，用于获取用户名并更新标签信息
     */
    private void updateKolTagList(List<YtCelebrityModel> kolModelList,KolSearchModel searchModel) {
        // 提取网红的用户名列表，以便后续查询使用
        List<String> kolUniqueId = kolModelList.stream().map(YtCelebrityModel::getUsername).collect(Collectors.toList());

        // 创建查询条件，配置部分必要字段
        LambdaQueryWrapper<YoutubeCelebrityTags> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(YoutubeCelebrityTags::getUsername, YoutubeCelebrityTags::getTagName, YoutubeCelebrityTags::getIsAllot, YoutubeCelebrityTags::getTagType,
                YoutubeCelebrityTags::getAllotTime);
        queryWrapper.in(YoutubeCelebrityTags::getUsername, kolUniqueId);
        if (oConvertUtils.isNotEmpty(searchModel.getCountryCode())) {
            queryWrapper.eq(YoutubeCelebrityTags::getCountry, searchModel.getCountryCode());
        }
        // 获取网标签列表
        List<YoutubeCelebrityTags> ytCelebrityTagsList = ytTagService.list(queryWrapper);

        // 将查询到的标签信息转换为kolTagMap对象列表，并按UniqueId分组
        Map<String, List<YtAllotTagModel>> kolTagMap = ytCelebrityTagsList.stream().map(tag -> {
            YtAllotTagModel tagModel = new YtAllotTagModel();
            tagModel.setUsername(tag.getUsername());
            tagModel.setTagName(tag.getTagName());
            tagModel.setIsAllot(tag.getIsAllot());
            tagModel.setAllotTime(tag.getAllotTime());
            tagModel.setTagType(tag.getTagType());
            return tagModel;
        }).collect(Collectors.groupingBy(YtAllotTagModel::getUsername));

        // 遍历网红模型列表，为每个网红模型添加对应的标签信息
        kolModelList.forEach(x -> {
            // 筛选出与当前网红用户名匹配的标签信息列表
            List<YtAllotTagModel> tagModelList = kolTagMap.getOrDefault(x.getUsername(), Collections.emptyList());
            // 将筛选出的标签信息列表设置为网红模型的属性
            if (oConvertUtils.listIsNotEmpty(tagModelList)) {
                x.setTagList(tagModelList);
            }
        });
    }

//    endregion

    /**
     * 方法描述: 查询未分配标签网红数量
     *
     * @author nqr
     * @date 2025/01/06 14:58
     **/
    @AutoLog(value = "查询未分配标签网红数量")
    @Operation(summary = "查询未分配标签网红数量", description = "查询未分配标签网红数量")
    @PostMapping(value = "/getNotAllotTagsList")
    public Result<?> getNotAllotTagsList(@RequestBody KolSearchModel kolSearchModel, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(name =
            "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Page<YtCelebrityModel> page = PageUtil.getOrderItems(pageNo, pageSize, req);
        Integer allotDays =
                sysBaseAPI.queryDictItemsByCode(CommonConstant.ALLOT_DAYS).stream().filter(x -> KOL_ALLOT_MAX_DAYS.equals(x.getValue())).findFirst().map(dict -> Integer.parseInt(dict.getText())).orElse(KOL_ALLOT_MAX_DAYS_DEFAULT);
        kolSearchModel.setAllotDays(allotDays);
        page.setSearchCount(false);
        int count = 5000;
        // count = ytCelebrityService.getNotAllotTagsListCount(kolSearchModel);
        IPage<Map<String, Object>> notAllotTagsList = ytCelebrityService.getNotAllotTagsList(page, kolSearchModel);
        if (notAllotTagsList.getRecords().isEmpty()) {
            count = 0;
        } else if (notAllotTagsList.getRecords().size() < pageSize) {
            count = notAllotTagsList.getRecords().size();
        }
        notAllotTagsList.setTotal(count);
        return Result.ok(notAllotTagsList);
    }

    @PutMapping(value = "/editEmail")
    public Result<?> editEmail(@RequestBody YoutubeCelebrity celebrityNew) {
        YoutubeCelebrity celebrity = new YoutubeCelebrity();
        celebrity.setId(celebrityNew.getId());
        celebrity.setEmail(celebrityNew.getEmail());
        ytCelebrityService.updateById(celebrity);
        return Result.ok("编辑成功!");
    }

    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        YoutubeCelebrity celebrity = ytCelebrityService.getById(id);
        return Result.ok(celebrity);
    }
}
