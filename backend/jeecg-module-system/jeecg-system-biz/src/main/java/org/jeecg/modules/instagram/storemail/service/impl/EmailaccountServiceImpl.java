package org.jeecg.modules.instagram.storemail.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.jeecg.common.util.RestUtil;
import org.jeecg.modules.instagram.storemail.entity.Emailaccount;
import org.jeecg.modules.instagram.storemail.mapper.EmailaccountMapper;
import org.jeecg.modules.instagram.storemail.service.IEmailaccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Description: 邮箱账号
 * @Author: jeecg-boot
 * @Date: 2020-05-11
 * @Version: V1.0
 */
@Service
public class EmailaccountServiceImpl extends ServiceImpl<EmailaccountMapper, Emailaccount> implements IEmailaccountService {

    @Resource
    private EmailaccountMapper emailaccountMapper;

    @Override
    public Emailaccount checkTime(String sellerId, String email) {
        // LambdaQueryWrapper<Emailaccount> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // lambdaQueryWrapper.eq(Emailaccount::getSellerId, sellerId);
        // lambdaQueryWrapper.eq(Emailaccount::getAccountName, email);
        // Emailaccount emailaccount = getOne(lambdaQueryWrapper);
        Emailaccount emailaccountNew = new Emailaccount();
    /*    String accessToken;
        System.out.println("已存在access_token --- " + emailaccount.getAccessToken());
        //判断access_token是否过期
        if (emailaccount.getExpiresTime().before(new Date())) {
            String clientId = "156590296858-ttthspcn2se6bo5qpp3ok845bsmohsa1.apps.googleusercontent.com";
            String clientSecret = "Hrck2GpcXGtKx5bXN_u88veA";
            String grantType = "refresh_token";
            try {
                String postUrl = "https://oauth2.googleapis.com/token";
                PostMethod postMethod;
                postMethod = new PostMethod(postUrl);
                postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
                //参数设置，需要注意的就是里边不能传NULL，要传空字符串
                NameValuePair[] data = {
                        new NameValuePair("client_id", clientId),
                        new NameValuePair("client_secret", clientSecret),
                        new NameValuePair("refresh_token", emailaccount.getRefreshToken()),
                        new NameValuePair("grant_type", grantType)
                };
                postMethod.setRequestBody(data);
                org.apache.commons.httpclient.HttpClient httpClient = new org.apache.commons.httpclient.HttpClient();
                // 执行POST方法
                int response = httpClient.executeMethod(postMethod);
                String result = postMethod.getResponseBodyAsString();
                JSONObject jsonObject = JSONObject.parseObject(result);
                String accessTokenNew = jsonObject.getString("access_token");
                String idToken = jsonObject.getString("id_token");
                String tokenInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
                JSONObject jsontokenInfoObject = RestUtil.get(tokenInfoUrl);
                String exp = jsontokenInfoObject.getString("exp") + "000";
                long expiresTime = new Long(exp);
                Date expiresDate = new Date(expiresTime);
                emailaccountNew.setId(emailaccount.getId());
                emailaccountNew.setAccessToken(accessTokenNew);
                emailaccountNew.setExpiresTime(expiresDate);
                emailaccountMapper.updateById(emailaccountNew);
                return emailaccountNew;
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        } else {
            accessToken = emailaccount.getAccessToken();
            emailaccountNew.setId(emailaccount.getId());
            emailaccountNew.setAccessToken(accessToken);
        }
        System.out.println("返回 access_token --- " + emailaccountNew.getAccessToken());*/
        return emailaccountNew;
    }
}
