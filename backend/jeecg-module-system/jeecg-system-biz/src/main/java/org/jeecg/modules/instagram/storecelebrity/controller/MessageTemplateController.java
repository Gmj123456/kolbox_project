package org.jeecg.modules.instagram.storecelebrity.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.enums.Coefficient;
import org.jeecg.common.constant.enums.DictCode;
import org.jeecg.common.constant.enums.UserType;
import org.jeecg.common.constant.enums.YesNoStatus;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.util.PageUtil;
import org.jeecg.common.util.RestUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.storemail.entity.DomainAccount;
import org.jeecg.modules.instagram.storemail.service.IDomainAccountService;
import org.jeecg.modules.instagram.storemail.entity.Emailaccount;
import org.jeecg.modules.instagram.storemail.service.IEmailaccountService;
import org.jeecg.modules.instagram.storecelebrity.entity.MessageTemplate;
import org.jeecg.modules.instagram.storecelebrity.model.MessageTemplateModel;
import org.jeecg.modules.instagram.storecelebrity.model.PushUser;
import org.jeecg.modules.instagram.storecelebrity.model.StringSimilarityInterfaceModel;
import org.jeecg.modules.instagram.storecelebrity.service.IMessageTemplateService;
import org.jeecg.modules.instagram.storecelebrity.utils.SingleSendMail;
import org.jeecg.modules.instagram.storepromotion.entity.StoreSensitiveWord;
import org.jeecg.modules.instagram.storepromotion.service.IStoreSensitiveWordService;
import org.jeecg.modules.instagram.utils.ToolHttpClient;
import org.jeecg.modules.system.entity.SysDictItem;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysDictItemService;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Description: 邮件推送模板
 * @Author: jeecg-boot
 * @Date: 2020-05-11
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "邮件推送模板")
@RestController
@RequestMapping("/instagram/messagetemplate")
public class MessageTemplateController extends JeecgController<MessageTemplate, IMessageTemplateService> {
    @Autowired
    private IMessageTemplateService messageTemplateService;

    @Autowired
    private IEmailaccountService emailaccountService;

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ISysDictService sysDictService;

    @Autowired
    private IStoreSensitiveWordService storeSensitiveWordService;

    @Autowired
    private ISysDictItemService dictItemService;

    @Autowired
    private IDomainAccountService domainAccountService;

    @Autowired
    private ISysDictService dictService;

    /**
     * 分页查询商家顾问下所有商家的模板
     */

    @AutoLog(value = "邮件推送模板-分页查询商家的模板")
@Operation(summary = "邮件推送模板-分页查询商家的模板", description = "邮件推送模板-分页查询商家的模板")
    @GetMapping(value = "/sellerCounselorTemplate")
    public Result<?> sellerCounselorTemplate(MessageTemplateModel messageTemplateModel,
                                             @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                             @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                             HttpServletRequest req) {

        Page<MessageTemplate> page = new Page<>(pageNo, pageSize);
        IPage<MessageTemplate> messageTemplateIPage = messageTemplateService.sellerCounselorPage(page, messageTemplateModel);

        return Result.ok(messageTemplateIPage);
    }


    /**
     * 分页列表查询
     *
     * @param messageTemplate
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "邮件推送模板-分页列表查询")
@Operation(summary = "邮件推送模板-分页列表查询", description = "邮件推送模板-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(MessageTemplate messageTemplate,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        String userId = getUserIdByToken();
        String userName = getUserNameByToken();

        LambdaQueryWrapper<MessageTemplate> lambdaQueryWrapper = new LambdaQueryWrapper<MessageTemplate>();
        //查询模板删除状态 0 未删除 1 已删除


        if (!"admin".equals(userName)) {
            lambdaQueryWrapper.and(i -> i.eq(MessageTemplate::getSellerId, userId).or().eq(MessageTemplate::getIsPublic, YesNoStatus.YES.getCode()));
            lambdaQueryWrapper.orderByDesc(MessageTemplate::getIsPublic);
            lambdaQueryWrapper.orderByDesc(MessageTemplate::getCreateTime);
            lambdaQueryWrapper.eq(MessageTemplate::getIsDelete, YesNoStatus.NO.getCode());
        }
        //查询标题
        if (StringUtils.isNotEmpty(messageTemplate.getTemplateTitle())) {
            lambdaQueryWrapper.like(MessageTemplate::getTemplateTitle, messageTemplate.getTemplateTitle());
        }

        if (StringUtils.isNotEmpty(messageTemplate.getTemplateName())) {
            lambdaQueryWrapper.like(MessageTemplate::getTemplateName, messageTemplate.getTemplateName());
        }

        IPage<MessageTemplate> pageList = messageTemplateService.page(PageUtil.getOrderItems(pageNo, pageSize, req), lambdaQueryWrapper);
        return Result.ok(pageList);
    }


    /**
     * 分页列表查询
     *
     * @param messageTemplate
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "邮件推送模板-分页列表查询")
@Operation(summary = "邮件推送模板-分页列表查询", description = "邮件推送模板-分页列表查询")
    @GetMapping(value = "/approvedList")
    public Result<?> approvedList(MessageTemplate messageTemplate,
                                  @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                  HttpServletRequest req) {
        String userId = getUserIdByToken();
        String userName = getUserNameByToken();

        LambdaQueryWrapper<MessageTemplate> lambdaQueryWrapper = new LambdaQueryWrapper<MessageTemplate>();
        lambdaQueryWrapper.eq(MessageTemplate::getIsPublic, YesNoStatus.NO.getCode());


        //查询标题
        if (StringUtils.isNotEmpty(messageTemplate.getTemplateTitle())) {
            lambdaQueryWrapper.like(MessageTemplate::getTemplateTitle, messageTemplate.getTemplateTitle());
        }

        if (StringUtils.isNotEmpty(messageTemplate.getTemplateName())) {
            lambdaQueryWrapper.like(MessageTemplate::getTemplateName, messageTemplate.getTemplateName());
        }
        if (StringUtils.isNotEmpty(messageTemplate.getSellerName())) {
            lambdaQueryWrapper.like(MessageTemplate::getSellerName, messageTemplate.getSellerName());
        }
        if (null != messageTemplate.getApprovedStatus()) {
            lambdaQueryWrapper.eq(MessageTemplate::getApprovedStatus, messageTemplate.getApprovedStatus());
        }
        Page<MessageTemplate> page = new Page<>(pageNo, pageSize);
        IPage<MessageTemplate> pageList = messageTemplateService.page(PageUtil.getOrderItems(pageNo, pageSize, req), lambdaQueryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 商品审核
     *
     * @param messageTemplate
     * @return
     */
    @AutoLog(value = "邮件推送模板-模板审核")
@Operation(summary = "邮件推送模板-模板审核", description = "邮件推送模板-模板审核")
    @PostMapping(value = "/approved")
    public Result<?> approved(@RequestBody MessageTemplate messageTemplate) {
        String userId = getUserIdByToken();
        String message = "";
        Integer approvedStatus = messageTemplate.getApprovedStatus();
        //审核成功上架状态
        if (messageTemplate.getApprovedStatus().equals(YesNoStatus.YES.getCode())) {
            messageTemplate.setApprovedFailReason("");
        }
        messageTemplate.setApprovedUserId(userId);
        messageTemplate.setApprovedDate(new Date());
        messageTemplateService.updateById(messageTemplate);
        if (approvedStatus.equals(YesNoStatus.YES.getCode())) {
            message = "审核成功!";
        } else {
            message = "驳回成功!";
        }
        return Result.ok(message);
    }

    /**
     * 公共分页列表查询
     *
     * @param messageTemplate
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "邮件推送模板-公共分页列表查询")
@Operation(summary = "邮件推送模板-公共分页列表查询", description = "邮件推送模板-公共分页列表查询")
    @GetMapping(value = "/publicList")
    public Result<?> queryPublicPageList(MessageTemplate messageTemplate,
                                         @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                         HttpServletRequest req) {
        String userId = getUserIdByToken();
        String userName = getUserNameByToken();

       /* QueryWrapper<MessageTemplate> queryWrapper = QueryGenerator.initQueryWrapper(messageTemplate, req.getParameterMap());
        if (!"admin".equals(userName)) {
            queryWrapper.eq("seller_id", userId);
        }*/


        LambdaQueryWrapper<MessageTemplate> lambdaQueryWrapper = new LambdaQueryWrapper<MessageTemplate>();
        if (StringUtils.isNotEmpty(messageTemplate.getTemplateTitle())) {
            lambdaQueryWrapper.like(MessageTemplate::getTemplateTitle, messageTemplate.getTemplateTitle());
        }
        //查询标题
        if (StringUtils.isNotEmpty(messageTemplate.getTemplateTitle())) {
            lambdaQueryWrapper.like(MessageTemplate::getTemplateTitle, messageTemplate.getTemplateTitle());
        }

        if (StringUtils.isNotEmpty(messageTemplate.getTemplateName())) {
            lambdaQueryWrapper.like(MessageTemplate::getTemplateName, messageTemplate.getTemplateName());
        }
        lambdaQueryWrapper.eq(MessageTemplate::getIsPublic, YesNoStatus.YES.getCode());


        Page<MessageTemplate> page = new Page<>(pageNo, pageSize);
        IPage<MessageTemplate> pageList = messageTemplateService.page(page, lambdaQueryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 功能描述:邮件推送模板
     *
     * @Author: nqr
     * @Date 2020-11-30 10:08:18
     */
    @AutoLog(value = "邮件推送模板")
@Operation(summary = "邮件推送模板", description = "邮件推送模板")
    @GetMapping(value = "/emailPushTemplateList")
    public Result<?> emailPushTemplateList() {
        LambdaQueryWrapper<MessageTemplate> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(MessageTemplate::getIsPublic, YesNoStatus.NO.getCode());
        lambdaQueryWrapper.eq(MessageTemplate::getApprovedStatus, YesNoStatus.YES.getCode());
        lambdaQueryWrapper.eq(MessageTemplate::getIsEnabled, YesNoStatus.YES.getCode());
        lambdaQueryWrapper.eq(MessageTemplate::getIsDelete, YesNoStatus.NO.getCode());
        if (!"admin".equals(getUserNameByToken())) {
            lambdaQueryWrapper.eq(MessageTemplate::getSellerId, getUserIdByToken());
        }
        return Result.ok(messageTemplateService.list(lambdaQueryWrapper));
    }

    /**
     * 复制
     *
     * @param messageTemplate
     * @return
     */
    @AutoLog(value = "邮件推送模板-复制")
@Operation(summary = "邮件推送模板-复制", description = "邮件推送模板-复制")
    @PostMapping(value = "/code")
    public Result<?> code(@RequestBody MessageTemplate messageTemplate) {
        String userId = getUserIdByToken();
        String userName = getUserNameByToken();

        if (messageTemplate.getApprovedStatus() == 1) {
            messageTemplate.setApprovedDate(new Date());
        }
        //查询字典表 获得邮件测试发送次数
        SysDictItem mailStartItem = dictItemService.selectValueByCode(DictCode.EMAIL_TEMPLATE_TEST_NUM);

        messageTemplate.setTestSendNum(Integer.parseInt(mailStartItem.getItemValue()));
        messageTemplate.setCreateBy(null);
        messageTemplate.setCreateTime(null);
        messageTemplate.setUpdateBy(null);
        messageTemplate.setUpdateTime(null);

        messageTemplate.setSellerId(userId);
        messageTemplate.setSellerName(userName);


/*        JSONObject jsonObject = new JSONObject();

        List<StoreSensitiveWord> sensitiveWordList = storeSensitiveWordService.list();
        StoreSensitiveWordModel storeSensitiveWordModel = sensitiveWord(messageTemplate, sensitiveWordList);
        jsonObject.put("keywordName", storeSensitiveWordModel.getStoreSensitiveWordModel());
        messageTemplate.setTemplateScore(storeSensitiveWordModel.getMessageTemplate().getTemplateScore());
        messageTemplate.setSensitiveJson(jsonObject.toString());*/

        messageTemplate.setApprovedStatus(YesNoStatus.YES.getCode());

        messageTemplateService.save(messageTemplate);
        return Result.ok("复制模板成功！");


    }

    /**
     * 添加
     *
     * @param messageTemplate
     * @return
     */
    @AutoLog(value = "邮件推送模板-添加")
@Operation(summary = "邮件推送模板-添加", description = "邮件推送模板-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody MessageTemplate messageTemplate) {
        String userId = getUserIdByToken();
        String userName = getUserNameByToken();
        messageTemplate.setSellerId(userId);
        messageTemplate.setSellerName(userName);

        //查询字典表 获得邮件测试发送次数
        SysDictItem mailStartItem = dictItemService.selectValueByCode(DictCode.EMAIL_TEMPLATE_TEST_NUM);
        messageTemplate.setTestSendNum(Integer.parseInt(mailStartItem.getItemValue()));


      /*
      扣分项 以json存储
         JSONObject jsonObject = new JSONObject();
      List<StoreSensitiveWord> sensitiveWordList = storeSensitiveWordService.list();
        StoreSensitiveWordModel storeSensitiveWordModel = sensitiveWord(messageTemplate, sensitiveWordList);
        jsonObject.put("keywordName", storeSensitiveWordModel.getStoreSensitiveWordModel());
        messageTemplate.setTemplateScore(storeSensitiveWordModel.getMessageTemplate().getTemplateScore());
         messageTemplate.setSensitiveJson(jsonObject.toString());
*/

        messageTemplate.setApprovedStatus(YesNoStatus.YES.getCode());

        messageTemplateService.save(messageTemplate);
        return Result.ok("添加模板成功！");
    }


    /**
     * 公共模板添加
     *
     * @param messageTemplate
     * @return
     */
    @AutoLog(value = "邮件推送模板-公共模板添加")
@Operation(summary = "邮件推送模板-公共模板添加", description = "邮件推送模板-公共模板添加")
    @PostMapping(value = "/addPublic")
    public Result<?> addPublic(@RequestBody MessageTemplate messageTemplate) {
        String userId = getUserIdByToken();
        String userName = getUserNameByToken();
        messageTemplate.setSellerId(userId);
        messageTemplate.setSellerName(userName);
        messageTemplate.setApprovedUserId(userId);
        messageTemplate.setApprovedDate(new Date());
        messageTemplateService.save(messageTemplate);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param messageTemplate
     * @return
     */
    @AutoLog(value = "邮件推送模板-编辑")
@Operation(summary = "邮件推送模板-编辑", description = "邮件推送模板-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody MessageTemplate messageTemplate) {
/*
        JSONObject jsonObject = new JSONObject();
        List<StoreSensitiveWord> sensitiveWordList = storeSensitiveWordService.list();
        StoreSensitiveWordModel storeSensitiveWordModel = sensitiveWord(messageTemplate, sensitiveWordList);
        jsonObject.put("keywordName", storeSensitiveWordModel.getStoreSensitiveWordModel());
        messageTemplate.setTemplateScore(storeSensitiveWordModel.getMessageTemplate().getTemplateScore());
        messageTemplate.setSensitiveJson(jsonObject.toString());*/

        messageTemplate.setApprovedStatus(YesNoStatus.YES.getCode());
        messageTemplateService.updateById(messageTemplate);
        return Result.ok("编辑成功！");

    }

    public Result errResult(JSONObject jsonObject) {
        Result<String> result = new Result<>();
        result.setSuccess(false);
        result.setCode(10005);
        result.setResult(jsonObject.toString());
        return result;
    }


   /* public StoreSensitiveWordModel sensitiveWord(MessageTemplate messageTemplate, List<StoreSensitiveWord> storeSensitiveWordList) {
        //扣分项属性（JSON结构存储）
        List<StoreSensitiveWordModel> storeSensitiveWordModelList = new ArrayList<>();
        StoreSensitiveWordModel sensitiveWordModel = new StoreSensitiveWordModel();
        if (storeSensitiveWordList.size() > 0) {
            for (StoreSensitiveWord storeSensitiveWord : storeSensitiveWordList) {
                int keywordNameCount = org.apache.commons.lang3.StringUtils.countMatches(StringUtils.lowerCase(messageTemplate.getTemplateContent()), StringUtils.lowerCase(storeSensitiveWord.getKeywordName()));
                if (keywordNameCount >= storeSensitiveWord.getScoreMinNum()) {
                    *//*int keywordNameMaxCount = keywordNameCount - storeSensitiveWord.getScoreMinNum();*//*
                    StoreSensitiveWordModel storeSensitiveWordModel = new StoreSensitiveWordModel();
                    List<StoreSensitiveWordModel> keywordNameMap = new ArrayList<>();
                    BeanUtils.copyProperties(storeSensitiveWord, storeSensitiveWordModel, new String[]{"id", "createBy", "createTime", "updateBy", "updateTime", "remark"});
                    storeSensitiveWordModel.setKeywordNameCount(keywordNameCount);
                    keywordNameMap.add(storeSensitiveWordModel);
                    storeSensitiveWordModelList.addAll(keywordNameMap);
                }
                System.out.println(keywordNameCount);
            }
            sensitiveWordModel.setStoreSensitiveWordModel(storeSensitiveWordModelList);
            sensitiveWordModel.setMessageTemplate(messageTemplate);
        }
        return sensitiveWordModel;
    }
*/

    @AutoLog(value = "邮件推送模板-敏感词检测")
@Operation(summary = "邮件推送模板-敏感词检测", description = "邮件推送模板-敏感词检测")
    @PostMapping(value = "/sensitiveWord")
    public Result<?> emailSensitiveWord(@RequestBody MessageTemplate messageTemplate) {
        String messageTemplateCont = messageTemplate.getTemplateRichContent();

        int keywordNameCount = org.apache.commons.lang3.StringUtils.countMatches(StringUtils.lowerCase(messageTemplate.getTemplateContent()), StringUtils.lowerCase("http"));
        //查询字典表 获得 超链接个数
        SysDictItem httpItem = dictItemService.selectValueByCode(DictCode.MAIL_HTTP_NUM);
        if (keywordNameCount > Integer.parseInt(httpItem.getItemValue())) {
            return Result.error("邮件内容中，超链接不能大于5条");
        }

        //检测拼写错误单词
/*        Set<String> errTextUrl = this.errTextUrl(messageTemplate);
        try {
            if (errTextUrl.size() > 0 && errTextUrl != null) {
                for (String errText : errTextUrl) {

                   messageTemplate.setTemplateRichContent(messageTemplate.getTemplateRichContent().replace(errText, "<span style='color: blue'>" + errText + "</span>"));
                   messageTemplate.setTemplateRichContent(messageTemplate.getTemplateRichContent().indexOf(errText))

                }
            }
        } catch (Exception e) {
            log.info("错误检测：   " + errTextUrl.size() + " ---   更改颜色失败 ");
        }*/
        //检测关键字
        List<StoreSensitiveWord> storeSensitiveWordList = storeSensitiveWordService.list();
        List<String> textErrList = new ArrayList<>();
        if (storeSensitiveWordList.size() > 0) {
            Set<String> errList = new HashSet<>();
            for (StoreSensitiveWord storeSensitiveWord : storeSensitiveWordList) {
                Pattern p = Pattern.compile(storeSensitiveWord.getKeywordName(), Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(messageTemplate.getTemplateRichContent());
                while (m.find()) {
                    if (StringUtils.isNotEmpty(m.group())) {
                        errList.add(m.group(0));
                    }
                }
            }
            List<String> textList = this.textUrl(messageTemplate);//分词列表
            if (textList.size() > 0) {
                for (String text : textList) {
                    if (errList.size() > 0) {
                        for (String errText : errList) {
                            if (text.equals(errText) == true) {
                                textErrList.add(text);
                            }
                        }
                    }
                }
            }
            try {
                System.out.println(messageTemplate.getTemplateRichContent());
                for (String errText : textErrList) {
                    messageTemplate.setTemplateRichContent(messageTemplate.getTemplateRichContent().replace(errText, "<span style='color: red'>" + errText + "</span>"));
                }
            } catch (Exception e) {
                log.info("循环替换敏感词错误：" + errList);
            }
        }
        MessageTemplateModel messageTemplateModel = new MessageTemplateModel();
        BeanUtils.copyProperties(messageTemplate, messageTemplateModel, new String[]{"templateRichContent"});
        messageTemplateModel.setTemplateRichContent(messageTemplateCont);
        messageTemplateModel.setTemplateRichContentText(messageTemplate.getTemplateRichContent());
        //查询字典表 获得 字符个数
        SysDictItem mailStartItem = dictItemService.selectValueByCode(DictCode.MAIL_START_NUM);
        SysDictItem mailEndItem = dictItemService.selectValueByCode(DictCode.MAIL_END_NUM);
        if (messageTemplate.getTemplateContent().length() < Integer.parseInt(mailStartItem.getItemValue()) || messageTemplate.getTemplateContent().length() > Integer.parseInt(mailEndItem.getItemValue())) {
            messageTemplateModel.setTextLen(messageTemplate.getTemplateContent().length());
        }
        if (textErrList.size() == 0) {
            String userId = getUserIdByToken();
            String userName = getUserNameByToken();
            messageTemplate.setSellerId(userId);
            messageTemplate.setSellerName(userName);

            //查询字典表 获得邮件测试发送次数

            SysDictItem emailTemplateTestNum = dictItemService.selectValueByCode(DictCode.EMAIL_TEMPLATE_TEST_NUM);
            messageTemplate.setTestSendNum(Integer.parseInt(emailTemplateTestNum.getItemValue()));

            messageTemplate.setApprovedStatus(YesNoStatus.YES.getCode());
            if (oConvertUtils.isNotEmpty(messageTemplate.getId())) {
                messageTemplateService.updateById(messageTemplate);
                return Result.ok("修改模板成功！");
            } else {
                messageTemplateService.save(messageTemplate);
                return Result.ok("添加模板成功！");
            }
        }
        return Result.ok(messageTemplateModel);
    }

    //检测拼写错误单词
    private Set<String> errTextUrl(MessageTemplate messageTemplate) {
        try {
            String replaceAll = messageTemplate.getTemplateContent().replaceAll("[^0-9a-zA-Z\u4e00-\u9fa5.，,。？“”]+", " ");
            //String url = "http://121.89.235.221:8801/errText?text=" + URLEncoder.encode(messageTemplate.getTemplateContent(), "UTF-8");
            String url = "http://121.89.235.221:8801/errText?text=" + URLEncoder.encode(replaceAll, "UTF-8");

            JSONObject jsonObject = RestUtil.get(url);
            MessageTemplateModel templateModel = JSONObject.toJavaObject(jsonObject, MessageTemplateModel.class);
            Set<String> errText = templateModel.getErrText();
            return errText;
        } catch (Exception e) {
            e.printStackTrace();
            log.info("错误： 请求敏感词接口错误 - 这个用户 ：" + getUserIdByToken() + " --> " + e);
            Set<String> errText = new HashSet<>();
            return errText;
        }

    }

    // 请求分词接口
    private List<String> textUrl(MessageTemplate messageTemplate) {
        try {
            String replaceAll = messageTemplate.getTemplateContent().replaceAll("[^0-9a-zA-Z\u4e00-\u9fa5.，,。？“”]+", " ");
            // String url = "http://121.89.235.221:8801/text?text=" + URLEncoder.encode(messageTemplate.getTemplateContent(), "UTF-8");
            String url = "http://121.89.235.221:8801/text?text=" + URLEncoder.encode(replaceAll, "UTF-8");
            JSONObject jsonObject = RestUtil.get(url);
            MessageTemplateModel templateModel = JSONObject.toJavaObject(jsonObject, MessageTemplateModel.class);
            List<String> errText = templateModel.getTextList();
            return errText;
        } catch (Exception e) {
            e.printStackTrace();
            log.info("错误： 请求分词接口错误 - 这个用户 ：" + getUserIdByToken() + " --> " + e);
            List<String> errText = new ArrayList<>();
            return errText;
        }

    }


    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "邮件推送模板-通过id删除")
@Operation(summary = "邮件推送模板-通过id删除", description = "邮件推送模板-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {


        MessageTemplate messageTemplate = messageTemplateService.getById(id);
        String userId = getUserIdByToken();
        //当前用户
        SysUser sysUser = sysUserService.getById(userId);
        if (messageTemplate.getIsPublic().equals(YesNoStatus.YES.getCode())) {
            if (sysUser.getUserType().equals(UserType.SELLER.getCode())) {
                return Result.error("公共模板不允许删除");
            }
        } else {
            //判断此模板是否是当前用户
            if (!messageTemplate.getSellerId().equals(userId) && !sysUser.getUserType().equals(UserType.PLATFORM.getCode())) {
                return Result.error("请不要删除别人的模板");
            } else {
                //更改删除状态 1 以删除
                messageTemplate.setIsDelete(YesNoStatus.YES.getCode());
                messageTemplateService.updateById(messageTemplate);
            }
        }
        return Result.ok("删除成功!");


    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "邮件推送模板-批量删除")
@Operation(summary = "邮件推送模板-批量删除", description = "邮件推送模板-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.messageTemplateService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "邮件推送模板-通过id查询")
@Operation(summary = "邮件推送模板-通过id查询", description = "邮件推送模板-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        MessageTemplate messageTemplate = messageTemplateService.getById(id);
        return Result.ok(messageTemplate);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param messageTemplate
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, MessageTemplate messageTemplate) {
        return super.exportXls(request, messageTemplate, MessageTemplate.class, "邮件推送模板");
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
        return super.importExcel(request, response, MessageTemplate.class);
    }


    /**
     * 测试邮件模板
     *
     * @return
     */
    @RequestMapping(value = "/emailScore", method = RequestMethod.GET)
    public Result<?> emailScore(MessageTemplateModel messageTemplateModel) {
        if (oConvertUtils.isEmpty(messageTemplateModel.getToEmail())) {
            return Result.error("请输入接受邮箱！");
        }
        if (oConvertUtils.isEmpty(messageTemplateModel.getEmailTitle())) {
            return Result.error("请输入邮件标题！");
        }
        if (oConvertUtils.isEmpty(messageTemplateModel.getEmailRichContent())) {
            return Result.error("请输入邮件内容！");
        }
        String toEmail = messageTemplateModel.getToEmail();
        String emailTitle = messageTemplateModel.getEmailTitle();
        String emailTextContent = messageTemplateModel.getEmailTextContent();
        String emailRichContent = messageTemplateModel.getEmailRichContent();
        try {
            log.info("toAddress :" + toEmail);
            log.info("emailTitle :" + emailTitle);
            log.info("emailTextContent :" + emailTextContent);
            boolean flag = SingleSendMail.send(toEmail, emailTitle, emailRichContent, emailTextContent);
            if (!flag) {
                return Result.error("发送失败，请重新发送！");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return Result.ok("发送成功！");
    }


    /**
     * 功能描述:邮件标题相似度匹配
     *
     * @Author: zhoushen
     * @Date 2020-12-2
     */
    @AutoLog(value = "邮件标题相似度匹配")
@Operation(summary = "邮件标题相似度匹配", description = "邮件标题相似度匹配")
    @GetMapping(value = "/templateTitleMatch")
    public Result<?> templateTitleMatch(@RequestParam(name = "templateTitle", required = true) String templateTitle, @RequestParam(name = "id", required = false) String id) {
        //字典读取邮件标题相似度匹配参数值,无则默认0.5
        String message_title_parameter = sysDictService.queryDictValueByText(DictCode.MATCH_SIMILARITY_PARAMETER, DictCode.MESSAGE_TITLE_PARAMETER);
        message_title_parameter = message_title_parameter != null ? message_title_parameter : String.valueOf(Coefficient.DEFAULT_COEFFICIENT.getCode());

        LambdaQueryWrapper<MessageTemplate> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.and(i -> i.eq(MessageTemplate::getSellerId, getUserIdByToken()).or().eq(MessageTemplate::getIsPublic, YesNoStatus.YES.getCode()));
        if (oConvertUtils.isNotEmpty(id)) {
            lambdaQueryWrapper.ne(MessageTemplate::getId, id);
        }
        lambdaQueryWrapper.eq(MessageTemplate::getIsDelete, YesNoStatus.NO.getCode());
        List<MessageTemplate> messageTemplateList = messageTemplateService.list(lambdaQueryWrapper);

        List<String> titleList = new ArrayList<>();
        if (messageTemplateList != null && messageTemplateList.size() > 0) {
            messageTemplateList.forEach(messageTemplate -> {
                titleList.add(messageTemplate.getTemplateTitle());
            });
        }
        JSONObject jsonObject = new JSONObject();
        //StringSimilarityInterfaceModel类为python接口需要提供的字段属性
        jsonObject.put(StringSimilarityInterfaceModel.PARAMS_NUM, message_title_parameter);
        jsonObject.put(StringSimilarityInterfaceModel.PARAMS_TITLE, templateTitle);
        jsonObject.put(StringSimilarityInterfaceModel.PARAMS_TITLELIST, titleList);
        String data = "";
        try {
            data = URLEncoder.encode(jsonObject.toJSONString(), StringSimilarityInterfaceModel.ENCODING);
        } catch (Exception e) {
            System.out.println(data);
        }

        String url = StringSimilarityInterfaceModel.URL + "?" + StringSimilarityInterfaceModel.PARAMS + "=" + data;
        String strResult = (String) ToolHttpClient.get(url, new HashMap<>());

        try {
            JSONObject result = JSONObject.parseObject(strResult);
            return Result.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    /**
     * 测试邮件模板
     *
     * @return
     * @author: zhoushen
     */
    @Operation(summary ="测试邮件模板")
    @RequestMapping(value = "/emailTemplateStyle", method = RequestMethod.GET)
    public Result<?> emailTemplateStyle(MessageTemplateModel messageTemplateModel) {
        String id = messageTemplateModel.getId();
        String accountId = messageTemplateModel.getAccountId();
        if (oConvertUtils.isEmpty(id)) {
            return Result.error("请选择邮箱！");
        }
        Emailaccount account = emailaccountService.getById(accountId);
        if (oConvertUtils.isEmpty(account)) {
            return Result.error("未查询到此邮箱，请重新选择！");
        }
        MessageTemplate messageTemplate = messageTemplateService.getById(id);
        if (oConvertUtils.isEmpty(messageTemplate)) {
            return Result.error("模板选择有误，请重新选择！");
        }
        MessageTemplate messageTemplateNew = new MessageTemplate();
        messageTemplateNew.setId(id);
        if (messageTemplate.getTestSendNum() <= 0) {
            return Result.error("该模板测试次数已达上限！");
        } else {
            messageTemplateNew.setTestSendNum(messageTemplate.getTestSendNum() - 1);
        }
        String sendEmail = account.getAccountName();
        PushUser pushUser = new PushUser();
        pushUser.setKolName("[nickname]");
        MessageTemplate replacTemplate = replaceAttributes(pushUser, messageTemplate);
        String emailTitle = replacTemplate.getTemplateTitle();
        String emailContent = replacTemplate.getTemplateRichContent();
        boolean flag = false;
        //使用阿里发送
        log.info("使用阿里发送--------------------------begin");
        //获取账号列表
        List<SysDictItem> dictItemList = dictService.getItemList(DictCode.JOB_SEND);
        String fromname = dictItemList.stream().filter(x -> "fromname".equals(x.getItemText())).collect(Collectors.toList()).get(0).getItemValue();
        LambdaQueryWrapper<DomainAccount> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DomainAccount::getStatus, YesNoStatus.NO.getCode());
        lambdaQueryWrapper.orderByDesc(DomainAccount::getWeight);
        List<DomainAccount> domainAccountList = domainAccountService.list(lambdaQueryWrapper);
        DomainAccount domainAccount = domainAccountList.get(0);
        String sendDomain = domainAccount.getSendDomain();
        try {
            flag = SingleSendMail.smtpSend(sendEmail, sendEmail, emailTitle, emailContent, sendDomain, fromname);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        //修改模板测试次数
        messageTemplateService.updateById(messageTemplateNew);
        if (!flag) {
            return Result.error("发送失败，请重新发送！");
        }
        return Result.ok("发送成功！");
    }

    /**
     * 功能描述:批量替换属性值
     *
     * @Author: nqr
     * @Date 2020-10-30 15:34:34
     */
    private MessageTemplate replaceAttributes(PushUser pushUser, MessageTemplate messageTemplate) {
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
                String title = messageTemplate.getTemplateTitle().replace(text, String.valueOf(resultValue));
                String content = messageTemplate.getTemplateContent().replace(text, String.valueOf(resultValue));
                String richContent = messageTemplate.getTemplateRichContent().replace(text, String.valueOf(resultValue));
                messageTemplate.setTemplateTitle(title);
                messageTemplate.setTemplateContent(content);
                messageTemplate.setTemplateRichContent(richContent);
            }
            return messageTemplate;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return messageTemplate;
        }
    }
}
