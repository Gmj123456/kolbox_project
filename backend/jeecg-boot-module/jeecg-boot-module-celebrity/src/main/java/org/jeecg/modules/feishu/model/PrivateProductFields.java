package org.jeecg.modules.feishu.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lf
 * @version 1.0.0
 * @ClassName PrivateProductFields.java
 * @Description TODO
 * @createTime 2025年07月30日 14:10:00
 */
@Data
public class PrivateProductFields {
    @JsonProperty("网红名称")
    private String account;

    @JsonProperty("品牌")
    private String brandName;

    @JsonProperty("产品名称")
    private String productName;

    @JsonProperty("平台")
    private String platformTypeStr;

    @JsonProperty("合作状态")
    private String relationStatusStr;


    @JsonProperty("是否同步")
    private String isUpdate;

    @JsonProperty("同步状态")
    private String updateStatus;

    @JsonProperty("异常信息")
    private String errorMessage;



    @JsonProperty("网红名称")
    public void setAccount(List<Tag> tags) {
        this.account = tags != null ? tags.stream()
                .map(tag -> tag.getText() != null ? tag.getText() : "")
                .collect(Collectors.joining("|")) : "";
    };

    @JsonProperty("品牌")
    public void setBrandName(List<Tag> tags) {
        this.brandName = tags != null ? tags.stream()
                .map(tag -> tag.getText() != null ? tag.getText() : "")
                .collect(Collectors.joining("|")) : "";
    };

    @JsonProperty("产品名称")
    public void setProductName(List<Tag> tags) {
        this.productName = tags != null ? tags.stream()
                .map(tag -> tag.getText() != null ? tag.getText() : "")
                .collect(Collectors.joining("|")) : "";
    }

    @JsonProperty("异常信息")
    public void setErrorMessage(List<Tag> tags) {
        this.errorMessage = tags != null ? tags.stream()
                .map(tag -> tag.getText() != null ? tag.getText() : "")
                .collect(Collectors.joining("|")) : "";
    }
}
