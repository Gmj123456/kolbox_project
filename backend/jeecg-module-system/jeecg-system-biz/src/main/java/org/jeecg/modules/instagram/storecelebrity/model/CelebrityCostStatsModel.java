package org.jeecg.modules.instagram.storecelebrity.model;

import lombok.Data;

import java.util.List;

/**
 * @author lf
 * @version 1.0.0
 * @ClassName CelebrityCostStatsModel.java
 * @Description TODO
 * @createTime 2025年03月17日 17:53:00
 */
@Data
public class CelebrityCostStatsModel {
    private String platformName;           // 平台
    private String costRange;              // 签约价格区间
    private Long totalCount;               // 数量
    private List<CelebrityStarModel> celebrityStarModelList; // 星级统计列表
}
