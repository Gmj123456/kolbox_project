package org.jeecg.modules.instagram.storecelebrity.model;

import lombok.Data;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityFollowup;

@Data
public class StoreCelebrityFollowupModel extends StoreCelebrityFollowup {
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 账号
     */
    private String account;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 粉丝数
     */
    private Integer followersNum;

    /**
     * 平台类型
     */
    private Integer platformType;
    /**
     * 跟进日期
     */
    private String followDate;
    /**
     * 跟进日期
     */
    private String nextFollowDate;
    /**
     * 发货日期
     */
    private String shipDate;
    /**
     * 反馈日期
     */
    private String feedbackDate;
    /**
     * 产品名称
     */
    private String goodsTitle;
    /**
     * 是否跟进
     */
    private Integer isFollowUp;

}
