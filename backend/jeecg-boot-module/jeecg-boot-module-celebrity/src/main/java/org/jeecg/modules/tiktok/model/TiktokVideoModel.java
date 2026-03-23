package org.jeecg.modules.tiktok.model;

import lombok.Data;
import org.jeecg.modules.tiktok.entity.TiktokVideo;

import java.util.List;

/**
 * @author lf
 * @version 1.0.0
 * @ClassName TiktokVideoModel.java
 * @Description TODO
 * @createTime 2024年05月30日 14:27:00
 */
@Data
public class TiktokVideoModel  extends TiktokVideo {
    private List<String> videoUrlList;
    private String startDateTime;
    private String endDateTime;
}
