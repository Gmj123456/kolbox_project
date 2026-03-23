package org.jeecg.modules.email.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.ExamConstants;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.email.entity.EmailPushDetail;
import org.jeecg.modules.email.entity.EmailPushMain;
import org.jeecg.modules.email.entity.EmailReply;
import org.jeecg.modules.email.model.EmailReplyRequestModel;
import org.jeecg.modules.email.service.IEmailPushDetailService;
import org.jeecg.modules.email.service.IEmailReplyService;

import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.constant.SystemConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

// 添加HTTP客户端相关导入
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 邮件回复表
 * @Author: jeecgboot
 * @Date: 2025-11-07
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "邮件回复表")
@RestController
@RequestMapping("/email/emailReply")
public class EmailReplyController extends JeecgController<EmailReply, IEmailReplyService> {
    @Autowired
    private IEmailReplyService emailReplyService;
    @Resource
    private ISysBaseAPI sysBaseAPI;
    @Autowired
    private IEmailPushDetailService pushDetailService;

    /**
     * 分页列表查询
     *
     * @param emailReply
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "邮件回复表-" + ExamConstants.PAGE_LIST_QUERY)
    @Operation(summary = "邮件回复表-" + ExamConstants.PAGE_LIST_QUERY, description = "邮件回复表-" + ExamConstants.PAGE_LIST_QUERY)
    @GetMapping(value = "/list")
    public Result<?> queryPageList(EmailReply emailReply,
                                   @RequestParam(name = ExamConstants.PAGE_NO, defaultValue = ExamConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                   @RequestParam(name = ExamConstants.PAGE_SIZE, defaultValue = ExamConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
                                   HttpServletRequest req) {
        LambdaQueryWrapper<EmailReply> queryWrapper = QueryGenerator
                .initQueryWrapper(emailReply, req.getParameterMap()).lambda();
        if (!oConvertUtils.checkUserType(getUserNameByToken())) {
            queryWrapper.eq(EmailReply::getCounselorId, getUserIdByToken());
        }
        Page<EmailReply> page = new Page<>(pageNo, pageSize);
        IPage<EmailReply> pageList = emailReplyService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param emailReply
     * @return
     */
    @AutoLog(value = "邮件回复表-" + ExamConstants.ADD)
    @Operation(summary = "邮件回复表-" + ExamConstants.ADD, description = "邮件回复表-" + ExamConstants.ADD)
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody EmailReply emailReply) {
        emailReplyService.save(emailReply);
        return Result.ok(ExamConstants.ADD_SUCCESS);
    }

    /**
     * 编辑
     *
     * @param emailReply
     * @return
     */
    @AutoLog(value = "邮件回复表-" + ExamConstants.EDIT)
    @Operation(summary = "邮件回复表-" + ExamConstants.EDIT, description = "邮件回复表-" + ExamConstants.EDIT)
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody EmailReply emailReply) {
        emailReplyService.updateById(emailReply);
        return Result.ok(ExamConstants.EDIT_SUCCESS);
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "邮件回复表-" + ExamConstants.DELETE_BY_ID)
    @Operation(summary = "邮件回复表-" + ExamConstants.DELETE_BY_ID, description = "邮件回复表-" + ExamConstants.DELETE_BY_ID)
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        emailReplyService.removeById(id);
        return Result.ok(ExamConstants.DELETE_SUCCESS);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "邮件回复表-" + ExamConstants.DELETE_BATCH)
    @Operation(summary = "邮件回复表-" + ExamConstants.DELETE_BATCH, description = "邮件回复表-" + ExamConstants.DELETE_BATCH)
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.emailReplyService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok(ExamConstants.DELETE_BATCH_SUCCESS);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "邮件回复表-" + ExamConstants.QUERY_BY_ID)
    @Operation(summary = "邮件回复表-" + ExamConstants.QUERY_BY_ID, description = "邮件回复表-" + ExamConstants.QUERY_BY_ID)
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        EmailReply emailReply = emailReplyService.getById(id);
        return Result.ok(emailReply);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param emailReply
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, EmailReply emailReply) {
        return super.exportXls(request, emailReply, EmailReply.class, "邮件回复表");
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
        return super.importExcel(request, response, EmailReply.class);
    }

    /**
     * 回复邮件接口
     *
     * @param requestModel 请求参数模型
     * @return
     */
    @AutoLog(value = "邮件回复表-回复邮件")
    @Operation(summary = "邮件回复表-回复邮件", description = "邮件回复表-回复邮件")
    @PostMapping(value = "/replyEmail")
    public Result<?> replyEmail(@RequestBody EmailReplyRequestModel requestModel) {
        try {
            // 校验请求参数
            Result<?> validationResult = validateRequest(requestModel);
            if (validationResult != null) {
                return validationResult;
            }

            // 根据ID查询邮件回复记录
            EmailReply originalEmailReply = emailReplyService.getById(requestModel.getId());
            if (originalEmailReply == null) {
                return Result.error("未找到邮件回复记录");
            }

            // 获取推送详情
            EmailPushDetail pushDetail = pushDetailService.getById(originalEmailReply.getPushDetailId());
            if (pushDetail == null) {
                return Result.error("未找到邮件详情记录");
            }
            String replyId = IdWorker.get32UUID();
            // 处理邮件内容中的占位符
            String processedEmailContent = processEmailContent(requestModel.getEmailContent(), pushDetail);
            processedEmailContent += "<br/><div>-----------------------------------------------------</div>";
            processedEmailContent += originalEmailReply.getEmailContent();
            // 调用外部接口发送邮件回复
            Object externalResult = callExternalApi(originalEmailReply, replyId, requestModel.getCcEmails(), processedEmailContent);

            // 检查外部接口调用是否成功
            Result<?> apiCallResult = handleExternalApiResult(externalResult);
            if (apiCallResult != null) {
                return apiCallResult;
            }

            // 保存新的邮件回复记录
            EmailReply newEmailReply = createNewEmailReply(originalEmailReply, pushDetail, processedEmailContent,
                    externalResult);
            emailReplyService.save(newEmailReply);

            // 返回成功结果
            return Result.ok(externalResult);
        } catch (Exception e) {
            log.error("回复邮件失败", e);
            return Result.error("邮件回复处理异常，请稍后重试");
        }
    }

    /**
     * 校验请求参数
     */
    private Result<?> validateRequest(EmailReplyRequestModel requestModel) {
        if (requestModel == null) {
            return Result.error("请求参数不能为空");
        }

        if (requestModel.getId() == null || requestModel.getId().trim().isEmpty()) {
            return Result.error("邮件回复ID不能为空");
        }

        String emailContent = requestModel.getEmailContent();
        if (emailContent == null || emailContent.trim().isEmpty()) {
            return Result.error("邮件内容不能为空");
        }

        return null;
    }

    /**
     * 处理邮件内容中的占位符
     */
    private String processEmailContent(String emailContent, EmailPushDetail pushDetail) {
        if (!emailContent.contains("{{name}}")) {
            return emailContent;
        }

        String username = pushDetail.getUsername();
        String nickname = pushDetail.getNickname();
        String account = pushDetail.getAccount();
        List<DictModel> templateVar = sysBaseAPI.queryDictItemsByCode("template_var");
        for (DictModel model : templateVar) {
            String value = model.getValue();
            if (oConvertUtils.isNotEmpty(username)) {
                emailContent = emailContent.replace(value, username);
            } else if (oConvertUtils.isNotEmpty(nickname)) {
                emailContent = emailContent.replace(value, nickname);
            } else if (oConvertUtils.isNotEmpty(account)) {
                emailContent = emailContent.replace(value, account);
            } else {
                emailContent = emailContent.replace(value, "");
            }
        }
        return emailContent;
    }

    /**
     * 处理外部API调用结果
     */
    private Result<?> handleExternalApiResult(Object externalResult) {
        if (!(externalResult instanceof Map)) {
            return Result.error("调用外部接口失败");
        }

        Map<String, Object> resultMap = (Map<String, Object>) externalResult;
        String status = (String) resultMap.get("status");
        if (!"success".equals(status)) {
            String errorMessage = (String) resultMap.get("message");
            if (errorMessage == null || errorMessage.trim().isEmpty()) {
                errorMessage = "邮件回复失败，请检查文件内容是否正确！";
            }
            log.error("邮件回复失败: " + errorMessage);
            return Result.error(errorMessage);
        }

        return null;
    }

    /**
     * 创建新的邮件回复记录
     */
    private EmailReply createNewEmailReply(EmailReply originalEmailReply, EmailPushDetail pushDetail,
                                           String emailContent, Object externalResult) {
        EmailReply newEmailReply = new EmailReply();
        newEmailReply.setPushDetailId(originalEmailReply.getPushDetailId());

        Map<String, Object> resultMap = (Map<String, Object>) externalResult;
        newEmailReply.setMessageId((String) resultMap.get("message_id"));
        newEmailReply.setThreadId((String) resultMap.get("thread_id"));
        newEmailReply.setSendEmail(originalEmailReply.getSendEmail());
        newEmailReply.setEmail(originalEmailReply.getEmail());

        // 检查并设置回复邮件标题
        String replyTitle = originalEmailReply.getEmailTitle();
        if (replyTitle != null && !replyTitle.startsWith("Re:")) {
            replyTitle = "Re:" + replyTitle;
        }
        newEmailReply.setEmailTitle(replyTitle);

        newEmailReply.setEmailContent(emailContent);
        // 设置回复类型为1（网红顾问回复邮件）
        newEmailReply.setReplyType(1);
        newEmailReply.setContactEmailId(originalEmailReply.getContactEmailId());
        newEmailReply.setCounselorId(originalEmailReply.getCounselorId());
        newEmailReply.setCounselorName(originalEmailReply.getCounselorName());
        newEmailReply.setReceivedTime(new Date());

        return newEmailReply;
    }

    /**
     * 调用外部接口回复邮件
     *
     * @param emailReply   邮件回复
     * @param emailContent 邮件内容
     * @return 外部接口返回结果
     */
    private Object callExternalApi(EmailReply emailReply, String replyId, String ccEmails, String emailContent) {
        try {
            // 创建RestTemplate实例
            RestTemplate restTemplate = new RestTemplate();

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 构建请求参数
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("user_email", emailReply.getSendEmail()); // 发送邮箱
            requestBody.put("in_reply_to_id", emailReply.getMessageId()); // 邮件消息ID
            requestBody.put("thread_id", emailReply.getThreadId()); // 邮件线程ID
            requestBody.put("subject", emailReply.getEmailTitle()); // 邮件标题
            requestBody.put("to_email", emailReply.getEmail()); // 接收者邮箱/网红邮箱
            requestBody.put("cc_email", ccEmails); // 抄送邮箱
            requestBody.put("html_content", emailContent); // 邮件内容
            requestBody.put("signature", ""); // 签名（可选，默认为空字符串）
            requestBody.put("reply_id", replyId);

            // 创建HTTP实体
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            // 调用外部接口
            List<DictModel> dictModels = sysBaseAPI.queryDictItemsByCode("email_api");
            if (dictModels == null || dictModels.isEmpty()) {
                throw new RuntimeException("未配置邮件API地址");
            }

            DictModel dictModel = dictModels.get(0);
            if (dictModel == null || dictModel.getValue() == null || dictModel.getValue().trim().isEmpty()) {
                throw new RuntimeException("邮件API地址配置为空");
            }

            String externalApiUrl = dictModel.getValue();
            ResponseEntity<Map> responseEntity = restTemplate.postForEntity(externalApiUrl, requestEntity, Map.class);

            // 处理响应结果
            Map<String, Object> responseBody = responseEntity.getBody();
            if (responseBody != null) {
                String status = (String) responseBody.get("status");
                if ("success".equals(status)) {
                    log.info("邮件回复成功，消息ID: " + responseBody.get("message_id") + "，线程ID: "
                            + responseBody.get("thread_id"));
                } else {
                    String errorMessage = (String) responseBody.get("message");
                    if (errorMessage == null || errorMessage.trim().isEmpty()) {
                        errorMessage = "邮件回复失败";
                    }
                    log.error("邮件回复失败: " + errorMessage);
                }
                return responseBody;
            }

            // 返回默认成功结果
            Map<String, Object> result = new HashMap<>();
            result.put("status", "success");
            result.put("message", "邮件回复请求已发送");
            return result;
        } catch (Exception e) {
            log.error("调用外部接口失败", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("status", "error");
            errorResult.put("message", "调用外部接口失败: " + e.getMessage());
            return errorResult;
        }
    }

    @GetMapping(value = "/queryByEmailDetailId")
    public Result<?> queryByEmailDetailId(@RequestParam(name = "id", required = true) String id) {
        List<EmailReply> replyList = emailReplyService.lambdaQuery().eq(EmailReply::getPushDetailId, id).list();
        return Result.ok(replyList);
    }
}