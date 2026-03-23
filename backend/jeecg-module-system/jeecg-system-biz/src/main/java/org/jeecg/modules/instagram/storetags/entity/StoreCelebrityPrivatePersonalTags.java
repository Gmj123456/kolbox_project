package org.jeecg.modules.instagram.storetags.entity;

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
 * @Description: 私有网红个性标签关联表
 * @Author: nqr
 * @Date: 2025-05-07
 * @Version: V1.0
 */
@Data
@TableName("store_celebrity_private_personal_tags")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreCelebrityPrivatePersonalTags {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
    /**
     * 网红ID, 关联store_celebrity_private.id
     */
    @Excel(name = "网红ID, 关联store_celebrity_private.id", width = 15)
    @Schema(title =  "网红ID, 关联store_celebrity_private.id")
    private String celebrityId;
    /**
     * 标签ID, 关联store_personal_tags.id
     */
    @Excel(name = "标签ID, 关联store_personal_tags.id", width = 15)
    @Schema(title =  "标签ID, 关联store_personal_tags.id")
    private String tagId;
    /**
     * 逻辑删除标志（0：未删除；1：已删除）
     */
    @Excel(name = "逻辑删除标志（0：未删除；1：已删除）", width = 15)
    @Schema(title =  "逻辑删除标志（0：未删除；1：已删除）")
    private Integer isDel;
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
}
