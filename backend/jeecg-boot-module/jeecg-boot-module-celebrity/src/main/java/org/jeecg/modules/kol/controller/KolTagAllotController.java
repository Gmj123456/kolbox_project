package org.jeecg.modules.kol.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.PageUtil;
import org.jeecg.modules.instagram.service.IIgCelebrityTagsCounselorService;
import org.jeecg.modules.kol.entity.KolBrand;
import org.jeecg.modules.kol.model.KolSearchModel;
import org.jeecg.modules.kol.model.KolTagAllotModel;
import org.jeecg.modules.kol.service.IKolTagAllotService;
import org.jeecg.modules.kol.service.impl.AsyncRefreshService;
import org.jeecg.modules.tiktok.service.ITkCelebrityTagsCounselorService;
import org.jeecg.modules.youtube.service.IYtCelebrityTagsCounselorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.jeecg.common.constant.SystemConstants.*;

/**
 * @Description: 网红公用标签分配表
 * @Author: xyc
 * @Date: 2024-12-30 15:43:23
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "网红公用标签分配表")
@RestController
@RequestMapping("/kol/tagAllot")
public class KolTagAllotController {

    @Autowired
    private IIgCelebrityTagsCounselorService igTagsCounselorService;

    @Autowired
    private ITkCelebrityTagsCounselorService tkTagsCounselorService;

    @Autowired
    private IYtCelebrityTagsCounselorService ytTagsCounselorService;
    @Autowired
    private AsyncRefreshService asyncRefreshService;

    /**
     * 分页列表查询
     *
     * @param searchModel
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "标签网红分配明细-分页列表查询")
    @Operation(summary = "标签网红分配明细-分页列表查询", description = "标签网红分配明细-分页列表查询")
    @PostMapping(value = LIST_URL)
    public Result<?> queryPageList(@RequestBody KolSearchModel searchModel,
            @RequestParam(name = PAGE_NO, defaultValue = PAGE_NO_DEFAULT) Integer pageNo,
            @RequestParam(name = PAGE_SIZE, defaultValue = PAGE_SIZE_DEFAULT) Integer pageSize,
            HttpServletRequest req) {
        Page<KolTagAllotModel> page = PageUtil.getOrderItems(pageNo, pageSize, req);
        // LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        // searchModel.setCounselorId(sysUser.getId());
        IKolTagAllotService kolTagAllotService = getKolConnectionService(searchModel.getPlatformType());
        page.setSearchCount(false);
        int count = 5000;
        IPage pageList = null;
        // count = kolTagAllotService.getKolTagAllottedListCount(searchModel);
        // IPage pageList = kolTagAllotService.getKolTagAllottedList(page, searchModel);
        CompletableFuture<Integer> countFuture = CompletableFuture.completedFuture(5000);
        try {
            if (!areOtherFieldsEmpty(searchModel)) {
                // 异步执行第一个查询
                countFuture = CompletableFuture
                        .supplyAsync(() -> kolTagAllotService.getKolTagAllottedListCount(searchModel));
            }
            // 异步执行第二个查询
            CompletableFuture<IPage<KolTagAllotModel>> pageListFuture = CompletableFuture
                    .supplyAsync(() -> kolTagAllotService.getKolTagAllottedList(page, searchModel));

            // 等待两个查询都完成
            CompletableFuture.allOf(countFuture, pageListFuture).join();

            // 获取结果
            count = countFuture.get();
            pageList = pageListFuture.get();
        } catch (Exception e) {
            kolTagAllotService.getKolTagAllottedListCount(searchModel);
            pageList = kolTagAllotService.getKolTagAllottedList(page, searchModel);
        }

        /*
         * if (pageList.getRecords().isEmpty()) {
         * count = 0;
         * } else if (pageList.getRecords().size() < pageSize) {
         * count = pageList.getRecords().size();
         * }
         */
        pageList.setTotal(count);
        return Result.ok(pageList);
    }

    /**
     * 根据平台类型获取对应的服务
     *
     * @param platformType 0=IG 1=YT 2=TK
     * @return
     */
    private IKolTagAllotService getKolConnectionService(Integer platformType) {
        switch (platformType) {
            case CommonConstant.IG:
                return igTagsCounselorService;
            case CommonConstant.YT:
                return ytTagsCounselorService;
            case CommonConstant.TK:
                return tkTagsCounselorService;
            default:
                throw new IllegalArgumentException("未知的平台类型参数: " + platformType);
        }
    }

    /**
     * 判断是否存在查询条件
     */
    public boolean areOtherFieldsEmpty(KolSearchModel model) {
        if (model == null)
            return true;

        Set<String> exemptFields = new HashSet<>(Arrays.asList("platformType", "includeBlacklist", "serialVersionUID"));

        Class<?> clazz = model.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (exemptFields.contains(field.getName())) {
                continue; // 跳过允许有值的字段
            }

            field.setAccessible(true); // 允许访问 private 字段

            try {
                Object value = field.get(model);

                if (value == null) {
                    continue; // 字段为 null，视为“空”，继续
                }

                // 如果是字符串类型，检查是否为空字符串
                if (value instanceof String) {
                    if (!((String) value).trim().isEmpty()) {
                        return false; // 非空字符串 → 有值 → 不满足“其余为空”
                    }
                } // 集合类：List, Set 等
                else if (value instanceof Collection<?>) {
                    if (!((Collection<?>) value).isEmpty()) {
                        return false;
                    }
                } else {
                    // 对于非 String 类型（如 Integer），只要不为 null 就算“有值”
                    return false;
                }

            } catch (IllegalAccessException e) {
                // 忽略访问异常（理论上不会发生，因 setAccessible(true)）
                throw new RuntimeException(e);
            }
        }

        return true; // 所有非豁免字段都为空
    }

    /**
     * @description: 手动刷新未分配标签数量
     * @author: nqr
     * @date: 2025/8/20 11:14
     **/
    @GetMapping(value = "/refreshUnallocatedQty")
    public Result<?> refreshUnallocatedQty(
            @RequestParam(name = "platformType", required = false) Integer platformType) {
        // 立即触发异步任务，不等待
        asyncRefreshService.asyncRefreshUnallocatedQty(platformType);
        // 立即返回响应
        return Result.ok("正在处理中，预计10分钟后可查看结果。");
    }

    /**
     * 根据平台类型获取当前用户分配的品牌列表
     *
     * @param platformType 平台类型 0=IG 1=YT 2=TK
     * @return
     */
    @AutoLog(value = "品牌列表-根据平台查询当前用户分配的品牌")
    @Operation(summary = "品牌列表-根据平台查询当前用户分配的品牌", description = "根据平台类型查询分配给当前用户的产品对应的品牌列表")
    @GetMapping(value = "/getBrandList")
    public Result<?> getBrandList(@RequestParam(name = "platformType") Integer platformType) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String counselorId = sysUser.getId();
        IKolTagAllotService kolTagAllotService = getKolConnectionService(platformType);
        List<KolBrand> brandList = kolTagAllotService.getBrandListByCounselorId(counselorId);
        return Result.ok(brandList);
    }
}
