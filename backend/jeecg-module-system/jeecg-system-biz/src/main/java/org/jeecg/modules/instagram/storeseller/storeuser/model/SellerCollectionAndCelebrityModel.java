package org.jeecg.modules.instagram.storeseller.storeuser.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrity;
import org.jeecgframework.poi.excel.annotation.Excel;

@Data
public class SellerCollectionAndCelebrityModel extends StoreCelebrity {
    /**
     *  商家ID
     */

    private String sellerId;
    private String celebrityId;

    /**
     * 粉丝开始数量
     */
    private Integer followersStartNum;

    /**
     * 粉丝结束数量
     */
    private Integer followersEndNum;

    /**
     * 帖子数开始数量
     */
    private Integer postsStartNum;

    /**
     * 帖子数结束数量
     */
    private Integer postsEndNum;

    /**
     * 点赞数开始数量
     */
    private Integer likeAvgStartNum;

    /**
     * 点赞数结束数量
     */
    private Integer likeAvgEndNum;

    /**
     * 评论数开始数量
     */
    private Integer commentAvgStartNum;

    /**
     * 评论数结束数量
     */
    private Integer commentAvgEndNum;

    /**
     * 播放数开始数量
     */
    private Integer playAvgStartNum;

    /**
     * 播放数结束数量
     */
    private Integer playAvgEndNum;
    /**
     * 价格区间
     */
    @Excel(name = "价格区间", width = 15)
    @Schema(title =  "价格区间")
    private String priceRange;
    /**
     * 发送次数
     */
    private Integer sendEmailCount;

}