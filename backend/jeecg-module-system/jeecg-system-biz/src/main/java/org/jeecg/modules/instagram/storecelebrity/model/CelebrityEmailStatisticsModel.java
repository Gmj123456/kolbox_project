package org.jeecg.modules.instagram.storecelebrity.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.jeecg.modules.instagram.storecelebrity.entity.MessagePushDetail;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class CelebrityEmailStatisticsModel extends MessagePushDetail {

    /**
     * 给网红发送次数
     */
    private Integer celebrityCount;
    /**
     * 网红打开次数
     */
    private Integer openCount;

    /**
     * 网红标签
     */
    private String tag;

    /**
     * 商家发送次数
     */
    private Integer sellerPushCount;

    /**
     * 发送日期开始时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String startEmailPushTime;
    /**
     * 发送日期结束时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String endEmailPushTime;
}
