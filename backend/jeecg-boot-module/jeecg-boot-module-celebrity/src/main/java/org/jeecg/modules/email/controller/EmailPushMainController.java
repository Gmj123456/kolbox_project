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
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.vo.StoreUserContactEmailVo;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.email.entity.*;
import org.jeecg.modules.email.model.EmailBatchPushModel;
import org.jeecg.modules.email.model.EmailPushMainDetailVO;
import org.jeecg.modules.email.service.IEmailPushDetailService;
import org.jeecg.modules.email.service.IEmailPushMainService;
import org.jeecg.modules.email.service.IEmailTemplateService;
import org.jeecg.modules.email.service.IEmailTemplateBusinessService;
import org.jeecg.modules.email.service.IStoreUserContactEmailSignatureService;
import org.jeecg.modules.kol.entity.KolContact;
import org.jeecg.modules.kol.entity.KolProduct;
import org.jeecg.modules.kol.service.IKolContactService;
import org.jeecg.modules.kol.service.IKolProductService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 邮件消息主表
 * @Author: jeecgboot
 * @Date: 2025-11-07
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "邮件消息主表")
@RestController
@RequestMapping("/email/emailPushMain")
public class EmailPushMainController extends JeecgController<EmailPushMain, IEmailPushMainService> {
    @Autowired
    private IEmailPushMainService emailPushMainService;

    @Autowired
    @Lazy
    private IEmailPushDetailService emailPushDetailService;

    @Autowired
    private IEmailTemplateService emailTemplateService;

    @Autowired
    private IEmailTemplateBusinessService emailTemplateBusinessService;

    @Autowired
    private IKolProductService kolProductService;

    @Autowired
    private IKolContactService kolContactService;

    @Autowired
    private IStoreUserContactEmailSignatureService signatureService;

    @Resource
    private ISysBaseAPI sysBaseAPI;

    /**
     * 分页列表查询
     *
     * @param emailPushMain
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "邮件消息主表-" + ExamConstants.PAGE_LIST_QUERY)
    @Operation(summary = "邮件消息主表-" + ExamConstants.PAGE_LIST_QUERY, description = "邮件消息主表-"
            + ExamConstants.PAGE_LIST_QUERY)
    @GetMapping(value = "/list")
    public Result<?> queryPageList(EmailPushMain emailPushMain,
            @RequestParam(name = ExamConstants.PAGE_NO, defaultValue = ExamConstants.PAGE_NO_DEFAULT) Integer pageNo,
            @RequestParam(name = ExamConstants.PAGE_SIZE, defaultValue = ExamConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
            HttpServletRequest req) {
        emailPushMain.setBusinessType(0);
        if (!oConvertUtils.checkUserType(getUserNameByToken())) {
            emailPushMain.setCounselorId(getUserIdByToken());
        }
        Page<EmailPushMain> page = new Page<>(pageNo, pageSize);
        IPage<EmailPushMain> pageList = emailPushMainService.pageList(page, emailPushMain);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param emailPushMain
     * @return
     */
    @AutoLog(value = "邮件消息主表-" + ExamConstants.ADD)
    @Operation(summary = "邮件消息主表-" + ExamConstants.ADD, description = "邮件消息主表-" + ExamConstants.ADD)
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody EmailPushMain emailPushMain) {
        emailPushMainService.save(emailPushMain);
        return Result.ok(ExamConstants.ADD_SUCCESS);
    }

    /**
     * 编辑
     *
     * @param emailPushMain
     * @return
     */
    @AutoLog(value = "邮件消息主表-" + ExamConstants.EDIT)
    @Operation(summary = "邮件消息主表-" + ExamConstants.EDIT, description = "邮件消息主表-" + ExamConstants.EDIT)
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody EmailPushMain emailPushMain) {
        try {
            // 参数校验
            if (emailPushMain.getId() == null || emailPushMain.getId().isEmpty()) {
                return Result.error("ID不能为空");
            }

            // 获取原主表记录
            EmailPushMain original = emailPushMainService.getById(emailPushMain.getId());
            if (original == null) {
                return Result.error("未找到当前记录");
            }
            // 校验是我的网红发送还是导入发送
            int sendType = Optional.ofNullable(original.getSendType()).orElse(0);

            // 0) 检查签名变更并同步更新签名内容
            String newSignatureId = emailPushMain.getSignatureId();
            boolean signatureChanged = !Objects.equals(original.getSignatureId(), newSignatureId);
            if (signatureChanged) {
                if (oConvertUtils.isNotEmpty(newSignatureId)) {
                    StoreUserContactEmailSignature signature = signatureService.getById(newSignatureId);
                    if (signature != null) {
                        emailPushMain.setSignatureTitle(signature.getSignatureTitle());
                        emailPushMain.setSignatureContent(signature.getSignatureContentOriginal());
                    } else {
                        return Result.error("签名不存在，请检查签名ID");
                    }
                } else {
                    emailPushMain.setSignatureTitle("");
                    emailPushMain.setSignatureContent("");
                }
            }

            // 1) 检查产品是否存在（如果提供了产品ID）
            String newProductId = emailPushMain.getProductId();
            if (oConvertUtils.isNotEmpty(newProductId)) {
                KolProduct product = kolProductService.getById(newProductId);
                if (product == null) {
                    return Result.error("产品不存在，请检查产品ID");
                }
                emailPushMain.setProductName(product.getProductName());
            }

            // 检查是否需要重新选择模板
            // boolean productChanged = !Objects.equals(original.getProductId(),
            // newProductId);
            boolean timeConfigChanged = !Objects.equals(original.getEmailPushDate(), emailPushMain.getEmailPushDate())
                    ||
                    !Objects.equals(original.getSendIntervalMin(), emailPushMain.getSendIntervalMin()) ||
                    !Objects.equals(original.getSendIntervalMax(), emailPushMain.getSendIntervalMax());

            /*
             * // 查询明细表中是否有已发送的邮件
             * LambdaQueryWrapper<EmailPushDetail> sentQueryWrapper = new
             * LambdaQueryWrapper<>();
             * sentQueryWrapper
             * .eq(EmailPushDetail::getMainId, emailPushMain.getId())
             * .eq(EmailPushDetail::getIsSend, 1); // 1: 已发送
             * List<EmailPushDetail> sentDetails =
             * emailPushDetailService.list(sentQueryWrapper);
             * 
             * // 如果存在已发送或正在发送的邮件，则不允许修改
             * if (!sentDetails.isEmpty()) {
             * return Result.error("存在已发送或正在发送的邮件，不能修改");
             * }
             */

            // 2) 如果产品变更，检查新产品是否有邮件模板
            List<EmailTemplate> availableTemplates = new ArrayList<>();
            if (oConvertUtils.isNotEmpty(newProductId)) {
                LambdaQueryWrapper<EmailTemplate> lamdaQueryWrapper = new LambdaQueryWrapper<>();
                lamdaQueryWrapper.eq(EmailTemplate::getProductId, newProductId);
                lamdaQueryWrapper.eq(EmailTemplate::getCounselorId, getUserIdByToken());
                if (sendType == 0) {
                    lamdaQueryWrapper.eq(EmailTemplate::getPlatformType, emailPushMain.getPlatformType());
                }
                lamdaQueryWrapper.orderByAsc(EmailTemplate::getSortCode);
                availableTemplates = emailTemplateService.list(lamdaQueryWrapper);

                if (availableTemplates.isEmpty()) {
                    return Result.error("该产品没有对应的邮件模板，请先为该产品创建邮件模板");
                }
            }

            // 查询建联邮箱信息
            StoreUserContactEmailVo contactEmailVo = sysBaseAPI.getContactEmailById(emailPushMain.getContactEmailId());
            if (contactEmailVo == null) {
                return Result.error("更新失败,未获取到邮箱信息");
            }
            if (oConvertUtils.isNotEmpty(contactEmailVo.getEmailPassword())) {
                emailPushMain.setEmailPassword(contactEmailVo.getEmailPassword());
            }
            // 更新主表
            boolean mainUpdateResult = emailPushMainService.updateById(emailPushMain);
            if (!mainUpdateResult) {
                return Result.error("更新失败");
            }

            // 获取所有未发送的明细
            List<EmailPushDetail> unsentDetails = emailPushDetailService.lambdaQuery()
                    .eq(EmailPushDetail::getMainId, emailPushMain.getId())
                    .eq(EmailPushDetail::getIsSend, 0).list();

            // 如果有未发送的明细，则统一更新明细表
            if (unsentDetails != null && !unsentDetails.isEmpty()) {
                Random random = new Random();
                Date currentSendTime = null;

                // 3) 如果时间配置有变更，重新生成预发送时间
                if (timeConfigChanged && emailPushMain.getEmailPushDate() != null) {
                    currentSendTime = emailPushMain.getEmailPushDate();
                }

                // 为每个明细设置与主表相同的公共字段
                for (EmailPushDetail detail : unsentDetails) {
                    detail.setSendEmail(emailPushMain.getSendEmail());
                    detail.setSignatureId(emailPushMain.getSignatureId());
                    detail.setSignatureTitle(emailPushMain.getSignatureTitle());
                    detail.setSignatureContent(emailPushMain.getSignatureContent());
                    detail.setContactEmailId(emailPushMain.getContactEmailId());
                    detail.setCcEmails(emailPushMain.getCcEmails());
                    detail.setRemark(emailPushMain.getRemark());
                    detail.setEmailPassword(emailPushMain.getEmailPassword());

                    // 4) 如果产品变更，为每个明细重新随机选择模板
                    if (!availableTemplates.isEmpty()) {
                        List<EmailTemplate> templates = availableTemplates.stream()
                                .filter(x -> Objects.equals(x.getPlatformType(), detail.getPlatformType()))
                                .collect(Collectors.toList());
                        if (templates.isEmpty()) {
                            return Result.error("该产品没有对应的邮件模板，请先为该产品创建邮件模板");
                        }
                        EmailTemplate selectedTemplate = templates.get(random.nextInt(templates.size()));
                        detail.setTemplateId(selectedTemplate.getId());
                        String username = detail.getUsername();
                        String nickname = detail.getNickname();

                        // 获取随机模版中的邮件标题
                        String replacement = oConvertUtils.isNotEmpty(username) ? username
                                : oConvertUtils.isNotEmpty(nickname) ? nickname : "";

                        // 获取随机模版中的邮件标题
                        String templateTitle = selectedTemplate.getTemplateTitle();
                        // 使用新模板内容并个性化处理
                        String personalizedContent = selectedTemplate.getTemplateContentOriginal();
                        templateTitle = templateTitle.replace("{{name}}", replacement);
                        personalizedContent = personalizedContent.replace("{{name}}", replacement);
                        if (personalizedContent.contains("{{emailDisplayName}}")) {
                            personalizedContent = personalizedContent.replace("{{emailDisplayName}}",
                                    contactEmailVo.getEmailDisplayName());
                        }

                        detail.setEmailTitle(templateTitle);
                        detail.setEmailContent(personalizedContent);
                    }

                    // 5) 如果时间配置变更，重新设置预发送时间
                    if (timeConfigChanged && emailPushMain.getEmailPushDate() != null) {
                        detail.setPlanSendTime(currentSendTime);

                        // 设置下一个邮件的发送时间间隔
                        long intervalSeconds;
                        Integer sendIntervalStart = emailPushMain.getSendIntervalMin();
                        Integer sendIntervalEnd = emailPushMain.getSendIntervalMax();

                        if (sendIntervalStart != null && sendIntervalEnd != null
                                && sendIntervalStart <= sendIntervalEnd) {
                            long randomMinutes = sendIntervalStart
                                    + (long) (Math.random() * (sendIntervalEnd - sendIntervalStart + 1));
                            intervalSeconds = randomMinutes * 60;
                        } else {
                            long randomMinutes = 1 + (long) (Math.random() * 5);
                            intervalSeconds = randomMinutes * 60;
                        }
                        currentSendTime = new Date(currentSendTime.getTime() + intervalSeconds * 1000);
                    }
                }

                // 批量更新明细表
                boolean detailUpdateResult = emailPushDetailService.updateBatchById(unsentDetails);
                if (!detailUpdateResult) {
                    return Result.error("更新明细表失败");
                }
            }

            log.info("成功修改邮件推送记录，主表ID：{}", emailPushMain.getId());
            return Result.ok(ExamConstants.EDIT_SUCCESS);
        } catch (Exception e) {
            log.error("编辑邮件推送记录异常：", e);
            return Result.error("修改失败，系统异常");
        }
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "邮件消息主表-" + ExamConstants.DELETE_BY_ID)
    @Operation(summary = "邮件消息主表-" + ExamConstants.DELETE_BY_ID, description = "邮件消息主表-" + ExamConstants.DELETE_BY_ID)
    @DeleteMapping(value = "/delete")
    @Transactional(rollbackFor = Exception.class)
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        EmailPushMain pushMain = emailPushMainService.getById(id);
        if (pushMain.getIsSend() != 0) {
            return Result.error("该邮件已发送或取消，不能删除");
        }
        emailPushMainService.removeById(id);
        emailPushDetailService.lambdaUpdate().eq(EmailPushDetail::getMainId, id).remove();

        // 删除已标记开发的网红列表
        kolContactService.deleteKolContactByEmailPushMainId(id);
        return Result.ok(ExamConstants.DELETE_SUCCESS);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "邮件消息主表-" + ExamConstants.DELETE_BATCH)
    @Operation(summary = "邮件消息主表-" + ExamConstants.DELETE_BATCH, description = "邮件消息主表-" + ExamConstants.DELETE_BATCH)
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.emailPushMainService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok(ExamConstants.DELETE_BATCH_SUCCESS);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "邮件消息主表-" + ExamConstants.QUERY_BY_ID)
    @Operation(summary = "邮件消息主表-" + ExamConstants.QUERY_BY_ID, description = "邮件消息主表-" + ExamConstants.QUERY_BY_ID)
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        EmailPushMain emailPushMain = emailPushMainService.getById(id);
        return Result.ok(emailPushMain);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param emailPushMain
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, EmailPushMain emailPushMain) {
        return super.exportXls(request, emailPushMain, EmailPushMain.class, "邮件消息主表");
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
        return super.importExcel(request, response, EmailPushMain.class);
    }

    @AutoLog(value = "商务发信-" + ExamConstants.PAGE_LIST_QUERY)
    @Operation(summary = "商务发信-" + ExamConstants.PAGE_LIST_QUERY, description = "商务发信-" + ExamConstants.PAGE_LIST_QUERY)
    @GetMapping(value = "/businessList")
    public Result<?> queryBusinessPageList(EmailPushMain emailPushMain,
            @RequestParam(name = ExamConstants.PAGE_NO, defaultValue = ExamConstants.PAGE_NO_DEFAULT) Integer pageNo,
            @RequestParam(name = ExamConstants.PAGE_SIZE, defaultValue = ExamConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
            HttpServletRequest req) {
        emailPushMain.setBusinessType(1);
        if (!oConvertUtils.checkUserType(getUserNameByToken())) {
            emailPushMain.setCounselorId(getUserIdByToken());
        }
        Page<EmailPushMain> page = new Page<>(pageNo, pageSize);
        IPage<EmailPushMain> pageList = emailPushMainService.pageList(page, emailPushMain);
        return Result.ok(pageList);
    }

    @AutoLog(value = "商务发信-" + ExamConstants.EDIT)
    @Operation(summary = "商务发信-" + ExamConstants.EDIT, description = "商务发信-" + ExamConstants.EDIT)
    @PutMapping(value = "/businessEdit")
    public Result<?> businessEdit(@RequestBody EmailPushMain emailPushMain) {
        try {
            String userId = getUserIdByToken();

            if (emailPushMain.getId() == null || emailPushMain.getId().isEmpty()) {
                return Result.error("ID不能为空");
            }

            EmailPushMain original = emailPushMainService.getById(emailPushMain.getId());
            if (original == null) {
                return Result.error("未找到当前记录");
            }

            if (original.getBusinessType() != 1) {
                return Result.error("该记录不是商务发信记录");
            }

            String newSignatureId = emailPushMain.getSignatureId();
            boolean signatureChanged = !Objects.equals(original.getSignatureId(), newSignatureId);
            if (signatureChanged) {
                if (oConvertUtils.isNotEmpty(newSignatureId)) {
                    StoreUserContactEmailSignature signature = signatureService.getById(newSignatureId);
                    if (signature != null) {
                        emailPushMain.setSignatureTitle(signature.getSignatureTitle());
                        emailPushMain.setSignatureContent(signature.getSignatureContentOriginal());
                    } else {
                        return Result.error("签名不存在，请检查签名ID");
                    }
                } else {
                    emailPushMain.setSignatureTitle("");
                    emailPushMain.setSignatureContent("");
                }
            }

            boolean timeConfigChanged = !Objects.equals(original.getEmailPushDate(), emailPushMain.getEmailPushDate())
                    ||
                    !Objects.equals(original.getSendIntervalMin(), emailPushMain.getSendIntervalMin()) ||
                    !Objects.equals(original.getSendIntervalMax(), emailPushMain.getSendIntervalMax());

            StoreUserContactEmailVo contactEmailVo = sysBaseAPI.getContactEmailById(emailPushMain.getContactEmailId());
            if (contactEmailVo == null) {
                return Result.error("更新失败,未获取到邮箱信息");
            }
            if (oConvertUtils.isNotEmpty(contactEmailVo.getEmailPassword())) {
                emailPushMain.setEmailPassword(contactEmailVo.getEmailPassword());
            }

            emailPushMain.setUpdateBy(userId);
            emailPushMain.setUpdateTime(new Date());

            boolean mainUpdateResult = emailPushMainService.updateById(emailPushMain);
            if (!mainUpdateResult) {
                return Result.error("更新失败");
            }

            if (signatureChanged || timeConfigChanged) {
                List<EmailPushDetail> unsentDetails = emailPushDetailService.lambdaQuery()
                        .eq(EmailPushDetail::getMainId, emailPushMain.getId())
                        .eq(EmailPushDetail::getIsSend, 0)
                        .list();

                if (unsentDetails != null && !unsentDetails.isEmpty()) {
                    Date currentSendTime = null;
                    if (emailPushMain.getEmailPushDate() != null) {
                        currentSendTime = emailPushMain.getEmailPushDate();
                    }

                    for (EmailPushDetail detail : unsentDetails) {
                        detail.setSendEmail(emailPushMain.getSendEmail());
                        detail.setEmailPassword(emailPushMain.getEmailPassword());
                        detail.setSignatureId(emailPushMain.getSignatureId());
                        detail.setSignatureTitle(emailPushMain.getSignatureTitle());
                        detail.setSignatureContent(emailPushMain.getSignatureContent());
                        detail.setContactEmailId(emailPushMain.getContactEmailId());
                        detail.setCcEmails(emailPushMain.getCcEmails());
                        detail.setRemark(emailPushMain.getRemark());

                        if (signatureChanged) {
                            String personalizedContent = detail.getEmailContent();
                            String originalSignature = original.getSignatureContent();
                            String newSignature = emailPushMain.getSignatureContent();

                            if (oConvertUtils.isNotEmpty(originalSignature)) {
                                String signatureSuffix = "<br/><br/>" + originalSignature;
                                if (personalizedContent.endsWith(signatureSuffix)) {
                                    personalizedContent = personalizedContent.substring(0,
                                            personalizedContent.length() - signatureSuffix.length());
                                }
                            }

                            if (oConvertUtils.isNotEmpty(newSignature)) {
                                personalizedContent = personalizedContent + "<br/><br/>" + newSignature;
                            }

                            detail.setEmailContent(personalizedContent);
                        }

                        if (timeConfigChanged && emailPushMain.getEmailPushDate() != null) {
                            detail.setPlanSendTime(currentSendTime);

                            long intervalSeconds;
                            Integer sendIntervalStart = emailPushMain.getSendIntervalMin();
                            Integer sendIntervalEnd = emailPushMain.getSendIntervalMax();

                            if (sendIntervalStart != null && sendIntervalEnd != null
                                    && sendIntervalStart <= sendIntervalEnd) {
                                long randomMinutes = sendIntervalStart
                                        + (long) (Math.random() * (sendIntervalEnd - sendIntervalStart + 1));
                                intervalSeconds = randomMinutes * 60;
                            } else {
                                long randomMinutes = 1 + (long) (Math.random() * 5);
                                intervalSeconds = randomMinutes * 60;
                            }
                            currentSendTime = new Date(currentSendTime.getTime() + intervalSeconds * 1000);
                        }
                    }

                    boolean detailUpdateResult = emailPushDetailService.updateBatchById(unsentDetails);
                    if (!detailUpdateResult) {
                        return Result.error("更新明细表失败");
                    }
                }
            }

            log.info("成功修改商务发信记录，主表ID：{}", emailPushMain.getId());
            return Result.ok(ExamConstants.EDIT_SUCCESS);
        } catch (Exception e) {
            log.error("编辑商务发信记录异常：", e);
            return Result.error("修改失败，系统异常");
        }
    }

    @AutoLog(value = "商务发信-批量导入邮件推送记录")
    @Operation(summary = "商务发信-批量导入邮件推送记录", description = "商务发信-批量导入邮件推送记录")
    @RequestMapping(value = "/businessBatchImport", method = RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class)
    public Result<?> businessBatchImport(HttpServletRequest request, EmailPushMain emailPushMain) {
        try {
            String userId = getUserIdByToken();
            String userName = getUserNameByToken();

            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            MultipartFile file = multipartRequest.getFile("file");
            if (file == null) {
                return Result.error("请选择要导入的文件");
            }

            ImportParams params = new ImportParams();
            params.setHeadRows(1);

            List<EmailPushDetail> emailPushDetails = ExcelImportUtil.importExcel(file.getInputStream(),
                    EmailPushDetail.class, params);

            List<EmailPushDetail> validRows = emailPushDetails.stream()
                    .filter(this::isNotEmptyBusinessRow)
                    .collect(Collectors.toList());

            if (validRows.isEmpty()) {
                return Result.error("导入失败，Excel文件中没有有效数据");
            }

            List<Map<String, Object>> errorList = new ArrayList<>();

            for (int i = 0; i < validRows.size(); i++) {
                EmailPushDetail detail = validRows.get(i);
                int rowNum = i + 2;

                if (oConvertUtils.isEmpty(detail.getBrandName())) {
                    addError(errorList, rowNum, "品牌名称不能为空");
                }

                if (oConvertUtils.isEmpty(detail.getEmail())) {
                    addError(errorList, rowNum, "品牌邮箱不能为空");
                }
                String email = detail.getEmail();
                if (email != null) {
                    // 只保留邮箱地址，去除所有特殊符号
                    email = email.replaceAll("[^a-zA-Z0-9@._-]", "");
                    detail.setEmail(email);
                }
                String sendEmail = detail.getSendEmail();
                if (sendEmail != null) {
                    // 只保留邮箱地址，去除所有特殊符号
                    sendEmail = sendEmail.replaceAll("[^a-zA-Z0-9@._-]", "");
                    detail.setSendEmail(sendEmail);
                }
            }

            if (!errorList.isEmpty()) {
                Result<?> errorResult = Result.error(500, "导入失败，存在错误数据", errorList);
                return errorResult;
            }

            emailPushMain.setBusinessType(1);

            Result<?> result = emailPushMainService.saveBusinessBatchImport(emailPushMain, validRows, userId, userName);
            if (!result.isSuccess()) {
                return result;
            }

            return result;
        } catch (Exception e) {
            log.error("商务批量导入邮件推送记录异常：", e);
            return Result.error("导入失败，系统异常");
        }
    }

    private boolean isNotEmptyBusinessRow(EmailPushDetail detail) {
        return oConvertUtils.isNotEmpty(detail.getBrandName()) || oConvertUtils.isNotEmpty(detail.getEmail());
    }

    private void addError(List<Map<String, Object>> errorList, int rowNum, String errorMsg) {
        Map<String, Object> error = new HashMap<>();
        error.put("rowNum", rowNum);
        error.put("errorMsg", errorMsg);
        errorList.add(error);
    }

    /**
     * 批量导入邮件推送记录（Excel）
     *
     * @param request
     * @param emailPushMain 邮件主表数据
     * @return
     */
    @AutoLog(value = "邮件消息主表-批量导入邮件推送记录")
    @Operation(summary = "邮件消息主表-批量导入邮件推送记录", description = "邮件消息主表-批量导入邮件推送记录")
    @RequestMapping(value = "/batchImport", method = RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class)
    public Result<?> batchImport(HttpServletRequest request, EmailPushMain emailPushMain) {
        try {
            String userId = getUserIdByToken();
            String userName = getUserNameByToken();

            // 获取上传的Excel文件
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            MultipartFile file = multipartRequest.getFile("file");
            if (file == null) {
                return Result.error("请选择要导入的文件");
            }

            // 定义导入参数
            ImportParams params = new ImportParams();
            params.setHeadRows(1);

            // 从Excel中读取EmailPushDetail数据
            List<EmailPushDetail> emailPushDetails = ExcelImportUtil.importExcel(file.getInputStream(),
                    EmailPushDetail.class, params);

            // 转换平台文字为数值
            for (EmailPushDetail detail : emailPushDetails) {
                if (detail != null) {
                    // 将platformTypeStr转换为platformType
                    convertPlatformTypeFromStr(detail);
                }
            }

            // 过滤掉完全空的行，只保留有数据的行
            List<EmailPushDetail> validRows = emailPushDetails.stream()
                    .filter(this::isNotEmptyRow)
                    .collect(Collectors.toList());

            if (validRows.isEmpty()) {
                return Result.error("导入失败，Excel文件中没有有效数据");
            }

            // ============ 收集所有错误：支持一行多错 ============
            List<Map<String, Object>> errorList = new ArrayList<>();

            for (int i = 0; i < validRows.size(); i++) {
                EmailPushDetail detail = validRows.get(i);
                int rowNum = i + 2; // Excel 第2行开始是数据

                // 每行一个错误容器
                List<String> rowErrors = new ArrayList<>();

                // 校验网红账号
                if (oConvertUtils.isEmpty(detail.getUsername())) {
                    rowErrors.add("网红账号不能为空");
                }
                detail.setAccount(detail.getUsername());

                // 校验网红邮箱
                if (oConvertUtils.isEmpty(detail.getEmail())) {
                    rowErrors.add("网红邮箱不能为空");
                } else if (!isValidEmail(detail.getEmail().trim())) {
                    rowErrors.add("网红邮箱格式不正确");
                }

                // 校验平台类型（必填项）
                if (detail.getPlatformType() == null) {
                    rowErrors.add("平台不能为空，请填写TK、YT或IG");
                } else {
                    // 校验平台类型是否有效（转换后应该已经是有效值）
                    Integer platformType = detail.getPlatformType();
                    if (platformType != 0 && platformType != 1 && platformType != 2) {
                        rowErrors.add("平台类型无效，只能填写：TK、YT或IG");
                    }
                }

                String username = "";
                String email = "";
                String platform = "";
                if (detail.getUsername() != null) {
                    username = detail.getUsername().trim();
                }
                if (detail.getEmail() != null) {
                    email = detail.getEmail().trim();
                }
                if (detail.getPlatformType() != null) {
                    // 将数字平台类型转换为文字显示
                    switch (detail.getPlatformType()) {
                        case 0:
                            platform = "IG";
                            break;
                        case 1:
                            platform = "YT";
                            break;
                        case 2:
                            platform = "TK";
                            break;
                        default:
                            platform = "无效平台(" + detail.getPlatformType() + ")";
                            break;
                    }
                }

                // 如果本行有任何错误，全部加入 errorList
                if (!rowErrors.isEmpty()) {
                    Map<String, Object> rowErrorMap = new HashMap<>();
                    rowErrorMap.put("row", rowNum);
                    rowErrorMap.put("errors", rowErrors); // 数组：支持多条
                    rowErrorMap.put("username", username);
                    rowErrorMap.put("email", email);
                    rowErrorMap.put("platform", platform);
                    errorList.add(rowErrorMap);
                }
            }

            // ============ 有错误：返回结构化 JSON ============
            if (!errorList.isEmpty()) {
                return Result.error(500, "导入校验失败，共 " + errorList.size() + " 行数据有错误", errorList);
            }

            // ============ 全部通过：保存 ============

            // 创建批量推送模型
            EmailBatchPushModel batchPushModel = new EmailBatchPushModel();

            // 设置主表字段 - 添加产品ID和相关信息
            batchPushModel.setSendEmail(emailPushMain.getSendEmail());
            batchPushModel.setSignatureId(emailPushMain.getSignatureId());
            batchPushModel.setContactEmailId(emailPushMain.getContactEmailId());
            batchPushModel.setCcEmails(emailPushMain.getCcEmails());
            batchPushModel.setRemark(emailPushMain.getRemark());
            batchPushModel.setEmailPushDate(emailPushMain.getEmailPushDate());
            batchPushModel.setProductId(emailPushMain.getProductId());
            batchPushModel.setProductName(emailPushMain.getProductName());
            batchPushModel.setBrandId(emailPushMain.getBrandId());
            batchPushModel.setBrandName(emailPushMain.getBrandName());
            batchPushModel.setSendIntervalMin(emailPushMain.getSendIntervalMin());
            batchPushModel.setSendIntervalMax(emailPushMain.getSendIntervalMax());
            batchPushModel.setSendType(1);
            // 设置明细列表
            batchPushModel.setEmailPushDetails(validRows);

            // 调用批量保存方法
            Result<?> result = emailPushMainService.saveBatchPush(batchPushModel, userId, userName);

            if (oConvertUtils.isEmpty(result.getMessage())) {
                return Result.ok("成功导入" + validRows.size() + "条邮件推送记录");
            } else {
                return result;
            }
        } catch (Exception e) {
            log.error("导入邮件推送记录失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 判断是否为非空行（至少有一个字段不为空）
     */
    private boolean isNotEmptyRow(EmailPushDetail detail) {
        return detail != null && (oConvertUtils.isNotEmpty(detail.getUsername()) ||
                oConvertUtils.isNotEmpty(detail.getEmail()) ||
                detail.getPlatformType() != null);
    }

    /**
     * 将platformTypeStr转换为platformType
     *
     * @param detail 邮件详情
     */
    private void convertPlatformTypeFromStr(EmailPushDetail detail) {
        try {
            // 直接获取platformTypeStr字段的值（现在是String类型）
            String platformTypeStrValue = detail.getPlatformTypeStr();

            if (oConvertUtils.isNotEmpty(platformTypeStrValue)) {
                String platformText = platformTypeStrValue.trim().toUpperCase();
                Integer platformType = convertPlatformTextToNumber(platformText);
                if (platformType != null) {
                    detail.setPlatformType(platformType);
                }
            }
        } catch (Exception e) {
            log.debug("转换platformTypeStr失败: " + e.getMessage());
            // 如果转换失败，保留原有的platformType值（可能已经是数值）
        }
    }

    /**
     * 将平台文字转换为数值
     *
     * @param platformText 平台文字（TK、YT、IG）
     * @return 平台数值（2、1、0）
     */
    private Integer convertPlatformTextToNumber(String platformText) {
        if (oConvertUtils.isEmpty(platformText)) {
            return null;
        }

        switch (platformText) {
            case "TK":
                return 2;
            case "YT":
                return 1;
            case "IG":
                return 0;
            default:
                return null;
        }
    }

    @AutoLog(value = "邮件消息主表-批量发送")
    @PostMapping(value = "/batchAdd")
    public Result<?> batchAdd(@RequestBody EmailBatchPushModel batchPushModel) {
        try {
            String userId = getUserIdByToken();
            String userName = getUserNameByToken();
            Integer platformType = batchPushModel.getPlatformType();
            if (platformType == null) {
                return Result.error("平台类型不能为空");
            }
            Result<?> result = emailPushMainService.saveBatchPush(batchPushModel, userId, userName);

            if (oConvertUtils.isEmpty(result.getMessage())) {
                return Result.ok("成功导入" + batchPushModel.getEmailPushDetails().size() + "条邮件推送记录");
            } else {
                return result;
            }
        } catch (Exception e) {
            log.error("添加邮件推送记录失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询主表明细信息列表
     *
     * @return
     */
    @AutoLog(value = "邮件消息主表-查询主表明细信息列表")
    @Operation(summary = "邮件消息主表-查询主表明细信息列表", description = "邮件消息主表-查询主表明细信息列表")
    @GetMapping(value = "/queryMainWithDetailsList")
    public Result<?> queryMainWithDetailsList() {
        List<EmailPushMainDetailVO> list = emailPushMainService.getMainWithDetailsList();
        return Result.ok(list);
    }

    /**
     * 严格校验邮箱格式
     */
    private boolean isValidEmail(String email) {
        if (email == null)
            return false;
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(regex);
    }

    @AutoLog(value = "邮件消息主表-批量取消发送")
    @GetMapping(value = "/batchCancel")
    @Transactional(rollbackFor = Exception.class)
    public Result<?> batchCancel(@RequestParam(name = "id", required = true) String id) {
        EmailPushMain pushMain = emailPushMainService.getById(id);
        if (pushMain.getIsSend() != 0) {
            return Result.error("取消失败，当前分组内存在已发送或正在发送的邮件，不能取消发送");
        }
        List<EmailPushDetail> list = emailPushDetailService.lambdaQuery().eq(EmailPushDetail::getMainId, id).list();
        boolean match = list.stream().anyMatch(x -> x.getIsSend() != 0 && x.getIsSend() != -2);
        if (match) {
            return Result.error("存在已发送或正在发送的邮件，不能取消发送");
        }
        emailPushDetailService.lambdaUpdate().set(EmailPushDetail::getIsSend, -2).eq(EmailPushDetail::getMainId, id)
                .update();
        emailPushMainService.lambdaUpdate().set(EmailPushMain::getIsSend, -2).eq(EmailPushMain::getId, id).update();

        // 删除已标记开发的网红列表
        kolContactService.deleteKolContactByEmailPushMainId(id);

        return Result.ok("取消发送成功");
    }

    /**
     * 重新发送邮件
     *
     * @param id 主表ID
     * @return
     */
    @AutoLog(value = "邮件消息主表-批量回复发送")
    @GetMapping(value = "/batchResend")
    @Transactional(rollbackFor = Exception.class)
    public Result<?> batchResend(@RequestParam(name = "id", required = true) String id) {
        EmailPushMain pushMain = emailPushMainService.getById(id);
        // 只有已取消发送(-2)的邮件才能重新发送
        if (pushMain.getIsSend() != -2) {
            return Result.error("只有已取消发送的邮件才能重新发送");
        }

        // 查询所有已取消发送的明细记录
        List<EmailPushDetail> list = emailPushDetailService.lambdaQuery()
                .eq(EmailPushDetail::getMainId, id)
                .eq(EmailPushDetail::getIsSend, -2)
                .list();

        // 如果没有已取消发送的明细，则提示无需重新发送
        if (list.isEmpty()) {
            return Result.error("没有已取消发送的邮件，无需重新发送");
        }

        // 将所有已取消发送的明细状态重置为未发送(0)
        emailPushDetailService.lambdaUpdate()
                .set(EmailPushDetail::getIsSend, 0)
                .eq(EmailPushDetail::getMainId, id)
                .eq(EmailPushDetail::getIsSend, -2)
                .update();

        // 更新主表状态为未发送(0)
        emailPushMainService.lambdaUpdate()
                .set(EmailPushMain::getIsSend, 0)
                .eq(EmailPushMain::getId, id)
                .update();

        // 重新创建网红联系人标记
        kolContactService.lambdaUpdate()
                .set(KolContact::getIsDelete, 0)
                .eq(KolContact::getEmailPushMainId, id)
                .update();
        return Result.ok("重新发送设置成功");
    }
}