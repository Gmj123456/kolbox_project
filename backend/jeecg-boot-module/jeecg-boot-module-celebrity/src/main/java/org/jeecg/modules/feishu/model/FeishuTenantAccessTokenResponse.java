package org.jeecg.modules.feishu.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 飞书租户访问令牌响应对象
 * @Author: dongruyang
 * @Date: 2025-09-15
 * @Version: V1.0
 */
@Data
public class FeishuTenantAccessTokenResponse {

    /**
     * 响应码，0表示成功
     */
    @JsonProperty("code")
    private Integer code;

    /**
     * 令牌过期时间（秒）
     */
    @JsonProperty("expire")
    private Integer expire;

    /**
     * 响应消息
     */
    @JsonProperty("msg")
    private String msg;

    /**
     * 租户访问令牌
     */
    @JsonProperty("tenant_access_token")
    private String tenantAccessToken;

    /**
     * 判断响应是否成功
     * @return true表示成功，false表示失败
     */
    public boolean isSuccess() {
        return code != null && code == 0;
    }
}