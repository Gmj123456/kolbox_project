package org.jeecg.modules.tiktok.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Description TODO
 * @Author nqr
 * @Date 2024/09/02 16:02
 */
@Data
public class TiktokAllotLogDetailModel {
    /**
     * 分配时间
     */
    @Excel(name = "分配时间", width = 20, format = "yyyy-MM-dd", orderNum = "0")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(title =  "分配时间")
    private java.util.Date allotTime;
    /**
     * 网红顾问名称
     */
    @Excel(name = "网红顾问", width = 15, orderNum = "1")
    @Schema(title =  "网红顾问")
    private String celebrityCounselorName;
    /**
     * 分配内容
     */
    @Excel(name = "分配标签", width = 15, orderNum = "2")
    @Schema(title =  "分配标签")
    private String allotContent;
    /**
     * 分配数量
     */
    @Excel(name = "分配数量", width = 15, orderNum = "3")
    @Schema(title =  "分配数量")
    private String allotNums;

    private String allotIds;
}
