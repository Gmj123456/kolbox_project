package org.jeecg.modules.instagram.storecelebrity.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 网红跟进管理
 * @Author: jeecg-boot
 * @Date: 2020-05-28
 * @Version: V1.0
 */
@Data
@TableName("store_celebrity_followup")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreCelebrityFollowup {

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
     * 反馈状态（0：未反馈1：已反馈）
     */
    @Excel(name = "反馈状态（0：未反馈1：已反馈）", width = 15)
    @Schema(title =  "反馈状态（0：未反馈1：已反馈）")
    private Integer feedbackStatus;
    /**
     * 红人反馈
     */
    @Excel(name = "红人反馈", width = 15)
    @Schema(title =  "红人反馈")
    private String feedback;
    /**
     * 反馈时间
     */
    @Excel(name = "反馈时间", width = 20, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(title =  "反馈时间")

    private Date feedbackTime;
    /**
     * 订单号列表用逗号隔开
     */
    @Excel(name = "订单号列表用逗号隔开", width = 15)
    @Schema(title =  "订单号列表用逗号隔开")
    private String orderIds;
    /**
     * 运单号列表用逗号隔开
     */
    @Excel(name = "运单号列表用逗号隔开", width = 15)
    @Schema(title =  "运单号列表用逗号隔开")
    private String waybills;
    /**
     * 是否发货（-1：无需发货0：未发货1：已发货）
     */
    @Excel(name = "是否发货（-1：无需发货0：未发货1：已发货）", width = 15)
    @Schema(title =  "是否发货（-1：无需发货0：未发货1：已发货）")
    private Integer isShipment;
    /**
     * 跟进日期
     */
    @Excel(name = "跟进日期", width = 20, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(title =  "跟进日期")

    private Date followUpDate;
    /**
     * 跟进备注
     */
    @Excel(name = "跟进备注", width = 15)
    @Schema(title =  "跟进备注")
    private String followUpRemark;
    /**
     * 下次跟进日期
     */
    @Excel(name = "下次跟进日期", width = 20, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(title =  "下次跟进日期")

    private Date nextFollowUpDate;
    /**
     * 跟进状态（0：未跟进，1：跟进中，10：成功结束-1失败）
     */
    @Excel(name = "跟进状态（0：未跟进，1：跟进中，10：成功结束-1失败）", width = 15)
    @Schema(title =  "跟进状态（0：未跟进，1：跟进中，10：成功结束-1失败）")
    private Integer followUpStatus;
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
