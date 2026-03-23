package org.jeecg.modules.instagram.storemerchant.entity;

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
 * @Description: 爬取任务
 * @Author: nqr
 * @Date: 2020-04-21
 * @Version: V1.0
 */
@Data
@TableName("store_task")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreTask {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
    /**
     * 任务名称
     */
    @Schema(title =  "任务名称")
    private String taskName;
    /**
     * 任务类型：0=递归执行
     */
    @Schema(title =  "任务类型")
    private Integer taskType;
    /**
     * 任务状态：0=未开始;1=执行中;90=中断完成;99=成功完成
     */
    @Schema(title =  "任务状态")
    private Integer taskStatus;
    /**
     * 起始执行账号
     */
    @Excel(name = "起始执行账号", width = 15)
    @Schema(title =  "起始执行账号")
    private String firstKolAccount;
    /**
     * 起始执行时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "起始执行时间")
    private Date firstExecDate;
    /**
     * 最后执行账号
     */
    @Schema(title =  "最后执行账号")
    private String lastKolAccount;
    /**
     * 最后更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "最后更新时间")
    private Date lastUpdateDate;
    /**
     * 平台类型 0=instagram;1=youtube
     */
    @Excel(name = "平台类型", width = 15)
    @Schema(title =  "平台类型 0=instagram;1=youtube")
    private Integer platformType;
    /**
     * 查询关键词
     */
    @Excel(name = "查询关键词", width = 15)
    @Schema(title =  "查询关键词")
    private String keyword;
    /**
     * 最低粉丝量
     */
    @Schema(title =  "最低粉丝量")
    private Integer minFansNum;
    /**
     * 最低播放量
     */
    @Schema(title =  "最低播放量")
    private Integer minPlayNum;
    /**
     * 备注
     */
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
     * 是否存储层级 0不存储 1存储
     */
    @Excel(name = "是否存储层级", width = 15)
    private Integer isLevel;
}
