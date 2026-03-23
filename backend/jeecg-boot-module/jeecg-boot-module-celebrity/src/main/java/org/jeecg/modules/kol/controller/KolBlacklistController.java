package org.jeecg.modules.kol.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.SystemConstants;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.kol.entity.KolBlacklist;
import org.jeecg.modules.kol.model.KolBlacklistModel;
import org.jeecg.modules.kol.service.IKolBlacklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Description: 网红屏蔽名单表
 * @Author: dongruyang
 * @Date: 2025-05-10
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "网红屏蔽名单表")
@RestController
@RequestMapping("/kol/kolBlacklist")
public class KolBlacklistController extends JeecgController<KolBlacklist, IKolBlacklistService> {
    @Autowired
    private IKolBlacklistService kolBlacklistService;

    /**
     * 分页列表查询
     *
     * @param kolBlacklistModel
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "网红屏蔽名单表-" + SystemConstants.PAGE_LIST_QUERY)
    @Operation(summary = "网红屏蔽名单表-" + SystemConstants.PAGE_LIST_QUERY, description = "网红屏蔽名单表-" + SystemConstants.PAGE_LIST_QUERY)
    @GetMapping(value = "/list")
    public Result<?> queryPageList(KolBlacklistModel kolBlacklistModel,
                                   @RequestParam(name = SystemConstants.PAGE_NO, defaultValue = SystemConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                   @RequestParam(name = SystemConstants.PAGE_SIZE, defaultValue = SystemConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
                                   HttpServletRequest req) {
        Page<KolBlacklistModel> page = new Page<>(pageNo, pageSize);
        IPage<KolBlacklistModel> pageList = kolBlacklistService.pageBlackList(page, kolBlacklistModel);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param kolBlacklistList
     * @return
     */
    @AutoLog(value = "网红屏蔽名单表-" + SystemConstants.ADD)
    @Operation(summary = "网红屏蔽名单表-" + SystemConstants.ADD, description = "网红屏蔽名单表-" + SystemConstants.ADD)
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody List<KolBlacklist> kolBlacklistList) {
        if (kolBlacklistList.isEmpty()) {
            return Result.error("请选择要屏蔽的网红");
        }
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String counselorId = sysUser.getId();
        String counselorName = sysUser.getUsername();
        String shortName = sysUser.getShortName();
        List<KolBlacklist> kolBlacklistListSave = new ArrayList<>();
        for (KolBlacklist kolBlacklist : kolBlacklistList) {
            // 网红唯一ID
            String celebrityId = kolBlacklist.getCelebrityId();
            if (oConvertUtils.isEmpty(celebrityId)) {
                return Result.error("网红唯一ID不能为空");
            }
            Integer platformType = kolBlacklist.getPlatformType();
            if (platformType == null) {
                return Result.error("平台类型不能为空");
            }
            // 查询是否重复屏蔽
            KolBlacklist blacklist = kolBlacklistService.lambdaQuery().eq(KolBlacklist::getCelebrityId, celebrityId)
                    .eq(KolBlacklist::getCounselorId, counselorId)
                    .eq(KolBlacklist::getPlatformType, platformType)
                    .eq(KolBlacklist::getIsDelete, 0).last("limit 1").one();
            if (blacklist != null) {
                return Result.error("该网红已被屏蔽");
            }
            kolBlacklist.setCounselorShortName(shortName);
            kolBlacklist.setCounselorId(counselorId);
            kolBlacklist.setCounselorName(counselorName);
            kolBlacklist.setIsDelete(0);
            kolBlacklist.setBlacklistTime(new Date());
            kolBlacklist.setCreateBy(counselorName);
            kolBlacklist.setCreateTime(new Date());
            kolBlacklistListSave.add(kolBlacklist);
        }
        kolBlacklistService.saveBatch(kolBlacklistListSave);
        return Result.ok(SystemConstants.ADD_SUCCESS);
    }

    /**
     * 编辑
     *
     * @param kolBlacklist
     * @return
     */
    @AutoLog(value = "网红屏蔽名单表-" + SystemConstants.EDIT)
    @Operation(summary = "网红屏蔽名单表-" + SystemConstants.EDIT, description = "网红屏蔽名单表-" + SystemConstants.EDIT)
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody KolBlacklist kolBlacklist) {
        kolBlacklistService.updateById(kolBlacklist);
        return Result.ok(SystemConstants.EDIT_SUCCESS);
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "网红屏蔽名单表-" + SystemConstants.DELETE_BY_ID)
    @Operation(summary = "网红屏蔽名单表-" + SystemConstants.DELETE_BY_ID, description = "网红屏蔽名单表-" + SystemConstants.DELETE_BY_ID)
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        kolBlacklistService.removeById(id);
        return Result.ok("取消成功");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "网红屏蔽名单表-" + SystemConstants.DELETE_BATCH)
    @Operation(summary = "网红屏蔽名单表-" + SystemConstants.DELETE_BATCH, description = "网红屏蔽名单表-" + SystemConstants.DELETE_BATCH)
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.kolBlacklistService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok(SystemConstants.DELETE_BATCH_SUCCESS);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "网红屏蔽名单表-" + SystemConstants.QUERY_BY_ID)
    @Operation(summary = "网红屏蔽名单表-" + SystemConstants.QUERY_BY_ID, description = "网红屏蔽名单表-" + SystemConstants.QUERY_BY_ID)
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        KolBlacklist kolBlacklist = kolBlacklistService.getById(id);
        return Result.ok(kolBlacklist);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param kolBlacklist
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, KolBlacklist kolBlacklist) {
        return super.exportXls(request, kolBlacklist, KolBlacklist.class, "网红屏蔽名单表");
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
        return super.importExcel(request, response, KolBlacklist.class);
    }

}
