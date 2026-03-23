package org.jeecg.modules.instagram.storecelebrity.model;

import lombok.Data;

/**
 * @author lf
 * @version 1.0.0
 * @ClassName CelebrityStarModel.java
 * @Description TODO
 * @createTime 2025年03月17日 16:23:00
 */
@Data
public class CelebrityStarModel {
    private String starName;               // 星级名称
    private Long starValue;                // 星级计数
    private String starColor;              // 星级颜色（可选）
}
