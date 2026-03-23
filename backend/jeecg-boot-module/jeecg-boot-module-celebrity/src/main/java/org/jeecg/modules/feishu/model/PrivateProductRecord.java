package org.jeecg.modules.feishu.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @Author: nqr
 * @Date: 2025/7/16 15:35
 * @Description:
 **/
@Data
public  class PrivateProductRecord {
    @JsonProperty("recordId")
    private String recordId;

    @JsonProperty("fields")
    private PrivateProductFields fields;
}