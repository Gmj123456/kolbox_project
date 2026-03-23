package org.jeecg.modules.instagram.storecelebrity.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;

@Data
public class PushCommonModel {
    /**
     * 用户id
     */
    private String ids;

    /**
     * 发送账号
     */
    private String accountId;

    /**
     * 邮件模板
     */
    private String template;

    /**
     * 备注
     */
    private String remark;

    /**
     * 标题
     */
    private String title;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 发送时间
     */
    private String sendTime;

    /**
     * 确认提示 0 未确认 1确认
     */
    private Integer submit;

    /**
     * 流量包id
     */
    private String packageId;

    /**
     * 流量包名称
     */
    private String packageName;

    /**
     * 流量包订购id
     */
    private String packagePurchaseId;
    private String sellerId;
    private String sellerName;
    /**
     * 邮件封数
     */
    private Integer emailNum;
    /**
     * 间隔时间
     */
    private Integer emailTime;

    private String emailTitle;

    private String noSendList;
}
