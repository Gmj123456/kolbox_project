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

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: 商家等级表
 * @Author: jeecg-boot
 * @Date: 2020-09-29
 * @Version: V1.0
 */
@Data
@TableName("store_seller_grade")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreSellerGrade {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
    /**
     * 等级名称
     */
    @Excel(name = "等级名称", width = 15)
    @Schema(title =  "等级名称")
    private String gradeName;
    /**
     * 邮件发送额度
     */
    @Excel(name = "邮件发送额度", width = 15)
    @Schema(title =  "邮件发送额度")
    private Integer emailPushCount;
    /**
     * 邮箱账号额度
     */
    @Excel(name = "邮箱账号额度", width = 15)
    @Schema(title =  "邮箱账号额度")
    private Integer emailAccountCount;
    /**
     * 排序码
     */
    @Excel(name = "排序码", width = 15)
    @Schema(title =  "排序码")
    private Integer sortCode;
    /**
     * 删除标记（0：未删除；1：已删除）
     */
    @Excel(name = "删除标记（0：未删除；1：已删除）", width = 15)
    @Schema(title =  "删除标记（0：未删除；1：已删除）")
    private Integer isDelete;
    /**
     * 有效标志
     */
    @Excel(name = "有效标志", width = 15)
    @Schema(title =  "有效标志")
    private Integer isEnabled;
    /**
     * 是否默认
     */
    @Excel(name = "是否默认", width = 15)
    @Schema(title =  "是否默认")
    private Integer isDefault;
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
    /**
     * 等级级别
     */
    private Integer gradeLever;
    /**
     * 等级周期
     */
    private Integer gradeCycle;
    /**
     * 等级价格
     */
    private BigDecimal gradePrice;
    /**
     * 等级描述
     */
    private String gradeDesc;

}
