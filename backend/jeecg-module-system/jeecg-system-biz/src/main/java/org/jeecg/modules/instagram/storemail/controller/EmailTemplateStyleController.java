package org.jeecg.modules.instagram.storemail.controller;

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
import org.jeecg.modules.instagram.storemail.entity.EmailTemplateStyle;
import org.jeecg.modules.instagram.storemail.service.IEmailTemplateStyleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Description: 邮件模板样式
 * @Author: jeecg-boot
 * @Date: 2020-12-15
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "邮件模板样式")
@RestController
@RequestMapping("/emailTemplateStyle")
public class EmailTemplateStyleController extends JeecgController<EmailTemplateStyle, IEmailTemplateStyleService> {
    @Autowired
    private IEmailTemplateStyleService emailTemplateStyleService;

    /**
     * 分页列表查询
     *
     * @param emailTemplateStyle
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "邮件模板样式-分页列表查询")
@Operation(summary = "邮件模板样式-分页列表查询", description = "邮件模板样式-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(EmailTemplateStyle emailTemplateStyle,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<EmailTemplateStyle> queryWrapper = QueryGenerator.initQueryWrapper(emailTemplateStyle, req.getParameterMap());
        Page<EmailTemplateStyle> page = new Page<EmailTemplateStyle>(pageNo, pageSize);
        IPage<EmailTemplateStyle> pageList = emailTemplateStyleService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param emailTemplateStyle
     * @return
     */
    @AutoLog(value = "邮件模板样式-添加")
@Operation(summary = "邮件模板样式-添加", description = "邮件模板样式-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody EmailTemplateStyle emailTemplateStyle) {
        emailTemplateStyleService.save(emailTemplateStyle);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param emailTemplateStyle
     * @return
     */
    @AutoLog(value = "邮件模板样式-编辑")
@Operation(summary = "邮件模板样式-编辑", description = "邮件模板样式-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody EmailTemplateStyle emailTemplateStyle) {
        emailTemplateStyleService.updateById(emailTemplateStyle);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "邮件模板样式-通过id删除")
@Operation(summary = "邮件模板样式-通过id删除", description = "邮件模板样式-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        emailTemplateStyleService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "邮件模板样式-批量删除")
@Operation(summary = "邮件模板样式-批量删除", description = "邮件模板样式-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.emailTemplateStyleService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "邮件模板样式-通过id查询")
@Operation(summary = "邮件模板样式-通过id查询", description = "邮件模板样式-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        EmailTemplateStyle emailTemplateStyle = emailTemplateStyleService.getById(id);
        return Result.ok(emailTemplateStyle);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param emailTemplateStyle
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, EmailTemplateStyle emailTemplateStyle) {
        return super.exportXls(request, emailTemplateStyle, EmailTemplateStyle.class, "邮件模板样式");
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
        return super.importExcel(request, response, EmailTemplateStyle.class);
    }

    @GetMapping(value = "/getHtmlEmail")
    public Result<?> getHtmlEmail() {
        String html = "<div style=\"width:500px;height: 400px;\">" +
                "<p style=\"color:red;font-size: 30px;\">${title1}</p>" +
                "<p style=\"font-size:14px;\">${content1}</p>" +
                "<img src=\"${url1}\" />" +
                "</div>";
        Result<String> result = new Result<>();
        result.setSuccess(true);
        result.setResult(html);
        return result;
    }

}
