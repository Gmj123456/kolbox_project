package org.jeecg.modules.instagram.storepromotion.entity;

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

/**
 * @Description: 产品网红历史匹配方案
 * @Author: nqr
 * @Date: 2023-09-02
 * @Version: V1.0
 */
@Data
@TableName("store_promotion_project")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StorePromotionProject {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
    /**
     * 商家ID
     */
    @Excel(name = "商家ID", width = 15)
    @Schema(title =  "商家ID")
    private String sellerId;
    /**
     * 商家昵称
     */
    @Excel(name = "商家昵称", width = 15)
    @Schema(title =  "商家昵称")
    private String sellerName;
    /**
     * 推广单号
     */
    @Excel(name = "推广单号", width = 15)
    @Schema(title =  "推广单号")
    private String extensionCode;
    /**
     * 推广ID
     */
    @Excel(name = "推广ID", width = 15)
    @Schema(title =  "推广ID")
    private String promId;
    /**
     * 方案名称
     */
    @Excel(name = "方案名称", width = 15)
    @Schema(title =  "方案名称")
    private String name;
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
    private java.util.Date createTime;
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
    private java.util.Date updateTime;
}
