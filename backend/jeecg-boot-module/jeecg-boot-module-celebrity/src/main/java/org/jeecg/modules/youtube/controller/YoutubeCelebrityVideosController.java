package org.jeecg.modules.youtube.controller;

import java.util.Arrays;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.SystemConstants;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.modules.youtube.entity.YoutubeCelebrityVideos;
import org.jeecg.modules.youtube.service.IYoutubeCelebrityVideosService;
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
 * @Description: YT网红视频
 * @Author: nqr
 * @Date: 2023-11-22
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "YT网红视频")
@RestController
@RequestMapping("/youtubeCelebrityVideos")
public class YoutubeCelebrityVideosController extends JeecgController<YoutubeCelebrityVideos, IYoutubeCelebrityVideosService> {
    @Autowired
    private IYoutubeCelebrityVideosService youtubeCelebrityVideosService;

    /**
     * 分页列表查询
     *
     * @param youtubeCelebrityVideos
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "YT网红视频-" + SystemConstants.PAGE_LIST_QUERY)
    @Operation(summary = "YT网红视频-" + SystemConstants.PAGE_LIST_QUERY, description = "YT网红视频-" + SystemConstants.PAGE_LIST_QUERY)
    @GetMapping(value = "/list")
    public Result<?> queryPageList(YoutubeCelebrityVideos youtubeCelebrityVideos,
                                   @RequestParam(name = SystemConstants.PAGE_NO, defaultValue = SystemConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                   @RequestParam(name = SystemConstants.PAGE_SIZE, defaultValue = SystemConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<YoutubeCelebrityVideos> queryWrapper = QueryGenerator.initQueryWrapper(youtubeCelebrityVideos, req.getParameterMap());
        Page<YoutubeCelebrityVideos> page = new Page<>(pageNo, pageSize);
        IPage<YoutubeCelebrityVideos> pageList = youtubeCelebrityVideosService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param youtubeCelebrityVideos
     * @return
     */
    @AutoLog(value = "YT网红视频-" + SystemConstants.ADD)
    @Operation(summary = "YT网红视频-" + SystemConstants.ADD, description = "YT网红视频-" + SystemConstants.ADD)
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody YoutubeCelebrityVideos youtubeCelebrityVideos) {
        youtubeCelebrityVideosService.save(youtubeCelebrityVideos);
        return Result.ok(SystemConstants.ADD_SUCCESS);
    }

    /**
     * 编辑
     *
     * @param youtubeCelebrityVideos
     * @return
     */
    @AutoLog(value = "YT网红视频-" + SystemConstants.EDIT)
    @Operation(summary = "YT网红视频-" + SystemConstants.EDIT, description = "YT网红视频-" + SystemConstants.EDIT)
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody YoutubeCelebrityVideos youtubeCelebrityVideos) {
        youtubeCelebrityVideosService.updateById(youtubeCelebrityVideos);
        return Result.ok(SystemConstants.EDIT_SUCCESS);
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "YT网红视频-" + SystemConstants.DELETE_BY_ID)
    @Operation(summary = "YT网红视频-" + SystemConstants.DELETE_BY_ID, description = "YT网红视频-" + SystemConstants.DELETE_BY_ID)
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        youtubeCelebrityVideosService.removeById(id);
        return Result.ok(SystemConstants.DELETE_SUCCESS);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "YT网红视频-" + SystemConstants.DELETE_BATCH)
    @Operation(summary = "YT网红视频-" + SystemConstants.DELETE_BATCH, description = "YT网红视频-" + SystemConstants.DELETE_BATCH)
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.youtubeCelebrityVideosService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok(SystemConstants.DELETE_BATCH_SUCCESS);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "YT网红视频-" + SystemConstants.QUERY_BY_ID)
    @Operation(summary = "YT网红视频-" + SystemConstants.QUERY_BY_ID, description = "YT网红视频-" + SystemConstants.QUERY_BY_ID)
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        YoutubeCelebrityVideos youtubeCelebrityVideos = youtubeCelebrityVideosService.getById(id);
        return Result.ok(youtubeCelebrityVideos);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param youtubeCelebrityVideos
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, YoutubeCelebrityVideos youtubeCelebrityVideos) {
        return super.exportXls(request, youtubeCelebrityVideos, YoutubeCelebrityVideos.class, "YT网红视频");
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
        return super.importExcel(request, response, YoutubeCelebrityVideos.class);
    }

}
