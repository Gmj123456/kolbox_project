package org.jeecg.modules.youtube.model;


import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.modules.kol.model.TypeData;
import org.jeecg.modules.youtube.entity.YoutubeCelebrity;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @Description: YT网红
 * @Author: xyc
 * @Date: 2024-12-26 08:48:02
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class YtCelebrityModel extends YoutubeCelebrity {
    /**
     * 网红标签关联视图列表
     */
    @Schema(title =  "标签列表")
    private List<YtAllotTagModel> tagList;

    /**
     * 私有网红Id
     */
    @Schema(title =  "私有网红Id")
    private String celebrityPrivateId;

    /**
     * 开发历史
     */
    @Schema(title =  "开发历史")
    private String contactHistory;


    List<TypeData> dataList;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 指定日期时间格式
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date contractTime;
}
