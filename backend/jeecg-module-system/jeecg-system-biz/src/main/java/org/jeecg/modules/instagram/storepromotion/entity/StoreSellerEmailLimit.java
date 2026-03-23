package org.jeecg.modules.instagram.storepromotion.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 邮件发送额度明细表
 * @Author: jeecg-boot
 * @Date: 2020-10-02
 * @Version: V1.0
 */
@Data
@TableName("store_seller_email_limit")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreSellerEmailLimit {

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
     * 商家名称
     */
    @Excel(name = "商家名称", width = 15)
    @Schema(title =  "商家名称")
    private String sellerName;
    /**
     * 邀请人邮件增加额度
     */
    @Excel(name = "邀请人邮件增加额度", width = 15)
    @Schema(title =  "邀请人邮件增加额度")
    private Integer sellerEmailCount;
    /**
     * 被邀请人邮件增加额度
     */
    @Excel(name = "被邀请人邮件增加额度", width = 15)
    @Schema(title =  "被邀请人邮件增加额度")
    private Integer invitationEmailCount;
    /**
     * 额度增加来源（0：等级；1：邀请）
     */
    @Excel(name = "额度增加来源（0：等级；1：邀请）", width = 15)
    @Schema(title =  "额度增加来源（0：等级；1：邀请）")
    private Integer source;
    /**
     * 被邀请人ID
     */
    @Excel(name = "被邀请人ID", width = 15)
    @Schema(title =  "被邀请人ID")
    private String invitationUserId;
    /**
     * 被邀请人名称
     */
    @Excel(name = "被邀请人名称", width = 15)
    @Schema(title =  "被邀请人名称")
    private String invitationUserName;
    /**
     * 被邀请人等级ID
     */
    @Excel(name = "被邀请人等级ID", width = 15)
    @Schema(title =  "被邀请人等级ID")
    private String invitationUserGradeId;
    /**
     * 被邀请人等级名称
     */
    @Excel(name = "被邀请人等级名称", width = 15)
    @Schema(title =  "被邀请人等级名称")
    private String invitationUserGradeName;
    /**
     * 消费明细ID
     */
    @Excel(name = "消费明细ID", width = 15)
    @Schema(title =  "消费明细ID")
    private String consumeId;
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
    /**
     * 邮箱增加额度（等级）
     */
    @Excel(name = "邮箱增加额度", width = 15)
    @Schema(title =  "邮箱增加额度")
    private Integer emailAccountCount;
}
