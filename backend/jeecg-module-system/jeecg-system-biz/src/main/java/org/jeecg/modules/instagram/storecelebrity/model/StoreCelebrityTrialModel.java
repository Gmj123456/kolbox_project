package org.jeecg.modules.instagram.storecelebrity.model;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityTrial;
import org.jeecgframework.poi.excel.annotation.Excel;

@Data
public class StoreCelebrityTrialModel extends StoreCelebrityTrial {

    @Excel(name = "粉丝数量开始数量", width = 15)
    @Schema(title =  "粉丝数量开始数量")
    @TableField(exist = false)
    private Integer followersStartNum;

    @Excel(name = "粉丝数量结束数量", width = 15)
    @Schema(title =  "粉丝数量结束数量")
    @TableField(exist = false)
    private Integer followersEndNum;


    @Excel(name = "平均播放数量开始数量", width = 15)
    @Schema(title =  "平均播放数量开始数量")
    @TableField(exist = false)
    private Integer playAvgStartNum;

    @Excel(name = "平均播放数量结束数量", width = 15)
    @Schema(title =  "平均播放数量结束数量")
    @TableField(exist = false)
    private Integer playAvgEndNum;

    @Excel(name = "帖子数量开始数量", width = 15)
    @Schema(title =  "帖子数量开始数量")
    @TableField(exist = false)
    private Integer postsStartNum;

    @Excel(name = "帖子数量结束数量", width = 15)
    @Schema(title =  "帖子数量结束数量")
    @TableField(exist = false)
    private Integer postsEndNum;

    /**
     * 收藏表的网红id
     * */
    private String collectionId;
    /**
     * 商家ID
     */
    private String sellerId;

    /**
     * 发送次数
     */
    private Integer sendEmailCount;

    /**
     * 价格区间
     */
    @Excel(name = "价格区间", width = 15)
    @Schema(title =  "价格区间")
    private String priceRange;
}
