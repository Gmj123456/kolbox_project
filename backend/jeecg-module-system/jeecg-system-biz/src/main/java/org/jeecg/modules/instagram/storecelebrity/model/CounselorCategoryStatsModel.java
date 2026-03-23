package org.jeecg.modules.instagram.storecelebrity.model;

import lombok.Data;

import java.util.List;

/**
 * @author lf
 * @version 1.0.0
 * @ClassName CounselorCategoryStatsModel.java
 * @Description TODO
 * @createTime 2025年03月18日 11:34:00
 */
@Data
public class CounselorCategoryStatsModel {
    private String platformName;
    private String counselorName;
    private String categoryName;
    private String  starLevel;  // Represents the is_top_star value
    private Long count;      // Count for this specific star level
}
