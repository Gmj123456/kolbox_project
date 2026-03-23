package org.jeecg.modules.instagram.storecelebrity.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author lf
 * @version 1.0.0
 * @ClassName PlatformDistributionModel.java
 * @Description TODO
 * @createTime 2025年03月17日 08:58:00
 */
@Data
public class PlatformDistributionModel {
    @Schema(description ="平台名称")
    private String platform;

    @Schema(description ="数量")
    private Integer count;

    @Schema(description ="占比百分比")
    private Double percentage;
}
