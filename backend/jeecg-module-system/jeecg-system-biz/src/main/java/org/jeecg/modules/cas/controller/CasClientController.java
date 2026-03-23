package org.jeecg.modules.cas.controller;

import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.cas.util.XmlUtils;
import org.jeecg.modules.system.entity.SysDepart;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysDepartService;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * <p>
 * CAS单点登录客户端登录认证
 * </p>
 *
 * @Author zhoujf
 * @since 2018-12-20
 */
@Slf4j
@RestController
@RequestMapping("/sys/cas/client")
public class CasClientController {

    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private ISysDepartService sysDepartService;
    @Autowired
    private RedisUtil redisUtil;

    @Value("${cas.prefixUrl}")
    private String prefixUrl;

    /*

        @GetMapping("/validateLogin")
        public Object validateLogin(@RequestParam(name="ticket") String ticket,
                                    @RequestParam(name="service") String service,
                                    HttpServletRequest request,
                                    HttpServletResponse response) throws Exception {
            Result<JSONObject> result = new Result<JSONObject>();
            log.info("Rest api login.");
            try {
                String validateUrl = prefixUrl+"/p3/serviceValidate";
                String res = CasServiceUtil.getStValidate(validateUrl, ticket, service);
                log.info("res."+res);
                final String error = XmlUtils.getTextForElement(res, "authenticationFailure");
                if(StringUtils.isNotEmpty(error)) {
                    throw new Exception(error);
                }
                final String principal = XmlUtils.getTextForElement(res, "user");
                if (StringUtils.isEmpty(principal)) {
                    throw new Exception("No principal was found in the response from the CAS server.");
                }
                log.info("-------token----username---"+principal);
                //1. 校验用户是否有效
                SysUser sysUser = sysUserService.getUserByName(principal);
                result = sysUserService.checkUserIsEffective(sysUser);
                if(!result.isSuccess()) {
                    return result;
                }
                String token = JwtUtil.sign(sysUser.getUsername(), sysUser.getId(), sysUser.getRealname(),sysUser.getUnionId());
                // 设置超时时间
                redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
                redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME*2 / 1000);

                //获取用户部门信息
                JSONObject obj = new JSONObject();
                List<SysDepart> departs = sysDepartService.queryUserDeparts(sysUser.getId());
                obj.put("departs", departs);
                if (departs == null || departs.size() == 0) {
                    obj.put("multi_depart", 0);
                } else if (departs.size() == 1) {
                    sysUserService.updateUserDepart(principal, departs.get(0).getOrgCode(),null);
                    obj.put("multi_depart", 1);
                } else {
                    obj.put("multi_depart", 2);
                }
                obj.put("token", token);
                obj.put("userInfo", sysUser);
                result.setResult(obj);
                result.success("登录成功");

            } catch (Exception e) {
                //e.printStackTrace();
                result.error500(e.getMessage());
            }
            return new HttpEntity<>(result);
        }

    */
    @GetMapping("/validateLogin")
    public Object validateLogin(
            @RequestParam(name = "ticket") String ticket,
            @RequestParam(name = "service") String service,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        Result<JSONObject> result = new Result<>();

        log.info("开始 CAS 单点登录验证 | ticket: {}, service: {}", ticket, service);

        try {
            // 1. 构建完整的验证 URL
            String encodedService = URLEncoder.encode(service, StandardCharsets.UTF_8.toString());
            String validateUrl = prefixUrl + "/p3/serviceValidate?ticket=" + ticket + "&service=" + encodedService;

            log.info("请求 CAS 验证地址: {}", validateUrl);

            // 2. 使用 HttpClient 发送 GET 请求
            String casResponse;
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpGet httpGet = new HttpGet(validateUrl);
                try (CloseableHttpResponse httpResponse = httpClient.execute(httpGet)) {
                    int statusCode = httpResponse.getStatusLine().getStatusCode();
                    casResponse = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);

                    log.info("CAS 返回状态码: {}, 响应内容: \n{}", statusCode, casResponse);

                    if (statusCode != 200) {
                        throw new Exception("CAS 服务返回非200状态码: " + statusCode);
                    }
                }
            }

            // 3. 解析 CAS 返回的 XML
            final String errorMsg = XmlUtils.getTextForElement(casResponse, "authenticationFailure");
            if (StringUtils.isNotBlank(errorMsg)) {
                log.warn("CAS 验证失败: {}", errorMsg);
                throw new Exception(errorMsg);  // 如 "INVALID_TICKET: 未能够识别出目标 'ST-...'票根"
            }

            final String principal = XmlUtils.getTextForElement(casResponse, "user");
            if (StringUtils.isBlank(principal)) {
                throw new Exception("CAS 响应中未找到 user/principal 信息");
            }

            log.info("CAS 验证成功，用户: {}", principal);

            // 4. 后续你的业务逻辑（保持不变）
            SysUser sysUser = sysUserService.getUserByName(principal);
            result = sysUserService.checkUserIsEffective(sysUser);
            if (!result.isSuccess()) {
                return result;
            }

            String token = JwtUtil.sign(sysUser.getUsername(), sysUser.getId(), sysUser.getRealname(), sysUser.getSalt());

            // 设置 Redis token
            String redisKey = CommonConstant.PREFIX_USER_TOKEN + token;
            redisUtil.set(redisKey, token);
            redisUtil.expire(redisKey, JwtUtil.EXPIRE_TIME * 2 / 1000);

            // 获取部门信息
            JSONObject obj = new JSONObject();
            List<SysDepart> departs = sysDepartService.queryUserDeparts(sysUser.getId());
            obj.put("departs", departs);

            if (departs == null || departs.isEmpty()) {
                obj.put("multi_depart", 0);
            } else if (departs.size() == 1) {
                sysUserService.updateUserDepart(principal, departs.get(0).getOrgCode(), null);
                obj.put("multi_depart", 1);
            } else {
                obj.put("multi_depart", 2);
            }

            obj.put("token", token);
            obj.put("userInfo", sysUser);

            result.setResult(obj);
            result.success("登录成功");

        } catch (Exception e) {
            log.error("CAS 登录验证异常", e);
            result.error500("验证失败: " + e.getMessage());
        }

        return new HttpEntity<>(result);
    }
}
