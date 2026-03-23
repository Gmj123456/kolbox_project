package org.jeecg.modules.instagram.storecelebrity.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author lf
 * @version 1.0.0
 * @ClassName CelebrityAbnormalStatusRequest.java
 * @Description TODO
 * @createTime 2025年04月09日 16:43:00
 */
@Data
public class CelebrityAbnormalStatusRequest {
    @JsonProperty("celebrityIds")
    private String celebrityIds; // 接收逗号分隔的字符串

    @JsonProperty("isAbnormalAccount")
    private Integer isAbnormalAccount; // 接收异常状态
}
