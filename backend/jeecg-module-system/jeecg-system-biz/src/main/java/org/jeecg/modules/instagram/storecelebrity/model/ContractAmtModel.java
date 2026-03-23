package org.jeecg.modules.instagram.storecelebrity.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;

/**
 * @Description: 私有网红表
 * @Author: jeecg-boot
 * @Date: 2020-11-11
 * @Version: V1.0
 */
@Data
public class ContractAmtModel {
    /**
     * 日期
     */
    @ExcelProperty(value = "日期")
    private String contractTimeStr;
    /**
     * 网红顾问
     */
    @ExcelProperty(value = "网红顾问")
    private String celebrityCounselorName;
    /**
     * 网红名
     */
    @ExcelProperty(value = "选中网红")
    private String account;
    /**
     * 平台类型 0=instagram;1=youtube
     */
    @ExcelProperty(value = "平台")
    @Schema(title =  "平台类型 0=instagram;1=youtube")
    private String platformTypeText;
    /**
     * 签约费用
     */
    @ExcelProperty(value = "网红成本$")
    @Schema(title =  "网红成本$")
    private BigDecimal contractAmount;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private java.util.Date contractTime;

}
