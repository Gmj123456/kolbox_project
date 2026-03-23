package org.jeecg.modules.email.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.ExamConstants;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.email.entity.EmailTemplateCategory;
import org.jeecg.modules.email.service.IEmailTemplateCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Description: Email模版所属类目
 * @Author: jeecgboot
 * @Date: 2025-11-07
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "Email模版所属类目")
@RestController
@RequestMapping("/email/emailTemplateCategory")
public class EmailTemplateCategoryController extends JeecgController<EmailTemplateCategory, IEmailTemplateCategoryService> {
    @Autowired
    private IEmailTemplateCategoryService emailTemplateCategoryService;

    @AutoLog(value = "Email模版所属类目-" + ExamConstants.PAGE_LIST_QUERY)
    @Operation(summary = "Email模版所属类目-" + ExamConstants.PAGE_LIST_QUERY, description = "Email模版所属类目-" + ExamConstants.PAGE_LIST_QUERY)
    @GetMapping(value = "/list")
    public Result<?> queryPageList(EmailTemplateCategory emailTemplateCategory,
                                   @RequestParam(name = ExamConstants.PAGE_NO, defaultValue = ExamConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                   @RequestParam(name = ExamConstants.PAGE_SIZE, defaultValue = ExamConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
                                   HttpServletRequest req) {
        LambdaQueryWrapper<EmailTemplateCategory> queryWrapper = QueryGenerator
                .initQueryWrapper(emailTemplateCategory, req.getParameterMap()).lambda();
        if (!oConvertUtils.checkUserType(getUserNameByToken())) {
            queryWrapper.eq(EmailTemplateCategory::getCounselorId, getUserIdByToken());
        }
        queryWrapper.orderByAsc(EmailTemplateCategory::getSortCode);
        Page<EmailTemplateCategory> page = new Page<>(pageNo, pageSize);
        IPage<EmailTemplateCategory> pageList = emailTemplateCategoryService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @GetMapping(value = "/listAll")
    public Result<?> listAll(EmailTemplateCategory emailTemplateCategory) {
        LambdaQueryWrapper<EmailTemplateCategory> queryWrapper = new LambdaQueryWrapper<>();
        if (!oConvertUtils.checkUserType(getUserNameByToken())) {
            queryWrapper.eq(EmailTemplateCategory::getCounselorId, getUserIdByToken());
        }
        queryWrapper.orderByAsc(EmailTemplateCategory::getSortCode);
        List<EmailTemplateCategory> categories = emailTemplateCategoryService.list(queryWrapper);
        return Result.ok(categories);
    }

    @AutoLog(value = "Email模版所属类目-" + ExamConstants.ADD)
    @Operation(summary = "Email模版所属类目-" + ExamConstants.ADD, description = "Email模版所属类目-" + ExamConstants.ADD)
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody EmailTemplateCategory emailTemplateCategory) {
        if (emailTemplateCategory.getCategoryName() == null || emailTemplateCategory.getCategoryName().trim().isEmpty()) {
            return Result.error("类目名称不能为空！");
        }

        String userId = getUserIdByToken();
        String categoryName = emailTemplateCategory.getCategoryName().trim();

        Long count = emailTemplateCategoryService.lambdaQuery()
                .eq(EmailTemplateCategory::getCategoryName, categoryName)
                .eq(EmailTemplateCategory::getCounselorId, userId)
                .count();
        if (count > 0) {
            return Result.error("类目名称已存在！");
        }

        emailTemplateCategory.setCounselorId(userId);
        emailTemplateCategory.setCounselorName(getUserNameByToken());
        emailTemplateCategory.setCategoryName(categoryName);
        if (emailTemplateCategory.getSortCode() == null) {
            emailTemplateCategory.setSortCode(1);
        }
        emailTemplateCategoryService.save(emailTemplateCategory);
        return Result.ok(ExamConstants.ADD_SUCCESS);
    }

    @AutoLog(value = "Email模版所属类目-" + ExamConstants.EDIT)
    @Operation(summary = "Email模版所属类目-" + ExamConstants.EDIT, description = "Email模版所属类目-" + ExamConstants.EDIT)
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody EmailTemplateCategory emailTemplateCategory) {
        String userId = getUserIdByToken();

        if (emailTemplateCategory.getId() == null || emailTemplateCategory.getId().trim().isEmpty()) {
            return Result.error("记录ID不能为空！");
        }

        EmailTemplateCategory existingCategory = emailTemplateCategoryService.getById(emailTemplateCategory.getId());
        if (existingCategory == null) {
            return Result.error("要编辑的记录不存在！");
        }

        if (emailTemplateCategory.getCategoryName() == null || emailTemplateCategory.getCategoryName().trim().isEmpty()) {
            return Result.error("类目名称不能为空！");
        }

        String categoryName = emailTemplateCategory.getCategoryName().trim();

        Long count = emailTemplateCategoryService.lambdaQuery()
                .ne(EmailTemplateCategory::getId, emailTemplateCategory.getId())
                .eq(EmailTemplateCategory::getCategoryName, categoryName)
                .eq(EmailTemplateCategory::getCounselorId, userId)
                .count();
        if (count > 0) {
            return Result.error("类目名称已存在！");
        }

        emailTemplateCategory.setUpdateBy(userId);
        emailTemplateCategory.setUpdateTime(new Date());
        emailTemplateCategory.setCategoryName(categoryName);

        emailTemplateCategoryService.updateById(emailTemplateCategory);
        return Result.ok(ExamConstants.EDIT_SUCCESS);
    }

    @AutoLog(value = "Email模版所属类目-" + ExamConstants.DELETE_BY_ID)
    @Operation(summary = "Email模版所属类目-" + ExamConstants.DELETE_BY_ID, description = "Email模版所属类目-" + ExamConstants.DELETE_BY_ID)
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        emailTemplateCategoryService.removeById(id);
        return Result.ok(ExamConstants.DELETE_SUCCESS);
    }

    @AutoLog(value = "Email模版所属类目-" + ExamConstants.DELETE_BATCH)
    @Operation(summary = "Email模版所属类目-" + ExamConstants.DELETE_BATCH, description = "Email模版所属类目-" + ExamConstants.DELETE_BATCH)
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.emailTemplateCategoryService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok(ExamConstants.DELETE_BATCH_SUCCESS);
    }

    @AutoLog(value = "Email模版所属类目-" + ExamConstants.QUERY_BY_ID)
    @Operation(summary = "Email模版所属类目-" + ExamConstants.QUERY_BY_ID, description = "Email模版所属类目-" + ExamConstants.QUERY_BY_ID)
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        EmailTemplateCategory emailTemplateCategory = emailTemplateCategoryService.getById(id);
        return Result.ok(emailTemplateCategory);
    }

    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, EmailTemplateCategory emailTemplateCategory) {
        return super.exportXls(request, emailTemplateCategory, EmailTemplateCategory.class, "Email模版所属类目");
    }

    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, EmailTemplateCategory.class);
    }
}
