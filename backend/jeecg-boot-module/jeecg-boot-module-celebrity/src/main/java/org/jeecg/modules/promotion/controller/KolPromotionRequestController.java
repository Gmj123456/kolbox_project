package org.jeecg.modules.promotion.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.aspect.annotation.LimitSubmit;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.promotion.entity.KolPromotionRequest;
import org.jeecg.modules.promotion.service.IKolPromotionRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Slf4j
@Tag(name = "推广需求管理")
@RestController
@RequestMapping("/promotion/request")
public class KolPromotionRequestController extends JeecgController<KolPromotionRequest, IKolPromotionRequestService> {

    @Autowired
    private IKolPromotionRequestService promotionRequestService;

    @AutoLog(value = "分页列表查询-推广需求")
    @Operation(summary = "分页列表查询-推广需求")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(
            KolPromotionRequest promotionRequest,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize,
            @RequestParam(name = "startDate", required = false) String startDate,
            @RequestParam(name = "endDate", required = false) String endDate,
            HttpServletRequest req) {

        Page<KolPromotionRequest> page = new Page<>(pageNo, pageSize);
        IPage<KolPromotionRequest> pageList = promotionRequestService.queryPageList(
                page,
                promotionRequest.getName(),
                promotionRequest.getCompanyName(),
                promotionRequest.getPhone(),
                promotionRequest.getEmail(),
                startDate,
                endDate
        );
        return Result.ok(pageList);
    }

    @AutoLog(value = "新增推广需求")
    @Operation(summary = "新增推广需求")
    @PostMapping(value = "/add")
    @LimitSubmit(key = "promotion:add", limit = 60, needAllWait = false)
    public Result<Object> add(@RequestBody KolPromotionRequest promotionRequest) {
        try {
            String username = "anonymous";
            try {
                LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                if (sysUser != null) {
                    username = sysUser.getUsername();
                }
            } catch (Exception e) {
            }
            promotionRequest.setCreateBy(username);
            promotionRequestService.addPromotionRequest(promotionRequest);
            return Result.ok("提交成功");
        } catch (Exception e) {
            log.error("新增推广需求失败", e);
            return Result.error("新增推广需求失败");
        }
    }

    @AutoLog(value = "修改推广需求")
    @Operation(summary = "修改推广需求")
    @PutMapping(value = "/edit")
    public Result<Object> edit(@RequestBody KolPromotionRequest promotionRequest) {
        try {
            String username = "anonymous";
            try {
                LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                if (sysUser != null) {
                    username = sysUser.getUsername();
                }
            } catch (Exception e) {
            }
            promotionRequest.setUpdateBy(username);
            promotionRequestService.updatePromotionRequest(promotionRequest);
            return Result.ok("更新成功");
        } catch (Exception e) {
            log.error("修改推广需求失败", e);
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    @AutoLog(value = "删除推广需求")
    @Operation(summary = "删除推广需求")
    @DeleteMapping(value = "/delete")
    public Result<Object> delete(@RequestParam(name = "id") String id) {
        try {
            promotionRequestService.deletePromotionRequest(id);
            return Result.ok("删除成功");
        } catch (Exception e) {
            log.error("删除推广需求失败", e);
            return Result.error("删除失败: " + e.getMessage());
        }
    }

    @AutoLog(value = "查询推广需求详情")
    @Operation(summary = "查询推广需求详情")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id") String id) {
        KolPromotionRequest promotionRequest = promotionRequestService.getById(id);
        if (promotionRequest == null) {
            return Result.error("未找到对应记录");
        }
        return Result.ok(promotionRequest);
    }
}
