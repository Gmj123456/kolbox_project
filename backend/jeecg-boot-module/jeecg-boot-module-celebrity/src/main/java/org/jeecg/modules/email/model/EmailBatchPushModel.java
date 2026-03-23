package org.jeecg.modules.email.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecg.modules.email.entity.EmailPushDetail;
import org.jeecg.modules.email.entity.EmailPushMain;

import java.util.Date;
import java.util.List;

/**
 * @Description: 邮件批量推送参数模型
 * @Author: jeecg-boot
 * @Date: 2025-11-07
 * @Version: V1.0
 */
@Data
public class EmailBatchPushModel extends EmailPushMain {

    /**
     * 邮件明细列表
     */
    @Schema(title =  "邮件明细列表")
    private List<EmailPushDetail> emailPushDetails;
}