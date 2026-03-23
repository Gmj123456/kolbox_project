package org.jeecg.modules.feishu.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "feishu")
public class FeishuAppConfig {
    private String appId;
    private String appSecret;
    private String appToken;
    private String tableId;
    private String viewId;
    private String sceneTableId;
    private String audienceTableId;
    private String expertsTableId;
    private String privateProductTableId;
    private String productTableId;
    private String productViewId;
    private String privateProductAppToken;
    private String tagProductTableId;
    private String tagProductViewId;
}
