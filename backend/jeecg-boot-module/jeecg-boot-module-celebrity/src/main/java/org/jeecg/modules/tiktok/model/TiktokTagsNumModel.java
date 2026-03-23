package org.jeecg.modules.tiktok.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 方法描述: 未分配标签对应的网红实体
 *
 * @author nqr
 * @date 2025/01/04 09:49
 **/
@Data
public class TiktokTagsNumModel {
    @Excel(name = "账号", width = 15)
    private String uniqueId;
    @Excel(name = "粉丝数", width = 15)
    private String authorFollowerCount;
    @Excel(name = "有效视频数", width = 15)
    private String videoStandardCount;
    @Excel(name = "标签", width = 15)
    private String tagName;
    @Excel(name = "邮箱", width = 15)
    private String email;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 指定日期时间格式
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date contractTime;
}
