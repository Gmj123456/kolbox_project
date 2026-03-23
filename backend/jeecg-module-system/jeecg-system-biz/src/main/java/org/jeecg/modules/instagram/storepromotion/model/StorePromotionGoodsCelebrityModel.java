package org.jeecg.modules.instagram.storepromotion.model;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionGoodsCelebrity;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class StorePromotionGoodsCelebrityModel extends StorePromotionGoodsCelebrity {
    // 粉丝数
    private Integer followersNum;
    // 平均点赞数
    private Integer likeAvgNum;
    // 平均互动率
    private BigDecimal interactAvgNum;
    // 平均观看数
    private Integer playAvgNum;
    // 总视频数
    private Integer videoCount;
    // 总点赞数
    private Integer likeCount;
    // 批量删除数据
    private List<JSONObject> data;

    /**
     * 网红账号
     */
    private String celebrityAccount;
    /**
     * 推广标题
     */
    private String promTitle;
    /**
     * 产品图片
     */
    private String goodsPicUrl;
    /**
     * 产品标题
     */
    private String goodsTitle;
    /**
     * 商家顾问
     */
    private String nickname;
    /**
     * 商家顾问Id
     */
    private String sid;
    /**
     * 商务人员
     */
    private String counselorUserName;
    /**
     * 商务人员id
     */
    private String counselorUserId;
    /**
     * 网红平台
     */
    private Integer platformType;
    /**
     * 推广费
     */
    private BigDecimal totalAmount;
    /**
     * 推广开始时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date promStartTime;

    /**
     * 推广结束时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date promEndTime;
    /**
     * 上线时间
     */
    private String startOnlineTime;
    /**
     * 上线时间
     */
    private String endOnlineTime;
    /**
     * 推广状态
     */
    private Integer promStatus;
    /**
     * 网红负责人
     */
    private String celebrityUserId;
    /**
     * 错误信息
     */
    private String errorMsg;
}
