package org.jeecg.modules.instagram.storecelebrity.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrity;
import org.jeecgframework.poi.excel.annotation.Excel;
@Data
public class StoreCelebrityFeedbackModel extends StoreCelebrity {
    /**
     * 网红ID
     */
    @Excel(name = "网红ID", width = 15)
    @Schema(title =  "网红ID")
    private String celebrityId;
    /**
     * 是否处理 0：未处理(默认) 1：已处理
     */
    @Excel(name = "是否处理 0：未处理(默认) 1：已处理", width = 15)
    @Schema(title =  "是否处理 0：未处理(默认) 1：已处理")
    private Integer isDispose;
    /**
     * 粉丝开始数量
     */
    private Integer followersStartNum;

    /**
     * 粉丝结束数量
     */
    private Integer followersEndNum;
}
