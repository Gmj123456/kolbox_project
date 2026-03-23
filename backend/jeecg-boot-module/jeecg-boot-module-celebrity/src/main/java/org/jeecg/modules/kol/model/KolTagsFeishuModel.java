package org.jeecg.modules.kol.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.Objects;

/**
 * @description: 飞书普通表格接收实体
 * @author: nqr
 * @date: 2025/9/16 18:39
 **/
@Data
public class KolTagsFeishuModel {
    @Excel(name = "品牌", width = 15)
    private String brandName;
    private String brandId;
    @Excel(name = "产品", width = 15)
    private String productName;
    private String productId;
    @Excel(name = "标签", width = 15)
    private String tagName;
    @Excel(name = "标签类型", width = 15)
    private String tagTypeStr;
    /**
     * 标签类型：0=hashtag 1=keyword 2=rootVideo 3=rootAccount
     */
    private Integer tagType;
    /**
     * 达人类型
     */
    @Excel(name = "达人类型1", width = 15)
    private String influencerType;
    @Excel(name = "达人类型2", width = 15)
    private String influencerType2;
    @Excel(name = "达人类型3", width = 15)
    private String influencerType3;
    @Excel(name = "达人类型4", width = 15)
    private String influencerType4;
    @Excel(name = "达人类型5", width = 15)
    private String influencerType5;
    /**
     * 更新标签状态 默认不更新
     */
    @Excel(name = "强制更新", width = 15)
    private String changeStatus;
    /**
     * 是否同步
     */
    @Excel(name = "是否同步", width = 15)
    private String isUpdate;
    @Excel(name = "行号", width = 15)
    private Integer rowNum;
    /**
     * 平台类型（0:IG;1:YT;2:TK）
     */
    @Schema(title =  "平台类型")
    private Integer platformType;
    /**
     * 视频链接(标签类型为种子视频时生效)
     */
    @Excel(name = "视频链接", width = 15)
    private String videoUrl;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KolTagsFeishuModel feishuModel = (KolTagsFeishuModel) o;
        return Objects.equals(tagName, feishuModel.tagName) && Objects.equals(platformType, feishuModel.platformType) && Objects.equals(tagType, feishuModel.tagType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tagName, platformType, tagType);
    }
}
