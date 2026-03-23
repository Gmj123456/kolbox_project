package org.jeecg.modules.kol.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.enums.PlatformType;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.instagram.service.IIgCelebrityTagsService;
import org.jeecg.modules.kol.model.KolSearchModel;
import org.jeecg.modules.kol.service.IKolAllotService;
import org.jeecg.modules.kol.service.IKolTagCelebrityService;
import org.jeecg.modules.tiktok.model.TiktokTagsNumModel;
import org.jeecg.modules.tiktok.service.ITiktokCelebrityTagsService;
import org.jeecg.modules.youtube.service.IYtCelebrityTagsService;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;


/**
 * @Description: 标签网红分配控制器
 * @Author: nqr
 * @Date: 2025-1-4 09:39:48
 * @Version: V1.0
 */
@Tag(name = "标签网红分配控制器")
@RestController
@RequestMapping("/kol/kolTagCelebrity")
@Slf4j
public class KolTagCelebrityController {
    @Autowired
    private IYtCelebrityTagsService ytTagsService;
    @Autowired
    private IIgCelebrityTagsService igTagsService;
    @Autowired
    private ITiktokCelebrityTagsService tkTagsService;


    /**
     * 方法描述: 根据平台区分。导出标签、数量
     *
     * @author nqr
     * @date 2025/01/03 18:17
     **/
    @RequestMapping(value = "/exportTagsExcel")
    public ModelAndView exportTagsExcel(@RequestBody KolSearchModel kolSearchModel) {
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        try{
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            String realName = sysUser.getRealname();
            Integer platformType = kolSearchModel.getPlatformType();
            if (platformType == null) throw new JeecgBootException("未获取到平台类型");
            // 获取网红分配过期天数 默认30天
            kolSearchModel.setAllotDays(tkTagsService.getAllotExpireDays());
            // 根据平台类型获取对应的服务
            List<TiktokTagsNumModel> tagsNumModels = getTagCelebrityService(platformType).exportTagsExcel(kolSearchModel);
            String title = "未分配完标签";
            // 导出Excel
            mv.addObject(NormalExcelConstants.FILE_NAME, title);
            mv.addObject(NormalExcelConstants.CLASS, TiktokTagsNumModel.class);
            mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("未分配完标签报表", "导出人:" + realName, title));
            mv.addObject(NormalExcelConstants.DATA_LIST, tagsNumModels);
        }catch (Exception e) {
            log.error("导出标签失败", e);
            return null;
        }
        return mv;
    }
    /**
     * 修改网红顾问
     *
     * @return
     */
    @AutoLog(value = "修改网红顾问")
    @Operation(summary = "修改网红顾问", description = "修改网红顾问")
    @GetMapping(value = "/updateCounselor")
    public Result<?> updateCounselor(KolSearchModel kolSearchModel) {
        IKolTagCelebrityService kolTagCelebrityService = getTagCelebrityService(kolSearchModel.getPlatformType());
        kolTagCelebrityService.updateCounselor(kolSearchModel);
        return Result.ok("修改成功！");
    }

    /**
     * 根据平台类型获取对应的服务
     *
     * @param platformType 0=IG 1=YT 2=TK
     * @return
     */
    private IKolTagCelebrityService getTagCelebrityService(Integer platformType) {
        switch (platformType) {
            case CommonConstant.IG:
                return igTagsService;
            case CommonConstant.YT:
                return ytTagsService;
            case CommonConstant.TK:
                return tkTagsService;
            default:
                throw new IllegalArgumentException("未知的平台类型参数: " + platformType);
        }
    }
}
