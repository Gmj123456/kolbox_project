package org.jeecg.modules.kol.wechatexcel.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: nqr
 * @Date: 2025/8/25 14:02
 * @Description:
 **/
@Data
public class SmartSheetResponse {
    private int errcode;
    private String errmsg;
    private int total;
    @JsonProperty("has_more")
    private boolean hasMore;
    private long next;
    private List<Record> records;
    private int ver;
}
