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

/**
 * @Description: 网红黑名单
 * @Author: dongruyang
 * @Date: 2025-05-10
 * @Version: V1.0
 */
@Data
@TableName("kol_blacklist")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class KolBlacklist {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
    /**
     * 网红唯一ID
     */
    @Excel(name = "网红唯一ID", width = 15)
    @Schema(title =  "网红唯一ID")
    private String uniqueId;
    /**
     * 平台类型（0:Instagram;1:YouTube;2:TikTok）
     */
    @Excel(name = "平台类型（0:Instagram;1:YouTube;2:TikTok）", width = 15)
    @Schema(title =  "平台类型（0:Instagram;1:YouTube;2:TikTok）")
    private Integer platformType;
    /**
     * 网红顾问ID
     */
    @Excel(name = "网红顾问ID", width = 15)
    @Schema(title =  "网红顾问ID")
    private String counselorId;
    /**
     * 网红顾问名称
     */
    @Excel(name = "网红顾问名称", width = 15)
    @Schema(title =  "网红顾问名称")
    private String counselorName;
    /**
     * 网红顾问短名称
     */
    @Excel(name = "网红顾问短名称", width = 15)
    @Schema(title =  "网红顾问短名称")
    private String counselorShortName;
    /**
     * 拉黑时间
     */
    @Excel(name = "拉黑时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "拉黑时间")
    private java.util.Date blacklistTime;
    /**
     * 拉黑原因
     */
    @Excel(name = "拉黑原因", width = 15)
    @Schema(title =  "拉黑原因")
    private String blacklistReason;
    /**
     * 删除状态（0:未删除;1:已删除）
     */
    @Excel(name = "删除状态（0:未删除;1:已删除）", width = 15)
    @Schema(title =  "删除状态（0:未删除;1:已删除）")
    private Integer isDelete;
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

    private String celebrityId;
}
