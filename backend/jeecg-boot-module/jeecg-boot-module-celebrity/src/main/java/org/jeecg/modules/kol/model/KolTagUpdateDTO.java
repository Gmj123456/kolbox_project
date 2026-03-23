package org.jeecg.modules.kol.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: nqr
 * @Date: 2025/7/22 16:39
 * @Description:
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KolTagUpdateDTO {
    private String tagName;
    private Integer unassignedCount;
    private Integer platformType;
    private String countryDataJson;
    private Date snapshotTime;
}
