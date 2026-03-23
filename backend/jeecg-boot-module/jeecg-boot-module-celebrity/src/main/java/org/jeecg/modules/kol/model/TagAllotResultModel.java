package org.jeecg.modules.kol.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Description: 顾问标签分配结果视图模型
 * @Author: xyc
 * @Date: 2024年12月10日 15:15:46
 * @Version: V1.0
 */
@Data
public class TagAllotResultModel  {
    /**
     * 分配时间
     */
    @Excel(name = "分配时间", width = 20, format = "yyyy-MM-dd", orderNum = "0")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(title =  "分配时间")
    private java.util.Date allotTime;
    /**
     * 顾问id
     */
    private String counselorId;
    /**
     * 顾问名称
     */
    @Excel(name = "网红顾问", width = 15, orderNum = "1")
    private String counselorName;
    /**
     * 私有网红数量
     */
    private Integer privateNum;
    /**
     * 分配内容
     */
    @Excel(name = "分配标签", width = 15, orderNum = "2")
    @Schema(title =  "分配标签")
    private String allotContent;

    /**
     * 分配的网红数量
     */
    @Excel(name = "分配数量", width = 15, orderNum = "3")
    private Integer allotNum;

}
