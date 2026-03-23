package org.jeecg.modules.instagram.storecelebrity.model;

import lombok.Data;

import java.util.List;

/**
 * @author lf
 * @version 1.0.0
 * @ClassName CelebrityCategoryStatsModel.java
 * @Description TODO
 * @createTime 2025年03月17日 18:50:00
 */
@Data
public class CelebrityCategoryStatsModel {
    private String platformName;           // 平台名称
    private String categoryName;           // 类目名称
    private Long totalCelebrityCount;      // 总网红数量
    private List<CelebrityStarModel> celebrityStarModelList; // 星级统计列表
}
