package org.jeecg.modules.kol.model;

import lombok.Data;

@Data
public class KolTagsResultModel {
    private String id;
    private String name;
    private Integer buildAllianceNum;
    private Integer isAllotNum;
    private Integer isPrivate;
}
