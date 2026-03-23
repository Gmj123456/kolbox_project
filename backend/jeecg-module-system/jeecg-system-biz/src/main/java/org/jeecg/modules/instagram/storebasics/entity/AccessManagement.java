package org.jeecg.modules.instagram.storebasics.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 访问管理
 * @Author: jeecg-boot
 * @Date: 2021-01-23
 * @Version: V1.0
 */
@Data
@TableName("access_management")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AccessManagement {

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "id")
    private String id;
    /**
     * ip
     */
    @Excel(name = "ip", width = 15)
    @Schema(title =  "ip")
    private String ip;
    /**
     * 地区
     */
    @Excel(name = "地区", width = 15)
    @Schema(title =  "district")
    private String district;
    /**
     * createTime
     */
    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "createTime")
    private Date createTime;
    /**
     * 平台
     */
    @Excel(name = "平台来源", width = 15)
    @Schema(title =  "origin")
    private String origin;
    /**
     * 平台Id
     */
    @Excel(name = "平台", width = 15)
    @Schema(title =  "originId")
    private String originId;
}
