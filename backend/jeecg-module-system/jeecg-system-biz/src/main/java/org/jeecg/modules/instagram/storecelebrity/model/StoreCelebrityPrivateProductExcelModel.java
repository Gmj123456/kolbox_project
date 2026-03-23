package org.jeecg.modules.instagram.storecelebrity.model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 私有网红表
 * @Author: jeecg-boot
 * @Date: 2020-11-11
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreCelebrityPrivateProductExcelModel {
    @Excel(name = "网红名称", width = 15)
    @Schema(title =  "网红名称")
    private String account;
    private String celebrityId;

    private Integer platformType;

    @Excel(name = "网红平台", width = 15)
    @Schema(title =  "网红平台")
    private String platformTypeStr;

    @Excel(name = "产品名称", width = 15)
    @Schema(title =  "产品名称")
    private String productInfo;

    private String brandId;
    private String productId;

    @Excel(name = "合作状态", width = 15)
    @Schema(title =  "合作状态")
    private String relationStatusStr;


    private Integer relationStatus;
}
