package org.jeecg.modules.email.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.StoreUserContactEmailVo;
import org.jeecg.common.util.oConvertUtils;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.email.entity.EmailPushMain;
import org.jeecg.modules.email.entity.EmailPushDetail;
import org.jeecg.modules.email.entity.EmailTemplate;
import org.jeecg.modules.email.entity.EmailTemplateBusiness;
import org.jeecg.modules.email.entity.StoreUserContactEmailSignature;
import org.jeecg.modules.email.service.*;
import org.jeecg.modules.kol.entity.KolProduct;
import org.jeecg.modules.email.mapper.EmailPushMainMapper;
import org.jeecg.modules.email.model.EmailBatchPushModel;
import org.jeecg.modules.email.model.EmailPushMainDetailVO;
import org.jeecg.modules.kol.service.IKolProductService;
import org.jeecg.modules.kol.service.IKolContactService;
import org.jeecg.modules.kol.model.KolBrandModel;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.annotation.Resource;

/**
 * @Description: 邮件消息主表
 * @Author: jeecgboot
 * @Date: 2025-11-07
 * @Version: V1.0
 */
@Slf4j
@Service
public class EmailPushMainServiceImpl extends ServiceImpl<EmailPushMainMapper, EmailPushMain>
        implements IEmailPushMainService {

    @Autowired
    @Lazy
    private IEmailPushDetailService emailPushDetailService;
    @Resource
    private IEmailTemplateService templateService;
    @Resource
    private IEmailTemplateBusinessService emailTemplateBusinessService;
    @Resource
    private IStoreUserContactEmailSignatureService signatureService;
    @Resource
    private IKolProductService kolProductService;
    @Resource
    private IKolContactService kolContactService;
    @Resource
    private ISysBaseAPI sysBaseAPI;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> saveBatchPush(EmailBatchPushModel batchPushModel, String userId, String userName) {
        // 获取公共参数
        String sendEmail = batchPushModel.getSendEmail();
        Date emailPushDate = batchPushModel.getEmailPushDate();
        String signatureId = batchPushModel.getSignatureId();
        String contactEmailId = batchPushModel.getContactEmailId();
        String ccEmails = batchPushModel.getCcEmails();
        String remark = batchPushModel.getRemark();
        String productId = batchPushModel.getProductId();
        Integer platformType = batchPushModel.getPlatformType();
        String platformTypeName = "";
        if (oConvertUtils.isNotEmpty(platformType)) {
            platformTypeName = platformType == 1 ? "YT" : platformType == 2 ? "TK" : "IG";
        }
        // 1) 验证产品是否存在
        if (oConvertUtils.isNotEmpty(productId)) {
            KolProduct product = kolProductService.getById(productId);
            if (product == null) {
                return Result.error("产品不存在，请检查产品ID");
            }
            batchPushModel.setProductName(product.getProductName());
            batchPushModel.setBrandName(product.getBrandName());
        }

        // 获取发送时间间隔参数
        Integer sendIntervalMin = batchPushModel.getSendIntervalMin();
        Integer sendIntervalMax = batchPushModel.getSendIntervalMax();

        List<EmailTemplate> availableTemplates = new ArrayList<>();

        // 2) 根据产品查询网红顾问对应的邮件模版列表
        if (oConvertUtils.isNotEmpty(productId)) {
            LambdaQueryWrapper<EmailTemplate> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(EmailTemplate::getProductId, productId);
            if (oConvertUtils.isNotEmpty(platformType)) {
                queryWrapper.eq(EmailTemplate::getPlatformType, platformType);
            }
            queryWrapper.eq(EmailTemplate::getCounselorId, userId);
            queryWrapper.orderByAsc(EmailTemplate::getSortCode);
            availableTemplates = templateService.list(queryWrapper);
        }

        if (oConvertUtils.isNotEmpty(productId) && availableTemplates.isEmpty()) {
            return Result.error(String.format("该产品没有%s平台的邮件模板，请先创建邮件模板", platformTypeName));
        }

        // 获取明细列表
        List<EmailPushDetail> emailPushDetails = batchPushModel.getEmailPushDetails();
        boolean match = emailPushDetails.stream().anyMatch(x -> oConvertUtils.isEmpty(x.getEmail()));
        if (match) {
            return Result.error("存在未填写邮箱的邮件明细，请检查");
        }

        // 查询签名
        String signatureTitle = "";
        String signatureContent = "";
        if (oConvertUtils.isNotEmpty(signatureId)) {
            StoreUserContactEmailSignature signature = signatureService.getById(signatureId);
            if (signature != null) {
                signatureTitle = signature.getSignatureTitle();
                signatureContent = signature.getSignatureContentOriginal();
            }
        }

        // 查询建联邮箱信息
        StoreUserContactEmailVo contactEmailVo = sysBaseAPI.getContactEmailById(contactEmailId);
        if (contactEmailVo == null) {
            return Result.error("未获取到发送邮箱相关数据，请检查是否正确");
        }
        // 2025年12月17日10:08:03 增加邮箱密码，适配企业邮箱smtp发送邮件逻辑
        String emailPwd = contactEmailVo.getEmailPassword();

        // 创建主表记录
        EmailPushMain emailPushMain = new EmailPushMain();
        emailPushMain.setSendEmail(sendEmail);
        emailPushMain.setSignatureId(signatureId);
        emailPushMain.setSignatureTitle(signatureTitle);
        emailPushMain.setSignatureContent(signatureContent);
        emailPushMain.setContactEmailId(contactEmailId);
        emailPushMain.setCcEmails(ccEmails);
        emailPushMain.setRemark(remark);
        emailPushMain.setCounselorId(userId);
        emailPushMain.setCounselorName(userName);
        emailPushMain.setIsSend(0); // 未发送状态
        emailPushMain.setBrandId(batchPushModel.getBrandId());
        emailPushMain.setBrandName(batchPushModel.getBrandName());
        emailPushMain.setProductId(productId);
        emailPushMain.setProductName(batchPushModel.getProductName());
        emailPushMain.setSendIntervalMin(sendIntervalMin);
        emailPushMain.setSendIntervalMax(sendIntervalMax);
        emailPushMain.setSendType(batchPushModel.getSendType());
        emailPushMain.setPlatformType(platformType);
        emailPushMain.setEmailPassword(emailPwd);
        // 设置发送时间
        if (emailPushDate != null) {
            emailPushMain.setEmailPushDate(emailPushDate);
        }
        // 保存主表记录
        this.save(emailPushMain);

        // 批量创建明细记录
        if (emailPushDetails != null && !emailPushDetails.isEmpty()) {
            // 如果设置了发送时间，则为每个明细设置随机的发送时间
            Date currentSendTime = null;
            if (emailPushDate != null) {
                currentSendTime = emailPushDate;
            }

            Random random = new Random();
            for (EmailPushDetail detail : emailPushDetails) {
                // 设置主表ID关联
                detail.setMainId(emailPushMain.getId());
                String email = detail.getEmail();
                detail.setEmail(email.split(",")[0]);
                // 替换邮件内容中的{name}变量为网红昵称
                String username = detail.getUsername();
                String nickname = detail.getNickname();
                // 为每个网红随机选择一个模板
                EmailTemplate selectedTemplate = null;
                String currentTemplateId = "";
                String personalizedContent = "";
                Integer type = detail.getPlatformType();
                platformTypeName = type == 1 ? "YT" : type == 2 ? "TK" : "IG";
                // 从可用模板中随机选择一个
                List<EmailTemplate> templates = availableTemplates.stream()
                        .filter(x -> x.getPlatformType().equals(type)).collect(Collectors.toList());
                if (templates.isEmpty()) {
                    return Result.error(String.format("该产品没有%s平台的邮件模板，请先创建邮件模板", platformTypeName));
                }
                selectedTemplate = templates.get(random.nextInt(templates.size()));
                currentTemplateId = selectedTemplate.getId();
                personalizedContent = selectedTemplate.getTemplateContentOriginal();

                String replacement = oConvertUtils.isNotEmpty(username) ? username
                        : oConvertUtils.isNotEmpty(nickname) ? nickname : "";

                // 获取随机模版中的邮件标题
                String templateTitle = selectedTemplate.getTemplateTitle();
                templateTitle = templateTitle.replace("{{name}}", replacement);
                personalizedContent = personalizedContent.replace("{{name}}", replacement);

                // 设置公共字段
                detail.setEmailTitle(templateTitle);
                detail.setSendEmail(sendEmail);
                detail.setEmailPassword(emailPwd);
                detail.setTemplateId(currentTemplateId);
                detail.setSignatureId(signatureId);
                detail.setSignatureTitle(signatureTitle);
                detail.setSignatureContent(signatureContent);
                detail.setContactEmailId(contactEmailId);
                detail.setCcEmails(ccEmails);
                detail.setRemark(remark);
                detail.setCounselorId(userId);
                detail.setCounselorName(userName);
                detail.setIsSend(0); // 未发送状态

                if (personalizedContent.contains("{{emailDisplayName}}")) {
                    personalizedContent = personalizedContent.replace("{{emailDisplayName}}",
                            contactEmailVo.getEmailDisplayName());
                }

                detail.setEmailContent(personalizedContent);

                // 设置发送时间
                if (emailPushDate != null) {
                    detail.setPlanSendTime(currentSendTime);
                    // 根据sendIntervalStart和sendIntervalEnd设置下一个邮件的发送时间
                    // 如果没有设置间隔参数，则使用默认的1-5分钟
                    long intervalSeconds;
                    if (sendIntervalMin != null && sendIntervalMax != null && sendIntervalMin <= sendIntervalMax) {
                        // 在指定的分钟区间内随机选择一个值（转换为毫秒）
                        long randomMinutes = sendIntervalMin
                                + (long) (Math.random() * (sendIntervalMax - sendIntervalMin + 1));
                        intervalSeconds = randomMinutes * 60;
                    } else {
                        // 默认使用1-5分钟
                        long randomMinutes = 1 + (long) (Math.random() * 5);
                        intervalSeconds = randomMinutes * 60;
                    }
                    currentSendTime = new Date(currentSendTime.getTime() + intervalSeconds * 1000);
                }
            }

            // 批量保存明细记录
            emailPushDetailService.saveBatch(emailPushDetails);
        }

        // 调用标记开发功能（在同一个事务中执行）
        try {
            KolBrandModel kolBrandModel = buildKolBrandModel(batchPushModel, emailPushDetails);
            if (kolBrandModel != null) {
                kolContactService.saveKolContact(kolBrandModel);
            }
        } catch (Exception markDevException) {
            // 标记开发失败不影响邮件推送记录的创建，只记录日志
            log.warn("调用标记开发功能失败，但不影响邮件推送记录的创建: {}", markDevException.getMessage());
        }

        return Result.ok();
    }

    @Override
    public List<EmailPushMainDetailVO> getMainWithDetailsList() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        LambdaQueryChainWrapper<EmailPushMain> lambdaQuery = lambdaQuery();
        if (!oConvertUtils.checkUserType(sysUser.getUsername())) {
            lambdaQuery.eq(EmailPushMain::getCounselorId, sysUser.getId());
        }
        // 查询所有主表信息
        List<EmailPushMain> mainList = lambdaQuery.list();

        // 查询所有明细表信息
        List<EmailPushDetail> detailList = emailPushDetailService.list();

        // 将明细表信息按mainId分组
        Map<String, List<EmailPushDetail>> detailMap = detailList.stream()
                .collect(Collectors.groupingBy(EmailPushDetail::getMainId));

        // 封装VO列表
        return mainList.stream().map(main -> {
            EmailPushMainDetailVO vo = new EmailPushMainDetailVO();
            vo.setMain(main);
            vo.setDetails(detailMap.getOrDefault(main.getId(), Collections.emptyList()));
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public IPage<EmailPushMain> pageList(Page<EmailPushMain> page, EmailPushMain emailPushMain) {
        return baseMapper.pageList(page, emailPushMain);
    }

    /**
     * 构建网红品牌开发模型
     *
     * @param batchPushModel   批量推送模型
     * @param emailPushDetails 邮件推送明细列表
     * @return KolBrandModel 网红品牌开发模型，如果参数不完整则返回null
     */
    private KolBrandModel buildKolBrandModel(EmailBatchPushModel batchPushModel,
            List<EmailPushDetail> emailPushDetails) {
        String productId = batchPushModel.getProductId();
        String brandId = batchPushModel.getBrandId();

        if (oConvertUtils.isEmpty(productId) || oConvertUtils.isEmpty(brandId) || emailPushDetails == null) {
            return null;
        }

        // 构造KolBrandModel参数
        KolBrandModel kolBrandModel = new KolBrandModel();
        kolBrandModel.setProductId(productId);
        kolBrandModel.setBrandId(brandId);

        // 构造网红列表
        List<KolBrandModel.KolSimpleModel> kolList = new ArrayList<>();
        for (EmailPushDetail detail : emailPushDetails) {
            if (oConvertUtils.isNotEmpty(detail.getCelebrityId())) {
                KolBrandModel.KolSimpleModel kol = new KolBrandModel.KolSimpleModel();
                kol.setId(detail.getCelebrityId());
                kol.setKolEmail(detail.getEmail());
                kolList.add(kol);
            }
        }
        kolBrandModel.setPlatformType(emailPushDetails.get(0).getPlatformType());
        kolBrandModel.setEmailPushMainId(emailPushDetails.get(0).getMainId());
        kolBrandModel.setKolList(kolList);
        return kolBrandModel;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> saveBusinessBatchImport(EmailPushMain emailPushMain, List<EmailPushDetail> emailPushDetails,
            String userId, String userName) {
        if (emailPushDetails == null || emailPushDetails.isEmpty()) {
            return Result.error("导入失败，Excel文件中没有有效数据");
        }

        String signatureId = emailPushMain.getSignatureId();
        String contactEmailId = emailPushMain.getContactEmailId();
        Date emailPushDate = emailPushMain.getEmailPushDate();
        Integer sendIntervalMin = emailPushMain.getSendIntervalMin();
        Integer sendIntervalMax = emailPushMain.getSendIntervalMax();

        String signatureTitle = "";
        String signatureContent = "";
        if (oConvertUtils.isNotEmpty(signatureId)) {
            StoreUserContactEmailSignature signature = signatureService.getById(signatureId);
            if (signature != null) {
                signatureTitle = signature.getSignatureTitle();
                signatureContent = signature.getSignatureContentOriginal();
            }
        }

        StoreUserContactEmailVo contactEmailVo = sysBaseAPI.getContactEmailById(contactEmailId);
        if (contactEmailVo == null) {
            return Result.error("未获取到发送邮箱相关数据，请检查是否正确");
        }
        String emailPwd = contactEmailVo.getEmailPassword();
        String stationeryId = contactEmailVo.getStationeryId();

        emailPushMain.setCounselorId(userId);
        emailPushMain.setCounselorName(userName);
        emailPushMain.setIsSend(0);
        emailPushMain.setBusinessType(1);
        emailPushMain.setSignatureTitle(signatureTitle);
        emailPushMain.setSignatureContent(signatureContent);
        emailPushMain.setEmailPassword(emailPwd);
        emailPushMain.setSendType(1);

        this.save(emailPushMain);

        Date currentSendTime = null;
        if (emailPushDate != null) {
            currentSendTime = emailPushDate;
        }

        Random random = new Random();
        for (EmailPushDetail detail : emailPushDetails) {
            detail.setMainId(emailPushMain.getId());
            detail.setSendEmail(emailPushMain.getSendEmail());
            detail.setEmailPassword(emailPwd);
            detail.setSignatureId(signatureId);
            detail.setSignatureTitle(signatureTitle);
            detail.setSignatureContent(signatureContent);
            detail.setContactEmailId(contactEmailId);
            detail.setCcEmails(emailPushMain.getCcEmails());
            detail.setRemark(emailPushMain.getRemark());
            detail.setCounselorId(userId);
            detail.setCounselorName(userName);
            detail.setIsSend(0);

            List<EmailTemplateBusiness> availableTemplates = new ArrayList<>();

            if (oConvertUtils.isNotEmpty(detail.getBrandCategory())) {
                LambdaQueryWrapper<EmailTemplateBusiness> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(EmailTemplateBusiness::getBrandCategory, detail.getBrandCategory());
                queryWrapper.eq(EmailTemplateBusiness::getCounselorId, userId);
                queryWrapper.orderByAsc(EmailTemplateBusiness::getSortCode);
                availableTemplates = emailTemplateBusinessService.list(queryWrapper);
            }

            if (availableTemplates.isEmpty()) {
                if(oConvertUtils.isEmpty(detail.getBrandCategory())){
                    return Result.error("未获取到品牌类目对应的邮件模板，请先创建邮件模板");
                }
                return Result.error("品牌类目【" + detail.getBrandCategory() + "】没有对应的邮件模板，请先创建邮件模板");
            }

            EmailTemplateBusiness selectedTemplate = availableTemplates.get(random.nextInt(availableTemplates.size()));
            detail.setTemplateId(selectedTemplate.getId());
            detail.setBrandCategoryId(selectedTemplate.getBrandCategoryId());
            String templateTitle = selectedTemplate.getTemplateTitle();
            String personalizedContent = selectedTemplate.getTemplateContentOriginal();
            String brandName = detail.getBrandName();

            if (personalizedContent.contains("{{brandName}}")) {
                personalizedContent = personalizedContent.replace("{{brandName}}", brandName);
            }

            if (personalizedContent.contains("{{emailDisplayName}}")) {
                personalizedContent = personalizedContent.replace("{{emailDisplayName}}",
                        contactEmailVo.getEmailDisplayName());
            }

          /*  if (oConvertUtils.isNotEmpty(signatureContent)) {
                personalizedContent = personalizedContent + "<br/><br/>" + signatureContent;
            }*/

            detail.setEmailTitle(templateTitle);

            // 2026年2月11日11:52:02 增加是否设置信纸判断  nqr
            if (oConvertUtils.isNotEmpty(stationeryId)) {
                String content = contactEmailVo.getStationeryImageCode()
                        .replace("{{emailContent}}", personalizedContent)
                        // 匹配各种常见的白色背景写法，并移除整条 background-color 属性
                        .replaceAll("(?i)background-color\\s*:\\s*(?:#fff(?:fff)?|rgb\\s*\\(\\s*255\\s*,\\s*255\\s*,\\s*255\\s*\\)|rgba\\s*\\(\\s*255\\s*,\\s*255\\s*,\\s*255\\s*,\\s*[0-9.]+\\s*\\)|white)\\s*;?\\s*", "");
                detail.setEmailContent(content);
            }else{
                detail.setEmailContent(personalizedContent);
            }

            if (emailPushDate != null) {
                detail.setPlanSendTime(currentSendTime);

                long intervalSeconds;
                if (sendIntervalMin != null && sendIntervalMax != null && sendIntervalMin <= sendIntervalMax) {
                    long randomMinutes = sendIntervalMin
                            + (long) (Math.random() * (sendIntervalMax - sendIntervalMin + 1));
                    intervalSeconds = randomMinutes * 60;
                } else {
                    long randomMinutes = 1 + (long) (Math.random() * 5);
                    intervalSeconds = randomMinutes * 60;
                }
                currentSendTime = new Date(currentSendTime.getTime() + intervalSeconds * 1000);
            }
        }

        emailPushDetailService.saveBatch(emailPushDetails);

        return Result.ok("导入成功，共生成 " + emailPushDetails.size() + " 封邮件");
    }
}