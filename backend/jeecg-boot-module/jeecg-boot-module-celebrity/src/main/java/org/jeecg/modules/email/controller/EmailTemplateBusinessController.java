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
import org.jeecg.modules.email.entity.EmailTemplateBusiness;
import org.jeecg.modules.email.service.IEmailTemplateBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @Description: 商务Email模板
 * @Author: jeecgboot
 * @Date: 2025-11-07
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "商务Email模板")
@RestController
@RequestMapping("/email/emailTemplateBusiness")
public class EmailTemplateBusinessController extends JeecgController<EmailTemplateBusiness, IEmailTemplateBusinessService> {
    @Autowired
    private IEmailTemplateBusinessService emailTemplateBusinessService;

    private static final String BRAND_NAME_PLACEHOLDER = "{brandName}";

    @AutoLog(value = "商务Email模板-" + ExamConstants.PAGE_LIST_QUERY)
    @Operation(summary = "商务Email模板-" + ExamConstants.PAGE_LIST_QUERY, description = "商务Email模板-" + ExamConstants.PAGE_LIST_QUERY)
    @GetMapping(value = "/list")
    public Result<?> queryPageList(EmailTemplateBusiness emailTemplateBusiness,
                                   @RequestParam(name = ExamConstants.PAGE_NO, defaultValue = ExamConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                   @RequestParam(name = ExamConstants.PAGE_SIZE, defaultValue = ExamConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
                                   HttpServletRequest req) {
        LambdaQueryWrapper<EmailTemplateBusiness> queryWrapper = QueryGenerator
                .initQueryWrapper(emailTemplateBusiness, req.getParameterMap()).lambda();
        if (!oConvertUtils.checkUserType(getUserNameByToken())) {
            queryWrapper.eq(EmailTemplateBusiness::getCounselorId, getUserIdByToken());
        }
        Page<EmailTemplateBusiness> page = new Page<>(pageNo, pageSize);
        IPage<EmailTemplateBusiness> pageList = emailTemplateBusinessService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @GetMapping(value = "/listAll")
    public Result<?> listAll(EmailTemplateBusiness emailTemplateBusiness) {
        List<EmailTemplateBusiness> templates = emailTemplateBusinessService.lambdaQuery().eq(EmailTemplateBusiness::getCounselorId, getUserIdByToken()).list();
        return Result.ok(templates);
    }

    @GetMapping(value = "/listByCategory")
    public Result<?> listByCategory(EmailTemplateBusiness emailTemplateBusiness) {
        LambdaQueryWrapper<EmailTemplateBusiness> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EmailTemplateBusiness::getCounselorId, getUserIdByToken());
        if (oConvertUtils.isNotEmpty(emailTemplateBusiness.getBrandCategoryId())) {
            queryWrapper.eq(EmailTemplateBusiness::getBrandCategoryId, emailTemplateBusiness.getBrandCategoryId());
        }
        List<EmailTemplateBusiness> templates = emailTemplateBusinessService.list(queryWrapper);
        return Result.ok(templates);
    }

    @AutoLog(value = "商务Email模板-" + ExamConstants.ADD)
    @Operation(summary = "商务Email模板-" + ExamConstants.ADD, description = "商务Email模板-" + ExamConstants.ADD)
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody EmailTemplateBusiness emailTemplateBusiness) {
        if (emailTemplateBusiness.getTemplateTitle() == null || emailTemplateBusiness.getTemplateTitle().trim().isEmpty()) {
            return Result.error("模板标题不能为空！");
        }

        if (oConvertUtils.isEmpty(emailTemplateBusiness.getTemplateContent())) {
            return Result.error("模板内容不能为空！");
        }

        if (oConvertUtils.isEmpty(emailTemplateBusiness.getBrandCategoryId())) {
            return Result.error("品牌类目不能为空！");
        }

        String placeholderWarning = validatePlaceholders(emailTemplateBusiness.getTemplateContent());
        if (placeholderWarning != null) {
            log.warn("商务Email模板占位符警告: {}", placeholderWarning);
        }

        emailTemplateBusiness.setCounselorId(getUserIdByToken());
        emailTemplateBusiness.setCounselorName(getUserNameByToken());
        emailTemplateBusiness.setTemplateContentOriginal(emailTemplateBusiness.getTemplateContentOriginal());
        if (emailTemplateBusiness.getSortCode() == null) {
            emailTemplateBusiness.setSortCode(0);
        }
        emailTemplateBusinessService.save(emailTemplateBusiness);
        return Result.ok(ExamConstants.ADD_SUCCESS);
    }

    @AutoLog(value = "商务Email模板-" + ExamConstants.EDIT)
    @Operation(summary = "商务Email模板-" + ExamConstants.EDIT, description = "商务Email模板-" + ExamConstants.EDIT)
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody EmailTemplateBusiness emailTemplateBusiness) {
        String userId = getUserIdByToken();

        if (emailTemplateBusiness.getId() == null || emailTemplateBusiness.getId().trim().isEmpty()) {
            return Result.error("记录ID不能为空！");
        }

        EmailTemplateBusiness existingTemplate = emailTemplateBusinessService.getById(emailTemplateBusiness.getId());
        if (existingTemplate == null) {
            return Result.error("要编辑的记录不存在！");
        }

        if (emailTemplateBusiness.getTemplateTitle() == null || emailTemplateBusiness.getTemplateTitle().trim().isEmpty()) {
            return Result.error("模板标题不能为空！");
        }

        if (emailTemplateBusiness.getTemplateContent() == null) {
            return Result.error("模板内容不能为空！");
        }

        if (oConvertUtils.isEmpty(emailTemplateBusiness.getBrandCategoryId())) {
            return Result.error("品牌类目不能为空！");
        }

        String placeholderWarning = validatePlaceholders(emailTemplateBusiness.getTemplateContent());
        if (placeholderWarning != null) {
            log.warn("商务Email模板占位符警告: {}", placeholderWarning);
        }

        emailTemplateBusiness.setUpdateBy(userId);
        emailTemplateBusiness.setUpdateTime(new Date());
        emailTemplateBusiness.setTemplateContentOriginal(emailTemplateBusiness.getTemplateContentOriginal());

        emailTemplateBusinessService.updateById(emailTemplateBusiness);
        return Result.ok(ExamConstants.EDIT_SUCCESS);
    }

    @AutoLog(value = "商务Email模板-" + ExamConstants.DELETE_BY_ID)
    @Operation(summary = "商务Email模板-" + ExamConstants.DELETE_BY_ID, description = "商务Email模板-" + ExamConstants.DELETE_BY_ID)
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        emailTemplateBusinessService.removeById(id);
        return Result.ok(ExamConstants.DELETE_SUCCESS);
    }

    @AutoLog(value = "商务Email模板-" + ExamConstants.DELETE_BATCH)
    @Operation(summary = "商务Email模板-" + ExamConstants.DELETE_BATCH, description = "商务Email模板-" + ExamConstants.DELETE_BATCH)
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.emailTemplateBusinessService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok(ExamConstants.DELETE_BATCH_SUCCESS);
    }

    @AutoLog(value = "商务Email模板-" + ExamConstants.QUERY_BY_ID)
    @Operation(summary = "商务Email模板-" + ExamConstants.QUERY_BY_ID, description = "商务Email模板-" + ExamConstants.QUERY_BY_ID)
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        EmailTemplateBusiness emailTemplateBusiness = emailTemplateBusinessService.getById(id);
        return Result.ok(emailTemplateBusiness);
    }

    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, EmailTemplateBusiness emailTemplateBusiness) {
        return super.exportXls(request, emailTemplateBusiness, EmailTemplateBusiness.class, "商务Email模板");
    }

    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, EmailTemplateBusiness.class);
    }

    private String validatePlaceholders(String content) {
        if (oConvertUtils.isEmpty(content)) {
            return null;
        }

        Pattern pattern = Pattern.compile("\\{[^}]+\\}");
        java.util.regex.Matcher matcher = pattern.matcher(content);
        StringBuilder unknownPlaceholders = new StringBuilder();

        while (matcher.find()) {
            String placeholder = matcher.group();
            if (!BRAND_NAME_PLACEHOLDER.equals(placeholder)) {
                if (unknownPlaceholders.length() > 0) {
                    unknownPlaceholders.append(", ");
                }
                unknownPlaceholders.append(placeholder);
            }
        }

        if (unknownPlaceholders.length() > 0) {
            return "模板中包含未知占位符: " + unknownPlaceholders.toString() + "，仅支持 " + BRAND_NAME_PLACEHOLDER;
        }

        return null;
    }
}
