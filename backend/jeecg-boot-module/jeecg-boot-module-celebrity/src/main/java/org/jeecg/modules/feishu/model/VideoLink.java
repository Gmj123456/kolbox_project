package org.jeecg.modules.feishu.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @Author: nqr
 * @Date: 2025/7/16 15:34
 * @Description:
 **/
@Data
public class VideoLink {
    @JsonProperty("link")
    private String link;

    @JsonProperty("text")
    private String text;

    @JsonProperty("type")
    private String type;
}
