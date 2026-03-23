package org.jeecg.modules.instagram.storebasics.entity;

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
 * @Description: 推广平台管理
 * @Author: jeecg-boot
 * @Date: 2021-01-23
 * @Version: V1.0
 */
@Data
@TableName("promotion_platform")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PromotionPlatform {

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "id")
    private String id;
    /**
     * 平台名称
     */
    @Excel(name = "平台名称", width = 15)
    @Schema(title =  "平台名称")
    private String name;
    /**
     * 二维码
     */
    @Excel(name = "二维码", width = 15)
    @Schema(title =  "二维码")
    private String qrCode;
    /**
     * 创建日期
     */
    @Excel(name = "创建日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "创建日期")
    private Date createTime;
    /**
     * 创建人
     */
    @Excel(name = "创建人", width = 15)
    @Schema(title =  "创建人")
    private String createBy;
    /**
     * 修改日期
     */
    @Excel(name = "修改日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "修改日期")
    private Date updateTime;
    /**
     * 修改人
     */
    @Excel(name = "修改人", width = 15)
    @Schema(title =  "修改人")
    private String updateBy;
}
