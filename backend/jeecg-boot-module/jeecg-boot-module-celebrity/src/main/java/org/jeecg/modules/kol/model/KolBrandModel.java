package org.jeecg.modules.kol.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: 网红品牌简易模型
 * @Author: xyc
 * @Date: 2024-12-17 19:23:47
 * @Version: V1.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class KolBrandModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 品牌id
     */
    @Schema(title =  "品牌id")
    private String brandId;
    private String productId;

    /**
     * 网红列表
     */
    @Schema(title =  "网红列表")
    private List<KolSimpleModel> kolList;

    /**
     * 平台类型（0:IG;1:YT;2:TK）
     */
    @Schema(title =  "平台类型")
    private Integer platformType;


    /**
     * 发送邮件主表ID
     */
    @Schema(title =  "发送邮件主表ID")
    private String emailPushMainId;


    /**
     * 简易网红信息
     */
    @Data
    @Accessors(chain = true)
    public static class KolSimpleModel {
        /**
         * 网红表对应的主键id
         */
        @Schema(title =  "网红表对应的主键id")
        private String id;

        /**
         * 网红邮箱
         */
        @Schema(title =  "网红邮箱")
        private String kolEmail;

    }
}
