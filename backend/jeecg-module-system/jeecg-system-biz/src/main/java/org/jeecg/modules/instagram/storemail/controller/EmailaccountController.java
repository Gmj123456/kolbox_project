package org.jeecg.modules.instagram.storemail.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.RestUtil;
import org.jeecg.modules.instagram.storemail.entity.Emailaccount;
import org.jeecg.modules.instagram.storemail.model.EmailAccountModel;
import org.jeecg.modules.instagram.storemail.service.IEmailaccountService;
import org.jeecg.modules.instagram.storepromotion.entity.StoreSellerGrade;
import org.jeecg.modules.instagram.storepromotion.service.IStoreSellerGradeService;
import org.jeecg.modules.instagram.utils.SendEmailTLS;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 邮箱账号
 * @Author: jeecg-boot
 * @Date: 2020-05-11
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "邮箱账号")
@RestController
@RequestMapping("/emailaccount/emailaccount")
public class EmailaccountController extends JeecgController<Emailaccount, IEmailaccountService> {
    @Autowired
    private IEmailaccountService emailaccountService;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private IStoreSellerGradeService sellerGradeService;

    /**
     * 分页列表查询
     *
     * @param emailaccount
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "邮箱账号-分页列表查询")
    @Operation(summary = "邮箱账号-分页列表查询", description = "邮箱账号-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(Emailaccount emailaccount,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        String userId = getUserIdByToken();
        String userName = getUserNameByToken();
        QueryWrapper<Emailaccount> queryWrapper = QueryGenerator.initQueryWrapper(emailaccount, req.getParameterMap());
        if (!"admin".equals(userName)) {
            queryWrapper.eq("seller_id", userId);
        }
        Page<Emailaccount> page = new Page<>(pageNo, pageSize);
        IPage<Emailaccount> pageList = emailaccountService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param emailaccount
     * @return
     */
    @AutoLog(value = "邮箱账号-添加")
    @Operation(summary = "邮箱账号-添加", description = "邮箱账号-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody Emailaccount emailaccount) {
        String userId = getUserIdByToken();
        String userName = getUserNameByToken();
        // 查询此邮箱是否已存在
        LambdaQueryWrapper<Emailaccount> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Emailaccount::getSellerId, userId);
        List<Emailaccount> list = emailaccountService.list(lambdaQueryWrapper);
        List<Emailaccount> emailaccountList = list.stream().filter(x -> x.getAccountName().equals(emailaccount.getAccountName())).collect(Collectors.toList());
        if (emailaccountList.size() > 0) {
            return Result.error("已绑定此邮箱！");
        }
        // 获取当前用户套餐
        SysUser sysUser = sysUserService.getById(userId);
        StoreSellerGrade sellerGrade = sellerGradeService.getById(sysUser.getGradeId());
        // 获取等级额度
        Integer emailAccountCount = sellerGrade.getEmailAccountCount();
        if (list.size() >= emailAccountCount) {
            return Result.error("绑定失败，绑定的邮箱数量已达上限！");
        }
        emailaccount.setSellerId(userId);
        emailaccount.setSellerName(userName);
        emailaccountService.save(emailaccount);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param emailaccount
     * @return
     */
    @AutoLog(value = "邮箱账号-编辑")
    @Operation(summary = "邮箱账号-编辑", description = "邮箱账号-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody Emailaccount emailaccount) {
        emailaccountService.updateById(emailaccount);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "邮箱账号-通过id删除")
    @Operation(summary = "邮箱账号-通过id删除", description = "邮箱账号-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        emailaccountService.removeById(id);
        return Result.ok("解绑成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "邮箱账号-批量删除")
    @Operation(summary = "邮箱账号-批量删除", description = "邮箱账号-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.emailaccountService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量解绑成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "邮箱账号-通过id查询")
    @Operation(summary = "邮箱账号-通过id查询", description = "邮箱账号-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        Emailaccount emailaccount = emailaccountService.getById(id);
        return Result.ok(emailaccount);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param emailaccount
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Emailaccount emailaccount) {
        return super.exportXls(request, emailaccount, Emailaccount.class, "邮箱账号");
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
        return super.importExcel(request, response, Emailaccount.class);
    }


    /**
     * Gmail邮箱授权
     *
     * @return
     */
    @AutoLog(value = "邮箱账号-分页列表查询")
    @Operation(summary = "邮箱账号-分页列表查询", description = "邮箱账号-分页列表查询")
    @GetMapping(value = "/googleEmailOauth")
    public Result<?> googleEmailOauth(String IdToken) {
       /* GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(Arrays.asList("55169054266-aps1eh9q4gigendgn7m0lvftavd5v3nq.apps.googleusercontent.com"))
                .build();*/
        // TODO 验证IdToken
        // log.info(emailAccountModel.getIdToken());

        // String oauthStepOneUrl = "https://accounts.google.com/o/oauth2/auth?client_id=55169054266-aps1eh9q4gigendgn7m0lvftavd5v3nq.apps.googleusercontent.com&redirect_uri=http%3A%2F%2Flocalhost%3A8888%2Fkol-api%2Femailaccount%2Femailaccount%2FgoogleOauthStepOne&scope=https://mail.google.com/&access_type=offline&include_granted_scopes=true&response_type=code";
        return Result.ok("授权成功");

    }


/*
    @AutoLog(value = "邮箱账号-分页列表查询")
@Operation(summary = "邮箱账号-分页列表查询", description = "邮箱账号-分页列表查询")
    @GetMapping(value = "/gmailOauth")
    public ModelAndView gmailOauth(EmailAccountModel emailAccountModel) {
        log.info("code：" + emailAccountModel.getCode());
        log.info("userId：" + emailAccountModel.getState());
        String clientId = "156590296858-ttthspcn2se6bo5qpp3ok845bsmohsa1.apps.googleusercontent.com";
        String clientSecret = "Hrck2GpcXGtKx5bXN_u88veA";
        String redirectUri = "https://gmail.torpedocross.com/kol-api/emailaccount/emailaccount/gmailOauth";
        String grantType = "authorization_code";
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("emailBindResult");
        try {
            String postURL = "https://oauth2.googleapis.com/token";
            PostMethod postMethod = null;
            postMethod = new PostMethod(postURL);
            postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            //参数设置，需要注意的就是里边不能传NULL，要传空字符串
            NameValuePair[] data = {
                    new NameValuePair("code", emailAccountModel.getCode()),
                    new NameValuePair("redirect_uri", redirectUri),
                    new NameValuePair("client_id", clientId),
                    new NameValuePair("client_secret", clientSecret),
                    new NameValuePair("scope", ""),
                    new NameValuePair("grant_type", grantType)

            };
            postMethod.setRequestBody(data);
            org.apache.commons.httpclient.HttpClient httpClient = new org.apache.commons.httpclient.HttpClient();
            int response = httpClient.executeMethod(postMethod); // 执行POST方法
            String result = postMethod.getResponseBodyAsString();
            JSONObject jsonObject;
            jsonObject = JSONObject.parseObject(result);
            String idToken = jsonObject.getString("id_token");
            log.info("IdToken: " + idToken);

            if (idToken != null) {
                String tokenInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
                JSONObject jsontokenInfoObject = RestUtil.get(tokenInfoUrl);
                //GoogleIdToken.Payload payload = gIdToken.getPayload();
                // Print user identifier
                //String userId = payload.getSubject();
                // Get profile information from payload
                String email = jsontokenInfoObject.getString("email");
                String name = jsontokenInfoObject.getString("name");
                String pictureUrl = jsontokenInfoObject.getString("picture");
                String exp = jsontokenInfoObject.getString("exp") + "000";
                long expiresTime = new Long(exp);
                Date expiresDate = new Date(expiresTime);
                //String locale = (String) payload.get("locale");
                //String familyName = (String) payload.get("family_name");
                //String givenName = (String) payload.get("given_name");
                String accessToken = jsonObject.getString("access_token");
                String refreshToken = jsonObject.getString("refresh_token");
                log.info("Email: " + email);
                log.info("AccessToken: " + accessToken);
                log.info("RefreshToken: " + refreshToken);
                log.info("Name: " + name);
                String userId = emailAccountModel.getState();
                //查询是否已经绑定了此邮箱
                LambdaQueryWrapper<Emailaccount> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(Emailaccount::getAccountName, email);
                lambdaQueryWrapper.eq(Emailaccount::getSellerId, userId);
                List<Emailaccount> emailaccounts = emailaccountService.list(lambdaQueryWrapper);

                if (null != emailaccounts && emailaccounts.size() > 0) {

                    modelAndView.addObject("oauthResult", "邮箱：" + email + "已经绑定了，请勿重复绑定");
                    modelAndView.addObject("oauthResultTitle", "重复绑定");
                    modelAndView.addObject("oauthResultStatus", "2");
                    return modelAndView;
                }
                Emailaccount emailaccount = new Emailaccount();
                emailaccount.setSellerId(userId);
                emailaccount.setAccountName(email);
                emailaccount.setAccountUserName(name);
                emailaccount.setAccountPictureUrl(pictureUrl);
                emailaccount.setAccessToken(accessToken);
                emailaccount.setRefreshToken(refreshToken);
                emailaccount.setExpiresTime(expiresDate);

                emailaccount.setCreateBy(userId);
                emailaccount.setCreateTime(new Date());
                emailaccountService.save(emailaccount);
                modelAndView.addObject("oauthResult", "邮箱：" + email + "绑定成功！");
                modelAndView.addObject("oauthResultTitle", "绑定成功");
                modelAndView.addObject("oauthResultStatus", "1");
                return modelAndView;

            } else {
                modelAndView.addObject("oauthResult", "获取IdToken异常！");
                modelAndView.addObject("oauthResultTitle", "绑定异常");
                modelAndView.addObject("oauthResultStatus", "0");
                return modelAndView;
            }

        } catch (Exception e) {
            log.info("请求异常" + e.getMessage(), e);
            modelAndView.addObject("oauthResult", "请求数据发生异常，请联系管理员！");
            modelAndView.addObject("oauthResultTitle", "绑定异常");
            modelAndView.addObject("oauthResultStatus", "0");
            return modelAndView;
        }


    }
*/

    @AutoLog(value = "邮箱账号-分页列表查询")
    @Operation(summary = "邮箱账号-分页列表查询", description = "邮箱账号-分页列表查询")
    @GetMapping(value = "/refreshGmailToken")
    public Result<?> refreshGmailToken(Emailaccount emailaccount) {
        if (StringUtils.isBlank(emailaccount.getRefreshToken())) {
            return Result.error("刷新token为空");
        }
        Emailaccount emailaccount1 = emailaccountService.checkTime(getUserIdByToken(), emailaccount.getAccountName());
        return Result.ok(emailaccount1);
    }


    @Operation(summary = "检测邮箱是否存在")
    @GetMapping(value = "/checkEmail")
    public Result<?> checkEmail(Emailaccount emailaccount) {
        String userId = getUserIdByToken();
        String accountName = emailaccount.getAccountName();
        LambdaQueryWrapper<Emailaccount> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Emailaccount::getAccountName, accountName);
        lambdaQueryWrapper.eq(Emailaccount::getSellerId, userId);
        List<Emailaccount> list = emailaccountService.list(lambdaQueryWrapper);
        if (list.size() > 0) {
            return Result.error("邮箱已存在！");
        } else {
            return Result.ok();
        }
    }
}
