package org.jeecg.modules.feishu.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: nqr
 * @Date: 2025/7/16 15:34
 * @Description:
 **/
@Data
public class Fields {
    @JsonProperty("平台")
    private String platformTypeStr;

    @JsonProperty("达人类型")
    private String influencerType;

    @JsonProperty("人群类型")
    private String audience;

    @JsonProperty("产品")
    private String productImportName;

    @JsonProperty("标签")
    private String tagName;

    @JsonProperty("物理空间")
    private String usageScenarios;

    @JsonProperty("强制更新")
    private String changeStatus;

    @JsonProperty("视频链接")
    private String videoUrl;

    @JsonProperty("标签类型")
    private String tagTypeStr;

    @JsonProperty("是否同步")
    private String isUpdate;

    @JsonProperty("异常信息")
    private String errorMsg;

    @JsonProperty("达人类型")
    public void setInfluencerType(List<String> influencerType) {
        this.influencerType = influencerType != null ? String.join("|", influencerType) : "";
    }

    @JsonProperty("人群类型")
    public void setAudience(List<String> audience) {
        this.audience = audience != null ? String.join("|", audience) : "";
    }

    @JsonProperty("标签")
    public void setTags(List<Tag> tags) {
        this.tagName = tags != null ? tags.stream()
                .map(tag -> tag.getText() != null ? tag.getText() : "")
                .collect(Collectors.joining("|")) : "";
    }
    @JsonProperty("异常信息")
    public void setErrorMsg(List<Tag> msgs) {
        this.errorMsg = msgs != null ? msgs.stream()
                .map(tag -> tag.getText() != null ? tag.getText() : "")
                .collect(Collectors.joining("|")) : "";
    }
    @JsonProperty("物理空间")
    public void setUsageScenarios(List<String> usageScenarios) {
        this.usageScenarios = usageScenarios != null ? String.join("|", usageScenarios) : "";
    }

    @JsonProperty("视频链接")
    public void setVideoUrl(List<VideoLink> videoLinks) {
        this.videoUrl = videoLinks != null ? videoLinks.stream()
                .map(videoLink -> videoLink.getText() != null ? videoLink.getText() : "")
                .collect(Collectors.joining("|")) : "";
    }
}