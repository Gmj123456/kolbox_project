package org.jeecg.modules.youtube.controller;

import java.util.Arrays;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.SystemConstants;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.modules.youtube.service.IYoutubeTagsService;
import org.jeecg.modules.youtube.entity.YoutubeTags;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * @Description: YT标签
 * @Author: nqr
 * @Date: 2023-11-22
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "YT标签")
@RestController
@RequestMapping("/youtubeTags")
public class YoutubeTagsController extends JeecgController<YoutubeTags, IYoutubeTagsService> {
    @Autowired
    private IYoutubeTagsService youtubeTagsService;

    /**
     * 分页列表查询
     *
     * @param youtubeTags
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "YT标签-" + SystemConstants.PAGE_LIST_QUERY)
    @Operation(summary = "YT标签-" + SystemConstants.PAGE_LIST_QUERY, description = "YT标签-" + SystemConstants.PAGE_LIST_QUERY)
    @GetMapping(value = "/list")
    public Result<?> queryPageList(YoutubeTags youtubeTags,
                                   @RequestParam(name = SystemConstants.PAGE_NO, defaultValue = SystemConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                   @RequestParam(name = SystemConstants.PAGE_SIZE, defaultValue = SystemConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<YoutubeTags> queryWrapper = QueryGenerator.initQueryWrapper(youtubeTags, req.getParameterMap());
        Page<YoutubeTags> page = new Page<>(pageNo, pageSize);
        IPage<YoutubeTags> pageList = youtubeTagsService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param youtubeTags
     * @return
     */
    @AutoLog(value = "YT标签-" + SystemConstants.ADD)
    @Operation(summary = "YT标签-" + SystemConstants.ADD, description = "YT标签-" + SystemConstants.ADD)
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody YoutubeTags youtubeTags) {
        youtubeTagsService.save(youtubeTags);
        return Result.ok(SystemConstants.ADD_SUCCESS);
    }

    /**
     * 编辑
     *
     * @param youtubeTags
     * @return
     */
    @AutoLog(value = "YT标签-" + SystemConstants.EDIT)
    @Operation(summary = "YT标签-" + SystemConstants.EDIT, description = "YT标签-" + SystemConstants.EDIT)
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody YoutubeTags youtubeTags) {
        youtubeTagsService.updateById(youtubeTags);
        return Result.ok(SystemConstants.EDIT_SUCCESS);
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "YT标签-" + SystemConstants.DELETE_BY_ID)
    @Operation(summary = "YT标签-" + SystemConstants.DELETE_BY_ID, description = "YT标签-" + SystemConstants.DELETE_BY_ID)
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        youtubeTagsService.removeById(id);
        return Result.ok(SystemConstants.DELETE_SUCCESS);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "YT标签-" + SystemConstants.DELETE_BATCH)
    @Operation(summary = "YT标签-" + SystemConstants.DELETE_BATCH, description = "YT标签-" + SystemConstants.DELETE_BATCH)
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.youtubeTagsService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok(SystemConstants.DELETE_BATCH_SUCCESS);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "YT标签-" + SystemConstants.QUERY_BY_ID)
    @Operation(summary = "YT标签-" + SystemConstants.QUERY_BY_ID, description = "YT标签-" + SystemConstants.QUERY_BY_ID)
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        YoutubeTags youtubeTags = youtubeTagsService.getById(id);
        return Result.ok(youtubeTags);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param youtubeTags
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, YoutubeTags youtubeTags) {
        return super.exportXls(request, youtubeTags, YoutubeTags.class, "YT标签");
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
        return super.importExcel(request, response, YoutubeTags.class);
    }

}
