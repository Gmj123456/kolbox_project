package org.jeecg.modules.instagram.storetags.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.storetags.entity.StoreTags;
import org.jeecg.modules.instagram.storetags.service.IStoreTagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Description: 标签表
 * @Author: jeecg-boot
 * @Date: 2021-02-07
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "标签表")
@RestController
@RequestMapping("/storetags/storeTags")
public class StoreTagsController extends JeecgController<StoreTags, IStoreTagsService> {
    @Autowired
    private IStoreTagsService storeTagsService;

    /**
     * 分页列表查询
     *
     * @param storeTags
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "标签表-分页列表查询")
@Operation(summary = "标签表-分页列表查询", description = "标签表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StoreTags storeTags,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<StoreTags> queryWrapper = QueryGenerator.initQueryWrapper(storeTags, req.getParameterMap());
        Page<StoreTags> page = new Page<StoreTags>(pageNo, pageSize);
        IPage<StoreTags> pageList = storeTagsService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param storeTags
     * @return
     */
    @AutoLog(value = "标签表-添加")
@Operation(summary = "标签表-添加", description = "标签表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody StoreTags storeTags) {
        String likeTagName = storeTags.getLikeTagName();
        if(oConvertUtils.isEmpty(likeTagName)){
            return Result.error("标签不能为空");
        }
        LambdaQueryWrapper<StoreTags> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StoreTags::getLikeTagName, likeTagName);
        List<StoreTags> list = storeTagsService.list(queryWrapper);
        if (!list.isEmpty()) {
            return Result.error("当前标签已存在");
        }
        storeTags.setCreateBy(getUserNameByToken());
        storeTags.setCreateTime(new Date());
        storeTagsService.save(storeTags);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param storeTags
     * @return
     */
    @AutoLog(value = "标签表-编辑")
@Operation(summary = "标签表-编辑", description = "标签表-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody StoreTags storeTags) {
        String likeTagName = storeTags.getLikeTagName();
        List<StoreTags> tagsList = storeTagsService.lambdaQuery().eq(StoreTags::getLikeTagName, likeTagName).ne(StoreTags::getId, storeTags.getId()).list();
        if (!tagsList.isEmpty()) {
            return Result.error("当前标签已存在");
        }
        storeTags.setUpdateBy(getUserNameByToken());
        storeTags.setUpdateTime(new Date());
        storeTagsService.updateById(storeTags);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "标签表-通过id删除")
@Operation(summary = "标签表-通过id删除", description = "标签表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        storeTagsService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "标签表-批量删除")
@Operation(summary = "标签表-批量删除", description = "标签表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.storeTagsService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "标签表-通过id查询")
@Operation(summary = "标签表-通过id查询", description = "标签表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        StoreTags storeTags = storeTagsService.getById(id);
        return Result.ok(storeTags);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param storeTags
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StoreTags storeTags) {
        return super.exportXls(request, storeTags, StoreTags.class, "标签表");
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
        return super.importExcel(request, response, StoreTags.class);
    }


    /**
     * 查询所有标签
     *
     * @return
     */
    @AutoLog(value = "标签表-查询所有标签")
@Operation(summary = "标签表-查询所有标签", description = "标签表-查询所有标签")
    @GetMapping(value = "/queryAll")
    public Result<?> queryAll() {
        return Result.ok(storeTagsService.list());
    }
}
