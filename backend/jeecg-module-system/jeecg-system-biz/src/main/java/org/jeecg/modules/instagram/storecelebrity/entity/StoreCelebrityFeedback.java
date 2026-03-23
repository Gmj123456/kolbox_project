package org.jeecg.modules.instagram.storecelebrity.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 反馈功能
 * @Author: jeecg-boot
 * @Date: 2021-03-01
 * @Version: V1.0
 */
@Data
@TableName("store_celebrity_feedback")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreCelebrityFeedback {

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
     * 所在国家
     */
    @Excel(name = "所在国家", width = 15)
    @Schema(title =  "所在国家")
    private String country;
    /**
     * 喜欢的标签
     */
    @Excel(name = "喜欢的标签", width = 15)
    @Schema(title =  "喜欢的标签")
    private String likeTags;
    /**
     * 性別（0：男；1：女）
     */
    @Excel(name = "性別（0：男；1：女）", width = 15)
    @Schema(title =  "性別（0：男；1：女）")
    private Integer gender;
    /**
     * 是否存在 0：否；1：是（默认）
     */
    @Excel(name = "是否存在 0：否；1：是（默认）", width = 15)
    @Schema(title =  "是否存在 0：否；1：是（默认）")
    private Integer isExistence;
    /**
     * 粉丝数、播放数、帖子数误差是否过大 0（默认）：否 1：是
     */
    @Excel(name = "粉丝数、播放数、帖子数误差是否过大 0（默认）：否 1：是", width = 15)
    @Schema(title =  "粉丝数、播放数、帖子数误差是否过大 0（默认）：否 1：是")
    private Integer isNumWrong;
    /**
     * 是否处理 0：未处理(默认) 1：已处理
     */
    @Excel(name = "是否处理 0：未处理(默认) 1：已处理", width = 15)
    @Schema(title =  "是否处理 0：未处理(默认) 1：已处理")
    private Integer isDispose;
    /**
     * 处理人ID
     */
    @Excel(name = "处理人ID", width = 15)
    @Schema(title =  "处理人ID")
    private String disposeUserId;
    /**
     * 处理人名称
     */
    @Excel(name = "处理人名称", width = 15)
    @Schema(title =  "处理人名称")
    private String disposeUserName;
    /**
     * 创建用户类型 0：后台；1：商家
     */
    @Excel(name = "创建用户类型 0：后台；1：商家", width = 15)
    @Schema(title =  "创建用户类型 0：后台；1：商家")
    private Integer createUserType;
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
