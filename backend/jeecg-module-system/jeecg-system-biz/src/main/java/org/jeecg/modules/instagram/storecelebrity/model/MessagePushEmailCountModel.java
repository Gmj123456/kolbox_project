package org.jeecg.modules.instagram.storecelebrity.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.jeecg.modules.instagram.storecelebrity.entity.MessagePushDetail;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class MessagePushEmailCountModel extends MessagePushDetail {
    /**
     * 排序字段
     */
    private String orderWord;
    /**
     * 排序
     */
    private String order;
    /**
     * 商家发送邮件总数
     */

    private Integer emailCount;
    /**
     * 商家发送邮件成功数
     */
    private Integer successCount;
    /**
     * 商家发送邮件等待数
     */
    private Integer waitCount;
    /**
     * 商家发送邮件失败数
     */
    private Integer errorCount;
    /**
     * 商家网红个数
     */
    private Integer kolCount;

    /**
     * 所有商家发送邮件总记录数
     */
    private Integer emailPushCount;
    /**
     * 所有商家网红总记录数
     */
    private Integer sellerKolCount;
    /**
     * 打开数量
     */
    private Integer openCount;
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
