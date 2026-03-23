package org.jeecg.modules.email.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import org.jeecg.modules.email.entity.EmailAuth;
import org.jeecg.modules.email.service.IEmailAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Description: 邮箱授权表
 * @Author: jeecgboot
 * @Date: 2025-11-07
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "邮箱授权表")
@RestController
@RequestMapping("/email/emailAuth")
public class EmailAuthController extends JeecgController<EmailAuth, IEmailAuthService> {
    @Autowired
    private IEmailAuthService emailAuthService;

    /**
     * 分页列表查询
     *
     * @param emailAuth
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "邮箱授权表-" + ExamConstants.PAGE_LIST_QUERY)
    @Operation(summary = "邮箱授权表-" + ExamConstants.PAGE_LIST_QUERY, description = "邮箱授权表-" + ExamConstants.PAGE_LIST_QUERY)
    @GetMapping(value = "/list")
    public Result<?> queryPageList(EmailAuth emailAuth,
                                   @RequestParam(name = ExamConstants.PAGE_NO, defaultValue = ExamConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                   @RequestParam(name = ExamConstants.PAGE_SIZE, defaultValue = ExamConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<EmailAuth> queryWrapper = QueryGenerator.initQueryWrapper(emailAuth, req.getParameterMap());
        Page<EmailAuth> page = new Page<EmailAuth>(pageNo, pageSize);
        IPage<EmailAuth> pageList = emailAuthService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param emailAuth
     * @return
     */
    @AutoLog(value = "邮箱授权表-" + ExamConstants.ADD)
    @Operation(summary = "邮箱授权表-" + ExamConstants.ADD, description = "邮箱授权表-" + ExamConstants.ADD)
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody EmailAuth emailAuth) {
        emailAuthService.save(emailAuth);
        return Result.ok(ExamConstants.ADD_SUCCESS);
    }

    /**
     * 编辑
     *
     * @param emailAuth
     * @return
     */
    @AutoLog(value = "邮箱授权表-" + ExamConstants.EDIT)
    @Operation(summary = "邮箱授权表-" + ExamConstants.EDIT, description = "邮箱授权表-" + ExamConstants.EDIT)
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody EmailAuth emailAuth) {
        emailAuthService.updateById(emailAuth);
        return Result.ok(ExamConstants.EDIT_SUCCESS);
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "邮箱授权表-" + ExamConstants.DELETE_BY_ID)
    @Operation(summary = "邮箱授权表-" + ExamConstants.DELETE_BY_ID, description = "邮箱授权表-" + ExamConstants.DELETE_BY_ID)
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        emailAuthService.removeById(id);
        return Result.ok(ExamConstants.DELETE_SUCCESS);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "邮箱授权表-" + ExamConstants.DELETE_BATCH)
    @Operation(summary = "邮箱授权表-" + ExamConstants.DELETE_BATCH, description = "邮箱授权表-" + ExamConstants.DELETE_BATCH)
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.emailAuthService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok(ExamConstants.DELETE_BATCH_SUCCESS);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "邮箱授权表-" + ExamConstants.QUERY_BY_ID)
    @Operation(summary = "邮箱授权表-" + ExamConstants.QUERY_BY_ID, description = "邮箱授权表-" + ExamConstants.QUERY_BY_ID)
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        EmailAuth emailAuth = emailAuthService.getById(id);
        return Result.ok(emailAuth);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param emailAuth
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, EmailAuth emailAuth) {
        return super.exportXls(request, emailAuth, EmailAuth.class, "邮箱授权表");
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
        return super.importExcel(request, response, EmailAuth.class);
    }

}
