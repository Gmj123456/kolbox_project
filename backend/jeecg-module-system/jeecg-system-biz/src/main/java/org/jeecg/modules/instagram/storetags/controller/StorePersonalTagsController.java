package org.jeecg.modules.instagram.storetags.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.SheetConstants;
import org.jeecg.common.constant.SystemConstants;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.storetags.entity.StorePersonalTags;
import org.jeecg.modules.instagram.storetags.service.IStorePersonalTagsService;
import org.jeecg.modules.feishu.service.IFeishuService;
import org.jeecg.modules.kol.wechatexcel.config.WechatDocConfig;
import org.jeecg.modules.kol.wechatexcel.model.Record;
import org.jeecg.modules.kol.wechatexcel.service.WechatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @Description: 个性化标签
 * @Author: nqr
 * @Date: 2023-07-11
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "个性化标签")
@RestController
@RequestMapping("/personalTags")
public class StorePersonalTagsController extends JeecgController<StorePersonalTags, IStorePersonalTagsService> {
    @Autowired
    private IStorePersonalTagsService storePersonalTagsService;
    @Resource
    private WechatService wechatService;
    @Resource
    private WechatDocConfig wechatDocConfig;
    @Resource
    private IFeishuService feishuService;


    /**
     * 分页列表查询
     *
     * @param storePersonalTags
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "个性化标签-" + SystemConstants.PAGE_LIST_QUERY)
@Operation(summary = "个性化标签-" + SystemConstants.PAGE_LIST_QUERY, description = "个性化标签-" + SystemConstants.PAGE_LIST_QUERY)
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StorePersonalTags storePersonalTags,
                                   @RequestParam(name = SystemConstants.PAGE_NO, defaultValue = SystemConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                   @RequestParam(name = SystemConstants.PAGE_SIZE, defaultValue = SystemConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
                                   HttpServletRequest req) {
        LambdaQueryWrapper<StorePersonalTags> queryWrapper = QueryGenerator.initQueryWrapper(storePersonalTags, req.getParameterMap()).lambda();
        queryWrapper.eq(StorePersonalTags::getDelFlag, 0);
        // queryWrapper.orderByAsc(StorePersonalTags::getSortCode);
        Page<StorePersonalTags> page = new Page<StorePersonalTags>(pageNo, pageSize);

        IPage<StorePersonalTags> pageList = storePersonalTagsService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @AutoLog(value = "个性化标签-" + SystemConstants.PAGE_LIST_QUERY)
@Operation(summary = "个性化标签-" + SystemConstants.PAGE_LIST_QUERY, description = "个性化标签-" + SystemConstants.PAGE_LIST_QUERY)
    @GetMapping(value = "/listAll")
    public Result<?> queryListAll(StorePersonalTags storePersonalTags) {
        return Result.ok(storePersonalTagsService.lambdaQuery().eq(StorePersonalTags::getDelFlag, 0).orderByAsc(StorePersonalTags::getSortCode).list());
    }

    /**
     * 添加
     *
     * @param storePersonalTags
     * @return
     */
    @AutoLog(value = "个性化标签-" + SystemConstants.ADD)
@Operation(summary = "个性化标签-" + SystemConstants.ADD, description = "个性化标签-" + SystemConstants.ADD)
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody StorePersonalTags storePersonalTags) {
        String tagName = storePersonalTags.getTagName();
        LambdaQueryWrapper<StorePersonalTags> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StorePersonalTags::getTagName, tagName);
        queryWrapper.eq(StorePersonalTags::getDelFlag, 0);
        List<StorePersonalTags> list = storePersonalTagsService.list(queryWrapper);
        if (!list.isEmpty()) {
            return Result.error("标签已存在");
        }
        storePersonalTags.setId(IdWorker.get32UUID());
        storePersonalTagsService.save(storePersonalTags);
        // 异步更新文档
        // asyncUpdateLabels(1, storePersonalTags);
        asyncFeishuUpdateLabels(0);
        return Result.ok();
    }

    /**
     * @description: 同步更新飞书表格
     * @author: nqr
     * @date: 2025/9/18 14:33
     **/
    private void asyncFeishuUpdateLabels(int type) {
       /* List<StorePersonalTags> personalTags = storePersonalTagsService.lambdaQuery().eq(StorePersonalTags::getDelFlag, 0).list();
        List<String> tagNames = personalTags.stream().map(StorePersonalTags::getTagName).filter(oConvertUtils::isNotEmpty).collect(Collectors.toList());
        // 获取当前操作
        switch (type) {
            case 0:
                feishuService.insertOrUpdatePersonalTagsSheetBatch(0, SheetConstants.PERSONAL_TAGS, tagNames);
                break;
            case 1:
                // 删除记录
                feishuService.insertOrUpdatePersonalTagsSheetBatch(1, SheetConstants.PERSONAL_TAGS, tagNames);
                break;
        }*/
    }

    private void asyncUpdateLabels(int type, StorePersonalTags storePersonalTags) {
        // 同步更新企微选项
        try {
            CompletableFuture.runAsync(() -> {
                /*List<String> tags = storePersonalTagsService.lambdaQuery().eq(StorePersonalTags::getDelFlag, 0).list().stream()
                        .map(StorePersonalTags::getTagName)
                        .collect(Collectors.toList());
                List<Map<String, Object>> maps = Collections.singletonList(new HashMap<String, Object>() {{
                    put("field_id", "fVgDhL");
                    put("field_title", "个性化标签");
                    put("field_type", "FIELD_TYPE_SELECT");
                    put("property_select", new HashMap<String, Object>() {{
                        put("is_multiple", true);
                        put("is_quick_add", false);
                        put("options", new ArrayList<Map<String, Object>>() {{
                            for (String tag : tags) {
                                add(Collections.singletonMap("text", tag));
                            }
                        }});
                    }});
                }});
                wechatService.updateFields(wechatDocConfig.getTemplateDocId(), wechatDocConfig.getPrivateSheetId(), maps);*/
              /*  List<StorePersonalTags> personalTagsList = storePersonalTagsService.lambdaQuery().eq(StorePersonalTags::getDelFlag, 0).list();
                List<Map<String, Object>> recores = new ArrayList<Map<String, Object>>() {{
                    personalTagsList.forEach(title -> {
                        add(new HashMap<String, Object>() {{
                            put("values", new HashMap<String, Object>() {{
                                put("个性化标签", new ArrayList<Map<String, Object>>() {{
                                    add(new HashMap<String, Object>() {{
                                        put("type", "text");
                                        put("text", title.getTagName());
                                    }});
                                }});
                            }});
                        }});
                    });
                }};
                wechatService.createRecords(wechatDocConfig.getTemplateDocId(), wechatDocConfig.getPrivatePersonalTagsSheetId(), recores);*/
                // 获取当前操作
                switch (type) {
                    case 0:
                        // 删除记录
                        wechatService.removeRecords(wechatDocConfig.getTemplateDocId(), wechatDocConfig.getPrivatePersonalTagsSheetId(), storePersonalTags.getRecordId());
                        break;
                    case 1:
                        // 新增记录
                        List<Map<String, Object>> recordList = new ArrayList<Map<String, Object>>() {{
                            add(new HashMap<String, Object>() {{
                                put("values", new HashMap<String, Object>() {{
                                    put("个性化标签", new ArrayList<Map<String, Object>>() {{
                                        add(new HashMap<String, Object>() {{
                                            put("type", "text");
                                            put("text", storePersonalTags.getTagName());
                                        }});
                                    }});
                                }});
                            }});
                        }};
                        List<Record> records = wechatService.createRecords(wechatDocConfig.getTemplateDocId(), wechatDocConfig.getPrivatePersonalTagsSheetId(), recordList);
                        // 更新数据库中对应标签的recordId
                        StorePersonalTags personalTags = new StorePersonalTags();
                        personalTags.setId(storePersonalTags.getId());
                        personalTags.setRecordId(records.get(0).getRecordId());
                        storePersonalTagsService.updateById(personalTags);
                        break;
                    case 2:
                        if (oConvertUtils.isEmpty(storePersonalTags.getRecordId())) {
                            break;
                        }
                        // 编辑记录
                        List<Map<String, Object>> updateRecords = Collections.singletonList(new HashMap<String, Object>() {{
                            put("record_id", storePersonalTags.getRecordId());
                            put("values", Collections.singletonMap("个性化标签", Collections.singletonList(new HashMap<String, Object>() {{
                                put("text", storePersonalTags.getTagName());
                                put("type", "text");
                            }})));
                        }});
                        wechatService.updateRecords(wechatDocConfig.getTemplateDocId(), wechatDocConfig.getPrivatePersonalTagsSheetId(), updateRecords);
                        break;
                }
            });
        } catch (Exception e) {
            log.error("同步企微标签失败", e);
        }
    }

    /**
     * 编辑
     *
     * @param storePersonalTags
     * @return
     */
    @AutoLog(value = "个性化标签-" + SystemConstants.EDIT)
@Operation(summary = "个性化标签-" + SystemConstants.EDIT, description = "个性化标签-" + SystemConstants.EDIT)
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody StorePersonalTags storePersonalTags) {
        String tagName = storePersonalTags.getTagName();
        Integer count = Math.toIntExact(storePersonalTagsService.lambdaQuery().eq(StorePersonalTags::getTagName, tagName).eq(StorePersonalTags::getDelFlag, 0).ne(StorePersonalTags::getId, storePersonalTags.getId()).count());
        if (count > 0) {
            return Result.error("标签已存在");
        }
        storePersonalTags.setRecordId(null);
        storePersonalTagsService.updateById(storePersonalTags);
        asyncFeishuUpdateLabels(0);
        return Result.ok(SystemConstants.EDIT_SUCCESS);
    }

    /**
     * 通过tagName删除
     *
     * @return
     */
    @AutoLog(value = "个性化标签-" + SystemConstants.DELETE_BY_ID)
@Operation(summary = "个性化标签-" + SystemConstants.DELETE_BY_ID, description = "个性化标签-" + SystemConstants.DELETE_BY_ID)
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "tagName", required = true) String tagName) {
        StorePersonalTags personalTags = storePersonalTagsService.lambdaQuery().eq(StorePersonalTags::getTagName, tagName).eq(StorePersonalTags::getDelFlag, 0).one();
        storePersonalTagsService.lambdaUpdate().eq(StorePersonalTags::getTagName, tagName).remove();
        // asyncUpdateLabels(0, personalTags);
        asyncFeishuUpdateLabels(1);
        return Result.ok(SystemConstants.DELETE_SUCCESS);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "个性化标签-" + SystemConstants.DELETE_BATCH)
@Operation(summary = "个性化标签-" + SystemConstants.DELETE_BATCH, description = "个性化标签-" + SystemConstants.DELETE_BATCH)
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        List<StorePersonalTags> personalTags = storePersonalTagsService.lambdaQuery().in(StorePersonalTags::getId, Arrays.asList(ids.split(","))).eq(StorePersonalTags::getDelFlag, 0).list();
        this.storePersonalTagsService.removeByIds(Arrays.asList(ids.split(",")));
        // 异步更新企微文档
        // personalTags.forEach(tag -> asyncUpdateLabels(0, tag));
        personalTags.forEach(tag -> asyncFeishuUpdateLabels(1));
        return Result.ok(SystemConstants.DELETE_BATCH_SUCCESS);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "个性化标签-" + SystemConstants.QUERY_BY_ID)
@Operation(summary = "个性化标签-" + SystemConstants.QUERY_BY_ID, description = "个性化标签-" + SystemConstants.QUERY_BY_ID)
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        StorePersonalTags storePersonalTags = storePersonalTagsService.getById(id);
        return Result.ok(storePersonalTags);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param storePersonalTags
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StorePersonalTags storePersonalTags) {
        return super.exportXls(request, storePersonalTags, StorePersonalTags.class, "个性化标签");
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
        return super.importExcel(request, response, StorePersonalTags.class);
    }

    @AutoLog(value = "个性化标签-同步个性化标签")
@Operation(summary = "个性化标签-同步个性化标签", description = "个性化标签-同步个性化标签")
    @GetMapping(value = "/synchronizeData")
    public Result<?> synchronizeData() {
        List<String> tagNames = storePersonalTagsService
                .lambdaQuery()
                .eq(StorePersonalTags::getDelFlag, 0)
                .ne(StorePersonalTags::getTagName, "无")
                .orderByAsc(StorePersonalTags::getSortCode)
                .list()
                .stream()
                .map(StorePersonalTags::getTagName)
                .filter(oConvertUtils::isNotEmpty)
                .collect(Collectors.toList());
        feishuService.insertOrUpdatePersonalTagsSheetBatch(1, SheetConstants.PERSONAL_TAGS, tagNames);
        return Result.ok("同步中，预计一分钟完成");
    }
}
