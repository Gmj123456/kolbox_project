package org.jeecg.modules.kol.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * @Description: 网红标签表
 * @Author: xyc
 * @Date: 2025-01-02 17:26:52
 * @Version: V1.0
 */
@Data
@TableName("kol_tags")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class KolTags extends ExtendedEntity {

    /**
     * 标签名称
     */
    @Excel(name = "标签", width = 15)
    @Schema(title =  "标签名称")
    private String tagName;
    /**
     * tag状态
     */
    private Integer tagStatus;
    /**
     * 最终状态（网红爬取完成）0：未完成；99：已完成
     */
    private Integer finalStatus;
    /**
     * 最后执行时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "最后执行时间")
    private Date lastExecutionTime;


    /**
     * 排序码
     */
    @Excel(name = "排序", width = 15)
    @Schema(title =  "排序")
    private Integer sortCode;
    /**
     * 平台类型（0:IG;1:YT;2:TK）
     */
    @Schema(title =  "平台类型")
    private Integer platformType;

    /**
     * 大类标签
     */
    @Excel(name = "主类目", width = 15)
    private String storeTags;
    /**
     * 标签观看数
     */
    @Excel(name = "标签观看数", width = 15)
    @Schema(title =  "标签观看数")
    private BigDecimal tagViews;

    /**
     * 标签帖子数
     */
    @Excel(name = "标签帖子数", width = 15)
    @Schema(title =  "标签帖子数")
    private BigDecimal tagPostsCount;

    /**
     * 标签网红数
     */
    @Excel(name = "标签网红数", width = 15)
    @Schema(title =  "标签网红数")
    private Integer tagUserCount;


    /**
     * 标签重试次数
     */
    @Excel(name = "标签重试次数", width = 15)
    @Schema(title =  "标签重试次数")
    private Integer tagRetryCount;


    /**
     * app端-标签状态
     */
    private Integer appStatus;

    /**
     * app端-是否存在下一页
     */
    private String appHasMore;
    /**
     * 品牌名称
     */
    @TableField(exist = false)
    @Excel(name = "品牌", width = 15)
    private String brandName;
    @TableField(exist = false)
    private String brandId;
    @TableField(exist = false)
    private String brandIds;
    /**
     * 细分类目
     */
    private String subCategory;
    /**
     * 适合产品
     */
    @Excel(name = "适合产品", width = 15)
    private String tagProduct;
    /**
     * 受众人群
     */
    @Excel(name = "受众人群", width = 15)
    private String tagAudience;

    /**
     * 类目id
     */
    @Schema(title =  "类目主键id")
    private String categoryId;

    /**
     * 类目名称
     */
    @Excel(name = "类目", width = 15)
    @Schema(title =  "类目名称")
    private String categoryName;


    /**
     * 标签类型：0=hashtag 1=keyword 2=rootVideo 3=rootAccount
     */
    @Excel(name = "标签类型", width = 15, dicCode = "tag_type")
    @Schema(title =  "标签类型")
    private Integer tagType;
    @TableField(exist = false)
    private String tagTypeStr;


    /**
     * 视频链接(标签类型为种子视频时生效)
     */
    @Excel(name = "视频链接", width = 15)
    @Schema(title =  "视频链接")
    private String videoUrl;
    @TableField(exist = false)
    private String storeTagsId;
    @TableField(exist = false)
    private String productName;
    /**
     * 更新标签状态 默认不更新
     */
    @Excel(name = "强制更新", width = 15)
    @TableField(exist = false)
    private String changeStatus;
    /**
     * 是否活跃标签 0:否；1：是
     */
    @Schema(title =  "是否活跃标签")
    private Integer isActive;

    /**
     * 导入时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "导入时间")
    private Date importTime;

    private Integer unassignedCount;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "导入时间")
    private Date snapshotTime;
    private String countryDataJson;
    /**
     * 按国家分组的总网红数量 (JSON: {"US": 123, "GB": 456})
     */
    private String totalKolsJson;
    /**
     * 按国家分组的已分配网红数量 (JSON)
     */
    private String allocatedKolsJson;
    /**
     * 按国家分组的未分配网红数量 (JSON)
     */
    private String unallocatedKolsJson;
    /**
     * 按国家分组的含有邮箱网红数量 (JSON)
     */
    private String withEmailKolsJson;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "统计时间")
    private String statsTime;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KolTags kolTags = (KolTags) o;
        return Objects.equals(tagName, kolTags.tagName) && Objects.equals(platformType, kolTags.platformType) && Objects.equals(tagType, kolTags.tagType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tagName, platformType, tagType);
    }

}
