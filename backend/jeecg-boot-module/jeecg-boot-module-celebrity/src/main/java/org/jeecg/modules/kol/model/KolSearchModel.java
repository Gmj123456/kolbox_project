package org.jeecg.modules.kol.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;
import java.util.Objects;


/**
 * @Description: 通用网红查询条件模型
 * @Author: xyc
 * @Date: 2024-12-17 15:43:27
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class KolSearchModel extends KolBaseSearchModel {
    private static final long serialVersionUID = 1L;

    /**
     * 网红账号
     */
    private String uniqueId;

    /**
     * 网红昵称
     */
    private String nickname;

    /**
     * 平台类型 0=IG 1=YT 2=TK
     */
    private Integer platformType;

    /**
     * 黑名单
     */
    private int includeBlacklist;
    /**
     * 类目Id
     */
    private String categoryIds;
    /**
     * 产品Id
     */
    private String productId;
    /**
     * 社媒属性id(一级)
     */
    private String attributeIds;
    /**
     * 网红顾问列表
     */
    private List<String> celebrityCounselorList;
    /**
     * 网红顾问id
     */
    private String counselorId;
    /**
     * 标签id
     */
    private String tagIds;
    /**
     * 产品Id
     */
    private String productIds;
    private String brandId;
    /**
     * 是否强制更新
     */
    private Integer forceUpdate;
    private String tempTableName;
    private String tempUniqueTableName;
    private Integer total;
    private String attributeName;

    private Integer allotType;
    private String userId;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date allotTime;

    private List<String> productTags;
    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        KolSearchModel model = (KolSearchModel) object;
        return includeBlacklist == model.includeBlacklist && Objects.equals(uniqueId, model.uniqueId) && Objects.equals(nickname, model.nickname) && Objects.equals(platformType, model.platformType) && Objects.equals(categoryIds, model.categoryIds) && Objects.equals(productId, model.productId) && Objects.equals(attributeIds, model.attributeIds) && Objects.equals(celebrityCounselorList, model.celebrityCounselorList) && Objects.equals(counselorId, model.counselorId) && Objects.equals(tagIds, model.tagIds) && Objects.equals(productIds, model.productIds) && Objects.equals(brandId, model.brandId) && Objects.equals(forceUpdate, model.forceUpdate) && Objects.equals(total, model.total) && Objects.equals(attributeName, model.attributeName) && Objects.equals(allotType, model.allotType) && Objects.equals(userId, model.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), uniqueId, nickname, platformType, includeBlacklist, categoryIds, productId, attributeIds, celebrityCounselorList, counselorId, tagIds, productIds, brandId, forceUpdate, total, attributeName, allotType, userId);
    }
}
