package org.jeecg.modules.instagram.storecelebrity.model;

import lombok.Data;

import java.util.List;

/**
 * @author lf
 * @version 1.0.0
 * @ClassName CelebrityCounselorStatsModel.java
 * @Description TODO
 * @createTime 2025年03月17日 18:19:00
 */
@Data
public class CelebrityCounselorStatsModel {
    private String platformName;           // 平台名称
    private String counselorName;          // 网红顾问名称
    private String counselorId;          // 网红顾问名称
    private Long totalSignedCount;         // 签约网红数量
    private List<CelebrityStarModel> celebrityStarModelList; // 星级统计列表
}
