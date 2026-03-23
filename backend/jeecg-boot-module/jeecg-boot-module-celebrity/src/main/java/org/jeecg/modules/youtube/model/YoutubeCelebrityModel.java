package org.jeecg.modules.youtube.model;

import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.youtube.entity.YoutubeCelebrity;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @Description: YT网红
 * @Author: nqr
 * @Date: 2023-11-22
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class YoutubeCelebrityModel extends YoutubeCelebrity {
    private String uniqueId;
    private Integer authorFollowerCount;

    // tag状态
    private Integer tagStatus = 99;
    /**
     * 播放开始数量
     */
    private Integer playAvgStartNum;
    /**
     * 播放结束数量
     */
    private Integer playAvgEndNum;

    // 开始时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 指定日期时间格式
    private Date startTime;

    // 结束时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 指定日期时间格式
    private Date endTime;

    /**
     * 列表国家名字
     */
    private String countryName;

    /**
     * 列表查询:网红顾问
     */
    private String celebrityCounselorName;

    private String celebrityCounselorId;

    private String celebrityTagCounselorId;

    /**
     * 列表查询:国家名字
     */
    private String shortCode;

    /**
     * 列表查询:是否包含邮箱 0:否 1:是
     */
    private Integer emailFlag;

    /**
     * 列表查询: 是否分配 0:否 1:是
     */
    private Integer isAllot;

    /***/
    private Integer developHistoryFlag = 0;

    private Integer tagFlag = 0;

    private Integer isTagDelete = 0;

    /**
     * 历史分配列表查询: 是否分配 0:否 1:是
     */
    private Integer historyIsAllot = 1;

    /**
     * 是否建联  0:显示为否 1:显示为是
     */
    private String celebrityPrivateId;


    private String tagName;
    private Integer tagNum;
    /**
     * tag标签
     */
    @Excel(name = "tag名称", width = 15)
    @Schema(title =  "tag名称")
    @TableLogic
    private String celebrityTags;

    /**
     * 开发历史
     */
    @Excel(name = "开发历史", width = 15)
    @Schema(title =  "网红顾问名称")
    private String contactHistory;
    /**
     * 网红顾问列表
     */
    private List<LoginUser> celebrityCounselorList;
    private LoginUser celebrityCounselor;
    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 是否开发，0：未开发；1：已开发
     */
    private Integer isContact;

    /**
     * 网红顾问短称
     */
    private String celebrityCounselorShortName;

    /**
     * 粉丝数
     */
    private Integer startFollowers;
    private Integer endFollowers;

    /**
     * 我的网红标识
     */
    private Integer myselfFlag;

    /**
     * 分配天数
     */
    private Integer days;
    private Integer isPrivate;
}
