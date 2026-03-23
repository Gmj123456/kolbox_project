package org.jeecg.modules.scrape.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.beans.Transient;
import java.util.Date;

@Data
@TableName("scrape_monitor")
public class ScrapeMonitor {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    @Excel(name = "IP地址")
    private String ipAddress;

    @Excel(name = "设备编码")
    private String deviceCode;

    @Excel(name = "远程ID")
    private String remoteId;

    @Excel(name = "爬虫名称")
    private String crawlerName;

    @Excel(name = "任务内容")
    private String taskContent;

    @Excel(name = "任务状态")
    private Integer taskStatus;

    @Excel(name = "是否提醒", dicCode = "yn_status")
    @Dict(dicCode = "yn_status") // 关联数据字典
    private Integer isReminder;

    @Excel(name = "剩余次数")
    private Integer remainingTimes;

    @Excel(name = "最后执行时间", format = "yyyy-MM-dd HH:mm:ss")
    private Date lastExecutionTime;

    @Excel(name = "备注")
    private String remark;

    @Excel(name = "创建人")
    private String createBy;

    @Excel(name = "创建时间", format = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @Excel(name = "更新人")
    private String updateBy;

    @Excel(name = "更新时间", format = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;


    @TableField(exist = false)
    private String platformType;

    public void setPlatformType(String platformType) {
        this.platformType = platformType;
    }
}
