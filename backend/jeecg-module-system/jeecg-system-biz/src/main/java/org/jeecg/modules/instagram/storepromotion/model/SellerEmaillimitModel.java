package org.jeecg.modules.instagram.storepromotion.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class SellerEmaillimitModel {
    /**
     * 商家名称
     */
    @Excel(name = "商家名称", width = 15)
    @Schema(title =  "商家名称")
    private String sellerName;

    /**
     * 邀请人邮件增加额度
     */
    @Excel(name = "邮件增加额度", width = 15)
    @Schema(title =  "邮件增加额度")
    private Integer emailCount;

    /**
     * 邀请人名称
     */
    @Excel(name = "邀请人名称", width = 15)
    @Schema(title =  "邀请人名称")
    private String inviterName;
    /**
     * 被邀请人名称
     */
    @Excel(name = "被邀请人名称", width = 15)
    @Schema(title =  "被邀请人名称")
    private String inviteeName;

    /**
     * 额度类型
     */
    @Excel(name = "额度类型", width = 15)
    @Schema(title =  "额度类型")
    private String sellerType;


    /**
     * 额度增加来源（0：等级；1：邀请）
     */
    @Excel(name = "额度增加来源（0：等级；1：邀请）", width = 15)
    @Schema(title =  "额度增加来源（0：等级；1：邀请）")
    private Integer source;
    /**
     * 可维护邮箱
     */
    @Excel(name = "可维护邮箱", width = 15)
    @Schema(title =  "可维护邮箱")
    private Integer emailAccountCount;



    /**
     * 创建时间
     */
    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "创建时间")
    private java.util.Date createTime;
}
