package org.jeecg.modules.instagram.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.modules.instagram.entity.IgCelebrityTags;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: ig_celebrity_tags
 * @Author: jeecg-boot
 * @Date: 2024-12-02
 * @Version: V1.0
 */
@Data
@TableName("ig_celebrity_tags")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class IgCelebrityTagsModel extends IgCelebrityTags {
    private String celebrityPrivateId;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 指定日期时间格式
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date contractTime;

    private String productId;
    private String attributeId;
}
