package org.jeecg.modules.instagram.storecelebrity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrity;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 网红档案
 * @Author: nqr
 * @Date: 2020-04-20
 * @Version: V1.0
 */
@Data
public class StoreCelebrityModel extends StoreCelebrity {

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
     * 日期开始时间
     */
    private String startCreateTime;

    /**
     * 日期结束时间
     */
    private String endCreateTime;

    /**
     * 有效邮箱
     */
    private Integer validEmail;

    /**
     * 排序列
     */
    private String column;

    /**
     * 商家ID
     */
    private String sellerId;

    /**
     * 发送次数
     */
    private Integer sendEmailCount;

    /**
     * 标签为空
     * */
    private Integer isLikeTagsNull;

    /**
     * 国家为空
     * */
    private Integer isCountryNull;
}

