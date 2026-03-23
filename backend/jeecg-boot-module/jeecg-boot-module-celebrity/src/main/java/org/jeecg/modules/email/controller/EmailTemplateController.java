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
import org.jeecg.modules.email.entity.EmailTemplate;
import org.jeecg.modules.email.service.IEmailTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Description: 邮件模板
 * @Author: jeecgboot
 * @Date: 2025-11-07
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "邮件模板")
@RestController
@RequestMapping("/email/emailTemplate")
public class EmailTemplateController extends JeecgController<EmailTemplate, IEmailTemplateService> {
    @Autowired
    private IEmailTemplateService emailTemplateService;

    /**
     * 分页列表查询
     *
     * @param emailTemplate
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "邮件模板-" + ExamConstants.PAGE_LIST_QUERY)
    @Operation(summary = "邮件模板-" + ExamConstants.PAGE_LIST_QUERY, description = "邮件模板-" + ExamConstants.PAGE_LIST_QUERY)
    @GetMapping(value = "/list")
    public Result<?> queryPageList(EmailTemplate emailTemplate,
                                   @RequestParam(name = ExamConstants.PAGE_NO, defaultValue = ExamConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                   @RequestParam(name = ExamConstants.PAGE_SIZE, defaultValue = ExamConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
                                   HttpServletRequest req) {
        LambdaQueryWrapper<EmailTemplate> queryWrapper = QueryGenerator
                .initQueryWrapper(emailTemplate, req.getParameterMap()).lambda();
        if (!oConvertUtils.checkUserType(getUserNameByToken())) {
            queryWrapper.eq(EmailTemplate::getCounselorId, getUserIdByToken());
        }
        Page<EmailTemplate> page = new Page<>(pageNo, pageSize);
        IPage<EmailTemplate> pageList = emailTemplateService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @GetMapping(value = "/listAll")
    public Result<?> listAll(EmailTemplate emailTemplate) {
        List<EmailTemplate> templates = emailTemplateService.lambdaQuery().eq(EmailTemplate::getCounselorId, getUserIdByToken()).list();
        return Result.ok(templates);
    }

    /**
     * 功能描述：根据产品ID获取邮件模版列表
     *
     * @param emailTemplate
     * @Param:
     * @Author: fengLiu
     * @Date: 2025/11/17 19:10
     */
    @GetMapping(value = "/listByProduct")
    public Result<?> listByProduct(EmailTemplate emailTemplate) {
        LambdaQueryWrapper<EmailTemplate> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EmailTemplate::getCounselorId, getUserIdByToken());
        if (oConvertUtils.isNotEmpty(emailTemplate.getPlatformType())) {
            queryWrapper.eq(EmailTemplate::getPlatformType, emailTemplate.getPlatformType());
        }
        queryWrapper.eq(EmailTemplate::getProductId, emailTemplate.getProductId());
        queryWrapper.orderByAsc(EmailTemplate::getPlatformType);
        List<EmailTemplate> templates = emailTemplateService.list(queryWrapper);
        return Result.ok(templates);
    }

    /**
     * 添加
     *
     * @param emailTemplate
     * @return
     */
    @AutoLog(value = "邮件模板-" + ExamConstants.ADD)
    @Operation(summary = "邮件模板-" + ExamConstants.ADD, description = "邮件模板-" + ExamConstants.ADD)
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody EmailTemplate emailTemplate) {
        // 检查必要字段
        if (emailTemplate.getTemplateTitle() == null || emailTemplate.getTemplateTitle().trim().isEmpty()) {
            return Result.error("模板标题不能为空！");
        }

        if (oConvertUtils.isEmpty(emailTemplate.getTemplateContent())) {
            return Result.error("模板内容不能为空！");
        }
        if (oConvertUtils.isEmpty(emailTemplate.getPlatformType())) {
            return Result.error("平台不能为空！");
        }
        // 检查模板标题是否已存在
/*        String templateTitle = emailTemplate.getTemplateTitle().trim();
        Integer count = emailTemplateService.lambdaQuery()
                .eq(EmailTemplate::getTemplateTitle, templateTitle)
                .eq(EmailTemplate::getCounselorId, getUserIdByToken())
                .count();
        if (count > 0) {
            return Result.error("模板标题已存在！");
        }*/
        emailTemplate.setCounselorId(getUserIdByToken());
        emailTemplate.setCounselorName(getUserNameByToken());
        emailTemplateService.save(emailTemplate);
        return Result.ok(ExamConstants.ADD_SUCCESS);
    }

    /**
     * 编辑
     *
     * @param emailTemplate
     * @return
     */
    @AutoLog(value = "邮件模板-" + ExamConstants.EDIT)
    @Operation(summary = "邮件模板-" + ExamConstants.EDIT, description = "邮件模板-" + ExamConstants.EDIT)
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody EmailTemplate emailTemplate) {
        String userId = getUserIdByToken();
        Integer platformType = emailTemplate.getPlatformType();
        // 检查ID是否存在
        if (emailTemplate.getId() == null || emailTemplate.getId().trim().isEmpty()) {
            return Result.error("记录ID不能为空！");
        }

        // 检查记录是否存在
        EmailTemplate existingTemplate = emailTemplateService.getById(emailTemplate.getId());
        if (existingTemplate == null) {
            return Result.error("要编辑的记录不存在！");
        }

        // 检查必要字段
        if (emailTemplate.getTemplateTitle() == null || emailTemplate.getTemplateTitle().trim().isEmpty()) {
            return Result.error("模板标题不能为空！");
        }

        if (emailTemplate.getTemplateContent() == null) {
            return Result.error("模板内容不能为空！");
        }
        if (oConvertUtils.isEmpty(platformType)) {
            return Result.error("平台不能为空！");
        }

        // 检查模板标题是否已存在（排除自己）
        // 与凌霄沟通，不需要检查模板标题是否已存在 2026年1月16日17:27:44
       /* String templateTitle = emailTemplate.getTemplateTitle().trim();
        Integer count = Math.toIntExact(emailTemplateService.lambdaQuery()
                .ne(EmailTemplate::getId, emailTemplate.getId())
                .eq(EmailTemplate::getPlatformType, platformType)
                .eq(EmailTemplate::getTemplateTitle, templateTitle)
                .eq(EmailTemplate::getCounselorId, userId)
                .count());
        if (count > 0) {
            return Result.error("模板标题已存在！");
        }*/

        // 设置更新人信息
        emailTemplate.setUpdateBy(userId);
        emailTemplate.setUpdateTime(new Date());

        emailTemplateService.updateById(emailTemplate);
        return Result.ok(ExamConstants.EDIT_SUCCESS);
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "邮件模板-" + ExamConstants.DELETE_BY_ID)
    @Operation(summary = "邮件模板-" + ExamConstants.DELETE_BY_ID, description = "邮件模板-" + ExamConstants.DELETE_BY_ID)
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        emailTemplateService.removeById(id);
        return Result.ok(ExamConstants.DELETE_SUCCESS);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "邮件模板-" + ExamConstants.DELETE_BATCH)
    @Operation(summary = "邮件模板-" + ExamConstants.DELETE_BATCH, description = "邮件模板-" + ExamConstants.DELETE_BATCH)
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.emailTemplateService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok(ExamConstants.DELETE_BATCH_SUCCESS);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "邮件模板-" + ExamConstants.QUERY_BY_ID)
    @Operation(summary = "邮件模板-" + ExamConstants.QUERY_BY_ID, description = "邮件模板-" + ExamConstants.QUERY_BY_ID)
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        EmailTemplate emailTemplate = emailTemplateService.getById(id);
        return Result.ok(emailTemplate);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param emailTemplate
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, EmailTemplate emailTemplate) {
        return super.exportXls(request, emailTemplate, EmailTemplate.class, "邮件模板");
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
        return super.importExcel(request, response, EmailTemplate.class);
    }

}
