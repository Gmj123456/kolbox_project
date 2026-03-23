package org.jeecg.modules.tiktok.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.SystemConstants;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.tiktok.entity.TiktokCelebrityRule;
import org.jeecg.modules.tiktok.service.ITiktokCelebrityRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 网红分配规则表
 * @Author: nqr
 * @Date: 2023-10-11
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "网红分配规则表")
@RestController
@RequestMapping("/tiktokcelebrityrule")
public class TiktokCelebrityRuleController extends JeecgController<TiktokCelebrityRule, ITiktokCelebrityRuleService> {
    @Autowired
    private ITiktokCelebrityRuleService tiktokCelebrityRuleService;

    /**
     * 分页列表查询
     *
     * @param tiktokCelebrityRule
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "网红分配规则表-" + SystemConstants.PAGE_LIST_QUERY)
    @Operation(summary = "网红分配规则表-" + SystemConstants.PAGE_LIST_QUERY, description = "网红分配规则表-" + SystemConstants.PAGE_LIST_QUERY)
    @GetMapping(value = "/list")
    public Result<?> queryPageList(TiktokCelebrityRule tiktokCelebrityRule,
                                   @RequestParam(name = SystemConstants.PAGE_NO, defaultValue = SystemConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                   @RequestParam(name = SystemConstants.PAGE_SIZE, defaultValue = SystemConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
                                   HttpServletRequest req) {
        LambdaQueryWrapper<TiktokCelebrityRule> queryWrapper = new LambdaQueryWrapper<>();
        if (oConvertUtils.isNotEmpty(tiktokCelebrityRule.getRuleLevel())) {
            queryWrapper.eq(TiktokCelebrityRule::getRuleLevel, tiktokCelebrityRule.getRuleLevel());
        }
        if (oConvertUtils.isNotEmpty(tiktokCelebrityRule.getPlatformType())) {
            queryWrapper.eq(TiktokCelebrityRule::getPlatformType, tiktokCelebrityRule.getPlatformType());
        }
        queryWrapper.orderByAsc(TiktokCelebrityRule::getPlatformType).orderByAsc(TiktokCelebrityRule::getRuleLevel);
        Page<TiktokCelebrityRule> page = new Page<>(pageNo, pageSize);
        IPage<TiktokCelebrityRule> pageList = tiktokCelebrityRuleService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param tiktokCelebrityRule
     * @return
     */
    @AutoLog(value = "网红分配规则表-" + SystemConstants.ADD)
    @Operation(summary = "网红分配规则表-" + SystemConstants.ADD, description = "网红分配规则表-" + SystemConstants.ADD)
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody TiktokCelebrityRule tiktokCelebrityRule) {
        LambdaQueryWrapper<TiktokCelebrityRule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TiktokCelebrityRule::getPlatformType, tiktokCelebrityRule.getPlatformType());
        List<TiktokCelebrityRule> list = tiktokCelebrityRuleService.list(queryWrapper);

        // 标志变量，用于记录是否存在冲突
        boolean hasConflict = false;
        for (TiktokCelebrityRule existingRule : list) {
            if (existingRule != null && tiktokCelebrityRule != null &&
                    existingRule.getRuleLevel() != null && tiktokCelebrityRule.getRuleLevel() != null &&
                    existingRule.getRuleLevel().equals(tiktokCelebrityRule.getRuleLevel())) {

                hasConflict = true;
                break;
            }

            // 检查是否存在范围交集
            if (isRangeOverlap(existingRule, tiktokCelebrityRule)) {
                hasConflict = true;
                break; // 找到冲突，提前退出循环
            }
        }
        if (hasConflict) {
            return Result.error("规则档位已存在或规则范围与现有规则存在交集");
        } else {
            tiktokCelebrityRuleService.save(tiktokCelebrityRule);
            return Result.ok(SystemConstants.ADD_SUCCESS);
        }
    }

    /**
     * 检查两个规则的范围是否存在交集
     *
     * @param rule1 第一个规则
     * @param rule2 第二个规则
     * @return 如果存在交集返回 true，否则返回 false
     */
    private boolean isRangeOverlap(TiktokCelebrityRule rule1, TiktokCelebrityRule rule2) {
        int rule1Min = rule1.getMinNum();
        int rule1Max = rule1.getMaxNum();
        int rule2Min = rule2.getMinNum();
        int rule2Max = rule2.getMaxNum();

        // 判断是否存在交集，允许最大值相等的情况
        return (rule2Min < rule1Max && rule2Max > rule1Min);
    }

    /**
     * 编辑
     *
     * @param tiktokCelebrityRule
     * @return
     */
    @AutoLog(value = "网红分配规则表-" + SystemConstants.EDIT)
    @Operation(summary = "网红分配规则表-" + SystemConstants.EDIT, description = "网红分配规则表-" + SystemConstants.EDIT)
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody TiktokCelebrityRule tiktokCelebrityRule) {
        String ruleLevel = tiktokCelebrityRule.getRuleLevel();
        if (oConvertUtils.isEmpty(ruleLevel)) {
            return Result.error("请输入规则档位！");
        }
        LambdaQueryWrapper<TiktokCelebrityRule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TiktokCelebrityRule::getPlatformType, tiktokCelebrityRule.getPlatformType());
        List<TiktokCelebrityRule> existingRules = tiktokCelebrityRuleService.list(queryWrapper);
        boolean hasConflict = false;

        for (TiktokCelebrityRule existingRule : existingRules) {
            if (existingRule.getId().equals(tiktokCelebrityRule.getId())) {
                // 跳过当前规则（正在编辑的规则）
                continue;
            }

            // 检查是否存在范围交集
            if (isRangeOverlap(existingRule, tiktokCelebrityRule)) {
                hasConflict = true;
                break; // 找到冲突，提前退出循环
            }
            // 检查是否存在相同的rule_level
            if (existingRule.getRuleLevel().equals(ruleLevel)) {
                hasConflict = true;
                break; // 找到冲突，提前退出循环
            }
        }

        if (hasConflict) {
            return Result.error("规则范围或规则档位与现有规则存在冲突");
        } else {
            tiktokCelebrityRuleService.updateById(tiktokCelebrityRule);
            return Result.ok(SystemConstants.EDIT_SUCCESS);
        }
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "网红分配规则表-" + SystemConstants.DELETE_BY_ID)
    @Operation(summary = "网红分配规则表-" + SystemConstants.DELETE_BY_ID, description = "网红分配规则表-" + SystemConstants.DELETE_BY_ID)
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        tiktokCelebrityRuleService.removeById(id);
        return Result.ok(SystemConstants.DELETE_SUCCESS);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "网红分配规则表-" + SystemConstants.DELETE_BATCH)
    @Operation(summary = "网红分配规则表-" + SystemConstants.DELETE_BATCH, description = "网红分配规则表-" + SystemConstants.DELETE_BATCH)
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.tiktokCelebrityRuleService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok(SystemConstants.DELETE_BATCH_SUCCESS);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "网红分配规则表-" + SystemConstants.QUERY_BY_ID)
    @Operation(summary = "网红分配规则表-" + SystemConstants.QUERY_BY_ID, description = "网红分配规则表-" + SystemConstants.QUERY_BY_ID)
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        TiktokCelebrityRule tiktokCelebrityRule = tiktokCelebrityRuleService.getById(id);
        return Result.ok(tiktokCelebrityRule);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param tiktokCelebrityRule
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TiktokCelebrityRule tiktokCelebrityRule) {
        return super.exportXls(request, tiktokCelebrityRule, TiktokCelebrityRule.class, "网红分配规则表");
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
        return super.importExcel(request, response, TiktokCelebrityRule.class);
    }

}
