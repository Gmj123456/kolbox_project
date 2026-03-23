package org.jeecg.modules.tiktok.entity;

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

import java.math.BigInteger;
import java.util.Date;

/**
 * @Description: tk标签网红顾问表
 * @Author: dongruyang
 * @Date: 2024-08-28
 * @Version: V1.0
 */
@Data
@TableName("tiktok_celebrity_tags_counselor")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TiktokCelebrityTagsCounselor {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
    /**
     * 网红标签表主键
     */
    @Excel(name = "网红标签表主键", width = 15)
    @Schema(title =  "网红标签表主键")
    private String tiktokCelebrityTagsId;
    /**
     * 网红顾问_ID
     */
    @Excel(name = "网红顾问_ID", width = 15)
    @Schema(title =  "网红顾问_ID")
    private String counselorId;
    /**
     * 网红顾问名称
     */
    @Excel(name = "网红顾问名称", width = 15)
    @Schema(title =  "网红顾问名称")
    private String counselorName;
    /**
     * 网红唯一ID
     */
    @Excel(name = "网红唯一ID", width = 15)
    @Schema(title =  "网红唯一ID")
    private String uniqueId;
    /**
     * 网红作者id
     */
    @Excel(name = "网红作者id", width = 15)
    @Schema(title =  "网红作者id")
    private String authorId;
    /**
     * 标签名称
     */
    @Excel(name = "标签名称", width = 15)
    @Schema(title =  "标签名称")
    private String tagName;

    /**
     * tag类型（tag类型：0=hashtag 1=keyword 2=rootVideo 3=rootAccount）
     */
    @Excel(name = "tag类型：0=hashtag 1=keyword 2=rootVideo 3=rootAccount", width = 15)
    @Schema(title =  "tag类型：0=hashtag 1=keyword 2=rootVideo 3=rootAccount")
    private Integer tagType;
    /**
     * 分配时间
     */
    @Excel(name = "分配时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "分配时间")
    private Date allotTime;
    /**
     * 删除状态（0=未删除,1=已删除）
     */
    @Excel(name = "删除状态（0=未删除,1=已删除）", width = 15)
    @Schema(title =  "删除状态（0=未删除,1=已删除）")
    private Integer isDelete;
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
     * 分配日志ID
     */
    @Excel(name = "分配日志ID", width = 15)
    @Schema(title =  "分配日志ID")
    private String allotLogId;
    /**
     * 分配日志明细ID
     */
    @Excel(name = "分配日志明细ID", width = 15)
    @Schema(title =  "分配日志明细ID")
    private String allotLogDetailId;

    /**
     * 网红国家
     */
    @Excel(name = "网红国家", width = 15)
    @Schema(title =  "网红国家")
    private String country;

    /**
     * 网红粉丝数
     */
    @Excel(name = "网红粉丝数", width = 15)
    @Schema(title =  "网红粉丝数")
    private BigInteger authorFollowerCount;

    private String productId;
    private String attributeId;
}
