package org.jeecg.modules.instagram.storecelebrity.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author lf
 * @version 1.0.0
 * @ClassName CelebritySignedModel.java
 * @Description TODO
 * @createTime 2025年03月17日 10:02:00
 */
@Data
public class CelebritySignedModel {

    private Integer totalCount;
    private Integer signedCount;
    private Double percentage;
}
