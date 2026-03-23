package org.jeecg.modules.instagram.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.modules.instagram.entity.IgCelebrity;
import org.jeecg.modules.kol.model.TypeData;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @Description: IG 视图模型
 * @Author: xyc
 * @Date: 2024年12月3日 15:22:29
 * @Version: V1.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class IgCelebrityModel extends IgCelebrity {

    /**
     * IG网红标签关联视图列表
     */
    @Schema(title =  "标签列表")
    private List<IgAllotTagModel> tagList;

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

    private String authorAvatarUrl;

    List<TypeData> dataList;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 指定日期时间格式
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date contractTime;
}
