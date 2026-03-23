package org.jeecg.modules.tiktok.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 标签类目表
 * @Author: dongruyang
 * @Date: 2024-07-18
 * @Version: V1.0
 */
@Data
@TableName("tiktok_tags_category")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TiktokTagsCategory {

    /**
     * tag名称
     */
    @Excel(name = "tag名称", width = 15)
    @Schema(title =  "tag名称")
    private String tagName;
    /**
     * 排序
     */
    @Excel(name = "排序", width = 15)
    @Schema(title =  "排序")
    private Integer sortCode;
    /**
     * 平台类型（2:TK;1:YT;1=0:IG）
     */
    @Excel(name = "平台类型（2:TK;1:YT;1=0:IG）", width = 15)
    @Schema(title =  "平台类型（2:TK;1:YT;1=0:IG）")
    private Integer platformType;
    /**
     * 大类标签JSON
     */
    @Excel(name = "大类标签JSON", width = 15)
    @Schema(title =  "大类标签JSON")
    private String storeTags;
    /**
     * 细分类目
     */
    @Excel(name = "细分类目", width = 15)
    @Schema(title =  "细分类目")
    private String subCategory;
    /**
     * 受众人群
     */
    @Excel(name = "受众人群", width = 15)
    @Schema(title =  "受众人群")
    private String tagAudience;
    /**
     * 适合产品
     */
    @Excel(name = "适合产品", width = 15)
    @Schema(title =  "适合产品")
    private String tagProduct;
    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @Schema(title =  "备注")
    private String remark;
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
     * 修改人
     */
    @Excel(name = "修改人", width = 15)
    @Schema(title =  "修改人")
    private String updateBy;
    /**
     * 修改时间
     */
    @Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "修改时间")
    private Date updateTime;
}
