package org.jeecg.modules.tiktok.controller;

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
import org.jeecg.modules.kol.service.IKolAllotService;
import org.jeecg.modules.kol.service.IKolCelebrityService;
import org.jeecg.modules.kol.service.IKolContactService;
import org.jeecg.modules.tiktok.entity.TiktokCelebrity;
import org.jeecg.modules.tiktok.entity.TiktokCelebrityTags;
import org.jeecg.modules.tiktok.model.TkAllotTagModel;
import org.jeecg.modules.tiktok.model.TkCelebrityModel;
import org.jeecg.modules.tiktok.service.ITiktokCelebrityService;
import org.jeecg.modules.tiktok.service.ITiktokCelebrityTagsService;
import org.jeecg.modules.youtube.entity.YoutubeCelebrity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.jeecg.common.constant.CelebrityPromStatus.KOL_MAX_RECORD;
import static org.jeecg.common.constant.CelebrityPromStatus.KOL_MAX_RECORD_DEFAULT;
import static org.jeecg.common.constant.SystemConstants.*;

/**
 * @Description: Tiktok网红表-新版
 * @Author: xyc
 * @Date: 2024-12-26 08:20:54
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "Tiktok网红表")
@RestController
@RequestMapping("/tiktok/tkCelebrity")
public class TkCelebrityController extends JeecgController<TiktokCelebrity, ITiktokCelebrityService> {
    @Autowired
    private ITiktokCelebrityService tkCelebrityService;
    @Autowired
    private ISysBaseAPI sysBaseAPI;
    @Autowired
    private IKolContactService kolContactService;
    @Autowired
    private ITiktokCelebrityTagsService tkCelebrityTagsService;
    @Autowired
    private IKolCelebrityService kolCelebrityService;

//    region =================新增重构方法，如有遗漏的方法 可参考对应的过期控制层类(TiktokCelebrityController)进行补充完善===============================

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
    public Result<?> getKolPageList(@RequestBody KolSearchModel searchModel,
                                    @RequestParam(name = PAGE_NO, defaultValue = PAGE_NO_DEFAULT) Integer pageNo,
                                    @RequestParam(name = PAGE_SIZE, defaultValue = PAGE_SIZE_DEFAULT) Integer pageSize,
                                    HttpServletRequest req) {
        // 记录起始执行时间
        long startExecTime = System.currentTimeMillis();
        Page<TkCelebrityModel> page = PageUtil.getOrderItems(pageNo, pageSize, req);
        page.setSearchCount(false);
        // 获取根据查询条件统计出记录行数

        // 判断是否存在查询条件
        kolCelebrityService.checkParams(searchModel);

        Long kolCount = 0L;
        IPage<TkCelebrityModel> pageList = null;
   /*     try {
            // 异步执行第一个查询
            CompletableFuture<Long> kolCountFuture = CompletableFuture.supplyAsync(() -> tkCelebrityService.getTkCelebrityCount(searchModel));

            // 异步执行第二个查询
            CompletableFuture<IPage<TkCelebrityModel>> pageListFuture = CompletableFuture.supplyAsync(() -> tkCelebrityService.getTkCelebrityModelList(page, searchModel));

            // 等待两个查询都完成
            CompletableFuture.allOf(kolCountFuture, pageListFuture).join();

            // 获取结果
            kolCount = kolCountFuture.get();
            pageList = pageListFuture.get();
        } catch (Exception e) {
            kolCount = tkCelebrityService.getTkCelebrityCount(searchModel);
            // 获取分页结果
            pageList = tkCelebrityService.getTkCelebrityModelList(page, searchModel);
        }*/

        pageList = tkCelebrityService.getTkCelebrityModelList(page, searchModel);
        if (pageList.getRecords().size() < pageSize) {
            kolCount = (long) pageList.getRecords().size();
        } else {
            kolCount = 5000L;
        }
        long configMaxRecordCount = processKolModelList(kolCount, pageList, tkCelebrityService, 2);
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
    public Result<?> getTkAllottedKolList(@RequestBody KolSearchModel searchModel,
                                          @RequestParam(name = PAGE_NO, defaultValue = PAGE_NO_DEFAULT) Integer pageNo,
                                          @RequestParam(name = PAGE_SIZE, defaultValue = PAGE_SIZE_DEFAULT) Integer pageSize,
                                          HttpServletRequest req) {
        // 记录起始执行时间
        long startExecTime = System.currentTimeMillis();
        // 获取当前登录用户
        String loginUserId = getUserIdByToken();
        // 目前暂定将查询的网红顾问id设置为当前登录用户，后续如果需要类似超管等查看指定人员的网红列表，可修改此处
        searchModel.setCounselorId(loginUserId);
        Page<TkCelebrityModel> page = PageUtil.getOrderItems(pageNo, pageSize, req);
        page.setSearchCount(false);
        // 获取根据查询条件统计出记录行数
        long celebrityCount = tkCelebrityService.getTkAllottedKolCount(searchModel);
        IPage<TkCelebrityModel> pageList = tkCelebrityService.getTkAllottedKolList(page, searchModel);
        long configMaxRecordCount = processKolModelList(celebrityCount, pageList, tkCelebrityService, 2);
        log.info("消耗时间" + (System.currentTimeMillis() - startExecTime) + "毫秒");
        return Result.OK(pageList, (int) configMaxRecordCount);
    }


    /**
     * 处理网红视图模型列表 更新建联字段和标签字段
     *
     * @param kolModelPageList
     * @return
     */
    private long processKolModelList(Long kolCount, IPage<TkCelebrityModel> kolModelPageList, IKolAllotService allotService,
                                     int platformType) {
        // 处理分页页码数据
        // 获取字典配置最大返回记录，默认为 5000
        int configMaxRecordCount = sysBaseAPI.queryDictItemsByCode(CommonConstant.RESULT_COUNT).stream()
                .filter(x -> KOL_MAX_RECORD.equals(x.getValue()))
                .findFirst()
                .map(dict -> Integer.parseInt(dict.getText()))
                .orElse(KOL_MAX_RECORD_DEFAULT);
        // 比较配置和查询结果，取较大值作为返回值
        int maxRecordCount = configMaxRecordCount;
        configMaxRecordCount = (int) Math.max(configMaxRecordCount, kolCount);
        kolCount = Math.min(maxRecordCount, kolCount);

        // 处理分页记录
        List<TkCelebrityModel> tkCelebrityModel = kolModelPageList.getRecords();
        if (!tkCelebrityModel.isEmpty()) {
            List<String> uniqueIdList = tkCelebrityModel.stream().map(TkCelebrityModel::getAuthorId).collect(Collectors.toList());

            // 更新开发建联历史字段 纵向变横向
            List<KolContact> kolContactListByIds = kolContactService.getContactListByIds(uniqueIdList);
            kolContactListByIds = kolContactListByIds.stream().filter(x -> x.getPlatformType() == platformType).collect(Collectors.toList());
            if (!kolContactListByIds.isEmpty()) {
                kolContactService.updateContactHistory(tkCelebrityModel, kolContactListByIds, TkCelebrityModel::getAuthorId,
                        TkCelebrityModel::setContactHistory);
            }
            // 处理标签字段赋值
            updateKolTagList(tkCelebrityModel);
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
    private void updateKolTagList(List<TkCelebrityModel> kolModelList) {
        // 提取网红的用户名列表，以便后续查询使用
        List<String> kolUniqueId = kolModelList.stream().map(TkCelebrityModel::getUniqueId).collect(Collectors.toList());

        // 创建查询条件，配置部分必要字段
        LambdaQueryWrapper<TiktokCelebrityTags> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(TiktokCelebrityTags::getUniqueId,
                TiktokCelebrityTags::getTagName,
                TiktokCelebrityTags::getIsAllot,
                TiktokCelebrityTags::getTagType,
                TiktokCelebrityTags::getAllotTime);
        queryWrapper.in(TiktokCelebrityTags::getUniqueId, kolUniqueId);

        // 获取网标签列表
        List<TiktokCelebrityTags> tkCelebrityTagsList = tkCelebrityTagsService.list(queryWrapper);

        // 将查询到的标签信息转换为kolTagMap对象列表，并按UniqueId分组
        Map<String, List<TkAllotTagModel>> kolTagMap = tkCelebrityTagsList.stream()
                .map(tag -> {
                    TkAllotTagModel tagModel = new TkAllotTagModel();
                    tagModel.setUniqueId(tag.getUniqueId());
                    tagModel.setTagName(tag.getTagName());
                    tagModel.setIsAllot(tag.getIsAllot());
                    tagModel.setAllotTime(tag.getAllotTime());
                    tagModel.setTagType(tag.getTagType());
                    return tagModel;
                })
                .collect(Collectors.groupingBy(TkAllotTagModel::getUniqueId));

        // 遍历网红模型列表，为每个网红模型添加对应的标签信息
        kolModelList.forEach(x -> {
            // 筛选出与当前网红用户名匹配的标签信息列表
            List<TkAllotTagModel> tagModelList = kolTagMap.getOrDefault(x.getUniqueId(), Collections.emptyList());
            // 将筛选出的标签信息列表设置为网红模型的属性
            // 根据 tagName 字段去重
            if (oConvertUtils.listIsNotEmpty(tagModelList)) {
                Set<String> seen = ConcurrentHashMap.newKeySet();
                List<TkAllotTagModel> distinctTagList = tagModelList.stream()
                        .filter(tag -> seen.add(tag.getTagName()))
                        .collect(Collectors.toList());
                x.setTagList(distinctTagList);
            }
        });
    }

    @PutMapping(value = "/editEmail")
    public Result<?> editEmail(@RequestBody TiktokCelebrity celebrity) {
        TiktokCelebrity celebrityNew = new TiktokCelebrity();
        celebrityNew.setId(celebrity.getId());
        celebrityNew.setEmail(celebrity.getEmail());
        tkCelebrityService.updateById(celebrityNew);
        return Result.ok("编辑成功!");
    }
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        TiktokCelebrity celebrity = tkCelebrityService.getById(id);
        return Result.ok(celebrity);
    }
}



