package org.jeecg.modules.instagram.storepromotion.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "wechat.work")
public class WeChatWorkConfig {
    
    /**
     * 企业微信机器人Webhook URL
     */
    private String webhookUrl;
}