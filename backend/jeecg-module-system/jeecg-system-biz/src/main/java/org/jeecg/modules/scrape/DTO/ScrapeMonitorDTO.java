package org.jeecg.modules.scrape.DTO;

import lombok.Data;

import java.util.Date;

@Data
public class ScrapeMonitorDTO {
    private String id;
    private String ipAddress;
    private String deviceCode;
    private String remoteId;
    private String crawlerName;
    private String taskContent;
    private Integer taskStatus;
    private Integer isReminder;
    private Integer remainingTimes;
    private Date lastExecutionTime;
    private String remark;
    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;

    // 新增 platform 字段
    private String platformType;
}

