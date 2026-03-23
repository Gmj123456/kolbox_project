package org.jeecg.modules.instagram.storecelebrity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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

/**
 * @Description: 私有网红品牌产品关联表
 * @Author: nqr
 * @Date: 2025-06-03
 * @Version: V1.0
 */
@Data
@TableName("store_celebrity_private_product")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreCelebrityPrivateProduct {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
    /**
     * 网红ID
     */
    @Excel(name = "网红ID", width = 15)
    @Schema(title =  "网红ID")
    private String celebrityId;
    /**
     * 品牌ID
     */
    @Excel(name = "品牌ID", width = 15)
    @Schema(title =  "品牌ID")
    private String brandId;
    /**
     * 产品ID
     */
    @Excel(name = "产品ID", width = 15)
    @Schema(title =  "产品ID")
    private String productId;
    /**
     * 活动ID
     */
    @Excel(name = "活动ID", width = 15)
    @Schema(title =  "活动ID")
    private String campaignId;
    /**
     * 关系类型（0:产品推广, 1:品牌宣传, 2:地推）
     */
    @Excel(name = "关系类型（0:产品推广, 1:品牌宣传, 2:地推）", width = 15)
    @Schema(title =  "关系类型（0:产品推广, 1:品牌宣传, 2:地推）")
    private Integer relationType;
    /**
     * 选中时间
     */
    @Excel(name = "选中时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "选中时间")
    private Date selectionTime;
    /**
     * 状态（0:已选中, 1:已合作, -1:未合作）
     */
    @Excel(name = "状态（0:已选中, 1:已合作, -1:未合作）", width = 15)
    @Schema(title =  "状态（0:已选中, 1:已合作, -1:未合作）")
    private Integer relationStatus;
    /**
     * 合作开始时间
     */
    @Excel(name = "合作开始时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "合作开始时间")
    private Date startTime;
    /**
     * 合作结束时间
     */
    @Excel(name = "合作结束时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "合作结束时间")
    private Date endTime;
    /**
     * 详情
     */
    @Excel(name = "详情", width = 15)
    @Schema(title =  "详情")
    private Object details;
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
     * 逻辑删除标志（0:未删除，1:已删除）
     */
    @Excel(name = "逻辑删除标志（0:未删除，1:已删除）", width = 15)
    @Schema(title =  "逻辑删除标志（0:未删除，1:已删除）")
    private Integer delFlag;

    /**
     * 网红顾问_ID
     */
    @Excel(name = " 网红顾问_ID", width = 15)
    @Schema(title =  " 网红顾问_ID")
    private String celebrityCounselorId;
    /**
     * 网红顾问名称
     */
    @Excel(name = " 网红顾问名称", width = 15)
    @Schema(title =  " 网红顾问名称")
    private String celebrityCounselorName;
    /**
     * 商务人员Id
     */
    @Excel(name = " 商务人员Id", width = 15)
    @Schema(title =  " 商务人员Id")
    private String counselorUserId;
    /**
     * 商务人员名称
     */
    @Excel(name = " 商务人员名称", width = 15)
    @Schema(title =  " 商务人员名称")
    private String counselorUserName;
}
