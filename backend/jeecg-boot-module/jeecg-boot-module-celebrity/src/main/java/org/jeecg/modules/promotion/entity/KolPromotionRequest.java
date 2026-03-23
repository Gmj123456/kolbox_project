package org.jeecg.modules.promotion.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("kol_promotion_request")
public class KolPromotionRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    @Excel(name = "姓名")
    private String name;

    @Excel(name = "公司名称")
    private String companyName;

    @Excel(name = "手机号")
    private String phone;

    @Excel(name = "邮箱")
    private String email;

    @Excel(name = "职位")
    private String position;

    @Excel(name = "所属行业")
    private String industry;

    @Excel(name = "计划推广地区")
    private String promotionArea;

    @Excel(name = "推广预算")
    private String promotionBudget;

    @Excel(name = "需求留言")
    private String demandMessage;

    @Excel(name = "创建人")
    private String createBy;

    @Excel(name = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @Excel(name = "更新人")
    private String updateBy;

    @Excel(name = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    @Excel(name = "删除标记", dicCode = "del_flag")
    private Integer delFlag;
}
