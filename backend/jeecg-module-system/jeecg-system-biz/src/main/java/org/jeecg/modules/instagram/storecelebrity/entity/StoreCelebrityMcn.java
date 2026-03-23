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

/**
 * @Description: MCN管理
 * @Author: nqr
 * @Date: 2023-09-14
 * @Version: V1.0
 */
@Data
@TableName("store_celebrity_mcn")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreCelebrityMcn {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
    /**
     * MCN名称
     */
    @Excel(name = "MCN名称", width = 15)
    @Schema(title =  "MCN名称")
    private String mcnName;
    /**
     * MCN网红名单URL
     */
    @Excel(name = "MCN网红名单URL", width = 15)
    @Schema(title =  "MCN网红名单URL")
    private String mcnCelebrityUrl;
    /**
     * MCN邮箱
     */
    @Excel(name = "MCN邮箱", width = 15)
    @Schema(title =  "MCN邮箱")
    private String mcnEmail;
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
    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @Schema(title =  "备注")
    private String remark;
    /**
     * 删除状态(0-正常,1-已删除)
     */
    @Excel(name = "删除状态(0-正常,1-已删除)", width = 15)
    @Schema(title =  "删除状态(0-正常,1-已删除)")
    private Integer delFlag;
    /**
     * 排序
     */
    @Excel(name = "排序", width = 15)
    @Schema(title =  "排序")
    private Integer sort;
}
