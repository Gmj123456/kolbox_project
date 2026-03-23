package org.jeecg.modules.email.model;

import org.jeecg.modules.email.entity.EmailPushMain;
import org.jeecg.modules.email.entity.EmailPushDetail;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

/**
 * @Description: 邮件推送主表明细VO
 * @Author:
 * @Date: 2025-11-07
 * @Version: V1.0
 */
@Data
public class EmailPushMainDetailVO {

    /**
     * 主表信息
     */
    @Schema(title =  "主表信息")
    private EmailPushMain main;

    /**
     * 明细表信息列表
     */
    @Schema(title =  "明细表信息列表")
    private List<EmailPushDetail> details;
}