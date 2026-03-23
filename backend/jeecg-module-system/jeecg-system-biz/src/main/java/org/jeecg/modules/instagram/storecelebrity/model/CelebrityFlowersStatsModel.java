package org.jeecg.modules.instagram.storecelebrity.model;

import lombok.Data;

import java.util.List;

/**
 * @author lf
 * @version 1.0.0
 * @ClassName CelebrityFlowersStatsModel.java
 * @Description TODO
 * @createTime 2025年03月17日 14:51:00
 */
@Data
public class CelebrityFlowersStatsModel {
    private String platformName;
    private String followerRange;
    private Long totalCount;
    private List<CelebrityStarModel> celebrityStarModelList;
}
