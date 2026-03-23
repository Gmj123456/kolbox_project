package org.jeecg.modules.instagram.history.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description: 历史提报网红明细表查询模型
 * @Author:
 * @Date: 2025-11-26
 * @Version: V1.0
 */
@Data
public class KolHistoryCelebrityDetailQueryModel {

    /**
     * 主表ID（即KolHistoryCelebrity的ID）
     */
    @Schema(title =  "主表ID（即KolHistoryCelebrity的ID）")
    private String celebrityId;

    /**
     * 平台类型
     */
    @Schema(title =  "平台类型")
    private Integer platformType;

    /**
     * 产品名称
     */
    @Schema(title =  "产品名称")
    private String productName;

    /**
     * 网红名称
     */
    @Schema(title =  "网红名称")
    private String celebrityName;

    /**
     * 达人类型
     */
    @Schema(title =  "达人类型")
    private String kolType;

    /**
     * 产品名称
     */
    @Schema(title = "产品名称")
    private String brandName;
}