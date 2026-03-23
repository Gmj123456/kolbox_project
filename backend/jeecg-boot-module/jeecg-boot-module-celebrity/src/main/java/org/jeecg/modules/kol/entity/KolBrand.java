package org.jeecg.modules.kol.entity;

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
 * @Description: 品牌表
 * @Author: xyc
 * @Date: 2024-12-18 11:53:47
 * @Version: V1.1
 * @History: V1.1 - [2024-12-18] - [重命名TiktokBrand==>KolBrand, 新加 product_name字段] - [xyc]
 */
@Data
@TableName("kol_brand")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class KolBrand {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
    /**
     * 品牌Logo的URL地址
     */
    @Excel(name = "品牌logo", width = 15)
    private String logoUrl;
    /**
     * 品牌名称
     */
    @Excel(name = "品牌名称", width = 15)
    @Schema(title =  "品牌名称")
    private String brandName;
    /**
     * 品牌描述
     */
    @Excel(name = "品牌描述", width = 15)
    private String description;
    /**
     * 品牌官方网站URL
     */
    @Excel(name = "品牌官方URL", width = 15)
    private String websiteUrl;
    /**
     * 品牌所属公司
     */
    @Excel(name = "所属公司", width = 15)
    private String companyName;
    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @Schema(title =  "备注")
    private String remark;
    /**
     * 创建人
     */
    @Schema(title =  "创建人")
    private String createBy;
    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "创建时间")
    private Date createTime;
    /**
     * 修改人
     */
    @Schema(title =  "修改人")
    private String updateBy;
    /**
     * 修改时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "修改时间")
    private Date updateTime;
    /**
     * 删除状态（0=未删除,1=已删除）
     */
    @Schema(title =  "删除状态（0=未删除,1=已删除）")
    private Integer isDelete;
}
