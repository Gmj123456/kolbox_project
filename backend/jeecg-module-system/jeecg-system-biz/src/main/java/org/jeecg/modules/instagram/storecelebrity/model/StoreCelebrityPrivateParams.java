package org.jeecg.modules.instagram.storecelebrity.model;

import lombok.Data;

import java.util.List;

/**
 * @author lf
 * @version 1.0.0
 * @ClassName StoreCelebrityPrivateParams.java
 * @Description TODO
 * @createTime 2024年05月24日 11:35:00
 */
@Data
public class StoreCelebrityPrivateParams {
    // 私有网红列表
    List<StoreCelebrityPrivateModel> celebrityPrivates;
}
