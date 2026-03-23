package org.jeecg.modules.instagram.storecelebrity.model;

import lombok.Data;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityFeedback;

@Data
public class StoreCelebrityFeedbackUserModel extends StoreCelebrityFeedback {

    /**
     * 反馈人员username
     */

    private String username;

}
