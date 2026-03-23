package org.jeecg.modules.feishu.utils;

import com.lark.oapi.Client;
import org.jeecg.modules.feishu.config.FeishuAppConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: nqr
 * @Date: 2025/7/17 13:58
 * @Description:
 **/
@Configuration
public class FeishuClientUtils {

    private final FeishuAppConfig feishuAppConfig;

    // Spring 将在此处注入 FeishuAppConfig
    public FeishuClientUtils(FeishuAppConfig feishuAppConfig) {
        this.feishuAppConfig = feishuAppConfig;
    }

    @Bean // 此方法告诉 Spring 创建一个单例 bean
    public Client getClient() {
        return Client.newBuilder(feishuAppConfig.getAppId(), feishuAppConfig.getAppSecret()).build();
    }
}
