package org.jeecg.modules.kol.wechatexcel.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "wechatdoc")
public class WechatDocConfig {
    private String templateDocId;
    private String privatePersonalTagsSheetId;
    private String privateSheetId;
    private String igTagSheetId;
    private String tkTagSheetId;
    private String ytTagSheetId;
    private String productTagSheetId;
    private String attributeSheetId;
    private String privateProductSheetId;
}
