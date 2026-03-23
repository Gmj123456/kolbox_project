package org.jeecg.modules.feishu.controller;



import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.modules.feishu.model.FeishuSheetResponse;
import org.jeecg.modules.feishu.service.IFeishuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 飞书在线表格数据获取控制器
 * @Author: jeecg-boot
 * @Date: 2025-09-15
 * @Version: V1.0
 */
@RestController
@RequestMapping("/feishu/sheet")
@Slf4j
public class FeishuSheetController {

    @Autowired
    private IFeishuService feishuService;

    /**
     * 获取飞书在线表格原始数据
     *
     * @param spreadSheetUrl 飞书在线表格URL
     * @param accessToken 访问令牌
     * @return 原始表格数据
     */
    @AutoLog(value = "获取飞书在线表格原始数据")
    @GetMapping("/getRawData")
    public Result<?> getFeishuSheetRawData(
            @RequestParam String spreadSheetUrl,
            @RequestParam(required = false) String accessToken) {
        
        try {
            log.info("开始获取飞书表格原始数据，URL: {}", spreadSheetUrl);
            
            FeishuSheetResponse response = feishuService.getFeishuSheetData(spreadSheetUrl, accessToken);
            
            if (response == null) {
                return Result.error("获取飞书表格数据失败");
            }
            
            if (response.getCode() != null && response.getCode() != 0) {
                return Result.error("飞书API返回错误: " + response.getMsg());
            }
            
            return Result.ok(response);
            
        } catch (Exception e) {
            log.error("获取飞书表格数据异常", e);
            return Result.error("获取数据异常: " + e.getMessage());
        }
    }

    /**
     * 获取飞书租户访问令牌
     *
     * @param appId 应用ID（可选，不传则使用配置文件中的默认值）
     * @param appSecret 应用密钥（可选，不传则使用配置文件中的默认值）
     * @return 租户访问令牌
     */
    @AutoLog(value = "获取飞书租户访问令牌")
    @PostMapping("/getTenantAccessToken")
    public Result<?> getFeishuTenantAccessToken(
            @RequestParam(required = false) String appId,
            @RequestParam(required = false) String appSecret) {
        
        try {
            String tenantAccessToken;
            
            if (appId != null && appSecret != null) {
                // 使用传入的appId和appSecret
                log.info("使用传入的参数获取tenantAccessToken，appId: {}", appId);
                tenantAccessToken = feishuService.getInternalTenantAccessToken(appId, appSecret);
            } else {
                // 使用默认配置
                log.info("使用默认配置获取tenantAccessToken");
                tenantAccessToken = feishuService.getInternalTenantAccessToken();
            }
            
            if (tenantAccessToken == null) {
                return Result.error("获取飞书tenantAccessToken失败，请检查应用配置");
            }
            
            // 返回结果（不要返回完整的token，只返回部分信息用于确认）
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("tokenType", "tenant_access_token");
            result.put("tokenLength", tenantAccessToken.length());
            result.put("tokenPrefix", tenantAccessToken.length() > 10 ? tenantAccessToken.substring(0, 10) + "..." : tenantAccessToken);
            result.put("tenantAccessToken", tenantAccessToken);
            
            return Result.ok(result);
            
        } catch (Exception e) {
            log.error("获取飞书tenantAccessToken异常", e);
            return Result.error("获取tenantAccessToken异常: " + e.getMessage());
        }
    }
}