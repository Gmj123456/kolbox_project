package org.jeecg.modules.instagram.storecelebrity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.constant.enums.MessageType;
import org.jeecg.common.constant.enums.YesNoStatus;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.storemail.entity.Emailaccount;
import org.jeecg.modules.instagram.storemail.service.IEmailaccountService;
import org.jeecg.modules.instagram.storecelebrity.entity.MessageTemplate;
import org.jeecg.modules.instagram.storecelebrity.mapper.MessageTemplateMapper;
import org.jeecg.modules.instagram.storecelebrity.model.MessageTemplateModel;
import org.jeecg.modules.instagram.storecelebrity.model.PushCommonModel;
import org.jeecg.modules.instagram.storecelebrity.model.PushUser;
import org.jeecg.modules.instagram.storecelebrity.entity.MessagePushDetail;

import org.jeecg.modules.instagram.storecelebrity.service.IMessageTemplateService;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrity;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityService;
import org.jeecg.modules.instagram.utils.EmojiCharUtils;
import org.jeecg.modules.system.service.ISysDictItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @Description: 消息发送模板
 * @Author: jeecg-boot
 * @Date: 2020-05-11
 * @Version: V1.0
 */
@Service
@Slf4j
public class MessageTemplateServiceImpl extends ServiceImpl<MessageTemplateMapper, MessageTemplate> implements IMessageTemplateService {


    @Autowired
    private IEmailaccountService emailaccountService;

    @Autowired
    private IStoreCelebrityService storeCelebrityService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ISysDictItemService dictItemService;

    @Autowired
    MessageTemplateMapper messageTemplateMapper;


    /**
     * 功能描述: 替换模板中占位符
     *
     * @Date: 2020/1/19 14:39
     */
    @Override
    @Transactional
    public MessagePushDetail pushEmailMessage(PushCommonModel pushCommonModel, PushUser pushUser) {
        MessagePushDetail messagePushDetail = null;
        //查询模板
        LambdaQueryWrapper<MessageTemplate> mesWrapper = new LambdaQueryWrapper<>();
        mesWrapper.eq(MessageTemplate::getIsEnabled, 1);
        mesWrapper.eq(MessageTemplate::getId, pushCommonModel.getTemplate());
        MessageTemplate messageTemplate = this.getOne(mesWrapper);
        if (messageTemplate != null) {
            pushCommonModel.setTitle(messageTemplate.getTemplateTitle());
        } else {
            System.err.println("messageType:" + MessageType.BUSINESS_CONSULTING.getInfo() + " 模板类型为空");
            return messagePushDetail;
        }
        // 获取对象属性,替换
        try {
            Field[] fields = PushUser.class.getDeclaredFields();
            for (Field field : fields) {
                // 私有属性必须设置访问权限
                field.setAccessible(true);
                String name = field.getName();
                Object resultValue = field.get(pushUser);
                String text = "${" + name + "}";
                //批量替换属性值
                if (resultValue == null) {
                    continue;
                }
                resultValue = EmojiCharUtils.replaceEmoji(String.valueOf(resultValue));
                String title = messageTemplate.getTemplateTitle().replace(text, String.valueOf(resultValue));
                String content = messageTemplate.getTemplateContent().replace(text, String.valueOf(resultValue));
                String richContent = messageTemplate.getTemplateRichContent().replace(text, String.valueOf(resultValue));

                messageTemplate.setTemplateTitle(title);
                messageTemplate.setTemplateContent(content);
                messageTemplate.setTemplateRichContent(richContent);
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return messagePushDetail;
        }
        //获取发送账号
        String accountId = pushCommonModel.getAccountId();
        LambdaQueryWrapper<Emailaccount> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Emailaccount::getId, accountId);
        Emailaccount emailaccount = emailaccountService.getOne(lambdaQueryWrapper);
        String account = emailaccount.getAccountName();
        String password = emailaccount.getAccountAppPassword();
        //发送的账号密码，接受的账号，模板标题，模板内容
        try {
            StoreCelebrity storeCelebrity = storeCelebrityService.getById(pushUser.getKolId());
            //添加到邮件发送表
            String sendTime = pushCommonModel.getSendTime();
            messagePushDetail = addSendEmail(pushUser.getSellerId(), pushUser.getSellerName(), account, password, pushUser.getKolId(), storeCelebrity.getNickName(), pushUser.getKolEmail(), pushUser.getReamrk(), sendTime, messageTemplate, pushCommonModel.getPackageId(), pushCommonModel.getPackageName(), pushCommonModel.getPackagePurchaseId());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return messagePushDetail;
        }
        return messagePushDetail;

    }

    @Override
    public IPage<MessageTemplate> sellerCounselorPage(Page<MessageTemplate> page, MessageTemplateModel messageTemplateModel) {
        return messageTemplateMapper.sellerCounselorPage(page,messageTemplateModel);
    }


    /**
     * 功能描述:添加到邮件发送表中
     *
     * @Author: nqr
     * @Date 2020-05-13 14:19:32
     */
    private MessagePushDetail addSendEmail(String sellerId, String sellerName, String account, String password, String kolId, String kolName, String kolEmail, String remark, String sendEmailTime, MessageTemplate messageTemplate, String packageId, String packageName, String packagePurchaseId) {
        String templateTitle = messageTemplate.getTemplateTitle();
//        String templateContent = messageTemplate.getTemplateContent();
        String templateContent = messageTemplate.getTemplateRichContent();
        MessagePushDetail pushDetail = new MessagePushDetail();
        String id = UUID.randomUUID().toString().replace("-", "");
        pushDetail.setId(id);
        pushDetail.setSellerId(sellerId);
        pushDetail.setSellerName(sellerName);
        pushDetail.setSendEmail(account);
        pushDetail.setSendEmailPassword(password);
        //发送时间
  /*      Date parseDate = null;
        Date date = new Date();
        try {
            parseDate = DateUtils.parseDate(sendEmailTime, DateUtils.formatStyle);
            if (parseDate.before(date)) {
                parseDate = date;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }*/
        String emailContent = EmojiCharUtils.transform(templateContent);
        String sendDate = (String) redisUtil.get(sellerId + "_" + kolId);
        if(oConvertUtils.isEmpty(sendDate)){
            sendDate = DateUtils.date2Str(new Date(),new SimpleDateFormat("yyyy-MM-dd"));
        }
        pushDetail.setEmailPushDate(sendDate);
        pushDetail.setEmailTitle(templateTitle);
        pushDetail.setKolId(kolId);
        pushDetail.setKolName(kolName);
        pushDetail.setKolEmail(kolEmail);
        pushDetail.setEmailContent(emailContent);
        pushDetail.setMessageTelType(messageTemplate.getId());
        //待发送
        pushDetail.setIsSend(0);
        pushDetail.setRemark(remark);
        pushDetail.setPackageId(packageId);
        pushDetail.setPackageName(packageName);
        pushDetail.setPackagePurchaseId(packagePurchaseId);
        pushDetail.setIsConfirm(YesNoStatus.NO.getCode());
        pushDetail.setCreateTime(new Date());
        return pushDetail;
    }
}
