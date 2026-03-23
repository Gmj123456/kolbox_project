package org.jeecg.modules.tiktok.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @Description: TikTok网红带货产品信息表
 * @Author: nqr
 * @Date: 2025-10-08
 * @Version: V1.0
 */
@Data
@TableName("tiktok_celebrity_product")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TiktokCelebrityProduct {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键ID")
    private String id;
    /**
     * 产品ID
     */
    @Excel(name = "产品ID", width = 15)
    @Schema(title =  "产品ID")
    private String productId;
    /**
     * 用户唯一ID
     */
    @Excel(name = "用户唯一ID", width = 15)
    @Schema(title =  "用户唯一ID")
    private String uniqueId;
    /**
     * 作者ID
     */
    @Excel(name = "作者ID", width = 15)
    @Schema(title =  "作者ID")
    private String authorId;
    /**
     * 安全用户ID
     */
    @Excel(name = "安全用户ID", width = 15)
    @Schema(title =  "安全用户ID")
    private String secUid;
    /**
     * 产品标题
     */
    @Excel(name = "产品标题", width = 15)
    @Schema(title =  "产品标题")
    private String title;
    /**
     * 简化标题
     */
    @Excel(name = "简化标题", width = 15)
    @Schema(title =  "简化标题")
    private String elasticTitle;
    /**
     * 卖家ID
     */
    @Excel(name = "卖家ID", width = 15)
    @Schema(title =  "卖家ID")
    private String sellerId;
    /**
     * 货币
     */
    @Excel(name = "货币", width = 15)
    @Schema(title =  "货币")
    private String currency;
    /**
     * 产品价格
     */
    @Excel(name = "产品价格", width = 15)
    @Schema(title =  "产品价格")
    private java.math.BigDecimal price;
    /**
     * 市场价格
     */
    @Excel(name = "市场价格", width = 15)
    @Schema(title =  "市场价格")
    private java.math.BigDecimal marketPrice;
    /**
     * 封面图URL
     */
    @Excel(name = "封面图URL", width = 15)
    @Schema(title =  "封面图URL")
    private String coverUrl;
    /**
     * 详情URL
     */
    @Excel(name = "详情URL", width = 15)
    @Schema(title =  "详情URL")
    private String detailUrl;
    /**
     * 是否在店铺内(0:否，1:是)
     */
    @Excel(name = "是否在店铺内(0:否，1:是)", width = 15)
    @Schema(title =  "是否在店铺内(0:否，1:是)")
    private Integer inShop;
    /**
     * 是否平台产品(0:否，1:是)
     */
    @Excel(name = "是否平台产品(0:否，1:是)", width = 15)
    @Schema(title =  "是否平台产品(0:否，1:是)")
    private Integer isPlatformProduct;
    /**
     * 创建人
     */
    @Excel(name = "创建人", width = 15)
    @Schema(title =  "创建人")
    private String createBy;
    /**
     * 创建时间
     */
    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "创建时间")
    private Date createTime;
    /**
     * 更新人
     */
    @Excel(name = "更新人", width = 15)
    @Schema(title =  "更新人")
    private String updateBy;
    /**
     * 更新时间
     */
    @Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "更新时间")
    private Date updateTime;
    /**
     * 逻辑删除标志(0:未删除，1:删除)
     */
    @Excel(name = "逻辑删除标志(0:未删除，1:删除)", width = 15)
    @Schema(title =  "逻辑删除标志(0:未删除，1:删除)")
    private Integer delFlag;
    private String seoUrl;

    /**
     * 分类信息
     */
    @TableField(exist = false)
    private List<TiktokCelebrityProductCategory> categories;
}
